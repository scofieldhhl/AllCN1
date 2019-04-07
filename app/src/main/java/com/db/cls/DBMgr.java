package com.db.cls;

import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import com.allcn.utils.AppMain;
import com.allcn.utils.EXVAL;
import com.datas.CKindObj;
import com.datas.CacheChalObj;
import com.datas.CacheKindObj;
import com.datas.ChalDatsObj;
import com.datas.ChalObj;
import com.datas.EpgObj;
import com.datas.FilmID;
import com.datas.KindObj;
import com.datas.Level;
import com.datas.LiveCKindObj;
import com.datas.LiveChalObj;
import com.datas.LiveKindObj;
import com.datas.MovieDetailsObj;
import com.datas.MovieObj;
import com.datas.ProgremPlayerInfo;
import com.db.gen.CKindObjDao;
import com.db.gen.ChalObjDao;
import com.db.gen.EpgObjDao;
import com.db.gen.FilmIDDao;
import com.db.gen.KindObjDao;
import com.db.gen.LiveCKindObjDao;
import com.db.gen.LiveChalObjDao;
import com.db.gen.LiveKindObjDao;
import com.db.gen.MovieDetailsObjDao;
import com.db.gen.MovieObjDao;

import com.db.gen.ProgremPlayerInfoDao;
import com.mast.lib.utils.MLog;

