package xyz.imxqd.twoer.game;

import android.graphics.Point;

/**
 * Created by imxqd on 17-4-3.
 */

public interface OnGameStatusChangeListener {


    void onPlacePiece(@FiveChessPanel.PieceType int type, Point point);
    void onUndo(@FiveChessPanel.PieceType int type, Point point);
    void onGameOver(int gameWinResult);//游戏结束
    void onGameRestart();
}
