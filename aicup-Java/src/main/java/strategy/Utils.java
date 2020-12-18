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

    public static Vec2Int findUnseenAngle(Status status) {
        int mapSize = status.getMapSize();
        Vec2Int res = new Vec2Int(0, 0);

        if (status.getLastSeen(0, mapSize - 1) < status.getLastSeen(res.getX(), res.getY())) {
            res.setX(0);
            res.setY(mapSize - 1);
        }
        if (status.getLastSeen(mapSize - 1, mapSize - 1) < status.getLastSeen(res.getX(), res.getY())) {
            res.setX(mapSize - 1);
            res.setY(mapSize - 1);
        }
        if (status.getLastSeen(mapSize - 1, 0) < status.getLastSeen(res.getX(), res.getY())) {
            res.setX(mapSize - 1);
            res.setY(0);
        }
        return res;
    }

    public static Vec2Int findClosesUnseenPoint(Status status, Vec2Int pos) {
        int mapSize = status.getMapSize();

        int x = mapSize - 1;
        int y = mapSize - 1;

        for (int i = 0; i < mapSize; ++i) {
            for (int j = 0; j < mapSize; ++j) {
                if (status.getLastSeen(i, j) < status.getLastSeen(x, y) ||
                   (status.getLastSeen(i, j) == status.getLastSeen(x, y) &&
                    Utils.distance(pos, new Vec2Int(i, j)) < Utils.distance(pos, new Vec2Int(x, y)))) {
                    x = i;
                    y = j;
                }
            }
        }
        return new Vec2Int(x, y);
    }
}
