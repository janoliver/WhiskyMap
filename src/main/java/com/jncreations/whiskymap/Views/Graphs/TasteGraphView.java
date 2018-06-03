/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jncreations.whiskymap.Views.Graphs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import com.jncreations.whiskymap.Helpers.Typefaces;
import com.jncreations.whiskymap.Models.Whisky;
import com.jncreations.whiskymap.R;

/**
 * This is the Flavor Graph View. It displays whisky sorts on a 2d flavor map and provides
 * zooming, scrolling, tapping, .. capabilities.
 */
public class TasteGraphView extends GraphBaseView {

    // Viewport extremes. See mCurrentViewport for a discussion of the viewport.
    private static final float AXIS_X_MIN = -10f;
    private static final float AXIS_X_MAX = 10f;
    private static final float AXIS_Y_MIN = 0f;
    private static final float AXIS_Y_MAX = 10f;

    // The colors of lines and texts
    protected int mFrontColor = Color.parseColor("#000000");
    protected int mGridLineColor = Color.parseColor("#bbbbbb");
    protected int mFrameLineColor = Color.parseColor("#111111");
    protected int mGridTextColor = Color.parseColor("#555555");
    protected int mHandleTextColor = Color.parseColor("#333333");

    // text, line and handler size settings in dp
    protected float mGridLineThickness = d(1f);
    protected float mFrameLineThickness = d(1f);
    protected float mIndicatorLineThickness = d(4f);
    protected float mHighlightLineThickness = d(2f);
    protected float mGridTextSize = d(12f);
    protected float mHandleTextSize = d(14f);
    protected float mAxesTextSize = d(17f);
    protected float mHandleRadius = d(11f);
    protected float mLabelSeparation = d(7f);

    // some paints for the various elements
    protected Paint mGridLinePaint = new Paint();
    protected Paint mFrameLinePaint = new Paint();
    protected Paint mFrameBackgroundPaint = new Paint();
    protected Paint mHandleTextPaint = new Paint();
    protected Paint mGridTextPaint = new Paint();
    protected Paint mAxesTextPaint = new Paint();
    protected Paint mHighlightPaint = new Paint();
    protected Paint mAxesIndicatorPaint = new Paint();
    protected Paint mAlphaPaint = new Paint();

    // the default number of whisky sorts to be displayed
    protected Integer mNumberWhiskys = 10;

    // text elements
    protected String mXLowLabel = "x";
    protected String mYLabel = "y";
    protected String mXHighLabel = "x";

    OnWhiskyClickListener mCallback = null;

    // this is the container of the registered whiskies as well as those rects, that
    // are currently shown.
    protected SparseArray<Whisky> mWhiskies = new SparseArray<Whisky>();
    protected SparseArray<RectF> mShownRects = new SparseArray<RectF>();
    protected Whisky mHighlight;

    // Buffers and caches used during drawing.
    protected final static int CACHE_FRAME = 1;
    protected final static int CACHE_HIGHLIGHT = 2;
    protected final static int CACHE_GRID = 3;
    protected SparseArray<Bitmap> mCache = new SparseArray<Bitmap>();


    @Override
    protected float getXMin() {
        return AXIS_X_MIN;
    }

    @Override
    protected float getXMax() {
        return AXIS_X_MAX;
    }

    @Override
    protected float getYMin() {
        return AXIS_Y_MIN;
    }

    @Override
    protected float getYMax() {
        return AXIS_Y_MAX;
    }

    /**
     * Implement this interface so a tap on a whisky can execute something
     */
    public interface OnWhiskyClickListener {
        public void onWhiskyClick(Whisky whisky);
    }

    /**
     * Sets the Callback to be called upon tap onto a whisky sort
     * @param listener The OnWhiskyClickListener to use
     */
    public void setOnWhiskyClickListener(OnWhiskyClickListener listener) {
        mCallback = listener;
    }

    public TasteGraphView(Context context) {
        this(context, null, 0);
    }

