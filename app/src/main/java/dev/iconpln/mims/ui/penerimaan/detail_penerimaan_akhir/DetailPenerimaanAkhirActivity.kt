package dev.iconpln.mims.ui.penerimaan.detail_penerimaan_akhir

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.data.scan.CustomScanActivity
import dev.iconpln.mims.data.scan.CustomScanBarcodeActivity
import dev.iconpln.mims.databinding.ActivityDetailPenerimaanAkhirBinding
import dev.iconpln.mims.ui.penerimaan.detail_penerimaan.DetailPenerimaanDokumentasiAdapter
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailPenerimaanAkhirActivity : BaseActivity() {
    private lateinit var binding: ActivityDetailPenerimaanAkhirBinding
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private lateinit var adapter: DetailPenerimaanAkhirAdapter
    private lateinit var penerimaan: dev.iconpln.mims.data.local.database.TPosPenerimaan
    private lateinit var listDokumentasi: List<String>
    private var noDo = ""
    private lateinit var dataPenerimaanAkhir: List<dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhir>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPenerimaanAkhirBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!
        noDo = intent.getStringExtra("noDo")!!
        getDokumentasi(noDo)

        dataPenerimaanAkhir = daoSession.tPosDetailPenerimaanAkhirDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhirDao.Properties.NoDoSmar.eq(
                    noDo
                )
            ).list()

        penerimaan = daoSession.tPosPenerimaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosPenerimaanDao.Properties.NoDoSmar.eq(
                    noDo
                )
            ).limit(1).unique()

        adapter = DetailPenerimaanAkhirAdapter(
            arrayListOf(),
            object : DetailPenerimaanAkhirAdapter.OnAdapterListener {
                override fun onClick(po: dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhir) {}

            },
            daoSession
        )

        adapter.setData(dataPenerimaanAkhir)

        with(binding) {
            btnBack.setOnClickListener { onBackPressed() }
            rvListSn.adapter = adapter
            rvListSn.layoutManager = LinearLayoutManager(
                this@DetailPenerimaanAkhirActivity,
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
                val dialog = Dialog(this@DetailPenerimaanAkhirActivity)
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
                    this@DetailPenerimaanAkhirActivity,
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

            cbSesuai.isEnabled = false
            cbTidakSesuai.isEnabled = false

            btnKomplain.isEnabled = false
            btnKomplain.setBackgroundColor(Color.GRAY)
            btnTerima.isEnabled = false
            btnTerima.setBackgroundColor(Color.GRAY)

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
                    val searchSnPenerimaanAkhir =
                        daoSession.tPosDetailPenerimaanAkhirDao.queryBuilder()
                            .where(
                                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhirDao.Properties.NoDoSmar.eq(
                                    noDo
                                )
                            )
                            .whereOr(
                                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhirDao.Properties.NoPackaging.like(
                                    "%" + s.toString() + "%"
                                ),
                                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhirDao.Properties.SerialNumber.like(
                                    "%" + s.toString() + "%"
                                )
                            )
                            .list()

                    adapter.setData(searchSnPenerimaanAkhir)
                }

            })

            btnScanPackaging.setOnClickListener {
                val dialog = BottomSheetDialog(
                    this@DetailPenerimaanAkhirActivity,
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
                    this@DetailPenerimaanAkhirActivity,
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

    }

    private fun getDokumentasi(noDo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                ApiConfig.getApiService(this@DetailPenerimaanAkhirActivity).getDokumentasi(noDo)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                        this@DetailPenerimaanAkhirActivity,
                        Config.IS_LOGIN_SSO, 0
                    )
                    try {
                        if (response.body()?.status == Config.KEY_SUCCESS) {
                            listDokumentasi = response.body()?.doc?.array!!
                        } else {
                            when (response.body()?.message) {
                                Config.DO_LOGOUT -> {
                                    Helper.logout(this@DetailPenerimaanAkhirActivity, isLoginSso)
                                    Toast.makeText(
                                        this@DetailPenerimaanAkhirActivity,
                                        getString(R.string.session_kamu_telah_habis),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> Toast.makeText(
                                    this@DetailPenerimaanAkhirActivity,
                                    response.body()?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@DetailPenerimaanAkhirActivity,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        e.printStackTrace()
                    } finally {
                    }
                } else {
                    Toast.makeText(
                        this@DetailPenerimaanAkhirActivity,
                        response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
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
                binding.srcNoSn.setText(result.contents)

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
                binding.srcNoSn.setText(result.contents)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}