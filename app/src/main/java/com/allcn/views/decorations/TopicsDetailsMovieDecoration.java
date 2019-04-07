package com.allcn.views.decorations;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.allcn.R;
import com.allcn.utils.AppMain;

public class TopicsDetailsMovieDecoration extends RecyclerView.ItemDecoration {

    private int topOffset, leftOffset, rightOffset, bottomOffset;

    public TopicsDetailsMovieDecoration() {
        this.topOffset =
                (int) AppMain.res().getDimension(R.dimen.topics_details_movie_item_top_offset);
        this.leftOffset =
                (int) AppMain.res().getDimension(R.dimen.topics_details_movie_item_left_offset);
        this.rightOffset =
                (int) AppMain.res().getDimension(R.dimen.topics_details_movie_item_right_offset);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int vIndex = parent.getChildLayoutPosition(view);
        if (vIndex == 0) {
            outRect.left += leftOffset;
        }
        outRect.right += rightOffset;
        outRect.top += topOffset;
    }
}
