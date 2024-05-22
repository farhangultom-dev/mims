package dev.iconpln.mims.ui.pemakaian

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.R
import dev.iconpln.mims.databinding.ItemDataDetailPemakaianUlpBinding
import dev.iconpln.mims.utils.Helper

class PemakaianDetailAdapter(
    val lisModels: MutableList<dev.iconpln.mims.data.local.database.TTransPemakaianDetail>,
    var listener: OnAdapterListener, val daoSession: dev.iconpln.mims.data.local.database.DaoSession
) : RecyclerView.Adapter<PemakaianDetailAdapter.ViewHolder>() {

    fun setpemakaianList(mat: List<dev.iconpln.mims.data.local.database.TTransPemakaianDetail>) {
        lisModels.clear()
        lisModels.addAll(mat)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemDataDetailPemakaianUlpBinding.inflate(
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

    inner class ViewHolder(val binding: ItemDataDetailPemakaianUlpBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pemakaian: dev.iconpln.mims.data.local.database.TTransPemakaianDetail) {
            with(binding) {

                if (pemakaian.isDone == 1) {
                    if (!pemakaian.isActive) {
                        ivDelivery.setImageResource(R.drawable.ic_input_doc_false)
                    } else {
                        ivDelivery.setImageResource(R.drawable.ic_input_doc_done)
                    }
                } else {
                    ivDelivery.setImageResource(R.drawable.ic_input_doc_active)
                }

                if (Helper.hasDecimal(pemakaian.qtyPemakaian)!!) {
                    txtJumlahPemakaian.text = pemakaian.qtyPemakaian.toString()
                } else {
                    txtJumlahPemakaian.text = pemakaian.qtyPemakaian.toInt().toString()
                }

                if (Helper.hasDecimal(pemakaian.qtyReservasi)!!) {
                    txtJumlahReservasi.text = pemakaian.qtyReservasi.toString()
                } else {
                    txtJumlahReservasi.text = pemakaian.qtyReservasi.toInt().toString()
                }

                txtSatuan.text = pemakaian.unit
                txtNamaMaterial.text = pemakaian.namaMaterial
                txtNoMaterial.text = pemakaian.nomorMaterial
                txtValuationType.text = pemakaian.valuationType

                ivDelivery.setOnClickListener { listener.onClick(pemakaian) }
            }
        }
    }

    interface OnAdapterListener {
        fun onClick(pemakaian: dev.iconpln.mims.data.local.database.TTransPemakaianDetail)
    }
}