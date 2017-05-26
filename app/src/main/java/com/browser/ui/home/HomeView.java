package com.browser.ui.home;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.browser.R;
import com.browser.dial.DialView;
import com.browser.dial.ViewHolderDialView;
import com.browser.dial.ViewHolderExitMode;
import com.browser.my.MyStatsActivity;
import com.browser.ui.widget.JView;
import com.browser.browser.uictrl.UIConfiguration;
import com.browser.browser.uictrl.UIControllerImpl;

import java.util.ArrayList;

/**
 * Created by ozgur on 04.08.2016.
 */
public class HomeView extends LinearLayout implements JView, UIConfiguration {

    private RecyclerView recyclerView;
    private LayoutInflater layoutInflater;
    private ArrayList<HomeViewRowType> dataList = new ArrayList<>();
    private HomeViewAdapter homeViewAdapter;

    /**/
    public DialView dialView;
    private UIControllerImpl uiController;

    /**/
    private Animation swingAnim;
    private boolean animIsRunning;


    public HomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    private void _init() {
        View wrapper = inflate(getContext(), R.layout.home, this);
        recyclerView = (RecyclerView) wrapper.findViewById(R.id.RecyclerView);

        /**/
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);

        recyclerView.setLayoutManager(mLayoutManager);

        /**/
        layoutInflater = LayoutInflater.from(getContext());

        swingAnim = AnimationUtils.loadAnimation(getContext(), R.anim.swing);
        homeViewAdapter = new HomeViewAdapter();
        recyclerView.setAdapter(homeViewAdapter);

    }

    @Override
    public void show() {
        setVisibility(VISIBLE);
        homeViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void hide() {
        setVisibility(GONE);
    }

    @Override
    public boolean onBackPressed() {
        return dialView.onBackPressed();
    }

    public void setUiController(UIControllerImpl uiController) {
        this.uiController = uiController;

        if(uiController.getCpm().isShowLogo()) {
            dataList.add(HomeViewRowType.ICON_JUST);
        }

        dataList.add(HomeViewRowType.DIAL);
        dialView = new DialView(uiController);
    }

    public void enterDialEditMode() {
        dataList.add(HomeViewRowType.DIAL_EXIT_MODE);
        homeViewAdapter.notifyDataSetChanged();
        dialView.enterDialEditMode();
    }

    public void exitDialEditMode() {
        int index = dataList.indexOf(HomeViewRowType.DIAL_EXIT_MODE);

        if(index != -1) {
            dataList.remove(index);
            homeViewAdapter.notifyDataSetChanged();
        }

        dialView.exitDialEditMode();
    }

    @Override
    public void _onConfigurationChanged(Configuration newConfig) {
        dialView._onConfigurationChanged(newConfig);
    }

    public class HomeViewAdapter extends RecyclerView.Adapter<BaseViewHolder>{

        @Override
        public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            BaseViewHolder viewHolder = null;

            if(viewType == HomeViewRowType.DIAL.getType()) {
                viewHolder = new ViewHolderDialView(dialView);
            }

            if(viewType == HomeViewRowType.DIAL_EXIT_MODE.getType()) {
                viewHolder = new ViewHolderExitMode(layoutInflater.inflate(R.layout.home_dial_exit_mode, parent, false));
            }

            if(viewType == HomeViewRowType.RATE_US.getType()) {
                viewHolder = new ViewHolderRateUs(layoutInflater.inflate(R.layout.home_rate_us_row, parent, false));
            }

            if(viewType == HomeViewRowType.ICON_JUST.getType()) {
                viewHolder = new ViewHolderJustIcon(layoutInflater.inflate(R.layout.home_just_icon, parent, false));
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final BaseViewHolder holder, final int position) {

            int type = holder.getItemViewType();

            if(type == HomeViewRowType.DIAL_EXIT_MODE.getType()) {
                ViewHolderExitMode vh = (ViewHolderExitMode) holder;
                vh.itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uiController.exitDialEditMode();
                    }
                });
            }

            else if(type == HomeViewRowType.RATE_US.getType()) {
                ViewHolderRateUs vh = (ViewHolderRateUs) holder;
                vh.itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO
                    }
                });
            }

            else if(type == HomeViewRowType.ICON_JUST.getType()) {

                ViewHolderJustIcon vh = (ViewHolderJustIcon) holder;

                vh.imageView.startAnimation(swingAnim);

                vh.itemView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uiController.startAct(MyStatsActivity.class);
                    }
                });
            }

        }

        @Override
        public int getItemViewType(int position) {

            if(_getItemType(position) == HomeViewRowType.DIAL){
                return HomeViewRowType.DIAL.getType();
            }

            else if(_getItemType(position) == HomeViewRowType.DIAL_EXIT_MODE){
                return HomeViewRowType.DIAL_EXIT_MODE.getType();
            }

            else if(_getItemType(position) == HomeViewRowType.RATE_US) {
                return HomeViewRowType.RATE_US.getType();
            }

            else if(_getItemType(position) == HomeViewRowType.MOST_VISITED) {
                return HomeViewRowType.MOST_VISITED.getType();
            }

            else if(_getItemType(position) == HomeViewRowType.ICON_JUST) {
                return HomeViewRowType.ICON_JUST.getType();
            }

            else return -1;
        }

        private HomeViewRowType _getItemType(int pos) {
            return dataList.get(dataList.size() - pos - 1);
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }
    }

    public void updateIcon() {

        int index = dataList.indexOf(HomeViewRowType.ICON_JUST);

        if(uiController.getCpm().isShowLogo()) {
            if(index == -1) {
                dataList.add(0, HomeViewRowType.ICON_JUST);
            }
        } else {
            if(index != -1) {
                dataList.remove(index);

            }
        }

        homeViewAdapter.notifyDataSetChanged();
    }

}
