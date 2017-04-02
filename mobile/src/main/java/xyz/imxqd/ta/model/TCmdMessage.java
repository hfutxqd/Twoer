package xyz.imxqd.ta.model;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

/**
 * Created by imxqd on 17-4-1.
 */

public abstract class TCmdMessage extends TMessage {
    public static final String CMD_SHOCK = "CMD_SHOCK";
    public static final String CMD_GET_STEPS = "CMD_GET_STEPS";
    public static final String CMD_GET_HEART_RATE = "CMD_GET_HEART_RATE";
    @StringDef({
            CMD_SHOCK,
            CMD_GET_STEPS,
            CMD_GET_HEART_RATE
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface Cmd {}


    public TCmdMessage(String mTargetId) {
        super(mTargetId);
    }

    @Override
    public MessageContent getContent() {
        TextMessage msg = TextMessage.obtain("");
        msg.setExtra(getCmd());
        return msg;
    }

    @Cmd
    public abstract String getCmd();

    @Override
    public int getMessageType() {
        return TYPE_CMD;
    }

}
