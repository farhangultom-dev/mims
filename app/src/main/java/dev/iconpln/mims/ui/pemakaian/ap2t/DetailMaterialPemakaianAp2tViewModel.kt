package dev.iconpln.mims.ui.pemakaian.ap2t

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.remote.response.DataMaterialAp2tResponse
import dev.iconpln.mims.data.remote.response.GetPejabatResponse
import dev.iconpln.mims.data.remote.response.UpdateDetailPemakaianResponse
import dev.iconpln.mims.data.remote.service.ApiService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class DetailMaterialPemakaianAp2tViewModel(private val apiService: ApiService) : ViewModel() {
    var job: Job? = null
    private val exception = CoroutineExceptionHandler { _, throwable ->
        onError("Exception Handled :  ${throwable.localizedMessage}")
    }
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _getDataDetailAp2t = MutableLiveData<DataMaterialAp2tResponse>()
    val getDataDetailAp2t: LiveData<DataMaterialAp2tResponse> = _getDataDetailAp2t

    private val _getPejabat = MutableLiveData<GetPejabatResponse>()
    val getPejabat: LiveData<GetPejabatResponse> = _getPejabat

    private val _getPejabatKepala = MutableLiveData<GetPejabatResponse>()
    val getPejabatKepala: LiveData<GetPejabatResponse> = _getPejabatKepala

    private val _updateDetailPemakaian = MutableLiveData<UpdateDetailPemakaianResponse>()
    val updateDetailPemakaian: LiveData<UpdateDetailPemakaianResponse> = _updateDetailPemakaian

    fun getDataDetailMaterialAp2t(noTransaksi: String, nomorMaterial: String = "") {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exception).launch {
            val response = apiService.getDetailMaterialAp2t(noTransaksi, nomorMaterial)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val getDataDetailAp2t = response.body()
                    _getDataDetailAp2t.postValue(getDataDetailAp2t!!)
                } else {
                    _isLoading.postValue(false)
                    val error = response.errorBody()?.string()
                    onError("Error: ${error?.let { getErrorMessage(it) }}")
                }
            }
        }
    }

    fun getPejabat(plant: String, storLoc: String, kdPejabat: String) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exception).launch {
            val response = apiService.getPejabat(plant, storLoc, kdPejabat)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val getPejabat = response.body()
                    _getPejabat.postValue(getPejabat!!)
                } else {
                    _isLoading.postValue(false)
                    val error = response.errorBody()?.string()
                    onError("Error: ${error?.let { getErrorMessage(it) }}")
                }
            }
        }
    }

    fun getPejabatKepala(plant: String, storLoc: String, kdPejabat: String) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exception).launch {
            val response = apiService.getPejabat(plant, storLoc, kdPejabat)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val getPejabat = response.body()
                    _getPejabatKepala.postValue(getPejabat!!)
                } else {
                    _isLoading.postValue(false)
                    val error = response.errorBody()?.string()
                    onError("Error: ${error?.let { getErrorMessage(it) }}")
                }
            }
        }
    }

    fun updateDetailPemakaian(
        noTransaksi: String,
        pemeriksa: String,
        penerima: String,
        pejabatKepala: String,
        pejabatPengesahan: String,
        lokasi: String = "",
        namaKegiatan: String = "",
        namaPelanggan: String = "",
        noPk: String = ""
    ) {
        _isLoading.value = true
        job = CoroutineScope(Dispatchers.IO + exception).launch {
            val requestBody = mutableMapOf<String, String>()
            requestBody["kepala_gudang"] = pejabatKepala
            requestBody["lokasi"] = lokasi
            requestBody["nama_kegiatan"] = namaKegiatan
            requestBody["nama_pelanggan"] = namaPelanggan
            requestBody["no_pk"] = noPk
            requestBody["no_transaksi"] = noTransaksi
            requestBody["pejabat_pengesahan"] = pejabatPengesahan
            requestBody["pemeriksa"] = pemeriksa
            requestBody["penerima"] = penerima
            val response = apiService.updateDetailPemakaian(requestBody)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.postValue(false)
                    val updateDetailPemakaian = response.body()
                    _updateDetailPemakaian.postValue(updateDetailPemakaian!!)
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

    fun getErrorMessage(raw: String): String {
        val obj = JSONObject(raw)
        return obj.getString("message")
    }
}



