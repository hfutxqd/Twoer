package xyz.imxqd.twoer.game;

import android.graphics.Point;

import java.util.List;

/**
 * Created by imxqd on 17-4-8.
 */

public interface AI {
    void initBoard(List<Point> whites, List<Point> blacks);
    void addPoint(Point p);
    void reset();
    Point nextBest();
}
