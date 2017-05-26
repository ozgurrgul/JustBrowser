package com.browser.bookmark;

/**
 * Created by ozgur on 06.08.2016.
 */
public class BookmarkItem {
    private int _id;
    private String url;
    private String title;
    private int date;
    private int pos;

    public BookmarkItem(int id, String url, String title, int date) {
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

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
