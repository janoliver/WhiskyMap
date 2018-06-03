package com.jncreations.whiskymap;

import android.os.Bundle;
import com.jncreations.whiskymap.Fragments.MapFragment;
import com.jncreations.whiskymap.R;

public class MapActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new MapFragment())
                .commit();

    }
}
