package com.allcn.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.adapters.SEDMCKindAdapter;
import com.allcn.adapters.SEDMMovieAdapter;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.EXVAL;
import com.allcn.utils.MSG;
import com.allcn.utils.ViewWrapper;
import com.allcn.views.FocusKeepRecyclerView;
import com.allcn.views.decorations.SEDMCKindItemDecoration;
import com.allcn.views.decorations.SEDMKindItemDecoration;
import com.allcn.views.decorations.SEDMMovieItemDecoration;
import com.allcn.views.focus.FocusBorder;
import com.blankj.utilcode.util.ScreenUtils;
import com.coorchice.library.SuperTextView;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.Arrays;
import java.util.List;

public class SEDMKindListAct extends BaseActivity {

    private static final String TAG = SEDMKindListAct.class.getSimpleName();
    private FocusKeepRecyclerView pkindRv, kindRv, cKindRv, movieRv;
    private TextView titleV, pageV;
    private SEDMCKindAdapter pKindAdapter, kindAdapter, cKindAdapter;
    private SEDMMovieAdapter movieAdapter;
    private GridLayoutManager movieLayoutMgr;
    private SEDMMovieItemDecoration movieItemDecoration;
    private SuperTextView searchV;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private int kindRvWidth, curShowLevel, cacheShowLevel;
    private MAnimatorListener mAnimatorListener;
    private ViewWrapper pkindWrapper, kindWrapper, movieWrapper, cKindWrapper;
    private FrameLayout contentV, pKindRootV, kindRootV, cKindRootV;
    private int cKindInitTranX, movieInitTranX, leftArrowWidth;
    private ObjectAnimator pkindWAnimator, kindAnimator, kindWAnimator, cKindAnimator, ckindWAnimator, movieAnimator, movieWAnimator;
    private PropertyValuesHolder kindWidthVH, kindTranXVH;
    private PropertyValuesHolder cKindWidthVH, cKindTranXVH;
    private PropertyValuesHolder pKindWidthVH;
    private PropertyValuesHolder movieTranXVH, movieWidthVH;
    private int screenWidth, screenHeight;
    private MHandler mHandler;
    private OnRvAdapterListenerForPkind pKindListener;
    private OnRvAdapterListenerForkind kindListener;
    private OnRvAdapterListenerForCkind cKindListener;
    private OnRvAdapterListenerForMovie movieListener;
    private boolean ckFocusInited, curIsFavCkind;
    private int movieWidth, pkindLeftOffset, cKindInitLeftOffset, movieInitLeftOffset;
    private View pkindBgV, kindBgV, cKindBgV;
    private int[] ckindNeedShowFlag = new int[]{
            EXVAL.NEED_BFSJ, //sedh
            EXVAL.NEED_BFSJ, //egyy
            EXVAL.NEED_YYPY_BFSJ, //jyxx
            EXVAL.NEED_BFSJ, //mxzq
            EXVAL.NEED_YYPY_BFSJ, //dmdy
            EXVAL.NEED_YYPY_BFSJ, //dmjj
    };
    private String[] ckindCidArr = new String[]{
            "92", //sedh
            "137", //egyy
            "138;117", //jyxx
            EXVAL.MXZQ_HY_CID, //mxzq
            "136;55", //dmdy
            "77;56", //dmjj
    };
    private String yypyStr, bfsjStr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int pKindIndex = intent.getIntExtra(EXVAL.HOME_INDEX, 0);
        int kindIndex = intent.getIntExtra(EXVAL.KIND_INDEX, 0);

        this.yypyStr = AppMain.res().getString(R.string.yypy);
        this.bfsjStr = AppMain.res().getString(R.string.bfsj);

        this.contentV = findViewById(android.R.id.content);

        this.mHandler = new MHandler(this);

        this.screenWidth = ScreenUtils.getScreenWidth();
        this.screenHeight = ScreenUtils.getScreenHeight();

//        MLog.d(TAG, String.format("Dev Screen(%dx%d)", this.screenWidth, this.screenHeight));

        this.curShowLevel = EXVAL.KIND_LIST_SHOW_MID_LEVEL;
        this.cacheShowLevel = this.curShowLevel;

