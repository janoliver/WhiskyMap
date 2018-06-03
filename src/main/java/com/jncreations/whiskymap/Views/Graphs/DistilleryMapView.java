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
import android.support.v4.util.LongSparseArray;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import com.jncreations.whiskymap.Helpers.Typefaces;
import com.jncreations.whiskymap.Models.Distillery;
import com.jncreations.whiskymap.R;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGBuilder;

import java.util.*;

/**
 * This is the Flavor Graph View. It displays whisky sorts on a 2d flavor map and provides
 * zooming, scrolling, tapping, .. capabilities.
 */
public class DistilleryMapView extends GraphBaseView {

    protected SparseArray<Distillery> mDistilleries = new SparseArray<Distillery>();
    protected SparseArray<RectF> mShownRects = new SparseArray<RectF>();

    protected DistilleryClusterManager mClusterManager = new DistilleryClusterManager();

    // this is magic that I'll never understand again in the future. ever.
    protected static float AXIS_X_MIN = 8420.79248833f;
    protected static float AXIS_X_MAX = 9036.59224583f;
    protected static float AXIS_Y_MAX = -5112.28362480f;
    protected static float AXIS_Y_MIN = -6072.42771795f;
    protected static int WORLD_MAP_WIDTH = 17895;

    protected float mDensity;
    protected float mScalefactor;
    protected float mScaleMin;
    protected float mScaleMax;
    protected float mRatio;
    protected float mPictureScale;

    protected SVG mSvg;
    protected SVG mRegionsSvg;
    protected boolean mRegionsParsed;
    protected Picture mPicture;
    protected Picture mRegionsPicture;

    protected int mFrontColor = Color.parseColor("#ef4933");
    protected int mFrameLineColor = Color.parseColor("#111111");
    protected int mHandleTextColor = Color.parseColor("#333333");

    protected Paint mAxesIndicatorPaint = new Paint();
    protected Paint mFrameLinePaint = new Paint();
    protected Paint mFrameBackgroundPaint = new Paint();
    protected Paint mHandleTextPaint = new Paint();

    protected float mHandleTextSize = d(14f);
    protected float mHandleRadius = d(11f);
    protected float mFrameLineThickness = d(1f);
    protected float mIndicatorLineThickness = d(4f);

    OnDistilleryClickListener mCallback = null;

    /**
     * Implement this interface so a tap on a whisky can execute something
     */
    public interface OnDistilleryClickListener {
        public void onDistilleryClick(Distillery distillery);
    }

    /**
     * Sets the Callback to be called upon tap onto a whisky sort
     * @param listener The OnWhiskyClickListener to use
     */
    public void setOnDistilleryClickListener(OnDistilleryClickListener listener) {
        mCallback = listener;
    }


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

    public DistilleryMapView(Context context) {
        this(context, null, 0);
    }

