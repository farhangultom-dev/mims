package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class AGOGetMasterClassification(
    @field:SerializedName("data")
    val data: List<AGOGetMasterClassificationData>,

    @field:SerializedName("msg")
    val msg: String?
)

data class AGOGetMasterClassificationData(
    @field:SerializedName("kode_klasifikasi")
    val kodeKlasifikasi: String?,

    @field:SerializedName("nama")
    var nama: String?
)