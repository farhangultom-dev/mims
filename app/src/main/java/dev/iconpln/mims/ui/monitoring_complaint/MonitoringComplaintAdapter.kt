package dev.iconpln.mims.ui.monitoring_complaint

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.R
import dev.iconpln.mims.databinding.ItemDataMonitoringKomplainBinding
import dev.iconpln.mims.utils.SharedPrefsUtils

class MonitoringComplaintAdapter(
    val lisModels: MutableList<dev.iconpln.mims.data.local.database.TMonitoringComplaint>,
    var listener: OnAdapterListener,
    var listenerGetData: OnAdapterListenerGetData,
    val daoSession: dev.iconpln.mims.data.local.database.DaoSession,
    var subrole: Int,
    val context: Context
) : RecyclerView.Adapter<MonitoringComplaintAdapter.ViewHolder>() {

    fun setComplaint(complaint: List<dev.iconpln.mims.data.local.database.TMonitoringComplaint>) {
        lisModels.clear()
        lisModels.addAll(complaint)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MonitoringComplaintAdapter.ViewHolder {
        val binding = ItemDataMonitoringKomplainBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonitoringComplaintAdapter.ViewHolder, position: Int) {
        holder.bind(lisModels[position])
    }

    override fun getItemCount(): Int = lisModels.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(val binding: ItemDataMonitoringKomplainBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(complaint: dev.iconpln.mims.data.local.database.TMonitoringComplaint) {
            with(binding) {
                txtDeliveryOrder.text = complaint.noDoSmar
                txtNoPo.text = if (complaint.poSapNo.isNullOrEmpty()) "-" else complaint.poSapNo
                txtStatusKomplain.text = complaint.status
                txtUnitTujuan.text = complaint.plantName
                tvTanggalPo.text = "Tgl PO " + complaint.tanggalPO

                var page = SharedPrefsUtils.getIntegerPreference(context,"PAGE_GET_KOMPLAIN_${complaint.noKomplain}",1)
                var totalPage = SharedPrefsUtils.getIntegerPreference(context,"TOTAL_PAGE_GET_KOMPLAIN_${complaint.noKomplain}", 0)
                var percentage = if (totalPage != 0)
                    ((page.toDouble() / totalPage.toDouble()) * 100).toInt() else 0

                if (page >= totalPage){
                    if (totalPage == 0){
                        tvPersentaseData.text = "Data $percentage %"
                    }else{
                        constraintFog.visibility = View.GONE
                        btnMontioring.visibility = View.VISIBLE
                    }
                }else {
                    tvPersentaseData.text = "Data $percentage %"
                }

                if (subrole != 3) {
                    val checkComplaintDetail =
                        daoSession.tMonitoringComplaintDetailDao.queryBuilder()
                            .where(
                                dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.NoKomplain.eq(
                                    complaint.noKomplain
                                )
                            )
                            .where(
                                dev.iconpln.mims.data.local.database.TMonitoringComplaintDetailDao.Properties.Status.eq(
                                    "SEDANG KOMPLAIN"
                                )
                            )
                            .list().size > 0

                    if (checkComplaintDetail) {
                        btnMontioring.setImageResource(R.drawable.ic_input_doc_active)
                    } else {
                        btnMontioring.setImageResource(R.drawable.ic_input_doc_done)
                    }
                }

                btnMontioring.setOnClickListener { listener.onClick(complaint) }
                constraintFog.setOnClickListener { listenerGetData.onClick(complaint) }
            }
        }
    }

    interface OnAdapterListener {
        fun onClick(mp: dev.iconpln.mims.data.local.database.TMonitoringComplaint)
    }

    interface OnAdapterListenerGetData {
        fun onClick(mp: dev.iconpln.mims.data.local.database.TMonitoringComplaint)
    }
}