import org.greenrobot.greendao.query.LazyList;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DBMgr {

    private static final String TAG = DBMgr.class.getSimpleName();
    private GeenDaoCls rDB, wDB, favDB, historyDB, othDB, tmpDB, allLiveDB;
    private static DBMgr dbMgr;

    private DBMgr() {
    }

    public static DBMgr Ins() {
        if (dbMgr == null) {
            synchronized (DBMgr.class) {
                if (dbMgr == null) {
                    dbMgr = new DBMgr();
                }
            }
        }
        return dbMgr;
    }

    public void wrapRW() {
        GeenDaoCls tmpDB = rDB;
        rDB = wDB;
        wDB = tmpDB;
    }

    public void createDB() {
        String pckName = AppMain.ctx().getPackageName();
        String dbDirPath = String.format("/data/data/%s/databases/", pckName);
        File rDbF = new File(dbDirPath, EXVAL.R_DB_NAME);
        if (rDbF.exists()) {
            rDB = new GeenDaoCls(EXVAL.R_DB_NAME);
        }
        favDB = new GeenDaoCls(EXVAL.FAV_DB_NAME);
        historyDB = new GeenDaoCls(EXVAL.HISTORY_DB_NAME);
        othDB = new GeenDaoCls(EXVAL.OTH_DB_NAME);
        allLiveDB = new GeenDaoCls(EXVAL.ALL_LIVE_DB_NAME);
    }

    public boolean initDBDataOk() {
        return rDB != null;
    }

    public void createRDB() {
        rDB = new GeenDaoCls(EXVAL.R_DB_NAME);
    }

    public void createWDB() {
        wDB = new GeenDaoCls(EXVAL.W_DB_NAME);
    }

    public void useNew() {
        if (rDB != null) {
            rDB.release();
            rDB = null;
        }
        String pckName = AppMain.ctx().getPackageName();
        String dbDirPath = String.format("/data/data/%s/databases/", pckName);
        File rDbF = new File(dbDirPath, EXVAL.R_DB_NAME);
        File wDbF = new File(dbDirPath, EXVAL.W_DB_NAME);
        File wJournalF = new File(dbDirPath, EXVAL.W_JOURNAL_NAME);
        File rJournalF = new File(dbDirPath, EXVAL.R_JOURNAL_NAME);
        if (rDbF.exists()) {
            rDbF.delete();
        }
        if (rJournalF.exists()) {
            rJournalF.delete();
        }
        wDbF.renameTo(rDbF);
        wJournalF.renameTo(rJournalF);
        rDB = new GeenDaoCls(EXVAL.R_DB_NAME);
        MLog.d(TAG, "use new db over");
    }

    /**progrem progress*/

    public int queryProgremPlayerInfo(ProgremPlayerInfo playerInfo) {
        int position = -1;
        List<ProgremPlayerInfo> list;
        try {
            ProgremPlayerInfoDao progremPlayerInfoDao = rDB.getWDaoSession().getProgremPlayerInfoDao();
            QueryBuilder<ProgremPlayerInfo> progremPlayerInfoQueryBuilder = progremPlayerInfoDao.queryBuilder();

            WhereCondition eq = ProgremPlayerInfoDao.Properties.Url.eq(playerInfo.getUrl());

            list = progremPlayerInfoQueryBuilder.where(ProgremPlayerInfoDao.Properties.Url.eq(playerInfo.getUrl())).list();

            if (list.size()!=0){
                String colName = list.get(0).getColName();
                String url = list.get(0).getUrl();
                if (playerInfo.getUrl().equals(url)&&playerInfo.getColName().equals(colName)){
                    position = list.get(0).getPosition();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            position = -1;
        }
        return position;
    }


    public void updateProgremPlayerInfo(ProgremPlayerInfo playerInfo) {
        try {
            ProgremPlayerInfoDao progremPlayerInfoDao = rDB.getWDaoSession().getProgremPlayerInfoDao();
            long insert = progremPlayerInfoDao.insert(playerInfo);
            Log.d(TAG, "updateProgremPlayerInfo: "+insert);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /*** Movie DB Options ***/
    public void insertMovies(List<MovieObj> movies) {
        wDB.getWDaoSession().getMovieObjDao().insertInTx(movies);
        if (movies != null) {
            movies.clear();
        }
    }

    public int queryColMovieNum(String colId) {
        return (int) rDB.getWDaoSession().getMovieObjDao().queryBuilder()
                .where(MovieObjDao.Properties.Cid.eq(colId)).count();
    }

    public MovieObj queryMovieForYearAndOffset(int year, int offset) {
        return rDB.getWDaoSession().getMovieObjDao().queryBuilder()
                .where(MovieObjDao.Properties.Year.ge(year))
                .orderDesc(MovieObjDao.Properties.Year)
                .offset(offset)
                .limit(1)
                .unique();
    }

    public int queryMovieNumForYear(String year) {
        return (int) rDB.getWDaoSession().getMovieObjDao().queryBuilder()
                .where(MovieObjDao.Properties.Year.ge(year))
                .orderDesc(MovieObjDao.Properties.Year)
                .count();
    }

    public List<MovieObj> queryMovieForKey(String key, int pageIndex) {
        return rDB.getWDaoSession().getMovieObjDao().queryBuilder()
                .where(MovieObjDao.Properties.Name.like(key))
                .offset(pageIndex * EXVAL.SEARCH_NUM_IN_PAGE)
                .limit(EXVAL.SEARCH_NUM_IN_PAGE)
                .orderDesc(MovieObjDao.Properties.Year).list();
    }

    public int queryMovieForKeyNum(String key) {
        return (int) rDB.getWDaoSession().getMovieObjDao().queryBuilder()
                .where(MovieObjDao.Properties.Name.like(key))
                .orderDesc(MovieObjDao.Properties.Year).count();
    }

    public MovieObj queryMovie(String colId, int movieId) {
        return rDB.getWDaoSession().getMovieObjDao().queryBuilder().where(
                MovieObjDao.Properties.Cid.eq(colId),
                MovieObjDao.Properties.MovieId.eq(Integer.valueOf(movieId))).unique();
    }

    public MovieObj queryMovieForPos(String cid, int pos) {
        return rDB.getWDaoSession().getMovieObjDao().queryBuilder()
                .where(MovieObjDao.Properties.Cid.eq(cid))
                .offset(pos)
                .orderAsc(MovieObjDao.Properties.Id)
                .limit(1)
                .unique();
    }

    public List<MovieObj> queryMovies(String colId) {
        return rDB.getWDaoSession().getMovieObjDao().queryBuilder()
                .where(MovieObjDao.Properties.Cid.eq(colId))
                .orderAsc(MovieObjDao.Properties.Id)
                .list();
    }

    public List<MovieObj> queryWMovies(String colId) {
        return wDB == null ? new ArrayList<MovieObj>(0) :
                wDB.getWDaoSession().getMovieObjDao().queryBuilder()
                        .where(MovieObjDao.Properties.Cid.eq(colId))
                        .orderAsc(MovieObjDao.Properties.Id)
                        .list();
    }

    public List<MovieObj> queryMovies(String colId, int pageIndex) {
        return rDB.getWDaoSession().getMovieObjDao().queryBuilder()
                .where(MovieObjDao.Properties.Cid.eq(colId))
                .offset(pageIndex * EXVAL.NUM_IN_PAGE)
                .orderAsc(MovieObjDao.Properties.Id)
                .limit(EXVAL.NUM_IN_PAGE)
                .list();
    }

    public List<MovieObj> queryMoviesBySortMovieId(String colId, int pageIndex) {
        return rDB.getWDaoSession().getMovieObjDao().queryBuilder()
                .where(MovieObjDao.Properties.Cid.eq(colId))
                .offset(pageIndex * EXVAL.NUM_IN_PAGE)
                .orderDesc(MovieObjDao.Properties.MovieId)
                .limit(EXVAL.NUM_IN_PAGE)
                .list();
    }

    public List<MovieObj> queryMoviesBySortMovieId(String colId, int pageIndex, int limit) {
        return rDB.getWDaoSession().getMovieObjDao().queryBuilder()
                .where(MovieObjDao.Properties.Cid.eq(colId))
                .offset(pageIndex * limit)
                .orderDesc(MovieObjDao.Properties.MovieId)
                .limit(limit)
                .list();
    }

    public List<MovieObj> queryMovies(String colId, int pageIndex, int limit) {
        return rDB.getWDaoSession().getMovieObjDao().queryBuilder()
                .where(MovieObjDao.Properties.Cid.eq(colId))
                .offset(pageIndex * limit)
                .orderAsc(MovieObjDao.Properties.Id)
                .limit(limit)
                .list();
    }

    public int queryMovieNumForCID(String cid) {
        return (int) rDB.getWDaoSession().getMovieObjDao().queryBuilder()
                .where(MovieObjDao.Properties.Cid.eq(cid)).count();
    }

    public int queryMovieNum() {
        return (int) rDB.getWDaoSession().getMovieObjDao().queryBuilder().count();
    }

    public void clearMoviesCache() {
        rDB.getWDaoSession().getMovieObjDao().detachAll();
    }

    public void clearMoviesDatas() {
        try {
            wDB.getWDaoSession().getMovieObjDao().deleteAll();
        } catch (Exception e) {
            try {
                wDB.release();
            } catch (Exception e1) {
            }
            try {
                wDB = new GeenDaoCls(EXVAL.W_DB_NAME);
                wDB.getWDaoSession().getMovieObjDao().deleteAll();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void insertFavMovies(List<MovieObj> movies) {
        try {
            favDB.getWDaoSession().getMovieObjDao().insertInTx(movies);
        } catch (Exception e) {
            int movieNum = movies.size();
            for (int i = 0; i < movieNum; i++) {
                insertFavMovie(movies.get(i));
            }
        }
        if (movies != null) {
            movies.clear();
        }
    }

    public void insertFavMovie(MovieObj movie) {
        try {
            favDB.getWDaoSession().getMovieObjDao().insert(movie);
        } catch (Exception e) {
        }
    }

    public boolean movieInFavDB(MovieObj movie) {
        if (movie == null) {
            return false;
        }
        MovieObj favMovie = favDB.getWDaoSession().getMovieObjDao().queryBuilder()
                .where(MovieObjDao.Properties.Name.eq(movie.getName()),
                        MovieObjDao.Properties.Cid.eq(movie.getCid()),
                        MovieObjDao.Properties.MovieId.eq(Integer.valueOf(movie.getMovieId())))
                .unique();
        return favMovie != null;
    }

    public List<MovieObj> queryFavMovies(int pageIndex, int limit) {
        return favDB.getWDaoSession().getMovieObjDao().queryBuilder()
                .offset(pageIndex * EXVAL.NUM_IN_PAGE)
                .orderDesc(MovieObjDao.Properties.Year)
                .limit(limit)
                .list();
    }

    public Query<MovieObj> favMovieQuery(MovieObj movie) {
        return favDB.getWDaoSession().getMovieObjDao().queryBuilder()
                .where(MovieObjDao.Properties.Name.eq(movie.getName()),
                        MovieObjDao.Properties.Cid.eq(movie.getCid()),
                        MovieObjDao.Properties.MovieId.eq(Integer.valueOf(movie.getMovieId())))
                .build();
    }

    public MovieObj queryFavMovie(MovieObj movie) {
        return favDB.getWDaoSession().getMovieObjDao().queryBuilder()
                .where(MovieObjDao.Properties.Name.eq(movie.getName()),
                        MovieObjDao.Properties.Cid.eq(movie.getCid()),
                        MovieObjDao.Properties.MovieId.eq(Integer.valueOf(movie.getMovieId())))
                .unique();
    }

    public int queryFavMovieNum() {
        return (int) favDB.getWDaoSession().getMovieObjDao().queryBuilder().count();
    }

    public void delFavMovie(MovieObj movie) {
        if (movie != null) {
            favDB.getWDaoSession().getMovieObjDao().delete(movie);
        }
    }

    public void delFavMovies(List<MovieObj> movies) {
        favDB.getWDaoSession().getMovieObjDao().deleteInTx(movies);
    }

    public void clearFavMovies() {
        favDB.getWDaoSession().getMovieObjDao().deleteAll();
    }

    public void insertFavCkind(CKindObj ckind) {
        try {
            favDB.getWDaoSession().getCKindObjDao().insert(ckind);
        } catch (Exception e) {
        }
    }

    public boolean ckindInFavDB(CKindObj cKind) {
        if (cKind == null) {
            return false;
        }
        return queryFavCkind(cKind) != null;
    }

    public List<CKindObj> queryFavCkinds(int pageIndex, int limit) {
        return favDB.getWDaoSession().getCKindObjDao().queryBuilder()
                .offset(pageIndex * limit)
                .limit(limit)
                .list();
    }

    public CKindObj queryFavCkind(CKindObj cKind) {
        return favDB.getWDaoSession().getCKindObjDao().queryBuilder()
                .where(CKindObjDao.Properties.Name.eq(cKind.getName()),
                        CKindObjDao.Properties.Cid.eq(cKind.getCid()),
                        CKindObjDao.Properties.PCid.eq(cKind.getPCid()))
                .unique();
    }

    public int queryFavCkindNum() {
        return (int) favDB.getWDaoSession().getCKindObjDao().queryBuilder().count();
    }

    public void delFavCkind(CKindObj cKind) {
        favDB.getWDaoSession().getCKindObjDao().delete(cKind);
    }

    public long getFavCkindNum() {
        return favDB.getWDaoSession().getCKindObjDao().queryBuilder().count();
    }

    public void clearFavCkind() {
        favDB.getWDaoSession().getCKindObjDao().deleteAll();
    }

    public void insertHistoryMovie(MovieObj movie) {
        try {
            MovieObj movieObj = queryHistoryMovie(movie);
            if (movieObj != null) {
                movieObj.setPlayPos(movie.getPlayPos());
                movieObj.setYear(movie.getYear());
                movieObj.setType(movie.getType());
                movieObj.setRate(movie.getRate());
                movieObj.setLabel(movie.getLabel());
                movieObj.setImgUrl(movie.getImgUrl());
                movieObj.setDuration(movie.getDuration());
                movieObj.setJs(movie.getJs());
                historyDB.getWDaoSession().getMovieObjDao().update(movieObj);
            } else {
                historyDB.getWDaoSession().getMovieObjDao().insert(movie);
            }
        } catch (Exception e) {
        }
    }

    public boolean movieInHistoryDB(MovieObj movie) {
        if (movie == null) {
            return false;
        }
        MovieObj historyMovie = historyDB.getWDaoSession().getMovieObjDao().queryBuilder()
                .where(MovieObjDao.Properties.Name.eq(movie.getName()),
                        MovieObjDao.Properties.Cid.eq(movie.getCid()),
                        MovieObjDao.Properties.MovieId.eq(Integer.valueOf(movie.getMovieId())))
                .unique();
        return historyMovie != null;
    }

    public MovieObj queryHistoryMovie(MovieObj movie) {
        MovieObj movieObj = null;
        try {
            movieObj = historyDB.getWDaoSession().getMovieObjDao().queryBuilder()
                    .where(MovieObjDao.Properties.Cid.eq(movie.getCid()),
                            MovieObjDao.Properties.Name.eq(movie.getName()),
                            MovieObjDao.Properties.MovieId.eq(movie.getMovieId())).unique();
        } catch (Exception e) {
        }
        return movieObj;
    }

    public List<MovieObj> queryHistoryMovies(int pageIndex, int limit) {
        return historyDB.getWDaoSession().getMovieObjDao().queryBuilder()
                .offset(pageIndex * EXVAL.NUM_IN_PAGE)
                .orderDesc(MovieObjDao.Properties.Id)
                .limit(limit)
                .list();
    }

    public int queryHistoryMovieNum() {
        return (int) historyDB.getWDaoSession().getMovieObjDao().queryBuilder().count();
    }

    public void clearHistoryMovies() {
        historyDB.getWDaoSession().getMovieObjDao().deleteAll();
    }
    /*** Movie DB Options ***/

    /*** Kind DB Options ***/
    public void insertKinds(List<KindObj> kinds) {
        wDB.getWDaoSession().getKindObjDao().insertInTx(kinds);
        if (kinds != null) {
            kinds.clear();
        }
    }

    public List<KindObj> queryKinds() {
        return rDB.getWDaoSession().getKindObjDao().queryBuilder().list();
    }

    public KindObj queryKind(String cid) {
        return rDB.getWDaoSession().getKindObjDao().queryBuilder().
                where(KindObjDao.Properties.Cid.eq(cid)).unique();
    }

    public void clearKindsCache() {
        rDB.getWDaoSession().getKindObjDao().detachAll();
    }

    public void clearKindsDatas() {
        wDB.getWDaoSession().getKindObjDao().deleteAll();
    }
    /*** Kind DB Options ***/

    /*** CKind DB Options ***/
    public void insertCKinds(List<CKindObj> cKinds) {
        wDB.getWDaoSession().getCKindObjDao().insertInTx(cKinds);
        if (cKinds != null) {
            cKinds.clear();
        }
    }

    public List<CKindObj> queryCKinds(String pCid) {
        return rDB.getWDaoSession().getCKindObjDao().queryBuilder().where(
                CKindObjDao.Properties.PCid.eq(pCid)).list();
    }

    public List<CKindObj> queryCKindsForPage(String pCid, int pageIndex) {
        return rDB.getWDaoSession().getCKindObjDao().queryBuilder()
                .offset(pageIndex * EXVAL.TOPICS_CKIND_NUM_IN_PAGE)
                .limit(EXVAL.TOPICS_CKIND_NUM_IN_PAGE)
                .where(CKindObjDao.Properties.PCid.eq(pCid)).list();
    }
    public List<CKindObj> queryCKindsForAll(String pCid) {
        return rDB.getWDaoSession().getCKindObjDao().queryBuilder()
                .where(CKindObjDao.Properties.PCid.eq(pCid)).list();
    }

    public int queryCKindNumForPCID(String pCid) {
        return (int) rDB.getWDaoSession().getCKindObjDao().queryBuilder()
                .where(CKindObjDao.Properties.PCid.eq(pCid)).count();
//        return 45;
    }

    public List<CKindObj> queryCkindsForKey(String pCid, String key, int pageIndex) {
        return rDB.getWDaoSession().getCKindObjDao().queryBuilder()
                .where(CKindObjDao.Properties.PCid.eq(pCid), CKindObjDao.Properties.Name.like(key))
                .offset(pageIndex * EXVAL.SEARCH_NUM_IN_PAGE)
                .limit(EXVAL.SEARCH_NUM_IN_PAGE).list();
    }

    public int queryCkindNumForKey(String pCid, String key) {
        return (int) rDB.getWDaoSession().getCKindObjDao().queryBuilder()
                .where(CKindObjDao.Properties.PCid.eq(pCid), CKindObjDao.Properties.Name.like(key))
                .count();
    }

    public CKindObj queryCKindForPCIDAndOffset(String pCid, int offset) {
        return rDB.getWDaoSession().getCKindObjDao().queryBuilder().where(
                CKindObjDao.Properties.PCid.eq(pCid)).offset(offset).limit(1).unique();
    }

    public CKindObj queryCKindForCID(String cid) {
        return rDB.getWDaoSession().getCKindObjDao().queryBuilder().where(
                CKindObjDao.Properties.Cid.eq(cid)).unique();
    }

    public List<CKindObj> queryCKinds(String pCid, int pageIndex) {
        return rDB.getWDaoSession().getCKindObjDao().queryBuilder()
                .where(CKindObjDao.Properties.PCid.eq(pCid))
                .offset(pageIndex * EXVAL.NUM_IN_PAGE_MTV)
                .limit(EXVAL.NUM_IN_PAGE_MTV).list();
    }

    public int queryCkindNumForPCID(String pCid) {
        return (int) rDB.getWDaoSession().getCKindObjDao().queryBuilder()
                .where(CKindObjDao.Properties.PCid.eq(pCid))
                .count();
    }

    public int queryCkindNum(boolean isW) {
        GeenDaoCls db = isW ? wDB : rDB;
        return (int) db.getWDaoSession().getCKindObjDao().queryBuilder().count();
    }

    public int queryCkindNum() {
        return (int) rDB.getWDaoSession().getCKindObjDao().queryBuilder().count();
    }

    public void clearCKindsCache() {
        rDB.getWDaoSession().getCKindObjDao().detachAll();
    }

    public void clearCKindsDatas() {
        wDB.getWDaoSession().getCKindObjDao().deleteAll();
    }
    /*** CKind DB Options ***/

    /*** FilmId DB Options Start ***/
    public void insertFilmIds(List<FilmID> filmIds, int filmIdNum) {
        try {
            othDB.getWDaoSession().getFilmIDDao().insertInTx(filmIds);
        } catch (Exception e) {
            for (int i = 0; i < filmIdNum; i++) {
                try {
                    othDB.getWDaoSession().getFilmIDDao().insert(filmIds.get(i));
                } catch (Exception e1) {
                }
            }
        }
    }

    public FilmID queryFilmIds(String movieName, String cid, int movieId, int pos) {
        return othDB.getWDaoSession().getFilmIDDao().queryBuilder().where(
                FilmIDDao.Properties.Cid.eq(cid),
                FilmIDDao.Properties.MovieId.eq(Integer.valueOf(movieId)),
                FilmIDDao.Properties.Name.eq(movieName),
                FilmIDDao.Properties.Pos.eq(Integer.valueOf(pos))).unique();
    }

    public void clearFilmDatas() {
        othDB.getWDaoSession().getFilmIDDao().deleteAll();
    }
    /*** FilmId DB Options End ***/

    /*** MovieDetails DB Options Start ***/
    public void insertMovieDetails(MovieDetailsObj movieDetailsObj) {
        try {
            othDB.getWDaoSession().getMovieDetailsObjDao().insert(movieDetailsObj);
        } catch (Exception e) {
        }
    }

    public MovieDetailsObj queryMovieDetails(String movieName, String cid, int movieId) {
        return othDB.getWDaoSession().getMovieDetailsObjDao().queryBuilder().where(
                MovieDetailsObjDao.Properties.Cid.eq(cid),
                MovieDetailsObjDao.Properties.Name.eq(movieName),
                MovieDetailsObjDao.Properties.MovieId.eq(Integer.valueOf(movieId))).unique();
    }

    public void clearMovieDetails() {
        othDB.getWDaoSession().getMovieDetailsObjDao().deleteAll();
    }
    /*** MovieDetails DB Options End ***/

    /*** Level DB Options Start ***/
    public void insertLevel(Level level) {
        othDB.getWDaoSession().getLevelDao().insert(level);
    }

    /*** Level DB Options End ***/

    public void insertPlayBackEpgs(List<EpgObj> epgObjs, int epgNum) {
        if (epgObjs == null || epgNum <= 0) {
            return;
        }
        try {
            rDB.getWDaoSession().getEpgObjDao().insertInTx(epgObjs);
        } catch (Exception e) {
            e.printStackTrace();
            for (int i = 0; i < epgNum; i++) {
                try {
                    rDB.getWDaoSession().getEpgObjDao().insert(epgObjs.get(i));
                } catch (Exception e1) {
                    e1.printStackTrace();
                    MLog.d(TAG, "");
                }
            }
        }
    }

    public void insertPlayBackEpgChals(List<ChalObj> chalObjs, int chalNum) {
        if (chalObjs == null || chalNum <= 0) {
            return;
        }
        try {
            rDB.getWDaoSession().getChalObjDao().insertInTx(chalObjs);
        } catch (Exception e) {
            e.printStackTrace();
            for (int i = 0; i < chalNum; i++) {
                try {
                    rDB.getWDaoSession().getChalObjDao().insert(chalObjs.get(i));
                } catch (Exception e1) {
                    e1.printStackTrace();
                    MLog.d(TAG, "");
                }
            }
        }
        chalObjs.clear();
    }

    public EpgObj getEpg(int epgId) {
        return rDB.getWDaoSession().getEpgObjDao().queryBuilder()
                .where(EpgObjDao.Properties.EpgId.eq(epgId)).unique();
    }

    public void updateEpg(EpgObj epgObj) {
        rDB.getWDaoSession().getEpgObjDao().update(epgObj);
    }

    public List<EpgObj> getPlayBackEpgs() {//获取评到列表
        return rDB.getWDaoSession().getEpgObjDao().queryBuilder().list();
    }

    public List<ChalObj> getPlayBackChals(EpgObj epgObj, String date) {
        MLog.d(TAG, String.format("getPlayBackChals EpgId=%d date=%s",
                epgObj.getEpgId(), date));
        return rDB.getWDaoSession().getChalObjDao().queryBuilder().
                where(ChalObjDao.Properties.EpgId.eq(epgObj.getEpgId()),
                        ChalObjDao.Properties.Date.eq(date)).list();
    }

    public int getPlayBackEpgNum() {
        return (int) rDB.getWDaoSession().getEpgObjDao().queryBuilder().count();
    }

    public int getPlayBackEpgChalNum() {
        return (int) rDB.getWDaoSession().getChalObjDao().queryBuilder().count();
    }

    public void clearPlayBack() {
        rDB.getWDaoSession().getEpgObjDao().deleteAll();
        rDB.getWDaoSession().getChalObjDao().deleteAll();
    }
    public void clearPlayBackEpg() {
        rDB.getWDaoSession().getEpgObjDao().deleteAll();
    }
    public void clearPlayBackChal() {
        rDB.getWDaoSession().getChalObjDao().deleteAll();
    }

    public void clearPlayBackEpgCache() {
        rDB.getWDaoSession().getEpgObjDao().detachAll();
    }

    public void clearPlayBackChalCache() {
        rDB.getWDaoSession().getChalObjDao().detachAll();
    }

    public void clearPlayBackCache() {
        rDB.getWDaoSession().getEpgObjDao().detachAll();
        rDB.getWDaoSession().getChalObjDao().detachAll();
    }

    /** Live DB */
    /**
     * Kind DB Start
     **/
    public void insertLiveKinds(List<LiveKindObj> kinds) {
        wDB.getWDaoSession().getLiveKindObjDao().insertInTx(kinds);
        if (kinds != null) {
            kinds.clear();
        }
    }

    public void insertLiveKind(LiveKindObj kind) {//插入直播分类数据
        rDB.getWDaoSession().getLiveKindObjDao().insert(kind);
    }

    public void updateLivekind(LiveKindObj kind, boolean isW) {
        GeenDaoCls geenDaoCls = isW ? wDB : rDB;
        geenDaoCls.getWDaoSession().getLiveKindObjDao().update(kind);
    }

    public LiveKindObj queryLivekind(String colId, String colName, boolean isW) {
        GeenDaoCls geenDaoCls = isW ? wDB : rDB;
        return geenDaoCls.getWDaoSession().getLiveKindObjDao().queryBuilder()
                .where(LiveKindObjDao.Properties.ColId.eq(colId),
                        LiveKindObjDao.Properties.ColName.eq(colName)).limit(1).unique();
    }

    public LiveKindObj queryLivekind(String colName) {//查询直播分类
        return rDB.getWDaoSession().getLiveKindObjDao().queryBuilder()
                .where(LiveKindObjDao.Properties.ColName.eq(colName)).limit(1).unique();
    }

    public List<LiveKindObj> queryLiveKinds() {//查询直播分类
        List<LiveKindObj> list = rDB.getWDaoSession().getLiveKindObjDao().queryBuilder()
                .where(LiveKindObjDao.Properties.PType.eq(EXVAL.PTYPE_CATEGORY_LIVE)).list();
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public List<LiveKindObj> queryNoChildrenLiveKinds() {
        return wDB.getWDaoSession().getLiveKindObjDao().queryBuilder()
                .where(LiveKindObjDao.Properties.HaveChildren.eq(false)).list();
    }

    public LiveKindObj queryLiveKindForPType(int pType) {
        return rDB.getWDaoSession().getLiveKindObjDao().queryBuilder()
                .where(LiveKindObjDao.Properties.PType.eq(pType)).limit(1).unique();
    }
    /** Kind DB End **/

    /**
     * CKind DB Start
     **/
    public void insertLiveCKinds(List<LiveCKindObj> ckinds) {
        wDB.getWDaoSession().getLiveCKindObjDao().insertInTx(ckinds);
        if (ckinds != null) {
            ckinds.clear();
        }
    }

    public void updateLiveCkind(LiveCKindObj cKind, boolean isW) {
        GeenDaoCls geenDaoCls = isW ? wDB : rDB;
        geenDaoCls.getWDaoSession().getLiveCKindObjDao().update(cKind);
    }

    public List<LiveCKindObj> queryLiveCKinds() {
        return wDB.getWDaoSession().getLiveCKindObjDao().queryBuilder().list();
    }

    public LiveCKindObj queryLiveCkind(String cColId, String cColName, boolean isW) {
        GeenDaoCls geenDaoCls = isW ? wDB : rDB;
        return geenDaoCls.getWDaoSession().getLiveCKindObjDao().queryBuilder()
                .where(LiveCKindObjDao.Properties.ColId.eq(cColId),
                        LiveCKindObjDao.Properties.ColName.eq(cColName)).unique();
    }

    public LiveCKindObj queryLiveCkind(String cColName, String cColId) {
        return rDB.getWDaoSession().getLiveCKindObjDao().queryBuilder()
                .where(LiveCKindObjDao.Properties.ColName.eq(cColName),
                        LiveCKindObjDao.Properties.ColId.eq(cColId)).unique();
    }

    public List<LiveCKindObj> queryLiveCKindsForCol(String pColId, String pColName) {
        MLog.d(TAG, String.format("pColId=%s\npColName=%s", pColId, pColName));
        return rDB.getWDaoSession().getLiveCKindObjDao().queryBuilder().
                where(LiveCKindObjDao.Properties.PColId.eq(pColId),
                        LiveCKindObjDao.Properties.PColName.eq(pColName)).list();
    }
    /** CKind DB End **/

    /**
     * Chal DB Start
     **/
    public void insertLiveChals(List<LiveChalObj> chals, int chalNum) {
        try {
            rDB.getWDaoSession().getLiveChalObjDao().insertInTx(chals);
        } catch (Exception e) {
            e.printStackTrace();
            for (int i = 0; i < chalNum; i++) {
                LiveChalObj chal = chals.get(i);
                try {
                    rDB.getWDaoSession().getLiveChalObjDao().insert(chal);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    MLog.e(TAG, String.format("insert %s err", chal));
                }
            }
        }
    }

    public List<LiveChalObj> queryLiveChals() {
        List<LiveChalObj> liveChalObjs = removeRepeatChal(rDB.getWDaoSession().getLiveChalObjDao().queryBuilder().list());
        return liveChalObjs;
    }

    public List<LiveChalObj> queryLiveChalsForCol(String cidStr, String cidName) {//查询分组的所有数据
        LazyList<LiveChalObj> liveChalObjs = rDB.getWDaoSession().getLiveChalObjDao().queryBuilder().where(LiveChalObjDao.Properties.ColId.eq(cidStr),
                LiveChalObjDao.Properties.ColName.eq(cidName)).listLazy();
        List<LiveChalObj> newList = removeRepeatChal(liveChalObjs);
        return newList;
    }
    public List<LiveChalObj> queryLiveChalsForCol(String cidStr, String cidName, int curPageIndex) {//查询分组的分页数据
        LazyList<LiveChalObj> liveChalObjs = rDB.getWDaoSession().getLiveChalObjDao().queryBuilder().where(LiveChalObjDao.Properties.ColId.eq(cidStr),
                LiveChalObjDao.Properties.ColName.eq(cidName)).listLazy();
        ArrayList<LiveChalObj> newList = new ArrayList<>();


        for (int i = curPageIndex* EXVAL.NUM_IN_COL_LIVE, count = 0; i < liveChalObjs.size()&&count<7; i++,count++) {
            newList.add(liveChalObjs.get(i));
        }

        return newList;
        //return rDB.getWDaoSession().getLiveChalObjDao().queryBuilder()
        //        .where(LiveChalObjDao.Properties.ColId.eq(cidStr),
        //                LiveChalObjDao.Properties.ColName.eq(cidName))
        //        .offset(curPageIndex * EXVAL.NUM_IN_COL_LIVE)
        //        .limit(EXVAL.NUM_IN_COL_LIVE).list();
    }

    public int queryLiveChalNum(boolean isW) {
        GeenDaoCls geenDaoCls = isW ? wDB : rDB;
        return (int) geenDaoCls.getWDaoSession().getLiveChalObjDao().queryBuilder().count();
    }

    public int queryLiveChalNumForCol(String cidStr, String cidName) {
        return (int) rDB.getWDaoSession().getLiveChalObjDao().queryBuilder()
                .where(LiveChalObjDao.Properties.ColId.eq(cidStr),
                        LiveChalObjDao.Properties.ColName.eq(cidName)).count();
    }

    public LiveChalObj queryLiveChal(LiveChalObj chal) {
        if (chal == null) {
            return null;
        }
        LiveChalObj dbChal = null;
        try {
            List<LiveChalObj> chals = rDB.getWDaoSession().getLiveChalObjDao().queryBuilder()
                    .where(LiveChalObjDao.Properties.Name.eq(chal.getName())).list();
            dbChal = chals.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            dbChal = null;
        }
        return dbChal;
    }

    public LiveChalObj queryLiveChal(String name) {
        LiveChalObj dbChal = null;
        try {
            List<LiveChalObj> chals = rDB.getWDaoSession().getLiveChalObjDao().queryBuilder()
                    .where(LiveChalObjDao.Properties.Name.eq(name)).list();
            dbChal = chals.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            dbChal = null;
        }
        return dbChal;
    }

    public List<LiveChalObj> queryLiveChalForName(String name) {

        return rDB.getWDaoSession().getLiveChalObjDao().queryBuilder()
                .where(LiveChalObjDao.Properties.Name.eq(name)).list();
    }

    public LiveChalObj queryLiveChal(String name, String colName) {
        LiveChalObj dbChal = null;
        try {
            List<LiveChalObj> chals = rDB.getWDaoSession().getLiveChalObjDao().queryBuilder()
                    .where(LiveChalObjDao.Properties.Name.eq(name),
                            LiveChalObjDao.Properties.ColName.eq(colName)).list();
            dbChal = chals.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            dbChal = null;
        }
        return dbChal;
    }

    public LiveChalObj queryLiveChal(String colName, String colId, int listPos) {
        LiveChalObj dbChal = null;
        try {
            List<LiveChalObj> chals = rDB.getWDaoSession().getLiveChalObjDao().queryBuilder()
                    .where(LiveChalObjDao.Properties.ListPos.eq(listPos),
                            LiveChalObjDao.Properties.ColName.eq(colName),
                            LiveChalObjDao.Properties.ColId.eq(colId)).list();
            dbChal = chals.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            dbChal = null;
        }
        return dbChal;
    }

    public void updateLiveChal(LiveChalObj chal) {
        rDB.getWDaoSession().getLiveChalObjDao().update(chal);
    }

    public void updateLiveChals(List<LiveChalObj> chals, int chalNum) {
        try {
            rDB.getWDaoSession().getLiveChalObjDao().updateInTx(chals);
        } catch (Exception e) {
            e.printStackTrace();
            for (int i = 0; i < chalNum; i++) {
                LiveChalObj chal = chals.get(i);
                try {
                    rDB.getWDaoSession().getLiveChalObjDao().update(chal);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    MLog.e(TAG, String.format("updateChals %s err", chal));
                }
            }
        }
    }

    public void clearLiveChalsCache() {
        rDB.getWDaoSession().getLiveChalObjDao().detachAll();
    }

    public void clearLiveChalsDatas() {
        if (rDB != null) {
            rDB.getWDaoSession().getLiveChalObjDao().deleteAll();
        }
    }
    /** Chal DB End **/

    /**
     * Fav DB Start
     **/
    public void insertLiveFavList(List<LiveChalObj> favList) {
        try {
            favDB.getWDaoSession().getLiveChalObjDao().insertInTx(favList);
        } catch (SQLiteConstraintException e) {
            int favNum = favList.size();
            for (int i = 0; i < favNum; i++) {
                try {
                    favDB.getWDaoSession().getLiveChalObjDao().insert(favList.get(i));
                } catch (Exception ee) {
                }
            }
        }
        if (favList != null) {
            favList.clear();
        }
    }

    public LiveChalObj queryLiveFavChal(String name) {
        return favDB.getWDaoSession().getLiveChalObjDao().queryBuilder()
                .where(LiveChalObjDao.Properties.Name.eq(name)).limit(1).unique();
    }

    public LiveChalObj queryLiveFavChal(int favListPos) {
        return favDB.getWDaoSession().getLiveChalObjDao().queryBuilder()
                .where(LiveChalObjDao.Properties.ListPos.eq(favListPos)).limit(1).unique();
    }

    public List<LiveChalObj> queryLiveFavList(int pageIndex) {
        List<LiveChalObj> liveChalObjs = queryLiveFavList();
        Log.e(TAG, "节目总数: "+liveChalObjs );
        for (int i = 0; i < liveChalObjs.size(); i++) {
            Log.e(TAG, "节目 List: " +liveChalObjs.get(i).getColName());
        }
        return favDB.getWDaoSession().getLiveChalObjDao().queryBuilder()
                .offset(pageIndex * EXVAL.NUM_IN_COL_LIVE)
                .limit(EXVAL.NUM_IN_COL_LIVE)
                .list();

    }

    public List<LiveChalObj> queryLiveFavList() {

        List<LiveChalObj> list = favDB.getWDaoSession().getLiveChalObjDao().queryBuilder().list();
        List<LiveChalObj> liveChalObjs = removeRepeatChal(list);
        return liveChalObjs;
    }

    public void clearLiveFavListCache() {
        favDB.getWDaoSession().getLiveChalObjDao().detachAll();
    }

    public void clearLiveFavList() {
        favDB.getWDaoSession().getLiveChalObjDao().deleteAll();
    }
    /** Fav DB End **/

    /**
     * CacheChal DB Start
     **/
    public void insertCacheChal(CacheChalObj cacheChal) {
        try {
            favDB.getWDaoSession().getCacheChalObjDao().insert(cacheChal);
        } catch (Exception e) {
            MLog.e(TAG, String.format("cache err:%s chal:%s", e.toString(), cacheChal.getName()));
        }
    }

    public CacheChalObj queryCacheChal() {
        return favDB.getWDaoSession().getCacheChalObjDao().queryBuilder().limit(1).unique();
    }

    public void clearCacheChalCache() {
        favDB.getWDaoSession().getCacheChalObjDao().detachAll();
    }

    public void clearCacheChalDatas() {
        favDB.getWDaoSession().getCacheChalObjDao().deleteAll();
    }
    /** CacheChal DB End **/

    /**
     * CacheKind DB Start
     **/
    public void insertCacheKind(CacheKindObj cacheKind) {
        try {
            favDB.getWDaoSession().getCacheKindObjDao().insert(cacheKind);
        } catch (Exception e) {
            MLog.e(TAG, String.format("cache err:%s kind:%s", e.toString(), cacheKind.getColName()));
        }
    }

    public CacheKindObj queryCacheKind() {
        return favDB.getWDaoSession().getCacheKindObjDao().queryBuilder().limit(1).unique();
    }

    public void clearCacheKindCache() {
        favDB.getWDaoSession().getCacheKindObjDao().detachAll();
    }

    public void clearCacheKindDatas() {
        favDB.getWDaoSession().getCacheKindObjDao().deleteAll();
    }

    /**
     * All Kind Live DB start
     */
    public void insertLiveDatasInAll() {
        List<LiveChalObj> list = queryLiveChals();
        int num = list == null ? 0 : list.size();
        if (num > 0) {
            for (int i = 0; i < num; i++) {
                LiveChalObj liveChalObj = list.get(i);
                liveChalObj.setListPos(i);
                liveChalObj.setUiPos(String.valueOf(i + 1));
            }
            allLiveDB.getWDaoSession().getLiveChalObjDao().insertInTx(list);
        }
    }

    public List<LiveChalObj> queryLiveChalsForAll() {

        if (allChalList!=null){
            return allChalList;
        }else {
            List<LiveChalObj> list = allLiveDB.getWDaoSession().getLiveChalObjDao().queryBuilder().listLazy();
            allChalList = removeRepeatChal(list);
            return allChalList;
        }
    }
    public List<LiveChalObj> queryLiveChalsForAll(int curPageIndex) {


        List<LiveChalObj> list = allLiveDB.getWDaoSession().getLiveChalObjDao().queryBuilder().listLazy();
        if (allChalList == null){
            allChalList = removeRepeatChal(list);
        }

        List<LiveChalObj> newList = new ArrayList<>();

        for (int i = curPageIndex* EXVAL.NUM_IN_COL_LIVE, count = 0; i < allChalList.size()&&count<7; i++,count++) {
            newList.add(allChalList.get(i));
        }
        return newList;
        //List<LiveChalObj> newList = new ArrayList<>();
        //for (int i = curPageIndex*EXVAL.NUM_IN_COL_LIVE,count = 0; i < allChalList.size()&&count<EXVAL.NUM_IN_COL_LIVE; i++,count++) {
        //    newList.add(allChalList.get(i));
        //}
        //return newList;
        //Map<String, LiveChalObj> map = new HashMap<>();
        //for (int i = 0; i < list.size(); i++) {
        //    map.put(list.get(i).getName(), list.get(i));
        //}
        //List<LiveChalObj> newList =new ArrayList<>();
        //Iterator<String> iterator = map.keySet().iterator();
        //for (int i = 0; i < map.size(); i++) {
        //    if (iterator.hasNext()) {
        //        newList.add(map.get(iterator.next()));
        //    }
        //}
        //int size = newList.size();

        //Log.d(TAG, "节目总数 queryLiveChalsForAll: "+list.size());
        //for (int i = 0; i < list.size(); i++) {
        //    LiveChalObj cacheChalObj = list.get(i);
        //    Log.d(TAG, "节目 queryLiveChalsForAll: "+cacheChalObj.getColName());
        //}
        //return allLiveDB.getWDaoSession().getLiveChalObjDao().queryBuilder()
        //        .offset(curPageIndex * EXVAL.NUM_IN_COL_LIVE)
        //        .limit(EXVAL.NUM_IN_COL_LIVE).list();
    }
    List<LiveChalObj> allChalList;
    private List<LiveChalObj> removeRepeatChal(List<LiveChalObj> list) {//移除重复的节目数据
        List<LiveChalObj> newList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String name = list.get(i).getName();
            int i1 = 0;
            for ( ; i1 < newList.size(); i1++) {
                if (name.equals(newList.get(i1).getName())){
                    break;
                }
            }
            if (i1==newList.size()) {
                LiveChalObj liveChalObj = list.get(i);
                liveChalObj.setUiPos(""+(i1+1));
                liveChalObj.setListPos(i1+1);
                newList.add(liveChalObj);
            }
        }
        return newList;
    }

    public LiveChalObj queryLiveChalForAll(String name) {
        LiveChalObj dbChal = null;
        try {
            List<LiveChalObj> chals = allLiveDB.getWDaoSession().getLiveChalObjDao().queryBuilder()
                    .where(LiveChalObjDao.Properties.Name.eq(name)).list();
            dbChal = chals.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            dbChal = null;
        }
        return dbChal;
    }

    public LiveChalObj queryLiveChalForAll(int listPos) {
        return allLiveDB.getWDaoSession().getLiveChalObjDao().queryBuilder()
                .where(LiveChalObjDao.Properties.ListPos.eq(listPos)).limit(1).unique();
    }

    public List<LiveChalObj> queryLiveChalForNameForAll(String name) {
        List<LiveChalObj> list = removeRepeatChal(allLiveDB.getWDaoSession().getLiveChalObjDao().queryBuilder()
                .where(LiveChalObjDao.Properties.Name.eq(name)).list());

        return list;
    }

    public void updateLiveChalsForAll(List<LiveChalObj> chals, int chalNum) {
        try {
            allLiveDB.getWDaoSession().getLiveChalObjDao().updateInTx(chals);
        } catch (Exception e) {
            e.printStackTrace();
            for (int i = 0; i < chalNum; i++) {
                LiveChalObj chal = chals.get(i);
                try {
                    allLiveDB.getWDaoSession().getLiveChalObjDao().update(chal);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    MLog.e(TAG, String.format("updateChals %s err", chal));
                }
            }
        }
    }

    public void clearLiveDatasForAll() {
        allLiveDB.getWDaoSession().getLiveChalObjDao().deleteAll();
    }

    public void clearLiveDatasCacheForAll() {
        allLiveDB.getWDaoSession().getLiveChalObjDao().detachAll();
    }

    /**
     * All Kind Live DB end
     */

    public void release() {
        if (rDB != null) {
            rDB.release();
        }
        if (wDB != null) {
            wDB.release();
        }
        if (favDB != null) {
            favDB.release();
        }
        if (historyDB != null) {
            historyDB.release();
        }
        if (othDB != null) {
            othDB.release();
        }
        if (allLiveDB != null) {
            allLiveDB.release();
        }
        rDB = wDB = favDB = othDB = historyDB = allLiveDB = null;
        dbMgr = null;
    }
}