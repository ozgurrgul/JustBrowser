package com.browser.settings;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;

import com.browser.R;
import com.browser.ui.dialog.JListDialog;
import com.browser.ui.widget.jlist.JLTV2;


/**
 * Created by ozgur on 28.06.2016.
 */
public class JListPreference extends ListPreference {

    public void setItemChosenListener(ItemChosenListener itemChosenListener) {
        this.itemChosenListener = itemChosenListener;
    }

    public interface ItemChosenListener {
        void onItemChosen(int pos);
    }

    private ItemChosenListener itemChosenListener;

    public JListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.j_pref_list);
    }

    @Override
    protected void onBindView(View wrapper) {
        super.onBindView(wrapper);
        initView(wrapper);
    }

    protected void initView(final View wrapper) {

        ((JLTV2) wrapper.findViewById(R.id.SelectedTextView)).setText(getEntry());

        final String[] mEntriesString = new String[getEntries().length];
        int i=0;

        for(CharSequence ch: getEntries()){
            mEntriesString[i++] = ch.toString();
        }

        wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JListDialog c = new JListDialog(getContext());
                c.setListItems(mEntriesString);
                c.setTitle(getContext().getString(R.string.options));
                c.setDialogClickListener(new JListDialog.DialogClickListener() {
                    @Override
                    public void onClick(String clickedItem, int position) {
                        setValueIndex(position);
                        ((JLTV2) wrapper.findViewById(R.id.SelectedTextView)).setText(getEntry());

                        //inform listener for
                        //CustomProxySettingsRowPreferenceForManagingDialogPlusChoices row
                        if(itemChosenListener != null) {
                            itemChosenListener.onItemChosen(position);
                        }
                    }
                });
                c.show();
            }
        });

    }



}
