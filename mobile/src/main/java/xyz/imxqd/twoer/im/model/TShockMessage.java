package xyz.imxqd.twoer.im.model;

import android.util.Base64;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;
import xyz.imxqd.twoer.App;
import xyz.imxqd.twoer.utils.Serializer;
import xyz.imxqd.twoer.utils.Shocker;
import xyz.imxqd.twoer.utils.UserSettings;

import static xyz.imxqd.twoer.Constants.SETTING_TARGET_ID;

/**
 * Created by imxqd on 17-4-2.
 */

public class TShockMessage extends TCmdMessage {

    private List<Long> list;

    public TShockMessage(String targetId, String senderId) {
        super(targetId, senderId);
        list = new ArrayList<>(2);
    }

    public static TShockMessage obtain() {
        TShockMessage message = new TShockMessage(UserSettings.readString(SETTING_TARGET_ID),
                UserSettings.getUserId(App.get()));
        return message;
    }

    public static TShockMessage obtain(String targetId) {
        TShockMessage message = new TShockMessage(targetId, UserSettings.getUserId(App.get()));
        return message;
    }

    public static TShockMessage obtain(Message message) {
        TextMessage msg = (TextMessage) message.getContent();
        TShockMessage shockMessage = new TShockMessage(message.getTargetId(), message.getSenderUserId());
        byte[] data = Base64.decode(msg.getContent(), Base64.DEFAULT);
        shockMessage.list = (List<Long>) Serializer.byteToObject(data);
        return shockMessage;
    }

    public List<Long> getList() {
        return list;
    }

    public void addPoint(long ms) {
        list.add(ms);
    }

    @Override
    public String getCmd() {
        return CMD_SHOCK;
    }

    public String toMorse() {

        return null;
    }

    public void play() {
        long[] data = new long[list.size()];
        for (int i = 0; i < list.size(); i++) {
            data[i] = list.get(i);
        }
        Shocker.shock(data);
    }

    @Override
    public MessageContent getContent() {
        TextMessage msg = (TextMessage) super.getContent();
        byte[] data = Serializer.objectToByte((Serializable) list);
        msg.setContent(Base64.encodeToString(data, Base64.DEFAULT));
        return msg;
    }
}
