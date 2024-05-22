package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class SyncDataResponse(

    @field:SerializedName("pemeriksaanDetail")
    val pemeriksaanDetail: List<PemeriksaanDetailItem?>? = null,

    @field:SerializedName("pemeriksan")
    val pemeriksan: List<PemeriksanItem?>? = null,

    @field:SerializedName("pos_sns")
    val posSns: List<PosSnsItem?>? = null,

    @field:SerializedName("message")
    val message: String? = "-",

    @field:SerializedName("token")
    val token: String? = "-",

    @field:SerializedName("id_token_sso")
    val idTokenSso: String? = "-",

    @field:SerializedName("webtoken")
    val webToken: String? = "-",

    @field:SerializedName("material_groups")
    val materialGroups: List<MaterialGroupsItem?>? = null,

    @field:SerializedName("pos_detail")
    val posDetail: List<PosDetailItem?>? = null,

    @field:SerializedName("pengujians")
    val pengujians: List<PengujiansItem?>? = null,

    @field:SerializedName("pos")
    val pos: List<PosItem?>? = null,

    @field:SerializedName("pengujian_details")
    val pengujianDetails: List<PengujianDetailsItem?>? = null,

    @field:SerializedName("materials")
    val materials: List<MaterialsItem?>? = null,

    @field:SerializedName("scope")
    val scope: String? = null,

    @field:SerializedName("lokasis")
    val lokasis: List<LokasisItem?>? = null,

    @field:SerializedName("material_details")
    val materialDetails: List<MaterialDetailsItem?>? = null,

    @field:SerializedName("dataRatings")
    val dataRatings: List<DataRatingsItem?>? = null,

    @field:SerializedName("status")
    val status: String? = "-",

    @field:SerializedName("monitoring_permintaan")
    val monitoringPermintaan: List<MonitoringPermintaanItem?>? = null,

    @field:SerializedName("monitoring_permintaan_details")
    val monitoringPermintaanDetails: List<MonitoringPermintaanDetailsItem?>? = null,

    @field:SerializedName("sn_permaterial")
    val snPermaterial: List<SnPermaterialItem?>? = null,

    @field:SerializedName("sn_permintaan")
    val snPermintaan: List<SnPermintaanItem?>? = null,

    @field:SerializedName("penerimaanUlp")
    val penerimaanUlp: List<PenerimaanUlpItem?>? = null,

    @field:SerializedName("penerimaanDetailUlp")
    val penerimaanDetailUlp: List<PenerimaanDetailUlpItem?>? = null,

    @field:SerializedName("pemakaian")
    val pemakaian: List<PemakaianItem?>? = null,

    @field:SerializedName("pemakaianDetail")
    val pemakaianDetail: List<PemakaianDetailItem?>? = null,

    @field:SerializedName("snPemakaianUlp")
    val snPemakaianUlp: List<SnPemakaianUlpItem?>? = null,

    @field:SerializedName("snPenerimaanUlp")
    val snPenerimaanUlp: List<SnPenerimaanUlpItem?>? = null,

    @field:SerializedName("petugas_pengujian")
    val petugasPengujian: List<PetugasPengujianItem?>? = null,

    @field:SerializedName("monitoring_komplain")
    val monitoringKomplain: List<MonitoringKomplainItem?>? = null,

    @field:SerializedName("monitoring_komplain_detail")
    val monitoringKomplainDetail: List<MonitoringKomplainDetailItem?>? = null,

    @field:SerializedName("data_penerimaan_akhir")
    val dataPenerimaanAkhir: List<DataPenerimaanAkhirItem?>? = null,

    @field:SerializedName("master_do_status")
    val masterDoStatus: List<DataMasterStatusDo?>? = null,

    @field:SerializedName("pejabat")
    val pejabat: List<DataPejabat?>? = null,

    @field:SerializedName("hasData")
    val hasData: Boolean = true
)

data class PosDetailItem(

    @field:SerializedName("no_mat_sap")
    val noMatSap: String? = "",

    @field:SerializedName("kd_pabrikan")
    val kdPabrikan: String? = "",

    @field:SerializedName("no_do_mims")
    val noDoMims: String? = "",

    @field:SerializedName("no_packaging")
    val noPackaging: String? = "",

    @field:SerializedName("plant_code_no")
    val plantCodeNo: String? = "",

    @field:SerializedName("stor_loc")
    val storLoc: String? = "",

    @field:SerializedName("uom")
    val uom: String? = "",

    @field:SerializedName("no_pemeriksaan")
    val noPemeriksaan: String? = "",

    @field:SerializedName("po_sap_no")
    val poSapNo: String? = "",

    @field:SerializedName("po_mp_no")
    val poMpNo: String? = "",

    @field:SerializedName("no_do_smar")
    val noDoSmar: String? = "",

    @field:SerializedName("qty")
    val qty: String? = "",

    @field:SerializedName("lead_time")
    val leadTime: Int? = 0,

    @field:SerializedName("created_date")
    val createdDate: String? = "",

    @field:SerializedName("do_status")
    val doStatus: String? = "",

    @field:SerializedName("plant_name")
    val plantName: String? = "",

    @field:SerializedName("nama_pabrikan")
    val namaPabrikan: String? = ""
)

