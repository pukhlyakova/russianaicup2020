package strategy;

import model.*;

import java.util.*;

public class Status {
    private int resource; // count of resource
    private int populationUse; // population that user use
    private int populationProvide; // population that buildings provide
    private int houseSize; // size of HOUSE
    private int baseSize; // size of BUILDER_BASE, RANGED_BASE and MELEE_BASE
    private int mapSize; // size of map

    private static final int ENTITY_TYPE_COUNT = 10;

    private int[] entityTypeCount; // count of my entities
    private int[] activeEntityTypeCount; // count of my entities that is active

    private int[][] map; // contains EntityType.tag + 1, because 0 is for empty

    private Vec2Int houseTarget; // coordinates for the construction of HOUSE

    private Vec2Int bigBuildingTarget; // coordinates for the construction BUILDER_BASE, RANGED_BASE and MELEE_BASE

    private List<Entity> resources; // list of resources

    private List<Entity> brokenHouses; // list of buildings that should be repaired

    private List<Entity> enemies; // list of enemies

    public Status() {
        entityTypeCount = new int[ENTITY_TYPE_COUNT];
        activeEntityTypeCount = new int[ENTITY_TYPE_COUNT];
        resources = new ArrayList<>();
        brokenHouses = new ArrayList<>();
        enemies = new ArrayList<>();
    }

    public void updateStatus(PlayerView playerView) {
        int myId = playerView.getMyId();

        // save sizes
        mapSize = playerView.getMapSize();
        houseSize = playerView.getEntityProperties().get(EntityType.HOUSE).getSize();
        baseSize = playerView.getEntityProperties().get(EntityType.BUILDER_BASE).getSize();

        // clear
        resources.clear();
        brokenHouses.clear();
        enemies.clear();

        // fill map
        fillMap(playerView);

        // update buildings target coordinates.
        updateHouseTarget();
        updateBigBuildingTarget();

        // save resource info
        for (Player player : playerView.getPlayers()) {
            if (player.getId() == myId) {
                resource = player.getResource();
            }
        }

        // save population info
        populationUse = 0;
        populationProvide = 0;

        // clear entityTypeCount and activeEntityTypeCount
        for (int i = 0; i < ENTITY_TYPE_COUNT; ++i) {
            entityTypeCount[i] = 0;
            activeEntityTypeCount[i] = 0;
        }

        for (Entity entity : playerView.getEntities()) {
            EntityProperties properties = playerView.getEntityProperties().get(entity.getEntityType());

            if (entity.getEntityType() == EntityType.RESOURCE) {
                resources.add(entity);
            }

            if (entity.getPlayerId() != null && entity.getPlayerId() != myId) {
                enemies.add(entity);
            }

            if (entity.getPlayerId() == null || entity.getPlayerId() != myId) {
                continue;
            }

            if (isHouse(entity.getEntityType()) && entity.getHealth() < properties.getMaxHealth()) {
                brokenHouses.add(entity);
            }

            if (entity.isActive()) {
                populationProvide += properties.getPopulationProvide();
            }
            populationUse += properties.getPopulationUse();

            entityTypeCount[entity.getEntityType().tag]++;
            if (entity.isActive()) {
                activeEntityTypeCount[entity.getEntityType().tag]++;
            }
        }
    }

    private void fillMap(PlayerView playerView) {
        // create or clear map
        if (map == null) {
            map = new int[mapSize][mapSize];
        } else {
            for (int i = 0; i < mapSize; i++) {
                for (int j = 0; j < mapSize; j++) {
                    map[i][j] = 0;
                }
            }
        }

        // fill the map
        for (Entity entity : playerView.getEntities()) {
            EntityProperties properties = playerView.getEntityProperties().get(entity.getEntityType());

            int x = entity.getPosition().getX();
            int y = entity.getPosition().getY();
            int size = properties.getSize();
            int tag = entity.getEntityType().tag;

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    map[x + i][y + j] = tag + 1;
                }
            }
        }
    }

    private void updateHouseTarget() {
        houseTarget = findHouseTarget(houseSize);
    }

    private void updateBigBuildingTarget() {
        bigBuildingTarget = findHouseTarget(baseSize);
    }

    public Vec2Int findHouseTarget(int buildingSize) {
        boolean found;

        int targetX = mapSize - 1;
        int targetY = mapSize - 1;

        int spaceSize = buildingSize + 2; // need space to stay and to move
        Vec2Int zero = new Vec2Int(0, 0);

        for (int i = 0; i < mapSize / 2; i++) {
            for (int j = 0; j < mapSize / 2; j++) {
                found = true;
                for (int k = 0; k < spaceSize; ++k) {
                    for (int l = 0; l < spaceSize; ++l) {
                        if (!isCellEmpty(i + k, j + l)) {
                            found = false;
                        }
                    }
                }
                if (found && Utils.distance(zero, new Vec2Int(i, j)) <
                             Utils.distance(zero, new Vec2Int(targetX, targetY))) {
                    targetX = i;
                    targetY = j;
                }
            }
        }

        return new Vec2Int(targetX, targetY);
    }

    private boolean isHouse(EntityType type) {
        return type == EntityType.WALL ||
               type == EntityType.HOUSE ||
               type == EntityType.BUILDER_BASE ||
               type == EntityType.MELEE_BASE ||
               type == EntityType.RANGED_BASE ||
               type == EntityType.TURRET;
    }

    private boolean isCellEmpty(int x, int y) {
        return map[x][y] == 0 || // empty
               map[x][y] == EntityType.BUILDER_UNIT.tag + 1 ||
               map[x][y] == EntityType.RANGED_UNIT.tag + 1 ||
               map[x][y] == EntityType.MELEE_UNIT.tag + 1;
    }

    public int countOfEntityWithType(EntityType entityType) {
        return entityTypeCount[entityType.tag];
    }

    public int countOfActiveEntityWithType(EntityType entityType) {
        return activeEntityTypeCount[entityType.tag];
    }

    public int getResource() {
        return resource;
    }

    public int getPopulationUse() {
        return populationUse;
    }

    public int getPopulationProvide() {
        return populationProvide;
    }

    public Vec2Int getHouseTarget() {
        return houseTarget;
    }

    public Vec2Int getBigBuildingTarget() {
        return bigBuildingTarget;
    }

    public List<Entity> getResources() {
        return resources;
    }

    public List<Entity> getBrokenHouses() {
        return brokenHouses;
    }

    public List<Entity> getEnemies() {
        return enemies;
    }

    public int getMapSize() {
        return mapSize;
    }
}
