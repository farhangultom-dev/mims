package dev.iconpln.mims.ui.pemakaian.ap2t

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.remote.response.PemakaianUlpAp2tResponse
import dev.iconpln.mims.data.remote.service.ApiService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class DetailPemakaianUlpAp2tViewModel(private val apiService: ApiService) : ViewModel() {

    var job: Job? = null
    private val exception = CoroutineExceptionHandler { _, throwable ->
        onError("Exception Handled: ${throwable.localizedMessage}")
    }

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _getDetailPelanggan = MutableLiveData<PemakaianUlpAp2tResponse>()
    val getDetailPelanggan: LiveData<PemakaianUlpAp2tResponse> = _getDetailPelanggan

//    private val _getPemakaianRes = MutableLiveData<PemakaianResponse>()
//    val getPemakaianRes: LiveData<PemakaianResponse> = _getPemakaianRes

    fun getPelangganAp2t(noTransaksi: String, idPelanggan: String = "") {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exception).launch {
            val response = apiService.getPemakaianMaterialAp2t(noTransaksi, idPelanggan)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val getDetailPelanggan = response.body()
                    Log.d("DetailAp2tViewModel", "di viewmodel : $getDetailPelanggan")
                    _getDetailPelanggan.postValue(getDetailPelanggan!!)
                } else {
                    _isLoading.postValue(false)
                    val error = response.errorBody()?.string()
                    onError("Error: ${error?.let { getErrorMessage(it) }}")
                }
            }
        }
    }

//    fun getPemakaianReservasi(pageIn: String, pageSize: String = "1", noReservasi: String = ""){
//        _isLoading.value = true
//        job = CoroutineScope(Dispatchers.IO + exception).launch {
//            val response = apiService.getPemakaian(pageIn, pageSize, noReservasi)
//            withContext(Dispatchers.Main){
//                if (response.isSuccessful){
//                    _isLoading.postValue(false)
//                    val getPemakaianRes = response.body()
//                    _getPemakaianRes.postValue(getPemakaianRes)
//                } else {
//                    _isLoading.postValue(false)
//                    val error = response.errorBody()?.string()
//                    onError("Error: ${error?.let { getErrorMessage(it) }}")
//                }
//            }
//        }
//    }

    fun onError(message: String) {
        _errorMessage.postValue(message)
        _isLoading.postValue(false)
    }

    fun getErrorMessage(raw: String): String {
        val obj = JSONObject(raw)
        return obj.getString("message")
    }
}
