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
import com.allcn.adapters.DetailsJSAdapter;
import com.allcn.adapters.DetailsReAdapter;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.utils.GlideUtils;
import com.allcn.views.FocusKeepRecyclerView;
import com.allcn.views.MoreWindow;
import com.allcn.views.decorations.DetailsJSItemDecoration;
import com.allcn.views.decorations.DetailsReItemDecoration;
import com.allcn.views.focus.FocusBorder;
import com.datas.MovieDetailsObj;
import com.datas.MovieObj;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.ShareAdapter;
import com.mast.lib.utils.Utils;

import java.util.List;

public class DetailsAct extends BaseActivity {

    private static final String TAG = DetailsAct.class.getSimpleName();

    private ImageView imgV;
    private TextView jsUpHeaderV, jsDownHeaderV, titleV, dyV, zyV, lxV, jsV, jjV, menuTipsV;
    private Button playV, favV, selV;
    private FocusKeepRecyclerView upRv, downRv, reRv;
    private DetailsJSAdapter upAdapter, downAdapter;
    private DetailsReAdapter reAdapter;
    private MovieObj movieObj;
    private MovieDetailsObj movieDetailsObj;
    /**
     * order true:顺序 false:倒序
     */
    private boolean curFavStatus, initFavStatus, isHistory, order, reFavChanged;
    private OnRvAdapterListenerForMovie reListener;
    private MOnClickListener mOnClickListener;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnKeyListener mOnKeyListener;
    private String favStr, unFavStr;
    private int filmIdLastPageIndex = -1, upPageIndex = -1, downPageIndex = -1;
    private OnRvAdapterListener upRvListener, downRvListener;
    private FocusBorder drawableFB, colorFB;
    private TextView moreV;
    private MoreWindow moreWindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        moreV = findViewById(R.id.details_more_v);
        imgV = findViewById(R.id.details_img_v);
        titleV = findViewById(R.id.details_title_v);
        dyV = findViewById(R.id.details_dy_v);
        zyV = findViewById(R.id.details_zy_v);
        lxV = findViewById(R.id.details_lx_v);
        jsV = findViewById(R.id.details_js_v);
        jjV = findViewById(R.id.details_jj_v);
        menuTipsV = findViewById(R.id.details_menu_tips_v);
        playV = findViewById(R.id.details_play_v);
        favV = findViewById(R.id.details_fav_v);
        selV = findViewById(R.id.details_selection_v);
        jsUpHeaderV = findViewById(R.id.details_js_header_up);
        jsDownHeaderV = findViewById(R.id.details_js_header_down);
        upRv = findViewById(R.id.details_js_rv_up);
        downRv = findViewById(R.id.details_js_rv_down);
        reRv = findViewById(R.id.details_re_rv);

        //this.order = ShareAdapter.Ins(AppMain.ctx()).getB(EXVAL.ORDER);

        favStr = AppMain.res().getString(R.string.fav);
        unFavStr = AppMain.res().getString(R.string.unfav);

        moreWindow = new MoreWindow(false);

        for (ViewGroup parent = (ViewGroup) this.titleV.getParent(); parent != null;
             parent = (ViewGroup) parent.getParent()) {
            parent.setBackgroundResource(0);
            parent.setClipToPadding(false);
            parent.setClipChildren(false);
        }

        findViewById(android.R.id.content).setBackgroundResource(R.drawable.yy_xf_bg);

        this.mOnClickListener = new MOnClickListener(this);
        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnKeyListener = new MOnKeyListener(this);

        this.playV.setOnClickListener(this.mOnClickListener);
        this.favV.setOnClickListener(this.mOnClickListener);
        this.selV.setOnClickListener(this.mOnClickListener);
        this.moreV.setOnClickListener(this.mOnClickListener);
        this.playV.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.favV.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.selV.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.moreV.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.playV.setOnKeyListener(this.mOnKeyListener);
        this.favV.setOnKeyListener(this.mOnKeyListener);
        this.selV.setOnKeyListener(this.mOnKeyListener);
        this.moreV.setOnKeyListener(this.mOnKeyListener);
        this.jsUpHeaderV.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.jsUpHeaderV.setOnKeyListener(this.mOnKeyListener);
        this.jsDownHeaderV.setOnFocusChangeListener(this.mOnFocusChangeListener);
        this.jsDownHeaderV.setOnKeyListener(this.mOnKeyListener);

