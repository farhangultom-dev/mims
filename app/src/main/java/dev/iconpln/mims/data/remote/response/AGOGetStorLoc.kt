package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class AGOStorLoc(
    @field:SerializedName("gudang")
    val storLoc: AGOStorLocData
)

data class AGOStorLocData(
    @field:SerializedName("data")
    val storData: List<AGOStorLocDetailData>,

    @field:SerializedName("msg")
    val msg: String
)

data class AGOStorLocDetailData(
    @field:SerializedName("jenis")
    val jenis: String?,

    @field:SerializedName("plant")
    val plant: String?,

    @field:SerializedName("stor_loc")
    val storLoc: String?,

    @field:SerializedName("stor_loc_name")
    val storLocName: String?,

    @field:SerializedName("unit")
    val unit: String?
)