package com.allcn.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.activities.LiveAct;
import com.allcn.interfaces.OnRvAdapterListener;
import com.allcn.utils.AppMain;
import com.allcn.utils.EXVAL;
import com.allcn.views.HorChalListV;
import com.allcn.views.focus.FocusBorder;
import com.datas.LiveChalObj;
import com.mast.lib.utils.MLog;
import com.mast.lib.utils.Utils;

import java.util.ArrayList;
import java.util.List;
public class ChalAdapter extends RecyclerView.Adapter<ChalAdapter.MVHolder> {

    private static final String TAG = ChalAdapter.class.getSimpleName();
    private LayoutInflater layoutInflater;
    private List<LiveChalObj> chals;
    private RecyclerView rv;
    private int size;
    private OnRvAdapterListener onAdapterItemListener;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private MOnLongClickListener mOnLongClickListener;
    private String curChalName;
    private LiveAct liveAct;
    private int lastIndex;
    private MVHolder mvHolder;
    private HorChalListV horChalListV;

    public ChalAdapter(LiveAct liveAct, HorChalListV horChalListV) {
        this.liveAct = liveAct;
        this.horChalListV = horChalListV;
        layoutInflater = LayoutInflater.from(AppMain.ctx());
        mOnFocusChangeListener = new MOnFocusChangeListener(this);
        mOnClickListener = new MOnClickListener(this);
        mOnKeyListener = new MOnKeyListener(this);
        mOnLongClickListener = new MOnLongClickListener(this);
    }

    public void setRecyclerView(RecyclerView rv) {
        this.rv = rv;
    }

    public void setDatas(List<LiveChalObj> chals) {
        clearDatas();
        this.chals = chals;
        size = this.chals == null ? 0 : this.chals.size();
        MLog.d(TAG, String.format("ChalAdapter setDatas %d", size));
        lastIndex = size - 1;
        notifyDataSetChanged();
    }

    List<LiveChalObj> allDataList = new ArrayList<>();
    public void setAllDatas(List<LiveChalObj> dataList) {
        allDataList.clear();
        allDataList.addAll(dataList);
    }

    public int getPageNum(){
        int num = allDataList.size() / EXVAL.NUM_IN_COL_LIVE;
        if (allDataList.size()%EXVAL.NUM_IN_COL_LIVE>0){
            num+=1;
        }
        return num;
    }

    public void setCurChalName(String chalName) {
        curChalName = chalName;
    }

    public void clearDatas() {
        if (this.chals != null) {
            this.chals.clear();
            this.chals = null;
        }
    }

