package com.allcn.views.decorations;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.allcn.R;
import com.allcn.utils.AppMain;

public class PlayBackLeftDecorations extends RecyclerView.ItemDecoration {

    private int bottomOffset;

    public PlayBackLeftDecorations() {
        this.bottomOffset = (int) AppMain.res().getDimension(R.dimen.playback_left_bottom_offset);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        int itemCount = parent.getAdapter().getItemCount();
        if (position == (itemCount - 1)) {
            outRect.bottom += this.bottomOffset;
        }
    }
}
