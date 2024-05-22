package dev.iconpln.mims.ui.monitoring_complaint.detail_monitoring_complaint

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.R
import dev.iconpln.mims.data.local.database_local.GenericReport
import dev.iconpln.mims.data.local.database_local.ReportParameter
import dev.iconpln.mims.data.local.database_local.ReportUploader
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.data.scan.CustomScanActivity
import dev.iconpln.mims.data.scan.CustomScanBarcodeActivity
import dev.iconpln.mims.databinding.ActivityMonitoringComplaintDetailBinding
import dev.iconpln.mims.tasks.Loadable
import dev.iconpln.mims.tasks.TambahReportTask
import dev.iconpln.mims.ui.monitoring_complaint.MonitoringComplaintActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.DateTimeUtils
import dev.iconpln.mims.utils.SharedPrefsUtils
import org.joda.time.DateTime
import org.joda.time.LocalDateTime

class MonitoringComplaintDetailActivity : BaseActivity(), Loadable {
    private lateinit var binding: ActivityMonitoringComplaintDetailBinding
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private lateinit var adapter: MonitoringComplaintDetailAdapter
    private lateinit var listComplaint: List<dev.iconpln.mims.data.local.database.TMonitoringComplaintDetail>
    private var noDo = ""
    private var noKomplain = ""
    private var status = ""
    private var alasan = ""
    private var totalCacat = 0
    private var totalNormal = 0
    private var subrole = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitoringComplaintDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!

        noDo = intent.getStringExtra("noDo")!!
        noKomplain = intent.getStringExtra("noKomplain")!!
        subrole = SharedPrefsUtils.getIntegerPreference(this, "subroleId", 0)
        status = intent.getStringExtra("status")!!
        alasan = intent.getStringExtra("alasan")!!

        if (subrole == 3) {
            listComplaint = daoSession.tMonitoringComplaintDetailDao.queryBuilder()
                .where(
                    dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.NoKomplain.eq(
                        noKomplain
                    )
                )
                .list()
        } else {
            listComplaint = daoSession.tMonitoringComplaintDetailDao.queryBuilder()
                .where(
                    dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.NoKomplain.eq(
                        noKomplain
                    )
                )
                .where(
                    dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.Status.eq(
                        "SEDANG KOMPLAIN"
                    )
                )
                .where(
                    dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.IsDone.eq(
                        0
                    )
                )
                .list()
        }

        adapter = MonitoringComplaintDetailAdapter(arrayListOf(),
            object : MonitoringComplaintDetailAdapter.OnAdapterListenerNormal {
                override fun onClick(po: Boolean) {
                    if (po) {
                        totalNormal++
                        binding.tvTotalCacat.text = "${totalCacat} Tidak Sesuai"
                        binding.tvTotalNormal.text = "${totalNormal} Sesuai"
                    } else {
                        totalNormal--
                        binding.tvTotalCacat.text = "${totalCacat} Tidak Sesuai"
                        binding.tvTotalNormal.text = "${totalNormal} Sesuai"
                    }
                }

            }, object : MonitoringComplaintDetailAdapter.OnAdapterListenerCacat {
                override fun onClick(po: Boolean) {
                    if (po) {
                        totalCacat++
                        binding.tvTotalCacat.text = "${totalCacat} Tidak Sesuai"
                        binding.tvTotalNormal.text = "${totalNormal} Sesuai"
                    } else {
                        totalCacat--
                        binding.tvTotalCacat.text = "${totalCacat} Tidak Sesuai"
                        binding.tvTotalNormal.text = "${totalNormal} Sesuai"
                    }
                }

            }, object : MonitoringComplaintDetailAdapter.OnAdapterAlasanKomplain {
                override fun onClick(po: dev.iconpln.mims.data.local.database.TMonitoringComplaintDetail) {
                    val dialog = Dialog(this@MonitoringComplaintDetailActivity)
                    dialog.setContentView(R.layout.popup_alasan_reject);
                    dialog.window!!.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    dialog.setCancelable(false);
                    dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown;
                    val btnYa = dialog.findViewById(R.id.btn_ya) as AppCompatButton
                    val alasans = dialog.findViewById(R.id.et_alasan_reject) as TextView

                    alasans.text = alasan

                    btnYa.setOnClickListener { dialog.dismiss() }

                    dialog.show();
                }
            }, daoSession, subrole, status
        )

        adapter.setComplaint(listComplaint)

