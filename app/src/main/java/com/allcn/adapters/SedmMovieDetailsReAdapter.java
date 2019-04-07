package com.allcn.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.activities.SEDMMovieDetailsAct;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.EXVAL;
import com.allcn.utils.GlideUtils;
import com.allcn.views.FocusKeepRecyclerView;
import com.datas.MovieObj;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.List;

public class SedmMovieDetailsReAdapter extends RecyclerView.Adapter<SedmMovieDetailsReAdapter.MVHolder> {

    private static final String TAG = SedmMovieDetailsReAdapter.class.getSimpleName();
    private int size, curSelIndex;
    private List<MovieObj> datas;
    private MVHolder mvHolder;
    private FocusKeepRecyclerView rv;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private SEDMMovieDetailsAct activity;
    private OnRvAdapterListener onRvAdapterListener;

    public SedmMovieDetailsReAdapter(SEDMMovieDetailsAct activity) {
        this.activity = activity;
        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnClickListener = new MOnClickListener(this);
        this.mOnKeyListener = new MOnKeyListener(this);
        setHasStableIds(true);
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
        return new MVHolder(AppMain.getView(R.layout.sedm_movie_details_re_item, viewGroup));
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

    public void setDatas(List<MovieObj> datas) {
        MLog.d(TAG, "setDatas");
        clearDatas();
        this.curSelIndex = 0;
        this.datas = datas;
        this.size = this.datas == null ? 0 : this.datas.size();
        notifyDataSetChanged();
    }

    public void marketForFocusVH(MVHolder mvHolder, boolean hasFocus) {
        if (mvHolder != null) {
            mvHolder.marketForFocus(hasFocus);
            if (this.activity != null) {
                this.activity.focusBorderVisible(hasFocus);
            }
            if (hasFocus && this.activity != null) {
                this.execFocusVEffect(mvHolder.itemView, hasFocus);
            }
        }
    }

    public void marketCacheForFocus(boolean hasFocus) {
        if (this.mvHolder == null) {
            if (hasFocus) {
                this.setSelection(this.curSelIndex, false);
            }
        } else {
            marketForFocusVH(this.mvHolder, hasFocus);
        }
        if (this.rv != null) {
            this.rv.setCanFocusOutVertical(!hasFocus);
            this.rv.setCanFocusOutHorizontal(!hasFocus);
        }
    }

    public void setSelection(int position, final boolean spanCountChanged) {
        this.marketCacheForFocus(false);
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
                    SedmMovieDetailsReAdapter.this.mvHolder = (MVHolder) SedmMovieDetailsReAdapter.this.rv.
                            findViewHolderForAdapterPosition(finalPosition);
                    marketForFocusVH(SedmMovieDetailsReAdapter.this.mvHolder, true);
                }
            });
        } else {
            marketForFocusVH(this.mvHolder, true);
        }
    }

    public void setOnRvAdapterListener(OnRvAdapterListener onRvAdapterListener) {
        this.onRvAdapterListener = onRvAdapterListener;
    }

    protected static class MVHolder extends RecyclerView.ViewHolder {

        private TextView nameV, topV;
        private View forgroundV;
        private ImageView imgV;

        public MVHolder(@NonNull View itemView) {
            super(itemView);
            this.nameV = itemView.findViewById(R.id.sedm_details_re_movie_item_name);
            this.topV = itemView.findViewById(R.id.sedm_details_re_movie_item_text);
            this.forgroundV = itemView.findViewById(R.id.sedm_details_re_movie_forground_v);
            this.imgV = itemView.findViewById(R.id.sedm_details_re_movie_item_img);
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
            if (this.forgroundV != null) {
                this.forgroundV.setBackgroundResource(hasFocus ? R.drawable.sedm_movie_f :
                        R.drawable.sedm_movie_nof);
            }
            if (this.nameV != null) {
                this.nameV.setSelected(hasFocus);
                this.nameV.setTextColor(hasFocus ? Color.WHITE :
                        AppMain.res().getColor(R.color.sedm_kind_movie_item_text_color));
            }
        }

        public void initAdapterListener(SedmMovieDetailsReAdapter hostCls) {
            if (this.itemView != null) {
                this.itemView.setOnClickListener(hostCls.mOnClickListener);
                this.itemView.setOnFocusChangeListener(hostCls.mOnFocusChangeListener);
                this.itemView.setOnKeyListener(hostCls.mOnKeyListener);
            }
        }
    }

    private void execFocusVEffect(View v, boolean hasFocus) {
        if (v != null && this.activity != null) {
            float scaleX = hasFocus ? 1.12f : 1f;
            float scaleY = hasFocus ? 1.12f : 1f;
            this.activity.focusBorderForV(v, scaleX, scaleY);
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private SedmMovieDetailsReAdapter hostCls;

        public MOnFocusChangeListener(SedmMovieDetailsReAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            MLog.d(TAG, String.format("onFocusChange v=%s hasFocus=%b", v, hasFocus));
            if (this.hostCls == null) {
                return;
            }
            if (hasFocus) {
                this.hostCls.setSel(v);
                this.hostCls.execFocusVEffect(v, hasFocus);
            }
            if (this.hostCls.mvHolder != null) {
                this.hostCls.mvHolder.marketForFocus(hasFocus);
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private SedmMovieDetailsReAdapter hostCls;

        public MOnClickListener(SedmMovieDetailsReAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {
            if (this.hostCls != null && this.hostCls.activity != null) {
                this.hostCls.setSel(v);
                Intent intent = new Intent(this.hostCls.activity, SEDMMovieDetailsAct.class);
                intent.putExtra(EXVAL.MOVIE_OBJ,
                        this.hostCls.getDataForItem(this.hostCls.curSelIndex));
                this.hostCls.activity.startActivity(intent);
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private SedmMovieDetailsReAdapter hostCls;

        public MOnKeyListener(SedmMovieDetailsReAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && this.hostCls != null) {
                this.hostCls.setSel(v);
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP: {
                        this.hostCls.marketCacheForFocus(false);
                        break;
                    }
                }
                if (this.hostCls.onRvAdapterListener != null) {
                    this.hostCls.onRvAdapterListener.onItemKey(v, this.hostCls.curSelIndex,
                            event, keyCode);
                }
            }
            return false;
        }
    }

    public MovieObj getDataForItem(int position) {
        return position >= 0 && position < size ? datas.get(position) : null;
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
