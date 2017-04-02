package xyz.imxqd.ta.im.model;

import android.util.Base64;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;
import xyz.imxqd.ta.utils.Serializer;
import xyz.imxqd.ta.utils.Shocker;
import xyz.imxqd.ta.utils.UserSettings;

import static xyz.imxqd.ta.Constants.SETTING_TARGET_ID;

/**
 * Created by imxqd on 17-4-2.
 */

public class TShockMessage extends TCmdMessage {

    private List<Long> list;

    public TShockMessage(String mTargetId) {
        super(mTargetId);
        list = new ArrayList<>(2);
    }

    public static TShockMessage obtain() {
        TShockMessage message = new TShockMessage(UserSettings.readString(SETTING_TARGET_ID));
        return message;
    }

    public static TShockMessage obtain(String targetId) {
        TShockMessage message = new TShockMessage(targetId);
        return message;
    }

    public static TShockMessage obtain(TextMessage msg) {
        TShockMessage message = obtain();
        byte[] data = Base64.decode(msg.getContent(), Base64.DEFAULT);
        message.list = (List<Long>) Serializer.byteToObject(data);
        return message;
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
