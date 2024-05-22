package dev.iconpln.mims.ui.pemakaian.maximo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.data.remote.response.getMaterialPemakaian
import dev.iconpln.mims.databinding.ItemSnMonitoringBinding

class InputSnMaterialMaximoAdapter :
    RecyclerView.Adapter<InputSnMaterialMaximoAdapter.ListViewHolder>() {
    private val listInputSnMaximo = ArrayList<getMaterialPemakaian>()

    inner class ListViewHolder(private val binding: ItemSnMonitoringBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: getMaterialPemakaian) {
            with(binding) {
                txtSnMaterial.text = item.serialNumber
//                txtNoMeter.text = item.noIdMeter
            }
        }
    }

    fun setListGetSnPemakaianMaximo(listDataSnMaterialMaximo: List<getMaterialPemakaian>) {
        this.listInputSnMaximo.clear()
        this.listInputSnMaximo.addAll(listDataSnMaterialMaximo)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): InputSnMaterialMaximoAdapter.ListViewHolder {
        return ListViewHolder(
            ItemSnMonitoringBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: InputSnMaterialMaximoAdapter.ListViewHolder,
        position: Int
    ) {
        holder.bind(listInputSnMaximo[position])
    }

    override fun getItemCount() = listInputSnMaximo.size


}