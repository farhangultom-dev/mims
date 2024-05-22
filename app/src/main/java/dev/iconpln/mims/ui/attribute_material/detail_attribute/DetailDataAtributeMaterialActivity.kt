package dev.iconpln.mims.ui.attribute_material.detail_attribute

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.data.remote.response.MaterialDetailDatasItem
import dev.iconpln.mims.databinding.ActivityDetailDataAtributeMaterialBinding
import dev.iconpln.mims.ui.attribute_material.MaterialViewModel
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.SharedPrefsUtils

class DetailDataAtributeMaterialActivity : BaseActivity() {
    private lateinit var binding: ActivityDetailDataAtributeMaterialBinding
    private val viewModel: MaterialViewModel by viewModels()
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private lateinit var adapter: DetailDataAttributeAdapter
    private var sn = ""
    private var noMaterial = ""
    private var noBatch = ""
    private var kdPabrikan = ""
    private var tahun = 0
    private var page = 1
    private var limit = 20
    private var isNextPage = false
    private var isTheLastPageData = false
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailDataAtributeMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
    }

    private fun initData() {
        daoSession = (application as MyApplication).daoSession!!
        noMaterial = intent.getStringExtra(Config.KEY_NOMOR_MATERIAL)!!
        noBatch = intent.getStringExtra(Config.KEY_NOMOR_BATCH)!!
        tahun = intent.getIntExtra(Config.KEY_TAHUN_SN, 0)
        kdPabrikan = SharedPrefsUtils.getStringPreference(
            this@DetailDataAtributeMaterialActivity,
            Config.KEY_KODE_USER,
            ""
        ).toString()

        viewModel.getDetailMaterial(
            this@DetailDataAtributeMaterialActivity,
            kdPabrikan,
            noMaterial,
            sn,
            noBatch,
            limit.toString(),
            page.toString(),
            tahun
        )
    }

    private fun initView() {
        adapter = DetailDataAttributeAdapter(
            arrayListOf(),
            object : DetailDataAttributeAdapter.OnAdapterListener {
                override fun onClick(datas: MaterialDetailDatasItem) {}

            })

        viewModel.isLoading.observe(this@DetailDataAtributeMaterialActivity) {
            isLoading = it
            when (it) {
                true -> binding.pgLoading.visibility = View.VISIBLE
                false -> binding.pgLoading.visibility = View.GONE
            }
        }

        viewModel.materialDataDetailResponse.observe(this@DetailDataAtributeMaterialActivity) {
            if (!it.datas.isNullOrEmpty()) {
                if (!isNextPage) {
                    isNextPage = true
                    adapter.setMaterialList(it.datas)
                    binding.totalData.text = "Total Data : ${it.totalData}"
                } else {
                    adapter.updateMaterialList(it.datas)
                }
                binding.rvDetailMaterial.visibility = View.VISIBLE
                binding.noData.visibility = View.GONE
            } else {
                isTheLastPageData = true
                if (!isNextPage) {
                    binding.rvDetailMaterial.visibility = View.INVISIBLE
                    binding.noData.visibility = View.VISIBLE
                    binding.totalData.text = "Total Data : ${it.totalData}"
                }
            }

        }

        with(binding) {
            btnClose.setOnClickListener { finish() }

            rvDetailMaterial.adapter = adapter
            rvDetailMaterial.setHasFixedSize(true)
            rvDetailMaterial.layoutManager = LinearLayoutManager(
                this@DetailDataAtributeMaterialActivity,
                LinearLayoutManager.VERTICAL,
                false
            )

            rvDetailMaterial.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        page++
                        loadNextPage()
                    }
                }
            })

            btnSrc.setOnClickListener {
                if (!isLoading) {
                    isNextPage = false
                    isTheLastPageData = false
                    page = 1
                    viewModel.getDetailMaterial(
                        this@DetailDataAtributeMaterialActivity,
                        kdPabrikan,
                        noMaterial,
                        sn,
                        noBatch, limit.toString(), page.toString(), tahun
                    )
                }

            }

            srcSnMaterial.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    sn = s.toString()
                }

            })

            btnClose.setOnClickListener { onBackPressed() }
        }
    }

    private fun loadNextPage() {
        if (!isTheLastPageData) {
            viewModel.getDetailMaterialPaging(
                this@DetailDataAtributeMaterialActivity,
                kdPabrikan, noMaterial, sn, noBatch, limit.toString(), page.toString(), tahun
            )
        }
    }
}