package com.allcn.activities;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;
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

import com.allcn.views.decorations.PlayBackWeekDecorations;
import com.allcn.views.focus.FocusBorder;
import com.datas.ChalDatsObj;
import com.datas.ChalObj;
import com.datas.EpgObj;
import com.datas.HomeMovie;
import com.datas.LiveChalObj;
import com.datas.ProgremPlayerInfo;
import com.db.cls.DBMgr;
import com.allcn.R;
import com.allcn.adapters.PBChalAdapter;
import com.allcn.adapters.PBLeftAdapter;
import com.allcn.adapters.PBWeekAdapter;
import com.allcn.interfaces.OnDataListener;
import com.allcn.interfaces.OnPlayListener;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.utils.MSG;
import com.allcn.utils.parsers.EpgParser;
import com.allcn.views.FocusKeepRecyclerView;
import com.allcn.views.LoadWindow;
import com.allcn.views.PayDialog;
import com.allcn.views.UiSeeKBar;
import com.allcn.views.decorations.PlayBackChalDecorations;
import com.mast.lib.parsers.MarqueeParser;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;
import com.mast.lib.views.VideoView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;


public class PlayBackMainAct extends BaseActivity {
    private static final String TAG = "PlayBackMainAct";

    //@BindView(R.id.playback_net_v)
    ImageView playbackNetV;
    //@BindView(R.id.playback_time_v)
    TextView playbackTimeV;
    //@BindView(R.id.playback_yy_title_v)
    TextView playbackYyTitleV;
    //@BindView(R.id.playback_zh_title_v)
    TextView playbackZhTitleV;
    //@BindView(R.id.playback_video_v)
    VideoView playbackVideoV;
    //@BindView(R.id.play_status_v)
    ImageView playStatusV;
    //@BindView(R.id.play_tips_v)
    TextView playTipsV;
    //@BindView(R.id.play_ctrl_title_v)
    TextView playCtrlTitleV;
    //@BindView(R.id.play_ctrl_epg_v)
    TextView playCtrlEpgV;
    //@BindView(R.id.play_ctrl_cur_time_v)
    TextView playCtrlCurTimeV;
    //@BindView(R.id.play_ctrl_speed_v)
    TextView playCtrlSpeedV;
    //@BindView(R.id.play_ctrl_total_time_v)
    TextView playCtrlTotalTimeV;
    //@BindView(R.id.seekbar)
    UiSeeKBar seekbar;
    //@BindView(R.id.play_ctrl_root_v)
    RelativeLayout playCtrlRootV;
    //@BindView(R.id.play_bottom_ctrl_root_v)
    FrameLayout playBottomCtrlRootV;
    //@BindView(R.id.playback_video_root_v)
    FrameLayout playbackVideoRootV;
    //@BindView(R.id.playback_cur_pd_head_v)
    TextView playbackCurPdHeadV;
    //@BindView(R.id.playback_cur_pd_v)
    TextView playbackCurPdV;
    //@BindView(R.id.playback_cur_jm_head_v)
    TextView playbackCurJmHeadV;
    //@BindView(R.id.playback_cur_jm_v)
    TextView playbackCurJmV;
    //@BindView(R.id.playback_left_list_v)
    FocusKeepRecyclerView playbackLeftListV;
    //@BindView(R.id.playback_chal_list_v)
    FocusKeepRecyclerView playbackChalListV;
    //@BindView(R.id.playback_week_list_v)
    FocusKeepRecyclerView playbackWeekListV;


    private TextView selTitleV;
    private LoadWindow loadWindow;

    private FocusBorder drawableFB;
    private FocusBorder colorFB;

    private int exW, exH, freeX, freeY;

    private MHandler mHandler;

    private boolean loadDataOver;
    private boolean isFullScreen;

    private MOnDataListener mOnDataListener;
    private MOnPlayListener mOnPlayListener;

    private MOnClickListener mOnClickListener;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnKeyListener mOnKeyListener;

    private OnRvAdapterListenerForLeft onRvAdapterListenerForLeft;
    private OnRvAdapterListenerForWeek onRvAdapterListenerForWeek;
    private OnRvAdapterListenerForProgram onRvAdapterListenerForProgram;

    private EpgObj epgObj;//当前选中的频道

    private ChalDatsObj.DataBean.ProgramsBean curProgramData;

    private List<EpgObj> mPlayBackEpgs;//recyclerView显示的数据
    private List<EpgObj> yyEpgs;
    private List<EpgObj> zhEpgs;
    private List<ChalDatsObj.DataBean> weekDataList;
    private List<ChalDatsObj.DataBean.ProgramsBean> programList;


    private PBLeftAdapter leftAdapter;
    private PBWeekAdapter weekAdapter;
    private PBChalAdapter programAdapter;
    private RelativeLayout.LayoutParams videoRootVLayoutParams, videoRootVScreenLayoutParams;


    //video相关参数
    private boolean isPlaying;
    private int curPlayStatus;
    private int totaltime;
    private int seekTime;
    private int curtime;
    private boolean isSeekIng;

    private MVRootKeyListener mvRootKeyListener;
    private MOnPreparedListener mOnPreparedListener;
    private MOnCompletionListener mOnCompletionListener;
    private MOnErrorListener mOnErrorListener;
    private MOnInfoListener mOnInfoListener;
    private MOnSeekCompleteListener mOnSeekCompleteListener;
    private AlertDialog tipsDialog;
    private PayDialog payDialog;

    private int SEEK_ONCE = 30 * 1000; // 30 sec

    private ORootVideoViewFocusChangeListener onVideoRootVFocusChangeL;
    private ORootVideoViewKeyListener onVideoRootVKeyL;
    private ORootVideoViewClickListner onVideoRootVClickL;


    public int focusShowState;
    public final static int FOCUS_SHOW_TITLE = 0;
    public final static int FOCUS_SHOW_LEFT = 1;
    public final static int FOCUS_SHOW_SMALL_VIDEO = 2;
    public final static int FOCUS_SHOW_WEEK = 3;
    public final static int FOCUS_SHOW_PROGRAM = 4;

    @Override
    protected void onStart() {
        super.onStart();
        DataCenter.Ins().addDataListener(mOnDataListener);
        DataCenter.Ins().loginPlayBack();
        DataCenter.Ins().addPlayListener(mOnPlayListener);
        DataCenter.Ins().setActivityType(EXVAL.TYPE_ACTIVITY_OTH);
        DataCenter.Ins().scanTime();
        DataCenter.Ins().regReceiver(AppMain.ctx());
    }

    @Override
    protected void stopAct() {
        DataCenter.Ins().unregReceiver(AppMain.ctx());
        DataCenter.Ins().delDataListener(mOnDataListener);
        playbackVideoV.stopPlayback();
        DBMgr.Ins().updateProgremPlayerInfo(new ProgremPlayerInfo(curProgramData.getName(), curProgramData.getVideo().getFilmId(), playbackVideoV.getCurrentPosition()));
        stopNetSpeed();
        DataCenter.Ins().delPlayListener(mOnPlayListener);
    }

