package com.browser.support;

import android.os.Bundle;
import android.view.View;

import com.browser.R;
import com.browser.base.BaseActivity;
import com.browser.ui.widget.JBB;
import com.browser.ui.widget.JTB;
import com.browser.utils.Utils;

/**
 * Created by ozgur on 11.08.2016.
 */
public class SupportActivity extends BaseActivity implements JBB.JBottomBarClickListener, View.OnClickListener {

    private JTB topBar;
    private JBB bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        topBar = (JTB) findViewById(R.id.TopBar);
        topBar.setTitle(R.string.s_support);
        bottomBar = (JBB) findViewById(R.id.BottomBar);
        bottomBar.setjBottomBarClickListener(this);
        bottomBar.hideRemove();
        setBackgroundTheme();
        findViewById(R.id.BuyProButton).setOnClickListener(this);
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onRemoveClick() {}

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.BuyProButton) {
            Utils.openPlay(this, getPackageName() + ".pro");
        }
    }
}
