package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class AGOUpdateMaterialInspectionBySNBody(
    @field:SerializedName("nomor_inspeksiretur")
    val nomorInspeksiRetur: String?,

    @field:SerializedName("serial_long")
    val serialLong: String?,

    @field:SerializedName("anggaran")
    val anggaran: String?,

    @field:SerializedName("klasifikasi")
    val klasifikasi: String?,

    @field:SerializedName("no_asset")
    val noAsset: String?,

    @field:SerializedName("qty")
    val qty: String?,

    @field:SerializedName("tahun")
    val tahun: String?,

    @field:SerializedName("idpabrikan")
    val idPabrikan: String?,

    @field:SerializedName("id_pelanggan")
    val idPelanggan: String?,

    @field:SerializedName("attb_limbah")
    val attbLimbah: String?,

    @field:SerializedName("no_polis")
    val noPolis: String?,
)

data class AGOUpdateMaterialInspectionBySN(
    @field:SerializedName("msg")
    val msg: String?,

    @field:SerializedName("out")
    val out: String?
)