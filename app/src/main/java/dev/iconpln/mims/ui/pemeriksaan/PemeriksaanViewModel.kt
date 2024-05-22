package dev.iconpln.mims.ui.pemeriksaan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class PemeriksaanViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _pemeriksaanResponse =
        MutableLiveData<List<dev.iconpln.mims.data.local.database.TPemeriksaan>>()
    val pemeriksaanResponse: LiveData<List<dev.iconpln.mims.data.local.database.TPemeriksaan>> =
        _pemeriksaanResponse

    private val _pemeriksaanDetailResponse =
        MutableLiveData<List<dev.iconpln.mims.data.local.database.TPemeriksaanDetail>>()
    val pemeriksaanDetailResponse: LiveData<List<dev.iconpln.mims.data.local.database.TPemeriksaanDetail>> =
        _pemeriksaanDetailResponse

    fun getPemeriksaan(daoSession: dev.iconpln.mims.data.local.database.DaoSession) {
        val listPemeriksaan = daoSession.tPemeriksaanDao.queryBuilder()
            .where(dev.iconpln.mims.data.local.database.TPemeriksaanDao.Properties.IsDone.eq(0))
            .list()

        _pemeriksaanResponse.postValue(listPemeriksaan)
    }
}