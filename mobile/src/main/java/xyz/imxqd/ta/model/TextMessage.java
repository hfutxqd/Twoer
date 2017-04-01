package xyz.imxqd.ta.model;

import io.rong.imlib.model.MessageContent;
import xyz.imxqd.ta.utils.UserSettings;

import static xyz.imxqd.ta.Constants.SETTING_TARGET_ID;

/**
 * Created by imxqd on 17-4-1.
 */

public class TextMessage extends Message {
    private String text;
    public TextMessage(String mTargetId) {
        super(mTargetId);
    }

    public static TextMessage obtain(String text, String targetId) {
        TextMessage message = new TextMessage(targetId);
        message.text = text;
        return message;
    }

    public static TextMessage obtain(String text) {
        TextMessage message = new TextMessage(UserSettings.readString(SETTING_TARGET_ID));
        message.text = text;
        return message;
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
        return io.rong.message.TextMessage.obtain(text);
    }
}
