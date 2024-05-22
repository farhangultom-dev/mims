package dev.iconpln.mims.ui.pemakaian.maximo

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dev.iconpln.mims.R
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.data.scan.CustomScanActivity
import dev.iconpln.mims.data.scan.CustomScanBarcodeActivity
import dev.iconpln.mims.databinding.ActivityInputSnPemakaianMaximoBinding
import dev.iconpln.mims.utils.ViewModelFactory

class InputSnPemakaianMaximoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInputSnPemakaianMaximoBinding
    private lateinit var viewModel: InputSnPemakaianMaximoViewModel
    private lateinit var rvAdapter: InputSnMaterialMaximoAdapter
    private var sn = ""
    private var noTransaksi = ""
    private var noMaterial = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputSnPemakaianMaximoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiService = ApiConfig.getApiService(this)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[InputSnPemakaianMaximoViewModel::class.java]

        var noReservasi = intent.getStringExtra(EXTRA_NO_RESERVASI).toString()
        binding.txtNoReservasi.text = noReservasi

        noMaterial = intent.getStringExtra(EXTRA_NO_MATERIAL).toString()
        binding.txtNoMaterial.text = noMaterial
        noTransaksi = intent.getStringExtra(EXTRA_NO_TRANSAKSI).toString()

        viewModel.getDataSnMaterialMaximoDanAp2t(noTransaksi, noMaterial, sn, valuationType = "")

        viewModel.getDataInputSnMaterialMaximoDanAp2t.observe(this) {
            if (it != null) {
                rvAdapter.setListGetSnPemakaianMaximo(it.data)
            }
        }

        viewModel.addSerialNumberUlpMaximo.observe(this) {
            if (it.message == "Success") {
                viewModel.getDataSnMaterialMaximoDanAp2t(
                    noTransaksi,
                    noMaterial,
                    "",
                    valuationType = ""
                )
            }
        }

        binding.btnScanSnMaterial.setOnClickListener {
            val dialog = BottomSheetDialog(
                this@InputSnPemakaianMaximoActivity,
                R.style.AppBottomSheetDialogTheme
            )
            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_menu, null)
            val btnScanBarcode = view.findViewById<CardView>(R.id.cv_button_satu)
            val btnScanQRCode = view.findViewById<CardView>(R.id.cv_button_dua)
            val ivScanBarcode = view.findViewById<ImageView>(R.id.imageView2)
            val ivScanQRcode = view.findViewById<ImageView>(R.id.imageView1)
            val txtScanBarcode = view.findViewById<TextView>(R.id.textView2)
            val txtScanQRcode = view.findViewById<TextView>(R.id.textView1)

            ivScanBarcode.setBackgroundResource(R.drawable.iv_btn_barcode)
            ivScanQRcode.setBackgroundResource(R.drawable.iv_btn_qr_code)
            txtScanBarcode.text = getString(R.string.scan_barcode)
            txtScanQRcode.text = getString(R.string.scan_qr_code)

            btnScanBarcode.setOnClickListener {
                openScanner("barcode")
                dialog.dismiss()
            }

            btnScanQRCode.setOnClickListener {
                openScanner("qrcode")
                dialog.dismiss()
            }

            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        }

//        viewModel.addSerialNumberUlpMaximo.observe(this){
//            Log.d("PemakaianMaximo", "Data cek : ${it}")
//        }

        viewModel.getDataInputSnMaterialMaximoDanAp2t.observe(this) {
            Log.d("PemakaianMaximo", "Data cek : ${it}")
            rvAdapter.setListGetSnPemakaianMaximo(it.data)
            if (it != null) {
                rvAdapter.setListGetSnPemakaianMaximo(it.data)
            }
        }

        setRecyclerInputSnMaximo()
        binding.btnBack.setOnClickListener { onBackPressed() }
    }

    private fun setRecyclerInputSnMaximo() {
        rvAdapter = InputSnMaterialMaximoAdapter()
        binding.rvListSn.apply {
            layoutManager = LinearLayoutManager(this@InputSnPemakaianMaximoActivity)
            setHasFixedSize(true)
            adapter = rvAdapter
        }
    }

    private fun openScanner(type: String) {
        val scan = ScanOptions()

        if (type == "barcode") {
            scan.setDesiredBarcodeFormats(ScanOptions.CODE_128)
            scan.setCameraId(0)
            scan.setBeepEnabled(true)
            scan.setBarcodeImageEnabled(true)
            scan.captureActivity = CustomScanBarcodeActivity::class.java
            barcodeLauncher.launch(scan)
        } else {
            scan.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            scan.setCameraId(0)
            scan.setBeepEnabled(true)
            scan.setBarcodeImageEnabled(true)
            scan.captureActivity = CustomScanActivity::class.java
            barcodeLauncher.launch(scan)
        }
    }

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        try {
            if (!result.contents.isNullOrEmpty()) {
                sn = result.contents
                viewModel.addSerialNumberUlpMaximo(noTransaksi, noMaterial, sn)
                Log.d("PemakaianMaximo", "SN Maximo cek : $sn")
                Log.d("PemakaianMaximo", "No Material cek : $noMaterial")
                Log.d("PemakaianMaximo", "No Transaksi cek : $noTransaksi")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val EXTRA_NO_TRANSAKSI = "extra_no_transaksi"
        const val EXTRA_NO_RESERVASI = "extra_no_reservasi"
        const val EXTRA_NO_MATERIAL = "extra_no_material"
    }
}