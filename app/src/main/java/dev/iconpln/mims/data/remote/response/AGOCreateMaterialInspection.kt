package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class AGOCreateMaterialInspectionBody(
    @field:SerializedName("nomor_inspeksiretur")
    val noInspeksiRetur: String?,

    @field:SerializedName("material_no")
    val materialNo: String?,

    @field:SerializedName("satuan")
    val satuan: String?,

    @field:SerializedName("qty_unit")
    val qtyUnit: String?,

    @field:SerializedName("tgl_inspeksiretur")
    val tglInspeksiRetur: String?,

    @field:SerializedName("nama_material")
    val namaMaterial: String?,

    @field:SerializedName("status_inspeksi")
    val statusInspeksi: String?,

    @field:SerializedName("lokasi_fisik")
    val lokasiFisik: String?,

    @field:SerializedName("unit")
    val unit: String?,

    @field:SerializedName("anggaran")
    val anggaran: String?,

    @field:SerializedName("klasifikasi")
    val klasifikasi: String?,

    @field:SerializedName("no_asset")
    val noAsset: String?,

    @field:SerializedName("detail")
    val detail: Map<String, Any>?,

    @field:SerializedName("nomor_referensi")
    val noReferensi: String?,

    @field:SerializedName("qty_siapinspeksiretur")
    val qtySiapInspeksiRetur: String?,

    @field:SerializedName("qty_inspeksiretur")
    val qtyInspeksiRetur: String?,

    @field:SerializedName("alasan_pengembalian")
    val alasanPengembalian: String?,

    @field:SerializedName("plant")
    val plant: String?,

    @field:SerializedName("stor_loc")
    val storLoc: String?,

    @field:SerializedName("created_by")
    val createdBy: String?,
)

data class AGOCreateMaterialInspection(
    @field:SerializedName("nomor_inspeksiretur")
    val noInspeksiRetur: String?,

    @field:SerializedName("msg")
    val msg: String?,

    @field:SerializedName("out")
    val out: String?
)