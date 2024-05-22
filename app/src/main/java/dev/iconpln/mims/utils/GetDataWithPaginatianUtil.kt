package dev.iconpln.mims.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.R
import dev.iconpln.mims.data.local.database.DaoSession
import dev.iconpln.mims.data.local.database.TMonitoringComplaint
import dev.iconpln.mims.data.local.database.TMonitoringComplaintDao
import dev.iconpln.mims.data.local.database.TMonitoringComplaintDetail
import dev.iconpln.mims.data.local.database.TMonitoringPermintaanDao
import dev.iconpln.mims.data.local.database.TMonitoringPermintaanDetail
import dev.iconpln.mims.data.local.database.TMonitoringPermintaanDetailDao
import dev.iconpln.mims.data.local.database.TMonitoringSnMaterial
import dev.iconpln.mims.data.local.database.TPemeriksaan
import dev.iconpln.mims.data.local.database.TPemeriksaanDetail
import dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhir
import dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao
import dev.iconpln.mims.data.local.database.TPosPenerimaan
import dev.iconpln.mims.data.local.database.TPosPenerimaanDao
import dev.iconpln.mims.data.local.database.TPosSns
import dev.iconpln.mims.data.local.database.TTransMonitoringPermintaan
import dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDao
import dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetail
import dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetailDao
import dev.iconpln.mims.data.remote.response.MonitoringKomplainDetailResponse
import dev.iconpln.mims.data.remote.response.MonitoringPemeriksaanDetailResponse
import dev.iconpln.mims.data.remote.response.MonitoringPermintaanDetailResponse
import dev.iconpln.mims.data.remote.response.PosPenerimaanAkhirResponse
import dev.iconpln.mims.data.remote.response.PosSnsResponse
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.ui.monitoring_complaint.MonitoringComplaintActivity
import dev.iconpln.mims.ui.monitoring_complaint.detail_monitoring_complaint.MonitoringComplaintDetailActivity
import dev.iconpln.mims.ui.monitoring_permintaan.MonitoringPermintaanActivity
import dev.iconpln.mims.ui.pemeriksaan.PemeriksaanActivity
import dev.iconpln.mims.ui.penerimaan.PenerimaanActivity
import dev.iconpln.mims.ui.penerimaan.detail_penerimaan_akhir.DetailPenerimaanAkhirActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class GetDataWithPaginatianUtil(context: Context, daoSession: DaoSession) {

    val mContext = context
    val mDaoSession = daoSession
    private lateinit var dialog: Dialog

    //TODO sisa pemeriksaan
    fun getDataPosSns(po: TPosPenerimaan, searchText: String){
        dialog = Dialog(mContext)
        dialog.setContentView(R.layout.popup_loading)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown

        if (!dialog.isShowing){
            dialog.show()
        }

        var page = SharedPrefsUtils.getIntegerPreference(mContext,"PAGE_GET_POSNS_${po.noDoSmar}",1)
        var totalPage = SharedPrefsUtils.getIntegerPreference(mContext,"TOTAL_PAGE_GET_POSNS_${po.noDoSmar}", 0)
        var messageGetData = if (totalPage == 0) "Memulai penarikan data" else "Penarikan data $page of $totalPage"
        Toast.makeText(mContext, messageGetData, Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.Main).launch {
            val response = ApiConfig.getApiService(mContext)
                .getPosSns(po.noDoSmar,page.toString(),"20000")
            if (response.isSuccessful) {
                try {
                    val isLoginSso =
                        SharedPrefsUtils.getIntegerPreference(
                            mContext,
                            Config.IS_LOGIN_SSO,
                            0
                        )

                    when (response.body()?.status) {
                        Config.KEY_SUCCESS -> {

                            insertDataPosSns(response,po,page,mDaoSession,searchText)

                        }

                        //lanjut force logout di endpoint yang lain
                        Config.KEY_FAILURE -> {
                            dialog.dismiss()
                            if (response.body()?.message == Config.DO_LOGOUT) {
                                Helper.logout(mContext, isLoginSso)
                                Toast.makeText(
                                    mContext,
                                    mContext.getString(R.string.session_kamu_telah_habis),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else Toast.makeText(
                                mContext,
                                response.body()?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    dialog.dismiss()
                    Toast.makeText(mContext, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
            } else {
                dialog.dismiss()
                Toast.makeText(
                    mContext,
                    if (response.body()?.message.isNullOrEmpty()) mContext.getString(R.string.periksa_vpn_anda)
                    else
                        response.body()?.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun getDataPosPenerimaanAkhir(po: TPosPenerimaan, searchText: String){
        dialog = Dialog(mContext)
        dialog.setContentView(R.layout.popup_loading)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown

        if (!dialog.isShowing){
            dialog.show()
        }

        var page = SharedPrefsUtils.getIntegerPreference(mContext,"PAGE_GET_PENERIMAAN_AKHIR_${po.noDoSmar}",1)
        var totalPage = SharedPrefsUtils.getIntegerPreference(mContext,"TOTAL_PAGE_GET_PENERIMAAN_AKHIR_${po.noDoSmar}", 0)
        var messageGetData = if (totalPage == 0) "Memulai penarikan data" else "Penarikan data $page of $totalPage"

        if (page >= totalPage){
            if (totalPage != 0){
                mContext.startActivity(
                    Intent(
                        mContext,
                        DetailPenerimaanAkhirActivity::class.java
                    )
                        .putExtra("noDo", po.noDoSmar)
                )
                dialog.dismiss()
                return
            }
        }

        Toast.makeText(mContext, messageGetData, Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.Main).launch {
            val response = ApiConfig.getApiService(mContext)
                .getPosSnsPenerimaanAkhir(po.noDoSmar,po.storloc,page.toString(),"20000")
            if (response.isSuccessful) {
                try {
                    val isLoginSso =
                        SharedPrefsUtils.getIntegerPreference(
                            mContext,
                            Config.IS_LOGIN_SSO,
                            0
                        )

                    when (response.body()?.status) {
                        Config.KEY_SUCCESS -> {

                            insertDataPenerimaanAkhir(response,po,page,mDaoSession,searchText)

                        }

                        //lanjut force logout di endpoint yang lain
                        Config.KEY_FAILURE -> {
                            dialog.dismiss()
                            if (response.body()?.message == Config.DO_LOGOUT) {
                                Helper.logout(mContext, isLoginSso)
                                Toast.makeText(
                                    mContext,
                                    mContext.getString(R.string.session_kamu_telah_habis),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else Toast.makeText(
                                mContext,
                                response.body()?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    dialog.dismiss()
                    Toast.makeText(mContext, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
            } else {
                dialog.dismiss()
                Toast.makeText(
                    mContext,
                    if (response.body()?.message.isNullOrEmpty()) mContext.getString(R.string.periksa_vpn_anda)
                    else
                        response.body()?.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun getMonitoringKomplainDetail(complaint: TMonitoringComplaint){
        dialog = Dialog(mContext)
        dialog.setContentView(R.layout.popup_loading)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown

        if (!dialog.isShowing){
            dialog.show()
        }

        var page = SharedPrefsUtils.getIntegerPreference(mContext,"PAGE_GET_KOMPLAIN_${complaint.noKomplain}",1)
        var totalPage = SharedPrefsUtils.getIntegerPreference(mContext,"TOTAL_PAGE_GET_KOMPLAIN_${complaint.noKomplain}", 0)
        var messageGetData = if (totalPage == 0) "Memulai penarikan data" else "Penarikan data $page of $totalPage"

        if (page >= totalPage){
            if (totalPage != 0){
                if (mContext is MonitoringComplaintActivity){
                    val complaints = mDaoSession.tMonitoringComplaintDao.queryBuilder()
                        .orderAsc(TMonitoringComplaintDao.Properties.Status)
                        .list()

                    mContext.adapter.setComplaint(complaints)
                }
                dialog.dismiss()
                return
            }
        }

        Toast.makeText(mContext, messageGetData, Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.Main).launch {
            val response = ApiConfig.getApiService(mContext)
                .getMonitoringKomplainDetail(complaint.noKomplain,page.toString(),"20000")
            if (response.isSuccessful) {
                try {
                    val isLoginSso =
                        SharedPrefsUtils.getIntegerPreference(
                            mContext,
                            Config.IS_LOGIN_SSO,
                            0
                        )

                    when (response.body()?.status) {
                        Config.KEY_SUCCESS -> {

                            insertDataKomplainDetail(response,complaint,page)

                        }

                        //lanjut force logout di endpoint yang lain
                        Config.KEY_FAILURE -> {
                            dialog.dismiss()
                            if (response.body()?.message == Config.DO_LOGOUT) {
                                Helper.logout(mContext, isLoginSso)
                                Toast.makeText(
                                    mContext,
                                    mContext.getString(R.string.session_kamu_telah_habis),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else Toast.makeText(
                                mContext,
                                response.body()?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    dialog.dismiss()
                    Toast.makeText(mContext, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
            } else {
                dialog.dismiss()
                Toast.makeText(
                    mContext,
                    if (response.body()?.message.isNullOrEmpty()) mContext.getString(R.string.periksa_vpn_anda)
                    else
                        response.body()?.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun getMonitoringPermintaanDetail(mp: TTransMonitoringPermintaan){
        dialog = Dialog(mContext)
        dialog.setContentView(R.layout.popup_loading)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown

        if (!dialog.isShowing){
            dialog.show()
        }

        var page = SharedPrefsUtils.getIntegerPreference(mContext,"PAGE_GET_PERMINTAAN_${mp.noTransaksi}",1)
        var totalPage = SharedPrefsUtils.getIntegerPreference(mContext,"TOTAL_PAGE_GET_PERMINTAAN_${mp.noTransaksi}", 0)
        var messageGetData = if (totalPage == 0) "Memulai penarikan data" else "Penarikan data $page of $totalPage"

        if (page >= totalPage){
            if (totalPage != 0){
                if (mContext is MonitoringPermintaanActivity){
                    val lisMonitoring = mDaoSession.tTransMonitoringPermintaanDao.queryBuilder().list()


                    mContext.adapter.setMpList(lisMonitoring)

                }
                dialog.dismiss()
                return
            }
        }

        Toast.makeText(mContext, messageGetData, Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.Main).launch {
            val response = ApiConfig.getApiService(mContext)
                .getMonitoringPermintaanDetail(mp.noTransaksi,mp.plant,page.toString(),"20000")
            if (response.isSuccessful) {
                try {
                    val isLoginSso =
                        SharedPrefsUtils.getIntegerPreference(
                            mContext,
                            Config.IS_LOGIN_SSO,
                            0
                        )

                    when (response.body()?.status) {
                        Config.KEY_SUCCESS -> {

                            insertDataPermintaanDetail(response,mp,page)

                        }

                        //lanjut force logout di endpoint yang lain
                        Config.KEY_FAILURE -> {
                            dialog.dismiss()
                            if (response.body()?.message == Config.DO_LOGOUT) {
                                Helper.logout(mContext, isLoginSso)
                                Toast.makeText(
                                    mContext,
                                    mContext.getString(R.string.session_kamu_telah_habis),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else Toast.makeText(
                                mContext,
                                response.body()?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    dialog.dismiss()
                    Toast.makeText(mContext, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
            } else {
                dialog.dismiss()
                Toast.makeText(
                    mContext,
                    if (response.body()?.message.isNullOrEmpty()) mContext.getString(R.string.periksa_vpn_anda)
                    else
                        response.body()?.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun getMonitoringPemeriksaanDetail(pe: TPemeriksaan,noDo: String){
        dialog = Dialog(mContext)
        dialog.setContentView(R.layout.popup_loading)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown

        if (!dialog.isShowing){
            dialog.show()
        }

        var page = SharedPrefsUtils.getIntegerPreference(mContext,"PAGE_GET_PEMERIKSAAN_${pe.noPemeriksaan}",1)
        var totalPage = SharedPrefsUtils.getIntegerPreference(mContext,"TOTAL_PAGE_GET_PEMERIKSAAN_${pe.noPemeriksaan}", 0)
        var messageGetData = if (totalPage == 0) "Memulai penarikan data" else "Penarikan data $page of $totalPage"

        if (page >= totalPage){
            if (totalPage != 0){
                if (mContext is PemeriksaanActivity){
                    val listPemeriksaan = mDaoSession.tPemeriksaanDao.queryBuilder()
                        .where(
                            dev.iconpln.mims.data.local.database.TPemeriksaanDao.Properties.IsDone.eq(
                                0
                            )
                        )
                        .whereOr(
                            dev.iconpln.mims.data.local.database.TPemeriksaanDao.Properties.NoDoSmar.like(
                                "%" + noDo + "%"
                            ),
                            dev.iconpln.mims.data.local.database.TPemeriksaanDao.Properties.PoSapNo.like(
                                "%" + noDo + "%"
                            )
                        ).list()

                    mContext.adapter.setPeList(listPemeriksaan)

                }
                dialog.dismiss()
                return
            }
        }

        Toast.makeText(mContext, messageGetData, Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.Main).launch {
            val response = ApiConfig.getApiService(mContext)
                .getMonitoringPemeriksaanDetail(pe.noPemeriksaan,page.toString(),"20000")
            if (response.isSuccessful) {
                try {
                    val isLoginSso =
                        SharedPrefsUtils.getIntegerPreference(
                            mContext,
                            Config.IS_LOGIN_SSO,
                            0
                        )

                    when (response.body()?.status) {
                        Config.KEY_SUCCESS -> {

                            insertDataPemeriksaanDetail(response,pe,page,noDo)

                        }

                        //lanjut force logout di endpoint yang lain
                        Config.KEY_FAILURE -> {
                            dialog.dismiss()
                            if (response.body()?.message == Config.DO_LOGOUT) {
                                Helper.logout(mContext, isLoginSso)
                                Toast.makeText(
                                    mContext,
                                    mContext.getString(R.string.session_kamu_telah_habis),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else Toast.makeText(
                                mContext,
                                response.body()?.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    dialog.dismiss()
                    Toast.makeText(mContext, response.body()?.message, Toast.LENGTH_SHORT)
                        .show()
                    e.printStackTrace()
                }
            } else {
                dialog.dismiss()
                Toast.makeText(
                    mContext,
                    if (response.body()?.message.isNullOrEmpty()) mContext.getString(R.string.periksa_vpn_anda)
                    else
                        response.body()?.message.toString(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private suspend fun insertDataPosSns(response: Response<PosSnsResponse>,
                                         po: TPosPenerimaan,
                                         page: Int, daoSession: DaoSession, searchText: String)
    {

        withContext(Dispatchers.IO){
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
                mDaoSession.tPosSnsDao.insertInTx(data)
            }
        }

        SharedPrefsUtils.setIntegerPreference(mContext,"PAGE_GET_POSNS_${po.noDoSmar}",page + 1)
        SharedPrefsUtils.setIntegerPreference(mContext,"TOTAL_PAGE_GET_POSNS_${po.noDoSmar}", response.body()?.totalPage!!)

        if (response.body()?.isNext == 1){
            dialog.dismiss()

            getDataPosSns(po,searchText)
        }else{
            insertDetailPenerimaan(daoSession,po.noDoSmar)

            dialog.dismiss()

            if (mContext is PenerimaanActivity){
                val penerimaans = daoSession.tPosPenerimaanDao.queryBuilder()
                    .where(TPosPenerimaanDao.Properties.NoDoSmar.like("%" + searchText + "%"))
                    .orderDesc(TPosPenerimaanDao.Properties.CreatedDate)
                    .list()

                mContext.adapter.setData(penerimaans)
            }
        }

    }

    private suspend fun insertDataPenerimaanAkhir(
        response: Response<PosPenerimaanAkhirResponse>,
        po: TPosPenerimaan,
        page: Int, daoSession: DaoSession, searchText: String
    )
    {

        withContext(Dispatchers.IO){
            val result = response.body()?.dataPenerimaanAkhir

            val posSnsItems = result?.map { model ->
                TPosDetailPenerimaanAkhir().apply {
                    kdPabrikan = model?.kdPabrikan
                    noDoSmar = model?.noDoSmar
                    qty = model?.qtyDo.toString()
                    storLoc = model?.storLoc
                    isComplaint = model?.isKomplained
                    isReceived = model?.isReceived
                    isRejected = model?.isRejected
                    namaKategoriMaterial = model?.namaKategoriMaterial
                    status = model?.status
                    noMaterial = model?.noMatSap
                    noPackaging = model?.noPackaging
                    serialNumber = model?.noSerial
                    namaPabrikan = model?.namaPabrikan
                }
            } ?: emptyList()

            posSnsItems.forEach { data ->
                mDaoSession.tPosDetailPenerimaanAkhirDao.insertInTx(data)
            }
        }

        SharedPrefsUtils.setIntegerPreference(mContext,"PAGE_GET_PENERIMAAN_AKHIR_${po.noDoSmar}",page + 1)
        SharedPrefsUtils.setIntegerPreference(mContext,"TOTAL_PAGE_GET_PENERIMAAN_AKHIR_${po.noDoSmar}", response.body()?.totalPage!!)

        if (response.body()?.isNext == 1){
            dialog.dismiss()

            getDataPosPenerimaanAkhir(po,searchText)
        }else{
            dialog.dismiss()

            mContext.startActivity(
                Intent(
                    mContext,
                    DetailPenerimaanAkhirActivity::class.java
                )
                    .putExtra("noDo", po.noDoSmar)
            )
        }

    }

    private suspend fun insertDataKomplainDetail(
        response: Response<MonitoringKomplainDetailResponse>,
        complaint: TMonitoringComplaint,
        page: Int
    )
    {

        withContext(Dispatchers.IO){
            val result = response.body()?.monitoringKomplainDetail

            val complaintDetailItem = result?.map { model ->
                TMonitoringComplaintDetail().apply {
                    doLineItem = model?.doLineItem
                    status = model?.status
                    noPackaging = model?.noPackaging
                    noMatSap = model?.noMatSap
                    noKomplain = model?.noKomplain
                    noSerial = model?.noSerial
                    tanggalPengajuan = model?.tanggalPengajuan
                    tanggalSelesai = ""
                    noDoSmar = model?.noDoSmar
                    isChecked = 0
                    isDone = 0
                    statusPeriksa = ""
                }
            } ?: emptyList()

            complaintDetailItem.forEach { data ->
                mDaoSession.tMonitoringComplaintDetailDao.insertInTx(data)
            }
        }

        SharedPrefsUtils.setIntegerPreference(mContext,"PAGE_GET_KOMPLAIN_${complaint.noKomplain}",page + 1)
        SharedPrefsUtils.setIntegerPreference(mContext,"TOTAL_PAGE_GET_KOMPLAIN_${complaint.noKomplain}", response.body()?.totalPage!!)

        if (response.body()?.isNext == 1){
            dialog.dismiss()

            getMonitoringKomplainDetail(complaint)
        }else{
            dialog.dismiss()

            if (mContext is MonitoringComplaintActivity){
                val complaints = mDaoSession.tMonitoringComplaintDao.queryBuilder()
                    .orderAsc(TMonitoringComplaintDao.Properties.Status)
                    .list()

                mContext.adapter.setComplaint(complaints)
            }
        }

    }

    private suspend fun insertDataPermintaanDetail(
        response: Response<MonitoringPermintaanDetailResponse>,
        mp: TTransMonitoringPermintaan,
        page: Int
    )
    {

        withContext(Dispatchers.IO){
            val result = response.body()?.monitoringPermintaanDetails
            val result2 = response.body()?.snPermintaan

            val permintaanDetailItem = result?.map { model ->
                TMonitoringPermintaanDetail().apply {
                    unit = model?.unit
                    nomorMaterial = model?.nomorMaterial
                    kategori = model?.kategori
                    materialDesc = model?.materialDesc
                    noPermintaan = model?.noPermintaan
                    noTransaksi = model?.noTransaksi
                    noRepackaging = model?.noRepackaging
                    qtyPengeluaran = model?.qtyPengeluaran
                    qtyPermintaan = model?.qtyPermintaan ?: 0.0
                    isActive = model?.isActive
                    qtyScan = model?.qtyScan
                    valuationType = model?.valuationType
                    jumlahKardus = model?.jumlahKardus
                }
            } ?: emptyList()

            val snPermintaanItems = result2?.map { model ->
                TMonitoringSnMaterial().apply {
                    noRepackaging = model?.noRepackaging
                    nomorMaterial = model?.nomorMaterial
                    serialNumber = model?.serialNumber
                    valuationType = model?.valuationType
                    noIdMeter = model?.noIdMeter
                    status = model?.status
                    isScanned = 0
                }
            } ?: emptyList()

            permintaanDetailItem.forEach { data ->
                mDaoSession.tMonitoringPermintaanDetailDao.insertInTx(data)
            }

            snPermintaanItems.forEach { data ->
                mDaoSession.tMonitoringSnMaterialDao.insertInTx(data)
            }
        }

        SharedPrefsUtils.setIntegerPreference(mContext,"PAGE_GET_PERMINTAAN_${mp.noTransaksi}",page + 1)
        SharedPrefsUtils.setIntegerPreference(mContext,"TOTAL_PAGE_GET_PERMINTAAN_${mp.noTransaksi}", response.body()?.totalPage!!)

        if (response.body()?.isNext == 1){
            dialog.dismiss()

            getMonitoringPermintaanDetail(mp)
        }else{
            setDetailPermintaan(mp)

            dialog.dismiss()

            if (mContext is MonitoringPermintaanActivity){
                val lisMonitoring = mDaoSession.tTransMonitoringPermintaanDao.queryBuilder().list()


                mContext.adapter.setMpList(lisMonitoring)

            }
        }

    }

    private suspend fun insertDataPemeriksaanDetail(
        response: Response<MonitoringPemeriksaanDetailResponse>,
        pe: TPemeriksaan,
        page: Int,
        noDo: String
    )
    {

        withContext(Dispatchers.IO){
            val result = response.body()?.pemeriksaanDetail

            val pemeriksaanItem = result?.map { model ->
                TPemeriksaanDetail().apply {
                    noPemeriksaan =
                        if (model?.noPemeriksaan.isNullOrEmpty()) "" else model?.noPemeriksaan
                    sn = model?.noSerial
                    noDoSmar = model?.noDoSmar
                    noMaterail = model?.noMatSap
                    noPackaging = model?.noPackaging
                    kategori = model?.namaKategoriMaterial
                    statusPenerimaan = "" //belum perlu ditarik
                    statusPemeriksaan = model?.status
                    if (model?.status == "BELUM DIPERIKSA") {
                        isPeriksa = 1
                        isComplaint = 0
                        isChecked = 0
                    }
                    if (model?.status == "KOMPLAIN") {
                        isPeriksa = 0
                        isComplaint = 1
                        isChecked = 1
                    }
                    if (model?.status.isNullOrEmpty()) {
                        isPeriksa = 0
                        isComplaint = 0
                        isChecked = 0
                    }
                    isDone = 0
                }
            } ?: emptyList()

            pemeriksaanItem.forEach { data ->
                mDaoSession.tPemeriksaanDetailDao.insertInTx(data)
            }
        }

        SharedPrefsUtils.setIntegerPreference(mContext,"PAGE_GET_PEMERIKSAAN_${pe.noPemeriksaan}",page + 1)
        SharedPrefsUtils.setIntegerPreference(mContext,"TOTAL_PAGE_GET_PEMERIKSAAN_${pe.noPemeriksaan}", response.body()?.totalPage!!)

        if (response.body()?.isNext == 1){
            dialog.dismiss()

            getMonitoringPemeriksaanDetail(pe,noDo)
        }else{
            dialog.dismiss()

            if (mContext is PemeriksaanActivity){
                val listPemeriksaan = mDaoSession.tPemeriksaanDao.queryBuilder()
                    .where(
                        dev.iconpln.mims.data.local.database.TPemeriksaanDao.Properties.IsDone.eq(
                            0
                        )
                    )
                    .whereOr(
                        dev.iconpln.mims.data.local.database.TPemeriksaanDao.Properties.NoDoSmar.like(
                            "%" + noDo + "%"
                        ),
                        dev.iconpln.mims.data.local.database.TPemeriksaanDao.Properties.PoSapNo.like(
                            "%" + noDo + "%"
                        )
                    ).list()

                mContext.adapter.setPeList(listPemeriksaan)

            }
        }

    }

    private fun insertDetailPenerimaan(daoSession: DaoSession,noDo: String) {
        val penerimaanDetails = daoSession.tPosDetailPenerimaanDao.queryBuilder()
            .where(TPosDetailPenerimaanDao.Properties.IsDone.eq(0),
                TPosDetailPenerimaanDao.Properties.NoDoSmar.eq(noDo))
            .list()

        if (penerimaanDetails.isNullOrEmpty()) {
            val listPos = daoSession.tPosSnsDao.queryBuilder().list()
            val size = listPos.size
            if (size > 0) {
                val items =
                    arrayOfNulls<dev.iconpln.mims.data.local.database.TPosDetailPenerimaan>(size)
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
                    item.namaPabrikan = model.namaPabrikan
                    if (model.statusPenerimaan.isNullOrEmpty()) item.statusPenerimaan =
                        "" else item.statusPenerimaan = model.statusPenerimaan
                    if (model.statusPemeriksaan.isNullOrEmpty()) item.statusPemeriksaan =
                        "" else item.statusPemeriksaan = model.statusPemeriksaan
                    item.doLineItem = model?.doLineItem
                    item.isComplaint = 0
                    if (model.statusPenerimaan == "DITERIMA") {
                        item.isDone = 1
                        item.isChecked = 1
                    } else if (model.statusPenerimaan == "BELUM DIPERIKSA") {
                        item.isDone = 1
                        item.isChecked = 1
                    } else {
                        item.isDone = 0
                        item.isChecked = 0
                    }
                    item.partialCode = ""
                    items[i] = item
                }
                daoSession.tPosDetailPenerimaanDao.insertInTx(items.toList())
            }
        }
    }

    private fun setDetailPermintaan(mp: TTransMonitoringPermintaan) {
        val isDataExist =
            mDaoSession.tTransMonitoringPermintaanDetailDao.queryBuilder()
                .where(TTransMonitoringPermintaanDetailDao.Properties.NoTransaksi.eq(mp.noTransaksi))
                .list().size > 0

        val listDetailPermintaan = mDaoSession.tMonitoringPermintaanDetailDao.queryBuilder()
            .where(TMonitoringPermintaanDetailDao.Properties.NoTransaksi.eq(mp.noTransaksi))
            .list()

        if (!isDataExist) {
            val size = listDetailPermintaan.size
            if (size > 0) {
                val items =
                    arrayOfNulls<dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetail>(
                        size
                    )
                var item: dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetail
                for ((i, model) in listDetailPermintaan.withIndex()) {
                    item =
                        dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetail()
                    item.unit = model?.unit
                    item.nomorMaterial = model?.nomorMaterial
                    item.kategori = model?.kategori
                    item.materialDesc = model?.materialDesc
                    item.noPermintaan = model?.noPermintaan
                    item.noTransaksi = model?.noTransaksi
                    item.noRepackaging = model?.noRepackaging
                    item.qtyPengeluaran = model?.qtyPengeluaran
                    item.qtyPermintaan = model?.qtyPermintaan ?: 0.0
                    item.qtyScan = model?.qtyScan
                    item.isActive = model?.isActive
                    item.qtyAkanDiScan = 0
                    item.isScannedSn = 0
                    item.isDone = 0
                    item.valuationType = model?.valuationType
                    item.jumlahKardus = model?.jumlahKardus

                    items[i] = item
                }
                mDaoSession.tTransMonitoringPermintaanDetailDao.insertInTx(items.toList())
            }
        }
    }
}