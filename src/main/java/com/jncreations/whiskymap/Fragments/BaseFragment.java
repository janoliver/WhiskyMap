package com.jncreations.whiskymap.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import com.jncreations.whiskymap.BaseActivity;
import com.jncreations.whiskymap.Helpers.DataBaseHelper;

/**
 * The Base class for a fragment
 */
public class BaseFragment extends Fragment {

    protected DataBaseHelper mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = new DataBaseHelper(getActivity());
    }

    protected void navigateTo(Class activity) {
        ((BaseActivity)getActivity()).navigateTo(activity);
    }

    protected void navigateTo(Class activity, Bundle extras) {
        ((BaseActivity)getActivity()).navigateTo(activity, extras);
    }

    protected void setSubtitle(String subtitle) {
        ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(subtitle);
    }

    protected void setSubtitle(int subtitle) {
        ((ActionBarActivity)getActivity()).getSupportActionBar().setSubtitle(subtitle);
    }
}
