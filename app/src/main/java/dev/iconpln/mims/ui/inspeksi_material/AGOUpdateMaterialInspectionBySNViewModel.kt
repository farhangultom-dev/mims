package dev.iconpln.mims.ui.inspeksi_material

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.remote.response.AGOUpdateMaterialInspectionBySN
import dev.iconpln.mims.data.remote.response.AGOUpdateMaterialInspectionBySNBody
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.data.remote.service.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AGOUpdateMaterialInspectionBySNViewModel(private val apiService: ApiService) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _updateMaterialInspectionResponse = MutableLiveData<AGOUpdateMaterialInspectionBySN?>()
    val updateMaterialInspectionResponse: MutableLiveData<AGOUpdateMaterialInspectionBySN?> = _updateMaterialInspectionResponse

    fun updateMaterialInspectionBySN(body: AGOUpdateMaterialInspectionBySNBody) {
        _isLoading.value = true
        Log.d("AGOUpdateMaterialInspectionBySNViewModel", "updateMaterialInspectionBySN: $body")
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.updateMaterialInspectionBySN(body)
            try {
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _isLoading.value = false
                        val result = response.body()
                        result?.let {
                            _updateMaterialInspectionResponse.postValue(it)
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