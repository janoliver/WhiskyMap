package com.jncreations.whiskymap;

import android.os.Bundle;
import com.jncreations.whiskymap.Fragments.DistilleryFragment;

public class DistilleryActivity extends BaseActivity {

    private DistilleryFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragment = (DistilleryFragment)getSupportFragmentManager().findFragmentByTag("distillery");
        if(mFragment == null)
            mFragment = DistilleryFragment.newInstance(mExtras.getInt("id"));

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction().add(R.id.content, mFragment, "distillery")
                    .commit();
        }
    }
}
