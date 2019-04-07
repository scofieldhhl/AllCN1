package com.allcn.activities;

import android.content.Intent;
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
import com.mast.lib.parsers.MarqueeParser;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.io.File;

public class YYSJAct extends BaseActivity {

    private static final String TAG = YYSJAct.class.getSimpleName();
    private TextView[] textViews;
    private MOnClickListener mOnClickListener;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private boolean isYYSJ;
    private int viewNum;
    private ImageView netV;
    private TextView timeV;
    private MOnDataListener mOnDataListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        this.isYYSJ = getIntent().getBooleanExtra(EXVAL.IS_YYSJ, false);

        super.onCreate(savedInstanceState);

        if (this.isYYSJ) {
            this.netV = findViewById(R.id.yy_sj_net_v);
            this.timeV = findViewById(R.id.yy_sj_time_v);
            ((TextView) findViewById(R.id.yy_sj_title_v)).setText(R.string.yy_world);
            this.viewNum = 6;
            this.textViews = new TextView[this.viewNum];
            this.textViews[0] = findViewById(R.id.yy_sj_dy_bl_v);
            this.textViews[1] = findViewById(R.id.yy_sj_rb_jj_v);
            this.textViews[2] = findViewById(R.id.yy_sj_zy_bs_v);
            this.textViews[3] = findViewById(R.id.yy_sj_zx_dy_v);
            this.textViews[4] = findViewById(R.id.yy_sj_zx_zj_v);
            this.textViews[5] = findViewById(R.id.yy_sj_jl_sc_v);
        } else {
            this.netV = findViewById(R.id.yy_xf_net_v);
            this.timeV = findViewById(R.id.yy_xf_time_v);
            ((TextView) findViewById(R.id.yy_xf_title_v)).setText(R.string.ys_first);
            this.viewNum = 8;
            this.textViews = new TextView[this.viewNum];
            this.textViews[0] = findViewById(R.id.yy_xf_jl_sc_v);
            this.textViews[1] = findViewById(R.id.yy_xf_zx_dy_v);
            this.textViews[2] = findViewById(R.id.yy_xf_zx_zj_v);
            this.textViews[3] = findViewById(R.id.yy_xf_dy_bl_v);
            this.textViews[4] = findViewById(R.id.yy_xf_rb_jj_v);
            this.textViews[5] = findViewById(R.id.yy_xf_ty_jj_v);
            this.textViews[6] = findViewById(R.id.yy_xf_ml_jl_v);
            this.textViews[7] = findViewById(R.id.yy_xf_zy_bs_v);
        }

        for (int i = 0; i < this.viewNum; i++) {
            this.textViews[i].setTag(i);
        }

        for (View parent = (View) this.netV.getParent(); parent != null;
             parent = (View) parent.getParent()) {
            parent.setBackgroundResource(0);
        }

        findViewById(android.R.id.content).setBackgroundResource(R.drawable.yy_xf_bg);

        this.mOnClickListener = new MOnClickListener(this);
        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);

        for (int i = 0; i < this.viewNum; i++) {
            TextView textView = this.textViews[i];
            textView.setTag(i);
            textView.setOnClickListener(this.mOnClickListener);
            textView.setOnFocusChangeListener(this.mOnFocusChangeListener);
        }

        this.textViews[0].post(new Runnable() {
            @Override
            public void run() {
                Utils.focusV(YYSJAct.this.textViews[0], true);
            }
        });

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
        if (this.mOnClickListener != null) {
            this.mOnClickListener.release();
            this.mOnClickListener = null;
        }
        if (this.mOnFocusChangeListener != null) {
            this.mOnFocusChangeListener.release();
            this.mOnFocusChangeListener = null;
        }
        this.textViews = null;
    }

    @Override
    FocusBorder createFocusBorder() {
        return new FocusBorder.Builder()
                .asDrawable()
                .borderResId(R.drawable.home_sel_bg)
                .build(this);
    }

    @Override
    int layoutId() {
        return this.isYYSJ ? R.layout.yy_sj_act : R.layout.yy_xf_act;
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private YYSJAct hostCls;

        public MOnFocusChangeListener(YYSJAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            MLog.d(TAG, String.format("onFocusChange v=%s hasFocus=%b", v, hasFocus));
            if (hasFocus) {
                v.bringToFront();
                this.hostCls.focusBorderForV(v, 1.15f, 1.15f);
            }

            v.setSelected(hasFocus);
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private YYSJAct hostCls;

        public MOnClickListener(YYSJAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();
            if (hostCls != null) {
                Intent intent = new Intent(hostCls, KindMovieAct.class);
                intent.putExtra(EXVAL.HOME_INDEX, this.hostCls.isYYSJ ? 0 : 1);
                intent.putExtra(EXVAL.KIND_INDEX, (Integer) v.getTag());
                hostCls.startActivity(intent);
            }
        }
    }

    private static class MOnDataListener implements OnDataListener {

        private YYSJAct hostCls;

        public MOnDataListener(YYSJAct hostCls) {
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
