package com.browser.adblock;

import android.content.Context;
import android.content.res.AssetManager;

import com.browser.browser.CPM;
import com.browser.utils.Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by ozgur on 22.06.2016.
 */
public class AdBlockManager {

    private static final String PATH = "hosts.txt";;
    private final Set<String> blockedUrls = new HashSet<>();
    private static AdBlockManager instance = new AdBlockManager();
    private Context context;
    private CPM cpm;

    private AdBlockManager() {
        //check preference
        new Thread(new Runnable() {
            @Override
            public void run() {
                initHostList(context);
            }
        }).start();
    }

    public static AdBlockManager getInstance() {
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        this.cpm = new CPM(context);
    }

    private void initHostList(Context context) {

        if(cpm == null) {
            cpm = new CPM(context);
        }

        if(!cpm.isAdBlockEnabled()) {
            return;
        }

        AssetManager assetManager = context.getResources().getAssets();

        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;

        try {
            fIn = assetManager.open(PATH);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line;

            while ((line = input.readLine()) != null) {
                if ((line.length() > 0) &&
                        (!line.startsWith("//"))) {
                    blockedUrls.add(line);
                }
            }

        }
        catch (Exception ignored) {}
        finally {
            try { isr.close(); } catch (Exception e) {}
            try { fIn.close(); } catch (Exception e) {}
            try { input.close(); } catch (Exception e) {}
        }

    }

    public boolean isBlocked(String requestUrl, String url) {

        if(!cpm.isAdBlockEnabled()) {
            return false;
        }

        String domain;

        try {
            domain = Utils.getBaseDomainName(requestUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        try {
            url = Utils.getBaseDomainName(url);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        boolean state = blockedUrls.contains(domain);

        if(state) {

            if(context == null) {
                return false;
            }

            //update url block count
            saveBlockedUrl(url);
        }

        return state;
    }

    public void saveBlockedUrl(String url) {
        AdBlockDb.getInstance(context).saveHistory(url);
    }

}
