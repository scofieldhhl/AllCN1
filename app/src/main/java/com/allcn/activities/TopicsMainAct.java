package com.allcn.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.adapters.TopicsKindAdapter;
import com.allcn.adapters.TopicsTopAdapter;
import com.allcn.interfaces.OnDataListener;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.utils.MSG;
import com.allcn.views.CoverFlowLayoutManger;
import com.allcn.views.FocusKeepRecyclerView;
import com.allcn.views.RecyclerCoverFlow;
import com.allcn.views.decorations.TopicsKindItemDecoration;
import com.allcn.views.focus.FocusBorder;
import com.datas.CKindObj;
import com.datas.HomeMovie;
import com.datas.LiveChalObj;
import com.mast.lib.parsers.MarqueeParser;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.io.File;
import java.util.List;

public class TopicsMainAct extends BaseActivity {

    private static final String TAG = TopicsMainAct.class.getSimpleName();

    private RecyclerCoverFlow topRv;
    private FocusKeepRecyclerView bottomRv;
    private TextView[] kindVArr;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnKeyListener mOnKeyListener;
    private TopicsTopAdapter topicsTopAdapter;
    private CoverRvSelectedListener coverRvSelectedListener;
    private MHandler mHandler;
    private RelativeLayout bottomRootV, topicsHomeKindRootV;
    private PropertyValuesHolder bottomRootVTranYVH;
    private ObjectAnimator objectAnimator;
    private TopicsKindAdapter topicsKindAdapter;
    private int kindTitleExW, kindTitleExH, spanCount, kindTitleNum, topicsHomeKindNum,
            topicsKindExW, topicsKindExH;
    private float bottomRootVTranY;
    private MAnimatorListenerAdapter mAnimatorListenerAdapter;
    private View selKindTitleV;
    private MOnRvAdapterListener mOnRvAdapterListener;
    private LinearLayout[] topicsHomeKindVArr;
    private MOnClickListener mOnClickListener;
    private ImageView netV;
    private TextView timeV;
    private MOnDataListener mOnDataListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.kindTitleNum = 4;
        this.topicsHomeKindNum = 9;
        this.kindTitleExW = (int) AppMain.res().getDimension(R.dimen.topics_kind_title_ex_w);
        this.kindTitleExH = (int) AppMain.res().getDimension(R.dimen.topics_kind_title_ex_h);
        this.bottomRootVTranY = AppMain.res().getDimension(R.dimen.topics_top_bottom_root_trany);
        this.topicsKindExW = (int) AppMain.res().getDimension(R.dimen.topics_kind_ex_w);
        this.topicsKindExH = (int) AppMain.res().getDimension(R.dimen.topics_kind_ex_h);

