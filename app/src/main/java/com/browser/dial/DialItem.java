package com.browser.dial;

/**
 * Created by ozgur on 27.06.2016.
 */
public class DialItem implements Comparable<DialItem> {

    private int id;
    private String url;
    private String title;
    private int position;
    private byte[] bitmap;
    private boolean isPlusButton;

    public static final int URL = 0;
    public static final int PLUS = 1;

    public DialItem(int id, String url, String title, int position, byte[] bitmap) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.position = position;
        this.bitmap = bitmap;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public int getPosition() {
        return position;
    }

    public byte[] getBitmap() {
        return bitmap;
    }

    public boolean isPlusButton() {
        return isPlusButton;
    }

    public void setPlusButton(boolean plusButton) {
        isPlusButton = plusButton;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "{ name: "+title+", pos: "+position+" } ";
    }

    @Override
    public int compareTo(DialItem o) {
        return position - o.position;
    }

    public void setBitmap(byte[] bitmap) {
        this.bitmap = bitmap;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