data class PengujianDetailsItem(

    @field:SerializedName("keterangan_material")
    val keteranganMaterial: String? = "-",

    @field:SerializedName("serial_number")
    val serialNumber: String? = "-",

    @field:SerializedName("status_uji")
    val statusUji: String? = "-",

    @field:SerializedName("no_pengujian")
    val noPengujian: String? = "-",

    @field:SerializedName("nama_kategori")
    val namaKategori: String? = "-"
)

data class PosSnsItem(

    @field:SerializedName("no_mat_sap")
    val noMatSap: String? = "",

    @field:SerializedName("mmc")
    val mmc: String? = "",

    @field:SerializedName("material_group")
    val materialGroup: String? = "",

    @field:SerializedName("tgl_produksi")
    val tglProduksi: String? = "",

    @field:SerializedName("kd_pabrikan")
    val kdPabrikan: String? = "",

    @field:SerializedName("nomor_sert_materologi")
    val nomorSertMaterologi: String? = "",

    @field:SerializedName("spln")
    val spln: String? = "",

    @field:SerializedName("no_produksi")
    val noProduksi: String? = "",

    @field:SerializedName("storloc")
    val storloc: String? = "",

    @field:SerializedName("nama_kategori_material")
    val namaKategoriMaterial: String? = "",

    @field:SerializedName("no_packaging")
    val noPackaging: String? = "",

    @field:SerializedName("no_serial")
    val noSerial: String? = "",

    @field:SerializedName("no_do_smar")
    val noDoSmar: String? = "",

    @field:SerializedName("spesifikasi")
    val spesifikasi: String? = "",

    @field:SerializedName("plant")
    val plant: String? = "",

    @field:SerializedName("material_id")
    val materialId: String? = "",

    @field:SerializedName("masa_garansi")
    val masaGaransi: String? = "",

    @field:SerializedName("do_status")
    val doStatus: String? = "",

    @field:SerializedName("do_line_item")
    val doLineItem: String? = "",

    @field:SerializedName("status_penerimaan")
    val statusPenerimaan: String? = "",

    @field:SerializedName("status_pemeriksaan")
    val statusPemeriksaan: String? = "",

    @field:SerializedName("nama_pabrikan")
    val namaPabrikan: String? = ""
)

data class MaterialsItem(

    @field:SerializedName("nomor_material")
    val nomorMaterial: String? = "",

    @field:SerializedName("mmc")
    val mmc: String? = "",

    @field:SerializedName("material_group")
    val materialGroup: String? = "",

    @field:SerializedName("tahun")
    val tahun: Int? = 0,

    @field:SerializedName("tgl_produksi")
    val tglProduksi: String? = "",

    @field:SerializedName("kd_pabrikan")
    val kdPabrikan: String? = "",

    @field:SerializedName("nomor_sert_materologi")
    val nomorSertMaterologi: String? = "",

    @field:SerializedName("no_produksi")
    val noProduksi: String? = "",

    @field:SerializedName("storloc")
    val storloc: String? = "",

    @field:SerializedName("nama_kategori_material")
    val namaKategoriMaterial: String? = "",

    @field:SerializedName("plant")
    val plant: String? = "",

    @field:SerializedName("material_id")
    val materialId: String? = "",

    @field:SerializedName("masa_garansi")
    val masaGaransi: String? = ""
)

