package dev.iconpln.mims.ui.monitoring.monitoring_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.databinding.ItemDataDetailPurchaseOrderBinding

class MonitoringDetailAdapter(
    val listModels: MutableList<dev.iconpln.mims.data.local.database.TPosDetail>,
    var listener: OnAdapterListener
) : RecyclerView.Adapter<MonitoringDetailAdapter.ViewHolder>() {

    fun setPoList(datas: List<dev.iconpln.mims.data.local.database.TPosDetail>) {
        listModels.clear()
        listModels.addAll(datas)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MonitoringDetailAdapter.ViewHolder {
        val binding = ItemDataDetailPurchaseOrderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonitoringDetailAdapter.ViewHolder, position: Int) {
        holder.bind(listModels[position])
    }

    override fun getItemCount(): Int = listModels.size

    inner class ViewHolder(val binding: ItemDataDetailPurchaseOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: dev.iconpln.mims.data.local.database.TPosDetail) {
            with(binding) {
                txtKuantitas.text = data.qty
                txtUnit.text = data.plantName
                txtDeliveryDate.text = data.createdDate
                txtLeadTime.text = "${data.leadTime} Hari"
                txtNoMaterial.text = data.noMatSap
                txtSatuan.text = data.uom
                txtNoPackaging.text = data.noPackaging
            }

            itemView.setOnClickListener { listener.onClick(data) }
        }
    }

    interface OnAdapterListener {
        fun onClick(data: dev.iconpln.mims.data.local.database.TPosDetail)
    }
}