package com.allcn.views.decorations;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.allcn.R;
import com.allcn.utils.AppMain;

public class KeyboardItemDecoration extends RecyclerView.ItemDecoration {

    private int bOffset;

    public KeyboardItemDecoration() {
        this.bOffset = (int) AppMain.res().getDimension(R.dimen.keyboard_bottom_offset);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        if (position < 30) {
            outRect.bottom += bOffset;
        }
    }
}
