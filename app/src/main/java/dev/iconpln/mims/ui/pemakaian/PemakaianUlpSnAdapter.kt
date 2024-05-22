package dev.iconpln.mims.ui.pemakaian

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.databinding.ItemSnMonitoringBinding

class PemakaianUlpSnAdapter(
    val lisModels: MutableList<dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlp>,
    var listener: OnAdapterListener
) : RecyclerView.Adapter<PemakaianUlpSnAdapter.ViewHolder>() {

    fun setTmsList(tms: List<dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlp>) {
        lisModels.clear()
        lisModels.addAll(tms)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PemakaianUlpSnAdapter.ViewHolder {
        val binding =
            ItemSnMonitoringBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PemakaianUlpSnAdapter.ViewHolder, position: Int) {
        holder.bind(lisModels[position])
    }

    override fun getItemCount(): Int = lisModels.size

    inner class ViewHolder(val binding: ItemSnMonitoringBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tms: dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlp) {
            with(binding) {
                txtSnMaterial.text = tms.noSerialNumber
                btnDelete.setOnClickListener { listener.onClick(tms) }

            }
        }
    }

    interface OnAdapterListener {
        fun onClick(tms: dev.iconpln.mims.data.local.database.TListSnMaterialPemakaianUlp)
    }
}