data class PosItem(

    @field:SerializedName("material_group")
    val materialGroup: String? = "",

    @field:SerializedName("kd_pabrikan")
    val kdPabrikan: String? = "",

    @field:SerializedName("no_do_mims")
    val noDoMims: String? = "",

    @field:SerializedName("nama_kategori_material")
    val namaKategoriMaterial: String? = "",

    @field:SerializedName("plant_code_no")
    val plantCodeNo: String? = "",

    @field:SerializedName("stor_loc")
    val storLoc: String? = "",

    @field:SerializedName("total")
    val total: String? = "",

    @field:SerializedName("tlsk_no")
    val tlskNo: String? = "",

    @field:SerializedName("po_sap_no")
    val poSapNo: String? = "",

    @field:SerializedName("po_mp_no")
    val poMpNo: String? = "",

    @field:SerializedName("no_do_smar")
    val noDoSmar: String? = "",

    @field:SerializedName("lead_time")
    val leadTime: Int? = 0,

    @field:SerializedName("created_date")
    val createdDate: String? = "",

    @field:SerializedName("po_date")
    val poDate: String? = "",

    @field:SerializedName("courier_person_name")
    val courierPersonName: String? = "",

    @field:SerializedName("do_status")
    val doStatus: String? = "",

    @field:SerializedName("ekspedition")
    val ekspedition: String? = "",

    @field:SerializedName("kode_status_do_mims")
    val kodeStatusDoMims: String? = "",

    @field:SerializedName("do_line_item")
    val DoLineItem: String? = "",

    @field:SerializedName("petugas_penerima")
    val PetugasPenerima: String? = "",

    @field:SerializedName("tgl_serah_terima")
    val TglSerahTerima: String? = "",

    @field:SerializedName("kurir_pengirim")
    val KurirPengirim: String? = "",

    @field:SerializedName("plant_name")
    val plantName: String? = "",

    @field:SerializedName("rating_delivery")
    val ratingDelivery: String? = "",

    @field:SerializedName("rating_response")
    val ratingResponse: String? = "",

    @field:SerializedName("eta")
    val eta: String? = "",

    @field:SerializedName("etd")
    val etd: String? = "",

    @field:SerializedName("rating_quality")
    val ratingQuality: String? = "",

    @field:SerializedName("status_pemeriksaan")
    val statusPemeriksaan: String? = "",

    @field:SerializedName("status_penerimaan")
    val statusPenerimaan: String? = "",

    @field:SerializedName("sudah_bisa_rating")
    val sudahBisaRating: Boolean,

    @field:SerializedName("isbabg")
    val isBabg: Boolean,

    @field:SerializedName("isbabgconfirm")
    val isBabgConfirm: Boolean,

    @field:SerializedName("sla_integrasi_sap")
    val slaIntegrasiSap: Boolean,

    @field:SerializedName("pallete_color_background")
    val palleteBackground: String,

    @field:SerializedName("pallete_color_text")
    val palleteText: String,

    @field:SerializedName("nama_pabrikan")
    val namaPabrikan: String,

    )

data class PengujiansItem(

    @field:SerializedName("tanggal_uji")
    val tglUji: String? = "-",

    @field:SerializedName("no_pengujian")
    val noPengujian: String? = "-",

    @field:SerializedName("nama_kategori")
    val namaKategori: String? = "-",

    @field:SerializedName("qty_material")
    val qtyMaterial: String? = "-",

    @field:SerializedName("qty_lolos")
    val qtyLolos: String? = "-",

    @field:SerializedName("qty_tdk_lolos")
    val qtyTdkLolos: String? = "-",

    @field:SerializedName("qty_rusak")
    val qtyRusak: Int? = 0,

    @field:SerializedName("unit")
    val unit: String? = "-",

    @field:SerializedName("status_uji")
    val statusUji: String? = "-",

    @field:SerializedName("kd_pabrikan")
    val kdPabrikan: String? = "-",

    @field:SerializedName("tanggal_usul_uji")
    val tanggalUsulUji: String? = "-"

)

data class MaterialGroupsItem(

    @field:SerializedName("material_group")
    val materialGroup: String? = "",

    @field:SerializedName("nama_kategori_material")
    val namaKategoriMaterial: String? = ""
)

data class MaterialDetailsItem(

    @field:SerializedName("nomor_material")
    val nomorMaterial: String? = "",

    @field:SerializedName("mmc")
    val mmc: String? = "",

    @field:SerializedName("material_group")
    val materialGroup: String? = "",

    @field:SerializedName("tahun")
    val tahun: Int? = 0,

    @field:SerializedName("tgl_produksi")
    val tglProduksi: String? = "",

    @field:SerializedName("kd_pabrikan")
    val kdPabrikan: String? = "",

    @field:SerializedName("serial_number")
    val serialNumber: String? = "",

    @field:SerializedName("nomor_sert_materologi")
    val nomorSertMaterologi: String? = "",

    @field:SerializedName("spln")
    val spln: String? = "",

    @field:SerializedName("no_produksi")
    val noProduksi: String? = "",

    @field:SerializedName("storloc")
    val storloc: String? = "",

    @field:SerializedName("nama_kategori_material")
    val namaKategoriMaterial: String? = "",

    @field:SerializedName("no_packaging")
    val noPackaging: String? = "",

    @field:SerializedName("spesifikasi")
    val spesifikasi: String? = "",

    @field:SerializedName("plant")
    val plant: String? = "",

    @field:SerializedName("material_id")
    val materialId: String? = "",

    @field:SerializedName("masa_garansi")
    val masaGaransi: String? = ""
)

