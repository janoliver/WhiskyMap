package com.jncreations.whiskymap.Fragments;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.jncreations.whiskymap.DistilleryActivity;
import com.jncreations.whiskymap.Models.Distillery;
import com.jncreations.whiskymap.R;
import com.jncreations.whiskymap.Views.Graphs.DistilleryMapView;

/**
 * The Map Fragment
 */
public class DistilleryMapFragment extends BaseFragment {

    protected DistilleryMapView mMap;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_distillery_map, container, false);

        // set up the flavor graph
        mMap = ((DistilleryMapView)v.findViewById(R.id.map));

        mMap.setOnDistilleryClickListener(new DistilleryMapView.OnDistilleryClickListener() {
            @Override
            public void onDistilleryClick(Distillery distillery) {
                Bundle extras = new Bundle();
                extras.putInt("id", distillery.getId());
                navigateTo(DistilleryActivity.class, extras);
            }
        });

        mMap.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int key = 0;
        SparseArray<Distillery> dists = mDatabase.getDistilleries();
        for(int i = 0; i < dists.size(); i++) {
            key = dists.keyAt(i);
            mMap.addDistillery(dists.get(key));
        }
    }
}
