package dev.iconpln.mims.data.scan

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import dev.iconpln.mims.R
import dev.iconpln.mims.databinding.ActivityCustomScanBarcodeBinding

class CustomScanBarcodeActivity : AppCompatActivity() {
    private lateinit var capture: CaptureManager
    private lateinit var barcodeScannerView: DecoratedBarcodeView
    private lateinit var binding: ActivityCustomScanBarcodeBinding
    private var flash = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomScanBarcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner)

        initializeQrScanner(savedInstanceState)

        binding.flash.setOnClickListener {
            if (!flash) {
                flash = true
                flashState(flash)
                barcodeScannerView.setTorchOn()
            } else {
                flash = false
                flashState(flash)
                barcodeScannerView.setTorchOff()
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.scannerbawah, View.TRANSLATION_Y, 1f, 580f).apply {
            duration = 1500
            repeatMode = ObjectAnimator.REVERSE
            repeatCount = ObjectAnimator.INFINITE
        }.start()
    }

    private fun flashState(flash: Boolean) {
        if (flash) {
            binding.flash.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_baseline_flash_on_24)
            )
        } else {
            binding.flash.setImageDrawable(
                ContextCompat.getDrawable(this, R.drawable.ic_baseline_flash_off_24)
            )
        }
    }

    private fun initializeQrScanner(savedInstanceState: Bundle?) = with(binding) {
        capture = CaptureManager(this@CustomScanBarcodeActivity, zxingBarcodeScanner)
        capture.initializeFromIntent(intent, savedInstanceState)
        capture.setShowMissingCameraPermissionDialog(false)
        capture.decode()
    }

    override fun onResume() {
        super.onResume()
        capture.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture.onSaveInstanceState(outState)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return binding.zxingBarcodeScanner.onKeyDown(keyCode, event) || super.onKeyDown(
            keyCode,
            event
        )
    }
}