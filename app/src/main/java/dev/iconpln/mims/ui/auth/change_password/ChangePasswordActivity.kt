package dev.iconpln.mims.ui.auth.change_password

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.R
import dev.iconpln.mims.databinding.ActivityChangePasswordBinding
import dev.iconpln.mims.ui.auth.AuthViewModel
import dev.iconpln.mims.ui.auth.LoginActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.SharedPrefsUtils

class ChangePasswordActivity : BaseActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var username: String
    private lateinit var dialog: Dialog
    private var usernameFp = ""
    private var isForgetPassword = false
    private var otp = ""
    private val uppercasePattern = "[A-Z]"
    private val lowercasePattern = "[a-z]"
    private val numberPattern = "[0-9]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
    }

    private fun initData() {
        username = SharedPrefsUtils.getStringPreference(this, Config.KEY_USERNAME, "")!!
        otp =
            if (intent.getStringExtra("otp").isNullOrEmpty()) "" else intent.getStringExtra("otp")!!

        usernameFp = intent.getStringExtra(Config.KEY_USERNAME).toString()
        isForgetPassword = intent.getIntExtra(Config.KEY_IS_FORGET_PASSWORD, 0) == 1
    }

    private fun initView() {
        dialog = Dialog(this@ChangePasswordActivity)
        dialog.setContentView(R.layout.popup_loading)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown

        viewModel.isLoading.observe(this) {
            if (it) dialog.show() else dialog.dismiss()
        }

        viewModel.changePassword.observe(this) {
            when (it.status) {
                Config.KEY_SUCCESS -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    Toast.makeText(
                        this@ChangePasswordActivity,
                        getString(R.string.silahkan_login_pass_baru),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }

                else -> Toast.makeText(
                    this@ChangePasswordActivity,
                    getString(R.string.ada_kesalahan_mengganti_password),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        with(binding) {
            btnKirim.setOnClickListener {
                tvMsgError.visibility = View.GONE
                val passwordBaru = edtPasswordBaru.text.toString()
                val konfPasswordBaru = edtKonfirmasiPasswordBaru.text.toString()
                val passwordSaatIni = edtPasswordSaatIni.text.toString()

                if (isForgetPassword) {
                    when {
                        passwordBaru.isNullOrEmpty() -> {
                            tvMsgError.visibility = View.VISIBLE
                            tvMsgError.text = getString(R.string.password_baru_tidak_boleh_kosong)
                        }

                        konfPasswordBaru.isNullOrEmpty() -> {
                            tvMsgError.visibility = View.VISIBLE
                            tvMsgError.text = getString(R.string.masukkan_konfirmasi_password)
                        }

                        passwordBaru != konfPasswordBaru -> {
                            tvMsgError.visibility = View.VISIBLE
                            tvMsgError.text =
                                getString(R.string.password_konfirmasi_password_tidak_cocok)
                        }

                        else -> {
                            val containsUppercase =
                                Regex(uppercasePattern).containsMatchIn(passwordBaru)
                            val containsLowercase =
                                Regex(lowercasePattern).containsMatchIn(passwordBaru)
                            val containsNumber = Regex(numberPattern).containsMatchIn(passwordBaru)
                            val containsSymbol = passwordBaru.matches(".*\\W.*".toRegex())
                            val lengthPassword = passwordBaru.length >= 8

                            if (containsUppercase && containsLowercase && containsNumber && lengthPassword && containsSymbol) {
                                viewModel.forgotPassword(
                                    this@ChangePasswordActivity,
                                    otp,
                                    passwordBaru,
                                    usernameFp
                                )
                            } else {
                                tvMsgError.visibility = View.VISIBLE
                                tvMsgError.text =
                                    getString(R.string.password_harus_mengandung_huruf_besar_kecil_dan_angka)
                            }
                        }
                    }
                } else {
                    when {
                        passwordSaatIni.isNullOrEmpty() -> {
                            tvMsgError.visibility = View.VISIBLE
                            tvMsgError.text =
                                getString(R.string.password_saat_ini_tidak_boleh_kosong)
                        }

                        passwordBaru.isNullOrEmpty() -> {
                            tvMsgError.visibility = View.VISIBLE
                            tvMsgError.text = getString(R.string.password_baru_tidak_boleh_kosong)
                        }

                        konfPasswordBaru.isNullOrEmpty() -> {
                            tvMsgError.visibility = View.VISIBLE
                            tvMsgError.text = getString(R.string.masukkan_konfirmasi_password)
                        }

                        passwordBaru != konfPasswordBaru -> {
                            tvMsgError.visibility = View.VISIBLE
                            tvMsgError.text =
                                getString(R.string.password_konfirmasi_password_tidak_cocok)
                        }

                        else -> {
                            val containsUppercase =
                                Regex(uppercasePattern).containsMatchIn(passwordBaru)
                            val containsLowercase =
                                Regex(lowercasePattern).containsMatchIn(passwordBaru)
                            val containsNumber = Regex(numberPattern).containsMatchIn(passwordBaru)
                            val containsSymbol = passwordBaru.matches(".*\\W.*".toRegex())
                            val lengthPassword = passwordBaru.length >= 8

                            if (containsUppercase && containsLowercase && containsNumber && lengthPassword && containsSymbol) {
                                viewModel.changePasswordProfile(
                                    this@ChangePasswordActivity,
                                    username,
                                    passwordBaru,
                                    passwordSaatIni
                                )
                            } else {
                                tvMsgError.visibility = View.VISIBLE
                                tvMsgError.text =
                                    getString(R.string.password_harus_mengandung_huruf_besar_kecil_dan_angka)
                            }
                        }
                    }
                }


            }
        }
        initForgetPassword()
    }

    private fun initForgetPassword() {
        if (isForgetPassword) {
            with(binding) {
                textView2.visibility = View.GONE
                textInputLayout.visibility = View.GONE
                lblPasswordSaatIni.visibility = View.GONE
            }
        }
    }
}