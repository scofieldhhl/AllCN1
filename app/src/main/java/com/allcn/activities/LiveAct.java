package com.allcn.activities;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.allcn.R;
import com.allcn.interfaces.ListWindowInterface;
import com.allcn.interfaces.OnDataListener;
import com.allcn.interfaces.OnPlayListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.utils.MSG;
import com.allcn.views.HorChalListV;
import com.allcn.views.LiveBottomEpgWindow;
import com.allcn.views.LoadWindow;
import com.allcn.views.TipsDialog;
import com.allcn.views.focus.FocusBorder;
import com.allcn.widget.media.IjkVideoView;
import com.bumptech.glide.Glide;
import com.datas.CacheKindObj;
import com.datas.HomeMovie;
import com.datas.LiveCKindObj;
import com.datas.LiveChalObj;
import com.datas.LiveKindObj;
import com.mast.lib.parsers.MarqueeParser;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;
import com.mast.lib.views.MarqueeView;
import com.umeng.commonsdk.internal.a;

import java.io.File;
import java.util.List;


import tv.danmaku.ijk.media.player.IMediaPlayer;

import static android.media.AudioManager.FLAG_SHOW_UI;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.umeng.commonsdk.internal.a.e;
import static com.umeng.commonsdk.internal.a.k;
import static com.umeng.commonsdk.internal.a.l;

public class LiveAct extends BaseActivity implements IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnInfoListener, IMediaPlayer.OnErrorListener, ListWindowInterface {

    private static final String TAG = LiveAct.class.getSimpleName();
    public MHandler mHandler;
    private IjkVideoView videoView;
    //com.mast.lib.views.VideoView
    //com.allcn.widget.media.IjkVideoView;

    private String[] kinds = new String[]{
            "全部", "H265", "港澳", "大陆", "台湾", "国际1", "国际2", "越南", "体育", "少儿", "菲律宾"
    };
    private String[] kindsId = new String[]{
            "全部", "520", "228", "229", "231", "230", "521", "232", "234", "522", "501"
    };
    public boolean pressKeyFirst, liveDataDBOK, isLiveFavK;
    private HorChalListV horChalListV1;//直播的菜单栏
    private FrameLayout rootV;
    private StringBuilder keyBuilder;
    private TextView keyV;
    private LoadWindow loadWindow;
    private AudioManager audioManager;
    private SparseArray<TipsDialog> tipsDialogArr;
    private MOnDataListener mOnDataListener;
    private DataCenter acterCls;
    private MOnPlayListener mOnPlayListener;
    public int curLiveKIndex, selLiveKIndex, curLiveCKIndex, selLiveCKIndex, curLiveChalIndex,
            selLiveChalIndex, curLivePageIndex, selLivePageIndex, livekindNum;
    public LiveKindObj curLiveKind, selLiveKind;
    public LiveCKindObj curLiveCKind, selLiveCKind;
    public CacheKindObj cacheLiveKind, tmpCacheLiveKind;
    public LiveChalObj curLiveChal, selLiveChal;
    public List<LiveKindObj> liveKinds;
    private LiveBottomEpgWindow liveBottomEpgWindow;//直播底部显示节目的框
    private FrameLayout marqueeRootv;
    private MarqueeView marqueeV;
    private ImageView tipsV;
    private int retryPlayCount = 0;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MLog.d(TAG, "onCreate");

        cacheLiveKind = new CacheKindObj();
        tmpCacheLiveKind = new CacheKindObj();
        mOnPlayListener = new MOnPlayListener(this);
        mOnDataListener = new MOnDataListener(this);
        tipsDialogArr = new SparseArray<>();
        mHandler = new MHandler(this);
        acterCls = DataCenter.Ins();

        Utils.getScreenSize(this);

        pressKeyFirst = true;
        keyBuilder = new StringBuilder();

