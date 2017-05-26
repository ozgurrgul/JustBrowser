package com.browser.tab;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by ozgur on 03.08.2016.
 */
public class TabData implements Serializable {

    private String id;
    private String parentId; //for pop up
    private String title;
    private String url;
    private Bitmap favicon;
    private boolean isIncognito;
    private boolean inBackground;
    private boolean closeTabWhenBack;
    private boolean isDesktopMode;
    private boolean browsing;
    private boolean destroying;
    private int progress;

    public TabData () {
        id = UUID.randomUUID().toString();
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

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getFavicon() {
        return favicon;
    }

    public void setFavicon(Bitmap favicon) {
        this.favicon = favicon;
    }

    public boolean isIncognito() {
        return isIncognito;
    }

    public void setIncognito(boolean incognito) {
        isIncognito = incognito;
    }

    public boolean isInBackground() {
        return inBackground;
    }

    public void setInBackground(boolean inBackground) {
        this.inBackground = inBackground;
    }

    public boolean isCloseTabWhenBack() {
        return closeTabWhenBack;
    }

    public void setCloseTabWhenBack(boolean closeTabWhenBack) {
        this.closeTabWhenBack = closeTabWhenBack;
    }

    public boolean isDesktopMode() {
        return isDesktopMode;
    }

    public void setDesktopMode(boolean desktopMode) {
        isDesktopMode = desktopMode;
    }

    public void setBrowsing(boolean browsing) {
        this.browsing = browsing;
    }

    public boolean isBrowsing() {
        return browsing;
    }

    public String getId() {
        return id;
    }

    public boolean isDestroying() {
        return destroying;
    }

    public void setDestroying(boolean destroying) {
        this.destroying = destroying;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }
}
