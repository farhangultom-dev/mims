package dev.iconpln.mims.ui.pengiriman

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.data.local.database.TPosSns
import dev.iconpln.mims.databinding.ActivityDetailPengirimanBinding
import dev.iconpln.mims.utils.Config

class DetailPengirimanActivity : BaseActivity() {
    private val viewModel: PengirimanViewModel by viewModels()
    private lateinit var binding: ActivityDetailPengirimanBinding
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private lateinit var rvAdapter: ListDetailPengirimanAdapter
    private var noSn = ""
    private var noPengiriman = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPengirimanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!

        initData()
        initView()
    }

    private fun initView() {
        viewModel.pengirimanResponse.observe(this) {
            binding.apply {
                it.forEach { data ->
                    txtIsiNopo.text = data.poSapNo
                    txtIsiNotlsk.text = data.tlskNo
                    txtIsiUnit.text = data.plantName
                    txtIsiPlant.text = data.planCodeNo
                    txtIsiDo.text = data.noDoSmar
                    txtIsiTglpengiriman.text = data.createdDate
                    txtIsiPetugasPengiriman.text = data.kurirPengantar
                    txtIsiEkspedisi.text = data.expeditions
                }
            }
        }

        viewModel.pengirimanDetailResponse.observe(this@DetailPengirimanActivity) {
            if (it.isNullOrEmpty()) binding.noData.visibility =
                View.VISIBLE else binding.noData.visibility = View.GONE
            binding.totalData.text = "Total : ${it.size} Data"
            rvAdapter.setDetailPengirimanList(it)

        }

        rvAdapter = ListDetailPengirimanAdapter(
            arrayListOf(),
            object : ListDetailPengirimanAdapter.OnAdapterListener {
                override fun onClick(data: TPosSns) {}
            })

        binding.rvDetailMaterial.apply {
            adapter = rvAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@DetailPengirimanActivity)
        }
        binding.srcSnMaterial.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                noSn = s.toString()
                viewModel.getDataDetailPengiriman(daoSession, noPengiriman, noSn)
            }
        })

        binding.btnClose.setOnClickListener { finish() }
    }

    private fun initData() {
        val extras = intent.extras
        noPengiriman = extras?.getString(Config.EXTRA_NO_PENGIRIMAN)!!

        viewModel.getDataByNoPengiriman(daoSession, noPengiriman)
        viewModel.getDataDetailPengiriman(daoSession, noPengiriman, noSn)
    }
}