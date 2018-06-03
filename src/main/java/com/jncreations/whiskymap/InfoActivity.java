package com.jncreations.whiskymap;

import android.os.Bundle;
import com.jncreations.whiskymap.Fragments.InfoFragment;
import com.jncreations.whiskymap.R;

public class InfoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new InfoFragment())
                .commit();

    }
}
