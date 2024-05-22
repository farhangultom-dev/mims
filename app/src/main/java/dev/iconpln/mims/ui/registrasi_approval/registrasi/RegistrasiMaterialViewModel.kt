package dev.iconpln.mims.ui.registrasi_approval.registrasi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.remote.response.CheckSerialNumberResponse
import dev.iconpln.mims.data.remote.response.InsertMaterialRegistrasiResponse
import dev.iconpln.mims.data.remote.response.MonitoringAktivasiMaterialResponse
import dev.iconpln.mims.data.remote.response.NewRegistrasiSnResponse
import dev.iconpln.mims.data.remote.response.RequestBodyCheckSerialNumber
import dev.iconpln.mims.data.remote.response.RequestBodyRegisSn
import dev.iconpln.mims.data.remote.service.ApiService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class RegistrasiMaterialViewModel(private val apiService: ApiService) : ViewModel() {

    var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _monitAktivMaterial = MutableLiveData<MonitoringAktivasiMaterialResponse?>()
    val monitAktivMaterial: LiveData<MonitoringAktivasiMaterialResponse?> = _monitAktivMaterial

    private val _insertMaterialRegistrasiByScan =
        MutableLiveData<InsertMaterialRegistrasiResponse?>()
    val insertMaterialRegistrasiByScan: LiveData<InsertMaterialRegistrasiResponse?> =
        _insertMaterialRegistrasiByScan

    private val _insertMaterialRegistrasi = MutableLiveData<InsertMaterialRegistrasiResponse?>()
    val insertMaterialRegistrasi: LiveData<InsertMaterialRegistrasiResponse?> =
        _insertMaterialRegistrasi

    private val _checkSerialNumber = MutableLiveData<CheckSerialNumberResponse?>()
    val checkSerialNumber: LiveData<CheckSerialNumberResponse?> = _checkSerialNumber

    private val _newGetRegistrasiSn = MutableLiveData<NewRegistrasiSnResponse?>()
    val newGetRegistrasiSn: LiveData<NewRegistrasiSnResponse?> = _newGetRegistrasiSn


    fun getCheckSerialNumber(
        plantCode: String = "",
        storLok: String = "",
        requestBodyCheckSerialNumber: RequestBodyCheckSerialNumber
    ) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response =
                apiService.checkSerialNumber(plantCode, storLok, requestBodyCheckSerialNumber)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val checkSerialNumberResponse = response.body()
                    _checkSerialNumber.postValue(checkSerialNumberResponse)
                } else {
                    _isLoading.postValue(false)
                    val error = response.errorBody()?.string()
                    onError("Error : ${error?.let { getErrorMessage(it) }}")
                }
            }
        }
    }

    fun getNewSerialNumber(plantCode: String, storLok: String, status: String) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = apiService.getMonitoringAktivasiMaterialWeb(
                vendor = "",
                starDate = "",
                endDate = "",
                status = status,
                type = "REGISTRASI",
                plantCode = plantCode,
                storLok,
                pageIn = 1,
                pageSize = 20,
                sorted = "DESC"
            )
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val newRegistrasiSnResponse = response.body()
                    _newGetRegistrasiSn.postValue(newRegistrasiSnResponse)
                } else {
                    _isLoading.postValue(false)
                    val error = response.errorBody()?.string()
                    onError("Error : ${error?.let { getErrorMessage(it) }}")
                }
            }
        }
    }


    fun getMonitoringMaterial(status: String, sn: String = "") {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = apiService.getMonitoringAktivasiMaterial(status, sn, "REGISTRASI")
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val monitAktivMaterialResponse = response.body()
                    _monitAktivMaterial.postValue(monitAktivMaterialResponse)
                } else {
                    _isLoading.postValue(false)
                    val error = response.errorBody()?.string()
                    onError("Error : ${error?.let { getErrorMessage(it) }}")
                }
            }
        }
    }

    fun setInsertMaterialRegistrasi(sn: String, inputType: String) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val requestBody = RequestBodyRegisSn(listOf(sn))
            val response = apiService.insertMaterialRegistrasi(requestBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val insertMaterialRegistrasiResponse = response.body()
                    if (inputType == "scan") {
                        _insertMaterialRegistrasiByScan.postValue(insertMaterialRegistrasiResponse)
                    } else {
                        _insertMaterialRegistrasi.postValue(insertMaterialRegistrasiResponse)
                    }
                } else {
                    _isLoading.postValue(false)
                    val error = response.errorBody()?.string()
                    onError("Gagal, ${error?.let { getErrorMessageArray(it) }}")
                }
            }
        }
    }

    fun onError(message: String) {
        _errorMessage.postValue(message)
        _isLoading.postValue(false)
    }

    fun getErrorMessage(raw: String): String {
        val obj = JSONObject(raw)
        return obj.getString("message")
    }

    fun getErrorMessageArray(raw: String): String {
        val obj = JSONObject(raw)
        val dataArray = obj.getJSONArray("data")
        return dataArray.getString(0)
    }
}