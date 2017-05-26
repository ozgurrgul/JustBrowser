package com.browser.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by ozgur on 2/6/16 at 9:09 PM.
 */
public class JListView extends ListView implements AbsListView.OnScrollListener {

    private boolean inEndOfList = false;
    private ListViewListener listViewListener;

    public JListView(Context context) {
        super(context);
        init(context);
    }

    public JListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public JListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        //Log("onScrollStateChanged(..), scrollState =" + scrollState);
    }

    @Override
    public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        int tot = firstVisibleItem + visibleItemCount;
        if(tot >= totalItemCount) {

            if(!inEndOfList) {
                //Log("onScroll, end of list");
                inEndOfList = true;

                if(listViewListener != null)
                    listViewListener.onEndOfList();
            }
        } else
            inEndOfList = false;
    }


    private void init(Context context) {
        setOnScrollListener(this);
    }

    public void setListViewListener(ListViewListener listViewListener) {
        this.listViewListener = listViewListener;
    }

    public interface ListViewListener {
        void onEndOfList() ;
    }

}