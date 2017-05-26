package com.browser.odm.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.browser.R;
import com.browser.odm.ODMItem;
import com.browser.odm.ui.DownloadsActivity;
import com.browser.ui.widget.NoDataToSee;

import java.util.ArrayList;

/**
 * Created by ozgur on 07.07.2016.
 */
public abstract class BaseFragment extends Fragment {

    /**/
    public abstract void fragmentCreated();
    protected LayoutInflater mInflater;

    /**/
    protected DownloadsActivity mainActivity;
    protected RecyclerView mRecyclerView;
    protected NoDataToSee mNoDataToSee;

    /**/
    protected ArrayList<ODMItem> dataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity = (DownloadsActivity) getActivity();
        mInflater = LayoutInflater.from(mainActivity);

        /**/
        mRecyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView);
        mNoDataToSee = (NoDataToSee) view.findViewById(R.id.NoDataToSee);

        LinearLayoutManager llm = new LinearLayoutManager(mainActivity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(llm);

        fragmentCreated();
    }

    protected void Log(String s) {
        Log.d(getClass().getSimpleName(), s);
    }

    protected void snack(int resId) {
       mainActivity.toast(resId);
    }
}
