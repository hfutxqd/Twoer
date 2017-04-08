package xyz.imxqd.ta.game;

import android.graphics.Point;

import java.util.List;

/**
 * Created by imxqd on 17-4-8.
 */

public interface AI {
    Point nextPoint(List<Point> whites, List<Point> blacks);
}
