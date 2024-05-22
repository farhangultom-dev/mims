package dev.iconpln.mims.ui.penerimaan

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.iconpln.mims.R
import dev.iconpln.mims.data.local.database.DaoSession
import dev.iconpln.mims.data.local.database.TPosPenerimaan
import dev.iconpln.mims.data.local.database.TPosSns
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class PenerimaanViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorResponse = MutableLiveData<Int>()
    val errorResponse: LiveData<Int> = _errorResponse

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _po = MutableLiveData<TPosPenerimaan>()
    val po : LiveData<TPosPenerimaan> = _po

    private val _penerimaanResponse =
        MutableLiveData<List<dev.iconpln.mims.data.local.database.TPosPenerimaan>>()
    val penerimaanResponse: LiveData<List<dev.iconpln.mims.data.local.database.TPosPenerimaan>> =
        _penerimaanResponse

    private val _penerimaanDetailResponse =
        MutableLiveData<List<dev.iconpln.mims.data.local.database.TPosDetailPenerimaan>>()
    val penerimaanDetailResponse: LiveData<List<dev.iconpln.mims.data.local.database.TPosDetailPenerimaan>> =
        _penerimaanDetailResponse

    fun insertDetailPenerimaan(
        daoSession: dev.iconpln.mims.data.local.database.DaoSession,
        listDetailPenerimaan: List<dev.iconpln.mims.data.local.database.TPosDetailPenerimaan>
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                _isLoading.value = true

                if (listDetailPenerimaan.isNotEmpty()) {
                    _penerimaanDetailResponse.postValue(listDetailPenerimaan)
                } else {
                    val listPos = daoSession.tPosSnsDao.queryBuilder().list()
                    val size = listPos.size
                    if (size > 0) {
                        val items =
                            arrayOfNulls<dev.iconpln.mims.data.local.database.TPosDetailPenerimaan>(
                                size
                            )
                        var item: dev.iconpln.mims.data.local.database.TPosDetailPenerimaan
                        for ((i, model) in listPos.withIndex()) {
                            item =
                                dev.iconpln.mims.data.local.database.TPosDetailPenerimaan()

                            item.noDoSmar = model.noDoSmar
                            item.qty = ""
                            item.kdPabrikan = model.kdPabrikan
                            item.doStatus = model.doStatus
                            item.noPackaging = model.noPackaging
                            item.serialNumber = model.noSerial
                            item.noMaterial = model.noMatSap
                            item.namaKategoriMaterial = model.namaKategoriMaterial
                            item.storLoc = model.storLoc
                            if (model.statusPenerimaan.isNullOrEmpty()) item.statusPenerimaan =
                                "" else item.statusPenerimaan = model.statusPenerimaan
                            if (model.statusPemeriksaan.isNullOrEmpty()) item.statusPemeriksaan =
                                "" else item.statusPemeriksaan = model.statusPemeriksaan
                            item.doLineItem = model?.doLineItem
                            item.isComplaint = 0
                            item.isChecked = 0
                            item.isDone = 0
                            items[i] = item
                        }
                        daoSession.tPosDetailPenerimaanDao.insertInTx(items.toList())
                    }
                }
                _isLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }

        }
    }

    fun getPenerimaan(
        daoSession: dev.iconpln.mims.data.local.database.DaoSession,
        penerimaans: List<dev.iconpln.mims.data.local.database.TPosPenerimaan>
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                _isLoading.value = true

                if (penerimaans.isNotEmpty()) {
                    _penerimaanResponse.postValue(penerimaans)
                } else {
                    val listPos = daoSession.tPosDao.queryBuilder().list()
                    val size = listPos.size
                    if (size > 0) {
                        val items =
                            arrayOfNulls<dev.iconpln.mims.data.local.database.TPosPenerimaan>(size)
                        var item: dev.iconpln.mims.data.local.database.TPosPenerimaan
                        for ((i, model) in listPos.withIndex()) {
                            item = dev.iconpln.mims.data.local.database.TPosPenerimaan()
                            item.createdDate = model?.createdDate
                            item.leadTime = model?.leadTime
                            item.storloc = model?.storLoc
                            item.noDoSmar = model?.noDoSmar
                            item.planCodeNo = model?.planCodeNo
                            item.plantName = model?.plantName
                            item.poMpNo = model?.poMpNo
                            item.poSapNo = model?.poSapNo
                            item.tlskNo = model?.tlskNo
                            item.total = model?.total
                            item.kdPabrikan = model?.kdPabrikan
                            item.materialGroup = model?.materialGroup
                            item.namaKategoriMaterial = model?.namaKategoriMaterial
                            item.noDoMims = model?.noDoMims
                            item.total = model?.total
                            item.courierPersonName = model.courierPersonName
                            item.doLineItem = model.doLineItem
                            item.namaPabrikan = model.namaPabrikan

                            if (model.expeditions.isNullOrEmpty()) {
                                item.expeditions = ""
                            } else {
                                item.expeditions = model.expeditions
                            }

                            if (model.tglDiterima.isNullOrEmpty()) item.tanggalDiterima =
                                "" else item.tanggalDiterima = model.tglDiterima
                            if (model.petugasPenerima.isNullOrEmpty()) item.petugasPenerima =
                                "" else item.petugasPenerima = model.petugasPenerima
                            if (model.kurirPengantar.isNullOrEmpty()) item.kurirPengantar =
                                "" else item.kurirPengantar = model.kurirPengantar

                            item.statusPemeriksaan =
                                if (model?.statusPemeriksaan.isNullOrEmpty()) "" else model?.statusPemeriksaan
                            item.statusPenerimaan =
                                if (model?.statusPenerimaan.isNullOrEmpty()) "" else model?.statusPenerimaan

                            item.poDate = model.poDate
                            item.kodeStatusDoMims = model.kodeStatusDoMims
                            item.doStatus = model.doStatus
                            item.expeditions = model.expeditions
                            item.kdPabrikan = model.kdPabrikan
                            item.materialGroup = model.materialGroup
                            item.namaKategoriMaterial = model.namaKategoriMaterial
                            item.ratingPenerimaan = model.ratingResponse
                            item.descPenerimaan = ""
                            item.ratingQuality = model.ratingQuality
                            item.descQuality = ""
                            item.ratingWaktu = model.ratingDelivery
                            item.descWaktu = ""
                            if (model != null) {
                                if (model.ratingResponse.isNullOrEmpty() &&
                                    model.ratingDelivery.isNullOrEmpty() &&
                                    model.ratingQuality.isNullOrEmpty()
                                ) item.isDone = 0 else item.isDone = 1

                                if (model.sudahBisaRating) {
                                    item.isRating = 1
                                } else {
                                    item.isRating = 0
                                }
                            }

                            item.ratingDone = if (model.ratingResponse.isNullOrEmpty() &&
                                model.ratingDelivery.isNullOrEmpty() &&
                                model.ratingQuality.isNullOrEmpty()
                            ) 0 else 1

                            item.nilaiRatingPenerimaan = ""
                            item.nilaiRatingQuality = ""
                            item.nilaiRatingWaktu = ""

                            if (model.isBabg && !model.isBabgConfirm || model.slaIntegrasiSap && model.poSapNo.isNullOrEmpty()) {
                                item.bisaTerima = 0
                            } else if (model.isBabg && model.isBabgConfirm || model.slaIntegrasiSap && model.poSapNo.isNotEmpty()) {
                                item.bisaTerima = 1
                            } else if (!model.isBabg || !model.slaIntegrasiSap) {
                                item.bisaTerima = 1
                            } else {
                                item.bisaTerima = 0
                            }
                            items[i] = item
                        }
                        daoSession.tPosPenerimaanDao.insertInTx(items.toList())
                    }
                }
                _isLoading.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }

        }
    }

    fun getPosSns(
        context: Context,
        daoSession: DaoSession,
        noDo: String,
        po: TPosPenerimaan
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val page = SharedPrefsUtils.getIntegerPreference(context,"PAGE_GET_POSNS",1)
                val response = ApiConfig.getApiService(context)
                    .getPosSns(noDo,page.toString(),"10000")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        try {
                            val isLoginSso =
                                SharedPrefsUtils.getIntegerPreference(
                                    context,
                                    Config.IS_LOGIN_SSO,
                                    0
                                )
                            when (response.body()?.status) {
                                Config.KEY_SUCCESS -> {
                                    if (page == response.body()?.totalPage!!){
                                        _po.postValue(po)
                                        _isLoading.postValue(false)
                                    }else{
                                        val result = response.body()?.posSns

                                        val posSnsItems = result?.map { model ->
                                            TPosSns().apply {
                                                doStatus = model?.doStatus
                                                kdPabrikan = model?.kdPabrikan
                                                masaGaransi = model?.masaGaransi.toString()
                                                mmc = "" //tarikan data skrg kosong
                                                materialId = model?.materialId
                                                namaKategoriMaterial = model?.namaKategoriMaterial
                                                noDoSmar = model?.noDoSmar
                                                noMatSap = model?.noMatSap
                                                noProduksi = model?.noProduksi
                                                noSerial = model?.noSerial
                                                noSertMeterologi = model?.nomorSertMaterologi
                                                plant = model?.plant
                                                spesifikasi = model?.spesifikasi
                                                spln = model?.spln
                                                statusPenerimaan =
                                                    if (model?.statusPenerimaan.isNullOrEmpty()) "" else model?.statusPenerimaan
                                                statusPemeriksaan =
                                                    if (model?.statusPemeriksaan.isNullOrEmpty()) "" else model?.statusPemeriksaan
                                                storLoc = model?.storloc
                                                tglProduksi = model?.tglProduksi
                                                noPackaging = model?.noPackaging
                                                doLineItem = model?.doLineItem
                                                namaPabrikan = model?.namaPabrikan
                                            }
                                        } ?: emptyList()

                                        posSnsItems.forEach { data ->
                                            daoSession.tPosSnsDao.insertInTx(data)
                                        }

                                        SharedPrefsUtils.setIntegerPreference(context,"PAGE_GET_POSNS",page + 1)

                                        if (response.body()?.isNext == 1){
                                            getPosSns(context,daoSession,noDo,po)
                                        }else{
                                            _po.postValue(po)
                                            _isLoading.postValue(false)
                                        }
                                    }

                                }

                                //lanjut force logout di endpoint yang lain
                                Config.KEY_FAILURE -> {
                                    if (response.body()?.message == Config.DO_LOGOUT) {
                                        Helper.logout(context, isLoginSso)
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.session_kamu_telah_habis),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else Toast.makeText(
                                        context,
                                        response.body()?.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    _isLoading.postValue(false)
                                }
                            }
                        } catch (e: Exception) {
                            _isLoading.value = false
                            Toast.makeText(context, response.body()?.message, Toast.LENGTH_SHORT)
                                .show()
                            e.printStackTrace()
                        }
                    } else {
                        _isLoading.value = false
                        _errorResponse.postValue(response.code())
                        val error = response.errorBody()?.toString()
                        onError("Error : ${error?.let { getErrorMessage(it) }}")
                        Toast.makeText(
                            context,
                            if (response.body()?.message.isNullOrEmpty()) context.getString(R.string.periksa_vpn_anda)
                            else
                                response.body()?.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun onError(message: String) {
        _isLoading.postValue(false)
        _errorMessage.postValue(message)
    }

    private fun getErrorMessage(raw: String): String {
        return try {
            val obj = JSONObject(raw)
            obj.getString("message")
        } catch (e: Exception) {
            "Error occurred: Unable to parse error message"
        }
    }
}