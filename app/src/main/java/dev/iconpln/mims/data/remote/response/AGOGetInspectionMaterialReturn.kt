package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class AGOGInspectionMaterialReturnData(
    @field:SerializedName("data")
    val data: List<AGODetailInspectionMaterialReturnData?>?,

    @field:SerializedName("msg")
    val msg: String?
)

data class AGODetailInspectionMaterialReturnData(
    @field:SerializedName("nomor_inspeksiretur")
    val noInspeksiRetur: String?,

    @field:SerializedName("plant")
    val plant: String?,

    @field:SerializedName("stor_loc")
    val storLoc: String?,

    @field:SerializedName("tgl_inspeksiretur")
    val tglInspeksiRetur: String?,

    @field:SerializedName("material_no")
    val materialNo: String?,

    @field:SerializedName("unit")
    val unit: String?,

    @field:SerializedName("qty_siapinspeksiretur")
    val qtySiapInspeksiRetur: String?,

    @field:SerializedName("qty_inspeksiretur")
    val qtyInspeksiRetur: String?,

    @field:SerializedName("valuation_type")
    val valuationType: String?,

    @field:SerializedName("created_by")
    val createdBy: String?,

    @field:SerializedName("tgl_ba")
    val tglBA: String?,

    @field:SerializedName("kode_status_inspeksi")
    val kodeStatusInspeksi: String?,

    @field:SerializedName("status_inspeksi")
    val statusInspeksi: String?,

    @field:SerializedName("qty_unit")
    val qtyUnit: String?,

    @field:SerializedName("nama_material")
    val namaMaterial: String?,

    @field:SerializedName("stor_loc_fisik")
    val storLocFisik: String?,
)