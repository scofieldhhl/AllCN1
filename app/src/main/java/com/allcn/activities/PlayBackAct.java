package com.allcn.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.adapters.PlayBackChalAdapter;
import com.allcn.adapters.PlayBackLeftAdapter;
import com.allcn.adapters.PlayBackWeekAdapter;
import com.allcn.interfaces.OnDataListener;
import com.allcn.interfaces.OnPlayListener;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.utils.MSG;
import com.allcn.views.FocusKeepRecyclerView;
import com.allcn.views.LoadWindow;
import com.allcn.views.PayDialog;
import com.allcn.views.UiSeeKBar;
import com.allcn.views.decorations.PlayBackChalDecorations;
import com.allcn.views.decorations.PlayBackWeekDecorations;
import com.allcn.views.focus.FocusBorder;
import com.datas.ChalObj;
import com.datas.EpgObj;
import com.datas.HomeMovie;
import com.datas.LiveChalObj;
import com.db.cls.DBMgr;
import com.mast.lib.parsers.MarqueeParser;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;
import com.mast.lib.views.VideoView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.Future;

public class PlayBackAct extends BaseActivity {

    private static final String TAG = PlayBackAct.class.getSimpleName();

    private TextView yyV, zhV, curPdV, curJmV, timeV, selTitleV, tipsV, titleV,
            epgV, curTimeV, speedV, totalTimeV;
    private VideoView videoView;
    private FocusKeepRecyclerView rightRv, leftRv, weekRv;
    private ImageView netV;
    private MOnDataListener mOnDataListener;
    private OnRvAdapterListenerForLeft onRvAdapterListenerForLeft;
    private PlayBackLeftAdapter leftAdapter;
    private PlayBackWeekAdapter weekAdapter;
    private PlayBackChalAdapter chalAdapter;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnKeyListener mOnKeyListener;
    private MOnClickListener mOnClickListener;
    private FocusBorder drawableFB, colorFB;
    private OnRvAdapterListenerForRight onRvAdapterListenerForRight;
    private OnRvAdapterListenerForWeek onRvAdapterListenerForWeek;
    private FrameLayout videoRootV, bottomCtrlRootV;
    private RelativeLayout playCtrlRootV;
    private UiSeeKBar seeKBar;

    private MOnPreparedListener mOnPreparedListener;
    private MOnInfoListener mOnInfoListener;
    private MOnErrorListener mOnErrorListener;
    private MOnCompletionListener mOnCompletionListener;
    private MHandler mHandler;
    private MOnPlayListener mOnPlayListener;

    private int curtime;
    private int seekTime, totaltime;
    private int SEEK_ONCE = 30 * 1000; // 30 sec
    private int curPlayStatus;
    private MOnSeekCompleteListener mOnSeekCompleteListener;
    private LoadWindow loadWindow;
    private ChalObj curChal;
    private ImageView operStatus;
    private EpgObj epgObj;
    private boolean isSeekIng, isFullScreen, isPlaying, loadDataOver;
    private RelativeLayout.LayoutParams videoRootVLayoutParams, videoRootVScreenLayoutParams;
    private PayDialog payDialog;
    private MVRootKeyListener mvRootKeyListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.isFullScreen = false;
        this.curPlayStatus = EXVAL.PLAY_NOREADY;

        this.loadWindow = new LoadWindow(this);
        this.loadWindow.setFullScreen(false);
        this.mHandler = new MHandler(this);

        this.videoRootVScreenLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        this.operStatus = findViewById(R.id.play_status_v);
        this.playCtrlRootV = findViewById(R.id.play_ctrl_root_v);
        this.titleV = findViewById(R.id.play_ctrl_title_v);
        this.epgV = findViewById(R.id.play_ctrl_epg_v);
        this.curTimeV = findViewById(R.id.play_ctrl_cur_time_v);
        this.speedV = findViewById(R.id.play_ctrl_speed_v);
        this.totalTimeV = findViewById(R.id.play_ctrl_total_time_v);
        this.seeKBar = findViewById(R.id.seekbar);
        this.tipsV = findViewById(R.id.play_tips_v);
        this.videoRootV = findViewById(R.id.playback_video_root_v);
        this.bottomCtrlRootV = findViewById(R.id.play_bottom_ctrl_root_v);
        this.yyV = findViewById(R.id.playback_yy_title_v);
        this.zhV = findViewById(R.id.playback_zh_title_v);
        this.curPdV = findViewById(R.id.playback_cur_pd_v);
        this.curJmV = findViewById(R.id.playback_cur_jm_v);
        this.timeV = findViewById(R.id.playback_time_v);
        this.videoView = findViewById(R.id.playback_video_v);
        this.leftRv = findViewById(R.id.playback_left_list_v);
        this.rightRv = findViewById(R.id.playback_chal_list_v);
        this.weekRv = findViewById(R.id.playback_week_list_v);
        this.netV = findViewById(R.id.playback_net_v);

        this.mOnDataListener = new MOnDataListener(this);
        this.mOnPlayListener = new MOnPlayListener(this);

        this.videoRootV.setOnKeyListener(this.mvRootKeyListener =
                new MVRootKeyListener(this));

        this.leftRv.setLayoutManager(new LinearLayoutManager(this));
        this.leftRv.setAdapter(this.leftAdapter = new PlayBackLeftAdapter(this));
        this.leftAdapter.setOnRvListener(
                this.onRvAdapterListenerForLeft = new OnRvAdapterListenerForLeft(this));

        this.weekRv.setAdapter(this.weekAdapter = new PlayBackWeekAdapter(this));
        this.weekRv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        this.weekRv.addItemDecoration(new PlayBackWeekDecorations());
        this.weekAdapter.setOnRvListener(this.onRvAdapterListenerForWeek =
                new OnRvAdapterListenerForWeek(this));

        this.rightRv.setAdapter(this.chalAdapter = new PlayBackChalAdapter(this));
        this.rightRv.setLayoutManager(new LinearLayoutManager(this));
        this.rightRv.addItemDecoration(new PlayBackChalDecorations());
        this.chalAdapter.setOnRvListener(this.onRvAdapterListenerForRight =
                new OnRvAdapterListenerForRight(this));

        this.mOnClickListener = new MOnClickListener(this);
        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnKeyListener = new MOnKeyListener(this);

