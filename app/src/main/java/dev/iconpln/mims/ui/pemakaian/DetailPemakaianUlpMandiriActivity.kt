package dev.iconpln.mims.ui.pemakaian

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.data.local.database_local.GenericReport
import dev.iconpln.mims.data.local.database_local.ReportParameter
import dev.iconpln.mims.data.local.database_local.ReportUploader
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivityDetailPemakaianUlpMandiriBinding
import dev.iconpln.mims.tasks.Loadable
import dev.iconpln.mims.tasks.TambahReportTask
import dev.iconpln.mims.ui.pemakaian.input_pemakaian.InputPemakaianActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.DateTimeUtils
import dev.iconpln.mims.utils.SharedPrefsUtils
import org.joda.time.DateTime
import org.joda.time.LocalDateTime

class DetailPemakaianUlpMandiriActivity : BaseActivity(), Loadable {
    private lateinit var binding: ActivityDetailPemakaianUlpMandiriBinding
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private lateinit var pemakaian: dev.iconpln.mims.data.local.database.TPemakaian
    private lateinit var adapter: PemakaianDetailAdapter
    private var noTransaksi: String = ""
    private lateinit var detailPemakaians: List<dev.iconpln.mims.data.local.database.TTransPemakaianDetail>
    private var noMat = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPemakaianUlpMandiriBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!

        noTransaksi = intent.getStringExtra("noTransaksi")!!

