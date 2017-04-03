package xyz.imxqd.ta.im.model;

import java.util.Random;

import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

/**
 * Created by imxqd on 17-4-2.
 */

public class TBindMessage extends TCmdMessage {

    private String randomCode;
    private String sender;

    public TBindMessage(String mTargetId) {
        super(mTargetId);
    }

    public static TBindMessage obtain(String targetId, String sender) {
        TBindMessage msg = new TBindMessage(targetId);
        msg.sender = sender;
        msg.randomCode = String.valueOf(new Random().nextInt(10000));
        return msg;
    }

    public static TBindMessage obtain(Message message) {
        TBindMessage msg = new TBindMessage(message.getTargetId());
        msg.sender = message.getSenderUserId();
        msg.randomCode = ((TextMessage)message.getContent()).getContent();
        return msg;
    }

    public String getSender() {
        return sender;
    }

    @Override
    public MessageContent getContent() {
        TextMessage msg = (TextMessage) super.getContent();
        msg.setContent(randomCode);
        return msg;
    }

    public String getRandomCode() {
        return randomCode;
    }

    @Override
    public String getCmd() {
        return CMD_BIND;
    }
}
