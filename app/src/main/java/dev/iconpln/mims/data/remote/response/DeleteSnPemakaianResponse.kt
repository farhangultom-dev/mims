package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class DeleteSnPemakaianResponse(

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("timestamp")
    val timestamp: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)
