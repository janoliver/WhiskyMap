package com.jncreations.whiskymap;

import android.content.res.Configuration;
import android.os.Bundle;
import com.jncreations.whiskymap.Fragments.DistilleryListFragment;
import com.jncreations.whiskymap.Fragments.DistilleryMapFragment;

public class DistilleryMapActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new DistilleryMapFragment())
                .commit();

        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content2, new DistilleryListFragment())
                    .commit();
        }

    }

    @Override
    public int getLayout() {
        if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE) {
            return R.layout.activity_two_fragments;
        } else {
            return R.layout.activity_single_fragment;
        }
    }
}
