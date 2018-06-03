package com.jncreations.whiskymap.Fragments;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.*;
import android.widget.*;
import com.jncreations.whiskymap.DetailActivity;
import com.jncreations.whiskymap.Models.Whisky;
import com.jncreations.whiskymap.R;
import com.jncreations.whiskymap.Views.Graphs.TasteGraphView;

import java.util.ArrayList;
import java.util.List;

/**
 * The Map Fragment
 */
public class MapFragment extends BaseFragment implements Filterable, AdapterView.OnItemSelectedListener {

    protected TasteGraphView mFlavorGraph;
    protected List<Whisky> mWhiskyList = new ArrayList<Whisky>();
    protected List<Whisky> mFilteredData = new ArrayList<Whisky>();

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);

        // set up the spinner
        Spinner spinner = (Spinner) v.findViewById(R.id.map_choices);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.map_display_choices, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // set up the flavor graph
        mFlavorGraph = ((TasteGraphView)v.findViewById(R.id.graph));

        mFlavorGraph.setOnWhiskyClickListener(new TasteGraphView.OnWhiskyClickListener() {
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.actionmenu_map, menu);

        MenuItem searchItem = menu.findItem(R.id.filter);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // change searchview looks
        // see here: http://stackoverflow.com/questions/11085308/changing-the-background-drawable-of-the-searchview-widget
        View searchPlate = searchView.findViewById(R.id.search_plate);
        searchPlate.setBackgroundResource(R.drawable.textfield_searchview_holo_dark);

        // set the on text change listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                getFilter().filter(newText);
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });
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
                    //graph_view.setAutoVisible();
                }
            });
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        mFlavorGraph.clearWhiskys();
        if(pos == 0) {
            mWhiskyList = mDatabase.getWhiskyList();
        } else {
            mWhiskyList = mDatabase.getBarWhiskys();
        }
        for(Whisky w: mWhiskyList)
            if(!w.isCustom())
                mFlavorGraph.addWhisky(w);
        mFlavorGraph.invalidate();
        mFlavorGraph.setAutoVisible();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // nothing
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
                    results.values = mWhiskyList;
                    results.count = mWhiskyList.size();
                }
                else
                {
                    ArrayList<Whisky> filterResultsData = new ArrayList<Whisky>();

                    for(Whisky whisky : mWhiskyList)
                        if(whisky.getName().toLowerCase().contains(charSequence.toString().toLowerCase()))
                            filterResultsData.add(whisky);

                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults)
            {
                mFilteredData = (ArrayList<Whisky>)filterResults.values;
                mFlavorGraph.clearWhiskys();

                if(mFilteredData.size() == 0)
                    mFlavorGraph.invalidate();
                else {
                    for(Whisky w: mFilteredData) {
                        if(!w.isCustom())
                            mFlavorGraph.addWhisky(w);
                    }
                    mFlavorGraph.invalidate();

                    mFlavorGraph.setAutoVisible();
                }
            }
        };
    }
}
