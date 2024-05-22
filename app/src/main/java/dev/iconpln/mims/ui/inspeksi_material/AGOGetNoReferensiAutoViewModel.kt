package dev.iconpln.mims.ui.inspeksi_material

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.remote.response.AGOReferenceNumber
import dev.iconpln.mims.data.remote.response.AGOReferenceNumberData
import dev.iconpln.mims.data.remote.service.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AGOGetNoReferensiAutoViewModel(private val apiService: ApiService) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: MutableLiveData<Boolean> = _isLoading

    private val _noReferensiData = MutableLiveData<List<AGOReferenceNumberData>>()
    val noReferensiData: MutableLiveData<List<AGOReferenceNumberData>> = _noReferensiData

    fun getNoReferensiAuto(
        plant: String,
        storLoc: String,
        noReferensi: String
    ) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.getNoReferensiAuto(plant, storLoc, noReferensi)
            try {
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _isLoading.value = false
                        val result = response.body()
                        result?.let {
                            _noReferensiData.postValue(it.data)
                        }
                    } else {
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}