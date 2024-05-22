package dev.iconpln.mims

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.Bundle
import android.os.Debug
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.scottyab.rootbeer.RootBeer
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.util.Locale

open class BaseActivity : AppCompatActivity() {

    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable
    private var mTime: Long = 1800000 //900000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isLoginSso = SharedPrefsUtils.getIntegerPreference(
            this,
            Config.IS_LOGIN_SSO, 0
        )

        mHandler = Handler(Looper.getMainLooper())
        mRunnable = Runnable {
            Helper.logout(this, isLoginSso)
            Toast.makeText(this, getString(R.string.session_kamu_telah_habis), Toast.LENGTH_LONG)
                .show()
        }

        startHandler()
    }

    private fun startHandler() {
        mHandler.postDelayed(mRunnable, mTime)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        stopHandler()

        startHandler()

        return super.onTouchEvent(event)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        stopHandler()

        startHandler()
    }

    private fun stopHandler() {
        mHandler.removeCallbacks(mRunnable)
    }

    override fun onResume() {
        super.onResume()
        startHandler()

//        if (isEmulatorPhase1() || isEmulatorPhase2() || isEmulatorPhase3()){
//            showAlert("Peringatan!","Kamu terdeteksi menggunakan emulator")
//            return
//        }
//
//        //anti debug
//        if (this.applicationContext.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE !== 0){
//            showAlert("Peringatan!","Kamu terdeteksi menggunakan debugable device")
//            return
//        }
//
//        //anti root
//        if (RootBeer(this).isRooted) {
//            showAlert("Peringatan!", "Kamu terdeteksi menggunakan root device")
//            return
//        }
//
//        //anti magisk
//        if (searchForMagisk(this)) {
//            showAlert("Peringatan!", "Kamu terdeteksi menggunakan software terlarang")
//            return
//        }
//
//        //debugger attached
//        if (isDebuggerAttached()){
//            showAlert("Peringatan!","Kamu terdeteksi sedang mencoba menyambungkan dengan perangkat debugger")
//            return
//        }
    }

    override fun onPause() {
        super.onPause()

        stopHandler()
    }

    override fun onStop() {
        super.onStop()

        stopHandler()
    }

    private fun showAlert(title: String, subtitle: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(subtitle)
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun isEmulatorPhase1(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic") ||
                Build.FINGERPRINT.startsWith("unknown") ||
                Build.MODEL.contains("google_sdk") ||
                Build.MODEL.contains("Emulator") ||
                Build.MODEL.contains("Android SDK built for x86") ||
                Build.MANUFACTURER.contains("Genymotion") ||
                Build.PRODUCT.contains("sdk") ||
                Build.PRODUCT.contains("google_sdk") ||
                Build.HARDWARE.contains("goldfish") ||
                Build.HARDWARE.contains("ranchu") ||
                Build.HARDWARE.contains("vbox86") ||
                Build.BOARD.contains("unknown") ||
                Build.BOOTLOADER.contains("android") ||
                Build.HOST.contains("android"))
    }

    private fun isEmulatorPhase2(): Boolean {
        val checkList = arrayListOf(
            "ro.kernel.qemu", "ro.product.device", "ro.hardware"
        )

        for (prop in checkList) {
            val value = getSystemProperty(prop)
            if (value?.toLowerCase(Locale.getDefault())?.contains("sdk") == true) {
                return true
            }
        }
        return false
    }

    private fun isEmulatorPhase3(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("which", "su"))
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
            bufferedReader.readLine() != null
        } catch (e: Exception) {
            false
        }
    }

    private fun getSystemProperty(name: String): String? {
        try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val getMethod = systemPropertiesClass.getMethod("get", String::class.java)
            return getMethod.invoke(systemPropertiesClass, name) as String?
        } catch (e: Exception) {
            // Handle exceptions as needed
        }
        return null
    }

    private fun searchForMagisk(context: Context): Boolean {
        val pm = context.packageManager
        @SuppressLint("QueryPermissionsNeeded") val installedPackages = pm.getInstalledPackages(0)
        for (i in installedPackages.indices) {
            val info = installedPackages[i]
            val appInfo = info.applicationInfo
            val nativeLibraryDir = appInfo.nativeLibraryDir
            val packageName = appInfo.packageName
            val f = File("$nativeLibraryDir/libstub.so")

            if (packageName == "com.topjohnwu.magisk") {
                return true
            }
        }
        return false
    }

    private fun isDebuggerAttached(): Boolean {
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger()
    }
}