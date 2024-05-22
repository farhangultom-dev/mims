package dev.iconpln.mims.ui.transmission_history

import android.app.ActivityManager
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.R
import dev.iconpln.mims.data.local.database_local.ReportUploader
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.ConnectionDetectorUtil
import dev.iconpln.mims.utils.DataController
import dev.iconpln.mims.utils.DataHelper
import dev.iconpln.mims.utils.EmailUtils2
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import dev.iconpln.mims.utils.StorageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.io.File
import java.util.Arrays

class TransmissionActivity : BaseActivity() {

    private lateinit var reciever: MyBroadcastReciever
    private lateinit var toSend: Array<String>
    private lateinit var sent: Array<String>

    private var adapterTerkirim: ArrayAdapter<String>? = null
    private var adapterBelumTerkirim: ArrayAdapter<String>? = null
    private var databaseReport: dev.iconpln.mims.data.local.database_local.DatabaseReport? = null
    private var listViewtoBeSent: ListView? = null
    private var listViewSent: ListView? = null
    private var switchOn = false
    private var sending = false

    private lateinit var btn: Button

    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession

    //region Privilege Variables
    private lateinit var mPrivileges: Map<String, String>
    private var mPageTitle: String = ""
    private var mEmailSupport: String = "support.mims@gmail.com"

    //endregion
    private lateinit var btnForceSending: ImageButton
    private lateinit var txtSending: TextView

    //    private lateinit var pgLoading: ProgressBar
    private lateinit var btnBack: ImageView
    private lateinit var btnSendEmail: ImageButton

