package dev.iconpln.mims.ui.auth.forgot_password

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.R
import dev.iconpln.mims.databinding.ActivityForgotPasswordBinding
import dev.iconpln.mims.ui.auth.AuthViewModel
import dev.iconpln.mims.ui.auth.otp.OtpActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.Helper

class ForgotPasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding
    private val viewModel: AuthViewModel by viewModels()
    private var username = ""
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()

    }

    private fun initData() {
        viewModel.genericResponse.observe(this) {
            when (it.status) {
                Config.KEY_FAILURE -> Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                Config.KEY_SUCCESS -> {
                    startActivity(
                        Intent(this, OtpActivity::class.java)
                            .putExtra(Config.KEY_USERNAME, username)
                            .putExtra(Config.KEY_ANDROID_ID, Helper.getAndroidId(this))
                            .putExtra(Config.KEY_DEVICE_DATA, Helper.deviceData)
                            .putExtra(Config.KEY_OTP_FROM, Config.OTP_FORM_FORGOT_PASSSWORD)
                    )
                }
            }
        }

        viewModel.isLoading.observe(this) {
            when (it) {
                true -> dialog.show()
                false -> dialog.dismiss()
            }
        }
    }

    private fun initView() {
        dialog = Dialog(this@ForgotPasswordActivity)
        dialog.setContentView(R.layout.popup_loading)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown

        with(binding) {
            btnKirim.setOnClickListener {
                tvMsgError.visibility = View.GONE
                validateUsername()
            }
        }
    }

    private fun validateUsername() {
        username = binding.edtUsername.text.toString()
        with(binding) {

            if (username.isNullOrEmpty()) {
                tvMsgError.visibility = View.VISIBLE
                tvMsgError.text = getString(R.string.user_tidak_boleh_kosong)
                return
            }

            sendRequest()
        }
    }

    private fun sendRequest() {
        viewModel.sendOtpForgotPassword(this, username)
    }
}