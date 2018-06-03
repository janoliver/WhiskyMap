package com.jncreations.whiskymap;

import android.os.Bundle;
import com.jncreations.whiskymap.BaseActivity;
import com.jncreations.whiskymap.Fragments.DistilleryListFragment;
import com.jncreations.whiskymap.R;

public class DistilleryListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new DistilleryListFragment())
                .commit();

    }
}
