package dev.iconpln.mims.ui.pemakaian.yantek

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivityDetailPemakaianUlpYantekBinding
import dev.iconpln.mims.utils.ViewModelFactory

class DetailPemakaianUlpYantekActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPemakaianUlpYantekBinding
    private lateinit var viewModel: DetailYantekViewModel
    private lateinit var rvAdapter: DetailYantekAdapter
    private var pemeriksa = ""
    private var penerima = ""
    private var noBon = ""
    private var namaPelanggan = ""
    private var namaKegiatan = ""
    private var noPk = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPemakaianUlpYantekBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiService = ApiConfig.getApiService(this)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[DetailYantekViewModel::class.java]

        var noReservasi = intent.getStringExtra(EXTRA_NO_RESERVASI).toString()
        binding.txtNoReservasi.text = noReservasi

        var noTransaksi = intent.getStringExtra(EXTRA_NO_TRANSAKSI).toString()
        viewModel.getDataDetailYantek(noTransaksi)

        pemeriksa = intent.getStringExtra(InputPemakaianYantekActivity.EXTRA_PEMERIKSA).toString()
        penerima = intent.getStringExtra(InputPemakaianYantekActivity.EXTRA_PENERIMA).toString()
        noBon = intent.getStringExtra(InputPemakaianYantekActivity.EXTRA_NO_BON).toString()
        namaKegiatan =
            intent.getStringExtra(InputPemakaianYantekActivity.EXTRA_NAMA_KEGIATAN).toString()
        namaPelanggan =
            intent.getStringExtra(InputPemakaianYantekActivity.EXTRA_NAMA_PELANGGAN).toString()
        noPk = intent.getStringExtra(InputPemakaianYantekActivity.EXTRA_NO_PK).toString()
        noBon = intent.getStringExtra(InputPemakaianYantekActivity.EXTRA_NO_BON).toString()


        Log.d("PemakaianActivity", "cek di transit pertama : ${namaPelanggan}")

        viewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        binding.btnPemakaian.setOnClickListener {
            startActivity(
                Intent(
                    this@DetailPemakaianUlpYantekActivity,
                    InputPemakaianYantekActivity::class.java
                )
                    .putExtra(InputPemakaianYantekActivity.EXTRA_NO_TRANSAKSI, noTransaksi)
                    .putExtra(InputPemakaianYantekActivity.EXTRA_PEMERIKSA, pemeriksa)
                    .putExtra(InputPemakaianYantekActivity.EXTRA_PENERIMA, penerima)
                    .putExtra(InputPemakaianYantekActivity.EXTRA_NAMA_PELANGGAN, namaPelanggan)
                    .putExtra(InputPemakaianYantekActivity.EXTRA_NAMA_KEGIATAN, namaKegiatan)
                    .putExtra(InputPemakaianYantekActivity.EXTRA_NO_PK, noPk)
                    .putExtra(InputPemakaianYantekActivity.EXTRA_NO_BON, noBon)
            )
        }

        viewModel.getDetailYantek.observe(this) {
            if (it != null) {
                rvAdapter.setListDataYantek(it.data)
            }
            binding.txtTotalData.text = "Total data : ${it.data.size.toString()}"
        }

        binding

        setRecyclerViewYantek()
        binding.btnBack.setOnClickListener { onBackPressed() }
    }

    private fun setRecyclerViewYantek() {
        rvAdapter = DetailYantekAdapter()
        binding.rvPemakaianUlp.apply {
            layoutManager = LinearLayoutManager(this@DetailPemakaianUlpYantekActivity)
            setHasFixedSize(true)
            adapter = rvAdapter
        }
    }

    companion object {
        const val EXTRA_NO_TRANSAKSI = "extra_no_transaksi"
        const val EXTRA_NO_RESERVASI = "extra_no_reservasi"
        const val EXTRA_PEMERIKSA = "extra_pemeriksa"
        const val EXTRA_PENERIMA = "extra_penerima"
        const val EXTRA_NO_PK = "extra_no_pk"
        const val EXTRA_NO_BON = "extra_no_bon"
        const val EXTRA_NAMA_PELANGGAN = "extra_nama_pelanggan"
        const val EXTRA_NAMA_KEGIATAN = "extra_nama_kegiatan"
    }
}