package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class DokumentasiRatingResponse(

    @field:SerializedName("doc")
    val doc: List<DocItem>? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class DocItem(

    @field:SerializedName("id_file")
    val idFile: String? = null
)
