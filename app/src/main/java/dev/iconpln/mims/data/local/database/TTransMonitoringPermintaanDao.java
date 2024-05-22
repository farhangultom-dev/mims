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
 * DAO for table "TTRANS_MONITORING_PERMINTAAN".
*/
public class TTransMonitoringPermintaanDao extends AbstractDao<TTransMonitoringPermintaan, Long> {

    public static final String TABLENAME = "TTRANS_MONITORING_PERMINTAAN";

    /**
     * Properties of entity TTransMonitoringPermintaan.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property NoPermintaan = new Property(1, String.class, "NoPermintaan", false, "NO_PERMINTAAN");
        public final static Property NoTransaksi = new Property(2, String.class, "NoTransaksi", false, "NO_TRANSAKSI");
        public final static Property StorLocTujuanName = new Property(3, String.class, "StorLocTujuanName", false, "STOR_LOC_TUJUAN_NAME");
        public final static Property KodePengeluaran = new Property(4, String.class, "KodePengeluaran", false, "KODE_PENGELUARAN");
        public final static Property StorLocTujuan = new Property(5, String.class, "StorLocTujuan", false, "STOR_LOC_TUJUAN");
        public final static Property CreatedBy = new Property(6, String.class, "CreatedBy", false, "CREATED_BY");
        public final static Property NoRepackaging = new Property(7, String.class, "NoRepackaging", false, "NO_REPACKAGING");
        public final static Property Plant = new Property(8, String.class, "Plant", false, "PLANT");
        public final static Property UpdatedBy = new Property(9, String.class, "UpdatedBy", false, "UPDATED_BY");
        public final static Property CreatedDate = new Property(10, String.class, "CreatedDate", false, "CREATED_DATE");
        public final static Property UpdatedDate = new Property(11, String.class, "UpdatedDate", false, "UPDATED_DATE");
        public final static Property JumlahKardus = new Property(12, Integer.class, "JumlahKardus", false, "JUMLAH_KARDUS");
        public final static Property StorLocAsalName = new Property(13, String.class, "StorLocAsalName", false, "STOR_LOC_ASAL_NAME");
        public final static Property TanggalPermintaan = new Property(14, String.class, "TanggalPermintaan", false, "TANGGAL_PERMINTAAN");
        public final static Property TanggalPengeluaran = new Property(15, String.class, "TanggalPengeluaran", false, "TANGGAL_PENGELUARAN");
        public final static Property PlantName = new Property(16, String.class, "PlantName", false, "PLANT_NAME");
        public final static Property StorLocAsal = new Property(17, String.class, "StorLocAsal", false, "STOR_LOC_ASAL");
        public final static Property IsActive = new Property(18, Integer.class, "IsActive", false, "IS_ACTIVE");
        public final static Property IsDone = new Property(19, Integer.class, "IsDone", false, "IS_DONE");
        public final static Property ValuationType = new Property(20, String.class, "ValuationType", false, "VALUATION_TYPE");
        public final static Property TotalQtyPermintaan = new Property(21, String.class, "totalQtyPermintaan", false, "TOTAL_QTY_PERMINTAAN");
        public final static Property TotalScanQty = new Property(22, String.class, "totalScanQty", false, "TOTAL_SCAN_QTY");
        public final static Property NoPengiriman = new Property(23, String.class, "noPengiriman", false, "NO_PENGIRIMAN");
    }


    public TTransMonitoringPermintaanDao(DaoConfig config) {
        super(config);
    }
    
    public TTransMonitoringPermintaanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TTRANS_MONITORING_PERMINTAAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"NO_PERMINTAAN\" TEXT," + // 1: NoPermintaan
                "\"NO_TRANSAKSI\" TEXT," + // 2: NoTransaksi
                "\"STOR_LOC_TUJUAN_NAME\" TEXT," + // 3: StorLocTujuanName
                "\"KODE_PENGELUARAN\" TEXT," + // 4: KodePengeluaran
                "\"STOR_LOC_TUJUAN\" TEXT," + // 5: StorLocTujuan
                "\"CREATED_BY\" TEXT," + // 6: CreatedBy
                "\"NO_REPACKAGING\" TEXT," + // 7: NoRepackaging
                "\"PLANT\" TEXT," + // 8: Plant
                "\"UPDATED_BY\" TEXT," + // 9: UpdatedBy
                "\"CREATED_DATE\" TEXT," + // 10: CreatedDate
                "\"UPDATED_DATE\" TEXT," + // 11: UpdatedDate
                "\"JUMLAH_KARDUS\" INTEGER," + // 12: JumlahKardus
                "\"STOR_LOC_ASAL_NAME\" TEXT," + // 13: StorLocAsalName
                "\"TANGGAL_PERMINTAAN\" TEXT," + // 14: TanggalPermintaan
                "\"TANGGAL_PENGELUARAN\" TEXT," + // 15: TanggalPengeluaran
                "\"PLANT_NAME\" TEXT," + // 16: PlantName
                "\"STOR_LOC_ASAL\" TEXT," + // 17: StorLocAsal
                "\"IS_ACTIVE\" INTEGER," + // 18: IsActive
                "\"IS_DONE\" INTEGER," + // 19: IsDone
                "\"VALUATION_TYPE\" TEXT," + // 20: ValuationType
                "\"TOTAL_QTY_PERMINTAAN\" TEXT," + // 21: totalQtyPermintaan
                "\"TOTAL_SCAN_QTY\" TEXT," + // 22: totalScanQty
                "\"NO_PENGIRIMAN\" TEXT);"); // 23: noPengiriman
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TTRANS_MONITORING_PERMINTAAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TTransMonitoringPermintaan entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String NoPermintaan = entity.getNoPermintaan();
        if (NoPermintaan != null) {
            stmt.bindString(2, NoPermintaan);
        }
 
        String NoTransaksi = entity.getNoTransaksi();
        if (NoTransaksi != null) {
            stmt.bindString(3, NoTransaksi);
        }
 
        String StorLocTujuanName = entity.getStorLocTujuanName();
        if (StorLocTujuanName != null) {
            stmt.bindString(4, StorLocTujuanName);
        }
 
        String KodePengeluaran = entity.getKodePengeluaran();
        if (KodePengeluaran != null) {
            stmt.bindString(5, KodePengeluaran);
        }
 
        String StorLocTujuan = entity.getStorLocTujuan();
        if (StorLocTujuan != null) {
            stmt.bindString(6, StorLocTujuan);
        }
 
        String CreatedBy = entity.getCreatedBy();
        if (CreatedBy != null) {
            stmt.bindString(7, CreatedBy);
        }
 
        String NoRepackaging = entity.getNoRepackaging();
        if (NoRepackaging != null) {
            stmt.bindString(8, NoRepackaging);
        }
 
        String Plant = entity.getPlant();
        if (Plant != null) {
            stmt.bindString(9, Plant);
        }
 
        String UpdatedBy = entity.getUpdatedBy();
        if (UpdatedBy != null) {
            stmt.bindString(10, UpdatedBy);
        }
 
        String CreatedDate = entity.getCreatedDate();
        if (CreatedDate != null) {
            stmt.bindString(11, CreatedDate);
        }
 
        String UpdatedDate = entity.getUpdatedDate();
        if (UpdatedDate != null) {
            stmt.bindString(12, UpdatedDate);
        }
 
        Integer JumlahKardus = entity.getJumlahKardus();
        if (JumlahKardus != null) {
            stmt.bindLong(13, JumlahKardus);
        }
 
        String StorLocAsalName = entity.getStorLocAsalName();
        if (StorLocAsalName != null) {
            stmt.bindString(14, StorLocAsalName);
        }
 
        String TanggalPermintaan = entity.getTanggalPermintaan();
        if (TanggalPermintaan != null) {
            stmt.bindString(15, TanggalPermintaan);
        }
 
        String TanggalPengeluaran = entity.getTanggalPengeluaran();
        if (TanggalPengeluaran != null) {
            stmt.bindString(16, TanggalPengeluaran);
        }
 
        String PlantName = entity.getPlantName();
        if (PlantName != null) {
            stmt.bindString(17, PlantName);
        }
 
        String StorLocAsal = entity.getStorLocAsal();
        if (StorLocAsal != null) {
            stmt.bindString(18, StorLocAsal);
        }
 
        Integer IsActive = entity.getIsActive();
        if (IsActive != null) {
            stmt.bindLong(19, IsActive);
        }
 
        Integer IsDone = entity.getIsDone();
        if (IsDone != null) {
            stmt.bindLong(20, IsDone);
        }
 
        String ValuationType = entity.getValuationType();
        if (ValuationType != null) {
            stmt.bindString(21, ValuationType);
        }
 
        String totalQtyPermintaan = entity.getTotalQtyPermintaan();
        if (totalQtyPermintaan != null) {
            stmt.bindString(22, totalQtyPermintaan);
        }
 
        String totalScanQty = entity.getTotalScanQty();
        if (totalScanQty != null) {
            stmt.bindString(23, totalScanQty);
        }
 
        String noPengiriman = entity.getNoPengiriman();
        if (noPengiriman != null) {
            stmt.bindString(24, noPengiriman);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TTransMonitoringPermintaan entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String NoPermintaan = entity.getNoPermintaan();
        if (NoPermintaan != null) {
            stmt.bindString(2, NoPermintaan);
        }
 
        String NoTransaksi = entity.getNoTransaksi();
        if (NoTransaksi != null) {
            stmt.bindString(3, NoTransaksi);
        }
 
        String StorLocTujuanName = entity.getStorLocTujuanName();
        if (StorLocTujuanName != null) {
            stmt.bindString(4, StorLocTujuanName);
        }
 
        String KodePengeluaran = entity.getKodePengeluaran();
        if (KodePengeluaran != null) {
            stmt.bindString(5, KodePengeluaran);
        }
 
        String StorLocTujuan = entity.getStorLocTujuan();
        if (StorLocTujuan != null) {
            stmt.bindString(6, StorLocTujuan);
        }
 
        String CreatedBy = entity.getCreatedBy();
        if (CreatedBy != null) {
            stmt.bindString(7, CreatedBy);
        }
 
        String NoRepackaging = entity.getNoRepackaging();
        if (NoRepackaging != null) {
            stmt.bindString(8, NoRepackaging);
        }
 
        String Plant = entity.getPlant();
        if (Plant != null) {
            stmt.bindString(9, Plant);
        }
 
        String UpdatedBy = entity.getUpdatedBy();
        if (UpdatedBy != null) {
            stmt.bindString(10, UpdatedBy);
        }
 
        String CreatedDate = entity.getCreatedDate();
        if (CreatedDate != null) {
            stmt.bindString(11, CreatedDate);
        }
 
        String UpdatedDate = entity.getUpdatedDate();
        if (UpdatedDate != null) {
            stmt.bindString(12, UpdatedDate);
        }
 
        Integer JumlahKardus = entity.getJumlahKardus();
        if (JumlahKardus != null) {
            stmt.bindLong(13, JumlahKardus);
        }
 
        String StorLocAsalName = entity.getStorLocAsalName();
        if (StorLocAsalName != null) {
            stmt.bindString(14, StorLocAsalName);
        }
 
        String TanggalPermintaan = entity.getTanggalPermintaan();
        if (TanggalPermintaan != null) {
            stmt.bindString(15, TanggalPermintaan);
        }
 
        String TanggalPengeluaran = entity.getTanggalPengeluaran();
        if (TanggalPengeluaran != null) {
            stmt.bindString(16, TanggalPengeluaran);
        }
 
        String PlantName = entity.getPlantName();
        if (PlantName != null) {
            stmt.bindString(17, PlantName);
        }
 
        String StorLocAsal = entity.getStorLocAsal();
        if (StorLocAsal != null) {
            stmt.bindString(18, StorLocAsal);
        }
 
        Integer IsActive = entity.getIsActive();
        if (IsActive != null) {
            stmt.bindLong(19, IsActive);
        }
 
        Integer IsDone = entity.getIsDone();
        if (IsDone != null) {
            stmt.bindLong(20, IsDone);
        }
 
        String ValuationType = entity.getValuationType();
        if (ValuationType != null) {
            stmt.bindString(21, ValuationType);
        }
 
        String totalQtyPermintaan = entity.getTotalQtyPermintaan();
        if (totalQtyPermintaan != null) {
            stmt.bindString(22, totalQtyPermintaan);
        }
 
        String totalScanQty = entity.getTotalScanQty();
        if (totalScanQty != null) {
            stmt.bindString(23, totalScanQty);
        }
 
        String noPengiriman = entity.getNoPengiriman();
        if (noPengiriman != null) {
            stmt.bindString(24, noPengiriman);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TTransMonitoringPermintaan readEntity(Cursor cursor, int offset) {
        TTransMonitoringPermintaan entity = new TTransMonitoringPermintaan( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // NoPermintaan
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // NoTransaksi
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // StorLocTujuanName
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // KodePengeluaran
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // StorLocTujuan
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // CreatedBy
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // NoRepackaging
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // Plant
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // UpdatedBy
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // CreatedDate
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // UpdatedDate
            cursor.isNull(offset + 12) ? null : cursor.getInt(offset + 12), // JumlahKardus
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // StorLocAsalName
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // TanggalPermintaan
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // TanggalPengeluaran
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // PlantName
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // StorLocAsal
            cursor.isNull(offset + 18) ? null : cursor.getInt(offset + 18), // IsActive
            cursor.isNull(offset + 19) ? null : cursor.getInt(offset + 19), // IsDone
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // ValuationType
            cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21), // totalQtyPermintaan
            cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22), // totalScanQty
            cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23) // noPengiriman
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TTransMonitoringPermintaan entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setNoPermintaan(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setNoTransaksi(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setStorLocTujuanName(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setKodePengeluaran(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setStorLocTujuan(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setCreatedBy(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setNoRepackaging(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setPlant(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setUpdatedBy(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setCreatedDate(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setUpdatedDate(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setJumlahKardus(cursor.isNull(offset + 12) ? null : cursor.getInt(offset + 12));
        entity.setStorLocAsalName(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setTanggalPermintaan(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setTanggalPengeluaran(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setPlantName(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setStorLocAsal(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setIsActive(cursor.isNull(offset + 18) ? null : cursor.getInt(offset + 18));
        entity.setIsDone(cursor.isNull(offset + 19) ? null : cursor.getInt(offset + 19));
        entity.setValuationType(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setTotalQtyPermintaan(cursor.isNull(offset + 21) ? null : cursor.getString(offset + 21));
        entity.setTotalScanQty(cursor.isNull(offset + 22) ? null : cursor.getString(offset + 22));
        entity.setNoPengiriman(cursor.isNull(offset + 23) ? null : cursor.getString(offset + 23));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TTransMonitoringPermintaan entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TTransMonitoringPermintaan entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TTransMonitoringPermintaan entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
