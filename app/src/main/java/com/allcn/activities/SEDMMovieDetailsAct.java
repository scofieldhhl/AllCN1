package com.allcn.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.adapters.SedmMovieDetailsReAdapter;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.utils.GlideUtils;
import com.allcn.views.FocusKeepRecyclerView;
import com.allcn.views.MoreWindow;
import com.allcn.views.SelectionWindow;
import com.allcn.views.decorations.SedmHotReDetailsMovieItemDecoration;
import com.allcn.views.focus.FocusBorder;
import com.datas.MovieDetailsObj;
import com.datas.MovieObj;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.List;

public class SEDMMovieDetailsAct extends BaseActivity {

    private static final String TAG = SEDMMovieDetailsAct.class.getSimpleName();

    private ImageView imgV;
    private TextView jjv, titleV, lxV, dqV, dyV;
    private FocusKeepRecyclerView reRv;
    private SedmMovieDetailsReAdapter reAdapter;
    private SelectionWindow selectionWindow;
    private View rootV;
    private MOnClickListener mOnClickListener;
    private Button playV, favV, selV, moreV;
    private MovieObj movieObj;
    private MovieDetailsObj movieDetailsObj;
    private String favStr, unFavStr;
    private OnRvAdapterListenerForMovie onRvAdapterListenerForMovie;
    /**
     * order true:顺序 false:倒序
     */
    private boolean curFavStatus, initFavStatus, isHistory, order, reFavChanged;
    private MOnKeyListener mOnKeyListener;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MoreWindow moreWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.moreV = findViewById(R.id.sedm_details_more_v);
        this.imgV = findViewById(R.id.sedm_details_img_v);
        this.reRv = findViewById(R.id.sedm_details_re_rv);
        this.jjv = findViewById(R.id.sedm_details_jj_v);
        this.titleV = findViewById(R.id.sedm_details_title_v);
        this.lxV = findViewById(R.id.sedm_details_lx_v);
        this.dqV = findViewById(R.id.sedm_details_dq_v);
        this.dyV = findViewById(R.id.sedm_details_dy_v);
        this.playV = findViewById(R.id.sedm_details_play_v);
        this.favV = findViewById(R.id.sedm_details_fav_v);
        this.selV = findViewById(R.id.sedm_details_selection_v);
        this.rootV = (View) this.reRv.getParent();

        this.favStr = AppMain.res().getString(R.string.fav);
        this.unFavStr = AppMain.res().getString(R.string.unfav);

        this.moreWindow = new MoreWindow(true);

        for (ViewGroup parent = (ViewGroup) this.titleV.getParent(); parent != null;
             parent = (ViewGroup) parent.getParent()) {
            parent.setBackgroundResource(0);
            parent.setClipToPadding(false);
            parent.setClipChildren(false);
        }

        findViewById(android.R.id.content).setBackgroundResource(R.drawable.sedm_main_bg);

        this.reRv.addItemDecoration(new SedmHotReDetailsMovieItemDecoration());
        this.reRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,
                false));

        this.reRv.setAdapter(this.reAdapter = new SedmMovieDetailsReAdapter(this));

