package com.jncreations.whiskymap.Models;

import java.util.ArrayList;

/**
 * Tag Model
 */
public class Region {
    private Integer mId;
    private String mName;
    private Country mCountry;
    private ArrayList<Distillery> mDistilleries = new ArrayList<Distillery>();

    public Integer getId() {
        return mId;
    }

    public void setId(Integer id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public ArrayList<Distillery> getDistilleries() {
        return mDistilleries;
    }

    public void setDistilleries(ArrayList<Distillery> distilleries) {
        mDistilleries = distilleries;
    }

    public void addDistillery(Distillery distillery) {
        mDistilleries.add(distillery);
    }

    public Country getCountry() {
        return mCountry;
    }

    public void setCountry(Country country) {
        mCountry = country;
    }



}
