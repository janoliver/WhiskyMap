package com.jncreations.whiskymap;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;
import android.text.format.DateFormat;
import android.widget.Toast;
import com.jncreations.whiskymap.Helpers.DataBaseHelper;

import java.io.*;
import java.util.Date;

/**
 * The App Menu
 */
public class SettingsFragment extends PreferenceFragment {
    private static int PICK_FILE = 1337;

    protected DataBaseHelper mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = new DataBaseHelper(getActivity());

        addPreferencesFromResource(R.xml.preferences);

        Preference importPref = findPreference("pref_key_import");
        importPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.setType("text/plain");
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FILE);
                return true;
            }
        });

        Preference exportPref = findPreference("pref_key_export");
        exportPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(isExternalStorageWritable()) {

                    String fname = getExportFileName();

                    File exportDirectory = new File(Environment.getExternalStorageDirectory(), "WhiskyMap/");
                    exportDirectory.mkdirs();
                    File file = new File(exportDirectory, fname);
                    FileOutputStream fos;
                    byte[] data = mDatabase.getUserDataSQL().getBytes();
                    try {
                        fos = new FileOutputStream(file);
                        fos.write(data);
                        fos.flush();
                        fos.close();
                        Toast.makeText(getActivity(), String.format(getString(R.string.message_exported), file.getAbsolutePath()), Toast.LENGTH_LONG).show();

                    } catch (FileNotFoundException e) {
                        Toast.makeText(getActivity(), getString(R.string.message_exported_failure), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), getString(R.string.message_exported_failure), Toast.LENGTH_LONG).show();
                    }

                }
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FILE) {
            if(resultCode == Activity.RESULT_OK) {
                if(isExternalStorageReadable()) {
                    try {
                        if(mDatabase.setUserDataSQL(getStringFromFile(data.getData()))) {
                            Toast.makeText(getActivity(), getString(R.string.message_imported), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.message_imported_failure), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), getString(R.string.message_imported_failure), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }


    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public String getStringFromFile(Uri uri) throws Exception {
        InputStream fin = getActivity().getContentResolver().openInputStream(uri);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

    public String getExportFileName() {
        String d = DateFormat.format("yyyy-MM-dd_hh-mm", new Date()).toString();
        return "whisky_map_export_" + d + ".sql";
    }
}
