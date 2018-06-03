package com.jncreations.whiskymap.Models;

import android.graphics.Bitmap;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The Whisky Model
 */
public class Whisky {
    private static SparseArray<Bitmap> sMapIndicators = new SparseArray<Bitmap>();

    private Integer mId;
    private Distillery mDistillery;
    private String mName;
    private String mDescription;
    private Integer mAge;
    private Double mVol;
    private Double mFruitFloralFeinty;
    private Double mIntensity;
    private Double mSherryWood;
    private Double mTurf;
    private Integer mPopularity;
    private Boolean mIsCustom = false;

    // bar features
    private Boolean mBarOwns = false;
    private Boolean mBarOwned = false;
    private Boolean mBarWants = false;
    private Boolean mBarTasted = false;
    private Float mRating = 0f;
    private String mBarNotes = "";

    public Integer getPopularity() {
        return mPopularity;
    }

    public void setPopularity(Integer popularity) {
        mPopularity = popularity;
    }

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public Distillery getDistillery() {
        return mDistillery;
    }

    public void setDistillery(Distillery distillery) {
        mDistillery = distillery;
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

    public Integer getAge() {
        return mAge;
    }

    public String getStringAge() {
        if(mAge < 0)
            return "-";
        return mAge.toString();
    }

    public void setAge(Integer age) {
        mAge = age;
    }

    public Double getVol() {
        return mVol;
    }

    public void setVol(Double vol) {
        mVol = vol;
    }

    public Double getIntensity() {
        return mIntensity;
    }

    public void setIntensity(Double intensity) {
        mIntensity = intensity;
    }

    public Double getTurf() {
        return mTurf;
    }

    public void setTurf(Double turf) {
        mTurf = turf;
    }

    public Double getFruitFloralFeinty() {
        return mFruitFloralFeinty;
    }

    public void setFruitFloralFeinty(Double fruitFloralFeinty) {
        mFruitFloralFeinty = fruitFloralFeinty;
    }

    public Double getSherryWood() {
        return mSherryWood;
    }

    public void setSherryWood(Double sherryWood) {
        mSherryWood = sherryWood;
    }

    public Double getTurfSweet() {
        return getTurf() - getFruitFloralFeinty();
    }

    public Boolean getBarOwns() {
        return mBarOwns;
    }

    public void setBarOwns(Boolean barOwns) {
        mBarOwns = barOwns;
    }

    public Boolean getBarOwned() {
        return mBarOwned;
    }

    public void setBarOwned(Boolean barOwned) {
        mBarOwned = barOwned;
    }

    public Boolean getBarWants() {
        return mBarWants;
    }

    public void setBarWants(Boolean barWants) {
        mBarWants = barWants;
    }

    public Boolean isCustom() {
        return mIsCustom;
    }

    public void setIsCustom(Boolean custom) {
        mIsCustom = custom;
    }

    public Boolean getBarTasted() {
        return mBarTasted;
    }

    public void setBarTasted(Boolean barTasted) {
        mBarTasted = barTasted;
    }

    public String getBarNotes() {
        return mBarNotes;
    }

    public void setBarNotes(String barNotes) {
        mBarNotes = barNotes;
    }

    public boolean hasBarNotes() {
        return mBarNotes != null && mBarNotes.compareTo("") != 0;
    }

    public Boolean isInBar() {
        return mBarOwned || mBarOwns || mBarTasted || mBarWants;
    }

    public Float getRating() {
        return mRating;
    }

    public void setRating(Float rating) {
        mRating = rating;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return getName();
    }

    public List<Whisky> getNeighbors(List<Whisky> all, float max_dist) {
        List<Whisky> res = new ArrayList<Whisky>();
        for(Whisky w: all)
            if(w.distance(this) < max_dist)
                res.add(w);
        return res;
    }

    public class WhiskyDistanceComparator implements Comparator<Whisky> {
        private Whisky mWhisky;

        public WhiskyDistanceComparator(Whisky w) {
            mWhisky = w;
        }

        @Override
        public int compare(Whisky x, Whisky y) {
            if(x.distance(mWhisky) < y.distance(mWhisky))
                return -1;
            if(x.distance(mWhisky) > y.distance(mWhisky))
                return 1;
            return 0;
        }
    }

    public List<Whisky> getNeighbors(List<Whisky> all, int number) {
        Collections.sort(all, new WhiskyDistanceComparator(this));
        return all.subList(1,number+1);
    }

    public Double distance(Whisky whisky) {
        if(isCustom() || whisky.isCustom())
            return 1000.;
        return Math.abs(Math.sqrt(
                Math.pow(whisky.getSherryWood() - getSherryWood(),2) +
                Math.pow(whisky.getTurfSweet() - getTurfSweet(),2)));
    }

    public void setMapIndicator(Bitmap b) {
        sMapIndicators.put(mId, b);
    }

    public Bitmap getMapIndicator() {
        return sMapIndicators.get(mId);
    }

    public static class NameComparator implements Comparator<Whisky> {
        @Override
        public int compare(Whisky o1, Whisky o2) {
            return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
        }
    }

    public static class PopularityComparator implements Comparator<Whisky> {
        @Override
        public int compare(Whisky o1, Whisky o2) {
            if(o2.getPopularity() == null || o1.getPopularity() == null)
                return 0;
            return o2.getPopularity().compareTo(o1.getPopularity());
        }
    }
}
