package com.allcn.utils;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allcn.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

public class AppMain extends Application {

    private static Context mCtx;
    private static Resources mRes;
    private static LayoutInflater layoutInflater;
//    private static Typeface jdfquFont, jdjquFont;

    @Override
    public void onCreate() {
        super.onCreate();
        mCtx = this;
        mRes = getResources();
        layoutInflater = LayoutInflater.from(this);
//        jdfquFont = Typeface.createFromAsset(getAssets(), "fonts/JDFQU.TTF");
//        jdjquFont = Typeface.createFromAsset(getAssets(), "fonts/JDJQU.TTF");
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_BOX, null);
        UMConfigure.setLogEnabled(true);
        UMConfigure.setEncryptEnabled(true);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
    }

    public static Context ctx() {
        return mCtx;
    }

    public static Resources res() {
        return mRes;
    }

    public static View getView(int layoutId, ViewGroup root) {
        return layoutInflater.inflate(layoutId, root, false);
    }

//    public static Typeface getJdfquFont() {
//        return jdfquFont;
//    }
//
//    public static Typeface getJdjquFont() {
//        return jdjquFont;
//    }
    public static void showToastTips(String msgStr) {

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
}
