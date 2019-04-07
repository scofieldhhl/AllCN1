package com.allcn.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.allcn.R;
import com.allcn.utils.AppMain;

/**
 * Created by linlinlin on 2016/7/29. 作者：TianNuo 邮箱：1320917731@qq.com
 */
public class UiSeeKBar extends SeekBar {

    // 进度条指示文字后缀
    private String numTextFormat = "%";

    private String numText;
    // 进度条指示文字的大小吗默认20px
    private int numTextSize = 20;
    // 进度条指示文字的背景
    private int numbackground;
    // numbackground对应的bitmap
    private int numTextColor;
    private Bitmap bm;
    // bitmap对应的宽高
    private float bmp_width, bmp_height;
    // 构建画笔和文字
    Paint bmPaint;
    // 文本的宽可能不准
    private float numTextWidth;
    // 测量seekbar的规格
    private Rect rect_seek;
    // 测量thum的规格
    private Rect rect_thum;

    // show 在top还是bottom
    private int type = Gravity.TOP;
    private Paint.FontMetrics fm;
    // 特别说明这个scale比例是滑动的指示器小箭头部分占全部图片的比列，为了使其文字完全居中
    private double numScale = 0.16;

    private int curProgress;

    private float rectSeekW, rectSeekH;

    public UiSeeKBar(Context context) {
        this(context, null);
    }

    public UiSeeKBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UiSeeKBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 初始化属性
        init(context, attrs);
        // 初始化bm
        initBm();
        // 构建画笔
        initPaint();
        // 由于view没有默认的padding需要设置预留显示图标
        setPadding();

    }

    /**
     * 定位文本的位置，让其居中
     */
    private void setTextLocation() {
        // fm=bmPaint.getFontMetrics();
        // 居中
        // float center_text=bmp_height/2-fm

    }

    private void setPadding() {
        switch (type) {
            case Gravity.TOP:
                setPadding((int) Math.ceil(bmp_width) / 2, (int) Math.ceil(bmp_height), (int) Math.ceil(bmp_width) / 2, 0);
                break;

            case Gravity.BOTTOM:
                setPadding((int) Math.ceil(bmp_width) / 2, 0, (int) Math.ceil(bmp_width) / 2, (int) Math.ceil(bmp_height));
                break;
        }

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {

            fm = bmPaint.getFontMetrics();
            // numText = (getProgress() * 100 / getMax()) + numTextFormat;
            numTextWidth = bmPaint.measureText(numText);

            if (null == rect_seek) {
                rect_seek = this.getProgressDrawable().getBounds();
                rectSeekW = rect_seek.width();
                rectSeekH = rect_seek.height();
            }
            float thum_height = 0;
            // api必须大于16
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                rect_thum = this.getThumb().getBounds();
                thum_height = rect_thum.height();
            }

            // 计算bitmap左上的位置坐标
//			float rectSeekW = rect_seek.width();
            float secondProgress = curProgress;
            float maxProgress = getMax();
            float bm_x = rectSeekW * secondProgress / maxProgress;
            // 计算文字的中心位置在bitmap
            float text_x = bm_x + (bmp_width - numTextWidth) / 2;
            // 还应该减去文字的高度

            // float text_y = bmp_height / 2;
            // float text_center = bmp_height / 2 - fm.descent + (fm.descent -
            // fm.ascent) / 2;
            switch (type) {
                case Gravity.TOP:
                    canvas.drawBitmap(bm, bm_x, rectSeekH, bmPaint);
//				canvas.drawText(numText, text_x, (float) (bmp_height / 2 - (fm.descent - (fm.descent - fm.ascent) / 2)
//						- (bmp_height * numScale) / 2), bmPaint);
                    break;
                case Gravity.BOTTOM:
                    canvas.drawBitmap(bm, bm_x, rectSeekH, bmPaint);
//				canvas.drawText(numText, text_x, (float) (thum_height
//						+ (bmp_height / 2 - (fm.descent - (fm.descent - fm.ascent) / 2) + bmp_height * numScale / 2))
//						+ 15, bmPaint);
                    break;
                default:
                    break;

            }

            // 设置文本的位置
        } catch (Exception e) {
            // 为什么要try因为你的参数可能没有填
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        invalidate();
        return super.onTouchEvent(event);
    }

    private void initPaint() {
        // 抗锯齿
        bmPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bmPaint.setTypeface(Typeface.DEFAULT);
//		bmPaint.setTypeface(AppMain.typeface);
        bmPaint.setTextSize(numTextSize);
        bmPaint.setColor(numTextColor);
    }

    private void initBm() {
        bm = BitmapFactory.decodeResource(getResources(), numbackground);
        // 注意判断是否是null
        if (bm != null) {
            int setWidth = (int) AppMain.res().getDimension(R.dimen.kj_w);
            int setHeight = (int) AppMain.res().getDimension(R.dimen.kj_h);
            bm = Bitmap.createScaledBitmap(bm, setWidth, setHeight, true);
            bmp_width = bm.getWidth();
            bmp_height = bm.getHeight();
        }
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomSeekBar);
        numTextFormat = array.getString(R.styleable.CustomSeekBar_numTextFormat);
        numbackground = array.getResourceId(R.styleable.CustomSeekBar_numbackground, 0);
        numTextSize = array.getDimensionPixelSize(R.styleable.CustomSeekBar_numTextSize,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
        numTextColor = array.getColor(R.styleable.CustomSeekBar_numTextColor, Color.WHITE);
        type = array.getInt(R.styleable.CustomSeekBar_numType, Gravity.TOP);

        numScale = Double.parseDouble(array.getString(R.styleable.CustomSeekBar_numScale) == null ? numScale + ""
                : array.getString(R.styleable.CustomSeekBar_numScale));
        numTextFormat = numTextFormat == null ? "%" : numTextFormat;

        array.recycle();
    }

    public String getNumText() {
        return numText;
    }

    public void setNumText(String numText) {
        this.numText = numText;
        invalidate();
    }

    public int getNumTextSize() {
        return numTextSize;
    }

    public void setNumTextSize(int numTextSize) {
        this.numTextSize = numTextSize;

    }

    public int getNumbackground() {
        return numbackground;
    }

    public void setNumbackground(int numbackground) {
        this.numbackground = numbackground;
    }

    public int getNumTextColor() {
        return numTextColor;
    }

    public void setNumTextColor(int numTextColor) {
        this.numTextColor = numTextColor;
    }

    public void setCurProgress(int progress) {
        this.curProgress = progress;
    }

    public int getCurPorgress() {
        return this.curProgress;
    }
}
