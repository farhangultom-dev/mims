package dev.iconpln.mims.ui.inspeksi_material.tim_inspeksi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.data.remote.response.AGODetailInspectionTeamData
import dev.iconpln.mims.databinding.ItemTimInspeksiBinding

class TimInspeksiAdapter : RecyclerView.Adapter<TimInspeksiAdapter.ListViewHolder>() {
    private val listTimInspeksi = ArrayList<AGODetailInspectionTeamData>()
    class ListViewHolder(private val binding: ItemTimInspeksiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AGODetailInspectionTeamData) {
            with(binding) {
                txtNamaAnggotaTimInspeksi.text = item.nama
                txtJabatan.text = item.jabatan
                txtEmail.text = item.email
                txtNip.text = item.nip
                btnDeleteAnggota.setOnClickListener {
                    var position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        (binding.root.context as TimInspeksiActivity).deleteInspectionTeam(position)
                    }
                }
            }
        }
    }

    fun setListTimInspeksi(dataTimInspeksi: List<AGODetailInspectionTeamData?>) {
        listTimInspeksi.clear()
        listTimInspeksi.addAll(dataTimInspeksi.filterNotNull())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding: ItemTimInspeksiBinding = ItemTimInspeksiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listTimInspeksi.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listTimInspeksi[position])
    }

}