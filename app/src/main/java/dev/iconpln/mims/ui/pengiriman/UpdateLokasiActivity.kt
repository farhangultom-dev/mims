package dev.iconpln.mims.ui.pengiriman

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.R
import dev.iconpln.mims.data.local.database.DaoSession
import dev.iconpln.mims.data.local.database.TPosDao
import dev.iconpln.mims.data.remote.response.DatasItemLokasi
import dev.iconpln.mims.databinding.ActivityUpdateLokasiBinding
import dev.iconpln.mims.utils.Config

class UpdateLokasiActivity : BaseActivity() {
    private val viewModel: PengirimanViewModel by viewModels()
    private lateinit var rvAdapter: HistoryAdapter
    private lateinit var binding: ActivityUpdateLokasiBinding
    private lateinit var daoSession: DaoSession
    private var doMims = ""
    private var kodeStatus = ""
    private var noDo = ""
    private var noPo = ""
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateLokasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!

        initData()
        initView()

    }

    private fun initData() {
        doMims = intent.getStringExtra(Config.KEY_NO_DO)!!
        kodeStatus = intent.getStringExtra(Config.KEY_CODE_STATUS)!!
        noDo = intent.getStringExtra(Config.KEY_DO_SMAR)!!
        noPo = intent.getStringExtra(Config.KEY_NO_PO)!!

        viewModel.getLokasi(this@UpdateLokasiActivity, doMims)

    }

    private fun initView() {
        dialog = Dialog(this@UpdateLokasiActivity)
        dialog.setContentView(R.layout.popup_loading)
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown

        viewModel.datasLokasi.observe(this@UpdateLokasiActivity) {
            if (it.isNullOrEmpty()) {
                binding.noData.visibility = View.VISIBLE
            } else {
                binding.noData.visibility = View.GONE
                val pengiriman = daoSession.tPosDao.queryBuilder()
                    .where(TPosDao.Properties.NoDoMims.eq(doMims)).list()[0]

                if (it.size < 2) {
                    pengiriman.kodeStatusDoMims = "100"
                    pengiriman.doStatus = "DIKIRIM"
                    pengiriman.backgroundColor = "#43CDC305"
                    pengiriman.textColor = "#DDCDC305"
                    daoSession.tPosDao.update(pengiriman)
                } else {
                    pengiriman.kodeStatusDoMims = "102"
                    pengiriman.doStatus = "DALAM PERJALANAN"
                    pengiriman.backgroundColor = "#6805136E"
                    pengiriman.textColor = "#05136E"
                    daoSession.tPosDao.update(pengiriman)
                }

                rvAdapter.setData(it)
            }
        }

        viewModel.genericResponse.observe(this@UpdateLokasiActivity) {
            if (it.status == Config.KEY_SUCCESS) {
                viewModel.getLokasi(this@UpdateLokasiActivity, doMims)

            } else Toast.makeText(this@UpdateLokasiActivity, it.message, Toast.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this@UpdateLokasiActivity) {
            if (it) dialog.show() else dialog.dismiss()
        }

        rvAdapter = HistoryAdapter(arrayListOf(), object : HistoryAdapter.OnAdapterListener {
            override fun onClick(data: DatasItemLokasi) {
                if (kodeStatus == Config.KEY_DITERIMA) {
                    Toast.makeText(
                        this@UpdateLokasiActivity,
                        getString(R.string.tidak_dapat_melakukan_delete_lokasi_karena_do_ini_sudah_berstatus_received),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (data.ket == Config.KEY_MATERIAL_DIKIRIM) {
                    Toast.makeText(
                        this@UpdateLokasiActivity,
                        getString(R.string.tidak_dapat_menghapus_status_default),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    deleteLokasi(data)
                }
            }
        })

        binding.rvHistory.apply {
            adapter = rvAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@UpdateLokasiActivity)
        }

        binding.btnUpdate.setOnClickListener {
            validateForm()
        }

        binding.btnClose.setOnClickListener {
            finish()
        }
    }

    private fun deleteLokasi(data: DatasItemLokasi) {
        val dialog = Dialog(this@UpdateLokasiActivity)
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

        message.text = getString(R.string.yakin_untuk_untuk_menghapus)
        txtMessage.text = getString(R.string.jika_ya_maka_lokasi_akan_di_hapus)
        icon.setImageResource(R.drawable.ic_warning)

        btnYa.setOnClickListener {
            viewModel.deleteLokasi(data, this@UpdateLokasiActivity)
            dialog.dismiss();

        }

        btnTidak.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show();
    }

    private fun validateForm() {
        val lokasi = binding.txtLokasi.text.toString()

        if (binding.txtLokasi.text.toString().isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.harap_isi_lokasi_terkini), Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (kodeStatus == Config.KEY_DITERIMA) {
            Toast.makeText(
                this,
                getString(R.string.tidak_dapat_melakukan_update_lokasi_karena_do_ini_sudah_berstatus_received),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        viewModel.addLokasi(this@UpdateLokasiActivity, lokasi, doMims!!, noDo, noPo, daoSession)
    }

}