    @NonNull
    @Override
    public MVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MVHolder(layoutInflater.inflate(R.layout.live_chal_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        LiveChalObj chal = chals.get(position);
        if (chal.getName().equals(liveAct.curLiveChal.getName())) {
            holder.idV.setText("");
            holder.idV.setBackgroundResource(R.drawable.chal_id_play);
            liveAct.curLiveChal.setUiPos(chal.getUiPos());
            liveAct.curLiveChal.setListPos(chal.getListPos());
        } else {
            holder.idV.setText(chal.getUiPos());
            holder.idV.setBackgroundResource(R.drawable.chal_id_bg);
        }
        holder.favV.setVisibility(chal.getIsFav() ? View.VISIBLE : View.GONE);
        holder.nameV.setText(chal.getName());
        holder.itemView.setOnFocusChangeListener(mOnFocusChangeListener);
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.itemView.setOnKeyListener(mOnKeyListener);
        holder.itemView.setOnLongClickListener(mOnLongClickListener);
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public LiveChalObj getItemObject(int pos) {
        return pos >= size ? null : chals.get(pos);
    }

    public int getItemPosInList(LiveChalObj chal) {
//        MLog.d(TAG, String.format("getItemPosInList %s", chal));
        return size > 0 ? chals.indexOf(chal) : -1;
    }

    public void release() {
        mvHolder = null;
        liveAct = null;
        curChalName = null;
        chals = null;
        rv = null;
        layoutInflater = null;
        onAdapterItemListener = null;
        horChalListV = null;
        if (mOnKeyListener != null) {
            mOnKeyListener.relase();
        }
        if (mOnClickListener != null) {
            mOnClickListener.relase();
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.relase();
        }
        if (mOnLongClickListener != null) {
            mOnLongClickListener.relase();
        }
        mOnLongClickListener = null;
        mOnKeyListener = null;
        mOnClickListener = null;
        mOnFocusChangeListener = null;
    }

    public void setOnAdapterItemListener(OnRvAdapterListener onAdapterItemListener) {
        this.onAdapterItemListener = onAdapterItemListener;
    }

    public int getNum() {
        if (allDataList==null)
            return 0;
        else
            return allDataList.size();
    }

    public static class MVHolder extends RecyclerView.ViewHolder {

        TextView idV, nameV;
        ImageView favV;

        public MVHolder(View itemView) {
            super(itemView);
            idV = itemView.findViewById(R.id.list1_id);
            nameV = itemView.findViewById(R.id.list1_name);
            favV = itemView.findViewById(R.id.list1_fav);
        }
    }

    public void setSelection(final FocusBorder focusBorder, int position) {
//        MLog.d(TAG, "ChalAdapter setSelection " + position);
//        MLog.d(TAG, "ChalAdapter rv " + rv);
        if (position >= getItemCount()) {
            position = 0;
        }
        mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(position);
        if (mvHolder == null) {
            rv.scrollToPosition(position);
            final int finalPosition = position;
            rv.post(new Runnable() {
                @Override
                public void run() {
                    mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(finalPosition);
                    if (mvHolder != null) {
                        horChalListV.handleChalItemFocus(mvHolder.itemView);
                        mvHolder.idV.setTextColor(Color.BLACK);
                        mvHolder.nameV.setTextColor(Color.WHITE);
                        Utils.focusV(mvHolder.itemView, true);
                        liveAct.focusBorderForV(mvHolder.itemView);
                    }
                }
            });
        } else {
            horChalListV.handleChalItemFocus(mvHolder.itemView);
            mvHolder.idV.setTextColor(Color.BLACK);
            mvHolder.nameV.setTextColor(Color.WHITE);
            Utils.focusV(mvHolder.itemView, true);
            liveAct.focusBorderForV(mvHolder.itemView);
        }
    }

    public void markPlay(LiveChalObj oldChal, LiveChalObj newChal) {
        int oldPos = chals.indexOf(oldChal);
        if (oldPos >= 0) {
            MVHolder mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(oldPos);
            if (mvHolder != null) {
                mvHolder.idV.setText(oldChal.getUiPos());
                mvHolder.idV.setBackgroundResource(R.drawable.chal_id_bg);
            }
        }
        int newPos = chals.indexOf(newChal);
        if (newPos >= 0) {
            MVHolder mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(newPos);
            if (mvHolder != null) {
                mvHolder.idV.setText("");
                mvHolder.idV.setBackgroundResource(R.drawable.chal_id_play);
            }
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private ChalAdapter hostCls;

        public MOnFocusChangeListener(ChalAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                if (hostCls != null && hostCls.onAdapterItemListener != null) {
                    int curPos = hostCls.rv.getChildLayoutPosition(v);
                    hostCls.onAdapterItemListener.onItemSelected(v, curPos);
                }
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private ChalAdapter hostCls;

        public MOnClickListener(ChalAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public void onClick(View v) {
            if (hostCls != null && hostCls.onAdapterItemListener != null) {
                int curPos = hostCls.rv.getChildLayoutPosition(v);
                hostCls.onAdapterItemListener.onItemClick(v, curPos);
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private ChalAdapter hostCls;

        public MOnKeyListener(ChalAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (hostCls != null && hostCls.onAdapterItemListener != null) {
                    int curPos = hostCls.rv.getChildLayoutPosition(v);
                    hostCls.onAdapterItemListener.onItemKey(v, curPos, event, keyCode);
                }
            }
            return false;
        }
    }

    private static class MOnLongClickListener implements View.OnLongClickListener {

        private ChalAdapter hostCls;

        public MOnLongClickListener(ChalAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public boolean onLongClick(View v) {
            if (hostCls != null && hostCls.onAdapterItemListener != null) {
                int curPos = hostCls.rv.getChildLayoutPosition(v);
                hostCls.onAdapterItemListener.onItemLongClick(v, curPos);
            }
            return true;
        }
    }
}

/*
public class ChalAdapter extends RecyclerView.Adapter<ChalAdapter.MVHolder> {

    private static final String TAG = ChalAdapter.class.getSimpleName();
    private LayoutInflater layoutInflater;
    private List<LiveChalObj> chals;
    private RecyclerView rv;
    private int size;
    private OnRvAdapterListener onAdapterItemListener;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private MOnLongClickListener mOnLongClickListener;
    private String curChalName;
    private LiveAct liveAct;
    private int lastIndex;
    private MVHolder mvHolder;
    private HorChalListV horChalListV;

    public ChalAdapter(LiveAct liveAct, HorChalListV horChalListV) {
        this.liveAct = liveAct;
        this.horChalListV = horChalListV;
        layoutInflater = LayoutInflater.from(AppMain.ctx());
        mOnFocusChangeListener = new MOnFocusChangeListener(this);
        mOnClickListener = new MOnClickListener(this);
        mOnKeyListener = new MOnKeyListener(this);
        mOnLongClickListener = new MOnLongClickListener(this);
    }

    public void setRecyclerView(RecyclerView rv) {
        this.rv = rv;
    }

    public void setDatas(List<LiveChalObj> chals) {
        clearDatas();
        this.chals = chals;
        dalRepeatData(this.chals);
        size = this.chals == null ? 0 : this.chals.size();
        MLog.d(TAG, String.format("ChalAdapter setDatas %d", size));
        lastIndex = size - 1;
        notifyDataSetChanged();
    }

    public void dalRepeatData(List<LiveChalObj> chals){//移除重复的数据
        long sl = System.currentTimeMillis();
        for (int i = 0; i < chals.size(); i++) {
            for (int j = i; j < chals.size(); j++) {
                if (chals.get(i).getColId().equals(chals.get(j).getVodId())){
                    chals.remove(j);
                }
            }
        }
        long el = System.currentTimeMillis();
        Log.d(TAG, "dalRepeatData: " +(el-sl)+", size() = "+chals.size());
    }
    public void setCurChalName(String chalName) {
        curChalName = chalName;
    }

    public void clearDatas() {
        if (this.chals != null) {
            this.chals.clear();
            this.chals = null;
        }
    }

    @NonNull
    @Override
    public MVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MVHolder(layoutInflater.inflate(R.layout.live_chal_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder");
        LiveChalObj chal = chals.get(position);
        if (chal.getName().equals(liveAct.curLiveChal.getName())) {
            holder.idV.setText("");
            holder.idV.setBackgroundResource(R.drawable.chal_id_play);
            liveAct.curLiveChal.setUiPos(chal.getUiPos());
            liveAct.curLiveChal.setListPos(chal.getListPos());
        } else {
            holder.idV.setText(chal.getUiPos());
            holder.idV.setBackgroundResource(R.drawable.chal_id_bg);
        }
        holder.favV.setVisibility(chal.getIsFav() ? View.VISIBLE : View.GONE);
        holder.nameV.setText(chal.getName());
        holder.itemView.setOnFocusChangeListener(mOnFocusChangeListener);
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.itemView.setOnKeyListener(mOnKeyListener);
        holder.itemView.setOnLongClickListener(mOnLongClickListener);
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public int getLastIndex() {
        return lastIndex;
    }

    public LiveChalObj getItemObject(int pos) {
        return pos >= size ? null : chals.get(pos);
    }

    public int getItemPosInList(LiveChalObj chal) {
//        MLog.d(TAG, String.format("getItemPosInList %s", chal));
        onAdapterItemListener = null;
        horChalListV = null;
        if (mOnKeyListener != null) {
            mOnKeyListener.relase();
        }
        if (mOnClickListener != null) {
            mOnClickListener.relase();
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.relase();
        }
        return size > 0 ? chals.indexOf(chal) : -1;
    }

    public void release() {
        mvHolder = null;
        liveAct = null;
        curChalName = null;
        chals = null;
        rv = null;
        layoutInflater = null;
        if (mOnLongClickListener != null) {
            mOnLongClickListener.relase();
        }
        mOnLongClickListener = null;
        mOnKeyListener = null;
        mOnClickListener = null;
        mOnFocusChangeListener = null;
    }

    public void setOnAdapterItemListener(OnRvAdapterListener onAdapterItemListener) {
        this.onAdapterItemListener = onAdapterItemListener;
    }

    public static class MVHolder extends RecyclerView.ViewHolder {

        TextView idV, nameV;
        ImageView favV;

        public MVHolder(View itemView) {
            super(itemView);
            idV = itemView.findViewById(R.id.list1_id);
            nameV = itemView.findViewById(R.id.list1_name);
            favV = itemView.findViewById(R.id.list1_fav);
        }
    }

    public void setSelection(final FocusBorder focusBorder, int position) {
//        MLog.d(TAG, "ChalAdapter setSelection " + position);
//        MLog.d(TAG, "ChalAdapter rv " + rv);
        if (position >= getItemCount()) {
            position = 0;
        }
        mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(position);
        if (mvHolder == null) {
            rv.scrollToPosition(position);
            final int finalPosition = position;
            rv.post(new Runnable() {
                @Override
                public void run() {
                    mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(finalPosition);
                    if (mvHolder != null) {
                        horChalListV.handleChalItemFocus(mvHolder.itemView);
                        mvHolder.idV.setTextColor(Color.BLACK);
                        mvHolder.nameV.setTextColor(Color.WHITE);
                        Utils.focusV(mvHolder.itemView, true);
                        liveAct.focusBorderForV(mvHolder.itemView);
                    }
                }
            });
        } else {
            if (horChalListV!=null)
                horChalListV.handleChalItemFocus(mvHolder.itemView);
            mvHolder.idV.setTextColor(Color.BLACK);
            mvHolder.nameV.setTextColor(Color.WHITE);
            Utils.focusV(mvHolder.itemView, true);
            liveAct.focusBorderForV(mvHolder.itemView);
        }
    }

    public void markPlay(LiveChalObj oldChal, LiveChalObj newChal) {
        int oldPos = chals.indexOf(oldChal);
        if (oldPos >= 0) {
            MVHolder mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(oldPos);
            if (mvHolder != null) {
                mvHolder.idV.setText(oldChal.getUiPos());
                mvHolder.idV.setBackgroundResource(R.drawable.chal_id_bg);
            }
        }
        int newPos = chals.indexOf(newChal);
        if (newPos >= 0) {
            MVHolder mvHolder = (MVHolder) rv.findViewHolderForAdapterPosition(newPos);
            if (mvHolder != null) {
                mvHolder.idV.setText("");
                mvHolder.idV.setBackgroundResource(R.drawable.chal_id_play);
            }
        }
    }

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private ChalAdapter hostCls;

        public MOnFocusChangeListener(ChalAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                if (hostCls != null && hostCls.onAdapterItemListener != null) {
                    int curPos = hostCls.rv.getChildLayoutPosition(v);
                    hostCls.onAdapterItemListener.onItemSelected(v, curPos);
                }
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private ChalAdapter hostCls;

        public MOnClickListener(ChalAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public void onClick(View v) {
            if (hostCls != null && hostCls.onAdapterItemListener != null) {
                int curPos = hostCls.rv.getChildLayoutPosition(v);
                hostCls.onAdapterItemListener.onItemClick(v, curPos);
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private ChalAdapter hostCls;

        public MOnKeyListener(ChalAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (hostCls != null && hostCls.onAdapterItemListener != null) {
                    int curPos = hostCls.rv.getChildLayoutPosition(v);
                    hostCls.onAdapterItemListener.onItemKey(v, curPos, event, keyCode);
                }
            }
            return false;
        }
    }

    private static class MOnLongClickListener implements View.OnLongClickListener {

        private ChalAdapter hostCls;

        public MOnLongClickListener(ChalAdapter hostCls) {
            this.hostCls = hostCls;
        }

        public void relase() {
            hostCls = null;
        }

        @Override
        public boolean onLongClick(View v) {
            if (hostCls != null && hostCls.onAdapterItemListener != null) {
                int curPos = hostCls.rv.getChildLayoutPosition(v);
                hostCls.onAdapterItemListener.onItemLongClick(v, curPos);
            }
            return true;
        }
    }


}
*/

