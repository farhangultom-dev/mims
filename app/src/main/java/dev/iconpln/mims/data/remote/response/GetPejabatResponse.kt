package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetPejabatResponse(

    @field:SerializedName("data")
    val data: List<DataItemPejabat>,

    @field:SerializedName("timestamp")
    val timestamp: String,

    @field:SerializedName("status")
    val status: Int
)

data class DataItemPejabat(

    @field:SerializedName("nama")
    val nama: String,

    @field:SerializedName("id")
    val id: String
)
