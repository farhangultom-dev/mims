package dev.iconpln.mims.ui.ulp.penerimaan.input_penerimaan

import android.R
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.data.local.database.TTransPenerimaanUlpDao
import dev.iconpln.mims.databinding.ActivityPetugasPenerimaanUlpBinding
import dev.iconpln.mims.ui.ulp.penerimaan.input_pemeriksaan.DetailPemeriksaanActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.SharedPrefsUtils
import org.joda.time.LocalDateTime
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PetugasPenerimaanULPActivity : BaseActivity() {
    private lateinit var binding: ActivityPetugasPenerimaanUlpBinding
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private lateinit var penerimaans: dev.iconpln.mims.data.local.database.TTransPenerimaanUlp
    private var noRepackaging: String = ""
    private var noPengiriman: String = ""
    private var plant: String = ""
    private var kepalaGudangName: String = ""
    private var penerimaName: String = ""
    private var userName: String = ""
    private lateinit var cal: Calendar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPetugasPenerimaanUlpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!
        cal = Calendar.getInstance()

        setDatePicker()

        noRepackaging = intent.getStringExtra("noRepackaging")!!
        noPengiriman = intent.getStringExtra("noPengiriman")!!
        plant = SharedPrefsUtils.getStringPreference(
            this@PetugasPenerimaanULPActivity,
            Config.KEY_PLANT, ""
        )!!
        userName = SharedPrefsUtils.getStringPreference(
            this@PetugasPenerimaanULPActivity,
            Config.KEY_FULL_NAME,
            ""
        )!!

        penerimaans = daoSession.tTransPenerimaanUlpDao.queryBuilder()
            .where(TTransPenerimaanUlpDao.Properties.NoPengiriman.eq(noPengiriman)).list()[0]

        binding.btnLanjutpenerimaanulp.setOnClickListener {
            validation()
        }

        with(binding) {
            btnBack.setOnClickListener { onBackPressed() }
            txtNoPengiriman.text = penerimaans.noPengiriman
            txtGudangTujuan2Penerimaanulp.text = penerimaans.gudangTujuan
            txtGudangTujuanPenerimaanulp.text = penerimaans.gudangAsal
            txtNoRepackagingpenerimaanulp.text = penerimaans.noRepackaging

            edtTanggalDokumen.setText(
                if (penerimaans.tanggalDokumen.isNullOrEmpty()) "" else penerimaans.tanggalDokumen.take(
                    10
                )
            )
            edtKurir.setText(penerimaans.kurir)
            edtNota.setText(penerimaans.noNota)
            edtNoPk.setText(penerimaans.noPk)

            if (!penerimaans.kepalaGudangPenerima.isNullOrEmpty()) {
                dropdownKepalaGudang.setText(penerimaans.kepalaGudangPenerima)
                kepalaGudangName = penerimaans.kepalaGudangPenerima
            }

            if (!penerimaans.pejabatPenerima.isNullOrEmpty()) {
                dropdownNamaPenerima.setText(penerimaans.pejabatPenerima)
                penerimaName = penerimaans.pejabatPenerima
            } else {
                dropdownNamaPenerima.setText("Pilih Pejabat Penerima")
                penerimaName = ""
            }
        }

        setKepalaGudang()
        setPenerima()
    }

    private fun validation() {
        with(binding) {
            val kepalaGudang = kepalaGudangName
            val noPk = penerimaans.noPk
            val tglDoc = edtTanggalDokumen.text.toString()
            val pejabatPenerima = penerimaName
            val kurir = edtKurir.text.toString()
            val noNota = edtNota.text.toString()

//            if (kepalaGudang.isNullOrEmpty()){
//                Toast.makeText(this@PetugasPenerimaanULPActivity, "Lengkapi semua data yang dibutuhkan", Toast.LENGTH_SHORT).show()
//                return
//            }
//
//            if (noPk.isNullOrEmpty()){
//                Toast.makeText(this@PetugasPenerimaanULPActivity, "Lengkapi semua data yang dibutuhkan", Toast.LENGTH_SHORT).show()
//                return
//            }

            if (tglDoc.isNullOrEmpty()) {
                Toast.makeText(
                    this@PetugasPenerimaanULPActivity,
                    "Lengkapi semua data yang dibutuhkan",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (pejabatPenerima.isNullOrEmpty()) {
                Toast.makeText(
                    this@PetugasPenerimaanULPActivity,
                    "Lengkapi semua data yang dibutuhkan",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (kurir.isNullOrEmpty()) {
                Toast.makeText(
                    this@PetugasPenerimaanULPActivity,
                    "Lengkapi semua data yang dibutuhkan",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (noNota.isNullOrEmpty()) {
                Toast.makeText(
                    this@PetugasPenerimaanULPActivity,
                    "Lengkapi semua data yang dibutuhkan",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            submitForm(kepalaGudang, noPk, tglDoc, pejabatPenerima, kurir, noNota)
        }
    }

    private fun submitForm(
        kepalaGudang: String,
        noPk: String,
        tglDoc: String,
        pejabatPenerima: String,
        kurir: String,
        noNota: String
    ) {
        penerimaans.kepalaGudangPenerima = kepalaGudang
        penerimaans.noPk = noPk
        penerimaans.pejabatPenerima = pejabatPenerima
        penerimaans.tanggalDokumen = tglDoc
        penerimaans.kurir = kurir
        penerimaans.noNota = noNota

        daoSession.tTransPenerimaanUlpDao.update(penerimaans)

        startActivity(
            Intent(this@PetugasPenerimaanULPActivity, DetailPemeriksaanActivity::class.java)
                .putExtra("noRepackaging", noRepackaging)
                .putExtra("noPengiriman", noPengiriman)
        )
    }

    private fun setDatePicker() {
        val dateSetListenerPermintaan =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "yyyy-MM-dd" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.edtTanggalDokumen.setText(sdf.format(cal.time))
            }

        binding.edtTanggalDokumen.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this@PetugasPenerimaanULPActivity, dateSetListenerPermintaan,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )

            val dateFormat = SimpleDateFormat(Config.DATE, Locale.getDefault())
            val date = dateFormat.parse(LocalDateTime.now().toString("yyyy-MM-dd"))
            val timeInMillis = date?.time ?: 0

            datePickerDialog.datePicker.maxDate = timeInMillis

            datePickerDialog.show()

        }
    }

    private fun setKepalaGudang() {
        val kepalaGudangs = arrayListOf(userName)
//            daoSession.tPegawaiUp3Dao
//            .queryBuilder()
//            .where(dev.iconpln.mims.data.local.database.TPegawaiUp3Dao.Properties.KodeJabatan.eq("15"))
//            .where(dev.iconpln.mims.data.local.database.TPegawaiUp3Dao.Properties.Plant.eq(plant))
//            .where(dev.iconpln.mims.data.local.database.TPegawaiUp3Dao.Properties.IsActive.eq(true)).list()

        val names = kepalaGudangs.map { it }

        val adapterStatus = ArrayAdapter(
            this@PetugasPenerimaanULPActivity,
            R.layout.simple_dropdown_item_1line,
            names
        )
        binding.dropdownKepalaGudang.setAdapter(adapterStatus)
        binding.dropdownKepalaGudang.setOnItemClickListener { parent, view, position, id ->
            kepalaGudangName = kepalaGudangs[position]
        }
    }

    private fun setPenerima() {
        val penerimas = daoSession.tPejabatDao.loadAll()

        val names = penerimas.map { it.nama }

        val adapterStatus = ArrayAdapter(
            this@PetugasPenerimaanULPActivity,
            R.layout.simple_dropdown_item_1line,
            names
        )
        binding.dropdownNamaPenerima.setAdapter(adapterStatus)
        binding.dropdownNamaPenerima.setOnItemClickListener { parent, view, position, id ->
            penerimaName = penerimas[position].nama
        }
    }
}