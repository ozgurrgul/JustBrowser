package com.browser.suggestion;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.browser.R;
import com.browser.browser.uictrl.UIControllerImpl;
import com.browser.history.HistoryItem;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.ui.widget.JInterceptor;
import com.browser.ui.widget.JView;
import com.browser.ui.widget.jlist.JLTV;
import com.browser.ui.widget.jlist.JLTV2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ozgur on 07.08.2016.
 */
public class SuggestionList extends LinearLayout implements ThemeListener, JView, SuggestionGoogleTaskListener, SuggestionHistoryTaskListener, JInterceptor.EmptySpaceClickListener {

    private ListView listView;
    private UIControllerImpl uiController;
    private SuggAdapter suggAdapter;
    private SuggestionGoogleTask suggestionGoogleTask;
    private SuggestionHistoryTask suggestionHistoryTask;
    private LayoutInflater layoutInflater;
    private ArrayList<HistoryItem> items;
    private JInterceptor jInterceptor;
    private String lastTxt;

    public SuggestionList(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    private void _init() {
        View wrapper = inflate(getContext(), R.layout.suggestion_list, this);
        listView = (ListView) wrapper.findViewById(R.id.ListView);
        layoutInflater = LayoutInflater.from(getContext());

        items = new ArrayList<>();

        suggAdapter = new SuggAdapter();
        listView.setAdapter(suggAdapter);


        /**/
        suggestionGoogleTask = new SuggestionGoogleTask(this);
        suggestionHistoryTask = new SuggestionHistoryTask(getContext(), this);

        jInterceptor = (JInterceptor) wrapper.findViewById(R.id.TouchInterceptor);
        jInterceptor.setEmptySpaceClickListener(this);

        /**/
        ThemeController.getInstance().register(this);
        changeTheme();

        /**/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if(i >= items.size()) {
                    return;
                }

                HistoryItem n = items.get(i);

                if(n.getType() == HistoryItem.HISTORY) {
                    uiController.loadUrl(n.getUrl(), true);
                }

                if(n.getType() == HistoryItem.SUGGESTION) {
                    uiController.search(n.getUrl());
                }

                uiController.hideSuggestionList();
                uiController.collapseUrlbar();
            }
        });
    }

    @Override
    public void themeChanged() {
        changeTheme();
    }

    @Override
    public void changeTheme() {

    }

    @Override
    public void show() {
        setVisibility(VISIBLE);
        reset();
    }

    @Override
    public void hide() {
        setVisibility(GONE);
        reset();
    }

    private void reset() {
        items.clear();
        suggAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onBackPressed() {

        if(isShown()) {
            hide();
            return true;
        }

        return false;
    }

    public void setUiController(UIControllerImpl uiController) {
        this.uiController = uiController;
    }

    public void updateSuggestionList(String txt) {

        lastTxt = txt;

        Log.d("SUGG1", "updateSugg txt: " + txt);

        if(TextUtils.isEmpty(txt)) {
            reset();
            return;
        }

        reset();

        suggestionGoogleTask.query(txt);
        suggestionHistoryTask.query(txt);
    }

    @Override
    public void onGoogleSuggestionsLoaded(ArrayList<HistoryItem> suggestionList) {
        addItems(suggestionList);
    }

    @Override
    public void onHistorySuggestionsLoaded(List<HistoryItem> suggestionList) {
        addItems(suggestionList);
    }

    private void addItems(List<HistoryItem> suggestionList) {

        if(TextUtils.isEmpty(lastTxt)) {
            reset();
            return;
        }

        items.addAll(suggestionList);
        sort();
        suggAdapter.notifyDataSetChanged();
    }

    private void sort() {
        Collections.sort(items, new Comparator<HistoryItem>() {
            @Override
            public int compare(HistoryItem o, HistoryItem o2) {

                if(o.getType() == HistoryItem.SUGGESTION && o2.getType() == HistoryItem.HISTORY) {
                    return 1;
                }

                if(o.getType() == HistoryItem.SUGGESTION && o2.getType() == HistoryItem.SUGGESTION) {
                    return o.getTitle().compareTo(o2.getTitle());
                }

                return 0;
            }
        });
    }

    @Override
    public void onEmptySpaceClicked(int id) {
        uiController.onExitQueryMode();
        uiController.getBottomPart().bottomFirstMenuAndUrlBar.urlbar.collapse();
    }

    private class SuggAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public HistoryItem getItem(int i) {
            return items.get(i);
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
            return getItem(position).getType();
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {

            RowHolder rowHolder;
            HistoryItem n = getItem(i);

            if(convertView == null) {
                convertView = layoutInflater.inflate(R.layout.suggestion_item, viewGroup, false);
                rowHolder = new RowHolder();
                rowHolder.titleTextView = (JLTV) convertView.findViewById(R.id.TitleTextView);
                rowHolder.urlTextView = (JLTV2) convertView.findViewById(R.id.UrlTextView);
                convertView.setTag(rowHolder);

            } else {
                rowHolder = (RowHolder) convertView.getTag();
            }

            rowHolder.titleTextView.setText(n.getTitle());

            if(n.getType() == HistoryItem.HISTORY) {
                rowHolder.urlTextView.setText(n.getUrl());
                rowHolder.urlTextView.setVisibility(VISIBLE);
            } else {
                rowHolder.urlTextView.setVisibility(GONE);
            }

            return convertView;
        }
    }

    private static class RowHolder {
        public JLTV titleTextView;
        public JLTV2 urlTextView;
    }
}
