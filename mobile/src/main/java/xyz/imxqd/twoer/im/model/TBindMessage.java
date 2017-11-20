package xyz.imxqd.twoer.im.model;

import java.util.Random;

import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

/**
 * Created by imxqd on 17-4-2.
 */

public class TBindMessage extends TCmdMessage {

    private String randomCode;

    public TBindMessage(String targetId, String senderId) {
        super(targetId, senderId);
    }

    public static TBindMessage obtain(String targetId, String sender) {
        TBindMessage msg = new TBindMessage(targetId, sender);
        msg.randomCode = String.valueOf(new Random().nextInt(10000));
        return msg;
    }

    public static TBindMessage obtain(Message message) {
        TBindMessage msg = new TBindMessage(message.getTargetId(), message.getSenderUserId());
        msg.randomCode = ((TextMessage)message.getContent()).getContent();
        return msg;
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
