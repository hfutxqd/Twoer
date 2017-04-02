package xyz.imxqd.ta.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
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
import xyz.imxqd.ta.media.AudioPlayer;
import xyz.imxqd.ta.model.TShockMessage;
import xyz.imxqd.ta.model.TVoiceMessage;

import static xyz.imxqd.ta.model.TCmdMessage.CMD_GET_HEART_RATE;
import static xyz.imxqd.ta.model.TCmdMessage.CMD_GET_STEPS;
import static xyz.imxqd.ta.model.TCmdMessage.CMD_SHOCK;
import static xyz.imxqd.ta.model.TTextTMessage.EXTRA_TEXT_FLAG;

public class MessageService extends Service implements RongIMClient.OnReceiveMessageListener,
        MobvoiApiClient.ConnectionCallbacks , MessageApi.MessageListener, NodeApi.NodeListener, DataApi.DataListener {

    private static final String TAG = "MessageService";

    private MessageBinder mBinder;
    private MobvoiApiClient mMobvoiApiClient;


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
        return mBinder;
    }

    @Override
    public boolean onReceived(Message message, int i) {

        Log.d(TAG, "onReceived: " + i);
        MessageContent content = message.getContent();
        if (content instanceof VoiceMessage) {
            TVoiceMessage msg = TVoiceMessage.obtain((VoiceMessage)content);
            msg.play();
        } else if (content instanceof  TextMessage) {
            TextMessage msg = (TextMessage) content;
            switch (msg.getExtra()) {
                case EXTRA_TEXT_FLAG:
                    Log.d(TAG, "onReceived: " + msg.getContent());
                    break;
                case CMD_SHOCK:
                    TShockMessage tmsg = TShockMessage.obtain(msg);
                    tmsg.play();
                    break;
                case CMD_GET_STEPS:
                    break;
                case CMD_GET_HEART_RATE:
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

    }
}
