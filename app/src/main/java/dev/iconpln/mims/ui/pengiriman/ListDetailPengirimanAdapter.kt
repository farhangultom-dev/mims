package dev.iconpln.mims.ui.pengiriman

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.databinding.ItemDetailPengirimanBinding

class ListDetailPengirimanAdapter(
    val listModels: MutableList<dev.iconpln.mims.data.local.database.TPosSns>,
    var listener: OnAdapterListener
) : RecyclerView.Adapter<ListDetailPengirimanAdapter.ViewHolder>() {

    fun setDetailPengirimanList(datas: List<dev.iconpln.mims.data.local.database.TPosSns>) {
        listModels.clear()
        listModels.addAll(datas)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemDetailPengirimanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listModels[position])
    }

    override fun getItemCount(): Int = listModels.size

    inner class ViewHolder(val binding: ItemDetailPengirimanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: dev.iconpln.mims.data.local.database.TPosSns) {
            with(binding) {
                txtSerialNumber.text = data.noSerial
                txtTglProduksi.text = data.tglProduksi
                txtNoMaterial.text = data.noMatSap
                txtIsiMeteorologi.text = data.noSertMeterologi
                txtIsiBatch.text = data.noProduksi
                txtIsiPackaging.text = data.noPackaging
                txtIsiSpesifikasi.text = data.spesifikasi
                txtGaransi.text = data.masaGaransi
                txtIsiSpln.text = data.spln
                txtIsiKategori.text = data.namaKategoriMaterial
            }

            itemView.setOnClickListener { listener.onClick(data) }
        }
    }

    interface OnAdapterListener {
        fun onClick(data: dev.iconpln.mims.data.local.database.TPosSns)
    }
}