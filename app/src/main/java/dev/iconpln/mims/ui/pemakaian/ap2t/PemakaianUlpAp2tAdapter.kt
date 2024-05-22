package dev.iconpln.mims.ui.pemakaian.ap2t

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.iconpln.mims.data.remote.response.DataItemUlpAp2t
import dev.iconpln.mims.databinding.ItemDetailPemakaianUlpAp2tBinding

class PemakaianUlpAp2tAdapter(val plant: String, val storLok: String) :
    RecyclerView.Adapter<PemakaianUlpAp2tAdapter.ListViewHolder>() {
    private val listAp2t = ArrayList<DataItemUlpAp2t>()
    private var noMaterial = ArrayList<String>()

    fun getNoMaterial(noMaterial: ArrayList<String>) {
        this.noMaterial = noMaterial
    }

    fun setListPemakaianAp2t(listAp2t: List<DataItemUlpAp2t>) {
        this.listAp2t.clear()
        this.listAp2t.addAll(listAp2t)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            ItemDetailPemakaianUlpAp2tBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = listAp2t.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listAp2t[position])

    }

    inner class ListViewHolder(private val binding: ItemDetailPemakaianUlpAp2tBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataItemUlpAp2t) {
            with(binding) {
                txtNoAgendaAp2t.text = item.noAgenda
                txtIdPelangganAp2t.text = item.idPelanggan
                txtNamaPelangganAp2t.text = item.namaPelanggan
                txtDaya.text = item.daya.toString()
                txtTarif.text = item.tarif
                txtJenisPekerjaan.text = item.jenisPekerjaan
                txtTanggalBayar.text = item.tanggalBayar
                txtAlamatPelanggan.text = item.alamatPelanggan

                ivScanAp2t.setOnClickListener {
                    val intent = Intent(itemView.context, InputSnPemakaianAp2tActivty::class.java)
                    intent.putExtra(InputSnPemakaianAp2tActivty.EXTRA_NO_AGENDA, item.noAgenda)
                    intent.putExtra(
                        InputSnPemakaianAp2tActivty.EXTRA_ID_PELANGGAN,
                        item.idPelanggan
                    )
                    intent.putExtra(
                        InputSnPemakaianAp2tActivty.EXTRA_JENIS_PEKERJAAN,
                        item.jenisPekerjaan
                    )
                    intent.putExtra(
                        InputSnPemakaianAp2tActivty.EXTRA_NAMA_PELANGGAN,
                        item.namaPelanggan
                    )
                    intent.putExtra(
                        InputSnPemakaianAp2tActivty.EXTRA_NO_TRANSAKSI,
                        item.noTransaksi
                    )
                    intent.putExtra(InputSnPemakaianAp2tActivty.EXTRA_NO_MATERIAL, noMaterial)
                    intent.putExtra(InputSnPemakaianAp2tActivty.EXTRA_PLANT, plant)
                    intent.putExtra(InputSnPemakaianAp2tActivty.EXTRA_STORLOK, storLok)

                    itemView.context.startActivity(intent)
                }
            }
        }
    }
}