package dev.iconpln.mims.ui.rating.detail_rating

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.R
import dev.iconpln.mims.databinding.ItemRatingBinding

class RatingPenyediaAdapter(
    val lisModels: MutableList<dev.iconpln.mims.data.local.database.TRating>,
    var listener: OnAdapterListener
) :
    RecyclerView.Adapter<RatingPenyediaAdapter.ViewHolder>() {

    fun setRatList(rat: List<dev.iconpln.mims.data.local.database.TRating>) {
        lisModels.clear()
        lisModels.addAll(rat)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemRatingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lisModels[position])
    }

    override fun getItemCount(): Int = lisModels.size

    inner class ViewHolder(val binding: ItemRatingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(rat: dev.iconpln.mims.data.local.database.TRating) {
            with(binding) {
                if (rat.isActive == 1) {
                    btnRate.setImageResource(R.drawable.ic_star_true)
                } else {
                    btnRate.setImageResource(R.drawable.ic_rating)
                }
                btnRate.setOnClickListener { listener.onClick(rat) }
            }
        }
    }

    interface OnAdapterListener {
        fun onClick(po: dev.iconpln.mims.data.local.database.TRating)
    }
}