        with(binding) {
            if (status == "ACTIVE") {
                cbSesuai.isEnabled = false
                cbTidakSesuai.isEnabled = false
            }

            btnBack.setOnClickListener { onBackPressed() }
            tvTotalData.text = "Total: ${listComplaint.size} data"
            txtNoDo.text = noDo

            rvListKomplain.adapter = adapter
            rvListKomplain.setHasFixedSize(true)
            rvListKomplain.layoutManager = LinearLayoutManager(
                this@MonitoringComplaintDetailActivity,
                LinearLayoutManager.VERTICAL,
                false
            )

            srcSerialNumber.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val searchList = daoSession.tMonitoringComplaintDetailDao.queryBuilder()
                        .where(
                            dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.NoKomplain.eq(
                                noKomplain
                            )
                        )
                        .where(
                            dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.Status.eq(
                                "SEDANG KOMPLAIN"
                            )
                        )
                        .where(
                            dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.NoSerial.like(
                                "%" + s.toString() + "%"
                            )
                        )
                        .list()

                    adapter.setComplaint(searchList)
                }

            })

            if (subrole == 3) {
                cbTidakSesuai.visibility = View.GONE
                cbSesuai.visibility = View.GONE
                lblSesuai.visibility = View.GONE
                lblTidakSesuai.visibility = View.GONE
                btnReject.visibility = View.GONE
//                btnKomplain.visibility = View.GONE
                btnTerima.visibility = View.GONE
                imageView20.visibility = View.GONE
                tvTotalNormal.visibility = View.GONE
                imageView21.visibility = View.GONE
                tvTotalCacat.visibility = View.GONE
            }

            cbSesuai.setOnCheckedChangeListener { buttonView, isChecked ->
                cbTidakSesuai.isEnabled = !isChecked
                if (isChecked) {
                    for (i in listComplaint) {
                        i.statusPeriksa = "SESUAI"
                        i.isChecked = 1
                        daoSession.update(i)
                    }
                    adapter.setComplaint(listComplaint)
                } else {
                    for (i in listComplaint) {
                        i.statusPeriksa = ""
                        i.isChecked = 0
                        daoSession.update(i)
                    }
                    adapter.setComplaint(listComplaint)
                }
            }

            cbTidakSesuai.setOnCheckedChangeListener { buttonView, isChecked ->
                cbSesuai.isEnabled = !isChecked
                if (isChecked) {
                    for (i in listComplaint) {
                        i.statusPeriksa = "TIDAK SESUAI"
                        i.isChecked = 1
                        daoSession.update(i)
                    }
                    adapter.setComplaint(listComplaint)
                } else {
                    for (i in listComplaint) {
                        i.statusPeriksa = ""
                        i.isChecked = 0
                        daoSession.update(i)
                    }
                    adapter.setComplaint(listComplaint)
                }
            }

            btnScan.setOnClickListener {
                val dialog = BottomSheetDialog(
                    this@MonitoringComplaintDetailActivity,
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
            btnTerima.setOnClickListener { validTerima() }
            btnReject.setOnClickListener { validReject() }

        }
    }

    private fun validReject() {
        val checkListKomplain = daoSession.tMonitoringComplaintDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.NoKomplain.eq(
                    noKomplain
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.Status.eq(
                    "SEDANG KOMPLAIN"
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.IsChecked.eq(
                    1
                )
            )
            .list()

        if (status == "ACTIVE") {
            Toast.makeText(
                this,
                "Tidak boleh reject dengan status complaint active",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (checkListKomplain.size == 0) {
            Toast.makeText(this, "Tidak boleh terima dengan status kosong", Toast.LENGTH_SHORT)
                .show()
            return
        }

        for (i in checkListKomplain) {
            if (i.statusPeriksa == "SESUAI") {
                Toast.makeText(this, "Tidak boleh reject dengan status sesuai", Toast.LENGTH_SHORT)
                    .show()
                return
            }
        }

        val dialog = Dialog(this@MonitoringComplaintDetailActivity)
        dialog.setContentView(R.layout.popup_insert_alasan_reject);
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false);
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown;
        val btnYa = dialog.findViewById(R.id.btn_ya) as AppCompatButton
        val btnTidak = dialog.findViewById(R.id.btn_tidak) as AppCompatButton
        val alasan = dialog.findViewById(R.id.et_alasan_reject) as EditText

        btnTidak.setOnClickListener {
            dialog.dismiss();
        }

        btnYa.setOnClickListener {
            updateDataReject(alasan.text.toString())
            dialog.dismiss()
        }

        dialog.show();
    }

    private fun updateDataReject(alasan: String) {
        val checkDoPenerimaan = daoSession.tPosPenerimaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosPenerimaanDao.Properties.NoDoSmar.eq(
                    noDo
                )
            ).list().get(0)

        val checkDetailPenerimaan = daoSession.tPosDetailPenerimaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.NoDoSmar.eq(
                    noDo
                )
            ).list()

        val checkSnKomplain = daoSession.tMonitoringComplaintDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.NoDoSmar.eq(
                    noDo
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.IsChecked.eq(
                    1
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.IsDone.eq(
                    0
                )
            ).list()

        val checkSnBelumDiperiksa = daoSession.tMonitoringComplaintDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.NoDoSmar.eq(
                    noDo
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.StatusPeriksa.eq(
                    ""
                )
            ).list().size > 0

        for (i in checkSnKomplain) {
            i.isDone = 1
            i.statusPeriksa = "REJECT"
            i.status = "SELESAI KOMPLAIN"
            daoSession.tMonitoringComplaintDetailDao.update(i)

            for (j in checkDetailPenerimaan) {
                if (j.serialNumber == i.noSerial) {
                    j.statusPenerimaan = "REJECT"
                    daoSession.tPosDetailPenerimaanDao.update(j)
                }
            }
        }

        if (!checkSnBelumDiperiksa) {
            checkDoPenerimaan.isRating = 1
            daoSession.tPosPenerimaanDao.update(checkDoPenerimaan)
        }

        var packagings = ""
        for (i in checkSnKomplain) {
            packagings += "${i.noPackaging},${i.noSerial},${i.noMatSap},REJECT,${i.doLineItem};"
        }

        if (packagings != "") {
            packagings = packagings.substring(0, packagings.length - 1)
        }

        val reports = ArrayList<GenericReport>()
        val currentDate = LocalDateTime.now().toString(Config.DATE)
        val currentDateTime = LocalDateTime.now().toString(Config.DATETIME)
        val currentUtc = DateTimeUtils.currentUtc

        //region Add report visit to queue
        var jwtWeb = SharedPrefsUtils.getStringPreference(
            this@MonitoringComplaintDetailActivity,
            "jwtWeb",
            ""
        )
        var jwtMobile =
            SharedPrefsUtils.getStringPreference(this@MonitoringComplaintDetailActivity, "jwt", "")
        var jwt = "$jwtMobile;$jwtWeb"
        var username = SharedPrefsUtils.getStringPreference(
            this@MonitoringComplaintDetailActivity,
            "username",
            "14.Hexing_Electrical"
        )
        val reportId =
            "temp_terima_komplain" + username + "_" + noDo + "_" + DateTime.now().toString(
                Config.DATETIME
            )
        val reportName = "Update Data Dokumen Monitoring Complaint"
        val reportDescription = "$reportName: " + " (" + reportId + ")"
        val params = ArrayList<ReportParameter>()
        params.add(ReportParameter("1", reportId, "no_do_smar", noDo!!, ReportParameter.TEXT))
        params.add(ReportParameter("2", reportId, "sns", packagings, ReportParameter.TEXT))
        params.add(ReportParameter("3", reportId, "status", "REJECT", ReportParameter.TEXT))
        params.add(ReportParameter("4", reportId, "alasan", alasan, ReportParameter.TEXT))
        params.add(
            ReportParameter(
                "5",
                reportId,
                "plant_code_no",
                checkDoPenerimaan.planCodeNo,
                ReportParameter.TEXT
            )
        )
        params.add(ReportParameter("6", reportId, "username", username!!, ReportParameter.TEXT))


        val report = GenericReport(
            reportId,
            jwt!!,
            reportName,
            reportDescription,
            ApiConfig.sendRejectMonitoringComplaint(),
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

    private fun validTerima() {
        val checkListKomplain = daoSession.tMonitoringComplaintDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.NoKomplain.eq(
                    noKomplain
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.Status.eq(
                    "SEDANG KOMPLAIN"
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.IsChecked.eq(
                    1
                )
            )
            .list()

        if (status == "ACTIVE") {
            Toast.makeText(
                this,
                "Tidak boleh terima dengan status complaint active",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (checkListKomplain.size == 0) {
            Toast.makeText(this, "Tidak boleh terima dengan status kosong", Toast.LENGTH_SHORT)
                .show()
            return
        }

        for (i in checkListKomplain) {
            if (i.statusPeriksa == "TIDAK SESUAI") {
                Toast.makeText(
                    this,
                    "Tidak boleh terima dengan status tidak sesuai",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        val dialog = Dialog(this@MonitoringComplaintDetailActivity)
        dialog.setContentView(R.layout.popup_validation);
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false);
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown;
        val btnYa = dialog.findViewById(R.id.btn_ya) as AppCompatButton
        val btnTidak = dialog.findViewById(R.id.btn_tidak) as AppCompatButton
        val message = dialog.findViewById(R.id.txt_message) as TextView

        message.text = "Apakah anda yakin untuk menyelesaikan complaint"

        btnTidak.setOnClickListener {
            dialog.dismiss();
        }

        btnYa.setOnClickListener {
            updateData()
            dialog.dismiss()
        }

        dialog.show();
    }

    private fun updateData() {
        val checkDoPenerimaan = daoSession.tPosPenerimaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosPenerimaanDao.Properties.NoDoSmar.eq(
                    noDo
                )
            ).list().get(0)

        val checkDetailPenerimaan = daoSession.tPosDetailPenerimaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.NoDoSmar.eq(
                    noDo
                )
            ).list()

        val checkSnKomplain = daoSession.tMonitoringComplaintDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.NoDoSmar.eq(
                    noDo
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.IsChecked.eq(
                    1
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.IsDone.eq(
                    0
                )
            ).list()

        val checkSnBelumDiperiksa = daoSession.tMonitoringComplaintDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.NoDoSmar.eq(
                    noDo
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.StatusPeriksa.eq(
                    ""
                )
            ).list().size > 0

        for (i in checkSnKomplain) {
            i.isDone = 1
            i.statusPeriksa = "DITERIMA"
            i.status = "SELESAI KOMPLAIN"
            daoSession.tMonitoringComplaintDetailDao.update(i)

            for (j in checkDetailPenerimaan) {
                if (j.serialNumber == i.noSerial) {
                    j.statusPenerimaan = "DITERIMA"
                    daoSession.tPosDetailPenerimaanDao.update(j)
                }
            }
        }

        if (!checkSnBelumDiperiksa) {
            checkDoPenerimaan.isRating = 1
            daoSession.tPosPenerimaanDao.update(checkDoPenerimaan)
        }

        var packagings = ""
        for (i in checkSnKomplain) {
            packagings += "${i.noPackaging},${i.noSerial},${i.noMatSap},DITERIMA,${i.doLineItem};"

        }

        if (packagings != "") {
            packagings = packagings.substring(0, packagings.length - 1)
        }

        val reports = ArrayList<GenericReport>()
        val currentDate = LocalDateTime.now().toString(Config.DATE)
        val currentDateTime = LocalDateTime.now().toString(Config.DATETIME)
        val currentUtc = DateTimeUtils.currentUtc

        //region Add report visit to queue
        var jwtWeb = SharedPrefsUtils.getStringPreference(
            this@MonitoringComplaintDetailActivity,
            "jwtWeb",
            ""
        )
        var jwtMobile =
            SharedPrefsUtils.getStringPreference(this@MonitoringComplaintDetailActivity, "jwt", "")
        var jwt = "$jwtMobile;$jwtWeb"
        var username = SharedPrefsUtils.getStringPreference(
            this@MonitoringComplaintDetailActivity,
            "username",
            "14.Hexing_Electrical"
        )
        val reportId =
            "temp_terima_komplain" + username + "_" + noDo + "_" + DateTime.now().toString(
                Config.DATETIME
            )
        val reportName = "Update Data Dokumen Monitoring Complaint"
        val reportDescription = "$reportName: " + " (" + reportId + ")"
        val params = ArrayList<ReportParameter>()
        params.add(ReportParameter("1", reportId, "no_do_smar", noDo!!, ReportParameter.TEXT))
        params.add(ReportParameter("2", reportId, "sns", packagings, ReportParameter.TEXT))
        params.add(ReportParameter("3", reportId, "status", "DITERIMA", ReportParameter.TEXT))
        params.add(
            ReportParameter(
                "4",
                reportId,
                "qty",
                checkSnKomplain.size.toString(),
                ReportParameter.TEXT
            )
        )
        params.add(ReportParameter("5", reportId, "tgl_terima", currentDate, ReportParameter.TEXT))
        params.add(
            ReportParameter(
                "6",
                reportId,
                "plant_code_no",
                checkDoPenerimaan.planCodeNo,
                ReportParameter.TEXT
            )
        )
        params.add(ReportParameter("7", reportId, "username", username!!, ReportParameter.TEXT))
        params.add(
            ReportParameter(
                "8",
                reportId,
                "no_komplain",
                noKomplain!!,
                ReportParameter.TEXT
            )
        )


        val report = GenericReport(
            reportId,
            jwt!!,
            reportName,
            reportDescription,
            ApiConfig.sendTerimaMonitoringComplaint(),
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
                binding.srcSerialNumber.setText(result.contents)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setLoading(show: Boolean, title: String, message: String) {
    }

    override fun setFinish(result: Boolean, message: String) {
        if (result) {
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        startActivity(
            Intent(this@MonitoringComplaintDetailActivity, MonitoringComplaintActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
    }
}