package com.jncreations.whiskymap.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.SparseArray;
import com.jncreations.whiskymap.Models.Country;
import com.jncreations.whiskymap.Models.Distillery;
import com.jncreations.whiskymap.Models.Region;
import com.jncreations.whiskymap.Models.Whisky;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class DataBaseHelper {

    private SQLiteDatabase mDataBase;
    private SQLiteDatabase mUserDataBase;

    SparseArray<Whisky> mWhiskies;
    SparseArray<Distillery> mDistilleries;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context
     */
    public DataBaseHelper(Context context) {
        WhiskyDatabaseOpenHelper helper = WhiskyDatabaseOpenHelper.getInstance(context);
        mDataBase = helper.getReadableDatabase();

        UserDatabaseOpenHelper user_helper = UserDatabaseOpenHelper.getInstance(context);
        mUserDataBase = user_helper.getWritableDatabase();
    }

    public void clearCaches() {
        mWhiskies = null;
        mDistilleries = null;
    }

    public SparseArray<Whisky> getWhiskies() {
        if(mWhiskies == null) {

            mWhiskies = new SparseArray<Whisky>();

            // select the my bar content
            SparseArray<BarTuple> bar_content = new SparseArray<BarTuple>();
            Cursor bar_c = mUserDataBase.rawQuery(
                    "select is_custom, whisky_id, owns, owned, tasted, rating, wants, notes, whisky_name from bar", null);
            bar_c.moveToFirst();
            while (!bar_c.isAfterLast()) {
                BarTuple n = new BarTuple(
                        bar_c.getInt(bar_c.getColumnIndex("is_custom")) != 0,
                        bar_c.getInt(bar_c.getColumnIndex("owns")) != 0,
                        bar_c.getInt(bar_c.getColumnIndex("owned")) != 0,
                        bar_c.getInt(bar_c.getColumnIndex("tasted")) != 0,
                        bar_c.getInt(bar_c.getColumnIndex("wants")) != 0,
                        bar_c.getFloat(bar_c.getColumnIndex("rating")),
                        bar_c.getString(bar_c.getColumnIndex("notes")),
                        bar_c.getString(bar_c.getColumnIndex("whisky_name"))
                );
                bar_content.put(bar_c.getInt(bar_c.getColumnIndex("whisky_id")), n);

                if(n.is_custom) {
                    Whisky whisky = new Whisky();
                    whisky.setIsCustom(true);
                    whisky.setId(bar_c.getInt(bar_c.getColumnIndex("whisky_id")));
                    whisky.setName(n.name);
                    whisky.setBarOwns(n.owns);
                    whisky.setBarOwned(n.owned);
                    whisky.setBarTasted(n.tasted);
                    whisky.setBarWants(n.wants);
                    whisky.setRating(n.rating);
                    whisky.setBarNotes(n.notes);

                    mWhiskies.append(bar_c.getInt(bar_c.getColumnIndex("whisky_id")), whisky);
                }
                bar_c.moveToNext();
            }
            bar_c.close();

            Cursor c = mDataBase.rawQuery("select " +
                    "w._id as wid, w.name as wname, w.description as wdescription," +
                    "w.age as wage, w.vol as wvol, w.fruitfloralfeinty as wfruitfloralfeinty, " +
                    "w.intensity as wintensity, w.sherrywood as wsherrywood, w.turf as wturf, " +
                    "w.popularity as wpopularity, " +
                    "d._id as did, d.name as dname, d.description as ddescription, " +
                    "d.latitude as dlatitude, d.longitude as dlongitude, " +
                    "r.name as rname, r._id as rid, c.name as cname, c._id as cid " +
                    "from whisky w left join distillery d on w.distillery_id=d._id " +
                    "left join region r on d.region_id=r._id " +
                    "left join country c on r.country_id=c._id " +
                    "order by w.name asc", null);
            c.moveToFirst();
            while (!c.isAfterLast()) {

                Country country = new Country();
                country.setId(c.getInt(c.getColumnIndex("cid")));

                Region region = new Region();
                region.setId(c.getInt(c.getColumnIndex("rid")));
                region.setName(c.getString(c.getColumnIndex("rname")));
                region.setCountry(country);

                Distillery distillery = new Distillery();
                distillery.setId(c.getInt(c.getColumnIndex("did")));
                distillery.setName(c.getString(c.getColumnIndex("dname")));
                distillery.setDescription(c.getString(c.getColumnIndex("ddescription")));
                distillery.setLatitude(c.getDouble(c.getColumnIndex("dlatitude")));
                distillery.setLongitude(c.getDouble(c.getColumnIndex("dlongitude")));
                distillery.setRegion(region);

                Whisky whisky = new Whisky();
                whisky.setId(c.getInt(c.getColumnIndex("wid")));
                whisky.setName(c.getString(c.getColumnIndex("wname")));
                whisky.setDescription(c.getString(c.getColumnIndex("wdescription")));
                whisky.setAge(c.getInt(c.getColumnIndex("wage")));
                whisky.setVol(c.getDouble(c.getColumnIndex("wvol")));
                whisky.setFruitFloralFeinty(c.getDouble(c.getColumnIndex("wfruitfloralfeinty")));
                whisky.setIntensity(c.getDouble(c.getColumnIndex("wintensity")));
                whisky.setSherryWood(c.getDouble(c.getColumnIndex("wsherrywood")));
                whisky.setTurf(c.getDouble(c.getColumnIndex("wturf")));
                whisky.setPopularity(c.getInt(c.getColumnIndex("wpopularity")));
                whisky.setDistillery(distillery);

                if(bar_content.indexOfKey(whisky.getId()) >= 0) {
                    BarTuple t = bar_content.get(whisky.getId());
                    whisky.setBarOwns(t.owns);
                    whisky.setBarOwned(t.owned);
                    whisky.setBarTasted(t.tasted);
                    whisky.setBarWants(t.wants);
                    whisky.setRating(t.rating);
                    whisky.setBarNotes(t.notes);
                }

                mWhiskies.append(whisky.getId(), whisky);
                c.moveToNext();
            }

            c.close();
        }

        return mWhiskies;
    }

    public String getUserDataSQL() {
        StringBuilder builder = new StringBuilder();
        Cursor bar_c = mUserDataBase.rawQuery(
                "select is_custom, whisky_id, owns, owned, tasted, rating, wants, notes, whisky_name from bar", null);
        bar_c.moveToFirst();
        while (!bar_c.isAfterLast()) {
            builder.append(String.format(Locale.ENGLISH,
                            "INSERT INTO bar (is_custom, whisky_id, owns, owned, tasted, wants, rating, notes, whisky_name) " +
                                    "VALUES (%d, %d, %d, %d, %d, %d, %f, %s, %s);\n",
                    bar_c.getInt(bar_c.getColumnIndex("is_custom")),
                    bar_c.getInt(bar_c.getColumnIndex("whisky_id")),
                    bar_c.getInt(bar_c.getColumnIndex("owns")),
                    bar_c.getInt(bar_c.getColumnIndex("owned")),
                    bar_c.getInt(bar_c.getColumnIndex("tasted")),
                    bar_c.getInt(bar_c.getColumnIndex("wants")),
                    bar_c.getFloat(bar_c.getColumnIndex("rating")),
                    getEscapedString(bar_c.getString(bar_c.getColumnIndex("notes"))),
                    getEscapedString(bar_c.getString(bar_c.getColumnIndex("whisky_name")))
                    ));
            bar_c.moveToNext();
        }
        bar_c.close();

        return builder.toString();
    }

    protected String getEscapedString(String notes) {
        if(notes == null)
            return "null";
        else
            return DatabaseUtils.sqlEscapeString(notes);
    }

    public boolean setUserDataSQL(String raw_sql) {
        mUserDataBase.beginTransaction();
        try {
            mUserDataBase.execSQL("delete from bar");
            String[] queries = raw_sql.split(";\n");
            for(String query : queries) {
                mUserDataBase.execSQL(query);
            }
            mUserDataBase.setTransactionSuccessful();
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            mUserDataBase.endTransaction();
        }
        return true;
    }

    public SparseArray<Distillery> getDistilleries() {
        if(mDistilleries == null) {

            mDistilleries = new SparseArray<Distillery>();
            Cursor c = mDataBase.rawQuery("select " +
                    "(SELECT COUNT(*) FROM whisky WHERE distillery_id=d._id) as wcount, " +
                    "d._id as did, d.name as dname, d.description as ddescription, " +
                    "d.latitude as dlatitude, d.longitude as dlongitude, " +
                    "r.name as rname, r._id as rid, c.name as cname, c._id as cid " +
                    "from distillery d left join region r on d.region_id=r._id " +
                    "left join country c on r.country_id=c._id where wcount > 0", null);
            c.moveToFirst();
            while (!c.isAfterLast()) {

                Country country = new Country();
                country.setId(c.getInt(c.getColumnIndex("cid")));
                //country.setName(c.getString(c.getColumnIndex("cname")));

                Region region = new Region();
                region.setId(c.getInt(c.getColumnIndex("rid")));
                region.setName(c.getString(c.getColumnIndex("rname")));
                region.setCountry(country);

                Distillery distillery = new Distillery();
                distillery.setId(c.getInt(c.getColumnIndex("did")));
                distillery.setName(c.getString(c.getColumnIndex("dname")));
                distillery.setDescription(c.getString(c.getColumnIndex("ddescription")));
                distillery.setLatitude(c.getDouble(c.getColumnIndex("dlatitude")));
                distillery.setLongitude(c.getDouble(c.getColumnIndex("dlongitude")));
                distillery.setRegion(region);

                mDistilleries.append(distillery.getId(), distillery);
                c.moveToNext();
            }

            c.close();
        }

        return mDistilleries;
    }

    public List<Whisky> getWhiskyList() {
        List<Whisky> w = Utils.asList(getWhiskies());
        Collections.sort(w, new Whisky.PopularityComparator());
        return w;
    }

    public List<Whisky> getWhiskyListByName() {
        List<Whisky> w =  Utils.asList(getWhiskies());
        Collections.sort(w, new Whisky.NameComparator());
        return w;
    }

    public List<Whisky> getWhiskyListByDistillery(Distillery distillery) {
        List<Whisky> w =  Utils.asList(getWhiskies());
        List<Whisky> ret = new ArrayList<Whisky>();
        for(Whisky whisky : w) {
            if(whisky.getDistillery() != null && whisky.getDistillery().getId().equals(distillery.getId())) {
                ret.add(whisky);
            }
        }
        Collections.sort(ret, new Whisky.NameComparator());
        return ret;
    }

    public Whisky getWhiskyById(Integer id) {
        return getWhiskies().get(id);
    }

    public Distillery getDistilleryById(Integer id) {
        return getDistilleries().get(id);
    }


    public List<Distillery> getDistilleryListByName() {
        List<Distillery> w =  Utils.asList(getDistilleries());
        Collections.sort(w, new Distillery.NameComparator());
        return w;
    }

    public List<Whisky> getBarWhiskys() {
        List<Whisky> w = getWhiskyListByName();
        List<Whisky> ret = new ArrayList<Whisky>();
        for(Whisky whisky: w) {
            if(whisky.getBarOwns() || whisky.getBarOwned() || whisky.getBarTasted() || whisky.getBarWants())
            {
                ret.add(whisky);
            }

        }

        return ret;
    }

    public List<Whisky> getOwnedWhiskys() {
        List<Whisky> w = getWhiskyListByName();
        List<Whisky> ret = new ArrayList<Whisky>();
        for(Whisky whisky: w)
            if(whisky.getBarOwned())
                ret.add(whisky);

        return ret;
    }

    public List<Whisky> getOwnsWhiskys() {
        List<Whisky> w = getWhiskyListByName();
        List<Whisky> ret = new ArrayList<Whisky>();
        for(Whisky whisky: w)
            if(whisky.getBarOwns())
                ret.add(whisky);

        return ret;
    }

    public List<Whisky> getTastedWhiskys() {
        List<Whisky> w = getWhiskyListByName();
        List<Whisky> ret = new ArrayList<Whisky>();
        for(Whisky whisky: w)
            if(whisky.getBarTasted())
                ret.add(whisky);

        return ret;
    }

    public List<Whisky> getWantsWhiskys() {
        List<Whisky> w = getWhiskyListByName();
        List<Whisky> ret = new ArrayList<Whisky>();
        for(Whisky whisky: w)
            if(whisky.getBarWants())
                ret.add(whisky);

        return ret;
    }

    public int getNextFreeCustomId() {
        int custom_id = 1000;
        Cursor c = mUserDataBase.rawQuery("select " +
                "whisky_id from bar where whisky_id >= 1000 order by whisky_id desc limit 1", null);
        if(c.getCount() > 0) {
            c.moveToFirst();
            custom_id = c.getInt(c.getColumnIndex("whisky_id")) + 1;
        }
        c.close();

        return custom_id;
    }

    public void addToBar(Whisky w, Boolean owns, Boolean owned, Boolean tasted,
                         Boolean wants, Float rating, String notes) {
        try {
            mUserDataBase.beginTransaction();

            // check, if whisky has an id. Otherwise, generate one.
            if(w.getId() == null)
                w.setId(getNextFreeCustomId());

            ContentValues values = new ContentValues();
            values.put("is_custom", w.isCustom() ? 1 : 0);
            values.put("whisky_id", w.getId());
            values.put("owns", owns ? 1 : 0);
            values.put("owned", owned ? 1 : 0);
            values.put("tasted", tasted ? 1 : 0);
            values.put("wants", wants ? 1 : 0);
            values.put("rating", rating);
            values.put("notes", notes);
            values.put("whisky_name", w.getName());

            mUserDataBase.insertWithOnConflict("bar", null, values, SQLiteDatabase.CONFLICT_REPLACE);
            mUserDataBase.setTransactionSuccessful();
        } finally {
            mUserDataBase.endTransaction();
        }
    }

    /**
     * The Helper that creates and opens the SQLite Database. It is stored as a Singleton
     * as suggested by the Android dev docs. We are using the following library to install
     * and upgrade the database: https://github.com/jgilfelt/android-sqlite-asset-helper
     */
    public static class WhiskyDatabaseOpenHelper extends SQLiteAssetHelper {

        private static WhiskyDatabaseOpenHelper mInstance;

        private static final String DB_NAME = "whisky.sqlite";
        private static final int DATABASE_VERSION = 7;

        private WhiskyDatabaseOpenHelper(Context context) {
            super(context, DB_NAME, null, DATABASE_VERSION);
            setForcedUpgrade();
        }

        public static WhiskyDatabaseOpenHelper getInstance(Context ctx) {

            // Use the application context, which will ensure that you
            // don't accidentally leak an Activity's context.
            // See this article for more information: http://bit.ly/6LRzfx
            if (mInstance == null) {
                mInstance = new WhiskyDatabaseOpenHelper(ctx.getApplicationContext());
            }
            return mInstance;
        }
    }

    /**
     * The Helper that creates and opens the user SQLite Database. It is stored as a Singleton
     * as suggested by the Android dev docs.
     */
    public static class UserDatabaseOpenHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 3;
        private static final String DATABASE_NAME = "whisky-user";
        private static UserDatabaseOpenHelper mInstance = null;

        private static final String BAR_TABLE_CREATE =
                "CREATE TABLE IF NOT EXISTS bar (" +
                        "is_custom INTEGER, " +
                        "whisky_id INTEGER UNIQUE ON CONFLICT REPLACE, " +
                        "tasted    INTEGER, " +
                        "owns      INTEGER, " +
                        "wants     INTEGER, " +
                        "owned     INTEGER, " +
                        "rating    FLOAT, " +
                        "notes     TEXT, " +
                        "whisky_name TEXT);";


        private UserDatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public static UserDatabaseOpenHelper getInstance(Context ctx) {

            // Use the application context, which will ensure that you
            // don't accidentally leak an Activity's context.
            // See this article for more information: http://bit.ly/6LRzfx
            if (mInstance == null) {
                mInstance = new UserDatabaseOpenHelper(ctx.getApplicationContext());
            }
            return mInstance;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(BAR_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            String upgradeQuery = "ALTER TABLE bar ADD COLUMN whisky_name TEXT";
            if (oldVersion == 1 && (newVersion == 2 || newVersion == 3))
                db.execSQL(upgradeQuery);
            String upgradeQuery2 = "ALTER TABLE bar ADD COLUMN is_custom INTEGER";
            if ((oldVersion == 1 || oldVersion == 2) && newVersion == 3)
                db.execSQL(upgradeQuery2);
        }
    }

    public class BarTuple {
        public final Boolean owns;
        public final Boolean owned;
        public final Boolean tasted;
        public final Boolean wants;
        public final Float rating;
        public final String notes;
        public final Boolean is_custom;
        public final String name;

        public BarTuple(Boolean is_custom, Boolean owns, Boolean owned, Boolean tasted, Boolean wants, Float rating,
                        String notes, String name) {
            this.owns = owns;
            this.owned = owned;
            this.tasted = tasted;
            this.wants = wants;
            this.rating = rating;
            this.notes = notes;
            this.name = name;
            this.is_custom = is_custom;
        }
    }

}