        pKindRootV = findViewById(R.id.sedm_kind_movie_pkind_root_v);
        kindRootV = findViewById(R.id.sedm_kind_movie_kind_root_v);
        cKindRootV = findViewById(R.id.sedm_kind_movie_ckind_root_v);
        searchV = findViewById(R.id.sedm_kind_movie_search_v);
        titleV = findViewById(R.id.sedm_kind_movie_title_v);
        pageV = findViewById(R.id.sedm_kind_movie_page_v);
        pkindRv = findViewById(R.id.sedm_kind_movie_pkind_v);
        kindRv = findViewById(R.id.sedm_kind_movie_kind_v);
        cKindRv = findViewById(R.id.sedm_kind_movie_ckind_v);
        movieRv = findViewById(R.id.sedm_kind_movie_v);
        pkindBgV = findViewById(R.id.sedm_kind_moive_pkind_bg_v);
        cKindBgV = findViewById(R.id.sedm_kind_moive_ckind_bg_v);
        kindBgV = findViewById(R.id.sedm_kind_moive_kind_bg_v);

        for (ViewGroup parent = (ViewGroup) this.titleV.getParent(); parent != null;
             parent = (ViewGroup) parent.getParent()) {
            parent.setClipToPadding(false);
            parent.setClipChildren(false);
            parent.setBackgroundResource(0);
        }

        contentV.setBackgroundResource(R.drawable.sedm_main_bg);

//        this.titleV.setTypeface(AppMain.getJdfquFont());
//        this.pageV.setTypeface(AppMain.getJdfquFont());
//        this.searchV.setTypeface(AppMain.getJdfquFont());

        this.movieWidth = (int) AppMain.res().getDimension(R.dimen.sedm_movie_item_w);
        this.pkindLeftOffset = (int) AppMain.res().getDimension(
                R.dimen.sedm_kind_movie_pkind_root_leftOffset);

        this.kindRvWidth = (int) AppMain.res().getDimension(R.dimen.sedm_kind_movie_kind_v_w);

        int marginTop = (int) AppMain.res().getDimension(R.dimen.sedm_kind_root_marginTop);

        this.cKindInitLeftOffset = this.kindRvWidth + this.pkindLeftOffset * 2;

        this.movieInitLeftOffset = this.cKindInitLeftOffset + this.kindRvWidth + this.pkindLeftOffset;

        FrameLayout.LayoutParams cKindParams =
                (FrameLayout.LayoutParams) this.cKindRootV.getLayoutParams();
        cKindParams.setMargins(this.cKindInitLeftOffset, marginTop, 0, 0);

        this.cKindRootV.setLayoutParams(cKindParams);
        this.cKindRootV.requestLayout();

        FrameLayout.LayoutParams movieParams =
                (FrameLayout.LayoutParams) this.movieRv.getLayoutParams();
        movieParams.width = this.screenWidth - this.kindRvWidth * 2 - this.pkindLeftOffset * 2;
        movieParams.setMargins(this.movieInitLeftOffset, marginTop, 0, 0);

        this.movieRv.setLayoutParams(movieParams);
        this.movieRv.requestLayout();

        this.pkindRv.setTag("-SEDM-PKind");
        this.kindRv.setTag("-SEDM-Kind");
        this.cKindRv.setTag("-SEDM-CKind");
        this.movieRv.setTag("-SEDM-Movie");

        this.pkindRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        this.kindRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        this.cKindRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        this.movieRv.setLayoutManager(this.movieLayoutMgr =
                new GridLayoutManager(this, 4));

        SEDMKindItemDecoration kindItemDecoration = new SEDMKindItemDecoration();
        SEDMCKindItemDecoration cKindItemDecoration = new SEDMCKindItemDecoration();

        this.pkindRv.addItemDecoration(kindItemDecoration);
        this.kindRv.addItemDecoration(kindItemDecoration);
        this.cKindRv.addItemDecoration(cKindItemDecoration);
        this.movieRv.addItemDecoration(this.movieItemDecoration = new SEDMMovieItemDecoration());

        this.pKindAdapter = new SEDMCKindAdapter(this, "SEDM-PKindAdapter");
        this.pkindRv.setAdapter(this.pKindAdapter);
        this.kindAdapter = new SEDMCKindAdapter(this, "SEDM-KindAdapter");
        this.kindRv.setAdapter(this.kindAdapter);
        this.cKindAdapter = new SEDMCKindAdapter(this, "SEDM-CKindAdapter");
        this.cKindRv.setAdapter(this.cKindAdapter);
        this.movieAdapter = new SEDMMovieAdapter(this, this.movieLayoutMgr,
                this.movieItemDecoration);
        this.movieRv.setAdapter(this.movieAdapter);

