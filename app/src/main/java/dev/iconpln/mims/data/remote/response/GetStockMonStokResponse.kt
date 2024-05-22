package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetStockMonStokResponse(

    @field:SerializedName("data")
    val data: List<DataMonStok>,

    @field:SerializedName("timestamp")
    val timestamp: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class DataMonStok(

    @field:SerializedName("nomor_material")
    val nomorMaterial: String? = null,

    @field:SerializedName("siap_dipakai")
    val siapDipakai: String? = null,

    @field:SerializedName("unit")
    val unit: String? = null,

    @field:SerializedName("siap_kirim")
    val siapKirim: String? = null,

    @field:SerializedName("material_desc")
    val materialDesc: String? = null,

    @field:SerializedName("valuation_type")
    val valuationType: String? = null,

    @field:SerializedName("stock_siap")
    val stockSiap: String? = null,

    @field:SerializedName("in_transit_up3")
    val inTransitUp3: String? = null,

    @field:SerializedName("in_transit_ulp")
    val inTransitUlp: String? = null,

    @field:SerializedName("dipakai")
    val dipakai: String? = null
)
