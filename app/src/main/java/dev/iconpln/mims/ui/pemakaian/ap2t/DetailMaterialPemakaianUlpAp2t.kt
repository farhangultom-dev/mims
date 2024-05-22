package dev.iconpln.mims.ui.pemakaian.ap2t

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.SapActivity
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivityDetailMaterialPemakaianUlpAp2tBinding
import dev.iconpln.mims.utils.ViewModelFactory

class DetailMaterialPemakaianUlpAp2t : AppCompatActivity() {

    private lateinit var binding: ActivityDetailMaterialPemakaianUlpAp2tBinding
    private lateinit var viewModel: DetailMaterialPemakaianAp2tViewModel
    private lateinit var rvAdapter: MaterialPemakaianAp2tAdapter
    private val noMaterial = ArrayList<String>()
    private var pemeriksa = ""
    private var penerima = ""
    private var noReservasi = ""
    private var plant = ""
    private var storlok = ""
    private var noPemakaian = ""
    private var idPelanggan = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailMaterialPemakaianUlpAp2tBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiService = ApiConfig.getApiService(this)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[DetailMaterialPemakaianAp2tViewModel::class.java]

        noReservasi = intent.getStringExtra(EXTRA_NO_RESERVASI).toString()
        binding.txtNoReservasi.text = noReservasi

        var noTransaksi = intent.getStringExtra(EXTRA_NO_TRANSAKSI).toString()
        viewModel.getDataDetailMaterialAp2t(noTransaksi)

        plant = intent.getStringExtra(EXTRA_PLANT).toString()
        storlok = intent.getStringExtra(EXTRA_storlok).toString()

        pemeriksa = intent.getStringExtra(EXTRA_PEMERIKSA).toString()
        penerima = intent.getStringExtra(EXTRA_PENERIMA).toString()
        idPelanggan = intent.getStringExtra(EXTRA_ID_PELANGGAN).toString()

        viewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.getDataDetailAp2t.observe(this) {
            if (it != null) {
                rvAdapter.setListDataMaterialAp2t(it.data, noReservasi)
                it.data.forEach { data ->
                    noMaterial.add(data.nomorMaterial.toString())
                }
            }
            binding.txtTotalData.text = "Total data : ${it.data.size.toString()}"
        }

        setRecyclerViewAp2t()
        binding.btnBack.setOnClickListener { onBackPressed() }

        binding.btnPelanggan.setOnClickListener {
            startActivity(
                Intent(
                    this@DetailMaterialPemakaianUlpAp2t,
                    DetailPemakaianUlpAp2tActivity::class.java
                )
                    .putExtra(DetailPemakaianUlpAp2tActivity.EXTRA_NO_TRANSAKSI, noTransaksi)
                    .putExtra(DetailPemakaianUlpAp2tActivity.EXTRA_NO_RESERVASI, noReservasi)
                    .putExtra(DetailPemakaianUlpAp2tActivity.EXTRA_NO_MATERIAL, noMaterial)
                    .putExtra(DetailPemakaianUlpAp2tActivity.EXTRA_PLANT, plant)
                    .putExtra(DetailPemakaianUlpAp2tActivity.EXTRA_STORLOK, storlok)
                    .putExtra(DetailPemakaianUlpAp2tActivity.EXTRA_ID_PELANGGAN, idPelanggan)
            )
        }

        binding.btnPemakaian.setOnClickListener {
            startActivity(
                Intent(
                    this@DetailMaterialPemakaianUlpAp2t,
                    InputPetugasPemakaianAp2tActivity::class.java
                )
                    .putExtra(InputPetugasPemakaianAp2tActivity.EXTRA_NO_TRANSAKSI, noTransaksi)
                    .putExtra(InputPetugasPemakaianAp2tActivity.EXTRA_PEMERIKSA, pemeriksa)
                    .putExtra(InputPetugasPemakaianAp2tActivity.EXTRA_PENERIMA, penerima)
            )
        }

        binding.btnSap.setOnClickListener {
            startActivity(
                Intent(this@DetailMaterialPemakaianUlpAp2t, SapActivity::class.java)
                    .putExtra(SapActivity.EXTRA_NO_PEMAKAIAN, noPemakaian)
            )
        }

        binding.srcNomorMaterial.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.getDataDetailMaterialAp2t(noTransaksi, p0.toString())
            }
        })
    }

    private fun setRecyclerViewAp2t() {
        rvAdapter = MaterialPemakaianAp2tAdapter(noReservasi)
        binding.rvPemakaianUlp.apply {
            layoutManager = LinearLayoutManager(this@DetailMaterialPemakaianUlpAp2t)
            setHasFixedSize(true)
            adapter = rvAdapter
        }
    }

    companion object {
        const val EXTRA_NO_TRANSAKSI = "extra_no_transaksi"
        const val EXTRA_NO_RESERVASI = "extra_no_reservasi"
        const val EXTRA_PEMERIKSA = "extra_pemeriksa"
        const val EXTRA_PENERIMA = "extra_penerima"
        const val EXTRA_PLANT = "extra_plant"
        const val EXTRA_storlok = "extra_storlok"
        const val EXTRA_ID_PELANGGAN = "extra_id_pelanggan"
        const val EXTRA_NO_PEMAKAIAN = "extra_no_pemakaian"
    }
}