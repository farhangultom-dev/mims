package dev.iconpln.mims.ui.penerimaan.detail_penerimaan

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.databinding.ItemSnMaterialBinding

class DetailPenerimaanAdapter(
    val lisModels: MutableList<dev.iconpln.mims.data.local.database.TPosDetailPenerimaan>,
    var listener: OnAdapterListener,
    var daoSession: dev.iconpln.mims.data.local.database.DaoSession,
    var partialCode: String,
    var role: Int,
    var context: Context
) : RecyclerView.Adapter<DetailPenerimaanAdapter.ViewHolder>() {

    fun setData(po: List<dev.iconpln.mims.data.local.database.TPosDetailPenerimaan>) {
        lisModels.clear()
        lisModels.addAll(po)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemSnMaterialBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lisModels[position])
    }

    override fun getItemCount(): Int = lisModels.size

    inner class ViewHolder(val binding: ItemSnMaterialBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tpd: dev.iconpln.mims.data.local.database.TPosDetailPenerimaan) {
            with(binding) {
                txtSnMaterial.text = tpd.serialNumber
                txtKategori.text = tpd.namaKategoriMaterial
                txtPackaging.text = if (tpd.noPackaging.isNullOrEmpty()) "-" else tpd.noPackaging
                txtNoMeter.text = tpd.serialNumber.takeLast(11)
//                cbSesuai.isChecked = po.isDone == 1

                if (role == 10) {
                    cbSesuai.isEnabled = false
                    cbTidakSesuai.isEnabled = false
                }

                if (tpd.isChecked == 1 && tpd.statusPenerimaan == "SESUAI"
                    || tpd.isChecked == 1 && tpd.statusPenerimaan == "DITERIMA"
                    || tpd.isChecked == 1 && tpd.statusPenerimaan == "BELUM DIPERIKSA"
                ) {
                    if (tpd.isDone == 1) {
                        cbSesuai.isChecked = true
                        cbTidakSesuai.isChecked = false
                        cbSesuai.isEnabled = false
                        cbTidakSesuai.isEnabled = false
                    } else {
                        cbSesuai.isChecked = true
                        cbTidakSesuai.isChecked = false
                    }
                } else if (tpd.isChecked == 1 && tpd.statusPenerimaan == "TIDAK SESUAI"
                    || tpd.isChecked == 1 && tpd.statusPenerimaan == "KOMPLAIN"
                ) {
                    if (tpd.isDone == 1) {
                        cbSesuai.isChecked = false
                        cbTidakSesuai.isChecked = true
                        cbSesuai.isEnabled = false
                        cbTidakSesuai.isEnabled = false
                    } else {
                        cbTidakSesuai.isChecked = true
                        cbSesuai.isChecked = false
                    }
                } else {
                    cbTidakSesuai.isChecked = false
                    cbSesuai.isChecked = false
                }

                cbTidakSesuai.setOnCheckedChangeListener { buttonView, isChecked ->
                    cbSesuai.isEnabled = !isChecked
                    if (isChecked) {
                        tpd.statusPenerimaan = "TIDAK SESUAI"
                        tpd.isChecked = 1
                        tpd.partialCode = partialCode
                        daoSession.tPosDetailPenerimaanDao.update(tpd)
                    } else {
                        tpd.statusPenerimaan = ""
                        tpd.isChecked = 0
                        tpd.partialCode = ""
                        daoSession.tPosDetailPenerimaanDao.update(tpd)
                        listener.onClick(tpd)
                    }
                }

                cbSesuai.setOnCheckedChangeListener { buttonView, isChecked ->
                    cbTidakSesuai.isEnabled = !isChecked
                    if (isChecked) {
                        tpd.statusPenerimaan = "SESUAI"
                        tpd.isChecked = 1
                        tpd.partialCode = partialCode
                        daoSession.tPosDetailPenerimaanDao.update(tpd)
                    } else {
                        tpd.statusPenerimaan = ""
                        tpd.isChecked = 0
                        tpd.partialCode = ""
                        daoSession.tPosDetailPenerimaanDao.update(tpd)
                        listener.onClick(tpd)
                    }

                }
            }
        }
    }

    interface OnAdapterListener {
        fun onClick(po: dev.iconpln.mims.data.local.database.TPosDetailPenerimaan)
    }
}