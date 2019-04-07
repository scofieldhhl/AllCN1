package com.allcn.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.adapters.PlayItemAdapter;
import com.allcn.interfaces.OnPlayListener;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.utils.MSG;
import com.allcn.views.LoadWindow;
import com.allcn.views.PlayItemDecoration;
import com.allcn.views.UiSeeKBar;
import com.allcn.views.focus.FocusBorder;
import com.datas.MovieObj;
import com.datas.ProgremPlayerInfo;
import com.db.cls.DBMgr;
import com.db.cls.ProgremPlayerDB;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;
import com.mast.lib.views.VideoView;

public class PlayAct extends BaseActivity {

    private static final String TAG = PlayAct.class.getSimpleName();
    private RecyclerView rv;
    private LinearLayoutManager rvLayoutMgr;
    private PlayItemDecoration rvDecoration;
    private PlayItemAdapter rvAdapter;
    private FrameLayout bottomCtrlRootV;
    private String movieName, movieCid;
    private int movieId, moviePos, movieNum;
    private UiSeeKBar seeKBar;
    private VideoView videoView;
    private MOnPreparedListener mOnPreparedListener;
    private MOnInfoListener mOnInfoListener;
    private MOnErrorListener mOnErrorListener;
    private MOnCompletionListener mOnCompletionListener;
    private MHandler mHandler;
    private MOnPlayListener mOnPlayListener;
    private RelativeLayout playCtrlRootV;

    private int seekTime, totaltime;
    private int SEEK_ONCE = 30 * 1000; // 30 sec
    private int curPlayStatus;
    private TextView totalTimeV, curTimeV, titleV, speedV, tipsV;
    private MOnSeekCompleteListener mOnSeekCompleteListener;
    private LoadWindow loadWindow;
    private MOnAdapterItemListener mOnAdapterItemListener;
    private ImageView operStatus;
    private boolean isSeekIng;
    private MovieObj movieObj;
    private String mUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.curPlayStatus = EXVAL.PLAY_NOREADY;
        this.mOnPlayListener = new MOnPlayListener(this);

        this.loadWindow = new LoadWindow(this);
        this.mHandler = new MHandler(this);
        this.tipsV = findViewById(R.id.play_tips_v);
        this.playCtrlRootV = findViewById(R.id.play_ctrl_root_v);
        this.seeKBar = findViewById(R.id.seekbar);
        this.videoView = findViewById(R.id.videoView);
        this.bottomCtrlRootV = findViewById(R.id.play_bottom_ctrl_root_v);
        this.operStatus = findViewById(R.id.play_status_v);
        this.rv = findViewById(R.id.play_list_v);
        this.rv.setAdapter(this.rvAdapter = new PlayItemAdapter());
        this.rv.addItemDecoration(this.rvDecoration = new PlayItemDecoration(
                (int) AppMain.res().getDimension(R.dimen.collection_item_left_offset)));
        this.rv.setLayoutManager(this.rvLayoutMgr = new LinearLayoutManager(
                this, LinearLayoutManager.HORIZONTAL, false));
        this.rvAdapter.attachRecylerView(this.rv);

        this.totalTimeV = findViewById(R.id.play_ctrl_total_time_v);
        this.curTimeV = findViewById(R.id.play_ctrl_cur_time_v);
        this.titleV = findViewById(R.id.play_ctrl_title_v);
        this.speedV = findViewById(R.id.play_ctrl_speed_v);

        this.titleV.setSelected(true);

        this.seeKBar.setFocusable(false);
        this.seeKBar.setFocusableInTouchMode(false);

        this.videoView.setOnPreparedListener(mOnPreparedListener = new MOnPreparedListener(this));
        this.videoView.setOnCompletionListener(mOnCompletionListener = new MOnCompletionListener(this));
        this.videoView.setOnErrorListener(mOnErrorListener = new MOnErrorListener(this));
        this.videoView.setOnInfoListener(mOnInfoListener = new MOnInfoListener(this));
        this.videoView.setOnSeekCompleteListener(mOnSeekCompleteListener = new MOnSeekCompleteListener(this));

