package com.browser.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.browser.R;
import com.browser.adblock.AdBlockActivity;
import com.browser.adblock.AdBlockDb;
import com.browser.base.BaseActivity;
import com.browser.browser.CPM;
import com.browser.ui.widget.JBB;
import com.browser.ui.widget.JTB;
import com.browser.ui.widget.jlist.JLTV;

/**
 * Created by ozgur on 12.08.2016.
 */
public class MyStatsActivity extends BaseActivity implements JBB.JBottomBarClickListener, View.OnClickListener {

    private JTB topBar;
    private JBB bottomBar;
    private CPM cpm;
    private JLTV adblockStatTextView;
    private JLTV totalTabStatTextView;
    private JLTV totalUrlStatTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        topBar = (JTB) findViewById(R.id.TopBar);
        topBar.setTitle(R.string.my_stats);
        bottomBar = (JBB) findViewById(R.id.BottomBar);
        bottomBar.setjBottomBarClickListener(this);
        bottomBar.hideRemove();
        cpm = new CPM(this);
        adblockStatTextView = (JLTV) findViewById(R.id.AdBlockStatTextView);
        totalTabStatTextView = (JLTV) findViewById(R.id.TotalTabStatTextView);
        totalUrlStatTextView = (JLTV) findViewById(R.id.TotalUrlStatTextView);
        findViewById(R.id.GoAdBlockActivity).setOnClickListener(this);

        /**/
        setBackgroundTheme();
        initTotalUrlCount();
        initTotalTabCount();
    }

    private void initTotalUrlCount() {
        long totalUrlCount = cpm.getTotalUrlCount();
        String txt = getString(R.string.total_url_count);
        totalUrlStatTextView.setText(txt.replace("{total}", "" + totalUrlCount));
    }

    private void initTotalTabCount() {
        int totalTabCount = cpm.getTotalTabCount();
        String txt = getString(R.string.total_tab_count);
        totalTabStatTextView.setText(txt.replace("{total}", "" + totalTabCount));
    }

    private void initAdBlockStat() {
        long total = AdBlockDb.getInstance(this).getTotalBlockCount();
        long sites = AdBlockDb.getInstance(this).getTotalProtectedUrls();
        String txt = getString(R.string.total_blocked_ad_row);
        adblockStatTextView.setText(txt.replace("{sites}", "" + sites).replace("{total}", ""+total));
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onRemoveClick() {}

    @Override
    protected void onResume() {
        super.onResume();
        initAdBlockStat();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.GoAdBlockActivity) {
            startActivity(new Intent(this, AdBlockActivity.class));
        }
    }
}
