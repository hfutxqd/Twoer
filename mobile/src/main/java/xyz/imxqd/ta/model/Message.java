package xyz.imxqd.ta.model;

/**
 * Created by imxqd on 17-4-1.
 */

public abstract class Message implements IMessage {

    private String mTargetId;

    public Message(String mTargetId) {
        this.mTargetId = mTargetId;
    }

    @Override
    public String getRecipientId() {
        return mTargetId;
    }
}
