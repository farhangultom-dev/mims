package dev.iconpln.mims.ui.inspeksi_material

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.remote.response.AGODetailATTBDataBasedOnRefNum
import dev.iconpln.mims.data.remote.service.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AGOGetATTBWithRefNumViewModel(private val apiService: ApiService): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _attbDataBasedOnRefNum = MutableLiveData<List<AGODetailATTBDataBasedOnRefNum>>()
    val attbDataBasedOnRefNum: LiveData<List<AGODetailATTBDataBasedOnRefNum>> = _attbDataBasedOnRefNum

    fun getAttbDataWithRefNum(
        plant: String,
        storLoc: String,
        materialDocument: String,
    ) {
        _isLoading.value = true

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.getATTBWithRefNum(plant, storLoc, materialDocument)
            try {
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _isLoading.value = false
                        val result = response.body()
                        result?.let {
                            _attbDataBasedOnRefNum.postValue(it.data)
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