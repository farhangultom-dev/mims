package dev.iconpln.mims.ui.auth

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.HomeActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.R
import dev.iconpln.mims.data.local.database_local.ReportUploader
import dev.iconpln.mims.databinding.ActivityLoginBinding
import dev.iconpln.mims.tasks.CheckUpdateTask
import dev.iconpln.mims.tasks.Loadable
import dev.iconpln.mims.ui.SsoLoginActivity
import dev.iconpln.mims.ui.auth.forgot_password.ForgotPasswordActivity
import dev.iconpln.mims.ui.auth.otp.OtpActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.ConnectionDetectorUtil
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import dev.iconpln.mims.utils.StorageUtils
import dev.iconpln.mims.utils.StorageUtils.isPermissionAllowed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTimeUtils


class LoginActivity : BaseActivity(), Loadable {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: AuthViewModel by viewModels()
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private var progressDialog: ProgressDialog? = null

    private var username: String = ""
    private var password: String = ""
    private var mAndroidId: String = ""
    private var mAppVersion: String = ""
    private var mDeviceData: String = ""
    private var mIpAddress: String = ""
    private var androidVersion: Int = 0
    private var dateTimeUtc: Long = 0L
    private var isRememberMe = false
    private var APP_STORAGE_ACCESS_REQUEST_CODE = 501

    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        val intent = Intent(this@LoginActivity, MonitoringStokActivity::class.java)
//        startActivity(intent)

        isRememberMe = SharedPrefsUtils.getBooleanPreference(
            this@LoginActivity,
            Config.KEY_IS_REMEMBER_ME,
            false
        )

        dialog = Dialog(this@LoginActivity)
        dialog.setContentView(R.layout.popup_loading)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown

        requestPermission()

        mAndroidId = Helper.getAndroidId(this)
        mAppVersion = Helper.getVersionApp(this)
        mDeviceData = Helper.deviceData
        mIpAddress = Helper.getIPAddress(true)
        androidVersion = Build.VERSION.SDK_INT
        dateTimeUtc = DateTimeUtils.currentTimeMillis()

        Log.d(
            "LoginActivity",
            "cek param : $mAndroidId $mAppVersion $mDeviceData $mIpAddress $androidVersion $dateTimeUtc"
        )

        loginViewModel.loginResponse.observe(this) {
            when (it.message) {
                Config.DO_LOGIN -> {
                    CoroutineScope(Dispatchers.IO).launch {

                        SharedPrefsUtils.setIntegerPreference(
                            this@LoginActivity,
                            Config.IS_LOGIN,
                            1
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginActivity,
                            Config.KEY_JWT_WEB,
                            it.webToken!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginActivity,
                            Config.KEY_JWT,
                            it.token!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginActivity,
                            Config.KEY_USERNAME,
                            username
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginActivity,
                            Config.KEY_FULL_NAME,
                            it.user?.fullName!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginActivity,
                            Config.KEY_ROLE_NAME,
                            it.user?.roleName!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginActivity,
                            Config.KEY_EMAIL,
                            it.user?.mail!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginActivity,
                            Config.KEY_PASSWORD,
                            password
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginActivity,
                            Config.KEY_PLANT,
                            it.user?.plant!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginActivity,
                            Config.KEY_PLANT_NAME,
                            it.user?.plantName!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginActivity,
                            Config.KEY_STOR_LOC,
                            it.user?.storloc!!
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginActivity,
                            Config.KEY_STOR_LOC_NAME,
                            it.user?.storLocName!!
                        )
                        SharedPrefsUtils.setIntegerPreference(
                            this@LoginActivity,
                            Config.KEY_SUBROLE_ID,
                            it.user?.subroleId!!
                        )
                        SharedPrefsUtils.setIntegerPreference(
                            this@LoginActivity,
                            Config.KEY_ROLE_ID,
                            it.user?.roleId!!
                        )
                        SharedPrefsUtils.setIntegerPreference(
                            this@LoginActivity,
                            Config.IS_LOGIN_SSO,
                            0
                        )
                        SharedPrefsUtils.setStringPreference(
                            this@LoginActivity,
                            Config.KEY_KODE_USER,
                            it.user.kdUser!!
                        )
                        SharedPrefsUtils.setBooleanPreference(
                            this@LoginActivity,
                            "isSyncDataInitialized",
                            false
                        )

                        withContext(Dispatchers.Main) {
                            val intentToHome = Intent(this@LoginActivity, HomeActivity::class.java)
                            startActivity(intentToHome)
                            finish()
                        }
                    }
                }

                Config.DO_OTP -> {
                    loginViewModel.sendOtp(this@LoginActivity, username)
                    val intentToOtp = Intent(this@LoginActivity, OtpActivity::class.java)
                    intentToOtp.putExtra(Config.KEY_USERNAME, username)
                    intentToOtp.putExtra(Config.KEY_PASSWORD, password)
                    intentToOtp.putExtra(Config.KEY_OTP_FROM, Config.OTP_FROM_LOGIN)
                    startActivity(intentToOtp)
                    finish()
                }
            }
        }

