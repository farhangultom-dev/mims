package dev.iconpln.mims.ui.pemakaian.yantek

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivityInputPemakaianYantekBinding
import dev.iconpln.mims.ui.pemakaian.ap2t.DetailMaterialPemakaianAp2tViewModel
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.SharedPrefsUtils
import dev.iconpln.mims.utils.ViewModelFactory

class InputPemakaianYantekActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputPemakaianYantekBinding
    private lateinit var viewModel: DetailMaterialPemakaianAp2tViewModel
    private val listKepalaGudang = ArrayList<String>()
    private val listPejabatPengesahan = ArrayList<String>()
    private var kepalaGudang = ""
    private var pejabatPengesahan = ""
    private var pemeriksa = ""
    private var penerima = ""
    private var noBon = ""
    private var namaKegiatan = ""
    private var namaPelanggan = ""
    private var noPk = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputPemakaianYantekBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiService = ApiConfig.getApiService(this)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[DetailMaterialPemakaianAp2tViewModel::class.java]

        var noTransaksi = intent.getStringExtra(EXTRA_NO_TRANSAKSI).toString()
        pemeriksa = intent.getStringExtra(EXTRA_PEMERIKSA).toString()
        if (pemeriksa == "null") {
            pemeriksa = ""
        }
        penerima = intent.getStringExtra(EXTRA_PENERIMA).toString()
        if (penerima == "null") {
            penerima = ""
        }
        noBon = intent.getStringExtra(EXTRA_NO_BON).toString()
        if (noBon == "null") {
            noBon = ""
        }
        namaKegiatan = intent.getStringExtra(EXTRA_NAMA_KEGIATAN).toString()
        if (namaKegiatan == "null") {
            namaKegiatan = ""
        }
        namaPelanggan = intent.getStringExtra(EXTRA_NAMA_PELANGGAN).toString()
        if (namaPelanggan == "null") {
            namaPelanggan = ""
        }
        noPk = intent.getStringExtra(EXTRA_NO_PK).toString()
        if (noPk == "null") {
            noPk = ""
        }

        Log.d("PemakaianActivity", "cek di tujuan akhir : ${namaPelanggan}")

        binding.edtPemeriksa.setText(pemeriksa)
        binding.edtPenerima.setText(penerima)
        binding.edtNoBon.setText(noBon)
        binding.edtNoBon.isEnabled = false
        binding.edtNoPk.setText(noPk)
        binding.edtNoPk.isEnabled = false
        binding.edtNamaKegiatan.setText(namaKegiatan)
        binding.edtNamaKegiatan.isEnabled = false
        binding.edtNamaPelanggan.setText(namaPelanggan)
        binding.edtNamaPelanggan.isEnabled = false

        val storLoc = SharedPrefsUtils.getStringPreference(this, Config.KEY_STOR_LOC, "")
        val plant = SharedPrefsUtils.getStringPreference(this, Config.KEY_PLANT, "")

        viewModel.getPejabatKepala(plant.toString(), storLoc.toString(), "7")
        viewModel.getPejabatKepala.observe(this) {
            it.data.forEach { data ->
                listKepalaGudang.add(data.nama)
            }
        }

        viewModel.getPejabat(plant.toString(), storLoc.toString(), "8")
        viewModel.getPejabat.observe(this) {
            it.data.forEach { data ->
                listPejabatPengesahan.add(data.nama)
            }
        }

        val adapterKepalaGudang =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listKepalaGudang)
        binding.dropdownKepalaGudang.setAdapter(adapterKepalaGudang)
        binding.dropdownKepalaGudang.setOnItemClickListener { parent, view, position, id ->
            kepalaGudang = binding.dropdownKepalaGudang.text.toString()
        }

        val adapterPejabatPengesahan =
            ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, listPejabatPengesahan)
        binding.dropdownPejabatPengesahan.setAdapter(adapterPejabatPengesahan)
        binding.dropdownPejabatPengesahan.setOnItemClickListener { parent, view, position, id ->
            pejabatPengesahan = binding.dropdownPejabatPengesahan.text.toString()
        }

        binding.edtPemeriksa.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Update pemeriksa whenever the text changes
                pemeriksa = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.edtPenerima.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Update penerima whenever the text changes
                penerima = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnSimpan.setOnClickListener {
            viewModel.updateDetailPemakaian(
                noTransaksi,
                pemeriksa,
                penerima,
                kepalaGudang,
                pejabatPengesahan,
                "",
                namaKegiatan,
                namaPelanggan,
                noPk
            )
        }

        viewModel.updateDetailPemakaian.observe(this) {
            if (it.message == "Success") {
                Toast.makeText(this, "Berhasil tersimpan", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
        }
    }

    companion object {
        const val EXTRA_NO_TRANSAKSI = "extra_no_transaksi"
        const val EXTRA_NO_PK = "extra_no_pk"
        const val EXTRA_NAMA_PELANGGAN = "extra_nama_pelanggan"
        const val EXTRA_NAMA_KEGIATAN = "extra_nama_kegiatan"
        const val EXTRA_NO_BON = "extra_no_bon"
        const val EXTRA_PEMERIKSA = "extra_pemeriksa"
        const val EXTRA_PENERIMA = "extra_penerima"
    }
}