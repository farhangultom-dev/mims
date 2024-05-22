package dev.iconpln.mims.ui.penerimaan.detail_penerimaan_akhir

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.databinding.ItemSnMaterialBinding

class DetailPenerimaanAkhirAdapter(
    val lisModels: MutableList<dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhir>,
    var listener: OnAdapterListener, var daoSession: dev.iconpln.mims.data.local.database.DaoSession
) : RecyclerView.Adapter<DetailPenerimaanAkhirAdapter.ViewHolder>() {

    fun setData(po: List<dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhir>) {
        lisModels.clear()
        lisModels.addAll(po)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemSnMaterialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lisModels[position])
    }

    override fun getItemCount(): Int = lisModels.size

    inner class ViewHolder(val binding: ItemSnMaterialBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tpd: dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhir) {

            with(binding) {
                txtSnMaterial.text = "${tpd.serialNumber}"
                txtKategori.text = tpd.namaKategoriMaterial
                txtPackaging.text = tpd.noPackaging

                if (tpd.status == "SESUAI") {
                    cbSesuai.isChecked = true
                    cbTidakSesuai.isChecked = false
                } else {
                    cbSesuai.isChecked = false
                    cbTidakSesuai.isChecked = true
                }

                cbSesuai.isEnabled = false
                cbTidakSesuai.isEnabled = false
            }
            itemView.setOnClickListener { listener.onClick(tpd) }
        }
    }

    interface OnAdapterListener {
        fun onClick(po: dev.iconpln.mims.data.local.database.TPosDetailPenerimaanAkhir)
    }
}