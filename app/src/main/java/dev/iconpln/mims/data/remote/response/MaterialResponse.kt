package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class MaterialResponse(

    @field:SerializedName("datas")
    val datas: List<MaterialDatasItem>,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("total_data")
    val totalData: Int? = 0,

    @field:SerializedName("status")
    val status: String? = null
)

data class MaterialDatasItem(

    @field:SerializedName("nomor_material")
    val nomorMaterial: String? = null,

    @field:SerializedName("material_group")
    val materialGroup: String? = null,

    @field:SerializedName("tahun")
    val tahun: Int? = null,

    @field:SerializedName("material_desc")
    val materialDesc: String? = null,

    @field:SerializedName("plant")
    val plant: String? = null,

    @field:SerializedName("material_id")
    val materialId: String? = null,

    @field:SerializedName("nomor_sert_materologi")
    val nomorSertMaterologi: Any? = null,

    @field:SerializedName("kode_pabrikan")
    val kodePabrikan: String? = null,

    @field:SerializedName("no_produksi")
    val noProduksi: String? = null,

    @field:SerializedName("storloc")
    val storloc: String? = null,

    @field:SerializedName("nama_kategori_material")
    val namaKategoriMaterial: String? = null,

    @field:SerializedName("masa_garansi")
    val masaGaransi: Int? = null
)
