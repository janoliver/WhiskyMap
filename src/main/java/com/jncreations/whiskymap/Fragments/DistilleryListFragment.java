package com.jncreations.whiskymap.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.*;
import android.widget.*;
import com.jncreations.whiskymap.DistilleryActivity;
import com.jncreations.whiskymap.Models.Country;
import com.jncreations.whiskymap.Models.Distillery;
import com.jncreations.whiskymap.R;
import com.jncreations.whiskymap.Views.FastscrollThemedListView;

import java.util.*;


/**
 * The Whisky List
 */
public class DistilleryListFragment extends BaseFragment {
    protected FastscrollThemedListView mList;
    protected List<Distillery> mDistilleryList = new ArrayList<Distillery>();
    protected List<Distillery> mFilteredData = new ArrayList<Distillery>();

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.actionmenu_list, menu);

        MenuItem searchItem = menu.findItem(R.id.filter);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // change searchview looks
        // see here: http://stackoverflow.com/questions/11085308/changing-the-background-drawable-of-the-searchview-widget
        View searchPlate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchPlate.setBackgroundResource(R.drawable.textfield_searchview_holo_dark);

        // set the on text change listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                ((DistilleryListAdapter)mList.getAdapter()).getFilter().filter(newText);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_distillerylist, container, false);
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mDistilleryList = mDatabase.getDistilleryListByName();
        mList = (FastscrollThemedListView)getView().findViewById(R.id.list_content);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle extras = new Bundle();
                extras.putInt("id", mFilteredData.get(i).getId());
                navigateTo(DistilleryActivity.class, extras);
            }
        });
        mList.setFastScrollEnabled(true);

        DistilleryListAdapter adapter = new DistilleryListAdapter();
        mList.setAdapter(adapter);

    }

    private class DistilleryListAdapter extends BaseAdapter implements Filterable, SectionIndexer {

        HashMap<String, Integer> mAlphaIndexer = new HashMap<String, Integer>();
        String[] mSections;

        public DistilleryListAdapter() {
            mFilteredData = mDistilleryList;
            initIndex();
        }

        public void initIndex() {
            mAlphaIndexer.clear();

            for(int i = mFilteredData.size()-1; i > 0; --i)
                mAlphaIndexer.put(mFilteredData.get(i).getName().substring(0, 1).toUpperCase(), i);

            Set<String> keys = mAlphaIndexer.keySet();

            Iterator<String> it = keys.iterator();
            ArrayList<String> keyList = new ArrayList<String>();

            while (it.hasNext()) {
                String key = it.next();
                keyList.add(key);
            }
            Collections.sort(keyList);
            mSections = new String[keyList.size()];
            keyList.toArray(mSections);
        }

        public int getCount() {
            return mFilteredData.size();
        }

        public Object getItem(int position) {
            return mFilteredData.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = getActivity().getLayoutInflater().inflate(R.layout.listitem_distillery_indicator, null);
            Distillery distillery = (Distillery)getItem(position);

            // set the name, striked if closed
            TextView title = (TextView) row.findViewById(R.id.title);
            title.setText(String.format("%s %s",
                    distillery.getName(),
                    getString(R.string.distillery)));

            // set the subtitle
            TextView region = (TextView) row.findViewById(R.id.region);
            region.setText(String.format("%s, %s",
                    distillery.getRegion().getName(),
                    getString(distillery.getRegion().getCountry().getName())));

            // set image
            ImageView country = (ImageView) row.findViewById(R.id.country_image);
            country.setImageDrawable(getResources().getDrawable(
                    Country.sDrawables.get(distillery.getRegion().getCountry().getId())));

            return row;
        }

        @Override
        public Filter getFilter()
        {
            return new Filter()
            {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence)
                {
                    FilterResults results = new FilterResults();

                    //If there's nothing to filter on, return the original data for your list
                    if(charSequence == null || charSequence.length() == 0)
                    {
                        results.values = mDistilleryList;
                        results.count = mDistilleryList.size();
                    }
                    else
                    {
                        ArrayList<Distillery> filterResultsData = new ArrayList<Distillery>();

                        for(Distillery distillery : mDistilleryList)
                            if(distillery.getName().toLowerCase().contains(charSequence.toString().toLowerCase()))
                                filterResultsData.add(distillery);

                        results.values = filterResultsData;
                        results.count = filterResultsData.size();
                    }

                    return results;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults)
                {
                    mFilteredData = (ArrayList<Distillery>)filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

        @Override
        public Object[] getSections() {
            return mSections;
        }

        @Override
        public int getPositionForSection(int sectionIndex) {
            return mAlphaIndexer.get(mSections[sectionIndex]);
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }
    }
}
