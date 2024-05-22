package dev.iconpln.mims.ui.monitoring


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MonitoringViewModel : ViewModel() {

    private val _monitoringPOResponse =
        MutableLiveData<List<dev.iconpln.mims.data.local.database.TPos>>()
    val monitoringPOResponse: LiveData<List<dev.iconpln.mims.data.local.database.TPos>> =
        _monitoringPOResponse

    private val _detailMonitoringPOResponse =
        MutableLiveData<List<dev.iconpln.mims.data.local.database.TPosDetail>>()
    val detailMonitoringPOResponse: LiveData<List<dev.iconpln.mims.data.local.database.TPosDetail>> =
        _detailMonitoringPOResponse

    fun getPo(daoSession: dev.iconpln.mims.data.local.database.DaoSession) {
        try {
            val dataPos = daoSession.tPosDao.queryBuilder()
                .orderDesc(dev.iconpln.mims.data.local.database.TPosDao.Properties.CreatedDate)
                .list()
            _monitoringPOResponse.postValue(dataPos)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getPoDetail(daoSession: dev.iconpln.mims.data.local.database.DaoSession, noDo: String) {
        try {
            val detailPo = daoSession.tPosDetailDao.queryBuilder()
                .where(
                    dev.iconpln.mims.data.local.database.TPosDetailDao.Properties.NoDoSmar.eq(
                        noDo
                    )
                ).list()
            _detailMonitoringPOResponse.postValue(detailPo)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getPoDetailFilter(
        daoSession: dev.iconpln.mims.data.local.database.DaoSession,
        noDo: String,
        noMaterial: String,
        noPackaging: String
    ) {
        try {
            val detailPoFilter = daoSession.tPosDetailDao.queryBuilder()
                .where(
                    dev.iconpln.mims.data.local.database.TPosDetailDao.Properties.NoDoSmar.eq(
                        noDo
                    )
                )
                .where(dev.iconpln.mims.data.local.database.TPosDetailDao.Properties.NoMatSap.like("%" + noMaterial + "%"))
                .where(
                    dev.iconpln.mims.data.local.database.TPosDetailDao.Properties.NoPackaging.like(
                        "%" + noPackaging + "%"
                    )
                ).list()
            _detailMonitoringPOResponse.postValue(detailPoFilter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getPoFilter(
        daoSession: dev.iconpln.mims.data.local.database.DaoSession,
        noPo: String,
        noDo: String,
        startDate: String,
        endDate: String
    ) {
        try {
            var listMonitoring: List<dev.iconpln.mims.data.local.database.TPos>
            if (startDate.equals("") || endDate.equals("")) {
                listMonitoring = daoSession.tPosDao.queryBuilder()
                    .where(dev.iconpln.mims.data.local.database.TPosDao.Properties.PoMpNo.like("%" + noPo + "%"))
                    .where(dev.iconpln.mims.data.local.database.TPosDao.Properties.NoDoSmar.like("%" + noDo + "%"))
                    .orderDesc(dev.iconpln.mims.data.local.database.TPosDao.Properties.CreatedDate)
                    .list()
                _monitoringPOResponse.postValue(listMonitoring)
            } else {
                listMonitoring = daoSession.tPosDao.queryBuilder()
                    .where(dev.iconpln.mims.data.local.database.TPosDao.Properties.PoMpNo.like("%" + noPo + "%"))
                    .where(dev.iconpln.mims.data.local.database.TPosDao.Properties.NoDoSmar.like("%" + noDo + "%"))
                    .where(
                        dev.iconpln.mims.data.local.database.TPosDao.Properties.CreatedDate.ge(
                            startDate
                        )
                    )
                    .where(
                        dev.iconpln.mims.data.local.database.TPosDao.Properties.CreatedDate.le(
                            endDate
                        )
                    )
                    .orderDesc(dev.iconpln.mims.data.local.database.TPosDao.Properties.CreatedDate)
                    .list()
                _monitoringPOResponse.postValue(listMonitoring)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}