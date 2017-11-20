package xyz.imxqd.twoer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;

import xyz.imxqd.twoer.App;

/**
 * Created by imxqd on 17-4-1.
 */

public class UserSettings {
    private static final String FILE_NAME = "settings";

    public static void save(String key, String value) {
        SharedPreferences preferences = App.get().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .putString(key, value)
                .apply();
    }

    public static void save(String key, boolean value) {
        SharedPreferences preferences = App.get().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .putBoolean(key, value)
                .apply();
    }

    public static String readString(String key) {
        SharedPreferences preferences = App.get().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }

    public static boolean readBoolean(String key) {
        SharedPreferences preferences = App.get().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, false);
    }

    public static String getUserId(Context context) {
        return Settings.System.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }


    public static int getAILevel(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.valueOf(preferences.getString("ai_level", "2000"));
    }
}
