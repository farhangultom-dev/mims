package dev.iconpln.mims.ui.monitoring_permintaan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MonitoringPermintaanViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _monitoringPermintaanResponse =
        MutableLiveData<List<dev.iconpln.mims.data.local.database.TTransMonitoringPermintaan>>()
    val monitoringPermintaanResponse: LiveData<List<dev.iconpln.mims.data.local.database.TTransMonitoringPermintaan>> =
        _monitoringPermintaanResponse

    private val _monitoringPermintaanDetailResponse =
        MutableLiveData<List<dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetail>>()
    val monitoringPermintaanDetailResponse: LiveData<List<dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetail>> =
        _monitoringPermintaanDetailResponse

    fun getMonitoringPermintaan(daoSession: dev.iconpln.mims.data.local.database.DaoSession) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                _isLoading.value = true

                val lisMonitoring = daoSession.tTransMonitoringPermintaanDao.queryBuilder().list()

                _monitoringPermintaanResponse.postValue(lisMonitoring)

                _isLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }

        }
    }

    fun getMonitoringPermintaanDetail(
        daoSession: dev.iconpln.mims.data.local.database.DaoSession,
        noTransaksi: String
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                _isLoading.value = true

                val listMonitoringDetail =
                    daoSession.tTransMonitoringPermintaanDetailDao.queryBuilder()
                        .where(
                            dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetailDao.Properties.NoTransaksi.eq(
                                noTransaksi
                            )
                        ).list()

                _monitoringPermintaanDetailResponse.postValue(listMonitoringDetail)

                _isLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }

        }
    }

    fun searchDetail(
        daoSession: dev.iconpln.mims.data.local.database.DaoSession,
        namaMaterial: String,
        noTransaksi: String
    ) {
        var listDetail = daoSession.tTransMonitoringPermintaanDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetailDao.Properties.NoTransaksi.eq(
                    noTransaksi
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetailDao.Properties.NomorMaterial.like(
                    "%" + namaMaterial + "%"
                )
            )
            .list()
        _monitoringPermintaanDetailResponse.postValue(listDetail)
    }

    fun search(
        daoSession: dev.iconpln.mims.data.local.database.DaoSession,
        srcNoPermintaanText: String,
        srcStatusPengeluaranText: String,
        srcGudangTujuanText: String,
        srcTglPermintaanText: String
    ) {
        var listDataMonitoring = daoSession.tTransMonitoringPermintaanDao.queryBuilder().where(
            dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDao.Properties.NoPermintaan.like(
                "%" + srcNoPermintaanText + "%"
            ),
            dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDao.Properties.KodePengeluaran.like(
                "%" + srcStatusPengeluaranText + "%"
            ),
            dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDao.Properties.StorLocTujuanName.like(
                "%" + srcGudangTujuanText + "%"
            ),
            dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDao.Properties.TanggalPermintaan.like(
                "%" + srcTglPermintaanText + "%"
            )
        ).list()

        _monitoringPermintaanResponse.postValue(listDataMonitoring)
    }
}