package com.browser.dial;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.browser.utils.BitmapUtils;

import java.util.ArrayList;

/**
 * Created by ozgur on 06.08.2016.
 */
public class DialDb extends SQLiteOpenHelper {

    private static DialDb instance = null;
    private static final String DB_NAME = "dial";
    private static final int DB_VERSION = 1;
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_POSITION = "pos";
    public static final String COLUMN_BITMAP = "bitmap";
    private DialDb(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized DialDb getInstance(Context context) {
        if (instance == null)
            instance = new DialDb(context);
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
                + COLUMN_POSITION + " INTEGER, " //date
                + COLUMN_BITMAP + " BLOB" //visit
                + ")";
    }

    public long add(String url, String title, Bitmap bitmap) {
        try {
            ContentValues c = new ContentValues();
            c.put(COLUMN_URL, url);
            c.put(COLUMN_TITLE, title);
            c.put(COLUMN_POSITION, getLastPosition());
            c.put(COLUMN_BITMAP, BitmapUtils.getByte(bitmap));
            SQLiteDatabase db = getWritableDatabase();
            return db.insert(DB_NAME, null, c);

        } catch (Exception ignored) {
            return -1;
        }
    }

    public void delete(int id) {
        getWritableDatabase().delete(DB_NAME, "_id = ?", new String[] {String.valueOf(id)});
    }

    public ArrayList<DialItem> getDials() {

        ArrayList<DialItem> dataList = new ArrayList<>();

        String sql = "SELECT * FROM " + DB_NAME + " ORDER BY "+COLUMN_POSITION;
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);

        while (cursor.moveToNext()) {
            dataList.add(new DialItem(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_URL)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_POSITION)),
                    cursor.getBlob(cursor.getColumnIndex(COLUMN_BITMAP))
            ));
        }

        return dataList;
    }

    public int getLastPosition() {
        int pos = 0;

        String sql = "SELECT * FROM "+DB_NAME+" WHERE "+COLUMN_POSITION+" = (SELECT MAX("+COLUMN_POSITION+") FROM "+DB_NAME+");";
        Cursor cursor = getReadableDatabase().rawQuery(sql, null);

        if(cursor.moveToLast()) {
            pos = cursor.getInt(cursor.getColumnIndex(COLUMN_POSITION)) + 1;
        }

        Log.d("DDDDD", "pos: " + pos);

        return pos;
    }

    public void updatePosition(int id, int pos) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_POSITION, pos);
        getWritableDatabase().update(DB_NAME, values, COLUMN_ID + " = ? ", new String[] { String.valueOf(id) });
    }
}
