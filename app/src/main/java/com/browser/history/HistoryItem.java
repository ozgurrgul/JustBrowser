package com.browser.history;

/**
 * Created by ozgur on 06.08.2016.
 */
public class HistoryItem {
    private int _id;
    private String url;
    private String title;
    private long date;
    private boolean separator;
    private int visits = 0;

    public static final int HISTORY = 0;
    public static final int SUGGESTION = 1;
    private int type;

    public HistoryItem(int id, String url, String title, long date) {
        _id = id;
        this.url = url;
        this.title = title;
        this.date = date;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public boolean isSeparator() {
        return separator;
    }

    public void setSeparator(boolean separator) {
        this.separator = separator;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }
}
