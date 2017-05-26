package com.browser.odm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by ozgur on 05.07.2016.
 */
public class ODMDatabase extends SQLiteOpenHelper {

    public static final String DB_NAME = "downloader.db";
    public static final int DB_VERSION = 5;
    public static final String TABLE = "downloads";
    public static final String COLUMN_ID = "_id"; //auto int
    public static final String COLUMN_DID = "did"; //dl id
    public static final String COLUMN_URL = "url"; //dl url
    public static final String COLUMN_PATH = "path"; //dl path
    public static final String COLUMN_FILENAME = "filename"; //dl file name
    public static final String COLUMN_FILESIZE = "filesize"; //dl file size
    public static final String COLUMN_STATE = "state"; //dl state
    public static final String COLUMN_PROGRESS = "progress";
    public static final String COLUMN_DL_TOTAL = "dltotal";
    public static final String COLUMN_CAN_RANGE_DOWNLOAD = "rangesupport";

    private static ODMDatabase instance = null;

    private ODMDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized ODMDatabase getInstance(Context context) {
        if (instance == null)
            instance = new ODMDatabase(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    private static String createTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," //_id
                + COLUMN_DID + " nvarchar(1024) UNIQUE," // did
                + COLUMN_URL + " nvarchar(1024), "  //url
                + COLUMN_PATH + " nvarchar(1024), " // path
                + COLUMN_FILENAME + " nvarchar(256), " //filename
                + COLUMN_FILESIZE + " INTEGER DEFAULT 0, " //filesize
                + COLUMN_PROGRESS + " INTEGER DEFAULT 0, " //prog
                + COLUMN_DL_TOTAL + " INTEGER DEFAULT 0, " //dl total bytes
                + COLUMN_CAN_RANGE_DOWNLOAD + " INTEGER DEFAULT 0, " //filesize
                + COLUMN_STATE + " nvarchar(1024)" // state
                + ")";
    }

    public long add(String did, String fileName, String url, String path, int fileSize) {
        try {

            ContentValues c = new ContentValues();
            c.put(COLUMN_DID, did);
            c.put(COLUMN_FILENAME, fileName);
            c.put(COLUMN_URL, url);
            c.put(COLUMN_PATH, path);
            c.put(COLUMN_FILESIZE, fileSize);
            c.put(COLUMN_STATE, ODMWorker.WORKER_WAITING);
            c.put(COLUMN_PROGRESS, 0);
            c.put(COLUMN_DL_TOTAL, 0);
            SQLiteDatabase db = getWritableDatabase();
            return db.insert(TABLE, null, c);

        } catch (Exception ignored) {
            return -1;
        }

    }

    public void update(String did, ContentValues values) {
        getWritableDatabase().update(TABLE, values, COLUMN_DID + " = ? ", new String[] { did });
    }

    public ArrayList<ODMWorker> getUnCompletedWorkers(ODMPool pool) {

        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM downloads WHERE state NOT IN ('COMPLETED', 'DELETED', 'ERROR')";
        Cursor cursor = db.rawQuery(sql, null);

        ArrayList<ODMWorker> workers = new ArrayList<>();

        while (cursor.moveToNext()) {

            workers.add(
                new ODMWorkerBuilder()
                    .setParentPool(pool)
                    .setDid(cursor.getString(cursor.getColumnIndex(COLUMN_DID)))
                    .setFileName(cursor.getString(cursor.getColumnIndex(COLUMN_FILENAME)))
                    .setPath(cursor.getString(cursor.getColumnIndex(COLUMN_PATH)))
                    .setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_URL)))
                    .setFileSize(cursor.getInt(cursor.getColumnIndex(COLUMN_FILESIZE)))
                    .setState(cursor.getString(cursor.getColumnIndex(COLUMN_STATE)))
                    .setProgress(cursor.getInt(cursor.getColumnIndex(COLUMN_PROGRESS)))
                    .setDlTotal(cursor.getInt(cursor.getColumnIndex(COLUMN_DL_TOTAL)))
                    .setCanRangeDownload(cursor.getInt(cursor.getColumnIndex(COLUMN_CAN_RANGE_DOWNLOAD)))
                    .setHttpClient(pool.getHttpClient())
                    .build()
            );

        }

        return workers;
    }

    public Cursor getRunningDownloadCursor() {
        String sql = "SELECT * FROM downloads WHERE state IN('RUNNING', 'PAUSED', 'WAITING') ORDER BY _id DESC";
       return getReadableDatabase().rawQuery(sql, null);
    }

    public Cursor getCompletedDownloadCursor(int limit) {
        String sql = "SELECT * FROM downloads WHERE state NOT IN('RUNNING', 'PAUSED', 'WAITING', 'DELETED') ORDER BY _id DESC LIMIT ?";
        return getReadableDatabase().rawQuery(sql, new String[]{String.valueOf(limit)});
    }

    public ArrayList<ODMItem> getRunningDownloadList() {
        ArrayList<ODMItem> odmItems = new ArrayList<>();
        Cursor cursor = getRunningDownloadCursor();

        /**/
        while (cursor.moveToNext()) {

            ODMItem odmItem = new ODMItem(
                    new ODMWorkerBuilder()
                            .setDid(cursor.getString(cursor.getColumnIndex(COLUMN_DID)))
                            .setFileName(cursor.getString(cursor.getColumnIndex(COLUMN_FILENAME)))
                            .setPath(cursor.getString(cursor.getColumnIndex(COLUMN_PATH)))
                            .setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_URL)))
                            .setFileSize(cursor.getInt(cursor.getColumnIndex(COLUMN_FILESIZE)))
                            .setState(cursor.getString(cursor.getColumnIndex(COLUMN_STATE)))
                            .setProgress(cursor.getInt(cursor.getColumnIndex(COLUMN_PROGRESS)))
                            .setDlTotal(cursor.getInt(cursor.getColumnIndex(COLUMN_DL_TOTAL)))
                            .setCanRangeDownload(cursor.getInt(cursor.getColumnIndex(COLUMN_CAN_RANGE_DOWNLOAD)))
            );

            odmItems.add(odmItem);
        }

        return odmItems;
    }

    public ArrayList<ODMItem> getCompletedDownloadList(int limit) {
        ArrayList<ODMItem> odmItems = new ArrayList<>();
        Cursor cursor = getCompletedDownloadCursor(limit);

        /**/
        while (cursor.moveToNext()) {

            ODMItem odmItem = new ODMItem(
                    new ODMWorkerBuilder()
                            .setDid(cursor.getString(cursor.getColumnIndex(COLUMN_DID)))
                            .setFileName(cursor.getString(cursor.getColumnIndex(COLUMN_FILENAME)))
                            .setPath(cursor.getString(cursor.getColumnIndex(COLUMN_PATH)))
                            .setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_URL)))
                            .setFileSize(cursor.getInt(cursor.getColumnIndex(COLUMN_FILESIZE)))
                            .setState(cursor.getString(cursor.getColumnIndex(COLUMN_STATE)))
                            .setProgress(cursor.getInt(cursor.getColumnIndex(COLUMN_PROGRESS)))
                            .setDlTotal(cursor.getInt(cursor.getColumnIndex(COLUMN_DL_TOTAL)))
                            .setCanRangeDownload(cursor.getInt(cursor.getColumnIndex(COLUMN_CAN_RANGE_DOWNLOAD)))
            );

            odmItems.add(odmItem);
        }

        return odmItems;
    }
}
