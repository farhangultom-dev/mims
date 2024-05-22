package dev.iconpln.mims.ui.attribute_material

import android.R
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.data.remote.response.MaterialDatasItem
import dev.iconpln.mims.databinding.ActivityDataAtributMaterialBinding
import dev.iconpln.mims.ui.attribute_material.detail_attribute.DetailDataAtributeMaterialActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.SharedPrefsUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class DataAtributMaterialActivity : BaseActivity() {
    private lateinit var binding: ActivityDataAtributMaterialBinding
    private val viewModel: MaterialViewModel by viewModels()
    private lateinit var adapter: DataAttributeAdapter
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private var isLoading = false
    private var pageList = 1
    private var kategori: String = ""
    private var kdKategori: String = ""
    private var tahun: String = ""
    private var snBatch: String = ""
    private var startDate = ""
    private var endDate = ""
    private var kdPabrikan = ""
    private var limit = 20
    private var isNextPage = false
    private var isTheLastPageData = false
    private lateinit var cal: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataAtributMaterialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
    }

    private fun initData() {
        cal = Calendar.getInstance()
        kdPabrikan = SharedPrefsUtils.getStringPreference(
            this@DataAtributMaterialActivity,
            Config.KEY_KODE_USER,
            ""
        ).toString()

        daoSession = (application as MyApplication).daoSession!!

        viewModel.getAllMaterial(
            this@DataAtributMaterialActivity,
            kdPabrikan,
            tahun,
            kdKategori, startDate, endDate, pageList.toString(), limit.toString(), snBatch
        )
    }

    private fun initView() {
        adapter =
            DataAttributeAdapter(arrayListOf(), object : DataAttributeAdapter.OnAdapterListener {
                override fun onClick(datas: MaterialDatasItem) {
                    startActivity(
                        Intent(
                            this@DataAtributMaterialActivity,
                            DetailDataAtributeMaterialActivity::class.java
                        )
                            .putExtra(Config.KEY_NOMOR_MATERIAL, datas.nomorMaterial)
                            .putExtra(Config.KEY_NOMOR_BATCH, datas.noProduksi)
                            .putExtra(Config.KEY_TAHUN_SN, datas.tahun)
                    )
                }

            })

        with(binding) {
            rvSerial.adapter = adapter
            rvSerial.setHasFixedSize(true)
            rvSerial.layoutManager = LinearLayoutManager(
                this@DataAtributMaterialActivity,
                LinearLayoutManager.VERTICAL,
                false
            )

            rvSerial.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && firstVisibleItemPosition >= 0) {
                        pageList++
                        loadNextPage()
                    }
                }
            })

            viewModel.isLoading.observe(this@DataAtributMaterialActivity) {
                isLoading = it
                when (it) {
                    true -> binding.pgLoading.visibility = View.VISIBLE
                    false -> binding.pgLoading.visibility = View.GONE
                }
            }

            viewModel.materialDataResponse.observe(this@DataAtributMaterialActivity) {
                if (!it.datas.isNullOrEmpty()) {
                    if (!isNextPage) {
                        isNextPage = true
                        adapter.setMaterialList(it.datas)
                    } else {
                        adapter.updateMaterialList(it.datas)
                    }
                    binding.totalData.text = "Total Data : ${it.totalData}"
                    binding.noData.visibility = View.GONE
                    binding.rvSerial.visibility = View.VISIBLE
                } else {
                    isTheLastPageData = true
                    if (!isNextPage) {
                        binding.noData.visibility = View.VISIBLE
                        binding.rvSerial.visibility = View.INVISIBLE
                        binding.totalData.text = "Total Data : 0"
                    }
                }
            }

            setCategoryTahun()
            setCategoryDropdown()
            setBatchSn()
            setDatePicker()

            btnBack.setOnClickListener { finish() }
        }
    }

    private fun loadNextPage() {
        if (!isTheLastPageData) {
            viewModel.getAllMaterial(
                this@DataAtributMaterialActivity,
                kdPabrikan,
                tahun,
                kdKategori, startDate, endDate, pageList.toString(), limit.toString(), snBatch
            )
        }
    }

    private fun setDatePicker() {
        val dateSetListenerStart =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "yyyy-MM-dd"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.txtTglMulai.text = sdf.format(cal.time)

                startDate = sdf.format(cal.time)
                clearEndCalender()

            }

        val dateSetListenerEnd =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "yyyy-MM-dd"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.txtTglSelesai.text = sdf.format(cal.time)

                endDate = sdf.format(cal.time)
                isNextPage = false
                isTheLastPageData = false
                pageList = 1

                viewModel.getAllMaterial(
                    this@DataAtributMaterialActivity,
                    kdPabrikan,
                    tahun,
                    kdKategori, startDate, endDate, pageList.toString(), limit.toString(), snBatch
                )

            }

        binding.cvTanggalMulai.setOnClickListener {
            val datePicker = DatePickerDialog(
                this@DataAtributMaterialActivity, dateSetListenerStart,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )

            if (tahun.isNotEmpty()) {
                cal.set(tahun.toInt(), Calendar.JANUARY, 1)
                datePicker.datePicker.minDate = cal.timeInMillis

                cal.set(tahun.toInt(), Calendar.DECEMBER, 31)
                datePicker.datePicker.maxDate = cal.timeInMillis
            }

            datePicker.show()
        }

        binding.cvTanggalSelesai.setOnClickListener {
            if (startDate.isNullOrEmpty()) {
                Toast.makeText(
                    this,
                    getString(dev.iconpln.mims.R.string.silahkan_pilih_tanggal_awal),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val datePickerDialog = DatePickerDialog(
                    this@DataAtributMaterialActivity, dateSetListenerEnd,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                )

                val dateFormat = SimpleDateFormat(Config.DATE, Locale.getDefault())
                val date = dateFormat.parse(startDate)
                val timeInMillis = date?.time ?: 0

                datePickerDialog.datePicker.minDate = timeInMillis

                if (tahun.isNotEmpty()) {
                    cal.set(tahun.toInt(), Calendar.DECEMBER, 31)
                    datePickerDialog.datePicker.maxDate = cal.timeInMillis
                }

                datePickerDialog.show()
            }

        }
    }

    private fun setBatchSn() {
        binding.btnSrc.setOnClickListener {
            if (!isLoading) {
                isNextPage = false
                isTheLastPageData = false
                pageList = 1
                viewModel.getAllMaterial(
                    this@DataAtributMaterialActivity,
                    kdPabrikan,
                    tahun,
                    kdKategori, startDate, endDate, pageList.toString(), limit.toString(), snBatch
                )
            }
        }

        binding.srcBatchSnMaterial.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                snBatch = s.toString()
            }

        })
    }

    private fun setCategoryTahun() {
        val years = getLastTenYears()

        if (!years.isNullOrEmpty()) {
            val adapterTahun =
                ArrayAdapter(this, R.layout.simple_dropdown_item_1line, years.distinct())
            binding.dropdownTahun.setAdapter(adapterTahun)
            binding.dropdownTahun.setOnItemClickListener { parent, view, position, id ->
                tahun = binding.dropdownTahun.text.toString()
                isNextPage = false
                isTheLastPageData = false
                pageList = 1

                viewModel.getAllMaterial(
                    this@DataAtributMaterialActivity,
                    kdPabrikan,
                    tahun,
                    kdKategori, startDate, endDate, pageList.toString(), limit.toString(), snBatch
                )
            }
        }

    }

    private fun setCategoryDropdown() {
        val categoriesNames =
            daoSession.tMaterialGroupsDao.queryBuilder().list().map { it.namaKategoriMaterial }
        val categoriesCodes =
            daoSession.tMaterialGroupsDao.queryBuilder().list().map { it.materialGroup }

        if (!categoriesNames.isNullOrEmpty()) {
            val adapterKategori = ArrayAdapter(
                this@DataAtributMaterialActivity,
                R.layout.simple_dropdown_item_1line,
                categoriesNames.distinct()
            )
            binding.dropdownKategori.setAdapter(adapterKategori)
            binding.dropdownKategori.setOnItemClickListener { parent, view, position, id ->
                kategori = binding.dropdownKategori.text.toString()
                kdKategori = categoriesCodes[position]
                isNextPage = false
                isTheLastPageData = false
                pageList = 1

                viewModel.getAllMaterial(
                    this@DataAtributMaterialActivity,
                    kdPabrikan,
                    tahun,
                    kdKategori, startDate, endDate, pageList.toString(), limit.toString(), snBatch
                )
            }
        }
    }

    private fun clearEndCalender() {
        binding.txtTglSelesai.setText("yyyy/mm/dd")
        endDate = ""
    }

    fun getLastTenYears(): List<Int> {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val lastTenYears = mutableListOf<Int>()

        for (i in currentYear downTo currentYear - 9) {
            lastTenYears.add(i)
        }

        return lastTenYears
    }
}