    private lateinit var dialogLoading: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transmission_history)
        dialogLoading = Helper.loadingDialog(this)
        initData()
        init()
        forceSending()
    }

    private fun initData() {
        daoSession = (application as MyApplication).daoSession!!
        btnForceSending = findViewById(R.id.btn_force_sending)
        btnBack = findViewById(R.id.btn_back)
        txtSending = findViewById(R.id.txtSending)
//        pgLoading = findViewById(R.id.pgLoading)
        btnSendEmail = findViewById(R.id.btn_send_email)

        btnSendEmail.setOnClickListener { sendEmail() }
        btnBack.setOnClickListener { onBackPressed() }

        dialogLoading.show()
    }

    private fun forceSending() {
        btnForceSending.setOnClickListener {
            val connectionDetector = ConnectionDetectorUtil(this@TransmissionActivity)
            if (!sending && connectionDetector.isConnectingToInternet) {
                val dialog = Dialog(this@TransmissionActivity)
                dialog.setContentView(R.layout.popup_force_sending);
                dialog.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                );
                dialog.setCancelable(false);
                dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown;
                val btnYa = dialog.findViewById(R.id.btn_ya) as AppCompatButton
                val btnTidak = dialog.findViewById(R.id.btn_tidak) as AppCompatButton
                btnTidak.setOnClickListener {
                    dialog.dismiss()
                }
                btnYa.setOnClickListener {
                    dialog.dismiss()
                    if (!databaseReport!!.isTransmitionNotSendExist()) {
                        txtSending.visibility = View.GONE
//                        pgLoading.visibility = View.GONE
                        Toast.makeText(
                            this@TransmissionActivity,
                            "Tidak ada data yang harus di kirim, karena data telah terkirim semua",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        txtSending.text = "Force Sending. . . "
                        txtSending.visibility = View.VISIBLE
//                        pgLoading.visibility = View.VISIBLE

                        GlobalScope.launch(Dispatchers.IO) { sendReport() }
                    }
                    dialog.dismiss()
                }
                dialog.show()
            } else if (!sending && !connectionDetector.isConnectingToInternet) {
                Toast.makeText(
                    this@TransmissionActivity,
                    "Anda tidak terhubung ke Server. Aktifkan internet terlebih dahulu",
                    Toast.LENGTH_LONG
                ).show()
            } else if (sending && connectionDetector.isConnectingToInternet) {
                Toast.makeText(
                    this@TransmissionActivity,
                    "Proses pengiriman data sedang berjalan, harap di tunggu hingga semua data terkirim",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this@TransmissionActivity,
                    "Anda tidak terhubung ke Server. Aktifkan internet terlebih dahulu",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter(Intent.ACTION_SEND)
        reciever = MyBroadcastReciever(this)
        registerReceiver(reciever, filter)
        update()
    }

    private fun init() {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        listViewSent = findViewById(R.id.transmission_listView_sent)
        listViewtoBeSent = findViewById(R.id.transmission_listView_tobesent)
        listViewSent?.emptyView = DataController.getEmptyView(this)
        listViewtoBeSent?.emptyView = DataController.getEmptyView(this)

        switchOn = true

        update()
    }

    private fun sendEmail() {
        val currentDateTime = DateTime.now().toString(Config.DATE)
        val username =
            SharedPrefsUtils.getStringPreference(this@TransmissionActivity, Config.KEY_USERNAME, "")

        val appZip = DataHelper.AppZip(
            StorageUtils.getDirectory(StorageUtils.DIRECTORY_ROOT),
            Environment.getExternalStorageDirectory().toString() + "/Mims-${currentDateTime}.zip"
        )
        appZip.run()
        val mailto = arrayOf(mEmailSupport)
        EmailUtils2.sendEmail(
            this,
            mailto,
            "Mims - ${getString(R.string.app_name)} - " + username,
            "Dear Team Support,",
            File(
                Environment.getExternalStorageDirectory(), "Mims-${currentDateTime}.zip"
            )
        )
    }

    fun update() {
        if (!isLoading) {
            val thread = Thread(Runnable {
                isLoading = true
                try {
                    databaseReport =
                        dev.iconpln.mims.data.local.database_local.DatabaseReport.getDatabase(
                            applicationContext
                        )

                    toSend = dev.iconpln.mims.data.local.database_local.DatabaseReport.getDeskripsi(
                        databaseReport?.reportToBeSent!!
                    )
                    sent = dev.iconpln.mims.data.local.database_local.DatabaseReport.getDeskripsi(
                        databaseReport?.reportSent!!
                    )

                    runOnUiThread {
                        if (adapterBelumTerkirim != null) {
                            adapterBelumTerkirim!!.clear()
                            adapterBelumTerkirim!!.addAll(*toSend)
                            adapterBelumTerkirim!!.notifyDataSetChanged()
                            dialogLoading.dismiss()
                        } else {
                            val itemToSend = ArrayList(Arrays.asList(*toSend))
                            adapterBelumTerkirim = ArrayAdapter(
                                this@TransmissionActivity,
                                R.layout.activity_transmission_list_row,
                                itemToSend
                            )
                            listViewtoBeSent?.adapter = adapterBelumTerkirim
                            dialogLoading.dismiss()
                        }

                        if (adapterTerkirim != null) {
                            adapterTerkirim!!.clear()
                            adapterTerkirim!!.addAll(*sent)
                            adapterTerkirim!!.notifyDataSetChanged()
                            dialogLoading.dismiss()
                        } else {
                            val itemSent = ArrayList(Arrays.asList(*sent))
                            adapterTerkirim = ArrayAdapter(
                                this@TransmissionActivity,
                                R.layout.activity_transmission_list_row,
                                itemSent
                            )
                            listViewSent?.adapter = adapterTerkirim
                            dialogLoading.dismiss()
                        }
                    }

                    if (!databaseReport!!.isTransmitionNotSendExist()) {
                        txtSending.visibility = View.GONE
//                        pgLoading.visibility = View.GONE
                        sending = false
                        Toast.makeText(
                            this@TransmissionActivity,
                            "Semua report berhasil terkirim",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()

                } finally {
                    isLoading = false
                }
            })

            thread.start()
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(reciever)
    }

    class MyBroadcastReciever : BroadcastReceiver {
        private var act: TransmissionActivity? = null

        constructor()

        constructor(activity: TransmissionActivity) {
            act = activity
        }

        override fun onReceive(context: Context, intent: Intent) {
            try {
                context.startService(Intent(context, ReportUploader::class.java))
                act?.update()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        var isLoading: Boolean = false
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    internal suspend fun sendReport() {
        try {
            sending = true
            val databaseReport =
                dev.iconpln.mims.data.local.database_local.DatabaseReport.getDatabase(
                    applicationContext
                )
            val toBeSent = databaseReport.reportToBeSent
            for (report in toBeSent) {
                if (report.sendReport()) {
                    databaseReport.doneReport(report, 1)
                    sendResult()
                }
            }
            sending = false
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
//            sending = false
        }

    }

    private suspend fun sendResult() {
        withContext(Dispatchers.IO) {
            if (!isLoading) {
                val intent = Intent(Intent.ACTION_SEND)
                sendBroadcast(intent)
            }
        }
    }
}
