package xyz.imxqd.ta.im.model;

import io.rong.imlib.model.MessageContent;

/**
 * Created by imxqd on 17-4-2.
 */

public class TStepsMessage extends TCmdMessage {

    public TStepsMessage(String targetId, String senderId) {
        super(targetId, senderId);
    }

    @Override
    public MessageContent getContent() {
        return null;
    }

    @Override
    public String getCmd() {
        return CMD_GET_STEPS;
    }
}
