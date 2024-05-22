package dev.iconpln.mims.ui.monitoring_permintaan.monitoring_permintaan_detail

import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.R
import dev.iconpln.mims.databinding.ItemDataMonitoringPermintaanDetailBinding
import dev.iconpln.mims.utils.Helper

class MonitoringPermintaanDetailAdapter(
    val lisModels: MutableList<dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetail>,
    var listener: OnAdapterListener,
    var daoSession: dev.iconpln.mims.data.local.database.DaoSession
) : RecyclerView.Adapter<MonitoringPermintaanDetailAdapter.ViewHolder>() {

    private var newQty: Int = 0
    private var newInpt: String = ""
    var listQuantity: MutableList<Int> = mutableListOf()
    var eachQty: MutableList<String> = mutableListOf()
    fun setMpList(po: List<dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetail>) {
        lisModels.clear()
        lisModels.addAll(po)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MonitoringPermintaanDetailAdapter.ViewHolder {
        val binding = ItemDataMonitoringPermintaanDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MonitoringPermintaanDetailAdapter.ViewHolder,
        position: Int
    ) {
        holder.bind(lisModels[position])
    }

    override fun getItemCount(): Int = lisModels.size

    inner class ViewHolder(val binding: ItemDataMonitoringPermintaanDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(mpd: dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetail) {
            with(binding) {
                btnDetail.setOnClickListener { listener.onClick(mpd) }

                if (Helper.hasDecimal(mpd.qtyPermintaan)!!) {
                    txtKuantitas.text = mpd.qtyPermintaan.toString()
                } else {
                    txtKuantitas.text = mpd.qtyPermintaan.toInt().toString()
                }

                if (Helper.hasDecimal(mpd.qtyScan)!!) {
                    txtKuantitasScan.text = mpd.qtyScan.toString()
                } else {
                    txtKuantitasScan.text = mpd.qtyScan.toInt().toString()
                }


                if (txtJumlahKardus.text == null) {
                    txtJumlahKardus.text = ""
                } else if (mpd.jumlahKardus != null) {
                    txtJumlahKardus.text = mpd.jumlahKardus.toString()
                }
                txtSatuan.text = mpd.unit
                txtNoMaterial.text = mpd.nomorMaterial
                ltxtDescMaterial.text = mpd.materialDesc
                txtValuationType.text = mpd.valuationType
                Log.d(
                    "MonitoringPermintaanDetailAdapter",
                    "bind: ${mpd.nomorMaterial} ${mpd.materialDesc} ${mpd.valuationType} ${mpd.unit} ${mpd.qtyPermintaan} ${mpd.qtyScan} ${mpd.isDone} ${mpd.isActive}"
                )

                if (mpd.qtyScan < mpd.qtyPermintaan) {
                    txtKuantitasScanKurang.text = "Kuantitas scan kurang"
                    txtKuantitasScanKurang.visibility = View.VISIBLE
                } else if (mpd.qtyScan > mpd.qtyPermintaan) {
                    txtKuantitasScanKurang.text = "Kuantitas scan kelebihan"
                    txtKuantitasScanKurang.visibility = View.VISIBLE
                } else {
                    txtKuantitasScanKurang.visibility = View.GONE
                }

                if (mpd.isDone == 1) {
                    btnDetail.setImageResource(R.drawable.ic_src_doc_selesai)
                }

//                if (mpd.isDone == 1){
//                    edtTotalRepackaging.isEnabled = false
//                    edtTotalRepackaging.setBackgroundColor(Color.GRAY)
//                }

                if (!mpd.isActive) {
                    btnDetail.setImageResource(R.drawable.ic_src_doc_notactive)
                }

                edtTotalRepackaging.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                    }

                    override fun afterTextChanged(s: android.text.Editable?) {
                        val position = bindingAdapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            newQty = s.toString().toIntOrNull() ?: 0
                            val inputan = s.toString()
                            if (listQuantity.size > position) {
                                // Hapus jumlah sebelumnya dari total
                                listQuantity.removeAt(position)
                                // Tambahkan jumlah yang baru ke total
                                listQuantity.add(newQty)
                                // Log total jumlah baru
                                Log.d(
                                    "TotalQuantity",
                                    "Total: ${listQuantity.size}, jumlah ${listQuantity.sum()}"
                                )

                            } else {
                                // Jika belum ada jumlah sebelumnya untuk posisi ini, tambahkan jumlah baru ke list
                                listQuantity.add(newQty)
                                // Log total jumlah baru
                                Log.d(
                                    "TotalQuantity",
                                    "Total: ${listQuantity.size}, jumlah ${listQuantity.sum()}"
                                )
                            }

                            while (eachQty.size <= position) {
                                eachQty.add("")
                            }
                            eachQty[position] = inputan
                            Log.d("Quantity", "Nilai pada posisi $position: $inputan")
                        }
                    }
                })
            }
        }
    }

    interface OnAdapterListener {
        fun onClick(mpd: dev.iconpln.mims.data.local.database.TTransMonitoringPermintaanDetail)
    }

//    fun disableEditext(position: Int){
//        lisModels[position].isDone = 1
//        lisModels[position].isActive = false
//        notifyDataSetChanged(po)
//    }
}