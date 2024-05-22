package dev.iconpln.mims.ui.penerimaan

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.data.local.database.TPosPenerimaan
import dev.iconpln.mims.data.local.database.TPosPenerimaanDao
import dev.iconpln.mims.databinding.ActivityPenerimaanBinding
import dev.iconpln.mims.ui.penerimaan.detail_penerimaan.DetailPenerimaanActivity
import dev.iconpln.mims.ui.penerimaan.detail_penerimaan_akhir.DetailPenerimaanAkhirActivity
import dev.iconpln.mims.ui.penerimaan.input_petugas.InputPetugasPenerimaanActivity
import dev.iconpln.mims.ui.rating.RatingActivity
import dev.iconpln.mims.utils.GetDataWithPaginatianUtil
import dev.iconpln.mims.utils.SharedPrefsUtils

class PenerimaanActivity : BaseActivity() {
    private lateinit var binding: ActivityPenerimaanBinding
    private val viewModel: PenerimaanViewModel by viewModels()
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    lateinit var adapter: PenerimaanAdapter
    private var filter: String = ""
    private var srcNoDo: String = ""
    private var role = 0
    private lateinit var getDataWithPaginationUtil: GetDataWithPaginatianUtil
    private lateinit var penerimaans: List<dev.iconpln.mims.data.local.database.TPosPenerimaan>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenerimaanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!
        role = SharedPrefsUtils.getIntegerPreference(this@PenerimaanActivity, "roleId", 0)
        getDataWithPaginationUtil = GetDataWithPaginatianUtil(this,daoSession)
//        insertDetailPenerimaan()
        penerimaans = daoSession.tPosPenerimaanDao.queryBuilder()
            .orderDesc(TPosPenerimaanDao.Properties.CreatedDate)
            .list()
        Log.d("PenerimaanActivity", "cek data penerimaan: $penerimaans")

//        viewModel.getPenerimaan(daoSession,penerimaans)

