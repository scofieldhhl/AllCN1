package com.allcn.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.interfaces.OnDataListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.views.focus.FocusBorder;
import com.datas.HomeMovie;
import com.datas.LiveChalObj;
import com.datas.MovieObj;
import com.db.cls.DBMgr;
import com.mast.lib.parsers.MarqueeParser;

import java.io.File;

public class SEDMMainAct extends BaseActivity {

    private static final String TAG = SEDMMainAct.class.getSimpleName();

    private TextView titleV;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private TextView[] mxzqVArr, upVArr;
    private MXZQOnClickListener mxzqOnClickListener;
    private String[] mxzqCidArr = new String[]{
            "502",
            "503",
            "504",
            "505",
            "506",
            "507",
            "508",
            "509",
    };
    private ImageView netV;
    private TextView timeV;
    private MOnDataListener mOnDataListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundResource(0);
        findViewById(android.R.id.content).setBackgroundResource(R.drawable.sedm_main_bg);
        this.titleV = findViewById(R.id.sedm_main_title_v);
        this.mxzqVArr = new TextView[8];
        this.upVArr = new TextView[5];
        this.mxzqVArr[0] = findViewById(R.id.sedm_main_mxzq_1_v);
        this.mxzqVArr[1] = findViewById(R.id.sedm_main_mxzq_2_v);
        this.mxzqVArr[2] = findViewById(R.id.sedm_main_mxzq_3_v);
        this.mxzqVArr[3] = findViewById(R.id.sedm_main_mxzq_4_v);
        this.mxzqVArr[4] = findViewById(R.id.sedm_main_mxzq_5_v);
        this.mxzqVArr[5] = findViewById(R.id.sedm_main_mxzq_6_v);
        this.mxzqVArr[6] = findViewById(R.id.sedm_main_mxzq_7_v);
        this.mxzqVArr[7] = findViewById(R.id.sedm_main_mxzq_8_v);
        this.upVArr[0] = findViewById(R.id.sedm_main_sedh_v);
        this.upVArr[1] = findViewById(R.id.sedm_main_egyy_v);
        this.upVArr[2] = findViewById(R.id.sedm_main_xxjy_v);
        this.upVArr[3] = findViewById(R.id.sedm_main_dmdy_v);
        this.upVArr[4] = findViewById(R.id.sedm_main_dmjj_v);
        this.netV = findViewById(R.id.sedm_home_net_v);
        this.timeV = findViewById(R.id.sedm_home_time_v);

//        this.titleV.setTypeface(AppMain.getJdjquFont());
        this.titleV.setText(AppMain.res().getString(R.string.sedh));

        String[] mxzqNameArr = AppMain.res().getStringArray(R.array.sedm_bottom_name_arr);

        int mxzqNameNum = mxzqNameArr.length;

        for (int i = 0; i < mxzqNameNum; i++) {
            this.mxzqVArr[i].setText(mxzqNameArr[i]);
        }

        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnClickListener = new MOnClickListener(this);
        this.mxzqOnClickListener = new MXZQOnClickListener(this);

        for (int i = 0; i < 8; i++) {
            this.mxzqVArr[i].setOnFocusChangeListener(this.mOnFocusChangeListener);
            this.mxzqVArr[i].setOnClickListener(this.mxzqOnClickListener);
            this.mxzqVArr[i].setTag(R.id.details_v_index, i);
            this.mxzqVArr[i].setTag(R.id.details_v_scale, 1.1f);
        }
        for (int i = 0; i < 5; i++) {
            this.upVArr[i].setOnFocusChangeListener(this.mOnFocusChangeListener);
            this.upVArr[i].setOnClickListener(this.mOnClickListener);
            if (i>2)
                upVArr[i].setTag(R.id.details_v_index, i+1);
            else upVArr[i].setTag(R.id.details_v_index, i);
            this.upVArr[i].setTag(R.id.details_v_scale, 1.15f);
        }

        this.upVArr[3].setTag(4);
        this.upVArr[4].setTag(5);

