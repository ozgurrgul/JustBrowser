package com.browser.suggestion;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.browser.Constants;
import com.browser.history.HistoryItem;
import com.browser.odm.Config;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


/**
 * Created by ozgur on 05.06.2016.
 */
public class SuggestionGoogleTask {

    private String mLanguage;
    //google, yahoo etc
    private static final String DEFAULT_LANGUAGE = "en";
    private OkHttpClient mHttpClient;

    /**/
    private AsyncTask<Void, Void, String> mXmlHttpRequest;
    private static XmlPullParser mXmlPullParser;

    /**/
    private ArrayList<HistoryItem> suggestionList = new ArrayList<>();

    /**/
    private String queryUrl;

    /**/
    private SuggestionGoogleTaskListener suggestionGoogleTaskListener;

    public SuggestionGoogleTask(SuggestionGoogleTaskListener suggestionGoogleTaskListener) {
        this.suggestionGoogleTaskListener = suggestionGoogleTaskListener;
        mLanguage = Locale.getDefault().getLanguage();
        mHttpClient = new OkHttpClient();
        mHttpClient.setFollowRedirects(true);
        mHttpClient.setFollowSslRedirects(false);

        // disable http/2
        mHttpClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));


        mHttpClient.setRetryOnConnectionFailure(true);
        mHttpClient.setConnectTimeout(Config.CONNECT_TIMEOUT, TimeUnit.SECONDS);
        mHttpClient.setReadTimeout(Config.READ_TIMEOUT, TimeUnit.SECONDS);
        mHttpClient.getDispatcher().setMaxRequests(Config.MAX_THREAD_CNT);

        if (TextUtils.isEmpty(mLanguage)) {
            mLanguage = DEFAULT_LANGUAGE;
        }

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            mXmlPullParser = factory.newPullParser();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void query(String txt) {

        if(TextUtils.isEmpty(txt)) {
            return;
        }

        if(mXmlHttpRequest != null && mXmlHttpRequest.getStatus() != AsyncTask.Status.FINISHED) {
            mXmlHttpRequest.cancel(true);
        }

        suggestionList.clear();

        /**/
        buildQueryUrl(txt);

        /* cancel if running */
        if(mXmlHttpRequest != null) {
            mXmlHttpRequest.cancel(true);
        }

        /**/
        mXmlHttpRequest = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {

                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.url(queryUrl);
                Request request = requestBuilder.build();
                Response response = null;
                Call call = mHttpClient.newCall(request);

                try {
                    response = call.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (response != null) {
                        return response.body().string();
                    } else {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                parseXmlResult(result);
            }

        }.execute();
    }

    public void cancel() {
        if(mXmlHttpRequest != null) {
            mXmlHttpRequest.cancel(true);
        }
    }

    private void parseXmlResult(String result) {

        if(TextUtils.isEmpty(result)) {
            return;
        }

        try {

            InputStream is;
            is = new ByteArrayInputStream(result.getBytes("UTF-8"));
            mXmlPullParser.setInput(is, "UTF-8");
            int eventType = mXmlPullParser.getEventType();
            int counter = 1;

            /**/
            while (eventType != XmlPullParser.END_DOCUMENT) {

                if (eventType == XmlPullParser.START_TAG &&
                    "suggestion".equals(mXmlPullParser.getName())) {

                    /**/
                    String suggestion = mXmlPullParser.getAttributeValue(null, "data");

                    Log("suggestion item: " + suggestion);

                    /* dummy */
                    HistoryItem item = new HistoryItem(-1, null, null, 0);
                    item.setTitle(suggestion);
                    item.setUrl(suggestion);
                    item.setType(HistoryItem.SUGGESTION);

                    /**/
                    suggestionList.add(item);

                    /**/
                    if (++counter > Constants.SUGGESTION_LIST_GOOGLE_ITEM_COUNT)
                        break;

                }
                eventType = mXmlPullParser.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        /**/
        if(suggestionGoogleTaskListener != null) {
            suggestionGoogleTaskListener.onGoogleSuggestionsLoaded(suggestionList);
        }

    }

    private void Log(String e) {
        Log.d(getClass().getSimpleName(), e);
    }

    private void buildQueryUrl(String q) {
        queryUrl = "https://suggestqueries.google.com/complete/search?output=toolbar&hl=" + mLanguage + "&q=" + q;
    }

}
