package dev.iconpln.mims.ui.inspeksi_material

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.data.local.database.DaoSession
import dev.iconpln.mims.data.local.database.TAGOMaterialData
import dev.iconpln.mims.data.remote.response.AGOMaterialInspectionData
import dev.iconpln.mims.data.remote.response.AGOMaterialInspectionDetail
import dev.iconpln.mims.data.remote.response.AGOMaterialInspectionTeamDetail
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.data.remote.service.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AGOGetMaterialInspectionBySNViewModel(private val apiService: ApiService) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _agoMaterialInspectionData = MutableLiveData<AGOMaterialInspectionDetail?>()
    val agoMaterialInspectionDetail: MutableLiveData<AGOMaterialInspectionDetail?> = _agoMaterialInspectionData

    private val _agoMaterialInspectionTeamData = MutableLiveData<List<AGOMaterialInspectionTeamDetail?>>()
    val agoMaterialInspectionTeamData: LiveData<List<AGOMaterialInspectionTeamDetail?>> = _agoMaterialInspectionTeamData

    fun getMaterialInspectionData(
        sn: String
    ) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.getMaterialInspectionBySN(sn)
            try {
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        _isLoading.value = false
                        val result = response.body()
                        result?.let {
                            _agoMaterialInspectionData.postValue(it.detailMaterialInspection)
                            result.inspectionTeam?.listDataTeam?.let { listDataTeam ->
                                _agoMaterialInspectionTeamData.postValue(listDataTeam)
                            }
                        }
                    } else {
                        _isLoading.value = false
                        _agoMaterialInspectionData.postValue(null)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private val _isSuccessInsertMaterialInspectionData = MutableLiveData<Boolean>()
    val isSuccessInsertMaterialInspectionData: LiveData<Boolean> = _isSuccessInsertMaterialInspectionData

    private val _dataExist = MutableLiveData<Boolean>()
    val dataExist: LiveData<Boolean> = _dataExist
    fun insertMaterialInspectionDataToLocalDB(
        daoSession: DaoSession,
        result: AGOMaterialInspectionDetail,
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val materialInspectionItem = result.let { data ->
                TAGOMaterialData().apply {
                    noMaterial = data.materialNo
                    namaMaterial = data.namaMaterial
                    qtyMaterial = data.qty
                    unitMaterial = data.unit
                    snMaterial = data.serialLong
                    inspectionNumber = data.nomorInspeksiRetur
                }
            }

            try {
                val existingData = daoSession.tagoMaterialDataDao.queryBuilder()
                    .where(dev.iconpln.mims.data.local.database.TAGOMaterialDataDao.Properties.SnMaterial.eq(materialInspectionItem.snMaterial))
                    .unique()

                if (existingData != null) {
                    _isSuccessInsertMaterialInspectionData.postValue(false)
                    _dataExist.postValue(true)
                } else {
                    daoSession.runInTx{
                        materialInspectionItem.let { daoSession.tagoMaterialDataDao.insertInTx(it) }
                    }
                    _dataExist.postValue(false)
                    _isSuccessInsertMaterialInspectionData.postValue(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resetInsertMaterialInspectionDataStatus() {
        _isSuccessInsertMaterialInspectionData.value = false
        _dataExist.value = false
    }
}