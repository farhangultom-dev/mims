package dev.iconpln.mims.ui.monitoring_stok

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.R
import dev.iconpln.mims.data.remote.response.DataNoMaterialMonStok
import dev.iconpln.mims.data.remote.response.StorLocItem
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivityMonitoringStokBinding
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.SharedPrefsUtils
import dev.iconpln.mims.utils.ViewModelFactory

class MonitoringStokActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMonitoringStokBinding
    private lateinit var viewModel: MonitoringStokViewModel
    private var storloc = arrayListOf<StorLocItem>()
    private var storlocName = arrayListOf<String>()
    private var noMaterialName = arrayListOf<DataNoMaterialMonStok>()
    private var noMat = arrayListOf<String>()
    private lateinit var rvAdapter: MonitoringStokAdapter
    private var valuationType = "NORMAL"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMonitoringStokBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRecyclerView()

        val apiService = ApiConfig.getApiService(this)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(apiService)
        )[MonitoringStokViewModel::class.java]

        viewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(this) {
            if (it != null) {
                Toast.makeText(this, "$it", Toast.LENGTH_SHORT).show()
            }
        }


        val plantName = SharedPrefsUtils.getStringPreference(this, Config.KEY_PLANT_NAME, "")
        val plant = SharedPrefsUtils.getStringPreference(this, Config.KEY_PLANT, "")
        val storloc1 = SharedPrefsUtils.getStringPreference(this, Config.KEY_STOR_LOC, "")

        viewModel.getMonitoringStok(
            plant.toString(),
            "NORMAL",
            storloc1.toString(),
            noMaterial = ""
        )
        viewModel.getMonitoringStok.observe(this) {
            if (it != null) {
                rvAdapter.setListMonstok(it.data)
            }
        }


        setRecyclerView()

        binding.edtUp3.setText(plantName)

        viewModel.getStorloc()
        viewModel.getStorloc.observe(this) {
            if (it.status == 200) {
                storloc.addAll(it.data.storLoc)
                storloc.forEach {
                    storlocName.add(it.storLocName.toString())
                }
                val adapterStorloc = ArrayAdapter(
                    this@MonitoringStokActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    storlocName
                )
                binding.dropdownGudang.setAdapter(adapterStorloc)
                binding.dropdownGudang.setOnItemClickListener { adapterView, view, g, l ->
                    val selectedGudang = adapterView.getItemAtPosition(g) as String
                    val selectedStorloc =
                        storloc.find { it.storLocName.toString() == selectedGudang }
                    val storloc1 = selectedStorloc?.storLoc ?: ""

                    viewModel.getMonitoringStok(
                        plant.toString(),
                        valuationType,
                        storloc1.toString(),
                        ""
                    )
                    viewModel.getNoMaterialMonStok(
                        plant.toString(),
                        valuationType,
                        storloc1.toString()
                    )

                }


                viewModel.getNoMaterialMonStok(plant.toString(), valuationType, storloc1.toString())
                viewModel.getNoMaterialMonStok.observe(this) {
                    if (it.status == 200) {
                        noMaterialName.clear()
                        noMaterialName.addAll(it.data)
                        noMaterialName.forEach {
                            noMat.add(it.materialDesc.toString())
                        }

                        updateDropdown()

                        val adapterNoMat = ArrayAdapter(
                            this@MonitoringStokActivity,
                            android.R.layout.simple_dropdown_item_1line,
                            noMat
                        )
                        binding.dropdownMaterial.setAdapter(adapterNoMat)
                        binding.dropdownMaterial.setOnItemClickListener { adapterView, view, n, l ->
                            val selectedNoMat = adapterView.getItemAtPosition(n) as String
                            val selectedNoMaterial =
                                noMaterialName.find { it.nomorMaterial.toString() == selectedNoMat }
                            val noMat1 = selectedNoMaterial?.nomorMaterial ?: ""

                            viewModel.getMonitoringStok(
                                plant.toString(),
                                valuationType,
                                storloc1.toString(),
                                noMat1
                            )
                        }
                    }
                }

                val statusValuation = listOf("NORMAL", "PRE-MEMORY")
                val adapterValuation = ArrayAdapter(
                    this@MonitoringStokActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    statusValuation
                )
                binding.dropdownValuationtype.setAdapter(adapterValuation)
                binding.dropdownValuationtype.setOnItemClickListener { adapterView, view, i, l ->
                    val selectedItem = adapterView.getItemAtPosition(i) as String
                    valuationType = selectedItem
                    viewModel.getMonitoringStok(
                        plant.toString(),
                        valuationType,
                        storloc1.toString(),
                        ""
                    )
                    viewModel.getNoMaterialMonStok(
                        plant.toString(),
                        valuationType,
                        storloc1.toString()
                    )
                }

            }
        }
        binding.btnBack.setOnClickListener { onBackPressed() }

    }

    private fun updateDropdown() {
        noMat.clear()
        noMaterialName.forEach {
            noMat.add(it.nomorMaterial.toString())
        }

        val adapterNoMat = ArrayAdapter(
            this@MonitoringStokActivity,
            android.R.layout.simple_dropdown_item_1line,
            noMat
        )
        binding.dropdownMaterial.setAdapter(adapterNoMat)
    }

    private fun setRecyclerView() {
        rvAdapter = MonitoringStokAdapter()
        binding.rvMonitoringStok.apply {
            layoutManager = LinearLayoutManager(this@MonitoringStokActivity)
            setHasFixedSize(true)
            adapter = rvAdapter
        }
    }
}