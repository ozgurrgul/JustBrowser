package com.browser.js;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ozgur on 20.06.2016.
 */
public abstract class BaseJs {

    private String path;
    private String code;
    private String fileName;
    private Context context;

    public BaseJs(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
        path = "js/" + this.fileName + ".js";
        new Thread(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }).start();
    }

    private void init() {
        AssetManager assetManager = context.getResources().getAssets();

        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        StringBuilder sb = new StringBuilder();

        try {
            fIn = assetManager.open(path);
            isr = new InputStreamReader(fIn, "UTF-8");
            input = new BufferedReader(isr);
            String line;

            while ((line = input.readLine()) != null) {
                if ((line.length() > 0) && (!line.startsWith("//"))) {
                    sb.append(line).append("\n");
                }
            }
        }
        catch (Exception ignored) {}
        finally {
            try {isr.close(); } catch (Exception e) {}
            try {fIn.close(); } catch (Exception e) {}
            try {input.close(); } catch (Exception e) {}
        }

        code = sb.toString();

    }

    public String getCode() {
        return "javascript:" + code;
    }
    public String getPureCode() {
        return code;
    }
}
