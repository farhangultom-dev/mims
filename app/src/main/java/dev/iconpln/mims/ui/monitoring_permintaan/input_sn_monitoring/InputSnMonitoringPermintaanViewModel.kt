package dev.iconpln.mims.ui.monitoring_permintaan.input_sn_monitoring

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.remote.response.GenericResponse
import dev.iconpln.mims.data.remote.response.SnResponse
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.utils.Config
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InputSnMonitoringPermintaanViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _deleteAllSnResponse = MutableLiveData<GenericResponse>()
    val deleteAllSnResponse: LiveData<GenericResponse> = _deleteAllSnResponse

    private val _deleteSnByCodeResponse = MutableLiveData<SnResponse>()
    val deleteSnByCodeResponse: LiveData<SnResponse> = _deleteSnByCodeResponse

    private val _addSnByCodeResponse = MutableLiveData<SnResponse>()
    val addSnByCodeResponse: LiveData<SnResponse> = _addSnByCodeResponse

    fun deleteAllSn(
        context: Context,
        noRepackaging: String,
        noMaterial: String,
        userPlant: String,
        userLoc: String,
        userName: String
    ) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiConfig.getApiService(context)
                .permintaanDeleteAllSn(noRepackaging, noMaterial, userPlant, userLoc, userName)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    try {
                        _isLoading.value = false
                        val responses = response.body()
                        _deleteAllSnResponse.postValue(responses!!)
                    } catch (e: Exception) {
                        _isLoading.value = false
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                } else {
                    _isLoading.value = false
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun deleteSnByCode(
        context: Context,
        kodeScan: String,
        noRepackaging: String,
        noMaterial: String,
        roleId: Int,
        plant: String,
        storloc: String,
        username: String,
        kodeGerakDelete: String,
        valuationType: String
    ) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiConfig.getApiService(context)
                .permintaanDeleteSn(
                    noRepackaging,
                    noMaterial,
                    kodeScan,
                    roleId,
                    plant,
                    storloc,
                    username,
                    kodeGerakDelete,
                    valuationType
                )
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    try {
                        val responses = response.body()
                        if (responses?.status == Config.KEY_SUCCESS) {
                            _deleteSnByCodeResponse.postValue(responses!!)
                        } else {
                            Toast.makeText(context, responses?.message, Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        _isLoading.value = false
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                } else {
                    _isLoading.value = false
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun addSnByCode(
        context: Context,
        kodeScan: String,
        noRepackaging: String,
        noMaterial: String,
        roleId: Int,
        plant: String,
        storloc: String,
        username: String,
        kodeGerak: String,
        valuationType: String,
        noIdMeter: String
    ) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiConfig.getApiService(context)
                .permintaanAddSn(
                    noRepackaging,
                    noMaterial,
                    kodeScan,
                    plant,
                    storloc,
                    roleId,
                    username,
                    kodeGerak,
                    valuationType,
                    noIdMeter
                )
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    try {
                        if (response.body()?.status == Config.KEY_SUCCESS) {
                            _isLoading.value = false
                            val responses = response.body()

                            _addSnByCodeResponse.postValue(responses!!)

                        } else {
                            _isLoading.value = false
                            Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                    } catch (e: Exception) {
                        _isLoading.value = false
                        Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                } else {
                    _isLoading.value = false
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}