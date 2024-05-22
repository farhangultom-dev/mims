package dev.iconpln.mims.ui.monitoring_permintaan

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.data.local.database.TTransMonitoringPermintaan
import dev.iconpln.mims.databinding.ActivityMonitoringPermintaanBinding
import dev.iconpln.mims.ui.monitoring_permintaan.monitoring_permintaan_detail.MonitoringPermintaanDetailActivity
import dev.iconpln.mims.utils.GetDataWithPaginatianUtil
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MonitoringPermintaanActivity : BaseActivity() {
    private lateinit var binding: ActivityMonitoringPermintaanBinding
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private val viewModel: MonitoringPermintaanViewModel by viewModels()
    lateinit var adapter: MonitoringPermintaanAdapter
    private var srcNoPermintaanText: String = ""
    private var srcStatusPengeluaranText: String = ""
    private var srcGudangTujuanText: String = ""
    private var srcTglPermintaanText: String = ""
    private lateinit var getDataWithPaginatianUtil: GetDataWithPaginatianUtil
    private lateinit var cal: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitoringPermintaanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!
        cal = Calendar.getInstance()
        setDataPermintaan()
//        setDetailPermintaan()
        getDataWithPaginatianUtil = GetDataWithPaginatianUtil(this@MonitoringPermintaanActivity,daoSession)

        viewModel.getMonitoringPermintaan(daoSession)

        adapter = MonitoringPermintaanAdapter(
            arrayListOf(),
            object : MonitoringPermintaanAdapter.OnAdapterListener {
                override fun onClick(mp: dev.iconpln.mims.data.local.database.TTransMonitoringPermintaan) {

                    startActivity(
                        Intent(
                            this@MonitoringPermintaanActivity,
                            MonitoringPermintaanDetailActivity::class.java
                        )
                            .putExtra("noPermintaan", mp.noPermintaan)
                            .putExtra("noTransaksi", mp.noTransaksi)
                    )
//                    }
//                }

                }

            },object : MonitoringPermintaanAdapter.OnAdapterListenerGetData {
                override fun onClick(mp: TTransMonitoringPermintaan) {
                    getDataWithPaginatianUtil.getMonitoringPermintaanDetail(mp)
                }

            },
            daoSession,this@MonitoringPermintaanActivity
        )

        with(binding) {
            btnBack.setOnClickListener { onBackPressed() }
            rvMonitoringPermintaan.adapter = adapter
            rvMonitoringPermintaan.setHasFixedSize(true)
            rvMonitoringPermintaan.layoutManager = LinearLayoutManager(
                this@MonitoringPermintaanActivity,
                LinearLayoutManager.VERTICAL,
                false
            )

            srcNoPermintaan.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    srcNoPermintaanText = s.toString()
                    viewModel.search(
                        daoSession,
                        srcNoPermintaanText,
                        srcStatusPengeluaranText,
                        srcGudangTujuanText,
                        srcTglPermintaanText
                    )
                }

            })

            setStatusPengeluaran()
            setGudangTujuan()
            setDatePicker()
        }

        viewModel.monitoringPermintaanResponse.observe(this) {
            adapter.setMpList(it)
            binding.totalData.text = "Total : ${it.size} Data"
        }


    }

    private fun setDataPermintaan() {
        val isDataExist = daoSession.tTransMonitoringPermintaanDao.queryBuilder().list().size > 0

        val listPermintaan = daoSession.tMonitoringPermintaanDao.queryBuilder().list()

        if (!isDataExist) {
            if (listPermintaan != null) {
                val size = listPermintaan.size
                if (size > 0) {
                    val items =
                        arrayOfNulls<dev.iconpln.mims.data.local.database.TTransMonitoringPermintaan>(
                            size
                        )
                    var item: dev.iconpln.mims.data.local.database.TTransMonitoringPermintaan
                    for ((i, model) in listPermintaan.withIndex()) {
                        item =
                            dev.iconpln.mims.data.local.database.TTransMonitoringPermintaan()
                        item.createdDate = model?.createdDate
                        item.plant = model?.plant
                        item.plantName = model?.plantName
                        item.createdBy = model?.createdBy
                        item.jumlahKardus =
                            if (model?.jumlahKardus == null) 0 else model?.jumlahKardus as Int?
                        item.kodePengeluaran = model?.kodePengeluaran.toString()
                        item.noPermintaan = model?.noPermintaan
                        item.noTransaksi = model?.noTransaksi
                        item.noRepackaging = model?.noRepackaging
                        item.storLocAsal = model?.storLocAsal
                        item.storLocAsalName = model?.storLocAsalName
                        item.storLocTujuan = model?.storLocTujuan
                        item.storLocTujuanName = model?.storLocTujuanName
                        item.tanggalPengeluaran = model?.tanggalPengeluaran.toString()
                        item.tanggalPermintaan = model?.tanggalPermintaan
                        item.updatedBy = model?.updatedBy
                        item.updatedDate = model?.updatedDate
                        item.isDone = 0
                        item.jumlahKardus = model?.jumlahKardus
                        item.valuationType = model?.valuationType
                        item.totalQtyPermintaan = model?.totalQtyPermintaan
                        item.totalScanQty = model?.totalScanQty
                        item.noPengiriman = model?.noPengiriman

                        items[i] = item
                    }
                    daoSession.tTransMonitoringPermintaanDao.insertInTx(items.toList())
                }
            }
        }
    }

