package xyz.imxqd.ta.model;

/**
 * Created by imxqd on 17-4-1.
 */

public abstract class TMessage implements ITMessage {

    private String mTargetId;

    public TMessage(String mTargetId) {
        this.mTargetId = mTargetId;
    }

    @Override
    public String getTargetId() {
        return mTargetId;
    }
}
