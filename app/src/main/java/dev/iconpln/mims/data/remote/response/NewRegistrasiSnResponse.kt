package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class NewRegistrasiSnResponse(

    @field:SerializedName("totalRow")
    val totalRow: Int,

    @field:SerializedName("data")
    val data: List<getSnMaterial>,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("timestamp")
    val timestamp: String,

    @field:SerializedName("status")
    val status: Int
)

data class getSnMaterial(

    @field:SerializedName("tgl_registrasi")
    val tglRegistrasi: String,

    @field:SerializedName("no_registrasi")
    val noRegistrasi: String,

    @field:SerializedName("flag_print")
    val flagPrint: Boolean
)
