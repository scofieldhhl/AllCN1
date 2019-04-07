package com.allcn.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.activities.TopicsMainAct;
import com.allcn.utils.AppMain;
import com.allcn.utils.GlideUtils;
import com.allcn.views.RecyclerCoverFlow;
import com.datas.CKindObj;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.List;

public class
TopicsTopAdapter extends RecyclerView.Adapter<TopicsTopAdapter.MVHolder> {

    private static final String TAG = TopicsTopAdapter.class.getSimpleName();
    private int size, curSelIndex, exWidth, exHeight, initPos, freeWidth, freeHeight;
    private RecyclerCoverFlow rv;
    private List<CKindObj> datas;
    private TopicsMainAct activity;
    private MVHolder mvHolder;

    public TopicsTopAdapter(TopicsMainAct activity) {
        this.activity = activity;
        this.exWidth = (int) AppMain.res().getDimension(R.dimen.topics_top_item_ex_w);
        this.exHeight = (int) AppMain.res().getDimension(R.dimen.topics_top_item_ex_h);
        this.freeWidth = (int) AppMain.res().getDimension(R.dimen.free_width);
        this.freeHeight = (int) AppMain.res().getDimension(R.dimen.free_height);
        setHasStableIds(true);
        this.reset();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        if (this.rv == null) {
            this.rv = (RecyclerCoverFlow) recyclerView;
        }
    }

    @NonNull
    @Override
    public MVHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MVHolder(AppMain.getView(R.layout.topics_top_item, viewGroup));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder mvHolder, int i) {
        CKindObj cKindObj = this.datas.get(i);
        mvHolder.setBg(this.activity, cKindObj.getImgUrl());
        mvHolder.setName(cKindObj.getName());
    }

    @Override
    public int getItemCount() {
        return size;
    }

    private void reset() {
        this.size = 0;
        this.curSelIndex = -1;
    }

    public void clearDatas() {
        if (this.datas != null) {
            this.datas.clear();
            this.datas = null;
        }
    }

    public void setDatas(List<CKindObj> datas, int num) {
        clearDatas();
        this.datas = datas;
        this.size = num;
        this.initPos = this.size > 2 ? this.size / 2 : 0;
        this.curSelIndex = this.initPos;
        notifyDataSetChanged();
    }

    public void release() {
        clearDatas();
        this.activity = null;
    }

    public static class MVHolder extends RecyclerView.ViewHolder {

        private TextView nameV;
        private ImageView imgV;
        private TextView topV;

        public MVHolder(@NonNull View itemView) {
            super(itemView);
            this.nameV = itemView.findViewById(R.id.topics_top_item_text_v);
            this.imgV = itemView.findViewById(R.id.topics_top_item_img_v);
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
                GlideUtils.Ins().loadTopTopicsUrlImg(activity, imgUrl, this.imgV);
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
    }

    public void setCurSelIndex(int curSelIndex) {
        this.curSelIndex = curSelIndex;
    }

    public int getCurSelIndex() {
        return this.curSelIndex;
    }

    public int getInitPos() {
        return this.initPos;
    }

    public void execFocusVEffect(View v) {
        if (v != null && this.activity != null) {
            //activity.focusBorderForV(v, exWidth, exHeight,
            //        freeWidth, freeHeight);

            activity.focusBorderForV(v,exWidth, exHeight, freeWidth, freeHeight);
        }
    }

    public void cacheMVHolder(MVHolder mvHolder) {
        this.mvHolder = mvHolder;
    }

    public MVHolder getCacheMVHolder() {
        return this.mvHolder;
    }

    public CKindObj getItemData(int position) {
        return position >= 0 && position < this.size ? this.datas.get(position) : null;
    }
}