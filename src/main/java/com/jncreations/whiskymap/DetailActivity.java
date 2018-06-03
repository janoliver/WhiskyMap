package com.jncreations.whiskymap;

import android.os.Bundle;
import com.jncreations.whiskymap.Fragments.WhiskyFragment;

public class DetailActivity extends BaseActivity {

    private WhiskyFragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFragment = (WhiskyFragment)getSupportFragmentManager().findFragmentByTag("whisky");
        if(mFragment == null)
            mFragment = WhiskyFragment.newInstance(mExtras.getInt("id"));

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction().add(R.id.content, mFragment, "whisky")
                    .commit();
        }
    }
}
