package com.mindbyromanzanoni.view.activity.splash

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.util.Base64
import androidx.lifecycle.lifecycleScope
import com.mindbyromanzanoni.R
import com.mindbyromanzanoni.base.BaseActivity
import com.mindbyromanzanoni.bioMatrixHelper.CustomFaceOrFingerprintAuth
import com.mindbyromanzanoni.databinding.ActivitySplashBinding
import com.mindbyromanzanoni.genrics.RunInScope
import com.mindbyromanzanoni.sharedPreference.BioMatrixSharedPrefs
import com.mindbyromanzanoni.sharedPreference.SharedPrefs
import com.mindbyromanzanoni.utils.MyProgressBar
import com.mindbyromanzanoni.utils.constant.AppConstants
import com.mindbyromanzanoni.utils.finishActivity
import com.mindbyromanzanoni.utils.launchActivity
import com.mindbyromanzanoni.utils.setStatusBarHideBoth
import com.mindbyromanzanoni.view.activity.dashboard.DashboardActivity
import com.mindbyromanzanoni.view.activity.onboarding.OnBoardingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import javax.inject.Inject


@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>() {
    private var bioMatrixSharedPrefs: BioMatrixSharedPrefs? = null
    private lateinit var fingerprintManager: FingerprintManager
    private lateinit var km: KeyguardManager
    var activity = this@SplashActivity

    @Inject
    lateinit var sharedPrefs: SharedPrefs

    override fun getLayoutRes(): Int {
        return R.layout.activity_splash
    }

    override fun initView() {
        setStatusBarHideBoth()
        initialiseBioMatrixClass()
        handlingBio()
        generateHashKey(applicationContext)
    }

    private fun showBioMatrixDialog() {
        RunInScope.mainThread {
            CustomFaceOrFingerprintAuth(activity).startBioAuth { status, result ->
                lifecycleScope.launch {
                    MyProgressBar.showProgress(activity)
                    delay(2000)
                    if (status) {
                        lifecycleScope.launch {
                            if (sharedPrefs.getUserLogin()) {
                                launchActivity<DashboardActivity> { }
                            } else {
                                launchActivity<OnBoardingActivity> { }
                            }
                            finish()
                        }
                    } else if (result == "CANCELED") {
                        finishActivity()
                    }
                    MyProgressBar.hideProgress()
                }
            }
        }
    }

    private fun generateHashKey(context: Context) {
        try {
            val packageInfo: PackageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in packageInfo.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), Base64.DEFAULT))
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }


    private fun handlingBio() {
        if (bioMatrixSharedPrefs?.getBoolean(AppConstants.BIO_MATRIX_STATUS_TOGGLE) == true) {
            showBioMatrixDialog()
        } else {
            lifecycleScope.launch {
                if (sharedPrefs.getUserLogin()) {
                    launchActivity<DashboardActivity> { }
                    finishAffinity()
                } else {
                    launchActivity<OnBoardingActivity> { }
                    finishAffinity()
                }
            }
        }
    }

    private fun initialiseBioMatrixClass() {
        try {
            bioMatrixSharedPrefs = BioMatrixSharedPrefs(this@SplashActivity)
            km = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            fingerprintManager = getSystemService(FingerprintManager::class.java)
            if (!km.isKeyguardSecure) {
                bioMatrixSharedPrefs?.save(AppConstants.BIO_MATRIX_STATUS_TOGGLE, false)
                return
            }

            if (!fingerprintManager.hasEnrolledFingerprints()) {
                bioMatrixSharedPrefs?.save(AppConstants.BIO_MATRIX_STATUS_TOGGLE, false)
                return
            }
        } catch (_: Exception) {
        }
    }


    override fun viewModel() {

    }

}