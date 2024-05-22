package dev.iconpln.mims.ui.ulp.penerimaan.input_pemeriksaan

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import dev.iconpln.mims.databinding.ActivityInputSnPemeriksaanBinding
import dev.iconpln.mims.tasks.Loadable
import dev.iconpln.mims.tasks.TambahReportTask
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.DateTimeUtils
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import org.joda.time.DateTime
import org.joda.time.LocalDateTime

class InputSnPemeriksaanActivity : BaseActivity(), Loadable {
    private lateinit var binding: ActivityInputSnPemeriksaanBinding
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private var noMaterial: String = ""
    private var noRepackaging: String = ""
    private var noTransaksi: String = ""
    private var valuationType: String = ""
    private lateinit var adapter: PemeriksaanUlpSnAdapter
    private lateinit var lisSn: List<dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlp>
    private lateinit var details: dev.iconpln.mims.data.local.database.TTransPenerimaanDetailUlp
    private lateinit var penerimaan: dev.iconpln.mims.data.local.database.TTransPenerimaanUlp
    private lateinit var dialogLoading: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputSnPemeriksaanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dialogLoading = Helper.loadingDialog(this)
        daoSession = (application as MyApplication).daoSession!!
        noMaterial = intent.getStringExtra(EXTRA_NO_MATERIAL).toString()
        noRepackaging = intent.getStringExtra(EXTRA_NO_REPACKAGING).toString()
        noTransaksi = intent.getStringExtra(EXTRA_NO_TRANSAKSI).toString()
        valuationType = intent.getStringExtra(EXTRA_VALUATION_TYPE).toString()

        lisSn = daoSession.tListSnMaterialPenerimaanUlpDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlpDao.Properties.NoRepackaging.eq(
                    noRepackaging
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlpDao.Properties.NoMaterial.eq(
                    noMaterial
                )
            ).list()

        details = daoSession.tTransPenerimaanDetailUlpDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TTransPenerimaanDetailUlpDao.Properties.NoMaterial.eq(
                    noMaterial
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TTransPenerimaanDetailUlpDao.Properties.NoTransaksi.eq(
                    noTransaksi
                )
            ).list().get(0)

