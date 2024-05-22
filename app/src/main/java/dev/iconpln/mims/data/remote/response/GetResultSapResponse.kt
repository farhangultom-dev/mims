package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetResultSapResponse(

    @field:SerializedName("data")
    val data: List<GetResutlSap>,

    @field:SerializedName("timestamp")
    val timestamp: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class GetResutlSap(

    @field:SerializedName("create_ON")
    val createON: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("nomor_SAP")
    val nomorSAP: String? = null,

    @field:SerializedName("nomor")
    val nomor: String? = null
)
