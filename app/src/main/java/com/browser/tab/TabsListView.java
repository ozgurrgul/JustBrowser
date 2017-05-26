package com.browser.tab;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.browser.R;
import com.browser.browser.uictrl.UIControllerImpl;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.theme.themes.ThemeModel;
import com.browser.ui.widget.JInterceptor;
import com.browser.ui.widget.JView;

/**
 * Created by ozgur on 06.08.2016.
 */
public class TabsListView extends LinearLayout implements JView, JInterceptor.EmptySpaceClickListener, View.OnClickListener, ThemeListener {

    private JInterceptor mTabsListJInterceptor;
    private RelativeLayout mTabsListBottomBackground;
    private RecyclerView recyclerView;
    private TabsAdapter tabsAdapter;
    private UIControllerImpl uiController;
    private ImageView mNewTabButton, mNewIncognitoTabButton;
    private boolean lockTouch;

    /* swiping */
    ItemTouchHelper.SimpleCallback simpleItemTouchCallback;

    public TabsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    private void _init() {

        View wrapper = inflate(getContext(), R.layout.tabs_list, this);
        mTabsListJInterceptor = (JInterceptor) wrapper.findViewById(R.id.TabsListTouchInterceptor);
        mTabsListJInterceptor.setEmptySpaceClickListener(this);
        recyclerView = (RecyclerView) wrapper.findViewById(R.id.RecyclerView);

        /**/
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        tabsAdapter = new TabsAdapter();
        recyclerView.setAdapter(tabsAdapter);
        mNewTabButton = (ImageView) wrapper.findViewById(R.id.NewTabButton);
        mNewIncognitoTabButton = (ImageView) wrapper.findViewById(R.id.NewIncognitoTabButton);
        mTabsListBottomBackground = (RelativeLayout) wrapper.findViewById(R.id.TabsListBottomBackground);
        mNewTabButton.setOnClickListener(this);
        mNewIncognitoTabButton.setOnClickListener(this);
        ThemeController.getInstance().register(this);
        changeTheme();

        simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                TabData tabData = getItem(viewHolder.getAdapterPosition());
                uiController.onCloseTab(tabData);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(this.recyclerView);
    }

    @Override
    public void show() {
        lockTouch = false;
        setVisibility(View.VISIBLE);
    }

    @Override
    public void hide() {
        lockTouch = false;
        setVisibility(GONE);
    }

    @Override
    public boolean onBackPressed() {

        if(isShown()) {
            uiController.hideTabsList();
            return true;
        }

        return false;
    }

    @Override
    public void onEmptySpaceClicked(int id) {
        uiController.hideTabsList();
    }

    public void setUiController(UIControllerImpl uiController) {
        this.uiController = uiController;
    }

    public void onNewTabAdded(TabView tabView) {
        update();
    }

    public void onPageFinished(TabView tabView) {
        update();
    }

    public void onPageStarted(TabView tabView) {
        update();
    }

    public void onReceivedTitle(TabView tabView) {
        update();
    }

    public void onReceivedIcon(TabView tabView) {
        update();
    }

    public void onShowHome(TabView tabView) {
        update();
    }

    public void onHideHome(TabView tabView) {
        update();
    }

    public void onTabClosed() {
        update();
    }

    public void onTabChosen(TabView tabView) {
        update();
    }

    private void update() {
        tabsAdapter.notifyDataSetChanged();
    }

    private TabData getItem(int pos) {
        return uiController.getTabsData().get(uiController.getTabsData().size() - 1 - pos);
    }

    @Override
    public void onClick(View view) {

        if (lockTouch) return;
        lockTouch = true;

        if(view == mNewTabButton) {
            uiController.newTab();
        }

        if(view == mNewIncognitoTabButton) {
            uiController.newIncognitoTab();
        }
    }

    @Override
    public void themeChanged() {
        changeTheme();
    }

    @Override
    public void changeTheme() {
        ThemeModel t = ThemeController.getInstance().getCurrentTheme();
        mTabsListBottomBackground.setBackgroundColor(t.tabsListBg);
        recyclerView.setBackgroundColor(t.tabsListBg);
        mNewTabButton.setColorFilter(t.bmiIconColor, PorterDuff.Mode.SRC_ATOP);
        update();
    }

    private class TabsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TabsListItemViewHolder(new TabListItemView(getContext()));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            TabsListItemViewHolder rowHolder = (TabsListItemViewHolder) holder;

            rowHolder.tabListItemView.setLayoutParams(new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            ));

            final TabData tabData = getItem(position);

            rowHolder.tabListItemView.setTabTitle(tabData.getTitle());
            rowHolder.tabListItemView.mWrapper.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    uiController.onChooseTab(tabData);
                }
            });

            rowHolder.tabListItemView.closeImageViewWrapper.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    uiController.onCloseTab(tabData);
                }
            });

            if(tabData.isIncognito()) {
                rowHolder.tabListItemView.iconImageView.clearColorFilter();
                rowHolder.tabListItemView.iconImageView.setImageResource(R.drawable.ic_incog);
            } else {

                if(tabData.getFavicon() != null) {
                    rowHolder.tabListItemView.iconImageView.clearColorFilter();
                    rowHolder.tabListItemView.iconImageView.setImageBitmap(tabData.getFavicon());
                } else {
                    rowHolder.tabListItemView.iconImageView.setImageResource(R.drawable.ic_world);
                    rowHolder.tabListItemView.iconImageView.setColorFilter(
                            ThemeController.getInstance().getCurrentTheme().tabsListItemTxtColor, PorterDuff.Mode.SRC_ATOP
                    );
                }
            }

            if(tabData.isBrowsing()) {
                rowHolder.tabListItemView.mWrapper.setBackgroundResource(R.drawable.tabs_list_item_selected_bg);
            } else {
                rowHolder.tabListItemView.changeTheme();
            }

        }

        @Override
        public int getItemCount() {
            return uiController == null ? 0 : uiController.getTabsData().size();
        }
    }

    private class TabsListItemViewHolder extends RecyclerView.ViewHolder {

        final TabListItemView tabListItemView;

        public TabsListItemViewHolder(View itemView) {
            super(itemView);
            tabListItemView = (TabListItemView) itemView;
        }
    }

}