        penerimaan = daoSession.tTransPenerimaanUlpDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TTransPenerimaanUlpDao.Properties.NoRepackaging.eq(
                    noRepackaging
                )
            ).list().get(0)

        adapter = PemeriksaanUlpSnAdapter(
            arrayListOf(),
            object : PemeriksaanUlpSnAdapter.OnAdapterListener {
                override fun onClick(tms: dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlp) {}

            })

        with(binding) {
            txtGudangAsal.text = penerimaan.gudangAsal
            txtNoMaterial.text = noMaterial
            txtSpesifikasi.text = details.materialDesc

            btnScanSnMaterial.setOnClickListener {
                val dialog = BottomSheetDialog(
                    this@InputSnPemeriksaanActivity,
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

            btnScan.setOnClickListener {
                val dialog = BottomSheetDialog(
                    this@InputSnPemeriksaanActivity,
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
            btnInputSnManual.setOnClickListener { showPopUp() }

            rvListSn.adapter = adapter
            rvListSn.layoutManager = LinearLayoutManager(
                this@InputSnPemeriksaanActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            rvListSn.setHasFixedSize(true)

            btnBack.setOnClickListener { onBackPressed() }
            btnSimpan.setOnClickListener { validation() }

            srcNoSn.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val sn = s.toString()
                    val listSearch = daoSession.tListSnMaterialPenerimaanUlpDao.queryBuilder()
                        .where(
                            dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlpDao.Properties.NoRepackaging.eq(
                                noRepackaging
                            )
                        )
                        .where(
                            dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlpDao.Properties.NoMaterial.eq(
                                noMaterial
                            )
                        )
                        .where(
                            dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlpDao.Properties.NoSerialNumber.like(
                                "%" + sn + "%"
                            )
                        ).list()

                    adapter.setTmsList(listSearch)
                }

            })
        }

        adapter.setTmsList(lisSn)
    }

    private fun validation() {
//        lisSn = daoSession.tListSnMaterialPenerimaanUlpDao.queryBuilder()
//            .where(TListSnMaterialPenerimaanUlpDao.Properties.NoRepackaging.eq(noRepackaging))
//            .where(TListSnMaterialPenerimaanUlpDao.Properties.NoMaterial.eq(noMaterial)).list()
//
//        if (lisSn.isEmpty()){
//            Toast.makeText(this@InputSnPemeriksaanActivity, "Kamu belum scan Serial Number", Toast.LENGTH_SHORT).show()
//            return
//        }

        submitForm()
    }

    private fun submitForm() {
        dialogLoading.show()
        val reports = ArrayList<GenericReport>()
        val currentDate = LocalDateTime.now().toString(Config.DATE)
        val currentDateTime = LocalDateTime.now().toString(Config.DATETIME)
        val currentUtc = DateTimeUtils.currentUtc

        details.isDone = 1
        daoSession.tTransPenerimaanDetailUlpDao.update(details)

        var jwt = SharedPrefsUtils.getStringPreference(this@InputSnPemeriksaanActivity, "jwt", "")
        var plant =
            SharedPrefsUtils.getStringPreference(this@InputSnPemeriksaanActivity, "plant", "")
        var storloc =
            SharedPrefsUtils.getStringPreference(this@InputSnPemeriksaanActivity, "storloc", "")
        var username = SharedPrefsUtils.getStringPreference(
            this@InputSnPemeriksaanActivity,
            "username",
            "14.Hexing_Electrical"
        )
        val reportId =
            "temp_pemeriksaanUlp_detail" + username + "_" + penerimaan.noPermintaan + "_" + DateTime.now()
                .toString(
                    Config.DATETIME
                )
        val reportName = "Update Data pemeriksaan Detail ulp"
        val reportDescription = "$reportName: " + " (" + reportId + ")"
        val params = ArrayList<ReportParameter>()

        params.add(
            ReportParameter(
                "1",
                reportId,
                "no_transaksi",
                details.noTransaksi!!,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "2",
                reportId,
                "no_material",
                details.noMaterial,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "3",
                reportId,
                "username",
                username!!,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "4",
                reportId,
                "qty",
                lisSn.size.toString(),
                ReportParameter.TEXT
            )
        )

        params.add(
            ReportParameter(
                "5",
                reportId,
                "valuation_type",
                valuationType,
                ReportParameter.TEXT
            )
        )

        val report = GenericReport(
            reportId,
            jwt!!,
            reportName,
            reportDescription,
            ApiConfig.sendReportPenerimaanUlpDetail(),
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

    private fun showPopUp() {
        val dialog = Dialog(this@InputSnPemeriksaanActivity)
        dialog.setContentView(R.layout.inputsn_pemeriksaan_popupdialog);
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false);
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown;
        val btnOk = dialog.findViewById(R.id.btn_Ok) as AppCompatButton
        val etMessage = dialog.findViewById(R.id.inpt_snMaterial) as EditText

        btnOk.setOnClickListener {
            val listSn =
                dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlp()
            listSn.noRepackaging = noRepackaging
            listSn.isScanned = 1
            listSn.status = "SESUAI"
            listSn.noMaterial = noMaterial
            listSn.noSerialNumber = etMessage.text.toString()
            daoSession.tListSnMaterialPenerimaanUlpDao.insert(listSn)
//            val listSn = TListSnMaterialPenerimaanUlp()
//            var isSnExist = false
//            for (i in lisSn){
//                if (i.noSerialNumber == etMessage.text.toString()){
//                    isSnExist = true
//                    listSn.noRepackaging = noRepackaging
//                    listSn.isScanned = 1
//                    listSn.serialNumber = etMessage.text.toString()
//                    listSn.nomorMaterial = noMaterial
//                    listSn.status = "SESUAI"
//                    daoSession.tMonitoringSnMaterialDao.insert(listSn)
//                }
//            }
//
//            if (!isSnExist){
//                Toast.makeText(this@InputSnPemeriksaanActivity, "SN Tidak ditemukan", Toast.LENGTH_SHORT).show()
//            }

            val reloadList = daoSession.tListSnMaterialPenerimaanUlpDao.queryBuilder()
                .where(
                    dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlpDao.Properties.NoRepackaging.eq(
                        noRepackaging
                    )
                )
                .where(
                    dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlpDao.Properties.NoMaterial.eq(
                        noMaterial
                    )
                ).list()

            adapter.setTmsList(reloadList)
            dialog.dismiss();
        }
        dialog.show();
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
                binding.srcNoSn.setText(result.contents)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setLoading(show: Boolean, title: String, message: String) {

    }

    override fun setFinish(result: Boolean, message: String) {
        dialogLoading.dismiss()
        Toast.makeText(this@InputSnPemeriksaanActivity, message, Toast.LENGTH_SHORT).show()
        startActivity(
            Intent(this@InputSnPemeriksaanActivity, DetailPemeriksaanActivity::class.java)
                .putExtra("noRepackaging", noRepackaging)
                .putExtra("noPengiriman", penerimaan.noPengiriman)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
    }

    companion object {
        const val EXTRA_NO_MATERIAL = "extra_no_material"
        const val EXTRA_NO_REPACKAGING = "extra_no_repackaging"
        const val EXTRA_NO_TRANSAKSI = "extra_no_transaksi"
        const val EXTRA_VALUATION_TYPE = "extra_valuation_type"
    }
}