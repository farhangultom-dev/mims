package dev.iconpln.mims.ui.tracking

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.data.remote.DataModelHistory
import dev.iconpln.mims.databinding.ItemHistoryDetailBinding
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistorySpecDetailAdapter(val lisModels: MutableList<DataModelHistory>) :
    RecyclerView.Adapter<HistorySpecDetailAdapter.ViewHolder>() {

    fun setData(po: List<DataModelHistory>) {
        lisModels.clear()
        lisModels.addAll(po)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistorySpecDetailAdapter.ViewHolder {
        val binding =
            ItemHistoryDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistorySpecDetailAdapter.ViewHolder, position: Int) {
        holder.bind(lisModels[position])
    }

    override fun getItemCount(): Int = lisModels.size

    inner class ViewHolder(val binding: ItemHistoryDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(data: DataModelHistory) {

            with(binding) {
                txtKey.text = data.key
                txtValue.text = data.value

                if (URLUtil.isValidUrl(data.value)) {
                    txtValue.setTextColor(Color.parseColor("#045A71"))
                }

                txtValue.setOnClickListener {
                    if (URLUtil.isValidUrl(data.value)) {
                        val url = URL(data.value)
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()))
                        itemView.context.startActivity(intent)
                    }
                }
            }
        }
    }
}