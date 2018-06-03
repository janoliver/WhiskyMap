package com.jncreations.whiskymap.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.*;
import android.widget.*;
import com.jncreations.whiskymap.BarActivity;
import com.jncreations.whiskymap.DetailActivity;
import com.jncreations.whiskymap.DistilleryActivity;
import com.jncreations.whiskymap.Helpers.Typefaces;
import com.jncreations.whiskymap.Models.Country;
import com.jncreations.whiskymap.Models.Whisky;
import com.jncreations.whiskymap.R;
import com.jncreations.whiskymap.Views.Graphs.TasteGraphView;
import com.jncreations.whiskymap.Views.TasteIndicatorDrawable;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;

/**
 * The whisky detail fragment
 */
public class WhiskyFragment extends BaseFragment
{
    protected Whisky mWhisky;

    private View mRoot;
    private WhiskyPagerAdapter mAdapter;
    private ViewPager mPager;


    public static WhiskyFragment newInstance(int id) {
        WhiskyFragment f = new WhiskyFragment();

        Bundle args = new Bundle();
        args.putInt("id", id);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mWhisky = mDatabase.getWhiskyById(getArguments().getInt("id"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mRoot = inflater.inflate(R.layout.fragment_whisky, null);

        mAdapter = new WhiskyPagerAdapter(getFragmentManager(), getArguments(), getActivity());
        mPager = (ViewPager)mRoot.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        TitlePageIndicator titleIndicator = (TitlePageIndicator)mRoot.findViewById(R.id.titles);
        titleIndicator.setViewPager(mPager);
        titleIndicator.setTypeface(Typefaces.get(getActivity(), "RobotoCondensed-Light"));

        ((TextView)mRoot.findViewById(R.id.title)).setText(mWhisky.getName().toUpperCase());
        ((TextView)mRoot.findViewById(R.id.subtitle)).setText(String.format("%s %s, %s",
                mWhisky.getDistillery().getName(),
                getString(R.string.distillery),
                getString(mWhisky.getDistillery().getRegion().getCountry().getName())));

        return mRoot;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.actionmenu_whisky, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.bar:
                BarDialogFragment d = BarDialogFragment.newInstance(false);
                d.setTargetFragment(this, 0);
                d.show(getFragmentManager(), "bar_dialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Whisky getWhisky() {
        return mWhisky;
    }

    public void setWhisky(Whisky w) {
        mWhisky = w;
        GeneralInfoFragment f = (GeneralInfoFragment)mAdapter.getFragmentAt(0);
        if(f != null)
            f.refresh(w);
    }

    public static class WhiskyPagerAdapter extends FragmentPagerAdapter {

        private SparseArray<Fragment> mRegisteredFragments = new SparseArray<Fragment>();
        private Bundle mArgs;
        private Context mContext;

        public WhiskyPagerAdapter(FragmentManager fm, Bundle args, Context cx) {
            super(fm);

            mContext = cx;
            mArgs = args;
        }

        public Fragment getFragmentAt(int index) {
            return mRegisteredFragments.get(index);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f;
            switch(position) {
                case 0:
                    f = new GeneralInfoFragment();
                    break;
                case 1:
                    f = new TasteFragment();
                    break;
                case 2:
                default:
                    f = new SurroundingFragment();
                    break;
                /*case 3:
                    f = new SimilarFragment();
                    break;*/
            }

            f.setArguments(mArgs);
            return f;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            mRegisteredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mRegisteredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        @Override
        public CharSequence getPageTitle(int position){
            String title = "";
            switch(position){
                case 0:
                    title = mContext.getString(R.string.tab_info);
                    break;
                case 1:
                    title = mContext.getString(R.string.tab_taste);
                    break;
                case 2:
                    title = mContext.getString(R.string.tab_surrounding);
                    break;
                case 3:
                    title = mContext.getString(R.string.tab_similar);
                    break;
            }
            return title;
        }

    }


    public static class WhiskyPageFragment extends BaseFragment {
        protected Whisky mWhisky;

        static WhiskyPageFragment newInstance(int num) {
            WhiskyPageFragment f = new WhiskyPageFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mWhisky = mDatabase.getWhiskyById(getArguments().getInt("id"));
        }
    }

    public static class GeneralInfoFragment extends WhiskyFragment.WhiskyPageFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
            View v = inflater.inflate(R.layout.fragment_general_info, container, false);

            ((TextView)v.findViewById(R.id.whisky_age)).setText(mWhisky.getStringAge());
            ((TextView)v.findViewById(R.id.whisky_distillery)).setText(mWhisky.getDistillery().getName());
            ((TextView)v.findViewById(R.id.whisky_country)).setText(
                    getString(mWhisky.getDistillery().getRegion().getCountry().getName()));
            ((TextView)v.findViewById(R.id.whisky_region)).setText(
                    mWhisky.getDistillery().getRegion().getName());
            ((TextView)v.findViewById(R.id.whisky_vol)).setText(mWhisky.getVol().toString() + "%");

            ((TextView)v.findViewById(R.id.bar_owns)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sidebar_bar, 0, 0, 0);
            ((TextView)v.findViewById(R.id.bar_owned)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sidebar_bar, 0, 0, 0);
            ((TextView)v.findViewById(R.id.bar_tasted)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sidebar_bar, 0, 0, 0);
            ((TextView)v.findViewById(R.id.bar_wants)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sidebar_bar, 0, 0, 0);
            ((TextView)v.findViewById(R.id.bar_rating_text)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_sidebar_bar, 0, 0, 0);
            ((TextView)v.findViewById(R.id.bar_notes)).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_notes, 0, 0, 0);

            ((ImageButton) v.findViewById(R.id.button_arrow_distillery)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle extras = new Bundle();
                    extras.putInt("id", mWhisky.getDistillery().getId());
                    navigateTo(DistilleryActivity.class, extras);
                }
            });

            if(mWhisky.getBarOwns())
                v.findViewById(R.id.bar_owns).setVisibility(View.VISIBLE);

            if(mWhisky.getBarOwned())
                v.findViewById(R.id.bar_owned).setVisibility(View.VISIBLE);

            if(mWhisky.getBarTasted())
                v.findViewById(R.id.bar_tasted).setVisibility(View.VISIBLE);

            if(mWhisky.getBarWants())
                v.findViewById(R.id.bar_wants).setVisibility(View.VISIBLE);

            if(mWhisky.isInBar()) {
                v.findViewById(R.id.bar_rating).setVisibility(View.VISIBLE);
                ((RatingBar)v.findViewById(R.id.bar_rating_bar)).setRating(mWhisky.getRating());
            }

            if(mWhisky.hasBarNotes()) {
                v.findViewById(R.id.bar_notes).setVisibility(View.VISIBLE);
                ((TextView)v.findViewById(R.id.bar_notes)).setText(mWhisky.getBarNotes());
            }

            return v;
        }

