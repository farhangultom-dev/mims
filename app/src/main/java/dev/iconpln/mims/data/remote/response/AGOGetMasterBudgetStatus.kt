package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class AGOMasterBudgetStatus(
    @field:SerializedName("data")
    val data: List<AGOBudgetStatusData>,

    @field:SerializedName("msg")
    val msg: String?
)

data class AGOBudgetStatusData(
    @field:SerializedName("kode_status_anggaran")
    val kodeStatusAnggaran: String?,

    @field:SerializedName("nama")
    val nama: String?
)