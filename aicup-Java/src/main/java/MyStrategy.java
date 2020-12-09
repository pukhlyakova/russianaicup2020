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

        int myId = playerView.getMyId();

        List<Entity> builders = new ArrayList<>();
        List<Entity> fighters = new ArrayList<>();
        List<Entity> houses = new ArrayList<>();

        status.updateStatus(playerView);

        for (Entity entity : playerView.getEntities()) {
            if (entity.getPlayerId() == null || entity.getPlayerId() != myId) {
                continue;
            }

            switch (entity.getEntityType()) {
                case BUILDER_UNIT:
                    builders.add(entity);
                    break;
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
                    houses.add(entity);
                    break;
                case RESOURCE:
                default:
                    break;
            }
        }

        HousesEntityActions housesEntityActions = new HousesEntityActions(status);
        FighterEntityActions fighterEntityActions = new FighterEntityActions();
        BuilderUnitEntityActions builderUnitEntityActions = new BuilderUnitEntityActions(status);

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