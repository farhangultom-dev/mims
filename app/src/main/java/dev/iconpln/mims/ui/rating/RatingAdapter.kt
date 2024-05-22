package dev.iconpln.mims.ui.rating

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.databinding.ItemPackagingBinding

class RatingAdapter(
    val lisModels: MutableList<dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhir>,
    var listener: OnAdapterListener
) : RecyclerView.Adapter<RatingAdapter.ViewHolder>() {

    fun setPedList(ped: List<dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhir>) {
        lisModels.clear()
        lisModels.addAll(ped)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemPackagingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lisModels[position])
    }

    override fun getItemCount(): Int = lisModels.size

    inner class ViewHolder(val binding: ItemPackagingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(po: dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhir) {
            with(binding) {
                txtNoPackaging.text = po.noPackaging
            }

            itemView.setOnClickListener { listener.onClick(po) }
        }
    }

    interface OnAdapterListener {
        fun onClick(po: dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhir)
    }
}