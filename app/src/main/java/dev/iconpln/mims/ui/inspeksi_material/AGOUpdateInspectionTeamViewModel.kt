package dev.iconpln.mims.ui.inspeksi_material

import android.content.Context
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.remote.response.AGOUpdateInspectionReturnTeam
import dev.iconpln.mims.data.remote.response.AGOUpdateInspectionReturnTeamDataBody
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.data.remote.service.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AGOUpdateInspectionTeamViewModel(private val apiService: ApiService): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _updateInspectionTeamResponse = MutableLiveData<AGOUpdateInspectionReturnTeam>()
    val updateInspectionTeamResponse: LiveData<AGOUpdateInspectionReturnTeam> = _updateInspectionTeamResponse

    fun updateInspectionReturnTeam(body: AGOUpdateInspectionReturnTeamDataBody) {
        _isLoading.value = true

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.updateInspectionTeam(body)
            try {
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _isLoading.value = false
                        val result = response.body()
                        result?.let {
                            _updateInspectionTeamResponse.postValue(it)
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