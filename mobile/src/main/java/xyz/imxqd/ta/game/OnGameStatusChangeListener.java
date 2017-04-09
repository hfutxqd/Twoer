package xyz.imxqd.ta.game;

import android.graphics.Point;

/**
 * Created by imxqd on 17-4-3.
 */

public interface OnGameStatusChangeListener {


    void onPlacePiece(@FiveChessPanel.Type int type, Point point);
    void onGameOver(int gameWinResult);//游戏结束
}
