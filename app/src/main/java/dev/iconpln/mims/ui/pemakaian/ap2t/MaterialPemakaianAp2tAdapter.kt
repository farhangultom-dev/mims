package dev.iconpln.mims.ui.pemakaian.ap2t

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.DetailPemakaianDetailDataAp2t
import dev.iconpln.mims.data.remote.response.DataMaterialAp2t
import dev.iconpln.mims.databinding.ItemDataDetailMaterialPemakaianUlpAp2tBinding

class MaterialPemakaianAp2tAdapter(val noreservasi: String) :
    RecyclerView.Adapter<MaterialPemakaianAp2tAdapter.ListViewHolder>() {

    private val listDataMaterialAp2t = ArrayList<DataMaterialAp2t>()


    inner class ListViewHolder(private val binding: ItemDataDetailMaterialPemakaianUlpAp2tBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataMaterialAp2t) {
            with(binding) {
                txtNoMaterial.text = item.nomorMaterial
                txtNamaMaterial.text = item.namaMaterial
                txtSatuan.text = item.unit
                txtJumlahPemakaian.text = item.qtyPemakaian
                txtValuationType.text = item.valuationType
                txtJumlahReservasi.text = item.qtyReservasi
                ivDetail.setOnClickListener {
                    val Intent = Intent(itemView.context, DetailPemakaianDetailDataAp2t::class.java)
                    Intent.putExtra(
                        DetailPemakaianDetailDataAp2t.EXTRA_NO_TRANSAKSI,
                        item.noTransaksi
                    )
                    Intent.putExtra(
                        DetailPemakaianDetailDataAp2t.EXTRA_NO_MATERIAL,
                        item.nomorMaterial
                    )
                    Intent.putExtra(DetailPemakaianDetailDataAp2t.EXTRA_NO_RESERVASI, noreservasi)
                    Log.d("MaterialPemakaianAp2tAdapter", "cek reservasi : $noreservasi")
                    it.context.startActivity(Intent)
                }
            }
        }
    }

    fun setListDataMaterialAp2t(listDataMaterialAp2t: List<DataMaterialAp2t>, noreservasi: String) {
        this.listDataMaterialAp2t.clear()
        this.listDataMaterialAp2t.addAll(listDataMaterialAp2t)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            ItemDataDetailMaterialPemakaianUlpAp2tBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount() = listDataMaterialAp2t.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listDataMaterialAp2t[position])
    }
}