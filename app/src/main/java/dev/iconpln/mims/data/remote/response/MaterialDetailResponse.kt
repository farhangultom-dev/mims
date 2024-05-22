package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class MaterialDetailResponse(

    @field:SerializedName("datas")
    val datas: List<MaterialDetailDatasItem>,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("total_data")
    val totalData: Int? = 0,

    @field:SerializedName("status")
    val status: String? = null
)

data class MaterialDetailDatasItem(

    @field:SerializedName("nomor_material")
    val nomorMaterial: String? = null,

    @field:SerializedName("material_group")
    val materialGroup: String? = null,

    @field:SerializedName("tahun")
    val tahun: Int? = null,

    @field:SerializedName("tgl_produksi")
    val tglProduksi: String? = null,

    @field:SerializedName("serial_number")
    val serialNumber: String? = null,

    @field:SerializedName("nomor_sert_materologi")
    val nomorSertMaterologi: String? = null,

    @field:SerializedName("spln")
    val spln: String? = null,

    @field:SerializedName("no_produksi")
    val noProduksi: String? = null,

    @field:SerializedName("storloc")
    val storloc: Any? = null,

    @field:SerializedName("nama_kategori_material")
    val namaKategoriMaterial: String? = null,

    @field:SerializedName("no_packaging")
    val noPackaging: String? = null,

    @field:SerializedName("spesifikasi")
    val spesifikasi: String? = null,

    @field:SerializedName("plant")
    val plant: String? = null,

    @field:SerializedName("material_id")
    val materialId: String? = null,

    @field:SerializedName("kode_pabrikan")
    val kodePabrikan: String? = null,

    @field:SerializedName("masa_garansi")
    val masaGaransi: Int? = null,

    @field:SerializedName("status_garansi")
    val statusGaransi: String? = "",

    @field:SerializedName("no_id_meter")
    val noIdMeter: String? = ""
)