        pemakaian = daoSession.tPemakaianDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPemakaianDao.Properties.NoTransaksi.eq(
                    noTransaksi
                )
            ).list().get(0)

        detailPemakaians = daoSession.tTransPemakaianDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TTransPemakaianDetailDao.Properties.NoTransaksi.eq(
                    noTransaksi
                )
            ).list()

        adapter = PemakaianDetailAdapter(
            arrayListOf(),
            object : PemakaianDetailAdapter.OnAdapterListener {
                override fun onClick(pemakaian: dev.iconpln.mims.data.local.database.TTransPemakaianDetail) {
                    if (!pemakaian.isActive) {
                        Toast.makeText(
                            this@DetailPemakaianUlpMandiriActivity,
                            "Material tidak dapat di scan karena merupakan material non mims",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (pemakaian.isDone == 1) {
                            Toast.makeText(
                                this@DetailPemakaianUlpMandiriActivity,
                                "Anda sudah menyelesaikan pemakaian ini",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            startActivity(
                                Intent(
                                    this@DetailPemakaianUlpMandiriActivity,
                                    InputSnPemakaianActivity::class.java
                                )
                                    .putExtra("noTransaksi", pemakaian.noTransaksi)
                                    .putExtra("noMat", pemakaian.nomorMaterial)
                            )
                        }
                    }
                }

            },
            daoSession
        )

        adapter.setpemakaianList(detailPemakaians)

        with(binding) {
            txtNoReservasi.text = pemakaian.noReservasi
            txtTotalData.text = "Total ${detailPemakaians.size} data"

            rvPemakaianUlp.adapter = adapter
            rvPemakaianUlp.setHasFixedSize(true)
            rvPemakaianUlp.layoutManager = LinearLayoutManager(
                this@DetailPemakaianUlpMandiriActivity,
                LinearLayoutManager.VERTICAL,
                false
            )

            btnBack.setOnClickListener { onBackPressed() }
            btnSimpan.setOnClickListener { validation() }

            btnPemakaian.setOnClickListener {
                startActivity(
                    Intent(
                        this@DetailPemakaianUlpMandiriActivity,
                        InputPemakaianActivity::class.java
                    )
                        .putExtra("noTransaksi", noTransaksi)
                )
            }

            srcNomorMaterial.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {
                    noMat = s.toString()
                    doSearch()
                }

            })
        }
    }

    private fun doSearch() {
        val listSearch = daoSession.tTransPemakaianDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TTransPemakaianDetailDao.Properties.NoTransaksi.eq(
                    noTransaksi
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TTransPemakaianDetailDao.Properties.NomorMaterial.like(
                    "%" + noMat + "%"
                )
            ).list()

        adapter.setpemakaianList(listSearch)
    }

    private fun validation() {
        if (pemakaian.isDonePemakai == 0) {
            Toast.makeText(
                this@DetailPemakaianUlpMandiriActivity,
                "Anda belum input data pemakai",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        for (i in detailPemakaians) {
            val jumlahPemakaian = daoSession.tListSnMaterialPemakaianUlpDao.queryBuilder()
                .where(
                    dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlpDao.Properties.NoTransaksi.eq(
                        i.noTransaksi
                    )
                )
                .where(
                    dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlpDao.Properties.NoMaterial.eq(
                        i.nomorMaterial
                    )
                ).list()

            if (i.isDone == 0) {
                Toast.makeText(
                    this@DetailPemakaianUlpMandiriActivity,
                    "Kamu belum menyelesaikan semua pemakaian",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (i.isActive) {
                if (i.qtyReservasi != jumlahPemakaian.size.toDouble()) {
                    Toast.makeText(
                        this@DetailPemakaianUlpMandiriActivity,
                        "Jumlah reservasi ${i.nomorMaterial} masih kurang",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
            }
        }

        submitForm()
    }

    private fun submitForm() {
        val detailPemakaian = daoSession.tTransPemakaianDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TTransPemakaianDetailDao.Properties.NoTransaksi.eq(
                    noTransaksi
                )
            ).list()

        var materials = ""
        val reports = ArrayList<GenericReport>()
        val currentDate = LocalDateTime.now().toString(Config.DATE)
        val currentDateTime = LocalDateTime.now().toString(Config.DATETIME)
        val currentUtc = DateTimeUtils.currentUtc

        pemakaian.isDone = 1
        pemakaian.statusPemakaian = "TERPAKAI"
        daoSession.tPemakaianDao.update(pemakaian)

        for (i in detailPemakaian) {
            materials += "${i.nomorMaterial},${i.qtyPemakaian};"
        }

        if (materials != "") {
            materials = materials.substring(0, materials.length - 1)
        }

        //region Add report visit to queue
        var jwtWeb = SharedPrefsUtils.getStringPreference(
            this@DetailPemakaianUlpMandiriActivity,
            "jwtWeb",
            ""
        )
        var jwtMobile =
            SharedPrefsUtils.getStringPreference(this@DetailPemakaianUlpMandiriActivity, "jwt", "")
        var jwt = "$jwtMobile;$jwtWeb"
        var plant = SharedPrefsUtils.getStringPreference(
            this@DetailPemakaianUlpMandiriActivity,
            "plant",
            ""
        )
        var storloc = SharedPrefsUtils.getStringPreference(
            this@DetailPemakaianUlpMandiriActivity,
            "storloc",
            ""
        )
        var username = SharedPrefsUtils.getStringPreference(
            this@DetailPemakaianUlpMandiriActivity,
            "username",
            "14.Hexing_Electrical"
        )
        val reportId =
            "temp_pemakaian Ulp" + username + "_" + pemakaian.noTransaksi + "_" + DateTime.now()
                .toString(
                    Config.DATETIME
                )
        val reportName = "Update Data pemakaian Ulp"
        val reportDescription = "$reportName: " + " (" + reportId + ")"
        val params = ArrayList<ReportParameter>()

        params.add(
            ReportParameter(
                "1",
                reportId,
                "no_transaksi",
                pemakaian.noTransaksi!!,
                ReportParameter.TEXT
            )
        )
        params.add(ReportParameter("2", reportId, "materials", materials, ReportParameter.TEXT))

        val report = GenericReport(
            reportId,
            jwt!!,
            reportName,
            reportDescription,
            ApiConfig.sendReportPemakaianUlpSelesai(),
            currentDate,
            Config.NO_CODE,
            currentUtc,
            params
        )
        reports.add(report)
        //endregion

        val task = TambahReportTask(this, reports)
        task.execute()

        val iService = Intent(applicationContext, ReportUploader::class.java)
        startService(iService)
    }

    override fun setLoading(show: Boolean, title: String, message: String) {

    }

    override fun setFinish(result: Boolean, message: String) {
        Toast.makeText(this@DetailPemakaianUlpMandiriActivity, message, Toast.LENGTH_SHORT).show()
        startActivity(
            Intent(this@DetailPemakaianUlpMandiriActivity, PemakaianActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
    }

    override fun onResume() {
        adapter.setpemakaianList(detailPemakaians)
        super.onResume()
    }
}