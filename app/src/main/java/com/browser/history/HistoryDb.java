package com.browser.history;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ozgur on 06.08.2016.
 */
public class HistoryDb extends SQLiteOpenHelper {

    private static HistoryDb instance = null;
    private static final String DB_NAME = "history";
    private static final int DB_VERSION = 1;
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_VISITS = "visits";
    private HistoryDb (Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized HistoryDb getInstance(Context context) {
        if (instance == null)
            instance = new HistoryDb(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    private static String createTable() {
        return "CREATE TABLE IF NOT EXISTS " + DB_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," //id
                + COLUMN_URL + " nvarchar(1024), "  //url
                + COLUMN_TITLE + " nvarchar(1024), " // title
                + COLUMN_DATE + " INTEGER, " //date
                + COLUMN_VISITS + " INTEGER DEFAULT 0 " //visit
                + ")";
    }

    public void save(final String url, final String title) {

        if(TextUtils.isEmpty(url) || TextUtils.isEmpty(title)) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                _save(url, title);
            }
        }).start();
    }

    private void  _save(String url, String title) {

        String sql = "SELECT * FROM " + DB_NAME + " WHERE " + COLUMN_URL + " = ?";
        Cursor possible = getReadableDatabase().rawQuery(sql, new String[]{url});

        if(possible.getCount() == 0) {
            add(url, title);
        } else {

            if(!possible.moveToNext())
                return;

            int _id = possible.getInt(possible.getColumnIndex(COLUMN_ID));

            ContentValues values = new ContentValues();
            values.put(COLUMN_VISITS, possible.getInt(possible.getColumnIndex(COLUMN_VISITS)) + 1);
            values.put(COLUMN_TITLE, title);

            getWritableDatabase().update(DB_NAME, values, COLUMN_ID + " = ? ", new String[] { String.valueOf(_id) });
        }

    }

    private long add(String url, String title) {
        try {
            ContentValues c = new ContentValues();
            c.put(COLUMN_URL, url);
            c.put(COLUMN_TITLE, title);
            c.put(COLUMN_DATE, System.currentTimeMillis());
            c.put(COLUMN_VISITS, 0);
            SQLiteDatabase db = getWritableDatabase();
            return db.insert(DB_NAME, null, c);

        } catch (Exception ignored) {
            return -1;
        }
    }

    public ArrayList<HistoryItem> getHistory(int limit, int offset) {

        ArrayList<HistoryItem> dataList = new ArrayList<>();

        String sql = "SELECT * FROM " + DB_NAME + " ORDER BY _id DESC LIMIT ? OFFSET ?";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{String.valueOf(limit), String.valueOf(offset)});

        while (cursor.moveToNext()) {
            dataList.add(new HistoryItem(
                cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_URL)),
                cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_DATE))
            ));
        }

        return dataList;
    }

    public void clearAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getWritableDatabase().delete(DB_NAME, null, null);

            }
        }).start();
    }

    public void delete(int id) {
        getWritableDatabase().delete(DB_NAME, "_id = ?", new String[] {String.valueOf(id)});
    }

    public List<HistoryItem> getSuggestions(String text) {
        List<HistoryItem> dataList = new ArrayList<>();

        String[] args = new String[] {
                "%"+text+"%",
                "%"+text+"%",
                String.valueOf(250)
        };

        String sql = "SELECT * FROM "+DB_NAME+" WHERE "+COLUMN_URL+" LIKE ? OR "+COLUMN_TITLE+" LIKE ? LIMIT ?";
        Cursor cursor = getReadableDatabase().rawQuery(sql, args);

        while (cursor.moveToNext()) {
            HistoryItem h = new HistoryItem(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_URL)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                    cursor.getLong(cursor.getColumnIndex(COLUMN_DATE))
            );
            h.setVisits(cursor.getInt(cursor.getColumnIndex(COLUMN_VISITS)));
            dataList.add(h);
        }

        Collections.sort(dataList, visitedComparator);

        int limit = 3;
        int i = 0;

        List<HistoryItem> lastResults = new ArrayList<>();

        for(HistoryItem c: dataList) {
            lastResults.add(c);
            i++;
            if(i >= limit) {
                break;
            }
        }

        return lastResults;
    }

    Comparator visitedComparator = new Comparator() {

        @Override
        public int compare(Object o, Object o2) {

            HistoryItem historyModel = (HistoryItem) o;
            HistoryItem historyModel2 = (HistoryItem) o2;

            int visitedCount = historyModel2.getVisits() - historyModel.getVisits();

            if(visitedCount != 0)
                return visitedCount;
            if (historyModel2.getDate() > historyModel.getDate()) {
                return 1;
            }

            return historyModel2.getDate() < historyModel.getDate() ? -1 : visitedCount;
        }
    };

}

