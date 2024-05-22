package dev.iconpln.mims.ui.pemakaian

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.R
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.data.scan.CustomScanActivity
import dev.iconpln.mims.data.scan.CustomScanBarcodeActivity
import dev.iconpln.mims.databinding.ActivityInputSnPemakaianBinding
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InputSnPemakaianActivity : BaseActivity() {
    private lateinit var binding: ActivityInputSnPemakaianBinding
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private var noTransaksi: String = ""
    private lateinit var adapter: PemakaianUlpSnAdapter
    private lateinit var pemakaian: dev.iconpln.mims.data.local.database.TPemakaian
    private lateinit var pemakaianDetail: dev.iconpln.mims.data.local.database.TTransPemakaianDetail
    private var plant: String = ""
    private var storloc: String = ""
    private var noMat = ""
    private var username = ""
    private lateinit var lisSn: List<dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlp>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputSnPemakaianBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!

        noTransaksi = intent.getStringExtra("noTransaksi")!!

        noMat = intent.getStringExtra("noMat")!!
        plant = SharedPrefsUtils.getStringPreference(this@InputSnPemakaianActivity, "plant", "")!!
        storloc =
            SharedPrefsUtils.getStringPreference(this@InputSnPemakaianActivity, "storloc", "")!!
        username =
            SharedPrefsUtils.getStringPreference(this@InputSnPemakaianActivity, "username", "")!!

        lisSn = daoSession.tListSnMaterialPemakaianUlpDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlpDao.Properties.NoTransaksi.eq(
                    noTransaksi
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlpDao.Properties.NoMaterial.eq(
                    noMat
                )
            ).list()

        pemakaian = daoSession.tPemakaianDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPemakaianDao.Properties.NoTransaksi.eq(
                    noTransaksi
                )
            ).list().get(0)

        pemakaianDetail = daoSession.tTransPemakaianDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TTransPemakaianDetailDao.Properties.NoTransaksi.eq(
                    noTransaksi
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TTransPemakaianDetailDao.Properties.NomorMaterial.eq(
                    noMat
                )
            ).list().get(0)

        adapter =
            PemakaianUlpSnAdapter(arrayListOf(), object : PemakaianUlpSnAdapter.OnAdapterListener {
                override fun onClick(tms: dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlp) {
                    val dialog = Dialog(this@InputSnPemakaianActivity)
                    dialog.setContentView(R.layout.popup_validation);
                    dialog.window!!.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    dialog.setCancelable(false);
                    dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown;
                    val btnYa = dialog.findViewById(R.id.btn_ya) as AppCompatButton
                    val btnTidak = dialog.findViewById(R.id.btn_tidak) as AppCompatButton
                    val message = dialog.findViewById(R.id.message) as TextView
                    val txtMessage = dialog.findViewById(R.id.txt_message) as TextView
                    val icon = dialog.findViewById(R.id.imageView11) as ImageView

                    message.text = "Yakin untuk untuk menghapus?"
                    txtMessage.text = "Jika ya, maka serial number akan di hapus"
                    icon.setImageResource(R.drawable.ic_warning)

                    btnYa.setOnClickListener {
                        deleteSn(tms.noSerialNumber)
                        dialog.dismiss();

                    }

                    btnTidak.setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show();
                }

            })

        adapter.setTmsList(lisSn)

        with(binding) {

            txtNoReservasi.text = pemakaian.noReservasi
            txtNoMaterial.text = noMat

            btnScanSnMaterial.setOnClickListener {
                val dialog = BottomSheetDialog(
                    this@InputSnPemakaianActivity,
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

            btnBack.setOnClickListener { onBackPressed() }
            btnSimpan.setOnClickListener {
                simpanData()

            }

            rvListSn.adapter = adapter
            rvListSn.setHasFixedSize(true)
            rvListSn.layoutManager = LinearLayoutManager(
                this@InputSnPemakaianActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
        }
    }

    private fun simpanData() {

        val jumlahPemakaian = daoSession.tListSnMaterialPemakaianUlpDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlpDao.Properties.NoTransaksi.eq(
                    noTransaksi
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlpDao.Properties.NoMaterial.eq(
                    noMat
                )
            ).list()

        if (pemakaianDetail.qtyReservasi != jumlahPemakaian.size.toDouble()) {
            Toast.makeText(
                this@InputSnPemakaianActivity,
                "Jumlah scan masih kurang",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        pemakaianDetail.isDone = 1
        pemakaianDetail.qtyPemakaian = jumlahPemakaian.size.toDouble()
        daoSession.tTransPemakaianDetailDao.update(pemakaianDetail)

        Toast.makeText(this@InputSnPemakaianActivity, "Berhasil simpan data", Toast.LENGTH_SHORT)
            .show()
        startActivity(
            Intent(this@InputSnPemakaianActivity, DetailPemakaianUlpMandiriActivity::class.java)
                .putExtra("noTransaksi", noTransaksi)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
    }

    private fun deleteSn(sn: String) {
        val dialog = Dialog(this@InputSnPemakaianActivity)
        dialog.setContentView(R.layout.popup_loading)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown
        dialog.show()

        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiConfig.getApiService(this@InputSnPemakaianActivity)
                .deleteSn(pemakaian.noTransaksi, pemakaianDetail.nomorMaterial, sn, plant, storloc)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    dialog.dismiss()
                    val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                        this@InputSnPemakaianActivity,
                        Config.IS_LOGIN_SSO, 0
                    )
                    try {
                        if (response.body()?.status == Config.KEY_SUCCESS) {
                            showPopUpSuccess("Hapus")
                            val toDelete = daoSession.tListSnMaterialPemakaianUlpDao.queryBuilder()
                                .where(
                                    dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlpDao.Properties.NoSerialNumber.eq(
                                        sn
                                    )
                                ).list().get(0)

                            daoSession.tListSnMaterialPemakaianUlpDao.delete(toDelete)

                            val refreshList =
                                daoSession.tListSnMaterialPemakaianUlpDao.queryBuilder()
                                    .where(
                                        dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlpDao.Properties.NoTransaksi.eq(
                                            noTransaksi
                                        )
                                    )
                                    .where(
                                        dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlpDao.Properties.NoMaterial.eq(
                                            noMat
                                        )
                                    ).list()
                            adapter.setTmsList(refreshList)
                            showLoading(false)
                        } else {
                            dialog.dismiss()
                            when (response.body()?.message) {
                                Config.DO_LOGOUT -> {
                                    Helper.logout(this@InputSnPemakaianActivity, isLoginSso)
                                    Toast.makeText(
                                        this@InputSnPemakaianActivity,
                                        getString(R.string.session_kamu_telah_habis),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> Snackbar.make(
                                    binding.main,
                                    "${response.body()?.message}",
                                    Snackbar.LENGTH_SHORT
                                )
                                    .setTextMaxLines(3)
                                    .show()
                            }
                        }
                    } catch (e: Exception) {
                        dialog.dismiss()
                        Toast.makeText(
                            this@InputSnPemakaianActivity,
                            e.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        e.printStackTrace()
                    }
                } else {
                    dialog.dismiss()
                    Toast.makeText(
                        this@InputSnPemakaianActivity,
                        "Data gagal dihapus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showPopUp() {
        val dialog = Dialog(this@InputSnPemakaianActivity)
        dialog.setContentView(R.layout.inputsn_pemakaian_popupdialog);
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false);
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown;
        val btnOk = dialog.findViewById(R.id.btn_Ok) as AppCompatButton
        val etMessage = dialog.findViewById(R.id.inpt_snMaterial) as EditText

        btnOk.setOnClickListener {
            if (etMessage.text.toString().isNullOrEmpty())
                Snackbar.make(binding.main, "Kamu belum menginputkan sn", Snackbar.LENGTH_SHORT)
                    .setTextMaxLines(3)
                    .show()
            else
                sendSn(etMessage.text.toString())

            dialog.dismiss();

        }
        dialog.show();
    }

    private fun sendSn(sn: String) {
        val dialog = Dialog(this@InputSnPemakaianActivity)
        dialog.setContentView(R.layout.popup_loading)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown
        dialog.show()

        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiConfig.getApiService(this@InputSnPemakaianActivity)
                .addSn(
                    pemakaian.noTransaksi,
                    noMat,
                    sn,
                    plant,
                    storloc,
                    username,
                    valuationType = ""
                )
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                        this@InputSnPemakaianActivity,
                        Config.IS_LOGIN_SSO, 0
                    )
                    try {
                        if (response.body()?.status == Config.KEY_SUCCESS) {
                            dialog.dismiss()
                            showPopUpSuccess("Simpan")
                            val listSn =
                                dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlp()
                            listSn.isScanned = 1
                            listSn.noMaterial = noMat
                            listSn.noSerialNumber = sn
                            listSn.noTransaksi = pemakaianDetail.noTransaksi
                            daoSession.tListSnMaterialPemakaianUlpDao.insert(listSn)

                            val reloadList =
                                daoSession.tListSnMaterialPemakaianUlpDao.queryBuilder()
                                    .where(
                                        dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlpDao.Properties.NoTransaksi.eq(
                                            noTransaksi
                                        )
                                    )
                                    .where(
                                        dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlpDao.Properties.NoMaterial.eq(
                                            noMat
                                        )
                                    ).list()

                            adapter.setTmsList(reloadList)
                        } else {
                            dialog.dismiss()
                            when (response.body()?.message) {
                                Config.DO_LOGOUT -> {
                                    Helper.logout(this@InputSnPemakaianActivity, isLoginSso)
                                    Toast.makeText(
                                        this@InputSnPemakaianActivity,
                                        getString(R.string.session_kamu_telah_habis),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> Snackbar.make(
                                    binding.main,
                                    "${response.body()?.message}",
                                    Snackbar.LENGTH_SHORT
                                )
                                    .setTextMaxLines(3)
                                    .show()


                            }
                        }
                    } catch (e: Exception) {
                        dialog.dismiss()
                        Toast.makeText(
                            this@InputSnPemakaianActivity,
                            e.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                        e.printStackTrace()
                    }
                } else {
                    dialog.dismiss()
                    Toast.makeText(
                        this@InputSnPemakaianActivity,
                        "Gagal menambah serial number",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showPopUpSuccess(messages: String) {
        val dialog = Dialog(this@InputSnPemakaianActivity)
        dialog.setContentView(R.layout.popup_penerimaan)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown
        val btnOk = dialog.findViewById(R.id.btn_ok) as AppCompatButton
        val message = dialog.findViewById(R.id.textView16) as TextView
        val txtMessage = dialog.findViewById(R.id.textView22) as TextView

        message.text = "Berhasil"
        txtMessage.text = "Data material berhasil di $messages"

        btnOk.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showLoading(isShowDialog: Boolean) {
        val dialog = Dialog(this@InputSnPemakaianActivity)
        dialog.setContentView(R.layout.popup_loading)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown

        if (isShowDialog) {
            dialog.show()
            return
        }

        dialog.dismiss()

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
                sendSn(result.contents)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}