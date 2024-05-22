package dev.iconpln.mims.ui.monitoring_permintaan.monitoring_permintaan_detail

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.R
import dev.iconpln.mims.data.local.database_local.GenericReport
import dev.iconpln.mims.data.local.database_local.ReportParameter
import dev.iconpln.mims.data.local.database_local.ReportUploader
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivityMonitoringPermintaanDetailBinding
import dev.iconpln.mims.tasks.Loadable
import dev.iconpln.mims.tasks.TambahReportTask
import dev.iconpln.mims.ui.monitoring_permintaan.MonitoringPermintaanActivity
import dev.iconpln.mims.ui.monitoring_permintaan.MonitoringPermintaanViewModel
import dev.iconpln.mims.ui.monitoring_permintaan.input_sn_monitoring.InputSnMonitoringPermintaanActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.DateTimeUtils
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import org.joda.time.DateTime
import org.joda.time.LocalDateTime

class MonitoringPermintaanDetailActivity : BaseActivity(), Loadable {
    private lateinit var binding: ActivityMonitoringPermintaanDetailBinding
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private val viewModel: MonitoringPermintaanViewModel by viewModels()
    private lateinit var adapter: MonitoringPermintaanDetailAdapter
    private lateinit var dialogLoading: Dialog
    private var noPermintaan: String = ""
    private var noTransaksi: String = ""
    private var srcNoMaterialTxt: String = ""
    private lateinit var monitoringPermintaan: dev.iconpln.mims.data.local.database.TTransMonitoringPermintaan
    private var progressDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitoringPermintaanDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!

        noPermintaan = intent.getStringExtra("noPermintaan")!!
        noTransaksi = intent.getStringExtra("noTransaksi")!!

        dialogLoading = Helper.loadingDialog(this)

