package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @field:SerializedName("privilege")
    val privilege: List<PrivilegeItem?>? = null,

    @field:SerializedName("token")
    val token: String? = "-",

    @field:SerializedName("webtoken")
    val webToken: String? = "-",

    @field:SerializedName("message")
    val message: String? = "-",

    @field:SerializedName("scope")
    val scope: String? = null,

    @field:SerializedName("user")
    val user: User? = null,

    @field:SerializedName("status")
    val status: String? = "-",
)