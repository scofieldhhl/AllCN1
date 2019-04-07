package com.allcn.views.decorations;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.allcn.R;
import com.allcn.utils.AppMain;

public class HotReDetailsMovieItemDecoration extends RecyclerView.ItemDecoration {

    private int rightOffset;

    public HotReDetailsMovieItemDecoration() {
        this.rightOffset = (int) AppMain.res().getDimension(
                R.dimen.hot_re_details_movie_right_space);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
//        int postion = parent.getChildAdapterPosition(view);
//        int count = parent.getAdapter().getItemCount();
//        if (postion >= 0 && postion < (count - 1)) {
            outRect.right += rightOffset;
//        }
    }
}
