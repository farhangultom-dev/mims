package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class DataMaterialAp2tResponse(

    @field:SerializedName("totalRow")
    val totalRow: Int? = null,

    @field:SerializedName("data")
    val data: List<DataMaterialAp2t>,

    @field:SerializedName("timestamp")
    val timestamp: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class DataMaterialAp2t(

    @field:SerializedName("no_transaksi")
    val noTransaksi: String? = null,

    @field:SerializedName("nomor_material")
    val nomorMaterial: String? = null,

    @field:SerializedName("unit")
    val unit: String? = null,

    @field:SerializedName("keterangan")
    val keterangan: String? = null,

    @field:SerializedName("material_group")
    val materialGroup: String? = null,

    @field:SerializedName("nama_material")
    val namaMaterial: String? = null,

    @field:SerializedName("qty_reservasi")
    val qtyReservasi: String? = null,

    @field:SerializedName("isactive")
    val isactive: String? = null,

    @field:SerializedName("qty_pemakaian")
    val qtyPemakaian: String? = null,

    @field:SerializedName("no_meter")
    val noMeter: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("valuation_type")
    val valuationType: String? = null
)
