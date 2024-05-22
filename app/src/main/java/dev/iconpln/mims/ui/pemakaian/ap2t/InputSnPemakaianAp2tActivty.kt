package dev.iconpln.mims.ui.pemakaian.ap2t

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dev.iconpln.mims.R
import dev.iconpln.mims.data.remote.response.DataItemAp2t
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.data.scan.CustomScanActivity
import dev.iconpln.mims.data.scan.CustomScanBarcodeActivity
import dev.iconpln.mims.databinding.ActivityInputSnPemakaianAp2tBinding
import dev.iconpln.mims.utils.ViewModelFactory

class InputSnPemakaianAp2tActivty : AppCompatActivity() {
    private lateinit var binding: ActivityInputSnPemakaianAp2tBinding
    private lateinit var viewModel: InputSnPemakaianAp2tViewModel
    private lateinit var viewModelDel: DeleteSnViewModel
    private lateinit var rvAdapter: InputSnMaterialAp2tAdapter
    private var sn = ""
    private var noAgenda = ""
    private var jenisPelanggan = ""
    private var idPelanggan = ""
    private var namaPelanggan = ""
    private var noTransaksi = ""
    private var noMaterial = ""
    private var qty = ""
    private var plant = ""
    private var storlok = ""
    private var listNoMaterial = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputSnPemakaianAp2tBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRecyclerInputSn()

        val apiService = ApiConfig.getApiService(this)
        viewModelDel = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[DeleteSnViewModel::class.java]


        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[InputSnPemakaianAp2tViewModel::class.java]

        noAgenda = intent.getStringExtra(EXTRA_NO_AGENDA).toString()
        binding.txtNoAgenda.text = noAgenda
        jenisPelanggan = intent.getStringExtra(EXTRA_JENIS_PEKERJAAN).toString()
        binding.txtJenisPekerjaan.text = jenisPelanggan
        namaPelanggan = intent.getStringExtra(EXTRA_NAMA_PELANGGAN).toString()
        binding.txtNamaPelanggan.text = intent.getStringExtra(EXTRA_NAMA_PELANGGAN)
        binding.txtIdPelanggan.text = intent.getStringExtra(EXTRA_ID_PELANGGAN)
        idPelanggan = intent.getStringExtra(EXTRA_ID_PELANGGAN).toString()
        noTransaksi = intent.getStringExtra(EXTRA_NO_TRANSAKSI).toString()
        listNoMaterial = intent.getStringArrayListExtra(EXTRA_NO_MATERIAL) as ArrayList<String>
        plant = intent.getStringExtra(EXTRA_PLANT).toString()
        storlok = intent.getStringExtra(EXTRA_STORLOK).toString()
//        Log.d("InputSnPemakaianAp2t", "cek plant : $plant & $storlok")

        viewModel.getDataSnMaterialAp2t(noTransaksi, noAgenda, sn)

        viewModel.getDataInputSnMaterial.observe(this) {
            if (it != null) {
                rvAdapter.setListSnPemakaianAp2t(it.data)
            }
        }

//        viewModel.addSerialNumberUlpAp2t.observe(this){
//            if(it.message == "Success"){
//                viewModel.getDataInputSnMaterial.observe(this){
//                    rvAdapter.setListSnPemakaianAp2t(it.data)
//                }
//            }
//        }


        viewModel.addSerialNumberUlpAp2t.observe(this) {
            Log.d("InputSnPemakaianAp2t", "cek status : ${it}")
            if (it.status == 200) {
                viewModel.getDataSnMaterialAp2t(noTransaksi, noAgenda, "")
                Toast.makeText(this, "Berhasil Menambahkan Serial Number $sn", Toast.LENGTH_SHORT)
                    .show()
            } else if (it.status == 400) {
                Toast.makeText(this, "Serial Number $sn Sudah Ada", Toast.LENGTH_SHORT).show()
                Log.d("InputSnPemakaianAp2t", "cek status : ${it.status}")
            } else {
                Toast.makeText(this, "Gagal Menambahkan Serial Number $sn", Toast.LENGTH_SHORT)
                    .show()
            }

        }

