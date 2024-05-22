package dev.iconpln.mims.ui.home

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dev.iconpln.mims.MyApplication
import dev.iconpln.mims.R
import dev.iconpln.mims.data.local.database.DaoSession
import dev.iconpln.mims.data.local.database_local.DatabaseReport
import dev.iconpln.mims.databinding.FragmentHomeBinding
import dev.iconpln.mims.ui.attribute_material.DataAtributMaterialActivity
import dev.iconpln.mims.ui.auth.AuthViewModel
import dev.iconpln.mims.ui.inspeksi_material.InspeksiMaterialActivity
import dev.iconpln.mims.ui.monitoring.MonitoringActivity
import dev.iconpln.mims.ui.monitoring_complaint.MonitoringComplaintActivity
import dev.iconpln.mims.ui.monitoring_permintaan.MonitoringPermintaanActivity
import dev.iconpln.mims.ui.monitoring_stok.MonitoringStokActivity
import dev.iconpln.mims.ui.pemakaian.PemakaianActivity
import dev.iconpln.mims.ui.pemeriksaan.PemeriksaanActivity
import dev.iconpln.mims.ui.penerimaan.PenerimaanActivity
import dev.iconpln.mims.ui.penerimaan.PenerimaanViewModel
import dev.iconpln.mims.ui.pengiriman.PengirimanActivity
import dev.iconpln.mims.ui.pengujian.PengujianActivity
import dev.iconpln.mims.ui.registrasi_approval.approval.ApprovalActivity
import dev.iconpln.mims.ui.registrasi_approval.registrasi.RegistrasiSnMaterialActivity
import dev.iconpln.mims.ui.tracking.TrackingHistoryActivity
import dev.iconpln.mims.ui.ulp.penerimaan.PenerimaanUlpActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.ConnectionDetectorUtil
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils
import org.joda.time.DateTimeUtils

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dialogLoading: Dialog
    private lateinit var daoSession: DaoSession
    private var databaseReport: DatabaseReport? = null
    private val viewModel: AuthViewModel by viewModels()
    private val penerimaanViewModel: PenerimaanViewModel by viewModels()
    private lateinit var mAndroidId: String
    private lateinit var mAppVersion: String
    private lateinit var mDeviceData: String
    private lateinit var mIpAddress: String
    private var androidVersion: Int = 0
    private var dateTimeUtc: Long = 0
    private lateinit var username: String
    private lateinit var mPassword: String
    private lateinit var fullName: String
    private lateinit var connectionDetector: ConnectionDetectorUtil
    private var isPemeriksa = false
    private var isRegister = false
    private var isApproval = false
    private var role = 0
    private var isSyncDataInitialized: Boolean = false
    private var isToastShowed = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        daoSession = (requireActivity().application as MyApplication).daoSession!!
        databaseReport = DatabaseReport.getDatabase(requireContext())
        connectionDetector = ConnectionDetectorUtil(requireContext())
        role = SharedPrefsUtils.getIntegerPreference(requireContext(), Config.KEY_ROLE_ID, 0)
        val kdPabrikan =
            SharedPrefsUtils.getStringPreference(requireContext(), Config.KEY_KODE_USER, "")
                .toString()

        if (role == 1) {
            viewModel.getDashboard(
                requireContext(),
                kdPabrikan,
                "999", "1"
            )
        }

