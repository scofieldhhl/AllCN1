package com.allcn.views.decorations;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.allcn.R;
import com.allcn.utils.AppMain;

public class KindItemDecoration extends RecyclerView.ItemDecoration {

    private int topOffset;

    public KindItemDecoration() {
        topOffset = (int) AppMain.res().getDimension(R.dimen.pkind_vertical_space);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int postion = parent.getChildAdapterPosition(view);
        if (postion != 0) {
            outRect.top += topOffset;
        }
    }
}
