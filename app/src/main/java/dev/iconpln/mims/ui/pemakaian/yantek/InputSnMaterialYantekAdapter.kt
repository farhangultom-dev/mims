package dev.iconpln.mims.ui.pemakaian.yantek

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.data.remote.response.getMaterialPemakaian
import dev.iconpln.mims.databinding.ItemSnMonitoringBinding

class InputSnMaterialYantekAdapter :
    RecyclerView.Adapter<InputSnMaterialYantekAdapter.ListViewHolder>() {

    private val listInputSnYantek = ArrayList<getMaterialPemakaian>()

    inner class ListViewHolder(private val binding: ItemSnMonitoringBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: getMaterialPemakaian) {
            with(binding) {
                txtSnMaterial.text = item.serialNumber
//                txtNoMeter.text = item.noIdMeter
            }
        }
    }

    fun setListGetSnPemakaianYantek(listDataSnMaterialYantek: List<getMaterialPemakaian>) {
        this.listInputSnYantek.clear()
        this.listInputSnYantek.addAll(listDataSnMaterialYantek)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InputSnMaterialYantekAdapter.ListViewHolder {
        return ListViewHolder(
            ItemSnMonitoringBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(
        holder: InputSnMaterialYantekAdapter.ListViewHolder,
        position: Int
    ) {
        holder.bind(listInputSnYantek[position])
    }

    override fun getItemCount() = listInputSnYantek.size
}