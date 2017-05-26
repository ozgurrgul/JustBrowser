package com.browser.dial;

import android.content.res.Configuration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.h6ah4i.android.widget.advrecyclerview.animator.GeneralItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.animator.RefactoredDefaultItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;
import com.browser.R;
import com.browser.theme.themes.ThemeListener;
import com.browser.ui.widget.JView;
import com.browser.browser.uictrl.UIConfiguration;
import com.browser.browser.uictrl.UIController;


/**
 * Created by ozgur on 06.06.2016.
 */
public class DialView extends LinearLayout implements ThemeListener, JView, UIConfiguration {

    public static final int LONG_PRESS_TIMEOUT = 750;
    public static final int LONG_PRESS_TIMEOUT_WHEN_EDITING = 150;

    /* recycler view stuff */
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    public DialViewAdapter mAdapter;
    private RecyclerView.Adapter mWrappedAdapter;
    public RecyclerViewDragDropManager mRecyclerViewDragDropManager;

    /**/
    public UIController uiController;

    public DialView(UIController uiController) {
        super(uiController.getActivity());
        this.uiController = uiController;
        init();
    }

    private void init() {
        View wrapper = LayoutInflater.from(getContext()).inflate(R.layout.dial, this);
        mRecyclerView = (RecyclerView) wrapper.findViewById(R.id.RecyclerView);

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float childWidth = 65f;
        int len = (int) Math.floor(dpWidth / childWidth);

        mLayoutManager = new GridLayoutManager(getContext(), len , GridLayoutManager.VERTICAL, false);
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setInitiateOnLongPress(true);
        mRecyclerViewDragDropManager.setInitiateOnMove(false);
        mRecyclerViewDragDropManager.setLongPressTimeout(LONG_PRESS_TIMEOUT);

        /**/
        final DialViewAdapter myItemAdapter = new DialViewAdapter(this);
        mAdapter = myItemAdapter;

        /**/
        mWrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(myItemAdapter); // wrap for dragging

        final GeneralItemAnimator animator = new RefactoredDefaultItemAnimator();

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mWrappedAdapter);  // requires *wrapped* adapter
        mRecyclerView.setItemAnimator(animator);

        mRecyclerViewDragDropManager.attachRecyclerView(mRecyclerView);
    }

    @Override
    public boolean onBackPressed() {
        return mAdapter.onBackPressed();
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void themeChanged() {}

    @Override
    public void changeTheme() {}


    @Override
    public void _onConfigurationChanged(Configuration newConfig) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float childWidth = 65f;
        int len = (int) Math.floor(dpWidth / childWidth);
        mLayoutManager = new GridLayoutManager(getContext(), len , GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    public void enterDialEditMode() {
        mAdapter.enterDialEditMode();
    }

    public void exitDialEditMode() {
        mAdapter.exitDialEditMode();
    }
}