        adapter = PenerimaanAdapter(this@PenerimaanActivity,arrayListOf(), object : PenerimaanAdapter.OnAdapterListener {
            override fun onClick(po: dev.iconpln.mims.data.local.database.TPosPenerimaan) {
                if (role == 10) {
                    startActivity(
                        Intent(this@PenerimaanActivity, InputPetugasPenerimaanActivity::class.java)
                            .putExtra("noDo", po.noDoSmar)
                            .putExtra("noPo", po.poMpNo)
                    )
                } else {
                    if (po.bisaTerima == 0) {
                        Toast.makeText(
                            this@PenerimaanActivity,
                            "BABG belum di konfirmasi",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (po.petugasPenerima.isNullOrEmpty()) {
                            startActivity(
                                Intent(
                                    this@PenerimaanActivity,
                                    InputPetugasPenerimaanActivity::class.java
                                )
                                    .putExtra("noDo", po.noDoSmar)
                                    .putExtra("noPo", po.poMpNo)
                            )
                        } else {
                            startActivity(
                                Intent(
                                    this@PenerimaanActivity,
                                    InputPetugasPenerimaanActivity::class.java
                                )
                                    .putExtra("noDo", po.noDoSmar)
                                    .putExtra("noPo", po.poMpNo)
                            )
                        }
                    }
                }
            }

        }, object : PenerimaanAdapter.OnAdapterListenerDoc {
            override fun onClick(po: TPosPenerimaan) {
                if (role == 10) {
                    startActivity(
                        Intent(this@PenerimaanActivity, DetailPenerimaanActivity::class.java)
                            .putExtra("noDo", po.noDoSmar)
                    )
                } else {
                    if (po.bisaTerima == 0) {
                        Toast.makeText(
                            this@PenerimaanActivity,
                            "BABG belum di konfirmasi",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (po.tanggalDiterima.isNullOrEmpty()) {//|| po.statusPenerimaan == "DITERIMA"){
                            Toast.makeText(
                                this@PenerimaanActivity,
                                "Kamu belum melakukan input data penerimaan",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val penerimaanDetails =
                                daoSession.tPosDetailPenerimaanDao.queryBuilder()
                                    .where(
                                        dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.NoDoSmar.eq(
                                            po.noDoSmar
                                        )
                                    )
                                    .where(
                                        dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsDone.eq(
                                            0
                                        )
                                    ).list()

                            if (penerimaanDetails.isNullOrEmpty()) {
                                getDataWithPaginationUtil.getDataPosPenerimaanAkhir(po,srcNoDo)

                            } else {
                                startActivity(
                                    Intent(
                                        this@PenerimaanActivity,
                                        DetailPenerimaanActivity::class.java
                                    )
                                        .putExtra("noDo", po.noDoSmar)
                                        .putExtra("isDone", po.isDone)
                                        .putExtra("checkSn", penerimaanDetails.size)
                                )
                            }
                        }
                    }
                }
            }

        }, object : PenerimaanAdapter.OnAdapterListenerRate {
            override fun onClick(po: dev.iconpln.mims.data.local.database.TPosPenerimaan) {
                if (role == 10) {
                    startActivity(
                        Intent(this@PenerimaanActivity, RatingActivity::class.java)
                            .putExtra("noDo", po.noDoSmar)
                    )
                } else {
                    if (po.bisaTerima == 0) {
                        Toast.makeText(
                            this@PenerimaanActivity,
                            "BABG belum di konfirmasi",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
//                        if (po.isDone == 1){
//                            Toast.makeText(this@PenerimaanActivity, "Kamu sudah melakukan rating di DO ini", Toast.LENGTH_SHORT).show()
//                        }else{
                        if (po.isRating == 1) {
                            startActivity(
                                Intent(this@PenerimaanActivity, RatingActivity::class.java)
                                    .putExtra("noDo", po.noDoSmar)
                            )
                        } else {
                            Toast.makeText(
                                this@PenerimaanActivity,
                                "Kamu belum bisa melakukan rating",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
//                        }
                    }
                }
            }

        },object : PenerimaanAdapter.OnAdapterListenerGetData {
            override fun onClick(po: TPosPenerimaan) {
                getDataWithPaginationUtil.getDataPosSns(po,srcNoDo)
            }
        },daoSession)

//        viewModel.penerimaanResponse.observe(this){
//            adapter.setData(penerimaans)
//        }
        adapter.setData(penerimaans)

        with(binding) {
            tvTotalData.text = "Total: ${penerimaans.size} data"
            btnBack.setOnClickListener { onBackPressed() }
            binding.rvPenerimaan.adapter = adapter
            binding.rvPenerimaan.setHasFixedSize(true)
            binding.rvPenerimaan.layoutManager =
                LinearLayoutManager(this@PenerimaanActivity, LinearLayoutManager.VERTICAL, false)

            srcNomorPoDo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    srcNoDo = s.toString()
                    doSearch()
                }

            })

            val statusArray = arrayOf(
                "TERBARU", "TERLAMA"
            )
            val adapterStatus = ArrayAdapter(
                this@PenerimaanActivity,
                android.R.layout.simple_dropdown_item_1line,
                statusArray
            )
            dropdownUrutkan.setAdapter(adapterStatus)
            dropdownUrutkan.setOnItemClickListener { parent, view, position, id ->
                filter = statusArray[position]
                doSearch()

            }
        }
    }

//    private fun insertDetailPenerimaan() {
//        val penerimaanDetails = daoSession.tPosDetailPenerimaanDao.queryBuilder()
//            .where(
//                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsDone.eq(
//                    0
//                )
//            ).list()
//
//        if (penerimaanDetails.isNullOrEmpty()) {
//            val listPos = daoSession.tPosSnsDao.queryBuilder().list()
//            val size = listPos.size
//            if (size > 0) {
//                val items =
//                    arrayOfNulls<dev.iconpln.mims.data.local.database.TPosDetailPenerimaan>(size)
//                var item: dev.iconpln.mims.data.local.database.TPosDetailPenerimaan
//                for ((i, model) in listPos.withIndex()) {
//                    item =
//                        dev.iconpln.mims.data.local.database.TPosDetailPenerimaan()
//
//                    item.noDoSmar = model.noDoSmar
//                    item.qty = ""
//                    item.kdPabrikan = model.kdPabrikan
//                    item.doStatus = model.doStatus
//                    item.noPackaging = model.noPackaging
//                    item.serialNumber = model.noSerial
//                    item.noMaterial = model.noMatSap
//                    item.namaKategoriMaterial = model.namaKategoriMaterial
//                    item.storLoc = model.storLoc
//                    item.namaPabrikan = model.namaPabrikan
//                    if (model.statusPenerimaan.isNullOrEmpty()) item.statusPenerimaan =
//                        "" else item.statusPenerimaan = model.statusPenerimaan
//                    if (model.statusPemeriksaan.isNullOrEmpty()) item.statusPemeriksaan =
//                        "" else item.statusPemeriksaan = model.statusPemeriksaan
//                    item.doLineItem = model?.doLineItem
//                    item.isComplaint = 0
//                    if (model.statusPenerimaan == "DITERIMA") {
//                        item.isDone = 1
//                        item.isChecked = 1
//                    } else if (model.statusPenerimaan == "BELUM DIPERIKSA") {
//                        item.isDone = 1
//                        item.isChecked = 1
//                    } else {
//                        item.isDone = 0
//                        item.isChecked = 0
//                    }
//                    item.partialCode = ""
//                    items[i] = item
//                }
//                daoSession.tPosDetailPenerimaanDao.insertInTx(items.toList())
//            }
//        }
//    }

    private fun doSearch() {
        if (filter == "TERBARU") {
            val search = daoSession.tPosPenerimaanDao.queryBuilder()
                .whereOr(
                    dev.iconpln.mims.data.local.database.TPosPenerimaanDao.Properties.NoDoSmar.like(
                        "%" + srcNoDo + "%"
                    ),
                    dev.iconpln.mims.data.local.database.TPosPenerimaanDao.Properties.PoSapNo.like("%" + srcNoDo + "%")
                )
                .orderDesc(dev.iconpln.mims.data.local.database.TPosPenerimaanDao.Properties.CreatedDate)
                .list()
            binding.rvPenerimaan.adapter = null
            binding.rvPenerimaan.layoutManager = null
            binding.rvPenerimaan.adapter = adapter
            binding.rvPenerimaan.setHasFixedSize(true)
            binding.rvPenerimaan.layoutManager =
                LinearLayoutManager(this@PenerimaanActivity, LinearLayoutManager.VERTICAL, false)
            adapter.setData(search)
        } else {
            val search = daoSession.tPosPenerimaanDao.queryBuilder()
                .whereOr(
                    dev.iconpln.mims.data.local.database.TPosPenerimaanDao.Properties.NoDoSmar.like(
                        "%" + srcNoDo + "%"
                    ),
                    dev.iconpln.mims.data.local.database.TPosPenerimaanDao.Properties.PoSapNo.like("%" + srcNoDo + "%")
                )
                .orderAsc(dev.iconpln.mims.data.local.database.TPosPenerimaanDao.Properties.CreatedDate)
                .list()
            binding.rvPenerimaan.adapter = null
            binding.rvPenerimaan.layoutManager = null
            binding.rvPenerimaan.adapter = adapter
            binding.rvPenerimaan.setHasFixedSize(true)
            binding.rvPenerimaan.layoutManager =
                LinearLayoutManager(this@PenerimaanActivity, LinearLayoutManager.VERTICAL, false)
            adapter.setData(search)
        }
    }
}