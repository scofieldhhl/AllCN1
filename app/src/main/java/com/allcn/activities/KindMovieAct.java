package com.allcn.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.adapters.CKindAdapter;
import com.allcn.adapters.MovieAdapter;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.utils.MSG;
import com.allcn.utils.ViewWrapper;
import com.allcn.views.FocusKeepRecyclerView;
import com.allcn.views.decorations.KindItemDecoration;
import com.allcn.views.decorations.MovieItemDecoration;
import com.allcn.views.focus.FocusBorder;
import com.blankj.utilcode.util.ScreenUtils;
import com.coorchice.library.SuperTextView;
import com.datas.CKindObj;
import com.datas.MovieObj;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.Arrays;
import java.util.List;

public class KindMovieAct extends BaseActivity {

    private static final String TAG = KindMovieAct.class.getSimpleName();
    private FocusKeepRecyclerView /*pkindRv,*/ kindRv, cKindRv, movieRv;
    private TextView titleV, pageV;
    private CKindAdapter /*pKindAdapter,*/ kindAdapter, cKindAdapter;
    private MovieAdapter movieAdapter;
    private GridLayoutManager movieLayoutMgr;
    private MovieItemDecoration movieItemDecoration;
    private SuperTextView searchV, leftArrowV;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private int kindRvWidth, curShowLevel, cacheShowLevel;
    private MAnimatorListener mAnimatorListener;
    private ViewWrapper pkindWrapper, kindWrapper, movieWrapper, cKindWrapper;
    private FrameLayout contentV;
    private LinearLayout cKindRootV;
    private int cKindInitTranX,//
            movieInitTranX,//电影位移宽度
            leftArrowWidth;
    private ObjectAnimator pkindWAnimator, kindAnimator, kindWAnimator, cKindAnimator, ckindWAnimator, movieAnimator, movieWAnimator;
    private PropertyValuesHolder kindWidthVH, kindTranXVH;
    private PropertyValuesHolder cKindWidthVH, cKindTranXVH;
    private PropertyValuesHolder pKindWidthVH;
    private PropertyValuesHolder movieTranXVH, movieWidthVH;
    private int screenWidth, screenHeight;
    private MHandler mHandler;
    private String[] kindCidArr;
    //private OnRvAdapterListenerForPkind pKindListener;
    private OnRvAdapterListenerForkind kindListener;
    private OnRvAdapterListenerForCkind cKindListener;
    private OnRvAdapterListenerForMovie movieListener;
    private boolean ckFocusInited, curIsFavCkind;
    private FocusBorder drawableFB, colorFB;
    private static int SHOW_CONTENT;
    private static final int SHOW_YYSY = 0;
    private static final int SHOW_YSXF = 1;
    private static final int SHOW_LGYY = 2;
    private long ANIMATOR_DURATION = 300;
    private View leftArrowV2;


    /**
     * focusState @link {
     *      #FOCUS_KIND
     *      #FOCUS_CKIND
     *      #FOCUS_SEACHER
     *      #FOCUS_MOVIE
     * }
     */
    private int focusState;
    private final static int FOCUS_KIND = 0;
    private final static int FOCUS_CKIND = 1;
    private final static int FOCUS_SEACHER = 2;
    private final static int FOCUS_MOVIE = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int pKindIndex = intent.getIntExtra(EXVAL.HOME_INDEX, 0);//显示的内容
        int kindIndex = intent.getIntExtra(EXVAL.KIND_INDEX, 0);//菜单默认选中的位置
        SHOW_CONTENT = pKindIndex;


        mHandler = new MHandler(this);
        screenWidth = ScreenUtils.getScreenWidth();//获取屏幕宽度
        screenHeight = ScreenUtils.getScreenHeight();//获取屏幕高度

        MLog.d(TAG, String.format("Dev Screen(%dx%d)", this.screenWidth, this.screenHeight));

