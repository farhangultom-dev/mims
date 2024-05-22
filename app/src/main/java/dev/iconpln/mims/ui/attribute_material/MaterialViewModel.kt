package dev.iconpln.mims.ui.attribute_material

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.R
import dev.iconpln.mims.data.remote.response.MaterialDetailResponse
import dev.iconpln.mims.data.remote.response.MaterialResponse
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MaterialViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _materialDataResponse = MutableLiveData<MaterialResponse>()
    val materialDataResponse: LiveData<MaterialResponse> = _materialDataResponse

    private val _materialDataDetailResponse = MutableLiveData<MaterialDetailResponse>()
    val materialDataDetailResponse: LiveData<MaterialDetailResponse> = _materialDataDetailResponse

    private val _materialResponse =
        MutableLiveData<List<dev.iconpln.mims.data.local.database.TMaterial>>()
    val materialResponse: LiveData<List<dev.iconpln.mims.data.local.database.TMaterial>> =
        _materialResponse

    private val _detailMaterialResponse =
        MutableLiveData<List<dev.iconpln.mims.data.local.database.TMaterialDetail>>()
    val detailMaterialResponse: LiveData<List<dev.iconpln.mims.data.local.database.TMaterialDetail>> =
        _detailMaterialResponse

    fun getDataDetailAttributeMaterial(
        noMaterial: String,
        noBatch: String,
        serialNumber: String,
        daoSession: dev.iconpln.mims.data.local.database.DaoSession
    ) {
        try {
            val dataAttributeMaterials = daoSession.tMaterialDetailDao.queryBuilder()
                .where(
                    dev.iconpln.mims.data.local.database.TMaterialDetailDao.Properties.NomorMaterial.eq(
                        noMaterial
                    )
                )
                .where(
                    dev.iconpln.mims.data.local.database.TMaterialDetailDao.Properties.NoProduksi.eq(
                        noBatch
                    )
                )
                .where(
                    dev.iconpln.mims.data.local.database.TMaterialDetailDao.Properties.SerialNumber.like(
                        "%" + serialNumber + "%"
                    )
                )
                .list()
            _detailMaterialResponse.postValue(dataAttributeMaterials)
        } catch (e: Exception) {
            throw e
        }
    }

    fun getDataAttributeMaterial(daoSession: dev.iconpln.mims.data.local.database.DaoSession) {
        try {
            val dataAttributeMaterials = daoSession.tMaterialDao.loadAll()
            _materialResponse.postValue(dataAttributeMaterials)
        } catch (e: Exception) {
            throw e
        }
    }

    fun searchDataMaterials(
        kategori: String,
        tahun: String,
        noBatch: String,
        startDate: String,
        endDate: String,
        daoSession: dev.iconpln.mims.data.local.database.DaoSession
    ) {
        try {
            var listMaterial = daoSession.tMaterialDao.queryBuilder()
                .where(
                    dev.iconpln.mims.data.local.database.TMaterialDao.Properties.NamaKategoriMaterial.like(
                        "%" + kategori + "%"
                    )
                )
                .where(dev.iconpln.mims.data.local.database.TMaterialDao.Properties.Tahun.like("%" + tahun + "%"))
                .whereOr(
                    dev.iconpln.mims.data.local.database.TMaterialDao.Properties.NoProduksi.like("%" + noBatch + "%"),
                    dev.iconpln.mims.data.local.database.TMaterialDao.Properties.NomorMaterial.like(
                        "%" + noBatch + "%"
                    )
                )
                .orderAsc(dev.iconpln.mims.data.local.database.TMaterialDao.Properties.TglProduksi)
                .list()

            if (!startDate.equals("") && !endDate.equals("")) {
                listMaterial = daoSession.tMaterialDao.queryBuilder()
                    .where(
                        dev.iconpln.mims.data.local.database.TMaterialDao.Properties.NamaKategoriMaterial.like(
                            "%" + kategori + "%"
                        )
                    )
                    .where(dev.iconpln.mims.data.local.database.TMaterialDao.Properties.Tahun.like("%" + tahun + "%"))
                    .whereOr(
                        dev.iconpln.mims.data.local.database.TMaterialDao.Properties.NoProduksi.like(
                            "%" + noBatch + "%"
                        ),
                        dev.iconpln.mims.data.local.database.TMaterialDao.Properties.NomorMaterial.like(
                            "%" + noBatch + "%"
                        )
                    )
                    .where(
                        dev.iconpln.mims.data.local.database.TMaterialDao.Properties.TglProduksi.ge(
                            startDate
                        )
                    )
                    .where(
                        dev.iconpln.mims.data.local.database.TMaterialDao.Properties.TglProduksi.le(
                            endDate
                        )
                    )
                    .orderAsc(dev.iconpln.mims.data.local.database.TMaterialDao.Properties.TglProduksi)
                    .list()
            } else if (!startDate.equals("") && endDate.equals("")) {
                listMaterial = daoSession.tMaterialDao.queryBuilder()
                    .where(
                        dev.iconpln.mims.data.local.database.TMaterialDao.Properties.NamaKategoriMaterial.like(
                            "%" + kategori + "%"
                        )
                    )
                    .where(dev.iconpln.mims.data.local.database.TMaterialDao.Properties.Tahun.like("%" + tahun + "%"))
                    .whereOr(
                        dev.iconpln.mims.data.local.database.TMaterialDao.Properties.NoProduksi.like(
                            "%" + noBatch + "%"
                        ),
                        dev.iconpln.mims.data.local.database.TMaterialDao.Properties.NomorMaterial.like(
                            "%" + noBatch + "%"
                        )
                    )
                    .where(
                        dev.iconpln.mims.data.local.database.TMaterialDao.Properties.TglProduksi.ge(
                            startDate
                        )
                    )
                    .orderAsc(dev.iconpln.mims.data.local.database.TMaterialDao.Properties.TglProduksi)
                    .list()
            } else if (startDate.equals("") && !endDate.equals("")) {
                listMaterial = daoSession.tMaterialDao.queryBuilder()
                    .where(
                        dev.iconpln.mims.data.local.database.TMaterialDao.Properties.NamaKategoriMaterial.like(
                            "%" + kategori + "%"
                        )
                    )
                    .where(dev.iconpln.mims.data.local.database.TMaterialDao.Properties.Tahun.like("%" + tahun + "%"))
                    .whereOr(
                        dev.iconpln.mims.data.local.database.TMaterialDao.Properties.NoProduksi.like(
                            "%" + noBatch + "%"
                        ),
                        dev.iconpln.mims.data.local.database.TMaterialDao.Properties.NomorMaterial.like(
                            "%" + noBatch + "%"
                        )
                    )
                    .where(
                        dev.iconpln.mims.data.local.database.TMaterialDao.Properties.TglProduksi.le(
                            endDate
                        )
                    )
                    .orderAsc(dev.iconpln.mims.data.local.database.TMaterialDao.Properties.TglProduksi)
                    .list()
            }

            _materialResponse.postValue(listMaterial)
        } catch (e: Exception) {
            throw e
        }
    }

    fun getAllMaterial(
        context: Context,
        kodePabrikan: String,
        tahun: String,
        kategori: String,
        tanggalAwal: String,
        tanggalAkhir: String,
        page: String,
        limit: String,
        noProduksiMaterial: String
    ) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiConfig.getApiService(context).getAllMaterial(
                    kodePabrikan,
                    tahun,
                    kategori,
                    tanggalAwal,
                    tanggalAkhir,
                    page,
                    limit,
                    noProduksiMaterial
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                            context,
                            dev.iconpln.mims.utils.Config.IS_LOGIN_SSO, 0
                        )
                        _isLoading.value = false

                        if (response.body()?.status == dev.iconpln.mims.utils.Config.KEY_SUCCESS) {
                            _materialDataResponse.postValue(response.body())
                        } else {
                            _isLoading.value = false
                            when (response.body()?.message) {
                                dev.iconpln.mims.utils.Config.DO_LOGOUT -> {
                                    Helper.logout(context, isLoginSso)
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.session_kamu_telah_habis),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> Toast.makeText(
                                    context,
                                    response.body()?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        _isLoading.value = false
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                e.printStackTrace()
            }
        }
    }

    fun getDetailMaterial(
        context: Context,
        kodePabrikan: String,
        noMaterial: String,
        serialNumber: String,
        noProduksiMaterial: String,
        limit: String,
        page: String,
        tahun: Int
    ) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiConfig.getApiService(context).getAllMaterialDetail(
                    kodePabrikan,
                    noProduksiMaterial,
                    noMaterial,
                    serialNumber,
                    limit,
                    page,
                    tahun
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                            context,
                            dev.iconpln.mims.utils.Config.IS_LOGIN_SSO, 0
                        )
                        _isLoading.value = false

                        if (response.body()?.status == dev.iconpln.mims.utils.Config.KEY_SUCCESS) {
                            _materialDataDetailResponse.postValue(response.body())
                        } else {
                            _isLoading.value = false
                            when (response.body()?.message) {
                                dev.iconpln.mims.utils.Config.DO_LOGOUT -> {
                                    Helper.logout(context, isLoginSso)
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.session_kamu_telah_habis),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> Toast.makeText(
                                    context,
                                    response.body()?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getDetailMaterialPaging(
        context: Context,
        kodePabrikan: String,
        noMaterial: String,
        serialNumber: String,
        noProduksiMaterial: String,
        limit: String,
        page: String,
        tahun: Int
    ) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiConfig.getApiService(context).getAllMaterialDetailPaging(
                    kodePabrikan,
                    noProduksiMaterial,
                    noMaterial,
                    serialNumber,
                    limit,
                    page,
                    tahun
                )
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                            context,
                            dev.iconpln.mims.utils.Config.IS_LOGIN_SSO, 0
                        )
                        _isLoading.value = false

                        if (response.body()?.status == dev.iconpln.mims.utils.Config.KEY_SUCCESS) {
                            _materialDataDetailResponse.postValue(response.body())
                        } else {
                            _isLoading.value = false
                            when (response.body()?.message) {
                                dev.iconpln.mims.utils.Config.DO_LOGOUT -> {
                                    Helper.logout(context, isLoginSso)
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.session_kamu_telah_habis),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> Toast.makeText(
                                    context,
                                    response.body()?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}