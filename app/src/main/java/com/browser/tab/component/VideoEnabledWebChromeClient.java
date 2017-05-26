package com.browser.tab.component;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import android.widget.VideoView;
import com.browser.R;
import com.browser.tab.TabView;

public class VideoEnabledWebChromeClient extends WebChromeClient implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener {

    private int originalOrientation;
    private FullscreenHolder fullscreenHolder;

    protected TabView tabView;

    private View allContent;
    private VideoEnabledWebView webView;

    private boolean isVideoFullscreen;
    private CustomViewCallback videoViewCallback;

    private View customView;
    private VideoView videoView;
    private CustomViewCallback customViewCallback;

    @SuppressWarnings("unused")
    public VideoEnabledWebChromeClient(TabView tabView) {
        this.tabView = tabView;
        this.allContent = tabView.getUiController().getRootLayout();
        this.webView = tabView.getWebView();
        this.isVideoFullscreen = false;
    }

    public boolean isVideoFullscreen()
    {
        return isVideoFullscreen;
    }

    private void setCustomFullscreen(boolean fullscreen) {
        WindowManager.LayoutParams layoutParams = tabView.getUiController().getActivity().getWindow().getAttributes();

        int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;

        if (fullscreen) {
            layoutParams.flags |= bits;
        } else {
            layoutParams.flags &= ~bits;
            if (customView != null) {
                customView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            } else {
                allContent.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }

        tabView.getUiController().getActivity().getWindow().setAttributes(layoutParams);
    }

    private void Log(String e) {
        Log.d("VEWCC", e);
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {

        Log("onShowCustomView");

        /**/
        originalOrientation = tabView.getUiController().getActivity().getRequestedOrientation();

        if (view == null) {
            return;
        }
        if (customView != null && callback != null) {
            callback.onCustomViewHidden();
            return;
        }

        customView = view;
        customViewCallback = callback;

        /**/
        tabView
            .getUiController()
            .getActivity()
            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        fullscreenHolder = new FullscreenHolder(tabView.getUiController().getActivity());
        fullscreenHolder.addView(
                customView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));

        FrameLayout decorView = (FrameLayout) tabView.getUiController().getActivity().getWindow().getDecorView();
        decorView.addView(
                fullscreenHolder,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));

        setCustomFullscreen(true);


        /*
        Log.d("vvvvvv", "onShow");

        if (view == null) {
            return;
        }
        if (customView != null && callback != null) {
            callback.onCustomViewHidden();
            return;
        }

        customView = view;
        originalOrientation = tabView.getUiController().getActivity().getRequestedOrientation();

        fullscreenHolder = new FullscreenHolder(tabView.getUiController().getActivity());
        fullscreenHolder.addView(
                customView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));

        FrameLayout decorView = (FrameLayout) tabView.getUiController().getActivity().getWindow().getDecorView();
        decorView.addView(
                fullscreenHolder,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));

        customView.setKeepScreenOn(true);
        setCustomFullscreen(true);

        if (view instanceof FrameLayout) {

            Log.d("vvvvvv", "FrameLayout");
            
            FrameLayout frameLayout = (FrameLayout) view;
            View focusedChild = frameLayout.getFocusedChild();

            if (focusedChild instanceof VideoView) {
                Log.d("vvvvvv", "VideoView");

                videoView = (VideoView) ((FrameLayout) view).getFocusedChild();
                videoView.setOnPreparedListener(this);
                videoView.setOnCompletionListener(this);
                videoView.setOnErrorListener(this);

            } else {

                if (webView.getSettings().getJavaScriptEnabled() && focusedChild instanceof SurfaceView) {

                    Log.d("vvvvvv", "SurfaceView");

                    String js = "javascript:";
                    js += "var _ytrp_html5_video_last;";
                    js += "var _ytrp_html5_video = document.getElementsByTagName('video')[0];";
                    js += "if (_ytrp_html5_video != undefined && _ytrp_html5_video != _ytrp_html5_video_last) {";
                    {
                        js += "_ytrp_html5_video_last = _ytrp_html5_video;";
                        js += "function _ytrp_html5_video_ended() {";
                        {
                            js += "_VideoEnabledWebView.notifyVideoEnd();"; // Must match Javascript interface name and method of VideoEnableWebView
                        }
                        js += "}";
                        js += "_ytrp_html5_video.addEventListener('ended', _ytrp_html5_video_ended);";
                    }
                    js += "}";
                    //webView.loadUrl(js);
                }
            }
        }

        customViewCallback = callback;
        tabView
            .getUiController()
            .getActivity()
            .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            */

        /*
        if (videoViewContainer != null && callback != null) {
            callback.onCustomViewHidden();
            return;
        }

        originalOrientation = tabView.getUiController().getActivity().getRequestedOrientation();

        if (view instanceof FrameLayout) {
            // A video wants to be shown
            FrameLayout frameLayout = (FrameLayout) view;
            View focusedChild = frameLayout.getFocusedChild();

            // Save video related variables
            this.isVideoFullscreen = true;
            this.videoViewContainer = frameLayout;
            this.videoViewCallback = callback;

            FrameLayout decorView = (FrameLayout) tabView.getUiController().getActivity().getWindow().getDecorView();

            // Hide the non-video view, add the video view, and show it
            allContent.setVisibility(View.GONE);
            //videoContainer.addView(videoViewContainer, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            decorView.addView(videoViewContainer, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

            if (focusedChild instanceof android.widget.VideoView) {
                // android.widget.VideoView (typically API level <11)
                android.widget.VideoView videoView = (android.widget.VideoView) focusedChild;

                // Handle all the required events
                videoView.setOnPreparedListener(this);
                videoView.setOnCompletionListener(this);
                videoView.setOnErrorListener(this);
            }

            else {
                // Other classes, including:
                // - android.webkit.HTML5VideoFullScreen$VideoSurfaceView, which inherits from android.view.SurfaceView (typically API level 11-18)
                // - android.webkit.HTML5VideoFullScreen$VideoTextureView, which inherits from android.view.TextureView (typically API level 11-18)
                // - com.android.org.chromium.content.browser.ContentVideoView$VideoSurfaceView, which inherits from android.view.SurfaceView (typically API level 19+)

                // Handle HTML5 video ended event only if the class is a SurfaceView
                // Test case: TextureView of Sony Xperia T API level 16 doesn't work fullscreen when loading the javascript below
                if (webView != null && webView.getSettings().getJavaScriptEnabled() && focusedChild instanceof SurfaceView) {
                    // Run javascript code that detects the video end and notifies the Javascript interface
                    String js = "javascript:";
                    js += "var _ytrp_html5_video_last;";
                    js += "var _ytrp_html5_video = document.getElementsByTagName('video')[0];";
                    js += "if (_ytrp_html5_video != undefined && _ytrp_html5_video != _ytrp_html5_video_last) {";
                    {
                        js += "_ytrp_html5_video_last = _ytrp_html5_video;";
                        js += "function _ytrp_html5_video_ended() {";
                        {
                            js += "_VideoEnabledWebView.notifyVideoEnd();"; // Must match Javascript interface name and method of VideoEnableWebView
                        }
                        js += "}";
                        js += "_ytrp_html5_video.addEventListener('ended', _ytrp_html5_video_ended);";
                    }
                    js += "}";
                    webView.loadUrl(js);
                }
            }

            // Notify full-screen change
            if (toggledFullscreenCallback != null) {
                toggledFullscreenCallback.toggledFullscreen(true);
            }

            // Auto landscape when video shows
            tabView
                .getUiController()
                .getActivity()
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        */
    }

    @Override
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {

        if (customView == null || customViewCallback == null) {
            return;
        }

        FrameLayout decorView = (FrameLayout) tabView.getUiController().getActivity().getWindow().getDecorView();
        if (decorView != null) {
            decorView.removeView(fullscreenHolder);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            try {
                customViewCallback.onCustomViewHidden();
            } catch (Throwable ignored) {}
        }

        customView.setKeepScreenOn(false);
        setCustomFullscreen(false);

        fullscreenHolder = null;
        customView = null;
        if (videoView != null) {
            videoView.setOnErrorListener(null);
            videoView.setOnCompletionListener(null);
            videoView = null;
        }

        tabView
            .getUiController()
            .getActivity()
            .setRequestedOrientation(originalOrientation);

    }

    @Override
    public View getVideoLoadingProgressView() {
        LayoutInflater inflater = LayoutInflater.from(tabView.getUiController().getActivity());
        return inflater.inflate(R.layout.video_progress, null);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //if (loadingView != null)  loadingView.setVisibility(View.GONE);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        onHideCustomView();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false; // By returning false, onCompletion() will be called
    }

    public boolean onBackPressed() {
        if (isVideoFullscreen) {
            onHideCustomView();
            return true;
        }
        else {
            return false;
        }
    }

}