        curShowLevel = EXVAL.KIND_LIST_SHOW_MID_LEVEL;
        if (SHOW_CONTENT == SHOW_LGYY)
            curShowLevel = EXVAL.KIND_LIST_SHOW_MIN_LEVEL;

        cacheShowLevel = curShowLevel;


        drawableFB = new FocusBorder.Builder()
           .asDrawable()
           .borderResId(R.drawable.kind_item_f)
           .needDuplicateIdleStatus(true)
           .build(this);
        colorFB = new FocusBorder.Builder()
                .asColor()
                .borderColor(Color.TRANSPARENT)
                .shadowColor(Color.TRANSPARENT)
                .borderWidth(1)
                .shadowWidth(1)
                .build(this);
        changeFocusBorder(this.drawableFB);

        for (View parent = (View) this.titleV.getParent(); parent != null; parent = (View) parent.getParent()) {
            parent.setBackgroundResource(0);
        }

        contentV.setBackgroundResource(R.drawable.yy_xf_bg);

        kindRvWidth = (int) AppMain.res().getDimension(R.dimen.kind_movie_kind_v_w);
        leftArrowWidth = (int) AppMain.res().getDimension(R.dimen.kind_movie_left_arrow_v_w);

        int marginTop = (int) AppMain.res().getDimension(R.dimen.kind_movie_title_v_h);

        cKindInitTranX = leftArrowWidth + kindRvWidth;

        FrameLayout.LayoutParams cKindRootParams = (FrameLayout.LayoutParams) this.cKindRootV.getLayoutParams();
        cKindRootParams.setMargins(cKindInitTranX, marginTop, 0, 0);

        cKindRootV.setLayoutParams(cKindRootParams);
        cKindRootV.requestLayout();

        movieInitTranX = cKindInitTranX + kindRvWidth;

        FrameLayout.LayoutParams movieParams = (FrameLayout.LayoutParams) this.movieRv.getLayoutParams();
        movieParams.setMargins(movieInitTranX, marginTop, 0, 0);

        movieRv.setLayoutParams(movieParams);
        movieRv.requestLayout();

        initRecyclerView();


        mOnFocusChangeListener = new MOnFocusChangeListener(this);
        mOnClickListener = new MOnClickListener(this);
        mOnKeyListener = new MOnKeyListener(this);

        searchV.setOnFocusChangeListener(this.mOnFocusChangeListener);
        searchV.setOnClickListener(this.mOnClickListener);
        searchV.setOnKeyListener(this.mOnKeyListener);


        initAnimator();



        //this.pKindAdapter.setMarketPos(pKindIndex);
        kindAdapter.setMarketPos(kindIndex);
        //this.pKindListener.setCacheSelIndex(pKindIndex);
        kindListener.setCacheSelIndex(kindIndex);
        cKindAdapter.setMarketPos(0);

