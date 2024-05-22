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
 * DAO for table "TDO_STATUS".
*/
public class TDoStatusDao extends AbstractDao<TDoStatus, Long> {

    public static final String TABLENAME = "TDO_STATUS";

    /**
     * Properties of entity TDoStatus.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property KodeDo = new Property(1, String.class, "KodeDo", false, "KODE_DO");
        public final static Property Keterangan = new Property(2, String.class, "Keterangan", false, "KETERANGAN");
        public final static Property BackgroundColor = new Property(3, String.class, "BackgroundColor", false, "BACKGROUND_COLOR");
        public final static Property TextColor = new Property(4, String.class, "TextColor", false, "TEXT_COLOR");
    }


    public TDoStatusDao(DaoConfig config) {
        super(config);
    }
    
    public TDoStatusDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TDO_STATUS\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"KODE_DO\" TEXT," + // 1: KodeDo
                "\"KETERANGAN\" TEXT," + // 2: Keterangan
                "\"BACKGROUND_COLOR\" TEXT," + // 3: BackgroundColor
                "\"TEXT_COLOR\" TEXT);"); // 4: TextColor
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TDO_STATUS\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TDoStatus entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String KodeDo = entity.getKodeDo();
        if (KodeDo != null) {
            stmt.bindString(2, KodeDo);
        }
 
        String Keterangan = entity.getKeterangan();
        if (Keterangan != null) {
            stmt.bindString(3, Keterangan);
        }
 
        String BackgroundColor = entity.getBackgroundColor();
        if (BackgroundColor != null) {
            stmt.bindString(4, BackgroundColor);
        }
 
        String TextColor = entity.getTextColor();
        if (TextColor != null) {
            stmt.bindString(5, TextColor);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TDoStatus entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String KodeDo = entity.getKodeDo();
        if (KodeDo != null) {
            stmt.bindString(2, KodeDo);
        }
 
        String Keterangan = entity.getKeterangan();
        if (Keterangan != null) {
            stmt.bindString(3, Keterangan);
        }
 
        String BackgroundColor = entity.getBackgroundColor();
        if (BackgroundColor != null) {
            stmt.bindString(4, BackgroundColor);
        }
 
        String TextColor = entity.getTextColor();
        if (TextColor != null) {
            stmt.bindString(5, TextColor);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TDoStatus readEntity(Cursor cursor, int offset) {
        TDoStatus entity = new TDoStatus( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // KodeDo
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // Keterangan
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // BackgroundColor
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // TextColor
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TDoStatus entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setKodeDo(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setKeterangan(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setBackgroundColor(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setTextColor(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TDoStatus entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TDoStatus entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TDoStatus entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
