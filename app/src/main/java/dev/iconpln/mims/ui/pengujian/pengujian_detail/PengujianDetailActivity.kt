package dev.iconpln.mims.ui.pengujian.pengujian_detail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.R
import dev.iconpln.mims.data.scan.CustomScanActivity
import dev.iconpln.mims.data.scan.CustomScanBarcodeActivity
import dev.iconpln.mims.databinding.ActivityPengujianDetailBinding

class PengujianDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityPengujianDetailBinding
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private lateinit var adapter: PengujianDetailAdapter
    private var serNumb: String = ""
    private var filter: String = "ALL"
    private var noPengujian: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengujianDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noPengujian = intent.getStringExtra("noPengujian")!!

        daoSession = (application as MyApplication).daoSession!!

        adapter = PengujianDetailAdapter(arrayListOf(),
            object : PengujianDetailAdapter.OnAdapterListener {
                override fun onClick(pengujian: dev.iconpln.mims.data.local.database.TPengujianDetails) {}

            })

        fetchLocalData(noPengujian)

        with(binding) {

            btnScan.setOnClickListener {
                val dialog = BottomSheetDialog(
                    this@PengujianDetailActivity,
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

            btnClose.setOnClickListener {
                onBackPressed()
            }

            rvPengujianDetail.adapter = adapter
            rvPengujianDetail.setHasFixedSize(true)
            rvPengujianDetail.layoutManager = LinearLayoutManager(
                this@PengujianDetailActivity,
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
                    serNumb = s.toString()
                    doSearch()
                }

            })

            tabLayout.addOnTabSelectedListener(object :
                com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                    if (tab?.text == "All") {
                        filter = tab.text.toString().uppercase()
                        doSearch()
                    } else if (tab?.text == "Lolos") {
                        filter = tab.text.toString().uppercase()
                        doSearch()
                    } else if (tab?.text == "Tidak Lolos") {
                        filter = tab.text.toString().uppercase()
                        doSearch()
                    } else if (tab?.text == "Rusak") {
                        filter = tab.text.toString().uppercase()
                        doSearch()
                    }
                }

                override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}

                override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}

            })
        }
    }

    private fun doSearch() {
        if (filter == "ALL") {
            val list = daoSession.tPengujianDetailsDao.queryBuilder()
                .where(
                    dev.iconpln.mims.data.local.database.TPengujianDetailsDao.Properties.NoPengujian.eq(
                        noPengujian
                    )
                )
                .where(
                    dev.iconpln.mims.data.local.database.TPengujianDetailsDao.Properties.SerialNumber.like(
                        "%" + serNumb + "%"
                    )
                ).list()

            adapter.setPengujianList(list)
        } else {
            val list = daoSession.tPengujianDetailsDao.queryBuilder()
                .where(
                    dev.iconpln.mims.data.local.database.TPengujianDetailsDao.Properties.NoPengujian.eq(
                        noPengujian
                    )
                )
                .where(
                    dev.iconpln.mims.data.local.database.TPengujianDetailsDao.Properties.SerialNumber.like(
                        "%" + serNumb + "%"
                    )
                )
                .where(
                    dev.iconpln.mims.data.local.database.TPengujianDetailsDao.Properties.StatusUji.eq(
                        filter
                    )
                ).list()

            adapter.setPengujianList(list)
        }

    }

    private fun fetchLocalData(noPengujian: String) {
        val list = daoSession.tPengujianDetailsDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPengujianDetailsDao.Properties.NoPengujian.eq(
                    noPengujian
                )
            ).list()
        adapter.setPengujianList(list)
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
}