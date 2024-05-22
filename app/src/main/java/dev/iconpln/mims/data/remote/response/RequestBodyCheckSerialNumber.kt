package dev.iconpln.mims.data.remote.response

import com.google.gson.annotations.SerializedName

/**
 * @author: Ilham Ardiansyah
 * @date: 07/03/2024
 */
data class RequestBodyCheckSerialNumber(

    @field:SerializedName("is_delete")
    val isDelete: Boolean,

    @field:SerializedName("nomor_transaksi")
    val nomorTransaksi: String,

    @field:SerializedName("serial_number")
    val serialNumber: String,

    @field:SerializedName("val_type")
    val valuationType: String
)
