package dev.iconpln.mims.ui.monitoring.monitoring_detail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.databinding.ActivityMonitoringDetailBinding
import dev.iconpln.mims.ui.monitoring.MonitoringViewModel
import dev.iconpln.mims.utils.Config

class MonitoringDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityMonitoringDetailBinding
    private val viewModel: MonitoringViewModel by viewModels()
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private lateinit var adapter: MonitoringDetailAdapter
    private var noDo = ""
    private var noMat = ""
    private var noPackaging = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitoringDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!

        initData()
        initView()

    }

    private fun initData() {
        noDo = intent.getStringExtra(Config.KEY_NO_DO)!!

        viewModel.getPoDetail(daoSession, noDo)
    }

    private fun initView() {

        adapter = MonitoringDetailAdapter(
            arrayListOf(),
            object : MonitoringDetailAdapter.OnAdapterListener {
                override fun onClick(data: dev.iconpln.mims.data.local.database.TPosDetail) {
                }
            })

        viewModel.detailMonitoringPOResponse.observe(this@MonitoringDetailActivity) {
            adapter.setPoList(it)

        }

        with(binding) {
            recyclerView.adapter = adapter
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(
                this@MonitoringDetailActivity,
                LinearLayoutManager.VERTICAL,
                false
            )

            srcNomorMaterial.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    noMat = s.toString()
                    viewModel.getPoDetailFilter(daoSession, noDo, noMat, noPackaging)
                }

            })

            srcNomorPackaging.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    noPackaging = s.toString()
                    viewModel.getPoDetailFilter(daoSession, noDo, noMat, noPackaging)
                }

            })

            btnClose.setOnClickListener { onBackPressed() }
        }
    }
}