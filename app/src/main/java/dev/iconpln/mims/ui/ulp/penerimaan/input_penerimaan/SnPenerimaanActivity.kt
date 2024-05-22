package dev.iconpln.mims.ui.ulp.penerimaan.input_penerimaan

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.databinding.ActivitySnPenerimaanBinding

class SnPenerimaanActivity : BaseActivity() {
    private lateinit var binding: ActivitySnPenerimaanBinding
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private var noMaterial: String = ""
    private var noRepackaging: String = ""
    private var noTransaksi: String = ""
    private lateinit var adapter: PenerimaanUlpSnAdapter
    private lateinit var lisSn: List<dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlp>
    private lateinit var details: dev.iconpln.mims.data.local.database.TTransPenerimaanDetailUlp
    private lateinit var penerimaan: dev.iconpln.mims.data.local.database.TTransPenerimaanUlp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySnPenerimaanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!
        noMaterial = intent.getStringExtra("noMaterial")!!
        noRepackaging = intent.getStringExtra("noRepackaging")!!
        noTransaksi = intent.getStringExtra("noTransaksi")!!

        lisSn = daoSession.tListSnMaterialPenerimaanUlpDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlpDao.Properties.NoRepackaging.eq(
                    noRepackaging
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlpDao.Properties.NoMaterial.eq(
                    noMaterial
                )
            ).list()

        details = daoSession.tTransPenerimaanDetailUlpDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TTransPenerimaanDetailUlpDao.Properties.NoTransaksi.eq(
                    noTransaksi
                )
            ).list()
            .get(0)

        penerimaan = daoSession.tTransPenerimaanUlpDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TTransPenerimaanUlpDao.Properties.NoRepackaging.eq(
                    noRepackaging
                )
            ).list().get(0)

        adapter = PenerimaanUlpSnAdapter(
            arrayListOf(),
            object : PenerimaanUlpSnAdapter.OnAdapterListener {
                override fun onClick(tms: dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlp) {}

            })

        with(binding) {
            txtGudangAsal.text = "PLN Area Cilacap"
            txtNoMaterial.text = noMaterial
            txtSpesifikasi.text = details.materialDesc

            rvListSn.adapter = adapter
            rvListSn.layoutManager = LinearLayoutManager(
                this@SnPenerimaanActivity,
                LinearLayoutManager.VERTICAL, false
            )
            rvListSn.setHasFixedSize(true)

            btnBack.setOnClickListener { onBackPressed() }
            btnSimpan.setOnClickListener {
                Toast.makeText(
                    this@SnPenerimaanActivity,
                    "Sn Berhasil Disimpan",
                    Toast.LENGTH_SHORT
                )
                onBackPressed()
            }
        }

        adapter.setTmsList(lisSn)
    }
}