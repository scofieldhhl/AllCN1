package com.allcn.interfaces;

import android.util.SparseArray;

import com.datas.HomeMovie;
import com.datas.LiveChalObj;
import com.mast.lib.interfaces.OnActListener;
import com.mast.lib.parsers.MarqueeParser;

import java.io.File;

public interface OnDataListener extends OnActListener {
    void onLoadInitDatas(SparseArray<HomeMovie> homeMovies);
    void onNetState(int netType, boolean isConnected);
    void onTimeDate(String time);
    void onUpdateApp(File apkF, String desStr);
    void onMarquee(MarqueeParser marqueeParser);
    void onForceTv();
    void onGetDataOver();
    void onInitMediaOver();
    void onFavUpdate();
    void onChalList(LiveChalObj chal, boolean liveDataDBOK);
}
