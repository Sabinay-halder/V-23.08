package com.widevision.pillreminder.util;

import android.content.Context;

import com.widevision.pillreminder.abstractClass.ValidationTemplate;

/**
 * Created by mercury-one on 14/1/16.
 */
public class Extension {
    public static ValidationTemplate vali;
    public static Extension ext;


    private Extension() {


    }

    public static Extension getInstance() {

        if (vali == null && ext == null) {
            if (vali == null && ext == null) {
                vali = new Implementation();
                ext = new Extension();
                return ext;
            } else {
                return ext;
            }
        } else {
            return ext;
        }


    }

    public boolean executeStrategy(Context context, String text_if_needed, String check_tag) {

        return vali.template(context, text_if_needed, check_tag);

    }

}