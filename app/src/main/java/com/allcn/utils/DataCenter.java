package com.allcn.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.allcn.BuildConfig;
import com.allcn.R;
import com.allcn.activities.DetailsAct;
import com.allcn.activities.HotReDetailsAct;
import com.allcn.activities.KindMovieAct;
import com.allcn.activities.LiveAct;
import com.allcn.activities.MainActivity;
import com.allcn.activities.PlayBackAct;
import com.allcn.activities.PlayBackMainAct;
import com.allcn.activities.SEDMKindListAct;
import com.allcn.activities.SEDMMovieDetailsAct;
import com.allcn.activities.TopicsDetailsAct;
import com.allcn.activities.TopicsMainAct;
import com.allcn.interfaces.OnDataListener;
import com.allcn.interfaces.OnPlayListener;
import com.allcn.interfaces.OnSearchListener;
import com.allcn.utils.parsers.ChalParser;
import com.allcn.utils.parsers.EpgParser;
import com.allcn.utils.parsers.EpgParser2;
import com.allcn.utils.parsers.MovieParser;
import com.datas.CKindObj;
import com.datas.CacheChalObj;
import com.datas.CacheKindObj;
import com.datas.ChalDatsObj;
import com.datas.ChalObj;
import com.datas.EpgObj;
import com.datas.FilmID;
import com.datas.LiveCKindObj;
import com.datas.LiveChalObj;
import com.datas.LiveKindObj;
import com.datas.MovieDetailsObj;
import com.datas.MovieObj;
import com.db.cls.DBMgr;
import com.forcetech.android.ForceTV;
import com.google.gson.Gson;
import com.mast.lib.cls.ActerCls;
import com.mast.lib.datas.CmsObj;
import com.mast.lib.datas.IPObj;
import com.mast.lib.parsers.MarqueeParser;
import com.mast.lib.parsers.SerListParser;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.NetUtils;
import com.mast.lib.utils.ShareAdapter;
import com.mast.lib.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.ResponseBody;

public class DataCenter extends ActerCls {

    private static final String TAG = DataCenter.class.getSimpleName();

    private static final String HOST = "http://list.auroratvbox.com:9500/cnw";
    private static final String[] DOAMIN_URLS = new String[]{
            "%s/list/vst/vst.txt",
            "playback",
    };
    private static final String[] CONF_NAMES = new String[]{
            ".wotv_conf",
            ".wotvplayback_conf",
    };
    private static final String IP_LIST_URL = "%s/list/iplist.txt";
    private static final String UPDATE_URL = "%s/list/update/vst/vst.json";
    private static final String VOD_CID_LIST_FMT = "%s:8080/api/vod/list?cid=%s&pageNum=%d";
    private static final String VOD_MOVIE_FMT = "%s:8080/api/vod/get?id=%d";
    private static final String VOD_IMG_FMT = "%s:8080/forcetech%s%s";
    private static final String VOD_IMG_FMT1 = "/resource/images/www1/";
    private static final String DB_URL = "%s/sql/sql_vst.txt";
    private static final String EPGS_URL = "%s:8080/api/liveBack/channel";
    private static final String EPGS_ITEM_URL = "%s:8080/api/liveBack/program?channelId=%d";
    private ExecutorService taskPool, serPool, imgPool;
    private boolean initDataIng, initOver, updatIng, initMediaDatasIng, initMediaDatasOver;
    private List<OnDataListener> onDataListeners;
    private int listenerNum, playListenerNum, searchListenerNum;
    public int curVodListTaskIndex, totalVodListTaskNum;
    private MBroadcastReceiver mBroadcastReceiver;
    private boolean forceTvOK, initSOing, vodTvDataOK, vodTvDatIng, playBackDataOK, playBackDatIng, playbackEpgIng, playbackDatesIng,
            liveDatIng, liveDataOK, downloadResIng;
    private boolean isDataOk;
    private int activityType;
    private List<OnPlayListener> onPlayListeners;
    private List<OnSearchListener> onSearchListeners;
    private ArrayMap<Class, MarqueeTask> marqueeTaskObjList;
    private ArrayMap<Class, Future<?>> marqueeTasks;
    private static final int RETRY_COUNT = 3;
    private String imgUrl;

    private static final DataCenter ourInstance = new DataCenter();

    public static DataCenter Ins() {
        return ourInstance;
    }

