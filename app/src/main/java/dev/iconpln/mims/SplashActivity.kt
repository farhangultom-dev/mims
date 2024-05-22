package dev.iconpln.mims

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import dev.iconpln.mims.ui.auth.LoginActivity
import dev.iconpln.mims.ui.auth.LoginBiometricActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.SharedPrefsUtils

class SplashActivity : AppCompatActivity() {
    private var isLogin = 0
    private var isLoginBiometric = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        isLoginBiometric = SharedPrefsUtils.getIntegerPreference(
            this@SplashActivity,
            Config.KEY_IS_LOGIN_BIOMETRIC,
            0
        )
        isLogin = SharedPrefsUtils.getIntegerPreference(this@SplashActivity, Config.IS_LOGIN, 0)

        Handler(Looper.getMainLooper()).postDelayed({
            when (isLoginBiometric) {
                1 -> {
                    startActivity(Intent(this, LoginBiometricActivity::class.java))
                    finish()
                }

                0 -> {
                    when (isLogin) {
                        1 -> {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }

                        0 -> {
                            startActivity(Intent(this, LoginActivity::class.java))
                            finish()
                        }
                    }
                }
            }

        }, 1500)
    }
}