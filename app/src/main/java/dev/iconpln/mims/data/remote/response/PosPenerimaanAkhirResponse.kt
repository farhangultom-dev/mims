package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class PosPenerimaanAkhirResponse(

	@field:SerializedName("is_next")
	val isNext: Int? = null,

	@field:SerializedName("data_penerimaan_akhir")
	val dataPenerimaanAkhir: List<PenerimaanAkhirItem?>? = null,

	@field:SerializedName("total_data")
	val totalData: Int? = null,

	@field:SerializedName("total_page")
	val totalPage: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class PenerimaanAkhirItem(

	@field:SerializedName("is_komplained")
	val isKomplained: Boolean? = null,

	@field:SerializedName("nama_pabrikan")
	val namaPabrikan: String? = null,

	@field:SerializedName("no_mat_sap")
	val noMatSap: String? = null,

	@field:SerializedName("kd_pabrikan")
	val kdPabrikan: String? = null,

	@field:SerializedName("is_received")
	val isReceived: Boolean? = null,

	@field:SerializedName("nama_kategori_material")
	val namaKategoriMaterial: String? = null,

	@field:SerializedName("no_packaging")
	val noPackaging: String? = null,

	@field:SerializedName("no_serial")
	val noSerial: String? = null,

	@field:SerializedName("stor_loc")
	val storLoc: String? = null,

	@field:SerializedName("no_do_smar")
	val noDoSmar: String? = null,

	@field:SerializedName("is_rejected")
	val isRejected: Boolean? = null,

	@field:SerializedName("qty_do")
	val qtyDo: Int? = null,

	@field:SerializedName("status")
	val status: String? = null
)