    private DataCenter() {
        super(AppMain.ctx());
        listenerNum = 0;
        taskPool = Executors.newFixedThreadPool(10);
        serPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);
        imgPool = Executors.newSingleThreadExecutor();
        onDataListeners = new ArrayList<>();
        onPlayListeners = new ArrayList<>();
        onSearchListeners = new ArrayList<>();
        marqueeTaskObjList = new ArrayMap<>();
        marqueeTasks = new ArrayMap<>();
    }

    public String getImgUrl() {
        return imgUrl;
    }

    @Override
    public void initForceTV() {
        if (!initSOing && !forceTvOK) {
            initSOing = true;
            taskPool.execute(new InitForceTVTask(this));
        }
    }

    @Override
    public void login() {
        loginTask();
    }

    @Override
    public void serList() {

    }

    @Override
    public void release() {
        super.relaseActer();
        unregReceiver(AppMain.ctx());
        if (onDataListeners != null) {
            onDataListeners.clear();
            onDataListeners = null;
        }
        if (onSearchListeners != null) {
            onSearchListeners.clear();
            onSearchListeners = null;
        }
        if (onPlayListeners != null) {
            onPlayListeners.clear();
            onPlayListeners = null;
        }
        if (marqueeTasks != null) {
            marqueeTasks.clear();
            marqueeTasks = null;
        }
        if (marqueeTaskObjList != null) {
            marqueeTaskObjList.clear();
            marqueeTaskObjList = null;
        }
        if (taskPool != null) {
            taskPool.shutdownNow();
            taskPool = null;
        }
        if (serPool != null) {
            serPool.shutdownNow();
            serPool = null;
        }
        if (mBroadcastReceiver != null) {
            mBroadcastReceiver.release();
            mBroadcastReceiver = null;
        }
    }

    public void setActivityType(int activityType) {
        this.activityType = activityType;
    }

    public void addPlayListener(OnPlayListener onPlayListener) {
        if (onPlayListener == null) {
            return;
        }
        if (onPlayListeners.indexOf(onPlayListener) < 0) {
            onPlayListeners.add(onPlayListener);
            playListenerNum = onPlayListeners.size();
        }
    }

    public void delPlayListener(OnPlayListener onPlayListener) {
        if (onPlayListener == null) {
            return;
        }
        int listenerIndex = onPlayListeners.indexOf(onPlayListener);
        if (listenerIndex >= 0) {
            onPlayListeners.remove(listenerIndex);
            playListenerNum = onPlayListeners.size();
        }
    }

    public void addDataListener(final OnDataListener onDataListener) {
        if (onDataListener == null) {
            return ;
        }
        //if (
            onDataListeners.indexOf(onDataListener);// < 0) {
            onDataListeners.add(onDataListener);
            listenerNum = onDataListeners.size();
        //}
        //return ;

    }

    public void delDataListener(OnDataListener onDataListener) {
        if (onDataListener == null) {
            return;
        }
        int listenerIndex = onDataListeners.indexOf(onDataListener);
        if (listenerIndex >= 0) {
            onDataListeners.remove(listenerIndex);
            listenerNum = onDataListeners.size();
        }
    }

    public void addSearchListener(OnSearchListener onSearchListener) {
        if (onSearchListener == null) {
            return;
        }
        if (onSearchListeners.indexOf(onSearchListener) < 0) {
            onSearchListeners.add(onSearchListener);
            searchListenerNum = onSearchListeners.size();
        }
    }

    public void delSearchListener(OnSearchListener onSearchListener) {
        if (onSearchListener == null) {
            return;
        }
        int listenerIndex = onSearchListeners.indexOf(onSearchListener);
        if (listenerIndex >= 0) {
            onSearchListeners.remove(listenerIndex);
            searchListenerNum = onSearchListeners.size();
        }
    }

    public void addMarqueeTask(Class cls) {
        delMarqueeTask(cls);
        MarqueeTask marqueeTaskObj = new MarqueeTask(this, cls);
        Future<?> marqueeTask = taskPool.submit(marqueeTaskObj);
        marqueeTasks.put(cls, marqueeTask);
        marqueeTaskObjList.put(cls, marqueeTaskObj);
    }

    public void delMarqueeTask(Class cls) {
        MarqueeTask marqueeTaskObj = marqueeTaskObjList.get(cls);
        Future<?> marqueeTask = marqueeTasks.get(cls);
        if (marqueeTaskObj != null) {
            marqueeTaskObjList.remove(cls);
            marqueeTaskObj.release();
            marqueeTaskObj = null;
        }
        if (marqueeTask != null) {
            marqueeTasks.remove(cls);
            marqueeTask.cancel(true);
            marqueeTask = null;
        }
    }

    public boolean isForceTvOK() {
        return forceTvOK;
    }

    public boolean isDataOK() {
        return isDataOk;
    }

    public int[] getResIdArr(int arrayResId) {
        TypedArray array = AppMain.res().obtainTypedArray(arrayResId);
        int length = array.length();
        int[] resIdarr = new int[length];
        for (int i = 0; i < length; i++) {
            resIdarr[i] = array.getResourceId(i, 0);
        }
        array.recycle();
        return resIdarr;
    }

    public int getExpire() {
        return super.getExpire(DOAMIN_URLS[0]);
    }

    public boolean isExpired() {
        return super.isExpired(DOAMIN_URLS[0]);
    }

    public boolean isLoginOK() {
        return super.isLoginOK(DOAMIN_URLS[0]);
    }

    private static class MBroadcastReceiver extends BroadcastReceiver {

        private DataCenter dataCenter;

        public MBroadcastReceiver(DataCenter dataCenter) {
            this.dataCenter = dataCenter;
        }

        public void release() {
            dataCenter = null;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            Log.d(TAG, "action = " + action);

            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager conMgr = (ConnectivityManager)
                        AppMain.ctx().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = conMgr.getActiveNetworkInfo();
                boolean isConnected = false;
                int netType = -1;
                if (activeNetworkInfo != null) {
                    isConnected = activeNetworkInfo.isConnected();
                    netType = activeNetworkInfo.getType();
                }
                if (dataCenter != null) {
                    for (int i = 0; i < dataCenter.listenerNum; i++) {
                        OnDataListener onDataListener = dataCenter.onDataListeners.get(i);
                        if (onDataListener != null) {
                            onDataListener.onNetState(netType, isConnected);
                        }
                    }
                }
            } else if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                    action.equals(Intent.ACTION_TIME_TICK) ||
                    action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                dataCenter.scanTime();
            }
        }
    }

    public void scanTime() {
        taskPool.execute(new ScanTimeTask(this));
    }

    public void checkUpdate() {
        if (!updatIng) {
            updatIng = true;
            taskPool.execute(new UpdateTask(this));
        }
    }

    public void regReceiver(Context context) {
        Log.d(TAG, "regReceiver");
        mBroadcastReceiver = new MBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        if (context != null) {
            context.registerReceiver(mBroadcastReceiver, intentFilter);
        }
    }

    public void unregReceiver(Context context) {
        Log.d(TAG, "unregReceiver");
        if (mBroadcastReceiver != null && context != null) {
            context.unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
    }

    public static class MarqueeTask implements Runnable {//主界面的跑马灯效果

        private DataCenter dataCenter;
        private boolean isRunning;
        private Class cls;

        public MarqueeTask(DataCenter dataCenter, Class cls) {
            this.dataCenter = dataCenter;
            this.cls = cls;
        }

        public void release() {
            dataCenter = null;
            isRunning = false;
            cls = null;
        }

        @Override
        public void run() {

            isRunning = true;

            Class[] classes = new Class[]{
                    MainActivity.class,
                    PlayBackMainAct.class,
                    LiveAct.class,
            };

            String[] marqueeUrls = new String[]{
                    "%s/list/paomadeng/vst.json",
                    "%s/list/paomadeng/playback.json",
                    "%s/list/paomadeng/live.json",
            };

            int clsIndex = -1, clsNum = classes.length;

            for (int i = 0; i < clsNum; i++) {
                if (classes[i] == cls) {
                    clsIndex = i;
                }
            }

            if (clsIndex < 0) {
                return;
            }

            while (isRunning && dataCenter != null) {
                MarqueeParser marqueeParser = dataCenter.checkMarquee(AppMain.ctx(),
                        String.format(marqueeUrls[clsIndex], HOST));
                if (marqueeParser != null) {
                    for (int i = 0; i < dataCenter.listenerNum; i++) {
                        OnDataListener onDataListener = dataCenter.onDataListeners.get(i);
                        if (onDataListener != null) {
                            onDataListener.onMarquee(marqueeParser);
                        }
                    }
                }

                try {
                    Thread.sleep(EXVAL.MARQUEE_DURATION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class UpdateTask implements Runnable {

        private DataCenter dataCenter;

        public UpdateTask(DataCenter dataCenter) {
            this.dataCenter = dataCenter;
        }

        @Override
        public void run() {

            String[] updateInfoArr = dataCenter.checkUpdate(AppMain.ctx(),
                    String.format(UPDATE_URL, HOST));

            if (updateInfoArr != null && updateInfoArr.length == 2) {
                Utils.installUpdateApk(AppMain.ctx(), updateInfoArr[0]);
            }

            dataCenter = null;
        }
    }

    private static class ScanTimeTask implements Runnable {

        private DataCenter dataCenter;

        public ScanTimeTask(DataCenter dataCenter) {
            this.dataCenter = dataCenter;
        }

        @Override
        public void run() {

            if (dataCenter == null) {
                return;
            }

            SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.is24HourFormat(AppMain.ctx()) ?
                    "HH:mm aa" : "hh:mm aa", Locale.ENGLISH);
            String formatTimeStr = sdf.format(new Date());
            MLog.d(TAG, String.format("formatTimeStr = %s", formatTimeStr));
            if (dataCenter != null) {
                for (int i = 0; i < dataCenter.listenerNum; i++) {
                    OnDataListener onDataListener = dataCenter.onDataListeners.get(i);
                    if (onDataListener != null) {
                        onDataListener.onTimeDate(formatTimeStr);
                    }
                }
            }
        }
    }

    private LoginTask loginTaskObj;
    private Future<?> loginTask;
    static Object loginLock = new Object();
    public void stopLoginTask() {
        synchronized (loginLock) {
            if (loginTaskObj != null) {
                loginTaskObj.release();
                loginTaskObj = null;
            }
            if (loginTask != null) {
                loginTask.cancel(true);
                loginTask = null;
            }
        }
    }

    public void loginTask() {
        stopLoginTask();
        loginTaskObj = new LoginTask(this);
        loginTask = taskPool.submit(loginTaskObj);
    }

    private static class LoginTask implements Runnable {

        private DataCenter dataCenter;

        public LoginTask(DataCenter dataCenter) {
            this.dataCenter = dataCenter;
        }

        public void release() {
            this.dataCenter = null;
        }

        @Override
        public void run() {
            synchronized (loginLock) {
                try {
                    if (dataCenter == null) {
                        return;
                    }

                    CmsObj cmsObj = dataCenter.getCms(DOAMIN_URLS[0], CONF_NAMES[0]);
                    String tipsStr = null;
                    boolean dialogCancelable = false;

                    if (cmsObj.isLoginOK() && !cmsObj.isExpired()) {
                        for (int i = 0; i < dataCenter.listenerNum; i++) {
                            OnDataListener onDataListener = dataCenter.onDataListeners.get(i);
                            if (onDataListener != null) {
                                onDataListener.onLogin(tipsStr, dialogCancelable);
                            }
                        }
                        dataCenter.logining = false;
                        dataCenter = null;
                        return;
                    }

                    String imgConfVal = NetUtils.Ins(AppMain.ctx()).getUrlStr(EXVAL.TIPS_IMG_URL,
                            null, null, null);

                    MLog.d(TAG, "imgConfVal = " + imgConfVal);

                    if (!TextUtils.isEmpty(imgConfVal)) {
                        dataCenter.imgUrl = imgConfVal.trim();
                    }

                    boolean limitArea = false;
                    String ipStr = null, areaStr = null, geoIp = null;

                    if (Utils.isNetOK(AppMain.ctx())) {

                        String hostStr = dataCenter.checkHost(AppMain.ctx(),
                                String.format(DOAMIN_URLS[0], HOST));
                        if (!TextUtils.isEmpty(hostStr)) {
                            cmsObj.setDominUrl(hostStr);
                        }

                        String macStr = NetUtils.Ins(AppMain.ctx()).getMac();

                        String areaListVal = NetUtils.Ins(AppMain.ctx()).getUrlStr(String.format(IP_LIST_URL, HOST),
                                null, null, null);

                        MLog.d(TAG, "areaListVal " + areaListVal);

                        String[] areaArr = TextUtils.isEmpty(areaListVal) ? new String[0] : areaListVal.split("\n");

                        int areaNum = areaArr.length;

                        try {
                            String ipCnStr = NetUtils.Ins(AppMain.ctx()).
                                    getUrlStr("https://ip.cn/", null, null, null);
                            String ipCnHead = AppMain.res().getString(R.string.ip_cn_head);
                            String areaCnHead = AppMain.res().getString(R.string.area_cn_head);
                            String geoIpCnHead = AppMain.res().getString(R.string.geoip_cn_head);
                            int startIndex = ipCnStr.indexOf(ipCnHead);
                            if (startIndex > 0) {
                                ipStr = ipCnStr.substring(startIndex + ipCnHead.length());
                                MLog.d(TAG, String.format("ipStr = %s", ipStr));
                                ipStr = ipStr.substring(ipStr.indexOf(">") + 1, ipStr.indexOf("</"));
                            }
                            startIndex = ipCnStr.indexOf(areaCnHead);
                            if (startIndex > 0) {
                                areaStr = ipCnStr.substring(startIndex + areaCnHead.length());
                                MLog.d(TAG, String.format("areaStr = %s", areaStr));
                                areaStr = areaStr.substring(areaStr.indexOf(">") + 1,
                                        areaStr.indexOf("</"));
                            }
                            startIndex = ipCnStr.indexOf(geoIpCnHead);
                            if (startIndex > 0) {
                                geoIp = ipCnStr.substring(startIndex + geoIpCnHead.length());
                                geoIp = geoIp.substring(0, geoIp.indexOf("</"));
                            }
                            if (!TextUtils.isEmpty(ipStr)) {
                                ipStr = ipStr.trim();
                            }
                            if (!TextUtils.isEmpty(areaStr)) {
                                areaStr = areaStr.trim();
                            }
                            if (!TextUtils.isEmpty(geoIp)) {
                                geoIp = geoIp.trim();
                            }
                            MLog.d(TAG, String.format("ipStr = %s", ipStr));
                            MLog.d(TAG, String.format("areaStr = %s", areaStr));
                            MLog.d(TAG, String.format("geoIp = %s", geoIp));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (TextUtils.isEmpty(ipStr) || TextUtils.isEmpty(areaStr) || TextUtils.isEmpty(geoIp)
                                || TextUtils.isEmpty(areaListVal)) {
                            limitArea = false;
                        } else {
                            limitArea = false;
                            geoIp = geoIp.toUpperCase();
                            List<String> limitAreas = Arrays.asList(AppMain.res().getStringArray(R.array.area_arr));
                            int limitAreaNum = limitAreas.size();
                            for (int i = 0; i < limitAreaNum; i++) {
                                String limitAreaStr = limitAreas.get(i).trim();
                                if (geoIp.contains(limitAreaStr)) {
                                    limitArea = true;
                                    break;
                                }
                            }
                            if (limitArea) {
                                MLog.d(TAG, "limit area, need check");
                                for (int i = 0; i < areaNum; i++) {
                                    String areaItem = areaArr[i].trim();
                                    if (Utils.ipIsValid(areaItem)) {
                                        MLog.d(TAG, "areaItem is IP");
                                        if (ipStr.equals(areaItem)) {
                                            limitArea = false;
                                            break;
                                        }
                                    } else if (Utils.macIsValid(areaItem)) {
                                        MLog.d(TAG, "areaItem is MAC");
                                        if (macStr.equalsIgnoreCase(areaItem)) {
                                            limitArea = false;
                                            break;
                                        }
                                    } else {
                                        MLog.d(TAG, "areaItem is OTH");
                                        if (areaStr.indexOf(areaItem) != -1 ||
                                                limitAreas.indexOf(areaItem) != -1) {
                                            limitArea = false;
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        if (TextUtils.isEmpty(ipStr)) {
                            ipStr = "xxx.xxx.xxx.xxx";
                        }

                        if (!limitArea) {

                            tipsStr = dataCenter.loginForceTvAuto(cmsObj, BuildConfig.ID_HEAD);

                            if (cmsObj.isLoginOK()) {

                                String serListStr = dataCenter.getSerList(cmsObj);
                                SerListParser serListParser = new SerListParser();
                                serListParser.parse(serListStr);
                                cmsObj.setIps(serListParser.getIps());
                            }
                        } else {
                            tipsStr = AppMain.res().getString(R.string.ser_con_err_tips,
                                    String.format("\n(IP: %s)", ipStr));
                        }
                    } else {
                        cmsObj.setLoginOK(false);
                        tipsStr = AppMain.res().getString(R.string.no_net);
                    }

                    if (dataCenter != null) {
                        for (int i = 0; i < dataCenter.listenerNum; i++) {
                            OnDataListener onDataListener = dataCenter.onDataListeners.get(i);
                            if (onDataListener != null) {
                                onDataListener.onLogin(tipsStr, dialogCancelable);
                            }
                        }
                    }
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    dataCenter.logining = false;
                    dataCenter = null;
                }
            }
        }
    }

    private static class InitForceTVTask implements Runnable {

        private DataCenter hostCls;

        public InitForceTVTask(DataCenter hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void run() {

            ForceTV.initForceClient();

            hostCls.forceTvOK = true;

            if (hostCls != null) {
                if (hostCls.onDataListeners != null) {
                    for (int i = 0; i < hostCls.listenerNum; i++) {
                        OnDataListener onDataListener = hostCls.onDataListeners.get(i);
                        if (onDataListener != null) {
                            onDataListener.onForceTv();
                        }
                    }
                }
            }

            hostCls.initSOing = false;
        }
    }

    public void initMediaDatas() {
        if (!initMediaDatasIng && !initMediaDatasOver) {
            initMediaDatasIng = true;
            taskPool.execute(new InitMediaDatasTask(this));
        }
    }

    private static class InitMediaDatasTask implements Runnable {

        private DataCenter hostCls;

        public InitMediaDatasTask(DataCenter hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void run() {

            hostCls.initMediaDatasOver = false;

            DBMgr dbMgr = DBMgr.Ins();
            dbMgr.createDB();
            dbMgr.clearFilmDatas();
            dbMgr.clearMovieDetails();
            Log.d(TAG, "加载数据信息："+dbMgr.initDBDataOk());
            if (dbMgr.initDBDataOk()) {

                hostCls.isDataOk = true;
                if (hostCls.onDataListeners != null) {
                    for (int i = 0; i < hostCls.listenerNum; i++) {
                        OnDataListener onDataListener = hostCls.onDataListeners.get(i);
                        if (onDataListener != null) {
                            onDataListener.onInitMediaOver();
                        }
                    }
                }
            }

            int retryCount = 0;
            List<String> urls = new ArrayList<>();

            String val = null;
            do {
                val = NetUtils.Ins(AppMain.ctx()).getUrlStr(String.format(DB_URL, HOST), null,
                        null, null);
            } while (TextUtils.isEmpty(val) && (retryCount++ < RETRY_COUNT));

            String dateStr = null, verStr = null, dbUrlStr = null;

            if (!TextUtils.isEmpty(val)) {
                String[] arr = val.split("\n");
                int arrNum = arr.length;
                for (int i = 0; i < arrNum; i++) {
                    String item = arr[i];
                    if (item.startsWith("url:")) {
                        dbUrlStr = item.substring(item.indexOf("url:") + "url:".length(),
                                item.length());
                        if (!TextUtils.isEmpty(dbUrlStr)) {
                            dbUrlStr = dbUrlStr.trim();
                            urls.add(dbUrlStr.trim());
                        }
                    } else if (item.startsWith("url2:")) {
                        dbUrlStr = item.substring(item.indexOf("url2:") + "url2:".length(),
                                item.length());
                        if (!TextUtils.isEmpty(dbUrlStr)) {
                            dbUrlStr = dbUrlStr.trim();
                            urls.add(dbUrlStr.trim());
                        }
                    }
                    MLog.d(TAG, String.format("ScanDBTask " + item));
                }
            }

            MLog.d(TAG, String.format("dateStr=%s, verStr=%s", dateStr, verStr));

            String dbRootPath = String.format("/data/data/%s/databases",
                    AppMain.ctx().getPackageName());

            int urlNum = urls.size();

            File wDbF = new File(dbRootPath, EXVAL.W_DB_NAME);
            File wJournalDbF = new File(dbRootPath, String.format("%s-journal", EXVAL.W_DB_NAME));

            if (wDbF.exists()) {
                wDbF.delete();
            }

            if (wJournalDbF.exists()) {
                wJournalDbF.delete();
            }

            for (int i = 0; i < urlNum; i++) {
                String dbUrl = urls.get(i);
                String dbName = dbUrl.substring(dbUrl.lastIndexOf("/") + 1,
                        dbUrl.length());
                String tmpPath = String.format("%s/%s", dbRootPath, dbName);

                MLog.d(TAG, "tmpPath = " + tmpPath);

                boolean downloadOk = false;
                int downloadCount = 0;
                File tmpF = new File(tmpPath);
                long fileSize = 0L;

                do {

                    tmpF.setReadable(true);
                    tmpF.setWritable(true);
                    if (tmpF.exists()) {
                        tmpF.delete();
                    }

                    retryCount = 0;
                    FileOutputStream fos = null;
                    InputStream is = null;
                    ResponseBody responseBody = null;
                    try {
                        do {
                            fos = new FileOutputStream(tmpF);
                            responseBody = NetUtils.Ins(AppMain.ctx()).getUrlResBody(
                                    dbUrl, null, null, null);
                            is = responseBody.byteStream();
                        } while ((is == null) && (retryCount++ < RETRY_COUNT));
                        fileSize = responseBody.contentLength();
                        byte[] buffer = new byte[512 * 1024];
                        int rLen = 0;
                        while ((rLen = is.read(buffer)) > 0) {
                            fos.write(buffer, 0, rLen);
                        }
                        fos.flush();
                        downloadOk = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        MLog.d(TAG, "");
                        downloadOk = false;
                    } finally {
                        try {
                            responseBody.close();
                        } catch (Exception e) {
                        }
                        try {
                            is.close();
                        } catch (Exception e) {
                        }
                        try {
                            fos.close();
                        } catch (Exception e) {
                        }
                    }
                } while (!downloadOk && (downloadCount++ < RETRY_COUNT));
                downloadOk = tmpF.length() == fileSize && fileSize > 0;
                if (downloadOk) {
                    if (dbName.endsWith(".db")) {
                        if (wDbF.exists()) {
                            wDbF.delete();
                        }
                        tmpF.renameTo(wDbF);
                    } else if (dbName.endsWith("-journal")) {
                        if (wJournalDbF.exists()) {
                            wJournalDbF.delete();
                        }
                        tmpF.renameTo(wJournalDbF);
                    }
                }
            }

            if (dbMgr.initDBDataOk()) {
                dbMgr.clearMoviesCache();
                dbMgr.clearKindsCache();
                dbMgr.clearCKindsCache();
                dbMgr.clearLiveChalsCache();
                dbMgr.clearLiveFavListCache();
            }

            boolean wDBOK = wDbF.exists() && wJournalDbF.exists();

            ShareAdapter.Ins(AppMain.ctx()).setB(EXVAL.NEW_MEDIA_OK, wDBOK);

            MLog.d(TAG, String.format("end isDataOk=%b wDBOK=%b", hostCls.isDataOk, wDBOK));

            if (hostCls.isDataOk) {
                if (wDBOK) {
                    boolean isMainAct = hostCls.activityType == EXVAL.TYPE_ACTIVITY_MAIN;
                    ShareAdapter.Ins(AppMain.ctx()).setB(EXVAL.NEED_UPDATE, !isMainAct);
                    if (isMainAct) {
                        MLog.d(TAG, "main act, use new db");
                        dbMgr.useNew();
                    }
                } else {
                    ShareAdapter.Ins(AppMain.ctx()).setB(EXVAL.NEED_UPDATE, false);
                }
            } else {
                if (wDBOK) {
                    dbMgr.useNew();
                    ShareAdapter.Ins(AppMain.ctx()).setB(EXVAL.NEED_UPDATE, false);
                    hostCls.isDataOk = true;
                    if (hostCls.onDataListeners != null) {
                        for (int i = 0; i < hostCls.listenerNum; i++) {
                            OnDataListener onDataListener = hostCls.onDataListeners.get(i);
                            if (onDataListener != null) {
                                onDataListener.onInitMediaOver();
                            }
                        }
                    }
                }
            }

            hostCls.initMediaDatasIng = false;
            hostCls.initMediaDatasOver = true;

            hostCls = null;
        }
    }


    private Future<?> netSpeedTask;
    private GetNetSpeedTask netSpeedTaskObj;

    public void execNetSpeed() {
        MLog.d(TAG, "execNetSpeed");
        stopNetSpeed();
        netSpeedTaskObj = new GetNetSpeedTask(this);
        netSpeedTask = taskPool.submit(netSpeedTaskObj);
    }

    public void stopNetSpeed() {
        MLog.d(TAG, "stopNetSpeed");
        if (netSpeedTaskObj != null) {
            netSpeedTaskObj.release();
            netSpeedTaskObj = null;
        }
        if (netSpeedTask != null) {
            netSpeedTask.cancel(true);
            netSpeedTask = null;
        }
    }

    private static class GetNetSpeedTask implements Runnable {

        private DataCenter hostCls;
        private boolean netSpeedRunning;

        public GetNetSpeedTask(DataCenter hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
            netSpeedRunning = false;
        }

        @Override
        public void run() {

            netSpeedRunning = true;

            long oldRxByte = 0, oldTime = 0;

            long newRxByte = 0L;
            long newTime = 0L;
            long getBytes = 0L;
            StringBuilder strBytes = new StringBuilder();

            while (netSpeedRunning) {
                newRxByte = TrafficStats.getTotalRxBytes();
                newTime = System.currentTimeMillis();
                if (((newRxByte - oldRxByte) != 0L)
                        && ((newTime - oldTime) != 0L)) {
                    getBytes = ((newRxByte - oldRxByte) * 1000L / (newTime - oldTime)) / 1024L;
                    if (getBytes >= 1000L) {
                        strBytes.append(new DecimalFormat("#.##")
                                .format(getBytes / 1024D)).append("MB/S");
                    } else {
                        strBytes.append(getBytes).append("KB/S");
                    }
                }

                if (hostCls.onPlayListeners != null) {
                    for (int i = 0; i < hostCls.playListenerNum; i++) {
                        OnPlayListener listener = hostCls.onPlayListeners.get(i);
                        if (listener != null) {
                            listener.onNetSpeed(strBytes.toString());
                        }
                    }
                }

                oldRxByte = newRxByte;
                oldTime = newTime;

                strBytes.setLength(0);

                try {
                    Thread.sleep(1500L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Future<?> submitExTask(Runnable runnable) {
        if (runnable == null || taskPool == null) {
            return null;
        }
        return taskPool.submit(runnable);
    }

    public void pay(String num, String pwd) {
        taskPool.execute(new PayTask(this, num, pwd));
    }

    private class PayTask implements Runnable {

        private DataCenter hostCls;
        private String num, pwd;

        public PayTask(DataCenter hostCls, String num, String pwd) {
            this.hostCls = hostCls;
            this.num = num;
            this.pwd = pwd;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void run() {

            if (hostCls == null) {
                return;
            }

            String domainUrl = DOAMIN_URLS[0];
            String confName = CONF_NAMES[0];
            CmsObj cmsObj = hostCls.getCms(domainUrl, confName);

            String payResult = hostCls.payForceTv(cmsObj, num, pwd);

            if (hostCls.onDataListeners != null) {
                for (int i = 0; i < hostCls.listenerNum; i++) {
                    OnDataListener onDataListener = hostCls.onDataListeners.get(i);
                    if (onDataListener != null) {
                        onDataListener.onPay(!TextUtils.isEmpty(payResult) &&
                                        payResult.equals(AppMain.res().getString(R.string.pay_ok)),
                                payResult);
                    }
                }
            }
        }
    }

    private ScanCkindTask scanCkindTaskObj;
    private Future<?> scanCkindTask;

    private void stopScanCkind() {
        if (this.scanCkindTaskObj != null) {
            this.scanCkindTaskObj.release();
            this.scanCkindTaskObj = null;
        }
        if (this.scanCkindTask != null) {
            this.scanCkindTask.cancel(true);
            this.scanCkindTask = null;
        }
    }

    public void scanCkind(Activity activity, String pCid) {
        stopScanCkind();
        this.scanCkindTaskObj = new ScanCkindTask(activity, pCid);
        this.scanCkindTask = this.taskPool.submit(this.scanCkindTaskObj);
    }

    private static class ScanCkindTask implements Runnable {

        private Activity activity;
        private String pCid;

        public ScanCkindTask(Activity activity, String pCid) {
            this.activity = activity;
            this.pCid = pCid;
        }

        public void release() {
            this.activity = null;
            this.pCid = null;
        }

        @Override
        public void run() {

            List<CKindObj> cKindObjs = null;

            if (this.pCid.equals(AppMain.res().getString(R.string.fav_history_kind_cid))) {
                cKindObjs = new ArrayList<>();
                String[] cidArr = AppMain.res().getStringArray(R.array.fav_history_cid_arr);
                String[] nameArr = AppMain.res().getStringArray(R.array.fav_history_name_arr);
                int num = cidArr.length;
                for (int i = 0; i < num; i++) {
                    CKindObj cKindObj = new CKindObj();
                    cKindObj.setName(nameArr[i]);
                    cKindObj.setCid(cidArr[i]);
                    cKindObj.setPCid(this.pCid);
                    cKindObjs.add(cKindObj);
                }
            } else {
                cKindObjs = DBMgr.Ins().queryCKinds(this.pCid);
            }

            int cKindObjNum = cKindObjs.size();

//            MLog.d(TAG, String.format("ScanCkindTask pCid=%s cKindObjNum=%d", pCid, cKindObjNum));

            if (this.activity != null) {
                if (this.activity instanceof KindMovieAct) {
                    ((KindMovieAct) this.activity).loadCKDatas(cKindObjs, cKindObjNum);
                } else if (this.activity instanceof TopicsMainAct) {
                    if (pCid.equals(EXVAL.TOPICS_TOP_CID)) {
                        ((TopicsMainAct) this.activity).loadTopCKDatas(cKindObjs, cKindObjNum);
                    }
                }
            }
        }
    }

    private ScanMoviesTask scanMoviesTaskObj;
    private Future<?> scanMoviesTask;

    private void stopScanMovies() {
        if (this.scanMoviesTaskObj != null) {
            this.scanMoviesTaskObj.release();
            this.scanMoviesTaskObj = null;
        }
        if (this.scanMoviesTask != null) {
            this.scanMoviesTask.cancel(true);
            this.scanMoviesTask = null;
        }
    }

    public void scanMovies(Activity activity, String cid, int curPageIndex, int level) {//
        stopScanMovies();
        this.scanMoviesTaskObj = new ScanMoviesTask(activity, cid, curPageIndex, level);
        this.scanMoviesTask = this.taskPool.submit(this.scanMoviesTaskObj);
    }

    private static class ScanMoviesTask implements Runnable {

        private Activity activity;
        private String cid;
        private int curPageIndex, level;

        public ScanMoviesTask(Activity activity, String cid, int curPageIndex, int level) {
            this.activity = activity;
            this.cid = cid;
            this.curPageIndex = curPageIndex;
            this.level = level;
        }

        public void release() {
            this.activity = null;
            this.cid = null;
        }

        @Override
        public void run() {

            MLog.d(TAG, String.format("ScanMoviesTask cid=%s", cid));

            boolean isFavKind = false;

            String[] cidArr = AppMain.res().getStringArray(R.array.fav_history_cid_arr);

            int cidNum = cidArr.length;
            int index = -1;

            for (int i = 0; i < cidNum; i++) {
                if (cid.equals(cidArr[i])) {
                    index = i;
                    break;
                }
            }

            List<Object> allMovieObjs = new ArrayList<>();

            int movieTotalNum = 0, movieTotalPageNum = 0;

            if (index == -1) {
                if (cid.equals(EXVAL.MXZQ_HY_CID)) {
                    List<CKindObj> cKindObjs = DBMgr.Ins().queryCKinds(cid);
                    int ckindObjNum = cKindObjs.size();
                    for (int i = 0; i < ckindObjNum; i++) {
                        List<MovieObj> ckMovieObjs =
                                DBMgr.Ins().queryMovies(cKindObjs.get(i).getCid());
                        int ckMovieNum = ckMovieObjs.size();
                        if (ckMovieNum > 0) {
                            allMovieObjs.addAll(ckMovieObjs);
                        }
                    }
                    movieTotalNum = allMovieObjs.size();
                    movieTotalPageNum = movieTotalNum / level;
                    if (movieTotalNum % level != 0) {
                        movieTotalPageNum++;
                    }
                } else {
                    movieTotalNum = DBMgr.Ins().queryMovieNumForCID(cid);
                    movieTotalPageNum = movieTotalNum / level;
                    if (movieTotalNum % level != 0) {
                        movieTotalPageNum++;
                    }
                }
            } else {
                if (index == 0) {
                    isFavKind = true;
                    movieTotalNum = DBMgr.Ins().queryFavMovieNum();
                } else if (index == 1) {
                    isFavKind = true;
                    movieTotalNum = DBMgr.Ins().queryFavCkindNum();
                } else {
                    movieTotalNum = DBMgr.Ins().queryHistoryMovieNum();
                }
                movieTotalPageNum = movieTotalNum / level;
                if (movieTotalNum % level != 0) {
                    movieTotalPageNum++;
                }
            }

            if (curPageIndex < 0) {
                curPageIndex = 0;
            }

            if (curPageIndex >= movieTotalPageNum) {
                curPageIndex = movieTotalPageNum - 1;
            }

            List<Object> movieObjs = new ArrayList<>();

            if (index == -1) {
                if (cid.equals(EXVAL.MXZQ_HY_CID)) {

                    int start = curPageIndex * level;
                    int end = start + level;

                    for (; start < end; start++) {
                        movieObjs.add(allMovieObjs.get(start));
                    }

                    MLog.d(TAG, String.format("ScanMoviesTask start=%d end=%d curPageIndex=%d",
                            start, end, this.curPageIndex));
                } else {
                    movieObjs.addAll(DBMgr.Ins().queryMovies(cid, curPageIndex, level));
                }
            } else {
                if (index == 0) {
                    movieObjs.addAll(DBMgr.Ins().queryFavMovies(curPageIndex, level));
                } else if (index == 1) {
                    movieObjs.addAll(DBMgr.Ins().queryFavCkinds(curPageIndex, level));
                } else {
                    movieObjs.addAll(DBMgr.Ins().queryHistoryMovies(curPageIndex, level));
                }
            }

            int movieNum = movieObjs.size();

            MLog.d(TAG, String.format("ScanMoviesTask movieTotalNum=%d movieTotalPageNum=%d movieNum=%d curPageIndex=%d",
                    movieTotalNum, movieTotalPageNum, movieNum, this.curPageIndex));

            if (activity != null) {
                if (activity instanceof KindMovieAct) {
                    ((KindMovieAct) activity).loadMoiveDatas(movieObjs, movieNum, movieTotalPageNum,
                            curPageIndex, isFavKind);
                } else if (activity instanceof SEDMKindListAct) {
                    ((SEDMKindListAct) activity).loadMoiveDatas(movieObjs, movieNum, movieTotalPageNum,
                            curPageIndex, isFavKind);
                }
            }
        }
    }

    private ScanMovieDetailsTask scanMovieDetailsTaskObj;
    private Future<?> scanMovieDetailsTask;

    public void stopScanMovieDetails() {
        if (this.scanMovieDetailsTaskObj != null) {
            this.scanMovieDetailsTaskObj.release();
            this.scanMovieDetailsTaskObj = null;
        }
        if (this.scanMovieDetailsTask != null) {
            this.scanMovieDetailsTask.cancel(true);
            this.scanMovieDetailsTask = null;
        }
    }

    public void scanMovieDetails(Activity activity, MovieObj movieObj) {
        stopScanMovieDetails();
        this.scanMovieDetailsTaskObj =
                new ScanMovieDetailsTask(this, activity, movieObj);
        this.scanMovieDetailsTask = taskPool.submit(this.scanMovieDetailsTaskObj);
    }

    private static class ScanMovieDetailsTask implements Runnable {

        private DataCenter hostCls;
        private MovieObj movieObj;
        private Activity activity;

        public ScanMovieDetailsTask(DataCenter hostCls, Activity activity, MovieObj movieObj) {
            this.hostCls = hostCls;
            this.movieObj = movieObj;
            this.activity = activity;
        }

        public void release() {
            this.activity = null;
            this.hostCls = null;
            this.movieObj = null;
        }

        @Override
        public void run() {

            try {
                if (this.movieObj == null) {
                    MLog.e(TAG, "ScanMovieDetailsTask movieObj null");
                    return;
                }

                MovieDetailsObj movieDetails = DBMgr.Ins().queryMovieDetails(this.movieObj.getName(),
                        this.movieObj.getCid(), this.movieObj.getMovieId());

                if (movieDetails == null) {
                    CmsObj cmsObj = this.hostCls.getCms(DOAMIN_URLS[0], CONF_NAMES[0]);
                    String detailsUrl = String.format(VOD_MOVIE_FMT, cmsObj.getDominUrl(),
                            this.movieObj.getMovieId());
                    String detailsVal = null;
                    int retryCount = 0;
                    do {
                        detailsVal = NetUtils.Ins(AppMain.ctx()).getUrlStr(
                                detailsUrl, cmsObj.getToken(), null, null);
                    } while (TextUtils.isEmpty(detailsVal) && (retryCount++ < RETRY_COUNT));

                    MLog.d(TAG, String.format("detailsUrl = %s detailsVal = %s", detailsUrl, detailsVal));

//                Utils.saveStrDataToSD(AppMain.ctx(), "details.txt", detailsVal);

                    MovieParser movieParser = new MovieParser(VOD_IMG_FMT, VOD_IMG_FMT1,
                            cmsObj.getDominUrl(), this.movieObj);
                    movieParser.parse(detailsVal);

                    movieDetails = movieParser.getMovieDetails();
                }

                boolean isFav = DBMgr.Ins().movieInFavDB(this.movieObj);

                MovieObj historyMovieObj = DBMgr.Ins().queryHistoryMovie(this.movieObj);

                boolean isHistory = historyMovieObj != null;

                if (isHistory) {
                    this.movieObj.setPlayPos(historyMovieObj.getPlayPos());
                }

                int movieNumForCid = DBMgr.Ins().queryMovieNumForCID(this.movieObj.getCid());

                MLog.d(TAG, String.format("cid=%s movieNumForCid=%d",
                        this.movieObj.getCid(), movieNumForCid));
                int[] allNumArr = new int[movieNumForCid];

                for (int i = 0; i < movieNumForCid; i++) {
                    allNumArr[i] = i;
                }

                String cid = movieObj.getCid();
                int index = 0, temp = 0, reMovieNum = Math.min(movieNumForCid, EXVAL.RE_MOVIE_NUM);
                List<MovieObj> reMovieObjs = new ArrayList<>(reMovieNum);

                for (int i = 0; i < reMovieNum; i++) {
                    Random random = new Random();
                    index = random.nextInt(movieNumForCid - i) + i;
                    temp = allNumArr[index];
                    allNumArr[index] = allNumArr[i];
                    allNumArr[i] = temp;
                    MovieObj movieObj = DBMgr.Ins().queryMovieForPos(cid, temp);
                    MLog.d(TAG, String.format("select re movies[%d]=%s", temp, movieObj));
                    if (movieObj != null) {
                        reMovieObjs.add(movieObj);
                    }
                }

                if (this.activity != null && movieDetails != null) {
                    if (this.activity instanceof DetailsAct) {
                        int filmIdNum = movieDetails.getFilmIdNum();
                        int filmIdPageNum = filmIdNum / EXVAL.COLLECTION_NUM_IN_LINE;
                        if (filmIdNum % EXVAL.COLLECTION_NUM_IN_LINE != 0) {
                            filmIdPageNum++;
                        }
                        movieDetails.setFilmIdPageNum(filmIdPageNum);
                        ((DetailsAct) this.activity).loadDetails(movieDetails, reMovieObjs, isFav, isHistory);
                    } else if (this.activity instanceof HotReDetailsAct) {
                        int filmIdNum = movieDetails.getFilmIdNum();
                        int filmIdPageNum = filmIdNum / EXVAL.COLLECTION_NUM_IN_LINE;
                        if (filmIdNum % EXVAL.COLLECTION_NUM_IN_LINE != 0) {
                            filmIdPageNum++;
                        }
                        movieDetails.setFilmIdPageNum(filmIdPageNum);
                        reMovieObjs.clear();
                        ((HotReDetailsAct) this.activity).loadDetails(movieDetails);
                    } else if (this.activity instanceof SEDMMovieDetailsAct) {
                        int filmIdNum = movieDetails.getFilmIdNum();
                        int filmIdPageNum = filmIdNum / EXVAL.SEDM_COLLECTION_NUM_IN_PAGE;
                        if (filmIdNum % EXVAL.SEDM_COLLECTION_NUM_IN_PAGE != 0) {
                            filmIdPageNum++;
                        }
                        movieDetails.setFilmIdPageNum(filmIdPageNum);
                        ((SEDMMovieDetailsAct) this.activity).loadDetails(movieDetails, reMovieObjs,
                                isFav, isHistory);
                    } else if (this.activity instanceof TopicsDetailsAct) {
                        reMovieObjs.clear();
                        ((TopicsDetailsAct) this.activity).loadDetails(movieDetails);
                    }
                }
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.release();
            }
        }
    }

    private Future<?> handleFavMovieTask;
    private InsertFavMovieTask handleFavMovieTaskObj;

    public void handleFavMovie(Object object, boolean isFav) {
        stopHandleFavMovie();
        handleFavMovieTaskObj = new InsertFavMovieTask(this, object, isFav);
        handleFavMovieTask = taskPool.submit(handleFavMovieTaskObj);
    }

    public void stopHandleFavMovie() {
        if (handleFavMovieTaskObj != null) {
            handleFavMovieTaskObj.release();
            handleFavMovieTaskObj = null;
        }
        if (handleFavMovieTask != null) {
            handleFavMovieTask.cancel(true);
            handleFavMovieTask = null;
        }
    }

    private static class InsertFavMovieTask implements Runnable {

        private Object object;
        private DataCenter hostCls;
        private boolean isFav;

        public InsertFavMovieTask(DataCenter hostCls, Object object, boolean isFav) {
            this.hostCls = hostCls;
            this.object = object;
            this.isFav = isFav;
        }

        public void release() {
            this.object = null;
            this.hostCls = null;
        }

        @Override
        public void run() {

            if (this.hostCls == null || this.object == null) {
                return;
            }

            if (isFav) {
                if (this.object instanceof MovieObj) {
                    DBMgr.Ins().insertFavMovie((MovieObj) this.object);
                } else if (this.object instanceof CKindObj) {
                    DBMgr.Ins().insertFavCkind((CKindObj) this.object);
                }
            } else {
                if (this.object instanceof MovieObj) {
                    MovieObj movieInDB = DBMgr.Ins().queryFavMovie((MovieObj) this.object);
                    DBMgr.Ins().delFavMovie(movieInDB);
                } else if (this.object instanceof CKindObj) {
                    CKindObj ckindInDB = DBMgr.Ins().queryFavCkind((CKindObj) this.object);
                    DBMgr.Ins().delFavCkind(ckindInDB);
                }
            }

            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendForceTVHttpReq(MovieObj movieObj) {
        if (!forceTvOK) {
            throw new IllegalArgumentException("Call initForceTV err");
        }
        taskPool.execute(new SendForceTVHttpReqTask(this, movieObj));
    }

    private static class SendForceTVHttpReqTask implements Runnable {

        private DataCenter hostCls;
        private MovieObj movieObj;

        public SendForceTVHttpReqTask(DataCenter hostCls, MovieObj movieObj) {
            this.hostCls = hostCls;
            this.movieObj = movieObj;
        }

        @Override
        public void run() {

            if (this.movieObj == null) {
                return;
            }

            FilmID filmId = DBMgr.Ins().queryFilmIds(
                    this.movieObj.getName(), this.movieObj.getCid(), this.movieObj.getMovieId(),
                    this.movieObj.getPlayPos());

            MLog.d(TAG, "filmId = " + filmId);

            if (filmId == null) {
                return;
            }

            DBMgr.Ins().insertHistoryMovie(this.movieObj);

            String ipStr = null, portStr = null;
            CmsObj cmsObj = hostCls.getCms(DOAMIN_URLS[0], CONF_NAMES[0]);
            SparseArray<IPObj> ips = cmsObj.getIps();
            int ipNum = ips == null ? 0 : ips.size();
            if (ipNum > 0) {
                IPObj ipObj = ips.get(0);
                ipStr = ipObj.getIp();
                portStr = ipObj.getPort();
            } else {
                MLog.e(TAG, "no ip info");
            }

            String reqValStr = hostCls.sendForceTVHttpReq(cmsObj, ipStr, portStr, filmId.getFilmId(),
                    String.valueOf(this.movieObj.getMovieId()), this.movieObj.getCid());

            if (!TextUtils.isEmpty(reqValStr)) {
                for (int i = 0; i < hostCls.playListenerNum; i++) {
                    OnPlayListener listener = hostCls.onPlayListeners.get(i);
                    if (listener != null) {
                        listener.onPlay(reqValStr);
                    }
                }
            } else {
                MLog.e(TAG, "connect forectv err");
            }

            hostCls = null;
        }
    }

    public void sendForceTVHttpReq(int pos, String movieName, int movieId, String kindName) {
        if (!forceTvOK) {
            throw new IllegalArgumentException("Call initForceTV err");
        }
        taskPool.execute(new SendForceTVHttpReqTask1(this, pos, movieName, movieId, kindName));
    }

    private static class SendForceTVHttpReqTask1 implements Runnable {

        private DataCenter hostCls;
        private String colId, movieName;
        private int pos, movieId;

        public SendForceTVHttpReqTask1(DataCenter hostCls, int pos, String movieName, int movieId,
                                       String colId) {
            this.hostCls = hostCls;
            this.movieId = movieId;
            this.colId = colId;
            this.movieName = movieName;
            this.pos = pos;
        }

        @Override
        public void run() {

            FilmID filmId = DBMgr.Ins().queryFilmIds(movieName, colId, movieId, pos);

            MLog.d(TAG, "filmId = " + filmId);

            if (filmId == null) {
                return;
            }

            String ipStr = null, portStr = null;
            CmsObj cmsObj = hostCls.getCms(DOAMIN_URLS[0], CONF_NAMES[0]);
            SparseArray<IPObj> ips = cmsObj.getIps();
            int ipNum = ips == null ? 0 : ips.size();
            if (ipNum > 0) {
                IPObj ipObj = ips.get(0);
                ipStr = ipObj.getIp();
                portStr = ipObj.getPort();
            } else {
                MLog.e(TAG, "no ip info");
            }

            String reqValStr = hostCls.sendForceTVHttpReq(cmsObj, ipStr, portStr, filmId.getFilmId(),
                    String.valueOf(movieId), colId);

            if (!TextUtils.isEmpty(reqValStr)) {
                for (int i = 0; i < hostCls.playListenerNum; i++) {
                    OnPlayListener listener = hostCls.onPlayListeners.get(i);
                    if (listener != null) {
                        listener.onPlay(reqValStr);
                    }
                }
            } else {
                MLog.e(TAG, "connect forectv err");
            }

            hostCls = null;
        }
    }

    private ScanAllMoviesTask scanAllMoviesTaskObj;
    private Future<?> scanAllMoviesTask;

    public void stopScanAllMovies() {
        if (this.scanAllMoviesTaskObj != null) {
            this.scanAllMoviesTaskObj.release();
            this.scanAllMoviesTaskObj = null;
        }
        if (this.scanAllMoviesTask != null) {
            this.scanAllMoviesTask.cancel(true);
            this.scanAllMoviesTask = null;
        }
    }

    public void scanAllMovies(Activity activity, String cid) {
        this.stopScanAllMovies();
        this.scanAllMoviesTaskObj = new ScanAllMoviesTask(activity, cid);
        this.scanAllMoviesTask = this.taskPool.submit(this.scanAllMoviesTaskObj);
    }

    private static class ScanAllMoviesTask implements Runnable {

        private Activity activity;
        private String cid;

        public ScanAllMoviesTask(Activity activity, String cid) {
            this.activity = activity;
            this.cid = cid;
        }

        public void release() {
            this.activity = null;
            this.cid = null;
        }

        @Override
        public void run() {

            List<MovieObj> movieObjs = DBMgr.Ins().queryMovies(cid);

            int movieNum = movieObjs.size();

            MLog.d(TAG, String.format("ScanAllMoviesTask cid=%s movieNum=%d", cid, movieNum));

            if (activity != null) {
                if (activity instanceof HotReDetailsAct) {
                    ((HotReDetailsAct) activity).loadMovieDatas(movieObjs, movieNum);
                } else if (activity instanceof TopicsDetailsAct) {
                    ((TopicsDetailsAct) activity).loadMovieDatas(movieObjs, movieNum);
                }
            }
        }
    }

    public void scanPlayPos(Activity activity, MovieObj movieObj) {
        taskPool.execute(new ScanPlayPosTask(activity, movieObj));
    }

    private static final class ScanPlayPosTask implements Runnable {

        private Activity activity;
        private MovieObj movieObj;

        public ScanPlayPosTask(Activity activity, MovieObj movieObj) {
            this.activity = activity;
            this.movieObj = movieObj;
        }

        @Override
        public void run() {

            MovieObj historyMovieObj = DBMgr.Ins().queryHistoryMovie(movieObj);

            int playPos = historyMovieObj == null ? -1 : historyMovieObj.getPlayPos();

            if (activity != null) {
                if (activity instanceof SEDMMovieDetailsAct) {
                    ((SEDMMovieDetailsAct) activity).loadPlayPos(playPos);
                }
            }

            this.activity = null;
        }
    }

    private ScanCkindForPageTask scanCkindTaskForPageObj;
    private Future<?> scanCkindTaskForPageTask;

    private void stopScanCkindForPage() {
        if (this.scanCkindTaskForPageObj != null) {
            this.scanCkindTaskForPageObj.release();
            this.scanCkindTaskForPageObj = null;
        }
        if (this.scanCkindTaskForPageTask != null) {
            this.scanCkindTaskForPageTask.cancel(true);
            this.scanCkindTaskForPageTask = null;
        }
    }

    public void scanCkindForPage(Activity activity, String pCid, int pageIndex, int numInPage,
                                 boolean hasFocus) {
        stopScanCkindForPage();
        this.scanCkindTaskForPageObj = new ScanCkindForPageTask(activity, pCid, pageIndex,
                numInPage, hasFocus);
        this.scanCkindTaskForPageTask = this.taskPool.submit(this.scanCkindTaskForPageObj);
    }

    private static class ScanCkindForPageTask implements Runnable {

        private Activity activity;
        private String pCid;
        private int pageIndex, numInPage;
        private boolean hasFocus;

        public ScanCkindForPageTask(Activity activity, String pCid, int pageIndex, int numInPage,
                                    boolean hasFocus) {
            this.activity = activity;
            this.pCid = pCid;
            this.pageIndex = pageIndex;
            this.numInPage = numInPage;
            this.hasFocus = hasFocus;
        }

        public void release() {
            this.activity = null;
            this.pCid = null;
        }

        @Override
        public void run() {

            int ckTotalNum = DBMgr.Ins().queryCKindNumForPCID(this.pCid);
            int ckTotalPageNum = ckTotalNum / numInPage;
            if (ckTotalNum % numInPage != 0) {
                ckTotalPageNum++;
            }

            if (this.pageIndex < 0) {
                this.pageIndex = 0;
            }

            if ((this.pageIndex >= ckTotalPageNum) && (ckTotalPageNum > 0)) {
                this.pageIndex = ckTotalPageNum - 1;
            }

            //List<CKindObj> cKindObjs = DBMgr.Ins().queryCKindsForPage(this.pCid, this.pageIndex);
            List<CKindObj> cKindObjs = new ArrayList<>();


            List<CKindObj> cKindAllObjs = //DBMgr.Ins().queryCKindsForPage(this.pCid);

            invertedOrder(DBMgr.Ins().queryCKindsForAll(this.pCid));//将原来的数据倒序

            int cKindObjNum = DBMgr.Ins().queryCKindsForPage(this.pCid, this.pageIndex).size();

            for (int i = pageIndex*EXVAL.TOPICS_CKIND_NUM_IN_PAGE,count = 0; i<cKindAllObjs.size()&&count < cKindObjNum; i++) {

                cKindObjs.add(cKindAllObjs.get(i));
            }

//            MLog.d(TAG, String.format("ScanCkindForPageTask pageIndex=%d pCid=%s cKindObjNum=%d",
//                    this.pageIndex, pCid, cKindObjNum));

            if (this.activity != null) {
                if (this.activity instanceof TopicsMainAct) {
                    ((TopicsMainAct) this.activity).loadCKDatas(cKindObjs, cKindObjNum, ckTotalNum,
                            ckTotalPageNum, this.pageIndex, this.hasFocus);
                }
            }
        }

        private List<CKindObj> invertedOrder(List<CKindObj> cKindObjs) {
            List<CKindObj> newList = new ArrayList<>();

            for (int i = cKindObjs.size()-1; i >+ 0; i--) {
                newList.add(cKindObjs.get(i));
            }

            return newList;
        }
    }

    public void clearHistoryOrFav(int pageType) {
        taskPool.execute(new ClearHistoryOrFav(pageType));
    }

    private class ClearHistoryOrFav implements Runnable {

        private int pageType;

        public ClearHistoryOrFav(int pageType) {
            this.pageType = pageType;
        }

        @Override
        public void run() {

            if (EXVAL.PAGE_FAV == pageType) {
                DBMgr.Ins().clearFavMovies();
                DBMgr.Ins().clearFavCkind();
            } else if (EXVAL.PAGE_HISTORY == pageType) {
                DBMgr.Ins().clearHistoryMovies();
            }
        }
    }

    public void handleChalInFav(LiveAct liveAct) {
        taskPool.execute(new HandleChalInFavTask(this, liveAct));
    }

    private static class HandleChalInFavTask implements Runnable {

        private DataCenter hostCls;
        private LiveAct liveAct;

        public HandleChalInFavTask(DataCenter hostCls, LiveAct liveAct) {
            this.hostCls = hostCls;
            this.liveAct = liveAct;
        }

        @Override
        public void run() {

            String favStr = AppMain.res().getString(R.string.fav_list);
            LiveChalObj tmpChal = liveAct.selLiveChal;
            LiveChalObj chal = new LiveChalObj();
            chal.setIsFav(liveAct.selLiveChal.getIsFav());
            chal.setColId(liveAct.selLiveChal.getColId());
            chal.setColName(favStr);
            chal.setName(liveAct.selLiveChal.getName());
            chal.setFilmId(liveAct.selLiveChal.getFilmId());
            chal.setVodId(liveAct.selLiveChal.getVodId());
            chal.setPType(liveAct.selLiveChal.getPType());

            boolean isFav = chal.getIsFav();

            List<LiveChalObj> favList = DBMgr.Ins().queryLiveFavList();
            int index = favList.indexOf(tmpChal);

            MLog.d(TAG, String.format("Fav item isFav=%b index=%d", isFav, index));

            if (isFav) {
                if (index < 0) {
                    favList.add(chal);
                }
            } else {
                if (index >= 0) {
                    favList.remove(index);
                }
            }

            int favNum = favList.size();
            for (int i = 0; i < favNum; i++) {
                LiveChalObj favChal = favList.get(i);
                favChal.setListPos(i);
                favChal.setUiPos(String.valueOf(i + 1));
            }

            DBMgr.Ins().clearLiveFavList();
            DBMgr.Ins().clearLiveFavListCache();
            DBMgr.Ins().insertLiveFavList(favList);

            int pageNum = favNum / EXVAL.NUM_IN_COL_LIVE;
            int pageMod = favNum % EXVAL.NUM_IN_COL_LIVE;
            if (pageMod != 0) {
                pageNum++;
            }

            LiveKindObj favKind = DBMgr.Ins().queryLivekind(favStr);
            favKind.setPageNum(pageNum);
            favKind.setChalNum(favNum);

            DBMgr.Ins().updateLivekind(favKind, false);

            if (liveAct.tmpCacheLiveKind.getColName().equals(favStr)) {
                liveAct.tmpCacheLiveKind.setChalNum(favNum);
                liveAct.tmpCacheLiveKind.setPageNum(pageNum);
                if (liveAct.cacheLiveKind.getColName().equals(favStr)) {
                    liveAct.setCacheKindVal(liveAct.tmpCacheLiveKind);
                }
                if (hostCls.onDataListeners != null) {
                    for (int i = 0; i < hostCls.listenerNum; i++) {
                        OnDataListener onDataListener = hostCls.onDataListeners.get(i);
                        if (onDataListener != null) {
                            onDataListener.onFavUpdate();
                        }
                    }
                }
            }

            List<LiveChalObj> chals = DBMgr.Ins().queryLiveChalForName(tmpChal.getName());

            int chalNum = chals.size();

            for (int i = 0; i < chalNum; i++) {
                chals.get(i).setIsFav(isFav);
            }

            DBMgr.Ins().updateLiveChals(chals, chalNum);
            DBMgr.Ins().clearLiveChalsCache();

            chals = DBMgr.Ins().queryLiveChalForNameForAll(tmpChal.getName());

            chalNum = chals.size();

            for (int i = 0; i < chalNum; i++) {
                chals.get(i).setIsFav(isFav);
            }

            DBMgr.Ins().updateLiveChalsForAll(chals, chalNum);
            DBMgr.Ins().clearLiveDatasCacheForAll();
            DBMgr.Ins().clearLiveChalsCache();

            hostCls = null;
            liveAct = null;
        }
    }

    private GetLiveDataTask getLiveDataTaskObj;
    private Future<?> getLiveDataTask;

    public void stopGetLiveData() {
        if (getLiveDataTaskObj != null) {
            getLiveDataTaskObj.release();
            getLiveDataTaskObj = null;
        }
        if (getLiveDataTask != null) {
            getLiveDataTask.cancel(true);
            getLiveDataTask = null;
        }
    }

    public void getLiveData(LiveAct liveAct) {
        stopGetLiveData();
        getLiveDataTaskObj = new GetLiveDataTask(this, liveAct);
        getLiveDataTask = taskPool.submit(getLiveDataTaskObj);
    }

    private static class GetLiveDataTask implements Runnable {

        private DataCenter hostCls;
        private LiveAct liveAct;

        public GetLiveDataTask(DataCenter hostCls, LiveAct liveAct) {
            this.hostCls = hostCls;
            this.liveAct = liveAct;
        }

        public void release() {
            this.liveAct = null;
        }

        @Override
        public void run() {

            while (!DBMgr.Ins().initDBDataOk() && (liveAct != null)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            liveAct.liveDataDBOK = false;
            try {

                int verCode = AppMain.ctx().getPackageManager().
                        getPackageInfo(AppMain.ctx().getPackageName(), 0).versionCode;

                String allKindName = AppMain.res().getString(R.string.all_kind_name);

                List<LiveChalObj> needUpdateChals = new ArrayList<>();
                List<LiveChalObj> newFavList = new ArrayList<>();
                List<LiveChalObj> cacheFavList = DBMgr.Ins().queryLiveFavList();

                int cacheFavNum = cacheFavList == null ? 0 : cacheFavList.size();

                for (int i = 0, j = 0; i < cacheFavNum; i++) {
                    LiveChalObj favChal = cacheFavList.get(i);
                    List<LiveChalObj> chals = DBMgr.Ins().queryLiveChalForName(favChal.getName());

                    int chalNum = chals == null ? 0 : chals.size();

                    for (int k = 0; k < chalNum; k++) {
                        LiveChalObj dbChal = chals.get(k);
                        dbChal.setIsFav(true);
                        if (k == 0) {
                            favChal.setIsFav(true);
                            favChal.setVodId(dbChal.getVodId());
                            favChal.setFilmId(dbChal.getFilmId());
                            favChal.setListPos(j++);
                            favChal.setUiPos(String.valueOf(j));
                            newFavList.add(favChal);
                        }
                        needUpdateChals.add(dbChal);
                    }
                }

                DBMgr.Ins().clearLiveFavListCache();
                DBMgr.Ins().clearLiveFavList();
                DBMgr.Ins().clearLiveDatasForAll();

                int newFavNum = newFavList.size();

                if (newFavNum > 0) {
                    DBMgr.Ins().updateLiveChals(needUpdateChals, newFavNum);
                    DBMgr.Ins().insertLiveFavList(newFavList);
                }

                int favPageNum = newFavNum / EXVAL.NUM_IN_COL_LIVE;
                int favPaageMod = newFavNum % EXVAL.NUM_IN_COL_LIVE;

                if (favPaageMod != 0) {
                    favPageNum++;
                }

                String favStr = AppMain.res().getString(R.string.fav_list);
                LiveKindObj favKind = DBMgr.Ins().queryLivekind(favStr);
                boolean isInsert = favKind == null;
                if (isInsert) {
                    favKind = new LiveKindObj();
                }
                favKind.setPType(EXVAL.PTYPE_FAV_LIVE);
                favKind.setChalNum(newFavNum);
                favKind.setPageNum(favPageNum);
                favKind.setColName(favStr);
                favKind.setColId(String.valueOf(EXVAL.PTYPE_FAV_LIVE));

                if (isInsert) {
                    DBMgr.Ins().insertLiveKind(favKind);
                } else {
                    DBMgr.Ins().updateLivekind(favKind, false);
                }

                MLog.d(TAG, "handle fav list cache over");
                MLog.d(TAG, "start handle cache chal and kind");

                liveAct.liveKinds = hostCls.getKinds(DBMgr.Ins().queryLiveKinds());

                int liveTotalNum = DBMgr.Ins().queryLiveChalNum(false);
                int liveTotalPageNum = liveTotalNum / EXVAL.NUM_IN_COL_LIVE;
                if (liveTotalNum % EXVAL.NUM_IN_COL_LIVE != 0) {
                    liveTotalPageNum++;
                }
                LiveKindObj allLiveKindObj = new LiveKindObj();
                allLiveKindObj.setCKNum(0);
                allLiveKindObj.setChalNum(liveTotalNum);
                allLiveKindObj.setPageNum(liveTotalPageNum);
                allLiveKindObj.setHaveChildren(false);
                allLiveKindObj.setColName(allKindName);
                allLiveKindObj.setColId(allKindName);
                liveAct.liveKinds.add(0, allLiveKindObj);

                liveAct.livekindNum = liveAct.liveKinds.size();
                MLog.d(TAG, "livekindNum = " + liveAct.livekindNum);

                DBMgr.Ins().insertLiveDatasInAll();
                DBMgr.Ins().clearLiveDatasCacheForAll();
                DBMgr.Ins().clearLiveChalsCache();

                CacheChalObj cacheChal = DBMgr.Ins().queryCacheChal();
                CacheKindObj cacheKind = DBMgr.Ins().queryCacheKind();

                if (cacheKind != null) {
                    if (cacheKind.getColName().equals(favKind.getColName())) {
                        liveAct.isLiveFavK = newFavNum > 0;
                        if (!liveAct.isLiveFavK) {
                            cacheKind = null;
                        }
                    }
                }

                boolean isAllKind = cacheKind == null || (cacheKind.getColName().equals(allKindName));

                int cacheVerCode = ShareAdapter.Ins(AppMain.ctx()).getI(EXVAL.VERCODE);

                if (cacheVerCode < verCode) {
                    isAllKind = true;
                    ShareAdapter.Ins(AppMain.ctx()).setI(EXVAL.VERCODE, verCode);
                }

                MLog.d(TAG, "isAllKind = " + isAllKind);

                if (cacheChal != null) {
                    if (liveAct.isLiveFavK) {
                        liveAct.curLiveChal = DBMgr.Ins().queryLiveFavChal(cacheChal.getName());
                        if (liveAct.curLiveChal == null) {
                            liveAct.curLiveChal = DBMgr.Ins().queryLiveFavChal(0);
                        }
                    } else {
                        liveAct.curLiveChal = isAllKind ?
                                DBMgr.Ins().queryLiveChalForAll(cacheChal.getName()) :
                                DBMgr.Ins().queryLiveChal(cacheChal.getName(),
                                        cacheChal.getColName());
                    }
                }

                if (liveAct.curLiveChal == null) {
                    liveAct.curLiveChal = isAllKind ?
                            DBMgr.Ins().queryLiveChalForAll(0) :
                            DBMgr.Ins().queryLiveChal(cacheChal.getName());
                }

                MLog.d(TAG, "init " + liveAct.curLiveChal);

                if (liveAct.curLiveChal != null) {
                    if (liveAct.isLiveFavK) {
                        liveAct.curLiveKind = DBMgr.Ins().queryLiveKindForPType(EXVAL.PTYPE_FAV_LIVE);
                        liveAct.curLiveKIndex = -1;
                        liveAct.curLiveCKIndex = -1;
                        liveAct.curLiveChalIndex = liveAct.curLiveChal.getListPos();
                        liveAct.curLiveCKind = null;
                        liveAct.curLiveKIndex = -1;
                        liveAct.curLiveCKIndex = -1;
                        liveAct.setCacheKindVal(String.valueOf(EXVAL.PTYPE_FAV_LIVE),
                                favKind.getColName(), newFavNum, favPageNum);
                    } else {

                        liveAct.curLiveKind = isAllKind ?
                                liveAct.liveKinds.get(0) :
                                DBMgr.Ins().queryLivekind(cacheKind.getColName());

                        if (liveAct.curLiveKind != null) {
                            liveAct.curLiveCKind = null;
                            liveAct.curLiveKIndex = liveAct.liveKinds.indexOf(liveAct.curLiveKind);
                            liveAct.curLiveCKIndex = -1;
                            liveAct.setCacheKindVal(liveAct.curLiveKind.getColId(),
                                    liveAct.curLiveKind.getColName(), liveAct.curLiveKind.getChalNum(),
                                    liveAct.curLiveKind.getPageNum());
                        } else {
                            liveAct.curLiveCKind = DBMgr.Ins().queryLiveCkind(
                                    liveAct.curLiveChal.getColName(), liveAct.curLiveChal.getColId());
                            if (liveAct.curLiveCKind != null) {
                                liveAct.cacheLiveKind.setColName(liveAct.curLiveCKind.getColName());
                                liveAct.cacheLiveKind.setColId(liveAct.curLiveCKind.getColId());
                                liveAct.curLiveKind = DBMgr.Ins().queryLivekind(liveAct.curLiveCKind.getPColName());
                                List<LiveCKindObj> curCKinds = DBMgr.Ins().queryLiveCKindsForCol(
                                        liveAct.curLiveKind.getColId(), liveAct.curLiveKind.getColName());
                                liveAct.curLiveKIndex = liveAct.liveKinds.indexOf(liveAct.curLiveKind);
                                liveAct.curLiveCKIndex = curCKinds.indexOf(liveAct.curLiveCKind);
                                liveAct.setCacheKindVal(liveAct.curLiveCKind.getColId(),
                                        liveAct.curLiveCKind.getColName(), liveAct.curLiveCKind.getChalNum(),
                                        liveAct.curLiveCKind.getPageNum());
                                curCKinds.clear();
                            }
                        }
                    }
                }

                if (TextUtils.isEmpty(liveAct.cacheLiveKind.getColId()) &&
                        TextUtils.isEmpty(liveAct.cacheLiveKind.getColName())) {
                    for (int i = 0; i < liveAct.livekindNum; i++) {
                        LiveKindObj kind = liveAct.liveKinds.get(i);
                        if (kind.getHaveChildren()) {
                            MLog.d(TAG, kind.toString());
                            List<LiveCKindObj> cKinds = DBMgr.Ins().queryLiveCKindsForCol(
                                    kind.getColId(), kind.getColName());
                            for (int ci = 0; ci < kind.getCKNum(); ci++) {
                                LiveCKindObj cKind = cKinds.get(ci);
                                if (cKind.getChalNum() > 0) {
                                    liveAct.curLiveKind = kind;
                                    liveAct.curLiveCKind = cKind;
                                    liveAct.curLiveKIndex = i;
                                    liveAct.curLiveCKIndex = 0;
                                    liveAct.setCacheKindVal(liveAct.curLiveCKind.getColId(),
                                            liveAct.curLiveCKind.getColName(), liveAct.curLiveCKind.getChalNum(),
                                            liveAct.curLiveCKind.getPageNum());
                                    break;
                                } else {
                                    cKinds.clear();
                                }
                            }
                        } else {
                            if (kind.getChalNum() > 0) {
                                liveAct.curLiveKind = kind;
                                liveAct.curLiveCKind = null;
                                liveAct.curLiveKIndex = i;
                                liveAct.curLiveCKIndex = -1;
                                liveAct.setCacheKindVal(liveAct.curLiveKind.getColId(),
                                        liveAct.curLiveKind.getColName(), liveAct.curLiveKind.getChalNum(),
                                        liveAct.curLiveKind.getPageNum());
                                break;
                            }
                        }

                        if (!TextUtils.isEmpty(liveAct.cacheLiveKind.getColId()) &&
                                !TextUtils.isEmpty(liveAct.cacheLiveKind.getColName())) {
                            break;
                        }
                    }
                }

                if ((liveAct.curLiveChal == null) && (!TextUtils.isEmpty(liveAct.cacheLiveKind.getColId()) &&
                        !TextUtils.isEmpty(liveAct.cacheLiveKind.getColName()))) {
                    liveAct.curLiveChal = DBMgr.Ins().queryLiveChal(
                            liveAct.cacheLiveKind.getColName(), liveAct.cacheLiveKind.getColId(), 0);
                }

                if (liveAct.curLiveChal != null) {
                    liveAct.liveDataDBOK = true;
                    liveAct.curLiveChalIndex = liveAct.curLiveChal.getListPos();
                    liveAct.setTmpCacheKindVal(liveAct.cacheLiveKind);
                    liveAct.curLivePageIndex = liveAct.curLiveChalIndex / EXVAL.NUM_IN_COL_LIVE;
                    liveAct.selLivePageIndex = liveAct.curLivePageIndex;
                    liveAct.selLiveChalIndex = liveAct.curLiveChalIndex % EXVAL.NUM_IN_COL_LIVE;
                    liveAct.selLiveCKIndex = liveAct.curLiveCKIndex;
                    liveAct.selLiveKIndex = liveAct.curLiveKIndex;
                    liveAct.selLiveKind = liveAct.curLiveKind;
                    liveAct.selLiveCKind = liveAct.curLiveCKind;
                    liveAct.selLiveChal = liveAct.curLiveChal;
                    if (hostCls.onDataListeners != null) {
                        for (int i = 0; i < hostCls.listenerNum; i++) {
                            OnDataListener onDataListener = hostCls.onDataListeners.get(i);
                            if (onDataListener != null) {
                                onDataListener.onChalList(liveAct.curLiveChal, true);
                            }
                        }
                    }
                } else {
                    liveAct.liveDataDBOK = false;
                }

                if (cacheFavNum > 0) {
                    cacheFavList.clear();
                }

                MLog.d(TAG, "over handle cache chal and kind");
            } catch (Exception e) {
                e.printStackTrace();
                MLog.e(TAG, "handle all chals err");
            }

            hostCls = null;
        }
    }
    private String[] kindStrs = new String[]{
            "全部", "H265", "港澳", "大陆", "台湾", "国际1", "国际2", "越南", "体育", "少儿", "菲律宾"
    };
    private List<LiveKindObj> getKinds(List<LiveKindObj> kinds) {

        List<LiveKindObj> kindList = new ArrayList<>();
        for (int i = 0; i < kindStrs.length; i++) {
            for (int j = 0; j < kinds.size(); j++) {
                String colName = kinds.get(j).getColName();
                String k = kindStrs[i];
                if (kinds.get(j).getColName().equals(kindStrs[i])){
                    kindList.add(kinds.get(j));
                    break;
                }
            }
        }
        return kindList;
    }
    private PlayChal playChalTaskObj;
    private Future<?> playChalTask;

    private void stopPlayChal() {
        if (playChalTask != null) {
            playChalTask.cancel(true);
            playChalTask = null;
        }
        if (playChalTaskObj != null) {
            playChalTaskObj.release();
            playChalTaskObj = null;
        }
    }

    public void playChal(LiveAct mainActivity, boolean autoPlay) {
        stopPlayChal();
        playChalTaskObj = new PlayChal(mainActivity, this, autoPlay);
        playChalTask = taskPool.submit(playChalTaskObj);
    }

    private static class PlayChal implements Runnable {

        private LiveAct liveAct;
        private DataCenter hostCls;
        private boolean autoPlay;

        public PlayChal(LiveAct liveAct, DataCenter hostCls, boolean autoPlay) {
            this.liveAct = liveAct;
            this.hostCls = hostCls;
            this.autoPlay = autoPlay;
        }

        public void release() {
            liveAct = null;
        }

        @Override
        public void run() {

            try {
                LiveChalObj curChal = null;

                if (liveAct.isLiveFavK) {
                    curChal = DBMgr.Ins().queryLiveFavChal(liveAct.curLiveChalIndex);
                } else {
                    if (liveAct.cacheLiveKind.getColName().equals(
                            AppMain.res().getString(R.string.all_kind_name))) {
                        curChal = DBMgr.Ins().queryLiveChalForAll(liveAct.curLiveChalIndex);
                    } else {
                        curChal = DBMgr.Ins().queryLiveChal(
                                liveAct.cacheLiveKind.getColName(), liveAct.cacheLiveKind.getColId(),
                                liveAct.curLiveChalIndex);
                    }
                }

                if (curChal != null && liveAct != null && curChal.getListPos() == liveAct.curLiveChalIndex) {
                    liveAct.onChalSel(curChal, autoPlay);
                }

                hostCls = null;
                liveAct = null;
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendDataToSer(String content) {
        taskPool.execute(new SendDataToSerTask(content));
    }

    public static class SendDataToSerTask implements Runnable {

        private String content;

        public SendDataToSerTask(String content) {
            this.content = content;
        }

        @Override
        public void run() {

            Socket socket = null;
            OutputStream os = null;
            InputStream is = null;
            PrintWriter printWriter = null;
            byte[] buffer = new byte[128];
            boolean isRunning = true;
            int count = 0;
            while (isRunning && count++ < RETRY_COUNT) {
                try {
                    socket = new Socket("jilu.auroratvbox.com", 19730);
                    socket.setSoTimeout(10000);
                    os = socket.getOutputStream();
                    is = socket.getInputStream();
                    printWriter = new PrintWriter(os);
                    printWriter.write(content);
                    printWriter.flush();
                    is.read(buffer);
                    String string = new String(buffer);
                    string = string.substring(0, 2);
                    Log.d(TAG, "string = " + string);
                    if (!TextUtils.isEmpty(string) && string.equalsIgnoreCase("ok")) {
                        Log.d(TAG, "data send ok, end");
                        isRunning = false;
                    } else {
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MLog.e(TAG, "SendDataToSerTask err");
                }
            }
            try {
                printWriter.close();
            } catch (Exception e) {
            }
            try {
                is.close();
            } catch (Exception e) {
            }
            try {
                os.close();
            } catch (Exception e) {
            }
            try {
                socket.shutdownInput();
            } catch (Exception e) {
            }
            try {
                socket.shutdownOutput();
            } catch (Exception e) {
            }
            try {
                socket.close();
            } catch (Exception e) {
            }
        }
    }

    private SendLiveForceTVHttpReqTask sendLiveForceTVHttpReqTaskObj;
    private Future<?> sendLiveForceTVHttpReqTask;

    public void stopSendLiveForceTVHttpReq() {
        if (sendLiveForceTVHttpReqTaskObj != null) {
            sendLiveForceTVHttpReqTaskObj.release();
            sendLiveForceTVHttpReqTaskObj = null;
        }
        if (sendLiveForceTVHttpReqTask != null) {
            sendLiveForceTVHttpReqTask.cancel(true);
            sendLiveForceTVHttpReqTask = null;
        }
    }

    public void sendLiveForceTVHttpReq(String filmId, String vodId, String kindName) {
        if (!forceTvOK) {
            throw new IllegalArgumentException("Call initForceTV err");
        }
        stopSendLiveForceTVHttpReq();
        sendLiveForceTVHttpReqTaskObj = new SendLiveForceTVHttpReqTask(this, filmId, vodId, kindName);
        sendLiveForceTVHttpReqTask = taskPool.submit(sendLiveForceTVHttpReqTaskObj);
    }

    private static class SendLiveForceTVHttpReqTask implements Runnable {

        private DataCenter hostCls;
        private final String filmId;
        private final String vodId;
        private final String colId;

        public SendLiveForceTVHttpReqTask(DataCenter hostCls, String filmId, String vodId, String colId) {
            this.hostCls = hostCls;
            this.filmId = filmId;
            this.vodId = vodId;
            this.colId = colId;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void run() {

            try {
                CmsObj cmsObj = hostCls.getCms(DOAMIN_URLS[0], CONF_NAMES[0]);
                String ipStr = null, portStr = null;
                int ipNum = cmsObj.getIps().size();
                if (ipNum > 0) {
                    IPObj ipObj = cmsObj.getIps().get(0);
                    ipStr = ipObj.getIp();
                    portStr = ipObj.getPort();
                } else {
                    MLog.e(TAG, "no ip info");
                }

                String reqValStr = hostCls.sendForceTVHttpReq(
                        cmsObj, ipStr, portStr, filmId, vodId, colId);

                if (!TextUtils.isEmpty(reqValStr)) {
                    if (hostCls != null && hostCls.onPlayListeners != null) {
                        for (int i = 0; i < hostCls.playListenerNum; i++) {
                            OnPlayListener onPlayListener = hostCls.onPlayListeners.get(i);
                            if (onPlayListener != null) {
                                onPlayListener.onPlay(reqValStr);
                            }
                        }
                    }
                } else {
                    MLog.e(TAG, "connect forectv err");
                }
                Thread.sleep(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void cacheData(LiveAct liveAct, LiveChalObj chal) {
        taskPool.execute(new CacheTask(this, liveAct, chal));
    }

    private static class CacheTask implements Runnable {

        private DataCenter hostCls;
        private LiveChalObj chal;
        private LiveAct liveAct;

        public CacheTask(DataCenter hostCls, LiveAct liveAct, LiveChalObj chal) {
            this.hostCls = hostCls;
            this.chal = chal;
            this.liveAct = liveAct;
        }

        @Override
        public void run() {

            CacheKindObj cacheKind = new CacheKindObj();
            CacheChalObj cacheChal = new CacheChalObj();

            cacheChal.setColId(chal.getColId());
            cacheChal.setColName(chal.getColName());
            cacheChal.setName(chal.getName());

            cacheKind.setColName(liveAct.cacheLiveKind.getColName());

            DBMgr.Ins().clearCacheChalDatas();
            DBMgr.Ins().clearCacheKindDatas();

            DBMgr.Ins().insertCacheChal(cacheChal);
            DBMgr.Ins().insertCacheKind(cacheKind);

            hostCls = null;
            liveAct = null;
        }
    }

    private Future<?> getSerPlayBackTask;
    private GetPlayBackEpgTask getPlayBackEpgTaskObj;

    public void getSerPlayBackEpg(PlayBackAct act) {
        stopGetPlayBack();
        getPlayBackEpgTaskObj = new GetPlayBackEpgTask(this, act);
        getSerPlayBackTask = taskPool.submit(getPlayBackEpgTaskObj);
    }

    public void stopGetPlayBack() {
        if (getPlayBackEpgTaskObj != null) {
            getPlayBackEpgTaskObj.release();
            getPlayBackEpgTaskObj = null;
        }
        if (getSerPlayBackTask != null) {
            getSerPlayBackTask.cancel(true);
            getSerPlayBackTask = null;
        }
    }

    public static class GetPlayBackEpgTask implements Runnable {//獲取回放的數據

        private DataCenter hostCls;
        private PlayBackAct act;
        private boolean isRunning;

        public GetPlayBackEpgTask(DataCenter hostCls, PlayBackAct act) {
            this.hostCls = hostCls;
            this.act = act;
        }

        public void release() {
            this.act = null;
            this.isRunning = false;
            if (this.hostCls != null) {
                this.hostCls.playBackDatIng = false;
            }
            this.hostCls = null;
        }

        @Override
        public void run() {

            DBMgr.Ins().clearPlayBack();
            isRunning = true;

            try {
                CmsObj cmsObj = hostCls.getCms(DOAMIN_URLS[1], CONF_NAMES[1]);
                EpgParser epgParser = new EpgParser();
                String epgUrl = String.format(EPGS_URL, cmsObj.getDominUrl());
                String epgStr = null;
                int retryCount = 0;
                do {
                    epgStr = NetUtils.Ins(AppMain.ctx()).getUrlStr(
                            epgUrl, cmsObj.getToken(), null, null);//頻道列表數據
                } while (TextUtils.isEmpty(epgStr) && (retryCount++ < RETRY_COUNT) && isRunning);
                MLog.d(TAG, "epgUrl = " + epgUrl);
                MLog.d(TAG, "epgStr = " + epgStr);

                Utils.saveStrDataToSD(AppMain.ctx(), "playback_epg.txt", epgStr);


                epgParser.parse(epgStr);

                //EpgParser2 epgParser2 = new EpgParser2();
                //epgParser2.parse(epgStr);
                //epgParser.parse(epgStr);

//                InputStream inputStream =
//                        AppMain.ctx().getAssets().open("json/Colors HD-1.txt");
//
//                byte[] bytes = new byte[1024];
//
//                StringBuilder stringBuilder = new StringBuilder();
//
//                int rLen = 0;
//
//                while ((rLen = inputStream.read(bytes)) >= 0) {
//                    stringBuilder.append(new String(bytes, 0, rLen));
//                }
//
//                inputStream.close();
//
//                String itemStr = stringBuilder.toString();
//
//                MLog.d(TAG, "itemStr=" + itemStr);

                int yyEpgNum = epgParser.getYyEpgNum();
                List<EpgObj> yyEpgObjs = epgParser.getYyEpgs();//粵語回放頻道

                for (int i = 0; i < yyEpgNum && isRunning; i++) {//星期数据获取
                    EpgObj epgObj = yyEpgObjs.get(i);
                    String itemUrl = String.format(EPGS_ITEM_URL, cmsObj.getDominUrl(), epgObj.getEpgId());
                    String itemStr = null;
                    retryCount = 0;
                    do {
                        itemStr = NetUtils.Ins(AppMain.ctx()).getUrlStr(
                                itemUrl, cmsObj.getToken(), null, null);
                    } while (TextUtils.isEmpty(itemStr) && (retryCount++ < RETRY_COUNT));
                    MLog.d(TAG, "itemUrl = " + itemUrl);
                    MLog.d(TAG, "itemStr = " + itemStr);

                    Utils.saveStrDataToSD(AppMain.ctx(), String.format("playback_chal_%s.txt",
                            epgObj.getName()), itemStr);

                    ChalParser chalParser = new ChalParser(hostCls, epgObj);

                    ChalDatsObj chalDatsObj = new Gson().fromJson(itemStr, ChalDatsObj.class);
                    //chalParser.parse(itemStr);
                    //chalParser.release();
                }

                int zhEpgNum = epgParser.getZhEpgNum();
                List<EpgObj> zhEpgObjs = epgParser.getZhEpgs();//

                for (int i = 0; i < zhEpgNum && isRunning; i++) {//节目信息获取
                    EpgObj epgObj = zhEpgObjs.get(i);
                    String itemUrl = String.format(EPGS_ITEM_URL, cmsObj.getDominUrl(), epgObj.getEpgId());
                    String itemStr = null;
                    retryCount = 0;
                    do {
                        itemStr = NetUtils.Ins(AppMain.ctx()).getUrlStr(
                                itemUrl, cmsObj.getToken(), null, null);
                    } while (TextUtils.isEmpty(itemStr) && (retryCount++ < RETRY_COUNT));
                    MLog.d(TAG, "itemUrl = " + itemUrl);
                    MLog.d(TAG, "itemStr = " + itemStr);

//                    Utils.saveStrDataToSD(AppMain.ctx(), String.format("playback_chal_%s.txt",
//                            epgObj.getName()), itemStr);

                    ChalParser chalParser = new ChalParser(hostCls, epgObj);
                    chalParser.parse(itemStr);
                    chalParser.release();
                }


                int epgChalNum = DBMgr.Ins().getPlayBackEpgChalNum();
                DBMgr.Ins().clearPlayBackCache();

                MLog.d(TAG, String.format("yyEpgNum=%d zhEpgNum=%d epgChalNum=%d", yyEpgNum,
                        zhEpgNum, epgChalNum));

                hostCls.playBackDataOK = yyEpgNum > 0 && zhEpgNum > 0 && epgChalNum > 0;

                epgParser.release();

                if (hostCls.playBackDataOK) {

                    if (act != null) {
                        act.loadEpgs(yyEpgObjs,  zhEpgObjs);//回调activity显示ui
                    }
                }
                Thread.sleep(0);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                this.release();
            }
        }
    }

    PlayBackEpgsTask playBackEpgsTask;
    Future<?> playBackEpgsFuture;

    public void getPlayBackEpgs(PlayBackMainAct act){
        stopPlayBackEpgs();
        playBackEpgsTask = new PlayBackEpgsTask(this, act);
        playBackEpgsFuture = taskPool.submit(playBackEpgsTask);
    }
    public void stopPlayBackEpgs(){
        if (playBackEpgsTask != null) {
            playBackEpgsTask.release();
            playBackEpgsTask = null;
        }
        if (playBackEpgsFuture != null){
            playBackEpgsFuture.cancel(true);
            playBackEpgsFuture = null;
        }
    }

    public static class PlayBackEpgsTask implements Runnable {

        private DataCenter mCenter;
        private PlayBackMainAct mAct;
        private boolean isRunning;

        public PlayBackEpgsTask(DataCenter center, PlayBackMainAct act) {
            mCenter = center;
            mAct = act;
        }

        public void release(){
            isRunning = false;
            if (mCenter != null){
                mCenter.playbackEpgIng = false;
            }
            mCenter = null;
            mAct = null;
        }

        @Override
        public void run() {
            try {

                CmsObj cmsObj = mCenter.getCms(DOAMIN_URLS[1], CONF_NAMES[1]);
                isRunning = true;
                //EpgParser epgParser = new EpgParser();
                String epgUrl = String.format(EPGS_URL, cmsObj.getDominUrl());
                String epgStr = null;
                int retryCount = 0;
                do {
                    epgStr = NetUtils.Ins(AppMain.ctx()).getUrlStr(
                            epgUrl, cmsObj.getToken(), null, null);//頻道列表數據
                } while (TextUtils.isEmpty(epgStr) && (retryCount++ < RETRY_COUNT) && isRunning);
                MLog.d(TAG, "epgUrl = " + epgUrl);
                MLog.d(TAG, "epgStr = " + epgStr);

                Utils.saveStrDataToSD(AppMain.ctx(), "playback_epg.txt", epgStr);

                EpgParser2 epgParser2 = new EpgParser2();
                epgParser2.parse(epgStr);
                mAct.loadEpgs(epgParser2.getAllEpgs(), epgParser2.getAllEpgs().size());

            }catch (Exception e){
            }
        }
    }

    PlayBackDatesTesk playBackDatesTesk;
    Future<?> playBackDatesFuture;
    public void getPlayBackDates(PlayBackMainAct act, EpgObj epgObj){//获取回放日期的信息
        stopPlayBackDates();
        playBackDatesTesk = new PlayBackDatesTesk(this, act, epgObj);
        playBackDatesFuture = taskPool.submit(playBackDatesTesk);
    }

    private void stopPlayBackDates() {

        if (playBackDatesTesk != null) {
            playBackDatesTesk.release();
            playBackDatesTesk = null;
        }
        if (playBackDatesFuture != null){
            playBackDatesFuture.cancel(true);
            playBackDatesFuture = null;
        }
    }

    public static class PlayBackDatesTesk implements Runnable {

        private PlayBackMainAct mContext;
        private EpgObj mEpgObj;
        private DataCenter hostCls;
        private boolean isRunning;


        public PlayBackDatesTesk(DataCenter center, PlayBackMainAct act, EpgObj epgObj) {
            hostCls = center;
            mContext = act;
            mEpgObj = epgObj;
        }

        public void release(){
            isRunning = false;
            if (hostCls!=null)
                hostCls.playbackDatesIng = false;
            hostCls = null;
            mEpgObj = null;
            mContext = null;
        }

        @Override
        public void run() {

            isRunning = true;
            CmsObj cmsObj = hostCls.getCms(DOAMIN_URLS[1], CONF_NAMES[1]);
            String epgUrl = String.format(EPGS_URL, cmsObj.getDominUrl());
            String itemUrl = String.format(EPGS_ITEM_URL, cmsObj.getDominUrl(), mEpgObj.getEpgId());
            String itemStr = null;
            int retryCount = 0;
            do {
                itemStr = NetUtils.Ins(AppMain.ctx()).getUrlStr(
                        itemUrl, cmsObj.getToken(), null, null);
            } while (TextUtils.isEmpty(itemStr) && (retryCount++ < RETRY_COUNT) && isRunning);
            MLog.d(TAG, "itemUrl = " + itemUrl);
            MLog.d(TAG, "itemStr = " + itemStr);

            Utils.saveStrDataToSD(AppMain.ctx(), String.format("playback_chal_%s.txt",
                    mEpgObj.getName()), itemStr);

            ChalDatsObj chalDatsObj = new Gson().fromJson(itemStr, ChalDatsObj.class);
            if (mContext!=null&&chalDatsObj != null){
                mContext.loadChalDatas(chalDatsObj, mEpgObj.getId());
            }
        }

    }


    private SendPlayBackForceTVHttpReqTask sendPlayBackForceTVHttpReqTaskObj;
    private Future<?> sendPlayBackForceTVHttpReqTask;

    public void stopPlayBackForceTVHttpReq() {
        if (sendPlayBackForceTVHttpReqTask != null) {
            sendPlayBackForceTVHttpReqTask.cancel(true);
            sendPlayBackForceTVHttpReqTask = null;
        }
        if (sendPlayBackForceTVHttpReqTaskObj != null) {
            sendPlayBackForceTVHttpReqTaskObj.release();
            sendPlayBackForceTVHttpReqTaskObj = null;
        }
    }

    public void sendPlayBackForceTVHttpReq(ChalObj chalObj) {
        if (!forceTvOK) {
            throw new IllegalArgumentException("Call initForceTV err");
        }
        stopPlayBackForceTVHttpReq();
        sendPlayBackForceTVHttpReqTaskObj = new SendPlayBackForceTVHttpReqTask(this, chalObj);
        sendPlayBackForceTVHttpReqTask = taskPool.submit(sendPlayBackForceTVHttpReqTaskObj);
    }

    private static class SendPlayBackForceTVHttpReqTask implements Runnable {

        private DataCenter hostCls;
        private ChalObj chalObj;

        public SendPlayBackForceTVHttpReqTask(DataCenter hostCls, ChalObj chalObj) {
            this.hostCls = hostCls;
            this.chalObj = chalObj;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void run() {

            try {
                CmsObj cmsObj = hostCls.getCms(DOAMIN_URLS[1], CONF_NAMES[1]);
                String ipStr = null, portStr = null;
                int ipNum = cmsObj.getIps().size();
                if (ipNum > 0) {
                    IPObj ipObj = cmsObj.getIps().get(0);
                    ipStr = ipObj.getIp();
                    portStr = ipObj.getPort();
                } else {
                    MLog.e(TAG, "no ip info");
                }

                String reqValStr = hostCls.sendForceTVHttpReq(cmsObj, ipStr, portStr,
                        chalObj.getFilmId(), String.valueOf(this.chalObj.getEpgId()), "");

                if (!TextUtils.isEmpty(reqValStr)) {
                    for (int i = 0; hostCls != null && i < hostCls.playListenerNum; i++) {
                        OnPlayListener listener = hostCls == null ?
                                null : hostCls.onPlayListeners.get(i);
                        if (listener != null) {
                            listener.onPlay(reqValStr);
                        }
                    }
                } else {
                    MLog.e(TAG, "connect forectv err");
                }
                Thread.sleep(0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            hostCls = null;
        }
    }

    private LoginPlaybackTask loginPlaybackTaskObj;
    private Future<?> loginPlaybackTask;

    public void stopLoginPlayBack() {
        if (loginPlaybackTaskObj != null) {
            loginPlaybackTaskObj.release();
            loginPlaybackTaskObj = null;
        }
        if (loginPlaybackTask != null) {
            loginPlaybackTask.cancel(true);
            loginPlaybackTask = null;
        }
    }

    public void loginPlayBack() {
        stopLoginPlayBack();
        loginPlaybackTaskObj = new LoginPlaybackTask(this);
        loginPlaybackTask = taskPool.submit(loginPlaybackTaskObj);
    }

    private static class LoginPlaybackTask implements Runnable {

        private DataCenter dataCenter;

        public LoginPlaybackTask(DataCenter dataCenter) {
            this.dataCenter = dataCenter;
        }

        public void release() {
            this.dataCenter = null;
        }

        @Override
        public void run() {

            try {
                if (dataCenter == null) {
                    return;
                }

                String dominUrl = DOAMIN_URLS[1];
                String confName = CONF_NAMES[1];
                CmsObj cmsObj = dataCenter.getCms(dominUrl, confName);
                String tipsStr = null;
                boolean dialogCancelable = false;

                if (cmsObj.isLoginOK() && !cmsObj.isExpired()) {
                    for (int i = 0; i < dataCenter.listenerNum; i++) {
                        OnDataListener onDataListener = dataCenter.onDataListeners.get(i);
                        if (onDataListener != null) {
                            onDataListener.onLogin(tipsStr, dialogCancelable);
                        }
                    }
                    dataCenter.logining = false;
                    dataCenter = null;
                    return;
                }

                if (Utils.isNetOK(AppMain.ctx())) {

//                    String hostStr = dataCenter.checkHost(AppMain.ctx(),
//                            String.format(dominUrl, HOST));
//                    if (!TextUtils.isEmpty(hostStr)) {
//                        cmsObj.setDominUrl(hostStr);
//                    }
                    // 回看后台IP地址
                    cmsObj.setDominUrl("http://23.237.32.106");

                    tipsStr = dataCenter.loginForceTvAuto(cmsObj, BuildConfig.ID_HEAD);

                    if (cmsObj.isLoginOK()) {
                        String serListStr = dataCenter.getSerList(cmsObj);
                        SerListParser serListParser = new SerListParser();
                        serListParser.parse(serListStr);
                        cmsObj.setIps(serListParser.getIps());
                    }
                } else {
                    cmsObj.setLoginOK(false);
                    tipsStr = AppMain.res().getString(R.string.no_net);
                }

                if (dataCenter != null) {
                    for (int i = 0; i < dataCenter.listenerNum; i++) {
                        OnDataListener onDataListener = dataCenter.onDataListeners.get(i);
                        if (onDataListener != null) {
                            onDataListener.onLogin(tipsStr, true);
                        }
                    }
                }
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dataCenter = null;
            }
        }
    }

    private InitSearchMoviesTask initSearchMoviesTaskObj;
    private Future<?> initSearchMoviesTask;

    public void stopInitSearchMoviesTask() {
        if (initSearchMoviesTaskObj != null) {
            initSearchMoviesTaskObj.release();
            initSearchMoviesTaskObj = null;
        }
        if (initSearchMoviesTask != null) {
            initSearchMoviesTask.cancel(true);
            initSearchMoviesTask = null;
        }
    }

    public void initSearchMovies(String key, boolean isCkind, boolean backupIsCkind, String cid) {
        stopInitSearchMoviesTask();
        initSearchMoviesTaskObj = new InitSearchMoviesTask(this, key, isCkind, backupIsCkind, cid);
        initSearchMoviesTask = taskPool.submit(initSearchMoviesTaskObj);
    }

    private class InitSearchMoviesTask implements Runnable {

        private DataCenter hostCls;
        private String key, cid;
        private boolean isCkind, backupIsCkind;

        public InitSearchMoviesTask(DataCenter dataCenter, String key, boolean isCkind,
                                    boolean backupIsCkind, String cid) {
            this.hostCls = dataCenter;
            this.key = key;
            this.cid = cid;
            this.isCkind = isCkind;
            this.backupIsCkind = backupIsCkind;
        }

        public void release() {
            hostCls = null;
        }

        @Override
        public void run() {

            MLog.d(TAG, "InitSearchMoviesTask");

            while (!DBMgr.Ins().initDBDataOk() && (hostCls != null)) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            List<Object> searchResults = new ArrayList<>();
            int resultNum = 0, searchTotalNum = 0;

            if (TextUtils.isEmpty(key)) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
                String year = sdf.format(new Date());
                MLog.d(TAG, "InitSearchMoviesTask year " + year);

                if (isCkind) {
                    searchTotalNum = DBMgr.Ins().queryCkindNumForPCID(cid);
                } else {
                    searchTotalNum = DBMgr.Ins().queryMovieNumForYear(year);
                }

                MLog.d(TAG, "InitSearchMoviesTask searchTotalNum " + searchTotalNum);

                SparseIntArray mediaPosInDBArr = new SparseIntArray();
                resultNum = Math.min(searchTotalNum, 8);

                if (searchTotalNum > 8) {
                    for (int i = 0; i < resultNum; ) {
                        Random random = new Random();
                        int movieIndex = random.nextInt(searchTotalNum);
                        if (mediaPosInDBArr.indexOfValue(movieIndex) < 0) {
                            mediaPosInDBArr.put(i++, movieIndex);
                        }
                    }
                } else if (searchTotalNum > 0) {
                    for (int i = 0; i < resultNum; i++) {
                        mediaPosInDBArr.put(i, i);
                    }
                }

                for (int i = 0; i < resultNum; i++) {
                    Object object = null;
                    int mediaIndex = mediaPosInDBArr.get(i);
                    if (isCkind) {
                        object = DBMgr.Ins().queryCKindForPCIDAndOffset(cid, mediaIndex);
                    } else {
                        object = DBMgr.Ins().queryMovieForYearAndOffset(Integer.valueOf(year),
                                mediaIndex);
                    }
                    if (object != null) {
//                        MLog.d(TAG, "object = " + object);
                        searchResults.add(object);
                    }
                }

                searchTotalNum = resultNum;
            } else {
                List<CKindObj> ckinds = null;
                List<MovieObj> movies = null;
                char[] keyArr = key.toCharArray();
                int keyNum = keyArr.length;
                StringBuilder keyBuilder = new StringBuilder();
                keyBuilder.append("%");
                for (int i = 0; i < keyNum; i++) {
                    keyBuilder.append(keyArr[i]).append("%");
                }
                String key = keyBuilder.toString();
                if (isCkind) {
                    searchTotalNum = DBMgr.Ins().queryCkindNumForKey(cid, key);
                    if (searchTotalNum > 0) {
                        ckinds = DBMgr.Ins().queryCkindsForKey(cid, key, 0);
                        resultNum = ckinds.size();
                    }
                } else {
                    if (backupIsCkind) {
                        searchTotalNum = DBMgr.Ins().queryColMovieNum(cid);
                        if (searchTotalNum > 0) {
                            movies = DBMgr.Ins().queryMovies(cid, 0, EXVAL.SEARCH_NUM_IN_PAGE);
                            resultNum = movies.size();
                        }
                    } else {
                        searchTotalNum = DBMgr.Ins().queryMovieForKeyNum(key);
                        if (searchTotalNum > 0) {
                            movies = DBMgr.Ins().queryMovieForKey(key, 0);
                            resultNum = movies.size();
                        }
                    }
                }

                for (int i = 0; i < resultNum; i++) {
                    searchResults.add(isCkind ? ckinds.get(i) : movies.get(i));
                }
            }

            MLog.d(TAG, "InitSearchMoviesTask searchMovieNum " + resultNum);
            MLog.d(TAG, "InitSearchMoviesTask searchTotalNum " + searchTotalNum);

            if (hostCls != null && hostCls.onSearchListeners != null) {
                for (int i = 0; i < hostCls.searchListenerNum; i++) {
                    OnSearchListener listener = hostCls.onSearchListeners.get(i);
                    if (listener != null) {
                        listener.onInitSearchResult(searchResults, searchTotalNum);
                    }
                }
            }

            hostCls = null;
        }
    }

    public void searchMoviesForPage(String key, String cid, int pageIndex, boolean isCkind, boolean backupIsCkind) {
        taskPool.execute(new SearchMoviesForPageTask(this, key, cid, pageIndex, isCkind, backupIsCkind));
    }

    private class SearchMoviesForPageTask implements Runnable {

        private boolean isCkind, backupIsCkind;
        private DataCenter hostCls;
        private String key, cid;
        private int pageIndex;

        public SearchMoviesForPageTask(DataCenter dataCenter, String key, String cid, int pageIndex,
                                       boolean isCkind, boolean backupIsCkind) {
            this.hostCls = dataCenter;
            this.key = key;
            this.pageIndex = pageIndex;
            this.isCkind = isCkind;
            this.backupIsCkind = backupIsCkind;
            this.cid = cid;
        }

        @Override
        public void run() {

            if (TextUtils.isEmpty(key)) {
                return;
            }

            char[] keyArr = key.toCharArray();
            int keyNum = keyArr.length;
            StringBuilder keyBuilder = new StringBuilder();
            keyBuilder.append("%");
            for (int i = 0; i < keyNum; i++) {
                keyBuilder.append(keyArr[i]).append("%");
            }

            int resultNum = 0;
            String key = keyBuilder.toString();
            List<Object> searchResults = new ArrayList<>();
            List<CKindObj> ckinds = null;
            List<MovieObj> movies = null;

            if (isCkind) {
                ckinds = DBMgr.Ins().queryCkindsForKey(cid, key, pageIndex);
                resultNum = ckinds.size();
            } else {
                if (backupIsCkind) {
                    movies = DBMgr.Ins().queryMovies(cid, pageIndex, EXVAL.SEARCH_NUM_IN_PAGE);
                    resultNum = movies.size();
                } else {
                    movies = DBMgr.Ins().queryMovieForKey(key, pageIndex);
                    resultNum = movies.size();
                }
            }

            for (int i = 0; i < resultNum; i++) {
                searchResults.add(isCkind ? ckinds.get(i) : movies.get(i));
            }

            MLog.d(TAG, "SearchMoviesTask resultNum " + resultNum);

            if (hostCls != null && hostCls.onSearchListeners != null) {
                for (int i = 0; i < hostCls.searchListenerNum; i++) {
                    OnSearchListener listener = hostCls.onSearchListeners.get(i);
                    if (listener != null) {
                        listener.onSearchResultForPage(searchResults);
                    }
                }
            }

            hostCls = null;
        }
    }

    private ScanAllCkindsTask scanAllCkindsTaskObj;
    private Future<?> scanAllCkindsTask;

    public void stopScanAllCkinds() {
        if (this.scanAllCkindsTaskObj != null) {
            this.scanAllCkindsTaskObj.release();
            this.scanAllCkindsTaskObj = null;
        }
        if (this.scanAllCkindsTask != null) {
            this.scanAllCkindsTask.cancel(true);
            this.scanAllCkindsTask = null;
        }
    }

    public void scanAllCkinds(Activity activity, String cid) {
        this.stopScanAllCkinds();
        this.scanAllCkindsTaskObj = new ScanAllCkindsTask(activity, cid);
        this.scanAllCkindsTask = this.taskPool.submit(this.scanAllCkindsTaskObj);
    }

    private static class ScanAllCkindsTask implements Runnable {

        private Activity activity;
        private String cid;

        public ScanAllCkindsTask(Activity activity, String cid) {
            this.activity = activity;
            this.cid = cid;
        }

        public void release() {
            this.activity = null;
            this.cid = null;
        }

        @Override
        public void run() {

            List<CKindObj> cKindObjs = DBMgr.Ins().queryCKinds(cid);

            int ckindNum = cKindObjs.size();

            MLog.d(TAG, String.format("ScanAllCkindsTask cid=%s ckindNum=%d", cid, ckindNum));

            if (activity != null) {
                if (activity instanceof HotReDetailsAct) {
                    ((HotReDetailsAct) activity).loadMovieDatas(cKindObjs, ckindNum);
                }
            }
        }
    }
}