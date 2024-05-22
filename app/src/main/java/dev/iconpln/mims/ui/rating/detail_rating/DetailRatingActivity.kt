package dev.iconpln.mims.ui.rating.detail_rating

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.iconpln.mims.AddPhotoAdapter
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.CameraXActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.R
import dev.iconpln.mims.data.local.database_local.GenericReport
import dev.iconpln.mims.data.local.database_local.ReportParameter
import dev.iconpln.mims.data.local.database_local.ReportUploader
import dev.iconpln.mims.data.remote.response.DocItem
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivityDetailRatingBinding
import dev.iconpln.mims.tasks.Loadable
import dev.iconpln.mims.tasks.TambahReportTask
import dev.iconpln.mims.ui.rating.RatingActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.DateTimeUtils
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import java.io.File

class DetailRatingActivity : BaseActivity(), Loadable {
    private lateinit var binding: ActivityDetailRatingBinding
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private lateinit var adapter: AddPhotoAdapter
    private lateinit var listPhoto: List<dev.iconpln.mims.data.local.database.TPhoto>
    private lateinit var ratingPenerimaans: List<dev.iconpln.mims.data.local.database.TRating>
    private lateinit var ratingPenyedias: List<dev.iconpln.mims.data.local.database.TRating>
    private lateinit var ratingWaktus: List<dev.iconpln.mims.data.local.database.TRating>
    private lateinit var penyediaAdapter: RatingPenyediaAdapter
    private lateinit var penerimaanAdapter: RatingPenerimaanAdapter
    private lateinit var waktuAdapter: RatingWaktuAdapter
    private lateinit var penerimaan: dev.iconpln.mims.data.local.database.TPosPenerimaan
    private lateinit var dataRating: dev.iconpln.mims.data.local.database.TTransDataRating
    private lateinit var detailPenerimaan: List<dev.iconpln.mims.data.local.database.TPosDetailPenerimaan>
    private lateinit var dialogLoading: Dialog
    private lateinit var listDokumentasi: List<DocItem>
    private val cameraRequestFoto = 101
    private val galleryRequestFoto = 102
    private var noDo: String? = ""
    private var photoNumber: Int = 0
    var ketPenyedia: String? = ""
    var ketWaktu: String? = ""
    var ketPenerimaan: String? = ""
    var nilaiPenyedia: Int? = 0
    var nilaiWaktu: Int? = 0
    var nilaiPenerimaan: Int? = 0
    var ratingPenyedia: String? = ""
    var ratingWaktu: String? = ""
    var ratingPenerimaan: String? = ""
    private var role = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailRatingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        daoSession = (application as MyApplication).daoSession!!
        noDo = intent.getStringExtra(Config.KEY_NO_DO)
        role =
            SharedPrefsUtils.getIntegerPreference(this@DetailRatingActivity, Config.KEY_ROLE_ID, 0)
        dialogLoading = Helper.loadingDialog(this)