        cKindAdapter.setNeedMarket(false);
        loadKindList(kindIndex, false);

//        this.movieRv.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
//            @Override
//            public void onGlobalFocusChanged(View oldFocus, View newFocus) {
//                MLog.d(TAG, String.format("oldFocus=%s\nnewFocus=%s", oldFocus, newFocus));
//            }
//        });
//
        if (SHOW_CONTENT == SHOW_LGYY) {
            startAnim(1,
                    getKindWidthAnimator(0),
                    getCkindTranXAnimator(-kindRvWidth),
                    getMovieTranXAnimator(-kindRvWidth),
                    getMovieWidthAnimator(screenWidth -
                            kindRvWidth - leftArrowWidth));
            leftArrowV.setVisibility(View.GONE);
            leftArrowV2.setVisibility(View.VISIBLE);
        }else {
            //kindAdapter.setSelection(kindIndex);
            //kindAdapter.setCurSelIndex(kindIndex);
            //kindAdapter.setMarketPos(kindIndex);
            kindAdapter.marketCurSelFocusVH(kindIndex, false);
            kindAdapter.marketCacheForFocus(false, false);
            cKindAdapter.setSelection(0);
        }
    }

    private void initRecyclerView() {

        //this.pkindRv.setTag("-PKind");
        kindRv.setTag("-Kind");
        cKindRv.setTag("-CKind");
        movieRv.setTag("-Movie");

        //this.pkindRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
        //        false));
        kindRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        cKindRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        movieRv.setLayoutManager(this.movieLayoutMgr =
                new GridLayoutManager(this, 4));

        KindItemDecoration kindItemDecoration = new KindItemDecoration();

        //pkindRv.addItemDecoration(kindItemDecoration);
        kindRv.addItemDecoration(kindItemDecoration);
        cKindRv.addItemDecoration(kindItemDecoration);

        this.movieRv.addItemDecoration(this.movieItemDecoration = new MovieItemDecoration());

        //this.pKindAdapter = new CKindAdapter(this, "PKindAdapter");
        //this.pkindRv.setAdapter(this.pKindAdapter);
        kindAdapter = new CKindAdapter(this, "KindAdapter");
        kindRv.setAdapter(this.kindAdapter);
        cKindAdapter = new CKindAdapter(this, "CKindAdapter");
        cKindRv.setAdapter(this.cKindAdapter);
        movieAdapter = new MovieAdapter(this, movieLayoutMgr, movieItemDecoration);
        movieRv.setAdapter(this.movieAdapter);

        //pKindAdapter.setOnRvListener(pKindListener = new OnRvAdapterListenerForPkind(this));
        kindAdapter.setOnRvListener(this.kindListener = new OnRvAdapterListenerForkind(this));
        cKindAdapter.setOnRvListener(this.cKindListener = new OnRvAdapterListenerForCkind(this));
        movieAdapter.setOnRvListener(this.movieListener = new OnRvAdapterListenerForMovie(this));
    }

    private void initAnimator() {

        //this.pkindWrapper = new ViewWrapper(this.pkindRv);
        kindWrapper = new ViewWrapper(this.kindRv);
        movieWrapper = new ViewWrapper(this.movieRv);
        cKindWrapper = new ViewWrapper(this.cKindRootV);


        kindWidthVH = PropertyValuesHolder.ofInt("width", 0);
        kindTranXVH = PropertyValuesHolder.ofFloat("translationX", 0);

        pKindWidthVH = PropertyValuesHolder.ofInt("width", 0);

        cKindWidthVH = PropertyValuesHolder.ofInt("width", 0);
        cKindTranXVH = PropertyValuesHolder.ofFloat("translationX", 0);

        movieTranXVH = PropertyValuesHolder.ofFloat("translationX", 0);
        movieWidthVH = PropertyValuesHolder.ofInt("width", 0);

        kindAnimator = new ObjectAnimator();
        kindWAnimator = new ObjectAnimator();

        cKindAnimator = new ObjectAnimator();
        ckindWAnimator = new ObjectAnimator();

        pkindWAnimator = new ObjectAnimator();
        //
        movieAnimator = new ObjectAnimator();
        movieWAnimator = new ObjectAnimator();
    }

    @Override
    protected void eventView() {

    }

    @Override
    protected void findView() {

        contentV = findViewById(android.R.id.content);

        cKindRootV = findViewById(R.id.kind_movie_ckind_root_v);
        leftArrowV = findViewById(R.id.kind_movie_left_arrow_v);
        leftArrowV2 = findViewById(R.id.kind_movie_left_arrow_v2);
        searchV = findViewById(R.id.kind_movie_search_v);
        titleV = findViewById(R.id.kind_movie_title_v);
        pageV = findViewById(R.id.kind_movie_page_v);
        //pkindRv = findViewById(R.id.kind_movie_pkind_v);
        kindRv = findViewById(R.id.kind_movie_kind_v);
        cKindRv = findViewById(R.id.kind_movie_ckind_v);
        movieRv = findViewById(R.id.kind_movie_v);
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
        return null;
    }

    @Override
    int layoutId() {
        return R.layout.kind_movie_act;
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
        //if (this.pKindListener != null) {
        //    this.pKindListener.release();
        //    this.pKindListener = null;
        //}
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
        //if (this.pKindAdapter != null) {
        //    this.pKindAdapter.release();
        //    this.pKindAdapter = null;
        //}
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

    private void startAnim(long l, Animator... items) {//开始动画
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(items);
        animatorSet.setDuration(l);
        animatorSet.addListener(this.mAnimatorListener = new MAnimatorListener(this));
        animatorSet.start();
    }

    public int getKindListShowLevel() {
        return this.curShowLevel < EXVAL.KIND_LIST_SHOW_MAX_LEVEL ||
                this.curShowLevel > EXVAL.KIND_LIST_SHOW_MIN_LEVEL ? EXVAL.KIND_LIST_SHOW_MID_LEVEL
                : this.curShowLevel;
    }

    private ObjectAnimator getPkindWidthAnimator(int newWidth) {
        this.pKindWidthVH.setIntValues(newWidth);
        this.pkindWAnimator.setTarget(this.pkindWrapper);
        this.pkindWAnimator.setValues(this.pKindWidthVH);
        return this.pkindWAnimator;
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
        this.kindAnimator.setTarget(this.kindRv);
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
        if (SHOW_CONTENT == SHOW_LGYY)//蓝光影视的时候只有一级菜单
            return;

        MLog.d(TAG, String.format("changeKindListShowLevel prevRv=%s curRv=%s", prevRv, curRv));
        if (prevRv == null || curRv == null) {
            return;
        }
        if (curRv == this.movieRv) {
            if (direction == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (prevRv == this.kindRv) {
                    int width = 0;// this.pkindRv == null ? 0 : this.pkindRv.getWidth();
                    if ( width > 0) {
                        this.curShowLevel = EXVAL.KIND_LIST_SHOW_MIN_LEVEL;
                        startAnim( ANIMATOR_DURATION,
                                this.getPkindWidthAnimator(this.kindRvWidth),
                                this.getKindTranXAnimator(this.kindRvWidth),
                                this.getMovieTranXAnimator(this.kindRvWidth),
                                this.getMovieWidthAnimator(this.screenWidth -
                                        this.kindRvWidth * 2));
                    }
                } else if (prevRv == this.cKindRv) {
                    int width = this.kindRv == null ? 0 : this.kindRv.getWidth();
                    if (width > 0) {
                        this.curShowLevel = EXVAL.KIND_LIST_SHOW_MIN_LEVEL;
                        startAnim(ANIMATOR_DURATION,
                                this.getKindWidthAnimator(0),
                                this.getCkindTranXAnimator(-this.kindRvWidth),
                                this.getMovieTranXAnimator(-this.kindRvWidth),
                                this.getMovieWidthAnimator(this.screenWidth -
                                        this.kindRvWidth - this.leftArrowWidth));
                    }
                }
            }
        } else if (curRv == this.kindRv) {
            if (direction == KeyEvent.KEYCODE_DPAD_LEFT) {
                int width = 0;//this.pkindRv == null ? 0 : this.pkindRv.getWidth();
                if (width == 0) {
                    width = this.cKindRootV == null ? 0 : this.cKindRootV.getWidth();
                    this.curShowLevel = width > 0 ? EXVAL.KIND_LIST_SHOW_MAX_LEVEL :
                            EXVAL.KIND_LIST_SHOW_MID_LEVEL;
                    int kindTranX = this.kindRvWidth - this.leftArrowWidth;
                    startAnim(ANIMATOR_DURATION,
                            this.getPkindWidthAnimator(this.kindRvWidth),
                            this.getKindTranXAnimator(kindTranX),
                            this.getCkindTranXAnimator(kindTranX),
                            this.getMovieTranXAnimator(kindTranX),
                            this.getMovieWidthAnimator(this.screenWidth -
                                    this.kindRvWidth * 3));

                }
            }
        } else if (curRv == this.cKindRv) {
            if (direction == KeyEvent.KEYCODE_DPAD_LEFT) {
                int width = this.kindRv == null ? 0 : this.kindRv.getWidth();
                if (width == 0) {
                    this.curShowLevel = EXVAL.KIND_LIST_SHOW_MID_LEVEL;
                    startAnim(ANIMATOR_DURATION,
                            this.getKindWidthAnimator(this.kindRvWidth),
                            this.getCkindTranXAnimator(0),
                            this.getMovieTranXAnimator(0),
                            this.getMovieWidthAnimator(this.screenWidth -
                                    this.kindRvWidth * 2 - this.leftArrowWidth));

                }
            } else if (direction == KeyEvent.KEYCODE_DPAD_RIGHT) {
                int width = 0;//this.pkindRv == null ? 0 : this.pkindRv.getWidth();
                if (width > 0) {
                    this.curShowLevel = EXVAL.KIND_LIST_SHOW_MID_LEVEL;
                    int kindTranX = this.kindRvWidth - this.leftArrowWidth;
                    startAnim(ANIMATOR_DURATION,
                            this.getPkindWidthAnimator(0),
                            this.getKindTranXAnimator(0),
                            this.getCkindTranXAnimator(0),
                            this.getMovieTranXAnimator(0),
                            this.getMovieWidthAnimator(this.screenWidth -
                                    this.kindRvWidth * 2 - this.leftArrowWidth));
                }
            }
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        public KindMovieAct hostCls;

        public MOnFocusChangeListener(KindMovieAct hostCls) {
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
                case R.id.kind_movie_search_v:
                    if (this.hostCls.searchV != null) {
                        this.hostCls.searchV.setShowState(!hasFocus);
                        this.hostCls.searchV.setShowState2(hasFocus);
                    }
                    break;
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        public KindMovieAct hostCls;

        public MOnClickListener(KindMovieAct hostCls) {
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
                case R.id.kind_movie_search_v:
                    Intent intent = new Intent(this.hostCls, SearchAct.class);
                    this.hostCls.startActivity(intent);
                    break;
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        public KindMovieAct hostCls;

        public MOnKeyListener(KindMovieAct hostCls) {
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
                    switch (vId) {
                        case R.id.kind_movie_search_v: {
                            if (this.hostCls.kindAdapter != null) {
                                this.hostCls.focusBorderVisible(true);
                                this.hostCls.kindAdapter.marketCacheForFocus(true, false);
                            }
                            Utils.noFocus(v);
                            break;
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    switch (vId) {
                        case R.id.kind_movie_search_v: {
                            if (this.hostCls.movieAdapter != null) {
                                this.hostCls.focusBorderVisible(true);
                                this.hostCls.movieAdapter.marketCacheForFocus(true);
                            }
                            Utils.noFocus(v);
                            break;
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    switch (vId) {
                        case R.id.kind_movie_search_v: {
                            if (this.hostCls.cKindAdapter != null) {
                                this.hostCls.focusBorderVisible(true);
                                this.hostCls.cKindAdapter.marketCacheForFocus(true, false);
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

        private KindMovieAct hostCls;

        public MAnimatorListener(KindMovieAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onAnimationEnd(Animator animation) {
            MLog.d(TAG, "onAnimationEnd");
            if (this.hostCls != null) {
                //int visibility = this.hostCls.getKindListShowLevel() == EXVAL.KIND_LIST_SHOW_MAX_LEVEL ? View.GONE : View.VISIBLE;
                int visibility = this.hostCls.getKindListShowLevel() == EXVAL.KIND_LIST_SHOW_MID_LEVEL ? View.GONE : View.VISIBLE;

                if (this.hostCls.leftArrowV != null) {
                    hostCls.leftArrowV.setVisibility(visibility);
                    if (SHOW_CONTENT != SHOW_LGYY) {
                        //hostCls.leftArrowV.setDrawableAsBackground(false);
                        hostCls.leftArrowV2.setVisibility(View.GONE);
                    }else {
                        //hostCls.leftArrowV.setDrawableAsBackground(true);
                        hostCls.leftArrowV.setVisibility(View.GONE);
                        hostCls.leftArrowV2.setVisibility(View.VISIBLE);
                    }
                }
                if (this.hostCls.curShowLevel == EXVAL.KIND_LIST_SHOW_MAX_LEVEL) {
                    //if (this.hostCls.pKindAdapter.getItemCount() == 0) {
                    //    this.hostCls.pKindAdapter.setNeedMarket(true);
                    //    this.hostCls.pKindAdapter.setDatas(Arrays.asList(
                    //            AppMain.res().getStringArray(R.array.yysj_yyxf_pkind_name_arr)));
                    //}
                    //if (this.hostCls.kindRv.hasFocus()) {
                    //    this.hostCls.kindAdapter.marketCacheForFocus(true, true);
                    //}
                } else if (this.hostCls.curShowLevel == EXVAL.KIND_LIST_SHOW_MID_LEVEL) {
                    hostCls.leftArrowV.setVisibility(View.GONE);
                    hostCls.leftArrowV2.setBackground(hostCls.getDrawable(R.color.kind_movie_kind_bg));
                    if (this.hostCls.cKindRv.hasFocus()) {
                        this.hostCls.cKindAdapter.marketCacheForFocus(true, true);
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

        private KindMovieAct hostCls;

        public MHandler(KindMovieAct hostCls) {
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

    public void loadCKDatas(final List<CKindObj> cKindObjs, final int cKindObjNum) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                KindMovieAct.this.cKindAdapter.setDatas(cKindObjs);
                if (cKindObjNum > 0) {
                    cKindRv.post(new Runnable() {
                        @Override
                        public void run() {
                            if (cKindRv.hasFocus() || !ckFocusInited) {
                                ckFocusInited = true;
                                cKindRv.unlockFocus();
                                if (SHOW_CONTENT == SHOW_LGYY){
                                    cKindAdapter.setSelection(0);
                                }
                            }
                        }
                    });
                }
                //else {
                //    if (!KindMovieAct.this.ckFocusInited) {
                //        KindMovieAct.this.ckFocusInited = true;
                //        KindMovieAct.this.cKindAdapter.marketCacheForFocus(false, false);
                //        KindMovieAct.this.kindAdapter.marketCacheForFocus(true, true);
                //    }
                //}
                refreshMovieDatas();
            }
        });
    }

    public void loadMoiveDatas(final List<Object> movieObjs, final int movieNum,
                               final int movieTotalPageNum, final int curPageIndex,
                               final boolean curIsFavCkind) {
        this.curIsFavCkind = curIsFavCkind;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                movieAdapter.setDatas(movieObjs, movieNum, movieTotalPageNum,
                        curPageIndex);
                if (movieNum <= 0) {
                    if (!cKindRv.hasFocus() &&
                            !kindRv.hasFocus()
                            //&& !KindMovieAct.this.pkindRv.hasFocus()
                            ) {
                        cKindAdapter.marketCacheForFocus(true, false);

                        changeKindListShowLevel(movieRv, cKindRv, KeyEvent.KEYCODE_DPAD_LEFT);
                    }
                }
            }
        });
    }

    private void loadCkindList(String pCid) {
        DataCenter.Ins().scanCkind(this, pCid);
    }

    private void refreshMovieDatas() {
        this.movieAdapter.reset();
        this.movieAdapter.setSelCKindObj((CKindObj) this.cKindAdapter.getDataForItem(
                this.cKindAdapter.getCurSelIndex()));
        this.movieAdapter.loadMovies(false);
    }

    private void loadKindList(int kindMarketPos, boolean isClicked) {
        //if (this.pKindAdapter != null) {
            //int pKindIndex = this.pKindAdapter.getCurSelIndex();

            int kindCidArrResId = -1, kindNameResId = -1;
            switch (SHOW_CONTENT) {
                case SHOW_YYSY: {
                    kindCidArrResId = R.array.yysj_kind_cid_arr;
                    kindNameResId = R.array.yysj_kind_name_arr;
                    break;
                }
                case SHOW_YSXF: {
                    kindCidArrResId = R.array.yyxf_kind_cid_arr;
                    kindNameResId = R.array.yyxf_kind_name_arr;
                    break;
                }
                case SHOW_LGYY: {
                    kindCidArrResId = R.array.lgyy_kind_cid_arr;
                    kindNameResId = R.array.lgyy_kind_name_arr;
                    break;
                }
            }
            kindCidArr = AppMain.res().getStringArray(kindCidArrResId);
            List<String> kindNames = Arrays.asList(AppMain.res().getStringArray(kindNameResId));
            this.updateTitle(kindNames.get(kindMarketPos));
            if (kindAdapter != null) {
                kindAdapter.reset();
                kindAdapter.setMarketPos(kindMarketPos);
                kindAdapter.setNeedMarket(true);
                kindAdapter.setDatas(kindNames);
                kindAdapter.setSelection(kindMarketPos);
            }
            if (cKindAdapter != null) {
                cKindAdapter.reset();
                cKindAdapter.setMarketPos(0);
                cKindAdapter.setNeedMarket(true);
            }
            loadCkindList(kindCidArr[kindMarketPos]);//加载收索栏菜单
        //}
    }

    private static class OnRvAdapterListenerForPkind implements OnRvAdapterListener {

        private KindMovieAct hostCls;
        private int cacheSelIndex;

        public OnRvAdapterListenerForPkind(KindMovieAct hostCls) {
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
                    this.hostCls.kindAdapter.setMarketPos(0);
                    this.hostCls.kindAdapter.setNeedMarket(true);
                    this.hostCls.loadKindList(0, false);
                }
                this.cacheSelIndex = position;
            }
        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (this.hostCls != null) {
                        //this.hostCls.pKindAdapter.setMarketPos(position);
                        //this.hostCls.pKindAdapter.marketCacheForFocus(false, false);
                        //this.hostCls.kindAdapter.marketCacheForFocus(true, true);
                        //this.hostCls.changeKindListShowLevel(this.hostCls.pkindRv,
                        //        this.hostCls.kindRv, keyCode);
                    }
                    break;
            }
        }

        @Override
        public void onItemClick(View view, int position) {
//            if (this.hostCls != null) {
//                this.hostCls.kindAdapter.setMarketPos(0);
//                this.hostCls.kindAdapter.setNeedMarket(true);
//                this.hostCls.loadKindList(0, true);
//                this.cacheSelIndex = position;
//            }
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

    private static class OnRvAdapterListenerForkind implements OnRvAdapterListener {//二级菜单第一级

        private KindMovieAct hostCls;
        private int cacheSelIndex;

        public OnRvAdapterListenerForkind(KindMovieAct hostCls) {
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
                    this.hostCls.loadCkindList(this.hostCls.kindCidArr[position]);
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
                    if (ckindNum > 0) { //int curSelIndex = hostCls.kindAdapter.getCurSelIndex();
                        this.hostCls.kindAdapter.setMarketPos(position);
                        this.hostCls.kindAdapter.marketCacheForFocus(false, false);
                        this.hostCls.cKindAdapter.marketCacheForFocus(true, true);
                        this.hostCls.changeKindListShowLevel(this.hostCls.kindRv, this.hostCls.cKindRv,
                                keyCode);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    //if (this.hostCls.pkindRv.getWidth() == this.hostCls.kindRvWidth) {
                    //    this.hostCls.kindAdapter.setMarketPos(position);
                    //    this.hostCls.kindAdapter.marketCacheForFocus(false, false);
                    //    this.hostCls.pKindAdapter.marketCacheForFocus(true, true);
                    //}
                    //this.hostCls.changeKindListShowLevel(this.hostCls.kindRv, this.hostCls.pkindRv,
                    //        keyCode);
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

    private static class OnRvAdapterListenerForCkind implements OnRvAdapterListener {//右边菜单焦点监听

        private KindMovieAct hostCls;

        public OnRvAdapterListenerForCkind(KindMovieAct hostCls) {
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
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (position == 0) {
                        if (this.hostCls.searchV != null) {
                            this.hostCls.focusBorderVisible(false);
                            Utils.focusV(this.hostCls.searchV, true);
                            if (hostCls.cKindAdapter != null) {
                                this.hostCls.cKindAdapter.setMarketPos(position);
                                this.hostCls.cKindAdapter.marketCacheForFocus(
                                        false, false);
                            }
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    int movieNum = this.hostCls.movieAdapter == null ? 0 :
                            this.hostCls.movieAdapter.getItemCount();
                    if (movieNum > 0) {
                        this.hostCls.drawableFB.setVisible(false);
                        this.hostCls.changeFocusBorder(this.hostCls.colorFB);
                        this.hostCls.cKindAdapter.setMarketPos(position);
                        this.hostCls.cKindAdapter.marketCacheForFocus(false, false);
                        this.hostCls.movieAdapter.marketCacheForFocus(true);
                        this.hostCls.changeKindListShowLevel(this.hostCls.cKindRv, this.hostCls.movieRv,
                                keyCode);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (SHOW_CONTENT != SHOW_LGYY) {
                        hostCls.cKindAdapter.setMarketPos(position);
                        hostCls.cKindAdapter.marketCacheForFocus(false, false);
                        int curSelIndex = hostCls.kindAdapter.getCurSelIndex();
                        int marketPos = hostCls.kindAdapter.getMarketPos();
                        hostCls.kindAdapter.marketCurSelFocusVH(marketPos, false);
                        hostCls.kindAdapter.marketCacheForFocus(false, false);
                        hostCls.kindAdapter.marketCacheForFocus(true, true);
                        //this.hostCls.changeKindListShowLevel(this.hostCls.cKindRv, this.hostCls.kindRv,
                        //        keyCode);
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
    }

    private static class OnRvAdapterListenerForMovie implements OnRvAdapterListener {

        private KindMovieAct hostCls;

        public OnRvAdapterListenerForMovie(KindMovieAct hostCls) {
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
                        this.hostCls.colorFB.setVisible(false);
                        this.hostCls.changeFocusBorder(this.hostCls.drawableFB);
                        this.hostCls.cKindAdapter.marketCacheForFocus(true, false);
                        this.hostCls.changeKindListShowLevel(this.hostCls.movieRv, this.hostCls.cKindRv,
                                keyCode);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP: {
                    if (position >= 0 && position < this.hostCls.movieAdapter.getSpanCount()) {
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
            Object object = this.hostCls.movieAdapter.getDataForItem(position);
            if (object instanceof MovieObj) {
                Intent intent = new Intent(this.hostCls, DetailsAct.class);
                intent.putExtra(EXVAL.MOVIE_OBJ, (MovieObj) object);
                this.hostCls.startActivityForResult(intent, EXVAL.DETAILS_REQ_CODE);
            } else if (object instanceof CKindObj) {
                Intent intent = new Intent(this.hostCls, TopicsDetailsAct.class);
                intent.putExtra(EXVAL.CKIND_OBJ, (CKindObj) object);
                this.hostCls.startActivityForResult(intent, EXVAL.TOPICS_DETAILS_REQ_CODE);
            }
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
            case EXVAL.TOPICS_DETAILS_REQ_CODE: {
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
}