//        this.titleV.setTypeface(AppMain.getJdjquFont());

        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnClickListener = new MOnClickListener(this);
        this.mOnKeyListener = new MOnKeyListener(this);
        this.playV.setOnClickListener(this.mOnClickListener);
        this.favV.setOnClickListener(this.mOnClickListener);
        this.selV.setOnClickListener(this.mOnClickListener);
        this.moreV.setOnClickListener(this.mOnClickListener);
        this.playV.setOnKeyListener(this.mOnKeyListener);
        this.favV.setOnKeyListener(this.mOnKeyListener);
        this.selV.setOnKeyListener(this.mOnKeyListener);
        this.moreV.setOnKeyListener(this.mOnKeyListener);
        this.playV.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.favV.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.selV.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.moreV.setOnFocusChangeListener(this.mOnFocusChangeListener);

        this.onRvAdapterListenerForMovie = new OnRvAdapterListenerForMovie(this);

        this.reAdapter.setOnRvAdapterListener(this.onRvAdapterListenerForMovie);

        Intent intent = getIntent();
        this.movieObj = intent.getParcelableExtra(EXVAL.MOVIE_OBJ);

        DataCenter.Ins().scanMovieDetails(this, this.movieObj);
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
        this.execStoped = false;
        DataCenter.Ins().scanPlayPos(this, this.movieObj);
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
    FocusBorder createFocusBorder() {
        return new FocusBorder.Builder()
                .asColor()
                .borderColor(Color.TRANSPARENT)
                .shadowWidth(10)
                .shadowColor(Color.TRANSPARENT)
                .build(this);
    }

    @Override
    int layoutId() {
        return R.layout.sedm_movie_details_act;
    }

    @Override
    protected void stopAct() {

    }

    @Override
    protected void destroyAct() {
        DataCenter.Ins().stopScanMovieDetails();
        if (this.curFavStatus != this.initFavStatus || this.reFavChanged) {
            DataCenter.Ins().handleFavMovie(this.movieObj, this.curFavStatus);
        }
        if (this.moreWindow != null) {
            this.moreWindow.relase();
            this.moreWindow = null;
        }
        if (this.mOnClickListener != null) {
            this.mOnClickListener.release();
            this.mOnClickListener = null;
        }
        if (this.onRvAdapterListenerForMovie != null) {
            this.onRvAdapterListenerForMovie.release();
            this.onRvAdapterListenerForMovie = null;
        }
        if (this.mOnKeyListener != null) {
            this.mOnKeyListener.release();
            this.mOnKeyListener = null;
        }
        if (this.mOnFocusChangeListener != null) {
            this.mOnFocusChangeListener.release();
            this.mOnFocusChangeListener = null;
        }
        this.movieObj = null;
        this.movieDetailsObj = null;
        if (this.curFavStatus != this.initFavStatus || this.reFavChanged) {
            Intent intent = new Intent();
            intent.putExtra(EXVAL.FAV_CHANGED, true);
            setResult(RESULT_OK, intent);
        }
    }

    @Override
    public void onBackPressed() {
        execStop();
        execDestroy();
        super.onBackPressed();
    }

    public void loadDetails(final MovieDetailsObj movieDetailsObj,
                            final List<MovieObj> reMovieObjs, final boolean isFav,
                            final boolean isHistory) {
        if (movieDetailsObj == null) {
            return;
        }
        this.movieDetailsObj = movieDetailsObj;
        this.curFavStatus = isFav;
        this.initFavStatus = isFav;
        this.isHistory = isHistory;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    SEDMMovieDetailsAct.this.titleV.setText(movieDetailsObj.getName());
                    GlideUtils.Ins().loadUrlImg(SEDMMovieDetailsAct.this,
                            movieDetailsObj.getImgUrl(), SEDMMovieDetailsAct.this.imgV);
                    SEDMMovieDetailsAct.this.dyV.setText(AppMain.res().getString(R.string.sedm_dy_fmt,
                            movieDetailsObj.getDirectors()));
                    SEDMMovieDetailsAct.this.dqV.setText(AppMain.res().getString(R.string.sedm_dq_fmt,
                            movieDetailsObj.getCountries()));
                    SEDMMovieDetailsAct.this.lxV.setText(AppMain.res().getString(R.string.sedm_lx_fmt,
                            SEDMMovieDetailsAct.this.movieObj.getLabel()));
                    String jjStr = AppMain.res().getString(R.string.intro,
                            movieDetailsObj.getSummary());
                    SEDMMovieDetailsAct.this.jjv.setText(jjStr);
                    SEDMMovieDetailsAct.this.moreWindow.loadMoreText(jjStr);
                    SEDMMovieDetailsAct.this.favV.setText(isFav ?
                            SEDMMovieDetailsAct.this.unFavStr : SEDMMovieDetailsAct.this.favStr);
                    SEDMMovieDetailsAct.this.reAdapter.setDatas(reMovieObjs);
                    if (movieDetailsObj.getFilmIdNum() > 1) {
                        SEDMMovieDetailsAct.this.selV.setVisibility(View.VISIBLE);
                    }
                    Utils.focusV(SEDMMovieDetailsAct.this.playV, true);
                } catch (Exception e) {
                }
            }
        });
    }

    private static class MOnClickListener implements View.OnClickListener {

        private SEDMMovieDetailsAct hostCls;

        private MOnClickListener(SEDMMovieDetailsAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.sedm_details_selection_v: {
                    if (hostCls.movieDetailsObj != null){
                        this.hostCls.resetFocusBorder();
                        this.hostCls.focusBorderVisible(false);
                        this.hostCls.selectionWindow = new SelectionWindow(hostCls);
                        this.hostCls.selectionWindow.initDatas(
                                this.hostCls.movieDetailsObj.getFilmIdPageNum(),
                                this.hostCls.movieDetailsObj.getFilmIdNum(),
                                this.hostCls.movieObj.getPlayPos());
                        this.hostCls.selectionWindow.show(this.hostCls.rootV);
                    }
                    break;
                }
                case R.id.sedm_details_play_v: {
                    this.hostCls.startPlay(0);
                    break;
                }
                case R.id.sedm_details_fav_v: {
                    this.hostCls.curFavStatus = !this.hostCls.curFavStatus;
                    this.hostCls.favV.setText(
                            this.hostCls.curFavStatus ? this.hostCls.unFavStr : this.hostCls.favStr);
                    break;
                }
                case R.id.sedm_details_more_v: {
                    hostCls.moreWindow.show((View) v.getParent());
                    break;
                }
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private SEDMMovieDetailsAct hostCls;

        private MOnKeyListener(SEDMMovieDetailsAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (this.hostCls != null && (event.getAction() == KeyEvent.ACTION_DOWN)) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        this.hostCls.focusBorderVisible(false);
                        this.hostCls.reAdapter.marketCacheForFocus(true);
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT: {
                        if (v == this.hostCls.moreV) {
                            Utils.noFocus(v);
                            if (this.hostCls.selV.isShown()) {
                                Utils.focusV(this.hostCls.favV, false);
                                Utils.focusV(this.hostCls.playV, false);
                                this.hostCls.selV.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utils.focusV(hostCls.selV, true);
                                    }
                                });
                            } else {
                                Utils.focusV(this.hostCls.playV, false);
                                this.hostCls.selV.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utils.focusV(hostCls.favV, true);
                                    }
                                });
                            }
                        }
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        if (v == this.hostCls.selV) {
                            Utils.noFocus(this.hostCls.playV);
                            Utils.noFocus(this.hostCls.favV);
                            Utils.noFocus(v);
                            Utils.focusV(this.hostCls.moreV, true);
                        } else if (v == this.hostCls.favV && !this.hostCls.selV.isShown()) {
                            Utils.noFocus(this.hostCls.playV);
                            Utils.noFocus(v);
                            Utils.focusV(this.hostCls.moreV, true);
                        }
                        break;
                }
            }

            return false;
        }
    }

    private static class OnRvAdapterListenerForMovie implements OnRvAdapterListener {

        private SEDMMovieDetailsAct hostCls;

        public OnRvAdapterListenerForMovie(SEDMMovieDetailsAct hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void onItemSelected(View view, int position) {

        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            if (this.hostCls == null) {
                return;
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    this.hostCls.focusBorderVisible(false);
                    Utils.focusV(this.hostCls.playV, true);
                    break;
            }
        }

        @Override
        public void onItemClick(View view, int position) {

        }

        @Override
        public void onItemLongClick(View view, int position) {

        }

        @Override
        public void release() {
            this.hostCls = null;
        }
    }

    public void startPlay(int playPos) {
        if (movieDetailsObj == null || movieObj == null ||
                this.movieDetailsObj.getFilmIdNum() < 0) {
            return;
        }
        this.isHistory = true;
        this.movieObj.setPlayPos(playPos);
        Intent intent = new Intent();
        intent.setClass(this, PlayAct.class);
        intent.putExtra(EXVAL.MOVIE_OBJ, this.movieObj);
        intent.putExtra(EXVAL.MOVIE_ITEM_NUM, movieDetailsObj.getFilmIdNum());
        startActivityForResult(intent, EXVAL.PLAY_REQ_CODE);
    }

    public void loadPlayPos(final int playPos) {
        MLog.d(TAG, "loadPlayPos " + playPos);
        if (movieObj != null) {
            movieObj.setPlayPos(playPos);
        }
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (selectionWindow != null) {
                    if (selectionWindow.isRelease()) {
                        selectionWindow = null;
                    }
                    if (selectionWindow != null) {
                        selectionWindow.refresh(playPos);
                    }
                }
            }
        });
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private SEDMMovieDetailsAct hostCls;

        public MOnFocusChangeListener(SEDMMovieDetailsAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (this.hostCls == null) {
                return;
            }

            MLog.d(TAG, String.format("onFocusChange v=%s, hasFocus=%b", v, hasFocus));

            if (hasFocus) {
                this.hostCls.focusBorderForV(v, 1.20f, 1.20f);
            }
        }
    }
}
