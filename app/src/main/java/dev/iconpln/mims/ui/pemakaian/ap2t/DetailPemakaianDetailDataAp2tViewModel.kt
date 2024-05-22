package dev.iconpln.mims.ui.pemakaian.ap2t

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.remote.response.PemakaianDetailResponse
import dev.iconpln.mims.data.remote.service.ApiService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject

class DetailPemakaianDetailDataAp2tViewModel(private val apiService: ApiService) : ViewModel() {
    var job: Job? = null
    private val exception = CoroutineExceptionHandler { _, throwable ->
        onError("Exception Handled : ${throwable.localizedMessage}")
    }

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _getDataDetailMaterialAp2t = MutableLiveData<PemakaianDetailResponse>()
    val getDataDetailMaterialAp2t: LiveData<PemakaianDetailResponse> = _getDataDetailMaterialAp2t


    fun getDataDetailMaterialAp2t(noTransaksi: String, noMaterial: String) {
        _isLoading.value = true
        job = CoroutineScope(exception).launch {
            val response = apiService.getPemakaianDetail(noTransaksi, noMaterial)
            if (response.isSuccessful) {
                _isLoading.postValue(false)
                val getDataDetailMaterialAp2t = response.body()
                _getDataDetailMaterialAp2t.postValue(getDataDetailMaterialAp2t!!)
            } else {
                _isLoading.postValue(false)
                val error = response.errorBody()?.string()
                onError("Error: ${error?.let { getErrorMessage(it) }}")
            }
        }
    }

    fun onError(message: String) {
        _errorMessage.postValue(message)
        _isLoading.postValue(false)
    }

    fun getErrorMessage(response: String): String {
        val jsonObject = JSONObject(response)
        return jsonObject.getString("message")
    }
}