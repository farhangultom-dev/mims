package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class AddSerialNumberUlpResponse(

    @field:SerializedName("data")
    val data: Data,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("timestamp")
    val timestamp: String,

    @field:SerializedName("status")
    val status: Int
)

data class Data(

    @field:SerializedName("no_transaksi")
    val noTransaksi: String,

    @field:SerializedName("nomor_material")
    val nomorMaterial: String,

    @field:SerializedName("serial_number")
    val serialNumber: String
)
