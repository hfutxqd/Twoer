package xyz.imxqd.ta.model;

import android.net.Uri;

import java.io.File;

import io.rong.imlib.model.MessageContent;

import static xyz.imxqd.ta.Constants.SETTING_TARGET_ID;

/**
 * Created by imxqd on 17-4-1.
 */

public class VoiceMessage extends Message {

    private Uri uri;

    public VoiceMessage(String mTargetId) {
        super(mTargetId);
    }

    public static VoiceMessage obtain(String path, String targetId) {
        VoiceMessage message = new VoiceMessage(targetId);
        message.uri = Uri.fromFile(new File(path));
        return message;
    }

    public static VoiceMessage obtain(String path) {
        VoiceMessage message = new VoiceMessage(SETTING_TARGET_ID);
        message.uri = Uri.fromFile(new File(path));
        return message;
    }

    @Override
    public int getMessageType() {
        return TYPE_VOICE;
    }

    @Override
    public MessageContent getContent() {
        return io.rong.message.VoiceMessage.obtain(uri, 0);
    }
}
