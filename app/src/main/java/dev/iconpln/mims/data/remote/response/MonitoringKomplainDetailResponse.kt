package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class MonitoringKomplainDetailResponse(

	@field:SerializedName("is_next")
	val isNext: Int? = null,

	@field:SerializedName("total_data")
	val totalData: Int? = null,

	@field:SerializedName("total_page")
	val totalPage: Int? = null,

	@field:SerializedName("monitoring_komplain_detail")
	val monitoringKomplainDetail: List<DataMonitoringKomplainDetailItem?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataMonitoringKomplainDetailItem(

	@field:SerializedName("no_mat_sap")
	val noMatSap: String? = null,

	@field:SerializedName("no_do_smar")
	val noDoSmar: String? = null,

	@field:SerializedName("do_line_item")
	val doLineItem: String? = null,

	@field:SerializedName("no_packaging")
	val noPackaging: String? = null,

	@field:SerializedName("tanggal_pengajuan")
	val tanggalPengajuan: String? = null,

	@field:SerializedName("no_serial")
	val noSerial: String? = null,

	@field:SerializedName("no_komplain")
	val noKomplain: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