        this.mOnDataListener = new MOnDataListener(this);
    }

    @Override
    protected void eventView() {

    }

    @Override
    protected void findView() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        this.execStoped = false;
        DataCenter.Ins().setActivityType(EXVAL.TYPE_ACTIVITY_OTH);
        DataCenter.Ins().addDataListener(this.mOnDataListener);
        DataCenter.Ins().scanTime();
        DataCenter.Ins().regReceiver(AppMain.ctx());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void stopAct() {
        DataCenter.Ins().unregReceiver(AppMain.ctx());
        DataCenter.Ins().delDataListener(this.mOnDataListener);
    }

    @Override
    protected void destroyAct() {
        if (this.mOnDataListener != null) {
            this.mOnDataListener.release();
            this.mOnDataListener = null;
        }
        if (this.mOnFocusChangeListener != null) {
            this.mOnFocusChangeListener.release();
            this.mOnFocusChangeListener = null;
        }
        if (this.mOnClickListener != null) {
            this.mOnClickListener.release();
            this.mOnClickListener = null;
        }
        if (this.mxzqOnClickListener != null) {
            this.mxzqOnClickListener.release();
            this.mxzqOnClickListener = null;
        }
        this.upVArr = this.mxzqVArr = null;
    }

    @Override
    FocusBorder createFocusBorder() {
        return new FocusBorder.Builder()
                .asColor()
                .borderColor(Color.TRANSPARENT)
                .shadowWidth(10)
                .shadowColor(Color.TRANSPARENT)
                .build(this);
    }

    @Override
    int layoutId() {
        return R.layout.sedm_main_act;
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private SEDMMainAct hostCls;

        public MOnFocusChangeListener(SEDMMainAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (hostCls == null) {
                return;
            }

            if (hasFocus) {
                float scale = (float) v.getTag(R.id.details_v_scale);
                this.hostCls.focusBorderForV(v, scale, scale);
                this.hostCls.titleV.setText(((TextView) v).getText());
                v.bringToFront();
            }
            v.setSelected(hasFocus);
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private SEDMMainAct hostCls;

        public MOnClickListener(SEDMMainAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {

            if (hostCls == null) {
                return;
            }

            Intent intent = new Intent(this.hostCls, SEDMKindListAct.class);
            intent.putExtra(EXVAL.KIND_INDEX, (int) v.getTag(R.id.details_v_index));
            this.hostCls.startActivity(intent);
        }
    }

    private static class MXZQOnClickListener implements View.OnClickListener {

        private SEDMMainAct hostCls;

        public MXZQOnClickListener(SEDMMainAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {

            if (hostCls == null) {
                return;
            }

            MovieObj movieObj = DBMgr.Ins().queryMovieForPos(
                    this.hostCls.mxzqCidArr[(int) v.getTag(R.id.details_v_index)], 0);
            if (movieObj != null) {
                Intent intent = new Intent(this.hostCls, SEDMMovieDetailsAct.class);
                intent.putExtra(EXVAL.MOVIE_OBJ, movieObj);
                this.hostCls.startActivity(intent);
            } else {
                AppMain.showToastTips(AppMain.res().getString(R.string.movie_not_exists));
            }
        }
    }

    private static class MOnDataListener implements OnDataListener {

        private SEDMMainAct hostCls;

        public MOnDataListener(SEDMMainAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onLoadInitDatas(final SparseArray<HomeMovie> homeMovies) {

        }

        @Override
        public void onNetState(int netType, boolean isConnected) {
            if (this.hostCls == null) {
                return;
            }
            if (isConnected) {
                this.hostCls.netV.setVisibility(View.VISIBLE);
                switch (netType) {
                    case ConnectivityManager.TYPE_WIFI:
                        this.hostCls.netV.setBackgroundResource(R.drawable.wifi_ok);
                        break;
                    default:
                        this.hostCls.netV.setBackgroundResource(R.drawable.eth_f);
                        break;
                }
            } else {
                this.hostCls.netV.setVisibility(View.GONE);
            }
        }

        @Override
        public void onTimeDate(final String time) {
            if (this.hostCls == null) {
                return;
            }
            this.hostCls.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MOnDataListener.this.hostCls.timeV.setText(time);
                }
            });
        }

        @Override
        public void onUpdateApp(final File apkF, final String desStr) {
        }

        @Override
        public void onMarquee(final MarqueeParser marqueeParser) {

        }

        @Override
        public void onForceTv() {
        }

        @Override
        public void onGetDataOver() {

        }

        @Override
        public void onInitMediaOver() {
        }

        @Override
        public void onFavUpdate() {

        }

        @Override
        public void onChalList(LiveChalObj chal, boolean liveDataDBOK) {

        }

        @Override
        public void onLogin(String loginStr, boolean dialogCancelable) {

        }

        @Override
        public void onToken() {

        }

        @Override
        public void onPay(final boolean b, final String s) {

        }
    }
}
