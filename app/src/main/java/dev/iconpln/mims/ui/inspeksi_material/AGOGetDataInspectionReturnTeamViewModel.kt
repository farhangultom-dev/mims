package dev.iconpln.mims.ui.inspeksi_material

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.remote.response.AGODetailInspectionTeamData
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.data.remote.service.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AGOGetDataInspectionReturnTeamViewModel(private val apiService: ApiService) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _inspectionReturnTeamData = MutableLiveData<List<AGODetailInspectionTeamData>>()
    val inspectionReturnTeamData: LiveData<List<AGODetailInspectionTeamData>> = _inspectionReturnTeamData

    fun getInspectionReturnTeam(noInspeksiRetur: String) {
        _isLoading.value = true

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.getDataInspectionReturnTeam(noInspeksiRetur)
            try {
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _isLoading.value = false
                        val result = response.body()
                        result?.let {
                            _inspectionReturnTeamData.postValue(it.data)
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