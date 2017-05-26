package com.browser.suggestion;

import com.browser.history.HistoryItem;

import java.util.List;

public interface SuggestionHistoryTaskListener {
    void onHistorySuggestionsLoaded(List<HistoryItem> suggestionList);
}