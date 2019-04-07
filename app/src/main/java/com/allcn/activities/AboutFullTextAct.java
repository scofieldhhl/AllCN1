package com.allcn.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.views.focus.FocusBorder;

public class AboutFullTextAct extends BaseActivity {

    private TextView contentV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        decorView.setBackgroundResource(R.drawable.set_bg);

        this.contentV = findViewById(R.id.about_fulltext_text_v);
        this.contentV.setText(AppMain.res().getString(R.string.about_all_content));
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
        this.contentV = null;
    }

    @Override
    FocusBorder createFocusBorder() {
        return null;
    }

    @Override
    int layoutId() {
        return R.layout.about_fulltext_act;
    }
}
