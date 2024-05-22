package dev.iconpln.mims.ui.pemeriksaan.complaint_pemeriksaan

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.iconpln.mims.AddPhotoAdapter
import dev.iconpln.mims.BaseActivity
import dev.iconpln.mims.CameraXActivity
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.R
import dev.iconpln.mims.data.local.database_local.GenericReport
import dev.iconpln.mims.data.local.database_local.ReportParameter
import dev.iconpln.mims.data.local.database_local.ReportUploader
import dev.iconpln.mims.data.remote.service.ApiConfig
import dev.iconpln.mims.databinding.ActivityComplaintPemeriksaanBinding
import dev.iconpln.mims.tasks.Loadable
import dev.iconpln.mims.tasks.TambahReportTask
import dev.iconpln.mims.ui.pemeriksaan.PemeriksaanActivity
import dev.iconpln.mims.ui.penerimaan.detail_penerimaan.DetailPenerimaanDokumentasiAdapter
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

class ComplaintPemeriksaanActivity : BaseActivity(), Loadable {
    private lateinit var binding: ActivityComplaintPemeriksaanBinding
    private var progressDialog: AlertDialog? = null
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private lateinit var listPhoto: List<dev.iconpln.mims.data.local.database.TPhoto>
    private lateinit var pemeriksaan: dev.iconpln.mims.data.local.database.TPemeriksaan
    private lateinit var adapter: AddPhotoAdapter
    private val cameraRequestFoto = 101
    private val galleryRequestFoto = 102
    private var noPem: String? = ""
    private var noDo: String? = ""
    private var photoNumber: Int = 0
    private lateinit var listDokumentasi: List<String>
    private lateinit var listDetailPem: MutableList<dev.iconpln.mims.data.local.database.TPemeriksaanDetail>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplaintPemeriksaanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        noPem = intent.getStringExtra("noPemeriksaan")
        noDo = intent.getStringExtra("noDo")

        getDokumentasi(noDo!!)

