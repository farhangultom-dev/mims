package dev.iconpln.mims.ui.pemakaian.maximo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.remote.response.AddSerialNumberUlpResponse
import dev.iconpln.mims.data.remote.response.GetPemakaianMaterialResponse
import dev.iconpln.mims.data.remote.service.ApiService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class InputSnPemakaianMaximoViewModel(private val apiService: ApiService) : ViewModel() {
    var job: Job? = null
    private val exception = CoroutineExceptionHandler { _, throwable ->
        onError("Exception Handled : ${throwable.localizedMessage}")
    }
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _getDataInputSnMaterialMaximoDanAp2t =
        MutableLiveData<GetPemakaianMaterialResponse>()
    val getDataInputSnMaterialMaximoDanAp2t: LiveData<GetPemakaianMaterialResponse> =
        _getDataInputSnMaterialMaximoDanAp2t

    private val _addSerialNumberUlpMaximo = MutableLiveData<AddSerialNumberUlpResponse>()
    val addSerialNumberUlpMaximo: LiveData<AddSerialNumberUlpResponse> = _addSerialNumberUlpMaximo

    fun getDataSnMaterialMaximoDanAp2t(
        noTransaksi: String,
        nomorMaterial: String,
        serialNumber: String,
        valuationType: String,
    ) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exception).launch {
            val response = apiService.getPemakaianMaterial(
                noTransaksi,
                nomorMaterial,
                serialNumber,
                valuationType
            )
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val getPemakaianSnMaximo = response.body()
                    _getDataInputSnMaterialMaximoDanAp2t.postValue(getPemakaianSnMaximo!!)
                } else {
                    _isLoading.postValue(false)
                    val error = response.errorBody()?.string()
                    onError("Error : ${error?.let { getErrorMessage(it) }}")
                }
            }
        }
    }

    fun addSerialNumberUlpMaximo(noTransaksi: String, nomorMaterial: String, serialNumber: String) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exception).launch {
            val requestBody = mutableMapOf<String, String>()
            requestBody["no_transaksi"] = noTransaksi
            requestBody["nomor_material"] = nomorMaterial
            requestBody["serial_number"] = serialNumber
            val response = apiService.addSerialNumberUlp(requestBody)
            with(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val addSNUlp = response.body()
                    _addSerialNumberUlpMaximo.postValue(addSNUlp!!)
                } else {
                    _isLoading.postValue(false)
                    val error = response.errorBody()?.string()
                    onError("Error : ${error?.let { getErrorMessage(it) }}")
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
}