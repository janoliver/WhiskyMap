package com.jncreations.whiskymap;

import android.os.Bundle;
import com.jncreations.whiskymap.Fragments.InfoFragment;
import com.jncreations.whiskymap.Fragments.ListFragment;
import com.jncreations.whiskymap.R;

public class ListActivity extends BaseActivity {

    public Class ATTACHED_FRAGMENT = InfoFragment.class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, new ListFragment())
                .commit();

    }
}
