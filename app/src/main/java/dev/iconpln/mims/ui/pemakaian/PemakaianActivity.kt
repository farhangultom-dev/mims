package dev.iconpln.mims.ui.pemakaian

import android.R
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.databinding.ActivityPemakaianBinding
import dev.iconpln.mims.ui.pemakaian.ap2t.DetailMaterialPemakaianUlpAp2t
import dev.iconpln.mims.ui.pemakaian.maximo.DetailPemakaianUlpMaximoActivity
import dev.iconpln.mims.ui.pemakaian.yantek.DetailPemakaianUlpYantekActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PemakaianActivity : BaseActivity() {
    private lateinit var binding: ActivityPemakaianBinding
    private lateinit var adapter: PemakaianAdapter
    private lateinit var pemakaians: List<dev.iconpln.mims.data.local.database.TPemakaian>
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private var noReservasi = ""
    private var tglReservasi = ""
    private var sumberReservasi = ""
    private lateinit var cal: Calendar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPemakaianBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!
        cal = Calendar.getInstance()

        pemakaians = daoSession.tPemakaianDao.loadAll()
        setSumberReservasi()

        adapter = PemakaianAdapter(arrayListOf(), object : PemakaianAdapter.OnAdapterListener {
            override fun onClick(pemakaian: dev.iconpln.mims.data.local.database.TPemakaian) {
                if (pemakaian.isDone == 1) {
                    Toast.makeText(
                        this@PemakaianActivity,
                        "Anda sudah menyelesaikan pemakaian ini",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (pemakaian.isDone == 1 || pemakaian.sumber == "AP2T") {
                    Log.d("PemakaianActivity", "cek di sumber : ${pemakaian.noReservasi}")
                    startActivity(
                        Intent(this@PemakaianActivity, DetailMaterialPemakaianUlpAp2t::class.java)
                            .putExtra(
                                DetailMaterialPemakaianUlpAp2t.EXTRA_NO_TRANSAKSI,
                                pemakaian.noTransaksi
                            )
                            .putExtra(
                                DetailMaterialPemakaianUlpAp2t.EXTRA_NO_RESERVASI,
                                pemakaian.noReservasi
                            )
                            .putExtra(
                                DetailMaterialPemakaianUlpAp2t.EXTRA_PEMERIKSA,
                                pemakaian.pemeriksa
                            )
                            .putExtra(
                                DetailMaterialPemakaianUlpAp2t.EXTRA_PENERIMA,
                                pemakaian.penerima
                            )
                            .putExtra(
                                DetailMaterialPemakaianUlpAp2t.EXTRA_PLANT,
                                pemakaian.plant
                            )
                            .putExtra(
                                DetailMaterialPemakaianUlpAp2t.EXTRA_storlok,
                                pemakaian.storLoc
                            )
//                            .putExtra(
//                                DetailMaterialPemakaianUlpAp2t.EXTRA_NO_PEMAKAIAN,
//                                pemakaian.
//                            )
                    )
                } else if (pemakaian.isDone == 1 || pemakaian.sumber == "YANTEK") {
                    startActivity(
                        Intent(this@PemakaianActivity, DetailPemakaianUlpYantekActivity::class.java)
                            .putExtra(
                                DetailPemakaianUlpYantekActivity.EXTRA_NO_TRANSAKSI,
                                pemakaian.noTransaksi
                            )
                            .putExtra(
                                DetailPemakaianUlpYantekActivity.EXTRA_NO_RESERVASI,
                                pemakaian.noReservasi
                            )
                            .putExtra(
                                DetailPemakaianUlpYantekActivity.EXTRA_PEMERIKSA,
                                pemakaian.pemeriksa
                            )
                            .putExtra(
                                DetailPemakaianUlpYantekActivity.EXTRA_PENERIMA,
                                pemakaian.penerima
                            )
                            .putExtra(DetailPemakaianUlpYantekActivity.EXTRA_NO_PK, pemakaian.noPk)
                            .putExtra(
                                DetailPemakaianUlpYantekActivity.EXTRA_NAMA_PELANGGAN,
                                pemakaian.namaPelanggan
                            )
                            .putExtra(
                                DetailPemakaianUlpYantekActivity.EXTRA_NAMA_KEGIATAN,
                                pemakaian.namaKegiatan
                            )
                            .putExtra(
                                DetailPemakaianUlpYantekActivity.EXTRA_NO_BON,
                                pemakaian.noBon
                            )
                    )
                    Log.d("PemakaianActivity", "cek di sumber : ${pemakaian.namaPelanggan}")
                } else if (pemakaian.isDone == 1 || pemakaian.sumber == "MAXIMO") {
                    startActivity(
                        Intent(this@PemakaianActivity, DetailPemakaianUlpMaximoActivity::class.java)
                            .putExtra(
                                DetailPemakaianUlpMaximoActivity.EXTRA_NO_TRANSAKSI,
                                pemakaian.noTransaksi
                            )
                            .putExtra(
                                DetailPemakaianUlpMaximoActivity.EXTRA_NO_RESERVASI,
                                pemakaian.noReservasi
                            )
                            .putExtra(
                                DetailPemakaianUlpMaximoActivity.EXTRA_PEMERIKSA,
                                pemakaian.pemeriksa
                            )
                            .putExtra(
                                DetailPemakaianUlpMaximoActivity.EXTRA_PENERIMA,
                                pemakaian.penerima
                            )
                            .putExtra(DetailPemakaianUlpMaximoActivity.EXTRA_NO_PK, pemakaian.noPk)
                            .putExtra(
                                DetailPemakaianUlpMaximoActivity.EXTRA_NAMA_PELANGGAN,
                                pemakaian.namaPelanggan
                            )
                            .putExtra(
                                DetailPemakaianUlpMaximoActivity.EXTRA_NAMA_KEGIATAN,
                                pemakaian.penerima
                            )
                            .putExtra(
                                DetailPemakaianUlpMaximoActivity.EXTRA_LOKASI,
                                pemakaian.lokasi
                            )
                    )
                    Log.d("PemakaianActivity", "cek penerima dari sumber : ${pemakaian.penerima}")
                } else {
                    insertDataDetail(pemakaian.noTransaksi)
                    startActivity(
                        Intent(
                            this@PemakaianActivity,
                            DetailPemakaianUlpMandiriActivity::class.java
                        )
                            .putExtra("noTransaksi", pemakaian.noTransaksi)
                    )
                }
            }

        })

        with(binding) {
            btnBack.setOnClickListener { onBackPressed() }
            rvPemakaianUlp.adapter = adapter
            rvPemakaianUlp.layoutManager =
                LinearLayoutManager(this@PemakaianActivity, LinearLayoutManager.VERTICAL, false)
            rvPemakaianUlp.setHasFixedSize(true)

            srcNomorReservasi.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    noReservasi = s.toString()
                    doSearch()
                }

            })

            val dateSetListenerStart =
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    var myFormat = "yyyy-MM-dd" // mention the format you need
                    var sdf = SimpleDateFormat(myFormat, Locale.US)
                    binding.edtTanggalDiterima.setText(sdf.format(cal.time))
                    tglReservasi = sdf.format(cal.time)
                    doSearch()
                }

            edtTanggalDiterima.setOnClickListener {
                DatePickerDialog(
                    this@PemakaianActivity, dateSetListenerStart,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }

        adapter.setpemakaianList(pemakaians)
    }

    private fun setSumberReservasi() {
        val reservasiNames = pemakaians.map { it.sumber }

        if (!reservasiNames.isNullOrEmpty()) {
            val adapterReservasi = ArrayAdapter(
                this@PemakaianActivity,
                R.layout.simple_dropdown_item_1line,
                reservasiNames.distinct()
            )
            binding.dropdownSumberReservasi.setAdapter(adapterReservasi)
            binding.dropdownSumberReservasi.setOnItemClickListener { parent, view, position, id ->
                sumberReservasi = binding.dropdownSumberReservasi.text.toString()
                doSearch()
            }
        }
    }

    private fun doSearch() {
        val searchList = daoSession.tPemakaianDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPemakaianDao.Properties.TanggalReservasi.like(
                    "%" + tglReservasi + "%"
                )
            )
            .where(dev.iconpln.mims.data.local.database.TPemakaianDao.Properties.NoReservasi.like("%" + noReservasi + "%"))
            .where(dev.iconpln.mims.data.local.database.TPemakaianDao.Properties.Sumber.like("%" + sumberReservasi + "%"))
            .list()

        binding.rvPemakaianUlp.adapter = null
        binding.rvPemakaianUlp.layoutManager = null
        binding.rvPemakaianUlp.adapter = adapter
        binding.rvPemakaianUlp.layoutManager =
            LinearLayoutManager(this@PemakaianActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvPemakaianUlp.setHasFixedSize(true)
        adapter.setpemakaianList(searchList)
    }

    private fun insertDataDetail(noTransaksi: String) {
        val checkDataExist = daoSession.tTransPemakaianDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TTransPemakaianDetailDao.Properties.NoTransaksi.eq(
                    noTransaksi
                )
            ).list().size > 0

        val dataDetail = daoSession.tPemakaianDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPemakaianDetailDao.Properties.NoTransaksi.eq(
                    noTransaksi
                )
            ).list()

        if (!checkDataExist) {
            for (i in dataDetail) {
                val item =
                    dev.iconpln.mims.data.local.database.TTransPemakaianDetail()
                item.valuationType = i.valuationType
                item.unit = i.unit
                item.nomorMaterial = i.nomorMaterial
                item.keterangan = i.keterangan
                item.namaMaterial = i.namaMaterial
                item.noMeter = i.noMeter
                item.noTransaksi = i.noTransaksi
                item.qtyPemakaian = i.qtyPemakaian
                item.qtyPengeluaran = i.qtyPengeluaran
                item.qtyReservasi = i.qtyReservasi
                item.isActive = i.isActive
                item.snScanned = ""
                item.isDone = if (!i.isActive) 1 else 0
                daoSession.tTransPemakaianDetailDao.insert(item)
            }
        }
    }
}