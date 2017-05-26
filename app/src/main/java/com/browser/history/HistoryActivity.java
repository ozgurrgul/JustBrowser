package com.browser.history;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.browser.App;
import com.browser.R;
import com.browser.base.BaseActivity;
import com.browser.ui.dialog.JDialog;
import com.browser.ui.dialog.JListDialog;
import com.browser.ui.widget.JBB;
import com.browser.ui.widget.JListView;
import com.browser.ui.widget.JTB;
import com.browser.ui.widget.NoDataToSee;
import com.browser.ui.widget.jlist.JLBG;
import com.browser.ui.widget.jlist.JLTV;
import com.browser.ui.widget.jlist.JLTV2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by ozgur on 06.08.2016.
 */
public class HistoryActivity extends BaseActivity implements JBB.JBottomBarClickListener, JListView.ListViewListener {

    private JTB topBar;
    private JBB bottomBar;
    private JListView listView;
    private HistoryAdapter historyAdapter;
    private LayoutInflater mLayoutInflater;
    private NoDataToSee noDataToSee;
    private int limit = 20;
    private int offset = 0;
    private ArrayList<Object> dataList = new ArrayList<>();
    AsyncTask<Void, Void, ArrayList<HistoryItem>> asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mLayoutInflater = LayoutInflater.from(this);
        topBar = (JTB) findViewById(R.id.TopBar);
        topBar.setTitle(R.string.history);
        bottomBar = (JBB) findViewById(R.id.BottomBar);
        bottomBar.setjBottomBarClickListener(this);
        listView = (JListView) findViewById(R.id.ListView);
        listView.setListViewListener(this);
        noDataToSee = (NoDataToSee) findViewById(R.id.NoDataToSee);
        historyAdapter = new HistoryAdapter();
        listView.setAdapter(historyAdapter);

        setBackgroundTheme();
        getData();
    }

    private void getData() {

        asyncTask = new AsyncTask<Void, Void, ArrayList<HistoryItem>>() {

            @Override
            protected ArrayList<HistoryItem> doInBackground(Void... voids) {
                return HistoryDb.getInstance(HistoryActivity.this).getHistory(limit, offset);
            }

            @Override
            protected void onPostExecute(ArrayList<HistoryItem> historyItems) {
                setSeparator(historyItems);
                checkDataList();
            }
        }.execute();

    }

    private void checkDataList() {
        if(dataList.size() == 0) {
            noDataToSee.show();
            listView.setVisibility(View.GONE);
        } else {
            noDataToSee.hide();
            listView.setVisibility(View.VISIBLE);
        }
    }

    public void setSeparator(ArrayList<HistoryItem> historyItems) {
        String last = "";

        for(HistoryItem item: historyItems) {

            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            SimpleDateFormat sdf2 = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
            SimpleDateFormat sdf3 = new SimpleDateFormat("EEEE", Locale.getDefault());

            /**/
            Date d = new Date(item.getDate());

            String dayForChecking = sdf.format(d);
            String dayForUi = sdf2.format(d);
            String dayForUiDayName = sdf3.format(d);

            if(!last.equals(dayForChecking)) {
                last = dayForChecking;
                dataList.add(dayForUi + " (" + dayForUiDayName + ")");
            }

            dataList.add(item);
        }

        historyAdapter.notifyDataSetChanged();
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
                    HistoryDb.getInstance(HistoryActivity.this).clearAll();
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
        offset = offset + limit;
        getData();
    }

    private class HistoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int i) {
            return dataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if(getItem(position) instanceof HistoryItem) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {

            RowHolder rowHolder;
            final Object item = getItem(i);

            if(convertView == null) {
                rowHolder = new RowHolder();

                if(getItemViewType(i) == 0) {
                    convertView = mLayoutInflater.inflate(R.layout.history_item, viewGroup, false);
                }
                if(getItemViewType(i) == 1) {
                    convertView = mLayoutInflater.inflate(R.layout.history_item_separator, viewGroup, false);
                }

                rowHolder.wrapper = (JLBG) convertView.findViewById(R.id.Wrapper);
                rowHolder.titleTextView = (JLTV) convertView.findViewById(R.id.TitleTextView);
                rowHolder.urlTextView = (JLTV2) convertView.findViewById(R.id.UrlTextView);
                convertView.setTag(rowHolder);
            } else {
                rowHolder = (RowHolder) convertView.getTag();
            }

            //history
            if(getItemViewType(i) == 0) {
                final HistoryItem historyItem = (HistoryItem) item;
                rowHolder.titleTextView.setText(historyItem.getTitle());
                rowHolder.urlTextView.setText(historyItem.getUrl());
                rowHolder.wrapper.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        App a = (App) getApplication();
                        a.loadUrl(historyItem.getUrl(), true);
                        finish();
                    }
                });

                rowHolder.wrapper.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        JListDialog j = new JListDialog(HistoryActivity.this);
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
                                    a.loadUrl(historyItem.getUrl(), false);
                                }

                                if(position == 1) {
                                    dataList.remove(item);
                                    HistoryDb.getInstance(HistoryActivity.this).delete(historyItem.get_id());

                                    //check if last element is separator, we don't need it
                                    if(dataList.size() == 1 && dataList.get(0) instanceof String) {
                                        dataList.remove(0);
                                    }

                                    historyAdapter.notifyDataSetChanged();
                                    checkDataList();
                                }
                            }
                        });

                        return false;
                    }
                });
            }

            //day separator
            if(getItemViewType(i) == 1) {
                rowHolder.titleTextView.setText((String) item);
            }

            return convertView;
        }
    }

    private static class RowHolder {
        public JLBG wrapper;
        public JLTV titleTextView;
        public JLTV2 urlTextView;
    }
}
