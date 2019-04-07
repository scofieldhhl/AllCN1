package com.allcn.views.decorations;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.allcn.R;
import com.allcn.utils.AppMain;

public class DetailsReItemDecoration extends RecyclerView.ItemDecoration {

    private int leftOffset;

    public DetailsReItemDecoration() {
        this.leftOffset = (int) AppMain.res().getDimension(R.dimen.details_re_item_left_offset);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int postion = parent.getChildAdapterPosition(view);
        if (postion != 0) {
            outRect.left += leftOffset;
        }
    }
}
