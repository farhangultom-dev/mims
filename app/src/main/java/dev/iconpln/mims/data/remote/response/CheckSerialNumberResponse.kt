package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class CheckSerialNumberResponse(

    @field:SerializedName("data")
    val data: List<CheckSN>,

    @field:SerializedName("timestamp")
    val timestamp: String,

    @field:SerializedName("status")
    val status: Int
)

data class CheckSN(

    @field:SerializedName("success")
    val success: Boolean,

    @field:SerializedName("serial_number")
    val serialNumber: String,

    @field:SerializedName("status")
    val status: String,

    @field:SerializedName("nomor_transaksi")
    val nomorTransaksi: String
)
