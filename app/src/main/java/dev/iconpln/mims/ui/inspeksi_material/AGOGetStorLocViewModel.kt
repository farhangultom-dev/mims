package dev.iconpln.mims.ui.inspeksi_material

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.remote.response.AGOStorLocDetailData
import dev.iconpln.mims.data.remote.service.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AGOGetStorLocViewModel(private val apiService: ApiService) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _storLocData = MutableLiveData<List<AGOStorLocDetailData>>()
    val storLocData: LiveData<List<AGOStorLocDetailData>> = _storLocData

    fun getStorLocData(
        plant: String,
        roleId: String,
        ) {
        _isLoading.value = true

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.getStorLoc(plant, roleId)
            try {
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _isLoading.value = false
                        val result = response.body()
                        result?.let {
                            _storLocData.postValue(it.storLoc.storData)
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