package com.browser.adblock;

/**
 * Created by ozgur on 06.08.2016.
 */
public class AdBlockItem {

    private int _id;
    private String url;
    private int times;

    public AdBlockItem(int id, String url, int times) {
        _id = id;
        this.url = url;
        this.times = times;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
