package com.allcn.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.activities.DetailsAct;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.EXVAL;
import com.allcn.utils.GlideUtils;
import com.allcn.views.FocusKeepRecyclerView;
import com.datas.MovieObj;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.List;

public class DetailsReAdapter extends RecyclerView.Adapter<DetailsReAdapter.MVHolder> {

    private static String TAG = DetailsReAdapter.class.getSimpleName();
    private FocusKeepRecyclerView rv;
    private int size, curSelIndex, movieItemExWidth, movieItemExHeight;
    private MVHolder mvHolder;
    private List<MovieObj> datas;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private DetailsAct activity;
    private OnRvAdapterListener onRvAdapterListener;

    public DetailsReAdapter(DetailsAct activity) {
        this.activity = activity;
        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnClickListener = new MOnClickListener(this);
        this.mOnKeyListener = new MOnKeyListener(this);
        this.movieItemExWidth = (int) AppMain.res().getDimension(R.dimen.re_movie_item_ex_w);
        this.movieItemExHeight = (int) AppMain.res().getDimension(R.dimen.re_movie_item_ex_h);
        setHasStableIds(true);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.rv = (FocusKeepRecyclerView) recyclerView;
    }

    @NonNull
    @Override
    public MVHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MVHolder(AppMain.getView(R.layout.details_re_item, viewGroup));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder mvHolder, int i) {
        MovieObj movieObj = datas.get(i);
        mvHolder.setName(movieObj.getName());
        mvHolder.setBg(this.activity, movieObj.getImgUrl());
        mvHolder.setTop(movieObj.getJs());
        mvHolder.initAdapterListener(this);
    }

    @Override
    public int getItemCount() {
        return size;
    }

    private void clearDatas() {
        if (this.datas != null) {
            this.datas.clear();
            this.datas = null;
        }
        this.size = 0;
    }

    public void setDatas(List<MovieObj> datas) {
        clearDatas();
        this.datas = datas;
        this.size = this.datas == null ? 0 : this.datas.size();
        notifyDataSetChanged();
    }

    protected static class MVHolder extends RecyclerView.ViewHolder {

        private TextView nameV;
        private ImageView imgV;
        private TextView topV;

        public MVHolder(@NonNull View itemView) {
            super(itemView);
            this.nameV = itemView.findViewById(R.id.details_re_movie_item_name);
            this.imgV = itemView.findViewById(R.id.details_re_movie_item_img);
            this.topV = itemView.findViewById(R.id.details_re_movie_item_text);
        }

        public void setName(String name) {
            if (this.nameV != null) {
                this.nameV.setText(name);
            }
        }

        public void scrollContent(boolean scroll) {
            if (this.nameV != null) {
                this.nameV.setSelected(scroll);
            }
        }

        public void setBg(Activity activity, String imgUrl) {
            if (this.imgV != null) {
                GlideUtils.Ins().loadUrlImg(activity, imgUrl, this.imgV);
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
                if (hasFocus) {
                    Utils.focusV(this.itemView, true);
                }
            }
        }

        public void initAdapterListener(DetailsReAdapter hostCls) {
            if (this.itemView != null) {
                this.itemView.setOnClickListener(hostCls.mOnClickListener);
                this.itemView.setOnFocusChangeListener(hostCls.mOnFocusChangeListener);
                this.itemView.setOnKeyListener(hostCls.mOnKeyListener);
            }
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private DetailsReAdapter hostCls;

        public MOnFocusChangeListener(DetailsReAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            MLog.d(TAG, String.format("onFocusChange v=%s hasFocus=%b", v, hasFocus));
            if (this.hostCls == null) {
                return;
            }
            if (hasFocus) {
                this.hostCls.setSel(v);
                this.hostCls.execFocusVEffect(v);
                if (this.hostCls.onRvAdapterListener != null) {
                    this.hostCls.onRvAdapterListener.onItemSelected(v, this.hostCls.curSelIndex);
                }
            }
            if (this.hostCls.mvHolder != null) {
                this.hostCls.mvHolder.scrollContent(hasFocus);
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private DetailsReAdapter hostCls;

        public MOnClickListener(DetailsReAdapter hostCls) {
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

        private DetailsReAdapter hostCls;

        public MOnKeyListener(DetailsReAdapter hostCls) {
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
        return position >= 0 && position < this.size ? this.datas.get(position) : null;
    }

    public void setSel(View view) {
//        MLog.d(TAG, "setSel " + view);
        if (view == null) {
            this.curSelIndex = -1;
            this.mvHolder = null;
        } else {
            this.curSelIndex = this.rv.getChildLayoutPosition(view);
            this.mvHolder = (MVHolder) this.rv.findViewHolderForAdapterPosition(this.curSelIndex);
        }
    }

    private void execFocusVEffect(View v) {
        if (v != null && this.activity != null) {
            this.activity.focusBorderForV(v, EXVAL.KIND_MOVIE_ITEM_SCALE,
                    EXVAL.KIND_MOVIE_ITEM_SCALE, this.movieItemExWidth,
                    this.movieItemExHeight);
        }
    }


    public void marketForFocusVH(MVHolder mvHolder, boolean hasFocus) {
        if (mvHolder != null) {
            mvHolder.marketForFocus(hasFocus);
            if (hasFocus && this.activity != null) {
                this.execFocusVEffect(mvHolder.itemView);
            }
        }
    }

    public void marketCacheForFocus(boolean hasFocus) {
        if (this.rv != null) {
            this.rv.setCanFocusOutVertical(!hasFocus);
            this.rv.setCanFocusOutHorizontal(!hasFocus);
        }
        if (this.activity != null) {
            this.activity.focusBorderVisible(hasFocus);
        }
        if (this.mvHolder == null) {
            if (hasFocus) {
                this.setSelection(this.curSelIndex);
            }
        } else {
            marketForFocusVH(this.mvHolder, hasFocus);
        }
    }

    public void setSelection(int position) {
        this.marketForFocusVH(this.mvHolder, false);
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
                    DetailsReAdapter.this.mvHolder = (MVHolder) DetailsReAdapter.this.rv.
                            findViewHolderForAdapterPosition(finalPosition);
                    marketForFocusVH(DetailsReAdapter.this.mvHolder, true);
                }
            });
        } else {
            marketForFocusVH(this.mvHolder, true);
        }
    }

    public void setOnRvListener(OnRvAdapterListener onRvAdapterListener) {
        this.onRvAdapterListener = onRvAdapterListener;
    }

    public int getCurSelIndex() {
        return this.curSelIndex;
    }

    public void release() {
        clearDatas();
        this.activity = null;
        this.onRvAdapterListener = null;
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
}
