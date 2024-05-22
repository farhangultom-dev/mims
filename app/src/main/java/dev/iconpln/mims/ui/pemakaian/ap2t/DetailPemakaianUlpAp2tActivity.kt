package dev.iconpln.mims.ui.pemakaian.ap2t

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivityDetailPemakaianUlpAp2tBinding
import dev.iconpln.mims.utils.ViewModelFactory

class DetailPemakaianUlpAp2tActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPemakaianUlpAp2tBinding
    private lateinit var viewModel: DetailPemakaianUlpAp2tViewModel
    private lateinit var rvAdapter: PemakaianUlpAp2tAdapter
    var plant = ""
    var storlok = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPemakaianUlpAp2tBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiService = ApiConfig.getApiService(this)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[DetailPemakaianUlpAp2tViewModel::class.java]

//
//        var idPelanggan = intent.getStringExtra(EXTRA_ID_PELANGGAN).toString()
//        binding.txtIdPelanggan.text = idPelanggan

        var noReservasi = intent.getStringExtra(EXTRA_NO_RESERVASI).toString()
        binding.txtNoReservasiAp2t.text = noReservasi

        var noTransaksi = intent.getStringExtra(EXTRA_NO_TRANSAKSI).toString()
        viewModel.getPelangganAp2t(noTransaksi)

        var noMaterial = intent.getStringArrayListExtra(EXTRA_NO_MATERIAL)

        var idPelanggan = intent.getStringExtra(EXTRA_ID_PELANGGAN).toString()

        plant = intent.getStringExtra(EXTRA_PLANT).toString()
//        viewModel.getPelangganAp2t(plant)
        storlok = intent.getStringExtra(EXTRA_STORLOK).toString()
//        viewModel.getPelangganAp2t(storlok)
//        Log.d("DetailPemakaianUlpAp2t", "cek plant : $plant & $storlok")

        viewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.getDetailPelanggan.observe(this) {
            Log.d("DetailPemakaianUlpAp2t", "cek response : $it")
            if (it != null) {
                rvAdapter.setListPemakaianAp2t(it.data)
                if (noMaterial != null) {
                    rvAdapter.getNoMaterial(noMaterial)
                }
            }
            binding.txtTotalData.text = it.data?.size.toString()
        }

        binding.srcIdPelanggan.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.getPelangganAp2t(noTransaksi, p0.toString())
            }
        })

        setRecyclerView()
        binding.btnBack.setOnClickListener { onBackPressed() }
    }

    private fun setRecyclerView() {
        rvAdapter = PemakaianUlpAp2tAdapter(plant, storlok)
        binding.rvPemakaianUlp.apply {
            layoutManager = LinearLayoutManager(this@DetailPemakaianUlpAp2tActivity)
            setHasFixedSize(true)
            adapter = rvAdapter
        }
    }

    companion object {
        const val EXTRA_NO_TRANSAKSI = "extra_no_transaksi"
        const val EXTRA_NO_RESERVASI = "extra_no_reservasi"
        const val EXTRA_NO_MATERIAL = "extra_no_material"
        const val EXTRA_ID_PELANGGAN = "extra_id_pelanggan"

        //        const val EXTRA_ID_PELANGGAN = "extra_id_pelanggan"
        const val EXTRA_PLANT = "extra_plant"
        const val EXTRA_STORLOK = "extra_storlok"
    }
}