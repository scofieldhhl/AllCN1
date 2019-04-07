package com.allcn.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.allcn.R;
import com.allcn.views.transformation.ReflectionTransformation;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class GlideUtils {

    private RequestOptions requestOptions = new RequestOptions();
    private RequestOptions sedmRequestOptions = new RequestOptions();
    private RequestOptions topicsTopRequestOptions = new RequestOptions();
    private static GlideUtils ins;

    private GlideUtils() {
        this.requestOptions
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(
                        (int) AppMain.res().getDimension(R.dimen.kind_movie_img_radius),
                        0, RoundedCornersTransformation.CornerType.ALL)))
                .skipMemoryCache(true);
        this.sedmRequestOptions
                .apply(RequestOptions.bitmapTransform(new RoundedCornersTransformation(
                        (int) AppMain.res().getDimension(R.dimen.sedm_kind_movie_img_radius),
                        0, RoundedCornersTransformation.CornerType.ALL)))
                .skipMemoryCache(true);
        this.topicsTopRequestOptions
                .skipMemoryCache(true);
    }

    public static GlideUtils Ins() {
        if (ins == null) {
            synchronized (GlideUtils.class) {
                if (ins == null) {
                    ins = new GlideUtils();
                }
            }
        }
        return ins;
    }

    public void loadUrlImg(Context mCtx, String imgUrl, final View view) {
        Glide.with(mCtx).load(imgUrl).into(new MSimpleTarget(view));
    }

    public void loadUrlImg(Context mCtx, String imgUrl, final ImageView view) {
        Glide.with(mCtx).load(imgUrl).transform(
                new CenterCrop(),
                new RoundedCornersTransformation(
                        (int) AppMain.res().getDimension(R.dimen.kind_movie_img_radius), 0)
        )
                .skipMemoryCache(true)
                .into(view);
    }

    public void loadSedmUrlImg(Context mCtx, String imgUrl, final ImageView view) {
        Glide.with(mCtx).load(imgUrl).transform(
                new CenterCrop(),
                new RoundedCornersTransformation(
                        (int) AppMain.res().getDimension(R.dimen.sedm_kind_movie_img_radius), 0)
        )
                .skipMemoryCache(true)
                .into(view);
    }

    public void loadTopTopicsUrlImg(Context mCtx, String imgUrl, final ImageView view) {
        Glide.with(mCtx).load(imgUrl).transform(
//                new CenterCrop(),
                new RoundedCornersTransformation(
                        (int) AppMain.res().getDimension(R.dimen.top_topics_img_radius), 0),
                new ReflectionTransformation(0,
                        (int) AppMain.res().getDimension(R.dimen.reflection_height))
        )
                .skipMemoryCache(true)
                .into(view);
    }

    public void loadSetImg(Context mCtx, int resId, final View view) {
        Glide.with(mCtx).load(AppMain.res().getDrawable(resId)).transform(
                new ReflectionTransformation(0,
                        (int) AppMain.res().getDimension(R.dimen.set_reflection_height))
        )
                .skipMemoryCache(true)
                .into(new MSimpleTarget(view));
    }

    public void clearDiskCache(Context mCtx) {
        Glide.get(mCtx).clearDiskCache();
    }

    public void clearMemCache(Context mCtx) {
        Glide.get(mCtx).clearMemory();
    }

    public void release(Context mCtx) {

    }

    private static class MSimpleTarget extends SimpleTarget<Drawable> {

        private View view;

        public MSimpleTarget(View view) {
            this(view, SIZE_ORIGINAL, SIZE_ORIGINAL);
        }

        public MSimpleTarget(View view, int width, int height) {
            super(width, height);
            this.view = view;
        }

        @Override
        public void onResourceReady(@NonNull Drawable resource,
                                    @Nullable Transition<? super Drawable> transition) {
            if (view != null) {
                view.setBackgroundDrawable(resource);
            }
            view = null;
        }
    }
}
