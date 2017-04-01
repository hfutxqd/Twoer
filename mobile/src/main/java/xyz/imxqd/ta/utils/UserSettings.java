package xyz.imxqd.ta.utils;

import android.content.Context;
import android.content.SharedPreferences;

import xyz.imxqd.ta.App;

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

    public static String readString(String key) {
        SharedPreferences preferences = App.get().getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return preferences.getString(key, null);
    }
}
