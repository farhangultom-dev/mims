package dev.iconpln.mims.ui.monitoring

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.R
import dev.iconpln.mims.databinding.ActivityMonitoringBinding
import dev.iconpln.mims.ui.monitoring.monitoring_detail.MonitoringDetailActivity
import dev.iconpln.mims.utils.Config
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MonitoringActivity : BaseActivity() {
    private lateinit var binding: ActivityMonitoringBinding
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private val viewModel: MonitoringViewModel by viewModels()
    private lateinit var adapter: MonitoringAdapter
    private var noPo = ""
    private var noDo = ""
    private var startDate = ""
    private var endDate = ""
    private lateinit var cal: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitoringBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!

        initData()
        initView()
    }

    private fun initData() {
        viewModel.getPo(daoSession)
    }

    private fun initView() {
        cal = Calendar.getInstance()

        adapter = MonitoringAdapter(arrayListOf(), object : MonitoringAdapter.OnAdapterListener {
            override fun onClick(po: dev.iconpln.mims.data.local.database.TPos) {
                val intent = Intent(this@MonitoringActivity, MonitoringDetailActivity::class.java)
                intent.putExtra(Config.KEY_NO_DO, po.noDoSmar)
                startActivity(intent)
            }

        })

        viewModel.monitoringPOResponse.observe(this) {
            binding.totalData.text = "Total : ${it.size} Data"
            if (it.isNullOrEmpty()) binding.noData.visibility =
                View.VISIBLE else binding.noData.visibility = View.GONE
            adapter.setPoList(it)
        }

        with(binding) {
            rvNoPo.adapter = adapter
            rvNoPo.setHasFixedSize(true)
            rvNoPo.layoutManager =
                LinearLayoutManager(this@MonitoringActivity, LinearLayoutManager.VERTICAL, false)

            btnBack.setOnClickListener { onBackPressed() }

            srcNomorPo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    noPo = s.toString()
                    viewModel.getPoFilter(daoSession, noPo, noDo, startDate, endDate)
                }

            })

            srcNomorDo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    noDo = s.toString()
                    viewModel.getPoFilter(daoSession, noPo, noDo, startDate, endDate)
                }

            })
        }

        setDatePicker()
    }

    private fun setDatePicker() {
        val dateSetListenerStart =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                var myFormat = Config.DATE
                var sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.txtTglMulai.text = sdf.format(cal.time)
                startDate = sdf.format(cal.time)

                clearEndCalender()
                viewModel.getPoFilter(daoSession, noPo, noDo, startDate, endDate)
            }

        val dateSetListenerEnd =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                var myFormat = Config.DATE
                var sdf = SimpleDateFormat(myFormat, Locale.US)
                binding.txtTglSelesai.text = sdf.format(cal.time)
                endDate = sdf.format(cal.time)
                viewModel.getPoFilter(daoSession, noPo, noDo, startDate, endDate)
            }

        binding.cvTanggalMulai.setOnClickListener {
            DatePickerDialog(
                this@MonitoringActivity, dateSetListenerStart,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }

        binding.cvTanggalSelesai.setOnClickListener {
            if (startDate.isNullOrEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.silahkan_pilih_tanggal_awal),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val datePickerDialog = DatePickerDialog(
                    this@MonitoringActivity, dateSetListenerEnd,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                )

                val dateFormat = SimpleDateFormat(Config.DATE, Locale.getDefault())
                val date = dateFormat.parse(startDate)
                val timeInMillis = date?.time ?: 0

                datePickerDialog.datePicker.minDate = timeInMillis

                datePickerDialog.show()
            }
        }
    }

    private fun clearEndCalender() {
        binding.txtTglSelesai.setText("yyyy/mm/dd")
        endDate = ""
    }
}