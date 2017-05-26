package com.browser.base;

import android.widget.Toast;

import com.browser.R;
import com.browser.theme.themes.ThemeController;

/**
 * Created by ozgur on 04.08.2016.
 */
public class BaseActivity extends SwipeBackActivity {

    public void toast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_LONG).show();
    }

    public void setBackgroundTheme() {
        if(findViewById(R.id.RootLayout) != null) {
            findViewById(R.id.RootLayout).setBackgroundColor(ThemeController.getInstance().getCurrentTheme().actBg);
        }
    }

}
