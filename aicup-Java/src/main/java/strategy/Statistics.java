package strategy;

import model.EntityType;
import model.Player;
import model.Vec2Int;

import java.util.LinkedList;

public class Statistics {
    private int resource;
    private int populationUse = 0;
    private int populationProvide = 0;
    private int[][] map; // contains EntityType.tag + 1, because 0 is for empty

    /**
     * This method return closes position of element with given type.
     */
    public Vec2Int closesPositionOfEntityType(EntityType type, Vec2Int currentPos) {
        LinkedList<Vec2Int> linkedList = new LinkedList<>();
        Vec2Int target = currentPos;
        linkedList.addLast(currentPos);

        while (!linkedList.isEmpty()) {
            Vec2Int pos = linkedList.removeFirst();
            if (map[pos.getX()][pos.getY()] == type.tag + 1) { // contains EntityType.tag + 1, because 0 is for empty
                target = pos;
                break;
            }

            if (pos.getX() - 1 >= 0) {
                linkedList.add(new Vec2Int(pos.getX() - 1, pos.getY()));
            }
            if (pos.getY() - 1 >= 0) {
                linkedList.add(new Vec2Int(pos.getX(), pos.getY() - 1));
            }
            if (pos.getX() + 1 < map.length) {
                linkedList.add(new Vec2Int(pos.getX() + 1, pos.getY()));
            }
            if (pos.getY() + 1  < map.length) {
                linkedList.add(new Vec2Int(pos.getX(), pos.getY() + 1));
            }
        }
        return target;
    }

    public void increasePopulationUse(int val) {
        populationUse += val;
    }

    public int getPopulationUse() {
        return populationUse;
    }

    public void increasePopulationProvide(int val) {
        populationProvide += val;
    }

    public int getPopulationProvide() {
        return populationProvide;
    }

    public boolean shouldBuildHouse() {
        return true;
    }

    public int[][] getMap() {
        return map;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }
}
