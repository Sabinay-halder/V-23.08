package com.widevision.pillreminder.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceConnector {
    public static final String PREF_NAME = "Virtual Nurse";
    public static final int MODE = Context.MODE_PRIVATE;
    public static final String NOTIFICATION_SOUND="notification_sound";
    public static final String NOTIFICATION_SOUND_TEXT="notification_sound_text";
    public static final String ADD_DRUG_NAME = "ADD_DRUG_NAME";
    public static final String ADD_DRUG_FORM = "ADD_DRUG_FORM";
    public static final String ADD_DRUG_IMAGE = "ADD_DRUG_IMAGE";
    public static final String Reminder_start = "Reminder_start";
    public static final String Reminder_end = "Reminder_end";
    public static final String Reminder_repeat_text =  "Reminder_repeat_text";
    public static final String Reminder_repeat_down =  "Reminder_repeat_down";
    public static final String Drug_notification = "Drug_notification";
    public static final String Reminder_dosage_time = "Reminder_dosage_time";
    public static final String Passworddata = "Passworddata";
    public static final String EmailData = "EmailData";
    /////////for subscription//////
    public static final String todayDate = "todayDate";
    public static String items[] = {"com.widevision.pillreminder.monthly", "com.widevision.pillreminder.sixmonth", "com.widevision.pillreminder.annual"};
    public static final String Free_Subscription = "Free_Subscription";
    public static final String subscription = "subscription";
    public static final String Free_Two_Month = "Free_Two_Month";
    public static final String Check_daily = "Check_Daily";

   	public static void writeInteger(Context context, String key, String value) {
		getEditor(context).putString(key, value).commit();

	}

	public static int readInteger(Context context, String key, int defValue) {
		return getPreferences(context).getInt(key, defValue);
	}

	public static void writeString(Context context, String key, String value) {
		getEditor(context).putString(key, value).commit();

	}

	public static String readString(Context context, String key, String defValue) {
		return getPreferences(context).getString(key, defValue);
	}

	public static void writeBoolean(Context context, String key, Boolean value) {
		getEditor(context).putBoolean(key, value).commit();

	}

	public static Boolean readBoolean(Context context, String key,
			Boolean defValue) {
		return getPreferences(context).getBoolean(key, defValue);
	}

	public static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(PREF_NAME, MODE);
	}

	public static Editor getEditor(Context context) {
		return getPreferences(context).edit();
	}

	public static void remove(Context context, String key) {
		getEditor(context).remove(key);

	}
}
