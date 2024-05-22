package dev.iconpln.mims.ui.attribute_material.detail_attribute

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.data.remote.response.MaterialDetailDatasItem
import dev.iconpln.mims.databinding.ItemDataDetailMaterialPabrikanBinding

class DetailDataAttributeAdapter(
    val listModels: MutableList<MaterialDetailDatasItem>,
    var listener: OnAdapterListener
) : RecyclerView.Adapter<DetailDataAttributeAdapter.ViewHolder>() {

    fun setMaterialList(datas: List<MaterialDetailDatasItem>) {
        listModels.clear()
        listModels.addAll(datas)
        notifyDataSetChanged()
    }

    fun updateMaterialList(newDatas: List<MaterialDetailDatasItem>) {
        listModels.addAll(newDatas)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemDataDetailMaterialPabrikanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listModels[position])
    }

    override fun getItemCount(): Int = listModels.size

    inner class ViewHolder(val binding: ItemDataDetailMaterialPabrikanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(datas: MaterialDetailDatasItem) {
            with(binding) {
                txtSerialNumber.text =
                    if (datas.serialNumber.isNullOrEmpty()) "-" else datas.serialNumber
                txtNoMaterial.text =
                    if (datas.nomorMaterial.isNullOrEmpty()) "-" else datas.nomorMaterial
                txtGaransi.text =
                    if (datas.masaGaransi == null) "-" else "${datas.masaGaransi} (${datas.statusGaransi})"
                txtKategori.text =
                    if (datas.namaKategoriMaterial.isNullOrEmpty()) "-" else datas.namaKategoriMaterial
                txtMetrologi.text =
                    if (datas.nomorSertMaterologi.isNullOrEmpty()) "-" else datas.nomorSertMaterologi
                txtSpesifikasi.text =
                    if (datas.spesifikasi.isNullOrEmpty()) "-" else datas.spesifikasi
                txtNoIdMeter.text =
                    if (datas.noIdMeter.isNullOrEmpty()) "-" else datas.noIdMeter
                txtSpln.text = if (datas.spln.isNullOrEmpty()) "-" else datas.spln
                txtTglProduksi.text =
                    if (datas.tglProduksi.isNullOrEmpty()) "-" else datas.tglProduksi
                txtNoBatch.text = if (datas.noProduksi.isNullOrEmpty()) "-" else datas.noProduksi
            }

            itemView.setOnClickListener { listener.onClick(datas) }
        }
    }

    interface OnAdapterListener {
        fun onClick(po: MaterialDetailDatasItem)
    }
}