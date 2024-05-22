package dev.iconpln.mims.data.local.database;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "TMONITORING_COMPLAINT".
 */
@Entity
public class TMonitoringComplaint {

    @Id
    private Long id;
    private String NoKomplainSmar;
    private String NoDoSmar;
    private String TanggalSelesai;
    private String PoSapNo;
    private Integer qty;
    private String NoKomplain;
    private String Alasan;
    private String Status;
    private String PlantName;
    private String TanggalPO;

    @Generated
    public TMonitoringComplaint() {
    }

    public TMonitoringComplaint(Long id) {
        this.id = id;
    }

    @Generated
    public TMonitoringComplaint(Long id, String NoKomplainSmar, String NoDoSmar, String TanggalSelesai, String PoSapNo, Integer qty, String NoKomplain, String Alasan, String Status, String PlantName, String TanggalPO) {
        this.id = id;
        this.NoKomplainSmar = NoKomplainSmar;
        this.NoDoSmar = NoDoSmar;
        this.TanggalSelesai = TanggalSelesai;
        this.PoSapNo = PoSapNo;
        this.qty = qty;
        this.NoKomplain = NoKomplain;
        this.Alasan = Alasan;
        this.Status = Status;
        this.PlantName = PlantName;
        this.TanggalPO = TanggalPO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNoKomplainSmar() {
        return NoKomplainSmar;
    }

    public void setNoKomplainSmar(String NoKomplainSmar) {
        this.NoKomplainSmar = NoKomplainSmar;
    }

    public String getNoDoSmar() {
        return NoDoSmar;
    }

    public void setNoDoSmar(String NoDoSmar) {
        this.NoDoSmar = NoDoSmar;
    }

    public String getTanggalSelesai() {
        return TanggalSelesai;
    }

    public void setTanggalSelesai(String TanggalSelesai) {
        this.TanggalSelesai = TanggalSelesai;
    }

    public String getPoSapNo() {
        return PoSapNo;
    }

    public void setPoSapNo(String PoSapNo) {
        this.PoSapNo = PoSapNo;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public String getNoKomplain() {
        return NoKomplain;
    }

    public void setNoKomplain(String NoKomplain) {
        this.NoKomplain = NoKomplain;
    }

    public String getAlasan() {
        return Alasan;
    }

    public void setAlasan(String Alasan) {
        this.Alasan = Alasan;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getPlantName() {
        return PlantName;
    }

    public void setPlantName(String PlantName) {
        this.PlantName = PlantName;
    }

    public String getTanggalPO() {
        return TanggalPO;
    }

    public void setTanggalPO(String TanggalPO) {
        this.TanggalPO = TanggalPO;
    }

}
