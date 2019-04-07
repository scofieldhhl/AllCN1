package com.allcn.adapters;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.allcn.R;
import com.allcn.utils.AppMain;
import com.allcn.utils.EXVAL;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.datas.CKindObj;
import com.datas.MovieObj;
import com.mast.lib.utils.Utils;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class HotReAdaper extends RecyclerView.Adapter<HotReAdaper.MVHolder> {

    private static final String TAG = HotReAdaper.class.getSimpleName();
    private int size;
    private List<Object> datas;
    private List<Object> backcupDatas;
    private MOnClickListener mOnClickListener;
    private MOnKeyListener mOnKeyListener;
    private MOnFocusChangeListener mOnFocusChangeListener;
    private RecyclerView rv;
    private ObjectAnimator fAnim;
    private ObjectAnimator nFAnim;
    private FrameLayout oldFocusView;
    private PropertyValuesHolder scaleXF;
    private PropertyValuesHolder scaleXNoF;
    private PropertyValuesHolder scaleYF;
    private PropertyValuesHolder scaleYNoF;

    public HotReAdaper() {
        size = 0;
        mOnClickListener = new MOnClickListener(this);
        mOnKeyListener = new MOnKeyListener(this);
        mOnFocusChangeListener = new MOnFocusChangeListener(this);
        fAnim = new ObjectAnimator();
        fAnim.setDuration(300);
        fAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        nFAnim = new ObjectAnimator();
        nFAnim.setDuration(300);
        nFAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXF = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.04f);
        scaleXNoF = PropertyValuesHolder.ofFloat("scaleX", 1.04f, 1f);
        scaleYF = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.04f);
        scaleYNoF = PropertyValuesHolder.ofFloat("scaleY", 1.04f, 1f);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.rv = recyclerView;
    }

    @NonNull
    @Override
    public MVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MVHolder(LayoutInflater.from(AppMain.ctx()).inflate(R.layout.hot_re_movie_item,
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MVHolder holder, int position) {
        Object item = datas.get(position);
        String name = null, imgUrl = null;
        int type = EXVAL.TYPE_AREA_NONE;
        if (item instanceof CKindObj) {
            CKindObj ckind = (CKindObj) item;
            name = ckind.getName();
            imgUrl = ckind.getImgUrl();
            type = ckind.getType();
        } else if (item instanceof MovieObj) {
            MovieObj movie = (MovieObj) item;
            name = movie.getName();
            imgUrl = movie.getImgUrl();
            type = movie.getType();
            String label = movie.getLabel();
            if (holder.infoV != null) {
                if (!TextUtils.isEmpty(movie.getJs()) && !movie.getJs().equals("1")) {
                    holder.infoV.setText(AppMain.res().getString(R.string.update_to_ji, movie.getJs()));
                    holder.infoV.setVisibility(View.VISIBLE);
                } else {
                    holder.infoV.setVisibility(View.GONE);
                }
            }
        }
        Glide.with(holder.itemView.getContext()).load(imgUrl).apply(
                RequestOptions.bitmapTransform(new RoundedCornersTransformation(
                        (int) AppMain.res().getDimension(R.dimen.kind_movie_img_radius),
                        0, RoundedCornersTransformation.CornerType.ALL))).into(holder.imgV);
        holder.textV.setText(name);
        holder.itemView.setOnClickListener(mOnClickListener);
        holder.itemView.setOnKeyListener(mOnKeyListener);
        holder.itemView.setOnFocusChangeListener(mOnFocusChangeListener);
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public void backupDatas() {
        backcupDatas = datas;
        datas = null;
    }

    public void resumeDatas() {
        setDatas(backcupDatas);
        backcupDatas = null;
    }

    public void setDatas(List<Object> datas) {
        clearDatas();
        this.datas = datas;
        this.size = this.datas == null ? 0 : this.datas.size();
        notifyDataSetChanged();
    }

    public void clearDatas() {
        if (datas != null) {
            datas.clear();
            datas = null;
        }
    }

    public void setOnAdapterListener(MOnAdapterListener mOnAdapterListener) {
        this.mOnAdapterListener = mOnAdapterListener;
    }

    public static class MVHolder extends RecyclerView.ViewHolder {

        public ImageView imgV, labelV;
        public TextView textV, infoV;

        public MVHolder(View itemView) {
            super(itemView);
            imgV = itemView.findViewById(R.id.hot_re_movie_item_img);
            labelV = itemView.findViewById(R.id.hot_re_movie_item_label);
            textV = itemView.findViewById(R.id.hot_re_movie_item_text);
            infoV = itemView.findViewById(R.id.hot_re_movie_item_info);
        }
    }

    public interface MOnAdapterListener {
        void onItemClick(View v, Object data, int position);

        void onItemKey(View v, int position, int keyCode, KeyEvent event);

        void onItemSelected(View v, Object data, int position);
    }

    private MOnAdapterListener mOnAdapterListener;

    private static class MOnFocusChangeListener implements View.OnFocusChangeListener {

        private HotReAdaper adaper;

        public MOnFocusChangeListener(HotReAdaper adaper) {
            this.adaper = adaper;
        }

        public void release() {
            adaper = null;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (hasFocus) {
                if (adaper != null) {
                    adaper.onFocus((FrameLayout) v);
                    int pos = adaper.rv.getChildAdapterPosition(v);
                    Object object = pos < 0 ? null : adaper.datas.get(pos);
                    if (adaper.mOnAdapterListener != null && pos >= 0) {
                        adaper.mOnAdapterListener.onItemSelected(v, object, pos);
                    }
                }
            } else {
                if (adaper != null) {
                    adaper.noFocus((FrameLayout) v);
                }
            }
        }
    }

    private static class MOnClickListener implements View.OnClickListener {

        private HotReAdaper adaper;

        public MOnClickListener(HotReAdaper adaper) {
            this.adaper = adaper;
        }

        public void release() {
            adaper = null;
        }

        @Override
        public void onClick(View v) {
            if (adaper != null && adaper.mOnAdapterListener != null) {
                int pos = adaper.rv.getChildAdapterPosition(v);
                Object object = pos < 0 ? null : adaper.datas.get(pos);
                adaper.mOnAdapterListener.onItemClick(v, object, pos);
            }
        }
    }

    private static class MOnKeyListener implements View.OnKeyListener {

        private HotReAdaper adaper;

        public MOnKeyListener(HotReAdaper adaper) {
            this.adaper = adaper;
        }

        public void release() {
            adaper = null;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (adaper != null && adaper.mOnAdapterListener != null) {
                    int pos = adaper.rv.getChildAdapterPosition(v);
                    adaper.mOnAdapterListener.onItemKey(v, pos, keyCode, event);
                }
            }

            return false;
        }
    }

    public void setSelection(final int position) {
        Log.d(TAG, "HomeKindAdapter setSelection " + position);
        Log.d(TAG, "HomeKindAdapter rv " + rv);
        try {
            oldFocusView = (FrameLayout) rv.getChildAt(position);
            if (oldFocusView == null) {
                Log.e(TAG, "HomeKindAdapter oldFocusView null");
                rv.scrollToPosition(position);
                rv.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "HomeKindAdapter oldFocusView again");
                        try {
                            oldFocusView = (FrameLayout) rv.getChildAt(position);
                            if (oldFocusView != null) {
                                oldFocusView.setSelected(true);
                                Utils.focusV(oldFocusView, true);
                            }
                        } catch (Exception e) {}
                    }
                });
            } else if (oldFocusView != null) {
                oldFocusView.setSelected(true);
                Utils.focusV(oldFocusView, true);
            }
        } catch (Exception e) {
        }
    }

    private void playAnim(View target, ObjectAnimator objAnim, PropertyValuesHolder... params) {
        if (objAnim == null) {
            return;
        }
        if (params == null || params.length <= 0) {
            return;
        }
        objAnim.setTarget(target);
        objAnim.setValues(params);
        objAnim.start();
    }

    private void stopAnim(ObjectAnimator objAnim) {
        if (objAnim != null && objAnim.isRunning()) {
            objAnim.end();
        }
    }

    public void onFocus(FrameLayout view) {
        if (view != null) {
            ((TextView) view.getChildAt(1)).setSelected(true);
            stopAnim(fAnim);
            playAnim(view, fAnim, scaleXF, scaleYF);
        }
    }

    public void noFocus(FrameLayout view) {
        if (view != null) {
            ((TextView) view.getChildAt(1)).setSelected(false);
            stopAnim(nFAnim);
            playAnim(view, nFAnim, scaleXNoF, scaleYNoF);
        }
    }

    public void release() {
        clearDatas();
        stopAnim(fAnim);
        stopAnim(nFAnim);
        rv = null;
        fAnim = null;
        nFAnim = null;
        if (mOnClickListener != null) {
            mOnClickListener.release();
            mOnClickListener = null;
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.release();
            mOnFocusChangeListener = null;
        }
        if (mOnKeyListener != null) {
            mOnKeyListener.release();
            mOnKeyListener = null;
        }
        scaleXF = null;
        scaleXNoF = null;
        scaleYF = null;
        scaleYNoF = null;
        oldFocusView = null;
    }
}
