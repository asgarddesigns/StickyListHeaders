package se.emilsjolander.stickylistheaders;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/**
 * add expand/collapse functions like ExpandableListView
 *
 * @author lsjwzh
 */
public class ExpandableStickyListHeadersListView extends StickyListHeadersListView {
    public interface IAnimationExecutor {
        public void executeAnim(View target, int animType);
    }

    public final static int ANIMATION_COLLAPSE = 1;
    public final static int ANIMATION_EXPAND = 0;

    ExpandableStickyListHeadersAdapter mExpandableStickyListHeadersAdapter;
    private boolean mStartCollapsed;


    IAnimationExecutor mDefaultAnimExecutor = new IAnimationExecutor() {
        @Override
        public void executeAnim(View target, int animType) {
            if (animType == ANIMATION_EXPAND) {
                target.setVisibility(VISIBLE);
            } else if (animType == ANIMATION_COLLAPSE) {
                target.setVisibility(GONE);
            }
        }
    };


    public ExpandableStickyListHeadersListView(Context context) {
        super(context);
    }

    public ExpandableStickyListHeadersListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.ExpandableStickyListHeadersListView);

            mStartCollapsed = typedArray.getBoolean(
                    R.styleable.ExpandableStickyListHeadersListView_startCollapsed,
                    false);
            typedArray.recycle();
        }
    }

    public ExpandableStickyListHeadersListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.ExpandableStickyListHeadersListView);

            mStartCollapsed = typedArray.getBoolean(
                    R.styleable.ExpandableStickyListHeadersListView_startCollapsed,
                    true);
            typedArray.recycle();
        }
    }

    @Override
    public ExpandableStickyListHeadersAdapter getAdapter() {
        return mExpandableStickyListHeadersAdapter;
    }

    @Override
    public void setAdapter(StickyListHeadersAdapter adapter) {
        mExpandableStickyListHeadersAdapter = new ExpandableStickyListHeadersAdapter(adapter, mStartCollapsed);
        super.setAdapter(mExpandableStickyListHeadersAdapter);
    }

    public View findViewByItemId(long itemId) {
        return mExpandableStickyListHeadersAdapter.findViewByItemId(itemId);
    }

    public long findItemIdByView(View view) {
        return mExpandableStickyListHeadersAdapter.findItemIdByView(view);
    }

    public void expand(long headerId) {
        if (!mExpandableStickyListHeadersAdapter.isHeaderCollapsed((int) headerId)) {
            return;
        }
        mExpandableStickyListHeadersAdapter.expand((int) headerId);
        //find and expand views in group
        List<View> itemViews = mExpandableStickyListHeadersAdapter.getItemViewsByHeaderId(headerId);
        if (itemViews == null) {
            return;
        }
        for (View view : itemViews) {
            animateView(view, ANIMATION_EXPAND);
        }
    }

    public void collapse(long headerId) {
        if (mExpandableStickyListHeadersAdapter.isHeaderCollapsed((int) headerId)) {
            return;
        }
        mExpandableStickyListHeadersAdapter.collapse((int) headerId);
        //find and hide views with the same header
        List<View> itemViews = mExpandableStickyListHeadersAdapter.getItemViewsByHeaderId(headerId);
        if (itemViews == null) {
            return;
        }
        for (View view : itemViews) {
            animateView(view, ANIMATION_COLLAPSE);
        }
    }

    public boolean isHeaderCollapsed(long headerId) {
        return mExpandableStickyListHeadersAdapter.isHeaderCollapsed((int) headerId);
    }

    public void setAnimExecutor(IAnimationExecutor animExecutor) {
        this.mDefaultAnimExecutor = animExecutor;
    }

    /**
     * Performs either COLLAPSE or EXPAND animation on the target view
     *
     * @param target the view to animate
     * @param type   the animation type, either ExpandCollapseAnimation.COLLAPSE
     *               or ExpandCollapseAnimation.EXPAND
     */
    private void animateView(final View target, final int type) {
        if (ANIMATION_EXPAND == type && target.getVisibility() == VISIBLE) {
            return;
        }
        if (ANIMATION_COLLAPSE == type && target.getVisibility() != VISIBLE) {
            return;
        }
        if (mDefaultAnimExecutor != null) {
            mDefaultAnimExecutor.executeAnim(target, type);
        }

    }

}
