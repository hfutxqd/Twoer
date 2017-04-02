package xyz.imxqd.ta.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.rong.imlib.model.MessageContent;

/**
 * Created by imxqd on 17-4-1.
 */

public interface ITMessage {
    int TYPE_TEXT = 1;
    int TYPE_VOICE = 2;
    int TYPE_CMD = 4;
    @IntDef({
            TYPE_TEXT,
            TYPE_VOICE,
            TYPE_CMD
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface Type {}

    @Type int getMessageType();

    String getTargetId();
    MessageContent getContent();
}
