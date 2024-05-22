package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class PosSnsResponse(

	@field:SerializedName("is_next")
	val isNext: Int? = null,

	@field:SerializedName("total_data")
	val totalData: Int? = null,

	@field:SerializedName("total_page")
	val totalPage: Int? = null,

	@field:SerializedName("pos_sns")
	val posSns: List<PosSns?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class PosSns(

	@field:SerializedName("nama_pabrikan")
	val namaPabrikan: String? = "",

	@field:SerializedName("no_mat_sap")
	val noMatSap: String? = null,

	@field:SerializedName("status_penerimaan")
	val statusPenerimaan: String? = null,

	@field:SerializedName("material_group")
	val materialGroup: String? = null,

	@field:SerializedName("tgl_produksi")
	val tglProduksi: String? = null,

	@field:SerializedName("kd_pabrikan")
	val kdPabrikan: String? = null,

	@field:SerializedName("nomor_sert_materologi")
	val nomorSertMaterologi: String? = null,

	@field:SerializedName("spln")
	val spln: String? = null,

	@field:SerializedName("no_produksi")
	val noProduksi: String? = null,

	@field:SerializedName("storloc")
	val storloc: String? = null,

	@field:SerializedName("nama_kategori_material")
	val namaKategoriMaterial: String? = null,

	@field:SerializedName("no_packaging")
	val noPackaging: String? = null,

	@field:SerializedName("no_serial")
	val noSerial: String? = null,

	@field:SerializedName("status_pemeriksaan")
	val statusPemeriksaan: String? = null,

	@field:SerializedName("no_do_smar")
	val noDoSmar: String? = null,

	@field:SerializedName("do_line_item")
	val doLineItem: String? = null,

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

	@field:SerializedName("do_status")
	val doStatus: String? = null
)