        this.yyV.setOnClickListener(this.mOnClickListener);
        this.zhV.setOnClickListener(this.mOnClickListener);
        this.yyV.setOnKeyListener(this.mOnKeyListener);
        this.zhV.setOnKeyListener(this.mOnKeyListener);
        this.yyV.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.zhV.setOnFocusChangeListener(this.mOnFocusChangeListener);
//
        this.drawableFB = new FocusBorder.Builder()
                .asDrawable()
                .borderResId(R.drawable.kind_item_f)
                .build(this);
        this.colorFB = new FocusBorder.Builder()
                .asColor()
                .borderColor(Color.TRANSPARENT)
                .shadowColor(Color.TRANSPARENT)
                .borderWidth(1)
                .shadowWidth(1)
                .build(this);

        for (ViewGroup parent = (ViewGroup) this.netV.getParent(); parent != null;
             parent = (ViewGroup) parent.getParent()) {
            parent.setClipToPadding(false);
            parent.setClipChildren(false);
        }

        this.seeKBar.setFocusable(false);
        this.seeKBar.setFocusableInTouchMode(false);

        this.videoView.setOnPreparedListener(this.mOnPreparedListener =
                new MOnPreparedListener(this));
        this.videoView.setOnCompletionListener(this.mOnCompletionListener =
                new MOnCompletionListener(this));
        this.videoView.setOnErrorListener(this.mOnErrorListener =
                new MOnErrorListener(this));
        this.videoView.setOnInfoListener(this.mOnInfoListener =
                new MOnInfoListener(this));
        this.videoView.setOnSeekCompleteListener(this.mOnSeekCompleteListener =
                new MOnSeekCompleteListener(this));

//        DataCenter.Ins().getSerPlayBackEpg(this);

        this.yyV.post(new Runnable() {
            @Override
            public void run() {
                Utils.focusV(PlayBackAct.this.yyV, true);
            }
        });

