package dev.iconpln.mims.ui.attribute_material

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.data.remote.response.MaterialDatasItem
import dev.iconpln.mims.databinding.ItemDataMaterialPabrikanBinding

class DataAttributeAdapter(
    val listModels: MutableList<MaterialDatasItem>,
    var listener: OnAdapterListener
) : RecyclerView.Adapter<DataAttributeAdapter.ViewHolder>() {

    fun setMaterialList(datas: List<MaterialDatasItem>) {
        listModels.clear()
        listModels.addAll(datas)
        notifyDataSetChanged()
    }

    fun updateMaterialList(newDatas: List<MaterialDatasItem>) {
        listModels.addAll(newDatas)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemDataMaterialPabrikanBinding.inflate(
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

    inner class ViewHolder(val binding: ItemDataMaterialPabrikanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(datas: MaterialDatasItem) {
            with(binding) {
                txtSerialNumber.text = datas.noProduksi
                txtNoMaterial.text = datas.nomorMaterial
                txtNoKategori.text = datas.namaKategoriMaterial
                txtNoTahun.text = datas.tahun.toString()
                txtSpesifikasi.text = datas.materialDesc
            }

            itemView.setOnClickListener { listener.onClick(datas) }
        }
    }

    interface OnAdapterListener {
        fun onClick(po: MaterialDatasItem)
    }
}