data class LokasisItem(
    @field:SerializedName("id")
    val id: String? = "",

    @field:SerializedName("no_do_mims")
    val noDoMims: String? = "",

    @field:SerializedName("ket")
    val ket: String? = "",

    @field:SerializedName("updated_date")
    val updatedDate: String? = ""
)

data class MonitoringPermintaanItem(

    @field:SerializedName("stor_loc_tujuan_name")
    val storLocTujuanName: String? = "-",

    @field:SerializedName("kode_pengeluaran")
    val kodePengeluaran: Int? = 0,

    @field:SerializedName("stor_loc_tujuan")
    val storLocTujuan: String? = "-",

    @field:SerializedName("created_by")
    val createdBy: String? = "-",

    @field:SerializedName("no_repackaging")
    val noRepackaging: String? = "-",

    @field:SerializedName("plant")
    val plant: String? = "-",

    @field:SerializedName("updated_by")
    val updatedBy: String? = "-",

    @field:SerializedName("id")
    val id: String? = "-",

    @field:SerializedName("created_date")
    val createdDate: String? = "-",

    @field:SerializedName("updated_date")
    val updatedDate: String? = "-",

    @field:SerializedName("no_permintaan")
    val noPermintaan: String? = "-",

    @field:SerializedName("jumlah_kardus")
    val jumlahKardus: Int? = 0,

    @field:SerializedName("stor_loc_asal_name")
    val storLocAsalName: String? = "-",

    @field:SerializedName("tanggal_permintaan")
    val tanggalPermintaan: String? = "-",

    @field:SerializedName("tanggal_pengeluaran")
    val tanggalPengeluaran: Any? = "-",

    @field:SerializedName("plant_name")
    val plantName: String? = "-",

    @field:SerializedName("stor_loc_asal")
    val storLocAsal: String? = "-",

    @field:SerializedName("no_transaksi")
    val noTransaksi: String? = "-",

    @field:SerializedName("valuation_type")
    val valuationType: String? = "-",

    @field:SerializedName("total_qty_permintaan")
    val totalQtyPermintaan: String? = "-",

    @field:SerializedName("total_qty_scan")
    val totalScanQty: String? = "-",

    @field:SerializedName("no_pengiriman")
    val noPengiriman: String? = "-"
)

data class MonitoringPermintaanDetailsItem(

    @field:SerializedName("no_repackaging")
    val noRepackaging: String? = "-",

    @field:SerializedName("nomor_material")
    val nomorMaterial: String? = "-",

    @field:SerializedName("unit")
    val unit: String? = "-",

    @field:SerializedName("qty_permintaan")
    val qtyPermintaan: Double? = 0.0,

    @field:SerializedName("material_desc")
    val materialDesc: String? = "-",

    @field:SerializedName("qty_scan")
    val qtyScan: Double? = 0.0,

    @field:SerializedName("kategori")
    val kategori: String? = "-",

    @field:SerializedName("id")
    val id: String? = "-",

    @field:SerializedName("no_permintaan")
    val noPermintaan: String? = "-",

    @field:SerializedName("qty_pengeluaran")
    val qtyPengeluaran: Double? = 0.0,

    @field:SerializedName("no_transaksi")
    val noTransaksi: String? = "-",

    @field:SerializedName("isactive")
    val isActive: Boolean? = false,

    @field:SerializedName("valuation_type")
    val valuationType: String? = "-",

    @field:SerializedName("jumlah_kardus")
    val jumlahKardus: Int? = 0


)

data class SnPermaterialItem(

    @field:SerializedName("no_mat_sap")
    val noMatSap: String? = "-",

    @field:SerializedName("mmc")
    val mmc: String? = "-",

    @field:SerializedName("material_group")
    val materialGroup: String? = "-",

    @field:SerializedName("tgl_produksi")
    val tglProduksi: String? = "-",

    @field:SerializedName("kd_pabrikan")
    val kdPabrikan: String? = "-",

    @field:SerializedName("nomor_sert_materologi")
    val nomorSertMaterologi: String? = "-",

    @field:SerializedName("spln")
    val spln: String? = "-",

    @field:SerializedName("no_produksi")
    val noProduksi: String? = "-",

    @field:SerializedName("storloc")
    val storloc: String? = "-",

    @field:SerializedName("nama_kategori_material")
    val namaKategoriMaterial: String? = "-",

    @field:SerializedName("no_packaging")
    val noPackaging: String? = "-",

    @field:SerializedName("no_serial")
    val noSerial: String? = "-",

    @field:SerializedName("no_do_smar")
    val noDoSmar: String? = "-",

    @field:SerializedName("do_line_item")
    val doLineItem: String? = "-",

    @field:SerializedName("spesifikasi")
    val spesifikasi: String? = "-",

    @field:SerializedName("plant")
    val plant: String? = "-",

    @field:SerializedName("material_id")
    val materialId: String? = "-",

    @field:SerializedName("masa_garansi")
    val masaGaransi: String? = "-",

    @field:SerializedName("do_status")
    val doStatus: String? = "-",

    @field:SerializedName("status")
    val status: String? = "-"
)

