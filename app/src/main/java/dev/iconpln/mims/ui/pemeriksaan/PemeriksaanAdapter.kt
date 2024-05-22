package dev.iconpln.mims.ui.pemeriksaan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.R
import dev.iconpln.mims.databinding.ItemDataPeemeriksaanBinding
import dev.iconpln.mims.utils.SharedPrefsUtils

class PemeriksaanAdapter(
    val lisModels: MutableList<dev.iconpln.mims.data.local.database.TPemeriksaan>,
    var listener: OnAdapterListener,
    var listenerDoc: OnAdapterListenerDoc,
    var listenerGetdata: OnAdapterListenerGetData,
    var daoSession: dev.iconpln.mims.data.local.database.DaoSession,
    var context: Context
) : RecyclerView.Adapter<PemeriksaanAdapter.ViewHolder>() {

    fun setPeList(pe: List<dev.iconpln.mims.data.local.database.TPemeriksaan>) {
        lisModels.clear()
        lisModels.addAll(pe)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemDataPeemeriksaanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lisModels[position])
    }

    override fun getItemCount(): Int = lisModels.size

    inner class ViewHolder(val binding: ItemDataPeemeriksaanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pe: dev.iconpln.mims.data.local.database.TPemeriksaan) {
            with(binding) {
                txtDeliveryOrder.text = pe.noDoSmar
                txtNoPoSap.text = if (pe.poSapNo.isNullOrEmpty()) "-" else pe.poSapNo
                txtTglKirim.text = "Tgl ${pe.createdDate}"
                txtVendor.text = pe.namaPabrikan
                txtNoPemeriksaan.text =
                    if (pe.noPemeriksaan.isNullOrEmpty()) "-" else pe.noPemeriksaan

                ivInputPerson.setOnClickListener { listener.onClick(pe) }
                ivDoc.setOnClickListener { listenerDoc.onClick(pe) }
                constraintFog.setOnClickListener { listenerGetdata.onClick(pe) }

                var page = SharedPrefsUtils.getIntegerPreference(context,"PAGE_GET_PEMERIKSAAN_${pe.noPemeriksaan}",1)
                var totalPage = SharedPrefsUtils.getIntegerPreference(context,"TOTAL_PAGE_GET_PEMERIKSAAN_${pe.noPemeriksaan}", 0)
                var percentage = if (totalPage != 0)
                    ((page.toDouble() / totalPage.toDouble()) * 100).toInt() else 0

                if (page >= totalPage){
                    if (totalPage == 0){
                        tvPersentaseData.text = "Data $percentage %"
                    }else{
                        constraintFog.visibility = View.GONE
                        ivInputPerson.visibility = View.VISIBLE
                        ivDoc.visibility = View.VISIBLE
                    }
                }else {
                    tvPersentaseData.text = "Data $percentage %"
                }

                var listPem = daoSession.tPemeriksaanDao.queryBuilder()
                    .where(
                        dev.iconpln.mims.data.local.database.TPemeriksaanDao.Properties.NoDoSmar.eq(
                            pe.noDoSmar
                        )
                    ).list().get(0)

                if (pe.namaKetua.isNotEmpty() && pe.isDone == 0) {
                    ivInputPerson.setImageResource(R.drawable.ic_input_petugas_done)
                    ivDoc.setImageResource(R.drawable.ic_input_doc_active)
                } else if (pe.namaKetua.isNullOrEmpty() && pe.isDone == 0) {
                    ivInputPerson.setImageResource(R.drawable.ic_input_petugas_active)
                    ivDoc.setImageResource(R.drawable.ic_input_doc_false)
                }

                if (pe.isDone == 1) {
                    listPem.statusPemeriksaan = "SUDAH DIPERIKSA"
                    daoSession.tPemeriksaanDao.update(listPem)
                    txtStatusPemeriksaan.text = "SUDAH DIPERIKSA"

                    ivDoc.setImageResource(R.drawable.ic_input_doc_done)
                    ivInputPerson.setImageResource(R.drawable.ic_input_petugas_done)
                } else {
                    txtStatusPemeriksaan.text = "SEDANG DIPERIKSA"
                }
            }
        }
    }

    interface OnAdapterListener {
        fun onClick(po: dev.iconpln.mims.data.local.database.TPemeriksaan)
    }

    interface OnAdapterListenerDoc {
        fun onClick(po: dev.iconpln.mims.data.local.database.TPemeriksaan)
    }

    interface OnAdapterListenerGetData {
        fun onClick(po: dev.iconpln.mims.data.local.database.TPemeriksaan)
    }
}