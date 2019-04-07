package com.db.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.datas.ChalObj;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CHAL_OBJ".
*/
public class ChalObjDao extends AbstractDao<ChalObj, Long> {

    public static final String TABLENAME = "CHAL_OBJ";

    /**
     * Properties of entity ChalObj.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property ChalId = new Property(1, int.class, "chalId", false, "CHAL_ID");
        public final static Property VideoId = new Property(2, int.class, "videoId", false, "VIDEO_ID");
        public final static Property EpgId = new Property(3, int.class, "epgId", false, "EPG_ID");
        public final static Property Name = new Property(4, String.class, "name", false, "NAME");
        public final static Property BeginTime = new Property(5, String.class, "beginTime", false, "BEGIN_TIME");
        public final static Property EndTime = new Property(6, String.class, "endTime", false, "END_TIME");
        public final static Property FilmId = new Property(7, String.class, "filmId", false, "FILM_ID");
        public final static Property Date = new Property(8, String.class, "date", false, "DATE");
    }


    public ChalObjDao(DaoConfig config) {
        super(config);
    }
    
    public ChalObjDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CHAL_OBJ\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"CHAL_ID\" INTEGER NOT NULL ," + // 1: chalId
                "\"VIDEO_ID\" INTEGER NOT NULL ," + // 2: videoId
                "\"EPG_ID\" INTEGER NOT NULL ," + // 3: epgId
                "\"NAME\" TEXT," + // 4: name
                "\"BEGIN_TIME\" TEXT," + // 5: beginTime
                "\"END_TIME\" TEXT," + // 6: endTime
                "\"FILM_ID\" TEXT," + // 7: filmId
                "\"DATE\" TEXT);"); // 8: date
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CHAL_OBJ\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ChalObj entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getChalId());
        stmt.bindLong(3, entity.getVideoId());
        stmt.bindLong(4, entity.getEpgId());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        String beginTime = entity.getBeginTime();
        if (beginTime != null) {
            stmt.bindString(6, beginTime);
        }
 
        String endTime = entity.getEndTime();
        if (endTime != null) {
            stmt.bindString(7, endTime);
        }
 
        String filmId = entity.getFilmId();
        if (filmId != null) {
            stmt.bindString(8, filmId);
        }
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(9, date);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ChalObj entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getChalId());
        stmt.bindLong(3, entity.getVideoId());
        stmt.bindLong(4, entity.getEpgId());
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(5, name);
        }
 
        String beginTime = entity.getBeginTime();
        if (beginTime != null) {
            stmt.bindString(6, beginTime);
        }
 
        String endTime = entity.getEndTime();
        if (endTime != null) {
            stmt.bindString(7, endTime);
        }
 
        String filmId = entity.getFilmId();
        if (filmId != null) {
            stmt.bindString(8, filmId);
        }
 
        String date = entity.getDate();
        if (date != null) {
            stmt.bindString(9, date);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public ChalObj readEntity(Cursor cursor, int offset) {
        ChalObj entity = new ChalObj( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // chalId
            cursor.getInt(offset + 2), // videoId
            cursor.getInt(offset + 3), // epgId
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // name
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // beginTime
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // endTime
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // filmId
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8) // date
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ChalObj entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setChalId(cursor.getInt(offset + 1));
        entity.setVideoId(cursor.getInt(offset + 2));
        entity.setEpgId(cursor.getInt(offset + 3));
        entity.setName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setBeginTime(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setEndTime(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setFilmId(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setDate(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ChalObj entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ChalObj entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ChalObj entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}