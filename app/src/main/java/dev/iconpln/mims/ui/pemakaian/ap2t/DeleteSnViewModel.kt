package dev.iconpln.mims.ui.pemakaian.ap2t

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.remote.response.DeleteSnPemakaianResponse
import dev.iconpln.mims.data.remote.service.ApiService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject

class DeleteSnViewModel(private val apiService: ApiService) : ViewModel() {
    var job: Job? = null
    private val exception = CoroutineExceptionHandler { _, throwable ->
        onError("Exception Handled : ${throwable.localizedMessage}")
    }

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _deleteSn = MutableLiveData<DeleteSnPemakaianResponse>()
    val deleteSn: MutableLiveData<DeleteSnPemakaianResponse> = _deleteSn

    fun deleteSn(
        noAgenda: String,
        noTransaksi: String,
        noMaterial: String,
        qty: String,
        sn: String,
        storLoc: String,
        plant: String
    ) {

        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exception).launch {
            val requestBody = mutableMapOf<String, String>()
            requestBody["no_agenda"] = noAgenda
            requestBody["no_transaksi"] = noTransaksi
            requestBody["nomor_material"] = noMaterial
            requestBody["plant"] = plant
            requestBody["qty"] = qty
            requestBody["serial_number"] = sn
            requestBody["stor_loc"] = storLoc
            val response = apiService.deleteSnMaterial(requestBody)
            with(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val deleteSn = response.body()
                    _deleteSn.postValue(deleteSn!!)
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
        val jsonObject = JSONObject(raw)
        return jsonObject.getString("message")
    }
}