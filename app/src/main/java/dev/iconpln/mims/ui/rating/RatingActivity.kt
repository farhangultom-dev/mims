package dev.iconpln.mims.ui.rating

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.R
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivityRatingBinding
import dev.iconpln.mims.ui.penerimaan.PenerimaanActivity
import dev.iconpln.mims.ui.penerimaan.detail_penerimaan.DetailPenerimaanDokumentasiAdapter
import dev.iconpln.mims.ui.rating.detail_rating.DetailRatingActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RatingActivity : BaseActivity() {
    private lateinit var binding: ActivityRatingBinding
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private lateinit var listPenDetail: MutableList<dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhir>
    private lateinit var penerimaan: dev.iconpln.mims.data.local.database.TPosPenerimaan
    private lateinit var adapter: RatingAdapter
    private lateinit var listDokumentasi: List<String>
    private var noDo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!
        noDo = intent.getStringExtra(Config.KEY_NO_DO)!!

        getDokumentasi(noDo)

        listPenDetail = daoSession.tPosDetailPenerimaanAkhirDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhirDao.Properties.NoDoSmar.eq(
                    noDo
                )
            ).list()

        penerimaan = daoSession.tPosPenerimaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosPenerimaanDao.Properties.NoDoSmar.eq(
                    noDo
                )
            ).list().get(0)

        adapter = RatingAdapter(arrayListOf(), object : RatingAdapter.OnAdapterListener {
            override fun onClick(po: dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhir) {}

        })

        adapter.setPedList(listPenDetail.distinctBy { it.noPackaging })

        with(binding) {
            btnBack.setOnClickListener { onBackPressed() }
            txtIsiEkspedisi.text = penerimaan.expeditions
            txtNoDo.text = penerimaan.noDoSmar
            txtPlant.text = penerimaan.plantName
            txtStoreloc.text = penerimaan.storloc
            txtKuantitasDiterima.text = penerimaan.total
            txtNamaKurir.text = penerimaan.kurirPengantar
            txtPetugasPenerima.text = penerimaan.petugasPenerima
            txtPrimaryOrder.text = penerimaan.poSapNo
            txtTglDiterima.text = penerimaan.tanggalDiterima.take(10)
            txtTglPengiriman.text = penerimaan.createdDate
            txtTglTerima.text = penerimaan.poDate.take(10)
            txtTlsk.text = penerimaan.tlskNo
            txtPrimaryOrder.text =
                if (penerimaan.poMpNo.isNullOrEmpty()) getString(R.string.string_null) else penerimaan.poMpNo

            txtDokumentasi.setOnClickListener {
                val dialog = Dialog(this@RatingActivity)
                dialog.setContentView(R.layout.popup_dokumentasi)
                dialog.window!!.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                dialog.setCancelable(false)
                dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown
                val adapter = DetailPenerimaanDokumentasiAdapter(arrayListOf())

                val recyclerView = dialog.findViewById<RecyclerView>(R.id.rv_dokumentasi)
                val btnClose = dialog.findViewById<ImageView>(R.id.btn_close)

                recyclerView.adapter = adapter
                recyclerView.layoutManager =
                    LinearLayoutManager(this@RatingActivity, LinearLayoutManager.HORIZONTAL, false)
                recyclerView.setHasFixedSize(true)

                adapter.setData(listDokumentasi)

                btnClose.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show();
            }

            if (penerimaan.ratingDone == 1) {
                btnRating.setImageResource(R.drawable.ic_rating_done)
            }


            btnRating.setOnClickListener {
//                if (penerimaan.ratingDone == 1){
//                    Toast.makeText(this@RatingActivity, getString(R.string.sudah_rating),Toast.LENGTH_SHORT).show()
//                }else{
                startActivity(
                    Intent(this@RatingActivity, DetailRatingActivity::class.java)
                        .putExtra(Config.KEY_NO_DO, noDo)
                )
//                }
            }

            srcNoPackaging.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val filter = listPenDetail.filter {
                        it.noPackaging.toLowerCase().contains(s.toString().toLowerCase())
                    }
                    adapter.setPedList(filter.distinctBy { it.noPackaging })
                }

            })

            rvPackaging.adapter = adapter
            rvPackaging.setHasFixedSize(true)
            rvPackaging.layoutManager =
                LinearLayoutManager(this@RatingActivity, LinearLayoutManager.VERTICAL, false)

            btnBack.setOnClickListener {
                onBackPressed()
            }

        }
        insertDataRating()
    }

    private fun getDokumentasi(noDo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiConfig.getApiService(this@RatingActivity).getDokumentasi(noDo)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                        this@RatingActivity,
                        Config.IS_LOGIN_SSO, 0
                    )
                    try {
                        if (response.body()?.status == Config.KEY_SUCCESS) {
                            listDokumentasi = response.body()?.doc?.array!!
                        } else {
                            when (response.body()?.message) {
                                Config.DO_LOGOUT -> {
                                    Helper.logout(this@RatingActivity, isLoginSso)
                                    Toast.makeText(
                                        this@RatingActivity,
                                        getString(R.string.session_kamu_telah_habis),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> Toast.makeText(
                                    this@RatingActivity,
                                    response.body()?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@RatingActivity,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        e.printStackTrace()
                    } finally {
                    }
                } else {
                    Toast.makeText(this@RatingActivity, response.message(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun insertDataRating() {
        val dataRating = daoSession.tDataRatingDao.queryBuilder()
            .where(dev.iconpln.mims.data.local.database.TDataRatingDao.Properties.NoDoSmar.eq(noDo))
            .list()
        if (dataRating != null) {
            val size = dataRating.size
            if (size > 0) {
                val items =
                    arrayOfNulls<dev.iconpln.mims.data.local.database.TTransDataRating>(size)
                var item: dev.iconpln.mims.data.local.database.TTransDataRating
                for ((i, model) in dataRating.withIndex()) {
                    item = dev.iconpln.mims.data.local.database.TTransDataRating()
                    item.noDoSmar = model?.noDoSmar
                    item.ratingResponse = model?.ratingResponse
                    item.ratingQuality = model?.ratingQuality
                    item.ratingDelivery = model?.ratingDelivery
                    item.selesaiRating = model?.selesaiRating
                    item.ketepatan = model?.ketepatan
                    item.noRating = model?.noRating
                    item.isDone = 0
                    items[i] = item
                }
                daoSession.tTransDataRatingDao.insertInTx(items.toList())
            }
        }
    }

    override fun onBackPressed() {
        startActivity(
            Intent(this, PenerimaanActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
    }
}