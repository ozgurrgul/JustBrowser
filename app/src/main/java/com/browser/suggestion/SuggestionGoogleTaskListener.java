package com.browser.suggestion;

import com.browser.history.HistoryItem;

import java.util.ArrayList;

public interface SuggestionGoogleTaskListener {
    void onGoogleSuggestionsLoaded(ArrayList<HistoryItem> suggestionList);
}