    public TasteGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TasteGraphView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.TasteGraphView, defStyle, defStyle);

        String temp;
        try {
            mFrontColor = a.getColor(R.styleable.TasteGraphView_axisIndicatorColor, mFrontColor);
            mGridLineColor = a.getColor(R.styleable.TasteGraphView_gridLineColor, mGridLineColor);
            mFrameLineColor = a.getColor(R.styleable.TasteGraphView_frameLineColor,
                    mFrameLineColor);
            mGridTextColor = a.getColor(R.styleable.TasteGraphView_gridTextColor, mGridTextColor);
            mHandleTextColor = a.getColor(R.styleable.TasteGraphView_handleTextColor,
                    mHandleTextColor);
            mHandleRadius = a.getDimension(R.styleable.TasteGraphView_handleRadius, mHandleRadius);
            mHandleTextSize = a.getDimension(
                    R.styleable.TasteGraphView_handleTextSize, mHandleTextSize);
            mAxesTextSize = a.getDimension(R.styleable.TasteGraphView_axesTextSize, mAxesTextSize);
            mGridTextSize = a.getDimension(R.styleable.TasteGraphView_gridTextSize, mGridTextSize);
            mGridLineThickness = a.getDimension(
                    R.styleable.TasteGraphView_gridLineThickness, mGridLineThickness);
            mFrameLineThickness = a.getDimension(
                    R.styleable.TasteGraphView_frameLineThickness, mFrameLineThickness);
            mIndicatorLineThickness = a.getDimension(
                    R.styleable.TasteGraphView_indicatorLineThickness, mIndicatorLineThickness);
            mHighlightLineThickness = a.getDimension(
                    R.styleable.TasteGraphView_highlightLineThickness, mHighlightLineThickness);
            mLabelSeparation = a.getDimension(
                    R.styleable.TasteGraphView_labelSeparation, mLabelSeparation);
            mNumberWhiskys = a.getInt(R.styleable.TasteGraphView_numberWhiskys, mNumberWhiskys);

            temp = a.getString(R.styleable.TasteGraphView_xLowLabel);
            if(temp != null)
                mXLowLabel = temp;

            temp = a.getString(R.styleable.TasteGraphView_xHighLabel);
            if(temp != null)
                mXHighLabel = temp;

            temp = a.getString(R.styleable.TasteGraphView_yLabel);
            if(temp != null)
                mYLabel = temp;
        } finally {
            a.recycle();
        }


        // set up the paints
        mGridLinePaint.setAntiAlias(true);
        mGridLinePaint.setStrokeWidth(mGridLineThickness);
        mGridLinePaint.setColor(mGridLineColor);
        mGridLinePaint.setStyle(Paint.Style.STROKE);

        mFrameLinePaint.setAntiAlias(true);
        mFrameLinePaint.setStrokeWidth(mFrameLineThickness);
        mFrameLinePaint.setColor(mFrameLineColor);
        mFrameLinePaint.setStyle(Paint.Style.STROKE);

        mAxesIndicatorPaint.setAntiAlias(true);
        mAxesIndicatorPaint.setStrokeWidth(mIndicatorLineThickness);
        mAxesIndicatorPaint.setColor(mFrontColor);
        mAxesIndicatorPaint.setStyle(Paint.Style.STROKE);

        mFrameBackgroundPaint.setAntiAlias(true);
        mFrameBackgroundPaint.setColor(Color.parseColor("#ffffff"));
        mFrameBackgroundPaint.setStyle(Paint.Style.FILL);

        mGridTextPaint.setColor(mGridTextColor);
        mGridTextPaint.setAntiAlias(true);
        mGridTextPaint.setStyle(Paint.Style.FILL);
        mGridTextPaint.setTextSize(mGridTextSize);
        mGridTextPaint.setTypeface(Typefaces.get(getContext(), "RobotoCondensed-Light"));

        mHandleTextPaint.setColor(mHandleTextColor);
        mHandleTextPaint.setAntiAlias(true);
        mHandleTextPaint.setStyle(Paint.Style.FILL);
        mHandleTextPaint.setTextSize(mHandleTextSize);
        mHandleTextPaint.setTypeface(Typefaces.get(getContext(), "RobotoCondensed-Regular"));

        mAxesTextPaint.setColor(mGridTextColor);
        mAxesTextPaint.setAntiAlias(true);
        mAxesTextPaint.setStyle(Paint.Style.FILL);
        mAxesTextPaint.setTextSize(mAxesTextSize);
        mAxesTextPaint.setTypeface(Typefaces.get(getContext(), "RobotoSlab-Regular"));

        mHighlightPaint.setColor(mFrontColor);
        mHighlightPaint.setAntiAlias(true);
        mHighlightPaint.setStyle(Paint.Style.FILL);
        mHighlightPaint.setStrokeWidth(mHighlightLineThickness);

    }


    @Override
    protected void constrainViewport() {
        super.constrainViewport();

        // we only allow zoom that keep the aspect ratio
        //mCurrentViewport.right = mCurrentViewport.left + mCurrentViewport.height() * getWidth()/getHeight();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mContentRect.set(
                getPaddingLeft() + i(mAxesTextSize + mLabelSeparation),
                getPaddingTop() + i(mAxesTextSize + mLabelSeparation),
                getWidth() - getPaddingRight(),
                getHeight() - getPaddingBottom());
    }

    /**
     * This function registers a whisky to the class
     * @param whisky The whisky to add to the drawing stack
     */
    public void addWhisky(Whisky whisky) {
        if(mWhiskies.indexOfKey(whisky.getId())<0)
            mWhiskies.put(whisky.getId(), whisky);

        // map indicator not available, so create it
        if(whisky.getMapIndicator() == null) {
            whisky.setMapIndicator(generateMapIndicator(whisky));
        }
    }

    /**
     * Clears all whiskys from the store
     */
    public void clearWhiskys() {
        mWhiskies.clear();
    }

    public int getNumberWhiskys() {
        return mWhiskies.size();
    }

    /**
     * Set the whisky to highlight with the drawHighlight method
     * @param whisky the whisky to highlight
     */
    public void setHighlight(Whisky whisky) {
        mHighlight = whisky;
    }


    /**
     * This function centers the view bounds around the displayed whiskys.
     */
    public void setAutoVisible() {
        setAutoVisible(1f);
    }

    public void setAutoVisible(float offset) {
        Float x_min = null, y_min = null, x_max = null, y_max = null;

        // find min and max values of x and y
        int count = 0;
        Whisky w;
        for(int i = -1; i < mWhiskies.size(); i++) {

            if(i < 0 && mHighlight != null)
                w = mHighlight;
            else if(i >= 0)
                w = mWhiskies.valueAt(i);
            else
                continue;

            float wx_min = w.getTurfSweet().floatValue() - offset;
            float wx_max = w.getTurfSweet().floatValue() + offset;
            float wy_min = w.getSherryWood().floatValue() - offset;
            float wy_max = w.getSherryWood().floatValue() + offset;

            if(x_min == null || wx_min < x_min)
                x_min = wx_min;
            if(x_max == null || wx_max > x_max)
                x_max = wx_max;
            if(y_min == null || wy_min < y_min)
                y_min = wy_min;
            if(y_max == null || wy_max > y_max)
                y_max = wy_max;

            if(mNumberWhiskys > 0 && ++count >= mNumberWhiskys)
                break;
        }

        if(x_min == null || x_max == null || y_min == null || y_max == null)
            return;

        mCurrentViewport.set(x_min, y_min, x_max, y_max);
        updateAlphaPaint();
        invalidate();
    }

    /**
     * This function updates the class's AlphaPaint based on the current scale factor
     */
    public void updateAlphaPaint() {
        int a = i(255 * (getScaleFactorX() - 1));
        if(a < 0) a = 0;
        if(a > 255) a = 255;
        mAlphaPaint.setAlpha(a);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //     Methods and objects related to drawing
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        updateAlphaPaint();

        // Draws axes and text labels
        drawAxes(canvas);
        drawGrid(canvas);

        // Clips the next few drawing operations to the content area
        int clipRestoreCount = canvas.save();
        canvas.clipRect(mContentRect);

        drawWhiskiesUnclipped(canvas);

        // Removes clipping rectangle
        canvas.restoreToCount(clipRestoreCount);

        // Draws chart container
        canvas.drawRect(mContentRect, mFrameLinePaint);

        // draw axis indicators
        int x_i_start = mContentRect.left +
                i((mCurrentViewport.left - AXIS_X_MIN) / (AXIS_X_MAX - AXIS_X_MIN) * mContentRect.width());
        int x_i_len = i(mCurrentViewport.width() / (AXIS_X_MAX - AXIS_X_MIN) * mContentRect.width());
        canvas.drawLine(x_i_start, mContentRect.top, x_i_start + x_i_len, mContentRect.top, mAxesIndicatorPaint);

        int y_i_start = mContentRect.top +
                i((mCurrentViewport.top - AXIS_Y_MIN) / (AXIS_Y_MAX - AXIS_Y_MIN) * mContentRect.height());
        int y_i_len = i(mCurrentViewport.height() / (AXIS_Y_MAX - AXIS_Y_MIN) * mContentRect.height());
        canvas.drawLine(mContentRect.left, y_i_start, mContentRect.left, y_i_start + y_i_len, mAxesIndicatorPaint);
    }

    /**
     * This function generates the map indicator for a whisky
     * @param whisky The whisky to generate the indicator for
     */
    protected Bitmap generateMapIndicator(Whisky whisky) {
        float r = mHandleRadius;

        float size_x = mHandleTextPaint.measureText(whisky.getName()) + 2 * r + d(10);
        float size_y = 2 * r;

        Bitmap b = Bitmap.createBitmap(i(size_x), i(size_y), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        RectF handle_rect = new RectF(0, 0, size_x, size_y);

        // draw the circle, the rounded rect and the name of the whisky
        canvas.drawRoundRect(handle_rect, r, r, mFrameBackgroundPaint);
        canvas.drawRoundRect(handle_rect, r, r, mFrameLinePaint);
        canvas.drawCircle(r, r, r, mHandleTextPaint);
        canvas.drawText(whisky.getName(), 2 * r + d(3), r  + mHandleTextSize / 2 - d(2), mHandleTextPaint);

        return b;
    }

    /**
     * This function draws the hairline grid onto the viewport
     * @param canvas the canvas to draw on
     */
    private void drawGrid(Canvas canvas) {
        if(mCache.indexOfKey(CACHE_GRID) < 0) {
            Bitmap b = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);

            // draw the grid and the line values
            for(float x = mContentRect.left; x < mContentRect.right; x += mContentRect.width() / 5)
                c.drawLine(x, mContentRect.top, x, mContentRect.bottom, mGridLinePaint);

            // draw the grid and the line values
            for(float y = mContentRect.top; y < mContentRect.bottom;
                y += mContentRect.height() / 5 * mContentRect.width() / mContentRect.height())
                c.drawLine(mContentRect.left, y, mContentRect.right, y, mGridLinePaint);

            mCache.put(CACHE_GRID, b);
        }

        canvas.drawBitmap(mCache.get(CACHE_GRID), 0, 0, null);
    }

    /**
     * Draws the chart axes and labels onto the canvas.
     */
    private void drawAxes(Canvas canvas) {

        if(mCache.indexOfKey(CACHE_FRAME) < 0) {
            Bitmap b = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);

            // find out where to start drawing the axis labels
            // they should be exactly in the middle of the free space.
            float offset_y = mContentRect.top + (mContentRect.height() +
                    mAxesTextPaint.measureText(mYLabel)) / 2;
            float start_x = mContentRect.left;
            float end_x = mContentRect.right - mAxesTextPaint.measureText(mXHighLabel);

            c.drawRect(0,0, mContentRect.right, mContentRect.top, mFrameBackgroundPaint);
            c.drawRect(0,0, mContentRect.left, mContentRect.bottom, mFrameBackgroundPaint);

            c.save();
            c.rotate(-90, mAxesTextSize, offset_y);
            c.drawText(mYLabel, mAxesTextSize, offset_y, mAxesTextPaint);
            c.restore();
            c.drawText(mXHighLabel, end_x, mAxesTextSize, mAxesTextPaint);
            c.drawText(mXLowLabel, start_x, mAxesTextSize, mAxesTextPaint);

            mCache.put(CACHE_FRAME, b);
        }

        canvas.drawBitmap(mCache.get(CACHE_FRAME), 0, 0, null);
    }

    /**
     * Draws the whiskies
     */
    private void drawWhiskiesUnclipped(Canvas canvas) {

        mShownRects.clear();
        int count = 0;
        for(int i = 0; i < mWhiskies.size(); i++) {
            count += drawWhisky(mWhiskies.valueAt(i), canvas) ? 1 : 0;
            if(mNumberWhiskys > 0 && count >= mNumberWhiskys)
                break;
        }

        if(mHighlight != null)
            drawHighlight(canvas);

    }

    /**
     * This function draws a whisky, if it should be visible in the current graph bounds, and
     * registers its rect.
     * @param whisky The whisky to draw
     * @param canvas The canvas to draw on
     * @return true, if drawn, false else
     */
    protected Boolean drawWhisky(Whisky whisky, Canvas canvas) {

        // find out the coordinates of the whisky
        float y = coordinateYToPixels(whisky.getSherryWood().floatValue()) - i(mHandleRadius);
        float x = coordinateXToPixels(whisky.getTurfSweet().floatValue()) - i(mHandleRadius);

        // return if not within the current bounds
        if(!isVisible(
                i(x),
                i(y),
                i(x + whisky.getMapIndicator().getWidth()),
                i(y + whisky.getMapIndicator().getHeight())))
            return false;

        if(getScaleFactorX() < 5.f) {
            int r = i(mHandleRadius);
            canvas.drawCircle(x + r, y + r, r, mHandleTextPaint);
            canvas.drawBitmap(whisky.getMapIndicator(), x, y, mAlphaPaint);
            mShownRects.put(whisky.getId(), new RectF(
                    x,
                    y,
                    x + whisky.getMapIndicator().getWidth(),
                    y + whisky.getMapIndicator().getHeight()));
        } else {

            canvas.drawBitmap(whisky.getMapIndicator(), x, y, null);
            mShownRects.put(whisky.getId(), new RectF(
                    x,
                    y,
                    x + whisky.getMapIndicator().getWidth(),
                    y + whisky.getMapIndicator().getHeight()));
        }

        return true;
    }

    /**
     * Draw the highlighted spot of the with setHighlight set whisky inspected whisky
     * @param canvas The canvas to draw on
     */
    protected void drawHighlight(Canvas canvas) {
        int r = i(mHandleRadius + d(4));

        if(mCache.indexOfKey(CACHE_HIGHLIGHT) < 0) {
            Bitmap b = Bitmap.createBitmap(2 * r, 2 * r, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);

            mHighlightPaint.setStyle(Paint.Style.FILL);
            c.drawCircle(r, r, mHandleRadius - d(2), mHighlightPaint);

            mHighlightPaint.setStyle(Paint.Style.STROKE);
            c.drawCircle(r, r, mHandleRadius + d(2), mHighlightPaint);

            mCache.put(CACHE_HIGHLIGHT, b);
        }

        float y = coordinateYToPixels(mHighlight.getSherryWood().floatValue());
        float x = coordinateXToPixels(mHighlight.getTurfSweet().floatValue());

        canvas.drawBitmap(mCache.get(CACHE_HIGHLIGHT), x - r, y - r, null);
    }

    /**
     * Test, whether the coordinates x and y are within the graph boundaries
     * @return true, if visible
     */
    protected boolean isVisible(int x_min, int y_min, int x_max, int y_max) {
        return mContentRect.intersects(x_min, y_min, x_max, y_max);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        int key;
        if(mCallback != null)
            for(int i = mShownRects.size()-1; i >= 0; --i) {
                key = mShownRects.keyAt(i);
                if(mShownRects.get(key).contains(e.getX(), e.getY())) {
                    mCallback.onWhiskyClick(mWhiskies.get(key));
                    break;
                }
            }
        return true;
    }

}
