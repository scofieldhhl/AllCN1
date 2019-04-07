package com.allcn.views.decorations;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.allcn.R;
import com.allcn.utils.AppMain;
import com.allcn.utils.EXVAL;

public class CollectionItemDecoration extends RecyclerView.ItemDecoration {

    private int rOffset, bOffset, lastStartIndex;

    public CollectionItemDecoration() {
        this.rOffset = (int) AppMain.res().getDimension(R.dimen.collection_item_r_offset);
        this.bOffset = (int) AppMain.res().getDimension(R.dimen.collection_item_b_offset);
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
        if (lastStartIndex == 0) {
            int count = parent.getAdapter().getItemCount();
            int pageNum = count / EXVAL.SEDM_COLLECTION_NUM_IN_LINE;
            if (count % EXVAL.SEDM_COLLECTION_NUM_IN_LINE != 0) {
                pageNum++;
            }
            lastStartIndex = (pageNum - 1) * EXVAL.SEDM_COLLECTION_NUM_IN_LINE;
        }
        int position = parent.getChildAdapterPosition(view);
        if (position < lastStartIndex) {
            outRect.bottom += bOffset;
        }
        if (position < (EXVAL.SEDM_COLLECTION_NUM_IN_LINE - 1)) {
            outRect.right += rOffset;
        }
    }

    public void reset() {
        this.lastStartIndex = 0;
    }
}
