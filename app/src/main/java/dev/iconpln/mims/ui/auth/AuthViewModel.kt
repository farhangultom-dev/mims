package dev.iconpln.mims.ui.auth

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.iconpln.mims.R
import dev.iconpln.mims.data.local.database.DaoSession
import dev.iconpln.mims.data.local.database.TDataRating
import dev.iconpln.mims.data.local.database.TDoStatus
import dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlp
import dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlp
import dev.iconpln.mims.data.local.database.TLokasi
import dev.iconpln.mims.data.local.database.TMaterial
import dev.iconpln.mims.data.local.database.TMaterialDetail
import dev.iconpln.mims.data.local.database.TMaterialGroups
import dev.iconpln.mims.data.local.database.TMonitoringComplaint
import dev.iconpln.mims.data.local.database.TMonitoringComplaintDetail
import dev.iconpln.mims.data.local.database.TMonitoringPermintaan
import dev.iconpln.mims.data.local.database.TMonitoringPermintaanDetail
import dev.iconpln.mims.data.local.database.TMonitoringSnMaterial
import dev.iconpln.mims.data.local.database.TPegawaiUp3
import dev.iconpln.mims.data.local.database.TPejabat
import dev.iconpln.mims.data.local.database.TPemakaian
import dev.iconpln.mims.data.local.database.TPemakaianDetail
import dev.iconpln.mims.data.local.database.TPemeriksaan
import dev.iconpln.mims.data.local.database.TPemeriksaanDetail
import dev.iconpln.mims.data.local.database.TPenerimaanDetailUlp
import dev.iconpln.mims.data.local.database.TPenerimaanUlp
import dev.iconpln.mims.data.local.database.TPenerimaanUlpDao
import dev.iconpln.mims.data.local.database.TPengujian
import dev.iconpln.mims.data.local.database.TPengujianDetails
import dev.iconpln.mims.data.local.database.TPetugasPengujian
import dev.iconpln.mims.data.local.database.TPos
import dev.iconpln.mims.data.local.database.TPosDetail
import dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhir
import dev.iconpln.mims.data.local.database.TPosSns
import dev.iconpln.mims.data.local.database.TPrivilege
import dev.iconpln.mims.data.local.database.TRating
import dev.iconpln.mims.data.local.database.TSnPermaterial
import dev.iconpln.mims.data.remote.response.DashboardResponse
import dev.iconpln.mims.data.remote.response.GenericResponse
import dev.iconpln.mims.data.remote.response.LoginResponse
import dev.iconpln.mims.data.remote.response.SyncDataResponse
import dev.iconpln.mims.data.remote.response.SyncUserDataResponse
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.ui.SsoLogoutActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AuthViewModel : ViewModel() {

    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoadingInitData = MutableLiveData<Boolean>()
    val isLoadingInitData: LiveData<Boolean> = _isLoadingInitData

    private val _loginResponse = MutableLiveData<LoginResponse>()
    val loginResponse: LiveData<LoginResponse> = _loginResponse

    private val _syncUserDataResponse = MutableLiveData<SyncUserDataResponse>()
    val syncUserDataResponse: LiveData<SyncUserDataResponse> = _syncUserDataResponse

    private val _syncDataResponse = MutableLiveData<SyncDataResponse>()
    val syncDataResponse: LiveData<SyncDataResponse> = _syncDataResponse

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _errorResponse = MutableLiveData<Int>()
    val errorResponse: LiveData<Int> = _errorResponse

    private val _checkOtpForgotPassword = MutableLiveData<GenericResponse>()
    val checkOtpForgotPassword: LiveData<GenericResponse> = _checkOtpForgotPassword

    private val _changePassword = MutableLiveData<GenericResponse>()
    val changePassword: LiveData<GenericResponse> = _changePassword

    private val _genericResponse = MutableLiveData<GenericResponse>()
    val genericResponse: LiveData<GenericResponse> = _genericResponse

    private val _dashboardResponse = MutableLiveData<DashboardResponse>()
    val dashboardResponse: LiveData<DashboardResponse> = _dashboardResponse

    private val _nilaiPenerimaanUlp = MutableLiveData<String>()
    val nilaiPenerimaanUlp: LiveData<String> = _nilaiPenerimaanUlp
    private val _nilaiMonitoring = MutableLiveData<String>()
    val nilaiMonitoring: LiveData<String> = _nilaiMonitoring
    private val _nilaiPemakaian = MutableLiveData<String>()
    val nilaiPemakaian: LiveData<String> = _nilaiPemakaian
    private val _nilaiPermintaan = MutableLiveData<String>()
    val nilaiPermintaan: LiveData<String> = _nilaiPermintaan
    private val _nilaiPengiriman = MutableLiveData<String>()
    val nilaiPengiriman: LiveData<String> = _nilaiPengiriman
    private val _nilaiPengujian = MutableLiveData<String>()
    val nilaiPengujian: LiveData<String> = _nilaiPengujian
    private val _nilaiPenerimaanUp3 = MutableLiveData<String>()
    val nilaiPenerimaanUp3: LiveData<String> = _nilaiPenerimaanUp3
    private var _nilaiMonitoringComplaint = MutableLiveData<String>()
    val nilaiMonitoringComplaint: LiveData<String> = _nilaiMonitoringComplaint

    private val _currentBatch = MutableLiveData(1) // Start with batch 1
    val currentBatch: LiveData<Int> = _currentBatch

    fun incrementBatch() {
        _currentBatch.value = _currentBatch.value?.plus(1) ?: 1
    }

    fun getDataForRole(daoSession: DaoSession) {
        _nilaiPenerimaanUlp.value = daoSession.tPenerimaanUlpDao.queryBuilder()
            .whereOr(
                TPenerimaanUlpDao.Properties.StatusPenerimaan.notEq(
                    "DITERIMA"
                ),
                TPenerimaanUlpDao.Properties.StatusPemeriksaan.notEq(
                    "SUDAH DIPERIKSA"
                )
            ).list().size.toString()
        _nilaiMonitoring.value = daoSession.tPosDao.loadAll().size.toString()
        _nilaiPemakaian.value = daoSession.tPemakaianDao.loadAll().size.toString()
        Log.d("AVM", "getDataForRole: cek ah ${nilaiPemakaian.value}")
        _nilaiPermintaan.value =
            daoSession.tMonitoringPermintaanDao.queryBuilder().list().size.toString()
        _nilaiPengiriman.value = daoSession.tPosDao.loadAll().size.toString()
        _nilaiPengujian.value = daoSession.tPengujianDao.loadAll().size.toString()
        _nilaiPenerimaanUp3.value = daoSession.tPosDao.queryBuilder().list().size.toString()
        _nilaiMonitoringComplaint.value =
            daoSession.tMonitoringComplaintDao.loadAll().size.toString()
    }

    fun getLogin(
        context: Context,
        daoSession: DaoSession,
        username: String,
        password: String,
        device_token: String,
        mAndroidId: String,
        mAppVersion: String,
        mDeviceData: String,
        mIpAddress: String,
        androidVersion: Int,
        dateTimeUtc: Long
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val requestBody = mutableMapOf<String, String>()
                requestBody["username"] = username
                requestBody["password"] = password
                requestBody["device_token"] = device_token
                requestBody["android_id"] = mAndroidId
                requestBody["app_version"] = mAppVersion
                requestBody["device_data"] = mDeviceData
                requestBody["datetime_utc"] = dateTimeUtc.toString()
                requestBody["ip_address"] = mIpAddress
                requestBody["android_version"] = androidVersion.toString()

                val response = ApiConfig.getApiService(context).login(requestBody)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        try {
                            if (response.body()?.status == Config.KEY_FAILURE) {
                                Toast.makeText(
                                    context,
                                    response.body()?.message,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                val loginResult = response.body()
                                initDbLocal(daoSession, loginResult!!)
                            }

                        } catch (e: Exception) {
                            _isLoading.value = false
                            Toast.makeText(context, response.body()?.message!!, Toast.LENGTH_SHORT)
                                .show()
                            e.printStackTrace()
                        } finally {
                            _isLoading.value = false
                        }
                    } else {
                        _isLoading.value = false
                        _errorResponse.postValue(response.code())
                        Toast.makeText(
                            context,
                            if (response.body()?.message.isNullOrEmpty()) context.getString(R.string.periksa_vpn_anda)
                            else
                                response.body()?.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun syncUserData(
        context: Context,
        daoSession: DaoSession,
        username: String
    ) {
        _isLoadingInitData.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val response = ApiConfig.getApiService(context)
                    .syncUserData(username)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _isLoadingInitData.postValue(false)
                        try {
                            val isLoginSso =
                                SharedPrefsUtils.getIntegerPreference(
                                    context,
                                    Config.IS_LOGIN_SSO,
                                    0
                                )
                            when (response.body()?.status) {
                                Config.KEY_SUCCESS -> {
                                    val dataResult = response.body()
                                    insertUserDataToDbLocal(daoSession, dataResult!!)
                                }
                                //lanjut force logout di endpoint yang lain
                                Config.KEY_FAILURE -> {
                                    if (response.body()?.message == Config.DO_LOGOUT) {
                                        Helper.logout(context, isLoginSso)
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.session_kamu_telah_habis),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else Toast.makeText(
                                        context,
                                        response.body()?.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } catch (e: Exception) {
                            _isLoadingInitData.value = false
                            Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT)
                                .show()
                            e.printStackTrace()
                        } finally {
                            _isLoadingInitData.value = false
                        }
                    } else {
                        _isLoadingInitData.value = false
                        _errorResponse.postValue(response.code())
                        val error = response.errorBody()?.toString()
                        onError("Error : ${error?.let { getErrorMessage(it) }}")
                        Toast.makeText(
                            context,
                            if (response.body()?.message.isNullOrEmpty()) context.getString(R.string.periksa_vpn_anda)
                            else
                                response.body()?.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }


    fun syncData(
        context: Context,
        daoSession: DaoSession,
        username: String
    ) {
        _isLoadingInitData.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val requestBody = mutableMapOf<String, String>()
                requestBody["username"] = username

                val response = ApiConfig.getApiService(context)
                    .syncData(requestBody)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _isLoadingInitData.postValue(false)
                        try {
                            val isLoginSso =
                                SharedPrefsUtils.getIntegerPreference(
                                    context,
                                    Config.IS_LOGIN_SSO,
                                    0
                                )
                            when (response.body()?.status) {
                                Config.KEY_SUCCESS -> {
                                    val dataResult = response.body()
//                                    insertAllDataToDbLocal(daoSession, dataResult!!)
                                    insertFromSyncToDbLocal(daoSession,dataResult!!)
                                    SharedPrefsUtils.setBooleanPreference(context, "IS_GET_DATA_DONE", true)
                                }
                                //lanjut force logout di endpoint yang lain
                                Config.KEY_FAILURE -> {
                                    if (response.body()?.message == Config.DO_LOGOUT) {
                                        Helper.logout(context, isLoginSso)
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.session_kamu_telah_habis),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else Toast.makeText(
                                        context,
                                        response.body()?.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        } catch (e: Exception) {
                            _isLoadingInitData.value = false
                            Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT)
                                .show()
                            e.printStackTrace()
                        }
                    } else {
                        _isLoadingInitData.value = false
                        _errorResponse.postValue(response.code())
                        val error = response.errorBody()?.toString()
                        onError("Error : ${error?.let { getErrorMessage(it) }}")
                        Toast.makeText(
                            context,
                            if (response.body()?.message.isNullOrEmpty()) context.getString(R.string.periksa_vpn_anda)
                            else
                                response.body()?.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun getLoginSso(
        context: Context,
        daoSession: DaoSession,
        code: String,
        device_token: String,
        mAndroidId: String,
        mAppVersion: String,
        mDeviceData: String,
        mIpAddress: String,
        androidVersion: Int,
        dateTimeUtc: Long
    ) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val requestBody = mutableMapOf<String, String>()
            requestBody["codeIam"] = code
            requestBody["device_token"] = device_token
            requestBody["android_id"] = mAndroidId
            requestBody["app_version"] = mAppVersion
            requestBody["device_data"] = mDeviceData
            requestBody["datetime_utc"] = dateTimeUtc.toString()
            requestBody["ip_address"] = mIpAddress
            requestBody["android_version"] = androidVersion.toString()

            val response = ApiConfig.getApiService(context).loginSso(requestBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    try {
                        _isLoading.value = false
                        if (response.body()?.status == Config.KEY_FAILURE) {
                            Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            val loginResult = response.body()
                            initDbLocal(daoSession, loginResult!!)
                        }
                    } catch (e: Exception) {
                        _isLoading.value = false
                        Toast.makeText(context, response.body()?.message!!, Toast.LENGTH_SHORT)
                            .show()
                        e.printStackTrace()
                    } finally {
                        _isLoading.value = false
                    }
                } else {
                    _isLoading.value = false
                    _errorResponse.postValue(response.code())
                    Toast.makeText(
                        context, if (response.body()?.message.isNullOrEmpty())
                            context.getString(R.string.periksa_vpn_anda)
                        else
                            response.body()?.message.toString(), Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun sendOtp(context: Context, username: String) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val requestBody = mutableMapOf<String, String>()
            requestBody["username"] = username

            val response = ApiConfig.getApiService(context).sendOtp(requestBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    try {
                        _isLoading.value = false
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    _isLoading.value = false
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun checkOtp(
        context: Context,
        username: String,
        otp: String,
        androidId: String,
        deviceData: String,
        ipAddress: String,
        daoSession: DaoSession
    ) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val requestBody = mutableMapOf<String, String>()
            requestBody["username"] = username
            requestBody["otp"] = otp
            requestBody["android_id"] = androidId
            requestBody["device_data"] = deviceData
            requestBody["ip_address"] = ipAddress

            val response = ApiConfig.getApiService(context).otpValid(requestBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    try {
                        val loginResult = response.body()
                        initDbLocal(daoSession, loginResult!!)
                    } catch (e: Exception) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                } else {
                    _isLoading.value = false
                    _errorResponse.postValue(response.code())
                    Toast.makeText(
                        context,
                        response.body()?.message ?: "Terjadi kesalahan",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun sendOtpForgotPassword(context: Context, username: String) {
        _isLoading.value = true
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val requestBody = mutableMapOf<String, String>()
                requestBody["username"] = username

                val response = ApiConfig.getApiService(context).getOtpForgotPassword(requestBody)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val isLoginSso =
                            SharedPrefsUtils.getIntegerPreference(context, Config.IS_LOGIN_SSO, 0)

                        when (response.body()?.status) {
                            Config.KEY_SUCCESS -> {
                                _isLoading.value = false
                                _genericResponse.postValue(response.body())
                            }

                            Config.KEY_FAILURE -> {
                                _isLoading.value = false
                                if (response.body()?.message == Config.DO_LOGOUT) {
                                    Helper.logout(context, isLoginSso)
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.session_kamu_telah_habis),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else Toast.makeText(
                                    context,
                                    response.body()?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        _isLoading.value = false
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: Exception) {
            _isLoading.value = false
            e.printStackTrace()
        }
    }

    fun checkOtpForgotPassword(context: Context, username: String, otp: String) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val requestBody = mutableMapOf<String, String>()
            requestBody["username"] = username
            requestBody["otp"] = otp

            val response = ApiConfig.getApiService(context).otpValidForgotPassword(requestBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    try {
                        _isLoading.value = false
                        val responses = response.body()
                        _checkOtpForgotPassword.postValue(responses!!)
                    } catch (e: Exception) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                } else {
                    _isLoading.value = false
                    Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun changePasswordProfile(
        context: Context,
        username: String,
        password: String,
        oldPassword: String
    ) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val requestBody = mutableMapOf<String, String>()
            requestBody["username"] = username
            requestBody["new_password"] = password
            requestBody["old_password"] = oldPassword

            val response = ApiConfig.getApiService(context).changePasswordProfile(requestBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    try {
                        _isLoading.value = false
                        if (response.body()?.status == Config.KEY_SUCCESS) {
                            val responses = response.body()
                            _changePassword.postValue(responses!!)
                        } else {
                            Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } catch (e: Exception) {
                        _isLoading.value = false
                        e.printStackTrace()
                    }
                } else {
                    _isLoading.value = false
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun forgotPassword(context: Context, otp: String, password: String, username: String) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val requestBody = mutableMapOf<String, String>()
            requestBody["username"] = username
            requestBody["otp"] = otp
            requestBody["new_password"] = password

            val response = ApiConfig.getApiService(context).forgotPassword(requestBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    try {
                        _isLoading.value = false
                        if (response.body()?.status == Config.KEY_SUCCESS) {
                            val responses = response.body()
                            _changePassword.postValue(responses!!)
                        } else {
                            Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    } catch (e: Exception) {
                        _isLoading.value = false
                        e.printStackTrace()
                    }
                } else {
                    _isLoading.value = false
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getDashboard(context: Context, kodePabrikan: String, limit: String, page: String) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiConfig.getApiService(context).getDashboard(kodePabrikan, limit, page)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    try {
                        _isLoading.value = false
                        if (response.body()?.status == Config.KEY_SUCCESS) {
                            val responses = response.body()
                            _dashboardResponse.postValue(responses!!)
                        } else {
                            Toast.makeText(context, response.body()?.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    } catch (e: Exception) {
                        _isLoading.value = false
                        e.printStackTrace()
                    }
                } else {
                    _isLoading.value = false
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun logout(context: Context, token: String, isLoginSso: Int, isLoginBiometric: Int) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {

            val response = ApiConfig.getApiService(context).logout()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    try {
                        _isLoading.value = false
                        if (response.body()?.status == Config.KEY_SUCCESS) {
                            doLogout(context, isLoginSso, isLoginBiometric)
                            Toast.makeText(
                                context,
                                "Logout berhasil",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } catch (e: Exception) {
                        _isLoading.value = false
                        e.printStackTrace()
                    }
                } else {
                    _isLoading.value = false
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun doLogout(context: Context, isLoginSso: Int, isLoginBiometric: Int) {
        when (isLoginSso) {
            1 -> {
                SharedPrefsUtils.clearPreferences(context)
                SharedPrefsUtils.setIntegerPreference(context, Config.KEY_IS_LOGIN_BIOMETRIC, isLoginBiometric)
                context.startActivity(Intent(context, SsoLogoutActivity::class.java))
            }

            0 -> {
                when (isLoginBiometric) {
                    1 -> {
                        val intent = Intent(context, LoginBiometricActivity::class.java)
                        SharedPrefsUtils.clearPreferences(context)
                        SharedPrefsUtils.setIntegerPreference(context, Config.KEY_IS_LOGIN_BIOMETRIC, isLoginBiometric)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }

                    0 -> {
                        SharedPrefsUtils.clearPreferences(context)
                        SharedPrefsUtils.setIntegerPreference(context, Config.KEY_IS_LOGIN_BIOMETRIC, isLoginBiometric)
                        val intent = Intent(context, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    private fun insertUserDataToDbLocal(daoSession: DaoSession, result: SyncUserDataResponse) {
        CoroutineScope(Dispatchers.IO).launch {

//            val privilegeItems = result.privilege?.map { model ->
//                TPrivilege().apply {
//                    isActive = model?.isActive.toString()
//                    methodId = model?.methodId
//                    methodValue = model?.methodValue
//                    moduleId = model?.moduleId
//                    roleId = model?.roleId.toString()
//                }
//            } ?: emptyList()
            val pejabatItems = result.pejabat?.map { model ->
                TPejabat().apply {
                    idPejabat = model?.idPejabat.toString()
                    nama = model?.nama
                }
            } ?: emptyList()
            val ratingItems = result.ratings?.map { model ->
                TRating().apply {
                    kdRating = model?.kdRating
                    nilai = model?.nilai
                    keterangan = model?.keterangan
                    isActive = 0
                    type = model?.type
                }
            } ?: emptyList()
            val pegawaiItems = result.pegawai?.map { model ->
                TPegawaiUp3().apply {
                    nip = model?.nip
                    namaPegawai = model?.namaPegawai
                    namaJabatan = model?.namaJabatan
                    kodeJabatan = model?.kodeJabatan
                    plant = model?.plant
                    isActive = model?.isActive
                }
            } ?: emptyList()

            try {
                daoSession.runInTx {
//                    daoSession.tPrivilegeDao.deleteAll()
                    daoSession.tRatingDao.deleteAll()
                    daoSession.tPegawaiUp3Dao.deleteAll()
                    daoSession.tPejabatDao.deleteAll()
                }

                daoSession.runInTx {
//                    privilegeItems.forEach { data ->
//                        daoSession.tPrivilegeDao.insertInTx(data)
//                    }
                    ratingItems.forEach { data ->
                        daoSession.tRatingDao.insertInTx(data)
                    }
                    pegawaiItems.forEach { data ->
                        daoSession.tPegawaiUp3Dao.insertInTx(data)
                    }
                    pejabatItems.forEach { data ->
                        daoSession.tPejabatDao.insertInTx(data)
                    }
                }
                _syncUserDataResponse.postValue(result)
            } catch (e: Throwable) {
                Log.e("DataInsertionError", "Error inserting data to local DB", e)
            }
        }
    }

    private fun insertAllDataToDbLocal(daoSession: DaoSession, result: SyncDataResponse) {
        CoroutineScope(Dispatchers.IO).launch {

            val posItems = result.pos?.map { model ->
                TPos().apply {
                    createdDate = model?.createdDate
                    leadTime = model?.leadTime
                    storloc = model?.storLoc
                    noDoSmar = model?.noDoSmar
                    planCodeNo = model?.plantCodeNo
                    plantName = model?.plantName
                    poMpNo = model?.poMpNo
                    poSapNo = model?.poSapNo
                    storLoc = model?.storLoc
                    tlskNo = model?.tlskNo
                    total = model?.total
                    kodeStatusDoMims = model?.kodeStatusDoMims
                    kdPabrikan = model?.kdPabrikan
                    materialGroup = model?.materialGroup
                    namaKategoriMaterial = model?.namaKategoriMaterial
                    noDoMims = model?.noDoMims
                    tglDiterima = model?.TglSerahTerima
                    kurirPengantar = model?.KurirPengirim
                    petugasPenerima = model?.PetugasPenerima
                    doLineItem = model?.DoLineItem
                    doStatus = model?.doStatus
                    expeditions = model?.ekspedition
                    poDate = model?.poDate
                    sudahBisaRating = model?.sudahBisaRating
                    ratingDelivery = model?.ratingDelivery
                    ratingQuality = model?.ratingQuality
                    ratingResponse = model?.ratingResponse
                    statusPemeriksaan =
                        if (model?.statusPemeriksaan.isNullOrEmpty()) "" else model?.statusPemeriksaan
                    statusPenerimaan =
                        if (model?.statusPenerimaan.isNullOrEmpty()) "" else model?.statusPenerimaan
                    isBabg = model?.isBabg
                    isBabgConfirm = model?.isBabgConfirm
                    slaIntegrasiSap = model?.slaIntegrasiSap
                    eta = model?.eta
                    etd = model?.etd
                    backgroundColor = model?.palleteBackground
                    textColor = model?.palleteText
                    namaPabrikan = model?.namaPabrikan
                    courierPersonName = model?.courierPersonName
                }
            } ?: emptyList()

            val posDetailItems = result.posDetail?.map { model ->
                TPosDetail().apply {
                    doStatus = model?.doStatus
                    kdPabrikan = model?.kdPabrikan
                    noDoSmar = model?.noDoSmar
                    noPackaging = model?.noPackaging
                    noMatSap = model?.noMatSap
                    qty = model?.qty
                    leadTime = model?.leadTime.toString()
                    noDoMims = model?.noDoMims
                    plantCodeNo = model?.plantCodeNo
                    poMpNo = model?.poMpNo
                    plantName = model?.plantName
                    poSapNo = model?.poSapNo
                    storLoc = model?.storLoc
                    uom = model?.uom
                    createdDate = model?.createdDate
                    noPemeriksaan = ""
                    namaPabrikan = model?.namaPabrikan
                }
            } ?: emptyList()

            val dataRatingItems = result.dataRatings?.map { model ->
                TDataRating().apply {
                    noDoSmar = model?.noDoSmar
                    ratingQuality = model?.ratingQuality
                    ratingDelivery = model?.ratingDelivery
                    ketepatan = model?.ketepatan
                    ratingResponse = model?.ratingResponse
                    selesaiRating = model?.selesaiRating
                    noRating = model?.noRating
                }
            } ?: emptyList()

            val monitoringPermintaanItems = result.monitoringPermintaan?.map { model ->
                TMonitoringPermintaan().apply {
                    createdDate = model?.createdDate
                    plant = model?.plant
                    plantName = model?.plantName
                    noTransaksi = model?.noTransaksi
                    createdBy = model?.createdBy
                    jumlahKardus = model?.jumlahKardus ?: 0
                    kodePengeluaran = model?.kodePengeluaran.toString()
                    noPermintaan = model?.noPermintaan
                    noRepackaging = model?.noRepackaging
                    storLocAsal = model?.storLocAsal
                    storLocAsalName = model?.storLocAsalName
                    storLocTujuan = model?.storLocTujuan
                    storLocTujuanName = model?.storLocTujuanName
                    tanggalPengeluaran = model?.tanggalPengeluaran.toString()
                    tanggalPermintaan = model?.tanggalPermintaan
                    updatedBy = model?.updatedBy
                    updatedDate = model?.updatedDate
                    valuationType = model?.valuationType
                    totalQtyPermintaan = model?.totalQtyPermintaan
                    totalScanQty = model?.totalScanQty
                    noPengiriman = model?.noPengiriman
                }
            } ?: emptyList()

            val pemerikaanItems = result.pemeriksan?.map { model ->
                TPemeriksaan().apply {
                    noPemeriksaan =
                        if (model?.noPemeriksaan.isNullOrEmpty()) "" else model?.noPemeriksaan
                    storLoc = model?.storloc
                    total = model?.total
                    tlskNo = model?.tlskNo
                    poSapNo = model?.poSapNo
                    poMpNo = model?.poMpNo
                    noDoSmar = model?.noDoSmar
                    leadTime = model?.leadTime
                    createdDate = model?.createdDate
                    planCodeNo = model?.plantCodeNo
                    plantName = model?.plantName
                    noDoMims = model?.noDoMims
                    doStatus = model?.doStatus
                    statusPemeriksaan =
                        if (model?.statusPemeriksaan.isNullOrEmpty()) "" else model?.statusPemeriksaan
                    expeditions = model?.ekspedition
                    courierPersonName = model?.courierPersonName
                    kdPabrikan = model?.kdPabrikan
                    materialGroup = model?.materialGroup
                    namaKategoriMaterial = model?.namaKategoriMaterial
                    tanggalDiterima = model?.tglSerahTerima
                    petugasPenerima = model?.petugasPenerima
                    namaPabrikan = model?.namaPabrikan
                    namaKurir = model?.courierPersonName
                    namaEkspedisi = ""
                    doLineItem = model?.doLineItem
                    namaKetua =
                        if (model?.ketuaPemeriksa.isNullOrEmpty()) "" else model?.ketuaPemeriksa
                    namaManager =
                        if (model?.managerPemeriksa.isNullOrEmpty()) model?.managerPemeriksa else ""
                    namaAnggota =
                        if (model?.anggota_pemeriksa_1.isNullOrEmpty()) model?.anggota_pemeriksa_1 else ""
                    namaSekretaris =
                        if (model?.sekretaris_pemeriksa.isNullOrEmpty()) model?.sekretaris_pemeriksa else ""
                    namaAnggotaBaru =
                        if (model?.anggota_pemeriksa_2.isNullOrEmpty()) model?.anggota_pemeriksa_2 else ""
                    isDone = 0
                }
            } ?: emptyList()

            val monitoringComplaintItems = result.monitoringKomplain?.map { model ->
                TMonitoringComplaint().apply {
                    noDoSmar = model?.noDoSmar
                    qty = model?.qty
                    poSapNo = model?.poSapNo
                    status = model?.status
                    alasan = model?.alasan
                    noKomplain = model?.noKomplain
                    noKomplainSmar = model?.noKomplainSmar
                    plantName = model?.plantName
                    tanggalSelesai =
                        if (model?.finishDate.isNullOrEmpty()) "" else model?.finishDate
                    tanggalPO = model?.tanggalPo
                }
            } ?: emptyList()

            val pemeriksaanDetailItems = result.pemeriksaanDetail?.map { model ->
                TPemeriksaanDetail().apply {
                    noPemeriksaan =
                        if (model?.noPemeriksaan.isNullOrEmpty()) "" else model?.noPemeriksaan
                    sn = model?.noSerial
                    noDoSmar = model?.noDoSmar
                    noMaterail = model?.noMatSap
                    noPackaging = model?.noPackaging
                    kategori = model?.namaKategoriMaterial
                    statusPenerimaan = "" //belum perlu ditarik
                    statusPemeriksaan = model?.status
                    if (model?.status == "BELUM DIPERIKSA") {
                        isPeriksa = 1
                        isComplaint = 0
                        isChecked = 0
                    }
                    if (model?.status == "KOMPLAIN") {
                        isPeriksa = 0
                        isComplaint = 1
                        isChecked = 1
                    }
                    if (model?.status.isNullOrEmpty()) {
                        isPeriksa = 0
                        isComplaint = 0
                        isChecked = 0
                    }
                    isDone = 0
                }
            } ?: emptyList()

            val materialDetailItems = result.materialDetails?.map { model ->
                TMaterialDetail().apply {
                    kdPabrikan = model?.kdPabrikan
                    masaGaransi = model?.masaGaransi
                    mmc = model?.mmc
                    materialId = model?.materialId.toString()
                    materialGroup = model?.materialGroup.toString()
                    noPackaging = model?.noPackaging.toString()
                    noProduksi = model?.noProduksi
                    nomorMaterial = model?.nomorMaterial
                    nomorSertMaterologi = model?.nomorSertMaterologi
                    plant = model?.plant.toString()
                    serialNumber = model?.serialNumber
                    spesifikasi = model?.spesifikasi
                    spln = model?.spln
                    storloc = model?.storloc.toString()
                    tglProduksi = model?.tglProduksi
                    namaKategoriMaterial = model?.namaKategoriMaterial
                    tahun = model?.tahun.toString()

                }
            } ?: emptyList()

            val materialItems = result.materials?.map { model ->
                TMaterial().apply {
                    masaGaransi = model?.masaGaransi
                    mmc = model?.mmc
                    materialId = model?.materialId.toString()
                    materialGroup = model?.materialGroup.toString()
                    nomorMaterial = model?.nomorMaterial
                    nomorSertMaterologi = model?.nomorSertMaterologi
                    plant = model?.plant.toString()
                    storloc = model?.storloc.toString()
                    tglProduksi = model?.tglProduksi
                    noProduksi = model?.noProduksi
                    kdPabrikan = model?.kdPabrikan
                    namaKategoriMaterial = model?.namaKategoriMaterial
                    tahun = model?.tahun.toString()
                }
            } ?: emptyList()

            val pengujianItems = result.pengujians?.map { model ->
                TPengujian().apply {
                    kdPabrikan = model?.kdPabrikan
                    namaKategori = model?.namaKategori
                    noPengujian = model?.noPengujian
                    qtyMaterial = model?.qtyMaterial
                    qtyLolos = model?.qtyLolos
                    statusUji = model?.statusUji
                    qtyRusak = model?.qtyRusak.toString()
                    qtyTdkLolos = model?.qtyTdkLolos.toString()
                    tanggalUji = model?.tglUji.toString()
                    tanggalUsulUji = model?.tanggalUsulUji
                    unit = model?.unit

                }
            } ?: emptyList()

            val pengujianDetailItems = result.pengujianDetails?.map { model ->
                TPengujianDetails().apply {
                    serialNumber = model?.serialNumber
                    namaKategori = model?.namaKategori
                    noPengujian = model?.noPengujian
                    statusUji = model?.statusUji
                    keteranganMaterial = model?.keteranganMaterial
                }
            } ?: emptyList()

            val materialGroupItems = result.materialGroups?.map { model ->
                TMaterialGroups().apply {
                    materialGroup = model?.materialGroup
                    namaKategoriMaterial = model?.namaKategoriMaterial
                }
            } ?: emptyList()

            val posSnsItems = result.posSns?.map { model ->
                TPosSns().apply {
                    doStatus = model?.doStatus
                    kdPabrikan = model?.kdPabrikan
                    masaGaransi = model?.masaGaransi
                    mmc = model?.mmc
                    materialId = model?.materialId
                    namaKategoriMaterial = model?.namaKategoriMaterial
                    noDoSmar = model?.noDoSmar
                    noMatSap = model?.noMatSap
                    noProduksi = model?.noProduksi
                    noSerial = model?.noSerial
                    noSertMeterologi = model?.nomorSertMaterologi
                    plant = model?.plant
                    spesifikasi = model?.spesifikasi
                    spln = model?.spln
                    statusPenerimaan =
                        if (model?.statusPenerimaan.isNullOrEmpty()) "" else model?.statusPenerimaan
                    statusPemeriksaan =
                        if (model?.statusPemeriksaan.isNullOrEmpty()) "" else model?.statusPemeriksaan
                    storLoc = model?.storloc
                    tglProduksi = model?.tglProduksi
                    noPackaging = model?.noPackaging
                    doLineItem = model?.doLineItem
                    namaPabrikan = model?.namaPabrikan
                }
            } ?: emptyList()

            val snPermaterialItems = result.snPermaterial?.map { model ->
                TSnPermaterial().apply {
                    doStatus = model?.doStatus
                    kdPabrikan = model?.kdPabrikan
                    masaGaransi = model?.masaGaransi
                    mmc = model?.mmc
                    materialId = model?.materialId
                    namaKategoriMaterial = model?.namaKategoriMaterial
                    noDoSmar = model?.noDoSmar
                    noMatSap = model?.noMatSap
                    noProduksi = model?.noProduksi
                    noSerial = model?.noSerial
                    noSertMeterologi = model?.nomorSertMaterologi
                    plant = model?.plant
                    spesifikasi = model?.spesifikasi
                    spln = model?.spln
                    status = if (model?.status.isNullOrEmpty()) "" else model?.status
                    storLoc = model?.storloc
                    tglProduksi = model?.tglProduksi
                    noPackaging = model?.noPackaging
                    doLineItem = model?.doLineItem
                }
            } ?: emptyList()

            val lokasisItems = result.lokasis?.map { model ->
                TLokasi().apply {
                    idLokasi = model?.id
                    noDoSns = model?.noDoMims
                    ket = model?.ket
                    updateDate = model?.updatedDate
                }
            } ?: emptyList()

            val monitoringPermintaanDetailItems = result.monitoringPermintaanDetails?.map { model ->
                TMonitoringPermintaanDetail().apply {
                    unit = model?.unit
                    nomorMaterial = model?.nomorMaterial
                    kategori = model?.kategori
                    materialDesc = model?.materialDesc
                    noPermintaan = model?.noPermintaan
                    noTransaksi = model?.noTransaksi
                    noRepackaging = model?.noRepackaging
                    qtyPengeluaran = model?.qtyPengeluaran
                    qtyPermintaan = model?.qtyPermintaan ?: 0.0
                    isActive = model?.isActive
                    qtyScan = model?.qtyScan
                    valuationType = model?.valuationType
                    jumlahKardus = model?.jumlahKardus
                }
            } ?: emptyList()

            val penerimaanUlpItems = result.penerimaanUlp?.map { model ->
                TPenerimaanUlp().apply {
                    noPengiriman = model?.noPengiriman
                    noPermintaan = model?.noPermintaan
                    statusPemeriksaan = model?.statusPemeriksaan
                    deliveryDate = model?.tanggalPengiriman
                    statusPenerimaan = model?.statusPenerimaan
                    jumlahKardus = model?.jumlahKardus
                    gudangAsal = model?.storLocAsalName
                    noRepackaging = model?.noRepackaging
                    gudangTujuan = model?.storLocTujuanName
                    tanggalPemeriksaan = model?.tanggalPemeriksaan
                    tanggalPenerimaan = model?.tanggalPenerimaan
                    kepalaGudangPemeriksa = model?.kepalaGudang
                    pejabatPemeriksa = model?.namaPemeriksa1
                    jabatanPemeriksa = model?.jabatanPemeriksa1
                    namaPetugasPemeriksa = model?.namaPemeriksa2
                    jabatanPetugasPemeriksa = model?.jabatanPemeriksa2
                    kepalaGudangPenerima = model?.kepalaGudang
                    noPk = model?.noPk
                    tanggalDokumen = model?.tanggalDokumen
                    pejabatPenerima = model?.pejabatPenerima
                    kurir = model?.kurir
                    noNota = model?.noNota
                    noMaterial = model?.nomorMaterial
                    spesifikasi = model?.materialDesc
                    kuantitasPeriksa = model?.qtyPemeriksaan
                    kuantitas = model?.qtyPenerimaan
                }
            } ?: emptyList()

            val penerimaanDetailUlpItems = result.penerimaanDetailUlp?.map { model ->
                TPenerimaanDetailUlp().apply {
                    noRepackaging = model?.noRepackaging
                    noTransaksi = model?.noTransaksi
                    qtyPenerimaan = model?.qtyPenerimaan
                    materialDesc = model?.materialDesc
                    noMaterial = model?.nomorMaterial
                    qtyPemeriksaan = model?.qtyPemeriksaan
                    qtyPengiriman = model?.qtyPengiriman
                    qtyPermintaan = model?.qtyPermintaan
                    qtySesuai = model?.qtySesuai
                    valuationType = model?.valuationType
                    isActive = model?.isActive
                }
            } ?: emptyList()

            val snPermintaanItems = result.snPermintaan?.map { model ->
                TMonitoringSnMaterial().apply {
                    noRepackaging = model?.noRepackaging
                    nomorMaterial = model?.nomorMaterial
                    serialNumber = model?.serialNumber
                    valuationType = model?.valuationType
                    noIdMeter = model?.noIdMeter
                    status = model?.status
                    isScanned = 0
                }
            } ?: emptyList()

            val pemakaianItems = result.pemakaian?.map { model ->
                TPemakaian().apply {
                    plant = model?.plant
                    storLoc = model?.storLoc
                    daya = model?.daya
                    noTransaksi = model?.noTransaksi
                    alamatPelanggan = model?.alamatPelanggan
                    idPelanggan = model?.idPelanggan
                    jenisPekerjaan = model?.jenisPekerjaan
                    kodeIntegrasi = model?.kodeIntegrasi
                    namaPelanggan = model?.namaPelanggan
                    noAgenda = model?.noAgenda
                    noPemesanan = model?.noPemesanan
                    noReservasi = model?.noReservasi
                    statusKirimAgo = model?.statusKirimAgo.toString()
                    statusPemakaian = model?.statusPemakaian
                    daya = model?.daya
                    alamatPelanggan = model?.alamatPelanggan
                    statusSap = model?.statusSap.toString()
                    noTransaksi = model?.noTransaksi
                    sumber = model?.sumber
                    tanggalBayar = model?.tanggalBayar.toString()
                    tanggalDokumen = model?.tanggalDokumen
                    tanggalPemakaian = model?.tanggalPemakaian
                    tanggalPengeluaran = model?.tanggalPengeluaran
                    tanggalReservasi = model?.tanggalReservasi
                    tarif = model?.tarif
                    noPk = model?.noPk
                    namaKegiatan = model?.namaKegiatan
                    lokasi = model?.lokasi
                    pemeriksa = model?.pemeriksa
                    penerima = model?.penerima
                    kepalaGudang = model?.kepalaGudang
                    noBon = model?.noBon
                    isDonePemakai = 0
                    isDone = 0
                }
            } ?: emptyList()

            val pemakaianDetailItems = result.pemakaianDetail?.map { model ->
                TPemakaianDetail().apply {
                    nomorMaterial = model?.nomorMaterial
                    noTransaksi = model?.noTransaksi
                    unit = model?.unit
                    keterangan = model?.keterangan
                    namaMaterial = model?.namaMaterial
                    noMeter = model?.noMeter
                    qtyPemakaian = model?.qtyPemakaian
                    qtyPengeluaran = model?.qtyPengeluaran
                    qtyReservasi = model?.qtyReservasi
                    valuationType = model?.valuationType
                    isActive = model?.isActive
                }
            } ?: emptyList()

            val snPenerimaanUlpItems = result.snPenerimaanUlp?.map { model ->
                TListSnMaterialPenerimaanUlp().apply {
                    status = ""
                    noRepackaging = model?.noRepackaging
                    noMaterial = model?.nomorMaterial
                    noSerialNumber = model?.serialNumber
                    isScanned = 0
                }
            } ?: emptyList()

            val snPemakaianUlpItems = result.snPemakaianUlp?.map { model ->
                TListSnMaterialPemakaianUlp().apply {
                    noTransaksi = model?.noTransaksi
                    noMaterial = model?.nomorMaterial
                    noSerialNumber = model?.serialNumber
                    isScanned = 0
                }
            } ?: emptyList()

            val petugasPengujianItems = result.petugasPengujian?.map { model ->
                TPetugasPengujian().apply {
                    jabatan = model?.jabatan
                    nip = model?.nip
                    namaPetugas = model?.namaPetugas
                    noPengujian = model?.noPengujian
                }
            } ?: emptyList()

            val monitoringComplaintDetailItems = result.monitoringKomplainDetail?.map { model ->
                TMonitoringComplaintDetail().apply {
                    doLineItem = model?.doLineItem
                    status = model?.status
                    noPackaging = model?.noPackaging
                    noMatSap = model?.noMatSap
                    noKomplain = model?.noKomplain
                    noSerial = model?.noSerial
                    tanggalPengajuan = model?.tanggalPengajuan
                    tanggalSelesai = ""
                    noDoSmar = model?.noDoSmar
                    isChecked = 0
                    isDone = 0
                    statusPeriksa = ""
                }
            } ?: emptyList()

            val dataPenerimaanAkhirItems = result.dataPenerimaanAkhir?.map { model ->
                TPosDetailPenerimaanAkhir().apply {
                    kdPabrikan = model?.kdPabrikan
                    noDoSmar = model?.noDoSmar
                    qty = model?.qtyDo.toString()
                    storLoc = model?.storLoc
                    isComplaint = model?.isKomplained
                    isReceived = model?.isReceived
                    isRejected = model?.isRejected
                    namaKategoriMaterial = model?.namaKategoriMaterial
                    status = model?.status
                    noMaterial = model?.noMatSap
                    noPackaging = model?.noPackaging
                    serialNumber = model?.noSerial
                    namaPabrikan = model?.namaPabrikan
                }
            } ?: emptyList()

            val masterDoStatusItems = result.masterDoStatus?.map { model ->
                TDoStatus().apply {
                    kodeDo = model?.kodeDo
                    keterangan = model?.keterangan
                    backgroundColor = model?.backgroundColor
                    textColor = model?.textColor

                }
            } ?: emptyList()

            val pejabatItems = result.pejabat?.map { model ->
                TPejabat().apply {
                    idPejabat = model?.idPejabat.toString()
                    nama = model?.nama

                }
            } ?: emptyList()

            try {

                daoSession.runInTx {
                    pemerikaanItems.forEach { data ->
                        daoSession.tPemeriksaanDao.insertInTx(data)
                    }
                    posItems.forEach { data ->
                        daoSession.tPosDao.insertInTx(data)
                    }
                    posDetailItems.forEach { data ->
                        daoSession.tPosDetailDao.insertInTx(data)
                    }
                    monitoringPermintaanItems.forEach { data ->
                        daoSession.tMonitoringPermintaanDao.insertInTx(data)
                    }
                    monitoringComplaintItems.forEach { data ->
                        daoSession.tMonitoringComplaintDao.insertInTx(data)
                    }
                    dataRatingItems.forEach { data ->
                        daoSession.tDataRatingDao.insertInTx(data)
                    }
                    pemeriksaanDetailItems.forEach { data ->
                        daoSession.tPemeriksaanDetailDao.insertInTx(data)
                    }
                    materialDetailItems.forEach { data ->
                        daoSession.tMaterialDetailDao.insertInTx(data)
                    }
                    materialItems.forEach { data ->
                        daoSession.tMaterialDao.insertInTx(data)
                    }
                    pengujianItems.forEach { data ->
                        daoSession.tPengujianDao.insertInTx(data)
                    }
                    pengujianDetailItems.forEach { data ->
                        daoSession.tPengujianDetailsDao.insertInTx(data)
                    }
                    materialGroupItems.forEach { data ->
                        daoSession.tMaterialGroupsDao.insertInTx(data)
                    }
                    posSnsItems.forEach { data ->
                        daoSession.tPosSnsDao.insertInTx(data)
                    }
                    snPermaterialItems.forEach { data ->
                        daoSession.tSnPermaterialDao.insertInTx(data)
                    }
                    lokasisItems.forEach { data ->
                        daoSession.tLokasiDao.insertInTx(data)
                    }
                    monitoringPermintaanDetailItems.forEach { data ->
                        daoSession.tMonitoringPermintaanDetailDao.insertInTx(data)
                    }
                    penerimaanUlpItems.forEach { data ->
                        daoSession.tPenerimaanUlpDao.insertInTx(data)
                    }
                    penerimaanDetailUlpItems.forEach { data ->
                        daoSession.tPenerimaanDetailUlpDao.insertInTx(data)
                    }
                    snPermintaanItems.forEach { data ->
                        daoSession.tMonitoringSnMaterialDao.insertInTx(data)
                    }
                    pemakaianItems.forEach { data ->
                        daoSession.tPemakaianDao.insertInTx(data)
                    }
                    pemakaianDetailItems.forEach { data ->
                        daoSession.tPemakaianDetailDao.insertInTx(data)
                    }
                    snPenerimaanUlpItems.forEach { data ->
                        daoSession.tListSnMaterialPenerimaanUlpDao.insertInTx(data)
                    }
                    snPemakaianUlpItems.forEach { data ->
                        daoSession.tListSnMaterialPemakaianUlpDao.insertInTx(data)
                    }
                    petugasPengujianItems.forEach { data ->
                        daoSession.tPetugasPengujianDao.insertInTx(data)
                    }
                    monitoringComplaintDetailItems.forEach { data ->
                        daoSession.tMonitoringComplaintDetailDao.insertInTx(data)
                    }
                    dataPenerimaanAkhirItems.forEach { data ->
                        daoSession.tPosDetailPenerimaanAkhirDao.insertInTx(data)
                    }
                    masterDoStatusItems.forEach { data ->
                        daoSession.tDoStatusDao.insertInTx(data)
                    }
                    pejabatItems.forEach { data ->
                        daoSession.tPejabatDao.insertInTx(data)
                    }
                }
                _syncDataResponse.postValue(result)
            } catch (e: Throwable) {
                Log.e("DataInsertionError", "Error inserting data to local DB", e)
            }
        }
    }

    private fun insertFromSyncToDbLocal(daoSession: DaoSession, result: SyncDataResponse) {
        CoroutineScope(Dispatchers.IO).launch {

            val posItems = result.pos?.map { model ->
                TPos().apply {
                    createdDate = model?.createdDate
                    leadTime = model?.leadTime
                    storloc = model?.storLoc
                    noDoSmar = model?.noDoSmar
                    planCodeNo = model?.plantCodeNo
                    plantName = model?.plantName
                    poMpNo = model?.poMpNo
                    poSapNo = model?.poSapNo
                    storLoc = model?.storLoc
                    tlskNo = model?.tlskNo
                    total = model?.total
                    kodeStatusDoMims = model?.kodeStatusDoMims
                    kdPabrikan = model?.kdPabrikan
                    materialGroup = model?.materialGroup
                    namaKategoriMaterial = model?.namaKategoriMaterial
                    noDoMims = model?.noDoMims
                    tglDiterima = model?.TglSerahTerima
                    kurirPengantar = model?.KurirPengirim
                    petugasPenerima = model?.PetugasPenerima
                    doLineItem = model?.DoLineItem
                    doStatus = model?.doStatus
                    expeditions = model?.ekspedition
                    poDate = model?.poDate
                    sudahBisaRating = model?.sudahBisaRating
                    ratingDelivery = model?.ratingDelivery
                    ratingQuality = model?.ratingQuality
                    ratingResponse = model?.ratingResponse
                    statusPemeriksaan =
                        if (model?.statusPemeriksaan.isNullOrEmpty()) "" else model?.statusPemeriksaan
                    statusPenerimaan =
                        if (model?.statusPenerimaan.isNullOrEmpty()) "" else model?.statusPenerimaan
                    isBabg = model?.isBabg
                    isBabgConfirm = model?.isBabgConfirm
                    slaIntegrasiSap = model?.slaIntegrasiSap
                    eta = model?.eta
                    etd = model?.etd
                    backgroundColor = model?.palleteBackground
                    textColor = model?.palleteText
                    namaPabrikan = model?.namaPabrikan
                    courierPersonName = model?.courierPersonName
                }
            } ?: emptyList()

            val posDetailItems = result.posDetail?.map { model ->
                TPosDetail().apply {
                    doStatus = model?.doStatus
                    kdPabrikan = model?.kdPabrikan
                    noDoSmar = model?.noDoSmar
                    noPackaging = model?.noPackaging
                    noMatSap = model?.noMatSap
                    qty = model?.qty
                    leadTime = model?.leadTime.toString()
                    noDoMims = model?.noDoMims
                    plantCodeNo = model?.plantCodeNo
                    poMpNo = model?.poMpNo
                    plantName = model?.plantName
                    poSapNo = model?.poSapNo
                    storLoc = model?.storLoc
                    uom = model?.uom
                    createdDate = model?.createdDate
                    noPemeriksaan = ""
                    namaPabrikan = model?.namaPabrikan
                }
            } ?: emptyList()

            val dataRatingItems = result.dataRatings?.map { model ->
                TDataRating().apply {
                    noDoSmar = model?.noDoSmar
                    ratingQuality = model?.ratingQuality
                    ratingDelivery = model?.ratingDelivery
                    ketepatan = model?.ketepatan
                    ratingResponse = model?.ratingResponse
                    selesaiRating = model?.selesaiRating
                    noRating = model?.noRating
                }
            } ?: emptyList()

            val monitoringPermintaanItems = result.monitoringPermintaan?.map { model ->
                TMonitoringPermintaan().apply {
                    createdDate = model?.createdDate
                    plant = model?.plant
                    plantName = model?.plantName
                    noTransaksi = model?.noTransaksi
                    createdBy = model?.createdBy
                    jumlahKardus = model?.jumlahKardus ?: 0
                    kodePengeluaran = model?.kodePengeluaran.toString()
                    noPermintaan = model?.noPermintaan
                    noRepackaging = model?.noRepackaging
                    storLocAsal = model?.storLocAsal
                    storLocAsalName = model?.storLocAsalName
                    storLocTujuan = model?.storLocTujuan
                    storLocTujuanName = model?.storLocTujuanName
                    tanggalPengeluaran = model?.tanggalPengeluaran.toString()
                    tanggalPermintaan = model?.tanggalPermintaan
                    updatedBy = model?.updatedBy
                    updatedDate = model?.updatedDate
                    valuationType = model?.valuationType
                    totalQtyPermintaan = model?.totalQtyPermintaan
                    totalScanQty = model?.totalScanQty
                    noPengiriman = model?.noPengiriman
                }
            } ?: emptyList()

            val pemerikaanItems = result.pemeriksan?.map { model ->
                TPemeriksaan().apply {
                    noPemeriksaan =
                        if (model?.noPemeriksaan.isNullOrEmpty()) "" else model?.noPemeriksaan
                    storLoc = model?.storloc
                    total = model?.total
                    tlskNo = model?.tlskNo
                    poSapNo = model?.poSapNo
                    poMpNo = model?.poMpNo
                    noDoSmar = model?.noDoSmar
                    leadTime = model?.leadTime
                    createdDate = model?.createdDate
                    planCodeNo = model?.plantCodeNo
                    plantName = model?.plantName
                    noDoMims = model?.noDoMims
                    doStatus = model?.doStatus
                    statusPemeriksaan =
                        if (model?.statusPemeriksaan.isNullOrEmpty()) "" else model?.statusPemeriksaan
                    expeditions = model?.ekspedition
                    courierPersonName = model?.courierPersonName
                    kdPabrikan = model?.kdPabrikan
                    materialGroup = model?.materialGroup
                    namaKategoriMaterial = model?.namaKategoriMaterial
                    tanggalDiterima = model?.tglSerahTerima
                    petugasPenerima = model?.petugasPenerima
                    namaPabrikan = model?.namaPabrikan
                    namaKurir = model?.courierPersonName
                    namaEkspedisi = ""
                    doLineItem = model?.doLineItem
                    namaKetua =
                        if (model?.ketuaPemeriksa.isNullOrEmpty()) "" else model?.ketuaPemeriksa
                    namaManager =
                        if (model?.managerPemeriksa.isNullOrEmpty()) model?.managerPemeriksa else ""
                    namaAnggota =
                        if (model?.anggota_pemeriksa_1.isNullOrEmpty()) model?.anggota_pemeriksa_1 else ""
                    namaSekretaris =
                        if (model?.sekretaris_pemeriksa.isNullOrEmpty()) model?.sekretaris_pemeriksa else ""
                    namaAnggotaBaru =
                        if (model?.anggota_pemeriksa_2.isNullOrEmpty()) model?.anggota_pemeriksa_2 else ""
                    isDone = 0
                }
            } ?: emptyList()

            val monitoringComplaintItems = result.monitoringKomplain?.map { model ->
                TMonitoringComplaint().apply {
                    noDoSmar = model?.noDoSmar
                    qty = model?.qty
                    poSapNo = model?.poSapNo
                    status = model?.status
                    alasan = model?.alasan
                    noKomplain = model?.noKomplain
                    noKomplainSmar = model?.noKomplainSmar
                    plantName = model?.plantName
                    tanggalSelesai =
                        if (model?.finishDate.isNullOrEmpty()) "" else model?.finishDate
                    tanggalPO = model?.tanggalPo
                }
            } ?: emptyList()

            try {

                daoSession.runInTx {
                    pemerikaanItems.forEach { data ->
                        daoSession.tPemeriksaanDao.insertInTx(data)
                    }
                    posItems.forEach { data ->
                        daoSession.tPosDao.insertInTx(data)
                    }
                    posDetailItems.forEach { data ->
                        daoSession.tPosDetailDao.insertInTx(data)
                    }
                    monitoringPermintaanItems.forEach { data ->
                        daoSession.tMonitoringPermintaanDao.insertInTx(data)
                    }
                    monitoringComplaintItems.forEach { data ->
                        daoSession.tMonitoringComplaintDao.insertInTx(data)
                    }
                    dataRatingItems.forEach { data ->
                        daoSession.tDataRatingDao.insertInTx(data)
                    }
                }
                _syncDataResponse.postValue(result)
            } catch (e: Throwable) {
                Log.e("DataInsertionError", "Error inserting data to local DB", e)
            }
        }
    }
    private fun initDbLocal(
        daoSession: DaoSession,
        result: LoginResponse
    ) {
        try {

            daoSession.runInTx {
                daoSession.tLokasiDao.deleteAll()
                daoSession.tMaterialDao.deleteAll()
                daoSession.tMaterialDetailDao.deleteAll()
                daoSession.tPosDao.deleteAll()
                daoSession.tPengujianDao.deleteAll()
                daoSession.tPengujianDetailsDao.deleteAll()
                daoSession.tPosDetailDao.deleteAll()
                daoSession.tMaterialGroupsDao.deleteAll()
                daoSession.tPrivilegeDao.deleteAll()
                daoSession.tPosSnsDao.deleteAll()
                daoSession.tPemeriksaanDao.deleteAll()
                daoSession.tPemeriksaanDetailDao.deleteAll()
                daoSession.tPosDetailPenerimaanDao.deleteAll()
                daoSession.tPosPenerimaanDao.deleteAll()
                daoSession.tRatingDao.deleteAll()
                daoSession.tMonitoringPermintaanDao.deleteAll()
                daoSession.tMonitoringPermintaanDetailDao.deleteAll()
                daoSession.tTransMonitoringPermintaanDao.deleteAll()
                daoSession.tTransMonitoringPermintaanDetailDao.deleteAll()
                daoSession.tSnMonitoringPermintaanDao.deleteAll()
                daoSession.tPenerimaanUlpDao.deleteAll()
                daoSession.tPenerimaanDetailUlpDao.deleteAll()
                daoSession.tTransPenerimaanUlpDao.deleteAll()
                daoSession.tTransPenerimaanDetailUlpDao.deleteAll()
                daoSession.tSnPermaterialDao.deleteAll()
                daoSession.tListSnMaterialPenerimaanUlpDao.deleteAll()
                daoSession.tListSnMaterialPemakaianUlpDao.deleteAll()
                daoSession.tPemakaianDao.deleteAll()
                daoSession.tPemakaianDetailDao.deleteAll()
                daoSession.tListSnMaterialPenerimaanUlpDao.deleteAll()
                daoSession.tListSnMaterialPemakaianUlpDao.deleteAll()
                daoSession.tDataRatingDao.deleteAll()
                daoSession.tPetugasPengujianDao.deleteAll()
                daoSession.tPhotoDao.deleteAll()
                daoSession.tMonitoringComplaintDao.deleteAll()
                daoSession.tMonitoringComplaintDetailDao.deleteAll()
                daoSession.tMonitoringSnMaterialDao.deleteAll()
                daoSession.tPegawaiUp3Dao.deleteAll()
                daoSession.tPosDetailPenerimaanAkhirDao.deleteAll()
                daoSession.tPejabatDao.deleteAll()
            }

            result.privilege?.takeIf { it.isNotEmpty() }.let { privilegeList ->
                val privilegeItems =
                    mutableListOf<TPrivilege>()
                privilegeList?.forEach { model ->
                    val item = TPrivilege().apply {
                        isActive = model?.isActive.toString()
                        methodId = model?.methodId
                        methodValue = model?.methodValue
                        moduleId = model?.moduleId
                        roleId = model?.roleId.toString()
                    }
                    privilegeItems.add(item)
                }
                daoSession.tPrivilegeDao.insertInTx(privilegeItems)
            }

            _loginResponse.postValue(result)
            _isLoading.value = false

        } catch (e: Exception) {
            e.printStackTrace()
            _loginResponse.postValue(result)
        }
    }

    fun deleteAllDbLocal(daoSession: DaoSession){
        daoSession.runInTx {
            // ... delete operations from multiple tables
            daoSession.tLokasiDao.deleteAll()
            daoSession.tMaterialDao.deleteAll()
            daoSession.tMaterialDetailDao.deleteAll()
            daoSession.tPosDao.deleteAll()
            daoSession.tPengujianDao.deleteAll()
            daoSession.tPengujianDetailsDao.deleteAll()
            daoSession.tPosDetailDao.deleteAll()
            daoSession.tMaterialGroupsDao.deleteAll()
            daoSession.tPosSnsDao.deleteAll()
            daoSession.tPemeriksaanDao.deleteAll()
            daoSession.tPemeriksaanDetailDao.deleteAll()
            daoSession.tPosDetailPenerimaanDao.deleteAll()
            daoSession.tPosPenerimaanDao.deleteAll()
            daoSession.tMonitoringPermintaanDao.deleteAll()
            daoSession.tMonitoringPermintaanDetailDao.deleteAll()
            daoSession.tTransMonitoringPermintaanDao.deleteAll()
            daoSession.tTransMonitoringPermintaanDetailDao.deleteAll()
            daoSession.tSnMonitoringPermintaanDao.deleteAll()
            daoSession.tPenerimaanUlpDao.deleteAll()
            daoSession.tPenerimaanDetailUlpDao.deleteAll()
            daoSession.tTransPenerimaanUlpDao.deleteAll()
            daoSession.tTransPenerimaanDetailUlpDao.deleteAll()
            daoSession.tSnPermaterialDao.deleteAll()
            daoSession.tListSnMaterialPenerimaanUlpDao.deleteAll()
            daoSession.tListSnMaterialPemakaianUlpDao.deleteAll()
            daoSession.tPemakaianDao.deleteAll()
            daoSession.tPemakaianDetailDao.deleteAll()
            daoSession.tListSnMaterialPenerimaanUlpDao.deleteAll()
            daoSession.tListSnMaterialPemakaianUlpDao.deleteAll()
            daoSession.tDataRatingDao.deleteAll()
            daoSession.tPetugasPengujianDao.deleteAll()
            daoSession.tPhotoDao.deleteAll()
            daoSession.tMonitoringComplaintDao.deleteAll()
            daoSession.tMonitoringComplaintDetailDao.deleteAll()
            daoSession.tMonitoringSnMaterialDao.deleteAll()
            daoSession.tPosDetailPenerimaanAkhirDao.deleteAll()
            daoSession.tPejabatDao.deleteAll()
        }
    }

    private fun onError(message: String) {
        _isLoading.postValue(false)
        _errorMessage.postValue(message)
    }

    private fun getErrorMessage(raw: String): String {
        return try {
            val obj = JSONObject(raw)
            obj.getString("message")
        } catch (e: Exception) {
            "Error occurred: Unable to parse error message"
        }
    }
}