//        viewModel.getDataForRole(daoSession = daoSession)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.dashboard1))
        imageList.add(SlideModel(R.drawable.dashboard1))
        imageList.add(SlideModel(R.drawable.dashboard1))
        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)

        dialogLoading = Dialog(requireContext())
        dialogLoading.setContentView(R.layout.popup_loading)
        dialogLoading.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialogLoading.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialogLoading.setCancelable(false)
        dialogLoading.window!!.attributes.windowAnimations = R.style.DialogUpDown

        mAndroidId = Helper.getAndroidId(requireContext())
        mAppVersion = Helper.getVersionApp(requireContext())
        mDeviceData = Helper.deviceData
        mIpAddress = Helper.getIPAddress(true)
        androidVersion = Build.VERSION.SDK_INT
        dateTimeUtc = DateTimeUtils.currentTimeMillis()
        mPassword =
            SharedPrefsUtils.getStringPreference(requireContext(), Config.KEY_PASSWORD, "")!!
        fullName =
            SharedPrefsUtils.getStringPreference(requireContext(), Config.KEY_FULL_NAME, "")!!
        username =
            SharedPrefsUtils.getStringPreference(requireContext(), Config.KEY_USERNAME, "")!!
        binding.txtdash1.text = fullName

        if (!SharedPrefsUtils.getBooleanPreference(requireActivity(),"IS_GET_DATA_DONE",false))
            initSyncData() else viewModel.getDataForRole(daoSession = daoSession)

        setPrivileges()

        binding.btnMonitoringComplaint.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
            val view =
                layoutInflater.inflate(R.layout.bottom_sheet_dialog_monitoring_complaint, null)
            val btnAdminGudang = view.findViewById<CardView>(R.id.cv_admin_gudang)
            val btnPemeriksa = view.findViewById<CardView>(R.id.cv_pemeriksa)

            if (isPemeriksa) {
                btnPemeriksa.visibility = View.VISIBLE
                btnAdminGudang.visibility = View.GONE
            } else if (!isPemeriksa) {
                btnAdminGudang.visibility = View.VISIBLE
                btnPemeriksa.visibility = View.GONE
            } else {
                btnAdminGudang.visibility = View.GONE
                btnPemeriksa.visibility = View.GONE
            }

            btnAdminGudang.setOnClickListener {
                startActivity(Intent(requireContext(), MonitoringComplaintActivity::class.java))
            }

            btnPemeriksa.setOnClickListener {
                startActivity(Intent(requireContext(), MonitoringComplaintActivity::class.java))
            }

            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        }

        binding.btnMonitoringPermintaan.setOnClickListener {
            startActivity(Intent(requireContext(), MonitoringPermintaanActivity::class.java))
        }

        binding.btnPemakaian.setOnClickListener {
            startActivity(Intent(requireContext(), PemakaianActivity::class.java))
        }

        binding.btnPenerimaanUlp.setOnClickListener {
            startActivity(
                Intent(
                    requireContext(),
                    PenerimaanUlpActivity::class.java
                )
            )
        }

        binding.btnSync.setOnClickListener {
            binding.btnSync.isEnabled = false
            syncData()
        }

        binding.btnMonitoring.setOnClickListener {
            startActivity(Intent(requireContext(), MonitoringActivity::class.java))
        }

        binding.btnDataAttr.setOnClickListener {
            startActivity(Intent(requireContext(), DataAtributMaterialActivity::class.java))
        }

        binding.btnPengujian.setOnClickListener {
            startActivity(Intent(requireContext(), PengujianActivity::class.java))
        }

        binding.btnPenerimaan.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
            val btnPemeriksaan = view.findViewById<CardView>(R.id.cv_pemeriksaan)
            val btnPenerimaan = view.findViewById<CardView>(R.id.cv_penerimaan)

