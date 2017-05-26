package com.browser.dial;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.browser.R;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.theme.themes.ThemeModel;
import com.browser.ui.widget.JTV;
import com.browser.utils.BitmapUtils;

/**
 * Created by ozgur on 08.06.2016.
 */
public class DialViewUrlItem extends LinearLayout implements ThemeListener {

    JTV mTextView; // url name
    ImageView mImageView;
    ImageView mRemoveButton;
    RelativeLayout mWrapper;
    private DialItem itemData;

    public DialViewUrlItem(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {

        View wrapper = inflate(context, R.layout.dial_url_item, this);

        mTextView = (JTV) wrapper.findViewById(R.id.TextView);
        mRemoveButton = (ImageView) wrapper.findViewById(R.id.RemoveButton);
        mImageView = (ImageView) wrapper.findViewById(R.id.ImageView);
        mWrapper = (RelativeLayout) wrapper.findViewById(R.id.Wrapper);

        ThemeController.getInstance().register(this);
        changeTheme(); //init colors onyl because there is drawView() method called from other component

        mRemoveButton.setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
    }


    public void showRemoveButton() {
        mRemoveButton.setVisibility(VISIBLE);
    }

    public void hideRemoveButton() {
        mRemoveButton.setVisibility(GONE);
    }

    @Override
    public void themeChanged() {
        changeTheme();
    }

    @Override
    public void changeTheme() {

        ThemeModel t = ThemeController.getInstance().getCurrentTheme();
        mTextView.setTextColor(t.dialItemTxtColor);
        mWrapper.setBackgroundResource(t.dialItemBg);

    }

    public void setItemData(DialItem itemData) {
        this.itemData = itemData;

        if(itemData.isPlusButton()) {

            mTextView.setVisibility(GONE);
            //center plus icon
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mImageView.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            mImageView.setLayoutParams(layoutParams);

            mImageView.setImageResource(R.drawable.ic_plus);

        } else {
            mTextView.setVisibility(VISIBLE);
            mTextView.setText(itemData.getTitle());

            //get bitmap from byte
            Bitmap bmp = BitmapUtils.getBitmap(itemData.getBitmap());

            //
            if(bmp != null) {
                mImageView.setImageBitmap(bmp);
            } else {
                mImageView.setImageResource(R.drawable.ic_world);
            }
        }
    }
}
