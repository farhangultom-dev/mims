package dev.iconpln.mims.data.remote.service

import com.google.gson.JsonObject
import dev.iconpln.mims.data.remote.response.AGOATTBData
import dev.iconpln.mims.data.remote.response.AGOATTBDataBasedOnRefNum
import dev.iconpln.mims.data.remote.response.AGOCreateMaterialInspection
import dev.iconpln.mims.data.remote.response.AGOCreateMaterialInspectionBody
import dev.iconpln.mims.data.remote.response.AGOGInspectionMaterialReturnData
import dev.iconpln.mims.data.remote.response.AGOGetMasterClassification
import dev.iconpln.mims.data.remote.response.AGOInspectionReturnTeamData
import dev.iconpln.mims.data.remote.response.AGOManufacturer
import dev.iconpln.mims.data.remote.response.AGOMasterBudgetStatus
import dev.iconpln.mims.data.remote.response.AGOMasterTimData
import dev.iconpln.mims.data.remote.response.AGOMaterialInspectionData
import dev.iconpln.mims.data.remote.response.AGOReferenceNumber
import dev.iconpln.mims.data.remote.response.AGOStorLoc
import dev.iconpln.mims.data.remote.response.AGOUpdateInspectionReturnTeam
import dev.iconpln.mims.data.remote.response.AGOUpdateInspectionReturnTeamDataBody
import dev.iconpln.mims.data.remote.response.AGOUpdateMaterialInspectionBySN
import dev.iconpln.mims.data.remote.response.AGOUpdateMaterialInspectionBySNBody
import dev.iconpln.mims.data.remote.response.AddSerialNumberUlpAp2tResponse
import dev.iconpln.mims.data.remote.response.AddSerialNumberUlpResponse
import dev.iconpln.mims.data.remote.response.AktivasiSerialNumberResponse
import dev.iconpln.mims.data.remote.response.CheckSerialNumberResponse
import dev.iconpln.mims.data.remote.response.DashboardResponse
import dev.iconpln.mims.data.remote.response.DataMaterialAp2tResponse
import dev.iconpln.mims.data.remote.response.DeleteSnPemakaianResponse
import dev.iconpln.mims.data.remote.response.DocumentationResponse
import dev.iconpln.mims.data.remote.response.DokumentasiRatingResponse
import dev.iconpln.mims.data.remote.response.GenericResponse
import dev.iconpln.mims.data.remote.response.GetDataStorlocResponse
import dev.iconpln.mims.data.remote.response.GetMaterialAktivasiResponse
import dev.iconpln.mims.data.remote.response.GetMaterialRegistrasiDetailByDateResponse
import dev.iconpln.mims.data.remote.response.GetNoMaterialMonStokResponse
import dev.iconpln.mims.data.remote.response.GetNomorMaterialForAktivasiResponse
import dev.iconpln.mims.data.remote.response.GetPejabatResponse
import dev.iconpln.mims.data.remote.response.GetPemakaianMaterialResponse
import dev.iconpln.mims.data.remote.response.GetResultSapResponse
import dev.iconpln.mims.data.remote.response.GetStockMonStokResponse
import dev.iconpln.mims.data.remote.response.InsertMaterialRegistrasiResponse
import dev.iconpln.mims.data.remote.response.LoginResponse
import dev.iconpln.mims.data.remote.response.LokasiResponse
import dev.iconpln.mims.data.remote.response.MaterialDetailResponse
import dev.iconpln.mims.data.remote.response.MaterialResponse
import dev.iconpln.mims.data.remote.response.MonitoringAktivasiMaterialResponse
import dev.iconpln.mims.data.remote.response.MonitoringKomplainDetailResponse
import dev.iconpln.mims.data.remote.response.MonitoringPemeriksaanDetailResponse
import dev.iconpln.mims.data.remote.response.MonitoringPermintaanDetailResponse
import dev.iconpln.mims.data.remote.response.NewRegistrasiSnResponse
import dev.iconpln.mims.data.remote.response.PemakaianDetailResponse
import dev.iconpln.mims.data.remote.response.PemakaianMaterialAp2tResponse
import dev.iconpln.mims.data.remote.response.PemakaianUlpAp2tResponse
import dev.iconpln.mims.data.remote.response.PosPenerimaanAkhirResponse
import dev.iconpln.mims.data.remote.response.PosSnsResponse
import dev.iconpln.mims.data.remote.response.RequestBodyAktivMaterial
import dev.iconpln.mims.data.remote.response.RequestBodyCheckSerialNumber
import dev.iconpln.mims.data.remote.response.RequestBodyRegisSn
import dev.iconpln.mims.data.remote.response.SnResponse
import dev.iconpln.mims.data.remote.response.SyncDataResponse
import dev.iconpln.mims.data.remote.response.SyncUserDataResponse
import dev.iconpln.mims.data.remote.response.TrackingHistoryResponse
import dev.iconpln.mims.data.remote.response.UpdateDetailPemakaianResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ApiService {

    @Headers("Content-Type:application/json")
    @POST("/mobile/users/login")
    suspend fun login(
        @Body body: Map<String, String>
    ): Response<LoginResponse>

    @Headers("Content-Type:application/json")
    @POST("/mobile/users/logout")
    suspend fun logout(): Response<GenericResponse>

    @Headers("Content-Type:application/json")
    @GET("/mobile/users/syncUserData")
    suspend fun syncUserData(
        @Query("username") username: String,
    ): Response<SyncUserDataResponse>

    @Headers("Content-Type:application/json")
    @Streaming
    @POST("/mobile/users/syncData")
    suspend fun syncData(
        @Body body: Map<String, String>
    ): Response<SyncDataResponse>

    @Headers("Content-Type:application/json")
    @POST("/mobile/users/loginSso")
    suspend fun loginSso(
        @Body body: Map<String, String>
    ): Response<LoginResponse>

    @Headers("Content-Type:application/json")
    @POST("/mobile/users/sentOtp")
    suspend fun sendOtp(
        @Body body: Map<String, String>
    ): Response<GenericResponse>

    @Headers("Content-Type:application/json")
    @POST("/mobile/users/getOtpForgotPassword")
    suspend fun getOtpForgotPassword(
        @Body body: Map<String, String>
    ): Response<GenericResponse>

    @Headers("Content-Type:application/json")
    @POST("/mobile/users/isOtpValid")
    suspend fun otpValid(
        @Body body: Map<String, String>
    ): Response<LoginResponse>

    @Headers("Content-Type:application/json")
    @POST("/mobile/users/isOtpValidRp")
    suspend fun otpValidForgotPassword(
        @Body body: Map<String, String>
    ): Response<GenericResponse>

    @Headers("Content-Type:application/json")
    @POST("/mobile/users/doForgotPassword")
    suspend fun forgotPassword(
        @Body body: Map<String, String>
    ): Response<GenericResponse>

    @Headers("Content-Type:application/json")
    @POST("/mobile/users/doChangePassword")
    suspend fun changePasswordProfile(
        @Body body: Map<String, String>
    ): Response<GenericResponse>

    @FormUrlEncoded
    @POST("/mobile/tracking/getTrackingHistory")
    suspend fun getTrackingHistory(
        @Field("sn") sn: String
    ): Response<TrackingHistoryResponse>

    @FormUrlEncoded
    @POST("/mobile/tracking/getTrackingHistoryDetail2")
    suspend fun getDetailTrackingHistory(
        @Field("sn") sn: String,
        @Field("no_transaksi") noTransaksi: String,
        @Field("status") status: String
    ): Response<JsonObject>

    @FormUrlEncoded
    @POST("/mobile/reports/pemakaian/addSn")
    suspend fun addSn(
        @Field("no_transaksi") noTransaksi: String,
        @Field("no_material") noMaterial: String,
        @Field("serial_number") serialNumber: String,
        @Field("user_plant") userPlant: String,
        @Field("user_loc") userLoc: String,
        @Field("user_name") username: String,
        @Field("valuation_type") valuationType: String
    ): Response<SnResponse>

    @FormUrlEncoded
    @POST("/mobile/reports/pemakaian/deleteSn")
    suspend fun deleteSn(
        @Field("no_transaksi") noTransaksi: String,
        @Field("no_material") noMaterial: String,
        @Field("serial_number") serialNumber: String,
        @Field("user_plant") userPlant: String,
        @Field("user_loc") userLoc: String
    ): Response<SnResponse>

    @FormUrlEncoded
    @POST("/mobile/reports/permintaan/addSn")
    suspend fun permintaanAddSn(
        @Field("no_repackaging") noRepackaging: String,
        @Field("no_material") noMaterial: String,
        @Field("kode_scan") kodeScan: String,
        @Field("user_plant") userPlant: String,
        @Field("user_loc") userLoc: String,
        @Field("role_id") roleId: Int,
        @Field("user_name") username: String,
        @Field("kode_gerak") kodeGerak: String,
        @Field("valuation_type") valuationType: String,
        @Field("no_id_meter") noIdMeter: String
    ): Response<SnResponse>

    @FormUrlEncoded
    @POST("/mobile/reports/permintaan/deleteSn")
    suspend fun permintaanDeleteSn(
        @Field("no_repackaging") noRepackaging: String,
        @Field("no_material") noMaterial: String,
        @Field("kode_scan") kodeScan: String,
        @Field("role_id") roleId: Int,
        @Field("user_plant") userPlant: String,
        @Field("user_loc") userLoc: String,
        @Field("user_name") username: String,
        @Field("kode_gerak") kodeGerak: String,
        @Field("valuation_type") valuationType: String
    ): Response<SnResponse>

    @FormUrlEncoded
    @POST("/mobile/reports/permintaan/deleteAllSn")
    suspend fun permintaanDeleteAllSn(
        @Field("no_repackaging") noRepackaging: String,
        @Field("no_material") noMaterial: String,
        @Field("user_plant") userPlant: String,
        @Field("user_loc") userLoc: String,
        @Field("user_name") username: String
    ): Response<GenericResponse>

    @FormUrlEncoded
    @POST("/mobile/reports/insertLokasi")
    suspend fun sendLokasi(
        @Field("do_mims") doMims: String,
        @Field("lokasi") lokasi: String,
        @Field("no_po") no_po: String,
        @Field("no_do_smar") noDoSmar: String
    ): Response<GenericResponse>

    @FormUrlEncoded
    @POST("/mobile/reports/deleteLokasi")
    suspend fun deleteLokasi(
        @Field("id_location") idLocation: String,
        @Field("no_do") noDo: String
    ): Response<GenericResponse>

    @FormUrlEncoded
    @POST("/mobile/reports/getLokasiByDoMims")
    suspend fun getLokasi(
        @Field("do_mims") doMims: String
    ): Response<LokasiResponse>

    @FormUrlEncoded
    @POST("/mobile/penerimaan/getDokumentasiPenerimaan")
    suspend fun getDokumentasi(
        @Field("no_do") noDo: String
    ): Response<DocumentationResponse>

    @FormUrlEncoded
    @POST("/mobile/penerimaan/getDokumentasiRating")
    suspend fun getDokumentasiRating(
        @Field("no_rating") noRating: String
    ): Response<DokumentasiRatingResponse>

    @GET("/aktivasimaterial/getMonitoringAktivasiMaterial")
    suspend fun getMonitoringAktivasiMaterial(
        @Query("status") status: String,
        @Query("filter") sn: String,
        @Query("type") type: String
    ): Response<MonitoringAktivasiMaterialResponse>

    @POST("/aktivasimaterial/insertMaterialRegistrasi")
    suspend fun insertMaterialRegistrasi(
        @Body requestBody: RequestBodyRegisSn
    ): Response<InsertMaterialRegistrasiResponse>

    @GET("/aktivasimaterial/getMaterialAktivasi")
    suspend fun getMaterialAktivasi(
        @Query("status") status: String
    ): Response<GetMaterialAktivasiResponse>

    @POST("/aktivasimaterial/aktivasiSerialNumber")
    suspend fun aktivasiMaterial(
        @Body requestBody: RequestBodyAktivMaterial
    ): Response<AktivasiSerialNumberResponse>

    @GET("/aktivasimaterial/getMaterialRegistrasiDetailByDate")
    suspend fun getMaterialRegistrasiDetailByDate(
        @Query("tgl_registrasi") tgl_registrasi: String,
        @Query("status") status: String,
        @Query("filter") sn: String,
        @Query("nomor_material") no_material: String
    ): Response<GetMaterialRegistrasiDetailByDateResponse>

    @GET("/aktivasimaterial/getNomorMaterialForAktivasi")
    suspend fun getNomorMaterialForAktivasi(): Response<GetNomorMaterialForAktivasiResponse>

    @GET("/pemakaian/getPemakaianAp2t")
    suspend fun getPemakaianMaterialAp2t(
        @Query("no_transaksi") noTransaksi: String,
        @Query("id_pelanggan") idPelanggan: String
    ): Response<PemakaianUlpAp2tResponse>

    @GET("/pemakaian/getPemakaianMaterialAp2t")
    suspend fun getPemakaianMaterialAp2t(
        @Query("no_transaksi") noTransaksi: String,
        @Query("no_agenda") noAgenda: String,
        @Query("serial_number") serialNumber: String
    ): Response<PemakaianMaterialAp2tResponse>

    @GET("/pemakaian/getPemakaianDetail")
    suspend fun getDetailMaterialAp2t(
        @Query("no_transaksi") noTransaksi: String,
        @Query("nomor_material") nomorMaterial: String
    ): Response<DataMaterialAp2tResponse>

    @GET("/pemakaian/getPemakaianMaterial")
    suspend fun getPemakaianMaterial(
        @Query("no_transaksi") noTransaksi: String,
        @Query("nomor_material") nomorMaterial: String,
        @Query("serial_number") serialNumber: String,
        @Query("valuation_type") valuationType: String
    ): Response<GetPemakaianMaterialResponse>

    @POST("/pemakaian/addSerialNumberUlpAp2t")
    suspend fun addSerialNumberUlpAp2t(
        @Body body: Map<String, String>
    ): Response<AddSerialNumberUlpAp2tResponse>

    @POST("/pemakaian/addSerialNumberUlp")
    suspend fun addSerialNumberUlp(
        @Body body: Map<String, String>
    ): Response<AddSerialNumberUlpResponse>

    @GET("/pemakaian/getPejabat")
    suspend fun getPejabat(
        @Query("plant") plant: String,
        @Query("stor_loc") storLoc: String,
        @Query("kd_pejabat") kdPejabat: String
    ): Response<GetPejabatResponse>

    @POST("/pemakaian/updateDetailMandiri")
    suspend fun updateDetailPemakaian(
        @Body body: Map<String, String>
    ): Response<UpdateDetailPemakaianResponse>

    @GET("/mobile/material/getAllMaterial")
    suspend fun getAllMaterial(
        @Query("kode_pabrikan") kdPabrikan: String,
        @Query("tahun") tahun: String,
        @Query("kategori") kategori: String,
        @Query("tanggal_awal") tanggalAwal: String,
        @Query("tanggal_akhir") tanggalAkhir: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("nomor_produksi_material") nomorProduksiMaterial: String
    ): Response<MaterialResponse>

    @GET("/mobile/material/getAllMaterialDetail")
    suspend fun getAllMaterialDetail(
        @Query("kode_pabrikan") kdPabrikan: String,
        @Query("nomor_produksi") nomorProduksi: String,
        @Query("nomor_material") nomorMaterial: String,
        @Query("serial_number") serialNumber: String,
        @Query("limit") limit: String,
        @Query("page") page: String,
        @Query("tahun") tahun: Int
    ): Response<MaterialDetailResponse>

    @GET("/mobile/material/getAllMaterialDetailWithPaging")
    suspend fun getAllMaterialDetailPaging(
        @Query("kode_pabrikan") kdPabrikan: String,
        @Query("nomor_produksi") nomorProduksi: String,
        @Query("nomor_material") nomorMaterial: String,
        @Query("serial_number") serialNumber: String,
        @Query("limit") limit: String,
        @Query("page") page: String,
        @Query("tahun") tahun: Int
    ): Response<MaterialDetailResponse>

    @GET("/mobile/users/getDashboard")
    suspend fun getDashboard(
        @Query("kode_pabrikan") kdPabrikan: String,
        @Query("limit") limit: String,
        @Query("page") page: String,
    ): Response<DashboardResponse>

    @GET("/mobile/users/getPemakaianDetail")
    suspend fun getPemakaianDetail(
        @Query("no_transaksi") noTransaksi: String,
        @Query("nomor_material") nomorMaterial: String,
    ): Response<PemakaianDetailResponse>

    @POST("/pemakaian/deleteSerialNumberUlpAp2t")
    suspend fun deleteSnMaterial(
        @Body body: Map<String, String>
    ): Response<DeleteSnPemakaianResponse>

    @GET("/pemakaian/getResultSapPemakaian")
    suspend fun getSap(
        @Query("no_pemakaian") noPemakaian: String,
    ): Response<GetResultSapResponse>

    @GET("/monitoringstock/getData")
    suspend fun getStorloc(
        @Query("plant") plant: String,
    ): Response<GetDataStorlocResponse>

    @GET("/monitoringstock/getStock")
    suspend fun getStockMonStok(
        @Query("plant") plant: String,
        @Query("valuation_type") valuationType: String,
        @Query("stor_loc") storLoc: String,
        @Query("nomor_material") material: String,
    ): Response<GetStockMonStokResponse>

    @GET("/monitoringstock/getNomorMaterial")
    suspend fun getNoMaterialMonStok(
        @Query("plant") plant: String,
        @Query("valuation_type") valuationType: String,
        @Query("stor_loc") storLoc: String,
    ): Response<GetNoMaterialMonStokResponse>

    @GET("/aktivasimaterial/checkSerialNumber")
    suspend fun checkSerialNumber(
        @Query("plant_code") plantCode: String,
        @Query("stor_loc") storLoc: String,
        @Body requestBody: RequestBodyCheckSerialNumber
    ): Response<CheckSerialNumberResponse>

    @GET("/aktivasimaterial/getMonitoringAktivasiMaterialWeb")
    suspend fun getMonitoringAktivasiMaterialWeb(
        @Query("vendor") vendor: String,
        @Query("start_date") starDate: String,
        @Query("end_date") endDate: String,
        @Query("status") status: String,
        @Query("type") type: String,
        @Query("plant_code") plantCode: String,
        @Query("stor_lok") storloc: String,
        @Query("page_in") pageIn: Int,
        @Query("page_size") pageSize: Int,
        @Query("sorted") sorted: String,
    ): Response<NewRegistrasiSnResponse>
    // --- AGO ENDPOINTS --- //
    @Headers("Content-Type:application/json")
    @GET("${ApiConfig.AGO_ENDPOINT}getInspeksiMobile/{sn}")
    suspend fun getMaterialInspectionBySN(
        @Path("sn") sn: String
    ): Response<AGOMaterialInspectionData>

    @Headers("Content-Type:application/json")
    @POST("${ApiConfig.AGO_ENDPOINT}updateInspeksiMobile/")
    suspend fun updateMaterialInspectionBySN(
        @Body body: AGOUpdateMaterialInspectionBySNBody
    ): Response<AGOUpdateMaterialInspectionBySN>

    @Headers("Content-Type:application/json")
    @POST("${ApiConfig.AGO_ENDPOINT}setInspeksiMaterialRetur/")
    suspend fun createMaterialInspection(
        @Body body : AGOCreateMaterialInspectionBody
    ): Response<AGOCreateMaterialInspection>

    @Headers("Content-Type:application/json")
    @GET("${ApiConfig.AGO_ENDPOINT}getMasterKlasifikasiRetur")
    suspend fun getMasterClassification(): Response<AGOGetMasterClassification>

    @Headers("Content-Type:application/json")
    @GET("${ApiConfig.AGO_ENDPOINT}getDataInspreturTim/{no_inspeksi}/")
    suspend fun getDataInspectionReturnTeam(
        @Path("no_inspeksi") no_inspeksi: String
    ): Response<AGOInspectionReturnTeamData>

    @Headers("Content-Type:application/json")
    @GET("${ApiConfig.AGO_ENDPOINT}getMasterTim/{plant}&{stor_loc}/")
    suspend fun getMasterTeam(
        @Path("plant") plant: String,
        @Path("stor_loc") storLoc: String
    ): Response<AGOMasterTimData>

    @Headers("Content-Type:application/json")
    @GET("${ApiConfig.AGO_ENDPOINT}getDataInspretur/{plant}&{stor_loc}&{tanggal}&{nomor_inspeksiretur}&{material_no}&{status_inspeksi}/")
    suspend fun getDataInspectionReturn(
        @Path("plant") plant: String?,
        @Path("stor_loc") stor_loc: String?,
        @Path("tanggal") tanggal: String?,
        @Path("nomor_inspeksiretur") nomor_inspeksiretur: String?,
        @Path("material_no") material_no: String?,
        @Path("status_inspeksi") status_inspeksi: String?
    ): Response<AGOGInspectionMaterialReturnData>

    @Headers("Content-Type:application/json")
    @POST("${ApiConfig.AGO_ENDPOINT}updateInspeksiMaterialReturTim/")
    suspend fun updateInspectionTeam(
        @Body body : AGOUpdateInspectionReturnTeamDataBody
    ): Response<AGOUpdateInspectionReturnTeam>

    @Headers("Content-Type:application/json")
    @GET("${ApiConfig.AGO_ENDPOINT}getAttb/{plant}&{storLoc}/")
    suspend fun getATTB(
        @Path("plant") plant: String,
        @Path("storLoc") storLoc: String
    ): Response<AGOATTBData>

    @Headers("Content-Type:application/json")
    @GET("${ApiConfig.AGO_ENDPOINT}getAttbReferensi/{plant}&{stor_loc}&{no_referensi}/")
    suspend fun getATTBWithRefNum(
        @Path("plant") plant: String,
        @Path("stor_loc") stor_loc: String,
        @Path("no_referensi") no_referensi: String
    ): Response<AGOATTBDataBasedOnRefNum>

    @Headers("Content-Type:application/json")
    @GET("${ApiConfig.AGO_ENDPOINT}func_getstorloc_bylogin/{plant}&{roleId}/")
    suspend fun getStorLoc(
        @Path("plant") plant: String,
        @Path("roleId") roleId: String
    ): Response<AGOStorLoc>

    @Headers("Content-Type:application/json")
    @GET("${ApiConfig.AGO_ENDPOINT}getPabrikan/{no_material}")
    suspend fun getManufacturer(
        @Path("no_material") no_material: String
    ): Response<AGOManufacturer>

    @Headers("Content-Type:application/json")
    @GET("${ApiConfig.AGO_ENDPOINT}getMasterStatusAnggaran")
    suspend fun getMasterBudgetStatus(): Response<AGOMasterBudgetStatus>

    @Headers("Content-Type:application/json")
    @GET("${ApiConfig.AGO_ENDPOINT}getNomorReferensiAuto/{plant}&{stor_loc}&{no_referensi}")
    suspend fun getNoReferensiAuto(
        @Path("plant") plant: String,
        @Path("stor_loc") stor_loc: String,
        @Path("no_referensi") no_referensi: String
    ): Response<AGOReferenceNumber>

    @Headers("Content-Type:application/json")
    @GET("/mobile/users/getPoSnsByNoDosByPagenation")
    suspend fun getPosSns(
        @Query("noDos") noDo: String,
        @Query("page") page: String,
        @Query("limit") limit: String
    ): Response<PosSnsResponse>

    @Headers("Content-Type:application/json")
    @GET("/mobile/users/getDataPenerimaanAkhirByNoDosByStorlocByPagenation")
    suspend fun getPosSnsPenerimaanAkhir(
        @Query("noDos") noDo: String,
        @Query("stor_loc") storLoc: String,
        @Query("page") page: String,
        @Query("limit") limit: String
    ): Response<PosPenerimaanAkhirResponse>

    @Headers("Content-Type:application/json")
    @GET("/mobile/users/getDataMonitoringKomplainDetailByNoKomplainByPagenation")
    suspend fun getMonitoringKomplainDetail(
        @Query("no_komplain") noKomplain: String,
        @Query("page") page: String,
        @Query("limit") limit: String
    ): Response<MonitoringKomplainDetailResponse>

    @Headers("Content-Type:application/json")
    @GET("/mobile/users/getDataMonitoringPermintaanDetailByNoTransaksiByPlantByPagenation")
    suspend fun getMonitoringPermintaanDetail(
        @Query("no_transaksi") noTransaksi: String,
        @Query("plant") plant: String,
        @Query("page") page: String,
        @Query("limit") limit: String
    ): Response<MonitoringPermintaanDetailResponse>

    @Headers("Content-Type:application/json")
    @GET("/mobile/users/getDataPemeriksaanDetailByNoPemeriksaanByPagenation")
    suspend fun getMonitoringPemeriksaanDetail(
        @Query("no_pemeriksaan") noPemeriksaan: String,
        @Query("page") page: String,
        @Query("limit") limit: String
    ): Response<MonitoringPemeriksaanDetailResponse>
}