    public DistilleryMapView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DistilleryMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);


        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.TasteGraphView, defStyle, defStyle);

        try {
            mFrontColor = a.getColor(R.styleable.TasteGraphView_axisIndicatorColor, mFrontColor);
            mFrameLineColor = a.getColor(R.styleable.TasteGraphView_frameLineColor,
                    mFrameLineColor);
            mHandleTextColor = a.getColor(R.styleable.TasteGraphView_handleTextColor,
                    mHandleTextColor);
            mHandleRadius = a.getDimension(R.styleable.TasteGraphView_handleRadius, mHandleRadius);
            mHandleTextSize = a.getDimension(
                    R.styleable.TasteGraphView_handleTextSize, mHandleTextSize);

        } finally {
            a.recycle();
        }

        mDensity = getResources().getDisplayMetrics().density;

        mSvg = new SVGBuilder().readFromResource(getResources(), R.raw.islands).build();

        mPicture = mSvg.getPicture();
        mRatio = (float) mPicture.getWidth()/mPicture.getHeight();

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

        mHandleTextPaint.setColor(mHandleTextColor);
        mHandleTextPaint.setAntiAlias(true);
        mHandleTextPaint.setStyle(Paint.Style.FILL);
        mHandleTextPaint.setTextSize(mHandleTextSize);
        mHandleTextPaint.setTypeface(Typefaces.get(getContext(), "RobotoCondensed-Regular"));


        new Thread(new Runnable() {
            @Override
            public void run() {
                mRegionsSvg = new SVGBuilder().readFromResource(getResources(), R.raw.regions).build();
                mRegionsPicture = mRegionsSvg.getPicture();
                mRegionsParsed = true;
            }
        }).start();

    }

    @Override
    protected void constrainViewport() {
        super.constrainViewport();

        // we only allow zoom that keep the aspect ratio
        mCurrentViewport.right = mCurrentViewport.left + mCurrentViewport.height() * mRatio;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPictureScale = Math.min(
                (float) mContentRect.width() / mPicture.getWidth(),
                (float) mContentRect.height() / mPicture.getHeight());

    }

    /**
     * This function registers a whisky to the class
     * @param distillery The whisky to add to the drawing stack
     */
    public void addDistillery(Distillery distillery) {
        if(mDistilleries.indexOfKey(distillery.getId())<0) {
            mDistilleries.put(distillery.getId(), distillery);
            mClusterManager.addItem(distillery);
        }

        // map indicator not available, so create it
        if(distillery.getMapIndicator() == null) {
            distillery.setMapIndicator(generateMapIndicator(distillery));
        }
    }

    public void clearDistilleries() {
        mDistilleries.clear();
        mClusterManager.clearItems();
    }

    protected float latToPixels(float latitude) {
        final double siny = Math.sin(Math.toRadians(latitude));
        final double y = 0.5 * Math.log((1 + siny) / (1 - siny)) / -(2 * Math.PI) + .5;
        return (float)y * WORLD_MAP_WIDTH;
    }

    protected float lonToPixels(float longitude) {
        return (longitude / 360f + .5f) * WORLD_MAP_WIDTH;
    }

    /**
     * This function draws a whisky, if it should be visible in the current graph bounds, and
     * registers its rect.
     * @param distillery The whisky to draw
     * @param canvas The canvas to draw on
     * @return true, if drawn, false else
     */
    protected Boolean drawDistillery(Distillery distillery, Canvas canvas) {

        // find out the coordinates of the whisky
        float x = coordinateXToPixels(lonToPixels(distillery.getLongitude())) - i(mHandleRadius);
        float y = coordinateYToPixels(-latToPixels(distillery.getLatitude())) - i(mHandleRadius);

        canvas.drawBitmap(distillery.getMapIndicator(), x, y, null);
        mShownRects.put(distillery.getId(), new RectF(
                x,
                y,
                x + distillery.getMapIndicator().getWidth(),
                y + distillery.getMapIndicator().getHeight()));

        return true;
    }

    protected Boolean drawDistilleryPoint(Distillery distillery, Canvas canvas) {

        // find out the coordinates of the whisky
        float x = coordinateXToPixels(lonToPixels(distillery.getLongitude())) - d(4f);
        float y = coordinateYToPixels(-latToPixels(distillery.getLatitude())) - d(4f);

        canvas.drawCircle(x, y, d(4f), mHandleTextPaint);

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //     Methods and objects related to drawing
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        canvas.translate(coordinateXToPixels(AXIS_X_MIN), coordinateYToPixels(AXIS_Y_MAX));
        canvas.scale(getScaleFactorX() * mPictureScale, getScaleFactorY() * mPictureScale);

        if(getScaleFactorX() > 1.5f && mRegionsParsed) {
            canvas.drawPicture(mRegionsPicture);
        }
        else
            canvas.drawPicture(mPicture);
        canvas.restore();

        // Clips the next few drawing operations to the content area
        int clipRestoreCount = canvas.save();
        canvas.clipRect(mContentRect);

        if(getScaleFactorX() > 1.5f && getScaleFactorX() <= 5.f) {
            for (int i = 0; i < mDistilleries.size(); i++)
                drawDistilleryPoint(mDistilleries.valueAt(i), canvas);
        }

        if(getScaleFactorX() > 5.f) {
            // draw distilleries
            mShownRects.clear();
            for (int i = 0; i < mDistilleries.size(); i++)
                drawDistillery(mDistilleries.valueAt(i), canvas);

            /*for(DistilleryCluster c : mClusterManager.getClusters((int)(mXMax - mXMin), (int)(mYMax - mYMin))) {
                canvas.drawCircle(c.getX(), c.getY(), i(mHandleRadius), mFrameLinePaint);
                canvas.drawText(c.getCount() + "", c.getX(), c.getY(), mHandleTextPaint);
            }*/
        }

        // Removes clipping rectangle
        canvas.restoreToCount(clipRestoreCount);

        canvas.drawRect(0, 0, mContentRect.right, mContentRect.bottom, mFrameLinePaint);
    }

    protected Bitmap generateMapIndicator(Distillery distillery) {
        float r = mHandleRadius;

        float size_x = mHandleTextPaint.measureText(distillery.getName()) + 2 * r + d(10);
        float size_y = 2 * r;

        Bitmap b = Bitmap.createBitmap(i(size_x), i(size_y), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        RectF handle_rect = new RectF(0, 0, size_x, size_y);

        // draw the circle, the rounded rect and the name of the whisky
        canvas.drawRoundRect(handle_rect, r, r, mFrameBackgroundPaint);
        canvas.drawRoundRect(handle_rect, r, r, mFrameLinePaint);
        canvas.drawCircle(r, r, r, mHandleTextPaint);
        canvas.drawText(distillery.getName(), 2 * r + d(3), r + mHandleTextSize / 2 - d(2), mHandleTextPaint);

        return b;
    }


    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        int key;
        if(mCallback != null)
            for(int i = mShownRects.size()-1; i >= 0; --i) {
                key = mShownRects.keyAt(i);
                if(mShownRects.get(key).contains(e.getX(), e.getY())) {
                    mCallback.onDistilleryClick(mDistilleries.get(key));
                    break;
                }
            }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int minChartSize = getResources().getDimensionPixelSize(R.dimen.min_chart_size);

        int width = -1;
        int height = -1;

        // when a specific value or match_parent is given, set the width accordingly, or -1
        // otherwise
        if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)
            width = widthSize;

        // when a specific value or match_parent is given, set the height accordingly, or -1
        // otherwise
        if (heightMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.AT_MOST)
            height = heightSize;

        // if only one dimension is specified, make the widget square, otherwise set it do
        // defaultSize
        if(width < 0 && height > 0)
            width = i(height * mRatio);
        else if(height < 0 && width > 0)
            height = i(width / mRatio);
        else if(width < 0 && height < 0) {
            width = minChartSize;
            height = i(width / mRatio);
        } else {
            // width centered
            height = i(width / mRatio);
        }

        // system call.
        setMeasuredDimension(width, height);
    }

    public class DistilleryClusterManager {
        private static final int GRID_SIZE = 5;
        private final Set<Distillery> mItems = Collections.synchronizedSet(new HashSet<Distillery>());

        public void addItem(Distillery item) {
            mItems.add(item);
        }

        public void addItems(Collection<Distillery> items) {
            mItems.addAll(items);
        }

        public void clearItems() {
            mItems.clear();
        }

        public void removeItem(Distillery item) {
            mItems.remove(item);
        }

        public Set<DistilleryCluster> getClusters(int width, int height) {
            long numCells = (long)(GRID_SIZE * getScaleFactorX());
            final int dx = (int)(width / numCells);
            final int dy = (int)(height / numCells);

            HashSet<DistilleryCluster> clusters = new HashSet<DistilleryCluster>();
            LongSparseArray<DistilleryCluster> sparseArray = new LongSparseArray<DistilleryCluster>();

            synchronized (mItems) {
                for (Distillery d : mItems) {

                    double x = Math.floor(coordinateXToPixels(lonToPixels(d.getLongitude())) / dx);
                    double y = Math.floor(coordinateYToPixels(-latToPixels(d.getLatitude())) / dy);

                    long coord = (long) (numCells * x + y);

                    DistilleryCluster cluster = sparseArray.get(coord);
                    if (cluster == null) {
                        cluster = new DistilleryCluster((int)(dx * (x + .5)), (int)(dy * (y + 0.5)));
                        sparseArray.put(coord, cluster);
                        clusters.add(cluster);
                    }

                    cluster.addDistillery(d);
                }
            }

            return clusters;
        }

        public Collection<Distillery> getItems() {
            return mItems;
        }
    }

    private static class DistilleryCluster {
        private ArrayList<Distillery> mDistilleries = new ArrayList<Distillery>();
        private int mX;
        private int mY;

        public DistilleryCluster(int x, int y) {
            mX = x;
            mY = y;
        }

        public int getX() {
            return mX;
        }

        public int getY() {
            return mY;
        }

        public void addDistillery(Distillery d) {
            mDistilleries.add(d);
        }

        public List<Distillery> getDistilleries() {
            return mDistilleries;
        }

        public int getCount() {
            return mDistilleries.size();
        }
    }

}
