package com.allcn.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.activities.TopicsDetailsAct;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.GlideUtils;
import com.allcn.views.FocusKeepRecyclerView;
import com.datas.MovieObj;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.List;

public class TopicsDetailsMovieAdapter extends RecyclerView.Adapter<TopicsDetailsMovieAdapter.MVHolder> {

    private static final String TAG = TopicsDetailsMovieAdapter.class.getSimpleName();

    private List<MovieObj> movieObjs;
    private int size, exWidth, exHeight, curSelIndex;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private FocusKeepRecyclerView rv;
    private TopicsDetailsAct act;
    private MVHolder mvHolder;
    private OnRvAdapterListener onRvAdapterListener;

    public TopicsDetailsMovieAdapter(TopicsDetailsAct act) {
        this.act = act;
        this.mOnFocusChangeListener = new MOnFocusChangeListener(this);
        this.mOnClickListener = new MOnClickListener(this);
        this.mOnKeyListener = new MOnKeyListener(this);
        this.exWidth = (int) AppMain.res().getDimension(R.dimen.topics_details_movie_item_ex_w);
        this.exHeight = (int) AppMain.res().getDimension(R.dimen.topics_details_movie_item_ex_h);
    }

    public void release() {
        this.clearDatas();
        this.act = null;
        this.rv = null;
        this.mvHolder = null;
        this.onRvAdapterListener = null;
        if (this.mOnFocusChangeListener != null) {
            this.mOnFocusChangeListener.release();
            this.mOnFocusChangeListener = null;
        }
        if (this.mOnClickListener != null) {
            this.mOnClickListener.relase();
            this.mOnClickListener = null;
        }
        if (this.mOnKeyListener != null) {
            this.mOnKeyListener.relase();
            this.mOnKeyListener = null;
        }
    }

    public void setOnRvListener(OnRvAdapterListener onRvAdapterListener) {
        this.onRvAdapterListener = onRvAdapterListener;
    }

    private void clearDatas() {
        if (this.movieObjs != null) {
            this.movieObjs.clear();
            this.movieObjs = null;
        }
    }

    public void setDatas(List<MovieObj> datas, int movieNum) {
        clearDatas();
        this.movieObjs = datas;
        this.size = movieNum;
        notifyDataSetChanged();
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
        return new MVHolder(AppMain.getView(R.layout.topics_details_movie_item, viewGroup));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder mvHolder, int i) {
        MovieObj movieObj = movieObjs.get(i);
        mvHolder.setBg(this.act, movieObj.getImgUrl());
        mvHolder.setTop(movieObj.getJs());
        mvHolder.setName(movieObj.getName());
        mvHolder.initAdapterListener(this);
    }

    @Override
    public int getItemCount() {
        return this.size;
    }

    public static class MVHolder extends RecyclerView.ViewHolder {

        private TextView topV, nameV;
        private ImageView imgV;

        public MVHolder(@NonNull View itemView) {
            super(itemView);
            this.topV = itemView.findViewById(R.id.topics_details_movie_item_text);
            this.nameV = itemView.findViewById(R.id.topics_details_movie_item_name);
            this.imgV = itemView.findViewById(R.id.topics_details_movie_item_img);
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

        public void setName(String name) {
            if (this.nameV != null) {
                this.nameV.setText(name);
            }
        }

        public void setBg(TopicsDetailsAct act, String bg) {
            if (this.imgV != null) {
                GlideUtils.Ins().loadUrlImg(act, bg, imgV);
            }
        }

        public void scrollContent(boolean scroll) {
            if (this.nameV != null) {
                this.nameV.setSelected(scroll);
            }
        }

        public void initAdapterListener(TopicsDetailsMovieAdapter hostCls) {
            if (this.itemView != null) {
                this.itemView.setOnClickListener(hostCls.mOnClickListener);
                this.itemView.setOnKeyListener(hostCls.mOnKeyListener);
                this.itemView.setOnFocusChangeListener(hostCls.mOnFocusChangeListener);
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private TopicsDetailsMovieAdapter hostCls;

        public MOnClickListener(TopicsDetailsMovieAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public void onClick(View v) {
            if (this.hostCls != null) {
                this.hostCls.setSel(v);
                if (this.hostCls.onRvAdapterListener != null) {
                    this.hostCls.onRvAdapterListener.onItemClick(v, this.hostCls.curSelIndex);
                }
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private TopicsDetailsMovieAdapter hostCls;

        public MOnKeyListener(TopicsDetailsMovieAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            this.hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            MLog.d(TAG, String.format("onKey v=%s event=%s", v, event));
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (this.hostCls != null) {
                    this.hostCls.setSel(v);
                    if (this.hostCls.onRvAdapterListener != null) {
                        this.hostCls.onRvAdapterListener.onItemKey(v, this.hostCls.curSelIndex,
                                event, keyCode);
                    }
                }
            }
            return false;
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private TopicsDetailsMovieAdapter hostCls;

        public MOnFocusChangeListener(TopicsDetailsMovieAdapter hostCls) {
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

            if (hasFocus) {
                this.hostCls.setSel(v);
                if (this.hostCls.act != null) {
                    this.hostCls.act.focusBorderForV(v, 1.1f, 1.1f,
                            this.hostCls.exWidth, this.hostCls.exHeight);
                }
                if (this.hostCls.onRvAdapterListener != null) {
                    this.hostCls.onRvAdapterListener.onItemSelected(v, this.hostCls.curSelIndex);
                }
            }

            if (this.hostCls.mvHolder != null) {
                this.hostCls.mvHolder.scrollContent(hasFocus);
            }
        }
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

    public void setSelection(final int position) {
        if (this.size == 0) {
            return;
        }
        this.mvHolder = (MVHolder) this.rv.findViewHolderForAdapterPosition(position);
        if (this.mvHolder == null) {
            this.rv.scrollToPosition(position);
            this.rv.post(new Runnable() {
                @Override
                public void run() {
                    TopicsDetailsMovieAdapter.this.mvHolder = (MVHolder)
                            TopicsDetailsMovieAdapter.this.rv.findViewHolderForAdapterPosition(position);
                    if (TopicsDetailsMovieAdapter.this.mvHolder != null) {
                        Utils.focusV(TopicsDetailsMovieAdapter.this.mvHolder.itemView, true);
                    }
                }
            });
        } else {
            Utils.focusV(TopicsDetailsMovieAdapter.this.mvHolder.itemView, true);
        }
    }

    public MovieObj getItemData(int position) {
        return position >= 0 && position < this.size ? this.movieObjs.get(position) : null;
    }

    public int getCurSelIndex() {
        return this.curSelIndex;
    }

    public void marketCacheFocus(boolean hasFocus) {
        if (this.mvHolder == null) {
            return;
        }
        if (hasFocus) {
            if (this.mvHolder != null) {
                Utils.focusV(this.mvHolder.itemView, true);
            }
        } else {
            if (this.act != null) {
                this.act.focusBorderForV(this.mvHolder.itemView);
            }
        }
        if (this.act != null) {
            this.act.focusBorderVisible(hasFocus);
        }
    }
}
