package dev.iconpln.mims.ui.pemakaian.ap2t

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.remote.response.GetResultSapResponse
import dev.iconpln.mims.data.remote.service.ApiService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class ResultSapViewModel(private val apiService: ApiService) : ViewModel() {

    var job: Job? = null
    private val exception = CoroutineExceptionHandler { _, throwable ->
        onError("Exception Handled :  ${throwable.localizedMessage}")
    }

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _getResultSap = MutableLiveData<GetResultSapResponse>()
    val getResultSap: LiveData<GetResultSapResponse> = _getResultSap


    fun getDataResultSap(noPemakaian: String) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exception).launch {
            val response = apiService.getSap(noPemakaian)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val getResultSap = response.body()
                    _getResultSap.postValue(getResultSap!!)
                } else {
                    _isLoading.postValue(false)
                    val error = response.errorBody()?.string()
                    onError("Error: ${error?.let { getErrorMessage(it) }}")
                }
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