package strategy;

import model.*;

public class Statistics {
    private int resource;
    private int populationUse;
    private int populationProvide;

    private static final int ENTITY_TYPE_COUNT = 10;

    private int[] entityTypeCount;

    private int[][] map; // contains EntityType.tag + 1, because 0 is for empty

    public Statistics() {
        entityTypeCount = new int[ENTITY_TYPE_COUNT];
    }

    public void updateStatistics(PlayerView playerView) {
        int myId = playerView.getMyId();

        // fill map
        fillMap(playerView);

        // save resource info
        for (Player player : playerView.getPlayers()) {
            if (player.getId() == myId) {
                resource = player.getResource();
            }
        }

        // save population info
        populationUse = 0;
        populationProvide = 0;

        // clear entityTypeCount
        for (int i = 0; i < ENTITY_TYPE_COUNT; ++i) {
            entityTypeCount[i] = 0;
        }

        for (Entity entity : playerView.getEntities()) {
            EntityProperties properties = playerView.getEntityProperties().get(entity.getEntityType());

            if (entity.getPlayerId() == null || entity.getPlayerId() != myId) {
                continue;
            }

            if (entity.isActive()) {
                populationProvide += properties.getPopulationProvide();
            }
            populationUse += properties.getPopulationUse();

            entityTypeCount[entity.getEntityType().tag]++;
        }
    }

    public void fillMap(PlayerView playerView) {
        int mapSize = playerView.getMapSize();

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

    public int countOfEntityWithType(EntityType entityType) {
        return entityTypeCount[entityType.tag];
    }

    public boolean shouldBuildHouse() {
        return true;
        //return populationProvide - populationUse < 5;
    }

    public int[][] getMap() {
        return map;
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
}