        listPhoto = daoSession.tPhotoDao.queryBuilder()
            .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.NoDo.eq(noDo))
            .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.Type.eq(Config.KEY_RATING))
            .list()

        penerimaan = daoSession.tPosPenerimaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosPenerimaanDao.Properties.NoDoSmar.eq(
                    noDo
                )
            ).limit(1).unique()

        dataRating = daoSession.tTransDataRatingDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TTransDataRatingDao.Properties.NoDoSmar.eq(
                    noDo
                )
            ).limit(1).unique()

        detailPenerimaan = daoSession.tPosDetailPenerimaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.NoDoSmar.eq(
                    noDo
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsChecked.eq(
                    1
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsDone.eq(
                    1
                )
            ).list()

        ratingPenyedias = daoSession.tRatingDao.queryBuilder()
            .where(dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(Config.KEY_RESPON_PENYEDIA))
            .list()

        ratingPenerimaans = daoSession.tRatingDao.queryBuilder()
            .where(dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(Config.KEY_KUALITAS_PENERIMAAN))
            .list()

        ratingWaktus = daoSession.tRatingDao.queryBuilder()
            .where(dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(Config.KEY_WAKTU_PENGIRIMAN))
            .list()

        setDefaultRating()

        photoNumber = listPhoto.size + 1

        adapter = AddPhotoAdapter(arrayListOf(), object : AddPhotoAdapter.OnAdapterListener {
            override fun onClick(po: dev.iconpln.mims.data.local.database.TPhoto) {
                if (po.photoNumber == 5) {
                    Toast.makeText(
                        this@DetailRatingActivity,
                        getString(R.string.max_upload_foto),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    doFoto()
                }
            }

        }, true,
            object : AddPhotoAdapter.OnAdapterListenerDelete {
                override fun onClick(po: dev.iconpln.mims.data.local.database.TPhoto) {
                    val delete = daoSession.tPhotoDao.queryBuilder()
                        .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.Id.eq(po.id))
                        .limit(1).unique()
                    daoSession.tPhotoDao.delete(delete)

                    listPhoto = daoSession.tPhotoDao.queryBuilder()
                        .where(
                            dev.iconpln.mims.data.local.database.TPhotoDao.Properties.NoDo.eq(
                                noDo
                            )
                        )
                        .where(
                            dev.iconpln.mims.data.local.database.TPhotoDao.Properties.Type.eq(
                                Config.KEY_RATING
                            )
                        )
                        .list()
                    var number = 0

                    for (i in listPhoto) {
                        number += 1
                        i.photoNumber = number
                        daoSession.update(i)
                    }

                    adapter.setPhotoList(listPhoto)
                    photoNumber--

                    if (listPhoto.isEmpty()) {
                        binding.btnUploadPhoto.visibility = View.VISIBLE
                        binding.maksfoto.visibility = View.VISIBLE
                    } else {
                        binding.btnUploadPhoto.visibility = View.GONE
                        binding.maksfoto.visibility = View.GONE
                    }
                }

            })

        penyediaAdapter =
            RatingPenyediaAdapter(arrayListOf(), object : RatingPenyediaAdapter.OnAdapterListener {
                override fun onClick(po: dev.iconpln.mims.data.local.database.TRating) {
                    if (penerimaan.ratingDone == 0) {
                        when (po.kdRating) {
                            Config.KEY_CODE_RATING_31 -> {
                                for (i in ratingPenyedias) {
                                    if (i.kdRating == Config.KEY_CODE_RATING_31) {
                                        i.isActive = 1
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    Config.KEY_RESPON_PENYEDIA
                                                )
                                            ).list()
                                        binding.txtMessageRespon.visibility = View.VISIBLE
                                        binding.txtMessageRespon.text = po.keterangan
                                        ketPenyedia = po.keterangan
                                        nilaiPenyedia = po.nilai
                                        ratingPenyedia = po.kdRating
                                        penyediaAdapter.setRatList(rating)
                                    } else {
                                        i.isActive = 0
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    Config.KEY_RESPON_PENYEDIA
                                                )
                                            ).list()
                                        penyediaAdapter.setRatList(rating)
                                    }
                                }
                            }

                            Config.KEY_CODE_RATING_32 -> {
                                for (i in ratingPenyedias) {
                                    if (i.kdRating == Config.KEY_CODE_RATING_31 || i.kdRating == Config.KEY_CODE_RATING_32) {
                                        i.isActive = 1
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    Config.KEY_RESPON_PENYEDIA
                                                )
                                            ).list()
                                        penyediaAdapter.setRatList(rating)
                                        ketPenyedia = po.keterangan
                                        nilaiPenyedia = po.nilai
                                        ratingPenyedia = po.kdRating
                                        binding.txtMessageRespon.visibility = View.VISIBLE
                                        binding.txtMessageRespon.text = po.keterangan
                                    } else {
                                        i.isActive = 0
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    Config.KEY_RESPON_PENYEDIA
                                                )
                                            ).list()
                                        penyediaAdapter.setRatList(rating)
                                    }
                                }
                            }

                            Config.KEY_CODE_RATING_33 -> {
                                for (i in ratingPenyedias) {
                                    if (i.kdRating == Config.KEY_CODE_RATING_31 || i.kdRating == Config.KEY_CODE_RATING_32 || i.kdRating == Config.KEY_CODE_RATING_33) {
                                        i.isActive = 1
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    Config.KEY_RESPON_PENYEDIA
                                                )
                                            ).list()
                                        penyediaAdapter.setRatList(rating)
                                        ketPenyedia = po.keterangan
                                        nilaiPenyedia = po.nilai
                                        ratingPenyedia = po.kdRating
                                        binding.txtMessageRespon.visibility = View.VISIBLE
                                        binding.txtMessageRespon.text = po.keterangan
                                    } else {
                                        i.isActive = 0
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    Config.KEY_RESPON_PENYEDIA
                                                )
                                            ).list()
                                        penyediaAdapter.setRatList(rating)
                                    }
                                }
                            }

                            Config.KEY_CODE_RATING_34 -> {
                                for (i in ratingPenyedias) {
                                    if (i.kdRating == Config.KEY_CODE_RATING_31 || i.kdRating == Config.KEY_CODE_RATING_32 || i.kdRating == Config.KEY_CODE_RATING_33 || i.kdRating == Config.KEY_CODE_RATING_34) {
                                        i.isActive = 1
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    Config.KEY_RESPON_PENYEDIA
                                                )
                                            ).list()
                                        penyediaAdapter.setRatList(rating)
                                        ketPenyedia = po.keterangan
                                        nilaiPenyedia = po.nilai
                                        ratingPenyedia = po.kdRating
                                        binding.txtMessageRespon.visibility = View.VISIBLE
                                        binding.txtMessageRespon.text = po.keterangan
                                    } else {
                                        i.isActive = 0
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    Config.KEY_RESPON_PENYEDIA
                                                )
                                            ).list()
                                        penyediaAdapter.setRatList(rating)
                                    }
                                }
                            }

                            Config.KEY_CODE_RATING_35 -> {
                                for (i in ratingPenyedias) {
                                    if (i.kdRating == Config.KEY_CODE_RATING_31 || i.kdRating == Config.KEY_CODE_RATING_32 || i.kdRating == Config.KEY_CODE_RATING_33 || i.kdRating == Config.KEY_CODE_RATING_34 || i.kdRating == Config.KEY_CODE_RATING_35) {
                                        i.isActive = 1
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    Config.KEY_RESPON_PENYEDIA
                                                )
                                            ).list()
                                        penyediaAdapter.setRatList(rating)
                                        ketPenyedia = po.keterangan
                                        nilaiPenyedia = po.nilai
                                        ratingPenyedia = po.kdRating
                                        binding.txtMessageRespon.visibility = View.VISIBLE
                                        binding.txtMessageRespon.text = po.keterangan
                                    } else {
                                        i.isActive = 0
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    Config.KEY_RESPON_PENYEDIA
                                                )
                                            ).list()
                                        penyediaAdapter.setRatList(rating)
                                    }
                                }
                            }
                        }
                    }
                }

            })

        waktuAdapter =
            RatingWaktuAdapter(arrayListOf(), object : RatingWaktuAdapter.OnAdapterListener {
                override fun onClick(po: dev.iconpln.mims.data.local.database.TRating) {
                    if (penerimaan.ratingDone == 0) {
                        if (dataRating.ratingDelivery == "25" || dataRating.ratingDelivery == "24") {
                            Toast.makeText(
                                this@DetailRatingActivity,
                                getString(R.string.terdapat_default_rating),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            when (po.kdRating) {
                                "21" -> {
                                    for (i in ratingWaktus) {
                                        if (i.kdRating == "21") {
                                            i.isActive = 1
                                            daoSession.tRatingDao.update(i)
                                            val rating = daoSession.tRatingDao.queryBuilder()
                                                .where(
                                                    dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                        "Waktu Pengiriman"
                                                    )
                                                ).list()
                                            binding.txtMessageWaktu.visibility = View.VISIBLE
                                            binding.txtMessageWaktu.text = po.keterangan
                                            ketWaktu = po.keterangan
                                            nilaiWaktu = po.nilai
                                            ratingWaktu = po.kdRating
                                            waktuAdapter.setRatList(rating)
                                        } else {
                                            i.isActive = 0
                                            daoSession.tRatingDao.update(i)
                                            val rating = daoSession.tRatingDao.queryBuilder()
                                                .where(
                                                    dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                        "Waktu Pengiriman"
                                                    )
                                                ).list()
                                            waktuAdapter.setRatList(rating)
                                        }
                                    }
                                }

                                "22" -> {
                                    for (i in ratingWaktus) {
                                        if (i.kdRating == "21" || i.kdRating == "22") {
                                            i.isActive = 1
                                            daoSession.tRatingDao.update(i)
                                            val rating = daoSession.tRatingDao.queryBuilder()
                                                .where(
                                                    dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                        "Waktu Pengiriman"
                                                    )
                                                ).list()
                                            waktuAdapter.setRatList(rating)
                                            ketWaktu = po.keterangan
                                            nilaiWaktu = po.nilai
                                            ratingWaktu = po.kdRating
                                            binding.txtMessageWaktu.visibility = View.VISIBLE
                                            binding.txtMessageWaktu.text = po.keterangan
                                        } else {
                                            i.isActive = 0
                                            daoSession.tRatingDao.update(i)
                                            val rating = daoSession.tRatingDao.queryBuilder()
                                                .where(
                                                    dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                        "Waktu Pengiriman"
                                                    )
                                                ).list()
                                            waktuAdapter.setRatList(rating)
                                        }
                                    }
                                }

                                "23" -> {
                                    for (i in ratingWaktus) {
                                        if (i.kdRating == "21" || i.kdRating == "22" || i.kdRating == "23") {
                                            i.isActive = 1
                                            daoSession.tRatingDao.update(i)
                                            val rating = daoSession.tRatingDao.queryBuilder()
                                                .where(
                                                    dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                        "Waktu Pengiriman"
                                                    )
                                                ).list()
                                            waktuAdapter.setRatList(rating)
                                            ketWaktu = po.keterangan
                                            nilaiWaktu = po.nilai
                                            ratingWaktu = po.kdRating
                                            binding.txtMessageWaktu.visibility = View.VISIBLE
                                            binding.txtMessageWaktu.text = po.keterangan
                                        } else {
                                            i.isActive = 0
                                            daoSession.tRatingDao.update(i)
                                            val rating = daoSession.tRatingDao.queryBuilder()
                                                .where(
                                                    dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                        "Waktu Pengiriman"
                                                    )
                                                ).list()
                                            waktuAdapter.setRatList(rating)
                                        }
                                    }
                                }

                                "24" -> {
                                    Toast.makeText(
                                        this@DetailRatingActivity,
                                        "Anda tidak dapat memilih rating lebih dari 3",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                "25" -> {
                                    Toast.makeText(
                                        this@DetailRatingActivity,
                                        "Anda tidak dapat memilih rating lebih dari 3",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            }
                        }
                    }
                }

            })

        penerimaanAdapter = RatingPenerimaanAdapter(
            arrayListOf(),
            object : RatingPenerimaanAdapter.OnAdapterListener {
                override fun onClick(po: dev.iconpln.mims.data.local.database.TRating) {
                    if (penerimaan.ratingDone == 0) {
                        when (po.kdRating) {
                            "11" -> {
                                for (i in ratingPenerimaans) {
                                    if (i.kdRating == "11") {
                                        i.isActive = 1
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    "Kualitas Penerimaan"
                                                )
                                            ).list()
                                        binding.txtMessagePenerimaan.visibility = View.VISIBLE
                                        binding.txtMessagePenerimaan.text = po.keterangan
                                        ketPenerimaan = po.keterangan
                                        nilaiPenerimaan = po.nilai
                                        ratingPenerimaan = po.kdRating
                                        penerimaanAdapter.setRatList(rating)
                                    } else {
                                        i.isActive = 0
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    "Kualitas Penerimaan"
                                                )
                                            ).list()
                                        penerimaanAdapter.setRatList(rating)
                                    }
                                }
                            }

                            "12" -> {
                                for (i in ratingPenerimaans) {
                                    if (i.kdRating == "11" || i.kdRating == "12") {
                                        i.isActive = 1
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    "Kualitas Penerimaan"
                                                )
                                            ).list()
                                        binding.txtMessagePenerimaan.visibility = View.VISIBLE
                                        binding.txtMessagePenerimaan.text = po.keterangan
                                        ketPenerimaan = po.keterangan
                                        nilaiPenerimaan = po.nilai
                                        ratingPenerimaan = po.kdRating
                                        penerimaanAdapter.setRatList(rating)
                                    } else {
                                        i.isActive = 0
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    "Kualitas Penerimaan"
                                                )
                                            ).list()
                                        penerimaanAdapter.setRatList(rating)
                                    }
                                }
                            }

                            "13" -> {
                                for (i in ratingPenerimaans) {
                                    if (i.kdRating == "11" || i.kdRating == "12" || i.kdRating == "13") {
                                        i.isActive = 1
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    "Kualitas Penerimaan"
                                                )
                                            ).list()
                                        binding.txtMessagePenerimaan.visibility = View.VISIBLE
                                        binding.txtMessagePenerimaan.text = po.keterangan
                                        ketPenerimaan = po.keterangan
                                        nilaiPenerimaan = po.nilai
                                        ratingPenerimaan = po.kdRating
                                        penerimaanAdapter.setRatList(rating)
                                    } else {
                                        i.isActive = 0
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    "Kualitas Penerimaan"
                                                )
                                            ).list()
                                        penerimaanAdapter.setRatList(rating)
                                    }
                                }
                            }

                            "14" -> {
                                for (i in ratingPenerimaans) {
                                    if (i.kdRating == "11" || i.kdRating == "12" || i.kdRating == "13" || i.kdRating == "14") {
                                        i.isActive = 1
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    "Kualitas Penerimaan"
                                                )
                                            ).list()
                                        binding.txtMessagePenerimaan.visibility = View.VISIBLE
                                        binding.txtMessagePenerimaan.text = po.keterangan
                                        ketPenerimaan = po.keterangan
                                        nilaiPenerimaan = po.nilai
                                        ratingPenerimaan = po.kdRating
                                        penerimaanAdapter.setRatList(rating)
                                    } else {
                                        i.isActive = 0
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    "Kualitas Penerimaan"
                                                )
                                            ).list()
                                        penerimaanAdapter.setRatList(rating)
                                    }
                                }
                            }

                            "15" -> {
                                for (i in ratingPenerimaans) {
                                    if (i.kdRating == "11" || i.kdRating == "12" || i.kdRating == "13" || i.kdRating == "14" || i.kdRating == "15") {
                                        i.isActive = 1
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    "Kualitas Penerimaan"
                                                )
                                            ).list()
                                        binding.txtMessagePenerimaan.visibility = View.VISIBLE
                                        binding.txtMessagePenerimaan.text = po.keterangan
                                        ketPenerimaan = po.keterangan
                                        nilaiPenerimaan = po.nilai
                                        ratingPenerimaan = po.kdRating
                                        penerimaanAdapter.setRatList(rating)
                                    } else {
                                        i.isActive = 0
                                        daoSession.tRatingDao.update(i)
                                        val rating = daoSession.tRatingDao.queryBuilder()
                                            .where(
                                                dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(
                                                    "Kualitas Penerimaan"
                                                )
                                            ).list()
                                        penerimaanAdapter.setRatList(rating)
                                    }
                                }
                            }
                        }
                    }
                }

            })

        adapter.setPhotoList(listPhoto)
        penyediaAdapter.setRatList(ratingPenyedias)
        waktuAdapter.setRatList(ratingWaktus)
        penerimaanAdapter.setRatList(ratingPenerimaans)

        with(binding) {

            if (listPhoto.isEmpty()) {
                btnUploadPhoto.visibility = View.VISIBLE
                maksfoto.visibility = View.VISIBLE
            } else {
                btnUploadPhoto.visibility = View.GONE
                maksfoto.visibility = View.GONE
            }

            rvAddFoto.adapter = adapter
            rvAddFoto.setHasFixedSize(true)
            rvAddFoto.layoutManager =
                LinearLayoutManager(this@DetailRatingActivity, LinearLayoutManager.VERTICAL, false)

            rvResponPenyedia.adapter = penyediaAdapter
            rvResponPenyedia.setHasFixedSize(true)
            rvResponPenyedia.layoutManager = LinearLayoutManager(
                this@DetailRatingActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            rvKualitasPenerimaan.adapter = penerimaanAdapter
            rvKualitasPenerimaan.setHasFixedSize(true)
            rvKualitasPenerimaan.layoutManager = LinearLayoutManager(
                this@DetailRatingActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            rvWaktuPengiriman.adapter = waktuAdapter
            rvWaktuPengiriman.setHasFixedSize(true)
            rvWaktuPengiriman.layoutManager = LinearLayoutManager(
                this@DetailRatingActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            if (listPhoto.isEmpty()) {
                btnUploadPhoto.visibility = View.VISIBLE
                maksfoto.visibility = View.VISIBLE
            } else {
                btnUploadPhoto.visibility = View.GONE
                maksfoto.visibility = View.GONE
            }

            btnUploadPhoto.setOnClickListener { doFoto() }
            btnClose.setOnClickListener { onBackPressed() }
            btnSimpan.setOnClickListener { validated() }

            if (role == 10) {
                lblFotoSuratBarang.visibility = View.GONE
                btnUploadPhoto.visibility = View.GONE
                maksfoto.visibility = View.GONE
                btnSimpan.isEnabled = false
                btnSimpan.setBackgroundColor(Color.GRAY)
            }
        }

    }

    private fun setDefaultRating() {

        if (penerimaan.ratingDone == 1) {
            getDokumentasi()
            binding.lblDokumentasi.visibility = View.VISIBLE
            binding.rvDokumentasiRating.visibility = View.VISIBLE
            binding.btnUploadPhoto.setBackgroundColor(Color.parseColor("#0096979B"))
            binding.lblFotoSuratBarang.setTextColor(Color.parseColor("#0096979B"))
            binding.maksfoto.setTextColor(Color.parseColor("#0096979B"))
            binding.btnSimpan.visibility = View.GONE

            for (i in ratingPenerimaans) {
                i.isActive = 1
                daoSession.tRatingDao.update(i)
                ratingPenerimaan = dataRating.ratingDelivery
                ketPenerimaan = i.keterangan
                nilaiPenerimaan = i.nilai
                binding.txtMessagePenerimaan.visibility = View.VISIBLE
                binding.txtMessagePenerimaan.text = i.keterangan
            }

            for (i in ratingPenyedias) {
                i.isActive = 1
                daoSession.tRatingDao.update(i)
                ratingPenyedia = dataRating.ratingDelivery
                ketPenyedia = i.keterangan
                nilaiPenyedia = i.nilai
                binding.txtMessageRespon.visibility = View.VISIBLE
                binding.txtMessageRespon.text = i.keterangan
            }
        }

        if (dataRating.ratingDelivery == "25") {
            for (i in ratingWaktus) {
                if (i.kdRating == "21" || i.kdRating == "22" || i.kdRating == "23" || i.kdRating == "24" || i.kdRating == "25") {
                    i.isActive = 1
                    daoSession.tRatingDao.update(i)
                    ratingWaktu = dataRating.ratingDelivery
                    ketWaktu = i.keterangan
                    nilaiWaktu = i.nilai
                    binding.txtMessageWaktu.visibility = View.VISIBLE
                    binding.txtMessageWaktu.text = i.keterangan
                }
            }
        }

        if (dataRating.ratingDelivery == "24") {
            for (i in ratingWaktus) {
                if (i.kdRating == "21" || i.kdRating == "22" || i.kdRating == "23" || i.kdRating == "24") {
                    i.isActive = 1
                    daoSession.tRatingDao.update(i)
                    ratingWaktu = dataRating.ratingDelivery
                    ketWaktu = i.keterangan
                    nilaiWaktu = i.nilai
                    binding.txtMessageWaktu.visibility = View.VISIBLE
                    binding.txtMessageWaktu.text = i.keterangan
                }
            }
        }
    }

    private fun generateNilaiRating(
        rating: String,
        daoSession: dev.iconpln.mims.data.local.database.DaoSession,
        type: String
    ): dev.iconpln.mims.data.local.database.TRating {
        val data = daoSession.tRatingDao.queryBuilder()
            .where(dev.iconpln.mims.data.local.database.TRatingDao.Properties.KdRating.eq(rating))
            .where(dev.iconpln.mims.data.local.database.TRatingDao.Properties.Type.eq(type))
            .list()[0]

        return data
    }

    private fun validated() {
        if (ketWaktu.isNullOrEmpty()) {
            Toast.makeText(
                this@DetailRatingActivity,
                "Silahkan melakukan rating dahulu",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (nilaiWaktu == 0) {
            Toast.makeText(
                this@DetailRatingActivity,
                "Silahkan melakukan rating dahulu",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (ketPenerimaan.isNullOrEmpty()) {
            Toast.makeText(
                this@DetailRatingActivity,
                "Silahkan melakukan rating dahulu",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (nilaiPenerimaan == 0) {
            Toast.makeText(
                this@DetailRatingActivity,
                "Silahkan melakukan rating dahulu",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (ketPenyedia.isNullOrEmpty()) {
            Toast.makeText(
                this@DetailRatingActivity,
                "Silahkan melakukan rating dahulu",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (nilaiPenyedia == 0) {
            Toast.makeText(
                this@DetailRatingActivity,
                "Silahkan melakukan rating dahulu",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (listPhoto.isEmpty()) {
            Toast.makeText(
                this@DetailRatingActivity,
                "Silahkan ambil foto terlebih dahulu",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        insertData()
    }

    private fun insertData() {
        for (i in ratingPenerimaans) {
            i.isActive = 0
            daoSession.tRatingDao.update(i)
        }

        for (i in ratingWaktus) {
            i.isActive = 0
            daoSession.tRatingDao.update(i)
        }

        for (i in ratingPenyedias) {
            i.isActive = 0
            daoSession.tRatingDao.update(i)
        }

        penerimaan.descWaktu = ketWaktu
        penerimaan.descQuality = ketPenyedia
        penerimaan.descPenerimaan = ketPenerimaan
        penerimaan.ratingQuality = nilaiPenyedia.toString()
        penerimaan.ratingPenerimaan = nilaiPenerimaan.toString()
        penerimaan.ratingWaktu = nilaiWaktu.toString()
        penerimaan.doStatus = "DITERIMA"
        penerimaan.ratingDone = 1

        daoSession.tPosPenerimaanDao.update(penerimaan)

        val dialog = Dialog(this@DetailRatingActivity)
        dialog.setContentView(R.layout.popup_penerimaan);
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false);
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown;
        val btnOk = dialog.findViewById(R.id.btn_ok) as AppCompatButton
        val message = dialog.findViewById(R.id.textView16) as TextView
        val txtMessage = dialog.findViewById(R.id.textView22) as TextView

        message.text = "Berhasil"
        txtMessage.text = "Data berhasil dirating"

        btnOk.setOnClickListener {
            submitForm()
            dialog.dismiss();
            dialogLoading.show()
        }

        dialog.show();
    }

    private fun doFoto() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        )
            ActivityCompat.requestPermissions(
                this@DetailRatingActivity, arrayOf(
                    Manifest.permission.CAMERA
                ), cameraRequestFoto
            )

        val dialog = BottomSheetDialog(this@DetailRatingActivity, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_photo, null)
        var btnCamera = view.findViewById<CardView>(R.id.cv_kamera)
        var btnGallery = view.findViewById<CardView>(R.id.cv_gallery)

        btnCamera.setOnClickListener {
//                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                    startActivityForResult(cameraIntent, cameraRequestFotoSuratBarang)
            val intent = Intent(this@DetailRatingActivity, CameraXActivity::class.java)
                .putExtra("fotoName", "fotoRating")
            startActivityForResult(intent, cameraRequestFoto)
            dialog.dismiss()
        }

        btnGallery.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, galleryRequestFoto)
            dialog.dismiss()
        }

        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    private fun submitForm() {
        val reports = ArrayList<GenericReport>()
        val currentDate = LocalDateTime.now().toString(Config.DATE)
        val currentDateTime = LocalDateTime.now().toString(Config.DATETIME)
        val currentUtc = DateTimeUtils.currentUtc

        penerimaan.isDone = 1
        penerimaan.statusPenerimaan = "DITERIMA"
        daoSession.tPosPenerimaanDao.update(penerimaan)

        //region Add report visit to queue
        var jwtWeb = SharedPrefsUtils.getStringPreference(this@DetailRatingActivity, "jwtWeb", "")
        var jwtMobile = SharedPrefsUtils.getStringPreference(this@DetailRatingActivity, "jwt", "")
        var jwt = "$jwtMobile;$jwtWeb"
        var username = SharedPrefsUtils.getStringPreference(
            this@DetailRatingActivity,
            "username",
            "14.Hexing_Electrical"
        )
        val reportId = "temp_penerimaan" + username + "_" + noDo + "_" + DateTime.now().toString(
            Config.DATETIME
        )
        val reportName = "Update Data Penerimaan"
        val reportDescription = "$reportName: " + " (" + reportId + ")"
        val params = ArrayList<ReportParameter>()
        params.add(ReportParameter("1", reportId, "no_do_smar", noDo!!, ReportParameter.TEXT))
        params.add(
            ReportParameter(
                "2",
                reportId,
                "qty_terima",
                detailPenerimaan.size.toString(),
                ReportParameter.TEXT
            )
        )
        params.add(ReportParameter("3", reportId, "qty_reject", "0", ReportParameter.TEXT))
        params.add(
            ReportParameter(
                "4",
                reportId,
                "rating_delivery",
                ratingWaktu!!,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "5",
                reportId,
                "rating_response",
                ratingPenyedia!!,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "6",
                reportId,
                "rating_quality",
                ratingPenerimaan!!,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "7",
                reportId,
                "rating_delivery_point",
                nilaiWaktu.toString(),
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "8",
                reportId,
                "rating_response_point",
                nilaiPenyedia.toString(),
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "9",
                reportId,
                "rating_quality_point",
                nilaiPenerimaan.toString(),
                ReportParameter.TEXT
            )
        )
        params.add(ReportParameter("10", reportId, "username", username!!, ReportParameter.TEXT))
        params.add(ReportParameter("11", reportId, "email", username!!, ReportParameter.TEXT))


        var i = 1
        var reportParameter = 12
        for (j in listPhoto) {
            params.add(
                ReportParameter(
                    reportParameter.toString(),
                    reportId,
                    "photos$i",
                    j.path,
                    ReportParameter.FILE
                )
            )
            i++
            reportParameter++
        }

        val report = GenericReport(
            reportId,
            jwt!!,
            reportName,
            reportDescription,
            ApiConfig.sendRating(),
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == galleryRequestFoto) {
            val imageUri = data?.data
//            val imageStream = contentResolver.openInputStream(imageUri!!)
//            val bitmap: Bitmap = BitmapFactory.decodeStream(imageStream)
//
//            val file_path = StorageUtils.getDirectory(StorageUtils.DIRECTORY_ROOT) +
//                    "/Images"
//            val dir = File(file_path)
//            if (!dir.exists()) dir.mkdirs()
//            val file = File(dir, "mims" + "picturesFotoKomplain-${noDo}-${DateTime.now()}" + ".png")
//            val fOut = FileOutputStream(file)
//
//            bitmap.compress(Bitmap.CompressFormat.PNG, 65, fOut)
//            fOut.flush()
//            fOut.close()

            insertDataFoto(File(getRealPathFromURI(imageUri!!).toString()))

        } else {
        }

        if (resultCode == RESULT_OK && requestCode == cameraRequestFoto) {
            val mPhotoPath = data?.getStringExtra("Path")

            var photo = dev.iconpln.mims.data.local.database.TPhoto()
            photo.noDo = noDo
            photo.type = "rating"
            photo.path = mPhotoPath
            photo.photoNumber = photoNumber++
            daoSession.tPhotoDao.insert(photo)

            listPhoto = daoSession.tPhotoDao.queryBuilder()
                .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.NoDo.eq(noDo))
                .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.Type.eq("rating"))
                .list()

            adapter.setPhotoList(listPhoto)

            if (listPhoto.isEmpty()) {
                binding.btnUploadPhoto.visibility = View.VISIBLE
                binding.maksfoto.visibility = View.VISIBLE
            } else {
                binding.btnUploadPhoto.visibility = View.GONE
                binding.maksfoto.visibility = View.GONE
            }
        } else {
        }
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        return cursor?.use {
            it.moveToFirst()
            val columnIndex = it.getColumnIndex(MediaStore.Images.Media.DATA)
            it.getString(columnIndex)
        }
    }

    private fun insertDataFoto(file: File) {

        if (file.length() > Config.MAX_PHOTO_SIZE) {
            Toast.makeText(
                this@DetailRatingActivity,
                getString(R.string.ukuran_foto_tidak_boleh_melebihi_2_mb),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        var photo = dev.iconpln.mims.data.local.database.TPhoto()
        photo.noDo = noDo
        photo.type = "rating"
        photo.path = file.toString()
        photo.photoNumber = photoNumber++
        daoSession.tPhotoDao.insert(photo)

        listPhoto = daoSession.tPhotoDao.queryBuilder()
            .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.NoDo.eq(noDo))
            .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.Type.eq("rating"))
            .list()

        adapter.setPhotoList(listPhoto)

        if (listPhoto.isEmpty()) {
            binding.btnUploadPhoto.visibility = View.VISIBLE
            binding.maksfoto.visibility = View.VISIBLE
        } else {
            binding.btnUploadPhoto.visibility = View.GONE
            binding.maksfoto.visibility = View.GONE

        }
    }

    override fun setLoading(show: Boolean, title: String, message: String) {
    }

    override fun setFinish(result: Boolean, message: String) {
        startActivity(
            Intent(this@DetailRatingActivity, RatingActivity::class.java)
                .putExtra("noDo", noDo)
        )
        finish()
        daoSession.tPhotoDao.deleteInTx(listPhoto)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        dialogLoading.dismiss()
    }

    override fun onBackPressed() {
        val dialog = Dialog(this@DetailRatingActivity)
        dialog.setContentView(R.layout.popup_validation);
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false);
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown;
        val btnYa = dialog.findViewById(R.id.btn_ya) as AppCompatButton
        val btnTidak = dialog.findViewById(R.id.btn_tidak) as AppCompatButton
        val message = dialog.findViewById(R.id.message) as TextView
        val txtMessage = dialog.findViewById(R.id.txt_message) as TextView
        val icon = dialog.findViewById(R.id.imageView11) as ImageView

        message.text = "Yakin untuk keluar?"
        txtMessage.text = "Jika keluar setiap perubahan tidak akan tersimpan"
        icon.setImageResource(R.drawable.ic_warning)

        btnYa.setOnClickListener {
            dialog.dismiss();
// diubah
            if (listPhoto.isNullOrEmpty()) {
                daoSession.tPhotoDao.deleteInTx(listPhoto)
            }

            for (i in ratingPenyedias) {
                i.isActive = 0
                daoSession.tRatingDao.update(i)
            }

            for (i in ratingWaktus) {
                i.isActive = 0
                daoSession.tRatingDao.update(i)
            }

            for (i in ratingPenerimaans) {
                i.isActive = 0
                daoSession.tRatingDao.update(i)
            }

            startActivity(
                Intent(this@DetailRatingActivity, RatingActivity::class.java)
                    .putExtra("noDo", noDo)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
            finish()
        }

        btnTidak.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show();
    }

//    private fun getDokumentasi() {
//        binding.loading.visibility = View.VISIBLE
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val noRating = dataRating?.noRating
//                if (noRating != null) {
//                    val response = ApiConfig.getApiService(this@DetailRatingActivity)
//                        .getDokumentasiRating(noRating)
//                    withContext(Dispatchers.Main) {
//                        if (response.isSuccessful) {
//                            binding.loading.visibility = View.GONE
//                            val isLoginSso = SharedPrefsUtils.getIntegerPreference(
//                                this@DetailRatingActivity,
//                                Config.IS_LOGIN_SSO,
//                                0
//                            )
//                            try {
//                                if (response.body()?.status == Config.KEY_SUCCESS) {
//                                    listDokumentasi = response.body()?.doc!!
//
//                                    val adapterPhoto =
//                                        DetailPenerimaanDokumentasiRatingAdapter(arrayListOf())
//                                    binding.rvDokumentasiRating.adapter = adapterPhoto
//                                    binding.rvDokumentasiRating.layoutManager =
//                                        LinearLayoutManager(
//                                            this@DetailRatingActivity,
//                                            LinearLayoutManager.HORIZONTAL,
//                                            false
//                                        )
//                                    binding.rvDokumentasiRating.setHasFixedSize(true)
//
//                                    adapterPhoto.setData(listDokumentasi)
//                                } else {
//                                    when (response.body()?.message) {
//                                        Config.DO_LOGOUT -> {
//                                            Helper.logout(this@DetailRatingActivity, isLoginSso)
//                                            Toast.makeText(
//                                                this@DetailRatingActivity,
//                                                getString(R.string.session_kamu_telah_habis),
//                                                Toast.LENGTH_SHORT
//                                            ).show()
//                                        }
//
//                                        else -> Toast.makeText(
//                                            this@DetailRatingActivity,
//                                            response.body()?.message,
//                                            Toast.LENGTH_SHORT
//                                        ).show()
//                                    }
//                                }
//                            } catch (e: Exception) {
//                                Toast.makeText(
//                                    this@DetailRatingActivity,
//                                    response.body()?.message,
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                e.printStackTrace()
//                            } finally {
//                                // Any cleanup code if needed
//                            }
//                        } else {
//                            binding.loading.visibility = View.GONE
//                            Toast.makeText(
//                                this@DetailRatingActivity,
//                                response.message(),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                } else {
//                    // Handle the case when noRating is null
//                    withContext(Dispatchers.Main) {
//                        binding.loading.visibility = View.GONE
//                        Toast.makeText(
//                            this@DetailRatingActivity,
//                            "No rating is null",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//}


    private fun getDokumentasi() {
        binding.loading.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val response = ApiConfig.getApiService(this@DetailRatingActivity)
                .getDokumentasiRating(dataRating.noRating!!)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    binding.loading.visibility = View.GONE
                    val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                        this@DetailRatingActivity,
                        Config.IS_LOGIN_SSO, 0
                    )
                    try {
                        if (response.body()?.status == Config.KEY_SUCCESS) {
                            listDokumentasi = response.body()?.doc!!

                            val adapterPhoto =
                                DetailPenerimaanDokumentasiRatingAdapter(arrayListOf())
                            binding.rvDokumentasiRating.adapter = adapterPhoto
                            binding.rvDokumentasiRating.layoutManager = LinearLayoutManager(
                                this@DetailRatingActivity,
                                LinearLayoutManager.HORIZONTAL, false
                            )
                            binding.rvDokumentasiRating.setHasFixedSize(true)

                            adapterPhoto.setData(listDokumentasi)
                        } else {
                            when (response.body()?.message) {
                                Config.DO_LOGOUT -> {
                                    Helper.logout(this@DetailRatingActivity, isLoginSso)
                                    Toast.makeText(
                                        this@DetailRatingActivity,
                                        getString(R.string.session_kamu_telah_habis),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> Toast.makeText(
                                    this@DetailRatingActivity,
                                    response.body()?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@DetailRatingActivity,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        e.printStackTrace()
                    } finally {
                    }
                } else {
                    binding.loading.visibility = View.GONE
                    Toast.makeText(
                        this@DetailRatingActivity,
                        response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}