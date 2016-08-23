package com.widevision.pillreminder.abstractClass;

import android.content.Context;

/**
 * Created by mercury-one on 14/1/16.
 */
public abstract class ValidationTemplate {
    public abstract boolean email_validation(String text);

    public abstract boolean nullity(String text);

    public abstract boolean internet_wifi_validation(Context context);

    public abstract boolean GPS_availability(Context context);

    public abstract boolean file_validation(String file_name);

    public abstract boolean file_extension(String file_name);

    public abstract boolean isNumeric(String number);

    public boolean template(Context context, String text, String check_tag) {

        if (check_tag.equals("email")) {
            return email_validation(text);
        } else if (check_tag.equals("internet")) {
            return internet_wifi_validation(context);
        } else if (check_tag.equals("gps")) {
            return GPS_availability(context);
        } else if (check_tag.equals("file")) {
            return file_validation(text);
        } else if (check_tag.equals("extension")) {
            return file_extension(text);
        } else if (check_tag.equals("isnumber")) {
            return isNumeric(text);
        } else {
            return nullity(text);
        }

    }

}
