package com.example.doctorsbuilding.nav.Util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

/**
 * Created by hossein on 7/24/2016.
 */
public class NonScrollExpanableListView extends ExpandableListView {
    public NonScrollExpanableListView(Context context) {
        super(context);
    }

    public NonScrollExpanableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NonScrollExpanableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureSpec_custom = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec_custom);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}
