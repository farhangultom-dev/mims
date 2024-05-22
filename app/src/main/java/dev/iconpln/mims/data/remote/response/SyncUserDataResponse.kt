package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class SyncUserDataResponse(
    @field:SerializedName("privilege")
    val privilege: List<PrivilegeItem?>? = null,

    @field:SerializedName("message")
    val message: String? = "-",

    @field:SerializedName("scope")
    val scope: String? = null,

    @field:SerializedName("ratings")
    val ratings: List<RatingsItem?>? = null,

    @field:SerializedName("user")
    val user: User? = null,

    @field:SerializedName("status")
    val status: String? = "-",

    @field:SerializedName("pegawai")
    val pegawai: List<PegawaiItem?>? = null,

    @field:SerializedName("pejabat")
    val pejabat: List<DataPejabat?>? = null
)

data class User(

    @field:SerializedName("kd_user")
    val kdUser: String? = "",

    @field:SerializedName("plant")
    val plant: String? = "",

    @field:SerializedName("plant_name")
    val plantName: String? = "",

    @field:SerializedName("stor_loc")
    val storloc: String? = "",

    @field:SerializedName("stor_loc_name")
    val storLocName: String? = "",

    @field:SerializedName("mail")
    val mail: String? = "",

    @field:SerializedName("user_id")
    val userId: Int? = 0,

    @field:SerializedName("role_id")
    val roleId: Int? = 0,

    @field:SerializedName("subrole_id")
    val subroleId: Int? = 0,

    @field:SerializedName("user_name")
    val userName: String? = "",

    @field:SerializedName("full_name")
    val fullName: String? = "",

    @field:SerializedName("role_name")
    val roleName: String? = ""
)

data class PrivilegeItem(

    @field:SerializedName("module_id")
    val moduleId: String? = "",

    @field:SerializedName("is_active")
    val isActive: Int? = 0,

    @field:SerializedName("role_id")
    val roleId: Int? = 0,

    @field:SerializedName("method_id")
    val methodId: String? = "",

    @field:SerializedName("method_value")
    val methodValue: String? = ""
)

data class PegawaiItem(

    @field:SerializedName("is_active")
    val isActive: Boolean? = false,

    @field:SerializedName("nama_pegawai")
    val namaPegawai: String? = "-",

    @field:SerializedName("nip")
    val nip: String? = "-",

    @field:SerializedName("plant")
    val plant: String? = "-",

    @field:SerializedName("kode_jabatan")
    val kodeJabatan: String? = "-",

    @field:SerializedName("nama_jabatan")
    val namaJabatan: String? = "-"
)

data class RatingsItem(

    @field:SerializedName("kd_rating")
    val kdRating: String? = "",

    @field:SerializedName("keterangan")
    val keterangan: String? = "",

    @field:SerializedName("nilai")
    val nilai: Int? = 0,

    @field:SerializedName("type")
    val type: String? = ""

)