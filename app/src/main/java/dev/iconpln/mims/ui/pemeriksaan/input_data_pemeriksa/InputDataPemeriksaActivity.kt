package dev.iconpln.mims.ui.pemeriksaan.input_data_pemeriksa

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.R
import dev.iconpln.mims.data.local.database_local.GenericReport
import dev.iconpln.mims.data.local.database_local.ReportParameter
import dev.iconpln.mims.data.local.database_local.ReportUploader
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivityInputDataPemeriksaBinding
import dev.iconpln.mims.tasks.Loadable
import dev.iconpln.mims.tasks.TambahReportTask
import dev.iconpln.mims.ui.pemeriksaan.PemeriksaanActivity
import dev.iconpln.mims.ui.penerimaan.detail_penerimaan.DetailPenerimaanDokumentasiAdapter
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.DateTimeUtils
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.joda.time.LocalDateTime

class InputDataPemeriksaActivity : BaseActivity(), Loadable {
    private lateinit var binding: ActivityInputDataPemeriksaBinding
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private lateinit var pemeriksaan: dev.iconpln.mims.data.local.database.TPemeriksaan
    private lateinit var listDokumentasi: List<String>
    private var noPem: String = ""
    private lateinit var ketua: dev.iconpln.mims.data.local.database.TPegawaiUp3
    private lateinit var manager: dev.iconpln.mims.data.local.database.TPegawaiUp3
    private lateinit var sekretaris: dev.iconpln.mims.data.local.database.TPegawaiUp3
    private lateinit var anggota: dev.iconpln.mims.data.local.database.TPegawaiUp3
    private var anggotaTambahan: dev.iconpln.mims.data.local.database.TPegawaiUp3? = null
    private lateinit var dialogLoading: Dialog
    private var plant = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputDataPemeriksaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!
        noPem = intent.getStringExtra("noPemeriksaan")!!
        dialogLoading = Helper.loadingDialog(this)
        plant = SharedPrefsUtils.getStringPreference(this@InputDataPemeriksaActivity, "plant", "")!!

