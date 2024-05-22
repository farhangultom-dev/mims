package dev.iconpln.mims.ui.pengiriman

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.databinding.ItemDataPengirimanBinding
import dev.iconpln.mims.utils.Config

class ListPengirimanAdapter(
    val context: Context,
    val listModels: MutableList<dev.iconpln.mims.data.local.database.TPos>,
    var listener: OnAdapterListener
) : RecyclerView.Adapter<ListPengirimanAdapter.ViewHolder>() {

    fun setPengirimanList(datas: List<dev.iconpln.mims.data.local.database.TPos>) {
        listModels.clear()
        listModels.addAll(datas)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemDataPengirimanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listModels[position])
    }

    override fun getItemCount(): Int = listModels.size

    inner class ViewHolder(val binding: ItemDataPengirimanBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: dev.iconpln.mims.data.local.database.TPos) {
            with(binding) {
                txtNoPengiriman.text = data.noDoSmar
                txtNoPo.text = data.poSapNo
                txtNoDo.text = data.noDoMims
                txtUnit.text = data.plantName
                txtQuantity.text = data.total
                txtEta.text = data.eta
                txtEtd.text = data.etd
                txtStatus.setBackgroundColor(Color.parseColor(data.backgroundColor))
                txtStatus.setTextColor(Color.parseColor(data.textColor))
                txtStatus.text = data.doStatus

                btnDetail.setOnClickListener {
                    val intent = Intent(context, DetailPengirimanActivity::class.java)
                    intent.putExtra(Config.EXTRA_NO_PENGIRIMAN, data.noDoSmar)
                    context.startActivity(intent)
                }

                btnLokasi.setOnClickListener {
                    val intent = Intent(context, UpdateLokasiActivity::class.java)
                    intent.putExtra(Config.KEY_NO_DO, data.noDoMims)
                    intent.putExtra(Config.KEY_CODE_STATUS, data.kodeStatusDoMims)
                    intent.putExtra(Config.KEY_DO_SMAR, data.noDoSmar)
                    intent.putExtra(Config.KEY_NO_PO, data.poMpNo)
                    context.startActivity(intent)
                }

            }

            itemView.setOnClickListener { listener.onClick(data) }
        }
    }

    interface OnAdapterListener {
        fun onClick(data: dev.iconpln.mims.data.local.database.TPos)
    }
}