data class PemeriksanItem(

    @field:SerializedName("tgl_serah_terima")
    val tglSerahTerima: String? = "-",

    @field:SerializedName("petugas_penerima")
    val petugasPenerima: String? = "-",

    @field:SerializedName("kurir_pengirim")
    val kurirPengirim: String? = "-",

    @field:SerializedName("rating_response")
    val ratingResponse: Any? = "-",

    @field:SerializedName("nama_kategori_material")
    val namaKategoriMaterial: String? = "-",

    @field:SerializedName("no_serial")
    val noSerial: String? = "-",

    @field:SerializedName("plant_code_no")
    val plantCodeNo: String? = "-",

    @field:SerializedName("rating_delivery")
    val ratingDelivery: Any? = "-",

    @field:SerializedName("ketua_pemeriksa")
    val ketuaPemeriksa: String? = "",

    @field:SerializedName("manager_pemeriksa")
    val managerPemeriksa: String? = "",

    @field:SerializedName("sekretaris_pemeriksa")
    val sekretaris_pemeriksa: String? = "",

    @field:SerializedName("anggota_pemeriksa_1")
    val anggota_pemeriksa_1: String? = "",

    @field:SerializedName("anggota_pemeriksa_2")
    val anggota_pemeriksa_2: String? = "",

    @field:SerializedName("total")
    val total: String? = "-",

    @field:SerializedName("no_do_smar")
    val noDoSmar: String? = "-",

    @field:SerializedName("lead_time")
    val leadTime: Int? = 0,

    @field:SerializedName("kode_status_do_mims")
    val kodeStatusDoMims: String? = "-",

    @field:SerializedName("no_mat_sap")
    val noMatSap: String? = "-",

    @field:SerializedName("material_group")
    val materialGroup: String? = "-",

    @field:SerializedName("kd_pabrikan")
    val kdPabrikan: String? = "-",

    @field:SerializedName("no_do_mims")
    val noDoMims: String? = "-",

    @field:SerializedName("no_packaging")
    val noPackaging: String? = "-",

    @field:SerializedName("rating_quality")
    val ratingQuality: Any? = "-",

    @field:SerializedName("status_pemeriksaan")
    val statusPemeriksaan: String? = "-",

    @field:SerializedName("no_pemeriksaan")
    val noPemeriksaan: String? = "-",

    @field:SerializedName("tlsk_no")
    val tlskNo: String? = "-",

    @field:SerializedName("po_sap_no")
    val poSapNo: String? = "-",

    @field:SerializedName("po_mp_no")
    val poMpNo: String? = "-",

    @field:SerializedName("do_line_item")
    val doLineItem: String? = "-",

    @field:SerializedName("created_date")
    val createdDate: String? = "-",

    @field:SerializedName("courier_person_name")
    val courierPersonName: String? = "-",

    @field:SerializedName("do_status")
    val doStatus: String? = "-",

    @field:SerializedName("ekspedition")
    val ekspedition: String? = "-",

    @field:SerializedName("plant_name")
    val plantName: String? = "-",

    @field:SerializedName("status")
    val status: String? = "-",

    @field:SerializedName("nama_pabrikan")
    val namaPabrikan: String? = "-",

    @field:SerializedName("stor_loc")
    val storloc: String? = "-"
)

data class PemeriksaanDetailItem(

    @field:SerializedName("no_mat_sap")
    val noMatSap: String? = "-",

    @field:SerializedName("no_pemeriksaan")
    val noPemeriksaan: String? = "-",

    @field:SerializedName("no_do_smar")
    val noDoSmar: String? = "-",

    @field:SerializedName("created_date")
    val createdDate: String? = "-",

    @field:SerializedName("nama_kategori_material")
    val namaKategoriMaterial: String? = "-",

    @field:SerializedName("no_packaging")
    val noPackaging: String? = "-",

    @field:SerializedName("no_serial")
    val noSerial: String? = "-",

    @field:SerializedName("status")
    val status: String? = "-"
)

