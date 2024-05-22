package dev.iconpln.mims.ui

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.HomeActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.R
import dev.iconpln.mims.databinding.ActivitySsoLoginBinding
import dev.iconpln.mims.ui.auth.AuthViewModel
import dev.iconpln.mims.ui.auth.LoginActivity
import dev.iconpln.mims.ui.auth.otp.OtpActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTimeUtils

class SsoLoginActivity : BaseActivity() {
    private lateinit var binding: ActivitySsoLoginBinding
    private lateinit var stringUrl: String

    private val loginViewModel: AuthViewModel by viewModels()
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession

    private var mAndroidId: String = ""
    private var mAppVersion: String = ""
    private var mDeviceData: String = ""
    private var mIpAddress: String = ""
    private var androidVersion: Int = 0
    private var dateTimeUtc: Long = 0L

    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySsoLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        stringUrl = intent.getStringExtra(Config.KEY_URL)!!

        dialog = Dialog(this@SsoLoginActivity)
        dialog.setContentView(R.layout.popup_loading)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown


        mAndroidId = Helper.getAndroidId(this)
        mAppVersion = Helper.getVersionApp(this)
        mDeviceData = Helper.deviceData
        mIpAddress = Helper.getIPAddress(true)
        androidVersion = Build.VERSION.SDK_INT
        dateTimeUtc = DateTimeUtils.currentTimeMillis()

        loginViewModel.loginResponse.observe(this) {
            when (it.message) {
                Config.DO_LOGIN -> {
                    CoroutineScope(Dispatchers.IO).launch {

                        SharedPrefsUtils.setIntegerPreference(
                            this@SsoLoginActivity,
                            Config.IS_LOGIN,
                            1
                        )
                        SharedPrefsUtils.setIntegerPreference(
                            this@SsoLoginActivity,
                            Config.IS_LOGIN_SSO,
                            1
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@SsoLoginActivity,
                            Config.KEY_JWT_WEB,
                            it.webToken!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@SsoLoginActivity,
                            Config.KEY_JWT,
                            it.token!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@SsoLoginActivity,
                            Config.KEY_USERNAME,
                            it.user?.userName!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@SsoLoginActivity,
                            Config.KEY_FULL_NAME,
                            it.user?.fullName!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@SsoLoginActivity,
                            Config.KEY_ROLE_NAME,
                            it.user?.roleName!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@SsoLoginActivity,
                            Config.KEY_EMAIL,
                            it.user?.mail!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@SsoLoginActivity,
                            Config.KEY_PLANT,
                            it.user?.plant!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@SsoLoginActivity,
                            Config.KEY_PLANT_NAME,
                            it.user?.plantName!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@SsoLoginActivity,
                            Config.KEY_STOR_LOC,
                            it.user?.storloc!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@SsoLoginActivity,
                            Config.KEY_STOR_LOC_NAME,
                            it.user?.storLocName!!
                        )
                        SharedPrefsUtils.setIntegerPreference(
                            this@SsoLoginActivity,
                            Config.KEY_SUBROLE_ID,
                            it.user?.subroleId!!
                        )
                        SharedPrefsUtils.setIntegerPreference(
                            this@SsoLoginActivity,
                            Config.KEY_ROLE_ID,
                            it.user?.roleId!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@SsoLoginActivity,
                            Config.KEY_KODE_USER,
                            it.user.kdUser!!
                        )

                        withContext(Dispatchers.Main) {
                            startActivity(
                                Intent(this@SsoLoginActivity, HomeActivity::class.java)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                    .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            )
                            finish()
                        }
                    }
                }

                Config.DO_OTP -> {
                    loginViewModel.sendOtp(this@SsoLoginActivity, it.user?.userName!!)
                    val intentToOtp = Intent(this@SsoLoginActivity, OtpActivity::class.java)
                    intentToOtp.putExtra(Config.KEY_USERNAME, it.user?.userName!!)
                    intentToOtp.putExtra(Config.KEY_OTP_FROM, Config.OTP_FROM_LOGIN)
                    startActivity(intentToOtp)
                    finish()
                }
            }
        }

        loginViewModel.errorResponse.observe(this) {
            if (it != 200) {
                dialog.dismiss()
                val intent = Intent(this@SsoLoginActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        loginViewModel.isLoading.observe(this) {
            when (it) {
                true -> {
                    binding.wvSso.visibility = View.VISIBLE
                    binding.bgLoading.visibility = View.VISIBLE
                    dialog.show()
                }

                false -> {
                    binding.wvSso.visibility = View.GONE
                    binding.bgLoading.visibility = View.GONE
                    dialog.dismiss()
                }
            }
        }

        with(binding) {
            wvSso.settings.javaScriptEnabled = true

            wvSso.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    progressBar.visibility = View.GONE
                    super.onPageFinished(view, url)
                }

                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    view?.loadUrl(url!!)
                    return true
                }

                override fun doUpdateVisitedHistory(
                    view: WebView?,
                    url: String?,
                    isReload: Boolean
                ) {
                    val uri = Uri.parse(url)
                    val code = uri.getQueryParameter(Config.KEY_CODE)

                    if (!code.isNullOrEmpty()) {
                        SharedPrefsUtils.setStringPreference(
                            this@SsoLoginActivity,
                            Config.KEY_CODE,
                            code
                        )
                        doLoginWithSso(code)
                    }

                    super.doUpdateVisitedHistory(view, url, isReload)
                }
            }
            wvSso.loadUrl(stringUrl)
        }
    }

    private fun doLoginWithSso(code: String) {
        daoSession = (application as MyApplication).daoSession!!
        loginViewModel.getLoginSso(
            this@SsoLoginActivity,
            daoSession, code, "",
            mAndroidId, mAppVersion, mDeviceData, mIpAddress,
            androidVersion, dateTimeUtc
        )
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}