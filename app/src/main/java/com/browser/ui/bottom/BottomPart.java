package com.browser.ui.bottom;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.browser.R;
import com.browser.tab.TabView;
import com.browser.ui.bottom.menu.BottomSecondMenu;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.ui.widget.JView;
import com.browser.browser.uictrl.UIControllerImpl;
import com.browser.browser.uictrl.UIKeyboard;

/**
 * Created by ozgur on 04.08.2016.
 */
public class BottomPart extends LinearLayout implements JView, ThemeListener, UIKeyboard {

    public BottomFirstMenuAndUrlBar bottomFirstMenuAndUrlBar;
    private BottomSecondMenu bottomSecondMenu;
    private LinearLayout mBottomPartWrapper;
    private View mBottomDivider;
    private JViewPager viewPager;

    /**/
    private boolean secondMenuChosen;
    private UIControllerImpl uiController;

    public BottomPart(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
    }

    private void _init() {
        View wrapper = inflate(getContext(), R.layout.bottom_part, this);

        /**/
        mBottomDivider = wrapper.findViewById(R.id.BottomDivider);
        mBottomPartWrapper = (LinearLayout) wrapper.findViewById(R.id.BottomPartWrapper);

        /**/
        bottomFirstMenuAndUrlBar = new BottomFirstMenuAndUrlBar(getContext());
        bottomSecondMenu = new BottomSecondMenu(getContext());
        bottomSecondMenu.setParent(this);

        /**/
        viewPager = (JViewPager) wrapper.findViewById(R.id.ViewPager);
        viewPager.setAdapter(new ViewPagerAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if(bottomFirstMenuAndUrlBar.isUrlBarExpanded() && positionOffset > 0) {
                    //viewPager.setCurrentItem(0, false);
                }

            }

            @Override
            public void onPageSelected(int position) {
                secondMenuChosen = (position == 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}

        });

        ThemeController.getInstance().register(this);
        changeTheme();
    }

    public void anim(final int page, int delay) {
        viewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(page, true);
            }
        }, delay);
    }

    public void setUiController(UIControllerImpl uiController) {
        this.uiController = uiController;
        bottomFirstMenuAndUrlBar.setUiController(uiController);
        bottomSecondMenu.setUiController(uiController);
    }

    public void lockPager() {
        viewPager.setEnabled(false);
    }

    public void unlockPager() {
        viewPager.setEnabled(true);
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

        if(secondMenuChosen) {
            secondMenuChosen = false;
            movePager(0);
            return true;
        }

        if(bottomFirstMenuAndUrlBar.urlbar.onBackPressed()) {
            return true;
        }

        return bottomFirstMenuAndUrlBar.onBackPressed();
    }

    public void setTabsCount(int count) {
        bottomFirstMenuAndUrlBar.setTabsCount(count);
    }

    public void movePager(int pos) {
        viewPager.setCurrentItem(pos);
        bottomFirstMenuAndUrlBar.hideBottomMenu();
    }

    @Override
    public void themeChanged() {
        changeTheme();
    }

    @Override
    public void changeTheme() {
        int bgColor = ThemeController.getInstance().getCurrentTheme().bottomBg;
        int dividerColor = ThemeController.getInstance().getCurrentTheme().bottomDivider;

        mBottomPartWrapper.setBackgroundColor(bgColor);
        mBottomDivider.setBackgroundColor(dividerColor);
    }

    public void onPageFinished(TabView tabView) {
        bottomFirstMenuAndUrlBar.onPageFinished(tabView);
    }

    public void onPageStarted(TabView tabView) {
        bottomFirstMenuAndUrlBar.onPageStarted(tabView);
    }

    public void onShowHome(TabView tabView) {
        bottomFirstMenuAndUrlBar.onShowHome(tabView);
        bottomSecondMenu.onShowHome(tabView);
    }

    public void onHideHome(TabView tabView) {
        bottomFirstMenuAndUrlBar.onHideHome(tabView);
        bottomSecondMenu.onHideHome(tabView);
    }

    @Override
    public void onKeyboardOpen() {}

    @Override
    public void onKeyboardHidden() {
        if(bottomFirstMenuAndUrlBar.isUrlBarExpanded()) {
            bottomFirstMenuAndUrlBar.urlbar.collapse();
        }
    }

    public void onPageProgress(TabView tabView) {
        bottomFirstMenuAndUrlBar.urlbar.onPageProgress(tabView);
    }

    public void hideProgress() {
        bottomFirstMenuAndUrlBar.urlbar.hideProgress();
    }

    public void showProgress() {
        bottomFirstMenuAndUrlBar.urlbar.showProgress();
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View v = null;

            if(position == 0) {
                v = bottomFirstMenuAndUrlBar;
            }

            if(position == 1) {
                v = bottomSecondMenu;
            }

            container.addView(v);

            return v;
        }
    }

}
