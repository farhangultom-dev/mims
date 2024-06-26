package dev.iconpln.mims.ui.rating.detail_rating

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.iconpln.mims.BuildConfig
import dev.iconpln.mims.R
import dev.iconpln.mims.data.remote.response.DocItem
import dev.iconpln.mims.databinding.ItemDokumentasiBinding

class DetailPenerimaanDokumentasiRatingAdapter(val lisModels: MutableList<DocItem>) :
    RecyclerView.Adapter<DetailPenerimaanDokumentasiRatingAdapter.ViewHolder>() {

    fun setData(po: List<DocItem>) {
        lisModels.clear()
        lisModels.addAll(po)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemDokumentasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lisModels[position].idFile!!)
    }

    override fun getItemCount(): Int = lisModels.size

    inner class ViewHolder(val binding: ItemDokumentasiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tpd: String) {

            with(binding) {
                Glide.with(itemView)
                    .load("${BuildConfig.BASE_URL}/resource/file/getFile/$tpd")
                    .placeholder(R.drawable.ic_not_found)
                    .into(binding.ivDokumentasi)
            }
        }
    }
}