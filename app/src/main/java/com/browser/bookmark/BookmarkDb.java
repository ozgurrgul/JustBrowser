package com.browser.bookmark;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ozgur on 06.08.2016.
 */
public class BookmarkDb extends SQLiteOpenHelper {

    private static BookmarkDb instance = null;
    private static final String DB_NAME = "bookmark";
    private static final int DB_VERSION = 1;
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_POS = "pos";
    private BookmarkDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized BookmarkDb getInstance(Context context) {
        if (instance == null)
            instance = new BookmarkDb(context);
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
                + COLUMN_DATE + " INTEGER," //date
                + COLUMN_POS + " INTEGER" //pos
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
            c.put(COLUMN_POS, getLastPosition());
            SQLiteDatabase db = getWritableDatabase();
            return db.insert(DB_NAME, null, c);

        } catch (Exception ignored) {
            return -1;
        }
    }

    public ArrayList<BookmarkItem> getBookmarks(int limit, int offset) {

        ArrayList<BookmarkItem> dataList = new ArrayList<>();

        String sql = "SELECT * FROM " + DB_NAME + " ORDER BY "+COLUMN_POS+" ASC LIMIT ? OFFSET ?";
        Cursor cursor = getReadableDatabase().rawQuery(sql, new String[]{String.valueOf(limit), String.valueOf(offset)});

        while (cursor.moveToNext()) {
            dataList.add(new BookmarkItem(
                cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_URL)),
                cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_DATE))
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

    public int getLastPosition() {
        int pos = 0;

        String sql = "SELECT * FROM "+DB_NAME+" WHERE "+COLUMN_POS+" = (SELECT MAX("+COLUMN_POS+") FROM "+DB_NAME+");";
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);

        if(cursor.moveToLast()) {
            pos = cursor.getInt(cursor.getColumnIndex(COLUMN_POS)) + 1;
        }

        return pos;
    }

}

