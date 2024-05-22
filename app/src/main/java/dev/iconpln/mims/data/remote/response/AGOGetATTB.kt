package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class AGOATTBData(
    @field:SerializedName("data")
    val data: List<AGODetailATTBData>,

    @field:SerializedName("msg")
    val msg: String
)

data class AGODetailATTBData(
    @field:SerializedName("stor_loc")
    val storLoc: String?,

    @field:SerializedName("material_no")
    val materialNo: String?,

    @field:SerializedName("nama_material")
    val namaMaterial: String?,

    @field:SerializedName("jenis_material")
    val jenisMaterial: String?,

    @field:SerializedName("group_material")
    val groupMaterial: String?,

    @field:SerializedName("valuation_type")
    val valuationType: String?,

    @field:SerializedName("satuan")
    val satuan: String?,

    @field:SerializedName("stock_attb")
    val stockATTB: String?,

    @field:SerializedName("active_input")
    val activeInput: String?,
)