        this.changeFocusBorder(this.colorFB);
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
        DataCenter.Ins().addDataListener(this.mOnDataListener);
        DataCenter.Ins().loginPlayBack();
        DataCenter.Ins().addPlayListener(this.mOnPlayListener);
        DataCenter.Ins().setActivityType(EXVAL.TYPE_ACTIVITY_OTH);
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
        this.videoView.stopPlayback();
        this.stopNetSpeed();
        DataCenter.Ins().delPlayListener(this.mOnPlayListener);
    }

    @Override
    protected void destroyAct() {
        DataCenter.Ins().loginPlayBack();
        DataCenter.Ins().stopGetPlayBack();
        DataCenter.Ins().stopPlayBackForceTVHttpReq();
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
        if (this.mOnKeyListener != null) {
            this.mOnKeyListener.release();
            this.mOnKeyListener = null;
        }
        if (this.leftAdapter != null) {
            this.leftAdapter.release();
            this.leftAdapter = null;
        }
        if (this.weekAdapter != null) {
            this.weekAdapter.release();
            this.weekAdapter = null;
        }
        if (this.onRvAdapterListenerForRight != null) {
            this.onRvAdapterListenerForRight.release();
            this.onRvAdapterListenerForRight = null;
        }
        if (this.chalAdapter != null) {
            this.chalAdapter.release();
            this.chalAdapter = null;
        }
        if (this.onRvAdapterListenerForLeft != null) {
            this.onRvAdapterListenerForLeft.release();
            this.onRvAdapterListenerForLeft = null;
        }
        if (this.onRvAdapterListenerForWeek != null) {
            this.onRvAdapterListenerForWeek.release();
            this.onRvAdapterListenerForWeek = null;
        }
        this.epgObj = null;
        if (this.mHandler != null) {
            this.mHandler.removeCallbacksAndMessages(null);
            this.mHandler.release();
            this.mHandler = null;
        }
        this.hideProgress();
        if (this.loadWindow != null) {
            this.loadWindow.relase();
            this.loadWindow = null;
        }
        if (this.mOnPlayListener != null) {
            this.mOnPlayListener.release();
            this.mOnPlayListener = null;
        }
        if (this.mOnPreparedListener != null) {
            this.mOnPreparedListener.release();
            this.mOnPreparedListener = null;
        }
        if (this.mOnCompletionListener != null) {
            this.mOnCompletionListener.release();
            this.mOnCompletionListener = null;
        }
        if (this.mOnErrorListener != null) {
            this.mOnErrorListener.release();
            this.mOnErrorListener = null;
        }
        if (this.mOnInfoListener != null) {
            this.mOnInfoListener.release();
            this.mOnInfoListener = null;
        }
        if (this.mOnSeekCompleteListener != null) {
            this.mOnSeekCompleteListener.release();
            this.mOnSeekCompleteListener = null;
        }
        if (this.videoView != null) {
            this.videoView.release();
            this.videoView = null;
        }
        if (this.mvRootKeyListener != null) {
            this.mvRootKeyListener.release();
            this.mvRootKeyListener = null;
        }
        this.bottomCtrlRootV = null;
        this.playCtrlRootV = null;
        this.totalTimeV = null;
        this.curTimeV = null;
        this.titleV = null;
        this.speedV = null;
        this.seeKBar = null;
        this.epgV = null;
    }

    @Override
    public FocusBorder createFocusBorder() {
        return null;
    }

    @Override
    public int layoutId() {
        return R.layout.playback_act;
    }

    private static class MOnDataListener implements OnDataListener {

        private PlayBackAct hostCls;

        public MOnDataListener(PlayBackAct hostCls) {
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
            if (hostCls == null) {
                return;
            }
            if (!DataCenter.Ins().isLoginOK()) {
                Utils.sendLoginTipsMsg(hostCls.mHandler, loginStr, dialogCancelable,
                        MSG.SHOW_LOGIN_TIPS);
            } else {
                if (DataCenter.Ins().isExpired()) {
                    Utils.sendMsg(hostCls.mHandler, MSG.SHOW_PAY);
                } else {
                    if (!TextUtils.isEmpty(loginStr)) {
                        Utils.sendLoginTipsMsg(hostCls.mHandler, loginStr, dialogCancelable,
                                MSG.SHOW_LOGIN_TIPS);
                    } else {
                        Utils.sendMsg(hostCls.mHandler, MSG.HIDE_LOGIN_TIPS);
                        Utils.sendMsg(hostCls.mHandler, MSG.INTO_UI);
                    }
                }
            }
        }

        @Override
        public void onToken() {

        }

        @Override
        public void onPay(final boolean b, final String s) {
            if (hostCls != null) {
                hostCls.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AppMain.showToastTips(s);
                        if (b) {
                            if (hostCls.payDialog != null) {
                                hostCls.payDialog.dismiss();
                                hostCls.payDialog = null;
                            }
                            DataCenter.Ins().loginPlayBack();
                        }
                    }
                });
            }
        }
    }

    private static class OnRvAdapterListenerForLeft implements OnRvAdapterListener {

        private PlayBackAct hostCls;

        public OnRvAdapterListenerForLeft(PlayBackAct hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void onItemSelected(View view, int position) {
            if (this.hostCls == null || this.hostCls.isFullScreen) {
                return;
            }
            EpgObj epgObj = this.hostCls.leftAdapter.getDataForItem(position);
            if (this.hostCls.epgObj == null || !this.hostCls.epgObj.equals(epgObj)) {
                this.hostCls.changeRelateDatasForEpg(
                        this.hostCls.leftAdapter.getDataForItem(position));
            }
        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            if (this.hostCls == null || this.hostCls.isFullScreen) {
                return;
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                    this.hostCls.leftAdapter.marketCacheForFocus(false);
                    this.hostCls.resetFocusBorder();
                    this.hostCls.leftAdapter.lock();
                    OnRvAdapterListenerForLeft.this.hostCls.chalAdapter.marketCacheForFocus(true);
//                    this.hostCls.rightRv.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            OnRvAdapterListenerForLeft.this.hostCls.chalAdapter.setSelection(
//                                    OnRvAdapterListenerForLeft.this.hostCls.chalAdapter.getCurSelIndex());
//                        }
//                    });
                    break;
                }
                case KeyEvent.KEYCODE_DPAD_UP: {
                    if (position == 0) {
                        this.hostCls.leftAdapter.lock();
                        this.hostCls.resetFocusBorder();
                        this.hostCls.leftAdapter.marketCacheForFocus(false);
                        this.hostCls.focusBorderVisible(false);
                        this.hostCls.changeFocusBorder(this.hostCls.colorFB);
                        this.hostCls.yyV.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.focusV(OnRvAdapterListenerForLeft.this.hostCls.selTitleV,
                                        true);
                                Utils.focusV(OnRvAdapterListenerForLeft.this.hostCls.selTitleV ==
                                        OnRvAdapterListenerForLeft.this.hostCls.yyV ?
                                        OnRvAdapterListenerForLeft.this.hostCls.zhV :
                                        OnRvAdapterListenerForLeft.this.hostCls.yyV, false);
                            }
                        });
                    }
                    break;
                }
                case KeyEvent.KEYCODE_BACK: {
                    if (!this.hostCls.isFullScreen) {
                        this.hostCls.onBackPressed();
                    }
                    break;
                }
            }
        }

        @Override
        public void onItemClick(View view, int position) {

        }

        @Override
        public void onItemLongClick(View view, int position) {

        }

        @Override
        public void release() {
            this.hostCls = null;
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private PlayBackAct hostCls;

        public MOnFocusChangeListener(PlayBackAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            MLog.d(TAG, String.format("onFocusChange v=%s hasFocus=%b", v, hasFocus));
            if (this.hostCls == null) {
                return;
            }

            if (hasFocus) {
                v.setActivated(false);
                this.hostCls.focusBorderForV(v, 1.2f, 1.2f);
                int vId = v.getId();
                if (this.hostCls.loadDataOver) {
                    this.hostCls.leftAdapter.setNeedMarket(true);
                    this.hostCls.leftAdapter.setMarketPos(0);
                    this.hostCls.leftAdapter.changeEpgList(vId);
                }
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private PlayBackAct hostCls;

        public MOnKeyListener(PlayBackAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            MLog.d(TAG, String.format("onKey v=%s event=%s", v, event));
            if (this.hostCls == null || (event.getAction() == KeyEvent.ACTION_UP)) {
                return false;
            }

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_DOWN: {
                    if (this.hostCls.leftAdapter.getItemCount() > 0) {
                        this.hostCls.resetFocusBorder();
                        this.hostCls.selTitleV = (TextView) v;
                        Utils.noFocus(v == this.hostCls.yyV ? this.hostCls.zhV :
                                this.hostCls.yyV);
                        Utils.noFocus(v);
                        v.setActivated(true);
                        this.hostCls.leftAdapter.unLock();
                        this.hostCls.colorFB.setVisible(false);
                        this.hostCls.changeFocusBorder(this.hostCls.drawableFB);
                        MOnKeyListener.this.hostCls.leftAdapter.marketCacheForFocus(true);
                    }
                    break;
                }
                case KeyEvent.KEYCODE_BACK: {
                    if (!this.hostCls.isFullScreen) {
                        this.hostCls.onBackPressed();
                    }
                    break;
                }
            }

            return false;
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private PlayBackAct hostCls;

        public MOnClickListener(PlayBackAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {

            if (this.hostCls != null) {

            }
        }
    }

    public void loadEpgs(final List<EpgObj> yyEpgObjs,
                         final List<EpgObj> zhEpgObjs) {
        this.loadDataOver = true;
        PlayBackAct.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlayBackAct.this.leftAdapter.setNeedMarket(true);
                PlayBackAct.this.leftAdapter.setMarketPos(0);
                leftAdapter.initDatas(yyEpgObjs, yyEpgObjs.size(), zhEpgObjs, zhEpgObjs.size());
                PlayBackAct.this.selTitleV = PlayBackAct.this.yyV.isFocused() ?
                        PlayBackAct.this.yyV : PlayBackAct.this.zhV;
                leftAdapter.changeEpgList(PlayBackAct.this.selTitleV.getId());
                Utils.focusV(PlayBackAct.this.selTitleV, true);
            }
        });
    }

    public void changeRelateDatasForEpg(EpgObj epgObj) {
        this.epgObj = epgObj;
//        MLog.d(TAG, "changeRelateDatasForEpg " + epgObj);
        if (this.epgObj != null) {
            List<String> dates = this.epgObj.getDates();
//            MLog.d(TAG, "changeRelateDatasForEpg dates " + dates);
            if (PlayBackAct.this.weekAdapter.getItemCount() > 0) {
                PlayBackAct.this.weekAdapter.clearCacheFocus();
            }
            PlayBackAct.this.weekAdapter.setNeedMarket(true);
            PlayBackAct.this.weekAdapter.setMarketPos(0);
            PlayBackAct.this.weekAdapter.setDatas(dates);
            if (PlayBackAct.this.weekAdapter.getItemCount() > 0) {
                PlayBackAct.this.chalAdapter.setNeedMarket(true);
                PlayBackAct.this.chalAdapter.setMarketPos(0);
                PlayBackAct.this.chalAdapter.setDatas(DBMgr.Ins().getPlayBackChals(epgObj,
                        dates.get(0)));
            }
        }
    }

    private static class OnRvAdapterListenerForRight implements OnRvAdapterListener {

        private PlayBackAct hostCls;

        public OnRvAdapterListenerForRight(PlayBackAct hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void onItemSelected(View view, int position) {

        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            if (this.hostCls == null) {
                return;
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT: {
                    this.hostCls.resetFocusBorder();
                    this.hostCls.leftAdapter.unLock();
                    this.hostCls.chalAdapter.marketCacheForFocus(false);
                    this.hostCls.leftAdapter.marketCacheForFocus(true);
//                    this.hostCls.leftRv.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            OnRvAdapterListenerForRight.this.hostCls.leftAdapter.setSelection(
//                                    OnRvAdapterListenerForRight.this.hostCls.leftAdapter.getCurSelIndex());
//                        }
//                    });
                    break;
                }
                case KeyEvent.KEYCODE_DPAD_UP: {
                    if (position == 0) {
                        this.hostCls.resetFocusBorder();
                        this.hostCls.chalAdapter.marketCacheForFocus(false);
                        this.hostCls.focusBorderVisible(false);
                        this.hostCls.changeFocusBorder(this.hostCls.colorFB);
                        this.hostCls.weekAdapter.marketCacheForFocus(true);
//                        final int weekCurSelIndex = OnRvAdapterListenerForRight.this.hostCls.
//                                weekAdapter.getCurSelIndex();
//                        this.hostCls.weekRv.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                OnRvAdapterListenerForRight.this.hostCls.weekAdapter.setSelection(
//                                        weekCurSelIndex);
//                                OnRvAdapterListenerForRight.this.hostCls.weekAdapter.setLock(false);
//                            }
//                        });
                    }
                    break;
                }
                case KeyEvent.KEYCODE_BACK: {
                    if (!this.hostCls.isFullScreen) {
                        this.hostCls.onBackPressed();
                    }
                    break;
                }
            }
        }

        @Override
        public void onItemClick(View view, int position) {
            if (this.hostCls == null) {
                return;
            }
            if (this.hostCls.videoRootVLayoutParams == null) {
                this.hostCls.videoRootVLayoutParams = (RelativeLayout.LayoutParams)
                        this.hostCls.videoRootV.getLayoutParams();
            }
            boolean isSmallChal = false;
            ChalObj chalObj = this.hostCls.chalAdapter.getDataForItem(position);
            if (chalObj != null && (this.hostCls.curChal == null ||
                    !(isSmallChal = chalObj.equals(this.hostCls.curChal)))) {
                this.hostCls.curChal = chalObj;
                this.hostCls.loadMoiveInfo(this.hostCls.curChal);
                Utils.sendMsg(this.hostCls.mHandler, MSG.PLAY_POS);
            }
            if (isSmallChal) {
                this.hostCls.changeVideoRootVScreenMod(true);
            }
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }

        @Override
        public void release() {
            this.hostCls = null;
        }
    }

    private static class OnRvAdapterListenerForWeek implements OnRvAdapterListener {

        private PlayBackAct hostCls;

        public OnRvAdapterListenerForWeek(PlayBackAct hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void onItemSelected(View view, int position) {
            if (this.hostCls == null) {
                return;
            }
            this.hostCls.chalAdapter.setDatas(DBMgr.Ins().getPlayBackChals(this.hostCls.epgObj,
                    this.hostCls.weekAdapter.getDataForItem(position)));
        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            if (this.hostCls == null) {
                return;
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP: {
                    this.hostCls.leftAdapter.unLock();
                    this.hostCls.weekAdapter.marketCacheForFocus(false);
                    this.hostCls.resetFocusBorder();
                    this.hostCls.changeFocusBorder(this.hostCls.drawableFB);
                    this.hostCls.leftAdapter.marketCacheForFocus(true);
//                    this.hostCls.leftRv.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            OnRvAdapterListenerForWeek.this.hostCls.leftAdapter.setSelection(
//                                    OnRvAdapterListenerForWeek.this.hostCls.leftAdapter.getCurSelIndex());
//                        }
//                    });
                    break;
                }
                case KeyEvent.KEYCODE_DPAD_DOWN: {
                    this.hostCls.resetFocusBorder();
                    this.hostCls.weekAdapter.marketCacheForFocus(false);
                    this.hostCls.changeFocusBorder(this.hostCls.drawableFB);
//                    this.hostCls.rightRv.setCacheHasFocus(true);
//                    this.hostCls.chalAdapter.marketCacheForFocus(true);
                    this.hostCls.rightRv.post(new Runnable() {
                        @Override
                        public void run() {
                            OnRvAdapterListenerForWeek.this.hostCls.rightRv.setCacheHasFocus(true);
                            OnRvAdapterListenerForWeek.this.hostCls.chalAdapter.marketCacheForFocus(true);
//                            OnRvAdapterListenerForWeek.this.hostCls.chalAdapter.setSelection(
//                                    OnRvAdapterListenerForWeek.this.hostCls.chalAdapter.getCurSelIndex());
                        }
                    });
                    break;
                }
                case KeyEvent.KEYCODE_DPAD_LEFT: {
                    if (position == 0) {
                        this.hostCls.leftAdapter.unLock();
                        this.hostCls.weekAdapter.marketCacheForFocus(false);
                        this.hostCls.resetFocusBorder();
                        this.hostCls.changeFocusBorder(this.hostCls.drawableFB);
                        this.hostCls.leftAdapter.marketCacheForFocus(true);
//                        this.hostCls.leftRv.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                OnRvAdapterListenerForWeek.this.hostCls.leftAdapter.setSelection(
//                                        OnRvAdapterListenerForWeek.this.hostCls.leftAdapter.getCurSelIndex());
//                            }
//                        });
                    }
                    break;
                }
                case KeyEvent.KEYCODE_BACK: {
                    if (!this.hostCls.isFullScreen) {
                        this.hostCls.onBackPressed();
                    }
                    break;
                }
            }
        }

        @Override
        public void onItemClick(View view, int position) {

        }

        @Override
        public void onItemLongClick(View view, int position) {

        }

        @Override
        public void release() {
            this.hostCls = null;
        }
    }

    private static class MHandler extends Handler {

        private PlayBackAct hostCls;

        public MHandler(PlayBackAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            hostCls = null;
        }

        @Override
        public void handleMessage(Message msg) {

            if (hostCls == null) {
                return;
            }

            MLog.d(TAG, "handleMessage " + msg.what);

            switch (msg.what) {
                case MSG.MSG_UPDATE_PROGRESS:
                    MLog.d(TAG, "MSG.MSG_UPDATE_PROGRESS " + hostCls.isSeekIng);
                    MLog.d(TAG, "MSG.MSG_UPDATE_PROGRESS " + hostCls.playCtrlRootV.isShown());
                    if (/*hostCls.playCtrlRootV.isShown() && */!hostCls.isSeekIng) {
                        MLog.d(TAG, "into MSG.MSG_UPDATE_PROGRESS");
                        int pos = hostCls.getCurrentPosition();
                        hostCls.updateProgressBar(false);
                        Utils.sendMsg(hostCls.mHandler, MSG.MSG_UPDATE_PROGRESS, 1000 - (pos % 1000));
                    }
                    break;
                case MSG.MSG_SHOW_SEEK_BAR:
                    hostCls.updateProgressBar(true);
                    hostCls.bottomCtrlRootV.setVisibility(View.VISIBLE);
                    hostCls.playCtrlRootV.setVisibility(View.VISIBLE);
                    hostCls.execNetSpeed();
                    break;
                case MSG.MSG_HIDE_SEEK_BAR:
                    hostCls.stopUpdateSeek();
                    hostCls.bottomCtrlRootV.setVisibility(View.GONE);
                    hostCls.playCtrlRootV.setVisibility(View.GONE);
                    if (!hostCls.loadWindow.isShown()) {
                        hostCls.stopNetSpeed();
                    }
                    break;
                case MSG.MSG_SEEK:
                    Log.d(TAG, "MSG_SEEK");
                    hostCls.seekByProgressBar();
                    break;
                case MSG.PLAY_POS:
                    if (hostCls.curChal != null) {
                        DataCenter.Ins().sendPlayBackForceTVHttpReq(hostCls.curChal);
                        hostCls.videoView.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    hostCls.showLoadWindow();
                                } catch (Exception e) {
                                }
                            }
                        });
                    }
                    break;
                case MSG.MSG_UPDATE_SPEED: {
                    Bundle bundle = msg.getData();
                    if (bundle != null) {
                        String speedStr = bundle.getString(EXVAL.SPEED);
                        if (hostCls.bottomCtrlRootV != null && hostCls.bottomCtrlRootV.isShown() &&
                                hostCls.playCtrlRootV.isShown()) {
                            hostCls.speedV.setText(speedStr);
                        }
                        if (hostCls.loadWindow != null && hostCls.loadWindow.isShown()) {
                            hostCls.loadWindow.updateLoadSpeed(speedStr);
                        }
                    }
                    break;
                }
                case MSG.MSG_STATUS_HIDE:
                    hostCls.operStatus.setVisibility(View.GONE);
                    break;
                case MSG.SHOW_LOGIN_TIPS: {
                    Bundle bundle = msg.getData();
                    hostCls.showTips(bundle.getString(EXVAL.LOGIN_MSG),
                            bundle.getBoolean(EXVAL.DIALOG_CANCELABLE));
                    break;
                }
                case MSG.HIDE_LOGIN_TIPS:
                    hostCls.hideTips();
                    break;
                case MSG.INTO_UI:
                    DataCenter.Ins().getSerPlayBackEpg(hostCls);
                    break;
                case MSG.SHOW_PAY: {
                    hostCls.payDialog = PayDialog.showPay(hostCls, this);
                    break;
                }
                case MSG.EXIT_APP: {
                    hostCls.finish();
                    break;
                }
            }
        }
    }

    private static class MOnPreparedListener implements MediaPlayer.OnPreparedListener {

        private PlayBackAct hostCls;

        public MOnPreparedListener(PlayBackAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            if (this.hostCls == null) {
                return;
            }
            this.hostCls.isPlaying = true;
            this.hostCls.videoView.start();
            this.hostCls.curPlayStatus = EXVAL.PLAY_PREPARED;
            this.hostCls.totaltime = this.hostCls.videoView.getDuration();
            this.hostCls.seekTime = -1;
            this.hostCls.curtime = 0;
            if (this.hostCls.isFullScreen) {
                this.hostCls.hideProgress();
                this.hostCls.showSeekBar();
                this.hostCls.hideSeekBar(5000);
            }
//            MLog.d(TAG, "onPrepared " + this.hostCls.videoView.getDuration());
            this.hostCls.loadWindow.dismiss();
        }
    }

    private static class MOnCompletionListener implements MediaPlayer.OnCompletionListener {

        private PlayBackAct hostCls;

        public MOnCompletionListener(PlayBackAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            MLog.d(TAG, "onCompletion");
            if (this.hostCls != null) {
                this.hostCls.curPlayStatus = EXVAL.PLAY_OVER;
                this.hostCls.playNext(true);
            }
        }
    }

    private static class MOnErrorListener implements MediaPlayer.OnErrorListener {

        private PlayBackAct hostCls;

        public MOnErrorListener(PlayBackAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }
    }

    private static class MOnInfoListener implements MediaPlayer.OnInfoListener {

        private PlayBackAct hostCls;

        public MOnInfoListener(PlayBackAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {

            MLog.d(TAG, "onInfo " + what);
            if (this.hostCls == null) {
                return false;
            }

            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    this.hostCls.showLoadWindow();
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    if (!this.hostCls.isSeekIng) {
                        this.hostCls.hideSeekBar(0);
                    }
                    if (this.hostCls.loadWindow != null) {
                        this.hostCls.loadWindow.dismiss();
                    }
                    break;
            }
            return false;
        }
    }

    private static class MOnSeekCompleteListener implements MediaPlayer.OnSeekCompleteListener {

        private PlayBackAct hostCls;

        public MOnSeekCompleteListener(PlayBackAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            MLog.d(TAG, "onSeekComplete");
            if (this.hostCls != null) {
                this.hostCls.isSeekIng = false;
                if (this.hostCls.curPlayStatus == EXVAL.PLAY_PAUSE) {
                    this.hostCls.curPlayStatus = EXVAL.PLAY_PREPARED;
                    if (this.hostCls.operStatus != null && this.hostCls.operStatus.isShown()) {
                        this.hostCls.operStatus.setVisibility(View.GONE);
                    }
                    if (this.hostCls.videoView != null) {
                        this.hostCls.videoView.start();
                    }
                }
            }
        }
    }

    private static class MOnPlayListener implements OnPlayListener {

        private PlayBackAct hostCls;

        public MOnPlayListener(PlayBackAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onPlay(final String url) {
            MLog.d(TAG, "onPlay " + url);
            this.hostCls.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        hostCls.videoView.setVideoPath(url);
                    } catch (Exception e) {
                    }
                }
            });
        }

        @Override
        public void onNetSpeed(final String speedStr) {
            MLog.d(TAG, "onNetSpeed " + speedStr);
            if (this.hostCls != null) {
                this.hostCls.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (hostCls.bottomCtrlRootV != null && hostCls.bottomCtrlRootV.isShown() &&
                                    hostCls.playCtrlRootV.isShown()) {
                                hostCls.speedV.setText(speedStr);
                            }
                            if (hostCls.loadWindow != null && hostCls.loadWindow.isShown()) {
                                hostCls.loadWindow.updateLoadSpeed(speedStr);
                            }
                        } catch (Exception e) {
                        }
                    }
                });
            }
        }
    }

    private void stopUpdateSeek() {
        Utils.removeMsg(this.mHandler, MSG.MSG_UPDATE_PROGRESS);
    }

    private void startUpdateSeek(int delay) {
        Utils.sendMsg(this.mHandler, MSG.MSG_UPDATE_PROGRESS, delay);
    }

    private void sendSeekMsg() {
        Utils.sendMsg(mHandler, MSG.MSG_SEEK, 300);
    }

    private void seekByProgressBar() {
        int dest = this.seeKBar.getCurPorgress();
        int pos = this.totaltime * (dest + 1) / 100;
        // check for small stream while seeking
        int pos_check = this.totaltime * (dest + 1) - pos * 100;
        if (pos_check > 0) {
            pos += 1;
        }
        // 防止在拖动到最后的时候，seek到totaltime时加载不出视频，故减去3秒
        if (pos >= this.totaltime) {
            pos = this.totaltime - 1000;
        }
        if (dest <= 1) {
            pos = 0;
        }
        this.videoView.seekTo(pos);

        this.showLoadWindow();
    }

    private void hideSeekBar(int timeout) {
        Utils.sendMsg(this.mHandler, MSG.MSG_HIDE_SEEK_BAR, timeout);
    }

    private void showSeekBar() {
        hideSeekBar(0);
        startUpdateSeek(0);
        Utils.sendMsg(this.mHandler, MSG.MSG_SHOW_SEEK_BAR);
        hideSeekBar(5000);
    }

    private int getCurrentPosition() {
        if (null != this.videoView) {
            return this.videoView.getCurrentPosition();
        }
        return 0;
    }

    private int getDuration() {
        if (null != this.videoView) {
            return this.videoView.getDuration();
        }
        return 0;
    }

    private String secToTime(int i) {
        String retStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (i <= 0) {
            return "00:00:00";
        } else {
            minute = i / 60;
            if (minute < 60) {
                second = i % 60;
                retStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99) {
                    return "00:00:00";
                }
                minute = minute % 60;
                second = i % 60;
                retStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return retStr;
    }

    private String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10) {
            retStr = "0" + Integer.toString(i);
        } else {
            retStr = Integer.toString(i);
        }
        return retStr;
    }

    private void updateProgressBar(boolean focusUpdate) {
        if (this.playCtrlRootV.isShown() || focusUpdate) {

            this.curtime = getCurrentPosition();

            Log.d(TAG, "curtime = " + this.curtime);

            this.totalTimeV.setText(secToTime(this.totaltime / 1000));
            if (this.totaltime != 0) {
                int curtimetmp = this.curtime / 1000;
                int totaltimetmp = this.totaltime / 1000;
                if (totaltimetmp != 0) {
                    int progress = curtimetmp * 100 / totaltimetmp;
                    this.seeKBar.setProgress(progress);
                    this.seeKBar.setSecondaryProgress(progress);
                    this.seeKBar.setCurProgress(progress);
                    String curTimeStr = secToTime(this.curtime / 1000);
                    this.seeKBar.setNumText(curTimeStr);
                    this.curTimeV.setText(curTimeStr);
                }
            } else {
                this.seeKBar.setProgress(0);
                this.seeKBar.setSecondaryProgress(0);
                this.seeKBar.setCurProgress(0);
                String curTimeStr = secToTime(0 / 1000);
                this.seeKBar.setNumText(curTimeStr);
                this.curTimeV.setText(curTimeStr);
            }
        }
    }

    private void hideProgress() {
        this.bottomCtrlRootV.setVisibility(View.GONE);
        if (this.playCtrlRootV.isShown()) {
            if (!this.loadWindow.isShown()) {
                this.stopNetSpeed();
            }
            this.hideSeekBar(0);
        }
        this.playCtrlRootV.setVisibility(View.GONE);
    }

    private void playNext(boolean autoPlay) {
        int moviePos = this.chalAdapter.getCurSelIndex();
        int movieNum = this.chalAdapter.getItemCount();
        moviePos++;
        if (moviePos < movieNum) {
            loadMoiveInfo(moviePos);
            if (autoPlay) {
                Utils.sendMsg(this.mHandler, MSG.PLAY_POS);
            }
        } else {
            this.videoView.stopPlayback();
            this.tipsV.setVisibility(View.VISIBLE);
            this.hideProgress();
            this.showSeekBar();
            this.hideSeekBar(0);
            this.loadWindow.dismiss();
        }
    }

    private void playPrev(boolean autoPlay) {
        int moviePos = this.chalAdapter.getCurSelIndex();
        moviePos--;
        if (moviePos >= 0) {
            loadMoiveInfo(moviePos);
            if (autoPlay) {
                Utils.sendMsg(this.mHandler, MSG.PLAY_POS);
            }
        }
    }

    public boolean isShowBottomProgress() {
        return this.bottomCtrlRootV.isShown() && this.playCtrlRootV.isShown();
    }

    private Future<?> netSpeedTask;
    private GetNetSpeedTask netSpeedTaskObj;

    public void execNetSpeed() {
        stopNetSpeed();
        this.netSpeedTaskObj = new GetNetSpeedTask(this);
        this.netSpeedTask = DataCenter.Ins().submitExTask(this.netSpeedTaskObj);
    }

    public void stopNetSpeed() {
        if (this.netSpeedTaskObj != null) {
            this.netSpeedTaskObj.release();
            this.netSpeedTaskObj = null;
        }
        if (this.netSpeedTask != null) {
            this.netSpeedTask.cancel(true);
            this.netSpeedTask = null;
        }
    }

    private static class GetNetSpeedTask implements Runnable {

        private PlayBackAct hostCls;
        private boolean netSpeedRunning;

        public GetNetSpeedTask(PlayBackAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
            this.netSpeedRunning = false;
        }

        @Override
        public void run() {

            this.netSpeedRunning = true;

            long oldRxByte = 0, oldTime = 0;

            long newRxByte = 0L;
            long newTime = 0L;
            long getBytes = 0L;
            StringBuilder strBytes = new StringBuilder();

            while (this.netSpeedRunning) {
                newRxByte = TrafficStats.getTotalRxBytes();
                newTime = System.currentTimeMillis();
                if (((newRxByte - oldRxByte) != 0L)
                        && ((newTime - oldTime) != 0L)) {
                    getBytes = 1000L * ((newRxByte - oldRxByte) / (newTime - oldTime)) / 1024L;
                    if (getBytes >= 1000L) {
                        strBytes.append(new DecimalFormat("#.##")
                                .format(getBytes / 1024D)).append("MB/S");
                    } else {
                        strBytes.append(getBytes).append("KB/S");
                    }
                }

                if (this.hostCls != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(EXVAL.SPEED, strBytes.toString());
                    Utils.sendMsg(this.hostCls.mHandler, bundle, MSG.MSG_UPDATE_SPEED);
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

    private void loadMoiveInfo(int moviePos) {
        this.tipsV.setVisibility(View.GONE);
        this.curChal = this.chalAdapter.getDataForItem(moviePos);
        if (this.curChal != null) {
            this.chalAdapter.setCurSelIndex(moviePos);
            this.epgV.setText(String.format("%s  %s",
                    this.curChal.getBeginTime(), this.curChal.getName()));
            this.curJmV.setText(this.curChal.getName());
        }
        if (this.epgObj != null) {
            this.curPdV.setText(epgObj.getName());
        }
        if (this.isFullScreen) {
            showSeekBar();
            hideSeekBar(5000);
        }
    }

    private void loadMoiveInfo(ChalObj chalObj) {
        this.tipsV.setVisibility(View.GONE);
        if (chalObj != null) {
            this.epgV.setText(String.format("%s  %s", chalObj.getBeginTime(), chalObj.getName()));
            this.curJmV.setText(chalObj.getName());
        }
        if (this.epgObj != null) {
            this.curPdV.setText(epgObj.getName());
        }
        if (this.isFullScreen) {
            showSeekBar();
            hideSeekBar(5000);
        }
    }

    private void showStatus(int bgResId) {
        if (!this.operStatus.isShown()) {
            this.operStatus.setVisibility(View.VISIBLE);
        }
        this.operStatus.setBackgroundResource(bgResId);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MLog.d(TAG, "onBackPressed");
        execStop();
        execDestroy();
    }

    private void changeVideoRootVScreenMod(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
        this.focusBorderVisible(false);
        if (this.isFullScreen) {
            this.videoRootV.setLayoutParams(this.videoRootVScreenLayoutParams);
            this.videoRootV.post(new Runnable() {
                @Override
                public void run() {
                    Utils.focusV(PlayBackAct.this.videoRootV, true);
                    PlayBackAct.this.videoRootV.setDescendantFocusability(
                            ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                }
            });
        } else {
            Utils.noFocus(this.videoRootV);
            this.videoRootV.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            this.videoRootV.setLayoutParams(this.videoRootVLayoutParams);
        }
        this.videoRootV.requestLayout();
        if (this.loadWindow.isShown()) {
            this.showLoadWindow();
        }
        this.loadMoiveInfo(this.curChal);
    }

    private void showLoadWindow() {
        this.loadWindow.dismiss();
        this.loadWindow.setFullScreen(this.isFullScreen);
        this.loadWindow.show(this.videoView);
        this.loadWindow.updateLoadSpeed(this.speedV.getText().toString());
        this.execNetSpeed();
    }

    private Dialog tipsDialog;

    private void hideTips() {
        if (tipsDialog != null) {
            tipsDialog.dismiss();
            tipsDialog = null;
        }
    }

    private void showTips(String msgStr, boolean dialogCancelable) {
        hideTips();
        View rootV = LayoutInflater.from(AppMain.ctx()).inflate(R.layout.tips_dialog_layout, null);
        rootV.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        finish();
                    }
                }
                return false;
            }
        });
        TextView msgV = rootV.findViewById(R.id.msg_v);
        msgV.setText(msgStr);
        tipsDialog = new AlertDialog.Builder(PlayBackAct.this, R.style.TipsDialog)
                .setView(rootV)
                .setCancelable(dialogCancelable)
                .create();
        tipsDialog.show();
    }

    private static class MVRootKeyListener implements View.OnKeyListener {

        private PlayBackAct hostCls;

        public MVRootKeyListener(PlayBackAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (this.hostCls == null) {
                return false;
            }

            if (event.getAction() == KeyEvent.ACTION_UP) {
                if ((KeyEvent.KEYCODE_DPAD_LEFT == keyCode)
                        || (KeyEvent.KEYCODE_DPAD_RIGHT == keyCode)) {
                    this.hostCls.hideSeekBar(5000);
                } else if ((KeyEvent.KEYCODE_DPAD_UP == keyCode)
                        || (KeyEvent.KEYCODE_DPAD_DOWN == keyCode)) {
                    if (this.hostCls.tipsV.getVisibility() != View.VISIBLE) {
                        Utils.sendMsg(this.hostCls.mHandler, MSG.PLAY_POS);
                    }
                }
            } else {
                if (KeyEvent.KEYCODE_BACK == keyCode) {
                    if (!this.hostCls.execDestroyed) {
                        if (this.hostCls.bottomCtrlRootV != null &&
                                this.hostCls.bottomCtrlRootV.isShown()) {
                            if (this.hostCls.playCtrlRootV.isShown()) {
                                this.hostCls.hideSeekBar(0);
                            } else {
                                this.hostCls.hideProgress();
                            }
                        } else if (this.hostCls.isFullScreen) {
                            this.hostCls.changeVideoRootVScreenMod(false);
                            this.hostCls.rightRv.post(new Runnable() {
                                @Override
                                public void run() {
                                    MVRootKeyListener.this.hostCls.chalAdapter.setSelection(
                                            MVRootKeyListener.this.hostCls.chalAdapter.getCurSelIndex());
                                    MVRootKeyListener.this.hostCls.rightRv.setCacheHasFocus(true);
                                }
                            });
                        }
                        return true;
                    }
                    return false;
                }

                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        if (MVRootKeyListener.this.hostCls.curPlayStatus == EXVAL.PLAY_PREPARED) {
                            MVRootKeyListener.this.hostCls.showSeekBar();
                            MVRootKeyListener.this.hostCls.hideSeekBar(5000);
                            if (MVRootKeyListener.this.hostCls.videoView.isPlaying()) {
                                MVRootKeyListener.this.hostCls.showStatus(R.drawable.pause_status);
                                MVRootKeyListener.this.hostCls.videoView.pause();
                            } else {
                                if (MVRootKeyListener.this.hostCls.operStatus.isShown()) {
                                    MVRootKeyListener.this.hostCls.operStatus.setVisibility(View.GONE);
                                }
                                MVRootKeyListener.this.hostCls.videoView.start();
                            }
                        }
                        break;
                    case KeyEvent.KEYCODE_MEDIA_REWIND:
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        if (MVRootKeyListener.this.hostCls.curPlayStatus == EXVAL.PLAY_PREPARED) {
                            MVRootKeyListener.this.hostCls.isSeekIng = true;
                            MVRootKeyListener.this.hostCls.stopUpdateSeek();
                            if (!MVRootKeyListener.this.hostCls.bottomCtrlRootV.isShown()) {
                                MVRootKeyListener.this.hostCls.seekTime = MVRootKeyListener.this.
                                        hostCls.getCurrentPosition();
                                MVRootKeyListener.this.hostCls.showSeekBar();
                            } else if (MVRootKeyListener.this.hostCls.playCtrlRootV.isShown()) {
                                MVRootKeyListener.this.hostCls.hideSeekBar(5000);
                                MVRootKeyListener.this.hostCls.seekTime -=
                                        MVRootKeyListener.this.hostCls.SEEK_ONCE;
                                if (MVRootKeyListener.this.hostCls.seekTime < 0) {
                                    MVRootKeyListener.this.hostCls.seekTime = 0;
                                }
                                float curtimetmp = (float) MVRootKeyListener.this.hostCls.seekTime / 1000;
                                float totaltimetmp = (float) MVRootKeyListener.this.hostCls.totaltime / 1000;
                                if (totaltimetmp != 0) {
                                    int progress = (int) (curtimetmp * 100 / totaltimetmp);
                                    MVRootKeyListener.this.hostCls.seeKBar.setProgress(progress);
                                    MVRootKeyListener.this.hostCls.seeKBar.setCurProgress(progress);
                                    String curTimeStr = MVRootKeyListener.this.hostCls.
                                            secToTime((int) curtimetmp);
                                    MVRootKeyListener.this.hostCls.seeKBar.setNumText(curTimeStr);
                                    MVRootKeyListener.this.hostCls.curTimeV.setText(curTimeStr);
                                }
                                MVRootKeyListener.this.hostCls.sendSeekMsg();
                            }
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                    case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                        if (MVRootKeyListener.this.hostCls.curPlayStatus == EXVAL.PLAY_PREPARED) {
                            MVRootKeyListener.this.hostCls.isSeekIng = true;
                            MVRootKeyListener.this.hostCls.stopUpdateSeek();
                            if (!MVRootKeyListener.this.hostCls.bottomCtrlRootV.isShown()) {
                                MVRootKeyListener.this.hostCls.seekTime = MVRootKeyListener.this.
                                        hostCls.getCurrentPosition();
                                MVRootKeyListener.this.hostCls.showSeekBar();
                            } else if (MVRootKeyListener.this.hostCls.playCtrlRootV.isShown()) {
                                MVRootKeyListener.this.hostCls.hideSeekBar(5000);
                                MVRootKeyListener.this.hostCls.seekTime +=
                                        MVRootKeyListener.this.hostCls.SEEK_ONCE;
                                if (MVRootKeyListener.this.hostCls.seekTime >
                                        MVRootKeyListener.this.hostCls.totaltime) {
                                    MVRootKeyListener.this.hostCls.seekTime =
                                            MVRootKeyListener.this.hostCls.totaltime;
                                }
                                float curtimetmp = (float) MVRootKeyListener.this.hostCls.seekTime / 1000;
                                float totaltimetmp = (float) MVRootKeyListener.this.hostCls.totaltime / 1000;
                                if (totaltimetmp != 0) {
                                    int progress = (int) (curtimetmp * 100 / (float) totaltimetmp);
                                    MVRootKeyListener.this.hostCls.seeKBar.setProgress(progress);
                                    MVRootKeyListener.this.hostCls.seeKBar.setCurProgress(progress);
                                    String curTimeStr = MVRootKeyListener.this.hostCls.
                                            secToTime((int) curtimetmp);
                                    MVRootKeyListener.this.hostCls.seeKBar.setNumText(curTimeStr);
                                    MVRootKeyListener.this.hostCls.curTimeV.setText(curTimeStr);
                                }
                                MVRootKeyListener.this.hostCls.sendSeekMsg();
                            }
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP: {
                        MVRootKeyListener.this.hostCls.playNext(false);
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_DOWN: {
                        MVRootKeyListener.this.hostCls.playPrev(false);
                        break;
                    }
                }
            }

            return false;
        }
    }
}
