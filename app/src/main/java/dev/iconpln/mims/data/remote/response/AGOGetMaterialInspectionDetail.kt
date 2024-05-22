package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class AGOMaterialInspectionData(
    @field:SerializedName("detail")
    val detailMaterialInspection: AGOMaterialInspectionDetail?,

    @field:SerializedName("tim")
    val inspectionTeam: AGOMaterialInspectionTeamData?
)

data class AGOMaterialInspectionDetail(
    @field:SerializedName("material_no")
    val materialNo: String?,

    @field:SerializedName("nama_material")
    val namaMaterial: String?,

    @field:SerializedName("tahun")
    val tahun: String?,

    @field:SerializedName("idpabrikan")
    val idPabrikan: String?,

    @field:SerializedName("namapabrikan")
    val namaPabrikan: String?,

    @field:SerializedName("no_asset")
    val noAsset: String?,

    @field:SerializedName("tgl_inspeksiretur")
    val tglInspeksiRetur: String?,

    @field:SerializedName("nama_inspeksiretur")
    val namaInspeksiRetur: String?,

    @field:SerializedName("serial_long")
    val serialLong: String?,

    @field:SerializedName("klasifikasi")
    val idKlasifikasi: String?,

    @field:SerializedName("nama_klasifikasi")
    val namaKlasifikasi: String?,

    @field:SerializedName("nomor_inspeksiretur")
    val nomorInspeksiRetur: String?,

    @field:SerializedName("qty")
    val qty: String?,

    @field:SerializedName("unit")
    val unit: String?,

    @field:SerializedName("anggaran")
    val anggaran: String?,

    @field:SerializedName("id_pelanggan")
    val idPelanggan: String?,

    @field:SerializedName("status_inspeksi")
    val statusInspeksi: String?
)


data class AGOMaterialInspectionTeamData(
    @field:SerializedName("data")
    val listDataTeam: List<AGOMaterialInspectionTeamDetail?>?,

    @field:SerializedName("msg")
    val msg: String?
)

data class AGOMaterialInspectionTeamDetail(
    @field:SerializedName("nomor_inspeksiretur")
    val nomorInspeksiRetur: String?,

    @field:SerializedName("created_by")
    val createdBy: String?,

    @field:SerializedName("nip")
    val nip: String?,

    @field:SerializedName("nama")
    val nama: String?,

    @field:SerializedName("jabatan")
    val jabatan: String?,

    @field:SerializedName("email")
    val email: String?
)