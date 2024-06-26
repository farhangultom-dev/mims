package dev.iconpln.mims.data.local.database;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "TTRANS_PENERIMAAN_DETAIL_ULP".
 */
@Entity
public class TTransPenerimaanDetailUlp {

    @Id
    private Long id;
    private String NoTransaksi;
    private String NoRepackaging;
    private String NoMaterial;
    private String MaterialDesc;
    private Double QtyPermintaan;
    private Double QtyPengiriman;
    private Double QtyPemeriksaan;
    private Double QtyPenerimaan;
    private Double QtySesuai;
    private Boolean IsActive;
    private String ValuationType;
    private Integer IsDone;

    @Generated
    public TTransPenerimaanDetailUlp() {
    }

    public TTransPenerimaanDetailUlp(Long id) {
        this.id = id;
    }

    @Generated
    public TTransPenerimaanDetailUlp(Long id, String NoTransaksi, String NoRepackaging, String NoMaterial, String MaterialDesc, Double QtyPermintaan, Double QtyPengiriman, Double QtyPemeriksaan, Double QtyPenerimaan, Double QtySesuai, Boolean IsActive, String ValuationType, Integer IsDone) {
        this.id = id;
        this.NoTransaksi = NoTransaksi;
        this.NoRepackaging = NoRepackaging;
        this.NoMaterial = NoMaterial;
        this.MaterialDesc = MaterialDesc;
        this.QtyPermintaan = QtyPermintaan;
        this.QtyPengiriman = QtyPengiriman;
        this.QtyPemeriksaan = QtyPemeriksaan;
        this.QtyPenerimaan = QtyPenerimaan;
        this.QtySesuai = QtySesuai;
        this.IsActive = IsActive;
        this.ValuationType = ValuationType;
        this.IsDone = IsDone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNoTransaksi() {
        return NoTransaksi;
    }

    public void setNoTransaksi(String NoTransaksi) {
        this.NoTransaksi = NoTransaksi;
    }

    public String getNoRepackaging() {
        return NoRepackaging;
    }

    public void setNoRepackaging(String NoRepackaging) {
        this.NoRepackaging = NoRepackaging;
    }

    public String getNoMaterial() {
        return NoMaterial;
    }

    public void setNoMaterial(String NoMaterial) {
        this.NoMaterial = NoMaterial;
    }

    public String getMaterialDesc() {
        return MaterialDesc;
    }

    public void setMaterialDesc(String MaterialDesc) {
        this.MaterialDesc = MaterialDesc;
    }

    public Double getQtyPermintaan() {
        return QtyPermintaan;
    }

    public void setQtyPermintaan(Double QtyPermintaan) {
        this.QtyPermintaan = QtyPermintaan;
    }

    public Double getQtyPengiriman() {
        return QtyPengiriman;
    }

    public void setQtyPengiriman(Double QtyPengiriman) {
        this.QtyPengiriman = QtyPengiriman;
    }

    public Double getQtyPemeriksaan() {
        return QtyPemeriksaan;
    }

    public void setQtyPemeriksaan(Double QtyPemeriksaan) {
        this.QtyPemeriksaan = QtyPemeriksaan;
    }

    public Double getQtyPenerimaan() {
        return QtyPenerimaan;
    }

    public void setQtyPenerimaan(Double QtyPenerimaan) {
        this.QtyPenerimaan = QtyPenerimaan;
    }

    public Double getQtySesuai() {
        return QtySesuai;
    }

    public void setQtySesuai(Double QtySesuai) {
        this.QtySesuai = QtySesuai;
    }

    public Boolean getIsActive() {
        return IsActive;
    }

    public void setIsActive(Boolean IsActive) {
        this.IsActive = IsActive;
    }

    public String getValuationType() {
        return ValuationType;
    }

    public void setValuationType(String ValuationType) {
        this.ValuationType = ValuationType;
    }

    public Integer getIsDone() {
        return IsDone;
    }

    public void setIsDone(Integer IsDone) {
        this.IsDone = IsDone;
    }

}
