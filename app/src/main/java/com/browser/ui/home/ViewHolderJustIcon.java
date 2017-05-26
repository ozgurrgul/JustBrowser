package com.browser.ui.home;

import android.view.View;
import android.widget.ImageView;

import com.browser.R;

public class ViewHolderJustIcon extends BaseViewHolder {

    public ImageView imageView;

    public ViewHolderJustIcon(View view) {
        super(view);

        imageView = (ImageView) view.findViewById(R.id.ImageView);
    }
}