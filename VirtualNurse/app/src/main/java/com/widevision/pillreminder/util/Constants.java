package com.widevision.pillreminder.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.widevision.pillreminder.R;
import com.widevision.pillreminder.showcaseview.OnShowcaseEventListener;
import com.widevision.pillreminder.showcaseview.ShowcaseView;
import com.widevision.pillreminder.showcaseview.targets.ViewTarget;

import org.joda.time.DateTime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mercury-one on 7/8/15.
 */
public class Constants {
    public static int width;
    public static int height;
    public static Context context;
    public static  String dateTimePattern = "dd-MM-yyyy HH:mm";
    public static  String datePattern = "dd-MM-yyyy";
    public static  String timePattern = "HH:mm";
    public static  String formatter = "dd_MM_yyyy";
    public static boolean buttonEnable = true;
    public static int WALKTHROUGH_ITEM_CLICK = 0;
    public static int visit=0;
    public static String Directory_path = ".virtualnurse3";

    public static void hideSoftKeyboard(Activity activity) {

        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
                    .getWindowToken(), 0);

        } catch (Exception ae) {

        }
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public static void alert(final Activity activity, final String msg) {
        final Dialog dialog = new Dialog(activity, R.style.DialogSlideAnim);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView message = (TextView) dialog.findViewById(R.id.dialog_message);
        message.setText(msg);
        TextView okBtn = (TextView) dialog.findViewById(R.id.dialog_ok);


        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                if (msg.equals("Registration done successfully.")) {
                    activity.finish();
                }

            }
        });

        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }


    public static String getCurrentTime() {
        DateTime dateTime = new DateTime();
        return dateTime.toString(dateTimePattern);
    }


    public static String getCurrentDate() {
        DateTime dateTime = new DateTime();
        return dateTime.toString(datePattern);
    }

    public static String getCurrentTimeOnly() {
        DateTime dateTime = new DateTime();
        return dateTime.toString(dateTimePattern);
    }
    public static void setButtonEnable() {
        buttonEnable = false;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                buttonEnable = true;
            }
        }, 800);
    }

    public static boolean email_validation(String text) {

        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@" + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
                + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
        Matcher emailMatcher = emailPattern.matcher(text);
        return emailMatcher.matches();
    }

    public static ShowcaseView showWalkThrough(Activity context, String preferenceId, int targetId,int buttonLayout, String titleText, String contentText, int styleTheme, boolean isCircular, OnShowcaseEventListener listener) {
        PreferenceConnector.writeString(context, preferenceId, "Yes");
        ViewTarget target = new ViewTarget(targetId, context);

        if (isCircular) {
            return new ShowcaseView.Builder(context)
                    .withNewStyleShowcase()
                    .setTarget(target)
                    .setContentTitle(titleText)
                    .setContentText(contentText)
                    .setStyle(styleTheme)
                    .replaceEndButton(buttonLayout)
                    .setShowcaseEventListener(listener)
                    .build();
        } else {
            return new ShowcaseView.Builder(context)
                    .withNewStyleShowcase(context.findViewById(targetId))
                    .setTarget(target)
                    .setContentTitle(titleText)
                    .setContentText(contentText)
                    .setStyle(styleTheme)
                    .replaceEndButton(buttonLayout)
                    .setShowcaseEventListener(listener)
                    .build();
        }
    }


    public static boolean isNetworkAvailable(Activity context, View coordinatorLayout) {
        ConnectivityManager connec =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            // if connected with internet


            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            alert(context, "Please connect to Internet.");
            return false;
        }
        return false;
    }
}