    @Override
    protected void destroyAct() {

    }

    @Override
    FocusBorder createFocusBorder() {
        return new FocusBorder.Builder()
                .asDrawable()
                .borderResId(R.drawable.home_sel_bg)
                .build(this);
    }

    @Override
    protected void eventView() {

        mOnClickListener = new MOnClickListener(this);
        mOnFocusChangeListener = new MOnFocusChangeListener(this);
        mOnKeyListener = new MOnKeyListener(this);

        playbackYyTitleV.setOnKeyListener(mOnKeyListener);
        playbackYyTitleV.setOnFocusChangeListener(mOnFocusChangeListener);
        playbackYyTitleV.setOnClickListener(mOnClickListener);

        playbackZhTitleV.setOnKeyListener(mOnKeyListener);
        playbackZhTitleV.setOnFocusChangeListener(mOnFocusChangeListener);
        playbackZhTitleV.setOnClickListener(mOnClickListener);

        mOnDataListener = new MOnDataListener(this);
        mOnPlayListener = new MOnPlayListener(this);

        playbackVideoV.setOnKeyListener(mvRootKeyListener = new MVRootKeyListener(this));
        playbackVideoV.setOnPreparedListener(mOnPreparedListener = new MOnPreparedListener(this));
        playbackVideoV.setOnCompletionListener(mOnCompletionListener = new MOnCompletionListener(this));
        playbackVideoV.setOnErrorListener(mOnErrorListener = new MOnErrorListener(this));
        playbackVideoV.setOnInfoListener(mOnInfoListener = new MOnInfoListener(this));
        playbackVideoV.setOnSeekCompleteListener(mOnSeekCompleteListener = new MOnSeekCompleteListener(this));

    }

    @Override
    protected void findView() {
        playbackNetV
                = findViewById(R.id.playback_net_v);
                playbackTimeV
                = findViewById(R.id.playback_time_v);
        playbackYyTitleV
                = findViewById(R.id.playback_yy_title_v);
                playbackZhTitleV
                = findViewById(R.id.playback_zh_title_v);
        playbackVideoV
                = findViewById(R.id.playback_video_v);
                playStatusV
                = findViewById(R.id.play_status_v);
        playTipsV
                = findViewById(R.id.play_tips_v);
                playCtrlTitleV
                = findViewById(R.id.play_ctrl_title_v);
        playCtrlEpgV
                = findViewById(R.id.play_ctrl_epg_v);
                playCtrlCurTimeV
                = findViewById(R.id.play_ctrl_cur_time_v);
        playCtrlSpeedV
                = findViewById(R.id.play_ctrl_speed_v);
                playCtrlTotalTimeV
                = findViewById(R.id.play_ctrl_total_time_v);
        seekbar
                = findViewById(R.id.seekbar);
                playCtrlRootV
                = findViewById(R.id.play_ctrl_root_v);
        playBottomCtrlRootV
                = findViewById(R.id.play_bottom_ctrl_root_v);
                playbackVideoRootV
                = findViewById(R.id.playback_video_root_v);
        playbackCurPdHeadV
                = findViewById(R.id.playback_cur_pd_head_v);
                playbackCurPdV
                = findViewById(R.id.playback_cur_pd_v);
        playbackCurJmHeadV
                = findViewById(R.id.playback_cur_jm_head_v);
                playbackCurJmV
                = findViewById(R.id.playback_cur_jm_v);
        playbackLeftListV
                = findViewById(R.id.playback_left_list_v);
                playbackChalListV
                = findViewById(R.id.playback_chal_list_v);
        playbackWeekListV
                = findViewById(R.id.playback_week_list_v);
    }

    @Override
    int layoutId() {
        return R.layout.playback_act;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        //ButterKnife.bind(this);
        mHandler = new MHandler(this);

        loadWindow = new LoadWindow(this);

        mPlayBackEpgs = new ArrayList<>();//显示频道的数据
        weekDataList = new ArrayList<>();//星期和节目的数据
        programList = new ArrayList<>();//星期和节目的数据


        videoRootVScreenLayoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        seekbar.setFocusable(false);
        seekbar.setFocusableInTouchMode(false);

        selTitleV = playbackYyTitleV;

        initRecyclerView();
        initData();

        initFocusStyle();

        loadData();

        //Utils.focusV(playbackYyTitleV, true);
        changeFocusBorder(colorFB);
        playbackYyTitleV.setSelected(true);


        exW = (int) AppMain.res().getDimension(R.dimen.playback_small_video_ex_width);
        exH = (int) AppMain.res().getDimension(R.dimen.playback_small_video_ex_height);
        freeY = (int) AppMain.res().getDimension(R.dimen.playback_small_video_free_x);
        freeY = (int) AppMain.res().getDimension(R.dimen.playback_small_video_free_y);
    }

    private void loadData() {
        //DataCenter.Ins().getPlayBackEpgs(this);
    }

    private void initFocusStyle() {

        drawableFB = new FocusBorder.Builder()
                .asDrawable()
                .borderResId(R.drawable.kind_item_f)
                .build(this);
        colorFB = new FocusBorder.Builder()
                .asColor()
                .borderColor(Color.TRANSPARENT)
                .shadowColor(Color.TRANSPARENT)
                .borderWidth(1)
                .shadowWidth(1)
                .build(this);

    }

