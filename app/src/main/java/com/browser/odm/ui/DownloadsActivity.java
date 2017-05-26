package com.browser.odm.ui;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.browser.R;
import com.browser.base.SwipeBackLayout;
import com.browser.odm.ODM;
import com.browser.odm.ui.fragments.CompletedFragment;
import com.browser.odm.ui.fragments.RunningFragment;
import com.browser.odm.clipboard.UrlCopiedPopupView;
import com.browser.odm.widget.SlidingTabLayout;
import com.browser.perm.PermissionsManager;
import com.browser.perm.PermissionsResultAction;
import com.browser.ui.dialog.JTwoEditTextDialog;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeModel;
import com.browser.ui.widget.JBB;


public class DownloadsActivity extends FragmentActivity implements View.OnClickListener, JBB.JBottomBarClickListener, SwipeBackLayout.SwipeBackListener {

    private FragmentAdapter mFragmentAdapter;
    private ViewPager mViewPager;
    private SlidingTabLayout mSlidingTabLayout;
    public ODM odm;
    private JBB bottomBar;

    /***/
    private SwipeBackLayout swipeBackLayout;
    private ImageView ivShadow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(getContainer());
        View view = LayoutInflater.from(this).inflate(R.layout.activity_dl, null);
        swipeBackLayout.addView(view);

        odm = ODM.getInstance();
        mFragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.ViewPager);
        mViewPager.setOffscreenPageLimit(2);
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.SlidingTabLayout);
        mViewPager.setAdapter(mFragmentAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);
        bottomBar = (JBB) findViewById(R.id.BottomBar);
        bottomBar.setjBottomBarClickListener(this);
        bottomBar.hideRemove();

        requestPermission();
        checkIntent(getIntent());

        ThemeModel t = ThemeController.getInstance().getCurrentTheme();
        mViewPager.setBackgroundColor(t.actBg);
    }

    private View getContainer() {
        RelativeLayout container = new RelativeLayout(this);
        swipeBackLayout = new SwipeBackLayout(this);
        swipeBackLayout.setOnSwipeBackListener(this);
        ivShadow = new ImageView(this);
        ivShadow.setBackgroundColor(Color.parseColor("#7f000000"));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        container.addView(ivShadow, params);
        container.addView(swipeBackLayout);
        return container;
    }

    @Override
    public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {
        ivShadow.setAlpha(1 - fractionScreen);
    }

    private void requestPermission() {
        PermissionsManager
                .getInstance()
                .requestPermissionsIfNecessaryForResult(
                        this,
                        new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        new PermissionsResultAction() {
                            @Override
                            public void onGranted() {

                            }

                            @Override
                            public void onDenied(String permission) {
                                toast(R.string.permission_denied);
                            }
                        });
    }

    private void checkIntent(Intent i) {

        if(i == null) {
            return;
        }

        if(!TextUtils.isEmpty(i.getDataString())) {
            String url = i.getDataString();
            showAddUrlDialog(url);
        }

        if(!TextUtils.isEmpty(i.getAction()) && UrlCopiedPopupView.URL_FROM_POPUP_SERVICE.equals(i.getAction())) {
            String url = i.getStringExtra("url");
            showAddUrlDialog(url);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("Main", "onNewIntent: " + intent);
        checkIntent(intent);
    }

    public void download(String url) {

        if(TextUtils.isEmpty(url)) {
            toast(R.string.url_is_empty);
            return;
        }

        odm.download(null, null, url);
    }

    public void toast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {

    }

    private void showAddUrlDialog(final String url) {

        JTwoEditTextDialog j = new JTwoEditTextDialog(this);
        j.setTitle(R.string.a_download);
        j.mEditTextOne.setText(url);
        j.mEditTextTwo.setVisibility(View.GONE);
        j.setNoButtonText(R.string.a_cancel);
        j.setYesButtonText(R.string.a_download);
        j.setDialogClickListener(new JTwoEditTextDialog.DialogClickListener() {
            @Override
            public void onYesOrNoClick(JTwoEditTextDialog.ButtonType buttonType) {
                if(buttonType == JTwoEditTextDialog.ButtonType.POSITIVE) {
                    download(url);
                }
            }
        });
        j.show();

    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onRemoveClick() {}

    public class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if(position == 0) {
                return new RunningFragment();
            }

            if(position == 1) {
                return new CompletedFragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {

                case 0:
                    return getString(R.string.w_running);
                case 1:
                    return getString(R.string.w_completed);

            }
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
