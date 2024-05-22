package dev.iconpln.mims.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.databinding.ActivitySsoLogoutBinding
import dev.iconpln.mims.ui.auth.LoginActivity
import dev.iconpln.mims.ui.auth.LoginBiometricActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.SharedPrefsUtils

class SsoLogoutActivity : BaseActivity() {
    private lateinit var binding: ActivitySsoLogoutBinding
    private var isLoginBiometric = 0
    private var stringUrl = ""
    private var idTokenSso = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySsoLogoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        isLoginBiometric = SharedPrefsUtils.getIntegerPreference(
            this@SsoLogoutActivity,
            Config.KEY_IS_LOGIN_BIOMETRIC, 0
        )
        idTokenSso =
            SharedPrefsUtils.getStringPreference(this@SsoLogoutActivity, "idTokenSso", "")!!
        stringUrl =
            "https://iam.pln.co.id/svc-core/oauth2/session/end?post_logout_redirect_uri=http://localhost:8080/user/logout&id_token_hint=$idTokenSso"

        setWebView()
    }

    private fun setWebView() {
        with(binding) {
            try {
                wvLogout.settings.javaScriptEnabled = true
                wvLogout.webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
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
                        if (url != stringUrl) {
                            wvLogout.visibility = View.GONE
                            logout
                        }
                        super.doUpdateVisitedHistory(view, url, isReload)
                    }
                }
                wvLogout.loadUrl(stringUrl)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val logout: () -> Unit by lazy {
        when (isLoginBiometric) {
            1 -> {
                val intent = Intent(this@SsoLogoutActivity, LoginBiometricActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }

            0 -> {
                SharedPrefsUtils.clearPreferences(this@SsoLogoutActivity)
                val intent = Intent(this@SsoLogoutActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
        {}
    }
}