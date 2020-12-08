import model.*;
import strategy.*;

import java.util.*;


public class MyStrategy {
    private Status status; // current status.
    private Statistics statistics; // world statistic

    public MyStrategy() {
        this.status = new Status();
        this.statistics = new Statistics();
    }

    public Action getAction(PlayerView playerView, DebugInterface debugInterface) {
        Action result = new Action(new java.util.HashMap<>());

        int myId = playerView.getMyId();
        List<Entity> builders = new ArrayList<>();
        List<Entity> fighters = new ArrayList<>();
        List<Entity> others = new ArrayList<>();
        List<Entity> houses = new ArrayList<>();
        List<Entity> brokenHouses = new ArrayList<>();

        statistics.updateStatistics(playerView);

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
                case TURRET:
                    if (entity.getHealth() < properties.getMaxHealth()) {
                        brokenHouses.add(entity);
                    }
                    houses.add(entity);
                    break;
                default:
                    others.add(entity);
                    break;
            }
        }

        BaseEntityActions baseEntityActions = new BaseEntityActions();
        HousesEntityActions housesEntityActions = new HousesEntityActions(statistics);
        FighterEntityActions fighterEntityActions = new FighterEntityActions();
        BuilderUnitEntityActions builderUnitEntityActions = new BuilderUnitEntityActions(brokenHouses, statistics, status);

        baseEntityActions.addEntityActions(playerView, others, result);
        housesEntityActions.addEntityActions(playerView, houses, result);
        fighterEntityActions.addEntityActions(playerView, fighters, result);
        builderUnitEntityActions.addEntityActions(playerView, builders, result);

        return result;
    }

    public void debugUpdate(PlayerView playerView, DebugInterface debugInterface) {
        debugInterface.send(new DebugCommand.Clear());
        debugInterface.getState();
    }
}