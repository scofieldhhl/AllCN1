package com.allcn.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.db.gen.CKindObjDao;
import com.db.gen.CacheChalObjDao;
import com.db.gen.CacheKindObjDao;
import com.db.gen.ChalObjDao;
import com.db.gen.DaoMaster;
import com.db.gen.EpgObjDao;
import com.db.gen.FilmIDDao;
import com.db.gen.KindObjDao;
import com.db.gen.LevelDao;
import com.db.gen.LiveCKindObjDao;
import com.db.gen.LiveChalObjDao;
import com.db.gen.LiveKindObjDao;
import com.db.gen.MovieDetailsObjDao;
import com.db.gen.MovieObjDao;
import com.db.gen.ReMovieObjDao;

import org.greenrobot.greendao.database.Database;

public class GreenHelper extends DaoMaster.OpenHelper {

    public GreenHelper(Context context, String name) {
        super(context, name);
    }

    public GreenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
                    @Override
                    public void onCreateAllTables(Database db, boolean ifNotExists) {
                        DaoMaster.createAllTables(db, ifNotExists);
                    }

                    @Override
                    public void onDropAllTables(Database db, boolean ifExists) {
                        DaoMaster.dropAllTables(db, ifExists);
                    }
                }, ChalObjDao.class, CKindObjDao.class, EpgObjDao.class, FilmIDDao.class
                , KindObjDao.class, LevelDao.class, MovieDetailsObjDao.class, MovieObjDao.class,
                ReMovieObjDao.class, LiveChalObjDao.class, LiveCKindObjDao.class, LiveKindObjDao.class
                , CacheChalObjDao.class, CacheKindObjDao.class);
    }
}
