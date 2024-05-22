package dev.iconpln.mims.ui.registrasi_approval.registrasi

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.RegistrasiSnScanActivity
import dev.iconpln.mims.data.remote.response.RequestBodyCheckSerialNumber
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivityRegisterSnMaterialBinding
import dev.iconpln.mims.utils.ViewModelFactory

class RegistrasiSnMaterialActivity : BaseActivity() {
    private lateinit var binding: ActivityRegisterSnMaterialBinding
    private lateinit var viewModel: RegistrasiMaterialViewModel
    private lateinit var rvAdapter: ListRegisSnMaterialAdapter
    private var status = "Processed"
    private var plantCode = ""
    private var storLok = ""
    private var isDelete = false
    private var noTransaksi = ""
    private var valType = ""
    private var snNew = ""
    private var requestBodyCheckSerialNumber =
        RequestBodyCheckSerialNumber(isDelete, noTransaksi, snNew, valType)
    private var sn = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterSnMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegis.setOnClickListener {
            val intent = Intent(this, RegistrasiSnScanActivity::class.java)
            startActivity(intent)
        }

        val apiService = ApiConfig.getApiService(this)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[RegistrasiMaterialViewModel::class.java]

//        viewModel.getMonitoringMaterial(status)
//        viewModel.monitAktivMaterial.observe(this) {
//            if (it != null) {
//                rvAdapter.setListRegisSn(it.data)
//            }
//        }

        viewModel.getNewSerialNumber(plantCode = "", storLok = "", status = "PROCESSED")
        viewModel.newGetRegistrasiSn.observe(this) {
            if (it != null) {
                if (it.message == "Success") {
                    rvAdapter.setListRegisSn(it.data)
                } else {
                    rvAdapter.setListRegisSn(arrayListOf())
                }
            }
        }


//        viewModel.insertMaterialRegistrasi.observe(this) {
//            if (it != null) {
//                if (it.message == "Success") {
//                    showSuccessInputDialogBox()
//                }
//            }
//        }

//        viewModel.insertMaterialRegistrasiByScan.observe(this) {
//            if (it != null) {
//                if (it.message == "Success") {
//                    showSuccessInputDialogBoxByScan()
//                }
//            }
//        }

        viewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) {
            if (it != null) {
                rvAdapter.setListRegisSn(arrayListOf())
//                viewModel.getMonitoringMaterial(status)
                Toast.makeText(this, "$it", Toast.LENGTH_LONG).show()
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object :
            com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                if (tab?.text == "Processed") {
                    status = tab.text.toString()
                    viewModel.getNewSerialNumber(plantCode = "", storLok = "", status = "PROCESSED")
                } else if (tab?.text == "Approved") {
                    status = tab.text.toString()
                    viewModel.getNewSerialNumber(plantCode = "", storLok = "", status = "APPROVED")
                }
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}

            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}

        })

        setRecyclerView()

        binding.btnBack.setOnClickListener { onBackPressed() }
    }

//    private fun showInputSnDialogBox() {
//        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
//        val view = layoutInflater.inflate(R.layout.input_dialog_registrasi, null)
//        val btnClose = view.findViewById<Button>(R.id.btn_close)
//        val btnRegis = view.findViewById<AppCompatButton>(R.id.btn_regis)
//        val btnScan = view.findViewById<AppCompatButton>(R.id.btn_scan)
//        val inputSn = view.findViewById<TextInputEditText>(R.id.edt_sn)
//        builder.setView(view)
//        btnClose.setOnClickListener {
//            builder.dismiss()
//        }
//        btnRegis.setOnClickListener {
////            viewModel.setInsertMaterialRegistrasi(inputSn.text.toString().trim(), "text")
//            viewModel.getCheckSerialNumber("plantCode", "storLok", RequestBodyCheckSerialNumber(isDelete, noTransaksi, snNew, valType))
//            snNew = inputSn.text.toString()
//        }
//        btnScan.setOnClickListener {
//            val dialog = BottomSheetDialog(
//                this@RegistrasiSnMaterialActivity,
//                R.style.AppBottomSheetDialogTheme
//            )
//            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_menu, null)
//            val btnScanBarcode = view.findViewById<CardView>(R.id.cv_button_satu)
//            val btnScanQRCode = view.findViewById<CardView>(R.id.cv_button_dua)
//            val ivScanBarcode = view.findViewById<ImageView>(R.id.imageView2)
//            val ivScanQRcode = view.findViewById<ImageView>(R.id.imageView1)
//            val txtScanBarcode = view.findViewById<TextView>(R.id.textView2)
//            val txtScanQRcode = view.findViewById<TextView>(R.id.textView1)
//
//            ivScanBarcode.setBackgroundResource(R.drawable.iv_btn_barcode)
//            ivScanQRcode.setBackgroundResource(R.drawable.iv_btn_qr_code)
//            txtScanBarcode.text = getString(R.string.scan_barcode)
//            txtScanQRcode.text = getString(R.string.scan_qr_code)
//
//            btnScanBarcode.setOnClickListener {
//                openScanner("barcode")
//                dialog.dismiss()
//            }
//
//            btnScanQRCode.setOnClickListener {
//                openScanner("qrcode")
//                dialog.dismiss()
//            }
//
//            dialog.setCancelable(true)
//            dialog.setContentView(view)
//            builder.dismiss()
//            dialog.show()
//        }
//        builder.setCanceledOnTouchOutside(false)
//        builder.show()
//    }