        public void refresh(Whisky w) {
            mWhisky = w;

            if(mWhisky.getBarOwns())
                getView().findViewById(R.id.bar_owns).setVisibility(View.VISIBLE);
            else
                getView().findViewById(R.id.bar_owns).setVisibility(View.GONE);

            if(mWhisky.getBarOwned())
                getView().findViewById(R.id.bar_owned).setVisibility(View.VISIBLE);
            else
                getView().findViewById(R.id.bar_owned).setVisibility(View.GONE);

            if(mWhisky.getBarTasted())
                getView().findViewById(R.id.bar_tasted).setVisibility(View.VISIBLE);
            else
                getView().findViewById(R.id.bar_tasted).setVisibility(View.GONE);

            if(mWhisky.getBarWants())
                getView().findViewById(R.id.bar_wants).setVisibility(View.VISIBLE);
            else
                getView().findViewById(R.id.bar_wants).setVisibility(View.GONE);

            if(mWhisky.isInBar()) {
                getView().findViewById(R.id.bar_rating).setVisibility(View.VISIBLE);
                ((RatingBar)getView().findViewById(R.id.bar_rating_bar)).setRating(mWhisky.getRating());
            } else {
                getView().findViewById(R.id.bar_rating).setVisibility(View.GONE);
            }

            if(mWhisky.hasBarNotes()) {
                getView().findViewById(R.id.bar_notes).setVisibility(View.VISIBLE);
                ((TextView)getView().findViewById(R.id.bar_notes)).setText(mWhisky.getBarNotes());
            } else {
                getView().findViewById(R.id.bar_notes).setVisibility(View.GONE);
            }
        }
    }

    public static class TasteFragment extends WhiskyFragment.WhiskyPageFragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
            View v = inflater.inflate(R.layout.fragment_taste, container, false);

            ((RatingBar)v.findViewById(R.id.whisky_turf)).setRating((float) (0.5 * mWhisky.getTurf()));
            ((RatingBar)v.findViewById(R.id.whisky_fruit)).setRating((float) (0.5 * mWhisky.getFruitFloralFeinty()));
            ((RatingBar)v.findViewById(R.id.whisky_intensity)).setRating((float) (0.5 * mWhisky.getIntensity()));
            ((RatingBar)v.findViewById(R.id.whisky_sherry)).setRating((float) (0.5 * mWhisky.getSherryWood()));

            if(v.findViewById(R.id.graph) != null)
                ((TasteIndicatorDrawable)v.findViewById(R.id.graph)).setValues(
                        (float)(0.5 * mWhisky.getSherryWood()), (float)(0.5 * mWhisky.getFruitFloralFeinty()),
                        (float)(0.5 * mWhisky.getIntensity()), (float)(0.5 * mWhisky.getTurf())
                );

            return v;
        }
    }

    public static class SurroundingFragment extends WhiskyFragment.WhiskyPageFragment {

        private int mNumberNeighbors = 10;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
            View v = inflater.inflate(R.layout.fragment_surrounding, container, false);

            if(v.findViewById(R.id.taste_graph) != null)
                ((TasteIndicatorDrawable)v.findViewById(R.id.taste_graph)).setValues(
                        (float)(0.5 * mWhisky.getSherryWood()), (float)(0.5 * mWhisky.getFruitFloralFeinty()),
                        (float)(0.5 * mWhisky.getIntensity()), (float)(0.5 * mWhisky.getTurf())
                );

            TasteGraphView graph = ((TasteGraphView)v.findViewById(R.id.graph));

            for(Whisky w: mWhisky.getNeighbors(mDatabase.getWhiskyList(), mNumberNeighbors))
                if(!w.isCustom())
                    graph.addWhisky(w);

            graph.setHighlight(mWhisky);

            graph.setOnWhiskyClickListener(new TasteGraphView.OnWhiskyClickListener() {
                @Override
                public void onWhiskyClick(Whisky whisky) {
                    Bundle extras = new Bundle();
                    extras.putInt("id", whisky.getId());
                    navigateTo(DetailActivity.class, extras);
                }
            });

            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);

            final TasteGraphView graph_view = (TasteGraphView)getView().findViewById(R.id.graph);
            ViewTreeObserver viewTreeObserver = graph_view.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        // this is to prevent readjustment of the scale after all other
                        // whiskys are added.
                        if(graph_view.getNumberWhiskys() <= mNumberNeighbors)
                            graph_view.setAutoVisible(.1f);

                        // now add all the others!
                        for(Whisky w: mDatabase.getWhiskyList())
                            if(!w.getId().equals(mWhisky.getId()) && !w.isCustom())
                                graph_view.addWhisky(w);
                    }
                });
            }

            graph_view.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });
        }
    }

    public static class SimilarFragment extends WhiskyFragment.WhiskyPageFragment {
        private ListView mListView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved) {
            View v = inflater.inflate(R.layout.fragment_similar, container, false);
            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mListView = (ListView)getView().findViewById(R.id.list);

            //WhiskyListAdapter adapter = new WhiskyListAdapter(mWhisky.getSimilar());
            //mListView.setAdapter(adapter);
        }

        private class WhiskyListAdapter extends BaseAdapter {

           private ArrayList<Whisky> mSimilar;

            public WhiskyListAdapter(ArrayList<Whisky> similar) {
                mSimilar = similar;
            }

            public int getCount() {
                return mSimilar.size();
            }

            public Object getItem(int position) {
                return mSimilar.get(position);
            }

            public long getItemId(int position) {
                return position;
            }

            public View getView(int position, View convertView, ViewGroup parent) {
                View row = getActivity().getLayoutInflater().inflate(R.layout.listitem_whisky_indicator, null);
                Whisky whisky = (Whisky)getItem(position);

                // set the name, striked if closed
                TextView title = (TextView) row.findViewById(R.id.title);
                title.setText(whisky.getName());

                // set the subtitle
                TextView subtitle = (TextView) row.findViewById(R.id.subtitle);
                String distillery_string = getActivity().getString(R.string.distillery);
                subtitle.setText(String.format("%s %s", whisky.getDistillery().getName(), distillery_string));

                // set the subtitle
                TextView region = (TextView) row.findViewById(R.id.region);
                region.setText(String.format("%s, %s",
                        whisky.getDistillery().getRegion().getName(),
                        getString(whisky.getDistillery().getRegion().getCountry().getName())));

                // set image
                ImageView country = (ImageView) row.findViewById(R.id.country_image);
                country.setImageDrawable(getResources().getDrawable(
                        Country.sDrawables.get(whisky.getDistillery().getRegion().getCountry().getId())));

                // set bar indicator
                if(whisky.isInBar()) {
                    ImageButton bar_button = (ImageButton) row.findViewById(R.id.button_bar);
                    bar_button.setVisibility(View.VISIBLE);
                    bar_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            navigateTo(BarActivity.class);
                        }
                    });
                }

                return row;
            }

        }
    }

}
