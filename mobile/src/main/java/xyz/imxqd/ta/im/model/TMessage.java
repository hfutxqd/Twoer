package xyz.imxqd.ta.im.model;

/**
 * Created by imxqd on 17-4-1.
 */

public abstract class TMessage implements ITMessage {

    private String mTargetId;
    private String mSenderId;

    public TMessage(String targetId, String senderId) {
        this.mTargetId = targetId;
        this.mSenderId = senderId;
    }

    public void setTargetId(String mTargetId) {
        this.mTargetId = mTargetId;
    }

    @Override
    public String getSenderId() {
        return mSenderId;
    }

    public void setSenderId(String mSenderId) {
        this.mSenderId = mSenderId;
    }

    @Override
    public String getTargetId() {
        return mTargetId;
    }
}
