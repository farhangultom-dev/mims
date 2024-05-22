package dev.iconpln.mims.ui.inspeksi_material.detail_inspeksi

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import dev.iconpln.mims.R
import dev.iconpln.mims.data.local.database.DaoSession
import dev.iconpln.mims.data.remote.response.AGOBudgetStatusData
import dev.iconpln.mims.data.remote.response.AGOGetMasterClassificationData
import dev.iconpln.mims.data.remote.response.AGOManufacturerData
import dev.iconpln.mims.data.remote.response.AGOUpdateMaterialInspectionBySNBody
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivityDetailInspeksiMaterialBinding
import dev.iconpln.mims.ui.inspeksi_material.AGOGetManufacturerViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetMasterBudgetStatusViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetMasterClassificationViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetMaterialInspectionBySNViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOUpdateMaterialInspectionBySNViewModel
import dev.iconpln.mims.utils.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailInspeksiMaterialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailInspeksiMaterialBinding

    private lateinit var getInspectionBySNViewModel: AGOGetMaterialInspectionBySNViewModel
    private lateinit var budgetStatusViewModel: AGOGetMasterBudgetStatusViewModel
    private lateinit var manufacturerViewModel: AGOGetManufacturerViewModel
    private lateinit var materialClassificationViewModel: AGOGetMasterClassificationViewModel
    private lateinit var updateMaterialInspectionBySNViewModel: AGOUpdateMaterialInspectionBySNViewModel

    private var listBudgetStatus = arrayListOf<AGOBudgetStatusData>()
    private var budgetStatusData = arrayListOf<String>()
    private var listManufacturer = arrayListOf<AGOManufacturerData>()
    private var manufacturerData = arrayListOf<String>()
    private var listMaterialClassification = arrayListOf<AGOGetMasterClassificationData>()
    private var materialClassificationData = arrayListOf<String>()

    private var inspectionNumberValue = ""
    private var budgetStatusValue = ""
    private var manufacturerValue = ""
    private var materialClassificationValue = ""
    private var materialInspectionATTBLimbahValue = ""
    private var classificationNameValue = ""

    val formatYear = SimpleDateFormat("yyyy", Locale.getDefault())
    var currentData = Date()
    var currentYear = formatYear.format(currentData)

    val materialInspectionATTBLimbah = listOf(
        "ATTB",
        "LIMBAH"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailInspeksiMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val apiService = ApiConfig.getApiService(this)

        getInspectionBySNViewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[AGOGetMaterialInspectionBySNViewModel::class.java]

        budgetStatusViewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[AGOGetMasterBudgetStatusViewModel::class.java]

        manufacturerViewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[AGOGetManufacturerViewModel::class.java]

        materialClassificationViewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[AGOGetMasterClassificationViewModel::class.java]

        updateMaterialInspectionBySNViewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[AGOUpdateMaterialInspectionBySNViewModel::class.java]

        getInspectionBySNViewModel.getMaterialInspectionData(intent.getStringExtra(EXTRA_SERIAL_NUMBER) ?: "")

        budgetStatusViewModel.getMasterBudgetStatus()

        materialClassificationViewModel.getMasterClassification()

        getInspectionBySNViewModel.agoMaterialInspectionDetail.observe(this) {
            val materialNumber = it?.materialNo.toString()
            manufacturerViewModel.getManufacturerData(materialNumber)
            inspectionNumberValue = it?.nomorInspeksiRetur ?: ""
            binding.edtNoMaterial.setText(materialNumber)
            binding.edtNamaMaterial.setText(it?.namaMaterial)
            if (it?.anggaran != null) {
                binding.dropdownStatusAnggaran.setText(it.anggaran.uppercase(), false)
            }
            if (it?.namaPabrikan != null) {
                binding.dropdownPilihAttbLimbah.setText(it.namaPabrikan.uppercase(), false)
            }
            if (it?.namaKlasifikasi != null) {
                binding.dropdownKlasifikasi.setText(it.namaKlasifikasi, false)
            }
            binding.edtQty.setText(it?.qty.toString())
            binding.edtUnit.setText(it?.unit)
            binding.edtSerialNumber.setText(it?.serialLong)
            binding.edtNoAsset.setText(it?.noAsset)
            binding.edtTahun.setText(it?.tahun)
            binding.edtIdPelanggan.setText(it?.idPelanggan)
        }

        val adapterMaterialInspectionATTBLimbah = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            materialInspectionATTBLimbah
        )

        binding.dropdownPilihAttbLimbah.setAdapter(adapterMaterialInspectionATTBLimbah)

        binding.dropdownPilihAttbLimbah.setOnItemClickListener { _, _, position, _ ->
            materialInspectionATTBLimbahValue = materialInspectionATTBLimbah[position]
        }

        budgetStatusViewModel.masterBudgetStatus.observe(this) {
            listBudgetStatus.clear()
            listBudgetStatus.addAll(it)
            listBudgetStatus.forEach {listBudgetStatus ->
                budgetStatusData.add(listBudgetStatus.nama ?: "")
            }
            val adapterBudgetStatus = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                budgetStatusData
            )
            binding.dropdownStatusAnggaran.setAdapter(adapterBudgetStatus)

            binding.dropdownStatusAnggaran.setOnItemClickListener { _, _, position, _ ->
                budgetStatusValue = listBudgetStatus[position].nama ?: ""
            }
        }

        manufacturerViewModel.manufacturerData.observe(this) {
            listManufacturer.clear()
            listManufacturer.addAll(it)
            if (it.isNullOrEmpty()) {
                manufacturerData.add("Tidak Ada Data Pabrikan")
            } else {
                listManufacturer.forEach {listManufacturer ->
                    manufacturerData.add(listManufacturer.namaPabrikan ?: "")
                }
            }
            val adapterManufacturer = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                manufacturerData
            )
            binding.dropdownPabrikan.setAdapter(adapterManufacturer)

            binding.dropdownPabrikan.setOnItemClickListener { _, _, position, _ ->
                manufacturerValue = if (manufacturerData[position] == "Tidak Ada Data Pabrikan") {
                    "0"
                } else {
                    listManufacturer[position].idPabrikan ?: "0"
                }
            }
        }

        materialClassificationViewModel.masterClassificationData.observe(this) {
            listMaterialClassification.clear()
            listMaterialClassification.addAll(it)
            listMaterialClassification.forEach {listMaterialClassification ->
                materialClassificationData.add(listMaterialClassification.nama ?: "")
            }
            val adapterMaterialClassification = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                materialClassificationData
            )
            binding.dropdownKlasifikasi.setAdapter(adapterMaterialClassification)

            binding.dropdownKlasifikasi.setOnItemClickListener { _, _, position, _ ->
                materialClassificationValue = listMaterialClassification[position].kodeKlasifikasi ?: ""
                classificationNameValue = listMaterialClassification[position].nama ?: ""

                if (classificationNameValue == "Claim Asuransi") {
                    binding.edtNoPolis.isEnabled = true
                    binding.edtNoPolis.hint = "Masukkan Nomor Polis"
                }
            }
        }

        binding.btnSimpan.setOnClickListener {
            val selectAttbLimbah = "Mohon Pilih ATTB/Limbah"
            val selectBudgetStatus = "Mohon Pilih Status Anggaran"
            val fillAssetNumber = "Mohon Isi Nomor Asset"
            val selectManufacturer = "Mohon Pilih Pabrikan"
            val fillYear = "Mohon Isi Tahun Pabrikan"
            val fillCustomerId = "Mohon Isi ID Pelanggan"
            val selectMaterialClassification = "Mohon Pilih Klasifikasi Material"
            val fillInsurancePolicyNumber = "Mohon Isi Nomor Polis"
            val yearExceededCurrentYear = "Tahun Melebihi Tahun Saat Ini"
            val yearAtLeast = "Tahun Harus Memiliki 4 Digit"

            if (materialInspectionATTBLimbahValue.isEmpty()) {
                Toast.makeText(this, selectAttbLimbah, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                binding.dropdownPilihAttbLimbah.error = null
            }

            if (budgetStatusValue.isEmpty()) {
                Toast.makeText(this, selectBudgetStatus, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                binding.dropdownStatusAnggaran.error = null
            }

            if (binding.edtNoAsset.text.isNullOrEmpty()) {
                binding.edtNoAsset.error = fillAssetNumber
                return@setOnClickListener
            } else {
                binding.edtNoAsset.error = null
            }

            if (manufacturerValue.isEmpty()) {
                Toast.makeText(this, selectManufacturer, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                binding.dropdownPabrikan.error = null
            }

            if (binding.edtTahun.text.isNullOrEmpty() ||
                binding.edtTahun.text.toString().toInt() > currentYear.toInt() ||
                binding.edtTahun.text.toString().length < 4
            ) {
                binding.edtTahun.error = when {
                    binding.edtTahun.text.isNullOrEmpty() -> fillYear
                    binding.edtTahun.text.toString().toInt() > currentYear.toInt() -> yearExceededCurrentYear
                    else -> yearAtLeast
                }
                return@setOnClickListener
            } else {
                binding.edtTahun.error = null
            }

            if (binding.edtIdPelanggan.text.isNullOrEmpty()) {
                binding.edtIdPelanggan.error = fillCustomerId
                return@setOnClickListener
            } else {
                binding.edtIdPelanggan.error = null
            }

            if (materialClassificationValue.isEmpty()) {
                Toast.makeText(this, selectMaterialClassification, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                binding.dropdownKlasifikasi.error = null
            }

            if (classificationNameValue == "Claim Asuransi" && binding.edtNoPolis.text.isNullOrEmpty()) {
                binding.edtNoPolis.error = fillInsurancePolicyNumber
                return@setOnClickListener
            } else {
                binding.edtNoPolis.error = null
            }


            val bodyRequest = AGOUpdateMaterialInspectionBySNBody(
                inspectionNumberValue,
                intent.getStringExtra(EXTRA_SERIAL_NUMBER) ?: "",
                budgetStatusValue.uppercase(),
                materialClassificationValue,
                binding.edtNoAsset.text.toString(),
                binding.edtQty.text.toString(),
                binding.edtTahun.text.toString(),
                manufacturerValue,
                binding.edtIdPelanggan.text.toString(),
                materialInspectionATTBLimbahValue,
                binding.edtNoPolis.text.toString()
            )
            updateMaterialInspectionBySNViewModel.updateMaterialInspectionBySN(
                bodyRequest
            )
        }

        updateMaterialInspectionBySNViewModel.isLoading.observe(this) {
            Toast.makeText(this, "Mengirim Data", Toast.LENGTH_SHORT).show()
        }

        updateMaterialInspectionBySNViewModel.updateMaterialInspectionResponse.observe(this) {
            if (it?.msg == "Inspeksi Material berhasil") {
                Toast.makeText(this, "Inspeksi Berhasil", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Gagal Inspeksi", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val EXTRA_SERIAL_NUMBER = "extra_serial_number"
    }
}