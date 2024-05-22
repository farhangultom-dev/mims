package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class MonitoringPemeriksaanDetailResponse(

	@field:SerializedName("is_next")
	val isNext: Int? = null,

	@field:SerializedName("pemeriksaanDetail")
	val pemeriksaanDetail: List<PemeriksaanDetailItem?>? = null,

	@field:SerializedName("total_data")
	val totalData: Int? = null,

	@field:SerializedName("total_page")
	val totalPage: Int? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
