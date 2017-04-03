package xyz.imxqd.ta.im.model;

import android.net.Uri;

import java.io.File;

import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.VoiceMessage;
import xyz.imxqd.ta.App;
import xyz.imxqd.ta.media.AudioPlayer;
import xyz.imxqd.ta.utils.UserSettings;

import static xyz.imxqd.ta.Constants.SETTING_TARGET_ID;

/**
 * Created by imxqd on 17-4-1.
 */

public class TVoiceMessage extends TMessage {

    private Uri uri;
    private int duration;

    public TVoiceMessage(String targetId, String senderId) {
        super(targetId, senderId);
    }

    public static TVoiceMessage obtain(String path, String targetId) {
        TVoiceMessage message = new TVoiceMessage(targetId, UserSettings.getUserId(App.get()));
        message.uri = Uri.fromFile(new File(path));
        return message;
    }

    public static TVoiceMessage obtain(String path, int duration) {
        TVoiceMessage message = new TVoiceMessage(UserSettings.readString(SETTING_TARGET_ID)
        , UserSettings.getUserId(App.get()));
        message.uri = Uri.fromFile(new File(path));
        message.duration = duration;
        return message;
    }

    public static TVoiceMessage obtain(Message m) {
        VoiceMessage message = (VoiceMessage) m.getContent();
        TVoiceMessage msg = new TVoiceMessage(m.getTargetId(), m.getSenderUserId());
        msg.uri = message.getUri();
        msg.duration = message.getDuration();
        return msg;
    }

    public void play() {
        AudioPlayer.playSound(uri, null);
    }

    @Override
    public int getMessageType() {
        return TYPE_VOICE;
    }

    @Override
    public MessageContent getContent() {
        return VoiceMessage.obtain(uri, duration);
    }
}
