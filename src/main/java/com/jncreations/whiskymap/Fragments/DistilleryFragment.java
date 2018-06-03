package com.jncreations.whiskymap.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.jncreations.whiskymap.DetailActivity;
import com.jncreations.whiskymap.Helpers.Typefaces;
import com.jncreations.whiskymap.Models.Distillery;
import com.jncreations.whiskymap.Models.Whisky;
import com.jncreations.whiskymap.R;
import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * The whisky detail fragment
 */
public class DistilleryFragment extends BaseFragment
{
    protected Distillery mDistillery;

    private DistilleryPagerAdapter mAdapter;
    private ViewPager mPager;


    public static DistilleryFragment newInstance(int id) {
        DistilleryFragment f = new DistilleryFragment();

        Bundle args = new Bundle();
        args.putInt("id", id);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        mDistillery = mDatabase.getDistilleryById(getArguments().getInt("id"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_distillery, container, false);

        mAdapter = new DistilleryPagerAdapter(getFragmentManager(), getArguments(), getActivity());
        mPager = (ViewPager)v.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        TitlePageIndicator titleIndicator = (TitlePageIndicator)v.findViewById(R.id.titles);
        titleIndicator.setViewPager(mPager);
        titleIndicator.setTypeface(Typefaces.get(getActivity(), "RobotoCondensed-Light"));

        ((TextView)v.findViewById(R.id.title)).setText(String.format("%s %s",
                mDistillery.getName(), getString(R.string.distillery)));
        ((TextView)v.findViewById(R.id.subtitle)).setText(String.format("%s, %s",
                mDistillery.getRegion().getName(), getString(mDistillery.getRegion().getCountry().getName())));

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    public static class DistilleryPagerAdapter extends FragmentPagerAdapter {

        private SparseArray<Fragment> mRegisteredFragments = new SparseArray<Fragment>();
        private Bundle mArgs;
        private Context mContext;

        public DistilleryPagerAdapter(FragmentManager fm, Bundle args, Context cx) {
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
                default:
                    f = new ProductsListFragment();
                    break;
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
                    title = mContext.getString(R.string.tab_products);
                    break;
            }
            return title;
        }

    }

    public static class DistilleryPageFragment extends BaseFragment {
        protected Distillery mDistillery;

        static DistilleryPageFragment newInstance(int num) {
            DistilleryPageFragment f = new DistilleryPageFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mDistillery = mDatabase.getDistilleryById(getArguments().getInt("id"));
        }
    }


    public static class ProductsListFragment extends DistilleryFragment.DistilleryPageFragment {
        protected ListView mListView;
        protected List<Whisky> mWhiskyList = new ArrayList<Whisky>();

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_distillery_list, container, false);

            mListView = (ListView)v.findViewById(R.id.products);

            return v;
        }


        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setRetainInstance(true);

            mWhiskyList = mDatabase.getWhiskyListByDistillery(mDistillery);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Bundle extras = new Bundle();
                    extras.putInt("id", mWhiskyList.get(i).getId());
                    navigateTo(DetailActivity.class, extras);
                }
            });

            WhiskyListAdapter adapter = new DistilleryFragment.WhiskyListAdapter(getActivity(), mWhiskyList);
            mListView.setAdapter(adapter);
        }

    }

    private static class WhiskyListAdapter extends ArrayAdapter<Whisky> {

        private final Context mContext;

        public WhiskyListAdapter(Context context, List<Whisky> values) {
            super(context, R.layout.listitem_distillery_whisky, values);
            mContext = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = inflater.inflate(R.layout.listitem_distillery_whisky, null);
            Whisky whisky = getItem(position);

            TextView title = (TextView) row.findViewById(R.id.title);
            title.setText(whisky.getName());

            // set bar indicator
            if(whisky.isInBar()) {
                ImageButton bar_button = (ImageButton) row.findViewById(R.id.button_bar);
                bar_button.setVisibility(View.VISIBLE);
                bar_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //navigateTo(BarActivity.class);
                    }
                });
            }

            return row;
        }

    }

}
