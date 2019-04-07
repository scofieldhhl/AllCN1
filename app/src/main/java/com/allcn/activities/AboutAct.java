package com.allcn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.views.focus.FocusBorder;
import com.mast.lib.utils.NetUtils;
import com.mast.lib.utils.Utils;

public class AboutAct extends BaseActivity {

    private TextView verV, macV, contentV, moreV;
    private MOnClickListener mOnClickListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        decorView.setBackgroundResource(R.drawable.set_bg);

        this.mOnClickListener = new MOnClickListener(this);
        this.verV = findViewById(R.id.about_ver_text);
        this.macV = findViewById(R.id.about_mac_text);
        this.contentV = findViewById(R.id.about_text_v);
        this.moreV = findViewById(R.id.about_more_v);
        this.moreV.setOnClickListener(this.mOnClickListener);

        this.contentV.setText(AppMain.res().getString(R.string.about_all_content));

        try {
            this.verV.setText(
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (Exception e) {}

        this.macV.setText(NetUtils.Ins(AppMain.ctx()).getMac());

        this.verV.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Utils.focusV((View) AboutAct.this.verV.getParent(), true);
                } catch (Exception e) {}
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
        DataCenter.Ins().setActivityType(EXVAL.TYPE_ACTIVITY_OTH);
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

    }

    @Override
    protected void destroyAct() {
        this.mOnClickListener.release();
        this.mOnClickListener = null;
        this.moreV.setOnClickListener(null);
        this.moreV = null;
        this.verV = null;
        this.macV = null;
        this.contentV = null;
    }

    @Override
    FocusBorder createFocusBorder() {
        return null;
    }

    @Override
    int layoutId() {
        return R.layout.about_act;
    }

    private static class MOnClickListener implements View.OnClickListener {

        private AboutAct hostCls;

        public MOnClickListener(AboutAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View view) {

            if (view == hostCls.moreV) {
                Intent intent = new Intent(hostCls, AboutFullTextAct.class);
                hostCls.startActivity(intent);
            }
        }
    }
}
