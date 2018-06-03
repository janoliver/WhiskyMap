package com.jncreations.whiskymap.Models;

import android.graphics.Bitmap;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Distillery model
 */
public class Distillery {
    private static SparseArray<Bitmap> sMapIndicators = new SparseArray<Bitmap>();

    private Integer mId;
    private String mName;
    private String mDescription;
    private Region mRegion;
    private Double mLatitude;
    private Double mLongitude;
    private ArrayList<Whisky> mWhiskies = new ArrayList<Whisky>();

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public ArrayList<Whisky> getWhiskies() {
        return mWhiskies;
    }

    public void setWhiskies(ArrayList<Whisky> whiskies) {
        mWhiskies = whiskies;
    }

    public void addWhisky(Whisky whisky) {
        mWhiskies.add(whisky);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public Region getRegion() {
        return mRegion;
    }

    public void setRegion(Region region) {
        mRegion = region;
    }

    public float getLatitude() {
        return mLatitude.floatValue();
    }

    public void setLatitude(Double latitude) {
        mLatitude = latitude;
    }

    public float getLongitude() {
        return mLongitude.floatValue();
    }

    public void setLongitude(Double longitude) {
        mLongitude = longitude;
    }

    public void setMapIndicator(Bitmap b) {
        sMapIndicators.put(mId, b);
    }

    public Bitmap getMapIndicator() {
        return sMapIndicators.get(mId);
    }


    public static class NameComparator implements Comparator<Distillery> {
        @Override
        public int compare(Distillery o1, Distillery o2) {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    }

}
