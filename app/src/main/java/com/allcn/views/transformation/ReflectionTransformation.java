package com.allcn.views.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

import java.security.MessageDigest;

import jp.wasabeef.glide.transformations.BitmapTransformation;

public class ReflectionTransformation extends BitmapTransformation {

    private static final String TAG = ReflectionTransformation.class.getSimpleName();
    private static final int VERSION = 1;
    private static final String ID = "zstar.ReflectionTransformation." + VERSION;
    private int reflectionWidth, reflectionHeight;

    public ReflectionTransformation() {
        this(0, 0);
    }

    public ReflectionTransformation(int reflectionWidth, int reflectionHeight) {
        this.reflectionWidth = reflectionWidth;
        this.reflectionHeight = reflectionHeight;
    }

    @Override
    protected Bitmap transform(Context context, BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        int width = toTransform.getWidth();
        int height = toTransform.getHeight();
        if (this.reflectionWidth == 0) {
            this.reflectionWidth = width;
        }
        if (this.reflectionHeight <= 0) {
            this.reflectionHeight = height / 3;
        }
        int endHeight = height + reflectionHeight;
        // 生成倒影
        Matrix matrix = new Matrix();
        matrix.setScale(1, -1);
        // 要从原图片的最底部绘制倒影，需要设置height - reflectionHeight
        // 设置为height，会从截取中间部分作为倒影
        Bitmap inverseBitmap = Bitmap.createBitmap(toTransform, 0, height - reflectionHeight, width,
                reflectionHeight, matrix, false);
        inverseBitmap.setHasAlpha(true);
        // 创建合并原始图像和倒影的Bitmap
        Bitmap endBitmap = pool.get(width, endHeight, toTransform.getConfig());

        Canvas canvas = new Canvas(endBitmap);
        //绘制原始图像
        canvas.drawBitmap(toTransform, 0, 0, null);
        //绘制原始图像倒影
        canvas.drawBitmap(inverseBitmap, 0, height, null);
        //使用合适的LinearGradient为倒影添加一个渐变效果
        Paint paint = new Paint();

        Shader.TileMode tileMode = Shader.TileMode.CLAMP;

        LinearGradient shader = new LinearGradient(0, height, 0, endHeight,
                0xa0ffffff, 0x00ffffff, tileMode);

        paint.setShader(shader);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        canvas.drawRect(0, height, width, endHeight, paint);

        inverseBitmap.recycle();

        return endBitmap;
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update((ID + reflectionHeight + reflectionWidth).getBytes(CHARSET));
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof ReflectionTransformation &&
                ((ReflectionTransformation) o).reflectionHeight == reflectionHeight &&
                ((ReflectionTransformation) o).reflectionWidth == reflectionWidth;
    }

    @Override
    public int hashCode() {
        return ID.hashCode() + reflectionWidth + reflectionHeight;
    }
}
