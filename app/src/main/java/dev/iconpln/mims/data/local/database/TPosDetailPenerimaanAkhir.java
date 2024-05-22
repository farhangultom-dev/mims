package dev.iconpln.mims.data.local.database;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "TPOS_DETAIL_PENERIMAAN_AKHIR".
 */
@Entity
public class TPosDetailPenerimaanAkhir {

    @Id
    private Long id;
    private String NoDoSmar;
    private String Qty;
    private String KdPabrikan;
    private String NoPackaging;
    private String SerialNumber;
    private String NoMaterial;
    private String NamaKategoriMaterial;
    private String StorLoc;
    private String Status;
    private String NamaPabrikan;
    private Boolean IsComplaint;
    private Boolean IsReceived;
    private Boolean IsRejected;

    @Generated
    public TPosDetailPenerimaanAkhir() {
    }

    public TPosDetailPenerimaanAkhir(Long id) {
        this.id = id;
    }

    @Generated
    public TPosDetailPenerimaanAkhir(Long id, String NoDoSmar, String Qty, String KdPabrikan, String NoPackaging, String SerialNumber, String NoMaterial, String NamaKategoriMaterial, String StorLoc, String Status, String NamaPabrikan, Boolean IsComplaint, Boolean IsReceived, Boolean IsRejected) {
        this.id = id;
        this.NoDoSmar = NoDoSmar;
        this.Qty = Qty;
        this.KdPabrikan = KdPabrikan;
        this.NoPackaging = NoPackaging;
        this.SerialNumber = SerialNumber;
        this.NoMaterial = NoMaterial;
        this.NamaKategoriMaterial = NamaKategoriMaterial;
        this.StorLoc = StorLoc;
        this.Status = Status;
        this.NamaPabrikan = NamaPabrikan;
        this.IsComplaint = IsComplaint;
        this.IsReceived = IsReceived;
        this.IsRejected = IsRejected;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNoDoSmar() {
        return NoDoSmar;
    }

    public void setNoDoSmar(String NoDoSmar) {
        this.NoDoSmar = NoDoSmar;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String Qty) {
        this.Qty = Qty;
    }

    public String getKdPabrikan() {
        return KdPabrikan;
    }

    public void setKdPabrikan(String KdPabrikan) {
        this.KdPabrikan = KdPabrikan;
    }

    public String getNoPackaging() {
        return NoPackaging;
    }

    public void setNoPackaging(String NoPackaging) {
        this.NoPackaging = NoPackaging;
    }

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String SerialNumber) {
        this.SerialNumber = SerialNumber;
    }

    public String getNoMaterial() {
        return NoMaterial;
    }

    public void setNoMaterial(String NoMaterial) {
        this.NoMaterial = NoMaterial;
    }

    public String getNamaKategoriMaterial() {
        return NamaKategoriMaterial;
    }

    public void setNamaKategoriMaterial(String NamaKategoriMaterial) {
        this.NamaKategoriMaterial = NamaKategoriMaterial;
    }

    public String getStorLoc() {
        return StorLoc;
    }

    public void setStorLoc(String StorLoc) {
        this.StorLoc = StorLoc;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getNamaPabrikan() {
        return NamaPabrikan;
    }

    public void setNamaPabrikan(String NamaPabrikan) {
        this.NamaPabrikan = NamaPabrikan;
    }

    public Boolean getIsComplaint() {
        return IsComplaint;
    }

    public void setIsComplaint(Boolean IsComplaint) {
        this.IsComplaint = IsComplaint;
    }

    public Boolean getIsReceived() {
        return IsReceived;
    }

    public void setIsReceived(Boolean IsReceived) {
        this.IsReceived = IsReceived;
    }

    public Boolean getIsRejected() {
        return IsRejected;
    }

    public void setIsRejected(Boolean IsRejected) {
        this.IsRejected = IsRejected;
    }

}
