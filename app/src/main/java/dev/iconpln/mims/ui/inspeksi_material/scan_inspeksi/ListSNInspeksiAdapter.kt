package dev.iconpln.mims.ui.inspeksi_material.scan_inspeksi

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.databinding.ItemInspeksiScanBinding
import dev.iconpln.mims.ui.inspeksi_material.detail_inspeksi.DetailInspeksiMaterialActivity
import dev.iconpln.mims.ui.inspeksi_material.tim_inspeksi.TimInspeksiActivity

class ListSNInspeksiAdapter(
    val listInspeksi: MutableList<dev.iconpln.mims.data.local.database.TAGOMaterialData>
) : RecyclerView.Adapter<ListSNInspeksiAdapter.ListViewHolder>() {
    class ListViewHolder(private val binding: ItemInspeksiScanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: dev.iconpln.mims.data.local.database.TAGOMaterialData) {
            with(binding) {
                txtSerialNumber.text = item.snMaterial
                btnEditTeam.setOnClickListener {
                    val intent = Intent(itemView.context, TimInspeksiActivity::class.java)

                    intent.putExtra(
                        TimInspeksiActivity.EXTRA_NO_INSPEKSI,
                        item.inspectionNumber
                    )

                    startActivity(itemView.context, intent, null)
                }

                cvDaftarList.setOnClickListener {
                    val intentDetailInspeksi = Intent(itemView.context, DetailInspeksiMaterialActivity::class.java)

                    intentDetailInspeksi.putExtra(
                        DetailInspeksiMaterialActivity.EXTRA_SERIAL_NUMBER,
                        item.snMaterial
                    )

                    startActivity(itemView.context, intentDetailInspeksi, null)
                }
            }
        }
    }

    fun setListInspeksi(dataInspeksi: List<dev.iconpln.mims.data.local.database.TAGOMaterialData>) {
        listInspeksi.clear()
        listInspeksi.addAll(dataInspeksi)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding: ItemInspeksiScanBinding = ItemInspeksiScanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listInspeksi.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listInspeksi[position])
    }
}