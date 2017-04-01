package xyz.imxqd.ta.services;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.data.FreezableUtils;
import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.Wearable;
import com.mobvoi.android.wearable.WearableListenerService;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;
import xyz.imxqd.ta.media.AudioPlayer;

import static xyz.imxqd.ta.Constants.PATH_CMD_MESSAGE;
import static xyz.imxqd.ta.Constants.PATH_TEXT_MESSAGE;
import static xyz.imxqd.ta.Constants.PATH_VOICE_MESSAGE;

/**
 * Created by imxqd on 17-4-1.
 */

public class DataLayerListenerService extends WearableListenerService {
    public static final String COUNT_PATH = "/count";
    public static final String IMAGE_PATH = "/image";
    public static final String IMAGE_KEY = "photo";
    private static final String TAG = "DataLayer";
    private static final String DATA_ITEM_RECEIVED_PATH = "/data-item-received";
    private static final String COUNT_KEY = "count";

    private MobvoiApiClient mMobvoiApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        mMobvoiApiClient = new MobvoiApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mMobvoiApiClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged: " + dataEvents);
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        ConnectionResult connectionResult = mMobvoiApiClient
                .blockingConnect(30, TimeUnit.SECONDS);
        if (!connectionResult.isSuccess()) {
            return;
        }

        for (DataEvent event : events) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            if (COUNT_PATH.equals(path)) {
                // Get the node id of the node that created the data item from the host portion of
                // the uri.
                String nodeId = uri.getHost();
                // Set the data of the message to be the bytes of the Uri.
                byte[] payload = uri.toString().getBytes();
                // Send the rpc
                Wearable.MessageApi.sendMessage(mMobvoiApiClient, nodeId, DATA_ITEM_RECEIVED_PATH,
                        payload);
            }
        }
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived: " + messageEvent);
        switch (messageEvent.getPath()) {
            case PATH_TEXT_MESSAGE: {
                TextMessage msg = new TextMessage(messageEvent.getData());
                Log.d(TAG, "onMessageReceived: Text " + msg.getContent());
                break;
            }
            case PATH_VOICE_MESSAGE: {
                VoiceMessage msg = new VoiceMessage(messageEvent.getData());
                File file = new File(getFilesDir(), UUID.randomUUID() + ".amr");
                Log.d(TAG, "MessageReceived! Voice + " + msg);
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(Base64.decode(msg.getBase64(), Base64.DEFAULT));
                    out.close();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
                AudioPlayer.playSound(file.getAbsolutePath(), null);
                break;
            }
            case PATH_CMD_MESSAGE: {
                Log.d(TAG, "MessageReceived! Cmd");
                break;
            }
            default: {
                Log.d(TAG, "MessageReceived!");
                break;
            }
        }
    }

    @Override
    public void onPeerConnected(Node peer) {
        Log.d(TAG, "onPeerConnected: " + peer);
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        Log.d(TAG, "onPeerDisconnected: " + peer);
    }
}