        viewModelDel.deleteSn.observe(this@InputSnPemakaianAp2tActivty) {
            if (it.status == 200) {
                viewModel.getDataSnMaterialAp2t(noTransaksi, noAgenda, "")
                Toast.makeText(
                    this@InputSnPemakaianAp2tActivty,
                    "Berhasil Menghapus Serial Number",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this@InputSnPemakaianAp2tActivty,
                    "Gagal Menghapus Serial Number",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        rvAdapter.setOnItemClickCallback(object : InputSnMaterialAp2tAdapter.OnItemClickCallback {
            override fun onItemClicked(data: DataItemAp2t) {
                viewModelDel.deleteSn(
                    noAgenda = noAgenda,
                    noTransaksi = noTransaksi,
                    noMaterial = data.nomorMaterial,
                    qty = data.qty,
                    plant = plant,
                    storLoc = storlok,
                    sn = data.serialNumber
                )
//                viewModelDel.deleteSn.observe(this@InputSnPemakaianAp2t) {
//                    if (it.status == 200) {
//                        viewModel.getDataSnMaterialAp2t(noTransaksi, noAgenda, "")
//                        Toast.makeText(
//                            this@InputSnPemakaianAp2t,
//                            "Berhasil Menghapus Serial Number ${data.serialNumber}",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    } else {
//                        Toast.makeText(
//                            this@InputSnPemakaianAp2t,
//                            "Gagal Menghapus Serial Number ${data.serialNumber}",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
            }
        })

        val adapterNoMaterial = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            listNoMaterial.distinct()
        )
        binding.dropdownNoMaterial.setAdapter(adapterNoMaterial)
        binding.dropdownNoMaterial.setOnItemClickListener { parent, view, position, id ->
            noMaterial = binding.dropdownNoMaterial.text.toString()

            if (noMaterial.contains("0025")) {
                binding.apply {
                    btnScanSnMaterial.visibility = View.GONE
                    textView12.visibility = View.GONE
                    btnInputSnManual.visibility = View.GONE
                    lblQtyAp2t.visibility = View.VISIBLE
                    edtQtyAp2t.visibility = View.VISIBLE
                    btnSimpanQty.visibility = View.VISIBLE
                }
            } else {
                binding.apply {
                    btnScanSnMaterial.visibility = View.VISIBLE
                    textView12.visibility = View.VISIBLE
                    btnInputSnManual.visibility = View.VISIBLE
                    lblQtyAp2t.visibility = View.GONE
                    edtQtyAp2t.visibility = View.GONE
                    btnSimpanQty.visibility = View.GONE
                }
            }
        }

        binding.edtQtyAp2t.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                qty = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnSimpanQty.setOnClickListener {
            viewModel.addSerialNumberUlpAp2t(
                noTransaksi,
                noMaterial,
                "",
                qty,
                noAgenda,
                valuationType = ""
            )
        }

        binding.btnScanSnMaterial.setOnClickListener {
            val dialog =
                BottomSheetDialog(
                    this@InputSnPemakaianAp2tActivty,
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
        binding.btnBack.setOnClickListener { onBackPressed() }
    }

    private fun setRecyclerInputSn() {
        rvAdapter = InputSnMaterialAp2tAdapter()
        binding.rvListSn.apply {
            layoutManager = LinearLayoutManager(this@InputSnPemakaianAp2tActivty)
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
                viewModel.addSerialNumberUlpAp2t(noTransaksi, noMaterial, sn, "", noAgenda, "")
                Log.d("PemakaianAp2t", "SN Ap2t cek : $sn")
                Log.d("PemakaianAp2t", "No Transaksi cek : $noTransaksi")
                Log.d("PemakaianAp2t", "No Agenda cek : $noAgenda")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        const val EXTRA_NO_AGENDA = "extra_no_agenda"
        const val EXTRA_ID_PELANGGAN = "extra_id_pelanggan"
        const val EXTRA_JENIS_PEKERJAAN = "extra_jenis_pekerjaan"
        const val EXTRA_NAMA_PELANGGAN = "extra_nama_pelanggan"
        const val EXTRA_NO_TRANSAKSI = "extra_no_transaksi"
        const val EXTRA_NO_MATERIAL = "extra_no_material"
        const val EXTRA_PLANT = "extra_plant"
        const val EXTRA_STORLOK = "extra_storlok"
    }
}