package com.browser.adblock;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.browser.R;
import com.browser.base.BaseActivity;
import com.browser.ui.dialog.JDialog;
import com.browser.ui.widget.JBB;
import com.browser.ui.widget.JListView;
import com.browser.ui.widget.JTB;
import com.browser.ui.widget.NoDataToSee;
import com.browser.ui.widget.jlist.JLBG;
import com.browser.ui.widget.jlist.JLTV;
import com.browser.ui.widget.jlist.JLTV2;

import java.util.ArrayList;

/**
 * Created by ozgur on 06.08.2016.
 */
public class AdBlockActivity extends BaseActivity implements JBB.JBottomBarClickListener, JListView.ListViewListener {

    private JTB topBar;
    private JBB bottomBar;
    private JListView listView;
    private AdBlockAdapter adBlockAdapter;
    private LayoutInflater mLayoutInflater;
    private NoDataToSee noDataToSee;
    private int limit = 15;
    private int offset = 0;
    private ArrayList<com.browser.adblock.AdBlockItem> dataList = new ArrayList<>();
    private AsyncTask<Void, Void, ArrayList<AdBlockItem>> asyncTask;
    private JLTV adblockStatTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adblock);
        mLayoutInflater = LayoutInflater.from(this);
        topBar = (JTB) findViewById(R.id.TopBar);
        topBar.setTitle(R.string.adblocking);
        bottomBar = (JBB) findViewById(R.id.BottomBar);
        bottomBar.setjBottomBarClickListener(this);
        listView = (JListView) findViewById(R.id.ListView);
        listView.setListViewListener(this);
        noDataToSee = (NoDataToSee) findViewById(R.id.NoDataToSee);
        adBlockAdapter = new AdBlockAdapter();
        listView.setAdapter(adBlockAdapter);
        adblockStatTextView = (JLTV) findViewById(R.id.AdBlockStatTextView);

        setBackgroundTheme();
        getData();
        initAdBlockStat();
    }

    private void initAdBlockStat() {
        long total = AdBlockDb.getInstance(this).getTotalBlockCount();
        long sites = AdBlockDb.getInstance(this).getTotalProtectedUrls();
        setAdblockStatTextView(total, sites);
    }

    private void setAdblockStatTextView(long total, long sites) {
        String txt = getString(R.string.total_blocked_ad_row);
        adblockStatTextView.setText(txt.replace("{sites}", "" + sites).replace("{total}", ""+total));
    }

    private void getData() {

        asyncTask = new AsyncTask<Void, Void, ArrayList<AdBlockItem>>() {

            @Override
            protected ArrayList<AdBlockItem> doInBackground(Void... voids) {
                return AdBlockDb.getInstance(AdBlockActivity.this).getBlockedDomains(limit, offset);
            }

            @Override
            protected void onPostExecute(ArrayList<AdBlockItem> historyItems) {

                dataList.addAll(historyItems);
                adBlockAdapter.notifyDataSetChanged();

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
                    AdBlockDb.getInstance(AdBlockActivity.this).clearAll();
                    dataList.clear();
                    offset = 0;
                    setAdblockStatTextView(0, 0);
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

    private class AdBlockAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public AdBlockItem getItem(int i) {
            return dataList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return dataList.get(i).get_id();
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {

            RowHolder rowHolder;
            final AdBlockItem item = getItem(i);

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

            rowHolder.titleTextView.setText(item.getUrl());
            rowHolder.urlTextView.setText("("+item.getTimes()+") ads");

            return convertView;
        }
    }

    private static class RowHolder {
        public JLBG wrapper;
        public JLTV titleTextView;
        public JLTV2 urlTextView;
    }
}
