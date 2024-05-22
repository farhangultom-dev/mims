package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class SnResponse(

    @field:SerializedName("no_material")
    val noMaterial: String? = null,

    @field:SerializedName("no_transaksi")
    val noTransaksi: String? = null,

    @field:SerializedName("type_scan")
    val typeScan: String? = null,

    @field:SerializedName("serial_numbers")
    val serialNumbers: List<SerialNumbers>? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("valuation_type")
    val valuationType: String? = null,

    @field:SerializedName("no_id_meter")
    val noIdMeter: String? = null
)

data class SerialNumbers(
    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("nomor_material")
    val noMaterial: String? = null,

    @field:SerializedName("serial_number")
    val serialNumber: String? = null,

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("no_packaging")
    val noPackaging: String? = null,

    @field:SerializedName("valuation_type")
    val valuantionType: String? = null,

    @field:SerializedName("no_id_meter")
    val noIdMeter: String? = null
)
