package dev.iconpln.mims.ui.penerimaan.detail_penerimaan


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
import dev.iconpln.mims.databinding.ActivityDetailPenerimaanBinding
import dev.iconpln.mims.tasks.Loadable
import dev.iconpln.mims.tasks.TambahReportTask
import dev.iconpln.mims.ui.pemeriksaan.complaint.ComplaintActivity
import dev.iconpln.mims.ui.penerimaan.PenerimaanActivity
import dev.iconpln.mims.ui.penerimaan.PenerimaanViewModel
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

class DetailPenerimaanActivity : BaseActivity(), Loadable {
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private var progressDialog: AlertDialog? = null
    private lateinit var binding: ActivityDetailPenerimaanBinding
    private val viewModel: PenerimaanViewModel by viewModels()
    private lateinit var adapter: DetailPenerimaanAdapter
    private lateinit var listDetailPen: MutableList<dev.iconpln.mims.data.local.database.TPosDetailPenerimaan>
    private lateinit var penerimaan: dev.iconpln.mims.data.local.database.TPosPenerimaan
    private lateinit var listDokumentasi: List<String>
    private var isListenerCheckedCall = true
    private var noDo: String = ""
    private var isDone = 0
    private var checkSn = 0
    private var partialCode = ""
    private var role = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPenerimaanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!
        noDo = intent.getStringExtra("noDo")!!
        isDone = intent.getIntExtra("isDone", 0)
        checkSn = intent.getIntExtra("checkSn", 0)
        partialCode = "${noDo}${DateTime.now()}"
        role = SharedPrefsUtils.getIntegerPreference(this@DetailPenerimaanActivity, "roleId", 0)
        getDokumentasi(noDo)

        listDetailPen = if (role == 10) {
            daoSession.tPosDetailPenerimaanDao.queryBuilder()
                .where(
                    dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.NoDoSmar.eq(
                        noDo
                    )
                ).list()
        } else {
            daoSession.tPosDetailPenerimaanDao.queryBuilder()
                .where(
                    dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.NoDoSmar.eq(
                        noDo
                    )
                )
                .whereOr(
                    dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.StatusPenerimaan.notEq(
                        "DITERIMA"
                    ),
                    dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.StatusPenerimaan.notEq(
                        "BELUM DIPERIKSA"
                    )
                )
                .where(
                    dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsDone.eq(
                        0
                    )
                )
                .list()
        }

