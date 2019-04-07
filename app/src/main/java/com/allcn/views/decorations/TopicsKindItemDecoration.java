package com.allcn.views.decorations;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.allcn.R;
import com.allcn.utils.AppMain;

public class TopicsKindItemDecoration extends RecyclerView.ItemDecoration {

    private int leftOffset, bottomOffset, spanCount;

    public TopicsKindItemDecoration() {
        this.leftOffset = (int) AppMain.res().getDimension(R.dimen.topics_kind_left_space);
        this.bottomOffset = (int) AppMain.res().getDimension(R.dimen.topics_kind_bottom_space);
        this.spanCount = 5;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view) + 1;
//        if (position % spanCount != 0) {
            outRect.right += leftOffset;
//        }
        outRect.bottom += bottomOffset;
    }
}