        pemeriksaan = daoSession.tPemeriksaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDao.Properties.NoPemeriksaan.eq(
                    noPem
                )
            ).list()[0]

        getDokumentasi(pemeriksaan.noDoSmar)

        with(binding) {
            txtDeliveryOrder.text = pemeriksaan.noDoSmar
            txtNamaKurir.text = pemeriksaan.namaKurir
            txtPetugasPenerima.text = pemeriksaan.petugasPenerima
            txtKurirPengiriman.text = pemeriksaan.expeditions
            txtTglKirim.text = pemeriksaan.createdDate

            txtDokumentasi.setOnClickListener {
                val dialog = Dialog(this@InputDataPemeriksaActivity)
                dialog.setContentView(R.layout.popup_dokumentasi)
                dialog.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialog.setCancelable(false)
                dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown
                val adapter = DetailPenerimaanDokumentasiAdapter(arrayListOf())

                val recyclerView = dialog.findViewById<RecyclerView>(R.id.rv_dokumentasi)
                val btnClose = dialog.findViewById<ImageView>(R.id.btn_close)

                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(
                    this@InputDataPemeriksaActivity,
                    LinearLayoutManager.HORIZONTAL, false
                )
                recyclerView.setHasFixedSize(true)

                adapter.setData(listDokumentasi)

                btnClose.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show();
            }

            btnSimpan.setOnClickListener { validation() }
            btnBack.setOnClickListener { finish() }

            btnTambahAnggota.setOnClickListener {
                lblNewAnggota.visibility = View.VISIBLE
                edtAnggotaTambahan.visibility = View.VISIBLE
                ivDeleteAnggotaTambahan.visibility = View.VISIBLE
                btnTambahAnggota.visibility = View.GONE
            }

            ivDeleteAnggotaTambahan.setOnClickListener {
                lblNewAnggota.visibility = View.GONE
                edtAnggotaTambahan.visibility = View.GONE
                ivDeleteAnggotaTambahan.visibility = View.GONE
                btnTambahAnggota.visibility = View.VISIBLE
                anggotaTambahan = null
            }
        }

        setKetua()
        setManager()
        setSekretaris()
        setAnggota()
        setAnggotaTambahan()
    }

    private fun getDokumentasi(noDo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                ApiConfig.getApiService(this@InputDataPemeriksaActivity).getDokumentasi(noDo)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                        this@InputDataPemeriksaActivity,
                        Config.IS_LOGIN_SSO, 0
                    )
                    try {
                        if (response.body()?.status == Config.KEY_SUCCESS) {
                            listDokumentasi = response.body()?.doc?.array!!
                        } else {
                            when (response.body()?.message) {
                                Config.DO_LOGOUT -> {
                                    Helper.logout(this@InputDataPemeriksaActivity, isLoginSso)
                                    Toast.makeText(
                                        this@InputDataPemeriksaActivity,
                                        getString(R.string.session_kamu_telah_habis),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> Toast.makeText(
                                    this@InputDataPemeriksaActivity,
                                    response.body()?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@InputDataPemeriksaActivity,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        e.printStackTrace()
                    } finally {
                    }
                } else {
                    Toast.makeText(
                        this@InputDataPemeriksaActivity,
                        response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setAnggotaTambahan() {
        val anggotasTambahan = daoSession.tPegawaiUp3Dao
            .queryBuilder()
            .where(dev.iconpln.mims.data.local.database.TPegawaiUp3Dao.Properties.KodeJabatan.eq("60"))
            .where(dev.iconpln.mims.data.local.database.TPegawaiUp3Dao.Properties.Plant.eq(plant))
            .list()

        val names = anggotasTambahan.map { it.namaPegawai }

        if (!pemeriksaan.namaAnggotaBaru.isNullOrEmpty()) {
            with(binding) {
                lblNewAnggota.visibility = View.VISIBLE
                edtAnggotaTambahan.visibility = View.VISIBLE
                ivDeleteAnggotaTambahan.visibility = View.VISIBLE
                btnTambahAnggota.visibility = View.GONE

                dropdownAnggotaTambahan.setText(pemeriksaan.namaAnggotaBaru)
                anggotaTambahan =
                    anggotasTambahan.filter { it.namaPegawai == pemeriksaan.namaAnggotaBaru && it.kodeJabatan == "60" }[0]
            }
        }

        val adapterStatus = ArrayAdapter(
            this@InputDataPemeriksaActivity,
            android.R.layout.simple_dropdown_item_1line,
            names
        )
        binding.dropdownAnggotaTambahan.setAdapter(adapterStatus)
        binding.dropdownAnggotaTambahan.setOnItemClickListener { parent, view, position, id ->
            anggotaTambahan = anggotasTambahan[position]
        }
    }

    private fun setAnggota() {
        val anggotas = daoSession.tPegawaiUp3Dao
            .queryBuilder()
            .where(dev.iconpln.mims.data.local.database.TPegawaiUp3Dao.Properties.KodeJabatan.eq("60"))
            .where(dev.iconpln.mims.data.local.database.TPegawaiUp3Dao.Properties.Plant.eq(plant))
            .list()

        val names = anggotas.map { it.namaPegawai }

        if (!pemeriksaan.namaAnggota.isNullOrEmpty()) {
            binding.dropdownAnggota.setText(pemeriksaan.namaAnggota)
            anggota =
                anggotas.filter { it.namaPegawai == pemeriksaan.namaAnggota && it.kodeJabatan == "60" }[0]
        }

        val adapterStatus = ArrayAdapter(
            this@InputDataPemeriksaActivity,
            android.R.layout.simple_dropdown_item_1line,
            names
        )
        binding.dropdownAnggota.setAdapter(adapterStatus)
        binding.dropdownAnggota.setOnItemClickListener { parent, view, position, id ->
            anggota = anggotas[position]
        }
    }

    private fun setSekretaris() {
        val sekretariss = daoSession.tPegawaiUp3Dao
            .queryBuilder()
            .where(dev.iconpln.mims.data.local.database.TPegawaiUp3Dao.Properties.KodeJabatan.eq("30"))
            .where(dev.iconpln.mims.data.local.database.TPegawaiUp3Dao.Properties.Plant.eq(plant))
            .where(dev.iconpln.mims.data.local.database.TPegawaiUp3Dao.Properties.IsActive.eq(true))
            .list()

        val names = sekretariss.map { it.namaPegawai }

        if (!pemeriksaan.namaSekretaris.isNullOrEmpty()) {
            binding.dropdownSekretaris.setText(pemeriksaan.namaSekretaris)
            sekretaris =
                sekretariss.filter { it.namaPegawai == pemeriksaan.namaSekretaris && it.kodeJabatan == "30" }[0]
        }

        val adapterStatus = ArrayAdapter(
            this@InputDataPemeriksaActivity,
            android.R.layout.simple_dropdown_item_1line,
            names
        )
        binding.dropdownSekretaris.setAdapter(adapterStatus)
        binding.dropdownSekretaris.setOnItemClickListener { parent, view, position, id ->
            sekretaris = sekretariss[position]
        }
    }

    private fun setManager() {
        val managers = daoSession.tPegawaiUp3Dao
            .queryBuilder()
            .where(dev.iconpln.mims.data.local.database.TPegawaiUp3Dao.Properties.KodeJabatan.eq("10"))
            .where(dev.iconpln.mims.data.local.database.TPegawaiUp3Dao.Properties.Plant.eq(plant))
            .where(dev.iconpln.mims.data.local.database.TPegawaiUp3Dao.Properties.IsActive.eq(true))
            .list()

        val names = managers.map { it.namaPegawai }

        if (!pemeriksaan.namaManager.isNullOrEmpty()) {
            binding.dropdownManager.setText(pemeriksaan.namaManager)
            manager =
                managers.filter { it.namaPegawai == pemeriksaan.namaManager && it.kodeJabatan == "10" }[0]
        }

        val adapterStatus = ArrayAdapter(
            this@InputDataPemeriksaActivity,
            android.R.layout.simple_dropdown_item_1line,
            names
        )
        binding.dropdownManager.setAdapter(adapterStatus)
        binding.dropdownManager.setOnItemClickListener { parent, view, position, id ->
            manager = managers[position]
        }

    }

    private fun setKetua() {
        val ketuas = daoSession.tPegawaiUp3Dao
            .queryBuilder()
            .where(dev.iconpln.mims.data.local.database.TPegawaiUp3Dao.Properties.KodeJabatan.eq("20"))
            .where(dev.iconpln.mims.data.local.database.TPegawaiUp3Dao.Properties.Plant.eq(plant))
            .where(dev.iconpln.mims.data.local.database.TPegawaiUp3Dao.Properties.IsActive.eq(true))
            .list()

        val names = ketuas.map { it.namaPegawai }

        if (!pemeriksaan.namaKetua.isNullOrEmpty()) {
            binding.btnSimpan.text = "Edit"
            binding.dropdownKetua.setText(pemeriksaan.namaKetua)
            ketua =
                ketuas.filter { it.namaPegawai == pemeriksaan.namaKetua && it.kodeJabatan == "20" }[0]
        }

        val adapterStatus = ArrayAdapter(
            this@InputDataPemeriksaActivity,
            android.R.layout.simple_dropdown_item_1line,
            names
        )
        binding.dropdownKetua.setAdapter(adapterStatus)
        binding.dropdownKetua.setOnItemClickListener { parent, view, position, id ->
            ketua = ketuas[position]
        }
    }

    private fun validation() {
        with(binding) {
            val namaManager = manager.namaPegawai
            val namaAnggota = anggota.namaPegawai
            val namaKetua = ketua.namaPegawai
            val namaSekretaris = sekretaris.namaPegawai

            if (namaManager.isNullOrEmpty()) {
                Toast.makeText(
                    this@InputDataPemeriksaActivity,
                    "Pastikan anda mengisi semua data yang akan di kirim",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (namaAnggota.isNullOrEmpty()) {
                Toast.makeText(
                    this@InputDataPemeriksaActivity,
                    "Pastikan anda mengisi semua data yang akan di kirim",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (namaKetua.isNullOrEmpty()) {
                Toast.makeText(
                    this@InputDataPemeriksaActivity,
                    "Pastikan anda mengisi semua data yang akan di kirim",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (namaSekretaris.isNullOrEmpty()) {
                Toast.makeText(
                    this@InputDataPemeriksaActivity,
                    "Pastikan anda mengisi semua data yang akan di kirim",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (anggotaTambahan == null) {

                showDialogWithoutAnggotaTambahan()

            } else {

                if (anggotaTambahan!!.namaPegawai == namaAnggota) {
                    Toast.makeText(
                        this@InputDataPemeriksaActivity,
                        "Anggota dan anggota tamabahan tidak boleh sama",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                if (anggotaTambahan!!.namaPegawai.isNullOrEmpty()) {
                    Toast.makeText(
                        this@InputDataPemeriksaActivity,
                        "Pastikan anda mengisi semua data yang akan di kirim",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }

                showDialog()
            }
        }
    }

    private fun showDialog() {
        val dialog = Dialog(this@InputDataPemeriksaActivity)
        dialog.setContentView(R.layout.popup_penerimaan)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown
        val btnOk = dialog.findViewById(R.id.btn_ok) as AppCompatButton

        btnOk.setOnClickListener {
            dialog.dismiss()
            dialogLoading.show()
            submitForm()
        }

        dialog.show();
    }

    private fun showDialogWithoutAnggotaTambahan() {
        val dialog = Dialog(this@InputDataPemeriksaActivity)
        dialog.setContentView(R.layout.popup_penerimaan)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown
        val btnOk = dialog.findViewById(R.id.btn_ok) as AppCompatButton

        btnOk.setOnClickListener {
            dialog.dismiss()
            dialogLoading.show()
            submitFormWithoutAnggotaTambahan()
        }

        dialog.show();
    }

    private fun submitFormWithoutAnggotaTambahan() {
        dialogLoading.show()
        pemeriksaan.namaManager = manager.namaPegawai
        pemeriksaan.namaAnggota = anggota.namaPegawai
        pemeriksaan.namaKetua = ketua.namaPegawai
        pemeriksaan.namaSekretaris = sekretaris.namaPegawai
        pemeriksaan.namaAnggotaBaru = ""
        daoSession.tPemeriksaanDao.update(pemeriksaan)

        val reports = ArrayList<GenericReport>()
        val currentDate = LocalDateTime.now().toString(Config.DATE)
        val currentDateTime = LocalDateTime.now().toString(Config.DATETIME)
        val currentUtc = DateTimeUtils.currentUtc

        //region Add report visit to queue
        var jwtWeb =
            SharedPrefsUtils.getStringPreference(this@InputDataPemeriksaActivity, "jwtWeb", "")
        var jwtMobile =
            SharedPrefsUtils.getStringPreference(this@InputDataPemeriksaActivity, "jwt", "")
        var jwt = "$jwtMobile;$jwtWeb"
        var username = SharedPrefsUtils.getStringPreference(
            this@InputDataPemeriksaActivity,
            "username",
            "14.Hexing_Electrical"
        )
        var plant =
            SharedPrefsUtils.getStringPreference(this@InputDataPemeriksaActivity, "plant", "")
        val reportId =
            "temp_pemeriksaan" + username + "_" + pemeriksaan.noDoSmar + "_" + DateTime.now()
                .toString(
                    Config.DATETIME
                )
        val reportName = "Update Data Petugas Pemeriksaan Dengan Anggota Tambahan"
        val reportDescription = "$reportName: " + " (" + reportId + ")"
        val params = ArrayList<ReportParameter>()
        params.add(
            ReportParameter(
                "1",
                reportId,
                "no_do_smar",
                pemeriksaan.noDoSmar,
                ReportParameter.TEXT
            )
        )
        params.add(ReportParameter("2", reportId, "ketua", ketua.namaPegawai, ReportParameter.TEXT))
        params.add(
            ReportParameter(
                "3",
                reportId,
                "jabatan_ketua",
                ketua.namaJabatan,
                ReportParameter.TEXT
            )
        )
        params.add(ReportParameter("4", reportId, "nip_ketua", ketua.nip, ReportParameter.TEXT))
        params.add(
            ReportParameter(
                "5",
                reportId,
                "kd_jabatan_ketua",
                ketua.kodeJabatan,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "6",
                reportId,
                "manager",
                manager.namaPegawai,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "7",
                reportId,
                "jabatan_manager",
                manager.namaJabatan,
                ReportParameter.TEXT
            )
        )
        params.add(ReportParameter("8", reportId, "nip_manager", manager.nip, ReportParameter.TEXT))
        params.add(
            ReportParameter(
                "9",
                reportId,
                "kd_jabatan_manager",
                manager.kodeJabatan,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "10",
                reportId,
                "sekretaris",
                sekretaris.namaPegawai,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "11",
                reportId,
                "jabatan_sekretaris",
                sekretaris.namaJabatan,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "12",
                reportId,
                "nip_sekretaris",
                sekretaris.nip,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "13",
                reportId,
                "kd_jabatan_sekretaris",
                sekretaris.kodeJabatan,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "14",
                reportId,
                "anggota",
                anggota.namaPegawai,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "15",
                reportId,
                "jabatan_anggota",
                anggota.namaJabatan,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "16",
                reportId,
                "nip_anggota",
                anggota.nip,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "17",
                reportId,
                "kd_jabatan_anggota",
                anggota.kodeJabatan,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "18",
                reportId,
                "no_pemeriksaan",
                pemeriksaan.noPemeriksaan,
                ReportParameter.TEXT
            )
        )
        params.add(ReportParameter("19", reportId, "plantation", plant!!, ReportParameter.TEXT))

        val report = GenericReport(
            reportId,
            jwt!!,
            reportName,
            reportDescription,
            ApiConfig.sendPemeriksaanPerson(),
            currentDate,
            Config.NO_CODE,
            currentUtc,
            params
        )
        reports.add(report)
        //endregion

        val task = TambahReportTask(this, reports)
        task.execute()

        val iService = Intent(applicationContext, ReportUploader::class.java)
        startService(iService)
    }

    private fun submitForm() {
        dialogLoading.show()
        pemeriksaan.namaManager = manager.namaPegawai
        pemeriksaan.namaAnggota = anggota.namaPegawai
        pemeriksaan.namaKetua = ketua.namaPegawai
        pemeriksaan.namaSekretaris = sekretaris.namaPegawai
        pemeriksaan.namaAnggotaBaru = anggotaTambahan?.namaPegawai
        daoSession.tPemeriksaanDao.update(pemeriksaan)

        val reports = ArrayList<GenericReport>()
        val currentDate = LocalDateTime.now().toString(Config.DATE)
        val currentDateTime = LocalDateTime.now().toString(Config.DATETIME)
        val currentUtc = DateTimeUtils.currentUtc

        //region Add report visit to queue
        var jwtWeb =
            SharedPrefsUtils.getStringPreference(this@InputDataPemeriksaActivity, "jwtWeb", "")
        var jwtMobile =
            SharedPrefsUtils.getStringPreference(this@InputDataPemeriksaActivity, "jwt", "")
        var jwt = "$jwtMobile;$jwtWeb"
        var username = SharedPrefsUtils.getStringPreference(
            this@InputDataPemeriksaActivity,
            "username",
            "14.Hexing_Electrical"
        )
        var plant =
            SharedPrefsUtils.getStringPreference(this@InputDataPemeriksaActivity, "plant", "")
        val reportId =
            "temp_pemeriksaan" + username + "_" + pemeriksaan.noDoSmar + "_" + DateTime.now()
                .toString(
                    Config.DATETIME
                )
        val reportName = "Update Data Petugas Pemeriksaan Dengan Anggota Tambahan"
        val reportDescription = "$reportName: " + " (" + reportId + ")"
        val params = ArrayList<ReportParameter>()
        params.add(
            ReportParameter(
                "1",
                reportId,
                "no_do_smar",
                pemeriksaan.noDoSmar,
                ReportParameter.TEXT
            )
        )
        params.add(ReportParameter("2", reportId, "ketua", ketua.namaPegawai, ReportParameter.TEXT))
        params.add(
            ReportParameter(
                "3",
                reportId,
                "jabatan_ketua",
                ketua.namaJabatan,
                ReportParameter.TEXT
            )
        )
        params.add(ReportParameter("4", reportId, "nip_ketua", ketua.nip, ReportParameter.TEXT))
        params.add(
            ReportParameter(
                "5",
                reportId,
                "kd_jabatan_ketua",
                ketua.kodeJabatan,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "6",
                reportId,
                "manager",
                manager.namaPegawai,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "7",
                reportId,
                "jabatan_manager",
                manager.namaJabatan,
                ReportParameter.TEXT
            )
        )
        params.add(ReportParameter("8", reportId, "nip_manager", manager.nip, ReportParameter.TEXT))
        params.add(
            ReportParameter(
                "9",
                reportId,
                "kd_jabatan_manager",
                manager.kodeJabatan,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "10",
                reportId,
                "sekretaris",
                sekretaris.namaPegawai,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "11",
                reportId,
                "jabatan_sekretaris",
                sekretaris.namaJabatan,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "12",
                reportId,
                "nip_sekretaris",
                sekretaris.nip,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "13",
                reportId,
                "kd_jabatan_sekretaris",
                sekretaris.kodeJabatan,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "14",
                reportId,
                "anggota",
                anggota.namaPegawai,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "15",
                reportId,
                "jabatan_anggota",
                anggota.namaJabatan,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "16",
                reportId,
                "nip_anggota",
                anggota.nip,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "17",
                reportId,
                "kd_jabatan_anggota",
                anggota.kodeJabatan,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "18",
                reportId,
                "anggota_tambahan",
                anggotaTambahan?.namaPegawai!!,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "19",
                reportId,
                "jabatan_anggota_tambahan",
                anggotaTambahan?.namaJabatan!!,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "20",
                reportId,
                "nip_anggota_tambahan",
                anggotaTambahan?.nip!!,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "21",
                reportId,
                "kd_jabatan_anggota_tambahan",
                anggotaTambahan?.kodeJabatan!!,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "22",
                reportId,
                "no_pemeriksaan",
                pemeriksaan.noPemeriksaan,
                ReportParameter.TEXT
            )
        )
        params.add(ReportParameter("23", reportId, "plantation", plant!!, ReportParameter.TEXT))

        val report = GenericReport(
            reportId,
            jwt!!,
            reportName,
            reportDescription,
            ApiConfig.sendPemeriksaanPerson(),
            currentDate,
            Config.NO_CODE,
            currentUtc,
            params
        )
        reports.add(report)
        //endregion

        val task = TambahReportTask(this, reports)
        task.execute()

        val iService = Intent(applicationContext, ReportUploader::class.java)
        startService(iService)

    }

    override fun setLoading(show: Boolean, title: String, message: String) {}

    override fun setFinish(result: Boolean, message: String) {
        if (result) {
        }
        startActivity(
            Intent(this@InputDataPemeriksaActivity, PemeriksaanActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
        dialogLoading.dismiss()
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}