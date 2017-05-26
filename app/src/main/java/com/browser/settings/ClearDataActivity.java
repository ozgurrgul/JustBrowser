package com.browser.settings;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ToggleButton;

import com.browser.R;
import com.browser.base.BaseActivity;
import com.browser.history.HistoryDb;
import com.browser.ui.widget.JBB;
import com.browser.ui.widget.JTB;
import com.browser.utils.Utils;

/**
 * Created by ozgur on 12.08.2016.
 */
public class ClearDataActivity extends BaseActivity implements JBB.JBottomBarClickListener, View.OnClickListener {

    private JTB topBar;
    private JBB bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_data);

        topBar = (JTB) findViewById(R.id.TopBar);
        topBar.setTitle(R.string.s_clear_data);
        bottomBar = (JBB) findViewById(R.id.BottomBar);
        bottomBar.setjBottomBarClickListener(this);
        bottomBar.hideRemove();

        setBackgroundTheme();
        findViewById(R.id.DeleteButton).setOnClickListener(this);
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onRemoveClick() {

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.DeleteButton) {

            boolean history = ((ToggleButton) findViewById(R.id.ClearBrowsingHistory)).isChecked();
            boolean cache = ((ToggleButton) findViewById(R.id.ClearBrowsingCache)).isChecked();
            boolean cookie = ((ToggleButton) findViewById(R.id.ClearCookie)).isChecked();

            if(history) {
                HistoryDb.getInstance(this).clearAll();
            }

            if(cache) {
                WebView w = new WebView(this);
                w.clearCache(true);
                deleteDatabase("webview.db");
                deleteDatabase("webviewCache.db");
            }

            if(cookie) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.clearCookies(ClearDataActivity.this);
                    }
                }).start();
            }

            toast(R.string.cleaned);
        }

    }
}
