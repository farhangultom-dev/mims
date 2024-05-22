package dev.iconpln.mims.ui.inspeksi_material.tim_inspeksi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.data.remote.response.AGODetailInspectionTeamData
import dev.iconpln.mims.data.remote.response.AGOMasterTimDetailData
import dev.iconpln.mims.data.remote.response.AGOUpdateInspectionReturnTeam
import dev.iconpln.mims.data.remote.response.AGOUpdateInspectionReturnTeamData
import dev.iconpln.mims.data.remote.response.AGOUpdateInspectionReturnTeamDataBody
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivityTimInspeksiBinding
import dev.iconpln.mims.ui.inspeksi_material.AGOGetDataInspectionReturnTeamViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOGetMasterTimViewModel
import dev.iconpln.mims.ui.inspeksi_material.AGOUpdateInspectionTeamViewModel
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.SharedPrefsUtils
import dev.iconpln.mims.utils.ViewModelFactory

class TimInspeksiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTimInspeksiBinding
    private lateinit var adapter: TimInspeksiAdapter

    private lateinit var timInspeksiVM: AGOGetDataInspectionReturnTeamViewModel
    private lateinit var masterTimInspeksiVM: AGOGetMasterTimViewModel
    private lateinit var updateTimInspeksiVM: AGOUpdateInspectionTeamViewModel

    private var timInspeksiData = arrayListOf<AGODetailInspectionTeamData>()
    private var masterTimInspeksiData = arrayListOf<AGOMasterTimDetailData>()
    private var listTeamName = arrayListOf<String>()
    private var listTeamNIP = arrayListOf<String>()
    private var listTeamEmail = arrayListOf<String>()
    private var jabatanValue = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimInspeksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRecyclerView()

        val apiService = ApiConfig.getApiService(this)


        val username = SharedPrefsUtils.getStringPreference(this, Config.KEY_USERNAME, "")
        val plant = SharedPrefsUtils.getStringPreference(this, Config.KEY_PLANT, "")
        val storLoc = SharedPrefsUtils.getStringPreference(this, Config.KEY_STOR_LOC, "")

        timInspeksiVM = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[AGOGetDataInspectionReturnTeamViewModel::class.java]

        masterTimInspeksiVM = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[AGOGetMasterTimViewModel::class.java]

        updateTimInspeksiVM = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[AGOUpdateInspectionTeamViewModel::class.java]

        timInspeksiVM.getInspectionReturnTeam(
            intent.getStringExtra(EXTRA_NO_INSPEKSI) ?: ""
        )

        masterTimInspeksiVM.getMasterTim(
            plant.toString(),
            storLoc.toString()
        )

        binding.edtNoInspeksi.setText(intent.getStringExtra(EXTRA_NO_INSPEKSI) ?: "")

        timInspeksiVM.inspectionReturnTeamData.observe(this) {
            timInspeksiData = it as ArrayList<AGODetailInspectionTeamData>
            adapter.setListTimInspeksi(timInspeksiData)
        }

        val daftarJabatan = listOf(
            "Ketua",
            "Anggota",
        )

        val adapterJabatan = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            daftarJabatan
        )

        binding.dropdownJabatan.setAdapter(adapterJabatan)

        binding.dropdownJabatan.setOnItemClickListener { _, _, position, _ ->
            jabatanValue = daftarJabatan[position]
        }

        binding.edtNama.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                masterTimInspeksiVM.getMasterTim(
                    plant.toString(),
                    storLoc.toString()
                )

                refreshMasterTimNamaData()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        val adapterNamaTim = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            listTeamName
        )

        binding.edtNama.setAdapter(adapterNamaTim)

        binding.edtNama.setOnItemClickListener { _, _, position, _ ->
            val selectedTeam = masterTimInspeksiData[position]
            binding.edtNip.setText(selectedTeam.nip)
            binding.edtNama.setText(selectedTeam.nama)
            binding.edtEmail.setText(selectedTeam.email)
            binding.dropdownJabatan.setText(selectedTeam.jabatan, false)
            if (selectedTeam.jabatan == "Ketua") {
                jabatanValue = "Ketua"
            } else {
                jabatanValue = "Anggota"
            }
        }


        binding.edtNip.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                masterTimInspeksiVM.getMasterTim(
                    plant.toString(),
                    storLoc.toString()
                )

                refreshMasterTimNIPData()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        val adapterNipTim = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            listTeamNIP
        )

        binding.edtNip.setAdapter(adapterNipTim)

        binding.edtNip.setOnItemClickListener { _, _, position, _ ->
            val selectedTeam = masterTimInspeksiData[position]
            binding.edtNip.setText(selectedTeam.nip)
            binding.edtNama.setText(selectedTeam.nama)
            binding.edtEmail.setText(selectedTeam.email)
            binding.dropdownJabatan.setText(selectedTeam.jabatan, false)
            jabatanValue = if (selectedTeam.jabatan == "Ketua") {
                "Ketua"
            } else {
                "Anggota"
            }
        }

        binding.edtEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                masterTimInspeksiVM.getMasterTim(
                    plant.toString(),
                    storLoc.toString()
                )

                refreshMasterTimEmailData()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        val adapterEmailTim = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            listTeamEmail
        )

        binding.edtEmail.setAdapter(adapterEmailTim)

        binding.edtEmail.setOnItemClickListener { _, _, position, _ ->
            val selectedTeam = masterTimInspeksiData[position]
            binding.edtNip.setText(selectedTeam.nip)
            binding.edtNama.setText(selectedTeam.nama)
            binding.edtEmail.setText(selectedTeam.email)
            binding.dropdownJabatan.setText(selectedTeam.jabatan, false)
            jabatanValue = if (selectedTeam.jabatan == "Ketua") {
                "Ketua"
            } else {
                "Anggota"
            }
        }

        binding.btnAddAnggota.setOnClickListener{
            addInspectionTeam(
                AGODetailInspectionTeamData(
                    binding.edtNoInspeksi.text.toString(),
                    username,
                    binding.edtNip.text.toString(),
                    binding.edtNama.text.toString(),
                    jabatanValue,
                    binding.edtEmail.text.toString()
                )
            )
        }

        binding.btnKirim.setOnClickListener{
            val inspectionTeamListForUpdate = timInspeksiData.map {
                AGOUpdateInspectionReturnTeamData(
                    it.nip,
                    it.nama,
                    it.jabatan,
                    it.email
                )
            }
            val bodyRequest = AGOUpdateInspectionReturnTeamDataBody(
                binding.edtNoInspeksi.text.toString(),
                username,
                inspectionTeamListForUpdate
            )
            updateTimInspeksiVM.updateInspectionReturnTeam(
                bodyRequest
            )
        }

        updateTimInspeksiVM.isLoading.observe(this) {
            Toast.makeText(this, "Mengirim Data Tim", Toast.LENGTH_SHORT).show()
        }

        updateTimInspeksiVM.updateInspectionTeamResponse.observe(this) {
            val response = it as AGOUpdateInspectionReturnTeam
            if (response.msg == "Tambah Tim Inspeksi Berhasil") {
                Toast.makeText(this, "Berhasil Memperbarui Data Tim", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal Memperbarui Data Tim", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setRecyclerView() {
        adapter = TimInspeksiAdapter()
        binding.rvTimInspeksi.layoutManager = LinearLayoutManager(this)
        binding.rvTimInspeksi.adapter = adapter
    }

    private fun refreshMasterTimNamaData() {
        masterTimInspeksiData.clear()
        listTeamName.clear()
        masterTimInspeksiVM.masterTimData.observe(this) {
            masterTimInspeksiData.addAll(it)
            masterTimInspeksiData.forEach { masterTimDetail ->
                listTeamName.add(masterTimDetail.nama.toString())
            }
        }
    }

    private fun refreshMasterTimNIPData() {
        masterTimInspeksiData.clear()
        listTeamNIP.clear()
        masterTimInspeksiVM.masterTimData.observe(this) {
            masterTimInspeksiData.addAll(it)
            masterTimInspeksiData.forEach { masterTimDetail ->
                listTeamNIP.add(masterTimDetail.nip.toString())
            }
        }
    }

    private fun refreshMasterTimEmailData() {
        masterTimInspeksiData.clear()
        listTeamEmail.clear()
        masterTimInspeksiVM.masterTimData.observe(this) {
            masterTimInspeksiData.addAll(it)
            masterTimInspeksiData.forEach { masterTimDetail ->
                listTeamEmail.add(masterTimDetail.email.toString())
            }
        }
    }

    private fun addInspectionTeam(data: AGODetailInspectionTeamData) {

        val emptyNipMessage = "Mohon Isi NIP Tim Inspeksi"
        val emptyNamaMessage = "Mohon Isi Nama Tim Inspeksi"
        val emptyJabatanMessage = "Mohon Pilih Jabatan"
        val emptyEmailMessage = "Mohon Isi Email Tim Inspeksi"
        val invalidEmailFormat = "Format Email Tidak Valid"

        if (data.nip.isNullOrEmpty()) {
            binding.edtNip.error = emptyNipMessage
            Toast.makeText(this, emptyNipMessage, Toast.LENGTH_SHORT).show()
            return
        } else {
            binding.edtNip.error = null
        }

        if (data.nama.isNullOrEmpty()) {
            Toast.makeText(this, emptyNamaMessage, Toast.LENGTH_SHORT).show()
            return
        } else {
            binding.edtNama.error = null
        }

        if (data.jabatan.isNullOrEmpty()) {
            Toast.makeText(this, emptyJabatanMessage, Toast.LENGTH_SHORT).show()
            return
        } else {
            binding.dropdownJabatan.error = null
        }

        if (data.email.isNullOrEmpty()) {
            Toast.makeText(this, emptyEmailMessage, Toast.LENGTH_SHORT).show()
            return
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(data.email).matches()) {
            Toast.makeText(this, invalidEmailFormat, Toast.LENGTH_SHORT).show()
            return
        } else {
            binding.edtEmail.error = null
        }

        val existingTeam = timInspeksiData.find {
            it.nip == data.nip || it.email == data.email || it.nama == data.nama
        }

        if (existingTeam != null) {
            Toast.makeText(this, "Sudah Terdaftar", Toast.LENGTH_SHORT).show()
        } else {
            timInspeksiData.add(data)
            adapter.setListTimInspeksi(timInspeksiData)
            adapter.notifyItemInserted(timInspeksiData.size)

            binding.edtNip.setText("")
            binding.edtNama.setText("")
            binding.edtEmail.setText("")
            binding.dropdownJabatan.setText("Pilih Jabatan", false)
        }
    }

    fun deleteInspectionTeam(position: Int) {
        timInspeksiData.removeAt(position)
        adapter.setListTimInspeksi(timInspeksiData)
        adapter.notifyItemMoved(position, timInspeksiData.size)
    }


    companion object {
        const val EXTRA_NO_INSPEKSI = "extra_no_inspeksi"
    }
}