        this.pKindAdapter.setMarketPos(pKindIndex);
        this.kindAdapter.setMarketPos(kindIndex);
        this.cKindAdapter.setMarketPos(0);

        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnClickListener = new MOnClickListener(this);
        this.mOnKeyListener = new MOnKeyListener(this);

        this.searchV.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.searchV.setOnClickListener(this.mOnClickListener);
        this.searchV.setOnKeyListener(this.mOnKeyListener);

        this.pkindWrapper = new ViewWrapper(this.pKindRootV);
        this.kindWrapper = new ViewWrapper(this.kindRootV);
        this.movieWrapper = new ViewWrapper(this.movieRv);
        this.cKindWrapper = new ViewWrapper(this.cKindRootV);
        //
        this.kindWidthVH = PropertyValuesHolder.ofInt("width", 0);
        this.kindTranXVH = PropertyValuesHolder.ofFloat("translationX", 0);
        //
        this.pKindWidthVH = PropertyValuesHolder.ofInt("width", 0);
        //
        this.cKindWidthVH = PropertyValuesHolder.ofInt("width", 0);
        this.cKindTranXVH = PropertyValuesHolder.ofFloat("translationX", 0);
        //
        this.movieTranXVH = PropertyValuesHolder.ofFloat("translationX", 0);
        this.movieWidthVH = PropertyValuesHolder.ofInt("width", 0);
        //
        this.kindAnimator = new ObjectAnimator();
        this.kindWAnimator = new ObjectAnimator();
        //
        this.cKindAnimator = new ObjectAnimator();
        this.ckindWAnimator = new ObjectAnimator();
        //
        this.pkindWAnimator = new ObjectAnimator();
        //
        this.movieAnimator = new ObjectAnimator();
        this.movieWAnimator = new ObjectAnimator();

        this.pKindAdapter.setOnRvListener(this.pKindListener =
                new OnRvAdapterListenerForPkind(this));
        this.kindAdapter.setOnRvListener(this.kindListener =
                new OnRvAdapterListenerForkind(this));
        this.cKindAdapter.setOnRvListener(this.cKindListener =
                new OnRvAdapterListenerForCkind(this));
        this.movieAdapter.setOnRvListener(this.movieListener =
                new OnRvAdapterListenerForMovie(this));

        this.pKindListener.setCacheSelIndex(pKindIndex);
        this.kindListener.setCacheSelIndex(kindIndex);

        this.cKindAdapter.setNeedMarket(false);
        this.loadKindList(kindIndex, false);

        this.movieRv.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
            @Override
            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                MLog.d(TAG, String.format("oldFocus=%s\nnewFocus=%s", oldFocus, newFocus));
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
        this.execStoped = false;
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
    protected FocusBorder createFocusBorder() {
        return new FocusBorder.Builder()
                .asColor()
                .borderColor(Color.TRANSPARENT)
                .shadowColor(Color.TRANSPARENT)
                .shadowWidth(10)
                .build(this);
    }

    @Override
    protected int layoutId() {
        return R.layout.sedm_kind_movie_act;
    }

    @Override
    protected void stopAct() {

    }

    @Override
    protected void destroyAct() {
        if (this.kindAnimator != null) {
            this.kindAnimator.cancel();
            this.kindAnimator = null;
        }
        if (this.pKindListener != null) {
            this.pKindListener.release();
            this.pKindListener = null;
        }
        if (this.kindListener != null) {
            this.kindListener.release();
            this.kindListener = null;
        }
        if (this.cKindListener != null) {
            this.cKindListener.release();
            this.cKindListener = null;
        }
        if (this.movieListener != null) {
            this.movieListener.release();
            this.movieListener = null;
        }
        if (this.pKindAdapter != null) {
            this.pKindAdapter.release();
            this.pKindAdapter = null;
        }
        if (this.kindAdapter != null) {
            this.kindAdapter.release();
            this.kindAdapter = null;
        }
        if (this.cKindAdapter != null) {
            this.cKindAdapter.release();
            this.cKindAdapter = null;
        }
        if (this.mHandler != null) {
            this.mHandler.release();
            this.mHandler.removeCallbacksAndMessages(null);
            this.mHandler = null;
        }
        if (this.mAnimatorListener != null) {
            this.mAnimatorListener.release();
            this.mAnimatorListener = null;
        }
        if (this.movieAdapter != null) {
            this.movieAdapter.release();
            this.movieAdapter = null;
        }
        if (this.mOnFocusChangeListener != null) {
            this.mOnFocusChangeListener.release();
            this.mOnFocusChangeListener = null;
        }
        if (this.mOnClickListener != null) {
            this.mOnClickListener.release();
            this.mOnClickListener = null;
        }
        if (this.mOnKeyListener != null) {
            this.mOnKeyListener.release();
            this.mOnKeyListener = null;
        }
    }

