package com.browser.ui;

import android.content.Context;
import android.graphics.Rect;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.browser.R;
import com.browser.ui.widget.JView;
import com.browser.browser.uictrl.UIController;
import com.browser.utils.Utils;


/**
 * Created by ozgur on 11/1/15 at 11:32 PM.
 */
public class MousePad extends LinearLayout implements JView {

    private static final int CLCK_DUR = 100;
    private static final int CLCK_DIST = 10;

    /**/
    LinearLayout wrapperLay;

    /**/
    private ImageView mouseIcon;
    private UIController uiController;


    /* for touch & click*/
    float dX, dY;
    float lastMouseY, lastMouseX;
    Rect touchDetectRect = new Rect();
    int screenWidth, screenHeight = 0;
    private long pressStartTime;

    /**/
    private int bottomBarHeight;

    public MousePad(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View wrapper = inflater.inflate(R.layout.view_mouse_pad, this, true);
        /**/
        wrapperLay = (LinearLayout) wrapper.findViewById(R.id.wrapperLay);

        /**/
        wrapperLay.getHitRect(touchDetectRect);
        wrapperLay.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getActionMasked()) {

                    case MotionEvent.ACTION_DOWN:
                        dX = motionEvent.getX();
                        dY = motionEvent.getY();
                        pressStartTime = System.currentTimeMillis();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float X = motionEvent.getX();
                        float Y = motionEvent.getY();

                        if(isTouched(X, Y))
                            moveMouse(X, Y);

                        break;

                    case MotionEvent.ACTION_UP:

                        long pressDuration = System.currentTimeMillis() - pressStartTime;
                        if(pressDuration < CLCK_DUR && (motionEvent.getX() - dX) < CLCK_DIST && (motionEvent.getY() - dY) < CLCK_DIST)
                            simulateClick(motionEvent.getX(), motionEvent.getY());

                        break;

                    default:
                        return false;
                }

                return true;
            }
        });

    }

    private void moveMouse(float X, float Y) {
        float diffX = X - dX;
        float diffY = Y - dY;

        dX = X;
        dY = Y;

        /**/
        float lastX = lastMouseX = this.mouseIcon.getX() + diffX;
        float lastY = lastMouseY = this.mouseIcon.getY() + diffY;

        if(lastX < screenWidth && lastX >= 0)
            this.mouseIcon.setX(lastX);

        if(lastY <= (screenHeight - bottomBarHeight) && lastY >= 0)
            this.mouseIcon.setY(lastY);

    }

    //TODO
    private boolean isTouched(float x, float y){
        //return touchDetectRect.contains((int)x, (int)y);
        return true;
    }

    private void Log(String str){
        Log.d("MousePad", str);
    }

    @Override
    public void show() {
        setVisibility(VISIBLE);
        mouseIcon.setVisibility(VISIBLE);
        wrapperLay.getHitRect(touchDetectRect);
    }

    @Override
    public void hide() {
        setVisibility(INVISIBLE);
        mouseIcon.setVisibility(INVISIBLE);
        wrapperLay.getHitRect(touchDetectRect);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public void simulateClick(float x, float y){

        Log("simulateClick");

        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_DOWN, lastMouseX, lastMouseY, 0);
        MotionEvent motionEvent2 = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, lastMouseX, lastMouseY, 0);

        if(uiController.getTab().isHome()) {
            uiController.getHomeView().dispatchTouchEvent(motionEvent);
            uiController.getHomeView().dispatchTouchEvent(motionEvent2);
        } else {
            uiController.getTab().getWebView().dispatchTouchEvent(motionEvent);
            uiController.getTab().getWebView().dispatchTouchEvent(motionEvent2);
        }

    }

    public void setUiController(UIController uiController, ImageView mouseIcon, int bottomBarHeight) {
        this.uiController = uiController;
        this.mouseIcon = mouseIcon;
        this.screenWidth = Utils.getScreenScreenSize(uiController.getActivity()).x;
        this.screenHeight = Utils.getScreenScreenSize(uiController.getActivity()).y;
        this.bottomBarHeight = bottomBarHeight;
    }

}