        loginViewModel.errorResponse.observe(this) {
            if (it != 200) dialog.dismiss()
        }

        loginViewModel.isLoading.observe(this) {
            when (it) {
                true -> {
                    dialog.show();
                }

                false -> {
                    dialog.dismiss()
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            binding.tvMsgError.visibility = View.GONE
            loginUser()
        }

        binding.btnUpdateVersion.setOnClickListener {
            val connectionDetectorUtil = ConnectionDetectorUtil(this@LoginActivity)
            if (connectionDetectorUtil.isConnectingToInternet) {
                checkForUpdate()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.info_msg_no_network_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.txtForgetPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }

        binding.cbRememberMe.setOnCheckedChangeListener { buttonView, isChecked ->
            SharedPrefsUtils.setBooleanPreference(
                this@LoginActivity,
                Config.KEY_IS_REMEMBER_ME,
                isChecked
            )
        }

        if (isRememberMe) {
            binding.cbRememberMe.isChecked = true
            binding.edtEmail.setText(
                SharedPrefsUtils.getStringPreference(
                    this@LoginActivity,
                    Config.KEY_USERNAME,
                    ""
                )
            )
        } else {
            binding.cbRememberMe.isChecked = false
        }

        binding.btnLoginSso.setOnClickListener {
            val url = Config.URL_LOGIN_SSO
            startActivity(
                Intent(this@LoginActivity, SsoLoginActivity::class.java)
                    .putExtra(Config.KEY_URL, url)
            )
        }

        binding.tvVersion.text = "${getString(R.string.version)}$mAppVersion"

    }

    private fun checkForUpdate() {
        val dialog2 = Dialog(this@LoginActivity)
        dialog2.setContentView(R.layout.popup_validation)
        dialog2.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog2.setCancelable(false)
        dialog2.window!!.attributes.windowAnimations = R.style.DialogUpDown
        val icon = dialog2.findViewById(R.id.imageView11) as ImageView
        val btnOk = dialog2.findViewById(R.id.btn_ya) as AppCompatButton
        val btnTidak = dialog2.findViewById(R.id.btn_tidak) as AppCompatButton
        val message = dialog2.findViewById(R.id.message) as TextView
        val txtMessage = dialog2.findViewById(R.id.txt_message) as TextView

        icon.setImageResource(R.drawable.ic_doc_diterima)
        message.text = getString(R.string.update_version_apps)
        txtMessage.text = getString(R.string.confirm_msg_update_app_version)


        btnTidak.setOnClickListener {
            dialog2.dismiss()
        }

        btnOk.setOnClickListener {
            dialog2.dismiss()
            val task = CheckUpdateTask(this@LoginActivity, Helper.getAbi())
            task.execute()
        }
        dialog2.show();
    }

    private fun requestPermission() {
        if (!isPermissionAllowed(this@LoginActivity)) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                Toast.makeText(
                    this,
                    getString(R.string.izinkan_aplikasi_mengakses_penyimpanan),
                    Toast.LENGTH_SHORT
                ).show();
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data =
                        Uri.parse(String.format("package:%s", applicationContext.packageName))
                    startActivityForResult(intent, APP_STORAGE_ACCESS_REQUEST_CODE)
                } catch (e: Exception) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                    startActivityForResult(intent, APP_STORAGE_ACCESS_REQUEST_CODE)
                }
            } else {
                //below android 11
                ActivityCompat.requestPermissions(
                    this@LoginActivity,
                    arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
                    APP_STORAGE_ACCESS_REQUEST_CODE
                )
            }
        }

    }


    private fun initDaoSession() {
        if ((application as MyApplication).daoSession == null) {
            val database =
                StorageUtils.getDirectory(StorageUtils.DIRECTORY_DATABASE) + "/" + Config.DATABASE_NAME
            val helper =
                dev.iconpln.mims.data.local.database.DaoMaster.DevOpenHelper(this, database)
            val db =
                helper.getEncryptedWritableDb(Config.KEY_ENCRYPTION_LOCAL_DB)//helper.writableDb
            (application as MyApplication).daoSession =
                dev.iconpln.mims.data.local.database.DaoMaster(
                    db
                ).newSession()
        }
    }

    private fun loginUser() {
        with(binding) {
            username = edtEmail.text.toString().trim()
            password = edtPass.text.toString().trim()

            if (username.isEmpty()) {
                tvMsgError.visibility = View.VISIBLE
                tvMsgError.text = getString(R.string.user_tidak_boleh_kosong)
                return
            }

            if (password.isEmpty()) {
                tvMsgError.visibility = View.VISIBLE
                tvMsgError.text = getString(R.string.password_tidak_boleh_kosong)
                return
            }

            if (password.length < 5) {
                tvMsgError.visibility = View.VISIBLE
                tvMsgError.text = getString(R.string.minimal_char_password)
                return
            }

            daoSession = (application as MyApplication).daoSession!!
            loginViewModel.getLogin(
                this@LoginActivity,
                daoSession, username, password, "",
                mAndroidId, mAppVersion, mDeviceData, mIpAddress,
                androidVersion, dateTimeUtc
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == APP_STORAGE_ACCESS_REQUEST_CODE) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    StorageUtils.createDirectories()
                    initDaoSession()
                    dev.iconpln.mims.data.local.database_local.DatabaseReport.getDatabase(this)
                    val iService = Intent(applicationContext, ReportUploader::class.java)
                    startService(iService)
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.izinkan_aplikasi_mengakses_penyimpanan),
                        Toast.LENGTH_SHORT
                    ).show();
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            APP_STORAGE_ACCESS_REQUEST_CODE -> if (grantResults.size > 0) {
                val READ_EXTERNAL_STORAGE = grantResults[0] === PackageManager.PERMISSION_GRANTED
                val WRITE_EXTERNAL_STORAGE = grantResults[1] === PackageManager.PERMISSION_GRANTED
                if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                    StorageUtils.createDirectories()
                    initDaoSession()
                    dev.iconpln.mims.data.local.database_local.DatabaseReport.getDatabase(this)
                    val iService = Intent(applicationContext, ReportUploader::class.java)
                    startService(iService)
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.izinkan_aplikasi_mengakses_penyimpanan),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun setLoading(show: Boolean, title: String, message: String) {
        try {
            if (progressDialog == null) progressDialog = ProgressDialog(this)
            progressDialog!!.setTitle(title)
            progressDialog!!.setMessage(message)
            progressDialog!!.setCancelable(false)
            if (show) {
                progressDialog!!.show()
            } else {
                progressDialog!!.dismiss()
            }
        } catch (e: Exception) {
            progressDialog!!.dismiss()
            e.printStackTrace()
        }
    }

    override fun setFinish(result: Boolean, message: String) {}
}
