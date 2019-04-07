package com.allcn.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.interfaces.OnDataListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.utils.MSG;
import com.allcn.views.FrameAnimation;
import com.allcn.views.PayDialog;
import com.allcn.views.focus.FocusBorder;
import com.datas.HomeMovie;
import com.datas.LiveChalObj;
import com.mast.lib.parsers.MarqueeParser;
import com.mast.lib.utils.Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeAct extends BaseActivity implements OnDataListener {
    @BindView(R.id.image_view)
    ImageView imageView;

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
    private MHandler mHandler;
    private PayDialog payDialog;
    private AlertDialog tipsDialog;
    private boolean animationEnd;
    private FrameAnimation frameAnimation;

    private static final String TAG = "WelcomeAct";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        imageView.setImageResource(R.drawable.welcome_anim);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.start();

        DataCenter.Ins().login();
        mHandler = new MHandler(this);


        frameAnimation = new FrameAnimation(imageView, startImgArr,
                EXVAL.START_ALLTV_ANIM_DURATION, true);

        frameAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {

            }

            @Override
            public void onAnimationEnd() {
                animationEnd = true;
                //frameAnimation.pauseAnimation();
                //boolean dataOK = DataCenter.Ins().isDataOK();
                //if (!execStoped&&DataCenter.Ins().isLoginOK() && DataCenter.Ins().isDataOK() &&
                //        !DataCenter.Ins().isExpired()) {
                ////if (execStoped&&){
                //    startActivity(new Intent(WelcomeAct.this, MainActivity.class));
                //    finish();
                //}
            }

            @Override
            public void onAnimationRepeat() {
                Log.d(TAG, "onAnimationRepeat: ");
            }
        });
        DataCenter.Ins().addDataListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    protected void eventView() {

    }

    @Override
    protected void findView() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DataCenter.Ins().delDataListener(this);
    }

    @Override
    int layoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void stopAct() {

    }

    @Override
    protected void destroyAct() {

    }

    @Override
    FocusBorder createFocusBorder() {
        return null;
    }

    @Override
    public void onLoadInitDatas(SparseArray<HomeMovie> homeMovies) {

    }

    @Override
    public void onNetState(int netType, boolean isConnected) {

    }

    @Override
    public void onTimeDate(String time) {

    }

    @Override
    public void onUpdateApp(File apkF, String desStr) {

    }

    @Override
    public void onMarquee(MarqueeParser marqueeParser) {

    }

    @Override
    public void onForceTv() {

    }

    @Override
    public void onLogin(String s, boolean b) {
        if (!DataCenter.Ins().isLoginOK()) {
            Utils.sendLoginTipsMsg(mHandler, s, b,
                    MSG.SHOW_LOGIN_TIPS);
        } else {
            if (DataCenter.Ins().isExpired()) {
                Utils.sendMsg(mHandler, MSG.SHOW_PAY);
            } else {
                if (!TextUtils.isEmpty(s)) {
                    Utils.sendLoginTipsMsg(mHandler, s, b,
                            MSG.SHOW_LOGIN_TIPS);
                } else {
                    Utils.sendMsg(mHandler, MSG.HIDE_LOGIN_TIPS);
                    Utils.sendMsg(mHandler, MSG.INTO_UI);
                }
            }
        }
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
    public void onFavUpdate() {

    }

    @Override
    public void onChalList(LiveChalObj chal, boolean liveDataDBOK) {

    }

    private static class MHandler extends Handler {

        private WelcomeAct hostCls;

        public MHandler(WelcomeAct hostCls) {
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
                    if (DataCenter.Ins().isLoginOK() && DataCenter.Ins().isDataOK() &&
                            !DataCenter.Ins().isExpired()) {
                        //if (hostCls.frameAnimation != null) {
                        //    hostCls.frameAnimation.release();
                        //    hostCls.frameAnimation = null;
                        //}
                        //if (hostCls.startV != null) {
                        //    hostCls.startV.setVisibility(View.GONE);
                        //}
                        //hostCls.startImgArr = null;
                        //hostCls.focusBorderVisible(true);

                        if (hostCls.animationEnd){
                            hostCls.startActivity(new Intent(hostCls, MainActivity.class));
                            hostCls.finish();
                        }
                    }
                    break;
                case MSG.EXIT_APP:
                    hostCls.finish();
                    break;
            }
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
        tipsDialog = new AlertDialog.Builder(this, R.style.TipsDialog)
                .setView(rootV)
                .setCancelable(dialogCancelable)
                .create();
        tipsDialog.show();
    }

    private void hideTips() {
        if (tipsDialog != null) {
            tipsDialog.dismiss();
            tipsDialog = null;
        }
    }
}
