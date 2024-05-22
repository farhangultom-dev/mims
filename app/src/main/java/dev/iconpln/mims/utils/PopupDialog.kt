package dev.iconpln.mims.utils

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dev.iconpln.mims.R
import dev.iconpln.mims.databinding.ActivityPopdialogBinding
import dev.iconpln.mims.ui.tracking.DataMaterialTrackingActivity
import dev.iconpln.mims.ui.tracking.SpecMaterialActivity
import dev.iconpln.mims.ui.tracking.TrackingHistoryViewModel

class PopupDialog : BottomSheetDialogFragment() {

    private var _binding: ActivityPopdialogBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TrackingHistoryViewModel by viewModels()
    var sn = ""
    private lateinit var dialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityPopdialogBinding.inflate(inflater, container, false)
        val view = binding.root
        dialog = Helper.loadingDialog(requireActivity())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.trackingResponse.observe(this) {
            binding.apply {
                if (it.datas.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(requireContext(), SpecMaterialActivity::class.java)
                    intent.putExtra(DataMaterialTrackingActivity.EXTRA_SN, sn)
                    startActivity(intent)
                }
            }
        }

        viewModel.isLoading.observe(requireActivity()) {
            when (it) {
                true -> dialog.show()
                false -> dialog.dismiss()
            }
        }

        binding.btnOk.setOnClickListener {
            if (binding.inptSnMaterial.text.toString().equals("")) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.harap_inputkan_serial_number),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                sn = binding.inptSnMaterial.text.toString()
                viewModel.getTrackingHistory(sn, requireContext())
            }

        }
    }
}