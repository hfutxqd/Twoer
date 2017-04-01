package xyz.imxqd.ta;

import android.app.Application;
import android.content.Intent;

import xyz.imxqd.ta.im.Client;
import xyz.imxqd.ta.service.MessageService;
import xyz.imxqd.ta.service.WatchService;

/**
 * Created by imxqd on 2017/3/31.
 */

public class App extends Application {

    private static App app;

    @Override
    public void onCreate() {
        super.onCreate();
        if (app == null) {
            Client.init(this);
            app = this;
            startService(new Intent(this, WatchService.class));
            startService(new Intent(this, MessageService.class));
        }
    }

    public static App get() {
        return app;
    }

}
