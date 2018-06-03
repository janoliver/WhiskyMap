package com.jncreations.whiskymap.Models;

import com.jncreations.whiskymap.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Tag Model
 */
public class Country {
    public static final Map<Integer, Integer> sDrawables;
    static
    {
        sDrawables = new HashMap<Integer, Integer>();
        sDrawables.put(1, R.drawable.scotland);
        sDrawables.put(2, R.drawable.ireland);
    }

    public static final Map<Integer, Integer> sNames;
    static
    {
        sNames = new HashMap<Integer, Integer>();
        sNames.put(1, R.string.scotland);
        sNames.put(2, R.string.ireland);
    }

    private Integer mId;
    //private String mName;
    private ArrayList<Region> mRegions = new ArrayList<Region>();

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public Integer getName() {
        return sNames.get(mId);
    }

    /*public void setName(String name) {
        mName = name;
    }*/

    public ArrayList<Region> getRegions() {
        return mRegions;
    }

    public void setRegions(ArrayList<Region> regions) {
        mRegions = regions;
    }

    public void addRegion(Region region) {
        mRegions.add(region);
    }

    
}
