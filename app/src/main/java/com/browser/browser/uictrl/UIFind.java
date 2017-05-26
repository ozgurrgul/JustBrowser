package com.browser.browser.uictrl;

/**
 * Created by ozgur on 09.08.2016.
 */
public interface UIFind {
    void handleFindDialog();
    void enterFindMode();
    void exitFindMode();
    void findInPage(String txt);
    void findNext();
    void findPrevious();
}
