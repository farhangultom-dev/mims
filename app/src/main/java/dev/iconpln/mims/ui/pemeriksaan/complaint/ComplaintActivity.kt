package dev.iconpln.mims.ui.pemeriksaan.complaint

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
import dev.iconpln.mims.databinding.ActivityComplaintBinding
import dev.iconpln.mims.tasks.Loadable
import dev.iconpln.mims.tasks.TambahReportTask
import dev.iconpln.mims.ui.penerimaan.PenerimaanActivity
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

class ComplaintActivity : BaseActivity(), Loadable {
    private lateinit var binding: ActivityComplaintBinding
    private var progressDialog: AlertDialog? = null
    private lateinit var daoSession: dev.iconpln.mims.data.local.database.DaoSession
    private lateinit var listPhoto: List<dev.iconpln.mims.data.local.database.TPhoto>
    private lateinit var penerimaan: dev.iconpln.mims.data.local.database.TPosPenerimaan
    private lateinit var adapter: AddPhotoAdapter
    private val cameraRequestFoto = 101
    private val galleryRequestFoto = 102
    private var noDo: String? = ""
    private var photoNumber: Int = 0
    private lateinit var dialogLoading: Dialog
    private lateinit var listDokumentasi: List<String>
    private lateinit var listDetailPen: MutableList<dev.iconpln.mims.data.local.database.TPosDetailPenerimaan>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComplaintBinding.inflate(layoutInflater)
        setContentView(binding.root)
        noDo = intent.getStringExtra("noDo")
        dialogLoading = Helper.loadingDialog(this)
        getDokumentasi(noDo!!)

