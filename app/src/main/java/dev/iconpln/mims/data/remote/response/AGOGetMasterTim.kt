package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class AGOMasterTimData (
    @field:SerializedName("data")
    val data: List<AGOMasterTimDetailData>,

    @field:SerializedName("msg")
    val msg: String?
)

data class AGOMasterTimDetailData (
    @field:SerializedName("plant")
    val plant: String?,

    @field:SerializedName("stor_loc")
    val storLoc: String?,

    @field:SerializedName("nip")
    val nip: String?,

    @field:SerializedName("nama")
    val nama: String?,

    @field:SerializedName("jabatan")
    val jabatan: String?,

    @field:SerializedName("email")
    val email: String?
)