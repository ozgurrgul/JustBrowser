package com.browser.adblock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ozgur on 06.08.2016.
 */
public class AdBlockDb extends SQLiteOpenHelper {

    private static AdBlockDb instance = null;
    private static final String DB_NAME = "adblock";
    private static final int DB_VERSION = 1;
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_TIMES = "times";

    private AdBlockDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized AdBlockDb getInstance(Context context) {
        if (instance == null)
            instance = new AdBlockDb(context);

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}

    private static String createTable() {
        return "CREATE TABLE IF NOT EXISTS " + DB_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," //id
                + COLUMN_URL + " nvarchar(1024), "  //url
                + COLUMN_TIMES + " INTEGER" //date
                + ")";
    }

    public void saveHistory(final String url) {

        if(TextUtils.isEmpty(url)) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                _saveHistory(url);
            }
        }).start();
    }

    private void  _saveHistory(String url) {
        increaseAdBlockCount(url, 1);
    }

    public void  increaseAdBlockCount(String url, int amount) {

        if(amount <= 0) {
            return;
        }

        String sql = "SELECT * FROM " + DB_NAME + " WHERE " + COLUMN_URL + " = ?";
        Cursor possible = getReadableDatabase().rawQuery(sql, new String[]{url});

        if(possible.getCount() == 0) {
            add(url);
            Log.d("AdblockDB", "added");
        } else {

            if(!possible.moveToNext()) {
                possible.close();
                return;
            }

            int _id = possible.getInt(possible.getColumnIndex(COLUMN_ID));

            ContentValues values = new ContentValues();
            values.put(COLUMN_TIMES, possible.getInt(possible.getColumnIndex(COLUMN_TIMES)) + amount);
            getWritableDatabase().update(DB_NAME, values, COLUMN_ID + " = ? ", new String[] { String.valueOf(_id) });

            Log.d("AdblockDB", "updated");
        }

        possible.close();

    }

    private long add(String url) {
        try {
            ContentValues c = new ContentValues();
            c.put(COLUMN_URL, url);
            c.put(COLUMN_TIMES, 1);
            SQLiteDatabase db = getWritableDatabase();
            return db.insert(DB_NAME, null, c);

        } catch (Exception ignored) {
            return -1;
        }
    }

    public ArrayList<AdBlockItem> getBlockedDomains(int limit, int offset) {

        ArrayList<AdBlockItem> dataList = new ArrayList<>();

        String sql = "SELECT * FROM " + DB_NAME + " ORDER BY times DESC LIMIT ? OFFSET ?";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{String.valueOf(limit), String.valueOf(offset)});

        while (cursor.moveToNext()) {
            dataList.add(new AdBlockItem(
                cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_URL)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_TIMES))
            ));
        }

        return dataList;
    }

    public void clearAll() {
        getWritableDatabase().delete(DB_NAME, null, null);
    }

    public void delete(int id) {
        getWritableDatabase().delete(DB_NAME, "_id = ?", new String[] {String.valueOf(id)});
    }

    public long getTotalProtectedUrls() {
        SQLiteStatement s = getReadableDatabase().compileStatement("SELECT COUNT(*) AS TOTAL FROM " + DB_NAME + "");
        return s.simpleQueryForLong();
    }

    public long getTotalBlockCount() {
        SQLiteStatement s = getReadableDatabase().compileStatement("SELECT SUM("+COLUMN_TIMES+") AS TOTAL FROM " + DB_NAME + "");
        return s.simpleQueryForLong();
    }
}

