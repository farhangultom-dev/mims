package dev.iconpln.mims.ui.monitoring_permintaan.input_sn_monitoring

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.data.local.database.TMonitoringSnMaterial
import dev.iconpln.mims.databinding.ItemSnMonitoringBinding

class MonitoringPermintaanSnAdapter(
    val lisModels: MutableList<dev.iconpln.mims.data.local.database.TMonitoringSnMaterial>,
    var listener: OnAdapterListener,
    var kodePengeluaran: String
) : RecyclerView.Adapter<MonitoringPermintaanSnAdapter.ViewHolder>() {

    fun setTmsList(tms: List<TMonitoringSnMaterial>) {
        lisModels.clear()
        lisModels.addAll(tms)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MonitoringPermintaanSnAdapter.ViewHolder {
        val binding =
            ItemSnMonitoringBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonitoringPermintaanSnAdapter.ViewHolder, position: Int) {
        holder.bind(lisModels[position])
    }

    override fun getItemCount(): Int = lisModels.size

    inner class ViewHolder(val binding: ItemSnMonitoringBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tms: TMonitoringSnMaterial) {
            with(binding) {
                txtSnMaterial.text = tms.serialNumber
                txtNoMeter.text = tms.noIdMeter
                Log.d("MonitoringPermintaanSnAdapter", "bind: ${tms.noIdMeter}")
                btnDelete.setOnClickListener { listener.onClick(tms) }

                if (kodePengeluaran == "2") {
                    btnDelete.visibility = View.GONE
                } else {
                    btnDelete.visibility = View.VISIBLE
                }
            }
        }
    }

    interface OnAdapterListener {
        fun onClick(tms: dev.iconpln.mims.data.local.database.TMonitoringSnMaterial)
    }
}