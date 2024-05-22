package dev.iconpln.mims.ui.tracking


import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.R
import dev.iconpln.mims.data.remote.DataModelHistory
import dev.iconpln.mims.data.remote.response.DatasItem
import dev.iconpln.mims.data.remote.response.HistorisItem
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivitySpecMaterialTrackingBinding
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


class SpecMaterialActivity : BaseActivity() {
    private lateinit var rvAdapter: SpecAdapter
    private lateinit var rvHistoryAdapter: HistorySpecAdapter

    private lateinit var binding: ActivitySpecMaterialTrackingBinding
    private val viewModel: TrackingHistoryViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpecMaterialTrackingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras
        val sn = extras?.getString(DataMaterialTrackingActivity.EXTRA_SN)
        if (sn != null) {
            viewModel.getTrackingHistory(sn, this)
            binding.txtSerialNumber.text = sn
        }

        rvAdapter = SpecAdapter(arrayListOf(), object : SpecAdapter.OnAdapterListener {
            override fun onClick(data: DatasItem) {

            }
        })

        rvHistoryAdapter =
            HistorySpecAdapter(arrayListOf(), object : HistorySpecAdapter.OnAdapterListener {
                override fun onClick(data: HistorisItem) {
                    showPopUp(data)
                }
            })

        binding.rvSpec.apply {
            adapter = rvAdapter
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@SpecMaterialActivity, 2)
        }

        binding.rvHistory.apply {
            adapter = rvHistoryAdapter
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(this@SpecMaterialActivity, LinearLayoutManager.VERTICAL, false)
        }
        viewModel.trackingResponse.observe(this) {
            binding.apply {
                if (it.datas.isNullOrEmpty()) {
                    Toast.makeText(
                        this@SpecMaterialActivity,
                        "Data tidak ditemukan",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    if (it.datas.size > 0) {
                        fetchDataLocal(it.datas)
                    }
                    if (it.historis.size > 0) {
                        fetchDataLocalHistory(it.historis)
                    }
                }
            }
        }
        binding.btnBack.setOnClickListener { onBackPressed() }

    }

    private fun showPopUp(data: HistorisItem) {
        val dialog = Dialog(this@SpecMaterialActivity)
        dialog.setContentView(R.layout.pop_up_tracking_history_detail)
        val btnOk = dialog.findViewById(R.id.btn_ok) as Button
        val recyclerView = dialog.findViewById(R.id.rv_detail) as RecyclerView
        val progressBar = dialog.findViewById(R.id.progressBarPopUp) as ProgressBar
        val hintKosong = dialog.findViewById(R.id.tv_kosong) as TextView
        val title = dialog.findViewById(R.id.txt_title) as TextView

        title.text = data.statusName

        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                ApiConfig.getApiService(this@SpecMaterialActivity).getDetailTrackingHistory(
                    data.serialNumber, data.nomorTransaksi, data.status
                )
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    try {
                        val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                            this@SpecMaterialActivity,
                            Config.IS_LOGIN_SSO, 0
                        )

                        progressBar.visibility = View.GONE
                        val adapterDetail = HistorySpecDetailAdapter(arrayListOf())
                        val gson = GsonBuilder().setPrettyPrinting().create()
                        var prettyJsonString = gson.toJson(response.body())

                        var arrayData = ArrayList<DataModelHistory>()
                        val jsonObj = JSONObject(prettyJsonString)
                        for (key in jsonObj.keys()) {
                            val value = jsonObj.get(key)
                            Log.d("SpecMaterialActivity", "showPopUp: apa ini key nyaa $key")
                            arrayData.add(DataModelHistory(key, value.toString()))
                        }

                        if (arrayData[0].key == "status") {
                            if (arrayData[1].value == Config.DO_LOGOUT) {
                                Helper.logout(this@SpecMaterialActivity, isLoginSso)
                            }

                            recyclerView.visibility = View.GONE
                            hintKosong.visibility = View.VISIBLE
                            title.visibility = View.GONE
                        } else {
                            recyclerView.visibility = View.VISIBLE
                            hintKosong.visibility = View.GONE
                            title.visibility = View.VISIBLE
                        }

                        adapterDetail.setData(arrayData)

                        recyclerView.adapter = adapterDetail
                        recyclerView.setHasFixedSize(true)
                        recyclerView.layoutManager = GridLayoutManager(this@SpecMaterialActivity, 2)

                    } catch (e: Exception) {
                        progressBar.visibility = View.GONE
                        e.printStackTrace()
                    }
                } else {
                    progressBar.visibility = View.GONE
                }
            }
        }

        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        val window: Window = dialog.window!!
        window.setLayout(
            WindowManager.LayoutParams.FILL_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setWindowAnimations(R.style.DialogUpDown)
        dialog.show()
    }

    fun formatString(text: String): String? {
        val json = StringBuilder()
        var indentString = ""
        var space = "\n"
        for (i in 0 until text.length) {
            val letter = text[i]
            when (letter) {
                '{', '[' -> {
                    json.append(
                        """
                        
                        $indentString$letter
                        
                        """.trimIndent()
                    )
                    indentString = indentString + "\t"
                    json.append(indentString)
                }

                '}', ']' -> {
                    indentString = indentString.replaceFirst("\t".toRegex(), "")
                    json.append(
                        """
                        
                        $indentString$letter
                        """.trimIndent()
                    )
                }

                ',' -> json.append(
                    """
                    $letter
                    $indentString
                    """.trimIndent()
                )

                else -> json.append(letter)
            }
        }
        return json.toString()
    }

    private fun fetchDataLocal(datas: List<DatasItem>) {
        rvAdapter.setData(datas)
    }

    private fun fetchDataLocalHistory(datas: List<HistorisItem>) {
        rvHistoryAdapter.setData(datas)
    }

    companion object {
        const val EXTRA_NO_DOMIMS = "extra_no_do_mims"
    }


}