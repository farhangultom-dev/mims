package dev.iconpln.mims.ui.pemakaian.maximo

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivityDetailPemakaianUlpMaximoBinding
import dev.iconpln.mims.utils.ViewModelFactory

class DetailPemakaianUlpMaximoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPemakaianUlpMaximoBinding
    private lateinit var viewModel: DetailPemakaianMaximoViewModel
    private lateinit var rvAdapter: DetailPemakaianMaximoAdapter
    private var pemeriksa = ""
    private var penerima = ""
    private var lokasi = ""
    private var namaPelanggan = ""
    private var namaKegiatan = ""
    private var noPk = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPemakaianUlpMaximoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiService = ApiConfig.getApiService(this)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[DetailPemakaianMaximoViewModel::class.java]

        var noReservasi = intent.getStringExtra(EXTRA_NO_RESERVASI).toString()
        binding.txtNoReservasi.text = noReservasi

        var noTransaksi = intent.getStringExtra(EXTRA_NO_TRANSAKSI).toString()

        pemeriksa = intent.getStringExtra(EXTRA_PEMERIKSA).toString()
        penerima = intent.getStringExtra(EXTRA_PENERIMA).toString()
        lokasi = intent.getStringExtra(EXTRA_LOKASI).toString()
        namaPelanggan = intent.getStringExtra(EXTRA_NAMA_PELANGGAN).toString()
        namaKegiatan = intent.getStringExtra(EXTRA_NAMA_KEGIATAN).toString()
        noPk = intent.getStringExtra(EXTRA_NO_PK).toString()

        viewModel.getDataDetailMaterialMaximo(noTransaksi)

        binding.srcNomorMaterial.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.getDataDetailMaterialMaximo(noTransaksi, p0.toString())
            }
        })

        viewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        binding.btnPemakaian.setOnClickListener {
            startActivity(
                Intent(
                    this@DetailPemakaianUlpMaximoActivity,
                    InputPetugasPemakaianMaximoActivity::class.java
                )
                    .putExtra(InputPetugasPemakaianMaximoActivity.EXTRA_NO_TRANSAKSI, noTransaksi)
                    .putExtra(InputPetugasPemakaianMaximoActivity.EXTRA_PEMERIKSA, pemeriksa)
                    .putExtra(InputPetugasPemakaianMaximoActivity.EXTRA_PENERIMA, penerima)
                    .putExtra(
                        InputPetugasPemakaianMaximoActivity.EXTRA_NAMA_PELANGGAN,
                        namaPelanggan
                    )
                    .putExtra(InputPetugasPemakaianMaximoActivity.EXTRA_NAMA_KEGIATAN, namaKegiatan)
                    .putExtra(InputPetugasPemakaianMaximoActivity.EXTRA_LOKASI, lokasi)
                    .putExtra(InputPetugasPemakaianMaximoActivity.EXTRA_NO_PK, noPk)
            )
        }

        viewModel.getDataDetailMaximo.observe(this) {
            if (it != null) {
                rvAdapter.setListDetailMaximo(it.data)
            }
            binding.txtTotalData.text = "Total data : ${it.data.size.toString()}"
        }

        setRecyclerViewMaximo()
        binding.btnBack.setOnClickListener { onBackPressed() }
    }

    private fun setRecyclerViewMaximo() {
        rvAdapter = DetailPemakaianMaximoAdapter()
        binding.rvPemakaianUlp.apply {
            layoutManager = LinearLayoutManager(this@DetailPemakaianUlpMaximoActivity)
            setHasFixedSize(true)
            adapter = rvAdapter
        }
    }

    companion object {
        const val EXTRA_NO_TRANSAKSI = "extra_no_transaksi"
        const val EXTRA_NO_RESERVASI = "extra_no_reservasi"
        const val EXTRA_NO_PK = "extra_no_pk"
        const val EXTRA_NAMA_PELANGGAN = "extra_nama_pelanggan"
        const val EXTRA_NAMA_KEGIATAN = "extra_nama_kegiatan"
        const val EXTRA_LOKASI = "extra_lokasi"
        const val EXTRA_PEMERIKSA = "extra_pemeriksa"
        const val EXTRA_PENERIMA = "extra_penerima"
    }
}