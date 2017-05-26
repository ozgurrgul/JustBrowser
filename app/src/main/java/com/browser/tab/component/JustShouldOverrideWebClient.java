package com.browser.tab.component;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebView;

import com.browser.Constants;
import com.browser.R;
import com.browser.tab.TabView;
import com.browser.utils.Utils;

import java.net.URISyntaxException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ozgur on 14.08.2016.
 */
public class JustShouldOverrideWebClient extends JustAdBlockWebClient {

    private static final Pattern ACCEPTED_URI_SCHEMA = Pattern.compile("(?i)"
            + // switch on case insensitive matching
            '('
            + // begin group for schema
            "(?:http|https|file)://" + "|(?:inline|data|about|javascript):" + "|(?:.*:.*@)"
            + ')' + "(.*)");

    public JustShouldOverrideWebClient(TabView tabView) {
        super(tabView);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(tabView.getTabData().isDestroying()) return true;

        //check mailto:
        if (url.startsWith("mailto:")) {
            MailTo mailTo = MailTo.parse(url);
            Intent i = Utils.newEmailIntent(mailTo.getTo(), mailTo.getSubject(), mailTo.getBody(), mailTo.getCc());
            tabView.getUiController().getActivity().startActivity(i);
            //view.reload();
            return true;
        }

        //check intent://
        if (url.startsWith("intent://")) {
            Intent intent;
            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            } catch (URISyntaxException ignored) {
                intent = null;
            }
            if (intent != null) {
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setComponent(null);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    intent.setSelector(null);
                }
                try {
                    tabView.getUiController().getActivity().startActivity(intent);
                } catch (ActivityNotFoundException ignored) {
                    tabView.getUiController().toast(R.string.no_app_found);
                }
                return true;
            }
        }

        if (startActivityForUrl(view, url)) {
            return true;
        }

        view.loadUrl(url);
        tabView.getUiController().getCpm().increaseTotalUrlCount();

        return true;
    }

    private boolean startActivityForUrl(WebView webView, String url) {

        Intent intent;
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
        } catch (URISyntaxException ex) {
            return false;
        }

        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setComponent(null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            intent.setSelector(null);
        }

        if (tabView.getUiController().getActivity().getPackageManager().resolveActivity(intent, 0) == null) {
            String packagename = intent.getPackage();
            if (packagename != null) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pname:"
                        + packagename));
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                tabView.getUiController().getActivity().startActivity(intent);
                return true;
            } else {
                return false;
            }
        }

        if (webView != null) {
            intent.putExtra(Constants.URL_INTENT_ORIGIN_SELF, 1);
        }

        Matcher m = ACCEPTED_URI_SCHEMA.matcher(url);
        if (m.matches() && !isSpecializedHandlerAvailable(intent)) {
            return false;
        }
        try {
            if (tabView.getUiController().getActivity().startActivityIfNeeded(intent, -1)) {
                return true;
            }
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private boolean isSpecializedHandlerAvailable(Intent intent) {

        PackageManager pm = tabView.getUiController().getActivity().getPackageManager();
        List<ResolveInfo> handlers = pm.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);

        if (handlers == null || handlers.isEmpty()) {
            return false;
        }

        for (ResolveInfo resolveInfo : handlers) {
            IntentFilter filter = resolveInfo.filter;
            if (filter == null) {
                continue;
            }
            if (filter.countDataAuthorities() == 0 || filter.countDataPaths() == 0) {
                continue;
            }
            return true;
        }

        return false;
    }
}
