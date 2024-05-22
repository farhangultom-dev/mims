package dev.iconpln.mims.ui.pemeriksaan.pemeriksaan_detail


import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import dev.iconpln.mims.databinding.ActivityPemeriksaanDetailBinding
import dev.iconpln.mims.tasks.Loadable
import dev.iconpln.mims.tasks.TambahReportTask
import dev.iconpln.mims.ui.pemeriksaan.PemeriksaanActivity
import dev.iconpln.mims.ui.pemeriksaan.complaint_pemeriksaan.ComplaintPemeriksaanActivity
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

class PemeriksaanDetailActivity : BaseActivity(), Loadable {
    private lateinit var binding: ActivityPemeriksaanDetailBinding
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private lateinit var adapter: DetailPemeriksaanAdapter
    private lateinit var listPemDetail: MutableList<dev.iconpln.mims.data.local.database.TPemeriksaanDetail>
    private lateinit var pemeriksaan: dev.iconpln.mims.data.local.database.TPemeriksaan
    private var isListenerCheckedCall = true
    private var progressDialog: AlertDialog? = null
    private lateinit var listDokumentasi: List<String>
    private var noPem: String = ""
    private var noDo: String = ""
    private var totalCacat = 0
    private var totalNormal = 0
    private lateinit var dialogLoading: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPemeriksaanDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!
        noPem = intent.getStringExtra("noPemeriksaan")!!
        noDo = intent.getStringExtra("noDo")!!
        dialogLoading = Helper.loadingDialog(this)
        getDokumentasi(noDo)

        listPemDetail = daoSession.tPemeriksaanDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.NoPemeriksaan.eq(
                    noPem
                )
            )
            .where(dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsDone.eq(0))
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsComplaint.eq(
                    0
                )
            )
            .list()
        pemeriksaan = daoSession.tPemeriksaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDao.Properties.NoPemeriksaan.eq(
                    noPem
                )
            ).limit(1).unique()


