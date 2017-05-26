package com.browser.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.browser.R;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.theme.themes.ThemeModel;
import com.browser.ui.widget.JTV;
import com.browser.ui.widget.jlist.JLTV;
import com.browser.utils.Utils;


/**
 * Created by ozgur on 21.06.2016.
 */
public class JListDialog extends JBaseDialog implements ThemeListener {

    private JTV mTitleTextView;
    private JTV mContentTextView;
    private ListView mListView;
    private ListAdapter listAdapter;
    private LayoutInflater mInflater;
    private LinearLayout mWrapper;
    private LinearLayout mTitleWrapper;

    private DialogClickListener dialogClickListener;

    /**/
    private String[] listItems;

    public JListDialog(Context context) {
        super(context);

        mInflater = LayoutInflater.from(context);

        /**/
        requestWindowFeature(Window.FEATURE_NO_TITLE);//before content
        setContentView(R.layout.j_list_dialog);

        setWH(Utils.dpToPx(getContext(), 300), ViewGroup.LayoutParams.WRAP_CONTENT); //after setContentView

        mTitleTextView = (JTV) findViewById(R.id.TitleTextView);
        mContentTextView = (JTV) findViewById(R.id.ContentTextView);
        mListView = (ListView) findViewById(R.id.ListView);
        mWrapper = (LinearLayout) findViewById(R.id.Wrapper);
        mTitleWrapper = (LinearLayout) findViewById(R.id.TitleWrapper);
        listAdapter = new ListAdapter();
        mListView.setAdapter(listAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                if(dialogClickListener!= null) {
                    dialogClickListener.onClick(listItems[pos], pos);
                }
                hide();
            }

        });

        ThemeController.getInstance().register(this);
        changeTheme();
    }

    @Override
    public void themeChanged() {
        changeTheme();
    }

    @Override
    public void changeTheme() {
        ThemeModel t = ThemeController.getInstance().getCurrentTheme();
        mWrapper.setBackgroundColor(t.dialogBg);
        mContentTextView.setTextColor(t.dialogButtonTxtColor);
        mTitleTextView.setTextColor(t.dialogTitleColor);
        mTitleWrapper.setBackgroundColor(t.dialogTitleBg);
    }

    public void setTitle(String title) {
        mTitleTextView.setText(title);
    }

    public void setTitle(int resId) {
        mTitleTextView.setText(resId);
    }

    public void setContent(String content) {
        mContentTextView.setText(content);
        mContentTextView.setVisibility(View.VISIBLE);
    }

    public void setDialogClickListener(DialogClickListener dialogClickListener) {
        this.dialogClickListener = dialogClickListener;
    }

    public void setListItems(String[] listItems) {
        this.listItems = listItems;
        listAdapter.notifyDataSetChanged();
    }

    public interface DialogClickListener {
        void onClick(String clickedItem, int position);
    }

    private class ListAdapter extends BaseAdapter {

        public int getCount() {
            return listItems == null ? 0 : listItems.length;
        }

        @Override
        public Object getItem(int i) {
            return listItems[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if(convertView == null) {
                convertView = mInflater.inflate(R.layout.j_list_dialog_item, parent, false);
            }

            JLTV textView = (JLTV) convertView.findViewById(R.id.TextView);
            textView.setText(listItems[position]);

            return convertView;
        }
    }

}
