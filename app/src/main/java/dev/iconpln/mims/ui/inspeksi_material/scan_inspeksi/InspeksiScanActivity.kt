package dev.iconpln.mims.ui.inspeksi_material.scan_inspeksi

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.R
import dev.iconpln.mims.data.local.database.DaoSession
import dev.iconpln.mims.data.remote.response.AGOMaterialInspectionData
import dev.iconpln.mims.data.remote.response.AGOMaterialInspectionDetail
import dev.iconpln.mims.data.remote.response.AGOUpdateMaterialInspectionBySNBody
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.data.scan.CustomScanActivity
import dev.iconpln.mims.data.scan.CustomScanBarcodeActivity
import dev.iconpln.mims.databinding.ActivityInspeksiScanBinding
import dev.iconpln.mims.databinding.PopUpDialogInspeksiBinding
import dev.iconpln.mims.ui.inspeksi_material.AGOGetManufacturerViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetMasterBudgetStatusViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetMasterClassificationViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetMaterialInspectionBySNViewModel
import dev.iconpln.mims.ui.inspeksi_material.InspeksiMaterialAdapter
import dev.iconpln.mims.ui.inspeksi_material.detail_inspeksi.DetailInspeksiMaterialActivity
import dev.iconpln.mims.utils.ViewModelFactory

class InspeksiScanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInspeksiScanBinding
    private lateinit var popUpDialogInspeksiBinding: PopUpDialogInspeksiBinding
    private lateinit var listSNInspeksiAdapter: ListSNInspeksiAdapter
    private lateinit var viewModel: AGOGetMaterialInspectionBySNViewModel
    private lateinit var daoSession: DaoSession

    private lateinit var listInspeksiData: List<dev.iconpln.mims.data.local.database.TAGOMaterialData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInspeksiScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setRecyclerView()
        daoSession = (application as MyApplication).daoSession!!

        listInspeksiData = daoSession.tagoMaterialDataDao.queryBuilder()
            .orderDesc(dev.iconpln.mims.data.local.database.TAGOMaterialDataDao.Properties.SnMaterial)
            .list()

        initView()

        val apiService = ApiConfig.getApiService(this)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[AGOGetMaterialInspectionBySNViewModel::class.java]

        listSNInspeksiAdapter.setListInspeksi(listInspeksiData)

        binding.edtSearchSnMaterial.setOnEditorActionListener { inputtedSN, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputtedSN.length() < 19) {
                    Toast.makeText(this, "Serial Number Tidak Valid", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.agoMaterialInspectionDetail.removeObservers(this)
                    popUpDialogInspeksiBinding = PopUpDialogInspeksiBinding.inflate(layoutInflater)
                    viewModel.getMaterialInspectionData(inputtedSN.text.toString())

                    viewModel.isLoading.observe(this) {
                        if (it) {
                            Toast.makeText(this, "Mencari Data...", Toast.LENGTH_SHORT).show()
                        }
                    }
                    val dialog = Dialog(this)
                    showDialog(
                        dialog,
                        popUpDialogInspeksiBinding
                    )
                }
            }
            false
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun initView() {
        binding.btnQr.setOnClickListener {
            openScanner("qr")
        }

        binding.btnBarcode.setOnClickListener {
            openScanner("barcode")
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

        val dialog = Dialog(this)
        if (result.contents != null) {
            viewModel.agoMaterialInspectionDetail.removeObservers(this)
            popUpDialogInspeksiBinding = PopUpDialogInspeksiBinding.inflate(layoutInflater)
            val sn = result.contents
            viewModel.getMaterialInspectionData(sn)
            showDialog(
                dialog,
                popUpDialogInspeksiBinding
            )
        }
    }

    private fun setRecyclerView() {
        listSNInspeksiAdapter = ListSNInspeksiAdapter(arrayListOf())
        binding.rvInspeksiMaterial.layoutManager = LinearLayoutManager(this)
        binding.rvInspeksiMaterial.adapter = listSNInspeksiAdapter
    }

    private fun refreshData() {
        listInspeksiData = daoSession.tagoMaterialDataDao.queryBuilder()
            .orderDesc(dev.iconpln.mims.data.local.database.TAGOMaterialDataDao.Properties.SnMaterial)
            .list()
        listSNInspeksiAdapter.setListInspeksi(listInspeksiData)
    }

    private fun showDialog(
        dialog: Dialog,
        popUpDialogInspeksiBinding: PopUpDialogInspeksiBinding
    ) {
        clearDialogUI()
        viewModel.agoMaterialInspectionDetail.observe(this) { it ->
            if (it == null) {
                Toast.makeText(this, "Data Tidak Ditemukan", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                return@observe
            } else {
                viewModel.agoMaterialInspectionTeamData.observe(this) { dataTim ->
                    if (dataTim == null) {
                        popUpDialogInspeksiBinding.txtTimInspeksi.text = "Tidak Ada Data"
                    } else {
                        popUpDialogInspeksiBinding.txtTimInspeksi.text = dataTim.joinToString(", ") { it?.nama ?: "Tidak Ada Data" }
                    }
                }
                val dataInspeksi: AGOMaterialInspectionDetail? = it
                popUpDialogInspeksiBinding.txtNoInspeksi.text = it.nomorInspeksiRetur ?: "Tidak Ada Data"
                popUpDialogInspeksiBinding.txtNoMaterial.text = it.materialNo ?: "Tidak Ada Data"
                popUpDialogInspeksiBinding.txtNamaMaterial.text = it.namaMaterial ?: "Tidak Ada Data"
                popUpDialogInspeksiBinding.txtSerialNumber.text = it.serialLong ?: "Tidak Ada Data"
                popUpDialogInspeksiBinding.txtQty.text = it.qty ?: "Tidak Ada Data"
                popUpDialogInspeksiBinding.txtSatuan.text = it.unit ?: "Tidak Ada Data"
                popUpDialogInspeksiBinding.txtStatusInspeksi.text = it.statusInspeksi ?: "Tidak Ada Data"
                popUpDialogInspeksiBinding.txtStatusAnggaran.text = it.anggaran ?: "Tidak Ada Data"
                popUpDialogInspeksiBinding.txtNoAsset.text = it.noAsset ?: "Tidak Ada Data"
                popUpDialogInspeksiBinding.txtNamaPabrikan.text = it.namaPabrikan ?: "Tidak Ada Data"
                popUpDialogInspeksiBinding.txtTahunBuat.text = it.tahun ?: "Tidak Ada Data"
                popUpDialogInspeksiBinding.txtIdPelanggan.text = it.idPelanggan ?: "Tidak Ada Data"
                popUpDialogInspeksiBinding.txtKlasifikasi.text = it.namaKlasifikasi ?: "Tidak Ada Data"

                val nomorInspeksi = it.nomorInspeksiRetur
                val serialLong = it.serialLong
                val statusInspeksi = it.statusInspeksi

                if (statusInspeksi == "Belum Inspeksi" || statusInspeksi == "Progress") {
                    popUpDialogInspeksiBinding.btnSelesaiInspeksi.visibility = View.GONE
                    popUpDialogInspeksiBinding.btnSimpan.visibility = View.VISIBLE
                    popUpDialogInspeksiBinding.btnInspeksi.visibility = View.VISIBLE
                } else {
                    popUpDialogInspeksiBinding.btnSimpan.visibility = View.GONE
                    popUpDialogInspeksiBinding.btnInspeksi.visibility = View.GONE
                    popUpDialogInspeksiBinding.btnSelesaiInspeksi.visibility = View.VISIBLE
                }
                popUpDialogInspeksiBinding.btnCloseDialogInspeksi.setOnClickListener {
                    dialog.dismiss()
                }

                popUpDialogInspeksiBinding.btnSelesaiInspeksi.setOnClickListener {
                    dialog.dismiss()
                }

                popUpDialogInspeksiBinding.btnSimpan.setOnClickListener {
                    viewModel.insertMaterialInspectionDataToLocalDB(
                        daoSession,
                        dataInspeksi!!,
                    )
                    dialog.dismiss()
                    refreshData()

                    viewModel.isSuccessInsertMaterialInspectionData.observe(this) { success ->
                        if (success) {
                            Toast.makeText(this, "Data Material Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
                            viewModel.resetInsertMaterialInspectionDataStatus()
                        }
                    }

                    viewModel.dataExist.observe(this) { dataExist ->
                        if (dataExist) {
                            Toast.makeText(this, "Data Material Sudah Ada!", Toast.LENGTH_SHORT).show()
                            viewModel.resetInsertMaterialInspectionDataStatus()
                        }
                    }
                }

                popUpDialogInspeksiBinding.btnInspeksi.setOnClickListener {
                    val intent = Intent(this, DetailInspeksiMaterialActivity::class.java)

                    intent.putExtra(
                        DetailInspeksiMaterialActivity.EXTRA_SERIAL_NUMBER,
                        serialLong
                    )

                    startActivity(intent)
                }



                dialog.setContentView(popUpDialogInspeksiBinding.root)
                dialog.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown
                dialog.show()
            }
        }
    }

    private fun clearDialogUI() {
        popUpDialogInspeksiBinding.apply {
            txtNoMaterial.text = ""
            txtNamaMaterial.text = ""
            txtSerialNumber.text = ""
            txtQty.text = ""
            txtSatuan.text = ""
            txtStatusInspeksi.text = ""
            txtStatusAnggaran.text = ""
            txtNoAsset.text = ""
            txtNamaPabrikan.text = ""
            txtTahunBuat.text = ""
            txtIdPelanggan.text = ""
            txtKlasifikasi.text = ""
            txtTimInspeksi.text = ""
        }
    }
}