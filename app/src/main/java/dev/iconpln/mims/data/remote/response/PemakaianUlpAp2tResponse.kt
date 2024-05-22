package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class PemakaianUlpAp2tResponse(

    @field:SerializedName("totalRow")
    val totalRow: Int? = null,

    @field:SerializedName("data")
    val data: List<DataItemUlpAp2t>,

    @field:SerializedName("timestamp")
    val timestamp: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class DataItemUlpAp2t(

    @field:SerializedName("jenis_pekerjaan")
    val jenisPekerjaan: String? = null,

    @field:SerializedName("no_transaksi")
    val noTransaksi: String? = null,

    @field:SerializedName("no_agenda")
    val noAgenda: String? = null,

    @field:SerializedName("nama_pelanggan")
    val namaPelanggan: String? = null,

    @field:SerializedName("tarif")
    val tarif: String? = null,

    @field:SerializedName("tanggal_bayar")
    val tanggalBayar: String? = null,

    @field:SerializedName("serials")
    val serials: List<Any?>? = null,

    @field:SerializedName("qty")
    val qty: Int? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("alamat_pelanggan")
    val alamatPelanggan: String? = null,

    @field:SerializedName("daya")
    val daya: Int? = null,

    @field:SerializedName("id_pelanggan")
    val idPelanggan: String? = null
)