//            if (isPemeriksa) {
//                btnPemeriksaan.visibility = View.VISIBLE
//            } else {
//                btnPemeriksaan.visibility = View.GONE
//            }

            btnPenerimaan.setOnClickListener {
                startActivity(Intent(requireContext(), PenerimaanActivity::class.java))
            }

            btnPemeriksaan.setOnClickListener {
                startActivity(Intent(requireContext(), PemeriksaanActivity::class.java))
            }

            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        }

        binding.btnRegistrasi.setOnClickListener {
            val dialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheetDialogTheme)
            val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_registrasi, null)
            val btnRegister = view.findViewById(R.id.cv_registrasi) as CardView
            val btnAproval = view.findViewById(R.id.cv_approval) as CardView

            when {
                isApproval && !isRegister -> {
                    btnRegister.visibility = View.GONE
                    btnAproval.visibility = View.VISIBLE
                }

                !isApproval && isRegister -> {
                    btnRegister.visibility = View.VISIBLE
                    btnAproval.visibility = View.GONE
                }

                isApproval && isRegister -> {
                    btnRegister.visibility = View.VISIBLE
                    btnAproval.visibility = View.VISIBLE
                }

                else -> {
                    btnRegister.visibility = View.GONE
                    btnAproval.visibility = View.GONE
                }
            }

            btnRegister.setOnClickListener {
                startActivity(Intent(requireContext(), RegistrasiSnMaterialActivity::class.java))
            }

            btnAproval.setOnClickListener {
                startActivity(Intent(requireContext(), ApprovalActivity::class.java))
            }

            dialog.setCancelable(true)
            dialog.setContentView(view)
            dialog.show()
        }

        binding.btnTracking.setOnClickListener {
            startActivity(Intent(requireContext(), TrackingHistoryActivity::class.java))
        }

        binding.btnPengiriman.setOnClickListener {
            startActivity(Intent(requireContext(), PengirimanActivity::class.java))
        }

        binding.btnPengujiancoba.setOnClickListener {
            startActivity(Intent(requireContext(), MonitoringStokActivity::class.java))
        }

        binding.btnInspeksiMaterial.setOnClickListener {
            startActivity(Intent(requireContext(), InspeksiMaterialActivity::class.java))
        }

        viewModel.dashboardResponse.observe(viewLifecycleOwner) {
            if (it.dataMaterialAttribute != null) {
                binding.txtJumlahAttribut.text = it.dataMaterialAttribute.toString()
            } else {
                binding.txtJumlahAttribut.text = "0"
            }
        }

        //TODO Apakah menggunakan endpoint login atau buat endpoint baru untuk sync data?

        viewModel.syncDataResponse.observe(viewLifecycleOwner) { response ->
            when (response.status) {
                Config.KEY_SUCCESS -> {
//                    if (!response.hasData) {
                        viewModel.getDataForRole(daoSession = daoSession)
                        val penerimaan = daoSession.tPosPenerimaanDao.queryBuilder().list()
                        penerimaanViewModel.getPenerimaan(daoSession, penerimaan)
                        dialogLoading.dismiss()
                        if (!isToastShowed) {
                            Toast.makeText(
                                requireContext(),
                                "Berhasil mengambil data",
                                Toast.LENGTH_SHORT
                            ).show()
                            isToastShowed = true
                        }
//                    } else {
//                        viewModel.incrementBatch()
//                        viewModel.syncData(
//                            requireContext(),
//                            daoSession,
//                            username = username
//                        )
//                    }
                }

                else -> {
                    dialogLoading.dismiss()
                    Toast.makeText(requireContext(), "Gagal mengambil data", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            when (it) {
                true -> binding.pgLoading.visibility = View.VISIBLE
                false -> binding.pgLoading.visibility = View.GONE
            }
        }

        viewModel.isLoadingInitData.observe(viewLifecycleOwner) {
            if (!it) {
                binding.pgBar.visibility = View.GONE
            } else {
                binding.pgBar.visibility = View.VISIBLE
            }
        }

        viewModel.errorResponse.observe(viewLifecycleOwner) {
            if (it != 200) dialogLoading.dismiss()
        }
    }

    private fun setPrivileges() {
        val listPrivilege = daoSession.tPrivilegeDao.queryBuilder().list()
        Log.d("HomeFragment", "cek setPrivileges: i.methodId = $listPrivilege")

        for (i in listPrivilege) {

            when (i.methodId) {
                "is_monitoring" -> {
                    binding.btnMonitoring.visibility = View.VISIBLE
                }

                "is_pengujian" -> {
                    binding.btnPengujian.visibility = View.VISIBLE
                    binding.btnPengujiancoba.visibility = View.GONE
                }

                "is_pengiriman" -> {
                    binding.btnPengiriman.visibility = View.VISIBLE
                }

                "is_data_atribut" -> {
                    binding.btnDataAttr.visibility = View.VISIBLE
                    binding.btnPengujiancoba.visibility = View.GONE
                    binding.btnInspeksiMaterial.visibility = View.GONE

                }

                "is_tracking" -> {
                    binding.btnTracking.visibility = View.VISIBLE
                }

                "is_penerimaan" -> {
                    binding.btnPenerimaan.visibility = View.VISIBLE
                    binding.btnInspeksiMaterial.visibility = View.VISIBLE
                }

                "is_pemakaian" -> {
                    binding.btnPemakaian.visibility = View.VISIBLE
                }

                "is_penerimaan_ulp" -> {
                    binding.btnPenerimaanUlp.visibility = View.VISIBLE
                    binding.btnInspeksiMaterial.visibility = View.GONE
                }

                "is_monitoring_permintaan" -> {
                    binding.btnMonitoringPermintaan.visibility = View.VISIBLE
                }

                "is_registrasi" -> {
                    binding.btnRegistrasi.visibility = View.VISIBLE
                }

                "is_monitoring_komplain" -> {
                    binding.btnMonitoringComplaint.visibility = View.VISIBLE
                }

                "is_pemeriksa" -> {
                    isPemeriksa = true
                    binding.btnInspeksiMaterial.visibility = View.GONE
                    binding.btnPengujiancoba.visibility = View.GONE
                }

                "is_register" -> {
                    isRegister = true
                }

                "is_approval" -> {
                    isApproval = true
                }
                "is_super_admin" -> {
                  binding.btnTracking.visibility = View.VISIBLE
                  binding.btnInspeksiMaterial.visibility = View.VISIBLE
                    binding.btnPengujiancoba.visibility = View.GONE
                }
            }
        }

        viewModel.nilaiMonitoring.observe(viewLifecycleOwner) {
            binding.txtJumlahMonitoring.text = it
        }
        viewModel.nilaiPemakaian.observe(viewLifecycleOwner) {
            binding.txtJumlahPemakaian.text = it
        }
        viewModel.nilaiPermintaan.observe(viewLifecycleOwner) {
            binding.txtJumlahMonitoringPermintaan.text = it
        }
        viewModel.nilaiPenerimaanUlp.observe(viewLifecycleOwner) {
            binding.txtJumlahPenerimaanUlp.text = it
        }
        viewModel.nilaiPengiriman.observe(viewLifecycleOwner) {
            binding.txtJumlahPengiriman.text = it
        }
        viewModel.nilaiPengujian.observe(viewLifecycleOwner) {
            binding.txtJumlahPengujian.text = it
        }
        viewModel.nilaiPenerimaanUp3.observe(viewLifecycleOwner) {
            binding.txtJumlahPenerimaanUp3.text = it
        }
        viewModel.nilaiMonitoringComplaint.observe(viewLifecycleOwner) {
            binding.txtJumlahMonitoringComplaint.text = it
        }
    }

    private fun syncData() {
        if (!connectionDetector.isConnectingToInternet) {
            Toast.makeText(
                requireContext(),
                getString(R.string.tidak_terhubung_internet),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (databaseReport?.isTransmitionNotSendExist()!!) {
            Toast.makeText(
                requireContext(),
                getString(R.string.masih_ada_data_yang_tidak_terikirim),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.popup_validation)
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown
        val btnYa = dialog.findViewById(R.id.btn_ya) as AppCompatButton
        val btnTidak = dialog.findViewById(R.id.btn_tidak) as AppCompatButton
        val message = dialog.findViewById(R.id.message) as TextView
        val textMessage = dialog.findViewById(R.id.txt_message) as TextView

        message.text = getString(R.string.melakukan_sync_data)
        textMessage.text = getString(R.string.melakukan_sync_data_content)

        btnTidak.setOnClickListener {
            dialog.dismiss()
            binding.btnSync.isEnabled = true
        }

        btnYa.setOnClickListener {
            viewModel.syncData(
                requireContext(),
                daoSession,
                username = username
            )
            dialog.dismiss()
            dialogLoading.show()
            binding.btnSync.isEnabled = true
        }

        dialog.show()
    }

    private fun initSyncData() {
        dialogLoading.show()
        if (!connectionDetector.isConnectingToInternet) {
            Toast.makeText(
                requireContext(),
                getString(R.string.tidak_terhubung_internet),
                Toast.LENGTH_SHORT
            ).show()
            dialogLoading.dismiss()
            return
        }

        Toast.makeText(
            requireContext(),
            "Sedang melakukan inisiasi pengambilan data",
            Toast.LENGTH_LONG
        ).show()

        viewModel.syncUserData(
            requireContext(),
            daoSession,
            username = username
        )

        viewModel.deleteAllDbLocal(daoSession)
        viewModel.syncData(
            requireContext(),
            daoSession,
            username = username
        )
        binding.btnSync.isEnabled = true
    }
}