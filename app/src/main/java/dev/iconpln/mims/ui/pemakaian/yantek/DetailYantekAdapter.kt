package dev.iconpln.mims.ui.pemakaian.yantek

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.data.remote.response.DataMaterialAp2t
import dev.iconpln.mims.databinding.ItemDetailYantekBinding

class DetailYantekAdapter : RecyclerView.Adapter<DetailYantekAdapter.ListViewHolder>() {
    private val listYantek = ArrayList<DataMaterialAp2t>()

    inner class ListViewHolder(private val binding: ItemDetailYantekBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataMaterialAp2t) {
            with(binding) {
                txtNoMaterial.text = item.nomorMaterial
                txtNamaMaterial.text = item.namaMaterial
                txtSatuan.text = item.unit
                txtJumlahPemakaian.text = item.qtyPemakaian
                txtJumlahReservasi.text = item.qtyReservasi
                txtValuationType.text = item.valuationType

                ivDelivery.setOnClickListener {
                    val intent =
                        Intent(itemView.context, InputSnPemakaianYantekActivity::class.java)
                    intent.putExtra(
                        InputSnPemakaianYantekActivity.EXTRA_NO_MATERIAL,
                        item.nomorMaterial
                    )
                    intent.putExtra(
                        InputSnPemakaianYantekActivity.EXTRA_NO_TRANSAKSI,
                        item.noTransaksi
                    )
                    itemView.context.startActivity(intent)
                }

            }
        }
    }

    fun setListDataYantek(listDataMaterialYantek: List<DataMaterialAp2t>) {
        this.listYantek.clear()
        this.listYantek.addAll(listDataMaterialYantek)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailYantekAdapter.ListViewHolder {
        return ListViewHolder(
            ItemDetailYantekBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: DetailYantekAdapter.ListViewHolder, position: Int) {
        holder.bind(listYantek[position])
    }

    override fun getItemCount() = listYantek.size
}

