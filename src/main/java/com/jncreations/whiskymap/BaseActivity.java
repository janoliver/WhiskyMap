package com.jncreations.whiskymap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import com.jncreations.whiskymap.Fragments.SidebarFragment;

public class BaseActivity extends ActionBarActivity {

    protected SharedPreferences mSettings;
    protected Bundle mExtras;
    protected SidebarFragment mSidebar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
        //    Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler());
        //}

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        // center the whisky text
        final View abView = getSupportActionBar().getCustomView().findViewById(R.id.whisky_string);
        ViewTreeObserver viewTreeObserver = abView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    abView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                    DisplayMetrics metrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(metrics);

                    int marginLeft = (metrics.widthPixels - abView.getWidth()) / 2;
                    abView.setPadding(marginLeft, 0, 0, 0);
                }
            });
        }

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mExtras = getIntent().getExtras();

        setContentView(getLayout());

        mSidebar = (SidebarFragment)getSupportFragmentManager().findFragmentByTag("sidebar");
        if(mSidebar == null)
            mSidebar = new SidebarFragment();

        // add the fragments
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.sidebar_container, mSidebar, "sidebar")
                    .commit();
        }
    }

    public int getLayout() {
        return R.layout.activity_single_fragment;
    }

    public void navigateTo(Class activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    public void navigateTo(Class activity, Bundle extras) {
        Intent intent = new Intent(this, activity);
        intent.putExtras(extras);
        startActivity(intent);
    }
}
