package com.allcn.views.decorations;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.allcn.R;
import com.allcn.utils.AppMain;

public class SEDMMovieItemDecoration extends RecyclerView.ItemDecoration {

    private int rightOffset, bottomOffset, spanCount;

    public SEDMMovieItemDecoration() {
        this.rightOffset = (int) AppMain.res().getDimension(R.dimen.sedm_movie_right_space);
        this.bottomOffset = (int) AppMain.res().getDimension(R.dimen.sedm_movie_bottom_space);
    }

    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int postion = parent.getChildAdapterPosition(view) + 1;
        if (postion % spanCount != 0) {
            outRect.right += rightOffset;
        }
        outRect.bottom += bottomOffset;
    }
}
