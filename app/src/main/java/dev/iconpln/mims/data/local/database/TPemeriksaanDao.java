package dev.iconpln.mims.data.local.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TPEMERIKSAAN".
*/
public class TPemeriksaanDao extends AbstractDao<TPemeriksaan, Long> {

    public static final String TABLENAME = "TPEMERIKSAAN";

    /**
     * Properties of entity TPemeriksaan.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property NoPemeriksaan = new Property(1, String.class, "NoPemeriksaan", false, "NO_PEMERIKSAAN");
        public final static Property StorLoc = new Property(2, String.class, "StorLoc", false, "STOR_LOC");
        public final static Property Total = new Property(3, String.class, "Total", false, "TOTAL");
        public final static Property TlskNo = new Property(4, String.class, "TlskNo", false, "TLSK_NO");
        public final static Property PoSapNo = new Property(5, String.class, "PoSapNo", false, "PO_SAP_NO");
        public final static Property PoMpNo = new Property(6, String.class, "PoMpNo", false, "PO_MP_NO");
        public final static Property NoDoSmar = new Property(7, String.class, "NoDoSmar", false, "NO_DO_SMAR");
        public final static Property LeadTime = new Property(8, Integer.class, "LeadTime", false, "LEAD_TIME");
        public final static Property CreatedDate = new Property(9, String.class, "CreatedDate", false, "CREATED_DATE");
        public final static Property PlanCodeNo = new Property(10, String.class, "PlanCodeNo", false, "PLAN_CODE_NO");
        public final static Property PlantName = new Property(11, String.class, "PlantName", false, "PLANT_NAME");
        public final static Property NoDoMims = new Property(12, String.class, "NoDoMims", false, "NO_DO_MIMS");
        public final static Property DoStatus = new Property(13, String.class, "DoStatus", false, "DO_STATUS");
        public final static Property StatusPemeriksaan = new Property(14, String.class, "StatusPemeriksaan", false, "STATUS_PEMERIKSAAN");
        public final static Property Expeditions = new Property(15, String.class, "Expeditions", false, "EXPEDITIONS");
        public final static Property CourierPersonName = new Property(16, String.class, "CourierPersonName", false, "COURIER_PERSON_NAME");
        public final static Property KdPabrikan = new Property(17, String.class, "KdPabrikan", false, "KD_PABRIKAN");
        public final static Property MaterialGroup = new Property(18, String.class, "MaterialGroup", false, "MATERIAL_GROUP");
        public final static Property NamaKategoriMaterial = new Property(19, String.class, "NamaKategoriMaterial", false, "NAMA_KATEGORI_MATERIAL");
        public final static Property TanggalDiterima = new Property(20, String.class, "TanggalDiterima", false, "TANGGAL_DITERIMA");
        public final static Property PetugasPenerima = new Property(21, String.class, "PetugasPenerima", false, "PETUGAS_PENERIMA");
        public final static Property NamaKurir = new Property(22, String.class, "NamaKurir", false, "NAMA_KURIR");
        public final static Property NamaEkspedisi = new Property(23, String.class, "NamaEkspedisi", false, "NAMA_EKSPEDISI");
        public final static Property DoLineItem = new Property(24, String.class, "DoLineItem", false, "DO_LINE_ITEM");
        public final static Property NamaPabrikan = new Property(25, String.class, "NamaPabrikan", false, "NAMA_PABRIKAN");
        public final static Property NamaManager = new Property(26, String.class, "NamaManager", false, "NAMA_MANAGER");
        public final static Property NamaKetua = new Property(27, String.class, "NamaKetua", false, "NAMA_KETUA");
        public final static Property NamaSekretaris = new Property(28, String.class, "NamaSekretaris", false, "NAMA_SEKRETARIS");
        public final static Property NamaAnggota = new Property(29, String.class, "NamaAnggota", false, "NAMA_ANGGOTA");
        public final static Property NamaAnggotaBaru = new Property(30, String.class, "NamaAnggotaBaru", false, "NAMA_ANGGOTA_BARU");
        public final static Property IsDone = new Property(31, Integer.class, "isDone", false, "IS_DONE");
    }


    public TPemeriksaanDao(DaoConfig config) {
        super(config);
    }
    
    public TPemeriksaanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TPEMERIKSAAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"NO_PEMERIKSAAN\" TEXT," + // 1: NoPemeriksaan
                "\"STOR_LOC\" TEXT," + // 2: StorLoc
                "\"TOTAL\" TEXT," + // 3: Total
                "\"TLSK_NO\" TEXT," + // 4: TlskNo
                "\"PO_SAP_NO\" TEXT," + // 5: PoSapNo
                "\"PO_MP_NO\" TEXT," + // 6: PoMpNo
                "\"NO_DO_SMAR\" TEXT," + // 7: NoDoSmar
                "\"LEAD_TIME\" INTEGER," + // 8: LeadTime
                "\"CREATED_DATE\" TEXT," + // 9: CreatedDate
                "\"PLAN_CODE_NO\" TEXT," + // 10: PlanCodeNo
                "\"PLANT_NAME\" TEXT," + // 11: PlantName
                "\"NO_DO_MIMS\" TEXT," + // 12: NoDoMims
                "\"DO_STATUS\" TEXT," + // 13: DoStatus
                "\"STATUS_PEMERIKSAAN\" TEXT," + // 14: StatusPemeriksaan
                "\"EXPEDITIONS\" TEXT," + // 15: Expeditions
                "\"COURIER_PERSON_NAME\" TEXT," + // 16: CourierPersonName
                "\"KD_PABRIKAN\" TEXT," + // 17: KdPabrikan
                "\"MATERIAL_GROUP\" TEXT," + // 18: MaterialGroup
                "\"NAMA_KATEGORI_MATERIAL\" TEXT," + // 19: NamaKategoriMaterial
                "\"TANGGAL_DITERIMA\" TEXT," + // 20: TanggalDiterima
                "\"PETUGAS_PENERIMA\" TEXT," + // 21: PetugasPenerima
                "\"NAMA_KURIR\" TEXT," + // 22: NamaKurir
                "\"NAMA_EKSPEDISI\" TEXT," + // 23: NamaEkspedisi
                "\"DO_LINE_ITEM\" TEXT," + // 24: DoLineItem
                "\"NAMA_PABRIKAN\" TEXT," + // 25: NamaPabrikan
                "\"NAMA_MANAGER\" TEXT," + // 26: NamaManager
                "\"NAMA_KETUA\" TEXT," + // 27: NamaKetua
                "\"NAMA_SEKRETARIS\" TEXT," + // 28: NamaSekretaris
                "\"NAMA_ANGGOTA\" TEXT," + // 29: NamaAnggota
                "\"NAMA_ANGGOTA_BARU\" TEXT," + // 30: NamaAnggotaBaru
                "\"IS_DONE\" INTEGER);"); // 31: isDone
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TPEMERIKSAAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TPemeriksaan entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String NoPemeriksaan = entity.getNoPemeriksaan();
        if (NoPemeriksaan != null) {
            stmt.bindString(2, NoPemeriksaan);
        }
 
        String StorLoc = entity.getStorLoc();
        if (StorLoc != null) {
            stmt.bindString(3, StorLoc);
        }
 
        String Total = entity.getTotal();
        if (Total != null) {
            stmt.bindString(4, Total);
        }
 
        String TlskNo = entity.getTlskNo();
        if (TlskNo != null) {
            stmt.bindString(5, TlskNo);
        }
 
        String PoSapNo = entity.getPoSapNo();
        if (PoSapNo != null) {
            stmt.bindString(6, PoSapNo);
        }
 
        String PoMpNo = entity.getPoMpNo();
        if (PoMpNo != null) {
            stmt.bindString(7, PoMpNo);
        }
 
        String NoDoSmar = entity.getNoDoSmar();
        if (NoDoSmar != null) {
            stmt.bindString(8, NoDoSmar);
        }
 
        Integer LeadTime = entity.getLeadTime();
        if (LeadTime != null) {
            stmt.bindLong(9, LeadTime);
        }
 
        String CreatedDate = entity.getCreatedDate();
        if (CreatedDate != null) {
            stmt.bindString(10, CreatedDate);
        }
 
        String PlanCodeNo = entity.getPlanCodeNo();
        if (PlanCodeNo != null) {
            stmt.bindString(11, PlanCodeNo);
        }
 
        String PlantName = entity.getPlantName();
        if (PlantName != null) {
            stmt.bindString(12, PlantName);
        }
 
        String NoDoMims = entity.getNoDoMims();
        if (NoDoMims != null) {
            stmt.bindString(13, NoDoMims);
        }
 
        String DoStatus = entity.getDoStatus();
        if (DoStatus != null) {
            stmt.bindString(14, DoStatus);
        }
 
        String StatusPemeriksaan = entity.getStatusPemeriksaan();
        if (StatusPemeriksaan != null) {
            stmt.bindString(15, StatusPemeriksaan);
        }
 
        String Expeditions = entity.getExpeditions();
        if (Expeditions != null) {
            stmt.bindString(16, Expeditions);
        }
 
        String CourierPersonName = entity.getCourierPersonName();
        if (CourierPersonName != null) {
            stmt.bindString(17, CourierPersonName);
        }
 
        String KdPabrikan = entity.getKdPabrikan();
        if (KdPabrikan != null) {
            stmt.bindString(18, KdPabrikan);
        }
 
        String MaterialGroup = entity.getMaterialGroup();
        if (MaterialGroup != null) {
            stmt.bindString(19, MaterialGroup);
        }
 
        String NamaKategoriMaterial = entity.getNamaKategoriMaterial();
        if (NamaKategoriMaterial != null) {
            stmt.bindString(20, NamaKategoriMaterial);
        }
 
        String TanggalDiterima = entity.getTanggalDiterima();
        if (TanggalDiterima != null) {
            stmt.bindString(21, TanggalDiterima);
        }
 
        String PetugasPenerima = entity.getPetugasPenerima();
        if (PetugasPenerima != null) {
            stmt.bindString(22, PetugasPenerima);
        }
 
        String NamaKurir = entity.getNamaKurir();
        if (NamaKurir != null) {
            stmt.bindString(23, NamaKurir);
        }
 
        String NamaEkspedisi = entity.getNamaEkspedisi();
        if (NamaEkspedisi != null) {
            stmt.bindString(24, NamaEkspedisi);
        }
 
        String DoLineItem = entity.getDoLineItem();
        if (DoLineItem != null) {
            stmt.bindString(25, DoLineItem);
        }
 
        String NamaPabrikan = entity.getNamaPabrikan();
        if (NamaPabrikan != null) {
            stmt.bindString(26, NamaPabrikan);
        }
 
        String NamaManager = entity.getNamaManager();
        if (NamaManager != null) {
            stmt.bindString(27, NamaManager);
        }
 
        String NamaKetua = entity.getNamaKetua();
        if (NamaKetua != null) {
            stmt.bindString(28, NamaKetua);
        }
 
        String NamaSekretaris = entity.getNamaSekretaris();
        if (NamaSekretaris != null) {
            stmt.bindString(29, NamaSekretaris);
        }
 
        String NamaAnggota = entity.getNamaAnggota();
        if (NamaAnggota != null) {
            stmt.bindString(30, NamaAnggota);
        }
 
        String NamaAnggotaBaru = entity.getNamaAnggotaBaru();
        if (NamaAnggotaBaru != null) {
            stmt.bindString(31, NamaAnggotaBaru);
        }
 
        Integer isDone = entity.getIsDone();
        if (isDone != null) {
            stmt.bindLong(32, isDone);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TPemeriksaan entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String NoPemeriksaan = entity.getNoPemeriksaan();
        if (NoPemeriksaan != null) {
            stmt.bindString(2, NoPemeriksaan);
        }
 
        String StorLoc = entity.getStorLoc();
        if (StorLoc != null) {
            stmt.bindString(3, StorLoc);
        }
 
        String Total = entity.getTotal();
        if (Total != null) {
            stmt.bindString(4, Total);
        }
 
        String TlskNo = entity.getTlskNo();
        if (TlskNo != null) {
            stmt.bindString(5, TlskNo);
        }
 
        String PoSapNo = entity.getPoSapNo();
        if (PoSapNo != null) {
            stmt.bindString(6, PoSapNo);
        }
 
        String PoMpNo = entity.getPoMpNo();
        if (PoMpNo != null) {
            stmt.bindString(7, PoMpNo);
        }
 
        String NoDoSmar = entity.getNoDoSmar();
        if (NoDoSmar != null) {
            stmt.bindString(8, NoDoSmar);
        }
 
        Integer LeadTime = entity.getLeadTime();
        if (LeadTime != null) {
            stmt.bindLong(9, LeadTime);
        }
 
        String CreatedDate = entity.getCreatedDate();
        if (CreatedDate != null) {
            stmt.bindString(10, CreatedDate);
        }
 
        String PlanCodeNo = entity.getPlanCodeNo();
        if (PlanCodeNo != null) {
            stmt.bindString(11, PlanCodeNo);
        }
 
        String PlantName = entity.getPlantName();
        if (PlantName != null) {
            stmt.bindString(12, PlantName);
        }
 
        String NoDoMims = entity.getNoDoMims();
        if (NoDoMims != null) {
            stmt.bindString(13, NoDoMims);
        }
 
        String DoStatus = entity.getDoStatus();
        if (DoStatus != null) {
            stmt.bindString(14, DoStatus);
        }
 
        String StatusPemeriksaan = entity.getStatusPemeriksaan();
        if (StatusPemeriksaan != null) {
            stmt.bindString(15, StatusPemeriksaan);
        }
 
        String Expeditions = entity.getExpeditions();
        if (Expeditions != null) {
            stmt.bindString(16, Expeditions);
        }
 
        String CourierPersonName = entity.getCourierPersonName();
        if (CourierPersonName != null) {
            stmt.bindString(17, CourierPersonName);
        }
 
        String KdPabrikan = entity.getKdPabrikan();
        if (KdPabrikan != null) {
            stmt.bindString(18, KdPabrikan);
        }
 
        String MaterialGroup = entity.getMaterialGroup();
        if (MaterialGroup != null) {
            stmt.bindString(19, MaterialGroup);
        }
 
        String NamaKategoriMaterial = entity.getNamaKategoriMaterial();
        if (NamaKategoriMaterial != null) {
            stmt.bindString(20, NamaKategoriMaterial);
        }
 
        String TanggalDiterima = entity.getTanggalDiterima();
        if (TanggalDiterima != null) {
            stmt.bindString(21, TanggalDiterima);
        }
 
        String PetugasPenerima = entity.getPetugasPenerima();
        if (PetugasPenerima != null) {
            stmt.bindString(22, PetugasPenerima);
        }
 
        String NamaKurir = entity.getNamaKurir();
        if (NamaKurir != null) {
            stmt.bindString(23, NamaKurir);
        }
 
        String NamaEkspedisi = entity.getNamaEkspedisi();
        if (NamaEkspedisi != null) {
            stmt.bindString(24, NamaEkspedisi);
        }
 
        String DoLineItem = entity.getDoLineItem();
        if (DoLineItem != null) {
            stmt.bindString(25, DoLineItem);
        }
 
        String NamaPabrikan = entity.getNamaPabrikan();
        if (NamaPabrikan != null) {
            stmt.bindString(26, NamaPabrikan);
        }
 
        String NamaManager = entity.getNamaManager();
        if (NamaManager != null) {
            stmt.bindString(27, NamaManager);
        }
 
        String NamaKetua = entity.getNamaKetua();
        if (NamaKetua != null) {
            stmt.bindString(28, NamaKetua);
        }
 
        String NamaSekretaris = entity.getNamaSekretaris();
        if (NamaSekretaris != null) {
            stmt.bindString(29, NamaSekretaris);
        }
 
        String NamaAnggota = entity.getNamaAnggota();
        if (NamaAnggota != null) {
            stmt.bindString(30, NamaAnggota);
        }
 
        String NamaAnggotaBaru = entity.getNamaAnggotaBaru();
        if (NamaAnggotaBaru != null) {
            stmt.bindString(31, NamaAnggotaBaru);
        }
 
        Integer isDone = entity.getIsDone();
        if (isDone != null) {
            stmt.bindLong(32, isDone);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TPemeriksaan readEntity(Cursor cursor, int offset) {
        TPemeriksaan entity = new TPemeriksaan( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // NoPemeriksaan
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // StorLoc
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // Total
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // TlskNo
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // PoSapNo
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // PoMpNo
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // NoDoSmar
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // LeadTime
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // CreatedDate
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // PlanCodeNo
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // PlantName
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // NoDoMims
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // DoStatus
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // StatusPemeriksaan
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // Expeditions
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // CourierPersonName
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // KdPabrikan
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // MaterialGroup
            cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19), // NamaKategoriMaterial
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // TanggalDiterima
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // PetugasPenerima
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // NamaKurir
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23), // NamaEkspedisi
            cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24), // DoLineItem
            cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25), // NamaPabrikan
            cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26), // NamaManager
            cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27), // NamaKetua
            cursor.isNull(offset + 28) ? null : cursor.getString(offset + 28), // NamaSekretaris
            cursor.isNull(offset + 29) ? null : cursor.getString(offset + 29), // NamaAnggota
            cursor.isNull(offset + 30) ? null : cursor.getString(offset + 30), // NamaAnggotaBaru
            cursor.isNull(offset + 31) ? null : cursor.getInt(offset + 31) // isDone
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TPemeriksaan entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setNoPemeriksaan(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setStorLoc(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTotal(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTlskNo(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setPoSapNo(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setPoMpNo(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setNoDoSmar(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setLeadTime(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setCreatedDate(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setPlanCodeNo(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setPlantName(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setNoDoMims(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setDoStatus(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setStatusPemeriksaan(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setExpeditions(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setCourierPersonName(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setKdPabrikan(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setMaterialGroup(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setNamaKategoriMaterial(cursor.isNull(offset + 19) ? null : cursor.getString(offset + 19));
        entity.setTanggalDiterima(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setPetugasPenerima(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setNamaKurir(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setNamaEkspedisi(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
        entity.setDoLineItem(cursor.isNull(offset + 24) ? null : cursor.getString(offset + 24));
        entity.setNamaPabrikan(cursor.isNull(offset + 25) ? null : cursor.getString(offset + 25));
        entity.setNamaManager(cursor.isNull(offset + 26) ? null : cursor.getString(offset + 26));
        entity.setNamaKetua(cursor.isNull(offset + 27) ? null : cursor.getString(offset + 27));
        entity.setNamaSekretaris(cursor.isNull(offset + 28) ? null : cursor.getString(offset + 28));
        entity.setNamaAnggota(cursor.isNull(offset + 29) ? null : cursor.getString(offset + 29));
        entity.setNamaAnggotaBaru(cursor.isNull(offset + 30) ? null : cursor.getString(offset + 30));
        entity.setIsDone(cursor.isNull(offset + 31) ? null : cursor.getInt(offset + 31));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TPemeriksaan entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TPemeriksaan entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TPemeriksaan entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
