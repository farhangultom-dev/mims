package dev.iconpln.mims.ui.inspeksi_material

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.remote.response.AGODetailInspectionMaterialReturnData
import dev.iconpln.mims.data.remote.response.AGOGInspectionMaterialReturnData
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.data.remote.service.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AGOGetInspectionReturnViewModel(private val apiService: ApiService): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _inspectionReturnData = MutableLiveData<List<AGODetailInspectionMaterialReturnData?>>()
    val inspectionReturnData: LiveData<List<AGODetailInspectionMaterialReturnData?>> = _inspectionReturnData

    fun getInspectionReturnData(
        plant: String?,
        storLoc: String?,
        date: String?,
        noInspeksiRetur: String?,
        materialNo: String?,
        statusInspeksi: String?,
    ) {
        _isLoading.value = true

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.getDataInspectionReturn(plant, storLoc, date, noInspeksiRetur, materialNo, statusInspeksi)
            try {
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _isLoading.value = false
                        val result = response.body()
                        result?.data?.let {
                            _inspectionReturnData.value = it
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