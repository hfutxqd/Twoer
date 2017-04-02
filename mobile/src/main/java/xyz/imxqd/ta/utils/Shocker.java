package xyz.imxqd.ta.utils;

import android.app.Service;
import android.os.Vibrator;

import xyz.imxqd.ta.App;

/**
 * Created by imxqd on 17-4-2.
 */

public class Shocker {
    public static void shock(long[] ms) {
        Vibrator vibrator = (Vibrator) App.get().getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(ms, -1);
    }

    public static void shock() {
        Vibrator vibrator = (Vibrator) App.get().getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(60000);
    }

    public static void cancal() {
        Vibrator vibrator = (Vibrator) App.get().getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.cancel();
    }
}