data class SnPermintaanItem(

    @field:SerializedName("no_repackaging")
    val noRepackaging: String? = "-",

    @field:SerializedName("nomor_material")
    val nomorMaterial: String? = "-",

    @field:SerializedName("serial_number")
    val serialNumber: String? = "-",

    @field:SerializedName("id")
    val id: String? = "-",

    @field:SerializedName("status")
    val status: String? = "-",

    @field:SerializedName("valuation_type")
    val valuationType: String? = "-",

    @field:SerializedName("no_id_meter")
    val noIdMeter: String? = "-",
)

data class PenerimaanDetailUlpItem(

    @field:SerializedName("no_transaksi")
    val noTransaksi: String? = "-",

    @field:SerializedName("no_repackaging")
    val noRepackaging: String? = "-",

    @field:SerializedName("nomor_material")
    val nomorMaterial: String? = "-",

    @field:SerializedName("qty_permintaan")
    val qtyPermintaan: Double? = 0.0,

    @field:SerializedName("qty_pengiriman")
    val qtyPengiriman: Double? = 0.0,

    @field:SerializedName("material_desc")
    val materialDesc: String? = "-",

    @field:SerializedName("qty_penerimaan")
    val qtyPenerimaan: Double? = 0.0,

    @field:SerializedName("id")
    val id: String? = "-",

    @field:SerializedName("qty_pemeriksaan")
    val qtyPemeriksaan: Double? = 0.0,

    @field:SerializedName("qty_sesuai")
    val qtySesuai: Double? = 0.0,

    @field:SerializedName("isactive")
    val isActive: Boolean? = false,

    @field:SerializedName("valuation_type")
    val valuationType: String? = "-",


)

data class PenerimaanUlpItem(

    @field:SerializedName("status_penerimaan")
    val statusPenerimaan: String? = "-",

    @field:SerializedName("stor_loc_tujuan_name")
    val storLocTujuanName: String? = "-",

    @field:SerializedName("pejabat_penerima")
    val pejabatPenerima: String? = "-",

    @field:SerializedName("jabatan_pemeriksa_2")
    val jabatanPemeriksa2: String? = "-",

    @field:SerializedName("qty_sesuai")
    val qtySesuai: Int? = 0,

    @field:SerializedName("no_penerimaan")
    val noPenerimaan: String? = "-",

    @field:SerializedName("tanggal_penerimaan")
    val tanggalPenerimaan: String? = "-",

    @field:SerializedName("jabatan_pemeriksa_1")
    val jabatanPemeriksa1: String? = "-",

    @field:SerializedName("no_transaksi")
    val noTransaksi: String? = "-",

    @field:SerializedName("qty_pengiriman")
    val qtyPengiriman: Int? = 0,

    @field:SerializedName("material_desc")
    val materialDesc: String? = "-",

    @field:SerializedName("qty_penerimaan")
    val qtyPenerimaan: Int? = 0,

    @field:SerializedName("tanggal_dokumen")
    val tanggalDokumen: String? = "-",

    @field:SerializedName("qty_pemeriksaan")
    val qtyPemeriksaan: Int? = 0,

    @field:SerializedName("tanggal_pengiriman")
    val tanggalPengiriman: String? = "-",

    @field:SerializedName("jumlah_kardus")
    val jumlahKardus: Int? = 0,

    @field:SerializedName("stor_loc_asal")
    val storLocAsal: String? = "-",

    @field:SerializedName("nomor_material")
    val nomorMaterial: String? = "-",

    @field:SerializedName("qty_permintaan")
    val qtyPermintaan: Int? = 0,

    @field:SerializedName("stor_loc_tujuan")
    val storLocTujuan: String? = "-",

    @field:SerializedName("no_nota")
    val noNota: String? = "-",

    @field:SerializedName("kurir")
    val kurir: String? = "-",

    @field:SerializedName("status_kirim_ago")
    val statusKirimAgo: Any? = "-",

    @field:SerializedName("nama_pemeriksa_2")
    val namaPemeriksa2: String? = "-",

    @field:SerializedName("nama_pemeriksa_1")
    val namaPemeriksa1: String? = "-",

    @field:SerializedName("no_repackaging")
    val noRepackaging: String? = "-",

    @field:SerializedName("no_pemeriksaan")
    val noPemeriksaan: String? = "-",

    @field:SerializedName("status_pemeriksaan")
    val statusPemeriksaan: String? = "-",

    @field:SerializedName("tanggal_pemeriksaan")
    val tanggalPemeriksaan: String? = "-",

    @field:SerializedName("plant")
    val plant: String? = "-",

    @field:SerializedName("no_pk")
    val noPk: String? = "-",

    @field:SerializedName("no_pengiriman")
    val noPengiriman: String? = "-",

    @field:SerializedName("no_permintaan")
    val noPermintaan: String? = "-",

    @field:SerializedName("kepala_gudang")
    val kepalaGudang: String? = "-",

    @field:SerializedName("stor_loc_asal_name")
    val storLocAsalName: String? = "-",

    @field:SerializedName("kode_integrasi")
    val kodeIntegrasi: String? = "-"
)

