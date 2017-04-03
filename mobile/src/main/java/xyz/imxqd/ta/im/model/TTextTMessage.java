package xyz.imxqd.ta.im.model;

import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;
import xyz.imxqd.ta.utils.UserSettings;

import static xyz.imxqd.ta.Constants.SETTING_TARGET_ID;

/**
 * Created by imxqd on 17-4-1.
 */

public class TTextTMessage extends TMessage {
    public static final String EXTRA_TEXT_FLAG = "text";

    private String text;
    public TTextTMessage(String mTargetId) {
        super(mTargetId);
    }

    public static TTextTMessage obtain(String text, String targetId) {
        TTextTMessage message = new TTextTMessage(targetId);
        message.text = text;
        return message;
    }

    public static TTextTMessage obtain(String text) {
        TTextTMessage message = new TTextTMessage(UserSettings.readString(SETTING_TARGET_ID));
        message.text = text;
        return message;
    }

    public static TTextTMessage obtain(TextMessage text) {
        TTextTMessage message = new TTextTMessage(UserSettings.readString(SETTING_TARGET_ID));
        message.text = text.getContent();
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
        TextMessage msg = TextMessage.obtain(text);
        msg.setExtra(EXTRA_TEXT_FLAG);
        return msg;
    }
}
