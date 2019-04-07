package com.allcn.views.decorations;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.allcn.R;
import com.allcn.utils.AppMain;

public class MovieItemDecoration extends RecyclerView.ItemDecoration {

    private int leftOffset, bottomOffset, spanCount;

    public MovieItemDecoration() {
//        this.leftOffset = (int) AppMain.res().getDimension(R.dimen.movie_left_space);
        this.bottomOffset = (int) AppMain.res().getDimension(R.dimen.movie_bottom_space);
    }

    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
//        int position = parent.getChildAdapterPosition(view) + 1;
//        if (position % spanCount != 0) {
//            outRect.right += leftOffset;
//        }
        outRect.bottom += bottomOffset;
    }
}