data class PemakaianDetailItem(

    @field:SerializedName("no_transaksi")
    val noTransaksi: String? = "",

    @field:SerializedName("nomor_material")
    val nomorMaterial: String? = "",

    @field:SerializedName("unit")
    val unit: String? = "",

    @field:SerializedName("keterangan")
    val keterangan: String? = "",

    @field:SerializedName("nama_material")
    val namaMaterial: String? = "",

    @field:SerializedName("qty_reservasi")
    val qtyReservasi: Double? = 0.0,

    @field:SerializedName("qty_pemakaian")
    val qtyPemakaian: Double? = 0.0,

    @field:SerializedName("no_meter")
    val noMeter: String? = "",

    @field:SerializedName("valuation_type")
    val valuationType: String? = "",

    @field:SerializedName("qty_pengeluaran")
    val qtyPengeluaran: Double? = 0.0,

    @field:SerializedName("isactive")
    val isActive: Boolean? = false
)

data class PemakaianItem(

    @field:SerializedName("no_reservasi")
    val noReservasi: String? = "",

    @field:SerializedName("tanggal_reservasi")
    val tanggalReservasi: String? = "",

    @field:SerializedName("status_pemakaian")
    val statusPemakaian: String? = "",

    @field:SerializedName("sumber")
    val sumber: String? = "",

    @field:SerializedName("no_pemesanan")
    val noPemesanan: String? = "",

    @field:SerializedName("status_kirim_ago")
    val statusKirimAgo: Any? = "",

    @field:SerializedName("daya")
    val daya: String? = "",

    @field:SerializedName("id_pelanggan")
    val idPelanggan: String? = "",

    @field:SerializedName("tanggal_pemakaian")
    val tanggalPemakaian: String? = "",

    @field:SerializedName("jenis_pekerjaan")
    val jenisPekerjaan: String? = "",

    @field:SerializedName("no_transaksi")
    val noTransaksi: String? = "",

    @field:SerializedName("no_agenda")
    val noAgenda: String? = "",

    @field:SerializedName("stor_loc")
    val storLoc: String? = "",

    @field:SerializedName("nama_pelanggan")
    val namaPelanggan: String? = "",

    @field:SerializedName("tarif")
    val tarif: String? = "",

    @field:SerializedName("tanggal_bayar")
    val tanggalBayar: Any? = "",

    @field:SerializedName("plant")
    val plant: String? = "",

    @field:SerializedName("status_sap")
    val statusSap: Any? = "",

    @field:SerializedName("tanggal_dokumen")
    val tanggalDokumen: String? = "",

    @field:SerializedName("alamat_pelanggan")
    val alamatPelanggan: String? = "",

    @field:SerializedName("no_pemakaian")
    val noPemakaian: String? = "",

    @field:SerializedName("kode_integrasi")
    val kodeIntegrasi: String? = "",

    @field:SerializedName("tanggal_pengeluaran")
    val tanggalPengeluaran: String? = "",

    @field:SerializedName("no_pk")
    val noPk: String? = "",

    @field:SerializedName("lokasi")
    val lokasi: String? = "",

    @field:SerializedName("kepala_gudang")
    val kepalaGudang: String? = "",

    @field:SerializedName("pejabat_pengesahan")
    val pejabatPengesahan: String? = "",

    @field:SerializedName("pemeriksa")
    val pemeriksa: String? = "",

    @field:SerializedName("penerima")
    val penerima: String? = "",

    @field:SerializedName("nama_kegiatan")
    val namaKegiatan: String? = "",

    @field:SerializedName("no_bon")
    val noBon: String? = "",
)

data class SnPemakaianUlpItem(

    @field:SerializedName("no_transaksi")
    val noTransaksi: String? = "-",

    @field:SerializedName("nomor_material")
    val nomorMaterial: String? = "-",

    @field:SerializedName("serial_number")
    val serialNumber: String? = "-",

    @field:SerializedName("id")
    val id: String? = "-"
)

