package dev.iconpln.mims.ui.pengiriman

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.iconpln.mims.R
import dev.iconpln.mims.data.local.database.DaoSession
import dev.iconpln.mims.data.local.database.TPosDao
import dev.iconpln.mims.data.local.database.TPosSnsDao
import dev.iconpln.mims.data.remote.response.DatasItemLokasi
import dev.iconpln.mims.data.remote.response.GenericResponse
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.LocalDateTime

class PengirimanViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _pengirimanResponse =
        MutableLiveData<List<dev.iconpln.mims.data.local.database.TPos>>()
    val pengirimanResponse: LiveData<List<dev.iconpln.mims.data.local.database.TPos>> =
        _pengirimanResponse

    private val _pengirimanDetailResponse =
        MutableLiveData<List<dev.iconpln.mims.data.local.database.TPosSns>>()
    val pengirimanDetailResponse: LiveData<List<dev.iconpln.mims.data.local.database.TPosSns>> =
        _pengirimanDetailResponse

    private val _datasLokasi = MutableLiveData<List<DatasItemLokasi>>()
    val datasLokasi: LiveData<List<DatasItemLokasi>> = _datasLokasi

    private val _genericResponse = MutableLiveData<GenericResponse>()
    val genericResponse: LiveData<GenericResponse> = _genericResponse

    fun getPengiriman(
        daoSession: dev.iconpln.mims.data.local.database.DaoSession,
        noDo: String,
        noPo: String,
        kodeStatus: String
    ) {
        try {
            val pengirimans = daoSession.tPosDao.queryBuilder()
                .where(
                    TPosDao.Properties.NoDoSmar.like("%" + noDo + "%"),
                    TPosDao.Properties.PoSapNo.like("%" + noPo + "%"),
                    TPosDao.Properties.KodeStatusDoMims.like("%" + kodeStatus + "%")
                )
                .orderDesc(TPosDao.Properties.PoDate).list()
            _pengirimanResponse.postValue(pengirimans)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getDataByNoPengiriman(
        daoSession: dev.iconpln.mims.data.local.database.DaoSession,
        noPengiriman: String
    ) {
        try {
            val pengirimans = daoSession.tPosDao
                .queryBuilder()
                .where(TPosDao.Properties.NoDoSmar.eq(noPengiriman)).list()
            _pengirimanResponse.postValue(pengirimans)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getDataDetailPengiriman(
        daoSession: dev.iconpln.mims.data.local.database.DaoSession,
        noPengiriman: String,
        noSn: String
    ) {
        try {
            var listDetailPengiriman = daoSession.tPosSnsDao.queryBuilder()
                .where(TPosSnsDao.Properties.NoSerial.like("%" + noSn + "%"))
                .where(TPosSnsDao.Properties.NoDoSmar.eq(noPengiriman)).list()
            _pengirimanDetailResponse.postValue(listDetailPengiriman)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getLokasi(context: Context, doMims: String) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiConfig.getApiService(context)
                .getLokasi(doMims!!)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                        context,
                        Config.IS_LOGIN_SSO, 0
                    )
                    try {
                        _isLoading.value = false
                        if (response.body()?.status == Config.KEY_SUCCESS) {
                            _datasLokasi.postValue(response.body()?.datas)
                        } else {
                            _isLoading.value = false
                            when (response.body()?.message) {
                                Config.DO_LOGOUT -> {
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
                    } catch (e: Exception) {
                        _isLoading.value = false
                        e.printStackTrace()
                    }
                } else {
                    _isLoading.value = false
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun deleteLokasi(data: DatasItemLokasi, context: Context) {
        _isLoading.value = true
        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiConfig.getApiService(context)
                .deleteLokasi(data.id!!, data.noDoMims!!)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                        context,
                        Config.IS_LOGIN_SSO, 0
                    )
                    try {
                        if (response.body()?.status == Config.KEY_SUCCESS) {
                            _genericResponse.postValue(response.body())

                        } else {
                            when (response.body()?.message) {
                                Config.DO_LOGOUT -> {
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
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun addLokasi(
        context: Context,
        lokasi: String,
        doMims: String,
        noDoSmar: String,
        noPo: String,
        daoSession: DaoSession
    ) {
        val currentDateTime = LocalDateTime.now().toString(Config.DATETIME)
        _isLoading.value = true

        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiConfig.getApiService(context)
                .sendLokasi(doMims, lokasi, noPo, noDoSmar)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                        context,
                        Config.IS_LOGIN_SSO, 0
                    )
                    try {
                        if (response.body()?.status == Config.KEY_SUCCESS) {
                            var mLokasi =
                                dev.iconpln.mims.data.local.database.TLokasi()
                            mLokasi.updateDate = currentDateTime
                            mLokasi.noDoSns = doMims
                            mLokasi.ket = lokasi
                            daoSession.tLokasiDao.insert(mLokasi)
                            _genericResponse.postValue(response.body())

                        } else {
                            when (response.body()?.message) {
                                Config.DO_LOGOUT -> {
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
                    } catch (e: Exception) {
                        _isLoading.value = false
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