        monitoringPermintaan = daoSession.tTransMonitoringPermintaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDao.Properties.NoPermintaan.eq(
                    noPermintaan
                )
            ).list()[0]

        viewModel.getMonitoringPermintaanDetail(daoSession, noTransaksi)

        adapter = MonitoringPermintaanDetailAdapter(
            arrayListOf(),
            object : MonitoringPermintaanDetailAdapter.OnAdapterListener {
                override fun onClick(mpd: dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetail) {
                    if (!mpd.isActive) {//mpd.isDone == 1){
                        Toast.makeText(
                            this@MonitoringPermintaanDetailActivity,
                            "Material tidak dapat di scan karena merupakan material non mims",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        startActivity(
                            Intent(
                                this@MonitoringPermintaanDetailActivity,
                                InputSnMonitoringPermintaanActivity::class.java
                            )
                                .putExtra("noPermintaan", mpd.noPermintaan)
                                .putExtra("noMat", mpd.nomorMaterial)
                                .putExtra("desc", mpd.materialDesc)
                                .putExtra("kategori", mpd.kategori)
                                .putExtra("noRepackaging", mpd.noRepackaging)
                                .putExtra("noTransaksi", mpd.noTransaksi)
                                .putExtra("qtyPermintaan", mpd.qtyPermintaan)
                                .putExtra("valuationType", mpd.valuationType)
                        )
                    }
                }

            },
            daoSession
        )

        viewModel.monitoringPermintaanDetailResponse.observe(this) {
            adapter.setMpList(it)
            binding.totalData.text = "Total : ${it.size} Data"
        }

        with(binding) {
            btnBack.setOnClickListener { onBackPressed() }

            if (monitoringPermintaan.kodePengeluaran == "2") {
                btnSimpan.isEnabled = false
                btnSimpan.setBackgroundColor(Color.GRAY)
            }

            btnSimpan.setOnClickListener {
                validate()
            }

            txtNoPermintaan.text = noPermintaan
            txtNoRepackaging.text = monitoringPermintaan.noRepackaging
            txtGudangTujuan.text = monitoringPermintaan.storLocTujuanName

            rvMonitoringPermintaanDetail.adapter = adapter
            rvMonitoringPermintaanDetail.layoutManager = LinearLayoutManager(
                this@MonitoringPermintaanDetailActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            rvMonitoringPermintaanDetail.setHasFixedSize(true)

            srcNoMaterial.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    srcNoMaterialTxt = s.toString()
                    viewModel.searchDetail(daoSession, srcNoMaterialTxt, noTransaksi)
                }

            })
        }

    }

    private fun validate() {
        val jumlahKardus = adapter.listQuantity.sum().toString()

        val listMonitoringDetail = daoSession.tTransMonitoringPermintaanDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetailDao.Properties.NoTransaksi.eq(
                    noTransaksi
                )
            ).list()

        if (jumlahKardus.isNullOrEmpty()) {
            Toast.makeText(
                this@MonitoringPermintaanDetailActivity,
                "repackaging atau jumlah kardus tidak boleh kosong",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        for (i in listMonitoringDetail) {
            if (i.qtyPermintaan.toInt() != i.qtyScan.toInt()) {
                Toast.makeText(
                    this@MonitoringPermintaanDetailActivity,
                    "Kuantitas scan harus sama dengan kuantitas yang dibutuhkan",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        val dialog = Dialog(this@MonitoringPermintaanDetailActivity)
        dialog.setContentView(R.layout.popup_validation)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown
        val icon = dialog.findViewById(R.id.imageView11) as ImageView
        val btnOk = dialog.findViewById(R.id.btn_ya) as AppCompatButton
        val btnTidak = dialog.findViewById(R.id.btn_tidak) as AppCompatButton
        val message = dialog.findViewById(R.id.message) as TextView
        val txtMessage = dialog.findViewById(R.id.txt_message) as TextView

        icon.setImageResource(R.drawable.ic_doc_diterima)
        message.text = "Kirim Material"
        txtMessage.text = "Apakah anda yakin mengirim material??"



        btnTidak.setOnClickListener {
            dialog.dismiss()
        }

        btnOk.setOnClickListener {
            dialog.dismiss();
            dialogLoading.show()
            submitForm(jumlahKardus)
        }
        dialog.show();
    }

    private fun submitForm(jumlahKardus: String) {
        var materials = ""

        val listIsDone = daoSession.tTransMonitoringPermintaanDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetailDao.Properties.NoTransaksi.eq(
                    noTransaksi
                )
            ).list()

        listIsDone.forEachIndexed { index, i ->
            materials += "${i.nomorMaterial},${i.qtyScan},${adapter.eachQty[index]},${i.valuationType}"
            materials += ";"
        }
//        for (i in listIsDone) {
//            materials += "${i.nomorMaterial},${i.qtyScan},${adapter.eachQty},${i.valuationType}"
//            materials += ";"
//                    Log.d("MonitoringPermintaanDetailActivity", "submitForm: ${adapter.eachQty} ${i.nomorMaterial} ${i.qtyScan} ${i.valuationType}")
//        }
//        Log.d("MonitoringPermintaanDetailActivity", "submitForm1: ${materials}")

        if (materials != "") {
            materials = materials.substring(0, materials.length - 1)
        }

        val permintaans = daoSession.tTransMonitoringPermintaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDao.Properties.NoTransaksi.eq(
                    noTransaksi
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDao.Properties.NoPermintaan.eq(
                    noPermintaan
                )
            ).list()

        for (i in permintaans) {
            i.kodePengeluaran = "2"
            daoSession.tTransMonitoringPermintaanDao.update(i)
        }

        val reports = ArrayList<GenericReport>()
        val currentDate = LocalDateTime.now().toString(Config.DATE)
        val currentDateTime = LocalDateTime.now().toString(Config.DATETIME)
        val currentUtc = DateTimeUtils.currentUtc

        //region Add report visit to queue
        var jwtWeb = SharedPrefsUtils.getStringPreference(
            this@MonitoringPermintaanDetailActivity,
            "jwtWeb",
            ""
        )
        var jwtMobile =
            SharedPrefsUtils.getStringPreference(this@MonitoringPermintaanDetailActivity, "jwt", "")
        var jwt = "$jwtMobile;$jwtWeb"
        var username = SharedPrefsUtils.getStringPreference(
            this@MonitoringPermintaanDetailActivity,
            "username",
            "14.Hexing_Electrical"
        )
        val reportId =
            "temp_permintaan" + username + "_" + noPermintaan + "_" + DateTime.now().toString(
                Config.DATETIME
            )
        val reportName = "Update Data Permintaan"
        val reportDescription = "$reportName: " + " (" + reportId + ")"
        val params = ArrayList<ReportParameter>()

        params.add(
            ReportParameter(
                "1",
                reportId,
                "no_transaksi",
                noTransaksi,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "2",
                reportId,
                "jumlah_kardus",
                jumlahKardus,
                ReportParameter.TEXT
            )
        )
        params.add(ReportParameter("3", reportId, "materials", materials, ReportParameter.TEXT))

        val report = GenericReport(
            reportId,
            jwt!!,
            reportName,
            reportDescription,
            ApiConfig.sendMonkitoringPermintaan(),
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

    override fun setLoading(show: Boolean, title: String, message: String) {
    }

    override fun setFinish(result: Boolean, message: String) {
        Toast.makeText(this@MonitoringPermintaanDetailActivity, message, Toast.LENGTH_SHORT).show()
        startActivity(
            Intent(
                this@MonitoringPermintaanDetailActivity,
                MonitoringPermintaanActivity::class.java
            )
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
        dialogLoading.dismiss()
    }


}