        daoSession = (application as MyApplication).daoSession!!
        listPhoto = daoSession.tPhotoDao.queryBuilder()
            .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.NoDo.eq(noPem))
            .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.Type.eq("complaint_pemeriksaan"))
            .list()

        listDetailPem = daoSession.tPemeriksaanDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.NoPemeriksaan.eq(
                    noPem
                )
            )
            .where(dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsDone.eq(0))
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsChecked.eq(
                    0
                )
            ).list()

        pemeriksaan = daoSession.tPemeriksaanDao.queryBuilder().where(
            dev.iconpln.mims.data.local.database.TPemeriksaanDao.Properties.NoPemeriksaan.eq(noPem)
        ).limit(1).unique()
        photoNumber = listPhoto.size + 1

        adapter = AddPhotoAdapter(arrayListOf(), object : AddPhotoAdapter.OnAdapterListener {
            override fun onClick(po: dev.iconpln.mims.data.local.database.TPhoto) {
                if (po.photoNumber == 5) {
                    Toast.makeText(
                        this@ComplaintPemeriksaanActivity,
                        "Anda sudah melebihi batas upload foto",
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
                        .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.Type.eq("complaint_pemeriksaan"))
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
                        binding.txtFilePhoto.visibility = View.VISIBLE
                    } else {
                        binding.btnUploadPhoto.visibility = View.GONE
                        binding.txtFilePhoto.visibility = View.GONE
                    }
                }

            })

        adapter.setPhotoList(listPhoto)

        with(binding) {
            rvAddFoto.adapter = adapter
            rvAddFoto.layoutManager = LinearLayoutManager(
                this@ComplaintPemeriksaanActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            rvAddFoto.setHasFixedSize(true)

            txtDeliveryOrder.text = pemeriksaan.noDoSmar
            txtKurirPengiriman.text = pemeriksaan.expeditions
            txtNamaKurir.text = pemeriksaan.namaKurir
            txtTglKirim.text = "Tgl ${pemeriksaan.createdDate}"

            if (listPhoto.isEmpty()) {
                btnUploadPhoto.visibility = View.VISIBLE
                txtFilePhoto.visibility = View.VISIBLE
            } else {
                btnUploadPhoto.visibility = View.GONE
                txtFilePhoto.visibility = View.GONE
            }

            btnUploadPhoto.setOnClickListener {
                doFoto()
            }

            txtDokumentasi.setOnClickListener {
                val dialog = Dialog(this@ComplaintPemeriksaanActivity)
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
                recyclerView.layoutManager = LinearLayoutManager(
                    this@ComplaintPemeriksaanActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                recyclerView.setHasFixedSize(true)

                adapter.setData(listDokumentasi)

                btnClose.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show();
            }

            btnClose.setOnClickListener { onBackPressed() }

            btnSimpan.setOnClickListener { validation() }
        }
    }

    private fun validation() {
        with(binding) {
            val fbComplaint = editText.text.toString()

            if (fbComplaint.isNullOrEmpty()) {
                Toast.makeText(
                    this@ComplaintPemeriksaanActivity,
                    "Silahkan lengkapi data feedback",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (listPhoto.isEmpty()) {
                Toast.makeText(
                    this@ComplaintPemeriksaanActivity,
                    "Silahkan lengkapi foto complaint",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            showPopUp()
        }
    }

    private fun showPopUp() {
        val dialog = Dialog(this@ComplaintPemeriksaanActivity)
        dialog.setContentView(R.layout.popup_complaint);
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        dialog.setCancelable(false);
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown;
        val btnOk = dialog.findViewById(R.id.btn_ok) as AppCompatButton

        btnOk.setOnClickListener {
            dialog.dismiss();
            insertData()
        }

        dialog.show();

    }

    private fun insertData() {
        var sns = ""

        var penerimaans = daoSession.tPosPenerimaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosPenerimaanDao.Properties.NoDoSmar.eq(
                    noDo
                )
            ).list().get(0)

        var checkDetPem = daoSession.tPemeriksaanDetailDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.NoDoSmar.eq(
                    noDo
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.NoPemeriksaan.eq(
                    noPem
                )
            )
            .where(dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsDone.eq(0))
            .where(
                dev.iconpln.mims.data.local.database.TPemeriksaanDetailDao.Properties.IsChecked.eq(
                    1
                )
            ).list()

        var detailPenerimaanAkhir = daoSession.tPosDetailPenerimaanAkhirDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhirDao.Properties.NoDoSmar.eq(
                    noDo
                )
            ).list()

        for (i in checkDetPem) {
            sns += "${i.noPackaging},${i.sn},SEDANG KOMPLAIN,${pemeriksaan.doLineItem},${i.noMaterail};"

        }
        if (sns != "") {
            sns = sns.substring(0, sns.length - 1)
        }

        for (i in checkDetPem) {
            i.isComplaint = 1
            i.isDone = 1
            i.statusPenerimaan = "KOMPLAIN"
            i.statusPemeriksaan = "SELESAI"
            daoSession.tPemeriksaanDetailDao.update(i)

            for (j in detailPenerimaanAkhir) {
                if (j.serialNumber == i.sn) {
                    j.isComplaint = true
                    j.status = "TIDAK SESUAI"
                    daoSession.tPosDetailPenerimaanAkhirDao.update(j)
                }
            }
        }

        if (checkDetPem.isEmpty()) {
            pemeriksaan.isDone = 1
            daoSession.tPemeriksaanDao.update(pemeriksaan)
        }

        penerimaans.statusPenerimaan = "SEDANG KOMPLAIN"
        penerimaans.statusPemeriksaan = "BELUM DIPERIKSA"
        daoSession.tPosPenerimaanDao.update(penerimaans)

        val reports = ArrayList<GenericReport>()
        val currentDate = LocalDateTime.now().toString(Config.DATE)
        val currentDateTime = LocalDateTime.now().toString(Config.DATETIME)
        val currentUtc = DateTimeUtils.currentUtc

        //region Add report visit to queue
        var jwtWeb =
            SharedPrefsUtils.getStringPreference(this@ComplaintPemeriksaanActivity, "jwtWeb", "")
        var jwtMobile =
            SharedPrefsUtils.getStringPreference(this@ComplaintPemeriksaanActivity, "jwt", "")
        var jwt = "$jwtMobile;$jwtWeb"
        var username = SharedPrefsUtils.getStringPreference(
            this@ComplaintPemeriksaanActivity,
            "username",
            "14.Hexing_Electrical"
        )
        val reportId = "temp_pemeriksaan" + username + "_" + noDo + "_" + DateTime.now().toString(
            Config.DATETIME
        )
        val reportName = "Update Data Komplain Pemeriksaan"
        val reportDescription = "$reportName: " + " (" + reportId + ")"
        val params = ArrayList<ReportParameter>()
        params.add(ReportParameter("1", reportId, "no_do_smar", noDo!!, ReportParameter.TEXT))
        params.add(
            ReportParameter(
                "2",
                reportId,
                "alasan",
                binding.editText.text.toString(),
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "3",
                reportId,
                "quantity",
                checkDetPem.size.toString(),
                ReportParameter.TEXT
            )
        )
        params.add(ReportParameter("4", reportId, "username", username!!, ReportParameter.TEXT))
        params.add(ReportParameter("5", reportId, "email", username, ReportParameter.TEXT))
        params.add(ReportParameter("6", reportId, "sns", sns, ReportParameter.TEXT))
        params.add(ReportParameter("7", reportId, "status", "ACTIVE", ReportParameter.TEXT))
        params.add(
            ReportParameter(
                "8",
                reportId,
                "plant_code_no",
                pemeriksaan.planCodeNo,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "9",
                reportId,
                "no_do_line",
                pemeriksaan.doLineItem,
                ReportParameter.TEXT
            )
        )

        var i = 1
        var reportParameter = 10
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
            ApiConfig.sendComplaintPemeriksaan(),
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

    private fun doFoto() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        )
            ActivityCompat.requestPermissions(
                this@ComplaintPemeriksaanActivity, arrayOf(
                    Manifest.permission.CAMERA
                ), cameraRequestFoto
            )

        val dialog =
            BottomSheetDialog(this@ComplaintPemeriksaanActivity, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_photo, null)
        var btnCamera = view.findViewById<CardView>(R.id.cv_kamera)
        var btnGallery = view.findViewById<CardView>(R.id.cv_gallery)

        btnCamera.setOnClickListener {
//                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                    startActivityForResult(cameraIntent, cameraRequestFotoSuratBarang)
            val intent = Intent(this@ComplaintPemeriksaanActivity, CameraXActivity::class.java)
                .putExtra("fotoName", "fotoComplaintPemeriksaan")
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
            photo.noDo = noPem
            photo.type = "complaint_pemeriksaan"
            photo.path = mPhotoPath
            photo.photoNumber = photoNumber++
            daoSession.tPhotoDao.insert(photo)

            listPhoto = daoSession.tPhotoDao.queryBuilder()
                .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.Type.eq("complaint_pemeriksaan"))
                .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.NoDo.eq(noPem))
                .list()

            if (listPhoto.isEmpty()) {
                binding.btnUploadPhoto.visibility = View.VISIBLE
                binding.txtFilePhoto.visibility = View.VISIBLE
            } else {
                binding.btnUploadPhoto.visibility = View.GONE
                binding.txtFilePhoto.visibility = View.GONE
            }

            adapter.setPhotoList(listPhoto)
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
                this@ComplaintPemeriksaanActivity,
                getString(R.string.ukuran_foto_tidak_boleh_melebihi_2_mb),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        var photo = dev.iconpln.mims.data.local.database.TPhoto()
        photo.noDo = noPem
        photo.type = "complaint_pemeriksaan"
        photo.path = file.toString()
        photo.photoNumber = photoNumber++
        daoSession.tPhotoDao.insert(photo)

        listPhoto = daoSession.tPhotoDao.queryBuilder()
            .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.Type.eq("complaint_pemeriksaan"))
            .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.NoDo.eq(noPem))
            .list()

        if (listPhoto.isEmpty()) {
            binding.btnUploadPhoto.visibility = View.VISIBLE
            binding.txtFilePhoto.visibility = View.VISIBLE
        } else {
            binding.btnUploadPhoto.visibility = View.GONE
            binding.txtFilePhoto.visibility = View.GONE
        }

        adapter.setPhotoList(listPhoto)
    }

    override fun setLoading(show: Boolean, title: String, message: String) {
        try {
            if (progressDialog != null) {
                if (show) {
                    progressDialog!!.apply { show() }
                } else {
                    progressDialog!!.dismiss()
                }
            }

        } catch (e: Exception) {
            progressDialog!!.dismiss()
            e.printStackTrace()
        }
    }

    private fun getDokumentasi(noDo: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response =
                ApiConfig.getApiService(this@ComplaintPemeriksaanActivity).getDokumentasi(noDo)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                        this@ComplaintPemeriksaanActivity,
                        Config.IS_LOGIN_SSO, 0
                    )
                    try {
                        if (response.body()?.status == Config.KEY_SUCCESS) {
                            listDokumentasi = response.body()?.doc?.array!!
                        } else {
                            when (response.body()?.message) {
                                Config.DO_LOGOUT -> {
                                    Helper.logout(this@ComplaintPemeriksaanActivity, isLoginSso)
                                    Toast.makeText(
                                        this@ComplaintPemeriksaanActivity,
                                        getString(R.string.session_kamu_telah_habis),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> Toast.makeText(
                                    this@ComplaintPemeriksaanActivity,
                                    response.body()?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ComplaintPemeriksaanActivity,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        e.printStackTrace()
                    } finally {
                    }
                } else {
                    Toast.makeText(
                        this@ComplaintPemeriksaanActivity,
                        response.message(),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun setFinish(result: Boolean, message: String) {
        if (result) {
        }
        startActivity(
            Intent(this@ComplaintPemeriksaanActivity, PemeriksaanActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
        daoSession.tPhotoDao.deleteInTx(listPhoto)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onBackPressed() {
        daoSession.tPhotoDao.deleteInTx(listPhoto)
        super.onBackPressed()
    }
}