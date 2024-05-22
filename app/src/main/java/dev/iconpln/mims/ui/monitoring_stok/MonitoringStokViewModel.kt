package dev.iconpln.mims.ui.monitoring_stok

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.remote.response.GetDataStorlocResponse
import dev.iconpln.mims.data.remote.response.GetNoMaterialMonStokResponse
import dev.iconpln.mims.data.remote.response.GetStockMonStokResponse
import dev.iconpln.mims.data.remote.service.ApiService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MonitoringStokViewModel(private val apiService: ApiService) : ViewModel() {
    var job: Job? = null
    private val exception = CoroutineExceptionHandler { _, throwable ->
        onError("Exception Handled: ${throwable.localizedMessage}")
    }

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: MutableLiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _getStorloc = MutableLiveData<GetDataStorlocResponse>()
    val getStorloc: LiveData<GetDataStorlocResponse> = _getStorloc

    private val _getNoMaterialMonStok = MutableLiveData<GetNoMaterialMonStokResponse>()
    val getNoMaterialMonStok: MutableLiveData<GetNoMaterialMonStokResponse> = _getNoMaterialMonStok

    private val _getMonitoringStok = MutableLiveData<GetStockMonStokResponse>()
    val getMonitoringStok: MutableLiveData<GetStockMonStokResponse> = _getMonitoringStok

    fun getStorloc(plant: String = "") {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exception).launch {
            val response = apiService.getStorloc(plant)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val getStorloc = response.body()
                    _getStorloc.postValue(getStorloc!!)
                } else {
                    _isLoading.postValue(false)
                    val error = response.errorBody()?.string()
                    onError("Error: ${error?.let { getErrorMessage(it) }}")
                }
            }
        }
    }

    fun getNoMaterialMonStok(plant: String, valuationType: String, storLoc: String) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exception).launch {
            val response = apiService.getNoMaterialMonStok(plant, valuationType, storLoc)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val getNoMaterialMonStok = response.body()
                    _getNoMaterialMonStok.postValue(getNoMaterialMonStok!!)
                } else {
                    _isLoading.postValue(false)
                    val error = response.errorBody()?.string()
                    onError("Error: ${error?.let { getErrorMessage(it) }}")
                }
            }
        }
    }

    fun getMonitoringStok(
        plant: String,
        valuationType: String,
        storLoc: String,
        noMaterial: String
    ) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exception).launch {
            val response = apiService.getStockMonStok(plant, valuationType, storLoc, noMaterial)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val getMonitoringStok = response.body()
                    _getMonitoringStok.postValue(getMonitoringStok!!)
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

    fun getErrorMessage(error: String): String {
        val jsonObject = JSONObject(error)
        return jsonObject.getString("message")
    }
}



