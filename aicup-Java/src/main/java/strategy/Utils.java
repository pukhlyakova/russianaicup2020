package strategy;

import model.Vec2Int;

public class Utils {

    public static double distance(Vec2Int c1, Vec2Int c2) {
        // distance between c1 and c2
        int x1 = c1.getX();
        int y1 = c1.getY();

        int x2 = c2.getX();
        int y2 = c2.getY();

        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
}
