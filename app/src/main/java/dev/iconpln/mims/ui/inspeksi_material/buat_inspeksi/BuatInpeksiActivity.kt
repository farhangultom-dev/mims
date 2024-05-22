package dev.iconpln.mims.ui.inspeksi_material.buat_inspeksi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import dev.iconpln.mims.R
import dev.iconpln.mims.data.remote.response.AGOCreateMaterialInspectionBody
import dev.iconpln.mims.data.remote.response.AGODetailATTBData
import dev.iconpln.mims.data.remote.response.AGODetailATTBDataBasedOnRefNum
import dev.iconpln.mims.data.remote.response.AGOReferenceNumberData
import dev.iconpln.mims.data.remote.response.AGOStorLocData
import dev.iconpln.mims.data.remote.response.AGOStorLocDetailData
import dev.iconpln.mims.data.remote.response.StorLocItem
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivityBuatInspeksiBinding
import dev.iconpln.mims.databinding.ActivityInspeksiMaterialBinding
import dev.iconpln.mims.ui.inspeksi_material.AGOCreateMaterialInspectionViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetATTBViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetATTBWithRefNumViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetNoReferensiAutoViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetStorLocViewModel
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.SharedPrefsUtils
import dev.iconpln.mims.utils.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BuatInspeksiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBuatInspeksiBinding
    private lateinit var createMaterialInspectionVM: AGOCreateMaterialInspectionViewModel
    private lateinit var getATTBDataBasedOnRefNumVM: AGOGetATTBWithRefNumViewModel
    private lateinit var getStorLocVM : AGOGetStorLocViewModel
    private lateinit var getNoRefAutoVM : AGOGetNoReferensiAutoViewModel
    private lateinit var noRefAdapter: ArrayAdapter<String>
    private var attbDataBasedOnRefNum = arrayListOf<AGODetailATTBDataBasedOnRefNum>()
    private var attbNameAndNumberBasedOnRefNum = arrayListOf<String>()
    private var attbBasedOnRefNum = ""

    private var storlocData = arrayListOf<AGOStorLocDetailData>()
    private var storlocName = arrayListOf<String>()
    private var storLoc = ""

    private var noRefData = arrayListOf<AGOReferenceNumberData>()
    private var referenceNumber = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBuatInspeksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val apiService = ApiConfig.getApiService(this)


        val plant = SharedPrefsUtils.getStringPreference(this, Config.KEY_PLANT, "")
        val storLocData = SharedPrefsUtils.getStringPreference(this, Config.KEY_STOR_LOC, "")
        val roleId = SharedPrefsUtils.getStringPreference(this, Config.KEY_ROLE_ID, "")
        val username = SharedPrefsUtils.getStringPreference(this, Config.KEY_USERNAME, "")

        val formatDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = Date()
        val date = formatDate.format(currentDate)

        binding.edtTanggalInspeksi.setText(date)

        getATTBDataBasedOnRefNumVM = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[AGOGetATTBWithRefNumViewModel::class.java]

        getStorLocVM = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[AGOGetStorLocViewModel::class.java]

        createMaterialInspectionVM = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[AGOCreateMaterialInspectionViewModel::class.java]

        getNoRefAutoVM = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[AGOGetNoReferensiAutoViewModel::class.java]

        getStorLocVM.getStorLocData(
            plant.toString(),
            roleId.toString(),
        )

        getATTBDataBasedOnRefNumVM.attbDataBasedOnRefNum.observe(this) {
            attbDataBasedOnRefNum.clear()
            attbNameAndNumberBasedOnRefNum.clear()
            attbDataBasedOnRefNum.addAll(it)
            attbDataBasedOnRefNum.forEach() { attbDetail ->
                attbNameAndNumberBasedOnRefNum.add("${attbDetail.materialNo} ${attbDetail.materialDesc}")
            }

            val adapterATTB = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                attbNameAndNumberBasedOnRefNum
            )

            binding.dropdownNamaMaterial.setAdapter(adapterATTB)

            binding.dropdownNamaMaterial.setOnItemClickListener { _, _, i, _ ->
                binding.edtQtySiapInspeksi.setText(attbDataBasedOnRefNum[i].jumlahPengembalian.toString())
                binding.edtSatuan.setText(attbDataBasedOnRefNum[i].satuan.toString())
                binding.edtJumlahUnit.isEnabled = attbDataBasedOnRefNum[i].satuan.toString() == "M"
                binding.edtAlasanPengembalian.setText(attbDataBasedOnRefNum[i].alasanPengembalian.toString())
                attbBasedOnRefNum = attbDataBasedOnRefNum[i].materialNo.toString()
            }
        }

        getStorLocVM.storLocData.observe(this) {
            storlocData.clear()
            storlocData.addAll(it)
            storlocData.forEach() { storLocDetail ->
                storlocName.add(storLocDetail.storLocName.toString())
            }

            val adapterStorLoc = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                storlocName
            )

            binding.dropdownLokasiInspeksi.setAdapter(adapterStorLoc)

            binding.dropdownLokasiInspeksi.setOnItemClickListener { _, _, i, _ ->
                storLoc = storlocData[i].storLoc.toString()
            }
        }


        noRefAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            referenceNumber
        )

        binding.edtNoReferensi.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                getNoRefAutoVM.getNoReferensiAuto(
                    plant.toString(),
                    storLocData.toString(),
                    binding.edtNoReferensi.text.toString()
                )

                noRefAdapter.notifyDataSetChanged()
            }
        })


        getNoRefAutoVM.noReferensiData.observe(this) {
            referenceNumber.clear()
            noRefData.addAll(it)
            noRefData.forEach() { noRefDetail ->
                referenceNumber.add(noRefDetail.material_document.toString())
            }

            binding.edtNoReferensi.setAdapter(noRefAdapter)
        }

        binding.edtNoReferensi.setOnItemClickListener { _, _, i, _ ->
            getATTBDataBasedOnRefNumVM.getAttbDataWithRefNum(
                plant.toString(),
                storLocData.toString(),
                noRefData[i].material_document.toString()
            )
            binding.dropdownNamaMaterial.isEnabled = true
        }

        binding.btnKirim.setOnClickListener{

            val selectReferenceNum = "Mohon Masukan Nomor Referensi"
            val selectMaterial = "Mohon Pilih Material"
            val selectStorLoc = "Mohon Pilih Lokasi Fisik"
            val inputQtyInspeksi = "Mohon Isi Jumlah Inspeksi"
            val inputQtyUnit = "Mohon Isi Jumlah Unit"

            if (binding.edtNoReferensi.text.toString().isEmpty()) {
                Toast.makeText(this, selectReferenceNum, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                binding.edtNoReferensi.error = null
            }

            if (binding.dropdownNamaMaterial.text.toString().isEmpty() || binding.dropdownNamaMaterial.text.toString() == "Pilih Nama Material") {
                Toast.makeText(this, selectMaterial, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                binding.dropdownNamaMaterial.error = null
            }

            if (binding.dropdownLokasiInspeksi.text.toString().isEmpty() || binding.dropdownLokasiInspeksi.text.toString() == "Pilih Lokasi Fisik"){
                Toast.makeText(this, selectStorLoc, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                binding.dropdownLokasiInspeksi.error = null
            }

            if (binding.edtQtyInspeksi.text.toString().isEmpty()) {
                Toast.makeText(this, inputQtyInspeksi, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                binding.edtQtyInspeksi.error = null
            }

            if (binding.edtSatuan.text.toString() == "M" && binding.edtJumlahUnit.text.toString().isEmpty()) {
                Toast.makeText(this, inputQtyUnit, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                binding.edtJumlahUnit.error = null
            }

            val bodyRequest = AGOCreateMaterialInspectionBody(
                "",
                attbBasedOnRefNum,
                "",
                "",
                binding.edtTanggalInspeksi.text.toString(),
                "",
                "",
                storLoc,
                binding.edtSatuan.text.toString(),
                "",
                "",
                "",
                emptyMap(),
                binding.edtNoReferensi.text.toString(),
                binding.edtQtySiapInspeksi.text.toString(),
                binding.edtQtyInspeksi.text.toString(),
                binding.edtAlasanPengembalian.text.toString(),
                plant.toString(),
                storLocData.toString(),
                username
            )
            createMaterialInspectionVM.createMaterialInspection(
                bodyRequest
            )
        }

        createMaterialInspectionVM.isLoading.observe(this) {
            Toast.makeText(this, "Mengirim Data...", Toast.LENGTH_SHORT).show()
        }

        createMaterialInspectionVM.createMaterialInspectionResponse.observe(this) {
            if (it?.msg == "Data berhasil disimpan") {
                Toast.makeText(this, "Data Berhasil Disimpan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, it?.msg, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}