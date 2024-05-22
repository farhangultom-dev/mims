package dev.iconpln.mims.ui.inspeksi_material

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.data.remote.response.AGODetailInspectionMaterialReturnData
import dev.iconpln.mims.databinding.ItemInspeksiMaterialBinding
import dev.iconpln.mims.ui.inspeksi_material.tim_inspeksi.TimInspeksiActivity

class InspeksiMaterialAdapter : RecyclerView.Adapter<InspeksiMaterialAdapter.ListViewHolder>(){
    private val listInspeksi = ArrayList<AGODetailInspectionMaterialReturnData>()
    class ListViewHolder(private val binding: ItemInspeksiMaterialBinding): RecyclerView.ViewHolder(binding.root)
    {
        fun bind(item: AGODetailInspectionMaterialReturnData) {
            with(binding){
                txtNoPo.text = item.noInspeksiRetur
                txtNamaMaterial.text = item.namaMaterial
                txtTanggalInspeksi.text = item.tglInspeksiRetur
                txtKuantitas.text = item.qtyInspeksiRetur
                txtStatusInspeksi.text = item.statusInspeksi
                btnAddAnggota.setOnClickListener {
                    val intent = Intent(itemView.context, TimInspeksiActivity::class.java)

                    intent.putExtra(
                        TimInspeksiActivity.EXTRA_NO_INSPEKSI,
                        item.noInspeksiRetur
                    )

                    startActivity(itemView.context, intent, null)
                }
            }
        }

    }


    fun setListinspeksi(dataInspeksi: List<AGODetailInspectionMaterialReturnData?>) {
        listInspeksi.clear()
        for (item in dataInspeksi) {
            if (item != null) {
                listInspeksi.add(item)
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding: ItemInspeksiMaterialBinding = ItemInspeksiMaterialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listInspeksi.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listInspeksi[position])
    }
}