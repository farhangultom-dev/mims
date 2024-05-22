package dev.iconpln.mims.ui.auth.otp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dev.iconpln.mims.HomeActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.R
import dev.iconpln.mims.databinding.ActivityOtpBinding
import dev.iconpln.mims.ui.auth.AuthViewModel
import dev.iconpln.mims.ui.auth.change_password.ChangePasswordActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OtpActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOtpBinding
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private var username: String = ""
    private var password: String = ""
    private var androidId: String = ""
    private var deviceData: String = ""
    private var ipAddress: String = ""
    private var otpFrom: String = ""
    private var otpInput: String = ""
    private var isForgetPassword: Boolean = false
    private var isReqOtpBack = false

    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        countdownTimer.start()

        dialog = Dialog(this@OtpActivity)
        dialog.setContentView(R.layout.popup_loading)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown

        daoSession = (application as MyApplication).daoSession!!

        username = intent.getStringExtra(Config.KEY_USERNAME).toString()
        password = intent.getStringExtra(Config.KEY_PASSWORD).toString()
        otpFrom = intent.getStringExtra(Config.KEY_OTP_FROM).toString()
        isForgetPassword = otpFrom.equals(Config.OTP_FORM_FORGOT_PASSSWORD)

        androidId = Helper.getAndroidId(this)
        deviceData = Helper.deviceData
        ipAddress = Helper.getIPAddress(true)

        viewModel.loginResponse.observe(this) {
            when (it.message) {
                Config.DO_LOGIN -> {
                    CoroutineScope(Dispatchers.IO).launch {

                        SharedPrefsUtils.setIntegerPreference(this@OtpActivity, Config.IS_LOGIN, 1)
                        SharedPrefsUtils.setStringPreference(
                            this@OtpActivity,
                            Config.KEY_JWT_WEB,
                            it.webToken!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@OtpActivity,
                            Config.KEY_JWT,
                            it.token!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@OtpActivity,
                            Config.KEY_USERNAME,
                            username
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@OtpActivity,
                            Config.KEY_FULL_NAME,
                            it.user?.fullName!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@OtpActivity,
                            Config.KEY_ROLE_NAME,
                            it.user?.roleName!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@OtpActivity,
                            Config.KEY_EMAIL,
                            it.user?.mail!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@OtpActivity,
                            Config.KEY_PASSWORD,
                            password
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@OtpActivity,
                            Config.KEY_PLANT,
                            it.user?.plant!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@OtpActivity,
                            Config.KEY_PLANT_NAME,
                            it.user?.plantName!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@OtpActivity,
                            Config.KEY_STOR_LOC,
                            it.user?.storloc!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@OtpActivity,
                            Config.KEY_STOR_LOC_NAME,
                            it.user?.storLocName!!
                        )
                        SharedPrefsUtils.setIntegerPreference(
                            this@OtpActivity,
                            Config.KEY_SUBROLE_ID,
                            it.user?.subroleId!!
                        )
                        SharedPrefsUtils.setIntegerPreference(
                            this@OtpActivity,
                            Config.KEY_ROLE_ID,
                            it.user?.roleId!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@OtpActivity,
                            Config.KEY_KODE_USER,
                            it.user.kdUser!!
                        )

                        withContext(Dispatchers.Main) {
                            val intentToHome = Intent(this@OtpActivity, HomeActivity::class.java)
                            startActivity(intentToHome)
                            finish()
                        }
                    }
                }

                Config.OTP_NOT_FOUND -> {
                    Toast.makeText(this, getString(R.string.otp_salah), Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.errorResponse.observe(this) {
            if (it != 200) dialog.dismiss()
        }

        viewModel.checkOtpForgotPassword.observe(this) {
            when (it.message) {
                Config.OTP_TERVALIDASI -> {
                    startActivity(
                        Intent(this, ChangePasswordActivity::class.java)
                            .putExtra(Config.KEY_USERNAME, username)
                            .putExtra("otp", otpInput)
                            .putExtra(
                                Config.KEY_IS_FORGET_PASSWORD,
                                isForgetPassword.compareTo(false)
                            )
                    )
                }

                Config.OTP_NOT_FOUND -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        with(binding) {
            btnSubmitotp.setOnClickListener {
                inputOtp()
            }

            txtBelumMenerimaKode.setOnClickListener {
                if (isReqOtpBack) {
                    Toast.makeText(
                        this@OtpActivity,
                        getString(R.string.otp_berhasil_dikirim),
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.sendOtp(this@OtpActivity, username)
                    countdownTimer.start()
                }
            }

        }

        viewModel.isLoading.observe(this) {
            if (it == true) {
                dialog.show()
            } else {
                dialog.dismiss()
            }
        }

        binding.btnBack.setOnClickListener { onBackPressed() }

        autoNextInput()
    }

    private fun inputOtp() {
        with(binding) {
            if (edtotp1.text.toString().isNullOrEmpty()) {
                Toast.makeText(this@OtpActivity, "kode otp tidak boleh kosong", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            if (edtotp2.text.toString().isNullOrEmpty()) {
                Toast.makeText(this@OtpActivity, "kode otp tidak boleh kosong", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            if (edtotp3.text.toString().isNullOrEmpty()) {
                Toast.makeText(this@OtpActivity, "kode otp tidak boleh kosong", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            if (edtotp4.text.toString().isNullOrEmpty()) {
                Toast.makeText(this@OtpActivity, "kode otp tidak boleh kosong", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            if (edtotp5.text.toString().isNullOrEmpty()) {
                Toast.makeText(this@OtpActivity, "kode otp tidak boleh kosong", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            if (edtotp6.text.toString().isNullOrEmpty()) {
                Toast.makeText(this@OtpActivity, "kode otp tidak boleh kosong", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            otpInput = edtotp1.text.toString() +
                    edtotp2.text.toString() +
                    edtotp3.text.toString() +
                    edtotp4.text.toString() +
                    edtotp5.text.toString() +
                    edtotp6.text.toString()

            when (otpFrom) {
                Config.OTP_FROM_LOGIN -> viewModel.checkOtp(
                    this@OtpActivity,
                    username,
                    otpInput,
                    androidId,
                    deviceData,
                    ipAddress,
                    daoSession
                )

                Config.OTP_FORM_FORGOT_PASSSWORD -> viewModel.checkOtpForgotPassword(
                    this@OtpActivity,
                    username,
                    otpInput
                )
            }
        }
    }

    val countdownTimer = object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val secondsRemaining = millisUntilFinished / 1000
            binding.txtBelumMenerimaKode.text = "Meminta OTP kembali dalam: $secondsRemaining detik"
        }

        override fun onFinish() {
            binding.txtBelumMenerimaKode.text = getString(R.string.minta_otp_kembali)
            isReqOtpBack = true
        }
    }

    private fun autoNextInput() {
        val input1 = binding.edtotp1
        val input2 = binding.edtotp2
        val input3 = binding.edtotp3
        val input4 = binding.edtotp4
        val input5 = binding.edtotp5
        val input6 = binding.edtotp6

        input1.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(input1, InputMethodManager.SHOW_IMPLICIT)

        binding.edtotp1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (input1.text.toString().isNotEmpty()) {
                    input1.clearFocus()
                    input2.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.edtotp2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (input2.text.toString().isNotEmpty()) {
                    input2.clearFocus()
                    input3.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.edtotp3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (input3.text.toString().isNotEmpty()) {
                    input3.clearFocus()
                    input4.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.edtotp4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (input4.text.toString().isNotEmpty()) {
                    input4.clearFocus()
                    input5.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.edtotp5.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (input5.text.toString().isNotEmpty()) {
                    input5.clearFocus()
                    input6.requestFocus()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}