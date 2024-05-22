package dev.iconpln.mims.ui.monitoring_complaint

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.data.local.database.TMonitoringComplaint
import dev.iconpln.mims.databinding.ActivityMonitoringComplaintBinding
import dev.iconpln.mims.ui.monitoring_complaint.detail_monitoring_complaint.MonitoringComplaintDetailActivity
import dev.iconpln.mims.utils.GetDataWithPaginatianUtil
import dev.iconpln.mims.utils.SharedPrefsUtils

class MonitoringComplaintActivity : BaseActivity() {
    lateinit var binding: ActivityMonitoringComplaintBinding
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private lateinit var listComplaint: List<dev.iconpln.mims.data.local.database.TMonitoringComplaint>
    lateinit var adapter: MonitoringComplaintAdapter
    private lateinit var getDataWithPaginatianUtil: GetDataWithPaginatianUtil
    private val listStatus: ArrayList<String> = ArrayList()
    private var subrole = 0
    private var statusChcked = "ALL"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitoringComplaintBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!
        subrole = SharedPrefsUtils.getIntegerPreference(this, "subroleId", 0)
        getDataWithPaginatianUtil = GetDataWithPaginatianUtil(this@MonitoringComplaintActivity,daoSession)

        listComplaint = daoSession.tMonitoringComplaintDao.queryBuilder()
            .orderAsc(dev.iconpln.mims.data.local.database.TMonitoringComplaintDao.Properties.Status)
            .list()

        listComplaint = daoSession.tMonitoringComplaintDao.queryBuilder().list()
        listStatus.add("ALL")
        for (i in listComplaint) {
            listStatus.add(i.status)
        }

