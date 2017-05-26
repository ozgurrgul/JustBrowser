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
public class JTwoEditTextDialog extends JBaseDialog implements ThemeListener {

    private JTV mTitleTextView;
    public JDialogEditText mEditTextOne, mEditTextTwo;
    private JDialogButton mYesButton;
    private JDialogButton mNoButton;
    private LinearLayout mWrapper;
    private LinearLayout mTitleWrapper;

    private DialogClickListener dialogClickListener;

    public JTwoEditTextDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.j_list_dialog_two_edittext);

        setWH(Utils.dpToPx(getContext(), 300), ViewGroup.LayoutParams.WRAP_CONTENT); //after setContentView

        mTitleTextView = (JTV) findViewById(R.id.TitleTextView);
        mYesButton = (JDialogButton) findViewById(R.id.YesButton);
        mNoButton = (JDialogButton) findViewById(R.id.NoButton);
        mEditTextOne = (JDialogEditText) findViewById(R.id.EditTextOne);
        mEditTextTwo = (JDialogEditText) findViewById(R.id.EditTextTwo);
        mWrapper = (LinearLayout) findViewById(R.id.Wrapper);
        mTitleWrapper = (LinearLayout) findViewById(R.id.TitleWrapper);

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
        mTitleTextView.setTextColor(t.dialogTitleColor);
        mTitleWrapper.setBackgroundColor(t.dialogTitleBg);
        mNoButton.setTextColor(t.dialogButtonTxtColor);
        mYesButton.setTextColor(t.dialogButtonTxtColor);
    }

    public void setTitle(int resId) {
        mTitleTextView.setText(resId);
    }
    public void setTitle(String title) {
        mTitleTextView.setText(title);
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
        if(mYesButton != null) {
            mYesButton.setText(e);
        }
    }

    public void setYesButtonText(int resId) {
        if(mYesButton != null) {
            mYesButton.setText(resId);
        }
    }

    public void setNoButtonText(String e) {
        if(mNoButton != null) {
            mNoButton.setText(e);
        }
    }

    public void setNoButtonText(int resId) {
        if(mNoButton != null) {
            mNoButton.setText(resId);
        }
    }

    public void setEditTextOneHint(String e) {
        if(mEditTextOne != null) {
            mEditTextOne.setHint(e);
        }
    }

    public void setEditTextOneHint(int resId) {
        if(mEditTextOne != null) {
            mEditTextOne.setHint(resId);
        }
    }

    public void setEditTextTwoHint(String e) {
        if(mEditTextTwo != null) {
            mEditTextTwo.setHint(e);
        }
    }

    public void setEditTextTwoHint(int resId) {
        if(mEditTextTwo != null) {
            mEditTextTwo.setHint(resId);
        }
    }
}
