package com.allcn.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.interfaces.OnDataListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.utils.GlideCatchUtil;
import com.allcn.utils.MSG;
import com.allcn.views.FrameAnimation;
import com.allcn.views.PayDialog;
import com.allcn.views.focus.FocusBorder;
import com.datas.HomeMovie;
import com.datas.LiveChalObj;
import com.db.cls.DBMgr;
import com.mast.lib.parsers.MarqueeParser;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.ShareAdapter;
import com.mast.lib.utils.Utils;
import com.umeng.commonsdk.debug.D;

import java.io.File;
import java.util.Timer;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    private static final String TAG = "AllCN/Main";
    @BindView(R.id.home_main_bg_v)
    ImageView homeMainBgV;
    @BindView(R.id.home_ver_v)
    TextView homeVerV;
    @BindView(R.id.home_net_v)
    ImageView homeNetV;
    @BindView(R.id.home_time_v)
    TextView homeTimeV;
    @BindView(R.id.home_img_big_v)
    ImageView homeImgBigV;
    @BindView(R.id.home_text_img_v)
    ImageView homeTextImgV;
    @BindView(R.id.home_set_mgr_img_v)
    ImageView homeSetMgrImgV;
    @BindView(R.id.home_set_mgr_text_v)
    TextView homeSetMgrTextV;
    @BindView(R.id.home_set_mgr_root_v)
    LinearLayout homeSetMgrRootV;
    @BindView(R.id.home_hot_topics_img_v)
    ImageView homeHotTopicsImgV;
    @BindView(R.id.home_hot_topics_text_v)
    TextView homeHotTopicsTextV;
    @BindView(R.id.home_hot_topics_root_v)
    LinearLayout homeHotTopicsRootV;
    @BindView(R.id.home_lg_yy_img_v)
    ImageView homeLgYyImgV;
    @BindView(R.id.home_lg_yy_text_v)
    TextView homeLgYyTextV;
    @BindView(R.id.home_lg_yy_root_v)
    LinearLayout homeLgYyRootV;
    @BindView(R.id.home_hot_re_img_v)
    ImageView homeHotReImgV;
    @BindView(R.id.home_hot_re_text_v)
    TextView homeHotReTextV;
    @BindView(R.id.home_hot_re_root_v)
    LinearLayout homeHotReRootV;
    @BindView(R.id.home_children_dm_img_v)
    ImageView homeChildrenDmImgV;
    @BindView(R.id.home_children_dm_text_v)
    TextView homeChildrenDmTextV;
    @BindView(R.id.home_children_dm_root_v)
    LinearLayout homeChildrenDmRootV;
    @BindView(R.id.home_ys_first_img_v)
    ImageView homeYsFirstImgV;
    @BindView(R.id.home_ys_first_text_v)
    TextView homeYsFirstTextV;
    @BindView(R.id.home_ys_first_root_v)
    LinearLayout homeYsFirstRootV;
    @BindView(R.id.home_yy_world_img_v)
    ImageView homeYyWorldImgV;
    @BindView(R.id.home_yy_world_text_v)
    TextView homeYyWorldTextV;
    @BindView(R.id.home_yy_world_root_v)
    LinearLayout homeYyWorldRootV;
    @BindView(R.id.home_playback_img_v)
    ImageView homePlaybackImgV;
    @BindView(R.id.home_playback_text_v)
    TextView homePlaybackTextV;
    @BindView(R.id.home_playback_root_v)
    LinearLayout homePlaybackRootV;
    @BindView(R.id.home_live_img_v)
    ImageView homeLiveImgV;
    @BindView(R.id.home_live_text_v)
    TextView homeLiveTextV;
    @BindView(R.id.home_live_root_v)
    LinearLayout homeLiveRootV;
    @BindView(R.id.home_start_v)
    ImageView homeStartV;

    private LinearLayout[] homeRootV = new LinearLayout[EXVAL.HOME_ICON_NUM];
    private TextView[] homeTextV = new TextView[EXVAL.HOME_ICON_NUM];
    private MOnClickListener mOnClickListener;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnKeyListener mOnKeyListener;
    private ImageView bigIconV, textV, mainBgV, netV, startV;
    private static int[] homeMainBgResIdArr = new int[]{
            R.drawable.home_live_bg,
            R.drawable.home_playback_bg,
            R.drawable.home_yy_world_bg,
            R.drawable.home_ys_first_bg,
            R.drawable.home_children_dm_bg,
            R.drawable.home_hot_re_bg,
            R.drawable.home_lg_yy_bg,
            R.drawable.home_hot_topics_bg,
            R.drawable.home_set_mgr_bg,
    };
    private static int[] homeBigIconResIdArr = new int[]{
            R.drawable.home_live_big,
            R.drawable.home_playback_big,
            R.drawable.home_yy_world_big,
            R.drawable.home_ys_first_big,
            R.drawable.home_children_dm_big,
            R.drawable.home_hot_re_big,
            R.drawable.home_lg_yy_big,
            R.drawable.home_hot_topics_big,
            R.drawable.home_set_big,
    };
    private static int[] homeTextImgResIdArr = new int[]{
            R.drawable.home_live_text_img,
            R.drawable.home_playback_text_img,
            R.drawable.home_yy_world_text_img,
            R.drawable.home_ys_first_text_img,
            R.drawable.home_children_dm_text_img,
            R.drawable.home_hot_re_text_img,
            R.drawable.home_lg_yy_text_img,
            R.drawable.home_hot_topics_text_img,
            R.drawable.home_set_text_img,
    };
    private MHandler mHandler;
    private TextView verV, timeV;
    private PayDialog payDialog;
    private MOnDataListener mOnDataListener;
    private FrameAnimation frameAnimation;
    private int[] startImgArr = new int[]{
            R.drawable.start1,
            R.drawable.start2,
            R.drawable.start3,
            R.drawable.start4,
            R.drawable.start5,
            R.drawable.start6,
            R.drawable.start7,
            R.drawable.start8,
            R.drawable.start9,
            R.drawable.start10,
            R.drawable.start11,
            R.drawable.start12,
            R.drawable.start13,
            R.drawable.start14,
            R.drawable.start15,
            R.drawable.start16,
            R.drawable.start17,
            R.drawable.start18,
            R.drawable.start19,
            R.drawable.start20,
            R.drawable.start21,
            R.drawable.start22,
            R.drawable.start23,
            R.drawable.start24,
            R.drawable.start25,
            R.drawable.start26,
            R.drawable.start27,
            R.drawable.start28,
            R.drawable.start29,
            R.drawable.start30

    };
    private Timer timer;
    private long createTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.welcomeTheme);
        createTime = System.currentTimeMillis();

        getWindow().getDecorView().setBackgroundResource(0);
        findViewById(android.R.id.content).setBackgroundResource(0);

        GlideCatchUtil.getInstance().clearCacheDiskSelf();
        GlideCatchUtil.getInstance().clearCacheMemory();

        Utils.getScreenSize(this);

        this.mHandler = new MHandler(this);

        try {
            verV.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            verV.setText("");
        }



        this.mOnClickListener = new MOnClickListener(this);
        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnKeyListener = new MOnKeyListener(this);

        this.homeRootV[0].setOnClickListener(this.mOnClickListener);
        this.homeRootV[1].setOnClickListener(this.mOnClickListener);
        this.homeRootV[2].setOnClickListener(this.mOnClickListener);
        this.homeRootV[3].setOnClickListener(this.mOnClickListener);
        this.homeRootV[4].setOnClickListener(this.mOnClickListener);
        this.homeRootV[5].setOnClickListener(this.mOnClickListener);
        this.homeRootV[6].setOnClickListener(this.mOnClickListener);
        this.homeRootV[7].setOnClickListener(this.mOnClickListener);
        this.homeRootV[8].setOnClickListener(this.mOnClickListener);

        this.homeRootV[0].setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.homeRootV[1].setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.homeRootV[2].setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.homeRootV[3].setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.homeRootV[4].setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.homeRootV[5].setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.homeRootV[6].setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.homeRootV[7].setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.homeRootV[8].setOnFocusChangeListener(this.mOnFocusChangeListener);

        this.homeRootV[0].setOnKeyListener(this.mOnKeyListener);
        this.homeRootV[1].setOnKeyListener(this.mOnKeyListener);
        this.homeRootV[2].setOnKeyListener(this.mOnKeyListener);
        this.homeRootV[3].setOnKeyListener(this.mOnKeyListener);
        this.homeRootV[4].setOnKeyListener(this.mOnKeyListener);
        this.homeRootV[5].setOnKeyListener(this.mOnKeyListener);
        this.homeRootV[6].setOnKeyListener(this.mOnKeyListener);
        this.homeRootV[7].setOnKeyListener(this.mOnKeyListener);
        this.homeRootV[8].setOnKeyListener(this.mOnKeyListener);

        this.homeRootV[0].post(new Runnable() {
            @Override
            public void run() {
                Utils.focusV(MainActivity.this.homeRootV[0], true);
            }
        });

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
              mOnDataListener = new MOnDataListener(MainActivity.this);

              new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                  @Override
                  public void run() {
                    DataCenter.Ins().addDataListener(mOnDataListener);
                  }
              }, 10);//不加延时动画出不来

                return null;
            }
        }.execute();

        DataCenter.Ins().initMediaDatas();
        this.frameAnimation = new FrameAnimation(this.startV, startImgArr,
                EXVAL.START_ALLTV_ANIM_DURATION, false);

        long l = System.currentTimeMillis();
        Log.d(TAG, "onResume: onreate执行时间："+(l-createTime));
        createTime = l;
    }

    @Override
    protected void eventView() {

    }

    @Override
    protected void findView() {

        this.startV = homeStartV;
        this.bigIconV = homeImgBigV;
        this.textV = homeTextImgV;
        this.mainBgV = homeMainBgV;
        this.verV = homeVerV;
        this.netV = homeNetV;
        this.timeV = homeTimeV;

        this.homeRootV[0] = homeLiveRootV;
        this.homeRootV[1] = homePlaybackRootV;
        this.homeRootV[2] = homeYyWorldRootV;
        this.homeRootV[3] = homeYsFirstRootV;
        this.homeRootV[4] = homeChildrenDmRootV;
        this.homeRootV[5] = homeHotReRootV;
        this.homeRootV[6] = homeLgYyRootV;
        this.homeRootV[7] = homeHotTopicsRootV;
        this.homeRootV[8] = homeSetMgrRootV;

        this.homeTextV[0] = homeLiveTextV;
        this.homeTextV[1] = homePlaybackTextV;
        this.homeTextV[2] = homeYyWorldTextV;
        this.homeTextV[3] = homeYsFirstTextV;
        this.homeTextV[4] = homeChildrenDmTextV;
        this.homeTextV[5] = homeHotReTextV;
        this.homeTextV[6] = homeLgYyTextV;
        this.homeTextV[7] = homeHotTopicsTextV;
        this.homeTextV[8] = homeSetMgrTextV;
    }

    @Override
    protected void onStart() {
        super.onStart();
        long sl = System.currentTimeMillis();
        Log.d(TAG, "onCreate -> onStart: 过渡时间 "+(sl-createTime));
        DataCenter.Ins().setActivityType(EXVAL.TYPE_ACTIVITY_MAIN);
        DataCenter.Ins().scanTime();
        DataCenter.Ins().regReceiver(AppMain.ctx());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!execStoped) {
                    DataCenter.Ins().login();
                }
            }
        }, 10);
        long l = System.currentTimeMillis();
        Log.d(TAG, "onStart: 执行时间 "+(l-sl));
        execStoped = false;
        createTime = l;
    }

    @Override
    protected void onResume() {
        super.onResume();

        long l = System.currentTimeMillis();
        Log.d(TAG, "onStart -> onResume: 过渡时间："+(l-createTime));
        if (ShareAdapter.Ins(AppMain.ctx()).getB(EXVAL.NEED_UPDATE)) {
            DBMgr.Ins().useNew();
            ShareAdapter.Ins(AppMain.ctx()).setB(EXVAL.NEED_UPDATE, false);
        }
        long l1 = System.currentTimeMillis();
        Log.e(TAG, "onResume: 执行时间 "+(l1-l) );
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void stopAct() {
        DataCenter.Ins().unregReceiver(AppMain.ctx());
        DataCenter.Ins().delMarqueeTask(this.getClass());
//        if (this.marqueeV != null && this.marqueeV.isRoll()) {
//            this.marqueeV.stopRoll();
//        }
    }

    @Override
    protected void destroyAct() {
        DataCenter.Ins().delDataListener(this.mOnDataListener);
        if (this.mOnDataListener != null) {
            this.mOnDataListener.release();
            this.mOnDataListener = null;
        }

        if (this.mHandler != null) {
            this.mHandler.release();
            this.mHandler.removeCallbacksAndMessages(null);
            this.mHandler = null;
        }
        System.exit(0);
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
        return R.layout.activity_main;
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private MainActivity hostCls;
        private LinearLayout oldFocusV;
        private View oldTextV;
        private View oldImgV;
        private int kindIndex;

        public MOnFocusChangeListener(MainActivity hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        public void handleVFocus(boolean hasFocus) {
            if (oldImgV != null) {
                oldImgV.setSelected(hasFocus);
            }
            if (oldTextV != null) {
                oldTextV.setSelected(hasFocus);
            }
        }

        @Override
        public void onFocusChange(final View v, boolean hasFocus) {

            if (hasFocus) {
                kindIndex = Integer.valueOf(v.getTag().toString());
                oldFocusV = (LinearLayout) v;
                oldImgV = oldFocusV.getChildAt(0);
                oldTextV = oldFocusV.getChildAt(1);
                this.hostCls.changeKindInfo();
                hostCls.focusBorderForV(v);
                this.hostCls.focusBorderVisible(!this.hostCls.startV.isShown());
            }
            handleVFocus(hasFocus);
        }

        public int getKindIndex() {
            return kindIndex;
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private MainActivity hostCls;

        public MOnClickListener(MainActivity hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {

            if (hostCls != null && !this.hostCls.startV.isShown()) {
                int index = Integer.valueOf(v.getTag().toString());
                Intent intent = new Intent(hostCls, EXVAL.kindClsArr[index]);
                switch (index) {
                    case EXVAL.HOME_YYSJ_INDEX:
                        intent.putExtra(EXVAL.IS_YYSJ, true);
                        break;
                    case EXVAL.HOME_YYXF_INDEX:
                        intent.putExtra(EXVAL.IS_YYSJ, false);
                        break;
                    case EXVAL.HOME_LGYY_INDEX:
                        intent.putExtra(EXVAL.HOME_INDEX, 2);
                        break;
                }
                hostCls.startActivity(intent);
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private MainActivity hostCls;

        public MOnKeyListener(MainActivity hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (event.getAction() == KeyEvent.ACTION_UP || !this.hostCls.startV.isShown()) {
                return false;
            }

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    long nowTime = System.currentTimeMillis();
                    long duration = nowTime - hostCls.cacheTime;
                    hostCls.cacheTime = nowTime;
                    MLog.d(TAG, "tow key cost " + duration);
                    break;
            }

            return false;
        }
    }

    public void changeKindInfo() {
        mHandler.removeMessages(MSG.CHANGE_HOME_INFO);
        Message message = Message.obtain(mHandler, MSG.CHANGE_HOME_INFO);
        message.arg1 = this.mOnFocusChangeListener.getKindIndex();
        mHandler.sendMessageDelayed(message, EXVAL.CHANGE_HONE_INFO_DURATION);
    }

    private static class MHandler extends Handler {

        private MainActivity hostCls;

        public MHandler(MainActivity hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void handleMessage(Message msg) {

            if (hostCls == null) {
                return;
            }

            switch (msg.what) {
                case MSG.CHANGE_HOME_INFO:
                    hostCls.mainBgV.setBackgroundResource(homeMainBgResIdArr[msg.arg1]);
                    hostCls.bigIconV.setBackgroundResource(homeBigIconResIdArr[msg.arg1]);
                    hostCls.textV.setBackgroundResource(homeTextImgResIdArr[msg.arg1]);
                    hostCls.mOnFocusChangeListener.handleVFocus(true);
                    break;
                case MSG.SHOW_PAY: {
                    hostCls.payDialog = PayDialog.showPay(hostCls, this);
                    break;
                }
                case MSG.SHOW_LOGIN_TIPS:
                    Bundle bundle = msg.getData();
                    hostCls.showTips(bundle.getString(EXVAL.LOGIN_MSG),
                            bundle.getBoolean(EXVAL.DIALOG_CANCELABLE));
                    break;
                case MSG.HIDE_LOGIN_TIPS:
                    hostCls.hideTips();
                    break;
                case MSG.INTO_UI:
                    boolean loginOK = DataCenter.Ins().isLoginOK();
                    boolean dataOK = DataCenter.Ins().isDataOK();
                    boolean expired = DataCenter.Ins().isExpired();
                    Log.d(TAG, "handleMessage: 登录 "+loginOK+", 数据加载 "+dataOK);
                    if (DataCenter.Ins().isLoginOK() && DataCenter.Ins().isDataOK() &&
                            !DataCenter.Ins().isExpired()) {
                        if (hostCls.frameAnimation != null) {
                            hostCls.frameAnimation.release();
                            hostCls.frameAnimation = null;
                        }
                        if (hostCls.startV != null) {
                            hostCls.startV.setVisibility(View.GONE);
                        }
                        hostCls.startImgArr = null;
                        hostCls.focusBorderVisible(true);
                    }
                    break;
                case MSG.EXIT_APP:
                    hostCls.finish();
                    break;
            }
        }
    }

    long cacheTime = 0;

    private static class MOnDataListener implements OnDataListener {

        private MainActivity hostCls;

        public MOnDataListener(MainActivity hostCls) {
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
            if (hostCls == null) {
                return;
            }
            if (isConnected) {
                Log.d(TAG, "onNetState: 网络已连接，初始化数据信息 ");
                DataCenter.Ins().initMediaDatas();
                DataCenter.Ins().initForceTV();
                DataCenter.Ins().addMarqueeTask(hostCls.getClass());
                DataCenter.Ins().checkUpdate();
                if (DataCenter.Ins().isLoginOK()) {
                    Utils.sendMsg(hostCls.mHandler, MSG.HIDE_LOGIN_TIPS);
                } else {
                    DataCenter.Ins().login();
                }
                hostCls.netV.setVisibility(View.VISIBLE);
                switch (netType) {
                    case ConnectivityManager.TYPE_WIFI:
                        hostCls.netV.setBackgroundResource(R.drawable.wifi_ok);
                        break;
                    default:
                        hostCls.netV.setBackgroundResource(R.drawable.eth_f);
                        break;
                }
            } else {
                hostCls.netV.setVisibility(View.GONE);
                Utils.sendLoginTipsMsg(
                        hostCls.mHandler,
                        AppMain.res().getString(R.string.no_net),
                        true,
                        MSG.SHOW_LOGIN_TIPS);
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
                    hostCls.timeV.setText(time);
                }
            });
        }

        @Override
        public void onUpdateApp(final File apkF, final String desStr) {
        }

        @Override
        public void onMarquee(final MarqueeParser marqueeParser) {
            if (hostCls == null || marqueeParser == null) {
                return;
            }
            final int visibility = marqueeParser.getState().equals("ON") ? View.VISIBLE : View.GONE;
            float speed = 0, textSize = 0;
            int color = 0;
            try {
                speed = Float.valueOf(marqueeParser.getSpeed());
                textSize = Float.valueOf(marqueeParser.getTextSize());
                color = Color.parseColor(marqueeParser.getColor());
            } catch (Exception e) {
            }
            final float speedF = speed;
            final float textSizeF = textSize;
            final int colorF = color;
//            hostCls.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        hostCls.marqueeRootv.setVisibility(visibility);
//                        if (visibility == View.VISIBLE) {
//                            if (hostCls.marqueeV.isRoll()) {
//                                hostCls.marqueeV.stopRoll();
//                            }
//                            hostCls.marqueeV.setTextSpeed(speedF);
//                            hostCls.marqueeV.setTextSize(textSizeF);
//                            hostCls.marqueeV.setTextColor(colorF);
//                            hostCls.marqueeV.setContent(marqueeParser.getDesStr());
//                        } else {
//                            hostCls.marqueeV.stopRoll();
//                        }
//                    } catch (Exception e) {
//                    }
//                }
//            });
        }

        @Override
        public void onForceTv() {
            MLog.d(TAG, "onForceTv");
        }

        @Override
        public void onGetDataOver() {
            MLog.d(TAG, "onGetDataOver");
            if (hostCls != null) {
                Utils.sendMsg(hostCls.mHandler, MSG.INTO_UI);
            }
        }

        @Override
        public void onInitMediaOver() {
            MLog.d(TAG, "onInitMediaOver");
            if (hostCls != null) {
                Utils.sendMsg(hostCls.mHandler, MSG.INTO_UI);
            }
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
                            DataCenter.Ins().login();
                        }
                    }
                });
            }
        }
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
        if (TextUtils.isEmpty(msgStr))
            return;
        msgV.setText(msgStr);
        tipsDialog = new AlertDialog.Builder(MainActivity.this, R.style.TipsDialog)
                .setView(rootV)
                .setCancelable(dialogCancelable)
                .create();
        tipsDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long l = System.currentTimeMillis();

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (l - checkBackTime > CHECK_BREAK_VALID_TIME) {
                checkBackTime = l;
                toastMessage(getString(R.string.press_back_again));
                return false;
            } else {
                finish();
            }
        }else if (keyCode == KeyEvent.KEYCODE_HOME){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}

 /*   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setBackgroundResource(0);
        findViewById(android.R.id.content).setBackgroundResource(0);

        GlideCatchUtil.getInstance().clearCacheDiskSelf();
        GlideCatchUtil.getInstance().clearCacheMemory();

        Utils.getScreenSize(this);

        this.mHandler = new MHandler(this);

        this.startV = findViewById(R.id.home_start_v);

        this.frameAnimation = new FrameAnimation(this.startV, startImgArr,
                EXVAL.START_ALLTV_ANIM_DURATION, false);
        //frameAnimation.restartAnimation();

        this.bigIconV = findViewById(R.id.home_img_big_v);
        this.textV = findViewById(R.id.home_text_img_v);
        this.mainBgV = findViewById(R.id.home_main_bg_v);
        this.verV = findViewById(R.id.home_ver_v);
        this.netV = findViewById(R.id.home_net_v);
        this.timeV = findViewById(R.id.home_time_v);
        this.homeRootV[0] = findViewById(R.id.home_live_root_v);
        this.homeRootV[1] = findViewById(R.id.home_playback_root_v);
        this.homeRootV[2] = findViewById(R.id.home_yy_world_root_v);
        this.homeRootV[3] = findViewById(R.id.home_ys_first_root_v);
        this.homeRootV[4] = findViewById(R.id.home_children_dm_root_v);
        this.homeRootV[5] = findViewById(R.id.home_hot_re_root_v);
        this.homeRootV[6] = findViewById(R.id.home_lg_yy_root_v);
        this.homeRootV[7] = findViewById(R.id.home_hot_topics_root_v);
        this.homeRootV[8] = findViewById(R.id.home_set_mgr_root_v);

        this.homeTextV[0] = findViewById(R.id.home_live_text_v);
        this.homeTextV[1] = findViewById(R.id.home_playback_text_v);
        this.homeTextV[2] = findViewById(R.id.home_yy_world_text_v);
        this.homeTextV[3] = findViewById(R.id.home_ys_first_text_v);
        this.homeTextV[4] = findViewById(R.id.home_children_dm_text_v);
        this.homeTextV[5] = findViewById(R.id.home_hot_re_text_v);
        this.homeTextV[6] = findViewById(R.id.home_lg_yy_text_v);
        this.homeTextV[7] = findViewById(R.id.home_hot_topics_text_v);
        this.homeTextV[8] = findViewById(R.id.home_set_mgr_text_v);

        try {
            verV.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            verV.setText("");
        }

        this.mOnClickListener = new MOnClickListener(this);
        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnKeyListener = new MOnKeyListener(this);

        this.homeRootV[0].setOnClickListener(this.mOnClickListener);
        this.homeRootV[1].setOnClickListener(this.mOnClickListener);
        this.homeRootV[2].setOnClickListener(this.mOnClickListener);
        this.homeRootV[3].setOnClickListener(this.mOnClickListener);
        this.homeRootV[4].setOnClickListener(this.mOnClickListener);
        this.homeRootV[5].setOnClickListener(this.mOnClickListener);
        this.homeRootV[6].setOnClickListener(this.mOnClickListener);
        this.homeRootV[7].setOnClickListener(this.mOnClickListener);
        this.homeRootV[8].setOnClickListener(this.mOnClickListener);

        this.homeRootV[0].setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.homeRootV[1].setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.homeRootV[2].setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.homeRootV[3].setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.homeRootV[4].setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.homeRootV[5].setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.homeRootV[6].setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.homeRootV[7].setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.homeRootV[8].setOnFocusChangeListener(this.mOnFocusChangeListener);

        this.homeRootV[0].setOnKeyListener(this.mOnKeyListener);
        this.homeRootV[1].setOnKeyListener(this.mOnKeyListener);
        this.homeRootV[2].setOnKeyListener(this.mOnKeyListener);
        this.homeRootV[3].setOnKeyListener(this.mOnKeyListener);
        this.homeRootV[4].setOnKeyListener(this.mOnKeyListener);
        this.homeRootV[5].setOnKeyListener(this.mOnKeyListener);
        this.homeRootV[6].setOnKeyListener(this.mOnKeyListener);
        this.homeRootV[7].setOnKeyListener(this.mOnKeyListener);
        this.homeRootV[8].setOnKeyListener(this.mOnKeyListener);

        this.homeRootV[0].post(new Runnable() {
            @Override
            public void run() {
                Utils.focusV(MainActivity.this.homeRootV[0], true);
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
        DataCenter.Ins().setActivityType(EXVAL.TYPE_ACTIVITY_MAIN);
        DataCenter.Ins().addDataListener(this.mOnDataListener);
        DataCenter.Ins().scanTime();
        DataCenter.Ins().regReceiver(AppMain.ctx());
        if (!this.execStoped) {
            DataCenter.Ins().login();
        }
        this.execStoped = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ShareAdapter.Ins(AppMain.ctx()).getB(EXVAL.NEED_UPDATE)) {
            DBMgr.Ins().useNew();
            ShareAdapter.Ins(AppMain.ctx()).setB(EXVAL.NEED_UPDATE, false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void stopAct() {
        DataCenter.Ins().unregReceiver(AppMain.ctx());
        DataCenter.Ins().delDataListener(this.mOnDataListener);
        DataCenter.Ins().delMarqueeTask(this.getClass());
//        if (this.marqueeV != null && this.marqueeV.isRoll()) {
//            this.marqueeV.stopRoll();
//        }
    }

    @Override
    protected void destroyAct() {
        if (this.mOnDataListener != null) {
            this.mOnDataListener.release();
            this.mOnDataListener = null;
        }
        if (this.mHandler != null) {
            this.mHandler.release();
            this.mHandler.removeCallbacksAndMessages(null);
            this.mHandler = null;
        }
        System.exit(0);
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
        return R.layout.activity_main;
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private MainActivity hostCls;
        private LinearLayout oldFocusV;
        private View oldTextV;
        private View oldImgV;
        private int kindIndex;

        public MOnFocusChangeListener(MainActivity hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        public void handleVFocus(boolean hasFocus) {
            if (oldImgV != null) {
                oldImgV.setSelected(hasFocus);
            }
            if (oldTextV != null) {
                oldTextV.setSelected(hasFocus);
            }
        }

        @Override
        public void onFocusChange(final View v, boolean hasFocus) {

            if (hasFocus) {
                kindIndex = Integer.valueOf(v.getTag().toString());
                oldFocusV = (LinearLayout) v;
                oldImgV = oldFocusV.getChildAt(0);
                oldTextV = oldFocusV.getChildAt(1);
                this.hostCls.changeKindInfo();
                hostCls.focusBorderForV(v);
                this.hostCls.focusBorderVisible(!hostCls.startV.isShown());
            }
            handleVFocus(hasFocus);
        }

        public int getKindIndex() {
            return kindIndex;
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private MainActivity hostCls;

        public MOnClickListener(MainActivity hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {

            if (hostCls != null && !this.hostCls.startV.isShown()) {
                int index = Integer.valueOf(v.getTag().toString());
                Intent intent = new Intent(hostCls, EXVAL.kindClsArr[index]);
                switch (index) {
                    case EXVAL.HOME_YYSJ_INDEX:
                        intent.putExtra(EXVAL.IS_YYSJ, true);
                        intent.putExtra(EXVAL.HOME_INDEX, 0);

                        break;
                    case EXVAL.HOME_YYXF_INDEX:
                        intent.putExtra(EXVAL.IS_YYSJ, false);
                        intent.putExtra(EXVAL.HOME_INDEX, 1);
                        break;
                    case EXVAL.HOME_LGYY_INDEX:
                        intent.putExtra(EXVAL.HOME_INDEX, 2);
                        break;
                }
                hostCls.startActivity(intent);
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private MainActivity hostCls;

        public MOnKeyListener(MainActivity hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (event.getAction() == KeyEvent.ACTION_UP || !this.hostCls.startV.isShown()) {
                return false;
            }

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    long nowTime = System.currentTimeMillis();
                    long duration = nowTime - hostCls.cacheTime;
                    hostCls.cacheTime = nowTime;
                    MLog.d(TAG, "tow key cost " + duration);
                    break;
            }

            return false;
        }
    }

    public void changeKindInfo() {
        mHandler.removeMessages(MSG.CHANGE_HOME_INFO);
        Message message = Message.obtain(mHandler, MSG.CHANGE_HOME_INFO);
        message.arg1 = this.mOnFocusChangeListener.getKindIndex();
        mHandler.sendMessageDelayed(message, EXVAL.CHANGE_HONE_INFO_DURATION);
    }

    private static class MHandler extends Handler {

        private MainActivity hostCls;

        public MHandler(MainActivity hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void handleMessage(Message msg) {

            if (hostCls == null) {
                return;
            }

            switch (msg.what) {
                case MSG.CHANGE_HOME_INFO:
                    hostCls.mainBgV.setBackgroundResource(homeMainBgResIdArr[msg.arg1]);
                    hostCls.bigIconV.setBackgroundResource(homeBigIconResIdArr[msg.arg1]);
                    hostCls.textV.setBackgroundResource(homeTextImgResIdArr[msg.arg1]);
                    hostCls.mOnFocusChangeListener.handleVFocus(true);
                    break;
                case MSG.SHOW_PAY: {
                    hostCls.payDialog = PayDialog.showPay(hostCls, this);
                    break;
                }
                case MSG.SHOW_LOGIN_TIPS:
                    Bundle bundle = msg.getData();
                    hostCls.showTips(bundle.getString(EXVAL.LOGIN_MSG),
                            bundle.getBoolean(EXVAL.DIALOG_CANCELABLE));
                    break;
                case MSG.HIDE_LOGIN_TIPS:
                    hostCls.hideTips();
                    break;
                case MSG.INTO_UI:
                    boolean loginOK = DataCenter.Ins().isLoginOK();
                    boolean dataOK = DataCenter.Ins().isDataOK();
                    boolean expired = DataCenter.Ins().isExpired();
                    if (DataCenter.Ins().isLoginOK() && DataCenter.Ins().isDataOK() &&
                            !DataCenter.Ins().isExpired()) {
                        if (hostCls.frameAnimation != null) {
                            hostCls.frameAnimation.release();
                            hostCls.frameAnimation = null;
                        }
                        if (hostCls.startV != null) {
                            hostCls.startV.setVisibility(View.GONE);
                        }
                        hostCls.startImgArr = null;
                        hostCls.focusBorderVisible(true);
                    }

                    //if (loginOK){//无法登录，测试使用，我们的盒子使用
                    //    if (hostCls.startV != null) {
                    //        hostCls.startV.setVisibility(View.GONE);
                    //    }
                    //}
                    break;
                case MSG.EXIT_APP:
                    hostCls.finish();
                    break;
            }
        }
    }

    long cacheTime = 0;

    private static class MOnDataListener implements OnDataListener {

        private MainActivity hostCls;

        public MOnDataListener(MainActivity hostCls) {
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
            if (hostCls == null) {
                return;
            }
            if (isConnected) {
                DataCenter.Ins().initMediaDatas();
                DataCenter.Ins().initForceTV();
                DataCenter.Ins().addMarqueeTask(hostCls.getClass());
                DataCenter.Ins().checkUpdate();
                if (DataCenter.Ins().isLoginOK()) {
                    Utils.sendMsg(hostCls.mHandler, MSG.HIDE_LOGIN_TIPS);
                } else {
                    DataCenter.Ins().login();
                }
                hostCls.netV.setVisibility(View.VISIBLE);
                switch (netType) {
                    case ConnectivityManager.TYPE_WIFI:
                        hostCls.netV.setBackgroundResource(R.drawable.wifi_ok);
                        break;
                    default:
                        hostCls.netV.setBackgroundResource(R.drawable.eth_f);
                        break;
                }
            } else {
                hostCls.netV.setVisibility(View.GONE);
                Utils.sendLoginTipsMsg(
                        hostCls.mHandler,
                        AppMain.res().getString(R.string.no_net),
                        true,
                        MSG.SHOW_LOGIN_TIPS);
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
                    hostCls.timeV.setText(time);
                }
            });
        }

        @Override
        public void onUpdateApp(final File apkF, final String desStr) {
        }

        @Override
        public void onMarquee(final MarqueeParser marqueeParser) {
            if (hostCls == null || marqueeParser == null) {
                return;
            }
            final int visibility = marqueeParser.getState().equals("ON") ? View.VISIBLE : View.GONE;
            float speed = 0, textSize = 0;
            int color = 0;
            try {
                speed = Float.valueOf(marqueeParser.getSpeed());
                textSize = Float.valueOf(marqueeParser.getTextSize());
                color = Color.parseColor(marqueeParser.getColor());
            } catch (Exception e) {
            }
            final float speedF = speed;
            final float textSizeF = textSize;
            final int colorF = color;
//            hostCls.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        hostCls.marqueeRootv.setVisibility(visibility);
//                        if (visibility == View.VISIBLE) {
//                            if (hostCls.marqueeV.isRoll()) {
//                                hostCls.marqueeV.stopRoll();
//                            }
//                            hostCls.marqueeV.setTextSpeed(speedF);
//                            hostCls.marqueeV.setTextSize(textSizeF);
//                            hostCls.marqueeV.setTextColor(colorF);
//                            hostCls.marqueeV.setContent(marqueeParser.getDesStr());
//                        } else {
//                            hostCls.marqueeV.stopRoll();
//                        }
//                    } catch (Exception e) {
//                    }
//                }
//            });
        }

        @Override
        public void onForceTv() {
            MLog.d(TAG, "onForceTv");
        }

        @Override
        public void onGetDataOver() {
            MLog.d(TAG, "onGetDataOver");
            if (hostCls != null) {
                Utils.sendMsg(hostCls.mHandler, MSG.INTO_UI);
            }
        }

        @Override
        public void onInitMediaOver() {
            MLog.d(TAG, "onInitMediaOver");
            if (hostCls != null) {
                Utils.sendMsg(hostCls.mHandler, MSG.INTO_UI);
            }
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
                            DataCenter.Ins().login();
                        }
                    }
                });
            }
        }
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
        tipsDialog = new AlertDialog.Builder(MainActivity.this, R.style.TipsDialog)
                .setView(rootV)
                .setCancelable(dialogCancelable)
                .create();
        tipsDialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            //Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
            //showTipsSchedule("再按一次退出", true);
            long l = System.currentTimeMillis();
            if (l - checkBackTime>CHECK_BREAK_VALID_TIME){
                checkBackTime = l;
                toastMessage(getString(R.string.press_back_again));//_exit"再按一次退出");
                return false;
            }else
                return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

}*/