package com.jncreations.whiskymap.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.util.AttributeSet;
import android.view.View;
import com.jncreations.whiskymap.R;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

public class TasteIndicatorDrawable extends View {

    private List<Drawable> mDrawables = new ArrayList<Drawable>();
    private int mFrontColor = Color.parseColor("#000000");
    private int mLineColor = Color.parseColor("#111111");

    private int mSize = 400;
    private float mX1, mX2, mY1, mY2;

    public TasteIndicatorDrawable(Context context) {
        super(context);
        init(null);
    }

    public TasteIndicatorDrawable(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public TasteIndicatorDrawable(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TasteIndicatorView);

        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.TasteIndicatorView_frontColor:
                    mFrontColor = a.getColor(attr, Color.parseColor("#000000"));
                    break;
                case R.styleable.TasteIndicatorView_lineColor:
                    mLineColor = a.getColor(attr, Color.parseColor("#333333"));
                    break;
            }
        }
        a.recycle();
    }

    private ShapeDrawable addArc(int quarter, float start, float end) {
        ShapeDrawable d = new ShapeDrawable(new ArcShape(quarter * 90,90));

        switch (quarter) {
            case 0: case 2:
                d.setBounds(
                        round(mSize * (1 - start) / 2),
                        round(mSize * (1 - end) / 2),
                        round(mSize * (1 + start) / 2),
                        round(mSize * (1 + end) / 2)
                );
                break;
            case 1: case 3:
                d.setBounds(
                        round(mSize * (1 - end) / 2),
                        round(mSize * (1 - start) / 2),
                        round(mSize * (1 + end) / 2),
                        round(mSize * (1 + start) / 2)
                );
                break;
        }

        d.getPaint().setColor(mFrontColor);

        return d;
    }

    public void setValues(float x1, float y1, float x2, float y2) {
        mX1 = x1;
        mX2 = x2;
        mY1 = y1;
        mY2 = y2;
    }

    protected void onDraw(Canvas canvas) {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        paint.setColor(mLineColor);
        paint.setStyle(Paint.Style.STROKE);

        canvas.drawLine(0, mSize/2, mSize, mSize/2, paint);
        canvas.drawLine(mSize/2, 0, mSize / 2, mSize, paint);

        // draw the lines
        for(float r = 1; r < 6; r += 1.) {
            GradientDrawable d = new GradientDrawable();
            d.setShape(GradientDrawable.OVAL);
            d.setColor(Color.TRANSPARENT);
            d.setBounds(round(mSize * (5 - r) / (float)10),
                    round(mSize * (5 - r) / (float)10),
                    round(mSize * (5 + r) / (float)10),
                    round(mSize * (5 + r) / (float)10));
            d.setStroke(1, mLineColor);
            mDrawables.add(d);
        }

        mDrawables.add(addArc(0, (float)(mX1 / 5.0), (float)(mY1 / 5.0)));
        mDrawables.add(addArc(1, (float)(mY1 / 5.0), (float)(mX2 / 5.0)));
        mDrawables.add(addArc(2, (float)(mX2 / 5.0), (float)(mY2 / 5.0)));
        mDrawables.add(addArc(3, (float)(mY2 / 5.0), (float)(mX1 / 5.0)));

        for(Drawable d : mDrawables)
            d.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        mSize = Math.min(width, height);

        setMeasuredDimension(mSize, mSize);
    }



}
