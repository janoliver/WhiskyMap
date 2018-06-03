package com.jncreations.whiskymap.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.CheckedTextView;
import com.jncreations.whiskymap.Helpers.Typefaces;
import com.jncreations.whiskymap.R;

/**
 * A custom view for the TextView, so we can use our own font
 */
public class CustomCheckedTextView extends CheckedTextView {

    private String mFontName = "RobotoSlab-Regular";

    public CustomCheckedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public CustomCheckedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomCheckedTextView(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextView);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.CustomTextView_assetFontFamiliy:
                    mFontName = a.getString(attr);
                    break;
            }
        }
        a.recycle();

        if(!isInEditMode()) {
            setTypeface(Typefaces.get(getContext(), mFontName));
        }
    }
}
