package dev.iconpln.mims

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dev.iconpln.mims.data.local.database.DaoSession
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.data.scan.CustomScanActivity
import dev.iconpln.mims.data.scan.CustomScanBarcodeActivity
import dev.iconpln.mims.databinding.ActivityHomeBinding
import dev.iconpln.mims.ui.home.HomeFragment
import dev.iconpln.mims.ui.penerimaan.PenerimaanViewModel
import dev.iconpln.mims.ui.profile.ProfileFragment
import dev.iconpln.mims.ui.tracking.DataMaterialTrackingActivity
import dev.iconpln.mims.ui.tracking.SpecMaterialActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : BaseActivity() {
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: PenerimaanViewModel by viewModels()
    private lateinit var daoSession: DaoSession
    private lateinit var dialog: Dialog
    private var jwt = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!
        val penerimaan = daoSession.tPosPenerimaanDao.queryBuilder().list()
        jwt = SharedPrefsUtils.getStringPreference(this@HomeActivity, "jwt", "")!!

        dialog = Dialog(this@HomeActivity)
        dialog.setContentView(R.layout.popup_loading)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown

        viewModel.getPenerimaan(daoSession, penerimaan)

        chipNav()

        binding.fab1.setOnClickListener {
            val dialog = BottomSheetDialog(this@HomeActivity, R.style.AppBottomSheetDialogTheme)
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

//        val navView: BottomNavigationView = binding.navViewPabrikan
//        navView.itemIconTintList = null;
//
//        val navController = findNavController(R.id.nav_host_fragment_activity_bottom_navigation)
//        navView.setupWithNavController(navController)
    }

    companion object {
        private var instance: HomeActivity? = null
        fun getInstance() = instance ?: HomeActivity().also {
            instance = it
        }

        var data: Any? = null
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
                val sn = result.contents
                val apiService = ApiConfig.getApiService(this@HomeActivity)
                CoroutineScope(Dispatchers.IO).launch {
                    val response = apiService.getTrackingHistory(sn)
                    withContext(Dispatchers.Main) {
                        dialog.show()
                        try {
                            if (response.isSuccessful) {
                                dialog.dismiss()
                                val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                                    this@HomeActivity,
                                    Config.IS_LOGIN_SSO, 0
                                )
                                if (response.body()?.datas.isNullOrEmpty()) {
                                    Toast.makeText(
                                        this@HomeActivity,
                                        response.body()?.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    dialog.dismiss()
                                    when (response.body()?.status) {
                                        Config.KEY_SUCCESS -> {
                                            val intent = Intent(
                                                this@HomeActivity,
                                                SpecMaterialActivity::class.java
                                            )
                                            intent.putExtra(
                                                DataMaterialTrackingActivity.EXTRA_SN,
                                                sn
                                            )
                                            startActivity(intent)
                                        }

                                        Config.KEY_FAILURE -> {
                                            if (response.body()?.message == Config.DO_LOGOUT) {
                                                Helper.logout(this@HomeActivity, isLoginSso)
                                                Toast.makeText(
                                                    this@HomeActivity,
                                                    getString(R.string.session_kamu_telah_habis),
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            } else Toast.makeText(
                                                this@HomeActivity,
                                                response.body()?.message,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            } else {
                                dialog.dismiss()
                                Toast.makeText(
                                    this@HomeActivity,
                                    "Data tidak ditemukan",
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@HomeActivity, e.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun chipNav() {
        binding.bottomNavPabrikan1.setOnItemSelectedListener {
            when (it) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_activity_bottom_navigation, HomeFragment())
                        .commit()
                }

                R.id.navigation_profile -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.nav_host_fragment_activity_bottom_navigation,
                        ProfileFragment()
                    ).commit()
                }
            }
        }
    }
}