        Intent intent = getIntent();
        this.movieObj = intent.getParcelableExtra(EXVAL.MOVIE_OBJ);
        this.movieNum = intent.getIntExtra(EXVAL.MOVIE_ITEM_NUM, 0);

        this.movieName = this.movieObj == null ? "" : this.movieObj.getName();
        this.movieCid = this.movieObj == null ? "" : this.movieObj.getCid();
        this.movieId = this.movieObj == null ? -1 : this.movieObj.getMovieId();
        this.moviePos = this.movieObj == null ? 0 : this.movieObj.getPlayPos();

        MLog.d(TAG, String.format("moviePos=%d movieNum=%d", moviePos, movieNum));

        this.rvAdapter.initDatas(this.movieNum);
        this.rvAdapter.setSelPos(this.moviePos);

        this.rvAdapter.setOnAdapterItemListener(
                this.mOnAdapterItemListener = new MOnAdapterItemListener(this));

        showProgress(false);
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
        Utils.sendMsg(this.mHandler, MSG.PLAY_POS);
        DataCenter.Ins().addPlayListener(this.mOnPlayListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.execStoped = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void stopAct() {
        if (videoView.getCurrentPosition()>0) {
            int currentPosition = videoView.getCurrentPosition();
            new ProgremPlayerDB(this).instert(new ProgremPlayerInfo(movieName, mUrl, videoView.getCurrentPosition()));
        }
        MLog.d(TAG, "stopAct");
        if (this.videoView != null) {
            this.videoView.stopPlayback();
        }
        DataCenter.Ins().delPlayListener(this.mOnPlayListener);
    }

    @Override
    protected void destroyAct() {
        MLog.d(TAG, "destroyAct");
        Intent intent = new Intent();
        intent.putExtra(EXVAL.CUR_PLAY_POS, this.movieObj.getPlayPos());
        setResult(RESULT_OK, intent);
        hideProgress(0);
        DataCenter.Ins().stopNetSpeed();
        this.mHandler.removeCallbacksAndMessages(null);
        this.mHandler.release();
        this.mHandler = null;
        this.loadWindow.relase();
        this.loadWindow = null;
        this.mOnPlayListener.release();
        this.mOnPlayListener = null;
        this.mOnPreparedListener.release();
        this.mOnPreparedListener = null;
        this.mOnCompletionListener.release();
        this.mOnCompletionListener = null;
        this.mOnErrorListener.release();
        this.mOnErrorListener = null;
        this.mOnInfoListener.release();
        this.mOnInfoListener = null;
        this.mOnSeekCompleteListener.release();
        this.mOnSeekCompleteListener = null;
        this.mOnAdapterItemListener.release();
        this.mOnAdapterItemListener = null;
        this.videoView.release();
        this.videoView = null;
        this.playCtrlRootV = null;
        this.totalTimeV = null;
        this.curTimeV = null;
        this.titleV = null;
        this.speedV = null;
        this.rvAdapter.release();
        this.rvAdapter = null;
        this.rvDecoration = null;
        this.rvLayoutMgr = null;
    }

    @Override
    FocusBorder createFocusBorder() {
        return null;
    }

    @Override
    int layoutId() {
        return R.layout.play_act;
    }

    private static class MOnPreparedListener implements MediaPlayer.OnPreparedListener {

        private PlayAct hostCls;

        public MOnPreparedListener(PlayAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            if (hostCls == null) {
                return;
            }
            Utils.setVideoScale(EXVAL.SCREEN_FULL, hostCls, hostCls.videoView);
            hostCls.videoView.start();
            hostCls.curPlayStatus = EXVAL.PLAY_PREPARED;
            if (hostCls.bottomCtrlRootV != null) {
                hostCls.hideProgress(0);
                hostCls.showSeekBar();
                hostCls.hideSeekBar(5000);
            }
            hostCls.loadWindow.dismiss();
            hostCls.totaltime = hostCls.videoView == null ? 0 : hostCls.videoView.getDuration();
        }
    }

    private static class MOnCompletionListener implements MediaPlayer.OnCompletionListener {

        private PlayAct hostCls;

        public MOnCompletionListener(PlayAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (hostCls != null) {
                hostCls.curPlayStatus = EXVAL.PLAY_OVER;
                hostCls.resetDatas();
                Utils.sendMsg(hostCls.mHandler, MSG.PLAY_NEXT);
            }
        }
    }

    private static class MOnErrorListener implements MediaPlayer.OnErrorListener {

        private PlayAct hostCls;

        public MOnErrorListener(PlayAct hostCls) {
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

        private PlayAct hostCls;

        public MOnInfoListener(PlayAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {

            MLog.d(TAG, "onInfo " + what);
            if (hostCls == null) {
                return false;
            }

            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//                    hostCls.showProgress(false);
                    hostCls.loadWindow.show(hostCls.videoView);
                    hostCls.loadWindow.updateLoadSpeed(hostCls.speedV.getText().toString());
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    if (!hostCls.isSeekIng) {
                        hostCls.rewindAndForwardInit = false;
                        hostCls.hideSeekBar(300);
                    }
                    if (hostCls.loadWindow != null) {
                        hostCls.loadWindow.dismiss();
                    }
                    break;
            }
            return false;
        }
    }

    private static class MOnSeekCompleteListener implements MediaPlayer.OnSeekCompleteListener {

        private PlayAct hostCls;

        public MOnSeekCompleteListener(PlayAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onSeekComplete(MediaPlayer mp) {
            MLog.d(TAG, "onSeekComplete");
            if (hostCls != null) {
                hostCls.isSeekIng = false;
                if (hostCls.curPlayStatus == EXVAL.PLAY_PAUSE) {
                    hostCls.curPlayStatus = EXVAL.PLAY_PREPARED;
                    if (hostCls.operStatus != null && hostCls.operStatus.isShown()) {
                        hostCls.operStatus.setVisibility(View.GONE);
                    }
                    if (hostCls.videoView != null) {
                        hostCls.videoView.start();
                    }
                }
            }
        }
    }

    private static class MOnPlayListener implements OnPlayListener {

        private PlayAct hostCls;

        public MOnPlayListener(PlayAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onPlay(final String url) {
            hostCls.mUrl = url;
            hostCls.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        hostCls.videoView.setVideoPath(url);
//                    hostCls.videoView.setVideoPath("http://150.116.81.67:45001/1");
                    } catch (Exception e) {
                    }

                    final long position = new ProgremPlayerDB(hostCls).getPosition(new ProgremPlayerInfo(hostCls.movieObj.getName(), url, 0));
                    if (position>0){
                        final AlertDialog alertDialog = new AlertDialog.Builder(hostCls)
                                .setTitle("提示")
                                .setMessage("检测到你播放过该视频，是否跳到上回播放的地方？")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        hostCls.videoView.seekTo((int) position);

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
                }
            });
        }

        @Override
        public void onNetSpeed(final String speedStr) {
            MLog.d(TAG, "onNetSpeed " + speedStr);
            if (hostCls != null) {
                hostCls.runOnUiThread(new Runnable() {
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
        Utils.sendMsg(this.mHandler, MSG.MSG_SEEK, 300);
    }

    private void seekByProgressBar() {
        int dest = this.seeKBar.getCurPorgress();
        int pos = this.totaltime * (dest + 1) / 100;
        // check for small stream while seeking
        int pos_check = this.totaltime * (dest + 1) - pos * 100;
        if (pos_check > 0) {
            pos += 1;
        }
        // 防止IJK在拖动到最后的时候，seek到totaltime时加载不出视频，故减去1秒
        if (pos >= this.totaltime) {
            pos = this.totaltime - 1000;
        }
        if (dest <= 1) {
            pos = 0;
        }
        this.videoView.seekTo(pos);
        this.loadWindow.show(this.videoView);
        this.loadWindow.updateLoadSpeed(this.speedV.getText().toString());
    }

    private void hideSeekBar(int timeout) {
        Utils.sendMsg(this.mHandler, MSG.MSG_HIDE_SEEK_BAR, timeout);
    }

    private void showSeekBar() {
        hideSeekBar(0);
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

            int curtime = getCurrentPosition();

            Log.d(TAG, "curtime = " + curtime);

            this.totalTimeV.setText(secToTime(this.totaltime / 1000));
            if (this.totaltime != 0) {
                int curtimetmp = curtime / 1000;
                int totaltimetmp = this.totaltime / 1000;
                if (totaltimetmp != 0) {
                    int progress = curtimetmp * 100 / totaltimetmp;
                    MLog.d(TAG, "progress = " + progress);
                    this.seeKBar.setProgress(progress);
                    this.seeKBar.setSecondaryProgress(progress);
                    this.seeKBar.setCurProgress(progress);
                    String curTimeStr = secToTime(curtime / 1000);
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

    private void showProgress(boolean isList) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("isList", isList);
        Utils.sendMsg(this.mHandler, bundle, MSG.MSG_SHOW_PROGRESS);
    }

    private void hideProgress(int timeout) {
        Utils.sendMsg(this.mHandler, MSG.MSG_HIDE_PROGRESS, timeout);
    }

    private static class MHandler extends Handler {

        private PlayAct hostCls;

        public MHandler(PlayAct hostCls) {
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
                    if (!hostCls.isSeekIng) {
                        MLog.d(TAG, "into MSG.MSG_UPDATE_PROGRESS");
                        int pos = hostCls.getCurrentPosition();
                        hostCls.updateProgressBar(false);
                        Utils.sendMsg(hostCls.mHandler, MSG.MSG_UPDATE_PROGRESS, 1000 - (pos % 1000));
                    }
                    break;
                case MSG.MSG_SHOW_SEEK_BAR:
                    hostCls.startUpdateSeek(0);
                    hostCls.bottomCtrlRootV.setVisibility(View.VISIBLE);
                    if (!hostCls.rv.isShown()) {
                        hostCls.playCtrlRootV.setVisibility(View.VISIBLE);
                    }
                    DataCenter.Ins().execNetSpeed();
                    break;
                case MSG.MSG_HIDE_SEEK_BAR:
                    hostCls.stopUpdateSeek();
                    hostCls.bottomCtrlRootV.setVisibility(View.GONE);
                    hostCls.playCtrlRootV.setVisibility(View.GONE);
                    if (!hostCls.loadWindow.isShown()) {
                        DataCenter.Ins().stopNetSpeed();
                    }
                    break;
                case MSG.MSG_SHOW_PROGRESS:
                    Bundle bundle = msg.getData();
                    if (!hostCls.bottomCtrlRootV.isShown()) {
                        if (!hostCls.loadWindow.isShown()) {
                            DataCenter.Ins().stopNetSpeed();
                        }
                        hostCls.bottomCtrlRootV.setVisibility(View.VISIBLE);
                        if (bundle.getBoolean("isList")) {
                            hostCls.rvAdapter.setSelPos(hostCls.moviePos);
                            hostCls.rv.setVisibility(View.VISIBLE);
                            hostCls.rv.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        hostCls.rvAdapter.setSelection();
                                    } catch (Exception e) {
                                    }
                                }
                            });
                        } else if (!hostCls.rv.isShown()) {
                            hostCls.showSeekBar();
                        }
                        hostCls.hideProgress(5000);
                    }
                    break;
                case MSG.MSG_HIDE_PROGRESS:
                    hostCls.bottomCtrlRootV.setVisibility(View.GONE);
                    if (hostCls.playCtrlRootV.isShown()) {
                        if (!hostCls.loadWindow.isShown()) {
                            DataCenter.Ins().stopNetSpeed();
                        }
                        hostCls.hideSeekBar(0);
                    }
                    hostCls.rv.setVisibility(View.GONE);
                    hostCls.playCtrlRootV.setVisibility(View.GONE);
                    break;
                case MSG.MSG_SEEK:
                    hostCls.seekByProgressBar();
                    break;
                case MSG.PLAY_NEXT:
                    hostCls.moviePos++;
                    if (hostCls.moviePos >= hostCls.movieNum) {
                        hostCls.moviePos = hostCls.movieNum;
                        hostCls.curPlayStatus = EXVAL.PLAY_OVER;
                        hostCls.resetDatas();
                        hostCls.tipsV.setVisibility(View.VISIBLE);
                        hostCls.loadWindow.dismiss();
                    } else {
                        Utils.sendMsg(hostCls.mHandler, MSG.PLAY_POS, 100);
                    }
                    break;
                case MSG.PLAY_PREV:
                    hostCls.moviePos--;
                    if (hostCls.moviePos < 0) {
                        hostCls.moviePos = 0;
                    } else {
                        Utils.sendMsg(hostCls.mHandler, MSG.PLAY_POS, 100);
                    }
                    break;
                case MSG.PLAY_POS:
                    hostCls.resetDatas();
                    if (hostCls.videoView != null) {
                        hostCls.videoView.stopPlayback();
                    }
                    if (hostCls.moviePos >= 0 && hostCls.moviePos < hostCls.movieNum) {
                        if (hostCls.movieObj != null) {
                            hostCls.movieObj.setPlayPos(hostCls.moviePos);
                        }
                        hostCls.titleV.setText(
                                AppMain.res().getString(R.string.play_act_title_fmt,
                                        hostCls.movieName, hostCls.moviePos + 1));
                        hostCls.tipsV.setVisibility(View.GONE);
                        DataCenter.Ins().sendForceTVHttpReq(hostCls.movieObj);
                        hostCls.videoView.post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    hostCls.loadWindow.show(hostCls.videoView);
                                    hostCls.loadWindow.updateLoadSpeed(hostCls.speedV.getText().toString());
                                } catch (Exception e) {
                                }
                            }
                        });
                    } else {
                        hostCls.tipsV.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    }

    private boolean rewindAndForwardInit = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown");
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (!this.execStoped && !this.execDestroyed && this.bottomCtrlRootV != null &&
                    this.bottomCtrlRootV.isShown()) {
                if (this.playCtrlRootV.isShown()) {
                    hideSeekBar(0);
                } else {
                    hideProgress(0);
                }
                return true;
            }else {
                long l = System.currentTimeMillis();
                if (l-checkBackTime > CHECK_BREAK_VALID_TIME){
                    checkBackTime = l;
                    toastMessage(getString(R.string.again_back_play));
                    return true;
                }
            }
            return super.onKeyDown(keyCode, event);
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (this.curPlayStatus == EXVAL.PLAY_PREPARED ||
                        this.curPlayStatus == EXVAL.PLAY_PAUSE) {
                    showSeekBar();
                    hideSeekBar(5000);
                    if (this.videoView.isPlaying()) {
                        this.curPlayStatus = EXVAL.PLAY_PAUSE;
                        showStatus(R.drawable.pause_status);
                        this.videoView.pause();
                    } else {
                        this.curPlayStatus = EXVAL.PLAY_PREPARED;
                        if (this.operStatus.isShown()) {
                            this.operStatus.setVisibility(View.GONE);
                        }
                        this.videoView.start();
                    }
                } else if (this.curPlayStatus == EXVAL.PLAY_OVER) {

                }
                break;
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (this.curPlayStatus == EXVAL.PLAY_PREPARED ||
                        this.curPlayStatus == EXVAL.PLAY_PAUSE) {
                    if (!this.rv.isShown() && !this.bottomCtrlRootV.isShown()) {
                        this.showSeekBar();
                    } else if (this.playCtrlRootV.isShown()) {
                        this.isSeekIng = true;
                        this.stopUpdateSeek();
                        this.hideSeekBar(5000);
                        if (!this.rewindAndForwardInit) {
                            this.rewindAndForwardInit = true;
                            this.seekTime = getCurrentPosition();
                        }
                        this.seekTime -= SEEK_ONCE;
                        if (this.seekTime < 0) {
                            this.seekTime = 0;
                        }
                        float curtimetmp = (float) this.seekTime / 1000;
                        float totaltimetmp = (float) this.totaltime / 1000;
                        if (totaltimetmp != 0) {
                            int progress = (int) (curtimetmp * 100 / (float) totaltimetmp);
                            this.seeKBar.setProgress(progress);
                            this.seeKBar.setCurProgress(progress);
                            String curTimeStr = secToTime((int) curtimetmp);
                            this.seeKBar.setNumText(curTimeStr);
                            this.curTimeV.setText(curTimeStr);
                        }
                        sendSeekMsg();
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                if (this.curPlayStatus == EXVAL.PLAY_PREPARED ||
                        this.curPlayStatus == EXVAL.PLAY_PAUSE) {
                    if (!this.rv.isShown() && !this.bottomCtrlRootV.isShown()) {
                        showSeekBar();
                    } else if (this.playCtrlRootV.isShown()) {
                        this.isSeekIng = true;
                        stopUpdateSeek();
                        hideSeekBar(5000);
                        if (!this.rewindAndForwardInit) {
                            this.rewindAndForwardInit = true;
                            this.seekTime = getCurrentPosition();
                        }
                        this.seekTime += SEEK_ONCE;
                        if (this.seekTime > this.totaltime) {
                            this.seekTime = this.totaltime;
                        }
                        float curtimetmp = (float) this.seekTime / 1000;
                        float totaltimetmp = (float) this.totaltime / 1000;
                        if (totaltimetmp != 0) {
                            int progress = (int) (curtimetmp * 100 / (float) totaltimetmp);
                            this.seeKBar.setProgress(progress);
                            this.seeKBar.setCurProgress(progress);
                            String curTimeStr = secToTime((int) curtimetmp);
                            this.seeKBar.setNumText(curTimeStr);
                            this.curTimeV.setText(curTimeStr);
                        }
                        sendSeekMsg();
                    }
                }
                break;
            case KeyEvent.KEYCODE_MENU:
                hideProgress(0);
                showProgress(true);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                Utils.sendMsg(mHandler, MSG.PLAY_NEXT);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                Utils.sendMsg(mHandler, MSG.PLAY_PREV);
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    public boolean isShowBottomProgress() {
        return this.bottomCtrlRootV.isShown() && this.playCtrlRootV.isShown();
    }

    private static class MOnAdapterItemListener implements OnRvAdapterListener {

        private PlayAct hostCls;

        public MOnAdapterItemListener(PlayAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onItemSelected(View view, int position) {
            if (hostCls == null) {
                return;
            }
            hostCls.hideProgress(5000);
        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            if (hostCls == null) {
                return;
            }
            hostCls.hideProgress(5000);
        }

        @Override
        public void onItemClick(View view, int position) {
            if (hostCls == null) {
                return;
            }
            if (position != hostCls.moviePos) {
                hostCls.moviePos = position;
                Utils.sendMsg(hostCls.mHandler, MSG.PLAY_POS, 100);
            }
            hostCls.hideProgress(0);
        }

        @Override
        public void onItemLongClick(View view, int position) {

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
        execStop();
        execDestroy();
        super.onBackPressed();
    }

    private void resetDatas() {
        this.totaltime = 0;
        this.seekTime = 0;
        String curTimeStr = secToTime(0);
        if (this.seeKBar != null) {
            this.seeKBar.setProgress(0);
            this.seeKBar.setCurProgress(0);
            this.seeKBar.setNumText(curTimeStr);
        }
        if (this.totalTimeV != null) {
            this.totalTimeV.setText(secToTime(this.totaltime / 1000));
        }
        if (this.curTimeV != null) {
            this.curTimeV.setText(curTimeStr);
        }
        if (this.videoView != null) {
            this.videoView.stopPlayback();
        }
        if (this.operStatus != null) {
            this.operStatus.setVisibility(View.GONE);
        }
        this.hideProgress(0);
        this.hideSeekBar(0);
        DataCenter.Ins().stopNetSpeed();
    }

}
