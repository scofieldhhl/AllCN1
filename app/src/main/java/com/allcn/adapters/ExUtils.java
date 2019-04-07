package com.allcn.adapters;

import android.graphics.Paint;
import android.widget.TextView;

import com.mast.lib.utils.Utils;

public class ExUtils extends Utils {

    /**
     * * 判断TextView的内容宽度是否超出其可用宽度
     * * @paramtv
     * * @return
     *  
     */
    public static boolean isOverFlowed(TextView tv) {
        int availableWidth = tv.getWidth() - tv.getPaddingLeft() - tv.getPaddingRight();
        Paint textViewPaint = tv.getPaint();
        float textWidth = textViewPaint.measureText(tv.getText().toString());
        if (textWidth > availableWidth) {
            return true;
        } else {
            return false;
        }
    }
}
