package xyz.imxqd.ta.model;

import io.rong.imlib.model.MessageContent;

/**
 * Created by imxqd on 17-4-1.
 */

public class CmdMessage extends Message {
    public static final int CMD_SHOCK = 1;
    public static final int CMD_GET_STEPS = 2;
    public static final int CMD_GET_HEART_RATE = 3;


    public CmdMessage(String mTargetId) {
        super(mTargetId);
    }

    @Override
    public int getMessageType() {
        return TYPE_CMD;
    }

    @Override
    public String getRecipientId() {
        return null;
    }

    @Override
    public MessageContent getContent() {
        return null;
    }
}
