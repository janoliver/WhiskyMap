package com.jncreations.whiskymap.Helpers;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;
import com.jncreations.whiskymap.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oli on 6/29/13.
 */
public class Utils {

    public static final String LOG_TAG = "WhiskyApp";

    public static void showToast(Context context, String message) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }

    public static void log(String msg) {
        Log.v(Utils.LOG_TAG, msg);
    }

    public static int getAppVersion(Context c) throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
        return pInfo.versionCode;
    }

    public static String inputStreamToString(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }

        bufferedReader.close();
        return stringBuilder.toString();
    }

    public static <C> List<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        List<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

    public static boolean isLite(Context c) {
        return c.getResources().getBoolean(R.bool.is_lite);
    }

    public static class Tuple<X, Y> {
        public final X x;
        public final Y y;
        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }

}
