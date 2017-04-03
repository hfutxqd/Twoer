package xyz.imxqd.ta.im.model;

import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;
import xyz.imxqd.ta.App;
import xyz.imxqd.ta.utils.UserSettings;

import static xyz.imxqd.ta.Constants.SETTING_TARGET_ID;

/**
 * Created by imxqd on 17-4-1.
 */

public class TTextTMessage extends TMessage {
    public static final String EXTRA_TEXT_FLAG = "text";

    private String text;
    public TTextTMessage(String targetId, String senderId) {

        super(targetId, senderId);
    }

    public static TTextTMessage obtain(String text, String targetId) {
        String senderId = UserSettings.getUserId(App.get());
        TTextTMessage message = new TTextTMessage(targetId, senderId);
        message.text = text;
        return message;
    }

    public static TTextTMessage obtain(String text) {
        String senderId = UserSettings.getUserId(App.get());
        TTextTMessage message = new TTextTMessage(UserSettings.readString(SETTING_TARGET_ID), senderId);
        message.text = text;
        return message;
    }

    public static TTextTMessage obtain(Message message) {
        TextMessage text = (TextMessage) message.getContent();
        TTextTMessage ttext = new TTextTMessage(message.getTargetId(), message.getSenderUserId());
        ttext.text = text.getContent();
        return ttext;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int getMessageType() {
        return TYPE_TEXT;
    }

    @Override
    public MessageContent getContent() {
        TextMessage msg = TextMessage.obtain(text);
        msg.setExtra(EXTRA_TEXT_FLAG);
        return msg;
    }
}