        this.netV = findViewById(R.id.topics_main_net_v);
        this.timeV = findViewById(R.id.topics_main_time_v);
        this.topicsHomeKindRootV = findViewById(R.id.topics_home_kind_root_v);
        this.bottomRootV = findViewById(R.id.bottom_root_v);
        this.topRv = findViewById(R.id.top_rv);
        this.bottomRv = findViewById(R.id.topics_bottom_rv_v);
        this.kindVArr = new TextView[this.kindTitleNum];
        this.kindVArr[0] = findViewById(R.id.topics_kind_1);
        this.kindVArr[1] = findViewById(R.id.topics_kind_2);
        this.kindVArr[2] = findViewById(R.id.topics_kind_3);
        this.kindVArr[3] = findViewById(R.id.topics_kind_4);
        this.topicsHomeKindVArr = new LinearLayout[this.topicsHomeKindNum];
        this.topicsHomeKindVArr[0] = findViewById(R.id.topics_home_live_root_v);
        this.topicsHomeKindVArr[1] = findViewById(R.id.topics_home_playback_root_v);
        this.topicsHomeKindVArr[2] = findViewById(R.id.topics_home_yy_world_root_v);
        this.topicsHomeKindVArr[3] = findViewById(R.id.topics_home_ys_first_root_v);
        this.topicsHomeKindVArr[4] = findViewById(R.id.topics_home_children_dm_root_v);
        this.topicsHomeKindVArr[5] = findViewById(R.id.topics_home_hot_re_root_v);
        this.topicsHomeKindVArr[6] = findViewById(R.id.topics_home_lg_yy_root_v);
        this.topicsHomeKindVArr[7] = findViewById(R.id.topics_home_hot_topics_root_v);
        this.topicsHomeKindVArr[8] = findViewById(R.id.topics_home_set_mgr_root_v);
        this.mOnClickListener = new MOnClickListener(this);
        this.mOnKeyListener = new MOnKeyListener(this);
        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);

        for (ViewGroup parent = (ViewGroup) this.topRv.getParent(); parent != null;
             parent = (ViewGroup) parent.getParent()) {
            parent.setBackgroundResource(0);
            parent.setClipChildren(false);
            parent.setClipToPadding(false);
        }

        findViewById(android.R.id.content).setBackgroundResource(R.drawable.topics_bg);

        for (int i = 0; i < this.kindTitleNum; i++) {
            this.kindVArr[i].setOnFocusChangeListener(this.mOnFocusChangeListener);
            this.kindVArr[i].setOnKeyListener(this.mOnKeyListener);
        }

        for (int i = 0; i < this.topicsHomeKindNum; i++) {
            this.topicsHomeKindVArr[i].setOnFocusChangeListener(this.mOnFocusChangeListener);
            this.topicsHomeKindVArr[i].setOnKeyListener(this.mOnKeyListener);
            this.topicsHomeKindVArr[i].setOnClickListener(this.mOnClickListener);
        }

        this.topicsTopAdapter = new TopicsTopAdapter(this);
        this.topRv.setAdapter(this.topicsTopAdapter);
        this.topRv.setOnItemSelectedListener(
                this.coverRvSelectedListener = new CoverRvSelectedListener(this));

        this.mHandler = new MHandler(this);

        this.mAnimatorListenerAdapter = new MAnimatorListenerAdapter(this);
        this.objectAnimator = new ObjectAnimator();
        this.objectAnimator.addListener(this.mAnimatorListenerAdapter);
        this.bottomRootVTranYVH = PropertyValuesHolder.ofFloat("translationY", 0);

        this.spanCount = EXVAL.TOPICS_CKIND_NUM_IN_PAGE / 2;
        this.bottomRv.setLayoutManager(new GridLayoutManager(this, this.spanCount));
        this.bottomRv.setAdapter(this.topicsKindAdapter = new TopicsKindAdapter(this));
        this.bottomRv.addItemDecoration(new TopicsKindItemDecoration());
        this.topicsKindAdapter.setOnRvListener(
                this.mOnRvAdapterListener = new MOnRvAdapterListener(this));

        this.topicsKindAdapter.setCid(EXVAL.TOPICS_YY_CID);
        DataCenter.Ins().scanCkind(this, EXVAL.TOPICS_TOP_CID);
        DataCenter.Ins().scanCkindForPage(this, EXVAL.TOPICS_YY_CID, 0,
                EXVAL.TOPICS_CKIND_NUM_IN_PAGE, false);

        this.mOnDataListener = new MOnDataListener(this);
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
        DataCenter.Ins().addDataListener(this.mOnDataListener);
        DataCenter.Ins().scanTime();
        DataCenter.Ins().regReceiver(AppMain.ctx());
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
        DataCenter.Ins().unregReceiver(AppMain.ctx());
        DataCenter.Ins().delDataListener(this.mOnDataListener);
    }

    @Override
    protected void destroyAct() {
        if (this.mOnDataListener != null) {
            this.mOnDataListener.release();
            this.mOnDataListener = null;
        }
        if (this.mOnRvAdapterListener != null) {
            this.mOnRvAdapterListener.release();
            this.mOnRvAdapterListener = null;
        }
        if (this.topicsKindAdapter != null) {
            this.topicsKindAdapter.release();
            this.topicsKindAdapter = null;
        }
        if (this.mAnimatorListenerAdapter != null) {
            this.mAnimatorListenerAdapter.release();
            this.mAnimatorListenerAdapter = null;
        }
        if (this.mOnFocusChangeListener != null) {
            this.mOnFocusChangeListener.release();
            this.mOnFocusChangeListener = null;
        }
        if (this.mOnKeyListener != null) {
            this.mOnKeyListener.release();
            this.mOnKeyListener = null;
        }
        if (this.mOnClickListener != null) {
            this.mOnClickListener.release();
            this.mOnClickListener = null;
        }
        if (this.topicsTopAdapter != null) {
            this.topicsTopAdapter.release();
            this.topicsTopAdapter = null;
        }
        if (this.coverRvSelectedListener != null) {
            this.coverRvSelectedListener.release();
            this.coverRvSelectedListener = null;
        }
        if (this.mHandler != null) {
            this.mHandler.release();
            this.mHandler.removeCallbacksAndMessages(null);
            this.mHandler = null;
        }
    }

    @Override
    protected FocusBorder createFocusBorder() {
        return new FocusBorder.Builder()
                .asDrawable()
                .borderResId(R.drawable.kind_item_f)
                .build(this);
    }

    @Override
    protected int layoutId() {
        return R.layout.topics_main_act;
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private TopicsMainAct hostCls;

        public MOnFocusChangeListener(TopicsMainAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            MLog.d(TAG, "onFocusChange");
            if (this.hostCls == null) {
                return;
            }

            int vId = v.getId();

            if (hasFocus) {

                int exW = 0, exH = 0;

                switch (vId) {
                    case R.id.topics_kind_1: {
                        exW = this.hostCls.kindTitleExW;
                        exH = this.hostCls.kindTitleExH;
                        v.setBackgroundResource(0);
                        this.hostCls.topicsKindAdapter.setCid(EXVAL.TOPICS_YY_CID);
                        DataCenter.Ins().scanCkindForPage(this.hostCls, EXVAL.TOPICS_YY_CID,
                                0, EXVAL.TOPICS_CKIND_NUM_IN_PAGE, false);
                        break;
                    }
                    case R.id.topics_kind_2: {
                        exW = this.hostCls.kindTitleExW;
                        exH = this.hostCls.kindTitleExH;
                        v.setBackgroundResource(0);
                        this.hostCls.topicsKindAdapter.setCid(EXVAL.TOPICS_ZH_CID);
                        DataCenter.Ins().scanCkindForPage(this.hostCls, EXVAL.TOPICS_ZH_CID,
                                0, EXVAL.TOPICS_CKIND_NUM_IN_PAGE, false);
                        break;
                    }
                    case R.id.topics_kind_3: {
                        exW = this.hostCls.kindTitleExW;
                        exH = this.hostCls.kindTitleExH;
                        v.setBackgroundResource(0);
                        this.hostCls.topicsKindAdapter.setCid(EXVAL.TOPICS_YS_CID);
                        DataCenter.Ins().scanCkindForPage(this.hostCls, EXVAL.TOPICS_YS_CID,
                                0, EXVAL.TOPICS_CKIND_NUM_IN_PAGE, false);
                        break;
                    }
                    case R.id.topics_kind_4: {
                        exW = this.hostCls.kindTitleExW;
                        exH = this.hostCls.kindTitleExH;
                        v.setBackgroundResource(0);
                        this.hostCls.bottomRv.setVisibility(View.GONE);
                        this.hostCls.topicsHomeKindRootV.setVisibility(View.VISIBLE);
                        break;
                    }
                    case R.id.topics_home_live_root_v:
                    case R.id.topics_home_playback_root_v:
                    case R.id.topics_home_yy_world_root_v:
                    case R.id.topics_home_ys_first_root_v:
                    case R.id.topics_home_children_dm_root_v:
                    case R.id.topics_home_hot_re_root_v:
                    case R.id.topics_home_lg_yy_root_v:
                    case R.id.topics_home_hot_topics_root_v:
                    case R.id.topics_home_set_mgr_root_v: {
                        ((ViewGroup) v).getChildAt(0).setSelected(true);
                        exW = this.hostCls.topicsKindExW;
                        exH = this.hostCls.topicsKindExH;
                        break;
                    }
                }

                this.hostCls.focusBorderForV(v, exW, exH);
            } else {
                switch (vId) {
                    case R.id.topics_home_live_root_v:
                    case R.id.topics_home_playback_root_v:
                    case R.id.topics_home_yy_world_root_v:
                    case R.id.topics_home_ys_first_root_v:
                    case R.id.topics_home_children_dm_root_v:
                    case R.id.topics_home_hot_re_root_v:
                    case R.id.topics_home_lg_yy_root_v:
                    case R.id.topics_home_hot_topics_root_v:
                    case R.id.topics_home_set_mgr_root_v: {
                        ((ViewGroup) v).getChildAt(0).setSelected(false);
                        break;
                    }
                }
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private TopicsMainAct hostCls;

        public MOnKeyListener(TopicsMainAct hostCls) {
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

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT: {
                    if (v.getId() == R.id.topics_kind_1) {
                        this.hostCls.selKindTitleV = v;
                        this.hostCls.changeKindTitleFocus(this.hostCls.selKindTitleV,
                                false);
                        this.hostCls.topicsKindAdapter.setSelection(0);
                    }
                    break;
                }
                case KeyEvent.KEYCODE_DPAD_DOWN: {
                    v.setBackgroundResource(R.drawable.topics_kind_item_sel_nof);
                    int vId = v.getId();
                    if (vId == R.id.topics_kind_1 || vId == R.id.topics_kind_2 ||
                            vId == R.id.topics_kind_3) {
                        if (this.hostCls.topicsKindAdapter != null) {
                            if (this.hostCls.topicsKindAdapter.getItemCount() > 0) {
                                this.hostCls.selKindTitleV = v;
                                this.hostCls.changeKindTitleFocus(this.hostCls.selKindTitleV,
                                        false);
                                this.hostCls.topicsKindAdapter.setSelection(0);
                            }
                        }
                    } else if (vId == R.id.topics_kind_4) {
                        this.hostCls.selKindTitleV = v;
                        Utils.focusV(this.hostCls.topicsHomeKindVArr[0], true);
                    }
                    break;
                }
                case KeyEvent.KEYCODE_DPAD_UP: {
                    int vId = v.getId();
                    if (vId == R.id.topics_kind_1 || vId == R.id.topics_kind_2 ||
                            vId == R.id.topics_kind_3 || vId == R.id.topics_kind_4) {
                        v.setBackgroundResource(0);
                        if (this.hostCls.topRv != null) {
                            this.hostCls.changeBottomRootVTranY(this.hostCls.bottomRootVTranY);
                            this.hostCls.topRv.setVisibility(View.VISIBLE);
                        }
                        DataCenter.Ins().scanCkindForPage(this.hostCls, EXVAL.TOPICS_YY_CID,
                                0, EXVAL.TOPICS_CKIND_NUM_IN_PAGE, false);
                    } else if (vId == R.id.topics_home_live_root_v ||
                            vId == R.id.topics_home_playback_root_v ||
                            vId == R.id.topics_home_yy_world_root_v ||
                            vId == R.id.topics_home_ys_first_root_v ||
                            vId == R.id.topics_home_children_dm_root_v ||
                            vId == R.id.topics_home_hot_re_root_v ||
                            vId == R.id.topics_home_lg_yy_root_v ||
                            vId == R.id.topics_home_hot_topics_root_v ||
                            vId == R.id.topics_home_set_mgr_root_v) {
                        this.hostCls.changeKindTitleFocus(this.hostCls.selKindTitleV, true);
                    }
                    break;
                }
                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                    int vId = v.getId();
                    if (vId == R.id.topics_kind_4) {
                        v.setBackgroundResource(R.drawable.topics_kind_item_sel_nof);
                        this.hostCls.selKindTitleV = v;
                    }
                    break;
                }
            }

            return false;
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private TopicsMainAct hostCls;

        public MOnClickListener(TopicsMainAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {
            int index = Integer.valueOf(v.getTag().toString());
            Intent intent = new Intent(hostCls, EXVAL.kindClsArr[index]);
            switch (index) {
                case EXVAL.HOME_YYSJ_INDEX:
                    intent.putExtra(EXVAL.IS_YYSJ, true);
                    break;
                case EXVAL.HOME_YYXF_INDEX:
                    intent.putExtra(EXVAL.IS_YYSJ, false);
                    break;
                case EXVAL.HOME_LGYY_INDEX:
                    intent.putExtra(EXVAL.HOME_INDEX, 2);
                    break;
            }
            hostCls.startActivity(intent);
        }
    }

    private static class CoverRvSelectedListener implements CoverFlowLayoutManger.OnSelected {

        private TopicsMainAct hostCls;

        public CoverRvSelectedListener(TopicsMainAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onItemSelected(int position) {
            MLog.d(TAG, "onItemSelected position=" + position);
            if (this.hostCls != null && this.hostCls.topRv != null && (position >= 0)) {
                TopicsTopAdapter.MVHolder mvHolder = (TopicsTopAdapter.MVHolder)
                        this.hostCls.topRv.findViewHolderForAdapterPosition(position);
//                mvHolder.scrollContent(true);
//                this.hostCls.topicsTopAdapter.cacheMVHolder(mvHolder);
                Utils.sendMsg(this.hostCls.mHandler, mvHolder.itemView, MSG.TOPICS_TOP_FOCUS,
                        EXVAL.TOPICS_TOP_FOCUS_DURATION);
            }
        }
    }

    private static class MHandler extends Handler {

        private TopicsMainAct hostCls;

        public MHandler(TopicsMainAct hostCls) {
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
                case MSG.TOPICS_TOP_FOCUS: {
                    View view = (View) msg.obj;
                    if (view != null) {
                        this.hostCls.topicsTopAdapter.execFocusVEffect(view);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        MLog.d(TAG, "onKeyDown " + event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (this.topRv != null && this.topRv.hasFocus() && this.topRv.isShown()) {
                    Intent intent = new Intent(this, TopicsDetailsAct.class);
                    intent.putExtra(EXVAL.CKIND_OBJ,
                            this.topicsTopAdapter.getItemData(this.topicsTopAdapter.getCurSelIndex()));
                    this.startActivity(intent);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (this.topRv != null && this.topRv.hasFocus() && this.topRv.isShown()) {
                    int prevIndex = this.topicsTopAdapter.getCurSelIndex() - 1;
//                    MLog.d(TAG, "onKeyDown prevIndex=" + prevIndex);
                    if (prevIndex >= 0) {
                        this.topicsTopAdapter.setCurSelIndex(prevIndex);
                        this.topRv.smoothScrollToPosition(prevIndex);
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (this.topRv != null && this.topRv.hasFocus() && this.topRv.isShown()) {
                    int nextIndex = this.topicsTopAdapter.getCurSelIndex() + 1;
//                    MLog.d(TAG, "onKeyDown nextIndex=" + nextIndex);
                    if (nextIndex < this.topicsTopAdapter.getItemCount()) {
                        this.topicsTopAdapter.setCurSelIndex(nextIndex);
                        this.topRv.smoothScrollToPosition(nextIndex);
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (this.topRv != null && this.topRv.hasFocus() && this.topRv.isShown()) {
                    this.topRv.setVisibility(View.GONE);
                    this.changeKindTitleFocus(this.kindVArr[0], true);
                    this.changeBottomRootVTranY(0f);
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void changeBottomRootVTranY(float tranY) {
        this.bottomRootVTranYVH.setFloatValues(tranY);
        this.objectAnimator.setTarget(this.bottomRootV);
        this.objectAnimator.setValues(this.bottomRootVTranYVH);
        this.objectAnimator.setDuration(300);
        this.objectAnimator.start();
    }

    public void loadTopCKDatas(final List<CKindObj> cKindObjs, final int cKindObjNum) {
        TopicsMainAct.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TopicsMainAct.this.topicsTopAdapter.setDatas(cKindObjs, cKindObjNum);
                TopicsMainAct.this.topRv.post(new Runnable() {
                    @Override
                    public void run() {
                        TopicsMainAct.this.topRv.smoothScrollToPosition(
                                TopicsMainAct.this.topicsTopAdapter.getInitPos());
                    }
                });
            }
        });
    }

    public void loadCKDatas(final List<CKindObj> cKindObjs, final int cKindObjNum, final int ckTotalNum,
                            final int ckTotalPageNum, final int pageIndex, final boolean hasFocus) {
        TopicsMainAct.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TopicsMainAct.this.bottomRv.isShown()) {
                    TopicsMainAct.this.bottomRv.setVisibility(View.VISIBLE);
                }
                if (TopicsMainAct.this.topicsHomeKindRootV.isShown()) {
                    TopicsMainAct.this.topicsHomeKindRootV.setVisibility(View.GONE);
                }
                if (TopicsMainAct.this.topicsKindAdapter != null) {
                    TopicsMainAct.this.topicsKindAdapter.setDatas(cKindObjs, cKindObjNum,
                            ckTotalNum, ckTotalPageNum, pageIndex);
                    if (hasFocus) {
                        TopicsMainAct.this.bottomRv.post(new Runnable() {
                            @Override
                            public void run() {
                                TopicsMainAct.this.topicsKindAdapter.setSelection(0);
                            }
                        });
                    }
                }
            }
        });
    }

    private static class MAnimatorListenerAdapter extends AnimatorListenerAdapter {

        private TopicsMainAct hostCls;

        public MAnimatorListenerAdapter(TopicsMainAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
//            MLog.d(TAG, "onAnimationEnd");
//            MLog.d(TAG, "onAnimationEnd " + this.hostCls.topRv.isShown());
            if (this.hostCls.topRv.isShown()) {
                int centerIndex = ((CoverFlowLayoutManger) this.hostCls.topRv.getLayoutManager())
                        .getCenterPosition();
                TopicsTopAdapter.MVHolder mvHolder = null;
                if (centerIndex < this.hostCls.topicsTopAdapter.getItemCount()) {
                    mvHolder = (TopicsTopAdapter.MVHolder)
                            this.hostCls.topRv.findViewHolderForLayoutPosition(centerIndex);
                }
                if (mvHolder != null) {
                    Utils.sendMsg(this.hostCls.mHandler, mvHolder.itemView, MSG.TOPICS_TOP_FOCUS,
                            EXVAL.TOPICS_TOP_FOCUS_DURATION);
                }
                this.hostCls.changeKindTitleFocus(this.hostCls.selKindTitleV, false);
            } else {
//                this.hostCls.changeKindTitleFocus(this.hostCls.kindVArr[0], true);
            }
        }
    }

    private static class MOnRvAdapterListener implements OnRvAdapterListener {

        private TopicsMainAct hostCls;

        public MOnRvAdapterListener(TopicsMainAct hostCls) {
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
                    if (position >= 0 && (position < this.hostCls.spanCount)) {
                        if (this.hostCls.topicsKindAdapter.getCurSelPageIndex() == 0) {
                            this.hostCls.changeKindTitleFocus(
                                    this.hostCls.selKindTitleV, true);
                        } else {
                            this.hostCls.topicsKindAdapter.loadPage(true);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
//                    MLog.d(TAG, "KEYCODE_DPAD_DOWN position " + position);
                    if (position >= this.hostCls.spanCount &&
                            (position < EXVAL.TOPICS_CKIND_NUM_IN_PAGE)) {
                        this.hostCls.topicsKindAdapter.loadPage(false);
                    }
                    break;
            }
        }

        @Override
        public void onItemClick(View view, int position) {
            CKindObj cKindObj = this.hostCls.topicsKindAdapter.getItemData(position);
            if (cKindObj != null) {
                Intent intent = new Intent(this.hostCls, TopicsDetailsAct.class);
                intent.putExtra(EXVAL.CKIND_OBJ, cKindObj);
                this.hostCls.startActivity(intent);
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

    private void changeKindTitleFocus(View selV, boolean hasFocus) {
//        MLog.d(TAG, "changeKindTitleFocus");
        if (hasFocus) {
            for (int i = 0; i < this.kindTitleNum; i++) {
                TextView textView = this.kindVArr[i];
                Utils.focusV(textView, textView == selV);
            }
        } else {
            for (int i = 0; i < this.kindTitleNum; i++) {
                TextView textView = this.kindVArr[i];
                if (textView != selV) {
                    Utils.noFocus(textView);
                }
            }
            Utils.noFocus(selV);
        }
    }

    private static class MOnDataListener implements OnDataListener {

        private TopicsMainAct hostCls;

        public MOnDataListener(TopicsMainAct hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onLoadInitDatas(final SparseArray<HomeMovie> homeMovies) {

        }

        @Override
        public void onNetState(int netType, boolean isConnected) {
            if (this.hostCls == null) {
                return;
            }
            if (isConnected) {
                this.hostCls.netV.setVisibility(View.VISIBLE);
                switch (netType) {
                    case ConnectivityManager.TYPE_WIFI:
                        this.hostCls.netV.setBackgroundResource(R.drawable.wifi_ok);
                        break;
                    default:
                        this.hostCls.netV.setBackgroundResource(R.drawable.eth_f);
                        break;
                }
            } else {
                this.hostCls.netV.setVisibility(View.GONE);
            }
        }

        @Override
        public void onTimeDate(final String time) {
            if (this.hostCls == null) {
                return;
            }
            this.hostCls.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MOnDataListener.this.hostCls.timeV.setText(time);
                }
            });
        }

        @Override
        public void onUpdateApp(final File apkF, final String desStr) {
        }

        @Override
        public void onMarquee(final MarqueeParser marqueeParser) {

        }

        @Override
        public void onForceTv() {
        }

        @Override
        public void onGetDataOver() {

        }

        @Override
        public void onInitMediaOver() {
        }

        @Override
        public void onFavUpdate() {

        }

        @Override
        public void onChalList(LiveChalObj chal, boolean liveDataDBOK) {

        }

        @Override
        public void onLogin(String loginStr, boolean dialogCancelable) {

        }

        @Override
        public void onToken() {

        }

        @Override
        public void onPay(final boolean b, final String s) {

        }
    }
}
