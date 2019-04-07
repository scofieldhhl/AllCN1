package com.allcn.views.layoutmanagers;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GalleryLayoutManager extends RecyclerView.LayoutManager {

    private static final String TAG = GalleryLayoutManager.class.getSimpleName();

    public static final int HORIZONTAL = OrientationHelper.HORIZONTAL;
    public static final int VERTICAL = OrientationHelper.VERTICAL;

    public static final int INVALID_POSITION = -1;
    public static final int MAX_VISIBLE_ITEMS = 2;

    private boolean mDecoratedChildSizeInvalid;
    private Integer mDecoratedChildWidth;
    private Integer mDecoratedChildHeight;

    private int mPendingScrollPosition;

    @Nullable
    private CarouselSavedState mPendingCarouselSavedState;

    private static final boolean CIRCLE_LAYOUT = false;

    private final int mOrientation;
    private final boolean mCircleLayout;

    private int mCenterItemPosition = INVALID_POSITION;
    private int mItemsCount;

    private final LayoutHelper mLayoutHelper = new LayoutHelper(MAX_VISIBLE_ITEMS);

    public GalleryLayoutManager(final int orientation) {
        this(orientation, CIRCLE_LAYOUT);
    }

    public GalleryLayoutManager(final int orientation, final boolean circleLayout) {
        if (HORIZONTAL != orientation && VERTICAL != orientation) {
            throw new IllegalArgumentException("orientation should be HORIZONTAL or VERTICAL");
        }
        mOrientation = orientation;
        mCircleLayout = circleLayout;
        mPendingScrollPosition = INVALID_POSITION;
    }


    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * onLayoutChildren 负责对子view的布局
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (0 == state.getItemCount()) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        if (null == mDecoratedChildWidth || mDecoratedChildSizeInvalid) {
            // 从Recycler获取一个Item的View
            final View view = recycler.getViewForPosition(0);
            // 将View加入到RecyclerView中
            addView(view);
            // 测量View,这个方法会考虑到View的ItemDecoration以及Margin
            measureChildWithMargins(view, 0, 0);
            // 获取View的宽高，包含ItemDecorate的尺寸
            final int decoratedChildWidth = getDecoratedMeasuredWidth(view);
            final int decoratedChildHeight = getDecoratedMeasuredHeight(view);

            // remove掉view，放到Recycler
            removeAndRecycleView(view, recycler);

            if (null != mDecoratedChildWidth && (mDecoratedChildWidth != decoratedChildWidth ||
                    mDecoratedChildHeight != decoratedChildHeight)) {
                if (INVALID_POSITION == mPendingScrollPosition && null == mPendingCarouselSavedState) {
                    mPendingScrollPosition = mCenterItemPosition;
                }
            }

            mDecoratedChildWidth = decoratedChildWidth;
            mDecoratedChildHeight = decoratedChildHeight;
            mDecoratedChildSizeInvalid = false;
        }
        // mPendingScrollPosition初始值为INVALID_POSITION
        if (INVALID_POSITION != mPendingScrollPosition) {
            final int itemsCount = state.getItemCount();
            mPendingScrollPosition = 0 == itemsCount ? INVALID_POSITION : Math.max(0, Math.min(
                    itemsCount - 1, mPendingScrollPosition));
        }

        if (INVALID_POSITION != mPendingScrollPosition) {
            mLayoutHelper.mScrollOffset =
                    calculateScrollForSelectingPosition(mPendingScrollPosition, state);
            mPendingScrollPosition = INVALID_POSITION;
            mPendingCarouselSavedState = null;
        } else if (null != mPendingCarouselSavedState) {
            mLayoutHelper.mScrollOffset = calculateScrollForSelectingPosition(
                    mPendingCarouselSavedState.mCenterItemPosition, state);
            mPendingCarouselSavedState = null;
        } else if (state.didStructureChange() && INVALID_POSITION != mCenterItemPosition) {
            // mCenterItemPosition初始值为INVALID_POSITION
            mLayoutHelper.mScrollOffset =
                    calculateScrollForSelectingPosition(mCenterItemPosition, state);
        }


    }

    private int calculateScrollForSelectingPosition(final int itemPosition,
                                                    final RecyclerView.State state) {
        final int fixedItemPosition = itemPosition < state.getItemCount() ?
                itemPosition : state.getItemCount() - 1;
        return fixedItemPosition * (VERTICAL == mOrientation ?
                mDecoratedChildHeight : mDecoratedChildWidth);
    }

    private void fillData(@NonNull final RecyclerView.Recycler recycler,
                          @NonNull final RecyclerView.State state) {
        // 通过当前滚动的距离计算当前选中的Item的位置
        final float currentScrollPosition = getCurrentScrollPosition();

        generateLayoutOrder(currentScrollPosition, state);
    }

    /**
     * 帮助方法，使滚动在[0, count)的范围内。通常这个方法只用于圆形布局。
     *
     * @param currentScrollPosition 任意滚动位置范围。
     * @param count                 适配器Item总数
     * @return 在[0, count)的范围内的最佳滚动的位置
     */
    private static float makeScrollPositionInRangeOToCount(final float currentScrollPosition,
                                                           final int count) {
        float absCurrentScrollPosition = currentScrollPosition;
        while (0 > absCurrentScrollPosition) {
            absCurrentScrollPosition += count;
        }
        // Math.round四舍五入取整
        while (Math.round(absCurrentScrollPosition) >= count) {
            absCurrentScrollPosition -= count;
        }
        return absCurrentScrollPosition;
    }

    /**
     * 由于我们需要支持老的Android版本，我们以指定的顺序来布局我们的子View，
     * 使我们中间的View显示在布局的最上面(中间这个Item应该最后布局)。因此，
     * 这个方法将计算布局的顺序，以及填充(@link #mLayoutHelper)对象。
     *
     * @param currentScrollPosition 当前滚动的位置，这个值表示中间Item的位置
     *                              (如果这个值为int，中间Item实际上在布局的中间，否则它接近state)
     *                              请注意，此值可以在任何范围内，因为它是圆形布局
     * @param state                 RecyclerView的短暂的状态
     * @see #getCurrentScrollPosition()
     */
    private void generateLayoutOrder(final float currentScrollPosition,
                                     @NonNull final RecyclerView.State state) {
        mItemsCount = state.getItemCount();
        final float absCurrentScrollPosition =
                makeScrollPositionInRangeOToCount(currentScrollPosition, mItemsCount);
        final int centerItem = Math.round(absCurrentScrollPosition);

        if (mCircleLayout && 1 < mItemsCount) {
            // +3 = 1 (center item) + 2 (addition bellow maxVisibleItems)
            final int layoutCount = Math.min(mLayoutHelper.mMaxVisibleItems * 2 + 3, mItemsCount);
            mLayoutHelper.initLayoutOrder(layoutCount);
            final int countLayoutHalf = layoutCount / 2;
            // 中间Item的前面
            for (int i = 1; i <= countLayoutHalf; ++i) {
                final int position = Math.round(
                        absCurrentScrollPosition - i + mItemsCount) % mItemsCount;
                mLayoutHelper.setLayoutOrder(countLayoutHalf - i, position,
                        centerItem - absCurrentScrollPosition - i);
            }
            // 中间Item的后面
            for (int i = layoutCount - 1; i >= countLayoutHalf + 1; --i) {
                final int position = Math.round(
                        absCurrentScrollPosition - i + layoutCount) % mItemsCount;
                mLayoutHelper.setLayoutOrder(i - 1, position,
                        centerItem - absCurrentScrollPosition + layoutCount - i);
            }
            mLayoutHelper.setLayoutOrder(layoutCount - 1, centerItem,
                    centerItem - absCurrentScrollPosition);
        } else {
            final int firstVisible = Math.max(centerItem - mLayoutHelper.mMaxVisibleItems - 1, 0);
            final int lastVisible = Math.min(centerItem + mLayoutHelper.mMaxVisibleItems + 1,
                    mItemsCount - 1);
            final int layoutCount = lastVisible - firstVisible + 1;

            mLayoutHelper.initLayoutOrder(layoutCount);

            for (int i = firstVisible; i <= lastVisible; ++i) {
                if (i == centerItem) {
                    mLayoutHelper.setLayoutOrder(layoutCount - 1, i,
                            i - absCurrentScrollPosition);
                }
            }
        }

    }

    /**
     * @return 中间Item的当前滚动位置。如果是循环布局，则该值可以在任何范围内。
     * 如果不是，它的范围就是[0, {@link #mItemsCount - 1}]
     */
    private float getCurrentScrollPosition() {
        final int fullScrollSize = getMaxScrollOffset();
        if (0 == fullScrollSize) {
            return 0;
        }
        return 1.0f * mLayoutHelper.mScrollOffset / getScrollItemSize();
    }

    /**
     * @return 最大滚动值，以填充布局中的所有item。
     * 通常，这仅适用于非循环布局
     */
    private int getMaxScrollOffset() {
        return getScrollItemSize() * (mItemsCount - 1);
    }

    /**
     * @return item的完整尺寸
     */
    protected int getScrollItemSize() {
        if (VERTICAL == mOrientation) {
            return mDecoratedChildHeight;
        } else {
            return mDecoratedChildWidth;
        }
    }

    protected static class CarouselSavedState implements Parcelable {

        private final Parcelable mSuperState;
        private int mCenterItemPosition;

        protected CarouselSavedState(@Nullable final Parcelable superState) {
            mSuperState = superState;
        }

        private CarouselSavedState(@NonNull final Parcel in) {
            mSuperState = in.readParcelable(Parcelable.class.getClassLoader());
            mCenterItemPosition = in.readInt();
        }

        protected CarouselSavedState(@NonNull final CarouselSavedState other) {
            mSuperState = other.mSuperState;
            mCenterItemPosition = other.mCenterItemPosition;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel parcel, final int i) {
            parcel.writeParcelable(mSuperState, i);
            parcel.writeInt(mCenterItemPosition);
        }

        public static final Parcelable.Creator<CarouselSavedState> CREATOR
                = new Parcelable.Creator<CarouselSavedState>() {
            @Override
            public CarouselSavedState createFromParcel(final Parcel parcel) {
                return new CarouselSavedState(parcel);
            }

            @Override
            public CarouselSavedState[] newArray(final int i) {
                return new CarouselSavedState[i];
            }
        };
    }

    /**
     * 持有item的数据的类
     * 这个类在{@link #generateLayoutOrder(float, RecyclerView.State)}期间被填充
     * 这个类在{@link #fillData(RecyclerView.Recycler, RecyclerView.State)}期间被使用
     */
    private static class LayoutOrder {
        /**
         * Item适配器中的位置
         */
        private int mItemAdapterPosition;

        /**
         * Item中心到布局中心的差距。
         * 如果item的中心就是布局的中心，这个值大于0，否则小于0
         */
        private float mItemPositionDiff;
    }

    /**
     * 持有当前可见item的帮助类。
     * 这个类持有所有滚动和最大可见的item的状态。
     */
    private static class LayoutHelper {
        private int mMaxVisibleItems;
        private int mScrollOffset;
        private LayoutOrder[] mLayoutOrder;
        private final List<WeakReference<LayoutOrder>> mReusedItems = new ArrayList<>();

        LayoutHelper(final int maxVisibleItems) {
            mMaxVisibleItems = maxVisibleItems;
        }

        /**
         * 在任何fill调用之前调用。需要回收老的items，
         * 初始化新的数组列表，通常，此列表是一个重用的数组。
         *
         * @param layoutCount 将要布局的items数量
         */
        void initLayoutOrder(final int layoutCount) {
            if (null == mLayoutOrder || mLayoutOrder.length != layoutCount) {
                if (null != mLayoutOrder) {
                    recycleItems(mLayoutOrder);
                }
                mLayoutOrder = new LayoutOrder[layoutCount];
                // 给LayoutOrder数组的每个元素赋值
                fillLayoutOrder();
            }
        }

        /**
         * 将之前的所有的LayoutOrder存到重用列表里
         */
        private void recycleItems(@NonNull final LayoutOrder... layoutOrders) {
            for (final LayoutOrder layoutOrder : layoutOrders) {
                mReusedItems.add(new WeakReference<LayoutOrder>(layoutOrder));
            }
        }

        /**
         * 从重用列表里取出已有的LayoutOrder，
         * 如果不够，则创建新的LayoutOrder
         */
        private void fillLayoutOrder() {
            for (int i = 0, length = mLayoutOrder.length; i < length; ++i) {
                if (null == mLayoutOrder[i]) {
                    mLayoutOrder[i] = createLayoutOrder();
                }
            }
        }

        private LayoutOrder createLayoutOrder() {
            final Iterator<WeakReference<LayoutOrder>> iterator = mReusedItems.iterator();
            while (iterator.hasNext()) {
                final WeakReference<LayoutOrder> layoutOrderWeakReference = iterator.next();
                final LayoutOrder layoutOrder = layoutOrderWeakReference.get();
                iterator.remove();
                if (null != layoutOrder) {
                    return layoutOrder;
                }
            }
            return new LayoutOrder();
        }

        /**
         * 布局生成过程中被调用。
         * 应该在{@link #initLayoutOrder(int)}方法调用之后再调用。
         *
         * @param arrayPosition       在layoutOrder中的位置
         * @param itemAdapterPosition item在适配器中的位置
         * @param itemPositionDiff    当前项目滚动的位置和中间item位置的差距。
         *                            如果这是item处于布局的中心，那么值为0.
         *                            如果当前布局不在中心，那么这个值永远不会是int。
         *                            如果这个item的中心低于布局中心线，这个值大于0，
         *                            否则小于0.
         */
        void setLayoutOrder(final int arrayPosition, final int itemAdapterPosition,
                            final float itemPositionDiff) {
            final LayoutOrder item = mLayoutOrder[arrayPosition];
            item.mItemAdapterPosition = itemAdapterPosition;
            item.mItemPositionDiff = itemPositionDiff;
        }

        /**
         * 检查adapterPosition对应的view是否在屏幕布局内
         *
         * @param adapterPosition item的适配器位置
         * @return 在布局中则返回true
         */
        boolean hasAdapterPosition(final int adapterPosition) {
            if (null != mLayoutOrder) {
                for (final LayoutOrder layoutOrder : mLayoutOrder) {
                    if (layoutOrder.mItemAdapterPosition == adapterPosition) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