    public void updatePageInfo(int curPageIndex, int totalPageNum) {
        if (this.pageV != null) {
            this.pageV.setText(AppMain.res().getString(R.string.page_fmt,
                    curPageIndex, totalPageNum));
        }
    }

    private void startAnim(Animator... items) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(items);
        animatorSet.setDuration(300);
        animatorSet.addListener(this.mAnimatorListener = new MAnimatorListener(this));
        animatorSet.start();
    }

    public int getKindListShowLevel() {
        return this.curShowLevel < EXVAL.KIND_LIST_SHOW_MAX_LEVEL ||
                this.curShowLevel > EXVAL.KIND_LIST_SHOW_MIN_LEVEL ? EXVAL.KIND_LIST_SHOW_MID_LEVEL
                : this.curShowLevel;
    }

    private ObjectAnimator getKindWidthAnimator(int newWidth) {
        this.kindWidthVH.setIntValues(newWidth);
        this.kindWAnimator.setTarget(this.kindWrapper);
        this.kindWAnimator.setValues(this.kindWidthVH);
        return this.kindWAnimator;
    }

    private ObjectAnimator getCkindWidthAnimator(int newWidth) {
        this.cKindWidthVH.setIntValues(newWidth);
        this.ckindWAnimator.setTarget(this.cKindWrapper);
        this.ckindWAnimator.setValues(this.cKindWidthVH);
        return this.ckindWAnimator;
    }

    private ObjectAnimator getMovieWidthAnimator(int newWidth) {
        this.movieWidthVH.setIntValues(newWidth);
        this.movieWAnimator.setTarget(this.movieWrapper);
        this.movieWAnimator.setValues(this.movieWidthVH);
        return this.movieWAnimator;
    }

    private ObjectAnimator getCkindTranXAnimator(float newTranX) {
        this.cKindTranXVH.setFloatValues(newTranX);
        this.cKindAnimator.setTarget(this.cKindRootV);
        this.cKindAnimator.setValues(this.cKindTranXVH);
        return this.cKindAnimator;
    }

    private ObjectAnimator getKindTranXAnimator(float newTranX) {
        this.kindTranXVH.setFloatValues(newTranX);
        this.kindAnimator.setTarget(this.kindRootV);
        this.kindAnimator.setValues(this.kindTranXVH);
        return this.kindAnimator;
    }

    private ObjectAnimator getMovieTranXAnimator(float newTranX) {
        this.movieTranXVH.setFloatValues(newTranX);
        this.movieAnimator.setTarget(this.movieRv);
        this.movieAnimator.setValues(this.movieTranXVH);
        return this.movieAnimator;
    }

    public void changeKindListShowLevel(RecyclerView prevRv, RecyclerView curRv, int direction) {
        MLog.d(TAG, String.format("changeKindListShowLevel prevRv=%s curRv=%s", prevRv, curRv));
        if (prevRv == null || curRv == null) {
            return;
        }
        if (curRv == this.movieRv) {
            if (direction == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (prevRv == this.kindRv) {
                    int width = this.pKindRootV == null ? 0 : this.pKindRootV.getWidth();
                    if (width > 0) {
                        this.curShowLevel = EXVAL.KIND_LIST_SHOW_MIN_LEVEL;
                        this.pKindRootV.setVisibility(View.GONE);
                        startAnim(
                                this.getKindTranXAnimator(-this.kindRvWidth),
                                this.getMovieTranXAnimator(-this.kindRvWidth),
                                this.getMovieWidthAnimator(this.screenWidth -
                                        this.kindRvWidth - this.pkindLeftOffset));
                    }
                } else if (prevRv == this.cKindRv) {
                    int width = this.kindRootV == null ? 0 : this.kindRootV.getWidth();
                    if (width > 0) {
                        this.curShowLevel = EXVAL.KIND_LIST_SHOW_MIN_LEVEL;
                        int offset = -this.kindRvWidth - this.pkindLeftOffset;
                        this.kindBgV.setVisibility(View.GONE);
                        startAnim(
                                this.getKindWidthAnimator(0),
                                this.getCkindTranXAnimator(offset),
                                this.getMovieTranXAnimator(offset),
                                this.getMovieWidthAnimator(this.screenWidth -
                                        this.kindRvWidth - this.pkindLeftOffset));
                    }
                }
            }
        } else if (curRv == this.kindRv) {
            if (direction == KeyEvent.KEYCODE_DPAD_LEFT) {
                int width = this.pKindRootV == null ? 0 : this.pKindRootV.getWidth();
                MLog.d(TAG, "pKindRootV width " + this.pKindRootV.getWidth());
                if (width == 0) {
                    width = this.cKindRootV == null ? 0 : this.cKindRootV.getWidth();
                    this.curShowLevel = width > 0 ? EXVAL.KIND_LIST_SHOW_MAX_LEVEL :
                            EXVAL.KIND_LIST_SHOW_MID_LEVEL;
                    int offset = this.kindRvWidth + this.pkindLeftOffset;
                    this.pkindBgV.setVisibility(View.VISIBLE);
                    startAnim(
                            this.getKindTranXAnimator(offset),
                            this.getCkindTranXAnimator(offset),
                            this.getMovieTranXAnimator(offset),
                            this.getMovieWidthAnimator(this.screenWidth -
                                    this.kindRvWidth * 3 - this.pkindLeftOffset * 3));

                }
            }
        } else if (curRv == this.cKindRv) {
            if (direction == KeyEvent.KEYCODE_DPAD_LEFT) {
                int width = this.kindRootV == null ? 0 : this.kindRootV.getWidth();
                if (width == 0) {
                    this.curShowLevel = EXVAL.KIND_LIST_SHOW_MID_LEVEL;
                    this.kindBgV.setVisibility(View.VISIBLE);
                    startAnim(
                            this.getKindWidthAnimator(this.kindRvWidth),
                            this.getCkindTranXAnimator(0),
                            this.getMovieTranXAnimator(0),
                            this.getMovieWidthAnimator(this.screenWidth - this.kindRvWidth
                                    * 2 - this.pkindLeftOffset * 2));

                }
            } else if (direction == KeyEvent.KEYCODE_DPAD_RIGHT) {
                int width = this.pKindRootV == null ? 0 : this.pKindRootV.getWidth();
                if (width > 0) {
                    this.curShowLevel = EXVAL.KIND_LIST_SHOW_MID_LEVEL;
                    this.pkindBgV.setVisibility(View.GONE);
                    startAnim(
                            this.getKindTranXAnimator(0),
                            this.getCkindTranXAnimator(0),
                            this.getMovieTranXAnimator(0),
                            this.getMovieWidthAnimator(this.screenWidth -
                                    this.kindRvWidth * 2 - this.pkindLeftOffset * 2));
                }
            }
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        public SEDMKindListAct hostCls;

        public MOnFocusChangeListener(SEDMKindListAct hostCls) {
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

            switch (v.getId()) {
                case R.id.sedm_kind_movie_search_v:
                    if (this.hostCls.searchV != null) {
                        this.hostCls.searchV.setShowState(!hasFocus);
                        this.hostCls.searchV.setShowState2(hasFocus);
                    }
                    break;
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        public SEDMKindListAct hostCls;

        public MOnClickListener(SEDMKindListAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {

            if (this.hostCls == null) {
                return;
            }

            switch (v.getId()) {
                case R.id.sedm_kind_movie_search_v:
                    Intent intent = new Intent(this.hostCls, SearchAct.class);
                    this.hostCls.startActivity(intent);
                    break;
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        public SEDMKindListAct hostCls;

        public MOnKeyListener(SEDMKindListAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (this.hostCls == null || event.getAction() == KeyEvent.ACTION_UP) {
                return false;
            }

            int vId = v.getId();

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    switch (vId) {
                        case R.id.sedm_kind_movie_search_v: {
                            if (this.hostCls.movieAdapter != null) {
                                this.hostCls.focusBorderVisible(true);
                                this.hostCls.movieAdapter.marketCacheForFocus(true);
                            }
                            Utils.noFocus(v);
                            break;
                        }
                    }
                    break;
            }

            return false;
        }
    }

    private static class MAnimatorListener implements Animator.AnimatorListener {

        private SEDMKindListAct hostCls;

        public MAnimatorListener(SEDMKindListAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            MLog.d(TAG, "onAnimationEnd");
            if (this.hostCls != null) {
                if (this.hostCls.curShowLevel == EXVAL.KIND_LIST_SHOW_MAX_LEVEL) {
                    if (this.hostCls.pKindAdapter.getItemCount() == 0) {
                        this.hostCls.pKindAdapter.setNeedMarket(true);
                        this.hostCls.pKindAdapter.setDatas(Arrays.asList(
                                AppMain.res().getStringArray(R.array.sedm_pkind_name_arr)));
                    }
                    if (this.hostCls.kindRv.hasFocus()) {
                        this.hostCls.kindAdapter.marketCacheForFocus(true);
                    }
                } else if (this.hostCls.curShowLevel == EXVAL.KIND_LIST_SHOW_MID_LEVEL) {
                    if (this.hostCls.cKindRv.hasFocus()) {
                        this.hostCls.cKindAdapter.marketCacheForFocus(true);
                    }
                }
                Utils.sendMsg(this.hostCls.mHandler, MSG.KIND_MOIVE_ANIM_END, 50);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    private static class MHandler extends Handler {

        private SEDMKindListAct hostCls;

        public MHandler(SEDMKindListAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void handleMessage(Message msg) {

            if (this.hostCls == null) {
                return;
            }

            switch (msg.what) {
                case MSG.KIND_MOIVE_ANIM_END: {
                    if (this.hostCls.movieAdapter != null) {
                        this.hostCls.movieAdapter.refreshCurPage(
                                this.hostCls.getKindListShowLevel() ==
                                        EXVAL.KIND_LIST_SHOW_MIN_LEVEL, false);
                    }
                }
            }
        }
    }

    public void loadMoiveDatas(final List<Object> movieObjs, final int movieNum,
                               final int movieTotalPageNum, final int curPageIndex,
                               final boolean curIsFavCkind) {
        this.curIsFavCkind = curIsFavCkind;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SEDMKindListAct.this.movieAdapter.setDatas(movieObjs, movieNum, movieTotalPageNum,
                        curPageIndex);
                if (movieNum <= 0) {
                    if (!SEDMKindListAct.this.cKindRv.hasFocus() &&
                            !SEDMKindListAct.this.kindRv.hasFocus() &&
                            !SEDMKindListAct.this.pkindRv.hasFocus()) {
                        SEDMKindListAct.this.cKindAdapter.
                                marketCacheForFocus(true);
                        SEDMKindListAct.this.changeKindListShowLevel(SEDMKindListAct.this.movieRv,
                                SEDMKindListAct.this.cKindRv, KeyEvent.KEYCODE_DPAD_LEFT);
                    }
                }
            }
        });
    }

    private void loadCkindList(int flag) {
        this.cKindAdapter.clearCacheFocus();
        this.cKindAdapter.setDatas(Arrays.asList(this.getCkindArr(flag)),
                this.ckindCidArr[this.kindAdapter.getCurSelIndex()]);
        boolean ckListShow = this.cKindAdapter.getItemCount() > 0;
        if (ckListShow) {
            SEDMKindListAct.this.cKindRv.post(new Runnable() {
                @Override
                public void run() {
                    if (SEDMKindListAct.this.cKindRv.hasFocus() ||
                            !SEDMKindListAct.this.ckFocusInited) {
                        SEDMKindListAct.this.ckFocusInited = true;
                        SEDMKindListAct.this.cKindRv.unlockFocus();
                        SEDMKindListAct.this.cKindAdapter.setSelection(0);
                    }
                }
            });
        } else {
            if (!SEDMKindListAct.this.ckFocusInited) {
                SEDMKindListAct.this.ckFocusInited = true;
                SEDMKindListAct.this.cKindAdapter.marketCacheForFocus(false);
                SEDMKindListAct.this.kindAdapter.marketCacheForFocus(true);
            }
        }
        refreshMovieDatas();
    }

    private void refreshMovieDatas() {
        this.movieAdapter.reset();
        this.movieAdapter.setCid(this.cKindAdapter.getCid(this.cKindAdapter.getCurSelIndex()));
        this.movieAdapter.loadMovies(false);
    }

    private void loadKindList(int kindMarketPos, boolean isClicked) {
        if (this.pKindAdapter != null) {
            List<String> kindNames = Arrays.asList(
                    AppMain.res().getStringArray(R.array.sedm_kind_name_arr));
            this.updateTitle(kindNames.get(kindMarketPos));
            if (this.kindAdapter != null) {
                this.kindAdapter.reset();
                this.kindAdapter.setMarketPos(kindMarketPos);
                this.kindAdapter.setNeedMarket(true);
                this.kindAdapter.setDatas(kindNames);
            }
            if (this.cKindAdapter != null) {
                this.cKindAdapter.reset();
                this.cKindAdapter.setMarketPos(0);
                this.cKindAdapter.setNeedMarket(true);
            }
            this.loadCkindList(this.ckindNeedShowFlag[kindMarketPos]);
        }
    }

    private static class OnRvAdapterListenerForPkind implements OnRvAdapterListener {

        private SEDMKindListAct hostCls;
        private int cacheSelIndex;

        public OnRvAdapterListenerForPkind(SEDMKindListAct hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onItemSelected(View view, int position) {
            if (this.hostCls != null) {
                this.cacheSelIndex = position;
            }
        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (this.hostCls != null) {
                        this.hostCls.pKindAdapter.setMarketPos(position);
                        this.hostCls.pKindAdapter.marketCacheForFocus(false);
                        this.hostCls.resetFocusBorder();
                        this.hostCls.kindAdapter.marketCacheForFocus(true);
                        this.hostCls.changeKindListShowLevel(this.hostCls.pkindRv,
                                this.hostCls.kindRv, keyCode);
                    }
                    break;
            }
        }

        @Override
        public void onItemClick(View view, int position) {

        }

        @Override
        public void onItemLongClick(View view, int position) {

        }

        public int getCacheSelIndex() {
            return cacheSelIndex;
        }

        public void setCacheSelIndex(int cacheSelIndex) {
            this.cacheSelIndex = cacheSelIndex;
        }
    }

    private static class OnRvAdapterListenerForkind implements OnRvAdapterListener {

        private SEDMKindListAct hostCls;
        private int cacheSelIndex;

        public OnRvAdapterListenerForkind(SEDMKindListAct hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onItemSelected(View view, int position) {
            if (this.hostCls != null) {
                if (this.cacheSelIndex != position) {
                    this.hostCls.cKindAdapter.setMarketPos(0);
                    this.hostCls.cKindAdapter.setNeedMarket(true);
                    this.hostCls.loadCkindList(this.hostCls.ckindNeedShowFlag[position]);
                }
                this.hostCls.updateTitle(this.hostCls.kindAdapter.
                        getDataForItem(position).toString());
                this.cacheSelIndex = position;
            }
        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            if (this.hostCls == null) {
                return;
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    int ckindNum = this.hostCls.cKindAdapter == null ? 0 :
                            this.hostCls.cKindAdapter.getItemCount();
                    if (ckindNum > 0) {
                        this.hostCls.kindAdapter.setMarketPos(position);
                        this.hostCls.kindAdapter.marketCacheForFocus(false);
                        this.hostCls.resetFocusBorder();
                        this.hostCls.cKindAdapter.marketCacheForFocus(true);
                        this.hostCls.changeKindListShowLevel(this.hostCls.kindRv, this.hostCls.cKindRv,
                                keyCode);
                    }
                    break;
            }
        }

        @Override
        public void onItemClick(View view, int position) {

        }

        @Override
        public void onItemLongClick(View view, int position) {

        }

        public int getCacheSelIndex() {
            return cacheSelIndex;
        }

        public void setCacheSelIndex(int cacheSelIndex) {
            this.cacheSelIndex = cacheSelIndex;
        }
    }

    private static class OnRvAdapterListenerForCkind implements OnRvAdapterListener {

        private SEDMKindListAct hostCls;

        public OnRvAdapterListenerForCkind(SEDMKindListAct hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onItemSelected(View view, int position) {
            if (this.hostCls == null) {
                return;
            }
            this.hostCls.refreshMovieDatas();
        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            if (this.hostCls == null) {
                return;
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    int movieNum = this.hostCls.movieAdapter == null ? 0 :
                            this.hostCls.movieAdapter.getItemCount();
                    if (movieNum > 0) {
                        this.hostCls.cKindAdapter.setMarketPos(position);
                        this.hostCls.cKindAdapter.marketCacheForFocus(false);
                        this.hostCls.resetFocusBorder();
                        this.hostCls.movieAdapter.marketCacheForFocus(true);
                        this.hostCls.changeKindListShowLevel(this.hostCls.cKindRv, this.hostCls.movieRv,
                                keyCode);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    this.hostCls.cKindAdapter.setMarketPos(position);
                    this.hostCls.cKindAdapter.marketCacheForFocus(false);
                    this.hostCls.resetFocusBorder();
                    this.hostCls.kindAdapter.marketCacheForFocus(true);
//                    this.hostCls.changeKindListShowLevel(this.hostCls.cKindRv, this.hostCls.kindRv,
//                            keyCode);
                    break;
            }
        }

        @Override
        public void onItemClick(View view, int position) {

        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    }

    private static class OnRvAdapterListenerForMovie implements OnRvAdapterListener {

        private SEDMKindListAct hostCls;

        public OnRvAdapterListenerForMovie(SEDMKindListAct hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onItemSelected(View view, int position) {
            if (this.hostCls == null) {
                return;
            }
        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            if (this.hostCls == null || this.hostCls.movieAdapter == null) {
                return;
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (position % this.hostCls.movieAdapter.getSpanCount() == 0) {
                        this.hostCls.cKindAdapter.marketCacheForFocus(true);
                        this.hostCls.changeKindListShowLevel(this.hostCls.movieRv, this.hostCls.cKindRv,
                                keyCode);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP: {
                    if (this.hostCls.movieAdapter.getCurPageIndex() == 0 && position >= 0 &&
                            position < this.hostCls.movieAdapter.getSpanCount()) {
                        this.hostCls.focusBorderVisible(false);

                        Utils.focusV(this.hostCls.searchV, true);
                        hostCls.focusBorderForV(hostCls.searchV, 1.1f, 1.1f);

                        this.hostCls.movieAdapter.marketCacheForFocus(false);
                    } else if (position >= 0 && position < this.hostCls.movieAdapter.getSpanCount()) {
                        this.hostCls.movieAdapter.loadMovies(true);
                    }
                    break;
                }
                case KeyEvent.KEYCODE_DPAD_DOWN: {
                    if (position >= this.hostCls.movieAdapter.getSpanCount() &&
                            position < this.hostCls.movieAdapter.getItemCount()) {
                        this.hostCls.movieAdapter.loadMovies(false);
                    }
                    break;
                }
            }
        }

        @Override
        public void onItemClick(View view, int position) {
            if (this.hostCls == null) {
                return;
            }
            Intent intent = new Intent(this.hostCls, SEDMMovieDetailsAct.class);
            intent.putExtra(EXVAL.MOVIE_OBJ, this.hostCls.movieAdapter.getDataForItem(position));
            this.hostCls.startActivityForResult(intent, EXVAL.DETAILS_REQ_CODE);
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    }

    public void updateTitle(String title) {
        if (this.titleV != null) {
            this.titleV.setText(title);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EXVAL.DETAILS_REQ_CODE: {
                if (data != null) {
                    boolean favChanged = data.getBooleanExtra(EXVAL.FAV_CHANGED, false);
                    if (favChanged) {
                        if (this.curIsFavCkind) {
//                            MLog.d(TAG, "refresh fav movie datas");
                            this.movieAdapter.refreshCurPage(true, true);
                        }
                    }
                }
                break;
            }
        }
    }

    private String[] getCkindArr(int flag) {
        MLog.d(TAG, "getCkindArr " + flag);
        if (flag == EXVAL.NEED_YYPY) {
            return new String[]{
                    this.yypyStr
            };
        } else if (flag == EXVAL.NEED_BFSJ) {
            return new String[]{
                    this.bfsjStr
            };
        } else if (flag == EXVAL.NEED_YYPY_BFSJ) {
            return new String[]{
                    this.yypyStr,
                    this.bfsjStr
            };
        } else {
            return new String[0];
        }
    }
}