//    private fun setDetailPermintaan() {
//        val isDataExist =
//            daoSession.tTransMonitoringPermintaanDetailDao.queryBuilder().list().size > 0
//
//        val listDetailPermintaan = daoSession.tMonitoringPermintaanDetailDao.queryBuilder().list()
//
//        if (!isDataExist) {
//            val size = listDetailPermintaan.size
//            if (size > 0) {
//                val items =
//                    arrayOfNulls<dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetail>(
//                        size
//                    )
//                var item: dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetail
//                for ((i, model) in listDetailPermintaan.withIndex()) {
//                    item =
//                        dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetail()
//                    item.unit = model?.unit
//                    item.nomorMaterial = model?.nomorMaterial
//                    item.kategori = model?.kategori
//                    item.materialDesc = model?.materialDesc
//                    item.noPermintaan = model?.noPermintaan
//                    item.noTransaksi = model?.noTransaksi
//                    item.noRepackaging = model?.noRepackaging
//                    item.qtyPengeluaran = model?.qtyPengeluaran
//                    item.qtyPermintaan = model?.qtyPermintaan ?: 0.0
//                    item.qtyScan = model?.qtyScan
//                    item.isActive = model?.isActive
//                    item.qtyAkanDiScan = 0
//                    item.isScannedSn = 0
//                    item.isDone = 0
//                    item.valuationType = model?.valuationType
//                    item.jumlahKardus = model?.jumlahKardus
//
//                    items[i] = item
//                }
//                daoSession.tTransMonitoringPermintaanDetailDao.insertInTx(items.toList())
//            }
//        }
//    }

    private fun setGudangTujuan() {
        val list = daoSession.tTransMonitoringPermintaanDao.queryBuilder().list()
        val listKategori: ArrayList<String> = ArrayList()
        for (i in list) {
            listKategori.add(i.storLocTujuanName)
        }

        val adapterKategori =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listKategori.distinct())
        binding.dropdownGudangTujuan.setAdapter(adapterKategori)
        binding.dropdownGudangTujuan.setOnItemClickListener { parent, view, position, id ->
            srcGudangTujuanText = binding.dropdownGudangTujuan.text.toString()
            viewModel.search(
                daoSession,
                srcNoPermintaanText,
                srcStatusPengeluaranText,
                srcGudangTujuanText,
                srcTglPermintaanText
            )
        }
    }

    private fun setStatusPengeluaran() {
        val list = daoSession.tMonitoringPermintaanDao.queryBuilder().list()
        val listKategori: ArrayList<String> = ArrayList()
        for (i in list) {
            when (i.kodePengeluaran) {
                "1" -> listKategori.add("Permintaan")
                "2" -> listKategori.add("Pengeluaran")
                "3" -> listKategori.add("All")
            }
        }

        val adapterKategori =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listKategori.distinct())
        binding.dropdownStatusPengeluaran.setAdapter(adapterKategori)
        binding.dropdownStatusPengeluaran.setOnItemClickListener { parent, view, position, id ->
            when (binding.dropdownStatusPengeluaran.text.toString()) {
                "Permintaan" -> srcStatusPengeluaranText = "1"
                "Pengeluaran" -> srcStatusPengeluaranText = "2"
            }
            viewModel.search(
                daoSession,
                srcNoPermintaanText,
                srcStatusPengeluaranText,
                srcGudangTujuanText,
                srcTglPermintaanText
            )
        }
    }

    private fun setDatePicker() {
        val dateSetListenerPermintaan =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "yyyy-MM-dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.txtTglMulai.text = sdf.format(cal.time)
                srcTglPermintaanText = sdf.format(cal.time)
                viewModel.search(
                    daoSession,
                    srcNoPermintaanText,
                    srcStatusPengeluaranText,
                    srcGudangTujuanText,
                    srcTglPermintaanText
                )

            }

        binding.cvTglPermintaan.setOnClickListener {
            DatePickerDialog(
                this@MonitoringPermintaanActivity, dateSetListenerPermintaan,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    override fun onStart() {
        viewModel.monitoringPermintaanResponse.observe(this) {
            adapter.setMpList(it)
        }
        super.onStart()
    }
}