package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class PemakaianMaterialAp2tResponse(

    @field:SerializedName("totalRow")
    val totalRow: Int? = null,

    @field:SerializedName("data")
    val data: List<DataItemAp2t>,

    @field:SerializedName("timestamp")
    val timestamp: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class DataItemAp2t(

    @field:SerializedName("no_transaksi")
    val noTransaksi: String,

    @field:SerializedName("nomor_material")
    val nomorMaterial: String,

    @field:SerializedName("no_agenda")
    val noAgenda: String,

    @field:SerializedName("unit")
    val unit: String,

    @field:SerializedName("qty")
    val qty: String,

    @field:SerializedName("serial_number")
    val serialNumber: String,

    @field:SerializedName("no_id_meter")
    val noIdMeter: String
)
