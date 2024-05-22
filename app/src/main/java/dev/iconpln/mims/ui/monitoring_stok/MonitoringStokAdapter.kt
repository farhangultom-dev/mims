package dev.iconpln.mims.ui.monitoring_stok

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.data.remote.response.DataMonStok
import dev.iconpln.mims.databinding.ItemDataMonitoringStokBinding

class MonitoringStokAdapter : RecyclerView.Adapter<MonitoringStokAdapter.ListViewHolder>() {

    private val listDataMonitoringStok = ArrayList<DataMonStok>()

    class ListViewHolder(private val binding: ItemDataMonitoringStokBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataMonStok) {
            with(binding) {
                txtNoMaterial.text = item.nomorMaterial
                txtValuationType.text = item.valuationType
                txtSatuan.text = item.unit
                txtStokSiap.text = item.stockSiap
                txtSiapKirim.text = item.siapKirim
                txtSiapPakai.text = item.siapDipakai
                txtKirimKeUlp.text = item.inTransitUlp
                txtKirimKeUp3.text = item.inTransitUp3
                txtTerpakai.text = item.dipakai
            }
        }

    }

    fun setListMonstok(listMonstok: List<DataMonStok>) {
        this.listDataMonitoringStok.clear()
        this.listDataMonitoringStok.addAll(listMonstok)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MonitoringStokAdapter.ListViewHolder {
        val binding = ItemDataMonitoringStokBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonitoringStokAdapter.ListViewHolder, position: Int) {
        holder.bind(listDataMonitoringStok[position])
    }

    override fun getItemCount(): Int {
        return listDataMonitoringStok.size
    }
}