        daoSession = (application as MyApplication).daoSession!!
        listPhoto = daoSession.tPhotoDao.queryBuilder()
            .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.NoDo.eq(noDo))
            .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.Type.eq("complaint"))
            .list()

        listDetailPen = daoSession.tPosDetailPenerimaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.NoDoSmar.eq(
                    noDo
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsDone.eq(
                    0
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsChecked.eq(
                    0
                )
            ).list()

        penerimaan = daoSession.tPosPenerimaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosPenerimaanDao.Properties.NoDoSmar.eq(
                    noDo
                )
            )
            .limit(1).unique()
        photoNumber = listPhoto.size + 1

        adapter = AddPhotoAdapter(arrayListOf(), object : AddPhotoAdapter.OnAdapterListener {
            override fun onClick(po: dev.iconpln.mims.data.local.database.TPhoto) {
                if (po.photoNumber == 5) {
                    Toast.makeText(
                        this@ComplaintActivity,
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
                        .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.Type.eq("complaint"))
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
            rvAddFoto.layoutManager =
                LinearLayoutManager(this@ComplaintActivity, LinearLayoutManager.VERTICAL, false)
            rvAddFoto.setHasFixedSize(true)

            txtDeliveryOrder.text = penerimaan.noDoSmar
            txtKurirPengiriman.text = penerimaan.expeditions
            txtNamaKurir.text = penerimaan.kurirPengantar
            txtTglKirim.text = "Tgl ${penerimaan.createdDate}"

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
                val dialog = Dialog(this@ComplaintActivity)
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
                    this@ComplaintActivity,
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
                    this@ComplaintActivity,
                    "Silahkan lengkapi data feedback",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            if (listPhoto.isEmpty()) {
                Toast.makeText(
                    this@ComplaintActivity,
                    "Silahkan lengkapi foto complaint",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            showPopUp()
        }
    }

    private fun showPopUp() {
        val dialog = Dialog(this@ComplaintActivity)
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
            dialogLoading.show()
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

        var checkedDetPen = daoSession.tPosDetailPenerimaanDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.NoDoSmar.eq(
                    noDo
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsDone.eq(
                    0
                )
            )
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsChecked.eq(
                    1
                )
            ).list()

        var detailPenerimaanAkhir = daoSession.tPosDetailPenerimaanAkhirDao.queryBuilder()
            .where(
                dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhirDao.Properties.NoDoSmar.eq(
                    noDo
                )
            ).list()

        for (i in checkedDetPen) {
            sns += "${i.noPackaging},${i.serialNumber},SEDANG KOMPLAIN,${i.doLineItem},${i.noMaterial};"

        }
        if (sns != "") {
            sns = sns.substring(0, sns.length - 1)
        }

        for (i in checkedDetPen) {
            i.isComplaint = 1
            i.isDone = 1
            i.statusPenerimaan = "KOMPLAIN"
            i.statusPemeriksaan = "SELESAI"
            daoSession.tPosDetailPenerimaanDao.update(i)

            for (j in detailPenerimaanAkhir) {
                if (j.serialNumber == i.serialNumber) {
                    j.isComplaint = true
                    j.status = "TIDAK SESUAI"
                    daoSession.tPosDetailPenerimaanAkhirDao.update(j)
                }
            }
        }

        penerimaans.statusPenerimaan = "SEDANG KOMPLAIN"
        penerimaans.statusPemeriksaan = "BELUM DIPERIKSA"
        daoSession.tPosPenerimaanDao.update(penerimaans)

        val reports = ArrayList<GenericReport>()
        val currentDate = LocalDateTime.now().toString(Config.DATE)
        val currentDateTime = LocalDateTime.now().toString(Config.DATETIME)
        val currentUtc = DateTimeUtils.currentUtc

        //region Add report visit to queue
        var jwtWeb = SharedPrefsUtils.getStringPreference(this@ComplaintActivity, "jwtWeb", "")
        var jwtMobile = SharedPrefsUtils.getStringPreference(this@ComplaintActivity, "jwt", "")
        var jwt = "$jwtMobile;$jwtWeb"
        var username = SharedPrefsUtils.getStringPreference(
            this@ComplaintActivity,
            "username",
            "14.Hexing_Electrical"
        )
        val reportId = "temp_penerimaan" + username + "_" + noDo + "_" + DateTime.now().toString(
            Config.DATETIME
        )
        val reportName = "Update Data Komplain Penerimaan"
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
                checkedDetPen.size.toString(),
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
                penerimaan.planCodeNo,
                ReportParameter.TEXT
            )
        )
        params.add(
            ReportParameter(
                "9",
                reportId,
                "no_do_line",
                penerimaan.doLineItem,
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
            ApiConfig.sendComplaint(),
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
                this@ComplaintActivity, arrayOf(
                    Manifest.permission.CAMERA
                ), cameraRequestFoto
            )

        val dialog = BottomSheetDialog(this@ComplaintActivity, R.style.AppBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_photo, null)
        var btnCamera = view.findViewById<CardView>(R.id.cv_kamera)
        var btnGallery = view.findViewById<CardView>(R.id.cv_gallery)

        btnCamera.setOnClickListener {
            val intent = Intent(this@ComplaintActivity, CameraXActivity::class.java)
                .putExtra("fotoName", "fotoComplaingPenerimaan")
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
            photo.noDo = noDo
            photo.type = "complaint"
            photo.path = mPhotoPath
            photo.photoNumber = photoNumber++
            daoSession.tPhotoDao.insert(photo)

            listPhoto = daoSession.tPhotoDao.queryBuilder()
                .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.Type.eq("complaint"))
                .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.NoDo.eq(noDo))
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
                this@ComplaintActivity,
                getString(R.string.ukuran_foto_tidak_boleh_melebihi_2_mb),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        var photo = dev.iconpln.mims.data.local.database.TPhoto()
        photo.noDo = noDo
        photo.type = "complaint"
        photo.path = file.toString()
        photo.photoNumber = photoNumber++
        daoSession.tPhotoDao.insert(photo)

        listPhoto = daoSession.tPhotoDao.queryBuilder()
            .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.Type.eq("complaint"))
            .where(dev.iconpln.mims.data.local.database.TPhotoDao.Properties.NoDo.eq(noDo))
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
            val response = ApiConfig.getApiService(this@ComplaintActivity).getDokumentasi(noDo)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val isLoginSso = SharedPrefsUtils.getIntegerPreference(
                        this@ComplaintActivity,
                        Config.IS_LOGIN_SSO, 0
                    )
                    try {
                        if (response.body()?.status == Config.KEY_SUCCESS) {
                            listDokumentasi = response.body()?.doc?.array!!
                        } else {
                            when (response.body()?.message) {
                                Config.DO_LOGOUT -> {
                                    Helper.logout(this@ComplaintActivity, isLoginSso)
                                    Toast.makeText(
                                        this@ComplaintActivity,
                                        getString(R.string.session_kamu_telah_habis),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                else -> Toast.makeText(
                                    this@ComplaintActivity,
                                    response.body()?.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ComplaintActivity,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        e.printStackTrace()
                    } finally {
                    }
                } else {
                    Toast.makeText(this@ComplaintActivity, response.message(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onBackPressed() {
        daoSession.tPhotoDao.deleteInTx(listPhoto)
        super.onBackPressed()
    }

    override fun setFinish(result: Boolean, message: String) {
        if (result) {
        }
        daoSession.tPhotoDao.deleteInTx(listPhoto)
        startActivity(
            Intent(this@ComplaintActivity, PenerimaanActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
        finish()
        dialogLoading.dismiss()
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}