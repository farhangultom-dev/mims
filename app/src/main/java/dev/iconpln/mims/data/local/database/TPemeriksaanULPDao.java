package dev.iconpln.mims.data.local.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "TPEMERIKSAAN_ULP".
 */
public class TPemeriksaanULPDao extends AbstractDao<TPemeriksaanULP, Long> {

    public static final String TABLENAME = "TPEMERIKSAAN_ULP";

    public TPemeriksaanULPDao(DaoConfig config) {
        super(config);
    }


    public TPemeriksaanULPDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS " : "";
        db.execSQL("CREATE TABLE " + constraint + "\"TPEMERIKSAAN_ULP\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"NO_PENGIRIMAN\" TEXT," + // 1: NoPengiriman
                "\"NO_PERMINTAAN\" TEXT," + // 2: NoPermintaan
                "\"STATUS_PEMERIKSAAN\" TEXT," + // 3: StatusPemeriksaan
                "\"DELIVERY_DATE\" TEXT," + // 4: DeliveryDate
                "\"STATUS_PENERIMAAN\" TEXT," + // 5: StatusPenerimaan
                "\"JUMLAH_KARDUS\" TEXT," + // 6: JumlahKardus
                "\"GUDANG_ASAL\" TEXT," + // 7: GudangAsal
                "\"NO_REPACKAGING\" TEXT," + // 8: NoRepackaging
                "\"GUDANG_TUJUAN\" TEXT," + // 9: GudangTujuan
                "\"TANGGAL_PEMERIKSAAN\" TEXT," + // 10: TanggalPemeriksaan
                "\"KEPALA_GUDANG\" TEXT," + // 11: KepalaGudang
                "\"PEJABAT_PEMERIKSA\" TEXT," + // 12: PejabatPemeriksa
                "\"JABATAN_PEMERIKSA\" TEXT," + // 13: JabatanPemeriksa
                "\"NAMA_PETUGAS_PEMERIKSA\" TEXT," + // 14: NamaPetugasPemeriksa
                "\"JABATAN_PETUGAS_PEMERIKSA\" TEXT," + // 15: JabatanPetugasPemeriksa
                "\"IS_DONE\" TEXT);"); // 16: IsDone
    }

    /**
     * Drops the underlying database table.
     */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TPEMERIKSAAN_ULP\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TPemeriksaanULP entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        String NoPengiriman = entity.getNoPengiriman();
        if (NoPengiriman != null) {
            stmt.bindString(2, NoPengiriman);
        }

        String NoPermintaan = entity.getNoPermintaan();
        if (NoPermintaan != null) {
            stmt.bindString(3, NoPermintaan);
        }

        String StatusPemeriksaan = entity.getStatusPemeriksaan();
        if (StatusPemeriksaan != null) {
            stmt.bindString(4, StatusPemeriksaan);
        }

        String DeliveryDate = entity.getDeliveryDate();
        if (DeliveryDate != null) {
            stmt.bindString(5, DeliveryDate);
        }

        String StatusPenerimaan = entity.getStatusPenerimaan();
        if (StatusPenerimaan != null) {
            stmt.bindString(6, StatusPenerimaan);
        }

        String JumlahKardus = entity.getJumlahKardus();
        if (JumlahKardus != null) {
            stmt.bindString(7, JumlahKardus);
        }

        String GudangAsal = entity.getGudangAsal();
        if (GudangAsal != null) {
            stmt.bindString(8, GudangAsal);
        }

        String NoRepackaging = entity.getNoRepackaging();
        if (NoRepackaging != null) {
            stmt.bindString(9, NoRepackaging);
        }

        String GudangTujuan = entity.getGudangTujuan();
        if (GudangTujuan != null) {
            stmt.bindString(10, GudangTujuan);
        }

        String TanggalPemeriksaan = entity.getTanggalPemeriksaan();
        if (TanggalPemeriksaan != null) {
            stmt.bindString(11, TanggalPemeriksaan);
        }

        String KepalaGudang = entity.getKepalaGudang();
        if (KepalaGudang != null) {
            stmt.bindString(12, KepalaGudang);
        }

        String PejabatPemeriksa = entity.getPejabatPemeriksa();
        if (PejabatPemeriksa != null) {
            stmt.bindString(13, PejabatPemeriksa);
        }

        String JabatanPemeriksa = entity.getJabatanPemeriksa();
        if (JabatanPemeriksa != null) {
            stmt.bindString(14, JabatanPemeriksa);
        }

        String NamaPetugasPemeriksa = entity.getNamaPetugasPemeriksa();
        if (NamaPetugasPemeriksa != null) {
            stmt.bindString(15, NamaPetugasPemeriksa);
        }

        String JabatanPetugasPemeriksa = entity.getJabatanPetugasPemeriksa();
        if (JabatanPetugasPemeriksa != null) {
            stmt.bindString(16, JabatanPetugasPemeriksa);
        }

        String IsDone = entity.getIsDone();
        if (IsDone != null) {
            stmt.bindString(17, IsDone);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TPemeriksaanULP entity) {
        stmt.clearBindings();

        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }

        String NoPengiriman = entity.getNoPengiriman();
        if (NoPengiriman != null) {
            stmt.bindString(2, NoPengiriman);
        }

        String NoPermintaan = entity.getNoPermintaan();
        if (NoPermintaan != null) {
            stmt.bindString(3, NoPermintaan);
        }

        String StatusPemeriksaan = entity.getStatusPemeriksaan();
        if (StatusPemeriksaan != null) {
            stmt.bindString(4, StatusPemeriksaan);
        }

        String DeliveryDate = entity.getDeliveryDate();
        if (DeliveryDate != null) {
            stmt.bindString(5, DeliveryDate);
        }

        String StatusPenerimaan = entity.getStatusPenerimaan();
        if (StatusPenerimaan != null) {
            stmt.bindString(6, StatusPenerimaan);
        }

        String JumlahKardus = entity.getJumlahKardus();
        if (JumlahKardus != null) {
            stmt.bindString(7, JumlahKardus);
        }

        String GudangAsal = entity.getGudangAsal();
        if (GudangAsal != null) {
            stmt.bindString(8, GudangAsal);
        }

        String NoRepackaging = entity.getNoRepackaging();
        if (NoRepackaging != null) {
            stmt.bindString(9, NoRepackaging);
        }

        String GudangTujuan = entity.getGudangTujuan();
        if (GudangTujuan != null) {
            stmt.bindString(10, GudangTujuan);
        }

        String TanggalPemeriksaan = entity.getTanggalPemeriksaan();
        if (TanggalPemeriksaan != null) {
            stmt.bindString(11, TanggalPemeriksaan);
        }

        String KepalaGudang = entity.getKepalaGudang();
        if (KepalaGudang != null) {
            stmt.bindString(12, KepalaGudang);
        }

        String PejabatPemeriksa = entity.getPejabatPemeriksa();
        if (PejabatPemeriksa != null) {
            stmt.bindString(13, PejabatPemeriksa);
        }

        String JabatanPemeriksa = entity.getJabatanPemeriksa();
        if (JabatanPemeriksa != null) {
            stmt.bindString(14, JabatanPemeriksa);
        }

        String NamaPetugasPemeriksa = entity.getNamaPetugasPemeriksa();
        if (NamaPetugasPemeriksa != null) {
            stmt.bindString(15, NamaPetugasPemeriksa);
        }

        String JabatanPetugasPemeriksa = entity.getJabatanPetugasPemeriksa();
        if (JabatanPetugasPemeriksa != null) {
            stmt.bindString(16, JabatanPetugasPemeriksa);
        }

        String IsDone = entity.getIsDone();
        if (IsDone != null) {
            stmt.bindString(17, IsDone);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    @Override
    public TPemeriksaanULP readEntity(Cursor cursor, int offset) {
        TPemeriksaanULP entity = new TPemeriksaanULP( //
                cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
                cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // NoPengiriman
                cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // NoPermintaan
                cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // StatusPemeriksaan
                cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // DeliveryDate
                cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // StatusPenerimaan
                cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // JumlahKardus
                cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // GudangAsal
                cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // NoRepackaging
                cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // GudangTujuan
                cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // TanggalPemeriksaan
                cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // KepalaGudang
                cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // PejabatPemeriksa
                cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // JabatanPemeriksa
                cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // NamaPetugasPemeriksa
                cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // JabatanPetugasPemeriksa
                cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16) // IsDone
        );
        return entity;
    }

    @Override
    public void readEntity(Cursor cursor, TPemeriksaanULP entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setNoPengiriman(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setNoPermintaan(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setStatusPemeriksaan(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDeliveryDate(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setStatusPenerimaan(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setJumlahKardus(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setGudangAsal(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setNoRepackaging(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setGudangTujuan(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setTanggalPemeriksaan(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setKepalaGudang(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setPejabatPemeriksa(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setJabatanPemeriksa(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setNamaPetugasPemeriksa(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setJabatanPetugasPemeriksa(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setIsDone(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
    }

    @Override
    protected final Long updateKeyAfterInsert(TPemeriksaanULP entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }

    @Override
    public Long getKey(TPemeriksaanULP entity) {
        if (entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TPemeriksaanULP entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }

    /**
     * Properties of entity TPemeriksaanULP.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property NoPengiriman = new Property(1, String.class, "NoPengiriman", false, "NO_PENGIRIMAN");
        public final static Property NoPermintaan = new Property(2, String.class, "NoPermintaan", false, "NO_PERMINTAAN");
        public final static Property StatusPemeriksaan = new Property(3, String.class, "StatusPemeriksaan", false, "STATUS_PEMERIKSAAN");
        public final static Property DeliveryDate = new Property(4, String.class, "DeliveryDate", false, "DELIVERY_DATE");
        public final static Property StatusPenerimaan = new Property(5, String.class, "StatusPenerimaan", false, "STATUS_PENERIMAAN");
        public final static Property JumlahKardus = new Property(6, String.class, "JumlahKardus", false, "JUMLAH_KARDUS");
        public final static Property GudangAsal = new Property(7, String.class, "GudangAsal", false, "GUDANG_ASAL");
        public final static Property NoRepackaging = new Property(8, String.class, "NoRepackaging", false, "NO_REPACKAGING");
        public final static Property GudangTujuan = new Property(9, String.class, "GudangTujuan", false, "GUDANG_TUJUAN");
        public final static Property TanggalPemeriksaan = new Property(10, String.class, "TanggalPemeriksaan", false, "TANGGAL_PEMERIKSAAN");
        public final static Property KepalaGudang = new Property(11, String.class, "KepalaGudang", false, "KEPALA_GUDANG");
        public final static Property PejabatPemeriksa = new Property(12, String.class, "PejabatPemeriksa", false, "PEJABAT_PEMERIKSA");
        public final static Property JabatanPemeriksa = new Property(13, String.class, "JabatanPemeriksa", false, "JABATAN_PEMERIKSA");
        public final static Property NamaPetugasPemeriksa = new Property(14, String.class, "NamaPetugasPemeriksa", false, "NAMA_PETUGAS_PEMERIKSA");
        public final static Property JabatanPetugasPemeriksa = new Property(15, String.class, "JabatanPetugasPemeriksa", false, "JABATAN_PETUGAS_PEMERIKSA");
        public final static Property IsDone = new Property(16, String.class, "IsDone", false, "IS_DONE");
    }

}
