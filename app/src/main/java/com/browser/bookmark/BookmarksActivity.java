package com.browser.bookmark;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.browser.App;
import com.browser.R;
import com.browser.base.BaseActivity;
import com.browser.ui.dialog.JDialog;
import com.browser.ui.dialog.JListDialog;
import com.browser.ui.widget.JBB;
import com.browser.ui.widget.JListView;
import com.browser.ui.widget.JTB;
import com.browser.ui.widget.NoDataToSee;
import com.browser.ui.widget.jlist.JLTV;
import com.browser.ui.widget.jlist.JLTV2;
import com.browser.ui.widget.jlist.JLBG;

import java.util.ArrayList;

/**
 * Created by ozgur on 06.08.2016.
 */
public class BookmarksActivity extends BaseActivity implements JBB.JBottomBarClickListener, JListView.ListViewListener {

    private JTB topBar;
    private JBB bottomBar;
    private JListView listView;
    private BookmarksAdapter bookmarksAdapter;
    private LayoutInflater mLayoutInflater;
    private NoDataToSee noDataToSee;
    private int limit = 100000;
    private int offset = 0;
    private ArrayList<BookmarkItem> dataList = new ArrayList<>();
    AsyncTask<Void, Void, ArrayList<BookmarkItem>> asyncTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mLayoutInflater = LayoutInflater.from(this);
        topBar = (JTB) findViewById(R.id.TopBar);
        topBar.setTitle(R.string.bookmarks);
        bottomBar = (JBB) findViewById(R.id.BottomBar);
        bottomBar.setjBottomBarClickListener(this);
        listView = (JListView) findViewById(R.id.ListView);
        listView.setListViewListener(this);
        noDataToSee = (NoDataToSee) findViewById(R.id.NoDataToSee);
        bookmarksAdapter = new BookmarksAdapter();
        listView.setAdapter(bookmarksAdapter);

        setBackgroundTheme();
        getData();
    }

    private void getData() {

        asyncTask = new AsyncTask<Void, Void, ArrayList<BookmarkItem>>() {

            @Override
            protected ArrayList<BookmarkItem> doInBackground(Void... voids) {
                return BookmarkDb.getInstance(BookmarksActivity.this).getBookmarks(limit, offset);
            }

            @Override
            protected void onPostExecute(ArrayList<BookmarkItem> bookmarkItems) {

                dataList.addAll(bookmarkItems);
                bookmarksAdapter.notifyDataSetChanged();

                if(dataList.size() == 0) {
                    noDataToSee.show();
                    listView.setVisibility(View.GONE);
                } else {
                    noDataToSee.hide();
                    listView.setVisibility(View.VISIBLE);
                }
            }
        }.execute();

    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(asyncTask != null) {
            asyncTask.cancel(true);
        }
    }

    @Override
    public void onRemoveClick() {
        JDialog j = new JDialog(this);
        j.setTitle(R.string.delete_all);
        j.setContent(R.string.delete_all);
        j.setYesButtonText(R.string.a_delete);
        j.setNoButtonText(R.string.a_cancel);
        j.setCancelable(true);
        j.setDialogClickListener(new JDialog.DialogClickListener() {
            @Override
            public void onYesOrNoClick(JDialog.ButtonType buttonType) {
                if(buttonType == JDialog.ButtonType.POSITIVE) {
                    //
                    BookmarkDb.getInstance(BookmarksActivity.this).clearAll();
                    dataList.clear();
                    offset = 0;
                    getData();
                }
            }
        });
        j.show();
    }

    @Override
    public void onEndOfList() {
        //offset = offset + limit;
        //getData();
    }

    private class BookmarksAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public BookmarkItem getItem(int i) {
            return dataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return dataList.get(i).get_id();
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {

            RowHolder rowHolder;
            final BookmarkItem item = getItem(i);

            if(convertView == null) {
                rowHolder = new RowHolder();
                convertView = mLayoutInflater.inflate(R.layout.history_item, viewGroup, false);
                rowHolder.wrapper = (JLBG) convertView.findViewById(R.id.Wrapper);
                rowHolder.titleTextView = (JLTV) convertView.findViewById(R.id.TitleTextView);
                rowHolder.urlTextView = (JLTV2) convertView.findViewById(R.id.UrlTextView);
                convertView.setTag(rowHolder);
            } else {
                rowHolder = (RowHolder) convertView.getTag();
            }

            rowHolder.titleTextView.setText(item.getTitle());
            rowHolder.urlTextView.setText(item.getUrl());
            rowHolder.wrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(BookmarksActivity.this, "" + item.getUrl(), Toast.LENGTH_SHORT).show();

                    App a = (App) getApplication();
                    a.loadUrl(item.getUrl(), true);
                    finish();
                }
            });

            rowHolder.wrapper.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    JListDialog j = new JListDialog(BookmarksActivity.this);
                    j.setTitle(R.string.menu);
                    j.setListItems(new String[]{
                        getString(R.string.a_bg_tab),
                        getString(R.string.a_delete)
                    });
                    j.setCancelable(true);
                    j.show();
                    j.setDialogClickListener(new JListDialog.DialogClickListener() {
                        @Override
                        public void onClick(String clickedItem, int position) {

                            if(position == 0) {
                                App a = (App) getApplication();
                                a.loadUrl(item.getUrl(), false);
                            }

                            if(position == 1) {
                                dataList.remove(item);
                                BookmarkDb.getInstance(BookmarksActivity.this).delete(item.get_id());
                                bookmarksAdapter.notifyDataSetChanged();
                            }
                        }
                    });

                    return false;
                }
            });

            return convertView;
        }
    }

    private static class RowHolder {
        public JLBG wrapper;
        public JLTV titleTextView;
        public JLTV2 urlTextView;
    }
}
