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
 * DAO for table "TPRIVILEGE".
*/
public class TPrivilegeDao extends AbstractDao<TPrivilege, Long> {

    public static final String TABLENAME = "TPRIVILEGE";

    /**
     * Properties of entity TPrivilege.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ModuleId = new Property(1, String.class, "ModuleId", false, "MODULE_ID");
        public final static Property IsActive = new Property(2, String.class, "IsActive", false, "IS_ACTIVE");
        public final static Property RoleId = new Property(3, String.class, "RoleId", false, "ROLE_ID");
        public final static Property MethodId = new Property(4, String.class, "MethodId", false, "METHOD_ID");
        public final static Property MethodValue = new Property(5, String.class, "MethodValue", false, "METHOD_VALUE");
    }


    public TPrivilegeDao(DaoConfig config) {
        super(config);
    }
    
    public TPrivilegeDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TPRIVILEGE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"MODULE_ID\" TEXT," + // 1: ModuleId
                "\"IS_ACTIVE\" TEXT," + // 2: IsActive
                "\"ROLE_ID\" TEXT," + // 3: RoleId
                "\"METHOD_ID\" TEXT," + // 4: MethodId
                "\"METHOD_VALUE\" TEXT);"); // 5: MethodValue
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TPRIVILEGE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TPrivilege entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String ModuleId = entity.getModuleId();
        if (ModuleId != null) {
            stmt.bindString(2, ModuleId);
        }
 
        String IsActive = entity.getIsActive();
        if (IsActive != null) {
            stmt.bindString(3, IsActive);
        }
 
        String RoleId = entity.getRoleId();
        if (RoleId != null) {
            stmt.bindString(4, RoleId);
        }
 
        String MethodId = entity.getMethodId();
        if (MethodId != null) {
            stmt.bindString(5, MethodId);
        }
 
        String MethodValue = entity.getMethodValue();
        if (MethodValue != null) {
            stmt.bindString(6, MethodValue);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TPrivilege entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String ModuleId = entity.getModuleId();
        if (ModuleId != null) {
            stmt.bindString(2, ModuleId);
        }
 
        String IsActive = entity.getIsActive();
        if (IsActive != null) {
            stmt.bindString(3, IsActive);
        }
 
        String RoleId = entity.getRoleId();
        if (RoleId != null) {
            stmt.bindString(4, RoleId);
        }
 
        String MethodId = entity.getMethodId();
        if (MethodId != null) {
            stmt.bindString(5, MethodId);
        }
 
        String MethodValue = entity.getMethodValue();
        if (MethodValue != null) {
            stmt.bindString(6, MethodValue);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TPrivilege readEntity(Cursor cursor, int offset) {
        TPrivilege entity = new TPrivilege( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // ModuleId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // IsActive
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // RoleId
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // MethodId
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // MethodValue
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TPrivilege entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setModuleId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setIsActive(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setRoleId(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setMethodId(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setMethodValue(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TPrivilege entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TPrivilege entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TPrivilege entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
