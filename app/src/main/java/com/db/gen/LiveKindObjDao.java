package com.db.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.datas.LiveKindObj;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "LIVE_KIND_OBJ".
*/
public class LiveKindObjDao extends AbstractDao<LiveKindObj, Long> {

    public static final String TABLENAME = "LIVE_KIND_OBJ";

    /**
     * Properties of entity LiveKindObj.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property KeyL = new Property(0, Long.class, "keyL", true, "_id");
        public final static Property ColName = new Property(1, String.class, "colName", false, "COL_NAME");
        public final static Property ColId = new Property(2, String.class, "colId", false, "COL_ID");
        public final static Property HaveChildren = new Property(3, boolean.class, "haveChildren", false, "HAVE_CHILDREN");
        public final static Property ChalNum = new Property(4, int.class, "chalNum", false, "CHAL_NUM");
        public final static Property PageNum = new Property(5, int.class, "pageNum", false, "PAGE_NUM");
        public final static Property CKNum = new Property(6, int.class, "cKNum", false, "C_KNUM");
        public final static Property PType = new Property(7, int.class, "pType", false, "P_TYPE");
    }


    public LiveKindObjDao(DaoConfig config) {
        super(config);
    }
    
    public LiveKindObjDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"LIVE_KIND_OBJ\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: keyL
                "\"COL_NAME\" TEXT NOT NULL ," + // 1: colName
                "\"COL_ID\" TEXT NOT NULL ," + // 2: colId
                "\"HAVE_CHILDREN\" INTEGER NOT NULL ," + // 3: haveChildren
                "\"CHAL_NUM\" INTEGER NOT NULL ," + // 4: chalNum
                "\"PAGE_NUM\" INTEGER NOT NULL ," + // 5: pageNum
                "\"C_KNUM\" INTEGER NOT NULL ," + // 6: cKNum
                "\"P_TYPE\" INTEGER NOT NULL );"); // 7: pType
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_LIVE_KIND_OBJ_COL_ID_COL_NAME ON \"LIVE_KIND_OBJ\"" +
                " (\"COL_ID\" ASC,\"COL_NAME\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LIVE_KIND_OBJ\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, LiveKindObj entity) {
        stmt.clearBindings();
 
        Long keyL = entity.getKeyL();
        if (keyL != null) {
            stmt.bindLong(1, keyL);
        }
        stmt.bindString(2, entity.getColName());
        stmt.bindString(3, entity.getColId());
        stmt.bindLong(4, entity.getHaveChildren() ? 1L: 0L);
        stmt.bindLong(5, entity.getChalNum());
        stmt.bindLong(6, entity.getPageNum());
        stmt.bindLong(7, entity.getCKNum());
        stmt.bindLong(8, entity.getPType());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, LiveKindObj entity) {
        stmt.clearBindings();
 
        Long keyL = entity.getKeyL();
        if (keyL != null) {
            stmt.bindLong(1, keyL);
        }
        stmt.bindString(2, entity.getColName());
        stmt.bindString(3, entity.getColId());
        stmt.bindLong(4, entity.getHaveChildren() ? 1L: 0L);
        stmt.bindLong(5, entity.getChalNum());
        stmt.bindLong(6, entity.getPageNum());
        stmt.bindLong(7, entity.getCKNum());
        stmt.bindLong(8, entity.getPType());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public LiveKindObj readEntity(Cursor cursor, int offset) {
        LiveKindObj entity = new LiveKindObj( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // keyL
            cursor.getString(offset + 1), // colName
            cursor.getString(offset + 2), // colId
            cursor.getShort(offset + 3) != 0, // haveChildren
            cursor.getInt(offset + 4), // chalNum
            cursor.getInt(offset + 5), // pageNum
            cursor.getInt(offset + 6), // cKNum
            cursor.getInt(offset + 7) // pType
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, LiveKindObj entity, int offset) {
        entity.setKeyL(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setColName(cursor.getString(offset + 1));
        entity.setColId(cursor.getString(offset + 2));
        entity.setHaveChildren(cursor.getShort(offset + 3) != 0);
        entity.setChalNum(cursor.getInt(offset + 4));
        entity.setPageNum(cursor.getInt(offset + 5));
        entity.setCKNum(cursor.getInt(offset + 6));
        entity.setPType(cursor.getInt(offset + 7));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(LiveKindObj entity, long rowId) {
        entity.setKeyL(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(LiveKindObj entity) {
        if(entity != null) {
            return entity.getKeyL();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(LiveKindObj entity) {
        return entity.getKeyL() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
