package dev.iconpln.mims.ui.pemakaian.ap2t

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.remote.response.AddSerialNumberUlpAp2tResponse
import dev.iconpln.mims.data.remote.response.PemakaianMaterialAp2tResponse
import dev.iconpln.mims.data.remote.service.ApiService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class InputSnPemakaianAp2tViewModel(private val apiService: ApiService) : ViewModel() {
    var job: Job? = null
    private val exception = CoroutineExceptionHandler { _, throwable ->
        onError("Exception Handled : ${throwable.localizedMessage}")
    }

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _getDataInputSnMaterial = MutableLiveData<PemakaianMaterialAp2tResponse>()
    val getDataInputSnMaterial: LiveData<PemakaianMaterialAp2tResponse> = _getDataInputSnMaterial

    private val _addSerialNumberUlpAp2t = MutableLiveData<AddSerialNumberUlpAp2tResponse>()
    val addSerialNumberUlpAp2t: LiveData<AddSerialNumberUlpAp2tResponse> = _addSerialNumberUlpAp2t

    fun getDataSnMaterialAp2t(noTransaksi: String, noAgenda: String, serialNumber: String) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exception).launch {
            val response = apiService.getPemakaianMaterialAp2t(noTransaksi, noAgenda, serialNumber)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val getPemakaianSnAp2t = response.body()
                    _getDataInputSnMaterial.postValue(getPemakaianSnAp2t!!)
                } else {
                    _isLoading.postValue(false)
                    val error = response.errorBody()?.string()
                    onError("Error : ${error?.let { getErrorMessage(it) }}")
                }
            }
        }
    }

    fun addSerialNumberUlpAp2t(
        noTransaksi: String,
        noMaterial: String,
        sn: String,
        qty: String,
        noAgenda: String,
        valuationType: String
    ) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exception).launch {
            val requestBody = mutableMapOf<String, String>()
            requestBody["no_transaksi"] = noTransaksi
            requestBody["nomor_material"] = noMaterial
            requestBody["serial_number"] = sn
            requestBody["qty"] = qty
            requestBody["no_agenda"] = noAgenda
            requestBody["valuation_type"] = valuationType
            val response = apiService.addSerialNumberUlpAp2t(requestBody)
            with(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val addSNUlpAp2t = response.body()
                    _addSerialNumberUlpAp2t.postValue(addSNUlpAp2t!!)
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