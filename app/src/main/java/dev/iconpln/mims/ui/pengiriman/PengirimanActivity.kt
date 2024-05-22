package dev.iconpln.mims.ui.pengiriman

import android.R
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.databinding.ActivityPengirimanBinding
import dev.iconpln.mims.utils.Config

class PengirimanActivity : BaseActivity() {
    private val viewModel: PengirimanViewModel by viewModels()
    private lateinit var rvAdapter: ListPengirimanAdapter
    private lateinit var binding: ActivityPengirimanBinding
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private var noDo = ""
    private var noPo = ""
    private var kodeStatus = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengirimanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!
        initData()
        initView()

    }

    private fun initView() {
        rvAdapter = ListPengirimanAdapter(
            this@PengirimanActivity,
            arrayListOf(),
            object : ListPengirimanAdapter.OnAdapterListener {
                override fun onClick(data: dev.iconpln.mims.data.local.database.TPos) {
                    val intent =
                        Intent(this@PengirimanActivity, DetailPengirimanActivity::class.java)
                    intent.putExtra(Config.EXTRA_NO_PENGIRIMAN, data.noDoSmar)
                    startActivity(intent)
                }
            })

        binding.rvPengiriman.apply {
            adapter = rvAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@PengirimanActivity)
        }

        viewModel.pengirimanResponse.observe(this@PengirimanActivity) {
            if (it.isNullOrEmpty()) binding.noData.visibility =
                View.VISIBLE else binding.noData.visibility = View.GONE
            binding.totalData.text = "Total : ${it.size.toString()} Data"
            binding.rvPengiriman.apply {
                adapter = null
                layoutManager = null

                adapter = rvAdapter
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(this@PengirimanActivity)
            }
            rvAdapter.setPengirimanList(it)
        }

        binding.txtPo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                noPo = s.toString()
                viewModel.getPengiriman(daoSession, noDo, noPo, kodeStatus)
            }
        })

        binding.txtDo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                noDo = s.toString()
                viewModel.getPengiriman(daoSession, noDo, noPo, kodeStatus)
            }
        })

        var statusArray = daoSession.tDoStatusDao.queryBuilder().list()
        val statusDo = statusArray.map { it.keterangan }
        val adapterStatus = ArrayAdapter(
            this@PengirimanActivity,
            R.layout.simple_dropdown_item_1line,
            statusDo.distinct()
        )
        binding.dropdownStatus.setAdapter(adapterStatus)
        binding.dropdownStatus.setOnItemClickListener { parent, view, position, id ->
            val status = statusArray[position]
            kodeStatus = status.kodeDo
            viewModel.getPengiriman(daoSession, noDo, noPo, kodeStatus)
        }
        binding.btnBack.setOnClickListener { onBackPressed() }

    }

    private fun initData() {
        viewModel.getPengiriman(daoSession, noDo, noPo, kodeStatus)
    }

    override fun onResume() {
        super.onResume()
        initData()
    }
}