package dev.iconpln.mims.ui.pemakaian.maximo

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.data.remote.response.DataMaterialAp2t
import dev.iconpln.mims.databinding.ItemDetailMaximoBinding

class DetailPemakaianMaximoAdapter :
    RecyclerView.Adapter<DetailPemakaianMaximoAdapter.ListViewHolder>() {
    private val listDataMaximo = ArrayList<DataMaterialAp2t>()

    inner class ListViewHolder(private val binding: ItemDetailMaximoBinding) :
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
                        Intent(itemView.context, InputSnPemakaianMaximoActivity::class.java)
                    intent.putExtra(
                        InputSnPemakaianMaximoActivity.EXTRA_NO_MATERIAL,
                        item.nomorMaterial
                    )
                    intent.putExtra(
                        InputSnPemakaianMaximoActivity.EXTRA_NO_TRANSAKSI,
                        item.noTransaksi
                    )
                    itemView.context.startActivity(intent)
                }

            }
        }
    }

    fun setListDetailMaximo(listMaximo: List<DataMaterialAp2t>) {
        this.listDataMaximo.clear()
        this.listDataMaximo.addAll(listMaximo)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailPemakaianMaximoAdapter.ListViewHolder {
        return ListViewHolder(
            ItemDetailMaximoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: DetailPemakaianMaximoAdapter.ListViewHolder,
        position: Int
    ) {
        holder.bind(listDataMaximo[position])
    }

    override fun getItemCount() = listDataMaximo.size
}