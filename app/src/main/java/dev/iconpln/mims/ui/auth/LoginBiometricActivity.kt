package dev.iconpln.mims.ui.auth

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.HomeActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.R
import dev.iconpln.mims.databinding.ActivityLoginBiometricBinding
import dev.iconpln.mims.ui.SsoLoginActivity
import dev.iconpln.mims.ui.auth.otp.OtpActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTimeUtils

class LoginBiometricActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBiometricBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession

    private var mAndroidId: String = ""
    private var mAppVersion: String = ""
    private var mDeviceData: String = ""
    private var mIpAddress: String = ""
    private var androidVersion: Int = 0
    private var dateTimeUtc: Long = 0L

    private var username: String = ""
    private var mPassword: String = ""

    private var isLoginSso = 0
    private var code = ""

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBiometricBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!

        initData()
        intiView()
    }

    private fun intiView() {
        biometricPrompt = createBiometricPrompt()
        dialog = Helper.loadingDialog(this@LoginBiometricActivity)

        if (Build.VERSION.SDK_INT >= 28) setVisibilityBiometric() else binding.btnBiometric.visibility =
            View.GONE

        binding.btnBiometric.setOnClickListener {
            setBiometric()
        }

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        viewModel.loginResponse.observe(this) {
            when (it.message) {
                Config.DO_LOGIN -> {
                    CoroutineScope(Dispatchers.IO).launch {

                        SharedPrefsUtils.setStringPreference(
                            this@LoginBiometricActivity,
                            Config.KEY_JWT_WEB,
                            it.webToken!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginBiometricActivity,
                            Config.KEY_JWT,
                            it.token!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginBiometricActivity,
                            Config.KEY_USERNAME,
                            username
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginBiometricActivity,
                            Config.KEY_FULL_NAME,
                            it.user?.fullName!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginBiometricActivity,
                            Config.KEY_ROLE_NAME,
                            it.user?.roleName!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginBiometricActivity,
                            Config.KEY_EMAIL,
                            it.user?.mail!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginBiometricActivity,
                            Config.KEY_PLANT,
                            it.user?.plant!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginBiometricActivity,
                            Config.KEY_PLANT_NAME,
                            it.user?.plantName!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginBiometricActivity,
                            Config.KEY_STOR_LOC,
                            it.user?.storloc!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginBiometricActivity,
                            Config.KEY_STOR_LOC_NAME,
                            it.user?.storLocName!!
                        )
                        SharedPrefsUtils.setIntegerPreference(
                            this@LoginBiometricActivity,
                            Config.KEY_SUBROLE_ID,
                            it.user?.subroleId!!
                        )
                        SharedPrefsUtils.setIntegerPreference(
                            this@LoginBiometricActivity,
                            Config.KEY_ROLE_ID,
                            it.user?.roleId!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginBiometricActivity,
                            Config.KEY_KODE_USER,
                            it.user.kdUser!!
                        )

                        withContext(Dispatchers.Main) {
                            val intentToHome =
                                Intent(this@LoginBiometricActivity, HomeActivity::class.java)
                            startActivity(intentToHome)
                            finish()
                        }
                    }
                }

                Config.DO_OTP -> {
                    viewModel.sendOtp(this@LoginBiometricActivity, username)
                    val intentToOtp = Intent(this@LoginBiometricActivity, OtpActivity::class.java)
                    intentToOtp.putExtra(Config.KEY_USERNAME, username)
                    intentToOtp.putExtra(Config.KEY_OTP_FROM, Config.OTP_FROM_LOGIN)
                    startActivity(intentToOtp)
                    finish()
                }
            }
        }

        viewModel.errorResponse.observe(this) {
            if (it != 200) dialog.dismiss()
        }

        viewModel.isLoading.observe(this) {
            if (it == true) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        }
    }

    private fun setVisibilityBiometric() {
        when (isLoginSso) {
            1 -> {
                val promptInfo = createPromptInfo()
                if (BiometricManager.from(this@LoginBiometricActivity)
                        .canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
                ) {
                    biometricPrompt.authenticate(promptInfo)
                }
            }

            0 -> {
                if (!username.isNullOrEmpty() && !mPassword.isNullOrEmpty()) {
                    val promptInfo = createPromptInfo()
                    if (BiometricManager.from(this@LoginBiometricActivity)
                            .canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
                    ) {
                        biometricPrompt.authenticate(promptInfo)
                    }
                } else {
                    binding.btnBiometric.visibility = View.GONE
                }
            }
        }
    }

    private fun setBiometric() {
        when (isLoginSso) {
            1 -> {
                val promptInfo = createPromptInfo()
                if (BiometricManager.from(this@LoginBiometricActivity)
                        .canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
                ) {
                    biometricPrompt.authenticate(promptInfo)
                }
            }

            0 -> {
                if (username.isNullOrEmpty() && mPassword.isNullOrEmpty()) {
                    Toast.makeText(
                        this,
                        getString(R.string.silahkan_masukkan_username_dan_password_dulu),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val promptInfo = createPromptInfo()
                    if (BiometricManager.from(this@LoginBiometricActivity)
                            .canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS
                    ) {
                        biometricPrompt.authenticate(promptInfo)
                    }
                }
            }
        }
    }

    private fun initData() {
        mAndroidId = Helper.getAndroidId(this)
        mAppVersion = Helper.getVersionApp(this)
        mDeviceData = Helper.deviceData
        mIpAddress = Helper.getIPAddress(true)
        androidVersion = Build.VERSION.SDK_INT
        dateTimeUtc = DateTimeUtils.currentTimeMillis()

        code =
            SharedPrefsUtils.getStringPreference(this@LoginBiometricActivity, Config.KEY_CODE, "")!!
        isLoginSso = SharedPrefsUtils.getIntegerPreference(
            this@LoginBiometricActivity,
            Config.IS_LOGIN_SSO,
            0
        )
        username = SharedPrefsUtils.getStringPreference(
            this@LoginBiometricActivity,
            Config.KEY_USERNAME,
            ""
        )!!
        mPassword = SharedPrefsUtils.getStringPreference(
            this@LoginBiometricActivity,
            Config.KEY_PASSWORD,
            ""
        )!!
    }

    private fun createBiometricPrompt(): BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(this)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            @SuppressLint("RestrictedApi")
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {

                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()

            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                startActivity(Intent(this@LoginBiometricActivity, HomeActivity::class.java))
                when (isLoginSso) {
                    1 -> {
                        startActivity(
                            Intent(this@LoginBiometricActivity, SsoLoginActivity::class.java)
                                .putExtra(Config.KEY_URL, Config.URL_LOGIN_SSO)
                        )
                        finish()
                    }

                    0 -> {
                        viewModel.getLogin(
                            this@LoginBiometricActivity,
                            daoSession, username, mPassword, "",
                            mAndroidId, mAppVersion, mDeviceData, mIpAddress,
                            androidVersion, dateTimeUtc
                        )
                    }
                }
            }
        }

        val biometricPrompt = BiometricPrompt(this, executor, callback)
        return biometricPrompt
    }

    private fun createPromptInfo(): BiometricPrompt.PromptInfo {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autentikasi Biometric")
            .setSubtitle("Harap letakan jempol anda pada sensor biometric")
            // Authenticate without requiring the user to press a "confirm"
            // button after satisfying the biometric check
            .setConfirmationRequired(false)
            .setNegativeButtonText("Autentikasi manual")
            .build()
        return promptInfo
    }
}