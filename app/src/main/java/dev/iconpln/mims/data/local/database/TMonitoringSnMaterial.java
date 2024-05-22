package dev.iconpln.mims.data.local.database;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "TMONITORING_SN_MATERIAL".
 */
@Entity
public class TMonitoringSnMaterial {

    @Id
    private Long id;
    private String NoRepackaging;
    private String NomorMaterial;
    private String SerialNumber;
    private String Packaging;
    private String Status;
    private Integer IsScanned;
    private String ValuationType;
    private String NoIdMeter;

    @Generated
    public TMonitoringSnMaterial() {
    }

    public TMonitoringSnMaterial(Long id) {
        this.id = id;
    }

    @Generated
    public TMonitoringSnMaterial(Long id, String NoRepackaging, String NomorMaterial, String SerialNumber, String Packaging, String Status, Integer IsScanned, String ValuationType, String NoIdMeter) {
        this.id = id;
        this.NoRepackaging = NoRepackaging;
        this.NomorMaterial = NomorMaterial;
        this.SerialNumber = SerialNumber;
        this.Packaging = Packaging;
        this.Status = Status;
        this.IsScanned = IsScanned;
        this.ValuationType = ValuationType;
        this.NoIdMeter = NoIdMeter;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNoRepackaging() {
        return NoRepackaging;
    }

    public void setNoRepackaging(String NoRepackaging) {
        this.NoRepackaging = NoRepackaging;
    }

    public String getNomorMaterial() {
        return NomorMaterial;
    }

    public void setNomorMaterial(String NomorMaterial) {
        this.NomorMaterial = NomorMaterial;
    }

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String SerialNumber) {
        this.SerialNumber = SerialNumber;
    }

    public String getPackaging() {
        return Packaging;
    }

    public void setPackaging(String Packaging) {
        this.Packaging = Packaging;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public Integer getIsScanned() {
        return IsScanned;
    }

    public void setIsScanned(Integer IsScanned) {
        this.IsScanned = IsScanned;
    }

    public String getValuationType() {
        return ValuationType;
    }

    public void setValuationType(String ValuationType) {
        this.ValuationType = ValuationType;
    }

    public String getNoIdMeter() {
        return NoIdMeter;
    }

    public void setNoIdMeter(String NoIdMeter) {
        this.NoIdMeter = NoIdMeter;
    }

}
