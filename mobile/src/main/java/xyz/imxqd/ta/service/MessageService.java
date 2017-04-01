package xyz.imxqd.ta.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.wearable.DataApi;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.PutDataMapRequest;
import com.mobvoi.android.wearable.PutDataRequest;
import com.mobvoi.android.wearable.Wearable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import xyz.imxqd.ta.media.AudioPlayer;
import xyz.imxqd.ta.ui.activities.MainActivity;
import xyz.imxqd.ta.utils.Task;

import static xyz.imxqd.ta.Constants.PATH_CMD_MESSAGE;
import static xyz.imxqd.ta.Constants.PATH_TEXT_MESSAGE;
import static xyz.imxqd.ta.Constants.PATH_VOICE_MESSAGE;

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

        MessageContent content = message.getContent();
        if (content instanceof VoiceMessage) {
            VoiceMessage msg = (VoiceMessage) content;
            Log.d(TAG, "onReceived: " + msg.getUri());
            if (!Task.isForeground(MainActivity.class)) {
                try {
                    FileInputStream in = new FileInputStream(msg.getUri().getPath());
                    byte[] data = new byte[in.available()];
                    in.read(data);
                    in.close();
                    msg.setBase64(Base64.encodeToString(data, Base64.DEFAULT));
                    if (mMobvoiApiClient.isConnected()) {
                        Wearable.MessageApi.sendMessage(mMobvoiApiClient, null, PATH_VOICE_MESSAGE, msg.encode())
                                .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                                    @Override
                                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                        Log.d(TAG, "onResult: " + sendMessageResult);
                                    }
                                });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                AudioPlayer.playSound(msg.getUri(), null);
            }

        } else if (content instanceof  TextMessage) {
            TextMessage msg = (TextMessage) content;
            Log.d(TAG, "onReceived: " + msg.getContent());
            if (mMobvoiApiClient.isConnected()) {
                Wearable.MessageApi.sendMessage(mMobvoiApiClient, null, PATH_TEXT_MESSAGE, msg.encode())
                        .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                            @Override
                            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                Log.d(TAG, "onResult: " + sendMessageResult);
                            }
                        });
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
