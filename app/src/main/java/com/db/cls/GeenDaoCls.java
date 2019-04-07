package com.db.cls;

import android.text.TextUtils;

import com.allcn.utils.AppMain;
import com.allcn.utils.GreenHelper;
import com.db.gen.DaoMaster;
import com.db.gen.DaoSession;

public class GeenDaoCls {

    private DaoMaster mWDaoMaster;
    private DaoSession mWDaoSession;

    public GeenDaoCls(String dbName) {
        if (TextUtils.isEmpty(dbName)) {
            throw new IllegalArgumentException("DB name can not be empty");
        }
//        DaoMaster.DevOpenHelper devOpenHelper =
//                new DaoMaster.DevOpenHelper(AppMain.ctx(), dbName, null);
        GreenHelper devOpenHelper = new GreenHelper(AppMain.ctx(), dbName, null);
        mWDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        mWDaoSession = mWDaoMaster.newSession();
    }

    public DaoSession getWDaoSession() {
        return mWDaoSession;
    }

    public void release() {
        if (mWDaoMaster != null) {
            mWDaoMaster.getDatabase().close();
            mWDaoSession.getDatabase().close();
            mWDaoMaster = null;
            mWDaoSession = null;
        }
        mWDaoSession = null;
    }
}