    private void initRecyclerView() {
        playbackLeftListV.setLayoutManager(new LinearLayoutManager(this));//频道列表
        playbackLeftListV.setAdapter(leftAdapter = new PBLeftAdapter(this, mPlayBackEpgs));

        leftAdapter.setOnRvListener(onRvAdapterListenerForLeft = new OnRvAdapterListenerForLeft(this));//频道列表焦点变化的监听器

        playbackWeekListV.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));//日期列表

        playbackWeekListV.setAdapter(weekAdapter = new PBWeekAdapter(this, weekDataList));
        playbackWeekListV.addItemDecoration(new PlayBackWeekDecorations());//繪製recyclerView的item間距


        weekAdapter.setOnRvListener(onRvAdapterListenerForWeek = new OnRvAdapterListenerForWeek(this));//添加焦點改變的監聽器


        playbackChalListV.setLayoutManager(new LinearLayoutManager(this));//节目列表
        playbackChalListV.setAdapter(programAdapter = new PBChalAdapter(this, programList));
        playbackChalListV.addItemDecoration(new PlayBackChalDecorations());//繪製分割綫

        programAdapter.setOnRvListener(onRvAdapterListenerForProgram = new OnRvAdapterListenerForProgram(this));//節目列表的焦點改變監聽器


        playbackVideoRootV.setOnFocusChangeListener(onVideoRootVFocusChangeL = new ORootVideoViewFocusChangeListener(this));
        playbackVideoRootV.setOnClickListener(onVideoRootVClickL = new ORootVideoViewClickListner(this));
        playbackVideoRootV.setOnKeyListener(onVideoRootVKeyL = new ORootVideoViewKeyListener(this));
        playbackVideoV.setOnKeyListener(onVideoRootVKeyL = new ORootVideoViewKeyListener(this));
    }

    private void initData() {


        curPlayStatus = EXVAL.PLAY_NOREADY;

        List<EpgObj> playBackEpgs;
        try{
            playBackEpgs = DBMgr.Ins().getPlayBackEpgs();//数据库中查询所有的节目列表
        }catch (Exception e){
            return;
        }
        if (playBackEpgs.size()<1)
           return;
        yyEpgs = new EpgParser().getYyEpgs(playBackEpgs);
        zhEpgs = new EpgParser().getZhEpgs(playBackEpgs);
        mPlayBackEpgs.clear();

        loadDataOver = true;
        if (selTitleV == null||selTitleV.getId()==R.id.playback_yy_title_v){
            mPlayBackEpgs.addAll(yyEpgs);
        }else
            mPlayBackEpgs.addAll(zhEpgs);
        if (leftAdapter != null) {
            //leftAdapter.setNeedMarket(true);
            leftAdapter.notifyDataSetChanged();
            leftAdapter.setMarketPos(0);
            //leftAdapter.execFocusVEffect(playbackYyTitleV);
        }
        focusRefrash();

        //if (mPlayBackEpgs.size()>0)
        //    DataCenter.Ins().getPlayBackDates(this, yyEpgs.get(0));
        //epgObj = yyEpgs.get(0);

    }

    public void loadChalDatas(final ChalDatsObj chalDatsObj, long loadId) {//日期数据下载完成回调
        List<ChalDatsObj.DataBean> datas = chalDatsObj.getData();
        if (datas.size()<1){
            return;
        }
        long id = epgObj.getId();
        if (loadId == id) {
            weekDataList.clear();
            for (int i = datas.size(); i > 0; i--) {//时间倒序
                weekDataList.add(datas.get(i-1));
            }

            final List<ChalDatsObj.DataBean.ProgramsBean> programs = weekDataList.get(0).getPrograms();
            boolean isFirst = false;
            if (programList.size() == 0){
                isFirst = true;
            }else
                programList.clear();

            programList.addAll(programs);

            if (isFirst) {
                programAdapter.setPlayId(programList.get(0).getVideo().getId());
                Utils.sendMsg(mHandler, MSG.FIRST_LOAD);
            }
            new Handler(getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    weekAdapter.setMarketPos(0);

                    weekAdapter.notifyDataSetChanged();
                    //weekAdapter.marketCacheForFocus(focusShowState == FOCUS_SHOW_WEEK);

                    //if (focusShowState == FOCUS_SHOW_WEEK){
                    //    changeFocusBorder(colorFB);
                    //    focusToWeekRv();
                    //}

                    //if (weekAdapter.getNeedMarket())//切换到星期的时候可能还没有刷新数据
                    //    weekAdapter.setSelection(0);
                    programAdapter.notifyDataSetChanged();
                    if (focusShowState != FOCUS_SHOW_WEEK) {
                        weekAdapter.marketCacheForFocus(false);
                        weekAdapter.setBackResource(R.drawable.play_title_sel_no_focus);
                        //colorFB.setVisible(false);
                        if (focusShowState == FOCUS_SHOW_PROGRAM){
                            resetFocusBorder();
                            programAdapter.setSelection(programAdapter.getCurSelIndex());
                        }
                    }else {
                        resetFocusBorder();
                        changeFocusBorder(colorFB);
                        weekAdapter.marketCacheForFocus(true);
                        weekAdapter.setSelection(0);
                    }

                }
            });

        }

    }

    private void focusRefrash() {
        if (focusShowState == FOCUS_SHOW_TITLE){
            changeFocusBorder(colorFB);
            focusToTitle();
        }else if (focusShowState == FOCUS_SHOW_LEFT){
            changeFocusBorder(drawableFB);
            focusToLeftRv();
        }else if (focusShowState == FOCUS_SHOW_PROGRAM){
            changeFocusBorder(drawableFB);
            focusToProgramRV();
        }else if (focusShowState == FOCUS_SHOW_WEEK){
            changeFocusBorder(colorFB);
            focusToWeekRv();
        }
    }

    public void loadEpgs(final List<EpgObj> epgObjs, int epgNum) {//频道信息加载完成回调
        if (epgNum < 1) return;

        loadDataOver = true;
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (mPlayBackEpgs == null||mPlayBackEpgs.size()<1){
                    yyEpgs = new EpgParser().getYyEpgs(epgObjs);
                    zhEpgs = new EpgParser().getZhEpgs(epgObjs);

                    if (selTitleV == null||selTitleV.getId()==R.id.playback_yy_title_v){
                        mPlayBackEpgs.addAll(yyEpgs);
                    }else
                        mPlayBackEpgs.addAll(zhEpgs);
                    leftAdapter.notifyDataSetChanged();
                    leftAdapter.setNeedMarket(true);
                    loadDataOver = true;
                    if (mPlayBackEpgs.size()>0)
                        DataCenter.Ins().getPlayBackDates(PlayBackMainAct.this, mPlayBackEpgs.get(0));
                    leftAdapter.setMarketPos(0);
                    epgObj = mPlayBackEpgs.get(0);
                    focusShowState = FOCUS_SHOW_TITLE;
                    focusRefrash();
                }
            }
        });

    }


    private static class MOnDataListener implements OnDataListener {//

        private PlayBackMainAct hostCls;

        public MOnDataListener(PlayBackMainAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            hostCls = null;
        }

        @Override
        public void onLoadInitDatas(final SparseArray<HomeMovie> homeMovies) {

        }

        @Override
        public void onNetState(int netType, boolean isConnected) {
            if (hostCls == null) {
                return;
            }
            if (isConnected) {
                hostCls.playbackNetV.setVisibility(View.VISIBLE);
                switch (netType) {
                    case ConnectivityManager.TYPE_WIFI:
                        hostCls.playbackNetV.setBackgroundResource(R.drawable.wifi_ok);
                        break;
                    default:
                        hostCls.playbackNetV.setBackgroundResource(R.drawable.eth_f);
                        break;
                }
            } else {
                hostCls.playbackNetV.setVisibility(View.GONE);
            }
        }

        @Override
        public void onTimeDate(final String time) {
            if (hostCls == null) {
                return;
            }
            hostCls.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hostCls.playbackTimeV.setText(time);
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
        public void onLogin(String loginStr, boolean dialogCancelable) {//登录回调
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

    private static class MOnPlayListener implements OnPlayListener {//播放監聽器

        private PlayBackMainAct hostCls;

        public MOnPlayListener(PlayBackMainAct hostCls) {
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
                        hostCls.playbackVideoV.setVideoPath(url);
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
                            if (hostCls.playBottomCtrlRootV != null && hostCls.playBottomCtrlRootV.isShown() &&
                                    hostCls.playCtrlRootV.isShown()) {
                                hostCls.playCtrlSpeedV.setText(speedStr);
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

    /**- - - - - - - - - - - - - - - - - - 粤语和综合的监听器-->*/
    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private PlayBackMainAct hostCls;

        public MOnFocusChangeListener(PlayBackMainAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onFocusChange(final View v, boolean hasFocus) {
            MLog.d(TAG, String.format("onFocusChange v=%s hasFocus=%b", v, hasFocus));
            if (hostCls == null||hostCls.isFullScreen) {
                return;
            }

            if (hasFocus) {
                hostCls.selTitleV = (TextView) v;
                v.setActivated(false);

                int vId = v.getId();

                hostCls.focusToTitle();

                if (hostCls.loadDataOver) {
                    hostCls.leftAdapter.setNeedMarket(true);
                    hostCls.leftAdapter.setMarketPos(0);
                    //hostCls.leftAdapter.changeEpgList(vId);
                    hostCls.mPlayBackEpgs.clear();

                    if (vId == R.id.playback_yy_title_v){
                        hostCls.mPlayBackEpgs.addAll(hostCls.yyEpgs);
                    }else if (vId == R.id.playback_zh_title_v) {
                        hostCls.mPlayBackEpgs.addAll(hostCls.zhEpgs);
                    }
                    if (hostCls.mPlayBackEpgs.size()>0) {
                        DataCenter.Ins().getPlayBackDates(hostCls, hostCls.mPlayBackEpgs.get(0));//粤语焦点改变
                        hostCls.epgObj = hostCls.mPlayBackEpgs.get(0);
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            hostCls.leftAdapter.notifyDataSetChanged();
                            //hostCls.leftAdapter.setMarketPos(0);
                        }
                    });

                }
            }
        }


    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private PlayBackMainAct hostCls;

        public MOnKeyListener(PlayBackMainAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(final View v, int keyCode, KeyEvent event) {
            MLog.d(TAG, String.format("onKey v=%s event=%s", v, event));
            if (this.hostCls == null || (event.getAction() == KeyEvent.ACTION_UP)||hostCls.isFullScreen) {
                return false;
            }

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_DOWN: {

                    if (hostCls.mPlayBackEpgs.size()>0){
                        hostCls.focusToLeftRv();//焦点移到频道上
                    }

                    // new Handler(Looper.getMainLooper()).post(new Runnable() {
                    //     @Override
                    //     public void run() {
                    //         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    //             v.setBackground(hostCls.getResources().getDrawable(R.drawable.play_title_sel_no_focus));
                    //         }
                    //     }
                    // });

                    // Utils.noFocus(v == hostCls.playbackYyTitleV ? hostCls.playbackZhTitleV : hostCls.playbackYyTitleV);
                    // Utils.noFocus(v);

                    // v.setActivated(true);

                    /*
                    if (hostCls.leftAdapter.getItemCount() > 0) {
                        hostCls.resetFocusBorder();
                        hostCls.selTitleV = (TextView) v;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    v.setBackground(hostCls.getResources().getDrawable(R.drawable.play_title_sel_no_focus));
                                }
                            }
                        });

                        Utils.noFocus(v == hostCls.playbackYyTitleV ? hostCls.playbackZhTitleV : hostCls.playbackYyTitleV);
                        Utils.noFocus(v);

                        v.setActivated(true);
                        hostCls.leftAdapter.unLock();
                        hostCls.colorFB.setVisible(false);
                        hostCls.changeFocusBorder(hostCls.drawableFB);
                        hostCls.leftAdapter.marketCacheForFocus(true);
                    }
                    */
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

        private PlayBackMainAct hostCls;

        public MOnClickListener(PlayBackMainAct hostCls) {
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
/**<--粤语和综合的监听器- - - - - - - - - - - - - - - - - - - - - - - - - - */

    private void focusToLeftRv() {//焦点切换到频道

        if (focusShowState == FOCUS_SHOW_TITLE){
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        selTitleV.setBackground(getResources().getDrawable(R.drawable.play_title_sel_no_focus));
                    }
                }
            });

            Utils.noFocus(selTitleV == playbackYyTitleV ? playbackZhTitleV : playbackYyTitleV);
            Utils.noFocus(selTitleV);

            selTitleV.setActivated(true);
        }else if (focusShowState == FOCUS_SHOW_SMALL_VIDEO){
            playbackVideoRootV.setSelected(false);
        }

        if (leftAdapter.getItemCount() > 0) {


            focusBorderVisible(false);
            resetFocusBorder();
            weekAdapter.setBackResource(R.drawable.play_title_sel_no_focus);
            leftAdapter.unLock();
            colorFB.setVisible(false);
            changeFocusBorder(drawableFB);
            leftAdapter.marketCacheForFocus(true);



            focusShowState = FOCUS_SHOW_LEFT;

        }
    }


    private void focusToWeekRv() {//焦点切换到星期

        if (weekDataList.size()>0) {
            if (focusShowState != FOCUS_SHOW_PROGRAM) {
                leftAdapter.setNeedMarket(false);
                leftAdapter.marketCacheForFocus(false);
                leftAdapter.lock();

            }else if (focusShowState == FOCUS_SHOW_SMALL_VIDEO){
                playbackVideoRootV.setSelected(false);
            }else {

                //programAdapter.setNeedMarket(false);
                programAdapter.marketCacheForFocus(false);
            }


            focusBorderVisible(false);
            resetFocusBorder();

            //focusBorderVisible(false);
            changeFocusBorder(colorFB);

            weekAdapter.setNeedMarket(true);//频道向右切换时日期可获取焦点
            new Handler().post(new Runnable() {
                @Override
                 public void run() {
                    weekAdapter.marketCacheForFocus(true);
                    weekAdapter.setSelection(weekAdapter.getCurSelIndex());
                }
            });
            focusShowState = FOCUS_SHOW_WEEK;
        }
    }


    private void focusToSmallVideoV() {

        //weekAdapter.setMarketPos(0);
        weekAdapter.setBackResource(R.drawable.play_title_sel_no_focus);
        weekAdapter.setNeedMarket(false);
        weekAdapter.marketCacheForFocus(false);
//
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                resetFocusBorder();
                changeFocusBorder(drawableFB);

                playbackVideoRootV.setSelected(true);

                focusBorderForV(playbackVideoRootV, exW, exH, freeX, freeY );
            }
        },2);

        focusShowState = FOCUS_SHOW_SMALL_VIDEO;

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void focusToProgramRV() {//焦点移动节目
        if (focusShowState == FOCUS_SHOW_WEEK) {
            weekAdapter.setNeedMarket(false);
            weekAdapter.setBackResource(R.drawable.play_title_sel_no_focus);
            weekAdapter.marketCacheForFocus(false);
        }
        colorFB.setVisible(false);
        resetFocusBorder();
        changeFocusBorder(drawableFB);
        programAdapter.setSelection(0);
        focusShowState = FOCUS_SHOW_PROGRAM;
    }
    private void focusToTitle() {
        //resetFocusBorder();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                selTitleV.setBackground(getResources().getDrawable(R.drawable.playback_title_bg));
            }
        });
        selTitleV.setSelected(true);
        focusBorderForV(selTitleV, 1.3f, 1.3f);
        focusShowState = FOCUS_SHOW_TITLE;

    }
    /** - - - - - - - - - - - - - - - - - - - 频道的item焦点改变的监听器-->*/
    private static class OnRvAdapterListenerForLeft implements OnRvAdapterListener {//频道焦點改變監聽器

        private PlayBackMainAct hostCls;

        public OnRvAdapterListenerForLeft(PlayBackMainAct hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void onItemSelected(View view, int position) {
            if (this.hostCls == null || this.hostCls.isFullScreen) {
                return;
            }
            //EpgObj epgObj = hostCls.leftAdapter.getDataForItem(position);// -->

            EpgObj epgObj = hostCls.mPlayBackEpgs.get(position);//

            if (hostCls.epgObj == null || !hostCls.epgObj.equals(epgObj)) {//如果当前显示的频道信息和当前切换的频道信息不同话更新星期信息
                //hostCls.changeRelateDatasForEpg(
                //        //hostCls.leftAdapter.getDataForItem(position));
                //        hostCls.mPlayBackEpgs.get(position));
            }
            if (epgObj.equals(hostCls.epgObj)&&hostCls.programList.size()>0) {//如果是回到保存的焦点的话不进行数据加载
                return;
            }
            hostCls.epgObj = epgObj;
            DataCenter.Ins().getPlayBackDates(hostCls, epgObj);//频道切换
        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            if (this.hostCls == null || this.hostCls.isFullScreen) {
                return;
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                    if (hostCls.weekDataList.size()>0) {

                        hostCls.focusToWeekRv();

                    }
                    break;
                }
                case KeyEvent.KEYCODE_DPAD_UP: {
                    if (position == 0) {
                        this.hostCls.leftAdapter.lock();
                        this.hostCls.resetFocusBorder();
                        this.hostCls.leftAdapter.marketCacheForFocus(false);
                        this.hostCls.focusBorderVisible(false);
                        this.hostCls.changeFocusBorder(this.hostCls.colorFB);
                        this.hostCls.playbackYyTitleV.post(new Runnable() {
                            @Override
                            public void run() {
                                Utils.focusV(hostCls.selTitleV, true);

                                Utils.focusV(hostCls.selTitleV == hostCls.playbackYyTitleV ? hostCls.playbackZhTitleV : hostCls.playbackYyTitleV, false);
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
/** <--频道的item焦点改变的监听器- - - - - - - - - - - - - - - - - - - -*/

    /** - - - - - - - - - - - - - - - - - - - 节目的item焦点改变的监听器-->*/
    private static class OnRvAdapterListenerForProgram implements OnRvAdapterListener {//節目列表焦點改變監聽器

        private PlayBackMainAct hostCls;

        public OnRvAdapterListenerForProgram(PlayBackMainAct hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void onItemSelected(View view, int position) {

        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            if (this.hostCls == null||hostCls.isFullScreen) {
                return;
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT: {
                    hostCls.focusToWeekRv();

                    break;
                }
                case KeyEvent.KEYCODE_DPAD_UP: {
                    if (position == 0) {

                        hostCls.focusToWeekRv();

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
            if (hostCls.videoRootVLayoutParams == null) {
                hostCls.videoRootVLayoutParams = (RelativeLayout.LayoutParams)
                        hostCls.playbackVideoRootV.getLayoutParams();
            }
            boolean isSmallChal = false;

            ChalDatsObj.DataBean.ProgramsBean programsBean = hostCls.programList.get(position);
            if (programsBean != null && (hostCls.curProgramData == null ||
                    !(isSmallChal = programsBean.equals(hostCls.curProgramData)))) {
                hostCls.curProgramData = programsBean;
                hostCls.loadMoiveInfo(hostCls.curProgramData);
                Utils.sendMsg(hostCls.mHandler, MSG.PLAY_POS);
            }

            if (isSmallChal) {
                hostCls.changeVideoRootVScreenMod(true);
            }

            hostCls.programAdapter.notifyDataSetChanged();
            hostCls.programAdapter.setSelection(position);
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }

        @Override
        public void release() {
            this.hostCls = null;
        }
    }
/** <--节目的item焦点改变的监听器- - - - - - - - - - - - - - - - - - - -*/

    /** - - - - - - - - - - - - - - - - - - - 星期的item焦点改变的监听器-->*/
    private static class OnRvAdapterListenerForWeek implements OnRvAdapterListener {//星期焦點改變監聽器

        private PlayBackMainAct hostCls;

        public OnRvAdapterListenerForWeek(PlayBackMainAct hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void onItemSelected(View view, int position) {
            if (hostCls == null) {
                return;
            }
            //hostCls.changeFocusBorder(hostCls.colorFB);
            //hostCls.weekAdapter.setNeedMarket(true);
            hostCls.weekAdapter.setBackResource(R.drawable.playback_title_bg);//焦点在星期上的时候的背景
            //hostCls.programAdapter.setNeedMarket(false);

            if (position>=0&&position<hostCls.weekDataList.size()) {
                hostCls.programAdapter.setDatas(hostCls.weekDataList.get(position).getPrograms());//切換星期的時候適配新數據 倒序適配數據

            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            if (hostCls == null||hostCls.isFullScreen) {
                return;
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP: {

                    hostCls.focusToSmallVideoV();

                    break;
                }
                case KeyEvent.KEYCODE_DPAD_DOWN: {

                    if (hostCls.programList.size() > 0) {
                        hostCls.focusToProgramRV();
                    }
                    break;
                }
                case KeyEvent.KEYCODE_DPAD_LEFT: {
                    if (position == 0) {
                        hostCls.focusToLeftRv();

                    }
                    break;
                }
                case KeyEvent.KEYCODE_BACK: {
                    if (!hostCls.isFullScreen) {
                        hostCls.onBackPressed();
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


/** <--星期的item焦点改变的监听器- - - - - - - - - - - - - - - - - - - -*/


/** - - - - - - - - - - - - - - - - - - - 小视频框的监听器 ------> */
private static class ORootVideoViewFocusChangeListener implements View.OnFocusChangeListener {

    private final PlayBackMainAct hostCls;

    public ORootVideoViewFocusChangeListener(PlayBackMainAct act) {
        hostCls = act;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        //if (hasFocus){
        //    v.setSelected(true);
        //}
    }
}


    private static class ORootVideoViewClickListner implements View.OnClickListener {

        private final PlayBackMainAct hostCls;

        public ORootVideoViewClickListner(PlayBackMainAct act) {
            hostCls = act;
        }

        @Override
        public void onClick(View v) {
            hostCls.isFullScreen = true;
            hostCls.changeVideoRootVScreenMod(hostCls.isFullScreen);
        }
    }
    private static class ORootVideoViewKeyListener implements View.OnKeyListener {

        private final PlayBackMainAct hostCls;

        public ORootVideoViewKeyListener(PlayBackMainAct act) {
            hostCls = act;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (hostCls == null||hostCls.isFullScreen)
                return false;
            switch (keyCode){
                case KeyEvent.KEYCODE_DPAD_UP:
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    hostCls.focusToLeftRv();//焦点移动到频道
                    //hostCls.leftAdapter.unLock();
                    //hostCls.leftAdapter.setNeedMarket(true);
                    //hostCls.weekAdapter.marketCacheForFocus(false);
                    //hostCls.resetFocusBorder();
                    //hostCls.changeFocusBorder(hostCls.drawableFB);
                    //hostCls.leftAdapter.marketCacheForFocus(true);
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    hostCls.focusToWeekRv();
                    break;
            }
            return false;
        }
    }



    /** <------- 小视频框的监听器 - - - - - - - - - - - - - - - - - -  */

    private void changeVideoRootVScreenMod(boolean isFullScreen) {//全屏切换
        this.isFullScreen = isFullScreen;
        if (isFullScreen) {
            resetFocusBorder();
            focusBorderVisible(false);
            loadWindow.setLoadImgVisibity(View.VISIBLE);
            playbackVideoRootV.setLayoutParams(videoRootVScreenLayoutParams);
            playbackVideoRootV.post(new Runnable() {
                @Override
                public void run() {
                    Utils.focusV(playbackVideoRootV, true);
                    playbackVideoRootV.setDescendantFocusability(
                            ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                }
            });
        } else {
            playBottomCtrlRootV.setVisibility(View.GONE);
            Utils.noFocus(playbackVideoRootV);
            playbackVideoRootV.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            playbackVideoRootV.setLayoutParams(videoRootVLayoutParams);
        }
        playbackVideoRootV.requestLayout();
        if (loadWindow.isShown()) {
            showLoadWindow();
        }
        loadMoiveInfo(curProgramData);
    }

    public void changeRelateDatasForEpg(EpgObj epgObj) {
        this.epgObj = epgObj;
        MLog.d(TAG, "changeRelateDatasForEpg " + epgObj);
        if (this.epgObj != null) {
            List<String> dates = this.epgObj.getDates();
            MLog.d(TAG, "changeRelateDatasForEpg dates " + dates);

            if (weekAdapter.getItemCount() > 0) {
                weekAdapter.clearCacheFocus();
            }
            if (weekAdapter.getCurSelIndex() != 0) {
                weekAdapter.setMarketPos(0);
            }
            weekAdapter.setNeedMarket(true);
            //weekAdapter.setDatas(dates);

            if (weekAdapter.getItemCount() > 0) {
                programAdapter.setNeedMarket(true);
                programAdapter.setMarketPos(0);

                //programAdapter.setDatas(DBMgr.Ins().getPlayBackChals(epgObj, dates.get(0)));//設置節目數據，第一次適配數據
                DataCenter.Ins().getPlayBackDates(this, epgObj);
            }
        }
    }
    private static class MHandler extends Handler {

        private PlayBackMainAct hostCls;

        public MHandler(PlayBackMainAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            hostCls = null;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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
                    hostCls.playBottomCtrlRootV.setVisibility(View.VISIBLE);
                    hostCls.playCtrlRootV.setVisibility(View.VISIBLE);
                    hostCls.execNetSpeed();
                    break;
                case MSG.MSG_HIDE_SEEK_BAR:
                    hostCls.stopUpdateSeek();
                    hostCls.playBottomCtrlRootV.setVisibility(View.GONE);
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
                    if (hostCls.curProgramData != null) {

                        ChalDatsObj.DataBean.ProgramsBean.VideoBean video = hostCls.curProgramData.getVideo();

                        String filmId = video.getFilmId();

                        //hostCls.playbackVideoV.setVideoUrl();

                        DataCenter.Ins().sendPlayBackForceTVHttpReq(
                                new ChalObj((long) 0, 0, 0,
                                        hostCls.mPlayBackEpgs.get(hostCls.leftAdapter.getCurSelIndex()).getEpgId(),
                                        null,null, null,filmId,null));

                        hostCls.playbackVideoV.post(new Runnable() {
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
                        if (hostCls.playBottomCtrlRootV != null && hostCls.playBottomCtrlRootV.isShown() &&
                                hostCls.playCtrlRootV.isShown()) {
                            hostCls.playCtrlSpeedV.setText(speedStr);
                        }
                        if (hostCls.loadWindow != null && hostCls.loadWindow.isShown()) {
                            hostCls.loadWindow.updateLoadSpeed(speedStr);
                        }
                    }
                    break;
                }
                case MSG.MSG_STATUS_HIDE:
                    //hostCls.operStatus.setVisibility(View.GONE);
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
                    DataCenter.Ins().getPlayBackEpgs(hostCls);

                    if (hostCls.mPlayBackEpgs.size()>0) {
                        hostCls.epgObj = hostCls.mPlayBackEpgs.get(0);
                        DataCenter.Ins().getPlayBackDates(hostCls, hostCls.yyEpgs.get(0));
                    }
                    break;
                case MSG.SHOW_PAY: {
                    hostCls.payDialog = PayDialog.showPay(hostCls, this);
                    break;
                }
                case MSG.EXIT_APP: {
                    hostCls.finish();
                    break;
                }
                case MSG.FIRST_LOAD: {
                    if (hostCls.programList.size()>0) {

                        /** 播放视频*/
                        if (hostCls.videoRootVLayoutParams == null) {
                            hostCls.videoRootVLayoutParams = (RelativeLayout.LayoutParams)
                                    hostCls.playbackVideoRootV.getLayoutParams();
                        }

                        ChalDatsObj.DataBean.ProgramsBean programsBean = hostCls.programList.get(0);
                        if (programsBean != null/* && (hostCls.curProgramData == null ||
                                !(isSmallChal = programsBean.equals(hostCls.curProgramData)))*/) {
                            hostCls.curProgramData = programsBean;
                            hostCls.loadMoiveInfo(hostCls.curProgramData);
                            Utils.sendMsg(hostCls.mHandler, MSG.PLAY_POS);
                        }
                         hostCls.loadWindow.setLoadImgVisibity(View.GONE);

                        hostCls.weekAdapter.marketCacheForFocus(false);



                    }

                    break;
                }
            }
        }
    }


    private void loadMoiveInfo(ChalDatsObj.DataBean.ProgramsBean chalObj) {//加载影片信息
        playTipsV.setVisibility(View.GONE);
        if (chalObj != null) {
            playCtrlEpgV.setText(String.format("%s  %s", chalObj.getBeginTime(), chalObj.getName()));
            playbackCurJmV.setText(chalObj.getName());
        }
        if (epgObj != null) {
            playbackCurPdV.setText(epgObj.getName());
        }
        if (isFullScreen) {
            showSeekBar();
            hideSeekBar(5000);
        }
    }
    private void loadMoiveInfo(int moviePos) {
        this.playTipsV.setVisibility(View.GONE);
        curProgramData = programList.get(moviePos);
        if (curProgramData != null) {
            programAdapter.setCurSelIndex(moviePos);
            playCtrlEpgV.setText(String.format("%s  %s",
                    curProgramData.getBeginTime(), curProgramData.getName()));
            playbackCurJmV.setText(curProgramData.getName());
        }
        if (this.epgObj != null) {
            playbackCurPdV.setText(epgObj.getName());
        }
        if (this.isFullScreen) {
            showSeekBar();
            hideSeekBar(5000);
        }
    }

    private void showLoadWindow() {//
        loadWindow.dismiss();
        loadWindow.setFullScreen(isFullScreen);
        loadWindow.show(playbackVideoV);
        loadWindow.updateLoadSpeed(playCtrlSpeedV.getText().toString());
        execNetSpeed();
    }

    private void hideSeekBar(int timeout) {//隐藏播放进度条
        Utils.sendMsg(this.mHandler, MSG.MSG_HIDE_SEEK_BAR, timeout);
    }

    private void showSeekBar() {//显示播放进度条
        hideSeekBar(0);
        startUpdateSeek(0);
        Utils.sendMsg(this.mHandler, MSG.MSG_SHOW_SEEK_BAR);
        hideSeekBar(5000);
    }
    private void startUpdateSeek(int delay) {
        Utils.sendMsg(this.mHandler, MSG.MSG_UPDATE_PROGRESS, delay);
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

        private PlayBackMainAct hostCls;
        private boolean netSpeedRunning;

        public GetNetSpeedTask(PlayBackMainAct hostCls) {
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


    /** - - - - - - - - - - - - - - - - - - -  - - - -videoView 监听器 ------>*/

    private static class MOnPreparedListener implements MediaPlayer.OnPreparedListener {

        private PlayBackMainAct hostCls;

        public MOnPreparedListener(PlayBackMainAct hostCls) {
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
            this.hostCls.playbackVideoV.start();
            final int position = DBMgr.Ins().queryProgremPlayerInfo(new ProgremPlayerInfo(hostCls.curProgramData.getName(), hostCls.curProgramData.getVideo().getFilmId(), 0));
            if (position>0){
                final AlertDialog alertDialog = new AlertDialog.Builder(hostCls)
                        .setTitle("提示")
                        .setMessage("检测到你播放过该视频，是否跳到上回播放的地方？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                hostCls.playbackVideoV.seekTo(position);

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .create();
                alertDialog.show();

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        alertDialog.cancel();
                    }
                }, 3000);
            }
            this.hostCls.curPlayStatus = EXVAL.PLAY_PREPARED;
            this.hostCls.totaltime = this.hostCls.playbackVideoV.getDuration();
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

        private PlayBackMainAct hostCls;

        public MOnCompletionListener(PlayBackMainAct hostCls) {
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

        private PlayBackMainAct hostCls;

        public MOnErrorListener(PlayBackMainAct hostCls) {
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

        private PlayBackMainAct hostCls;

        public MOnInfoListener(PlayBackMainAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {

            MLog.d(TAG, "onInfo " + what);
            if (this.hostCls == null||hostCls.isFullScreen) {
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

        private PlayBackMainAct hostCls;

        public MOnSeekCompleteListener(PlayBackMainAct hostCls) {
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
                    //if (this.hostCls.operStatus != null && this.hostCls.operStatus.isShown()) {
                    //    this.hostCls.operStatus.setVisibility(View.GONE);
                    //}
                    if (this.hostCls.playbackVideoV != null) {
                        this.hostCls.playbackVideoV.start();
                    }
                }
            }
        }
    }

    /**
     * video监听器
     */
    private static class MVRootKeyListener implements View.OnKeyListener {

        private PlayBackMainAct hostCls;

        public MVRootKeyListener(PlayBackMainAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (this.hostCls == null||hostCls.isFullScreen) {
                return false;
            }

            if (event.getAction() == KeyEvent.ACTION_UP) {
                if ((KeyEvent.KEYCODE_DPAD_LEFT == keyCode)
                        || (KeyEvent.KEYCODE_DPAD_RIGHT == keyCode)) {
                    this.hostCls.hideSeekBar(5000);
                } else if ((KeyEvent.KEYCODE_DPAD_UP == keyCode)
                        || (KeyEvent.KEYCODE_DPAD_DOWN == keyCode)) {
                    if (this.hostCls.playTipsV.getVisibility() != View.VISIBLE) {
                        Utils.sendMsg(this.hostCls.mHandler, MSG.PLAY_POS);
                    }
                }
            } else {
                if (KeyEvent.KEYCODE_BACK == keyCode) {
                    if (!this.hostCls.execDestroyed) {
                        if (this.hostCls.playBottomCtrlRootV != null &&
                                this.hostCls.playBottomCtrlRootV.isShown()) {
                            if (this.hostCls.playCtrlRootV.isShown()) {
                                this.hostCls.hideSeekBar(0);
                            } else {
                                this.hostCls.hideProgress();
                            }
                        } else if (this.hostCls.isFullScreen) {
                            this.hostCls.changeVideoRootVScreenMod(false);
                            this.hostCls.playbackChalListV.post(new Runnable() {
                                @Override
                                public void run() {
                                    hostCls.programAdapter.setSelection(
                                            hostCls.programAdapter.getCurSelIndex());
                                    hostCls.playbackChalListV.setCacheHasFocus(true);
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
                        if (hostCls.curPlayStatus == EXVAL.PLAY_PREPARED) {
                            hostCls.showSeekBar();
                            hostCls.hideSeekBar(5000);
                            if (hostCls.playbackVideoV.isPlaying()) {
                                hostCls.showStatus(R.drawable.pause_status);
                                hostCls.playbackVideoV.pause();
                            } else {
                                //if (MVRootKeyListener.this.hostCls.operStatus.isShown()) {
                                //    MVRootKeyListener.this.hostCls.operStatus.setVisibility(View.GONE);
                                //}
                                hostCls.playbackVideoV.start();
                            }
                        }
                        break;
                    case KeyEvent.KEYCODE_MEDIA_REWIND:
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        if (hostCls.curPlayStatus == EXVAL.PLAY_PREPARED) {
                            hostCls.isSeekIng = true;
                            hostCls.stopUpdateSeek();
                            if (!hostCls.playBottomCtrlRootV.isShown()) {
                                hostCls.seekTime = hostCls.getCurrentPosition();
                                hostCls.showSeekBar();
                            } else if (hostCls.playCtrlRootV.isShown()) {
                                hostCls.hideSeekBar(5000);
                                hostCls.seekTime -=
                                        hostCls.SEEK_ONCE;
                                if (hostCls.seekTime < 0) {
                                    hostCls.seekTime = 0;
                                }
                                float curtimetmp = (float) hostCls.seekTime / 1000;
                                float totaltimetmp = (float) hostCls.totaltime / 1000;
                                if (totaltimetmp != 0) {
                                    int progress = (int) (curtimetmp * 100 / totaltimetmp);
                                    hostCls.seekbar.setProgress(progress);
                                    hostCls.seekbar.setCurProgress(progress);
                                    String curTimeStr = hostCls.
                                            secToTime((int) curtimetmp);
                                    hostCls.seekbar.setNumText(curTimeStr);
                                    hostCls.playCtrlCurTimeV.setText(curTimeStr);
                                }
                                hostCls.sendSeekMsg();
                            }
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                    case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                        if (hostCls.curPlayStatus == EXVAL.PLAY_PREPARED) {
                            hostCls.isSeekIng = true;
                            hostCls.stopUpdateSeek();
                            if (!hostCls.playBottomCtrlRootV.isShown()) {
                                hostCls.seekTime = hostCls.getCurrentPosition();
                                hostCls.showSeekBar();
                            } else if (hostCls.playCtrlRootV.isShown()) {
                                hostCls.hideSeekBar(5000);
                                hostCls.seekTime += hostCls.SEEK_ONCE;
                                if (hostCls.seekTime > hostCls.totaltime) {
                                    hostCls.seekTime = hostCls.totaltime;
                                }
                                float curtimetmp = (float) hostCls.seekTime / 1000;
                                float totaltimetmp = (float) hostCls.totaltime / 1000;
                                if (totaltimetmp != 0) {
                                    int progress = (int) (curtimetmp * 100 / (float) totaltimetmp);
                                    hostCls.seekbar.setProgress(progress);
                                    hostCls.seekbar.setCurProgress(progress);
                                    String curTimeStr = hostCls.secToTime((int) curtimetmp);
                                    hostCls.seekbar.setNumText(curTimeStr);
                                    hostCls.playCtrlCurTimeV.setText(curTimeStr);
                                }
                                hostCls.sendSeekMsg();
                            }
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP: {
                        hostCls.playNext(false);
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_DOWN: {
                        hostCls.playPrev(false);
                        break;
                    }
                }
            }

            return false;
        }
    }
    /** <-------videoView 监听器 - - - - - - - - - - - - - - - - - -  - - - - */



    private void hideTips() {
        if (tipsDialog != null) {
            tipsDialog.dismiss();
            tipsDialog = null;
        }
    }

    private void showTips(String msgStr, boolean dialogCancelable) {
        hideTips();
        if (TextUtils.isEmpty(msgStr))
            return;
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
        tipsDialog = new AlertDialog.Builder(PlayBackMainAct.this, R.style.TipsDialog)
                .setView(rootV)
                .setCancelable(dialogCancelable)
                .create();
        tipsDialog.show();
    }
    private void seekByProgressBar() {
        int dest = seekbar.getCurPorgress();
        int pos = totaltime * (dest + 1) / 100;
        // check for small stream while seeking
        int pos_check = totaltime * (dest + 1) - pos * 100;
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
        playbackVideoV.seekTo(pos);

        showLoadWindow();
    }

    private void stopUpdateSeek() {
        Utils.removeMsg(this.mHandler, MSG.MSG_UPDATE_PROGRESS);
    }

    private int getCurrentPosition() {
        if (null != playbackVideoV) {
            return playbackVideoV.getCurrentPosition();
        }
        return 0;
    }

    private void sendSeekMsg() {
        Utils.sendMsg(mHandler, MSG.MSG_SEEK, 300);
    }

    private void updateProgressBar(boolean focusUpdate) {
        if (this.playCtrlRootV.isShown() || focusUpdate) {

            this.curtime = getCurrentPosition();

            Log.d(TAG, "curtime = " + this.curtime);

            playCtrlTotalTimeV.setText(secToTime(this.totaltime / 1000));
            if (this.totaltime != 0) {
                int curtimetmp = this.curtime / 1000;
                int totaltimetmp = this.totaltime / 1000;
                if (totaltimetmp != 0) {
                    int progress = curtimetmp * 100 / totaltimetmp;
                    seekbar.setProgress(progress);
                    seekbar.setSecondaryProgress(progress);
                    seekbar.setCurProgress(progress);
                    String curTimeStr = secToTime(this.curtime / 1000);
                    seekbar.setNumText(curTimeStr);
                    playCtrlCurTimeV.setText(curTimeStr);
                }
            } else {
                seekbar.setProgress(0);
                seekbar.setSecondaryProgress(0);
                seekbar.setCurProgress(0);
                String curTimeStr = secToTime(0 / 1000);
                seekbar.setNumText(curTimeStr);
                playCtrlCurTimeV.setText(curTimeStr);
            }
        }
    }

    private void playNext(boolean autoPlay) {
        int moviePos = programAdapter.getCurSelIndex();
        int movieNum = programAdapter.getItemCount();
        moviePos++;
        if (moviePos < movieNum) {
            loadMoiveInfo(moviePos);
            if (autoPlay) {
                Utils.sendMsg(this.mHandler, MSG.PLAY_POS);
            }
        } else {
            playbackVideoV.stopPlayback();
            playTipsV.setVisibility(View.VISIBLE);
            hideProgress();
            showSeekBar();
            hideSeekBar(0);
            loadWindow.dismiss();
        }
    }


    private void playPrev(boolean autoPlay) {
        int moviePos = programAdapter.getCurSelIndex();
        moviePos--;
        if (moviePos >= 0) {
            loadMoiveInfo(moviePos);
            if (autoPlay) {
                Utils.sendMsg(mHandler, MSG.PLAY_POS);
            }
        }
    }
    private void showStatus(int bgResId) {
        //if (!this.operStatus.isShown()) {
        //    this.operStatus.setVisibility(View.VISIBLE);
        //}
        //this.operStatus.setBackgroundResource(bgResId);
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

    private void hideProgress() {
        playBottomCtrlRootV.setVisibility(View.GONE);
        if (this.playCtrlRootV.isShown()) {
            if (!this.loadWindow.isShown()) {
                this.stopNetSpeed();
            }
            this.hideSeekBar(0);
        }
        this.playCtrlRootV.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long l = System.currentTimeMillis();
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (isFullScreen) {
                if (l - checkBackTime > CHECK_BREAK_VALID_TIME && isFullScreen) {
                    checkBackTime = l;
                    toastMessage(getString(R.string.again_back_playback));
                    return false;
                }else{
                    loadWindow.setLoadImgVisibity(View.GONE);
                    changeVideoRootVScreenMod(false);
                    //focusToProgramRV();
                    drawableFB.setVisible(true);
                    Utils.focusV(playbackVideoRootV,true);
                    //playBottomCtrlRootV.setVisibility(View.VISIBLE);
                    focusToSmallVideoV();
                    //focusRefrash();
                    return false;
                }
            }else return super.onKeyDown(keyCode,event);
        }
        return super.onKeyDown(keyCode, event);
    }
}
