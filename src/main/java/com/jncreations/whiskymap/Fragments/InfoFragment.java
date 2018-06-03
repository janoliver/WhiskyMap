package com.jncreations.whiskymap.Fragments;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import com.jncreations.whiskymap.Helpers.Utils;
import com.jncreations.whiskymap.R;

/**
 * Info Fragment
 */
public class InfoFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_info, container, false);
        WebView info = (WebView)v.findViewById(R.id.info_webview);

        String html_string = getString(R.string.about_html);
        if(Utils.isLite(getActivity()))
            html_string = html_string.replace("{{UPGRADE}}", getString(R.string.upgrade_sentence));
        else
            html_string = html_string.replace("{{UPGRADE}}", "");

        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            html_string = html_string.replace("{{VERSION}}", pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            html_string = html_string.replace("{{VERSION}}", "1.0");
            e.printStackTrace();
        }

        info.loadDataWithBaseURL("file:///android_asset/", html_string, "text/html", "UTF-8", null);

        return v;
    }

}
