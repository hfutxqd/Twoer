package xyz.imxqd.ta.im;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import org.apache.commons.codec_android.digest.DigestUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import io.rong.imlib.IRongCallback;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserOnlineStatusInfo;
import xyz.imxqd.ta.Constants;
import xyz.imxqd.ta.im.model.ITMessage;
import xyz.imxqd.ta.utils.UserSettings;

import static xyz.imxqd.ta.Constants.SETTING_TARGET_ID;

/**
 * Created by imxqd on 2017/3/31.
 */

public class Client extends RongIMClient.ConnectCallback {

    private static final String TAG = "Client";

    public static String PATH_AUDIO = "/sdcard/data/xyz.imxqd.ta/files";


    private static Client client;
    private Context mContext;
    private String mToken;
    private RongIMClient imClient;
    private String mUserId;
    private String mTargetId;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    static {
        System.loadLibrary("tool-lib");
    }

    private Client() {
    }

    public static String getUserId() {
        if (client != null) {
            return client.mUserId;
        }
        return null;
    }

    @Override
    public void onSuccess(String s) {
        Log.d(TAG, "connect onSuccess: " + s);
    }

    @Override
    public void onError(RongIMClient.ErrorCode errorCode) {
        Log.e(TAG, "connect onError: " + errorCode);
    }

    @Override
    public void onTokenIncorrect() {
        Log.e(TAG, "connect onTokenIncorrect: ");
        initToken();
    }

    private native static String stringFromJNI();

    private native static String URL();

    public static void init(Context context) {
        if (client == null) {
            RongIMClient.init(context);
            client = new Client();
            client.mUserId = Settings.System.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            File cache = context.getExternalCacheDir();
            if (cache != null) {
                PATH_AUDIO = cache.getAbsolutePath();
            }
            client.mContext = context;
            client.initToken();
            client.imClient = RongIMClient.getInstance();
        }
    }

    public static RongIMClient get() {
        return client.imClient;
    }

    public static void setTargetId(String targetId) {
        client.mTargetId = targetId;
    }

    public static void getHistoryMessage() {

    }

    final static Object lock = new Object();
    static Boolean isOnline = null;

    public static boolean isUserOnline(final String userId) {
        isOnline = null;
        client.imClient.getUserOnlineStatus(userId, new IRongCallback.IGetUserOnlineStatusCallback() {

            @Override
            public void onSuccess(ArrayList<UserOnlineStatusInfo> arrayList) {
                Log.d(TAG, "isUserOnline onSuccess : " + userId);
                isOnline = (arrayList != null);
                synchronized (lock) {
                    isOnline = true;
                    lock.notifyAll();
                }
            }

            @Override
            public void onError(int i) {
                Log.e(TAG, "isUserOnline onError: " + i);
                isOnline = false;
               synchronized (lock) {
                   isOnline = false;
                   lock.notifyAll();
               }
            }
        });
        synchronized (lock) {
            if (isOnline == null) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return isOnline;
    }

    private static void send(String targetId, MessageContent content) {
        Log.d(TAG, "send: " + content);
        client.imClient.sendMessage(Conversation.ConversationType.PRIVATE, targetId, content,
                null, null, new IRongCallback.ISendMessageCallback() {
            @Override
            public void onAttached(Message message) {
                Log.d(TAG, "send onAttached: " + message);
            }

            @Override
            public void onSuccess(Message message) {
                Log.d(TAG, "send onSuccess: " + message);
            }

            @Override
            public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                Log.e(TAG, "send onError: " + message);
            }
        });
    }

    public static void sendMessage(ITMessage msg) {
        Log.d(TAG, "sendMessage: " + msg.getTargetId());
        send(msg.getTargetId(), msg.getContent());
    }

    public void initToken() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long time = System.currentTimeMillis();
                    String nonce = String.valueOf(new Random(time).nextLong());
                    String timestamp = String.valueOf(time / 1000);
                    Document doc = Jsoup.connect(URL())
                            .header("App-Key", Constants.APP_KEY)
                            .header("Nonce", nonce)
                            .header("Timestamp", timestamp)
                            .header("Signature", getSignature(nonce, timestamp))
                            .data("userId", mUserId)
                            .data("name", "1")
                            .data("portraitUri", "2")
                            .post();
                    String code = doc.getElementsByTag("code").text();
                    String token = doc.getElementsByTag("token").text();
                    Log.d(TAG, "code : " + code);
                    Log.d(TAG, "token: " + token);
                    if ("200".equals(code)) {
                        mToken = token;
                    }
                    client.mTargetId = UserSettings.readString(SETTING_TARGET_ID);
                    RongIMClient.connect(mToken, Client.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String getSignature(String nonce, String timestamp) {
        return DigestUtils.sha1Hex(stringFromJNI() + nonce + timestamp);
    }
}