//        adapter.setPedList(listPemDetail)
        adapter = DetailPemeriksaanAdapter(
            arrayListOf(),
            object : DetailPemeriksaanAdapter.OnAdapterListenerNormal {
                override fun onClick(po: Boolean) {
                    if (po) {
                        totalNormal++
                        binding.tvTotalCacat.text = "${totalCacat} Cacat"
                        binding.tvTotalNormal.text = "${totalNormal} Normal"
                    } else {
                        totalNormal--
                        binding.tvTotalCacat.text = "${totalCacat} Cacat"
                        binding.tvTotalNormal.text = "${totalNormal} Normal"
                    }
                }

            },
            object : DetailPemeriksaanAdapter.OnAdapterListenerCacat {
                override fun onClick(po: Boolean) {
                    if (po) {
                        totalCacat++
                        binding.tvTotalCacat.text = "${totalCacat} Cacat"
                        binding.tvTotalNormal.text = "${totalNormal} Normal"
                    } else {
                        totalCacat--
                        binding.tvTotalCacat.text = "${totalCacat} Cacat"
                        binding.tvTotalNormal.text = "${totalNormal} Normal"
                    }
                }

            },
            object : DetailPemeriksaanAdapter.OnAdapterSetCheckedAll {
                override fun onClick() {
                    isListenerCheckedCall = false
                    binding.cbNormal.isChecked = false
                    binding.cbCacat.isChecked = false
                    isListenerCheckedCall = true
                }
            },
            daoSession,
            pemeriksaan.kdPabrikan
        )
        adapter.setPedList(listPemDetail)

        with(binding) {
            tvTotalData.text = "Total: ${listPemDetail.size} data"

            rvListSn.adapter = adapter
            rvListSn.layoutManager = LinearLayoutManager(
                this@PemeriksaanDetailActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            rvListSn.setHasFixedSize(true)

            txtKurirPengiriman.text = pemeriksaan.expeditions
            txtTglKirim.text = "Tgl ${pemeriksaan.createdDate}"
            txtPetugasPenerima.text = pemeriksaan.petugasPenerima
            txtDeliveryOrder.text = pemeriksaan.noDoSmar
            txtNamaKurir.text = pemeriksaan.namaKurir

            btnScanPackaging.setOnClickListener {
                val dialog = BottomSheetDialog(
                    this@PemeriksaanDetailActivity,
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
                    openScanner(1, "barcode")
                    dialog.dismiss()
                }

                btnScanQRCode.setOnClickListener {
                    openScanner(1, "qrcode")
                    dialog.dismiss()
                }

                dialog.setCancelable(true)
                dialog.setContentView(view)
                dialog.show()
            }

            btnScanSn.setOnClickListener {
                val dialog = BottomSheetDialog(
                    this@PemeriksaanDetailActivity,
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
                    openScanner(2, "barcode")
                    dialog.dismiss()
                }

                btnScanQRCode.setOnClickListener {
                    openScanner(2, "qrcode")
                    dialog.dismiss()
                }

                dialog.setCancelable(true)
                dialog.setContentView(view)
                dialog.show()
            }

            txtDokumentasi.setOnClickListener {
                val dialog = Dialog(this@PemeriksaanDetailActivity)
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
                    this@PemeriksaanDetailActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                recyclerView.setHasFixedSize(true)

                adapter.setData(listDokumentasi)

                btnClose.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show();
            }

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
                    val listSnsFilter = daoSession.tPemeriksaanDetailDao.queryBuilder()
                        .where(
                            dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.NoPemeriksaan.eq(
                                noPem
                            )
                        )
                        .where(
                            dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsComplaint.eq(
                                0
                            )
                        )
                        .whereOr(
                            dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.Sn.like(
                                "%" + s.toString() + "%"
                            ),
                            dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.NoPackaging.like(
                                "%" + s.toString() + "%"
                            )
                        )
                        .list()
                    adapter.setPedList(listSnsFilter)
                }

            })

            cbNormal.setOnCheckedChangeListener { buttonView, isChecked ->
                cbCacat.isEnabled = !isChecked
                if (isListenerCheckedCall) {
                    if (isChecked) {
                        for (i in listPemDetail) {
                            i.statusPemeriksaan = "NORMAL"
                            i.isChecked = 1
                            daoSession.update(i)
                        }

                        tvTotalCacat.text = "${0} Cacat"
                        tvTotalNormal.text = "${listPemDetail.size} Normal"
                        adapter.setPedList(listPemDetail)
                    } else {
                        for (i in listPemDetail) {
                            i.statusPemeriksaan = ""
                            i.isChecked = 0
                            daoSession.update(i)
                        }
                        tvTotalCacat.text = "${0} Cacat"
                        tvTotalNormal.text = "${0} Normal"
                        adapter.setPedList(listPemDetail)
                    }
                }
            }

            cbCacat.setOnCheckedChangeListener { buttonView, isChecked ->
                cbNormal.isEnabled = !isChecked
                if (isListenerCheckedCall) {
                    if (isChecked) {
                        for (i in listPemDetail) {
                            i.statusPemeriksaan = "CACAT"
                            i.isChecked = 1
                            daoSession.update(i)
                        }
                        tvTotalCacat.text = "${listPemDetail.size} Cacat"
                        tvTotalNormal.text = "${0} Normal"
                        adapter.setPedList(listPemDetail)
                    } else {
                        for (i in listPemDetail) {
                            i.statusPemeriksaan = ""
                            i.isChecked = 0
                            daoSession.update(i)
                        }
                        tvTotalCacat.text = "${0} Cacat"
                        tvTotalNormal.text = "${0} Normal"
                        adapter.setPedList(listPemDetail)
                    }
                }
            }

            btnKomplain.setOnClickListener {
                validComplaint()
            }

            btnTerima.setOnClickListener {
                validTerima()
            }

            btnBack.setOnClickListener { onBackPressed() }
        }
    }

    private fun getDokumentasi(noDo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                ApiConfig.getApiService(this@PemeriksaanDetailActivity).getDokumentasi(noDo)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                        this@PemeriksaanDetailActivity,
                        Config.IS_LOGIN_SSO, 0
                    )
                    try {
                        if (response.body()?.status == Config.KEY_SUCCESS) {
                            listDokumentasi = response.body()?.doc?.array!!
                        } else {
                            when (response.body()?.message) {
                                Config.DO_LOGOUT -> {
                                    Helper.logout(this@PemeriksaanDetailActivity, isLoginSso)
                                    Toast.makeText(
                                        this@PemeriksaanDetailActivity,
                                        getString(R.string.session_kamu_telah_habis),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> Toast.makeText(
                                    this@PemeriksaanDetailActivity,
                                    response.body()?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@PemeriksaanDetailActivity,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        e.printStackTrace()
                    } finally {
                    }
                } else {
                    Toast.makeText(
                        this@PemeriksaanDetailActivity,
                        response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun validTerima() {
        val data = daoSession.tPemeriksaanDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.NoPemeriksaan.eq(
                    noPem
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsChecked.eq(
                    1
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsPeriksa.eq(
                    1
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsComplaint.eq(
                    0
                )
            )
            .list()

        if (data.size == 0) {
            Toast.makeText(
                this@PemeriksaanDetailActivity,
                "Tidak boleh terima dengan status kosong",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        for (i in data) {
            if (i.statusPemeriksaan == "CACAT") {
                Toast.makeText(
                    this@PemeriksaanDetailActivity,
                    "Tidak boleh terima dengan status cacat",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        val dialog = Dialog(this@PemeriksaanDetailActivity)
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

        message.text = "Apakah anda yakin untuk menyelesaikan pemeriksaan"

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
        var checkIsDone = daoSession.tPemeriksaanDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.NoPemeriksaan.eq(
                    noPem
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsPeriksa.eq(
                    1
                )
            )
            .where(dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsDone.eq(1))
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsComplaint.eq(
                    0
                )
            )
            .list()

        var checkSnDiterima = daoSession.tPemeriksaanDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.NoPemeriksaan.eq(
                    noPem
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsPeriksa.eq(
                    1
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsComplaint.eq(
                    0
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsChecked.eq(
                    1
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.StatusPemeriksaan.notEq(
                    "DITERIMA"
                )
            )
            .list()

        var checkSnKomplain = daoSession.tPemeriksaanDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.NoPemeriksaan.eq(
                    noPem
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsComplaint.eq(
                    1
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsChecked.eq(
                    1
                )
            )
            .list()

        var listPemDetailChecked = daoSession.tPemeriksaanDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.NoPemeriksaan.eq(
                    noPem
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsPeriksa.eq(
                    1
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsComplaint.eq(
                    0
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsChecked.eq(
                    1
                )
            )
            .where(dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsDone.eq(0))
            .list()

        var packagings = ""
        for (i in listPemDetailChecked) {
            packagings += "${i.noPackaging},${i.sn},${i.noMaterail},${i.statusPemeriksaan},${pemeriksaan.doLineItem};"

        }

        if (packagings != "") {
            packagings = packagings.substring(0, packagings.length - 1)
        }

        for (i in listPemDetail.filter { it.isChecked == 1 }) {
            i.isDone = 1
            daoSession.tPemeriksaanDetailDao.update(i)
        }

        if (checkSnDiterima.size == 0) {
            pemeriksaan.statusPemeriksaan = "SELESAI"
            pemeriksaan.isDone = 1
            daoSession.tPemeriksaanDao.update(pemeriksaan)
        }

        val dialog = Dialog(this@PemeriksaanDetailActivity)
        dialog.setContentView(R.layout.popup_complaint)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown
        val btnOk = dialog.findViewById(R.id.btn_ok) as AppCompatButton
        val txtMessage = dialog.findViewById(R.id.txt_message) as TextView

        txtMessage.text = "Data sudah berhasil diterima"

        btnOk.setOnClickListener {
            dialog.dismiss();
            pemeriksaan.isDone = 1
            daoSession.tPemeriksaanDao.update(pemeriksaan)

            if (checkSnKomplain.isNullOrEmpty()) {
                val updatePenerimaan = daoSession.tPosPenerimaanDao.queryBuilder()
                    .where(
                        dev.iconpln.mims.data.local.database.TPosPenerimaanDao.Properties.NoDoSmar.eq(
                            noDo
                        )
                    ).list().get(0)
                updatePenerimaan.isRating = 1
                updatePenerimaan.statusPemeriksaan = "SELESAI"
                daoSession.tPosPenerimaanDao.update(updatePenerimaan)
            }

            if (checkIsDone.size == listPemDetail.size) {
                val updatePenerimaan = daoSession.tPosPenerimaanDao.queryBuilder()
                    .where(
                        dev.iconpln.mims.data.local.database.TPosPenerimaanDao.Properties.NoDoSmar.eq(
                            noDo
                        )
                    ).list().get(0)
                updatePenerimaan.statusPemeriksaan = "SELESAI"
                daoSession.tPosPenerimaanDao.update(updatePenerimaan)

                pemeriksaan.statusPemeriksaan = "SELESAI"
                daoSession.tPemeriksaanDao.update(pemeriksaan)
            }

            submitForm(packagings)
            startActivity(
                Intent(this@PemeriksaanDetailActivity, PemeriksaanActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
        }
        dialog.show();
    }

    private fun submitForm(packagings: String) {
        var quantity = daoSession.tPemeriksaanDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.NoPemeriksaan.eq(
                    noPem
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsPeriksa.eq(
                    1
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsComplaint.eq(
                    0
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsChecked.eq(
                    1
                )
            )
            .where(dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsDone.eq(0))
            .list().size

        val reports = ArrayList<GenericReport>()
        val currentDate = LocalDateTime.now().toString(Config.DATE)
        val currentDateTime = LocalDateTime.now().toString(Config.DATETIME)
        val currentUtc = DateTimeUtils.currentUtc

        //region Add report visit to queue
        var jwtWeb =
            SharedPrefsUtils.getStringPreference(this@PemeriksaanDetailActivity, "jwtWeb", "")
        var jwtMobile =
            SharedPrefsUtils.getStringPreference(this@PemeriksaanDetailActivity, "jwt", "")
        var jwt = "$jwtMobile;$jwtWeb"
        var username = SharedPrefsUtils.getStringPreference(
            this@PemeriksaanDetailActivity,
            "username",
            "14.Hexing_Electrical"
        )
        val reportId = "temp_pemeriksaan" + username + "_" + noDo + "_" + DateTime.now().toString(
            Config.DATETIME
        )
        val reportName = "Update Data Dokumen Pemeriksaan"
        val reportDescription = "$reportName: " + " (" + reportId + ")"
        val params = ArrayList<ReportParameter>()
        params.add(ReportParameter("1", reportId, "no_do_smar", noDo!!, ReportParameter.TEXT))
        params.add(ReportParameter("2", reportId, "sns", packagings, ReportParameter.TEXT))
        params.add(ReportParameter("3", reportId, "status", "DITERIMA", ReportParameter.TEXT))
        params.add(ReportParameter("4", reportId, "qty", quantity.toString(), ReportParameter.TEXT))
        params.add(
            ReportParameter(
                "5",
                reportId,
                "tgl_terima",
                pemeriksaan.tanggalDiterima,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "6",
                reportId,
                "plant_code_no",
                pemeriksaan.planCodeNo,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "7",
                reportId,
                "no_pemeriksaan",
                pemeriksaan.noPemeriksaan!!,
                ReportParameter.TEXT
            )
        )

        val report = GenericReport(
            reportId,
            jwt!!,
            reportName,
            reportDescription,
            ApiConfig.sendPemeriksaan(),
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

    private fun validComplaint() {
        val data = daoSession.tPemeriksaanDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.NoPemeriksaan.eq(
                    noPem
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsChecked.eq(
                    1
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsPeriksa.eq(
                    1
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsComplaint.eq(
                    0
                )
            )
            .list()

        if (data.isNullOrEmpty()) {
            Toast.makeText(
                this@PemeriksaanDetailActivity,
                "Tidak boleh melakukan komplain dengan status kosong",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        for (i in data) {
            if (i.statusPemeriksaan == "NORMAL") {
                Toast.makeText(
                    this@PemeriksaanDetailActivity,
                    "Tidak boleh melakukan komplain dengan status normal",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        startActivity(
            Intent(this@PemeriksaanDetailActivity, ComplaintPemeriksaanActivity::class.java)
                .putExtra("noDo", noDo)
                .putExtra("noPemeriksaan", noPem)
        )
    }

    private fun openScanner(typeScanning: Int, type: String) {
        val scan = ScanOptions()

        if (type == "barcode") {
            scan.setDesiredBarcodeFormats(ScanOptions.CODE_128)
            scan.setCameraId(0)
            scan.setBeepEnabled(true)
            scan.setBarcodeImageEnabled(true)
            scan.captureActivity = CustomScanBarcodeActivity::class.java
            when (typeScanning) {
                1 -> barcodeLauncherPackaging.launch(scan)
                2 -> barcodeLauncherSn.launch(scan)
            }
        } else {
            scan.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            scan.setCameraId(0)
            scan.setBeepEnabled(true)
            scan.setBarcodeImageEnabled(true)
            scan.captureActivity = CustomScanActivity::class.java
            when (typeScanning) {
                1 -> barcodeLauncherPackaging.launch(scan)
                2 -> barcodeLauncherSn.launch(scan)
            }
        }
    }

    private val barcodeLauncherPackaging = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        try {
            if (!result.contents.isNullOrEmpty()) {
                val listPackagings = daoSession.tPemeriksaanDetailDao.queryBuilder().where(
                    dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.NoPackaging.eq(
                        result.contents
                    )
                ).list()
                for (i in listPackagings) {
                    i.statusPemeriksaan = "NORMAL"
                    i.isChecked = 1
                    daoSession.tPemeriksaanDetailDao.update(i)
                }
                adapter.setPedList(listPemDetail)
            }
        } catch (e: Exception) {
        }
    }

    private val barcodeLauncherSn = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        try {
            if (!result.contents.isNullOrEmpty()) {
                val listSns = daoSession.tPemeriksaanDetailDao.queryBuilder()
                    .where(
                        dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.Sn.eq(
                            result.contents
                        )
                    ).limit(1).unique()

                listSns.statusPemeriksaan = "NORMAL"
                listSns.isChecked = 1
                daoSession.tPemeriksaanDetailDao.update(listSns)

                adapter.setPedList(listPemDetail)
            }
        } catch (e: Exception) {
        }
    }

    override fun setLoading(show: Boolean, title: String, message: String) {
        try {
            if (progressDialog != null) {
                if (show) {
                    progressDialog!!.apply { show() }
                } else {
                    progressDialog!!.dismiss()
                }
            }

        } catch (e: Exception) {
            progressDialog!!.dismiss()
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        listPemDetail = daoSession.tPemeriksaanDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.NoPemeriksaan.eq(
                    noPem
                )
            )
            .where(dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsDone.eq(0))
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsComplaint.eq(
                    0
                )
            )
            .list()

        val dialog = Dialog(this@PemeriksaanDetailActivity)
        dialog.setContentView(R.layout.popup_validation)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown

        val btnYa = dialog.findViewById(R.id.btn_ya) as AppCompatButton
        val btnTidak = dialog.findViewById(R.id.btn_tidak) as AppCompatButton
        val message = dialog.findViewById(R.id.message) as TextView
        val txtMessage = dialog.findViewById(R.id.txt_message) as TextView
        val icon = dialog.findViewById(R.id.imageView11) as ImageView

        message.text = "Yakin untuk keluar?"
        txtMessage.text = "Jika ya maka data tidak akan tersimpan"
        icon.setImageResource(R.drawable.ic_warning)

        btnTidak.setOnClickListener {
            dialog.dismiss();
        }

        btnYa.setOnClickListener {
            super.onBackPressed()
            for (i in listPemDetail) {
                i.statusPemeriksaan = ""
                i.isChecked = 0
                daoSession.update(i)
            }

            dialog.dismiss()
        }

        dialog.show();
    }

    override fun setFinish(result: Boolean, message: String) {
        if (result) {
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}