//    private fun showSuccessInputDialogBoxByScan() {
//        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
//        val view = layoutInflater.inflate(R.layout.alert_dialog_berhasil_regis_by_scan, null)
//        val tvSN = view.findViewById<TextView>(R.id.textView22)
//        val tvMessage = view.findViewById<TextView>(R.id.textView23)
//        val btnSelesai = view.findViewById<Button>(R.id.btn_berhasil_regis)
//        val btnScanLagi = view.findViewById<Button>(R.id.btn_scan_again)
//        builder.setView(view)
//        tvSN.text = "SN material $sn"
//        tvMessage.text = "Telah sesuai SPLN dan berhasil di registrasi."
//        btnSelesai.setOnClickListener {
//            builder.dismiss()
//            viewModel.getCheckSerialNumber(plantCode, storLok,requestBodyCheckSerialNumber)
//        }
//        btnScanLagi.setOnClickListener {
//            val dialog = BottomSheetDialog(
//                this@RegistrasiSnMaterialActivity,
//                R.style.AppBottomSheetDialogTheme
//            )
//            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_menu, null)
//            val btnScanBarcode = view.findViewById<CardView>(R.id.cv_button_satu)
//            val btnScanQRCode = view.findViewById<CardView>(R.id.cv_button_dua)
//            val ivScanBarcode = view.findViewById<ImageView>(R.id.imageView2)
//            val ivScanQRcode = view.findViewById<ImageView>(R.id.imageView1)
//            val txtScanBarcode = view.findViewById<TextView>(R.id.textView2)
//            val txtScanQRcode = view.findViewById<TextView>(R.id.textView1)
//
//            ivScanBarcode.setBackgroundResource(R.drawable.iv_btn_barcode)
//            ivScanQRcode.setBackgroundResource(R.drawable.iv_btn_qr_code)
//            txtScanBarcode.text = getString(R.string.scan_barcode)
//            txtScanQRcode.text = getString(R.string.scan_qr_code)
//
//            btnScanBarcode.setOnClickListener {
//                openScanner("barcode")
//                dialog.dismiss()
//            }
//
//            btnScanQRCode.setOnClickListener {
//                openScanner("qrcode")
//                dialog.dismiss()
//            }
//
//            dialog.setCancelable(true)
//            dialog.setContentView(view)
//            builder.dismiss()
//            dialog.show()
//        }
//        builder.setCanceledOnTouchOutside(false)
//        builder.show()
//    }

//    private fun showSuccessInputDialogBox() {
//        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog).create()
//        val view = layoutInflater.inflate(R.layout.alert_dialog_berhasil_regis, null)
//        val tvSN = view.findViewById<TextView>(R.id.textView22)
//        val tvMessage = view.findViewById<TextView>(R.id.textView23)
//        val btnSelesai = view.findViewById<Button>(R.id.btn_berhasil_regis)
//        builder.setView(view)
//        tvSN.text = "SN material $sn"
//        tvMessage.text = "Telah sesuai SPLN dan berhasil di registrasi."
//        btnSelesai.setOnClickListener {
//            builder.dismiss()
//            viewModel.getCheckSerialNumber("plantCode", "storLok", RequestBodyCheckSerialNumber(isDelete, noTransaksi, snNew, valType))
//        }
//        builder.setCanceledOnTouchOutside(false)
//        builder.show()
//    }

//    private fun openScanner(type: String) {
//        val scan = ScanOptions()
//
//        if (type == "barcode") {
//            scan.setDesiredBarcodeFormats(ScanOptions.CODE_128)
//            scan.setCameraId(0)
//            scan.setBeepEnabled(true)
//            scan.setBarcodeImageEnabled(true)
//            scan.captureActivity = CustomScanBarcodeActivity::class.java
//            barcodeLauncher.launch(scan)
//        } else {
//            scan.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
//            scan.setCameraId(0)
//            scan.setBeepEnabled(true)
//            scan.setBarcodeImageEnabled(true)
//            scan.captureActivity = CustomScanActivity::class.java
//            barcodeLauncher.launch(scan)
//        }
//    }

//    private val barcodeLauncher = registerForActivityResult(
//        ScanContract()
//    ) { result: ScanIntentResult ->
//        try {
//            if (!result.contents.isNullOrEmpty()) {
//                sn = result.contents
//                viewModel.getCheckSerialNumber("plantCode", "storLok", RequestBodyCheckSerialNumber(isDelete, noTransaksi, snNew, valType)) // snNew
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    private fun setRecyclerView() {
        rvAdapter = ListRegisSnMaterialAdapter()
        binding.rvRegisSn.apply {
            layoutManager = LinearLayoutManager(this@RegistrasiSnMaterialActivity)
            setHasFixedSize(true)
            adapter = rvAdapter
        }
    }
}