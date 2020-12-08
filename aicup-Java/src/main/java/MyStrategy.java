import model.*;
import strategy.*;

import java.util.*;


public class MyStrategy {
    private Status status;

    public MyStrategy() {
        this.status = new Status();
    }

    public Action getAction(PlayerView playerView, DebugInterface debugInterface) {
        Action result = new Action(new java.util.HashMap<>());
        Statistics statistics = new Statistics();

        int myId = playerView.getMyId();
        List<Entity> builders = new ArrayList<>();
        List<Entity> fighters = new ArrayList<>();
        List<Entity> others = new ArrayList<>();
        List<Entity> brokenHouse = new ArrayList<>();

        updateStatistics(statistics, playerView);

        for (Entity entity : playerView.getEntities()) {
            if (entity.getPlayerId() == null || entity.getPlayerId() != myId) {
                continue;
            }

            EntityProperties properties = playerView.getEntityProperties().get(entity.getEntityType());

            switch (entity.getEntityType()) {
                case BUILDER_UNIT: {
                    builders.add(entity);
                    break;
                }
                case MELEE_UNIT:
                case RANGED_UNIT:
                    fighters.add(entity);
                    break;
                case WALL:
                case HOUSE:
                case BUILDER_BASE:
                case MELEE_BASE:
                case RANGED_BASE:
                    if (entity.getHealth() < properties.getMaxHealth()) {
                        brokenHouse.add(entity);
                    }
                    others.add(entity);
                    break;
                default:
                    others.add(entity);
                    break;
            }
        }

        BaseEntityActions baseEntityActions = new BaseEntityActions();
        FighterEntityActions fighterEntityActions = new FighterEntityActions();
        BuilderUnitEntityActions builderUnitEntityActions = new BuilderUnitEntityActions(brokenHouse, statistics, status);

        baseEntityActions.addEntityActions(playerView, others, result);
        fighterEntityActions.addEntityActions(playerView, fighters, result);
        builderUnitEntityActions.addEntityActions(playerView, builders, result);

        return result;
    }

    private void updateStatistics(Statistics statistics, PlayerView playerView) {
        int myId = playerView.getMyId();
        int mapSize = playerView.getMapSize();
        int[][] map = new int[mapSize][mapSize];

        for (Player player : playerView.getPlayers()) {
            if (player.getId() == myId) {
                statistics.setResource(player.getResource());
            }
        }

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

            if (entity.getPlayerId() == null || entity.getPlayerId() != myId) {
                continue;
            }

            if (entity.isActive()) {
                statistics.increasePopulationProvide(properties.getPopulationProvide());
            }
            statistics.increasePopulationUse(properties.getPopulationUse());
        }

        statistics.setMap(map);
    }

    public void debugUpdate(PlayerView playerView, DebugInterface debugInterface) {
        debugInterface.send(new DebugCommand.Clear());
        debugInterface.getState();
    }
}