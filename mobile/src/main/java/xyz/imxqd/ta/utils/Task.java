package xyz.imxqd.ta.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import java.util.List;

import xyz.imxqd.ta.App;

/**
 * Created by imxqd on 17-4-1.
 */

public class Task {
    public static boolean isForeground(Class activity) {
        ActivityManager am = (ActivityManager) App.get().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (activity.getName().equals(cpn.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
