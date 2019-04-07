package com.allcn.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.utils.MSG;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

public class TipsDialog extends Dialog {

    private static final String TAG = TipsDialog.class.getSimpleName();
    private TextView tipsV1, tipsV2;
    private String tips1Str, tips2Str;
    private Activity activity;
    private StringBuilder keyBuilder;
    private MHandler mHandler;

    public TipsDialog(@NonNull Context context) {
        this(context, 0);
    }

    public TipsDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        activity = (Activity) context;
    }

    public void setShowTips(String tips1Str, String tips2Str) {
        this.tips1Str = tips1Str;
        this.tips2Str = tips2Str;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.live_tips_dialog_layout);

        mHandler = new MHandler(this);
        keyBuilder = new StringBuilder();

        tipsV1 = findViewById(R.id.tips1);
        tipsV2 = findViewById(R.id.tips2);

        tipsV1.setText(tips1Str);

        if (!TextUtils.isEmpty(tips2Str)) {
            tipsV2.setVisibility(View.VISIBLE);
            tipsV2.setText(tips2Str);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {


        if ((KeyEvent.KEYCODE_0 <= keyCode) && (keyCode <= KeyEvent.KEYCODE_9)) {
            keyBuilder.append(String.valueOf(keyCode - 7));
            Utils.sendMsg(mHandler, MSG.TIPS_RESET_KEY, 5000L);
            if (!TextUtils.isEmpty(tips2Str) && keyBuilder.toString().equals(tips2Str)) {
                keyBuilder.setLength(0);
                dismiss();
            }
        } else if (KeyEvent.KEYCODE_BACK == keyCode) {
            keyBuilder.setLength(0);
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MLog.d(TAG, "onStop");
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.release();
            mHandler = null;
        }
        tips1Str = null;
        tips2Str = null;
        tipsV1 = null;
        tipsV2 = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MLog.d(TAG, "onBackPressed");
        if (TextUtils.isEmpty(tips2Str)) {
            activity.finish();
            activity = null;
        }
    }

    private static class MHandler extends Handler {

        private TipsDialog hostCls;

        public MHandler(TipsDialog hostCls) {
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
                case MSG.TIPS_RESET_KEY:
                    Utils.removeMsg(this, MSG.TIPS_RESET_KEY);
                    hostCls.keyBuilder.setLength(0);
                    break;
            }
        }
    }
}
