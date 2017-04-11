package xyz.imxqd.ta.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.wearable.DataApi;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.Wearable;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import xyz.imxqd.ta.Constants;
import xyz.imxqd.ta.im.model.TBindMessage;
import xyz.imxqd.ta.im.model.TShockMessage;
import xyz.imxqd.ta.im.model.TVoiceMessage;
import xyz.imxqd.ta.ui.activities.InvitationActivity;
import xyz.imxqd.ta.utils.UserSettings;

import static xyz.imxqd.ta.im.model.TCmdMessage.CMD_BIND;
import static xyz.imxqd.ta.im.model.TCmdMessage.CMD_GET_HEART_RATE;
import static xyz.imxqd.ta.im.model.TCmdMessage.CMD_GET_STEPS;
import static xyz.imxqd.ta.im.model.TCmdMessage.CMD_SHOCK;
import static xyz.imxqd.ta.im.model.TTextTMessage.EXTRA_TEXT_FLAG;

public class MessageService extends Service implements RongIMClient.OnReceiveMessageListener,
        MobvoiApiClient.ConnectionCallbacks , MessageApi.MessageListener, NodeApi.NodeListener, DataApi.DataListener {

    private static final String TAG = "MessageService";

    private MessageBinder mBinder;
    private MobvoiApiClient mMobvoiApiClient;

    private BindCallback mBindCallback;

    private Handler mHandler = new Handler(Looper.getMainLooper());


    public MessageService() {
    }

    @Override
    public void onCreate() {
        RongIMClient.setOnReceiveMessageListener(this);
        mBinder = new MessageBinder();
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
        mMobvoiApiClient.connect();
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

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected: ");
        Wearable.MessageApi.addListener(mMobvoiApiClient, this);
        Wearable.NodeApi.addListener(mMobvoiApiClient, this);
        Wearable.DataApi.addListener(mMobvoiApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: " + i);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived: " + messageEvent);
    }

    @Override
    public void onPeerConnected(Node node) {
        Log.d(TAG, "onPeerConnected: " + node);
    }

    @Override
    public void onPeerDisconnected(Node node) {
        Log.d(TAG, "onPeerDisconnected: " + node);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {

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
