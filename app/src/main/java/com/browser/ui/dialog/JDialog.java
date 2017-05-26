package com.browser.ui.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.browser.R;
import com.browser.theme.themes.ThemeController;
import com.browser.theme.themes.ThemeListener;
import com.browser.theme.themes.ThemeModel;
import com.browser.ui.widget.JTV;
import com.browser.utils.Utils;

/**
 * Created by ozgur on 21.06.2016.
 */
public class JDialog extends JBaseDialog implements ThemeListener {

    private JTV mTitleTextView;
    private JTV mContentTextView;
    private JDialogButton mYesButton;
    private JDialogButton mNoButton;
    private LinearLayout mWrapper;

    private DialogClickListener dialogClickListener;

    public JDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.j_dialog);

        setWH(Utils.dpToPx(getContext(), 300), ViewGroup.LayoutParams.WRAP_CONTENT); //after setContentView

        mTitleTextView = (JTV) findViewById(R.id.TitleTextView);
        mContentTextView = (JTV) findViewById(R.id.ContentTextView);
        mYesButton = (JDialogButton) findViewById(R.id.YesButton);
        mNoButton = (JDialogButton) findViewById(R.id.NoButton);
        mWrapper = (LinearLayout) findViewById(R.id.Wrapper);

        ThemeController.getInstance().register(this);
        changeTheme();

        /**/
        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialogClickListener != null) {
                    dialogClickListener.onYesOrNoClick(ButtonType.POSITIVE);
                }
                hide();
            }
        });

        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialogClickListener != null) {
                    dialogClickListener.onYesOrNoClick(ButtonType.NEGATIVE);
                }
                hide();
            }
        });
    }

    @Override
    public void themeChanged() {
        changeTheme();
    }

    @Override
    public void changeTheme() {
        ThemeModel t = ThemeController.getInstance().getCurrentTheme();
        mWrapper.setBackgroundColor(t.dialogBg);
        mNoButton.setTextColor(t.dialogButtonTxtColor);
        mYesButton.setTextColor(t.dialogButtonTxtColor);
        mContentTextView.setTextColor(t.dialogButtonTxtColor);
        mTitleTextView.setTextColor(t.dialogTitleColor);
        mTitleTextView.setBackgroundColor(t.dialogTitleBg);
    }

    public void setTitle(String title) {
        mTitleTextView.setText(title);
    }

    public void setTitle(int resId) {
        mTitleTextView.setText(resId);
    }

    public void setContent(String content) {
        mContentTextView.setVisibility(View.VISIBLE);
        mContentTextView.setText(content);
    }

    public void setContent(int resId) {
        mContentTextView.setVisibility(View.VISIBLE);
        mContentTextView.setText(resId);
    }

    public void setDialogClickListener(DialogClickListener dialogClickListener) {
        this.dialogClickListener = dialogClickListener;
    }

    public interface DialogClickListener {
        void onYesOrNoClick(ButtonType buttonType);
    }

    public enum ButtonType {
        POSITIVE,
        NEGATIVE
    }

    public void setYesButtonText(String e) {
        mYesButton.setText(e);
    }

    public void setYesButtonText(int e) {
        mYesButton.setText(e);
    }

    public void setNoButtonText(String e) {
        mNoButton.setText(e);
    }
    public void setNoButtonText(int e) {
        mNoButton.setText(e);
    }
}
