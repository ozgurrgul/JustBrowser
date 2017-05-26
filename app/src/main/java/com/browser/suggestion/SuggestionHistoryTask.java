package com.browser.suggestion;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.browser.history.HistoryDb;
import com.browser.history.HistoryItem;

import java.util.List;

/**
 * Created by ozgur on 08.06.2016.
 */
public class SuggestionHistoryTask {

    private final Context context;
    AsyncTask asyncTask;

    private SuggestionHistoryTaskListener suggestionListTaskListener;

    public SuggestionHistoryTask(Context context, SuggestionHistoryTaskListener _suggestionListTaskListener) {
        suggestionListTaskListener = _suggestionListTaskListener;
        this.context = context;
    }

    public void query(final String text) {

        if(asyncTask != null && asyncTask.getStatus() != AsyncTask.Status.FINISHED) {
            asyncTask.cancel(true);
        }

        asyncTask = new AsyncTask<Void, Void, List<HistoryItem>>() {
            @Override
            protected List<HistoryItem> doInBackground(Void... voids) {

                return HistoryDb.getInstance(context).getSuggestions(text);
            }

            @Override
            protected void onPostExecute(List<HistoryItem> results) {
                super.onPostExecute(results);

                if(suggestionListTaskListener != null) {
                    suggestionListTaskListener.onHistorySuggestionsLoaded(results);
                }
            }
        }.execute();

    }

    public void cancel() {

    }

    private void Log(String e) {
        Log.d(getClass().getSimpleName(), e);
    }
}
