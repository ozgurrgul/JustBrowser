package com.browser.dial;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ozgur on 07.08.2016.
 */
public class DialItems {

    private Context context;
    private ArrayList<DialItem> dialItems;

    public DialItems(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        this.dialItems = DialDb.getInstance(context).getDials();

        int pos = 999999;
        DialItem plus = new DialItem(pos, null, null, pos, null);
        plus.setPlusButton(true);
        dialItems.add(plus);

        sort();
    }

    public int size() {
        return dialItems.size();
    }

    public void add(String url, String title, Bitmap bitmap) {
        DialDb.getInstance(context).add(url, title, bitmap);
        init();
    }

    public DialItem get(int position) {
        return dialItems.get(position);
    }

    public void sort() {
        Collections.sort(dialItems);
        //Collections.reverse(dialItems);
    }

    public void swap(DialItem touching, DialItem target) {
        int from = touching.getPosition();
        int to = target.getPosition();

        touching.setPosition(to);
        target.setPosition(from);

        DialDb.getInstance(context).updatePosition(touching.getId(), to);
        DialDb.getInstance(context).updatePosition(target.getId(), from);

        sort();
    }

    public void delete(DialItem dialItem) {
        dialItems.remove(dialItem);
        DialDb.getInstance(context).delete(dialItem.getId());
        sort();
    }
}
