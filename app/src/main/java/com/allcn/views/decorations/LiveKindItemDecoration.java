package com.allcn.views.decorations;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.allcn.R;
import com.allcn.utils.AppMain;

public class LiveKindItemDecoration extends RecyclerView.ItemDecoration {

    private static final String TAG = LiveKindItemDecoration.class.getSimpleName();
    private int topOffset, bottomOffset;

    public LiveKindItemDecoration() {
        this.topOffset = (int) AppMain.res().getDimension(R.dimen.live_kind_top_space);
        this.bottomOffset = (int) AppMain.res().getDimension(R.dimen.live_kind_bottom_space);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildLayoutPosition(view);
        int childCount = parent.getChildCount();
        //if (position == 0) {
            outRect.top += topOffset;
        //}
        //if(position==childCount-1)
            outRect.bottom += bottomOffset;
    }
}