        tipsV = findViewById(R.id.tips_v);
        marqueeRootv = findViewById(R.id.live_marquee_rootv);
        marqueeV = findViewById(R.id.live_marquee_v);
        keyV = findViewById(R.id.key_v);
        rootV = findViewById(R.id.root_v);
        videoView = findViewById(R.id.videoview);
        videoView.setOnPreparedListener(this);
        videoView.setOnErrorListener(this);
        videoView.setOnInfoListener(this);

        horChalListV1 = new HorChalListV(this);
        horChalListV1.setListWindowInterface(this);

        loadWindow = new LoadWindow(this);
        liveBottomEpgWindow = new LiveBottomEpgWindow();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        acterCls.getLiveData(this);
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
        MLog.d(TAG, "onStart");
        DataCenter.Ins().regReceiver(AppMain.ctx());
        this.acterCls.addDataListener(this.mOnDataListener);
        this.acterCls.addPlayListener(this.mOnPlayListener);
        DataCenter.Ins().setActivityType(EXVAL.TYPE_ACTIVITY_OTH);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MLog.d(TAG, "onResume");
        this.execStoped = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MLog.d(TAG, "onPause");
    }

    @Override
    protected void stopAct() {
        DataCenter.Ins().unregReceiver(AppMain.ctx());
        this.acterCls.delDataListener(this.mOnDataListener);
        this.acterCls.delPlayListener(this.mOnPlayListener);
        if (this.marqueeV != null && this.marqueeV.isRoll()) {
            this.marqueeV.stopRoll();
        }
        DataCenter.Ins().delMarqueeTask(this.getClass());
        if (this.videoView.isPlaying()) {
            this.videoView.stopPlayback();
        }
    }

    @Override
    protected void destroyAct() {
        DataCenter.Ins().stopGetLiveData();
        this.liveDataDBOK = false;
        this.mOnDataListener.release();
        this.mOnDataListener = null;
        this.mOnPlayListener.release();
        this.mOnPlayListener = null;
        this.tipsDialogArr.clear();
        this.tipsDialogArr = null;
        this.loadWindow.relase();
        this.loadWindow = null;
        this.videoView.setOnPreparedListener(null);
        this.videoView.setOnErrorListener(null);
        this.videoView.setOnCompletionListener(null);
        this.videoView = null;
        this.mHandler.removeCallbacksAndMessages(null);
        this.mHandler = null;
        this.horChalListV1.setListWindowInterface(null);
        this.horChalListV1.relase();
        this.horChalListV1 = null;
        this.acterCls = null;
        this.keyBuilder.setLength(0);
        this.keyBuilder = null;
        this.curLiveKind = this.selLiveKind = null;
        this.curLiveCKind = this.selLiveCKind = null;
        this.cacheLiveKind = this.tmpCacheLiveKind = null;
        this.curLiveChal = this.selLiveChal = null;
        this.liveBottomEpgWindow.dismiss();
        this.liveBottomEpgWindow = null;
        if (this.liveKinds != null) {
            this.liveKinds.clear();
            this.liveKinds = null;
        }
    }

    @Override
    FocusBorder createFocusBorder() {
        return null;
    }

    @Override
    int layoutId() {
        return R.layout.live_main_act;
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        return true;
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {

        switch (what) {
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                Utils.sendMsg(this.mHandler, MSG.SHOW_LOAD, 0);
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                Utils.sendMsg(this.mHandler, MSG.HIDE_MAINTENANCE);
                Utils.sendMsg(this.mHandler, MSG.HIDE_LOAD, 0);
                break;
        }

        return false;
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        MLog.d(TAG, "onPrepared");
        //Utils.setVideoScale(VAL.SCREEN_FULL, this, this.videoView);
        this.videoView.start();
        Utils.sendMsg(this.mHandler, MSG.HIDE_MAINTENANCE);
        Utils.sendMsg(this.mHandler, MSG.HIDE_LOAD, 0);
        Utils.sendMsg(this.mHandler, MSG.HIDE_BOTTOM, EXVAL.BOTTOM_EPG_TIMEOUT);
    }

    @Override
    public void onChalSel(LiveChalObj chalObj, boolean autoPlay) {
        Utils.removeMsg(this.mHandler, MSG.PLAY_CHAL);
        Utils.removeMsg(this.mHandler, MSG.PLAY_URL);
        Utils.removeMsg(this.mHandler, MSG.PLAY);
        this.curLiveChal = chalObj;
        this.acterCls.cacheData(this, chalObj);
        Utils.sendMsg(this.mHandler, MSG.HIDE_MAINTENANCE);
        Utils.sendMsg(this.mHandler, MSG.SHOW_BOTTOM);
        Utils.sendMsg(this.mHandler, MSG.SEND_TO_SER, EXVAL.SEND_TO_SER_DURATION);
        this.retryPlayCount = 0;
        if (autoPlay) {
            Utils.sendMsg(this.mHandler, MSG.PLAY_CHAL);
        }
    }

    private static class MHandler extends Handler {

        private LiveAct hostCls;

        public MHandler(LiveAct hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void handleMessage(Message msg) {

            MLog.d(TAG, String.format("handleMessage %d", msg.what));
            if (hostCls == null) {
                return;
            }

            switch (msg.what) {
                case MSG.INIT_OVER:
                    if (hostCls.liveDataDBOK) {
                        hostCls.acterCls.playChal(hostCls, true);
                    }
                    if (hostCls.acterCls.isExpired()) {
                        hostCls.sendTipsMsg(EXVAL.APP_TIPS_TYPE,
                                AppMain.res().getString(R.string.expired),
                                null);
                    }
                    break;
                case MSG.PLAY:
                    Bundle bundle = msg.getData();
                    String filmId = bundle.getString(EXVAL.FILMID);
                    String vodId = bundle.getString(EXVAL.VODID);
                    String kindName = bundle.getString(EXVAL.KIND_NAME);
                    hostCls.acterCls.sendLiveForceTVHttpReq(filmId, vodId, kindName);
                    break;
                case MSG.PLAY_URL:
                    hostCls.retryPlayCount++;
                    String vUrl = msg.getData().getString(EXVAL.MOVIE_URL);
                    MLog.d(TAG, "vUrl = " + vUrl);
                    if (!TextUtils.isEmpty(vUrl)) {
                        hostCls.videoView.stopPlayback();
                        String urlPath = "s" + vUrl;
                        Log.d(TAG, "player path: "+urlPath);
                        hostCls.videoView.setVideoPath(""+vUrl);

                    }
                    Utils.sendMsg(hostCls.mHandler, MSG.SHOW_MAINTENANCE, EXVAL.SHOW_MAINTANCE_TIMEOUT);
                    Utils.sendMsg(hostCls.mHandler, MSG.WATCH_TIMEOUT, EXVAL.WATCH_TIME);
                    if (hostCls.retryPlayCount >= EXVAL.RETRY_PLAY_COUNT) {
                        MLog.d(TAG, String.format("retry play count %d, stop retry",
                                hostCls.retryPlayCount));
                        hostCls.retryPlayCount = 0;
                    } else {
                        MLog.d(TAG, String.format("retry play count %d, retry",
                                hostCls.retryPlayCount));
                        Utils.sendMsg(this, MSG.PLAY_CHAL, EXVAL.RETRY_PLAY_DURATION);
                    }
                    break;
                case MSG.PLAY_CHAL: {
                    Utils.sendMsg(this, MSG.SHOW_LOAD, 0);
                    bundle = new Bundle();
                    MLog.d(TAG, "playChal curLiveChal " + hostCls.curLiveChal);
                    bundle.putString(EXVAL.FILMID, hostCls.curLiveChal.getFilmId());
                    bundle.putString(EXVAL.VODID, hostCls.curLiveChal.getVodId());
                    bundle.putString(EXVAL.KIND_NAME, hostCls.curLiveChal.getColId());
                    Utils.sendMsg(this, bundle, MSG.PLAY, 0);
                    return;
                }
                case MSG.SURE_KEY:
                    hostCls.retryPlayCount = 0;
                    hostCls.pressKeyFirst = true;
                    hostCls.keyV.setVisibility(View.GONE);
                    int keyNum = Integer.valueOf(hostCls.keyBuilder.toString());
//                    MLog.d(TAG, hostCls.curLiveKind.toString());
                    int curChalNum = 0;
                    if (hostCls.cacheLiveKind != null) {
                        curChalNum = hostCls.cacheLiveKind.getChalNum();
                        MLog.d(TAG, hostCls.cacheLiveKind.toString() + " " + curChalNum);
                    }
                    if (keyNum > 0 && keyNum <= curChalNum) {
                        hostCls.curLiveChalIndex = keyNum - 1;
                        hostCls.acterCls.playChal(hostCls, true);
                    } else {
                        hostCls.showToastTips(AppMain.res().getString(R.string.invalid_chal_num));
                    }
                    hostCls.keyBuilder.setLength(0);
                    hostCls.keyV.setText("");
                    break;
                case MSG.HIDE_LIST:
                    hostCls.horChalListV1.dismiss();
                    break;
                case MSG.GET_CHAL:
                    hostCls.acterCls.playChal(hostCls, msg.getData().getBoolean(EXVAL.AUTO_PLAY));
                    break;
                case MSG.SHOW_LOAD:
                    Utils.removeMsg(hostCls.mHandler, MSG.HIDE_LOAD);
                    if (!hostCls.loadWindow.isShown()) {
                        hostCls.loadWindow.show(hostCls.rootV);
                    }
                    break;
                case MSG.HIDE_LOAD:
                    if (hostCls.loadWindow.isShown()) {
                        hostCls.loadWindow.dismiss();
                    }
                    break;
                case MSG.SHOW_TIPS:
                    bundle = msg.getData();
                    String tips1Str = bundle.getString(EXVAL.TIPS_1);
                    String tips2Str = bundle.getString(EXVAL.TIPS_2);
                    int tipsType = bundle.getInt(EXVAL.TIPS_TYPE);
                    hostCls.showTipsDialog(tipsType, tips1Str, tips2Str,
                            TextUtils.isEmpty(tips2Str) ? true : false);
                    break;
                case MSG.WATCH_TIMEOUT:
                    hostCls.sendTipsMsg(EXVAL.APP_TIPS_TYPE,
                            AppMain.res().getString(R.string.long_time_tips),
                            String.valueOf((int) (Math.random() * 90 + 10)));
                    break;
                case MSG.SHOW_MAINTENANCE:
                    Utils.sendMsg(this, MSG.HIDE_LOAD);
                    String imgUrl = DataCenter.Ins().getImgUrl();
                    if (!TextUtils.isEmpty(imgUrl)) {
                        Glide.with(hostCls).load(imgUrl).into(hostCls.tipsV);
                        hostCls.tipsV.setVisibility(View.VISIBLE);
                    }
                    break;
                case MSG.HIDE_MAINTENANCE:
                    Utils.removeMsg(this, MSG.SHOW_MAINTENANCE);
                    hostCls.tipsV.setVisibility(View.GONE);
                    break;
                case MSG.SHOW_BOTTOM:
                    Utils.removeMsg(hostCls.mHandler, MSG.HIDE_BOTTOM);
                    if (hostCls.liveBottomEpgWindow != null) {
                        hostCls.liveBottomEpgWindow.loadData(hostCls.curLiveChal);
                        hostCls.liveBottomEpgWindow.show(hostCls.rootV);
                    }
                    break;
                case MSG.HIDE_BOTTOM:
                    if (hostCls.liveBottomEpgWindow != null) {
                        hostCls.liveBottomEpgWindow.dismiss();
                    }
                    break;
                case MSG.SEND_TO_SER:
                    if (hostCls.curLiveChal != null) {
                        String name = hostCls.curLiveChal.getName();
                        if (!TextUtils.isEmpty(name)) {
                            DataCenter.Ins().sendDataToSer(hostCls.curLiveChal.getName());
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (!this.liveDataDBOK) {
            return super.onKeyUp(keyCode, event);
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (!this.horChalListV1.isShown()) {
                    turnDownVol();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (!this.horChalListV1.isShown()) {
                    turnUpVol();
                }
                break;
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (!this.liveDataDBOK) {
            return super.onKeyDown(keyCode, event);
        }

        if (KeyEvent.KEYCODE_0 <= keyCode && keyCode <= KeyEvent.KEYCODE_9) {
            this.keyBuilder.append(keyCode - 7);
            this.keyV.setText(this.keyBuilder);
            if (this.pressKeyFirst) {
                this.pressKeyFirst = false;
                this.keyV.setVisibility(View.VISIBLE);
                Utils.sendMsg(this.mHandler, MSG.SURE_KEY, EXVAL.SURE_KEY_TIMEOUT);
            }
            return super.onKeyDown(keyCode, event);
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (this.horChalListV1 != null && !this.horChalListV1.isShown()) {
                    this.horChalListV1.show(this.rootV);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                this.curLiveChalIndex++;
                if (this.curLiveChalIndex >= this.cacheLiveKind.getChalNum()) {
                    this.curLiveChalIndex = 0;
                }
                sendPlayMsg(true);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                this.curLiveChalIndex--;
                if (this.curLiveChalIndex < 0) {
                    this.curLiveChalIndex = this.cacheLiveKind.getChalNum() - 1;
                }
                sendPlayMsg(true);
                break;

            case KeyEvent.KEYCODE_BACK:
                long l = System.currentTimeMillis();
                if (horChalListV1.isShown()) {
                    horChalListV1.dismiss();
                    //resetFocusBorder();
                    //horChalListV1.clearFocus();
                    //focusBorderVisible(false);
                }
                if (l - checkBackTime > CHECK_BREAK_VALID_TIME) {
                    checkBackTime = l;
                    toastMessage(getString(R.string.again_back_live));
                    return false;
                } else {
                    return super.onKeyDown(keyCode, event);
                }
            case KeyEvent.KEYCODE_MENU:

                videoView.setPlayerMode(this);//切换播放器模式
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void showToastTips(String msgStr) {

        View view = LayoutInflater.from(AppMain.ctx()).
                inflate(R.layout.toast_layout, null, false);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                (int) AppMain.res().getDimension(R.dimen.toast_layout_w),
                (int) AppMain.res().getDimension(R.dimen.toast_layout_h));
        TextView textView = view.findViewById(R.id.toast_textv);
        textView.setLayoutParams(layoutParams);
        textView.setText(msgStr);
        Toast toast = new Toast(AppMain.ctx());
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void showExitTips() {
        View view = LayoutInflater.from(AppMain.ctx()).
                inflate(R.layout.exit_layout, null, false);
        MLog.d(TAG, String.format("exit layout root v %s", view));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                (int) AppMain.res().getDimension(R.dimen.exit_layout_w),
                (int) AppMain.res().getDimension(R.dimen.exit_layout_h));
        TextView textView = view.findViewById(R.id.exit_textv);
        textView.setLayoutParams(layoutParams);
        Toast toast = new Toast(AppMain.ctx());
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0,
                (int) AppMain.res().getDimension(R.dimen.exit_layout_y_offset));
        toast.show();
    }

    private void turnUpVol() {
        int maxVolume = this.audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = this.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (curVolume++ > maxVolume) {
            curVolume = maxVolume;
        } else {
            this.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, FLAG_SHOW_UI);
        }
    }

    private void turnDownVol() {
        int curVolume = this.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (curVolume-- < 0) {
            curVolume = 0;
        } else {
            this.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, curVolume, FLAG_SHOW_UI);
        }
    }

    private void sendTipsMsg(int dialogType, String tips1Str, String tips2Str) {
        Bundle bundle = new Bundle();
        bundle.putString(EXVAL.TIPS_1, tips1Str);
        bundle.putString(EXVAL.TIPS_2, tips2Str);
        bundle.putInt(EXVAL.TIPS_TYPE, dialogType);
        Utils.sendMsg(this.mHandler, bundle, MSG.SHOW_TIPS, 0);
    }

    private void showTipsDialog(int dialogType, String tips1Str, String tips2Str,
                                boolean isCancelable) {
        hideTipsDialog(dialogType);
        TipsDialog tipsDialog = new TipsDialog(LiveAct.this, R.style.TIPS_DIALOG_STYLE);
        tipsDialog.setShowTips(tips1Str, tips2Str);
        tipsDialog.setCancelable(isCancelable);
        tipsDialog.show();
        tipsDialog.getWindow().setLayout(
                MATCH_PARENT,
                (int) AppMain.res().getDimension(R.dimen.tips_dialog_h));
        this.tipsDialogArr.put(dialogType, tipsDialog);
    }

    private void hideTipsDialog(int dialogType) {
        int dialogIndex = this.tipsDialogArr.indexOfKey(dialogType);
        if (dialogIndex >= 0) {
            TipsDialog tipsDialog = this.tipsDialogArr.get(dialogType);
            this.tipsDialogArr.remove(dialogType);
            if (tipsDialog != null) {
                tipsDialog.dismiss();
                tipsDialog = null;
            }
        }
    }

    private static class MOnDataListener implements OnDataListener {

        private LiveAct hostCls;

        public MOnDataListener(LiveAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            hostCls = null;
        }

        @Override
        public void onLoadInitDatas(SparseArray<HomeMovie> homeMovies) {

        }

        @Override
        public void onNetState(int netType, boolean isConnected) {
            if (hostCls == null) {
                return;
            }
            if (isConnected) {
                DataCenter.Ins().addMarqueeTask(hostCls.getClass());
                hostCls.hideTipsDialog(EXVAL.NET_TIPS_TYPE);
                if (hostCls.liveDataDBOK) {
                    Utils.sendMsg(hostCls.mHandler, MSG.SEND_PLAY, 0);
                }
            } else {
                hostCls.sendTipsMsg(EXVAL.NET_TIPS_TYPE,
                        AppMain.res().getString(R.string.net_bad),
                        null);
            }
        }

        @Override
        public void onTimeDate(String time) {

        }

        @Override
        public void onUpdateApp(File apkF, String desStr) {

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
            hostCls.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (hostCls != null && hostCls.marqueeRootv != null &&
                                hostCls.marqueeV != null && marqueeParser != null) {
                            try {
                                hostCls.marqueeRootv.setVisibility(visibility);
                                if (visibility == View.VISIBLE) {
                                    if (hostCls.marqueeV.isRoll()) {
                                        hostCls.marqueeV.stopRoll();
                                    }
                                    hostCls.marqueeV.setTextSpeed(speedF);
                                    hostCls.marqueeV.setTextSize(textSizeF);
                                    hostCls.marqueeV.setTextColor(colorF);
                                    hostCls.marqueeV.setContent(marqueeParser.getDesStr());
                                } else {
                                    hostCls.marqueeV.stopRoll();
                                }
                            } catch (Exception e) {
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            });
        }

        @Override
        public void onForceTv() {
        }

        @Override
        public void onLogin(String loginStr, boolean dialogCancelable) {

        }

        @Override
        public void onToken() {

        }

        @Override
        public void onPay(boolean b, String s) {

        }

        @Override
        public void onGetDataOver() {

        }

        @Override
        public void onInitMediaOver() {

        }

        @Override
        public void onChalList(LiveChalObj chal, boolean liveDataDBOK) {
            if (hostCls != null && liveDataDBOK && (hostCls.curLiveChalIndex >= 0)) {
                hostCls.onChalSel(chal, true);
            }
        }

        @Override
        public void onFavUpdate() {
            if (hostCls == null) {
                return;
            }
            hostCls.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (hostCls != null && hostCls.horChalListV1 != null) {
                        hostCls.horChalListV1.updateFavList();
                    }
                }
            });
        }
    }

    private static class MOnPlayListener implements OnPlayListener {

        private LiveAct hostCls;

        public MOnPlayListener(LiveAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onPlay(String url) {
            Bundle bundle = new Bundle();
            bundle.putString(EXVAL.MOVIE_URL, url);
            Utils.sendMsg(hostCls.mHandler, bundle, MSG.PLAY_URL);
        }

        @Override
        public void onNetSpeed(final String speedStr) {
            if (hostCls == null) {
                return;
            }
            hostCls.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (hostCls != null && hostCls.loadWindow != null) {
                        hostCls.loadWindow.updateLoadSpeed(speedStr);
                    }
                }
            });
        }
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER){
            //videoView.setPlayerMode();//切换播放器模式
        }
        return super.onKeyLongPress(keyCode, event);
    }

    public boolean isLiveFavK() {
        return this.isLiveFavK;
    }

    public void setLiveFavK(boolean isLiveFavK) {
        this.isLiveFavK = isLiveFavK;
    }

    public void setCacheKindVal(String colId, String colName, int chalNum,
                                int pageNum) {
        this.cacheLiveKind.setColId(colId);
        this.cacheLiveKind.setColName(colName);
        this.cacheLiveKind.setPageNum(pageNum);
        this.cacheLiveKind.setChalNum(chalNum);
    }

    public void setCacheKindVal(CacheKindObj cacheKind) {
        if (cacheKind == null) {
            return;
        }
        this.cacheLiveKind.setColId(cacheKind.getColId());
        this.cacheLiveKind.setColName(cacheKind.getColName());
        this.cacheLiveKind.setPageNum(cacheKind.getPageNum());
        this.cacheLiveKind.setChalNum(cacheKind.getChalNum());
    }

    public void setTmpCacheKindVal(LiveKindObj kind) {
        if (kind == null) {
            return;
        }
        this.tmpCacheLiveKind.setColName(kind.getColName());
        this.tmpCacheLiveKind.setColId(kind.getColId());
        this.tmpCacheLiveKind.setPageNum(kind.getPageNum());
        this.tmpCacheLiveKind.setChalNum(kind.getChalNum());
    }

    public void setTmpCacheKindVal(LiveCKindObj cKind) {
        if (cKind == null) {
            return;
        }
        this.tmpCacheLiveKind.setColName(cKind.getColName());
        this.tmpCacheLiveKind.setColId(cKind.getColId());
        this.tmpCacheLiveKind.setPageNum(cKind.getPageNum());
        this.tmpCacheLiveKind.setChalNum(cKind.getChalNum());
    }

    public void setTmpCacheKindVal(CacheKindObj cacheKind) {
        if (cacheKind == null) {
            return;
        }
        this.tmpCacheLiveKind.setChalNum(cacheKind.getChalNum());
        this.tmpCacheLiveKind.setPageNum(cacheKind.getPageNum());
        this.tmpCacheLiveKind.setColName(cacheKind.getColName());
        this.tmpCacheLiveKind.setColId(cacheKind.getColId());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        execStop();
        execDestroy();
    }

    private void sendPlayMsg(boolean autoPlay) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(EXVAL.AUTO_PLAY, autoPlay);
        Utils.sendMsg(this.mHandler, bundle, MSG.GET_CHAL, EXVAL.SEND_PLAY_DURATION);
    }
}
