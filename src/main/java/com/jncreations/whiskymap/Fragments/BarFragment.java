package com.jncreations.whiskymap.Fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.jncreations.whiskymap.DetailActivity;
import com.jncreations.whiskymap.Models.Country;
import com.jncreations.whiskymap.Models.Whisky;
import com.jncreations.whiskymap.R;

import java.util.ArrayList;
import java.util.List;


/**
 * The Whisky List
 */
public class BarFragment extends BaseFragment {
    protected ExpandableListView mList;
    protected List<Whisky> mWhiskyList = new ArrayList<Whisky>();
    protected List<ArrayList<Whisky>> mBuckets = new ArrayList<ArrayList<Whisky>>() {{
        add(new ArrayList<Whisky>());
        add(new ArrayList<Whisky>());
        add(new ArrayList<Whisky>());
        add(new ArrayList<Whisky>());
    }};
    protected WhiskyListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bar, container, false);
        mList = (ExpandableListView)v.findViewById(R.id.list_content);
        mList.setGroupIndicator(null);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.actionmenu_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add:
                BarDialogFragment d = BarDialogFragment.newInstance(true);
                d.setTargetFragment(this, 0);
                d.show(getFragmentManager(), "bar_dialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mWhiskyList = mDatabase.getBarWhiskys();

        for(Whisky w: mWhiskyList) {
            if(w.getBarOwns())
                mBuckets.get(0).add(w);
            if(w.getBarOwned())
                mBuckets.get(1).add(w);
            if(w.getBarTasted())
                mBuckets.get(2).add(w);
            if(w.getBarWants())
                mBuckets.get(3).add(w);
        }

        mList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                if(id >= 0 && id < 1000) {
                    Bundle extras = new Bundle();
                    extras.putInt("id", (int)id);
                    navigateTo(DetailActivity.class, extras);

                } else {
                    BarDialogFragment d = BarDialogFragment.newInstance(true, (int)id);
                    d.setTargetFragment(BarFragment.this, 0);
                    d.show(getFragmentManager(), "bar_dialog");
                    return true;
                }
                return true;
            }
        });

        mAdapter = new WhiskyListAdapter();
        mList.setAdapter(mAdapter);

        mList.expandGroup(0);
        mList.expandGroup(1);
        mList.expandGroup(2);
        mList.expandGroup(3);

    }

    public void refresh() {
        mDatabase.clearCaches();
        mBuckets.get(0).clear();
        mBuckets.get(1).clear();
        mBuckets.get(2).clear();
        mBuckets.get(3).clear();
        mWhiskyList = mDatabase.getBarWhiskys();

        for(Whisky w: mWhiskyList) {
            if(w.getBarOwns())
                mBuckets.get(0).add(w);
            if(w.getBarOwned())
                mBuckets.get(1).add(w);
            if(w.getBarTasted())
                mBuckets.get(2).add(w);
            if(w.getBarWants())
                mBuckets.get(3).add(w);
        }

        mAdapter.notifyDataSetChanged();
    }

    private class WhiskyListAdapter extends BaseExpandableListAdapter {
        public Object getChild(int groupPosition, int childPosition) {
            if(groupPosition == 0 && mBuckets.get(0).size() > childPosition)
                return mBuckets.get(0).get(childPosition);
            else if(groupPosition == 1 && mBuckets.get(1).size() > childPosition)
                return mBuckets.get(1).get(childPosition);
            else if(groupPosition == 2 && mBuckets.get(2).size() > childPosition)
                return mBuckets.get(2).get(childPosition);
            else if(groupPosition == 3 && mBuckets.get(3).size() > childPosition)
                return mBuckets.get(3).get(childPosition);
            return null;
        }

        public long getChildId(int groupPosition, int childPosition) {
            Whisky w = (Whisky)getChild(groupPosition, childPosition);
            if(w == null)
                return -1;
            else
                return w.getId();
        }

        public int getChildrenCount(int groupPosition) {
            int size = 0;
            if(groupPosition == 0)
                size = mBuckets.get(0).size();
            else if(groupPosition == 1)
                size = mBuckets.get(1).size();
            else if(groupPosition == 2)
                size = mBuckets.get(2).size();
            else if(groupPosition == 3)
                size = mBuckets.get(3).size();

            if(size == 0)
                size++;
            return size;
        }

        public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                                 View convertView, ViewGroup parent) {

            Whisky whisky = (Whisky) getChild(groupPosition, childPosition);

            if(whisky == null) {
                return getActivity().getLayoutInflater().inflate(R.layout.listitem_whisky_empty, null);
            }

            View row = getActivity().getLayoutInflater().inflate(R.layout.listitem_whisky, null);

            // set the name, striked if closed
            TextView title = (TextView) row.findViewById(R.id.title);
            title.setText(whisky.getName());

            // set the subtitle
            if(!whisky.isCustom()) {
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

            } else {
                TextView subtitle = (TextView) row.findViewById(R.id.subtitle);
                subtitle.setText(String.format("%s", whisky.getBarNotes()));

                // set image
                ImageView country = (ImageView) row.findViewById(R.id.country_image);
                country.setImageDrawable(getResources().getDrawable(R.drawable.nowhere));
            }

            // ratingbar
            RatingBar rating_bar = (RatingBar) row.findViewById(R.id.rating_bar);
            rating_bar.setRating(whisky.getRating());

            // set notes icon
            if(whisky.hasBarNotes())
                row.findViewById(R.id.notes_icon).setVisibility(View.VISIBLE);

            // set separator
            if(isLastChild) {
                row.findViewById(R.id.separator).setVisibility(View.INVISIBLE);
            }

            return row;

        }

        public Object getGroup(int groupPosition) {
            if(groupPosition == 0)
                return getString(R.string.dialog_bar_owns);
            else if(groupPosition == 1)
                return getString(R.string.dialog_bar_owned);
            else if(groupPosition == 2)
                return getString(R.string.dialog_bar_tasted);
            else if(groupPosition == 3)
                return getString(R.string.dialog_bar_wants);
            return null;
        }

        public int getGroupCount() {
            return 4;
        }

        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                                 ViewGroup parent) {
            View row = getActivity().getLayoutInflater().inflate(R.layout.listitem_bar_category, null);

            String cat = (String) getGroup(groupPosition);

            TextView name = (TextView) row.findViewById(R.id.text_name);
            name.setText(cat);

            return row;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public boolean hasStableIds() {
            return true;
        }

    }
}
