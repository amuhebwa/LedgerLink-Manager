package org.grameenfoundation.applabs.ledgerlinkmanager.helpers;

import android.app.Activity;
import android.preference.PreferenceManager;

public class SharedPrefs {
    public static void saveSharedPreferences(Activity activity, String key, String value) {
        android.content.SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(
                activity.getApplicationContext());
        android.content.SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String readSharedPreferences(Activity activity, String key, String defaultValue) {
        android.content.SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(
                activity.getApplicationContext());
        return sp.getString(key, defaultValue);
    }
}
