package dev.iconpln.mims.ui.penerimaan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.R
import dev.iconpln.mims.databinding.ItemDataPenerimaanBinding
import dev.iconpln.mims.utils.SharedPrefsUtils

class PenerimaanAdapter(
    val context: Context,
    val lisModels: MutableList<dev.iconpln.mims.data.local.database.TPosPenerimaan>,
    var listener: OnAdapterListener,
    var listenerDoc: OnAdapterListenerDoc,
    var listenerRate: OnAdapterListenerRate,
    var listenerGetData: OnAdapterListenerGetData,
    var daoSession: dev.iconpln.mims.data.local.database.DaoSession
) : RecyclerView.Adapter<PenerimaanAdapter.ViewHolder>() {

    fun setData(pe: List<dev.iconpln.mims.data.local.database.TPosPenerimaan>) {
        lisModels.clear()
        lisModels.addAll(pe)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemDataPenerimaanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lisModels[position])
    }

    override fun getItemCount(): Int = lisModels.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(val binding: ItemDataPenerimaanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pe: dev.iconpln.mims.data.local.database.TPosPenerimaan) {
            with(binding) {

                var page = SharedPrefsUtils.getIntegerPreference(context,"PAGE_GET_POSNS_${pe.noDoSmar}",1)
                var totalPage = SharedPrefsUtils.getIntegerPreference(context,"TOTAL_PAGE_GET_POSNS_${pe.noDoSmar}", 0)
                var percentage = if (totalPage != 0)
                    ((page.toDouble() / totalPage.toDouble()) * 100).toInt() else 0

                if (page >= totalPage){
                    if (totalPage == 0){
                        tvPersentaseData.text = "Data $percentage %"
                    }else{
                        constraintFog.visibility = View.GONE
                        ivDelivery.visibility = View.VISIBLE
                        ivDoc.visibility = View.VISIBLE
                        ivInputPerson.visibility = View.VISIBLE
                    }
                }else {
                    tvPersentaseData.text = "Data $percentage %"
                }

                val listDetailPen = daoSession.tPosDetailPenerimaanDao.queryBuilder()
                    .where(
                        dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.NoDoSmar.eq(
                            pe.noDoSmar
                        )
                    )
                    .where(
                        dev.iconpln.mims.data.local.database.TPosDetailPenerimaanDao.Properties.IsChecked.eq(
                            0
                        )
                    ).list()

                txtDeliveryOrder.text = pe.noDoSmar
                txtStatusPenerimaan.text =
                    if (pe.statusPenerimaan.isNullOrEmpty()) "BELUM DITERIMA" else pe.statusPenerimaan
                txtStatusPemeriksaan.text =
                    if (pe.statusPemeriksaan.isNullOrEmpty()) "BELUM DIPERIKSA" else pe.statusPemeriksaan
                txtNoPo.text = if (pe.poSapNo.isNullOrEmpty()) "-" else pe.poSapNo
                txtTglKirim.text = "Tgl ${pe.createdDate}"
                txtVendor.text = pe.namaPabrikan

                ivInputPerson.setOnClickListener { listener.onClick(pe) }
                ivDoc.setOnClickListener { listenerDoc.onClick(pe) }
                ivDelivery.setOnClickListener { listenerRate.onClick(pe) }
                constraintFog.setOnClickListener { listenerGetData.onClick(pe) }

                if (pe.bisaTerima == 1) {
                    if (!pe.tanggalDiterima.isNullOrEmpty()) {
                        if (listDetailPen.isNullOrEmpty()) {
                            if (pe.isRating == 1) {
                                if (pe.isDone == 1) {
                                    ivDoc.setImageResource(R.drawable.ic_input_doc_done)
                                    ivDelivery.setImageResource(R.drawable.ic_input_delivery_to_rating_done)
                                    ivInputPerson.setImageResource(R.drawable.ic_input_petugas_done)
                                } else {
                                    ivInputPerson.setImageResource(R.drawable.ic_input_petugas_done)
                                    ivDoc.setImageResource(R.drawable.ic_input_doc_done)
                                    ivDelivery.setImageResource(R.drawable.ic_input_delivery_to_rating_active)
                                }
                            } else {
                                ivDelivery.setImageResource(R.drawable.ic_input_delivery_to_rating_false)
                                ivInputPerson.setImageResource(R.drawable.ic_input_petugas_done)
                                ivDoc.setImageResource(R.drawable.ic_input_doc_done)
                            }
                        } else {
                            ivDoc.setImageResource(R.drawable.ic_input_doc_active)
                            ivDelivery.setImageResource(R.drawable.ic_input_delivery_to_rating_false)
                            ivInputPerson.setImageResource(R.drawable.ic_input_petugas_done)
                        }
                    } else {
                        ivDoc.setImageResource(R.drawable.ic_input_doc_false)
                        ivDelivery.setImageResource(R.drawable.ic_input_delivery_to_rating_false)
                        ivInputPerson.setImageResource(R.drawable.ic_input_petugas_active)
                    }
                } else {
                    ivDoc.setImageResource(R.drawable.ic_input_doc_false)
                    ivDelivery.setImageResource(R.drawable.ic_input_delivery_to_rating_false)
                    ivInputPerson.setImageResource(R.drawable.ic_input_petugas_false)
                }
            }
        }
    }

    interface OnAdapterListener {
        fun onClick(po: dev.iconpln.mims.data.local.database.TPosPenerimaan)
    }

    interface OnAdapterListenerDoc {
        fun onClick(po: dev.iconpln.mims.data.local.database.TPosPenerimaan)
    }

    interface OnAdapterListenerRate {
        fun onClick(po: dev.iconpln.mims.data.local.database.TPosPenerimaan)
    }

    interface OnAdapterListenerGetData {
        fun onClick(po: dev.iconpln.mims.data.local.database.TPosPenerimaan)
    }
}