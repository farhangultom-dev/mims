package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetNoMaterialMonStokResponse(

    @field:SerializedName("data")
    val data: List<DataNoMaterialMonStok>,

    @field:SerializedName("timestamp")
    val timestamp: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class DataNoMaterialMonStok(

    @field:SerializedName("nomor_material")
    val nomorMaterial: String? = null,

    @field:SerializedName("material_desc")
    val materialDesc: String? = null
)
