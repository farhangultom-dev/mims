package dev.iconpln.mims.ui.registrasi_approval.registrasi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.data.remote.response.getSnMaterial
import dev.iconpln.mims.databinding.ItemRegisSnMaterialBinding

class ListRegisSnMaterialAdapter :
    RecyclerView.Adapter<ListRegisSnMaterialAdapter.ListViewHolder>() {
    private val listRegisSn = ArrayList<getSnMaterial>()

    fun setListRegisSn(listDoa: List<getSnMaterial>) {
        this.listRegisSn.clear()
        this.listRegisSn.addAll(listDoa)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemRegisSnMaterialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listRegisSn.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listRegisSn[position])
    }

    inner class ListViewHolder(private val binding: ItemRegisSnMaterialBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: getSnMaterial) {
            with(binding) {
                txtNoRegistrasi.text = item.noRegistrasi
                if (item.flagPrint) {
                    lblTglRegistrasi.text = "Tanggal Approval"
                } else {
                    lblTglRegistrasi.text = "Tanggal Registrasi"
                }

            }
        }
    }
}