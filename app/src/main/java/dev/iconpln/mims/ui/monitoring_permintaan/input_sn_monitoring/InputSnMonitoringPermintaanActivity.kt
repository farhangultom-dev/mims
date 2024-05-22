package dev.iconpln.mims.ui.monitoring_permintaan.input_sn_monitoring

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
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import de.hdodenhof.circleimageview.CircleImageView
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.R
import dev.iconpln.mims.data.local.database.TMonitoringSnMaterial
import dev.iconpln.mims.data.local.database.TMonitoringSnMaterialDao
import dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetailDao
import dev.iconpln.mims.data.remote.response.GenericResponse
import dev.iconpln.mims.data.remote.response.SnResponse
import dev.iconpln.mims.data.scan.CustomScanActivity
import dev.iconpln.mims.data.scan.CustomScanBarcodeActivity
import dev.iconpln.mims.databinding.ActivityInputSnMonitoringPermintaanBinding
import dev.iconpln.mims.ui.monitoring_permintaan.monitoring_permintaan_detail.MonitoringPermintaanDetailActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils

class InputSnMonitoringPermintaanActivity : BaseActivity() {
    private lateinit var binding: ActivityInputSnMonitoringPermintaanBinding
    private val viewModel: InputSnMonitoringPermintaanViewModel by viewModels()
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private lateinit var listSnm: List<TMonitoringSnMaterial>
    private lateinit var monitoringPenerimaan: dev.iconpln.mims.data.local.database.TTransMonitoringPermintaan
    private lateinit var adapter: MonitoringPermintaanSnAdapter
    private lateinit var dialogLoading: Dialog
    private var kodeScan = ""
    private var noPermintaan = ""
    private var noTransaksi = ""
    private var noRepackaging = ""
    private var noMaterial = ""
    private var descMaterial = ""
    private var kategori = ""
    private var storloc = ""
    private var plant = ""
    private var qtyPermintaan = 0.0
    private var roleId = 0
    private var username = ""
    private var kodeGerak = ""
    private var kodeGerakDelete = ""
    private var valuationType = ""
    private var isLoginSso = 0
    private var noIdMeter = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputSnMonitoringPermintaanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dialogLoading = Helper.loadingDialog(this)

        daoSession = (application as MyApplication).daoSession!!
        noPermintaan = intent.getStringExtra("noPermintaan").toString()
        noTransaksi = intent.getStringExtra("noTransaksi").toString()
        noRepackaging = intent.getStringExtra("noRepackaging").toString()
        noMaterial = intent.getStringExtra("noMat").toString()
        descMaterial = intent.getStringExtra("desc").toString()
        kategori = intent.getStringExtra("kategori").toString()
        qtyPermintaan = intent.getDoubleExtra("qtyPermintaan", 0.0)
        valuationType = intent.getStringExtra("valuationType").toString()
        username = SharedPrefsUtils.getStringPreference(
            this@InputSnMonitoringPermintaanActivity,
            "username",
            ""
        )!!
        isLoginSso = SharedPrefsUtils.getIntegerPreference(this, Config.IS_LOGIN_SSO, 0)

        plant = SharedPrefsUtils.getStringPreference(
            this@InputSnMonitoringPermintaanActivity,
            "plant",
            ""
        ).toString()
        storloc = SharedPrefsUtils.getStringPreference(
            this@InputSnMonitoringPermintaanActivity,
            "storloc",
            ""
        ).toString()
        roleId = SharedPrefsUtils.getIntegerPreference(
            this@InputSnMonitoringPermintaanActivity,
            "roleId",
            0
        )
        kodeGerak = if (roleId == 3) "500" else "600"
        kodeGerakDelete = if (roleId == 3) "501" else "601"

        listSnm = daoSession.tMonitoringSnMaterialDao.queryBuilder()
            .where(TMonitoringSnMaterialDao.Properties.NoRepackaging.eq(noRepackaging))
            .where(TMonitoringSnMaterialDao.Properties.NomorMaterial.eq(noMaterial))
            .where(TMonitoringSnMaterialDao.Properties.ValuationType.eq(valuationType))
            .list()

