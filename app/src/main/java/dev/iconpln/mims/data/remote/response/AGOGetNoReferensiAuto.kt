package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class AGOReferenceNumber(
    @field:SerializedName("data")
    val data: List<AGOReferenceNumberData>,

    @field:SerializedName("msg")
    val msg: String?
)

data class AGOReferenceNumberData(
    @field:SerializedName("material_document")
    val material_document: String?,

    @field:SerializedName("plant")
    val plant: String?,

    @field:SerializedName("stor_loc")
    val storLoc: String?,

    @field:SerializedName("alasan_pengembalian")
    val reasonOfReturn: String?,

    @field:SerializedName("penerimaan_up3")
    val up3Acceptance: String?,
)