        this.upRv.addItemDecoration(new DetailsJSItemDecoration());
        this.downRv.addItemDecoration(new DetailsJSItemDecoration());
        this.reRv.addItemDecoration(new DetailsReItemDecoration());
        this.upRv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        this.downRv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        this.reRv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));

        this.upAdapter = new DetailsJSAdapter(this, this.jsUpHeaderV);
        this.downAdapter = new DetailsJSAdapter(this, this.jsDownHeaderV);
        this.reAdapter = new DetailsReAdapter(this);

        this.reAdapter.setOnRvListener(this.reListener = new OnRvAdapterListenerForMovie(this));
        this.upAdapter.setOnRvAdapterListener(this.upRvListener =
                new OnRvAdapterListenerForUp(this));
        this.downAdapter.setOnRvAdapterListener(this.downRvListener =
                new OnRvAdapterListenerForDown(this));

        this.upRv.setAdapter(this.upAdapter);
        this.downRv.setAdapter(this.downAdapter);
        this.reRv.setAdapter(this.reAdapter);

        this.drawableFB = new FocusBorder.Builder()
                .asDrawable()
                .borderResId(R.drawable.kind_item_f)
                .build(this);
        this.colorFB = new FocusBorder.Builder()
                .asColor()
                .borderColor(Color.TRANSPARENT)
                .shadowColor(Color.TRANSPARENT)
                .borderWidth(1)
                .shadowWidth(1)
                .build(this);
        this.changeFocusBorder(this.colorFB);

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
    protected FocusBorder createFocusBorder() {
        return null;
    }

    @Override
    protected int layoutId() {
        return R.layout.details_act;
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
        if (this.upAdapter != null) {
            this.upAdapter.release();
            this.upAdapter = null;
        }
        if (this.downAdapter != null) {
            this.downAdapter.release();
            this.downAdapter = null;
        }
        if (this.upRvListener != null) {
            this.upRvListener.release();
            this.upRvListener = null;
        }
        if (this.downRvListener != null) {
            this.downRvListener.release();
            this.downRvListener = null;
        }
        if (this.reListener != null) {
            this.reListener.release();
            this.reListener = null;
        }
        if (this.reAdapter != null) {
            this.reAdapter.release();
            this.reAdapter = null;
        }
        if (this.mOnKeyListener != null) {
            this.mOnKeyListener.release();
            this.mOnKeyListener = null;
        }
        if (this.mOnClickListener != null) {
            this.mOnClickListener.release();
            this.mOnClickListener = null;
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
        this.filmIdLastPageIndex = this.movieDetailsObj.getFilmIdPageNum() - 1;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    DetailsAct.this.titleV.setText(movieDetailsObj.getName());
                    GlideUtils.Ins().loadUrlImg(DetailsAct.this, movieDetailsObj.getImgUrl(),
                            DetailsAct.this.imgV);
                    DetailsAct.this.dyV.setText(AppMain.res().getString(R.string.dy_fmt,
                            movieDetailsObj.getDirectors()));
                    DetailsAct.this.jsV.setText(AppMain.res().getString(R.string.zy_fmt,
                            movieDetailsObj.getCasts()));
                    DetailsAct.this.lxV.setText(AppMain.res().getString(R.string.lx_fmt,
                            DetailsAct.this.movieObj.getLabel()));
                    DetailsAct.this.zyV.setText(AppMain.res().getString(R.string.js_fmt,
                            DetailsAct.this.movieObj.getJs()));
                    String jjStr = AppMain.res().getString(R.string.intro,
                            movieDetailsObj.getSummary());
                    DetailsAct.this.jjV.setText(jjStr);
                    DetailsAct.this.moreWindow.loadMoreText(jjStr);
                    DetailsAct.this.favV.setText(isFav ?
                            DetailsAct.this.unFavStr : DetailsAct.this.favStr);
                    DetailsAct.this.reAdapter.setDatas(reMovieObjs);
                    if (movieDetailsObj.getFilmIdNum() > 1) {
                        DetailsAct.this.selV.setVisibility(View.VISIBLE);
                    }
                    Utils.focusV(DetailsAct.this.playV, true);
                } catch (Exception e) {
                }
            }
        });
    }

    private static class OnRvAdapterListenerForMovie implements OnRvAdapterListener {

        private DetailsAct hostCls;

        public OnRvAdapterListenerForMovie(DetailsAct hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onItemSelected(View view, int position) {

        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            if (this.hostCls == null || this.hostCls.reAdapter == null) {
                return;
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP: {
                    this.hostCls.reAdapter.marketCacheForFocus(false);
                    this.hostCls.changeFocusBorder(this.hostCls.colorFB);
                    this.hostCls.drawableFB.setVisible(false);
                    Utils.focusV(this.hostCls.playV, true);
                    Utils.focusV(this.hostCls.favV, false);
                    Utils.focusV(this.hostCls.selV, false);
                    break;
                }
            }
        }

        @Override
        public void onItemClick(View view, int position) {
            if (this.hostCls == null) {
                return;
            }
            Intent intent = new Intent(this.hostCls, DetailsAct.class);
            intent.putExtra(EXVAL.MOVIE_OBJ, this.hostCls.reAdapter.getDataForItem(position));
            this.hostCls.startActivityForResult(intent, EXVAL.DETAILS_REQ_CODE);
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private DetailsAct hostCls;

        public MOnClickListener(DetailsAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {

            if (this.hostCls == null || this.hostCls.movieDetailsObj == null) {
                return;
            }

            switch (v.getId()) {
                case R.id.details_play_v: {
                    this.hostCls.startPlay(0);
                    break;
                }
                case R.id.details_fav_v: {
                    this.hostCls.curFavStatus = !this.hostCls.curFavStatus;
                    this.hostCls.favV.setText(
                            this.hostCls.curFavStatus ? this.hostCls.unFavStr : this.hostCls.favStr);
                    break;
                }
                case R.id.details_selection_v:

                    break;
                case R.id.details_more_v: {
                    hostCls.moreWindow.show((View) v.getParent());
                    break;
                }
            }
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private DetailsAct hostCls;

        public MOnFocusChangeListener(DetailsAct hostCls) {
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
                switch (v.getId()) {
                    case R.id.details_play_v:
                        this.hostCls.ctrlRe(true);
                        this.hostCls.ctrlSelRv(false, true);
                        this.hostCls.focusBorderForV(v, 1.20f, 1.20f);
                        break;
                    case R.id.details_fav_v: {
                        this.hostCls.focusBorderForV(v, 1.20f, 1.20f);
                        break;
                    }
                    case R.id.details_selection_v:
                        this.hostCls.selV.setActivated(false);
                        this.hostCls.ctrlRe(false);
                        this.hostCls.ctrlSelRv(true, false);
                        this.hostCls.loadSelRvData(false, true);
                        this.hostCls.focusBorderForV(v, 1.20f, 1.20f);
                        break;
                    case R.id.details_more_v:
                        this.hostCls.focusBorderForV(v, 1.20f, 1.20f);
                        break;
                    case R.id.details_js_header_up:
                    case R.id.details_js_header_down: {
                        v.setActivated(false);
                        break;
                    }
                }
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private DetailsAct hostCls;

        public MOnKeyListener(DetailsAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (this.hostCls == null || this.hostCls.movieDetailsObj == null) {
                return false;
            }

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                int vId = v.getId();
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        if (vId == R.id.details_play_v) {
                            Utils.noFocus(this.hostCls.selV);
                            Utils.noFocus(this.hostCls.favV);
                            Utils.noFocus(this.hostCls.playV);
                            this.hostCls.colorFB.setVisible(false);
                            this.hostCls.changeFocusBorder(this.hostCls.drawableFB);
                            if (this.hostCls.reRv.isShown()) {
                                this.hostCls.reAdapter.marketCacheForFocus(true);
                            }
                        } else if (vId == R.id.details_selection_v) {
                            this.hostCls.colorFB.setVisible(false);
                            if (this.hostCls.upRv.getVisibility() == View.VISIBLE) {
                                this.hostCls.selV.setActivated(true);
                                this.hostCls.upAdapter.setIgoreFocus(true);
                                this.hostCls.downAdapter.setIgoreFocus(true);
                                this.hostCls.jsDownHeaderV.setActivated(false);
                                this.hostCls.jsUpHeaderV.setActivated(false);
                                this.hostCls.jsUpHeaderV.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utils.focusV(MOnKeyListener.this.hostCls.jsUpHeaderV,
                                                true);
                                    }
                                });
                            }
                            Utils.noFocus(this.hostCls.playV);
                            Utils.noFocus(this.hostCls.favV);
                            Utils.noFocus(this.hostCls.selV);
                        } else if (vId == R.id.details_js_header_down) {
                            if (this.hostCls.filmIdLastPageIndex > this.hostCls.downPageIndex &&
                                    this.hostCls.downPageIndex > 0) {
                                this.hostCls.upAdapter.setIgoreFocus(true);
                                this.hostCls.downAdapter.setIgoreFocus(true);
                                this.hostCls.loadSelRvData(false, false);
                                this.hostCls.jsDownHeaderV.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (MOnKeyListener.this.hostCls.jsDownHeaderV != null) {
                                            Utils.focusV(MOnKeyListener.this.hostCls.jsDownHeaderV,
                                                    true);
                                        }
                                    }
                                });
                            }
                        } else if (vId == R.id.details_js_header_up) {
                            Utils.noFocus(v);
                            this.hostCls.jsDownHeaderV.post(new Runnable() {
                                @Override
                                public void run() {
                                    Utils.focusV(MOnKeyListener.this.hostCls.jsDownHeaderV,
                                            true);
                                }
                            });
                        } else if (vId == R.id.details_fav_v) {
                            if (this.hostCls.reRv.isShown()) {
                                this.hostCls.colorFB.setVisible(false);
                                this.hostCls.changeFocusBorder(this.hostCls.drawableFB);
                                this.hostCls.reAdapter.marketCacheForFocus(true);
                            }
                            if (this.hostCls.upRv.getVisibility() == View.VISIBLE) {
                                this.hostCls.selV.setActivated(true);
                                this.hostCls.upAdapter.setIgoreFocus(true);
                                this.hostCls.downAdapter.setIgoreFocus(true);
                                this.hostCls.jsDownHeaderV.setActivated(false);
                                this.hostCls.jsUpHeaderV.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utils.focusV(MOnKeyListener.this.hostCls.jsUpHeaderV,
                                                true);
                                    }
                                });
                            }
                            Utils.noFocus(this.hostCls.selV);
                            Utils.noFocus(this.hostCls.playV);
                            Utils.noFocus(this.hostCls.favV);
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        if (vId == R.id.details_js_header_up) {
                            this.hostCls.upAdapter.setIgoreFocus(false);
                            this.hostCls.upRv.post(new Runnable() {
                                @Override
                                public void run() {
                                    MOnKeyListener.this.hostCls.upAdapter.setSelection(0);
                                }
                            });
                            this.hostCls.jsUpHeaderV.setActivated(true);
                            Utils.focusV(v, false);
                        } else if (vId == R.id.details_js_header_down) {
                            this.hostCls.downAdapter.setIgoreFocus(false);
                            this.hostCls.downRv.post(new Runnable() {
                                @Override
                                public void run() {
                                    MOnKeyListener.this.hostCls.downAdapter.setSelection(0);
                                }
                            });
                            this.hostCls.jsDownHeaderV.setActivated(true);
                            Utils.focusV(v, false);
                        } else if (vId == R.id.details_fav_v) {
                            if (!this.hostCls.selV.isShown()) {
                                Utils.noFocus(this.hostCls.playV);
                                Utils.focusV(this.hostCls.moreV, true);
                                Utils.noFocus(v);
                            }
                        } else if (vId == R.id.details_selection_v) {
                            Utils.noFocus(this.hostCls.playV);
                            Utils.noFocus(this.hostCls.favV);
                            Utils.focusV(this.hostCls.moreV, true);
                            Utils.noFocus(v);
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP:
                        if (vId == R.id.details_js_header_up) {
//                            MLog.d(TAG, "details_js_header_up KEYCODE_DPAD_UP " + this.hostCls.upPageIndex);
                            if (this.hostCls.upPageIndex < this.hostCls.filmIdLastPageIndex &&
                                    this.hostCls.upPageIndex > 0) {
                                this.hostCls.upAdapter.setIgoreFocus(true);
                                this.hostCls.downAdapter.setIgoreFocus(true);
                                this.hostCls.loadSelRvData(true, false);
                                this.hostCls.jsUpHeaderV.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (MOnKeyListener.this.hostCls.jsUpHeaderV != null) {
                                            Utils.focusV(MOnKeyListener.this.hostCls.jsUpHeaderV,
                                                    true);
                                        }
                                    }
                                });
                            } else {
                                this.hostCls.selV.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (MOnKeyListener.this.hostCls.jsUpHeaderV != null) {
                                            MOnKeyListener.this.hostCls.jsUpHeaderV.
                                                    setActivated(false);
                                            Utils.noFocus(MOnKeyListener.this.hostCls.jsUpHeaderV);
                                        }
                                        if (MOnKeyListener.this.hostCls.selV != null) {
                                            Utils.focusV(MOnKeyListener.this.hostCls.selV,
                                                    true);
                                            Utils.focusV(MOnKeyListener.this.hostCls.favV,
                                                    false);
                                            Utils.focusV(MOnKeyListener.this.hostCls.playV,
                                                    false);
                                        }
                                    }
                                });
                            }
                        } else if (vId == R.id.details_js_header_down) {
                            Utils.noFocus(v);
                            this.hostCls.jsUpHeaderV.post(new Runnable() {
                                @Override
                                public void run() {
                                    Utils.focusV(MOnKeyListener.this.hostCls.jsUpHeaderV,
                                            true);
                                }
                            });
                        }
                        break;
                    case KeyEvent.KEYCODE_MENU: {
                        if ((vId == R.id.details_js_header_up) ||
                                (vId == R.id.details_js_header_down)) {
                            this.hostCls.changeJSOrder();
                        }
                        break;
                    }
                    case KeyEvent.KEYCODE_DPAD_LEFT: {
                        if (vId == R.id.details_more_v) {
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
                                this.hostCls.favV.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Utils.focusV(hostCls.favV, true);
                                    }
                                });
                            }
                        }
                        break;
                    }
                }
            }

            return false;
        }
    }

    private void ctrlRe(boolean show) {
        if (this.reRv != null) {
            this.reRv.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void ctrlSelRv(boolean show, boolean ctrlRvAndHeaderV) {
        int visibility = show ? View.VISIBLE : View.GONE;
        if (this.menuTipsV != null) {
            this.menuTipsV.setVisibility(visibility);
        }
        if (ctrlRvAndHeaderV) {
            if (this.jsUpHeaderV != null) {
                this.jsUpHeaderV.setVisibility(visibility);
            }
            if (this.jsDownHeaderV != null) {
                this.jsDownHeaderV.setVisibility(visibility);
            }
            if (this.upRv != null) {
                this.upRv.setVisibility(visibility);
            }
            if (this.downRv != null) {
                this.downRv.setVisibility(visibility);
            }
        }
    }

    private void loadSelRvData(boolean isUP, boolean init) {
        if (this.movieObj == null || this.movieDetailsObj == null) {
            return;
        }
        this.jsUpHeaderV.setActivated(false);
        this.jsDownHeaderV.setActivated(false);
        int pageNum = this.movieDetailsObj.getFilmIdPageNum();
//        MLog.d(TAG, "loadSelRvData pageNum = " + pageNum);
        if (pageNum == 1) {
            this.upPageIndex = 0;
            this.jsUpHeaderV.setText(String.format("1-%d", this.movieDetailsObj.getFilmIdNum()));
            this.upAdapter.setMarketPos(this.isHistory ? 0 : -1);
            this.upAdapter.refreshData(0, this.movieDetailsObj.getFilmIdNum());
            this.jsUpHeaderV.setVisibility(View.VISIBLE);
            this.upRv.setVisibility(View.VISIBLE);
            this.jsDownHeaderV.setVisibility(View.GONE);
            this.downRv.setVisibility(View.GONE);
            return;
        } else if (pageNum == 0) {
            this.jsUpHeaderV.setVisibility(View.GONE);
            this.upRv.setVisibility(View.GONE);
            this.jsDownHeaderV.setVisibility(View.GONE);
            this.downRv.setVisibility(View.GONE);
            return;
        }
        this.jsUpHeaderV.setVisibility(View.VISIBLE);
        this.upRv.setVisibility(View.VISIBLE);
        this.jsDownHeaderV.setVisibility(View.VISIBLE);
        this.downRv.setVisibility(View.VISIBLE);
        if (!init) {
            if (isUP) {
                int curUpPageIndex = this.upPageIndex;
                this.upPageIndex += this.order ? -2 : 2;
                if (this.upPageIndex < 0) {
                    this.upPageIndex = 0;
                } else if (this.upPageIndex > this.filmIdLastPageIndex) {
                    this.upPageIndex = this.filmIdLastPageIndex;
                }
                if (curUpPageIndex == this.downPageIndex) {
                    return;
                }
                this.downPageIndex = this.upPageIndex + (order ? 1 : -1);
            } else {
                int curDownPageIndex = this.downPageIndex;
                this.downPageIndex += this.order ? 2 : -2;
                if (this.downPageIndex < 0) {
                    this.downPageIndex = 0;
                } else if (this.downPageIndex > this.filmIdLastPageIndex) {
                    this.downPageIndex = this.filmIdLastPageIndex;
                }
                if (curDownPageIndex == this.downPageIndex) {
                    return;
                }
                this.upPageIndex = this.downPageIndex + (order ? -1 : 1);
            }
        }
        int curPlayPos = this.isHistory ? this.movieObj.getPlayPos() : -1;
        int pageIndex = 0, startIndex = -1, endIndex = -1;
        if (init) {
            if (this.isHistory) {
                for (; pageIndex < this.movieDetailsObj.getFilmIdPageNum(); pageIndex++) {
                    startIndex = pageIndex * EXVAL.COLLECTION_NUM_IN_LINE;
                    endIndex = startIndex + EXVAL.COLLECTION_NUM_IN_LINE;
                    if (curPlayPos >= startIndex && curPlayPos < endIndex) {
                        if (pageIndex == this.filmIdLastPageIndex) {
                            this.upPageIndex = this.order ? pageIndex - 1 : pageIndex;
                            this.downPageIndex = this.order ? pageIndex : pageIndex - 1;
                        } else if (pageIndex == 0) {
                            this.upPageIndex = this.order ? pageIndex : pageIndex + 1;
                            this.downPageIndex = this.upPageIndex + (this.order ? 1 : -1);
                        } else {
                            this.upPageIndex = pageIndex;
                            this.downPageIndex = this.upPageIndex + (this.order ? 1 : -1);
                        }
                        break;
                    }
                }
            } else {
                this.upPageIndex = this.order ? 0 : this.filmIdLastPageIndex;
                this.downPageIndex = this.upPageIndex + (this.order ? 1 : -1);
            }
        }
//        MLog.d(TAG, String.format("upPageIndex=%d, downPageIndex=%d",
//                this.upPageIndex, this.downPageIndex));
        startIndex = this.upPageIndex * EXVAL.COLLECTION_NUM_IN_LINE;
        endIndex = startIndex + (this.upPageIndex == this.filmIdLastPageIndex ?
                this.movieDetailsObj.getFilmIdNum() - startIndex : EXVAL.COLLECTION_NUM_IN_LINE);
        this.jsUpHeaderV.setText(String.format("%d-%d", startIndex + 1, endIndex));
        this.upAdapter.setMarketPos(curPlayPos);
        this.upAdapter.refreshData(startIndex, endIndex);
        startIndex = this.downPageIndex * EXVAL.COLLECTION_NUM_IN_LINE;
        endIndex = startIndex + (this.downPageIndex == this.filmIdLastPageIndex ?
                this.movieDetailsObj.getFilmIdNum() - startIndex : EXVAL.COLLECTION_NUM_IN_LINE);
        this.jsDownHeaderV.setText(String.format("%d-%d", startIndex + 1, endIndex));
        this.downAdapter.setMarketPos(curPlayPos);
        this.downAdapter.refreshData(startIndex, endIndex);
    }

    private static class OnRvAdapterListenerForUp implements OnRvAdapterListener {

        private DetailsAct hostCls;

        public OnRvAdapterListenerForUp(DetailsAct hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void onItemSelected(View view, int position) {

        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            MLog.d(TAG, "OnRvAdapterListenerForUp onItemKey " + event);
            if (this.hostCls == null || this.hostCls.movieDetailsObj == null) {
                return;
            }
//            MLog.d(TAG, "OnRvAdapterListenerForUp upPageIndex " + this.hostCls.upPageIndex);
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (this.hostCls.upPageIndex == this.hostCls.filmIdLastPageIndex ||
                            this.hostCls.upPageIndex == 0) {
                        Utils.focusV(this.hostCls.selV, true);
                        Utils.focusV(this.hostCls.playV, false);
                        Utils.focusV(this.hostCls.favV, false);
                        this.hostCls.jsUpHeaderV.setActivated(false);
                        this.hostCls.jsDownHeaderV.setActivated(false);
                        this.hostCls.upAdapter.marketCacheHolder(false);
                        this.hostCls.downAdapter.marketCacheHolder(false);
                    } else {
                        this.hostCls.upAdapter.setIgoreFocus(false);
                        this.hostCls.downAdapter.setIgoreFocus(true);
                        this.hostCls.loadSelRvData(true, false);
                        this.hostCls.upRv.post(new Runnable() {
                            @Override
                            public void run() {
                                if (OnRvAdapterListenerForUp.this.hostCls != null) {
                                    if (OnRvAdapterListenerForUp.this.hostCls.jsUpHeaderV != null) {
                                        OnRvAdapterListenerForUp.this.hostCls.jsUpHeaderV
                                                .setActivated(true);
                                    }
                                    if (OnRvAdapterListenerForUp.this.hostCls.upAdapter != null) {
                                        OnRvAdapterListenerForUp.this.
                                                hostCls.upAdapter.setSelection(0);
                                    }
                                }
                            }
                        });
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (this.hostCls.downAdapter.getItemCount() > 0) {
                        this.hostCls.upAdapter.setIgoreFocus(true);
                        this.hostCls.downAdapter.setIgoreFocus(false);
                        this.hostCls.jsUpHeaderV.setActivated(false);
                        this.hostCls.jsDownHeaderV.setActivated(true);
                        this.hostCls.upAdapter.marketCacheHolder(true);
                        this.hostCls.downAdapter.setSelection(0);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (position == 0) {
                        this.hostCls.colorFB.setVisible(false);
                        this.hostCls.upAdapter.setIgoreFocus(true);
                        this.hostCls.upAdapter.marketCacheHolder(true);
                        Utils.focusV(this.hostCls.jsUpHeaderV, true);
                    }
                    break;
                case KeyEvent.KEYCODE_MENU:
                    this.hostCls.changeJSOrder();
                    break;
            }
        }

        @Override
        public void onItemClick(View view, int position) {
            if (this.hostCls != null && this.hostCls.upAdapter != null) {
                this.hostCls.startPlay(this.hostCls.upAdapter.getSelNum(position));
            }
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }

        @Override
        public void release() {
            this.hostCls = null;
        }
    }

    private static class OnRvAdapterListenerForDown implements OnRvAdapterListener {

        private DetailsAct hostCls;

        public OnRvAdapterListenerForDown(DetailsAct hostCls) {
            this.hostCls = hostCls;
        }

        @Override
        public void onItemSelected(View view, int position) {

        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            MLog.d(TAG, "OnRvAdapterListenerForDown onItemKey " + event);
            if (this.hostCls == null || this.hostCls.movieDetailsObj == null) {
                return;
            }
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (this.hostCls.downPageIndex > 0 && this.hostCls.downPageIndex <
                            this.hostCls.filmIdLastPageIndex) {
                        this.hostCls.upAdapter.setIgoreFocus(true);
                        this.hostCls.downAdapter.setIgoreFocus(false);
                        this.hostCls.loadSelRvData(false, false);
                        this.hostCls.downRv.post(new Runnable() {
                            @Override
                            public void run() {
                                if (OnRvAdapterListenerForDown.this.hostCls != null) {
                                    if (OnRvAdapterListenerForDown.this.hostCls.jsDownHeaderV != null) {
                                        OnRvAdapterListenerForDown.this.hostCls.jsDownHeaderV
                                                .setActivated(true);
                                    }
                                    if (OnRvAdapterListenerForDown.this.hostCls.downAdapter != null) {
                                        OnRvAdapterListenerForDown.this.
                                                hostCls.downAdapter.setSelection(0);
                                    }
                                }
                            }
                        });
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    this.hostCls.colorFB.setVisible(false);
                    this.hostCls.upAdapter.setIgoreFocus(false);
                    this.hostCls.downAdapter.setIgoreFocus(true);
                    this.hostCls.jsDownHeaderV.setActivated(false);
                    this.hostCls.jsUpHeaderV.setActivated(true);
                    this.hostCls.downAdapter.marketCacheHolder(true);
                    this.hostCls.upAdapter.setSelection(0);
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (position == 0) {
                        this.hostCls.colorFB.setVisible(false);
                        this.hostCls.downAdapter.setIgoreFocus(true);
                        this.hostCls.downAdapter.marketCacheHolder(true);
                        Utils.focusV(this.hostCls.jsDownHeaderV, true);
                    }
                    break;
                case KeyEvent.KEYCODE_MENU:
                    this.hostCls.changeJSOrder();
                    break;
            }
        }

        @Override
        public void onItemClick(View view, int position) {
            if (this.hostCls != null && this.hostCls.downAdapter != null) {
                this.hostCls.startPlay(this.hostCls.downAdapter.getSelNum(position));
            }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        MLog.d(TAG, String.format("onActivityResult requestCode=%d resultCode=%d",
//                requestCode, resultCode));
        switch (requestCode) {
            case EXVAL.PLAY_REQ_CODE: {
                if (data != null) {
                    int curPlayPos = data.getIntExtra(EXVAL.CUR_PLAY_POS, 0);
                    this.movieObj.setPlayPos(curPlayPos);
                    if (this.upRv.getVisibility() == View.VISIBLE) {
                        loadSelRvData(false, true);
                        if (!this.playV.isFocused() && !favV.isFocused() && !selV.isFocused()) {
                            this.upRv.post(new Runnable() {
                                @Override
                                public void run() {
                                    DetailsAct.this.upAdapter.handleCacheHolderfocus(true);
                                }
                            });
                        }
                    }
                }
                break;
            }
            case EXVAL.DETAILS_REQ_CODE: {
                if (data != null) {
                    boolean favChanged = data.getBooleanExtra(EXVAL.FAV_CHANGED, false);
                    if (!this.reFavChanged && favChanged) {
                        this.reFavChanged = favChanged;
                    }
                }
                break;
            }
        }
    }

    private void changeJSOrder() {
        this.order = !this.order;
        ShareAdapter.Ins(AppMain.ctx()).setB(EXVAL.ORDER, this.order);
        this.loadSelRvData(false, true);
    }
}