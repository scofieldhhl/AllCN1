package com.db.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.db.converters.ListStrConverter;
import java.util.List;

import com.datas.EpgObj;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "EPG_OBJ".
*/
public class EpgObjDao extends AbstractDao<EpgObj, Long> {

    public static final String TABLENAME = "EPG_OBJ";

    /**
     * Properties of entity EpgObj.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property EpgId = new Property(1, int.class, "epgId", false, "EPG_ID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Dates = new Property(3, String.class, "dates", false, "DATES");
    }

    private final ListStrConverter datesConverter = new ListStrConverter();

    public EpgObjDao(DaoConfig config) {
        super(config);
    }
    
    public EpgObjDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"EPG_OBJ\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"EPG_ID\" INTEGER NOT NULL ," + // 1: epgId
                "\"NAME\" TEXT," + // 2: name
                "\"DATES\" TEXT);"); // 3: dates
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_EPG_OBJ_EPG_ID_NAME ON \"EPG_OBJ\"" +
                " (\"EPG_ID\" ASC,\"NAME\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"EPG_OBJ\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, EpgObj entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getEpgId());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        List dates = entity.getDates();
        if (dates != null) {
            stmt.bindString(4, datesConverter.convertToDatabaseValue(dates));
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, EpgObj entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getEpgId());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        List dates = entity.getDates();
        if (dates != null) {
            stmt.bindString(4, datesConverter.convertToDatabaseValue(dates));
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public EpgObj readEntity(Cursor cursor, int offset) {
        EpgObj entity = new EpgObj( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // epgId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : datesConverter.convertToEntityProperty(cursor.getString(offset + 3)) // dates
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, EpgObj entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setEpgId(cursor.getInt(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDates(cursor.isNull(offset + 3) ? null : datesConverter.convertToEntityProperty(cursor.getString(offset + 3)));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(EpgObj entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(EpgObj entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(EpgObj entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
