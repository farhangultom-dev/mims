package dev.iconpln.mims.ui.monitoring_permintaan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.R
import dev.iconpln.mims.databinding.ItemDataMonitoringPermintaanBinding
import dev.iconpln.mims.utils.SharedPrefsUtils

class MonitoringPermintaanAdapter(
    val lisModels: MutableList<dev.iconpln.mims.data.local.database.TTransMonitoringPermintaan>,
    var listener: OnAdapterListener,
    var listenerGetData: OnAdapterListenerGetData,
    var daoSession: dev.iconpln.mims.data.local.database.DaoSession,
    var context: Context
) : RecyclerView.Adapter<MonitoringPermintaanAdapter.ViewHolder>() {

    fun setMpList(po: List<dev.iconpln.mims.data.local.database.TTransMonitoringPermintaan>) {
        lisModels.clear()
        lisModels.addAll(po)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MonitoringPermintaanAdapter.ViewHolder {
        val binding = ItemDataMonitoringPermintaanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonitoringPermintaanAdapter.ViewHolder, position: Int) {
        holder.bind(lisModels[position])
    }

    override fun getItemCount(): Int = lisModels.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class ViewHolder(val binding: ItemDataMonitoringPermintaanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mp: dev.iconpln.mims.data.local.database.TTransMonitoringPermintaan) {
            with(binding) {
                var page = SharedPrefsUtils.getIntegerPreference(context,"PAGE_GET_PERMINTAAN_${mp.noTransaksi}",1)
                var totalPage = SharedPrefsUtils.getIntegerPreference(context,"TOTAL_PAGE_GET_PERMINTAAN_${mp.noTransaksi}", 0)
                var percentage = if (totalPage != 0)
                    ((page.toDouble() / totalPage.toDouble()) * 100).toInt() else 0

                if (page >= totalPage){
                    if (totalPage == 0){
                        tvPersentaseData.text = "Data $percentage %"
                    }else{
                        constraintFog.visibility = View.GONE
                        btnDetail.visibility = View.VISIBLE
                    }
                }else {
                    tvPersentaseData.text = "Data $percentage %"
                }

                val checkDetail = daoSession.tTransMonitoringPermintaanDetailDao.queryBuilder()
                    .where(
                        dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetailDao.Properties.NoTransaksi.eq(
                            mp.noTransaksi
                        )
                    )
                    .where(
                        dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetailDao.Properties.IsDone.eq(
                            0
                        )
                    ).list()

                btnDetail.setOnClickListener { listener.onClick(mp) }
                txtGudangAsal.text = mp.storLocAsalName
                txtGudangTujuan.text = mp.storLocTujuanName
                txtNoPermintaan.text = mp.noPermintaan
                txtJumlahKardus.text = mp.jumlahKardus.toString()
                txtQtyPermintaan.text = mp.totalQtyPermintaan.toString()
                txtQtyScanned.text = mp.totalScanQty.toString()
                txtNoTransaksi.text = mp.noTransaksi.toString()
                if (mp.noPengiriman == null || mp.noPengiriman.isEmpty()) {
                    txtNoPengiriman.text = "-"
                } else {
                    txtNoPengiriman.text = mp.noPengiriman
                }
                if (!mp.tanggalPermintaan.isNullOrEmpty()) {
                    val adjustTglPermintaan = mp.tanggalPermintaan.take(10)
                    txtTglPermintaan.text = adjustTglPermintaan
                }
                when (mp.kodePengeluaran) {
                    "1" -> txtStatusPengeluaran.text = "Permintaan"
                    "2" -> txtStatusPengeluaran.text = "Pengeluaran"
                    "0" -> txtStatusPengeluaran.text = "All"
                    else -> txtStatusPengeluaran.text = "-"
                }

                if (mp.kodePengeluaran == "2") {
                    btnDetail.setImageResource(R.drawable.ic_src_doc_selesai)
                } else {
                    btnDetail.setImageResource(R.drawable.ic_src_doc)
                }

//                if (checkDetail.isNullOrEmpty()){
//                    btnDetail.setImageResource(R.drawable.ic_src_doc_selesai)
//                }

                constraintFog.setOnClickListener { listenerGetData.onClick(mp) }

            }
        }
    }

    interface OnAdapterListener {
        fun onClick(mp: dev.iconpln.mims.data.local.database.TTransMonitoringPermintaan)
    }

    interface OnAdapterListenerGetData {
        fun onClick(mp: dev.iconpln.mims.data.local.database.TTransMonitoringPermintaan)
    }
}