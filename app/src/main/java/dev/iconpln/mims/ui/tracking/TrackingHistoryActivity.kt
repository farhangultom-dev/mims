package dev.iconpln.mims.ui.tracking

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.R
import dev.iconpln.mims.data.scan.CustomScanActivity
import dev.iconpln.mims.data.scan.CustomScanBarcodeActivity
import dev.iconpln.mims.databinding.ActivityTrackingBinding
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.PopupDialog

class TrackingHistoryActivity : BaseActivity() {
    private lateinit var binding: ActivityTrackingBinding
    private val viewModel: TrackingHistoryViewModel by viewModels()
    private var sn = ""
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

    }

    private fun initView() {
        dialog = Helper.loadingDialog(this@TrackingHistoryActivity)
        val popupDialog = PopupDialog()

        binding.btnScan.setOnClickListener {
            val dialog =
                BottomSheetDialog(this@TrackingHistoryActivity, R.style.AppBottomSheetDialogTheme)
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

        binding.btnInputManual.setOnClickListener {
            popupDialog.show(supportFragmentManager, getString(R.string.masukan_sn_manual))
        }

        viewModel.trackingResponse.observe(this) {
            binding.apply {
                if (it.datas.isNullOrEmpty()) {
                    Toast.makeText(this@TrackingHistoryActivity, it.message, Toast.LENGTH_SHORT)
                        .show()
                } else {
                    val intent =
                        Intent(this@TrackingHistoryActivity, SpecMaterialActivity::class.java)
                    intent.putExtra(DataMaterialTrackingActivity.EXTRA_SN, sn)
                    startActivity(intent)
                }
            }
        }

        viewModel.isLoading.observe(this) {
            when (it) {
                true -> {
                    dialog.show()
                }

                else -> {
                    dialog.dismiss()
                }
            }
        }

        binding.btnBack.setOnClickListener { finish() }

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
                viewModel.getTrackingHistory(result.contents, this)
                dialog.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}