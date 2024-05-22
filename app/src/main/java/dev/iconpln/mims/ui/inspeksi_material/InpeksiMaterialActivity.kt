package dev.iconpln.mims.ui.inspeksi_material

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.R
import dev.iconpln.mims.data.remote.response.AGODetailATTBData
import dev.iconpln.mims.data.remote.response.AGODetailInspectionMaterialReturnData
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivityInspeksiMaterialBinding
import dev.iconpln.mims.ui.inspeksi_material.buat_inspeksi.BuatInspeksiActivity
import dev.iconpln.mims.ui.inspeksi_material.scan_inspeksi.InspeksiScanActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.SharedPrefsUtils
import dev.iconpln.mims.utils.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class InspeksiMaterialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInspeksiMaterialBinding
    private lateinit var inspectionReturnVM: AGOGetInspectionReturnViewModel
    private lateinit var getATTBVM: AGOGetATTBViewModel
    private lateinit var adapter: InspeksiMaterialAdapter
    private var attbData = arrayListOf<AGODetailATTBData>()
    private var attbNameAndNumber = arrayListOf<String>()
    private var statusInspeksiValue = ""
    private var attbValue = ""
    private var inspectionDateValue = ""
    private lateinit var calendar : Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInspeksiMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRecyclerView()

        calendar = Calendar.getInstance()

        binding.btnScan.setOnClickListener {
            startActivity(Intent(this, InspeksiScanActivity::class.java))
        }

        binding.btnKirim.setOnClickListener {
            startActivity(Intent(this, BuatInspeksiActivity::class.java))
        }


        val apiService = ApiConfig.getApiService(this)
        
        inspectionReturnVM = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[AGOGetInspectionReturnViewModel::class.java]

        getATTBVM = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[AGOGetATTBViewModel::class.java]

        val plant = SharedPrefsUtils.getStringPreference(this, Config.KEY_PLANT, "")
        val storloc = SharedPrefsUtils.getStringPreference(this, Config.KEY_STOR_LOC, "")


        inspectionReturnVM.getInspectionReturnData(
            plant.toString(),
            storloc.toString(),
            "",
            "",
            "",
            "",
        )

        inspectionReturnVM.isLoading.observe(this) {
            Toast.makeText(this, "Memuat Data", Toast.LENGTH_SHORT).show()
        }

        inspectionReturnVM.inspectionReturnData.observe(this) { data ->
            adapter.setListinspeksi(data)
        }

        getATTBVM.getATTBData(
            plant.toString(),
            storloc.toString()
        )

        getATTBVM.attbData.observe(this) {
            attbData.clear()
            attbData.addAll(it)
            attbData.forEach() { attbDetail ->
                attbNameAndNumber.add("${attbDetail.materialNo} ${attbDetail.namaMaterial}")
            }

            val adapterATTB = ArrayAdapter(
                this,
                    android.R.layout.simple_dropdown_item_1line,
                attbNameAndNumber
            )

            binding.dropdownNamaMaterial.setAdapter(adapterATTB)

            binding.dropdownNamaMaterial.setOnItemClickListener { _, _, position, _ ->
                attbValue = attbData[position].materialNo!!
                inspectionReturnVM.getInspectionReturnData(
                    plant.toString(),
                    storloc.toString(),
                    inspectionDateValue,
                    binding.edtNoInspeksi.text.toString(),
                    attbValue,
                    statusInspeksiValue,
                )
                adapter.notifyDataSetChanged()
            }
        }

        binding.cvTanggalInspeksi.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val dateForParamFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    binding.txtTglMulai.text = dateFormat.format(calendar.time)
                    inspectionDateValue = dateForParamFormat.format(calendar.time)
                    inspectionReturnVM.getInspectionReturnData(
                        plant.toString(),
                        storloc.toString(),
                        inspectionDateValue,
                        binding.edtNoInspeksi.text.toString(),
                        attbValue,
                        statusInspeksiValue,
                    )
                    adapter.notifyDataSetChanged()
                    setRecyclerView()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePicker.show()
        }

        val statusInspeksi = listOf(
            "Semua",
            "Menunggu Cetak Barcode atau QRcode",
            "Belum Inspeksi",
            "Progress",
            "Selesai Inspeksi",
            "Cetak BA",
            "Material Teraktivasi, Diperbaiki atau Dihapus"
        )

        val adapterStatusInspeksi = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            statusInspeksi
        )

        binding.dropdownStatusInspeksi.setAdapter(adapterStatusInspeksi)

        binding.dropdownStatusInspeksi.setOnItemClickListener { _, _, position, _ ->
            statusInspeksiValue = if (position == 0) {
                ""
            } else {
                statusInspeksi[position]
            }
            inspectionReturnVM.getInspectionReturnData(
                plant.toString(),
                storloc.toString(),
                inspectionDateValue,
                binding.edtNoInspeksi.text.toString(),
                attbValue,
                statusInspeksiValue,
            )
            adapter.notifyDataSetChanged()
        }

        binding.edtNoInspeksi.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                inspectionReturnVM.getInspectionReturnData(
                    plant.toString(),
                    storloc.toString(),
                    inspectionDateValue,
                    s.toString(),
                    attbValue,
                    statusInspeksiValue,
                )
                adapter.notifyDataSetChanged()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }
        })

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setRecyclerView() {
        adapter = InspeksiMaterialAdapter()
        binding.rvInspeksiMaterial.layoutManager = LinearLayoutManager(this)
        binding.rvInspeksiMaterial.adapter = adapter
    }
}