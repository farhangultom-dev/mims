package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class AGOUpdateInspectionReturnTeamDataBody(
    @field:SerializedName("nomor_inspeksiretur")
    val noInspeksiRetur: String?,

    @field:SerializedName("created_by")
    val createdBy: String?,

    @field:SerializedName("tim")
    val teamData: List<AGOUpdateInspectionReturnTeamData?>
)

data class AGOUpdateInspectionReturnTeamData(
    @field:SerializedName("nip")
    val nip: String?,

    @field:SerializedName("nama")
    val nama: String?,

    @field:SerializedName("jabatan")
    val jabatan: String?,

    @field:SerializedName("email")
    val email: String?,
)

data class AGOUpdateInspectionReturnTeam(
    @field:SerializedName("msg")
    val msg: String?,

    @field:SerializedName("out")
    val out: String?
)