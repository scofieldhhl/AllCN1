package com.allcn.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.activities.DetailsAct;
import com.allcn.activities.HotReDetailsAct;
import com.allcn.activities.TopicsDetailsAct;
import com.allcn.utils.AppMain;
import com.allcn.utils.DataCenter;
import com.allcn.utils.EXVAL;
import com.allcn.utils.GlideUtils;
import com.allcn.views.FocusKeepRecyclerView;
import com.datas.CKindObj;
import com.datas.MovieObj;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.List;

public class HotReDetailsMovieAdapter extends RecyclerView.Adapter<HotReDetailsMovieAdapter.MVHolder> {

    private static final String TAG = HotReDetailsMovieAdapter.class.getSimpleName();
    private int size;
    private FocusKeepRecyclerView rv;
    private List<? extends Object> datas;
    private MVHolder mvHolder;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private HotReDetailsAct activity;
    private int curSelIndex, movieItemExWidth, movieItemExHeight;

    public HotReDetailsMovieAdapter(HotReDetailsAct activity) {
        this.activity = activity;
        this.movieItemExWidth = (int) AppMain.res().getDimension(R.dimen.hot_re_details_movie_item_ex_w);
        this.movieItemExHeight = (int) AppMain.res().getDimension(R.dimen.hot_re_details_movie_item_ex_h);
        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnClickListener = new MOnClickListener(this);
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
        return new MVHolder(AppMain.getView(R.layout.hot_re_details_movie_item, viewGroup));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder mvHolder, int i) {
        Object object = this.datas.get(i);
        if (object instanceof CKindObj) {
            CKindObj cKindObj = (CKindObj) object;
            mvHolder.setName(cKindObj.getName());
            mvHolder.setTop("");
            mvHolder.setBg(this.activity, cKindObj.getImgUrl());
        } else if (object instanceof MovieObj) {
            MovieObj movieObj = (MovieObj) object;
            mvHolder.setName(movieObj.getName());
            mvHolder.setTop(movieObj.getJs());
            mvHolder.setBg(this.activity, movieObj.getImgUrl());
        }
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
    }

    public void setDatas(List<? extends Object> datas, int movieNum) {
        clearDatas();
        this.datas = datas;
        this.size = movieNum;
        notifyDataSetChanged();
    }

    public void release() {
        this.rv = null;
        this.activity = null;
        if (this.mOnClickListener != null) {
            this.mOnClickListener.relase();
            this.mOnClickListener = null;
        }
        if (this.mOnFocusChangeListener != null) {
            this.mOnFocusChangeListener.relase();
            this.mOnFocusChangeListener = null;
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
                    HotReDetailsMovieAdapter.this.mvHolder = (MVHolder) HotReDetailsMovieAdapter.
                            this.rv.findViewHolderForAdapterPosition(finalPosition);
                    HotReDetailsMovieAdapter.this.marketForFocusVH(
                            HotReDetailsMovieAdapter.this.mvHolder, true);
                }
            });
        } else {
            HotReDetailsMovieAdapter.this.marketForFocusVH(this.mvHolder, true);
        }
    }

    private void marketForFocusVH(MVHolder mvHolder, boolean hasFocus) {
        if (mvHolder != null) {
            mvHolder.marketForFocus(hasFocus);
        }
    }

    protected static class MVHolder extends RecyclerView.ViewHolder {

        private TextView nameV, topV;
        private ImageView imgV;

        public MVHolder(@NonNull View itemView) {
            super(itemView);
            this.nameV = itemView.findViewById(R.id.hot_re_details_item_name_v);
            this.topV = itemView.findViewById(R.id.hot_re_details_item_text_v);
            this.imgV = itemView.findViewById(R.id.hot_re_details_item_img_v);
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

        public void initAdapterListener(HotReDetailsMovieAdapter hostCls) {
            if (this.itemView != null) {
                this.itemView.setOnClickListener(hostCls.mOnClickListener);
                this.itemView.setOnFocusChangeListener(hostCls.mOnFocusChangeListener);
            }
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private HotReDetailsMovieAdapter hostCls;

        public MOnFocusChangeListener(HotReDetailsMovieAdapter hostCls) {
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
                this.hostCls.execFocusVEffect(v);
                Object object = this.hostCls.getDataForItem(this.hostCls.curSelIndex);
                if (object instanceof MovieObj) {
                    DataCenter.Ins().scanMovieDetails(this.hostCls.activity, (MovieObj) object);
                } else if (object instanceof CKindObj) {
                    this.hostCls.activity.loadCkindName((CKindObj) object);
                }
            }
            if (this.hostCls.mvHolder != null) {
                this.hostCls.mvHolder.scrollContent(hasFocus);
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private HotReDetailsMovieAdapter hostCls;

        public MOnClickListener(HotReDetailsMovieAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {
            if (this.hostCls != null && this.hostCls.activity != null) {
                this.hostCls.setSel(v);
                Object object = this.hostCls.getDataForItem(this.hostCls.curSelIndex);
                if (object instanceof MovieObj) {
                    Intent intent = new Intent(this.hostCls.activity, DetailsAct.class);
                    intent.putExtra(EXVAL.MOVIE_OBJ, (Parcelable) object);
                    this.hostCls.activity.startActivityForResult(intent, EXVAL.DETAILS_REQ_CODE);
                } else if (object instanceof CKindObj) {
                    Intent intent = new Intent(this.hostCls.activity, TopicsDetailsAct.class);
                    intent.putExtra(EXVAL.CKIND_OBJ, (Parcelable) object);
                    this.hostCls.activity.startActivity(intent);
                }
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

    private void setSel(View view) {
        MLog.d(TAG, "setSel " + view);
        if (view == null) {
            this.curSelIndex = -1;
            this.mvHolder = null;
        } else {
            this.curSelIndex = this.rv.getChildLayoutPosition(view);
            this.mvHolder = (MVHolder) this.rv.findViewHolderForAdapterPosition(this.curSelIndex);
        }
    }

    public int getCurSelIndex() {
        return this.curSelIndex;
    }

    public Object getDataForItem(int position) {
        return position >= 0 && position < this.size ? this.datas.get(position) : null;
    }
}