data class SnPenerimaanUlpItem(

    @field:SerializedName("no_repackaging")
    val noRepackaging: String? = "-",

    @field:SerializedName("nomor_material")
    val nomorMaterial: String? = "-",

    @field:SerializedName("serial_number")
    val serialNumber: String? = "-",

    @field:SerializedName("id")
    val id: String? = "-",

    @field:SerializedName("status")
    val status: Any? = "-"
)

data class DataRatingsItem(

    @field:SerializedName("ketepatan")
    val ketepatan: Int? = 0,

    @field:SerializedName("rating_quality")
    val ratingQuality: String? = "-",

    @field:SerializedName("rating_delivery")
    val ratingDelivery: String? = "-",

    @field:SerializedName("selesai_rating")
    val selesaiRating: Boolean? = false,

    @field:SerializedName("rating_doc")
    val ratingDoc: List<String?>? = null,

    @field:SerializedName("no_do_smar")
    val noDoSmar: String? = "-",

    @field:SerializedName("rating_response")
    val ratingResponse: String? = "-",

    @field:SerializedName("no_rating")
    val noRating: String? = "-"
)

data class PetugasPengujianItem(
    @field:SerializedName("nip")
    val nip: String? = "-",

    @field:SerializedName("jabatan")
    val jabatan: String? = "-",

    @field:SerializedName("nama_petugas")
    val namaPetugas: String? = "-",

    @field:SerializedName("no_pengujian")
    val noPengujian: String? = "-"
)

data class MonitoringKomplainItem(

    @field:SerializedName("no_komplain_smar")
    val noKomplainSmar: String? = "-",

    @field:SerializedName("no_do_smar")
    val noDoSmar: String? = "-",

    @field:SerializedName("finish_date")
    val finishDate: String? = "-",

    @field:SerializedName("po_sap_no")
    val poSapNo: String? = "-",

    @field:SerializedName("qty")
    val qty: Int? = 0,

    @field:SerializedName("no_komplain")
    val noKomplain: String? = "-",

    @field:SerializedName("alasan")
    val alasan: String? = "-",

    @field:SerializedName("status")
    val status: String? = "-",

    @field:SerializedName("plant_name")
    val plantName: String? = "-",

    @field:SerializedName("tanggal_po")
    val tanggalPo: String? = "-"
)

data class MonitoringKomplainDetailItem(

    @field:SerializedName("no_mat_sap")
    val noMatSap: String? = "-",

    @field:SerializedName("no_do_smar")
    val noDoSmar: String? = "-",

    @field:SerializedName("do_line_item")
    val doLineItem: String? = "-",

    @field:SerializedName("tanggal_pengajuan")
    val tanggalPengajuan: String? = "-",

    @field:SerializedName("no_packaging")
    val noPackaging: String? = "-",

    @field:SerializedName("no_serial")
    val noSerial: String? = "-",

    @field:SerializedName("no_komplain")
    val noKomplain: String? = "-",

    @field:SerializedName("status")
    val status: String? = "-"
)

data class DataPenerimaanAkhirItem(

    @field:SerializedName("is_komplained")
    val isKomplained: Boolean? = false,

    @field:SerializedName("no_mat_sap")
    val noMatSap: String? = "",

    @field:SerializedName("stor_loc")
    val storLoc: String? = "",

    @field:SerializedName("no_do_smar")
    val noDoSmar: String? = "-",

    @field:SerializedName("kd_pabrikan")
    val kdPabrikan: String? = "",

    @field:SerializedName("is_rejected")
    val isRejected: Boolean? = false,

    @field:SerializedName("qty_do")
    val qtyDo: Int? = 0,

    @field:SerializedName("is_received")
    val isReceived: Boolean? = false,

    @field:SerializedName("nama_kategori_material")
    val namaKategoriMaterial: String? = "",

    @field:SerializedName("status")
    val status: String? = "",

    @field:SerializedName("no_packaging")
    val noPackaging: String? = "",

    @field:SerializedName("no_serial")
    val noSerial: String? = "",

    @field:SerializedName("nama_pabrikan")
    val namaPabrikan: String? = ""
)

data class DataMasterStatusDo(

    @field:SerializedName("kode_do")
    val kodeDo: String? = "",

    @field:SerializedName("keterangan")
    val keterangan: String? = "",

    @field:SerializedName("pallete_color_background")
    val backgroundColor: String? = "",

    @field:SerializedName("pallete_color_text")
    val textColor: String? = "",
)

data class DataPejabat(

    @field:SerializedName("id")
    val idPejabat: Int? = 0,

    @field:SerializedName("nama")
    val nama: String? = ""
)
