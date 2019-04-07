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
import com.allcn.utils.GlideUtils;
import com.allcn.views.focus.FocusBorder;
import com.datas.HomeMovie;
import com.datas.LiveChalObj;
import com.mast.lib.parsers.MarqueeParser;
import com.mast.lib.utils.Utils;

import java.io.File;

public class SetAct extends BaseActivity {

    private static final String TAG = SetAct.class.getSimpleName();

    private TextView clearV, updateV, aboutV, timeV;
    private ImageView netV;
    private int exW, exH, freeH;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private MOnDataListener mOnDataListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.clearV = findViewById(R.id.set_cache_v);
        this.aboutV = findViewById(R.id.set_about_v);
        this.updateV = findViewById(R.id.set_update_v);
        this.timeV = findViewById(R.id.set_time_v);
        this.netV = findViewById(R.id.set_net_v);

        this.exW = (int) AppMain.res().getDimension(R.dimen.set_ex_width);
        this.exH = (int) AppMain.res().getDimension(R.dimen.set_ex_height);
        this.freeH = (int) AppMain.res().getDimension(R.dimen.set_free_height);

        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnClickListener = new MOnClickListener(this);

        this.clearV.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.updateV.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.aboutV.setOnFocusChangeListener(this.mOnFocusChangeListener);

        this.clearV.setOnClickListener(this.mOnClickListener = new MOnClickListener(this));
        this.updateV.setOnClickListener(this.mOnClickListener = new MOnClickListener(this));
        this.aboutV.setOnClickListener(this.mOnClickListener = new MOnClickListener(this));

        GlideUtils.Ins().loadSetImg(this, R.drawable.set_update_img, this.updateV);
        GlideUtils.Ins().loadSetImg(this, R.drawable.set_cache_img, this.clearV);
        GlideUtils.Ins().loadSetImg(this, R.drawable.set_about_img, this.aboutV);

        this.mOnDataListener = new MOnDataListener(this);

        this.updateV.post(new Runnable() {
            @Override
            public void run() {
                Utils.focusV(SetAct.this.updateV, true);
            }
        });
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
    }

    @Override
    protected FocusBorder createFocusBorder() {
        return new FocusBorder.Builder()
                .asDrawable()
                .borderResId(R.drawable.kind_item_f)
                .build(this);
    }

    @Override
    protected int layoutId() {
        return R.layout.set_act;
    }

    private static class MOnClickListener implements View.OnClickListener {

        private SetAct hostCls;

        public MOnClickListener(SetAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {
            if (this.hostCls != null) {
                switch (v.getId()) {
                    case R.id.set_update_v:
                        AppMain.showToastTips(AppMain.res().getString(R.string.latest_ver));
                        break;
                    case R.id.set_cache_v: {
                        Intent intent = new Intent();
                        intent.setClass(hostCls, ClearAct.class);
                        hostCls.startActivity(intent);
                        break;
                    }
                    case R.id.set_about_v: {
                        Intent intent = new Intent();
                        intent.setClass(hostCls, AboutAct.class);
                        hostCls.startActivity(intent);
                        break;
                    }
                }
            }
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private SetAct hostCls;

        public MOnFocusChangeListener(SetAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (this.hostCls == null) {
                return;
            }

            if (hasFocus) {
                this.hostCls.focusBorderForV(v, 1.1f, 1.1f,
                        this.hostCls.exW, this.hostCls.exH, 0, this.hostCls.freeH);
            }
        }
    }

    private static class MOnDataListener implements OnDataListener {

        private SetAct hostCls;

        public MOnDataListener(SetAct hostCls) {
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
