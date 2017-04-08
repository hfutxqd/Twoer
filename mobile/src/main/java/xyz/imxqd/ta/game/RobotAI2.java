package xyz.imxqd.ta.game;

import android.graphics.Point;

import java.util.List;

/**
 * Created by imxqd on 17-4-8.
 */

public class RobotAI2 implements AI{
    static {
        System.loadLibrary("GameAi");
    }

    @Override
    public Point nextPoint(List<Point> whites, List<Point> blacks) {
        int[][] map = new int[15][15];
        for (Point p : whites) {
            map[p.x][p.y] = WuziqiPanel.TYPE_WHITE;
        }
        for (Point p : blacks) {
            map[p.x][p.y] = WuziqiPanel.TYPE_BLACK;
        }
        init(map, 15);
        return null;
    }

    private native void init(int[][] map, int size);
}
