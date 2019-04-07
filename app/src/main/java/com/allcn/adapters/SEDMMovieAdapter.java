package com.allcn.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.activities.SEDMKindListAct;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.utils.GlideUtils;
import com.allcn.views.FocusKeepRecyclerView;
import com.allcn.views.decorations.SEDMMovieItemDecoration;
import com.datas.MovieObj;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SEDMMovieAdapter extends RecyclerView.Adapter<SEDMMovieAdapter.MVHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();
    private int size, curPageIndex, curTotalPageNum, spanCount, curSelIndex, movieItemExWidth,
            movieItemExHeight;
    private List<Object> datas;
    private MVHolder mvHolder;
    private FocusKeepRecyclerView rv;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private SEDMKindListAct activity;
    private GridLayoutManager gridLayoutManager;
    private SEDMMovieItemDecoration movieItemDecoration;
    private int refreshDataSelPos;
    private boolean forceFocus;
    private OnRvAdapterListener onRvAdapterListener;
    private String cid;

    public SEDMMovieAdapter(SEDMKindListAct activity, GridLayoutManager gridLayoutManager,
                            SEDMMovieItemDecoration movieItemDecoration) {
        this.activity = activity;
        this.movieItemDecoration = movieItemDecoration;
        this.gridLayoutManager = gridLayoutManager;
        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnClickListener = new MOnClickListener(this);
        this.mOnKeyListener = new MOnKeyListener(this);
        this.movieItemExWidth = (int) AppMain.res().getDimension(R.dimen.movie_item_ex_w);
        this.movieItemExHeight = (int) AppMain.res().getDimension(R.dimen.movie_item_ex_h);
        setHasStableIds(true);
        this.reset();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (this.rv == null) {
            this.rv = (FocusKeepRecyclerView) recyclerView;
        }
    }

    @NonNull
    @Override
    public MVHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MVHolder(AppMain.getView(R.layout.sedm_movie_item, viewGroup));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder mvHolder, int i) {
        MovieObj movieObj = (MovieObj) datas.get(i);
        mvHolder.setName(movieObj.getName());
        mvHolder.setBg(this.activity, movieObj.getImgUrl());
        mvHolder.setTop(movieObj.getJs());
        mvHolder.initAdapterListener(this);
    }

    @Override
    public int getItemCount() {
        return size;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void clearDatas() {
        if (this.datas != null) {
            this.datas.clear();
            this.datas = null;
        }
    }

    public void reset() {
        this.cid = "";
        this.curTotalPageNum = 0;
        this.curPageIndex = -1;
        this.spanCount = 0;
    }

    public void setDatas(List<Object> datas, int movieNum, int movieTotalPageNum,
                         int curPageIndex) {
        MLog.d(TAG, "setDatas");
        int cacheSize = this.size;
        clearDatas();
        this.curSelIndex = 0;
        this.curTotalPageNum = movieTotalPageNum;
        this.curPageIndex = curPageIndex;
        this.datas = datas;
        this.size = movieNum;
        if (this.activity != null) {
            this.activity.updatePageInfo(curTotalPageNum == 0 ? 0 : curPageIndex + 1,
                    curTotalPageNum);
        }
        notifyDataSetChanged();
        if (this.rv != null && this.rv.hasFocus() && (this.forceFocus || (cacheSize != this.size))) {
            this.forceFocus = false;
            this.rv.post(new Runnable() {
                @Override
                public void run() {
                    SEDMMovieAdapter.this.setSelection(
                            SEDMMovieAdapter.this.refreshDataSelPos, forceFocus);
                }
            });
        }
    }

    public void marketForFocusVH(MVHolder mvHolder, boolean hasFocus, boolean spanCountChanged) {
        if (mvHolder != null) {
            mvHolder.marketForFocus(hasFocus);
            if (hasFocus/* && spanCountChanged*/ && this.activity != null) {
                this.execFocusVEffect(mvHolder.itemView);
            }
        }
    }

    public void marketCacheForFocus(boolean hasFocus) {
        if (this.rv != null) {
            this.rv.setCanFocusOutVertical(!hasFocus);
            this.rv.setCanFocusOutHorizontal(!hasFocus);
        }
        if (this.mvHolder == null) {
            if (hasFocus) {
                this.setSelection(this.curSelIndex, false);
            }
        } else {
            this.marketForFocusVH(this.mvHolder, hasFocus, false);
        }
    }

    public void setSelection(int position, final boolean spanCountChanged) {
        this.marketForFocusVH(this.mvHolder, false, false);
        if (position >= getItemCount()) {
            position = 0;
        }
        if (this.rv == null) {
            return;
        }
        this.mvHolder = (MVHolder) this.rv.findViewHolderForAdapterPosition(position);
        if (this.mvHolder == null) {
            this.rv.scrollToPosition(position);
            final int finalPosition = position;
            this.rv.post(new Runnable() {
                @Override
                public void run() {
                    SEDMMovieAdapter.this.mvHolder = (MVHolder) SEDMMovieAdapter.this.rv.
                            findViewHolderForAdapterPosition(finalPosition);
                    marketForFocusVH(SEDMMovieAdapter.this.mvHolder, true, spanCountChanged);
                }
            });
        } else {
            marketForFocusVH(this.mvHolder, true, spanCountChanged);
        }
    }

    public int getSpanCount() {
        return this.spanCount;
    }

    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
    }

    public RecyclerView getRelateRv() {
        return this.rv;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    protected static class MVHolder extends RecyclerView.ViewHolder {

        private TextView nameV;
        private ImageView imgV, forgroundV;
        private TextView topV;

        public MVHolder(@NonNull View itemView) {
            super(itemView);
            this.nameV = itemView.findViewById(R.id.sedm_movie_item_name);
            this.imgV = itemView.findViewById(R.id.sedm_movie_item_img);
            this.forgroundV = itemView.findViewById(R.id.sedm_movie_forground_v);
            this.topV = itemView.findViewById(R.id.sedm_movie_item_text);
        }

        public void setName(String name) {
            if (this.nameV != null) {
                this.nameV.setText(name);
            }
        }

        public void setBg(Activity activity, String imgUrl) {
            if (this.imgV != null) {
                GlideUtils.Ins().loadSedmUrlImg(activity, imgUrl, this.imgV);
            }
        }

        public void handleFocus(boolean hasFocus) {
            if (this.forgroundV != null) {
                this.forgroundV.setBackgroundResource(hasFocus ? R.drawable.sedm_movie_f :
                        R.drawable.sedm_movie_nof);
            }
            if (this.nameV != null) {
                this.nameV.setTextColor(hasFocus ? Color.WHITE :
                        AppMain.res().getColor(R.color.sedm_kind_movie_item_text_color));
                this.nameV.setSelected(hasFocus);
            }
        }

        public void setTop(String updateTo) {
            if (this.topV != null) {
                if (!TextUtils.isEmpty(updateTo) && !updateTo.equals("1")) {
                    this.topV.setText(AppMain.res().getString(R.string.update_to_ji, updateTo));
                    this.topV.setVisibility(View.VISIBLE);
                } else {
                    this.topV.setVisibility(View.GONE);
                }
            }
        }

        public void marketForFocus(boolean hasFocus) {
            MLog.d(TAG, String.format("marketForFocus hasFocus=%b", hasFocus));
            if (this.itemView != null) {
                this.itemView.setSelected(hasFocus);
                handleFocus(hasFocus);
                if (hasFocus) {
                    Utils.focusV(this.itemView, true);
                }
            }
        }

        public void initAdapterListener(SEDMMovieAdapter hostCls) {
            if (this.itemView != null) {
                this.itemView.setOnClickListener(hostCls.mOnClickListener);
                this.itemView.setOnFocusChangeListener(hostCls.mOnFocusChangeListener);
                this.itemView.setOnKeyListener(hostCls.mOnKeyListener);
            }
        }
    }

    private void execFocusVEffect(View v) {
        if (v != null && this.activity != null) {
            this.activity.focusBorderForV(v, EXVAL.KIND_MOVIE_ITEM_SCALE,
                    EXVAL.KIND_MOVIE_ITEM_SCALE, this.movieItemExWidth,
                    this.movieItemExHeight);
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private SEDMMovieAdapter hostCls;

        public MOnFocusChangeListener(SEDMMovieAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            MLog.d(TAG, String.format("onFocusChange v=%s hasFocus=%b", v, hasFocus));
            if (hasFocus) {
                if (this.hostCls != null) {
                    this.hostCls.setSel(v);
                    this.hostCls.execFocusVEffect(v);
                    if (this.hostCls.onRvAdapterListener != null) {
                        this.hostCls.onRvAdapterListener.onItemSelected(v, this.hostCls.curSelIndex);
                    }
                }
            }
            if (this.hostCls.mvHolder != null) {
                this.hostCls.mvHolder.handleFocus(hasFocus);
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private SEDMMovieAdapter hostCls;

        public MOnClickListener(SEDMMovieAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {
            if (this.hostCls != null && this.hostCls.activity != null) {
                this.hostCls.setSel(v);
                if (this.hostCls.onRvAdapterListener != null) {
                    this.hostCls.onRvAdapterListener.onItemClick(v, this.hostCls.curSelIndex);
                }
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private SEDMMovieAdapter hostCls;

        public MOnKeyListener(SEDMMovieAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (this.hostCls != null) {
                    this.hostCls.setSel(v);
                    if (this.hostCls.onRvAdapterListener != null) {
                        this.hostCls.onRvAdapterListener.onItemKey(v,
                                this.hostCls.curSelIndex, event, keyCode);
                    }
                }
            }
            return false;
        }
    }

    public MovieObj getDataForItem(int position) {
        return position >= 0 && position < this.size ? (MovieObj) this.datas.get(position) : null;
    }

    public int getCurPageIndex() {
        return curPageIndex;
    }

    public int getCurTotalPageNum() {
        return curTotalPageNum;
    }

    public void setCurPageIndex(int curPageIndex) {
        this.curPageIndex = curPageIndex;
    }

    public void setCurTotalPageNum(int curTotalPageNum) {
        this.curTotalPageNum = curTotalPageNum;
    }

    public void setSel(View view) {
        MLog.d(TAG, "setSel " + view);
        if (view == null) {
            this.curSelIndex = -1;
            this.mvHolder = null;
        } else {
            this.curSelIndex = this.rv.getChildLayoutPosition(view);
            this.mvHolder = (MVHolder) this.rv.findViewHolderForAdapterPosition(this.curSelIndex);
        }
    }

    public void refreshCurPage(final boolean forceFocus, boolean pageChange) {
        int level = EXVAL.KIND_LIST_SHOW_MID_LEVEL;
        if (this.activity != null) {
            level = this.activity.getKindListShowLevel();
        }
        int cacheSpanCount = this.spanCount;
        this.spanCount = level / 2;
        final boolean spanCountChanged = cacheSpanCount != this.spanCount;
        MLog.d(TAG, String.format("refreshCurPage[forceFocus=%b pageChange=%b spanCountChanged=%b]",
                forceFocus, pageChange, spanCountChanged));
        if (!spanCountChanged && !pageChange) {
            return;
        }
        if (this.gridLayoutManager != null) {
            this.gridLayoutManager.setSpanCount(this.spanCount);
        }
        if (this.movieItemDecoration != null) {
            this.movieItemDecoration.setSpanCount(this.spanCount);
        }

        this.refreshDataSelPos = 0;
        this.forceFocus = forceFocus;
        DataCenter.Ins().scanMovies(this.activity, this.cid, this.curPageIndex, level);
    }

    public void loadMovies(boolean isUP) {
        if (TextUtils.isEmpty(this.cid)) {
            setDatas(new ArrayList<>(0), 0,
                    0, 0);
            return;
        }
        int oldCurPageIndex = this.curPageIndex;
        if (isUP) {
            oldCurPageIndex--;
            if (oldCurPageIndex < 0) {
                oldCurPageIndex = 0;
            }
        } else {
            oldCurPageIndex++;
            if (oldCurPageIndex >= this.curTotalPageNum) {
                oldCurPageIndex = this.curTotalPageNum - 1;
            }
        }
        MLog.d(TAG, String.format("loadMovies oldCurPageIndex=%d curPageIndex=%d",
                oldCurPageIndex, curPageIndex));
        boolean pageChange = oldCurPageIndex != this.curPageIndex;
        this.curPageIndex = oldCurPageIndex;
        refreshCurPage(false, pageChange);
    }

    public void setOnRvListener(OnRvAdapterListener onRvAdapterListener) {
        this.onRvAdapterListener = onRvAdapterListener;
    }

    public void release() {
        clearDatas();
        this.activity = null;
        this.onRvAdapterListener = null;
        this.movieItemDecoration = null;
        this.gridLayoutManager = null;
        this.rv = null;
        this.mvHolder = null;
        if (this.mOnKeyListener != null) {
            this.mOnKeyListener.relase();
            this.mOnKeyListener = null;
        }
        if (this.mOnClickListener != null) {
            this.mOnClickListener.relase();
            this.mOnClickListener = null;
        }
        if (this.mOnFocusChangeListener != null) {
            this.mOnFocusChangeListener.relase();
            this.mOnFocusChangeListener = null;
        }
    }

    public void clearCacheFocus() {
        if (this.mvHolder != null) {
            this.mvHolder.handleFocus(false);
        }
    }
}
