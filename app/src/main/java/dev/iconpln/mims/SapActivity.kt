package dev.iconpln.mims

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivitySapBinding
import dev.iconpln.mims.ui.pemakaian.ap2t.ResultSapViewModel
import dev.iconpln.mims.utils.ViewModelFactory

class SapActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySapBinding
    private lateinit var viewModel: ResultSapViewModel
    private var noPemakaian = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiService = ApiConfig.getApiService(this)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[ResultSapViewModel::class.java]

        noPemakaian = intent.getStringExtra(EXTRA_NO_PEMAKAIAN).toString()

        viewModel.getDataResultSap(noPemakaian)
        viewModel.getResultSap.observe(this) {
            it.data.forEach { data ->
                binding.lblNomor.text = data.nomor
                binding.lblMaterialDocSap.text = data.nomorSAP
                binding.lblMessageSap.text = data.message
                binding.lblCreateOnSap.text = data.createON
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    companion object {
        const val EXTRA_NO_PEMAKAIAN = "extra_no_pemakaian"
    }
}