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
 * DAO for table "TMONITORING".
*/
public class TMonitoringDao extends AbstractDao<TMonitoring, Long> {

    public static final String TABLENAME = "TMONITORING";

    /**
     * Properties of entity TMonitoring.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property NomorMaterial = new Property(1, String.class, "NomorMaterial", false, "NOMOR_MATERIAL");
        public final static Property KdPabrikan = new Property(2, String.class, "KdPabrikan", false, "KD_PABRIKAN");
        public final static Property MatSapNo = new Property(3, String.class, "MatSapNo", false, "MAT_SAP_NO");
        public final static Property StoreLoc = new Property(4, String.class, "StoreLoc", false, "STORE_LOC");
        public final static Property Uom = new Property(5, String.class, "Uom", false, "UOM");
        public final static Property Unit = new Property(6, String.class, "Unit", false, "UNIT");
        public final static Property MaterialDesc = new Property(7, String.class, "MaterialDesc", false, "MATERIAL_DESC");
        public final static Property TlskNo = new Property(8, String.class, "TlskNo", false, "TLSK_NO");
        public final static Property PoSapNo = new Property(9, String.class, "PoSapNo", false, "PO_SAP_NO");
        public final static Property PoMpNo = new Property(10, String.class, "PoMpNo", false, "PO_MP_NO");
        public final static Property Qty = new Property(11, String.class, "Qty", false, "QTY");
        public final static Property LeadTime = new Property(12, String.class, "LeadTime", false, "LEAD_TIME");
        public final static Property CreatedDate = new Property(13, String.class, "CreatedDate", false, "CREATED_DATE");
    }


    public TMonitoringDao(DaoConfig config) {
        super(config);
    }
    
    public TMonitoringDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TMONITORING\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"NOMOR_MATERIAL\" TEXT," + // 1: NomorMaterial
                "\"KD_PABRIKAN\" TEXT," + // 2: KdPabrikan
                "\"MAT_SAP_NO\" TEXT," + // 3: MatSapNo
                "\"STORE_LOC\" TEXT," + // 4: StoreLoc
                "\"UOM\" TEXT," + // 5: Uom
                "\"UNIT\" TEXT," + // 6: Unit
                "\"MATERIAL_DESC\" TEXT," + // 7: MaterialDesc
                "\"TLSK_NO\" TEXT," + // 8: TlskNo
                "\"PO_SAP_NO\" TEXT," + // 9: PoSapNo
                "\"PO_MP_NO\" TEXT," + // 10: PoMpNo
                "\"QTY\" TEXT," + // 11: Qty
                "\"LEAD_TIME\" TEXT," + // 12: LeadTime
                "\"CREATED_DATE\" TEXT);"); // 13: CreatedDate
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TMONITORING\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TMonitoring entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String NomorMaterial = entity.getNomorMaterial();
        if (NomorMaterial != null) {
            stmt.bindString(2, NomorMaterial);
        }
 
        String KdPabrikan = entity.getKdPabrikan();
        if (KdPabrikan != null) {
            stmt.bindString(3, KdPabrikan);
        }
 
        String MatSapNo = entity.getMatSapNo();
        if (MatSapNo != null) {
            stmt.bindString(4, MatSapNo);
        }
 
        String StoreLoc = entity.getStoreLoc();
        if (StoreLoc != null) {
            stmt.bindString(5, StoreLoc);
        }
 
        String Uom = entity.getUom();
        if (Uom != null) {
            stmt.bindString(6, Uom);
        }
 
        String Unit = entity.getUnit();
        if (Unit != null) {
            stmt.bindString(7, Unit);
        }
 
        String MaterialDesc = entity.getMaterialDesc();
        if (MaterialDesc != null) {
            stmt.bindString(8, MaterialDesc);
        }
 
        String TlskNo = entity.getTlskNo();
        if (TlskNo != null) {
            stmt.bindString(9, TlskNo);
        }
 
        String PoSapNo = entity.getPoSapNo();
        if (PoSapNo != null) {
            stmt.bindString(10, PoSapNo);
        }
 
        String PoMpNo = entity.getPoMpNo();
        if (PoMpNo != null) {
            stmt.bindString(11, PoMpNo);
        }
 
        String Qty = entity.getQty();
        if (Qty != null) {
            stmt.bindString(12, Qty);
        }
 
        String LeadTime = entity.getLeadTime();
        if (LeadTime != null) {
            stmt.bindString(13, LeadTime);
        }
 
        String CreatedDate = entity.getCreatedDate();
        if (CreatedDate != null) {
            stmt.bindString(14, CreatedDate);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TMonitoring entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String NomorMaterial = entity.getNomorMaterial();
        if (NomorMaterial != null) {
            stmt.bindString(2, NomorMaterial);
        }
 
        String KdPabrikan = entity.getKdPabrikan();
        if (KdPabrikan != null) {
            stmt.bindString(3, KdPabrikan);
        }
 
        String MatSapNo = entity.getMatSapNo();
        if (MatSapNo != null) {
            stmt.bindString(4, MatSapNo);
        }
 
        String StoreLoc = entity.getStoreLoc();
        if (StoreLoc != null) {
            stmt.bindString(5, StoreLoc);
        }
 
        String Uom = entity.getUom();
        if (Uom != null) {
            stmt.bindString(6, Uom);
        }
 
        String Unit = entity.getUnit();
        if (Unit != null) {
            stmt.bindString(7, Unit);
        }
 
        String MaterialDesc = entity.getMaterialDesc();
        if (MaterialDesc != null) {
            stmt.bindString(8, MaterialDesc);
        }
 
        String TlskNo = entity.getTlskNo();
        if (TlskNo != null) {
            stmt.bindString(9, TlskNo);
        }
 
        String PoSapNo = entity.getPoSapNo();
        if (PoSapNo != null) {
            stmt.bindString(10, PoSapNo);
        }
 
        String PoMpNo = entity.getPoMpNo();
        if (PoMpNo != null) {
            stmt.bindString(11, PoMpNo);
        }
 
        String Qty = entity.getQty();
        if (Qty != null) {
            stmt.bindString(12, Qty);
        }
 
        String LeadTime = entity.getLeadTime();
        if (LeadTime != null) {
            stmt.bindString(13, LeadTime);
        }
 
        String CreatedDate = entity.getCreatedDate();
        if (CreatedDate != null) {
            stmt.bindString(14, CreatedDate);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TMonitoring readEntity(Cursor cursor, int offset) {
        TMonitoring entity = new TMonitoring( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // NomorMaterial
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // KdPabrikan
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // MatSapNo
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // StoreLoc
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // Uom
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // Unit
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // MaterialDesc
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // TlskNo
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // PoSapNo
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // PoMpNo
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // Qty
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // LeadTime
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13) // CreatedDate
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TMonitoring entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setNomorMaterial(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setKdPabrikan(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setMatSapNo(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setStoreLoc(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setUom(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setUnit(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setMaterialDesc(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setTlskNo(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setPoSapNo(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setPoMpNo(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setQty(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setLeadTime(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setCreatedDate(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TMonitoring entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TMonitoring entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TMonitoring entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
