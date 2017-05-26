package com.browser.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.browser.R;
import com.browser.ui.widget.JView;
import com.browser.ui.widget.jlist.JLTV;
import com.browser.ui.widget.jlist.JLBG;
import com.browser.browser.uictrl.UIFind;

/**
 * Created by ozgur on 09.08.2016.
 */
public class FindInPageView extends JLBG implements JView, View.OnClickListener {

    private JLTV findNext, findPrevious, findCancel;
    private UIFind uiFind;

    public FindInPageView(Context context) {
        super(context);
        _init2();
    }

    public FindInPageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init2();
    }

    public FindInPageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _init2();
    }

    private void _init2() {
        View wrapper = inflate(getContext(), R.layout.find_in_page, this);
        findNext = (JLTV) wrapper.findViewById(R.id.FindNext);
        findPrevious = (JLTV) wrapper.findViewById(R.id.FindPrevious);
        findCancel = (JLTV) wrapper.findViewById(R.id.FindCancel);
        findNext.setOnClickListener(this);
        findPrevious.setOnClickListener(this);
        findCancel.setOnClickListener(this);
    }

    public void setFindCtrl(UIFind uiFind) {
        this.uiFind = uiFind;
    }

    @Override
    public void show() {
        setVisibility(VISIBLE);
    }

    @Override
    public void hide() {
        setVisibility(GONE);
    }

    @Override
    public boolean onBackPressed() {

        if(isShown()) {
            uiFind.exitFindMode();
            return true;
        }

        return false;
    }

    @Override
    public void onClick(View view) {
        if(view == findNext) {
            uiFind.findNext();
        }

        else if(view == findPrevious) {
            uiFind.findPrevious();
        }

        else if(view == findCancel) {
            uiFind.exitFindMode();
        }
    }
}
