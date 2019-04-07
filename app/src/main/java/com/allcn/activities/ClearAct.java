package com.allcn.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;

import com.allcn.R;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.views.focus.FocusBorder;
import com.umeng.analytics.MobclickAgent;

public class ClearAct extends BaseActivity {

    private FrameLayout clearHistoryV, clearFavV;
    private MOnClickListener mOnClickListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        decorView.setBackgroundResource(R.drawable.set_bg);

        this.mOnClickListener = new MOnClickListener(this);

        this.clearHistoryV = findViewById(R.id.clear_history_v);
        this.clearFavV = findViewById(R.id.clear_fav_v);

        this.clearHistoryV.setOnClickListener(this.mOnClickListener);
        this.clearFavV.setOnClickListener(this.mOnClickListener);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.mOnClickListener.release();
        this.mOnClickListener = null;
        this.clearHistoryV.setOnClickListener(null);
        this.clearFavV.setOnClickListener(null);
        this.clearHistoryV = null;
        this.clearFavV = null;
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
    int layoutId() {
        return R.layout.clear_layout;
    }

    private static class MOnClickListener implements View.OnClickListener {

        private ClearAct hostCls;

        public MOnClickListener(ClearAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {

            if (v == hostCls.clearHistoryV) {
                DataCenter.Ins().clearHistoryOrFav(EXVAL.PAGE_HISTORY);
            } else if (v == hostCls.clearFavV) {
                DataCenter.Ins().clearHistoryOrFav(EXVAL.PAGE_FAV);
            }

            AppMain.showToastTips(AppMain.res().getString(R.string.clear_over));
        }
    }
}
