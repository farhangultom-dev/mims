package dev.iconpln.mims

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivityDetailPemakaianDetailDataAp2tBinding
import dev.iconpln.mims.ui.pemakaian.ap2t.DataDetailMaterialAp2tAdapter
import dev.iconpln.mims.ui.pemakaian.maximo.InputSnPemakaianMaximoViewModel
import dev.iconpln.mims.utils.ViewModelFactory

class DetailPemakaianDetailDataAp2t : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPemakaianDetailDataAp2tBinding
    private lateinit var rvAdapter: DataDetailMaterialAp2tAdapter
    private lateinit var viewModel: InputSnPemakaianMaximoViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPemakaianDetailDataAp2tBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiService = ApiConfig.getApiService(this)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[InputSnPemakaianMaximoViewModel::class.java]


        var noReservasi = intent.getStringExtra(EXTRA_NO_RESERVASI).toString()
        binding.txtNoReservasi.text = noReservasi
        Log.d("DetailPemakaianDetailDataAp2t", "cek noReservasi : $noReservasi")

        var noMaterial = intent.getStringExtra(EXTRA_NO_MATERIAL).toString()
        binding.txtNoMaterial.text = noMaterial

        var noTransaksi = intent.getStringExtra(EXTRA_NO_TRANSAKSI).toString()


        Log.d("DetailPemakaianDetailDataAp2t", "cek noTransaksi : $noTransaksi")
        Log.d("DetailPemakaianDetailDataAp2t", "cek noMaterial : $noMaterial")
        viewModel.getDataSnMaterialMaximoDanAp2t(noTransaksi, noMaterial, "", valuationType = "")

        binding.srcNoMaterial.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.getDataSnMaterialMaximoDanAp2t(
                    noTransaksi,
                    noMaterial,
                    p0.toString(),
                    valuationType = ""
                )
            }
        })

        viewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.getDataInputSnMaterialMaximoDanAp2t.observe(this) {
            if (it != null) {
                Log.d("DetailPemakaianDetailDataAp2t", "cek response : $it")
                rvAdapter.setListDataDetailMaterialAp2t(it.data)
            }
        }

        setRecyclerView()
    }

    private fun setRecyclerView() {
        rvAdapter = DataDetailMaterialAp2tAdapter()
        binding.rvMonitoringPermintaanDetail.apply {
            layoutManager = LinearLayoutManager(this@DetailPemakaianDetailDataAp2t)
            setHasFixedSize(true)
            adapter = rvAdapter
        }
    }

    companion object {
        const val EXTRA_NO_RESERVASI = "extra_no_reservasi"
        const val EXTRA_NO_TRANSAKSI = "extra_no_transaksi"
        const val EXTRA_NO_MATERIAL = "extra_no_material"
    }
}