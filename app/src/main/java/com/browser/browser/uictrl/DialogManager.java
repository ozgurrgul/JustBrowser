package com.browser.browser.uictrl;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Environment;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;

import com.browser.Constants;
import com.browser.R;
import com.browser.odm.ODM;
import com.browser.odm.ODMUtils;
import com.browser.support.SupportActivity;
import com.browser.ui.dialog.JDialog;
import com.browser.ui.dialog.JDirChooserDialog;
import com.browser.ui.dialog.JListDialog;
import com.browser.ui.dialog.JTwoEditTextDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ozgur on 06.08.2016.
 */
public class DialogManager {

    private final UIController uiController;

    public DialogManager(UIController uiController) {
        this.uiController = uiController;
    }

    public void onFormResubmission(final Message dontResend, final Message resend) {
        JDialog c = new JDialog(uiController.getActivity());
        c.setTitle(R.string.title_form_resubmission);
        c.setContent(R.string.message_form_resubmission);
        c.setDialogClickListener(new JDialog.DialogClickListener() {
            @Override
            public void onYesOrNoClick(JDialog.ButtonType buttonType) {
                if(buttonType == JDialog.ButtonType.POSITIVE) {
                    resend.sendToTarget();
                }

                if(buttonType == JDialog.ButtonType.NEGATIVE) {
                    dontResend.sendToTarget();
                }
            }
        });

        c.show();
    }

