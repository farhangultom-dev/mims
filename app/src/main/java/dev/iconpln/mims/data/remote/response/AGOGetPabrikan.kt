package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class AGOManufacturer(
    @field:SerializedName("data")
    val data: List<AGOManufacturerData>,

    @field:SerializedName("msg")
    val msg: String?
)

data class AGOManufacturerData(
    @field:SerializedName("idpabrikan")
    val idPabrikan: String?,

    @field:SerializedName("namapabrikan")
    val namaPabrikan: String?
)