        adapter = MonitoringComplaintAdapter(
            arrayListOf(),
            object : MonitoringComplaintAdapter.OnAdapterListener {
                override fun onClick(mp: dev.iconpln.mims.data.local.database.TMonitoringComplaint) {
                    if (subrole != 3) {
                        val checkComplaintDetail =
                            daoSession.tMonitoringComplaintDetailDao.queryBuilder()
                                .where(
                                    dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.NoKomplain.eq(
                                        mp.noKomplain
                                    )
                                )
                                .where(
                                    dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.Status.eq(
                                        "SEDANG KOMPLAIN"
                                    )
                                ).list().size

                        if (checkComplaintDetail > 0) {
                            startActivity(
                                Intent(
                                    this@MonitoringComplaintActivity,
                                    MonitoringComplaintDetailActivity::class.java
                                )
                                    .putExtra("noKomplain", mp.noKomplain)
                                    .putExtra("noDo", mp.noDoSmar)
                                    .putExtra("status", mp.status)
                                    .putExtra("alasan", mp.alasan)
                            )
                        } else {
                            Toast.makeText(
                                this@MonitoringComplaintActivity,
                                "Komplain ini sudah di selesai di periksa",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        startActivity(
                            Intent(
                                this@MonitoringComplaintActivity,
                                MonitoringComplaintDetailActivity::class.java
                            )
                                .putExtra("noKomplain", mp.noKomplain)
                                .putExtra("noDo", mp.noDoSmar)
                                .putExtra("status", mp.status)
                                .putExtra("alasan", mp.alasan)
                        )
                    }
                }

            },object : MonitoringComplaintAdapter.OnAdapterListenerGetData{
                override fun onClick(mp: TMonitoringComplaint) {
                    getDataWithPaginatianUtil.getMonitoringKomplainDetail(mp)
                }

            },
            daoSession,
            subrole,this@MonitoringComplaintActivity
        )

        adapter.setComplaint(listComplaint)

        with(binding) {
            btnBack.setOnClickListener { onBackPressed() }
            tvTotalData.text = "Total data ${listComplaint.size}"
            srcNomorPoDo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val searchList = daoSession.tMonitoringComplaintDao.queryBuilder()
                        .whereOr(
                            dev.iconpln.mims.data.local.database.TMonitoringComplaintDao.Properties.NoDoSmar.like(
                                "%" + s.toString() + "%"
                            ),
                            dev.iconpln.mims.data.local.database.TMonitoringComplaintDao.Properties.PoSapNo.like(
                                "%" + s.toString() + "%"
                            )
                        )
                        .orderAsc(dev.iconpln.mims.data.local.database.TMonitoringComplaintDao.Properties.Status)
                        .list()

                    binding.rvMonitoringKomplain.adapter = null
                    binding.rvMonitoringKomplain.layoutManager = null
                    binding.rvMonitoringKomplain.adapter = adapter
                    binding.rvMonitoringKomplain.setHasFixedSize(true)
                    binding.rvMonitoringKomplain.layoutManager = LinearLayoutManager(
                        this@MonitoringComplaintActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter.setComplaint(searchList)

                    tvTotalData.text = "Total data ${searchList.size}"
                }

            })

            val statusArray = arrayOf(
                "TERBARU", "TERLAMA"
            )
            val adapterStatus = ArrayAdapter(
                this@MonitoringComplaintActivity,
                android.R.layout.simple_dropdown_item_1line,
                statusArray
            )
            dropdownUrutkan.setAdapter(adapterStatus)
            dropdownUrutkan.setOnItemClickListener { parent, view, position, id ->
                if (statusArray[position] == "TERBARU") {
                    val searchList = daoSession.tMonitoringComplaintDao.queryBuilder()
                        .orderDesc(dev.iconpln.mims.data.local.database.TMonitoringComplaintDao.Properties.TanggalPO)
                        .list()

                    binding.rvMonitoringKomplain.adapter = null
                    binding.rvMonitoringKomplain.layoutManager = null
                    binding.rvMonitoringKomplain.adapter = adapter
                    binding.rvMonitoringKomplain.setHasFixedSize(true)
                    binding.rvMonitoringKomplain.layoutManager = LinearLayoutManager(
                        this@MonitoringComplaintActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter.setComplaint(searchList)

                    tvTotalData.text = "Total data ${searchList.size}"
                } else {
                    val searchList = daoSession.tMonitoringComplaintDao.queryBuilder()
                        .orderAsc(dev.iconpln.mims.data.local.database.TMonitoringComplaintDao.Properties.TanggalPO)
                        .list()

                    binding.rvMonitoringKomplain.adapter = null
                    binding.rvMonitoringKomplain.layoutManager = null
                    binding.rvMonitoringKomplain.adapter = adapter
                    binding.rvMonitoringKomplain.setHasFixedSize(true)
                    binding.rvMonitoringKomplain.layoutManager = LinearLayoutManager(
                        this@MonitoringComplaintActivity,
                        LinearLayoutManager.VERTICAL,
                        false
                    )
                    adapter.setComplaint(searchList)

                    tvTotalData.text = "Total data ${searchList.size}"
                }

            }

            val adapterStatusCheck = ArrayAdapter(
                this@MonitoringComplaintActivity,
                android.R.layout.simple_dropdown_item_1line,
                listStatus.distinct()
            )
            binding.dropdownUrutkanStatus.setAdapter(adapterStatusCheck)
            binding.dropdownUrutkanStatus.setOnItemClickListener { parent, view, position, id ->
                statusChcked = binding.dropdownUrutkanStatus.text.toString()
                doSearch()
            }

            rvMonitoringKomplain.adapter = adapter
            rvMonitoringKomplain.setHasFixedSize(true)
            rvMonitoringKomplain.layoutManager = LinearLayoutManager(
                this@MonitoringComplaintActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
        }
    }

    private fun doSearch() {
        if (statusChcked == "ALL") {
            val listSearch = daoSession.tMonitoringComplaintDao.queryBuilder()
                .orderDesc(dev.iconpln.mims.data.local.database.TMonitoringComplaintDao.Properties.TanggalPO)
                .list()
            adapter.setComplaint(listSearch)
            binding.tvTotalData.text = "Total data ${listSearch.size}"

        } else {
            val listSearch = daoSession.tMonitoringComplaintDao.queryBuilder()
                .where(
                    dev.iconpln.mims.data.local.database.TMonitoringComplaintDao.Properties.Status.eq(
                        statusChcked
                    )
                ).list()
            adapter.setComplaint(listSearch)
            binding.tvTotalData.text = "Total data ${listSearch.size}"

        }
    }
}