package dev.iconpln.mims.ui.profile

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dev.iconpln.mims.R
import dev.iconpln.mims.databinding.FragmentProfileBinding
import dev.iconpln.mims.ui.auth.AuthViewModel
import dev.iconpln.mims.ui.auth.change_password.ChangePasswordActivity
import dev.iconpln.mims.ui.transmission_history.TransmissionActivity
import dev.iconpln.mims.utils.Config
import dev.iconpln.mims.utils.Helper
import dev.iconpln.mims.utils.SharedPrefsUtils

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val viewModel: AuthViewModel by viewModels()
    private lateinit var dialogLoading: Dialog
    private val binding get() = _binding!!
    private var isLoginSso = 0
    private var isLoginBiometric = 0
    private var fullName = ""
    private var roleName = ""
    private var plantName = ""
    private var username = ""
    private var tokenJwt = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogLoading = Helper.loadingDialog(requireActivity())

        isLoginSso =
            SharedPrefsUtils.getIntegerPreference(requireActivity(), Config.IS_LOGIN_SSO, 0)
        fullName =
            SharedPrefsUtils.getStringPreference(requireActivity(), Config.KEY_FULL_NAME, "")!!
        roleName =
            SharedPrefsUtils.getStringPreference(requireActivity(), Config.KEY_FULL_NAME, "")!!
        isLoginBiometric = SharedPrefsUtils.getIntegerPreference(
            requireActivity(),
            Config.KEY_IS_LOGIN_BIOMETRIC,
            0
        )
        plantName =
            SharedPrefsUtils.getStringPreference(requireActivity(), Config.KEY_PLANT_NAME, "")!!
        username =
            SharedPrefsUtils.getStringPreference(requireActivity(), Config.KEY_USERNAME, "")!!
        tokenJwt = SharedPrefsUtils.getStringPreference(requireActivity(), Config.KEY_JWT, "")!!

        viewModel.isLoading.observe(requireActivity()) {
            when (it) {
                true -> dialogLoading.show()
                false -> dialogLoading.dismiss()
            }
        }

        if (Helper.hasFingerprintSensor(requireActivity()))
            binding.cvBiometric.visibility = View.VISIBLE
        else
            binding.cvBiometric.visibility = View.GONE

        binding.txtName.text = "$fullName ($roleName)"
        binding.txtNameCabang.text = plantName

        when (isLoginBiometric) {
            1 -> binding.btnSwitch.isChecked = true
            else -> binding.btnSwitch.isChecked = false
        }

        binding.cvTransmisson.setOnClickListener {
            startActivity(Intent(requireActivity(), TransmissionActivity::class.java))
        }

        binding.cvUbahPassword.setOnClickListener {
            startActivity(Intent(requireActivity(), ChangePasswordActivity::class.java))
        }

        binding.btnSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> {
                    SharedPrefsUtils.setIntegerPreference(
                        requireActivity(),
                        Config.KEY_IS_LOGIN_BIOMETRIC,
                        1
                    )
                }

                false -> {
                    SharedPrefsUtils.setIntegerPreference(
                        requireActivity(),
                        Config.KEY_IS_LOGIN_BIOMETRIC,
                        0
                    )
                }
            }
        }

        binding.cvLogout.setOnClickListener {
            val dialog = Dialog(requireActivity())
            dialog.setContentView(R.layout.popup_validation)
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.setCancelable(false)
            dialog.window!!.attributes.windowAnimations = R.style.DialogUpDown
            val ivLogout = dialog.findViewById(R.id.imageView11) as ImageView
            val message = dialog.findViewById(R.id.message) as TextView
            val txtMessage = dialog.findViewById(R.id.txt_message) as TextView
            val btnYa = dialog.findViewById(R.id.btn_ya) as AppCompatButton
            val btnTidak = dialog.findViewById(R.id.btn_tidak) as AppCompatButton

            ivLogout.setImageResource(R.drawable.ic_warning)
            message.text = "Logout"
            txtMessage.text = "Apakah kamu yakin untuk logout?"

            btnTidak.setOnClickListener {
                dialog.dismiss()
            }

            btnYa.setOnClickListener {
                viewModel.logout(requireActivity(), tokenJwt, isLoginSso, isLoginBiometric)
                dialog.dismiss()
            }

            dialog.show()
        }
    }
}