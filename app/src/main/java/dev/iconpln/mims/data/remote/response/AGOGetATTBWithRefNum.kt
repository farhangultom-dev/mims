package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class AGOATTBDataBasedOnRefNum(
    @field:SerializedName("data")
    val data: List<AGODetailATTBDataBasedOnRefNum>,

    @field:SerializedName("msg")
    val msg: String
)

data class AGODetailATTBDataBasedOnRefNum (
    @field:SerializedName("material_document")
    val materialDocument: String?,

    @field:SerializedName("plant")
    val plant: String?,

    @field:SerializedName("stor_loc")
    val storLoc: String?,

    @field:SerializedName("material_no")
    val materialNo: String?,

    @field:SerializedName("material_desc")
    val materialDesc: String?,

    @field:SerializedName("satuan")
    val satuan: String?,

    @field:SerializedName("jumlah_pengembalian")
    val jumlahPengembalian: String?,

    @field:SerializedName("valuation_type")
    val valuationType: String?,

    @field:SerializedName("alasan_pengembalian")
    val alasanPengembalian: String?,

    @field:SerializedName("penerimaan_up3")
    val penerimaanUp3: String?,
)
