package com.allcn.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.activities.LiveAct;
import com.allcn.adapters.CKAdapter;
import com.allcn.adapters.ChalAdapter;
import com.allcn.adapters.KindAdapter;
import com.allcn.adapters.KindsAdapter;
import com.allcn.interfaces.ChalListWindow;
import com.allcn.interfaces.ListWindowInterface;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.utils.MSG;
import com.allcn.utils.ViewWrapper;
import com.allcn.views.decorations.LiveKindItemDecoration;
import com.allcn.views.focus.AbsFocusBorder;
import com.allcn.views.focus.FocusBorder;
import com.coorchice.library.SuperTextView;
import com.datas.LiveCKindObj;
import com.datas.LiveChalObj;
import com.datas.LiveKindObj;
import com.db.cls.DBMgr;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class HorChalListV implements ChalListWindow {//直播列表

    private static final String TAG = HorChalListV.class.getSimpleName();
    private final View list1MoreImg;
    private PopupWindow window;
    private RecyclerView list1RV, list2RV, list3RV;
    private RecyclerView.LayoutManager list1LMgr, list2LMgr, list3LMgr;
    private boolean isShown, list1NoFocus, list2NoFocus, list3NoFocus;
    private ListWindowInterface listWindowInterface;
    private LiveAct mainActivityP;
    private LinearLayout rootV, list1RootV;
    private SuperTextView aboutUsV, favV, cateV;
    private TextView list2KindV, list3KindV, list2PageV, list3PageV, aboutV;
    private ObjectAnimator list1WAnimObj;
    private int list1SW, list1BW;
    private KindAdapter list1Adapter;
    private ChalAdapter list3Adapter;
    private CKAdapter list2Adapter;
    private FrameLayout list2RootV, list3RootV;
    private FocusBorder focusBorder;
    private ImageView list2LineImg;
    private MOnKeyListener mOnKeyListener;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private ViewWrapper list1RootWraper;
    private static final int descendantFocusabilityFocus = ViewGroup.FOCUS_BEFORE_DESCENDANTS;
    private static final int descendantFocusabilityNoFocus = ViewGroup.FOCUS_BLOCK_DESCENDANTS;
    private boolean firstRest, isAllKind;
    private DataCenter actCls;
    private KOnAdapterItemListener kOnAdapterItemListener;
    private COnAdapterItemListener cOnAdapterItemListener;
    private CCOnAdapterItemListener ccOnAdapterItemListener;
    private Handler handler = new Handler();
    private Runnable task;
    private String allKindName;
    private int exW, exH, kindExW, kindExH;

    public HorChalListV(LiveAct mainActivity) {

        isAllKind = false;
        allKindName = AppMain.res().getString(R.string.all_kind_name);
        mainActivityP = mainActivity;
        actCls = DataCenter.Ins();

        this.exW = (int) AppMain.res().getDimension(R.dimen.live_chal_ex_width);
        this.exH = (int) AppMain.res().getDimension(R.dimen.live_chal_ex_height);
        this.kindExW = (int) AppMain.res().getDimension(R.dimen.live_kind_ex_width);
        this.kindExH = (int) AppMain.res().getDimension(R.dimen.live_kind_ex_height);

        LayoutInflater layoutInflater = LayoutInflater.from(AppMain.ctx());

        rootV = (LinearLayout) layoutInflater.inflate(R.layout.live_hor_chal_list_v, null,
                false);
        focusBorder = new FocusBorder.Builder()
                .asDrawable()
                .borderResId(R.drawable.kind_item_f)
                .build(rootV);

        mOnKeyListener = new MOnKeyListener(this);
        mOnFocusChangeListener = new MOnFocusChangeListener(this);

        aboutV = rootV.findViewById(R.id.live_about_v);
        list1RootV = rootV.findViewById(R.id.list1_root_v);
        list2RootV = rootV.findViewById(R.id.list2_root_v);
        list3RootV = rootV.findViewById(R.id.list3_root_v);
        aboutUsV = rootV.findViewById(R.id.about_us_v);
        favV = rootV.findViewById(R.id.favorite_v);
        cateV = rootV.findViewById(R.id.category_v);
        list1RV = rootV.findViewById(R.id.list1_rv);
        list2RV = rootV.findViewById(R.id.list2_rv);
        list3RV = rootV.findViewById(R.id.list3_rv);
        list2KindV = rootV.findViewById(R.id.list2_kind_v);
        list2PageV = rootV.findViewById(R.id.list2_page_v);
        list3KindV = rootV.findViewById(R.id.list3_kind_v);
        list3PageV = rootV.findViewById(R.id.list3_page_v);
        list2LineImg = rootV.findViewById(R.id.list2_line_v);
        list1MoreImg = rootV.findViewById(R.id.list1_more);


        aboutUsV.setOnFocusChangeListener(mOnFocusChangeListener);
        favV.setOnFocusChangeListener(mOnFocusChangeListener);
        cateV.setOnFocusChangeListener(mOnFocusChangeListener);
        aboutUsV.setOnKeyListener(mOnKeyListener);
        cateV.setOnKeyListener(mOnKeyListener);
        favV.setOnKeyListener(mOnKeyListener);


        list1LMgr = new LinearLayoutManager(
                mainActivityP, LinearLayoutManager.VERTICAL, false);
        list2LMgr = new LinearLayoutManager(
                mainActivityP, LinearLayoutManager.VERTICAL, false);
        list3LMgr = new LinearLayoutManager(
                mainActivityP, LinearLayoutManager.VERTICAL, false);

        list1Adapter = new KindAdapter(mainActivityP, this);
        list2Adapter = new CKAdapter(mainActivityP);
        list3Adapter = new ChalAdapter(mainActivityP, this);

        list1Adapter.setRecyclerView(list1RV);
        list3Adapter.setRecyclerView(list3RV);

        list1RV.addItemDecoration(new LiveKindItemDecoration());

        list1RV.setLayoutManager(list1LMgr);
        list2RV.setLayoutManager(list2LMgr);
        list3RV.setLayoutManager(list3LMgr);

        list1RV.setHasFixedSize(true);
        list2RV.setHasFixedSize(true);
        list3RV.setHasFixedSize(true);

        list1RV.setAdapter(list1Adapter);
        list3RV.setAdapter(list3Adapter);

        list1SW = (int) AppMain.res().getDimension(R.dimen.list1_small_w);
        list1BW = (int) AppMain.res().getDimension(R.dimen.list1_big_w);
        list1RootWraper = new ViewWrapper(list1RootV);
        list1WAnimObj = ObjectAnimator
                .ofInt(list1RootWraper, "width", list1BW, list1SW)
                .setDuration(EXVAL.LIST1_ANIM_DURATION);
        list1WAnimObj.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
                MLog.d(TAG, "onAnimationCancel");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                MLog.d(TAG, "onAnimationEnd");
                if (favV.isFocused()) {
                    focusBorder.onFocus(favV, AbsFocusBorder.Options.get(exW, exH), false);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                MLog.d(TAG, "onAnimationStart");
            }
        });

        list1Adapter.setOnAdapterItemListener(kOnAdapterItemListener =
                new KOnAdapterItemListener(this));
        list2Adapter.setOnAdapterItemListener(cOnAdapterItemListener =
                new COnAdapterItemListener(this));
        list3Adapter.setOnAdapterItemListener(ccOnAdapterItemListener =
                new CCOnAdapterItemListener(this));

        window = new PopupWindow(rootV, MATCH_PARENT, MATCH_PARENT);
        window.setTouchable(true);
        window.setFocusable(true);
        window.setBackgroundDrawable(new BitmapDrawable());
        window.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                MLog.d(TAG, "HorChalListV window dismiss");
                dismiss();
            }
        });
    }

    @Override
    public void show(View rootV) {

        MLog.d(TAG, "HorChalListV show");

        isShown = true;

        long startT = System.currentTimeMillis();

        String verName = "";

        try {
            verName = AppMain.ctx().getPackageManager().getPackageInfo(
                    AppMain.ctx().getPackageName(), 0).versionName;
        } catch (Exception e) {
        }

        aboutV.setText(AppMain.res().getString(R.string.about_live_content, verName));

        list1Adapter.resumeSel(mainActivityP.selLiveKIndex);

        mainActivityP.setTmpCacheKindVal(mainActivityP.cacheLiveKind);
        mainActivityP.selLiveKIndex = mainActivityP.curLiveKIndex;
        mainActivityP.selLiveCKIndex = mainActivityP.curLiveCKIndex;
        mainActivityP.selLiveChalIndex = mainActivityP.curLiveChalIndex % EXVAL.NUM_IN_COL_LIVE;
        mainActivityP.curLivePageIndex = mainActivityP.curLiveChalIndex / EXVAL.NUM_IN_COL_LIVE;
        mainActivityP.selLivePageIndex = mainActivityP.curLivePageIndex;
        mainActivityP.selLiveCKind = mainActivityP.curLiveCKind;
        mainActivityP.selLiveKind = mainActivityP.curLiveKind;
        mainActivityP.selLiveChal = mainActivityP.curLiveChal;

        this.isAllKind = mainActivityP.tmpCacheLiveKind.getColName().equals(allKindName);

        if (!firstRest) {
            firstRest = true;
            resetUI();
        }

        if (list1Adapter.getItemCount() == 0) {
            list1Adapter.setDatas(mainActivityP.liveKinds);
        }

        if (mainActivityP.tmpCacheLiveKind.getColName().equals(AppMain.res().getString(R.string.fav_list))) {
            list1Adapter.resumeSel(mainActivityP.selLiveKIndex);
            mainActivityP.setLiveFavK(true);
            list3RootV.setVisibility(View.GONE);
            list3Adapter.setRecyclerView(list2RV);
            list2RV.setAdapter(list3Adapter);
            initChalData();
            list2PageV.setText(String.format("%s/%s", mainActivityP.curLiveChalIndex + 1,
                    list3Adapter.getNum()/*mainActivityP.tmpCacheLiveKind.getChalNum()*/));
            list2KindV.setText(AppMain.res().getString(R.string.fav_list));
            favV.setShowState(false);
            favV.setShowState2(true);
            favV.setTextColor(AppMain.res().getColor(R.color.list_item_nofocus_color));
            if (mainActivityP.tmpCacheLiveKind.getChalNum() > 0) {
                Utils.focusV(list2RootV, true);
                final int selPos = list3Adapter.getItemPosInList(mainActivityP.selLiveChal);
                list3RV.post(new Runnable() {
                    @Override
                    public void run() {
                        list2RootV.setDescendantFocusability(descendantFocusabilityFocus);
                        list3Adapter.setSelection(focusBorder, selPos);
                        list2NoFocus = false;
                    }
                });
            } else {
                list1RootV.setDescendantFocusability(descendantFocusabilityFocus);
                Utils.focusV(list1RootV, true);
                favV.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.focusV(favV, true);
                    }
                });
            }
        } else {
            list1Adapter.marketSel(mainActivityP.selLiveKIndex);
            mainActivityP.setLiveFavK(false);
            cateV.setShowState(false);
            cateV.setShowState2(true);
            cateV.setTextColor(AppMain.res().getColor(R.color.list_item_nofocus_color));
            if (mainActivityP.curLiveKind.getCKNum() > 0) {
                list2Adapter.setRecyclerView(list2RV);
                list2RV.setAdapter(list2Adapter);
                list3Adapter.setRecyclerView(list3RV);
                list3RV.setAdapter(list3Adapter);
                list3RootV.setVisibility(View.VISIBLE);
                list2LineImg.setBackgroundResource(R.drawable.chal_list_line_l);
                list2PageV.setText(String.format("%s/%s", mainActivityP.curLiveCKIndex + 1,
                        mainActivityP.curLiveKind.getCKNum()));
                list2KindV.setText(mainActivityP.curLiveKind.getColName());
                list3PageV.setText(String.format("%s/%s", mainActivityP.curLiveChalIndex + 1,
                        mainActivityP.tmpCacheLiveKind.getChalNum()));
                list3KindV.setText(mainActivityP.curLiveCKind.getColName());
                List<LiveCKindObj> ckinds = DBMgr.Ins().queryLiveCKindsForCol(
                        mainActivityP.curLiveKind.getColId(), mainActivityP.curLiveKind.getColName());
                list2Adapter.setDatas(ckinds);
                if (mainActivityP.curLiveCKind.getChalNum() > 0) {
                    Utils.focusV(list3RootV, true);
                    initChalData();
                    final int selPos = list3Adapter.getItemPosInList(mainActivityP.curLiveChal);
                    list3RV.post(new Runnable() {
                        @Override
                        public void run() {
                            list2Adapter.marketSel(mainActivityP.selLiveCKIndex);
                            list3RootV.setDescendantFocusability(descendantFocusabilityFocus);
                            list3Adapter.setSelection(focusBorder, selPos);
                            list3NoFocus = false;
                        }
                    });
                } else {
                    initChalData();
                    Utils.focusV(list2RootV, true);
                    final int selPos = list2Adapter.getItemPosInList(mainActivityP.selLiveCKind);
                    list2RV.post(new Runnable() {
                        @Override
                        public void run() {
                            list2RootV.setDescendantFocusability(descendantFocusabilityFocus);
                            list2Adapter.setSelection(focusBorder, selPos);
                            list2NoFocus = false;
                        }
                    });
                }
            } else {
                list3RootV.setVisibility(View.GONE);
                list3Adapter.setRecyclerView(list2RV);
                list2RV.setAdapter(list3Adapter);
                list2KindV.setText(mainActivityP.curLiveKind.getColName());
                if (mainActivityP.curLiveKind.getChalNum() > 0) {
                    cateV.setShowState(false);
                    cateV.setShowState2(true);
                    Utils.focusV(list2RootV, true);
                    initChalData();
                    final int selPos = list3Adapter.getItemPosInList(mainActivityP.selLiveChal);
                    list2RV.post(new Runnable() {
                        @Override
                        public void run() {
                            list2RootV.setDescendantFocusability(descendantFocusabilityFocus);
                            list3Adapter.setSelection(focusBorder, selPos);
                            list2NoFocus = false;
                        }
                    });
                } else {
                    initChalData();
                    cateV.setShowState(true);
                    cateV.setShowState2(false);
                    Utils.focusV(list1RootV, true);
                    Utils.focusV(list1RV, true);
                    if (task != null) {
                        handler.removeCallbacks(task);
                    }
                    task = new Runnable() {
                        @Override
                        public void run() {
                            list1RootV.setDescendantFocusability(descendantFocusabilityFocus);
                            list1Adapter.setSelection(focusBorder, mainActivityP.curLiveKIndex);
                            list1NoFocus = false;
                        }
                    };
                    handler.postDelayed(task, EXVAL.LIST1_ANIM_DURATION);
                }
                list2PageV.setText(String.format("%s/%s", mainActivityP.curLiveChalIndex + 1,
                        list3Adapter.getNum()/*mainActivityP.tmpCacheLiveKind.getChalNum()*/));
            }
        }

        window.showAtLocation(rootV, Gravity.LEFT, 0, 0);

        MLog.d(TAG, "show cost " + (System.currentTimeMillis() - startT));
        Utils.sendMsg(mainActivityP.mHandler, MSG.HIDE_LIST, EXVAL.SHOW_LIST_TIMEOUT);
    }

    private void resetUI() {

        if (!list2RV.isShown()) {
            list2RV.setVisibility(View.VISIBLE);
            list2PageV.setVisibility(View.VISIBLE);
            list2KindV.setVisibility(View.VISIBLE);
            list2LineImg.setVisibility(View.VISIBLE);
        }

        list1NoFocus = true;
        list2NoFocus = true;
        list3NoFocus = true;
        list1RootV.setDescendantFocusability(descendantFocusabilityNoFocus);
        list2RootV.setDescendantFocusability(descendantFocusabilityNoFocus);
        list3RootV.setDescendantFocusability(descendantFocusabilityNoFocus);
        Utils.noFocus(list1RootV);
        Utils.noFocus(list2RootV);
        Utils.noFocus(list3RootV);
        Utils.noFocus(aboutUsV);
        Utils.noFocus(favV);
        Utils.noFocus(cateV);

        cateV.setShowState(true);
        cateV.setShowState2(false);
        favV.setShowState(true);
        favV.setShowState2(false);
        aboutUsV.setShowState(true);
        aboutUsV.setShowState2(false);

        String curColName = mainActivityP == null ? null : mainActivityP.cacheLiveKind == null ?
                null : mainActivityP.cacheLiveKind.getColName();

        if (!TextUtils.isEmpty(curColName)) {
            mainActivityP.setLiveFavK(curColName.equals(AppMain.res().getString(R.string.fav_list)));
        }
    }

    @Override
    public void dismiss() {
        if (isShown) {
            window.dismiss();
            isShown = false;
            Utils.removeMsg(mainActivityP.mHandler, MSG.HIDE_LIST);
            resetUI();
        }
    }

    @Override
    public void relase() {
        if (task != null) {
            handler.removeCallbacks(task);
        }
        handler.removeCallbacksAndMessages(null);
        handler = null;
        dismiss();
        mOnFocusChangeListener.release();
        mOnKeyListener.release();
        list1Adapter.release();
        list2Adapter.release();
        list3Adapter.release();
        kOnAdapterItemListener.release();
        kOnAdapterItemListener = null;
        cOnAdapterItemListener.release();
        cOnAdapterItemListener = null;
        ccOnAdapterItemListener.release();
        ccOnAdapterItemListener = null;
        list1Adapter = null;
        list2Adapter = null;
        list3Adapter = null;
        list1LMgr = null;
        list2LMgr = null;
        list3LMgr = null;
        listWindowInterface = null;
        window.setOnDismissListener(null);
        window = null;
        rootV = null;
        mainActivityP = null;
        mOnFocusChangeListener = null;
        mOnKeyListener = null;
        allKindName = null;
    }

    public boolean isShown() {
        return isShown;
    }

    public void setListWindowInterface(ListWindowInterface listWindowInterface) {
        this.listWindowInterface = listWindowInterface;
    }

    private void initChalData() {
//        MLog.d(TAG, String.format("initChalData %s", actCls.tmpCacheKind.getColName()));
        List<LiveChalObj> chals = null;
        List<LiveChalObj> allChalList = null;
        if (mainActivityP.isLiveFavK()) {
            chals = DBMgr.Ins().queryLiveFavList(mainActivityP.selLivePageIndex);
            allChalList = DBMgr.Ins().queryLiveFavList();
        } else {
            chals = this.isAllKind ?
                    DBMgr.Ins().queryLiveChalsForAll(mainActivityP.selLivePageIndex) :
                    DBMgr.Ins().queryLiveChalsForCol(mainActivityP.tmpCacheLiveKind.getColId(),
                            mainActivityP.tmpCacheLiveKind.getColName(),
                            mainActivityP.selLivePageIndex);
            allChalList = this.isAllKind ?
                    DBMgr.Ins().queryLiveChalsForAll() :
                    DBMgr.Ins().queryLiveChalsForCol(mainActivityP.tmpCacheLiveKind.getColId(),
                            mainActivityP.tmpCacheLiveKind.getColName());


        }
        list3Adapter.setDatas(chals);
        list3Adapter.setAllDatas(allChalList);

        if (mainActivityP.isLiveFavK() && chals.size() == 0 && !list1RootV.isFocusable()) {
            Utils.focusV(favV, false);
            Utils.focusV(cateV, false);
            Utils.focusV(aboutUsV, false);
            list2RootV.setDescendantFocusability(descendantFocusabilityNoFocus);
            list2NoFocus = true;
            Utils.noFocus(list2RootV);
            Utils.focusV(list1RootV, true);
            favV.setShowState(true);
            favV.setShowState2(false);
            list1RootV.setDescendantFocusability(descendantFocusabilityFocus);
            Utils.focusV(favV, true);
        }
    }


    private void loadChalData(boolean isFocus, final boolean isDown) {//加载节目数据
        List<LiveChalObj> chals = null;
        List<LiveChalObj> allChalList = null;
        if (mainActivityP.isLiveFavK()) {
            chals = DBMgr.Ins().queryLiveFavList(mainActivityP.selLivePageIndex);
            allChalList = DBMgr.Ins().queryLiveFavList();
        } else {
            String colName = mainActivityP.tmpCacheLiveKind.getColName();
            String colId = mainActivityP.tmpCacheLiveKind.getColId();
            chals = this.isAllKind ?
                    DBMgr.Ins().queryLiveChalsForAll(mainActivityP.selLivePageIndex) :
                    DBMgr.Ins().queryLiveChalsForCol(
                            colId, colName, mainActivityP.selLivePageIndex);
            allChalList = this.isAllKind ?
                    DBMgr.Ins().queryLiveChalsForAll() : DBMgr.Ins().queryLiveChalsForCol(colId, colName);
        }
        list3Adapter.setDatas(chals);
        list3Adapter.setAllDatas(allChalList);
        if (chals.size() > 0) {
            mainActivityP.selLiveChal = chals.get(0);
        }
        final int selPos = isDown ? 0 : list3Adapter.getItemCount() - 1;
        if (isFocus) {
            list2RV.post(new Runnable() {
                @Override
                public void run() {
                    list3Adapter.setSelection(focusBorder, selPos);
                }
            });
        }
    }

    public void updateFavList() {
        boolean changeToOthPage = false;
        int pageNum = mainActivityP.tmpCacheLiveKind.getPageNum();
        if (mainActivityP.selLivePageIndex >= pageNum) {
            mainActivityP.selLivePageIndex = pageNum - 1;
            changeToOthPage = true;
        }
        initChalData();
        int loadNum = list3Adapter.getItemCount();
        if (changeToOthPage || (mainActivityP.selLiveChalIndex >= loadNum)) {
            mainActivityP.selLiveChalIndex = loadNum - 1;
        }
        list2RV.post(new Runnable() {
            @Override
            public void run() {
                list3Adapter.setSelection(focusBorder, mainActivityP.selLiveChalIndex);
            }
        });
    }

    public void clearFocus() {
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private HorChalListV ref;

        public MOnFocusChangeListener(HorChalListV horChalListV) {
            ref = horChalListV;
        }

        public void release() {
            ref = null;
        }

        @Override
        public void onFocusChange(final View v, boolean hasFocus) {
            if (ref == null) {
                return;
            }
            MLog.d(TAG, String.format("onFocusChange %b %s %b", hasFocus, v, ref.list1NoFocus));

            Utils.sendMsg(ref.mainActivityP.mHandler, MSG.HIDE_LIST, EXVAL.SHOW_LIST_TIMEOUT);

            if (ref.list1RV.isFocused()) {
                return;
            }

            SuperTextView superTextView = null;

            switch (v.getId()) {
                case R.id.about_us_v:
                    superTextView = (SuperTextView) v;
                    if (hasFocus) {
                        ref.aboutV.setVisibility(View.VISIBLE);
                        ref.list2RV.setVisibility(View.GONE);
                        ref.list2PageV.setVisibility(View.GONE);
                        ref.list2KindV.setVisibility(View.GONE);
                        ref.list2LineImg.setVisibility(View.GONE);
                    } else {
                        ref.aboutV.setVisibility(View.GONE);
                    }
                    break;
                case R.id.favorite_v:
                    superTextView = (SuperTextView) v;
                    if (hasFocus) {
                        ref.mainActivityP.setLiveFavK(true);
                        if (ref.list3RootV.isShown()) {
                            ref.list3NoFocus = true;
                            ref.list3RootV.setVisibility(View.GONE);
                            ref.list2LineImg.setBackgroundResource(R.drawable.chal_list_line);
                        }
                        if (!ref.list2RV.isShown()) {
                            ref.list2RV.setVisibility(View.VISIBLE);
                            ref.list2PageV.setVisibility(View.VISIBLE);
                            ref.list2KindV.setVisibility(View.VISIBLE);
                            ref.list2LineImg.setVisibility(View.VISIBLE);
                        }
                        ref.mainActivityP.selLivePageIndex = 0;
                        LiveKindObj favKind = DBMgr.Ins().queryLivekind(
                                String.valueOf(EXVAL.PTYPE_FAV_LIVE),
                                AppMain.res().getString(R.string.fav_list), false);
                        ref.list2KindV.setText(favKind.getColName());
                        ref.list2PageV.setText(String.format("%d/%d",
                                favKind.getChalNum() > 0 ? 1 : 0, favKind.getChalNum()));
                        ref.mainActivityP.setTmpCacheKindVal(favKind);
                        ref.loadChalData(false, false);
                    }
                    break;
                case R.id.category_v:
                    superTextView = (SuperTextView) v;
                    if (hasFocus) {
                        ref.mainActivityP.setLiveFavK(false);
                        ref.mainActivityP.selLiveKIndex = 0;
                        ref.mainActivityP.selLivePageIndex = 0;
                        ref.mainActivityP.selLiveKind = ref.mainActivityP.liveKinds.get(ref.mainActivityP.selLiveKIndex);
                        if (ref.mainActivityP.selLiveKind.getCKNum() > 0) {
                            List<LiveCKindObj> cKinds = DBMgr.Ins().queryLiveCKindsForCol(
                                    ref.mainActivityP.selLiveKind.getColId(), ref.mainActivityP.selLiveKind.getColName());
                            ref.mainActivityP.selLiveCKIndex = 0;
                            ref.mainActivityP.selLiveCKind = cKinds.get(0);
                            ref.list2KindV.setText(ref.mainActivityP.selLiveKind.getColName());
                            ref.list2PageV.setText(String.format("%d/%d", 1, ref.mainActivityP.selLiveKind.getCKNum()));
                            ref.list3KindV.setText(ref.mainActivityP.selLiveCKind.getColName());
                            ref.list3PageV.setText(String.format("%d/%d", 1, ref.mainActivityP.selLiveCKind.getChalNum()));
                            ref.list2Adapter.setRecyclerView(ref.list2RV);
                            ref.list3Adapter.setRecyclerView(ref.list3RV);
                            ref.list2RV.setAdapter(ref.list2Adapter);
                            ref.list2Adapter.setDatas(cKinds);
                            ref.list2LineImg.setBackgroundResource(R.drawable.chal_list_line_l);
                            ref.list3RootV.setVisibility(View.VISIBLE);
                            ref.mainActivityP.setTmpCacheKindVal(ref.mainActivityP.selLiveCKind);
                            ref.list2RV.post(new Runnable() {
                                @Override
                                public void run() {
                                    ref.list1Adapter.marketSel(0);
                                    ref.list2Adapter.marketSel(0);
                                }
                            });
                        } else {
                            ref.mainActivityP.setTmpCacheKindVal(ref.mainActivityP.selLiveKind);
                            ref.list2KindV.setText(ref.mainActivityP.selLiveKind.getColName());
                            ref.list2PageV.setText(String.format("%d/%d", 1, ref.mainActivityP.selLiveKind.getChalNum()));
                            ref.list2RV.setAdapter(ref.list3Adapter);
                            ref.list3Adapter.setRecyclerView(ref.list2RV);
                            ref.list3RootV.setVisibility(View.GONE);
                            Utils.noFocus(ref.list3RootV);
                            ref.list3RootV.setDescendantFocusability(descendantFocusabilityNoFocus);
                            ref.list2LineImg.setBackgroundResource(R.drawable.chal_list_line);
                        }
                        ref.loadChalData(false, false);
                    }
                    break;
                default:
                    superTextView = null;
                    break;
            }

            if (superTextView != null) {
                if (hasFocus) {
                    superTextView.setShowState(false);
                    superTextView.setShowState2(true);
                    superTextView.setTextColor(
                            AppMain.res().getColor(R.color.list_item_nofocus_color));
                    v.post(new Runnable() {
                        @Override
                        public void run() {

                            ref.focusBorder.onFocus(v, AbsFocusBorder.Options.get(ref.exW, ref.exH),
                                    false);
                        }
                    });
                } else {
                    superTextView.setShowState(true);
                    superTextView.setShowState2(false);
                    superTextView.setTextColor(Color.WHITE);
                }
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private HorChalListV ref;

        public MOnKeyListener(HorChalListV horChalListV) {
            ref = horChalListV;
        }

        public void release() {
            ref = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (ref == null) {
                return false;
            }

            Utils.sendMsg(ref.mainActivityP.mHandler, MSG.HIDE_LIST, EXVAL.SHOW_LIST_TIMEOUT);

            int vId = v.getId();

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
//                    case KeyEvent.KEYCODE_DPAD_UP:
//                        switch (vId) {
//                            case R.id.category_v:
//                                Utils.focusV(ref.favV, true);
//                                break;
//                            case R.id.favorite_v:
//                                Utils.focusV(ref.aboutUsV, true);
//                                break;
//                        }
//                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        switch (vId) {
//                            case R.id.about_us_v:
//                                Utils.focusV(ref.favV, true);
//                                break;
//                            case R.id.favorite_v:
//                                Utils.focusV(ref.cateV, true);
//                                break;
                            case R.id.category_v:
//                                Utils.focusV(ref.list1RV, true);
                                ref.list1RV.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ref.mainActivityP.selLiveChalIndex = 0;
                                        ref.list1Adapter.setSelection(ref.focusBorder, 0);
                                        ref.list1NoFocus = false;
                                    }
                                });
                                break;
                        }
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        switch (vId) {
                            case R.id.favorite_v:
                            case R.id.category_v:
                                if (ref.list2RV.getChildCount() > 0) {
                                    ref.list1RootV.setDescendantFocusability(
                                            descendantFocusabilityNoFocus);
                                    Utils.noFocus(ref.aboutUsV);
                                    Utils.noFocus(ref.favV);
                                    Utils.noFocus(ref.cateV);
                                    Utils.noFocus(ref.list1RootV);
                                    ref.list1NoFocus = true;
                                    ((SuperTextView) v).setShowState(false);
                                    ((SuperTextView) v).setShowState2(true);
                                    ((SuperTextView) v).setTextColor(AppMain.res().getColor(R.color.list_item_nofocus_color));
                                    Utils.focusV(ref.list2RootV, true);
                                    ref.list2RootV.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ref.mainActivityP.selLiveChalIndex = 0;
                                            ref.list2RootV.setDescendantFocusability(
                                                    descendantFocusabilityFocus);
                                            if (ref.list3RootV.isShown()) {
                                                ref.list2Adapter.setSelection(ref.focusBorder, 0);
                                            } else {
                                                ref.list3Adapter.setSelection(ref.focusBorder, 0);
                                            }
                                            ref.list2NoFocus = false;
                                        }
                                    });
                                }
                                break;
                        }
                        break;
                }
            }

            return false;
        }
    }

    private void handleChalTips(final View view) {
        if (mainActivityP == null) {
            return;
        }
        Utils.removeMsg(mainActivityP.mHandler, MSG.HIDE_LIST);
        OpDialog opDialog = new OpDialog(mainActivityP);
        opDialog.initDatas(this, mainActivityP, view);
        opDialog.show();
        opDialog.getWindow().setLayout(
                (int) AppMain.res().getDimension(R.dimen.live_op_dialog_w),
                (int) AppMain.res().getDimension(R.dimen.live_op_dialog_h));
    }

    private static class KOnAdapterItemListener implements OnRvAdapterListener {

        private HorChalListV hostCls;

        public KOnAdapterItemListener(HorChalListV hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onItemSelected(final View view, int position) {
            if (hostCls == null) {
                return;
            }
            MLog.d(TAG, String.format("list1Adapter onItemSelect list1NoFocus=%b", hostCls.list1NoFocus));
            Utils.sendMsg(hostCls.mainActivityP.mHandler, MSG.HIDE_LIST, EXVAL.SHOW_LIST_TIMEOUT);
            if (hostCls.list1NoFocus) {
                return;
            }
            hostCls.mainActivityP.selLiveKIndex = position;
            hostCls.mainActivityP.selLivePageIndex = 0;
            hostCls.mainActivityP.selLiveKind = hostCls.mainActivityP.liveKinds.get(position);
            hostCls.mainActivityP.selLiveChalIndex = 0;
            if (hostCls.mainActivityP.selLiveKind.getCKNum() > 0) {
                List<LiveCKindObj> cKinds = DBMgr.Ins().queryLiveCKindsForCol(
                        hostCls.mainActivityP.selLiveKind.getColId(), hostCls.mainActivityP.selLiveKind.getColName());
                hostCls.mainActivityP.selLiveCKIndex = 0;
                hostCls.mainActivityP.selLiveCKind = cKinds.get(0);
                hostCls.list2KindV.setText(hostCls.mainActivityP.selLiveKind.getColName());
                hostCls.list2PageV.setText(String.format("%d/%d", 1, hostCls.mainActivityP.selLiveKind.getCKNum()));
                hostCls.list3KindV.setText(hostCls.mainActivityP.selLiveCKind.getColName());
                hostCls.list3PageV.setText(String.format("%d/%d",
                        hostCls.mainActivityP.selLiveCKind.getChalNum() > 0 ? 1 : 0, hostCls.mainActivityP.selLiveCKind.getChalNum()));
                hostCls.list2Adapter.setRecyclerView(hostCls.list2RV);
                hostCls.list3Adapter.setRecyclerView(hostCls.list3RV);
                hostCls.list2RV.setAdapter(hostCls.list2Adapter);
                hostCls.list2Adapter.setDatas(cKinds);
                hostCls.list2LineImg.setBackgroundResource(R.drawable.chal_list_line_l);
                hostCls.list3RootV.setVisibility(View.VISIBLE);
                hostCls.mainActivityP.setTmpCacheKindVal(hostCls.mainActivityP.selLiveCKind);
            } else {
                hostCls.mainActivityP.setTmpCacheKindVal(hostCls.mainActivityP.selLiveKind);
                hostCls.list2KindV.setText(hostCls.mainActivityP.selLiveKind.getColName());
                hostCls.list2PageV.setText(String.format("%d/%d",
                        hostCls.mainActivityP.selLiveKind.getChalNum() > 0 ? 1 : 0, hostCls.mainActivityP.selLiveKind.getChalNum()));
                hostCls.list2RV.setAdapter(hostCls.list3Adapter);
                hostCls.list3Adapter.setRecyclerView(hostCls.list2RV);
                hostCls.list3RootV.setVisibility(View.GONE);
                Utils.noFocus(hostCls.list3RootV);
                hostCls.list3RootV.setDescendantFocusability(descendantFocusabilityNoFocus);
                hostCls.list2LineImg.setBackgroundResource(R.drawable.chal_list_line);
            }
            hostCls.isAllKind = hostCls.mainActivityP.tmpCacheLiveKind.getColName().
                    equals(hostCls.allKindName);
            hostCls.loadChalData(false, false);
            hostCls.list1RV.post(new Runnable() {
                @Override
                public void run() {
                    hostCls.handleKindItemFocus(view);
                }
            });

            if (position == hostCls.list1Adapter.getItemCount() - 1){
                hostCls.list1MoreImg.setVisibility(View.GONE);
            }else {
                hostCls.list1MoreImg.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            if (hostCls == null) {
                return;
            }
            Utils.sendMsg(hostCls.mainActivityP.mHandler, MSG.HIDE_LIST, EXVAL.SHOW_LIST_TIMEOUT);
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (hostCls.mainActivityP.selLiveKIndex == 0) {
                        hostCls.cateV.post(new Runnable() {
                            @Override
                            public void run() {
                                hostCls.list1NoFocus = true;
                                Utils.focusV(hostCls.cateV, true);
                            }
                        });
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (hostCls.list2RV.getChildCount() > 0) {
                        hostCls.list1RootV.setDescendantFocusability(descendantFocusabilityNoFocus);
                        Utils.noFocus(hostCls.aboutUsV);
                        Utils.noFocus(hostCls.favV);
                        Utils.noFocus(hostCls.cateV);
                        Utils.noFocus(hostCls.list1RootV);
                        hostCls.list1NoFocus = true;
                        hostCls.cateV.setShowState(false);
                        hostCls.cateV.setShowState2(true);
                        Utils.focusV(hostCls.list2RootV, true);
                        hostCls.list1Adapter.marketSel(hostCls.mainActivityP.selLiveKIndex);
                        hostCls.list2RootV.post(new Runnable() {
                            @Override
                            public void run() {
                                hostCls.list2RootV.setDescendantFocusability(descendantFocusabilityFocus);
                                if (hostCls.list3RootV.isShown()) {
                                    hostCls.list2Adapter.setSelection(hostCls.focusBorder,
                                            hostCls.mainActivityP.selLiveCKIndex);
                                } else {
                                    hostCls.list3Adapter.setSelection(hostCls.focusBorder,
                                            hostCls.mainActivityP.selLiveChalIndex);
                                }
                                hostCls.list2NoFocus = false;
                            }
                        });
                    }
                    break;
            }
        }

        @Override
        public void onItemClick(View view, int position) {
            if (hostCls == null) {
                return;
            }
            Utils.sendMsg(hostCls.mainActivityP.mHandler, MSG.HIDE_LIST, EXVAL.SHOW_LIST_TIMEOUT);
        }

        @Override
        public void onItemLongClick(View view, int position) {
            if (hostCls == null) {
                return;
            }
            Utils.sendMsg(hostCls.mainActivityP.mHandler, MSG.HIDE_LIST, EXVAL.SHOW_LIST_TIMEOUT);
        }
    }

    private static class COnAdapterItemListener implements OnRvAdapterListener {

        private HorChalListV hostCls;

        public COnAdapterItemListener(HorChalListV hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onItemSelected(final View view, int position) {
            if (hostCls == null) {
                return;
            }
            MLog.d(TAG, String.format("list2Adapter list2NoFocus=%b", hostCls.list2NoFocus));
            Utils.sendMsg(hostCls.mainActivityP.mHandler, MSG.HIDE_LIST, EXVAL.SHOW_LIST_TIMEOUT);
            if (hostCls.list2NoFocus) {
                return;
            }
            hostCls.mainActivityP.selLiveChalIndex = 0;
            hostCls.mainActivityP.selLiveCKIndex = position;
            hostCls.mainActivityP.selLivePageIndex = 0;
            hostCls.mainActivityP.selLiveCKind = hostCls.list2Adapter.getItemObject(position);
            MLog.d(TAG, String.format("onItemSelect %s", hostCls.mainActivityP.selLiveCKind));
            hostCls.mainActivityP.setTmpCacheKindVal(hostCls.mainActivityP.selLiveCKind);
            hostCls.list3KindV.setText(hostCls.mainActivityP.selLiveCKind.getColName());
            hostCls.list3PageV.setText(String.format("%d/%d",
                    hostCls.mainActivityP.selLiveCKind.getChalNum() > 0 ? 1 : 0,
                    hostCls.mainActivityP.selLiveCKind.getChalNum()));
            hostCls.loadChalData(false, false);
            hostCls.list2RV.post(new Runnable() {
                @Override
                public void run() {
                    hostCls.focusBorder.onFocus(view,
                            AbsFocusBorder.Options.get(hostCls.exW, hostCls.exH), false);
                }
            });
        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            if (hostCls == null) {
                return;
            }
            Utils.sendMsg(hostCls.mainActivityP.mHandler, MSG.HIDE_LIST, EXVAL.SHOW_LIST_TIMEOUT);
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    if (hostCls.list3RV.getChildCount() > 0) {
                        hostCls.list2NoFocus = true;
                        final int selPos = hostCls.mainActivityP.selLiveChalIndex;
                        hostCls.list2RootV.setDescendantFocusability(descendantFocusabilityNoFocus);
                        Utils.noFocus(hostCls.list2RootV);
                        Utils.focusV(hostCls.list3RootV, true);
                        hostCls.list2Adapter.marketSel(hostCls.mainActivityP.selLiveCKIndex);
                        hostCls.list3RV.post(new Runnable() {
                            @Override
                            public void run() {
                                hostCls.list3RootV.setDescendantFocusability(descendantFocusabilityFocus);
                                hostCls.list3Adapter.setSelection(hostCls.focusBorder, selPos);
                                hostCls.list3NoFocus = false;
                            }
                        });
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    if (hostCls.task != null) {
                        hostCls.handler.removeCallbacks(hostCls.task);
                    }
                    Utils.focusV(hostCls.favV, false);
                    Utils.focusV(hostCls.cateV, false);
                    Utils.focusV(hostCls.aboutUsV, false);
                    hostCls.list2RootV.setDescendantFocusability(descendantFocusabilityNoFocus);
                    hostCls.list2NoFocus = true;
                    Utils.noFocus(hostCls.list2RootV);
                    Utils.focusV(hostCls.list1RootV, true);
                    hostCls.cateV.setShowState(true);
                    hostCls.cateV.setShowState2(false);
                    hostCls.cateV.setTextColor(Color.WHITE);
                    hostCls.list2Adapter.marketSel(hostCls.mainActivityP.selLiveCKIndex);
                    final int selPos = hostCls.list1Adapter.getItemPosInList(hostCls.mainActivityP.selLiveKind);
                    hostCls.task = new Runnable() {
                        @Override
                        public void run() {
                            hostCls.list1RootV.setDescendantFocusability(descendantFocusabilityFocus);
                            hostCls.list1Adapter.setSelection(hostCls.focusBorder, selPos);
                            hostCls.list1NoFocus = false;
                        }
                    };
                    hostCls.handler.postDelayed(hostCls.task, EXVAL.LIST1_ANIM_DURATION);
                    break;
            }
        }

        @Override
        public void onItemClick(View view, int position) {
            if (hostCls == null) {
                return;
            }
            Utils.sendMsg(hostCls.mainActivityP.mHandler, MSG.HIDE_LIST, EXVAL.SHOW_LIST_TIMEOUT);
        }

        @Override
        public void onItemLongClick(View view, int position) {
            if (hostCls == null) {
                return;
            }
            Utils.sendMsg(hostCls.mainActivityP.mHandler, MSG.HIDE_LIST, EXVAL.SHOW_LIST_TIMEOUT);
        }
    }

    private static class CCOnAdapterItemListener implements OnRvAdapterListener {

        private HorChalListV hostCls;

        public CCOnAdapterItemListener(HorChalListV hostCls) {
            this.hostCls = hostCls;
        }

        public void release() {
            this.hostCls = null;
        }

        @Override
        public void onItemSelected(final View view, int position) {
            if (hostCls == null) {
                return;
            }
            MLog.d(TAG, String.format("list3Adapter list3NoFocus=%b list2NoFocus=%b",
                    hostCls.list3NoFocus, hostCls.list2NoFocus));
            Utils.sendMsg(hostCls.mainActivityP.mHandler, MSG.HIDE_LIST, EXVAL.SHOW_LIST_TIMEOUT);
            boolean list3RootVShown = hostCls.list3RootV.isShown();
            if (list3RootVShown) {
                if (hostCls.list3NoFocus) {
                    return;
                }
            } else {
                if (hostCls.list2NoFocus) {
                    return;
                }
            }
            MLog.d(TAG, String.format("list3Adapter onItemSelect %d", position));
            hostCls.mainActivityP.selLiveChalIndex = position;
            hostCls.mainActivityP.selLiveChal = hostCls.list3Adapter.getItemObject(position);
            if (hostCls.mainActivityP.selLiveChal == null) {
                return;
            }
            MLog.d(TAG, String.format("onItemSelect %s", hostCls.mainActivityP.selLiveChal));
            if (list3RootVShown) {
                hostCls.list3PageV.setText(String.format("%s/%s", hostCls.mainActivityP.selLiveChal.getUiPos(),
                        hostCls.mainActivityP.tmpCacheLiveKind.getChalNum()));
            } else {
                hostCls.list2PageV.setText(String.format("%s/%s", hostCls.mainActivityP.selLiveChal.getUiPos(),
                        hostCls.list3Adapter.getNum()/*hostCls.mainActivityP.tmpCacheLiveKind.getChalNum()*/));
            }
            hostCls.list3RV.post(new Runnable() {
                @Override
                public void run() {
                    hostCls.handleChalItemFocus(view);
                }
            });
        }

        @Override
        public void onItemKey(View view, int position, KeyEvent event, int keyCode) {
            if (hostCls == null) {
                return;
            }
            Utils.sendMsg(hostCls.mainActivityP.mHandler, MSG.HIDE_LIST, EXVAL.SHOW_LIST_TIMEOUT);
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (hostCls.mainActivityP.selLiveChalIndex == 0) {
                        hostCls.mainActivityP.selLivePageIndex--;
                        if (hostCls.mainActivityP.selLivePageIndex < 0) {
                            hostCls.mainActivityP.selLivePageIndex = /*hostCls.mainActivityP.tmpCacheLiveKind.getPageNum()*/hostCls.list3Adapter.getPageNum() - 1;
                        }
                        hostCls.loadChalData(true, false);
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (hostCls.mainActivityP.selLiveChalIndex == hostCls.list3Adapter.getLastIndex()) {
                        hostCls.mainActivityP.selLivePageIndex++;
                        if (hostCls.mainActivityP.selLivePageIndex >=hostCls.list3Adapter.getPageNum() /*hostCls.mainActivityP.tmpCacheLiveKind.getPageNum()*/) {
                            hostCls.mainActivityP.selLivePageIndex = 0;
                        }
                        hostCls.loadChalData(true, true);
                    }
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    hostCls.mainActivityP.selLivePageIndex--;
                    if (hostCls.mainActivityP.selLivePageIndex < 0) {
                        hostCls.mainActivityP.selLivePageIndex = hostCls.list3Adapter.getPageNum()/*hostCls.mainActivityP.tmpCacheLiveKind.getPageNum()*/-1;
                    }
                    hostCls.loadChalData(true, true);
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    hostCls.mainActivityP.selLivePageIndex++;
                    if (hostCls.mainActivityP.selLivePageIndex >= hostCls.list3Adapter.getPageNum()/*hostCls.mainActivityP.tmpCacheLiveKind.getPageNum()*/) {
                        hostCls.mainActivityP.selLivePageIndex = 0;
                    }
                    hostCls.loadChalData(true, true);
                    break;

                case KeyEvent.KEYCODE_DPAD_LEFT:
                    Utils.focusV(hostCls.aboutUsV, false);
                    Utils.focusV(hostCls.cateV, false);
                    Utils.focusV(hostCls.favV, false);
                    if (hostCls.list3RootV.isShown()) {
                        hostCls.list3NoFocus = true;
                        hostCls.list3RootV.setDescendantFocusability(descendantFocusabilityNoFocus);
                        Utils.noFocus(hostCls.list3RootV);
                        Utils.focusV(hostCls.list2RootV, false);
                        final int selPos = hostCls.list2Adapter.getItemPosInList(hostCls.mainActivityP.selLiveCKind);
                        hostCls.list2RootV.post(new Runnable() {
                            @Override
                            public void run() {
                                hostCls.list2RootV.setDescendantFocusability(descendantFocusabilityFocus);
                                hostCls.list2Adapter.setSelection(hostCls.focusBorder, selPos);
                                hostCls.list2NoFocus = false;
                            }
                        });
                    } else {
                        if (hostCls.task != null) {
                            hostCls.handler.removeCallbacks(hostCls.task);
                        }
                        hostCls.list2RootV.setDescendantFocusability(descendantFocusabilityNoFocus);
                        hostCls.list2NoFocus = true;
                        Utils.noFocus(hostCls.list2RootV);
                        Utils.focusV(hostCls.list1RootV, true);
                        if (hostCls.mainActivityP.isLiveFavK()) {
                            hostCls.favV.setShowState(true);
                            hostCls.favV.setShowState2(false);
                            hostCls.favV.setTextColor(Color.WHITE);
                            hostCls.list1RootV.setDescendantFocusability(descendantFocusabilityFocus);
                            Utils.focusV(hostCls.favV, true);
                        } else {
                            hostCls.cateV.setShowState(true);
                            hostCls.cateV.setShowState2(false);
                            hostCls.cateV.setTextColor(Color.WHITE);
                            final int selPos = hostCls.list1Adapter.getItemPosInList(hostCls.mainActivityP.selLiveKind);
                            hostCls.task = new Runnable() {
                                @Override
                                public void run() {
                                    Utils.focusV(hostCls.list1RV, true);
                                    hostCls.list1RootV.setDescendantFocusability(descendantFocusabilityFocus);
                                    hostCls.list1Adapter.setSelection(hostCls.focusBorder, selPos);
                                    hostCls.list1NoFocus = false;
                                }
                            };
                            hostCls.handler.postDelayed(hostCls.task, EXVAL.LIST1_ANIM_DURATION);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_MENU:
                    hostCls.handleChalTips(view);
                    break;
            }
        }

        @Override
        public void onItemClick(View view, int position) {
            if (hostCls == null || hostCls.mainActivityP == null) {
                return;
            }
            Utils.sendMsg(hostCls.mainActivityP.mHandler, MSG.HIDE_LIST, EXVAL.SHOW_LIST_TIMEOUT);
            MLog.d(TAG, String.format("onItemClick %s", hostCls.mainActivityP.selLiveChal));
            hostCls.list3Adapter.markPlay(hostCls.mainActivityP.curLiveChal, hostCls.mainActivityP.selLiveChal);
            hostCls.mainActivityP.setCacheKindVal(hostCls.mainActivityP.tmpCacheLiveKind);
            hostCls.mainActivityP.curLiveKind = hostCls.mainActivityP.selLiveKind;
            hostCls.mainActivityP.curLiveCKind = hostCls.mainActivityP.selLiveCKind;
            hostCls.mainActivityP.curLiveChalIndex = hostCls.mainActivityP.selLiveChal.getListPos();
            hostCls.mainActivityP.selLiveChalIndex = hostCls.mainActivityP.curLiveChalIndex % EXVAL.NUM_IN_COL_LIVE;
            hostCls.mainActivityP.curLiveKIndex = hostCls.mainActivityP.selLiveKIndex;
            hostCls.mainActivityP.curLiveCKIndex = hostCls.mainActivityP.selLiveCKIndex;
            hostCls.mainActivityP.onChalSel(hostCls.mainActivityP.selLiveChal, true);
        }

        @Override
        public void onItemLongClick(View view, int position) {
            if (hostCls != null) {
                hostCls.handleChalInFavWrapper(true, view);
            }
        }
    }

    public void handleChalInFavWrapper(boolean needHandle, View view) {
        if (needHandle) {
            mainActivityP.selLiveChal.setIsFav(!mainActivityP.selLiveChal.getIsFav());
            View favV = ((ViewGroup) view).getChildAt(2);
            favV.setVisibility(mainActivityP.selLiveChal.getIsFav() ? View.VISIBLE : View.GONE);
            actCls.handleChalInFav(mainActivityP);
        }
    }

    public void handleChalItemFocus(View view) {
        this.focusBorder.onFocus(view, AbsFocusBorder.Options.get(this.exW, this.exH),
                false);
    }

    public void handleKindItemFocus(View view) {
        this.focusBorder.onFocus(view, AbsFocusBorder.Options.get(this.kindExW, this.kindExH),
                false);
    }
}
