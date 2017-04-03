package xyz.imxqd.ta.im.model;

import android.graphics.Point;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

/**
 * Created by imxqd on 17-4-4.
 */

public class TGameMessage extends TCmdMessage {

    public static final int ACTION_START = 0;
    public static final int ACTION_BLACK = 1;
    public static final int ACTION_WHITE = 2;
    public static final int ACTION_EXIT = 3;
    public static final int ACTION_UNDO = 4;
    public static final int ACTION_RESTART = 5;

    @IntDef({
            ACTION_START,
            ACTION_BLACK,
            ACTION_WHITE,
            ACTION_EXIT,
            ACTION_UNDO,
            ACTION_RESTART
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface Action {}

    @Action
    private int action;

    private Point point;

    public TGameMessage(String targetId, String senderId) {
        super(targetId, senderId);
    }

    @SuppressWarnings("WrongConstant")
    public static TGameMessage obtain(Message message) {
        TGameMessage msg = new TGameMessage(message.getTargetId(), message.getSenderUserId());
        TextMessage textMessage = (TextMessage) message.getContent();
        try {
            JSONObject object = new JSONObject(textMessage.getContent());
            msg.action = object.getInt("action");
            int x = object.getInt("x");
            int y = object.getInt("y");
            msg.point = new Point(x, y);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msg;
    }

    @Action
    public int getAction() {
        return action;
    }

    public void setAction(@Action int action) {
        this.action = action;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public MessageContent getContent() {
        TextMessage msg = (TextMessage)super.getContent();
        JSONObject object = new JSONObject();
        try {
            object.put("action", action);
            object.put("x", point.x);
            object.put("y", point.y);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        msg.setContent(object.toString());
        return msg;
    }

    @Override
    public String getCmd() {
        return CMD_GAME;
    }
}
