package dev.iconpln.mims.ui.monitoring

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.data.local.database.TPos
import dev.iconpln.mims.databinding.ItemDataMonitoringPurchaseBinding

class MonitoringAdapter(
    val listModels: MutableList<dev.iconpln.mims.data.local.database.TPos>,
    var listener: OnAdapterListener
) :
    RecyclerView.Adapter<MonitoringAdapter.ViewHolder>() {

    fun setPoList(datas: List<dev.iconpln.mims.data.local.database.TPos>) {
        listModels.clear()
        listModels.addAll(datas)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MonitoringAdapter.ViewHolder {
        val binding = ItemDataMonitoringPurchaseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonitoringAdapter.ViewHolder, position: Int) {
        holder.bind(listModels[position])
    }

    override fun getItemCount(): Int = listModels.size

    inner class ViewHolder(val binding: ItemDataMonitoringPurchaseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TPos) {
            with(binding) {
                txtNoDoNew.text = data.noDoSmar
                txtNoPo.text = data.poMpNo
                txtNoTlsk.text = data.tlskNo
                txtKuantitas.text = data.total
                txtDeliveryDate.text = data.poDate
            }

            itemView.setOnClickListener { listener.onClick(data) }
        }
    }

    interface OnAdapterListener {
        fun onClick(data: dev.iconpln.mims.data.local.database.TPos)
    }
}