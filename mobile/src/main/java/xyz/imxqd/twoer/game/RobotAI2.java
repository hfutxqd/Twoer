package xyz.imxqd.twoer.game;

import android.graphics.Point;

import java.util.List;

/**
 * Created by imxqd on 17-4-8.
 */

public class RobotAI2 implements AI{

    private int mSize;

    static {
        System.loadLibrary("GameAi");
    }

    public RobotAI2(int size, int timeout) {
        this.mSize = size;
        start(size);
        setTimeout(timeout);
    }

    @Override
    public void initBoard(List<Point> whites, List<Point> blacks) {
        int size = whites.size() + blacks.size();
        int[][] map = new int[size][2];
        for (int i = 0,j = 0, k = 0; i < size; i++) {
            if (i % 2 == 0) {
                Point p = blacks.get(j);
                map[i][0] = p.x;
                map[i][1] = p.y;
                j++;
            } else {
                Point p = whites.get(k);
                map[i][0] = p.x;
                map[i][1] = p.y;
                k++;
            }
        }
        initBoard(map, size);
    }

    @Override
    public void addPoint(Point p) {
        int[] a = new int[2];
        a[0] = p.x;
        a[1] = p.y;
        turnMove(a);
    }

    @Override
    public void reset() {
        start(mSize);
    }

    @Override
    public Point nextBest() {
        int[] a = turnBest();
        return new Point(a[0], a[1]);
    }

    private native void initBoard(int[][] map, int size);

    private native void start(int size);

    public native void setTimeout(int timeout);

    private native void turnMove(int[] p);

    private native int[] turnBest();

    public native void restart();
}
