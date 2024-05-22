package dev.iconpln.mims.ui.ulp.penerimaan.input_penerimaan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.databinding.ItemSnPenerimaanUlpBinding

class PenerimaanUlpSnAdapter(
    val lisModels: MutableList<dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlp>,
    var listener: OnAdapterListener
) : RecyclerView.Adapter<PenerimaanUlpSnAdapter.ViewHolder>() {

    fun setTmsList(tms: List<dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlp>) {
        lisModels.clear()
        lisModels.addAll(tms)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PenerimaanUlpSnAdapter.ViewHolder {
        val binding =
            ItemSnPenerimaanUlpBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PenerimaanUlpSnAdapter.ViewHolder, position: Int) {
        holder.bind(lisModels[position])
    }

    override fun getItemCount(): Int = lisModels.size

    inner class ViewHolder(val binding: ItemSnPenerimaanUlpBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tms: dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlp) {
            with(binding) {
                txtSnMaterial.text = tms.noSerialNumber

            }
        }
    }

    interface OnAdapterListener {
        fun onClick(tms: dev.iconpln.mims.data.local.database.TListSnMaterialPenerimaanUlp)
    }
}