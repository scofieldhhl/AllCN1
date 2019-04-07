package com.allcn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.adapters.HotReDetailsMovieAdapter;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.views.FocusKeepRecyclerView;
import com.allcn.views.decorations.HotReDetailsMovieItemDecoration;
import com.allcn.views.focus.FocusBorder;
import com.datas.CKindObj;
import com.datas.MovieDetailsObj;
import com.datas.MovieObj;

import java.util.List;

public class HotReDetailsAct extends BaseActivity {

    private static final String TAG = HotReDetailsAct.class.getSimpleName();

    private TextView titleV;
    private FocusKeepRecyclerView rv;
    private TextView detailsTextV;
    private HotReDetailsMovieAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundResource(0);
        FrameLayout contentV = findViewById(android.R.id.content);
        contentV.setClipChildren(false);
        contentV.setClipToPadding(false);
        contentV.setBackgroundResource(R.drawable.hot_re_bg);
        this.titleV = findViewById(R.id.hot_re_details_title_v);
        this.rv = findViewById(R.id.hot_re_details_rv);
        this.detailsTextV = findViewById(R.id.hot_re_details_text_v);

        Intent intent = getIntent();
        String titleText = intent.getStringExtra(EXVAL.TITLE_TEXT);
        String cid = intent.getStringExtra(EXVAL.CID);

        if (TextUtils.isEmpty(cid)) {
            finish();
        }

        if (TextUtils.isEmpty(titleText)) {
            titleText = "";
        } else {
            titleText = titleText.replaceAll("\n", " ");
        }

        this.titleV.setText(titleText);
        this.rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                false));
        this.rv.addItemDecoration(new HotReDetailsMovieItemDecoration());
        this.rv.setAdapter(this.adapter = new HotReDetailsMovieAdapter(this));

        if (cid.equals(EXVAL.ZT_HOT_RE_CID)) {
            DataCenter.Ins().scanAllCkinds(this, cid);
        } else {
            DataCenter.Ins().scanAllMovies(this, cid);
        }
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void stopAct() {

    }

    @Override
    protected void destroyAct() {
        if (this.adapter != null) {
            this.adapter.release();
            this.adapter = null;
        }
    }

    @Override
    protected FocusBorder createFocusBorder() {
        return new FocusBorder.Builder()
                .asDrawable()
                .borderResId(R.drawable.kind_item_f)
                .needDuplicateIdleStatus(true)
                .build(this);
    }

    @Override
    protected int layoutId() {
        return R.layout.hot_re_details_act;
    }

    public void loadMovieDatas(final List<? extends Object> datas, final int dataNum) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HotReDetailsAct.this.adapter.setDatas(datas, dataNum);
                if (dataNum > 0) {
                    Object object = datas.get(0);
                    if (object instanceof MovieObj) {
                        DataCenter.Ins().scanMovieDetails(HotReDetailsAct.this,
                                (MovieObj) object);
                    } else if (object instanceof CKindObj) {
                        HotReDetailsAct.this.loadCkindName((CKindObj) object);
                    }
                }
                HotReDetailsAct.this.rv.post(new Runnable() {
                    @Override
                    public void run() {
                        HotReDetailsAct.this.adapter.setSelection(0);
                    }
                });
            }
        });
    }

    public void loadDetails(final MovieDetailsObj movieDetailsObj) {
        if (movieDetailsObj == null || movieDetailsObj.getName().equals(this.adapter.getDataForItem(this.adapter.getCurSelIndex()))) {
            return;
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HotReDetailsAct.this.detailsTextV.setText(String.format("%s\n%s\n%s",
                        AppMain.res().getString(R.string.dy_fmt, movieDetailsObj.getDirectors()),
                        AppMain.res().getString(R.string.zy_fmt, movieDetailsObj.getCasts()),
                        AppMain.res().getString(R.string.intro, movieDetailsObj.getSummary())
                ));
            }
        });
    }

    public void loadCkindName(final CKindObj cKindObj) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String name = cKindObj == null ? "" : cKindObj.getName();
                HotReDetailsAct.this.detailsTextV.setText(name);
            }
        });
    }
}
