package com.browser.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by ozgur on 04.08.2016.
 */
public class BitmapUtils {

    public static Bitmap getBitmap(byte[] data) {

        if(data == null) {
            return null;
        }

        Bitmap bmp;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        return bmp;
    }

    public static byte[] getByte(Bitmap b){

        Log.d("Utils", "bitmap: " + b);

        if(b == null)
            return null;

        byte[] array = new byte[0];

        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 100, stream);
            array = stream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return array;
    }

}