    public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
        final boolean remember = true;
        JDialog c = new JDialog(uiController.getActivity());
        c.setTitle(R.string.location_permission);
        c.setContent(R.string.location_permission);
        c.setDialogClickListener(new JDialog.DialogClickListener() {
            @Override
            public void onYesOrNoClick(JDialog.ButtonType buttonType) {
                if(buttonType == JDialog.ButtonType.POSITIVE) {
                    callback.invoke(origin, true, remember);
                }

                if(buttonType == JDialog.ButtonType.NEGATIVE) {
                    callback.invoke(origin, false, remember);
                }
            }
        });
        c.setCancelable(false);
        c.show();
    }

    public void onReceivedHttpRequest(final HttpAuthHandler handler) {
        final JTwoEditTextDialog c = new JTwoEditTextDialog(uiController.getActivity());
        c.setTitle(R.string.title_sign_in);
        c.setEditTextOneHint(R.string.hint_username);
        c.setEditTextTwoHint(R.string.hint_password);
        c.setYesButtonText(R.string.title_sign_in);
        c.setNoButtonText(android.R.string.cancel);
        c.setDialogClickListener(new JTwoEditTextDialog.DialogClickListener() {

            @Override
            public void onYesOrNoClick(JTwoEditTextDialog.ButtonType buttonType) {
                if(buttonType == JTwoEditTextDialog.ButtonType.NEGATIVE) {
                    String user = c.mEditTextOne.getText().toString();
                    String pass = c.mEditTextTwo.getText().toString();
                    handler.proceed(user.trim(), pass.trim());
                }
                if(buttonType == JTwoEditTextDialog.ButtonType.POSITIVE) {
                    handler.cancel();
                }
                c.dismiss();
            }

        });
        c.show();
    }

    private static List<Integer> getAllSslErrorMessageCodes(SslError error) {
        List<Integer> errorCodeMessageCodes = new ArrayList<>(1);

        if (error.hasError(SslError.SSL_DATE_INVALID)) errorCodeMessageCodes.add(R.string.ssl_cert_date_invalid);
        if (error.hasError(SslError.SSL_EXPIRED)) errorCodeMessageCodes.add(R.string.ssl_cert_expired);
        if (error.hasError(SslError.SSL_IDMISMATCH)) errorCodeMessageCodes.add(R.string.ssl_cert_domain_mismatch);
        if (error.hasError(SslError.SSL_NOTYETVALID)) errorCodeMessageCodes.add(R.string.ssl_cert_not_yet_valid);
        if (error.hasError(SslError.SSL_UNTRUSTED)) errorCodeMessageCodes.add(R.string.ssl_cert_untrusted);
        if (error.hasError(SslError.SSL_INVALID)) errorCodeMessageCodes.add(R.string.ssl_cert_invalid);

        return errorCodeMessageCodes;
    }

    public void onReceivedSslError(final SslErrorHandler handler, SslError error) {
        JDialog c = new JDialog(uiController.getActivity());

        List<Integer> errorCodeMessageCodes = getAllSslErrorMessageCodes(error);

        StringBuilder stringBuilder = new StringBuilder();
        for (Integer messageCode : errorCodeMessageCodes) {
            stringBuilder.append(" - ").append(uiController.getString(messageCode)).append('\n');
        }
        String alertMessage = uiController.getActivity().getString(R.string.ssl_insecure_connection, stringBuilder.toString());

        c.setTitle(uiController.getString(R.string.title_warning));
        c.setContent(alertMessage);

        c.setDialogClickListener(new JDialog.DialogClickListener() {
            @Override
            public void onYesOrNoClick(JDialog.ButtonType buttonType) {
                if(buttonType == JDialog.ButtonType.POSITIVE) {
                    handler.proceed();
                }

                if(buttonType == JDialog.ButtonType.NEGATIVE) {
                    handler.cancel();
                }
            }
        });

        c.show();
    }

    public void addSpeedDial(String url, String title, final Bitmap bitmap) {
        final JTwoEditTextDialog j = new JTwoEditTextDialog(uiController.getActivity());
        j.setTitle(R.string.add_speed_dial);
        j.setEditTextOneHint(R.string.title);
        j.setEditTextTwoHint(R.string.url);
        j.setYesButtonText(R.string.a_add);
        j.setNoButtonText(R.string.a_cancel);
        j.setCancelable(false);

        if(!TextUtils.isEmpty(url)) {
            j.mEditTextTwo.setText(url);
        }

        if(!TextUtils.isEmpty(title)) {
            j.mEditTextOne.setText(title);
        }

        j.setDialogClickListener(new JTwoEditTextDialog.DialogClickListener() {
            @Override
            public void onYesOrNoClick(JTwoEditTextDialog.ButtonType buttonType) {
                if(buttonType == JTwoEditTextDialog.ButtonType.POSITIVE) {

                    String url = j.mEditTextTwo.getText().toString();
                    String title = j.mEditTextOne.getText().toString();

                    if(TextUtils.isEmpty(url) || TextUtils.isEmpty(title)) {
                        uiController.toast(R.string.provide_url_and_title);
                        return;
                    }

                    uiController.getDialItems().add(
                        url,
                        title,
                        bitmap
                    );

                    uiController.toast(R.string.added);
                }
            }
        });

        j.show();
    }

    public void findDialog() {
        final JTwoEditTextDialog j = new JTwoEditTextDialog(uiController.getActivity());
        j.setTitle(R.string.a_find_in_page);
        j.mEditTextTwo.setVisibility(View.GONE);
        j.setYesButtonText(R.string.a_find_in_page);
        j.setNoButtonText(R.string.a_cancel);
        j.setDialogClickListener(new JTwoEditTextDialog.DialogClickListener() {
            @Override
            public void onYesOrNoClick(JTwoEditTextDialog.ButtonType buttonType) {
                if(buttonType == JTwoEditTextDialog.ButtonType.POSITIVE) {
                    uiController.enterFindMode();
                    uiController.findInPage(j.mEditTextOne.getText().toString());
                }
            }
        });
        j.show();
    }

    public void downloadDialog(final String url, long contentLength) {

        String title = uiController.getString(R.string.a_download);

        if(contentLength != 0) {
            title = title + " (" + ODMUtils.getSizeString(contentLength) + ")";
        }

        final JTwoEditTextDialog j = new JTwoEditTextDialog(uiController.getActivity());
        j.setTitle(title);
        j.mEditTextOne.setText(ODM.guessFileName(url));
        j.setEditTextOneHint(R.string.file_name);
        try {
            j.mEditTextTwo.setText(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        j.mEditTextTwo.setInputType(InputType.TYPE_NULL);
        j.mEditTextTwo.setFocusable(false);
        j.mEditTextTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JDirChooserDialog jdir = new JDirChooserDialog(uiController.getActivity());

                try {
                    jdir.setCurrentDir(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getCanonicalPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                jdir.setDirectoryChooserListener(new JDirChooserDialog.DirectoryChooserListener() {
                    @Override
                    public void onDirChosen(String dir) {
                        j.mEditTextTwo.setText(dir);
                    }
                });
                jdir.show();

            }
        });
        j.setYesButtonText(R.string.a_download);
        j.setNoButtonText(R.string.a_cancel);
        j.setDialogClickListener(new JTwoEditTextDialog.DialogClickListener() {
            @Override
            public void onYesOrNoClick(JTwoEditTextDialog.ButtonType buttonType) {
                if(buttonType == JTwoEditTextDialog.ButtonType.POSITIVE) {
                    uiController.getODM().download(
                        j.mEditTextTwo.getText().toString(),
                        j.mEditTextOne.getText().toString(),
                        url
                    );
                }
            }
        });
        j.setCancelable(false);
        j.show();
    }

    public void showTabLongPressUrl(final String url) {
        String content = url;

        if(!TextUtils.isEmpty(content) && content.length() > 120) {
            content = content.substring(0, 110) + "...";
        }

        JListDialog c = new JListDialog(uiController.getActivity());
        c.setTitle(R.string.menu);
        c.setContent(content);

        c.setListItems(new String[]{
                uiController.getString(R.string.a_new_tab),
                uiController.getString(R.string.a_bg_tab),
                uiController.getString(R.string.a_incognito),
                uiController.getString(R.string.copy_link),
                uiController.getString(R.string.a_share)
        });

        c.setDialogClickListener(new JListDialog.DialogClickListener() {
            @Override
            public void onClick(String clickedItem, int position) {
                switch (position) {
                    case 0: uiController.addTab(url, false, false, true, false);break;
                    case 1: uiController.addTab(url, false, true, true, true);break;
                    case 2: uiController.addTab(url, true, false, true, false); break;
                    case 3: uiController.copyToClipboard(url); break;
                    case 4: uiController.share(url); break;
                }
            }
        });

        c.show();
    }


    public void showTabLongPressImage(final String imgUrl) {
        String content = imgUrl;

        if(!TextUtils.isEmpty(content) && content.length() > 110) {
            content = content.substring(0, 110) + "...";
        }

        JListDialog c = new JListDialog(uiController.getActivity());
        c.setTitle(R.string.menu);
        c.setContent(content);

        c.setListItems(new String[]{
                uiController.getString(R.string.a_new_tab),
                uiController.getString(R.string.a_bg_tab),
                uiController.getString(R.string.a_save_image),
                uiController.getString(R.string.copy_link)
        });

        c.setDialogClickListener(new JListDialog.DialogClickListener() {
            @Override
            public void onClick(String clickedItem, int position) {
                switch (position) {
                    case 0: uiController.addTab(imgUrl, false, false, true, false); break;
                    case 1: uiController.addTab(imgUrl, false, true, true, true); break;
                    case 2: uiController.getDialogManager().downloadDialog(imgUrl, 0); break;
                    case 3: uiController.copyToClipboard(imgUrl);  break;
                }
            }
        });

        c.show();
    }

    public void showToolsMenu() {
        JListDialog j = new JListDialog(uiController.getActivity());
        j.setListItems(new String[]{
                uiController.getString(R.string.a_bookmark),
                uiController.getString(R.string.add_speed_dial),
                uiController.getString(R.string.copy_link),
                uiController.getString(R.string.a_share),
                uiController.getString(R.string.a_find_in_page),
                uiController.getString(R.string.reload_tab),
                uiController.isDesktopMode() ?
                        uiController.getString(R.string.phone_mode) :
                        uiController.getString(R.string.desktop_mode),
                uiController.getString(R.string.exit)
        });
        j.setTitle(R.string.menu);
        j.setDialogClickListener(new JListDialog.DialogClickListener() {
            @Override
            public void onClick(String clickedItem, int position) {

                if (position == 0) {
                    uiController.bookmark(uiController.getTab().getTabData().getUrl(), uiController.getTab().getTabData().getTitle());
                }

                if (position == 1) {
                    addSpeedDial(
                            uiController.getTab().getTabData().getUrl(),
                            uiController.getTab().getTabData().getTitle(),
                            uiController.getTab().getTabData().getFavicon()
                    );
                }

                if (position == 2) {
                    uiController.copyToClipboard(uiController.getTab().getTabData().getUrl());
                }

                if (position == 3) {
                    uiController.share(uiController.getTab().getTabData().getUrl());
                }

                if (position == 4) {
                    uiController.handleFindDialog();
                }

                if (position == 5) {
                    uiController.reloadCurrentTab();
                }

                if (position == 6) {
                    if(uiController.isDesktopMode()) {
                        uiController.setPhoneMode();
                    } else {
                        uiController.setDesktopMode();
                    }
                }

                if (position == 7) {
                    uiController.exitManual();
                }

            }
        });

        j.show();
    }

    public void showProThanksDialog() {
        JDialog c = new JDialog(uiController.getActivity());
        c.setTitle(R.string.app_name);
        c.setContent(R.string.thanks_for_support);
        c.setCancelable(false);
        c.show();
    }

    public void showUpdateDialog() {
        JDialog c = new JDialog(uiController.getActivity());
        c.setTitle(R.string.update_dialog_title);
        c.setContent(R.string.update_dialog_content);
        c.setYesButtonText(R.string.update);
        c.setCancelable(false);
        c.setDialogClickListener(new JDialog.DialogClickListener() {
            @Override
            public void onYesOrNoClick(JDialog.ButtonType buttonType) {
                if(buttonType == JDialog.ButtonType.POSITIVE) {
                    downloadDialog(Constants.VERSION_APK_URL, 0);
                }
            }
        });
        c.show();
    }

    public void showBeProDialog() {
        JDialog c = new JDialog(uiController.getActivity());
        c.setTitle(R.string.be_pro_dialog_title);
        c.setContent(R.string.be_pro_dialog_content);
        c.setYesButtonText(R.string.support);
        c.setNoButtonText(R.string.a_cancel);
        c.setCancelable(false);
        c.setDialogClickListener(new JDialog.DialogClickListener() {
            @Override
            public void onYesOrNoClick(JDialog.ButtonType buttonType) {
                if(buttonType == JDialog.ButtonType.POSITIVE) {
                    uiController.startAct(SupportActivity.class);
                }
            }
        });
        c.show();
    }

    public void showBeProDialog2() {
        JDialog c = new JDialog(uiController.getActivity());
        c.setTitle(R.string.be_pro_dialog_title);
        c.setContent(R.string.be_pro_dialog2_content);
        c.setYesButtonText(R.string.buy);
        c.setNoButtonText(R.string.a_cancel);
        c.setCancelable(false);
        c.setDialogClickListener(new JDialog.DialogClickListener() {
            @Override
            public void onYesOrNoClick(JDialog.ButtonType buttonType) {
                if(buttonType == JDialog.ButtonType.POSITIVE) {
                    uiController.startAct(SupportActivity.class);
                }
            }
        });
        c.show();
    }

    public void showSecondMenuRemindDialog() {
        JDialog c = new JDialog(uiController.getActivity());
        c.setTitle(R.string.second_menu_remind_dialog_title);
        c.setContent(R.string.second_menu_remind_dialog_content);
        c.setYesButtonText(R.string.a_okay);
        c.setNoButtonText(R.string.a_cancel);
        c.setCancelable(false);
        c.setDialogClickListener(new JDialog.DialogClickListener() {
            @Override
            public void onYesOrNoClick(JDialog.ButtonType buttonType) {
                uiController.getBottomPart().anim(1, 800);
                uiController.getBottomPart().anim(0, 1500);
            }
        });
        c.show();
    }
}
