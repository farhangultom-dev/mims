package dev.iconpln.mims.ui.pemakaian.ap2t

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.data.remote.response.getMaterialPemakaian
import dev.iconpln.mims.databinding.ItemDetailMaterialDetailAp2tBinding

class DataDetailMaterialAp2tAdapter :
    RecyclerView.Adapter<DataDetailMaterialAp2tAdapter.ListViewHolder>() {

    private val listDataDetailMaterialAp2t = ArrayList<getMaterialPemakaian>()
    private var noMaterial = ArrayList<String>()

    fun getNoMaterial(noMaterial: ArrayList<String>) {
        this.noMaterial = noMaterial
    }

    class ListViewHolder(private val binding: ItemDetailMaterialDetailAp2tBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: getMaterialPemakaian) {
            with(binding) {
                txtSnMaterial.text = item.serialNumber
                txtNoMeter.text = item.noIdMeter
            }
        }
    }

    fun setListDataDetailMaterialAp2t(listDetailDataMaterialAp2t: List<getMaterialPemakaian>) {
        this.listDataDetailMaterialAp2t.clear()
        this.listDataDetailMaterialAp2t.addAll(listDetailDataMaterialAp2t)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            ItemDetailMaterialDetailAp2tBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount() = listDataDetailMaterialAp2t.size


    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listDataDetailMaterialAp2t[position])
    }

}