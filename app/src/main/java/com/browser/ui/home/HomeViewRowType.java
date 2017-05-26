package com.browser.ui.home;

/**
 * Created by ozgur on 23.07.2016.
 */
public enum HomeViewRowType {

    DIAL(1),
    DIAL_EXIT_MODE(2),
    RATE_US(3),
    MOST_VISITED(4),
    ICON_JUST(6),
    ;

    private final int type;

    HomeViewRowType(int i) {
        type = i;
    }

    public int getType() {
        return type;
    }
}