        penerimaan = daoSession.tPosPenerimaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosPenerimaanDao.Properties.NoDoSmar.eq(
                    noDo
                )
            ).limit(1).unique()

        adapter = DetailPenerimaanAdapter(
            arrayListOf(),
            object : DetailPenerimaanAdapter.OnAdapterListener {
                override fun onClick(po: dev.iconpln.mims.data.local.database.TPosDetailPenerimaan) {
                    isListenerCheckedCall = false
                    binding.cbSesuai.isChecked = false
                    binding.cbTidakSesuai.isChecked = false
                    isListenerCheckedCall = true
                }
            },
            daoSession,
            partialCode,
            role,
            this
        )

        adapter.setData(listDetailPen)

        with(binding) {
            rvListSn.adapter = adapter
            rvListSn.layoutManager = LinearLayoutManager(
                this@DetailPenerimaanActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            rvListSn.setHasFixedSize(true)

            txtKurirPengiriman.text = penerimaan.expeditions
            txtTglKirim.text = "Tgl ${penerimaan.createdDate}"
            txtPetugasPenerima.text = penerimaan.petugasPenerima
            txtDeliveryOrder.text = penerimaan.noDoSmar
            txtNamaKurir.text = penerimaan.kurirPengantar

            txtDokumentasi.setOnClickListener {
                val dialog = Dialog(this@DetailPenerimaanActivity)
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
                    this@DetailPenerimaanActivity,
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

            btnScanPackaging.setOnClickListener {
                if (isDone == 1 || role == 10 || checkSn == 0) {
                    Toast.makeText(
                        this@DetailPenerimaanActivity,
                        "DO tidak dapat di scan",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val dialog = BottomSheetDialog(
                        this@DetailPenerimaanActivity,
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
            }

            btnScanSn.setOnClickListener {
                if (isDone == 1 || role == 10 || checkSn == 0) {
                    Toast.makeText(
                        this@DetailPenerimaanActivity,
                        "DO tidak dapat di scan",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val dialog = BottomSheetDialog(
                        this@DetailPenerimaanActivity,
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
                    val listSnsFilter = listDetailPen.filter {
                        it.serialNumber.toLowerCase().contains(s.toString().toLowerCase())
                    }
                    adapter.setData(listSnsFilter)
                }

            })

            if (isDone == 1 || checkSn == 0) {
                btnKomplain.isEnabled = false
                btnKomplain.setBackgroundColor(Color.GRAY)
                btnTerima.isEnabled = false
                btnTerima.setBackgroundColor(Color.GRAY)
            }

            if (role == 10) {
                cbSesuai.isEnabled = false
                cbTidakSesuai.isEnabled = false

                btnKomplain.isEnabled = false
                btnKomplain.setBackgroundColor(Color.GRAY)
                btnTerima.isEnabled = false
                btnTerima.setBackgroundColor(Color.GRAY)
            } else {
                cbSesuai.isEnabled = true
                cbTidakSesuai.isEnabled = true

                btnKomplain.isEnabled = true
                btnTerima.isEnabled = true
            }

            setCheckBox()

            btnKomplain.setOnClickListener {
                validComplaint()
            }

            btnTerima.setOnClickListener {
                validTerima()
            }

            btnBack.setOnClickListener { onBackPressed() }
        }
    }

    private fun setCheckBox() {
        with(binding) {
            cbSesuai.setOnCheckedChangeListener { _, isChecked ->
                cbTidakSesuai.isEnabled = !isChecked
                if (isListenerCheckedCall) {
                    if (isChecked) {
                        for (i in listDetailPen) {
                            if (i.isDone != 1) {
                                i.statusPenerimaan = "SESUAI"
                                i.isChecked = 1
                                i.partialCode = partialCode
                                daoSession.update(i)
                            }
                        }
                        adapter.setData(listDetailPen)
                    } else {
                        for (i in listDetailPen) {
                            if (i.isDone != 1) {
                                i.statusPenerimaan = ""
                                i.isChecked = 0
                                i.partialCode = ""
                                daoSession.update(i)
                            }
                        }
                        adapter.setData(listDetailPen)
                    }
                }
            }

            cbTidakSesuai.setOnCheckedChangeListener { _, isChecked ->
                cbSesuai.isEnabled = !isChecked
                if (isListenerCheckedCall) {
                    if (isChecked) {
                        for (i in listDetailPen) {
                            if (i.isDone != 1) {
                                i.statusPenerimaan = "TIDAK SESUAI"
                                i.isChecked = 1
                                daoSession.update(i)
                            }
                        }
                        adapter.setData(listDetailPen)
                    } else {
                        for (i in listDetailPen) {
                            if (i.isDone != 1) {
                                i.statusPenerimaan = ""
                                i.isChecked = 0
                                daoSession.update(i)
                            }
                        }
                        adapter.setData(listDetailPen)
                    }
                }
            }
        }
    }

    private fun getDokumentasi(noDo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                ApiConfig.getApiService(this@DetailPenerimaanActivity).getDokumentasi(noDo)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                        this@DetailPenerimaanActivity,
                        Config.IS_LOGIN_SSO, 0
                    )
                    try {
                        if (response.body()?.status == Config.KEY_SUCCESS) {
                            listDokumentasi = response.body()?.doc?.array!!
                        } else {
                            when (response.body()?.message) {
                                Config.DO_LOGOUT -> {
                                    Helper.logout(this@DetailPenerimaanActivity, isLoginSso)
                                    Toast.makeText(
                                        this@DetailPenerimaanActivity,
                                        getString(R.string.session_kamu_telah_habis),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> Toast.makeText(
                                    this@DetailPenerimaanActivity,
                                    response.body()?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@DetailPenerimaanActivity,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        e.printStackTrace()
                    } finally {
                    }
                } else {
                    Toast.makeText(
                        this@DetailPenerimaanActivity,
                        response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun validTerima() {
        var data = daoSession.tPosDetailPenerimaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.NoDoSmar.eq(
                    noDo
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsDone.eq(
                    0
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsChecked.eq(
                    1
                )
            ).list()

        if (isDone == 1 || checkSn == 0) {
            Toast.makeText(
                this@DetailPenerimaanActivity,
                "DO ini sudah di terima",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (data.size > 0) {
            for (i in data) {
                if (i.statusPenerimaan == "TIDAK SESUAI") {
                    Toast.makeText(
                        this@DetailPenerimaanActivity,
                        "Tidak boleh terima dengan status tidak sesuai",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
            }
        } else {
            Toast.makeText(
                this@DetailPenerimaanActivity,
                "Tidak boleh terima dengan status kosong",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val dialog = Dialog(this@DetailPenerimaanActivity)
        dialog.setContentView(R.layout.popup_validation);
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false);
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown;
        val message = dialog.findViewById(R.id.message) as TextView
        val subMessage = dialog.findViewById(R.id.txt_message) as TextView
        val btnYa = dialog.findViewById(R.id.btn_ya) as AppCompatButton
        val btnTidak = dialog.findViewById(R.id.btn_tidak) as AppCompatButton

        message.text = "Penerimaan"
        subMessage.text = "Yakin untuk melakukan penerimaan?"

        btnTidak.setOnClickListener {
            showDialogTerima()
            dialog.dismiss();
        }

        btnYa.setOnClickListener {
            showDialogTerima()
            dialog.dismiss()
        }

        dialog.show();
    }

    private fun showDialogTerima() {
        val dialog = Dialog(this@DetailPenerimaanActivity)
        dialog.setContentView(R.layout.popup_validation);
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false);
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown;
        val btnYa = dialog.findViewById(R.id.btn_ya) as AppCompatButton
        val btnTidak = dialog.findViewById(R.id.btn_tidak) as AppCompatButton

        btnTidak.setOnClickListener {
            submitForm(0)
            dialog.dismiss();
        }

        btnYa.setOnClickListener {
            submitForm(1)
            dialog.dismiss()
        }

        dialog.show();
    }

    private fun submitForm(isPeriksa: Int) {
        var sns = ""
        var statusSn = if (isPeriksa == 0) "DITERIMA" else "BELUM DIPERIKSA"

        var checkedDetPen = daoSession.tPosDetailPenerimaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.NoDoSmar.eq(
                    noDo
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsChecked.eq(
                    1
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsDone.eq(
                    0
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsComplaint.eq(
                    0
                )
            ).list()

        var detailPenerimaanAkhir = daoSession.tPosDetailPenerimaanAkhirDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhirDao.Properties.NoDoSmar.eq(
                    noDo
                )
            ).list()

        for (i in checkedDetPen) {
            sns += "${i.noPackaging},${i.serialNumber},${i.noMaterial},$statusSn,${i.doLineItem};"

        }

        if (sns != "") {
            sns = sns.substring(0, sns.length - 1)
        }

        val reports = ArrayList<GenericReport>()
        val currentDate = LocalDateTime.now().toString(Config.DATE)
        val currentDateTime = LocalDateTime.now().toString(Config.DATETIME)
        val currentUtc = DateTimeUtils.currentUtc

        //region Add report visit to queue
        var jwtWeb =
            SharedPrefsUtils.getStringPreference(this@DetailPenerimaanActivity, "jwtWeb", "")
        var jwtMobile =
            SharedPrefsUtils.getStringPreference(this@DetailPenerimaanActivity, "jwt", "")
        var jwt = "$jwtMobile;$jwtWeb"
        var username = SharedPrefsUtils.getStringPreference(
            this@DetailPenerimaanActivity,
            "username",
            "14.Hexing_Electrical"
        )
        val reportId = "temp_penerimaan" + username + "_" + noDo + "_" + DateTime.now().toString(
            Config.DATETIME
        )
        val reportName = "Update Data Dokumen Penerimaan"
        val reportDescription = "$reportName: " + " (" + reportId + ")"
        val params = ArrayList<ReportParameter>()
        params.add(ReportParameter("1", reportId, "no_do_smar", noDo!!, ReportParameter.TEXT))
        params.add(
            ReportParameter(
                "2",
                reportId,
                "plant_code_no",
                penerimaan.planCodeNo,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "3",
                reportId,
                "receieve_date",
                currentDate,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "4",
                reportId,
                "quantity",
                checkedDetPen.size.toString(),
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "5",
                reportId,
                "is_periksa",
                isPeriksa.toString(),
                ReportParameter.TEXT
            )
        )
        params.add(ReportParameter("6", reportId, "sns", sns, ReportParameter.TEXT))
        params.add(ReportParameter("7", reportId, "username", username!!, ReportParameter.TEXT))
        params.add(ReportParameter("8", reportId, "email", username, ReportParameter.TEXT))

        val report = GenericReport(
            reportId,
            jwt!!,
            reportName,
            reportDescription,
            ApiConfig.sendPenerimaan(),
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

        for (i in checkedDetPen) {
            i.isDone = 1
            daoSession.tPosDetailPenerimaanDao.update(i)

            if (detailPenerimaanAkhir.isNullOrEmpty()) {
                val dataPenerimaanAkhir =
                    dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhir()
                dataPenerimaanAkhir.qty = i.qty
                dataPenerimaanAkhir.isReceived = true
                dataPenerimaanAkhir.noDoSmar = i.noDoSmar
                dataPenerimaanAkhir.isRejected = false
                dataPenerimaanAkhir.noPackaging = i.noPackaging
                dataPenerimaanAkhir.namaKategoriMaterial = i.namaKategoriMaterial
                dataPenerimaanAkhir.noMaterial = i.noMaterial
                dataPenerimaanAkhir.kdPabrikan = i.kdPabrikan
                dataPenerimaanAkhir.isComplaint = false
                dataPenerimaanAkhir.serialNumber = i.serialNumber
                dataPenerimaanAkhir.storLoc = i.storLoc
                dataPenerimaanAkhir.status = "SESUAI"
                daoSession.tPosDetailPenerimaanAkhirDao.insert(dataPenerimaanAkhir)
            } else {
                for (j in detailPenerimaanAkhir) {
                    if (j.serialNumber == i.serialNumber) {
                        j.isReceived = true
                        j.status = "SESUAI"
                        daoSession.tPosDetailPenerimaanAkhirDao.update(j)
                    }
                }
            }
        }

        var noPrk = "PRK${noDo}${currentDateTime}"
        if (isPeriksa == 1) {

            var item = dev.iconpln.mims.data.local.database.TPemeriksaan()
            item.isDone = 0
            item.createdDate = penerimaan.createdDate
            item.namaKurir = penerimaan.kurirPengantar
            item.kdPabrikan = penerimaan.kdPabrikan
            item.materialGroup = penerimaan.materialGroup
            item.namaEkspedisi = penerimaan.expeditions
            item.petugasPenerima = penerimaan.petugasPenerima
            item.tanggalDiterima = penerimaan.tanggalDiterima
            item.namaKategoriMaterial = penerimaan.namaKategoriMaterial
            item.courierPersonName = penerimaan.courierPersonName
            item.expeditions = penerimaan.expeditions
            item.doStatus = penerimaan.doStatus
            item.noDoMims = penerimaan.noDoMims
            item.plantName = penerimaan.plantName
            item.planCodeNo = penerimaan.planCodeNo
            item.leadTime = penerimaan.leadTime
            item.noDoSmar = penerimaan.noDoSmar
            item.poMpNo = penerimaan.poMpNo
            item.poSapNo = penerimaan.poSapNo
            item.tlskNo = penerimaan.tlskNo
            item.total = penerimaan.total
            item.storLoc = penerimaan.storloc
            item.noPemeriksaan = noPrk
            item.doLineItem = penerimaan.doLineItem

            item.namaKetua = ""
            item.namaManager = ""
            item.namaSekretaris = ""
            item.namaAnggota = ""
            item.namaAnggotaBaru = ""

            daoSession.tPemeriksaanDao.insert(item)

            val detailListlistToInsert = daoSession.tPosDetailPenerimaanDao.queryBuilder()
                .where(
                    dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.NoDoSmar.eq(
                        noDo
                    )
                )
                .where(
                    dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsDone.eq(
                        1
                    )
                )
                .where(
                    dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.PartialCode.eq(
                        partialCode
                    )
                )
                .where(
                    dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsComplaint.eq(
                        0
                    )
                ).list()

            val size = detailListlistToInsert.size
            if (size > 0) {
                val items =
                    arrayOfNulls<dev.iconpln.mims.data.local.database.TPemeriksaanDetail>(size)
                var item: dev.iconpln.mims.data.local.database.TPemeriksaanDetail
                for ((i, model) in detailListlistToInsert.withIndex()) {
                    item = dev.iconpln.mims.data.local.database.TPemeriksaanDetail()
                    item.isDone = 0
                    item.isChecked = 0
                    item.statusPenerimaan = model.statusPenerimaan
                    item.statusPemeriksaan = "BELUM DIPERIKSA"
                    item.sn = model.serialNumber
                    item.noDoSmar = model.noDoSmar
                    item.kategori = model.namaKategoriMaterial
                    item.noMaterail = model.noMaterial
                    item.noPackaging = model.noPackaging
                    item.noPemeriksaan = noPrk
                    item.isComplaint = model.isComplaint
                    item.isPeriksa = 1

                    items[i] = item
                }
                daoSession.tPemeriksaanDetailDao.insertInTx(items.toList())
            }
        }

        updateData(isPeriksa)
    }

    private fun updateData(isPeriksa: Int) {
        val checkedList = daoSession.tPosDetailPenerimaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.NoDoSmar.eq(
                    noDo
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsDone.eq(
                    0
                )
            ).list()

        val checkSnKomplaint = daoSession.tPosDetailPenerimaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.NoDoSmar.eq(
                    noDo
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsDone.eq(
                    1
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsComplaint.eq(
                    1
                )
            ).list().size > 0

        val checkListPemeriksaan = daoSession.tPemeriksaanDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.NoDoSmar.eq(
                    noDo
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.StatusPemeriksaan.eq(
                    "BELUM DIPERIKSA"
                )
            ).list().size > 0

        if (checkedList.size == 0) {

            if (checkListPemeriksaan) {
                if (checkSnKomplaint) {
                    penerimaan.statusPenerimaan = "KOMPLAIN"
                    penerimaan.statusPemeriksaan = "SEDANG DIPERIKSA"
                    penerimaan.isRating = 0
                    daoSession.tPosPenerimaanDao.update(penerimaan)
                } else {
                    penerimaan.statusPenerimaan = "DITERIMA"
                    penerimaan.statusPemeriksaan = "SEDANG DIPERIKSA"
                    penerimaan.isRating = 0
                    daoSession.tPosPenerimaanDao.update(penerimaan)
                }
            } else {
                if (checkSnKomplaint) {
                    penerimaan.statusPenerimaan = "KOMPLAIN"
                    penerimaan.statusPemeriksaan = "SELESAI"
                    penerimaan.isRating = 0
                    daoSession.tPosPenerimaanDao.update(penerimaan)
                } else {
                    penerimaan.statusPenerimaan = "DITERIMA"
                    penerimaan.isRating = 1
                    penerimaan.statusPemeriksaan = "SELESAI"
                    daoSession.tPosPenerimaanDao.update(penerimaan)
                }
            }
        }

        val dialog = Dialog(this@DetailPenerimaanActivity)
        dialog.setContentView(R.layout.popup_complaint);
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false);
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown;
        val btnOk = dialog.findViewById(R.id.btn_ok) as AppCompatButton
        val txtMessage = dialog.findViewById(R.id.txt_message) as TextView

        if (isPeriksa == 1) {
            txtMessage.text = "Material berhasil diterima dengan pemeriksaan"
        } else {
            txtMessage.text = "Material berhasil diterima tanpa pemeriksaan"
        }

        btnOk.setOnClickListener {
            dialog.dismiss();
            startActivity(
                Intent(this@DetailPenerimaanActivity, PenerimaanActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
        }
        dialog.show();
    }

    private fun validComplaint() {
        var data = daoSession.tPosDetailPenerimaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.NoDoSmar.eq(
                    noDo
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsDone.eq(
                    0
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsChecked.eq(
                    1
                )
            ).list()

        if (isDone == 1 || checkSn == 0) {
            Toast.makeText(
                this@DetailPenerimaanActivity,
                "DO ini sudah di terima",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (data.size > 0) {
            for (i in data) {
                if (i.statusPenerimaan == "SESUAI") {
                    Toast.makeText(
                        this@DetailPenerimaanActivity,
                        "Tidak boleh melakukan komplain dengan status sesuai atau kosong",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
            }
        } else {
            Toast.makeText(
                this@DetailPenerimaanActivity,
                "Harap pilih serial number yang tidak sesuai",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        startActivity(
            Intent(this@DetailPenerimaanActivity, ComplaintActivity::class.java)
                .putExtra("noDo", noDo)
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
                val listPackagings = daoSession.tPosDetailPenerimaanDao.queryBuilder().where(
                    dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.NoPackaging.eq(
                        result.contents
                    )
                ).list()
                for (i in listPackagings) {
                    if (i.isDone != 1) {
                        i.statusPenerimaan = "SESUAI"
                        i.isChecked = 1
                        daoSession.tPosDetailPenerimaanDao.update(i)
                    }
                }
                adapter.setData(listDetailPen)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val barcodeLauncherSn = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        try {
            if (!result.contents.isNullOrEmpty()) {
                val listSns = daoSession.tPosDetailPenerimaanDao.queryBuilder()
                    .where(
                        dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.SerialNumber.eq(
                            result.contents
                        )
                    ).limit(1).unique()

                if (listSns.isDone != 1) {
                    listSns.statusPenerimaan = "SESUAI"
                    listSns.isChecked = 1
                    daoSession.tPosDetailPenerimaanDao.update(listSns)
                }

                adapter.setData(listDetailPen)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        val dialog = Dialog(this@DetailPenerimaanActivity)
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
            for (i in listDetailPen) {
                if (i.isDone != 1) {
                    i.statusPenerimaan = ""
                    i.isChecked = 0
                    i.partialCode = ""
                    daoSession.update(i)
                }
            }

            dialog.dismiss()
        }

        dialog.show();
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

    override fun setFinish(result: Boolean, message: String) {
        if (result) {
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}