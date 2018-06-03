package com.jncreations.whiskymap.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.widget.ListView;
import com.jncreations.whiskymap.R;

public class FastscrollThemedListView extends ListView {

    public FastscrollThemedListView(Context context, AttributeSet attrs) {
        super(new ContextThemeWrapper(context, R.style.FastscrollThemedListView), attrs);
    }
}