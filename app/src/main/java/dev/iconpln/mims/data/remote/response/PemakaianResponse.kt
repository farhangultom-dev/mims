package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

data class PemakaianResponse(

    @field:SerializedName("totalRow")
    val totalRow: Int? = null,

    @field:SerializedName("data")
    val data: List<DataPemakaian>,

    @field:SerializedName("timestamp")
    val timestamp: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)

data class DataPemakaian(

    @field:SerializedName("no_reservasi")
    val noReservasi: String? = null,

    @field:SerializedName("sumber")
    val sumber: String? = null,

    @field:SerializedName("deleted_by")
    val deletedBy: String? = null,

    @field:SerializedName("pejabat_pengesahan_id")
    val pejabatPengesahanId: String? = null,

    @field:SerializedName("no_transaksi")
    val noTransaksi: String? = null,

    @field:SerializedName("stor_loc")
    val storLoc: String? = null,

    @field:SerializedName("deleted_date")
    val deletedDate: String? = null,

    @field:SerializedName("status_sap")
    val statusSap: String? = null,

    @field:SerializedName("total_qty_reservasi")
    val totalQtyReservasi: String? = null,

    @field:SerializedName("no_sap")
    val noSap: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("tanggal_dokumen")
    val tanggalDokumen: String? = null,

    @field:SerializedName("kepala_gudang_id")
    val kepalaGudangId: String? = null,

    @field:SerializedName("tanggal_reservasi")
    val tanggalReservasi: String? = null,

    @field:SerializedName("status_pemakaian")
    val statusPemakaian: String? = null,

    @field:SerializedName("penerima")
    val penerima: String? = null,

    @field:SerializedName("flag_delete")
    val flagDelete: String? = null,

    @field:SerializedName("pemeriksa")
    val pemeriksa: String? = null,

    @field:SerializedName("status_kirim_ago")
    val statusKirimAgo: String? = null,

    @field:SerializedName("pejabat_pengesahan")
    val pejabatPengesahan: String? = null,

    @field:SerializedName("created_by")
    val createdBy: String? = null,

    @field:SerializedName("tanggal_pemakaian")
    val tanggalPemakaian: String? = null,

    @field:SerializedName("jenis_pekerjaan")
    val jenisPekerjaan: String? = null,

    @field:SerializedName("nama_pelanggan")
    val namaPelanggan: String? = null,

    @field:SerializedName("nama_kegiatan")
    val namaKegiatan: String? = null,

    @field:SerializedName("lokasi")
    val lokasi: String? = null,

    @field:SerializedName("plant")
    val plant: String? = null,

    @field:SerializedName("total_qty_scan")
    val totalQtyScan: String? = null,

    @field:SerializedName("updated_by")
    val updatedBy: String? = null,

    @field:SerializedName("no_pk")
    val noPk: String? = null,

    @field:SerializedName("created_date")
    val createdDate: String? = null,

    @field:SerializedName("updated_date")
    val updatedDate: String? = null,

    @field:SerializedName("no_pemakaian")
    val noPemakaian: String? = null,

    @field:SerializedName("kepala_gudang")
    val kepalaGudang: String? = null,

    @field:SerializedName("no_bon")
    val noBon: String? = null,

    @field:SerializedName("kode_integrasi")
    val kodeIntegrasi: String? = null
)
