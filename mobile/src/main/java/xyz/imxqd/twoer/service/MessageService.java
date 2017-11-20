package xyz.imxqd.twoer.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import xyz.imxqd.twoer.Constants;
import xyz.imxqd.twoer.im.model.TBindMessage;
import xyz.imxqd.twoer.im.model.TShockMessage;
import xyz.imxqd.twoer.im.model.TVoiceMessage;
import xyz.imxqd.twoer.ui.activities.InvitationActivity;
import xyz.imxqd.twoer.utils.UserSettings;

import static xyz.imxqd.twoer.im.model.TCmdMessage.CMD_BIND;
import static xyz.imxqd.twoer.im.model.TCmdMessage.CMD_GET_HEART_RATE;
import static xyz.imxqd.twoer.im.model.TCmdMessage.CMD_GET_STEPS;
import static xyz.imxqd.twoer.im.model.TCmdMessage.CMD_SHOCK;
import static xyz.imxqd.twoer.im.model.TTextTMessage.EXTRA_TEXT_FLAG;

public class MessageService extends Service implements RongIMClient.OnReceiveMessageListener {

    private static final String TAG = "MessageService";

    private MessageBinder mBinder;

    private BindCallback mBindCallback;

    private Handler mHandler = new Handler(Looper.getMainLooper());


    public MessageService() {
    }

    @Override
    public void onCreate() {
        RongIMClient.setOnReceiveMessageListener(this);
        mBinder = new MessageBinder();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        mBindCallback = null;
        return true;
    }

    @Override
    public boolean onReceived(Message message, int i) {

        Log.d(TAG, "onReceived: " + i);
        MessageContent content = message.getContent();
        if (content instanceof VoiceMessage) {
            TVoiceMessage msg = TVoiceMessage.obtain((message));
            msg.play();
        } else if (content instanceof  TextMessage) {
            TextMessage msg = (TextMessage) content;
            Log.d(TAG, "onReceived: " + msg.getContent());
            switch (msg.getExtra()) {
                case CMD_BIND: {
                    TBindMessage m = TBindMessage.obtain(message);
                    if (m.getSenderId().equals(UserSettings.readString(Constants.SETTING_TARGET_ID))) {
                        UserSettings.save(Constants.SETTING_ACCEPTED, true);
                        if (mBindCallback != null) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mBindCallback.onAccept();
                                }
                            });
                        }
                    } else {
                        InvitationActivity.start(m.getSenderId(), m.getRandomCode(), getApplicationContext());
                    }
                }

                    break;
                case EXTRA_TEXT_FLAG: {
                    Log.d(TAG, "onReceived: " + msg.getContent());
                }

                    break;
                case CMD_SHOCK: {
                    TShockMessage tmsg = TShockMessage.obtain(message);
                    tmsg.play();
                }
                    break;
                case CMD_GET_STEPS: {

                }
                    break;
                case CMD_GET_HEART_RATE: {

                }
                    break;
            }
        }
        return true;
    }

    public class MessageBinder extends Binder {
        public void setBindCallback(BindCallback callback) {
            mBindCallback = callback;
        }
    }

    public interface BindCallback {
        void onAccept();
    }
}
