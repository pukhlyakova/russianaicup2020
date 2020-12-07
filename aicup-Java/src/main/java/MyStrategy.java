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
        Set<Entity> builders = new HashSet<>();
        Set<Entity> fighters = new HashSet<>();
        Set<Entity> others = new HashSet<>();
        List<Entity> brokenHouse = new ArrayList<>();

        for (Entity entity : playerView.getEntities()) {
            if (entity.getPlayerId() == null || entity.getPlayerId() != myId) {
                continue;
            }

            EntityProperties properties = playerView.getEntityProperties().get(entity.getEntityType());
            updateStatistics(statistics, entity, properties);

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

    private void updateStatistics(Statistics statistics, Entity entity, EntityProperties properties) {
        if (entity.isActive()) {
            statistics.increasePopulationProvide(properties.getPopulationProvide());
        }
        statistics.increasePopulationUse(properties.getPopulationUse());
    }

    public void debugUpdate(PlayerView playerView, DebugInterface debugInterface) {
        debugInterface.send(new DebugCommand.Clear());
        debugInterface.getState();
    }
}