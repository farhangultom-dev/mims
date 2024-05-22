package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class GetDataStorlocResponse(

    @field:SerializedName("data")
    val data: DataValuationType,

    @field:SerializedName("timestamp")
    val timestamp: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class DataValuationType(

    @field:SerializedName("stor_loc")
    val storLoc: List<StorLocItem>,

    @field:SerializedName("valuation_type")
    val valuationType: List<ValuationTypeItem?>? = null
)

data class ValuationTypeItem(

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("valuation_type")
    val valuationType: String? = null
)

data class StorLocItem(

    @field:SerializedName("stor_loc")
    val storLoc: String? = null,

    @field:SerializedName("stor_loc_name")
    val storLocName: String? = null
)
