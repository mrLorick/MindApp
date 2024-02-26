package com.mindbyromanzanoni.view.activity.login

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.CallbackManager.Factory.create
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.databinding.ActivityLoginBinding
import com.mindbyromanzanoni.genrics.Resource
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.sharedPreference.RememberMeSharedPrefs
import com.mindbyromanzanoni.sharedPreference.SharedPrefs
import com.mindbyromanzanoni.utils.MyProgressBar
import com.mindbyromanzanoni.utils.applyTextWatcherAndFilter
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.constant.AppConstants.SAVE_EMAIL
import com.mindbyromanzanoni.utils.constant.AppConstants.SAVE_PASSWORD
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.showErrorSnack
import com.mindbyromanzanoni.validators.Validator
import com.mindbyromanzanoni.view.activity.dashboard.DashboardActivity
import com.mindbyromanzanoni.view.activity.forgotPassword.ForgotPasswordActivity
import com.mindbyromanzanoni.view.activity.signup.SignupActivity
import com.mindbyromanzanoni.view.activity.verificationCode.VerificationCodeActivity
import com.mindbyromanzanoni.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@Suppress("DEPRECATION")
@AndroidEntryPoint
class LoginActivity : BaseActivity<ActivityLoginBinding>() {
    private val viewModal: AuthViewModel by viewModels()
    var activity = this@LoginActivity
    private lateinit var auth: FirebaseAuth
    private lateinit var rememberMeSharedPref: RememberMeSharedPrefs
    private var mCallbackManager: CallbackManager? = null


    @Inject
    lateinit var validator: Validator

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun getLayoutRes() = R.layout.activity_login

    private val signInActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if(activityResult.resultCode==Activity.RESULT_OK){
                viewModal.googleSignIn?.googleSignInResult(
                    activityResult = activityResult,
                    firebaseAuth = auth,
                    result = { result ->
                        viewModal.applicationID.set(result?.user?.uid)
                        viewModal.emailGmail.set(result?.user?.email)
                        viewModal.name.set(result?.user?.displayName)
                        viewModal.contactNumber.set(result?.user?.phoneNumber)
                        viewModal.loginMode.set("Gmail")
                        RunInScope.ioThread {
                            viewModal.hitLoginGmailApi()
                        }
                    }
                )
            }
        }
    override fun initView() {
        rememberMeSharedPref = RememberMeSharedPrefs(applicationContext)
        getWatcherEmail()
        auth = FirebaseAuth.getInstance()
        onClickListener()
        setInUiEmailPassword()
        observeDataFromViewModal()
        mCallbackManager = create()
        LoginManager.getInstance().registerCallback(mCallbackManager,
            object : FacebookCallback<LoginResult> {

                override fun onCancel() {
                    Toast.makeText(this@LoginActivity, "Login Cancel", Toast.LENGTH_LONG).show()
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(this@LoginActivity, error.message, Toast.LENGTH_LONG).show()
                }

                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result.accessToken)

                }
            })


        binding.appCompatImageView13.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(
                activity,
                mutableListOf("public_profile", "user_friends", "email")
            )
        }
    }
    private fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    updateUI(null)
                 }
                AccessToken.setCurrentAccessToken(null)
                LoginManager.getInstance().logOut()
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user!=null) {
            viewModal.applicationID.set(user.uid)
            viewModal.emailGmail.set(user.email)
            viewModal.name.set(user.displayName)
            viewModal.contactNumber.set(user.phoneNumber)
            viewModal.loginMode.set("Facebook")
            RunInScope.ioThread {
                viewModal.hitLoginGmailApi()
            }
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mCallbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    /** check email is valid or not*/
    private fun getWatcherEmail() {
        binding.etEmail.applyTextWatcherAndFilter(validator, binding.ivTick)
    }

    /**When Remember me enabled or checked save data set in views email or password*/
    private fun setInUiEmailPassword() {
        if (rememberMeSharedPref.getRememberMe()) {
            binding.checkRememberMe.isChecked = true
            viewModal.email.set(rememberMeSharedPref.getString(SAVE_EMAIL))
            viewModal.password.set(rememberMeSharedPref.getString(SAVE_PASSWORD))
        }
    }

    override fun viewModel() {
        binding.viewModel = viewModal
    }

    private fun onClickListener() {
        binding.apply {
            tvForgotPassword.setOnClickListener {
                launchActivity<ForgotPasswordActivity> { }
            }

            btnSignIn.setOnClickListener {
                if (validator.validateLogin(activity, binding)) {
                    RunInScope.ioThread {
                        viewModal.hitLoginApi()
                    }
                }
            }
            tvSignUp.setOnClickListener {
                launchActivity<SignupActivity> { }
            }
            ivGoogle.setOnClickListener {
                if (viewModal.googleSignIn == null) viewModal.initializeGoogleSignIn()
                val googleSignInClient = viewModal.googleSignIn?.googleSignIn(context = activity)
                viewModal.googleSignIn?.signOut(firebaseAuth = auth, context = activity)
                signInActivityResult.launch(googleSignInClient?.signInIntent)
            }
        }
    }

    /** Observer Response via View model*/
    private fun observeDataFromViewModal() {
        lifecycleScope.launch {
            viewModal.loginResponseSharedFlow.collectLatest { isResponse ->
                when (isResponse) {
                    is Resource.Success -> {
                        val data = isResponse.data
                        if (data?.isSuccess() == true) {
                            /**When Remember me enabled or checked save data in shared preference mail or password otherwise clear Data*/
                            rememberMeSharedPref.apply {
                                if (binding.checkRememberMe.isChecked) {
                                    saveRememberMe(true)
                                    save(SAVE_EMAIL, viewModal.email.get().toString())
                                    save(SAVE_PASSWORD, viewModal.password.get().toString())
                                } else {
                                    saveRememberMe(false)
                                    clearPreference()
                                }
                            }
                            if (data.data!!.isEmailVerified) {
                                sharedPrefs.save(AppConstants.USER_AUTH_TOKEN, data.data.token)
                                sharedPrefs.saveUserLogin(true)
                                sharedPrefs.save(
                                    AppConstants.SHARED_PREFS_USER_DATA,
                                    Gson().toJson(data.data)
                                )
                                launchActivity<DashboardActivity> { }
                                finishAffinity()
                            } else {
                                val bundle = bundleOf(
                                    AppConstants.SCREEN_TYPE to AppConstants.SIGN_UP,
                                    AppConstants.USER_EMAIL to data.data.email
                                )
                                launchActivity<VerificationCodeActivity>(0, bundle) {}
                            }
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



