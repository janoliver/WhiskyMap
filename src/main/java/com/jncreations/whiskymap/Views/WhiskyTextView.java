package com.jncreations.whiskymap.Views;

import android.content.Context;
import android.util.AttributeSet;
import com.jncreations.whiskymap.Helpers.Typefaces;

/**
 * A custom view for the TextView, so we can use our own font
 */
public class WhiskyTextView extends LetterSpacingTextView {
    public WhiskyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public WhiskyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WhiskyTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if(!isInEditMode()) {
            setTypeface(Typefaces.get(getContext(), "whisky"));
        }
        setText("whisky");
        setLetterSpacing(-4);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // a fix for the font we use, which is slightly too large.
        setMeasuredDimension((int) (getMeasuredWidth() * 1.1), getMeasuredHeight());
    }
}
