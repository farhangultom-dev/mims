package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetPemakaianMaterialResponse(

    @field:SerializedName("totalRow")
    val totalRow: Int? = null,

    @field:SerializedName("data")
    val data: List<getMaterialPemakaian>,

    @field:SerializedName("timestamp")
    val timestamp: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class getMaterialPemakaian(

    @field:SerializedName("no_transaksi")
    val noTransaksi: String? = null,

    @field:SerializedName("nomor_material")
    val nomorMaterial: String? = null,

    @field:SerializedName("no_agenda")
    val noAgenda: String? = null,

    @field:SerializedName("petugas_pemasangan")
    val petugasPemasangan: String? = null,

    @field:SerializedName("qty")
    val qty: String? = null,

    @field:SerializedName("tanggal_ambil")
    val tanggalAmbil: String? = null,

    @field:SerializedName("tanggal_pasang")
    val tanggalPasang: String? = null,

    @field:SerializedName("serial_number")
    val serialNumber: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("no_id_meter")
    val noIdMeter: String? = null,

    @field:SerializedName("valuation_type")
    val valuationType: String? = null,
)