        monitoringPenerimaan = daoSession.tTransMonitoringPermintaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDao.Properties.NoTransaksi.eq(
                    noTransaksi
                )
            ).list()[0]

        adapter = MonitoringPermintaanSnAdapter(
            arrayListOf(),
            object : MonitoringPermintaanSnAdapter.OnAdapterListener {
                override fun onClick(tms: dev.iconpln.mims.data.local.database.TMonitoringSnMaterial) {
                    if (monitoringPenerimaan.kodePengeluaran == "2") {
                        Toast.makeText(
                            this@InputSnMonitoringPermintaanActivity,
                            "Tidak dapat melakukan delete serial number, karena sudah menjadi pengeluaran",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val dialog = Dialog(this@InputSnMonitoringPermintaanActivity)
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
                            kodeScan = tms.serialNumber
                            showPopuDeleteByPackagingOrSn()
                            dialog.dismiss();

                        }

                        btnTidak.setOnClickListener {
                            dialog.dismiss()
                        }

                        dialog.show();
                    }
                }

            },
            monitoringPenerimaan.kodePengeluaran
        )

        adapter.setTmsList(listSnm)

        if (listSnm.isNotEmpty()) binding.viewDataKosong.visibility =
            View.GONE else binding.viewDataKosong.visibility = View.VISIBLE

        with(binding) {
            txtDescMaterial.text = descMaterial
            txtKategori.text = kategori
            txtNoMaterial.text = noMaterial
            txtUnitAsal.text = monitoringPenerimaan.storLocAsalName
            txtQtyPermintaan.text =
                if (Helper.hasDecimal(qtyPermintaan) == true) qtyPermintaan.toString() else qtyPermintaan.toInt()
                    .toString()
            txtQtyScanned.text =
                if (Helper.hasDecimal(qtyPermintaan) == true) "${listSnm.size.toDouble()}" else "${listSnm.size}"

            if (monitoringPenerimaan.kodePengeluaran == "2") {
                btnScanSnMaterial.visibility = View.GONE
                btnInputSnManual.visibility = View.GONE
                lblScan.visibility = View.GONE
            } else {
                btnScanSnMaterial.visibility = View.VISIBLE
                btnInputSnManual.visibility = View.VISIBLE
                lblScan.visibility = View.VISIBLE
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
                    val searchListSnm = daoSession.tMonitoringSnMaterialDao.queryBuilder()
                        .where(TMonitoringSnMaterialDao.Properties.NoRepackaging.eq(noRepackaging))
                        .where(TMonitoringSnMaterialDao.Properties.NomorMaterial.eq(noMaterial))
                        .where(TMonitoringSnMaterialDao.Properties.SerialNumber.like("%" + s.toString() + "%"))
                        .list()

                    adapter.setTmsList(searchListSnm)
                    if (searchListSnm.isNotEmpty()) binding.viewDataKosong.visibility =
                        View.GONE else binding.viewDataKosong.visibility = View.VISIBLE
                }

            })

            btnBack.setOnClickListener { onBackPressed() }

            rvListSn.adapter = adapter
            rvListSn.layoutManager = LinearLayoutManager(
                this@InputSnMonitoringPermintaanActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            rvListSn.setHasFixedSize(true)

            btnScanSnMaterial.setOnClickListener {
                btnScanSnMaterial.isEnabled = false
                showDialogScanType()
            }

            btnInputSnManual.setOnClickListener {
                btnInputSnManual.isEnabled = false
                showPopUp()
            }

            btnDeleteAll.setOnClickListener {
                if (listSnm.isNotEmpty()) {
                    btnDeleteAll.isEnabled = false
                    showPopUpDeleteAll()
                } else {
                    Toast.makeText(
                        this@InputSnMonitoringPermintaanActivity,
                        "List material masih kosonng",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            viewModel.isLoading.observe(this@InputSnMonitoringPermintaanActivity) {
                if (it) {
                    dialogLoading.show()
                } else {
                    dialogLoading.dismiss()
                }
            }

            viewModel.deleteAllSnResponse.observe(this@InputSnMonitoringPermintaanActivity) {
                doDeleteAllSn(it)
            }

            viewModel.deleteSnByCodeResponse.observe(this@InputSnMonitoringPermintaanActivity) {
                doDeleteSnByCode(it)
            }

            viewModel.addSnByCodeResponse.observe(this@InputSnMonitoringPermintaanActivity) {
                doAddSn(it)
            }
        }
    }

    private fun doAddSn(it: SnResponse?) {
        try {
            if (it?.status == Config.KEY_SUCCESS) {
                showPopUpSuccess("Simpan")
                val serialNumbers = it.serialNumbers

                if (serialNumbers != null) {
                    val size = serialNumbers.size
                    if (size > 0) {
                        val items = arrayOfNulls<TMonitoringSnMaterial>(size)
                        var item: TMonitoringSnMaterial
                        for ((i, model) in serialNumbers.withIndex()) {
                            item = TMonitoringSnMaterial()
                            item.noRepackaging = noRepackaging
                            item.isScanned = 1
                            item.serialNumber = model.serialNumber
                            item.nomorMaterial = model.noMaterial
                            item.noIdMeter = model.noIdMeter
                            item.packaging =
                                if (model.noPackaging.isNullOrEmpty()) "-" else model.noPackaging
                            item.status = "SESUAI"
                            item.valuationType = model.valuantionType
                            items[i] = item
//                            Log.d("TAG", "doAddSn: ${model.valuantionType}")
                        }
                        daoSession.tMonitoringSnMaterialDao.insertInTx(items.toList())
                    }

                    listSnm = daoSession.tMonitoringSnMaterialDao.queryBuilder()
                        .where(TMonitoringSnMaterialDao.Properties.NoRepackaging.eq(noRepackaging))
                        .where(TMonitoringSnMaterialDao.Properties.NomorMaterial.eq(noMaterial))
                        .where(TMonitoringSnMaterialDao.Properties.ValuationType.eq(valuationType))
                        .list()

                    adapter.setTmsList(listSnm)
                    if (listSnm.isNotEmpty()) binding.viewDataKosong.visibility =
                        View.GONE else binding.viewDataKosong.visibility = View.VISIBLE

                    binding.txtQtyScanned.text = listSnm.size.toString()

                } else {
                    Toast.makeText(
                        this@InputSnMonitoringPermintaanActivity,
                        it.message,
                        Toast.LENGTH_SHORT
                    )
                }
            } else {
                when (it?.message) {
                    Config.DO_LOGOUT -> {
                        Helper.logout(this@InputSnMonitoringPermintaanActivity, isLoginSso)
                        Toast.makeText(
                            this@InputSnMonitoringPermintaanActivity,
                            getString(R.string.session_kamu_telah_habis),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        Snackbar.make(binding.main, "${it?.message}", Snackbar.LENGTH_SHORT)
                            .setTextMaxLines(3)
                            .show()
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                this@InputSnMonitoringPermintaanActivity,
                e.toString(),
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }

    private fun doDeleteSnByCode(it: SnResponse) {
        try {
            if (it.status == Config.KEY_SUCCESS) {
                showPopUpSuccess("Hapus")

                if (it.typeScan == "PACKAGING" ||
                    it.typeScan == "REGISTRASI"
                ) {
                    val listToDelete = daoSession.tMonitoringSnMaterialDao.queryBuilder()
                        .where(TMonitoringSnMaterialDao.Properties.Packaging.eq(kodeScan))
                        .where(TMonitoringSnMaterialDao.Properties.NoRepackaging.eq(noRepackaging))
                        .where(TMonitoringSnMaterialDao.Properties.NomorMaterial.eq(noMaterial))
//                        .where(TMonitoringSnMaterialDao.Properties.ValuationType.eq(valuationType))
                        .list()

                    daoSession.tMonitoringSnMaterialDao.deleteInTx(listToDelete)

                    listSnm = daoSession.tMonitoringSnMaterialDao.queryBuilder()
                        .where(TMonitoringSnMaterialDao.Properties.NoRepackaging.eq(noRepackaging))
                        .where(TMonitoringSnMaterialDao.Properties.NomorMaterial.eq(noMaterial))
                        .where(TMonitoringSnMaterialDao.Properties.ValuationType.eq(valuationType))
                        .list()

                    adapter.setTmsList(listSnm)
                    if (listSnm.isNotEmpty()) binding.viewDataKosong.visibility =
                        View.GONE else binding.viewDataKosong.visibility = View.VISIBLE

                    val permintaanDetail =
                        daoSession.tTransMonitoringPermintaanDetailDao.queryBuilder()
                            .where(
                                TTransMonitoringPermintaanDetailDao.Properties.NoTransaksi.eq(
                                    noTransaksi
                                )
                            )
                            .where(
                                TTransMonitoringPermintaanDetailDao.Properties.NomorMaterial.eq(
                                    noMaterial
                                )
                            )
                            .list()[0]

                    permintaanDetail.qtyScan = listSnm.size.toDouble()
                    daoSession.tTransMonitoringPermintaanDetailDao.update(permintaanDetail)
                    binding.txtQtyScanned.text = listSnm.size.toString()
                } else {
                    val objectToDelete = daoSession.tMonitoringSnMaterialDao.queryBuilder()
                        .where(TMonitoringSnMaterialDao.Properties.SerialNumber.eq(kodeScan))
                        .where(TMonitoringSnMaterialDao.Properties.NoRepackaging.eq(noRepackaging))
                        .where(TMonitoringSnMaterialDao.Properties.NomorMaterial.eq(noMaterial))
                        .list()[0]

                    daoSession.tMonitoringSnMaterialDao.delete(objectToDelete)

                    listSnm = daoSession.tMonitoringSnMaterialDao.queryBuilder()
                        .where(TMonitoringSnMaterialDao.Properties.NoRepackaging.eq(noRepackaging))
                        .where(TMonitoringSnMaterialDao.Properties.NomorMaterial.eq(noMaterial))
                        .list()

                    adapter.setTmsList(listSnm)
                    if (listSnm.isNotEmpty()) binding.viewDataKosong.visibility =
                        View.GONE else binding.viewDataKosong.visibility = View.VISIBLE

                    val permintaanDetail =
                        daoSession.tTransMonitoringPermintaanDetailDao.queryBuilder()
                            .where(
                                TTransMonitoringPermintaanDetailDao.Properties.NoTransaksi.eq(
                                    noTransaksi
                                )
                            )
                            .where(
                                TTransMonitoringPermintaanDetailDao.Properties.NomorMaterial.eq(
                                    noMaterial
                                )
                            )
                            .list()[0]

                    permintaanDetail.qtyScan = listSnm.size.toDouble()
                    daoSession.tTransMonitoringPermintaanDetailDao.update(permintaanDetail)
                    binding.txtQtyScanned.text = listSnm.size.toString()
                }
            } else {
                when (it.message) {
                    Config.DO_LOGOUT -> {
                        Helper.logout(this@InputSnMonitoringPermintaanActivity, isLoginSso)
                        Toast.makeText(
                            this@InputSnMonitoringPermintaanActivity,
                            getString(R.string.session_kamu_telah_habis),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> Snackbar.make(binding.main, "${it.message}", Snackbar.LENGTH_SHORT)
                        .setTextMaxLines(3)
                        .show()

                }
            }
        } catch (e: Exception) {
            Toast.makeText(
                this@InputSnMonitoringPermintaanActivity,
                e.toString(),
                Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }
    }

    private fun doDeleteAllSn(it: GenericResponse) {
        if (it.status == Config.KEY_SUCCESS) {
            showPopUpSuccess("Hapus")

            listSnm = daoSession.tMonitoringSnMaterialDao.queryBuilder()
                .where(TMonitoringSnMaterialDao.Properties.NoRepackaging.eq(noRepackaging))
                .where(TMonitoringSnMaterialDao.Properties.NomorMaterial.eq(noMaterial))
                .list()

            daoSession.tMonitoringSnMaterialDao.deleteInTx(listSnm)

            listSnm = daoSession.tMonitoringSnMaterialDao.queryBuilder()
                .where(TMonitoringSnMaterialDao.Properties.NoRepackaging.eq(noRepackaging))
                .where(TMonitoringSnMaterialDao.Properties.NomorMaterial.eq(noMaterial))
                .list()

            adapter.setTmsList(listSnm)

            if (listSnm.isNotEmpty()) binding.viewDataKosong.visibility =
                View.GONE else binding.viewDataKosong.visibility = View.VISIBLE

            val permintaanDetail = daoSession.tTransMonitoringPermintaanDetailDao.queryBuilder()
                .where(TTransMonitoringPermintaanDetailDao.Properties.NoTransaksi.eq(noTransaksi))
                .where(TTransMonitoringPermintaanDetailDao.Properties.NomorMaterial.eq(noMaterial))
                .list()[0]

            permintaanDetail.qtyScan = listSnm.size.toDouble()
            daoSession.tTransMonitoringPermintaanDetailDao.update(permintaanDetail)
            binding.txtQtyScanned.text = listSnm.size.toString()
        } else {
            when (it.message) {
                Config.DO_LOGOUT -> {
                    Helper.logout(this@InputSnMonitoringPermintaanActivity, isLoginSso)
                    Toast.makeText(
                        this@InputSnMonitoringPermintaanActivity,
                        getString(R.string.session_kamu_telah_habis),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> Snackbar.make(binding.main, "${it.message}", Snackbar.LENGTH_SHORT)
                    .setTextMaxLines(3)
                    .show()
            }
        }
    }

    private fun showPopUpDeleteAll() {
        val dialog = Dialog(this@InputSnMonitoringPermintaanActivity)
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
        txtMessage.text = "Jika ya, maka seluruh serial number akan di hapus"
        icon.setImageResource(R.drawable.ic_warning)

        dialog.setOnDismissListener {
            binding.btnDeleteAll.isEnabled = true
        }

        btnYa.setOnClickListener {
            viewModel.deleteAllSn(
                this@InputSnMonitoringPermintaanActivity,
                noRepackaging, noMaterial, plant, storloc, username
            )
            dialog.dismiss();

        }

        btnTidak.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show();
    }

    private fun showDialogScanType() {
        val dialog = BottomSheetDialog(
            this@InputSnMonitoringPermintaanActivity,
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

        dialog.setOnDismissListener {
            binding.btnScanSnMaterial.isEnabled = true
        }

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

    private fun showPopUp() {
        val dialog = Dialog(this@InputSnMonitoringPermintaanActivity)
        dialog.setContentView(R.layout.monitoring_permintaan_popdialog);
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown;
        var isTambahSn = true
        val btnTambah = dialog.findViewById(R.id.btn_tambah_sn) as CircleImageView
        val btnKurang = dialog.findViewById(R.id.btn_kurang_sn) as CircleImageView
        val btnOk = dialog.findViewById(R.id.btn_Ok) as AppCompatButton
        val btnBatal = dialog.findViewById(R.id.btn_batal) as AppCompatButton
        val etMessage = dialog.findViewById(R.id.inpt_snMaterial) as EditText
        val txtTitle = dialog.findViewById(R.id.txt_snMaterial_popup) as TextView

        etMessage.hint = "Input Serial Number / Packaging / No. Registrasi"
        txtTitle.text = "Input Material"

        dialog.setOnDismissListener {
            binding.btnInputSnManual.isEnabled = true
        }

        btnTambah.setOnClickListener {
            btnTambah.setImageResource(R.drawable.ic_plus_active)
            btnKurang.setImageResource(R.drawable.ic_minus_disable)
            isTambahSn = true
        }

        btnKurang.setOnClickListener {
            btnTambah.setImageResource(R.drawable.ic_plus_disable)
            btnKurang.setImageResource(R.drawable.ic_minus_active)
            isTambahSn = false
        }

        btnBatal.setOnClickListener {
            Snackbar.make(binding.main, "Batal input manual", Snackbar.LENGTH_SHORT)
                .setTextMaxLines(3)
                .show()
            dialog.dismiss()
        }

        btnOk.setOnClickListener {
            if (isTambahSn) {
                kodeScan = etMessage.text.toString()
                sendSn()
            } else {
                val dialog = Dialog(this@InputSnMonitoringPermintaanActivity)
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
                txtMessage.text =
                    "Jika ya, maka sn yang sesuai dengan data yang anda inputkan akan di hapus"
                icon.setImageResource(R.drawable.ic_warning)

                btnYa.setOnClickListener {
                    kodeScan = etMessage.text.toString()
                    showPopuDeleteByPackagingOrSn()
                    dialog.dismiss();

                }

                btnTidak.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show();
            }
            dialog.dismiss();
        }
        dialog.show();
    }

    private fun showPopuDeleteByPackagingOrSn() {
        viewModel.deleteSnByCode(
            this@InputSnMonitoringPermintaanActivity,
            kodeScan,
            noRepackaging,
            noMaterial,
            roleId,
            plant,
            storloc,
            username,
            kodeGerakDelete,
            valuationType
        )
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
                kodeScan = result.contents
                sendSn()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendSn() {
        viewModel.addSnByCode(
            this,
            kodeScan,
            noRepackaging,
            noMaterial,
            roleId,
            plant,
            storloc,
            username,
            kodeGerak,
            valuationType,
            noIdMeter
        )
    }

    private fun showPopUpSuccess(messages: String) {
        val dialog = Dialog(this@InputSnMonitoringPermintaanActivity)
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

    override fun onBackPressed() {
        val dialog = Dialog(this@InputSnMonitoringPermintaanActivity)
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

        icon.setImageResource(R.drawable.ic_popup_delete)
        message.text = "Keluar"
        txtMessage.text = "Yakin untuk keluar?"


        btnTidak.setOnClickListener {
            dialog.dismiss()
        }

        btnOk.setOnClickListener {
            dialog.dismiss();
            val listSnm = daoSession.tMonitoringSnMaterialDao.queryBuilder()
                .where(
                    dev.iconpln.mims.data.local.database.TMonitoringSnMaterialDao.Properties.NoRepackaging.eq(
                        noRepackaging
                    )
                )
                .where(
                    dev.iconpln.mims.data.local.database.TMonitoringSnMaterialDao.Properties.NomorMaterial.eq(
                        noMaterial
                    )
                )
                .where(
                    dev.iconpln.mims.data.local.database.TMonitoringSnMaterialDao.Properties.ValuationType.eq(
                        valuationType
                    )
                )
                .list()
//
//            daoSession.tMonitoringSnMaterialDao.deleteInTx(listSnm)

            val permintaanDetail = daoSession.tTransMonitoringPermintaanDetailDao.queryBuilder()
                .where(
                    dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetailDao.Properties.NoTransaksi.eq(
                        noTransaksi
                    )
                )
                .where(
                    dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetailDao.Properties.NomorMaterial.eq(
                        noMaterial
                    )
                )
                .list()[0]

            permintaanDetail.qtyScan = listSnm.size.toDouble()
            daoSession.tTransMonitoringPermintaanDetailDao.update(permintaanDetail)

            startActivity(
                Intent(
                    this@InputSnMonitoringPermintaanActivity,
                    MonitoringPermintaanDetailActivity::class.java
                )
                    .putExtra("noPermintaan", monitoringPenerimaan.noPermintaan)
                    .putExtra("noTransaksi", noTransaksi)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
        }
        dialog.show();
    }
}