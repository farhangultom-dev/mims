package dev.iconpln.mims.ui.pemakaian.yantek

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.remote.response.DataMaterialAp2tResponse
import dev.iconpln.mims.data.remote.service.ApiService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class DetailYantekViewModel(private val apiService: ApiService) : ViewModel() {
    var job: Job? = null
    private val exception = CoroutineExceptionHandler { _, throwable ->
        onError("Exception Handled : ${throwable.localizedMessage}")
    }
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _getDetailYantek = MutableLiveData<DataMaterialAp2tResponse>()
    val getDetailYantek: LiveData<DataMaterialAp2tResponse> = _getDetailYantek

    fun getDataDetailYantek(noTransaksi: String, nomorMaterial: String = "") {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exception).launch {
            val response = apiService.getDetailMaterialAp2t(noTransaksi, nomorMaterial)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val getDetailYantekUlp = response.body()
                    _getDetailYantek.postValue(getDetailYantekUlp!!)
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
        return obj.getString("Message")
    }
}