package dev.iconpln.mims.ui.pemakaian.ap2t

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.data.remote.response.DataItemAp2t
import dev.iconpln.mims.databinding.ItemSnMonitoringAp2tBinding

class InputSnMaterialAp2tAdapter :
    RecyclerView.Adapter<InputSnMaterialAp2tAdapter.ListViewHolder>() {

    private val listInputSnAp2t = ArrayList<DataItemAp2t>()
    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setListSnPemakaianAp2t(listDataSnMaterialAp2t: List<DataItemAp2t>) {
        this.listInputSnAp2t.clear()
        this.listInputSnAp2t.addAll(listDataSnMaterialAp2t)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InputSnMaterialAp2tAdapter.ListViewHolder {
        return ListViewHolder(
            ItemSnMonitoringAp2tBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: InputSnMaterialAp2tAdapter.ListViewHolder,
        position: Int
    ) {
        holder.bind(listInputSnAp2t[position])
    }

    override fun getItemCount() = listInputSnAp2t.size

    inner class ListViewHolder(private val binding: ItemSnMonitoringAp2tBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataItemAp2t) {
            with(binding) {
                txtSnMaterial.text = item.serialNumber
                txtNoMeter.text = item.noIdMeter
                txtNoMaterial.text = item.nomorMaterial
                txtQty.text = item.qty
                txtSatuan.text = item.unit

                binding.btnDelete.setOnClickListener {
                    onItemClickCallback?.onItemClicked(item)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: DataItemAp2t)
    }

}

