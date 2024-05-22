package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class AGOInspectionReturnTeamData(
    @field:SerializedName("data")
    val data: List<AGODetailInspectionTeamData>,

    @field:SerializedName("msg")
    val msg: String
)

data class AGODetailInspectionTeamData(
    @field:SerializedName("nomor_inspeksiretur")
    val noInspeksiretur: String?,

    @field:SerializedName("created_by")
    val createdBy: String?,

    @field:SerializedName("nip")
    val nip: String?,

    @field:SerializedName("nama")
    val nama: String?,

    @field:SerializedName("jabatan")
    val jabatan: String?,

    @field:SerializedName("email")
    val email: String?
)
