package com.mindbyromanzanoni.view.activity.editProfile

import android.Manifest
import android.net.Uri
import android.os.Build
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.databinding.ActivityEditProfileBinding
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.sharedPreference.SharedPrefs
import com.mindbyromanzanoni.utils.MyProgressBar
import com.mindbyromanzanoni.utils.checkCameraPermissionForCaptureImage
import com.mindbyromanzanoni.utils.checkingGalleryPermissionForPickImage
import com.mindbyromanzanoni.utils.compressImage
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.getFileFromUri
import com.mindbyromanzanoni.utils.getUriForCamera
import com.mindbyromanzanoni.utils.imagePicker
import com.mindbyromanzanoni.utils.setImageFromUrl
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.validators.Validator
import com.mindbyromanzanoni.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileActivity : BaseActivity<ActivityEditProfileBinding>() {
    private val viewModal: AuthViewModel by viewModels()
    var activity = this@EditProfileActivity

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    @Inject
    lateinit var validator: Validator
    override fun getLayoutRes(): Int = R.layout.activity_edit_profile

    companion object {
        lateinit var callbackUpdatedProfile :(Boolean)->Unit
    }

    override fun initView() {
        setToolbar()
        observeDataFromViewModal()
        setOnClickListener()
        setUserDataOnViews()
    }

    override fun viewModel() {
        binding.viewModel = viewModal
    }

    private fun setUserDataOnViews() {
        binding.apply {
            cvImg.setImageFromUrl(R.drawable.no_image_placeholder,sharedPrefs.getUserData()?.userImage)
            viewModal.contactNumber.set(sharedPrefs.getUserData()?.contactNumber)
            viewModal.name.set(sharedPrefs.getUserData()?.name)
            viewModal.countryCode.set(countryCodePicker.selectedCountryCodeWithPlus)
            countryCodePicker.setCountryForPhoneCode(sharedPrefs.getUserData()?.countryCode?.toInt() ?: 1)
        }
    }

    private fun setOnClickListener() {
        binding.apply {
            btnCreatePassword.setOnClickListener {
                if (validator.validateUpdateProfile(activity, binding)) {
                    viewModal.countryCode.set(countryCodePicker.selectedCountryCodeWithPlus)
                    RunInScope.ioThread {
                        viewModal.hitUpdateProfileApi()
                    }
                }
            }
            imagePicker.setOnClickListener {
                pickImage()
            }
        }
    }

    private fun setToolbar() {
        binding.toolbar.apply {
            tvToolTitle.text = getString(R.string.edit_profile)
            ivBack.setOnClickListener {
                finishActivity()
            }
        }
    }



    private fun pickImage() {
        imagePicker(activity,{ selectedType ->
            if (selectedType == 0) {
                checkCameraPermissionForCaptureImage(this,{
                    captureCameraImage()
                }, { permissionList ->
                    cameraPermission.launch(permissionList)
                })
                return@imagePicker
            }

            checkingGalleryPermissionForPickImage(activity, {
                captureGalleryImage()
            }, { permissionList ->
                galleryPermission.launch(permissionList)
            }
            )
        }
        )
    }

    private val galleryPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (result[Manifest.permission.READ_EXTERNAL_STORAGE] == true) captureGalleryImage()
    }


    private val pickImageRequest = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        viewModal.imageUri = uri
        uri?.let { imageUri ->
            setImageAndApiRequest(
                imageUri = imageUri
            )
        }
    }

    private fun captureGalleryImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            pickImageRequest.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            return
        }
        pickGalleryImage.launch("image/*")
    }

    private val pickGalleryImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        viewModal.imageUri = uri
        uri?.let { imageUri ->
            setImageAndApiRequest(imageUri)
        }
    }

    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (result[Manifest.permission.CAMERA] == true) captureCameraImage()
            return@registerForActivityResult
        }
        if (result[Manifest.permission.CAMERA] == true && result[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true) captureCameraImage()
    }


    private fun captureCameraImage() {
        viewModal.tempImageUri = getUriForCamera(this)
        cameraImage.launch(viewModal.tempImageUri)
    }


    private val cameraImage = registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
        if (result) {
            viewModal.imageUri = viewModal.tempImageUri
            viewModal.imageUri?.let { imageUri ->
                setImageAndApiRequest( imageUri)
            }
        }
    }


    private fun setImageAndApiRequest(imageUri: Uri) {
        getFileFromUri( this, imageUri) { file ->
            viewModal.image = file
            lifecycleScope.launch {
                viewModal.image = compressImage(viewModal.image,activity)

                withContext(Dispatchers.Main) {
                    binding.cvImg.setImageFromUrl(
                        R.drawable.placeholder_mind,
                        imageUri.toString(),
                    )
                }
            }
        }
    }


    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.updateProfileSharedFlow.collectLatest {isResponse->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.success == true) {
                            val userData = sharedPrefs.getUserData()
                            userData?.name = viewModal.name.get().toString()
                            userData?.contactNumber = viewModal.contactNumber.get().toString()
                            userData?.countryCode = viewModal.countryCode.get().toString()
                            if(viewModal.image != null){
                                userData?.userImage = viewModal.image.toString()
                            }
                            sharedPrefs.save(AppConstants.SHARED_PREFS_USER_DATA, Gson().toJson(userData))
                            finishActivity()
                            callbackUpdatedProfile.invoke(true)
                        } else {
                            showErrorSnack(activity, data?.message ?: "")
                        }
                    }
                    is Resource.Error -> {
                        isResponse.message?.let { msg ->
                            showErrorSnack(activity, msg)
                        }
                    }
                }
            }
        }

        viewModal.showLoading.observe(activity) {
            if (it) {
                MyProgressBar.showProgress(activity)
            } else {
                MyProgressBar.hideProgress()
            }
        }
    }
}