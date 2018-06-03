package com.jncreations.whiskymap.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.jncreations.whiskymap.*;
import com.jncreations.whiskymap.Helpers.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * The App Menu
 */
public class SidebarFragment extends BaseFragment {

    protected ListView mMenuListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sidebar, container, false);

        mMenuListView = (ListView) v.findViewById(R.id.sidebar_menu_list);

        String[] navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        TypedArray navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        ArrayList<Pair<Integer, String>> menuEntries = new ArrayList<Pair<Integer, String>>();

        int length = navMenuTitles.length;
        if(!Utils.isLite(getActivity()))
            length = length - 1;

        for(int i = 0; i < length; ++i)
            menuEntries.add(new Pair<Integer, String>(navMenuIcons.getResourceId(i, -1), navMenuTitles[i]));

        MenuListAdapter adapter = new MenuListAdapter(
                getActivity(), R.layout.listitem_sidebar_menu, menuEntries);
        mMenuListView.setAdapter(adapter);

        mMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(i == 0) {
                    navigateTo(MapActivity.class);
                }
                if(i == 1) {
                    navigateTo(ListActivity.class);
                }
                if(i == 2) {
                    navigateTo(DistilleryMapActivity.class);
                }
                if(i == 3) {
                    navigateTo(DistilleryListActivity.class);
                }
                if(i == 4) {
                    navigateTo(BarActivity.class);
                }
                if(i == 5) {
                    navigateTo(SettingsActivity.class);
                }
                if(i == 6) {
                    navigateTo(InfoActivity.class);
                }
                if(i == 7) {
                    // this is only needed for the lite version. However, since I do not want
                    // to keep a separate version of this file, it is kept here. Ignore it, if
                    // you want.
                    String url = "https://play.google.com/store/apps/details?id=com.jncreations.whiskymap";
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            }
        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public class MenuListAdapter extends ArrayAdapter<Pair<Integer, String>> {

        public MenuListAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        private List<Pair<Integer, String>> items;

        public MenuListAdapter(Context context, int resource, List<Pair<Integer, String>> items) {
            super(context, resource, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.listitem_sidebar_menu, null);
            }

            Pair<Integer, String> p = items.get(position);

            Drawable image = getResources().getDrawable(p.first);

            TextView name = (TextView) v.findViewById(R.id.name);
            name.setCompoundDrawablesWithIntrinsicBounds(image, null, null, null);
            name.setText(p.second);

            return v;
        }
    }
}
