package dev.iconpln.mims.ui.ulp.penerimaan.input_pemeriksaan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.R
import dev.iconpln.mims.databinding.ItemDetailPemeriksaanUlpBinding
import dev.iconpln.mims.utils.Helper

class DetailPemeriksaanUlpAdapter(
    val lisModels: MutableList<dev.iconpln.mims.data.local.database.TTransPenerimaanDetailUlp>,
    var listener: OnAdapterListener
) : RecyclerView.Adapter<DetailPemeriksaanUlpAdapter.ViewHolder>() {

    fun setPenerimaanList(mat: List<dev.iconpln.mims.data.local.database.TTransPenerimaanDetailUlp>) {
        lisModels.clear()
        lisModels.addAll(mat)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemDetailPemeriksaanUlpBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lisModels[position])
    }

    override fun getItemCount(): Int = lisModels.size

    inner class ViewHolder(val binding: ItemDetailPemeriksaanUlpBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pengujian: dev.iconpln.mims.data.local.database.TTransPenerimaanDetailUlp) {
            with(binding) {
                if (Helper.hasDecimal(pengujian.qtyPenerimaan)!!) {
                    txtKuantitas.text = pengujian.qtyPenerimaan.toString()
                } else {
                    txtKuantitas.text = pengujian.qtyPenerimaan.toInt().toString()
                }

                if (Helper.hasDecimal(pengujian.qtyPemeriksaan)!!) {
                    txtKuantitasPeriksa.text = pengujian.qtyPemeriksaan.toString()
                } else {
                    txtKuantitasPeriksa.text = pengujian.qtyPemeriksaan.toInt().toString()
                }

                txtSpesifikasi.text = pengujian.materialDesc
                txtNoMaterial.text = pengujian.noMaterial
                txtValuationType.text = pengujian.valuationType

                if (pengujian.isDone == 1) {
                    if (!pengujian.isActive) {
                        ivDelivery.setImageResource(R.drawable.ic_check_mat_notactive)
                    } else {
                        ivDelivery.setImageResource(R.drawable.ic_check_mat_done)
                    }
                }

                ivDelivery.setOnClickListener { listener.onClick(pengujian) }
            }
        }
    }

    interface OnAdapterListener {
        fun onClick(pengujian: dev.iconpln.mims.data.local.database.TTransPenerimaanDetailUlp)
    }
}