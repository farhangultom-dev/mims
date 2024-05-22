package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class AddSerialNumberUlpAp2tResponse(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("timestamp")
    val timestamp: String,

    @field:SerializedName("status")
    val status: Int
)
