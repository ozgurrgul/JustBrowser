package com.browser.odm;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Patterns;

import com.browser.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by ozgur on 05.07.2016.
 */
public class ODMUtils {

    private static final String ALLOWED_CHARACTERS = "0123456789qwertyuiopasdfghjklzxcvbnm";
    /**/
    private static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("0.##");
    private static final int MB_2_BYTE = 1024 * 1024;
    private static final int KB_2_BYTE = 1024;

    public static String getRandomString(int len) {
        final Random r = new Random();
        final StringBuilder sb = new StringBuilder(len);

        for(int i = 0; i < len; ++i) {
            sb.append(ALLOWED_CHARACTERS.charAt(r.nextInt(ALLOWED_CHARACTERS.length())));
        }

        return sb.toString();
    }

    public static CharSequence getSizeString(long size) {

        if (size <= 0) {
            return "0M";
        }

        if (size >= MB_2_BYTE) {
            return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double)size / MB_2_BYTE)).append("M");
        } else if (size >= KB_2_BYTE) {
            return new StringBuilder(16).append(DOUBLE_DECIMAL_FORMAT.format((double)size / KB_2_BYTE)).append("K");
        } else {
            return size + "B";
        }

    }

    public static boolean isValidUrl(String urlStr) {
        return Patterns.WEB_URL.matcher(urlStr).matches();
    }

    public static void openFile(File f, Context context) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);

        String type = MimeTypes.getMimeType(f);
        if(!TextUtils.isEmpty(type)) {
            Uri uri = Uri.fromFile(f);
            intent.setDataAndType(uri, type);
            Intent startintent = Intent.createChooser(intent, context.getResources().getString(R.string.a_open_with));

            try {
                context.startActivity(startintent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
