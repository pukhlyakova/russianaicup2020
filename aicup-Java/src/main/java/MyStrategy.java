import model.*;

public class MyStrategy {
    public Action getAction(PlayerView playerView, DebugInterface debugInterface) {
        Action result = new Action(new java.util.HashMap<>());
        int myId = playerView.getMyId();
        for (Entity entity : playerView.getEntities()) {
            if (entity.getPlayerId() == null || entity.getPlayerId() != myId) {
                continue;
            }
            EntityProperties properties = playerView.getEntityProperties().get(entity.getEntityType());

            MoveAction moveAction = createMovingAction(playerView, entity, properties);
            BuildAction buildAction = createBuildAction(playerView, entity, properties);
            AttackAction attackAction = createAttackAction(playerView, entity, properties);

            result.getEntityActions().put(entity.getId(), new EntityAction(
                    moveAction,
                    buildAction,
                    attackAction,
                    null
            ));
        }
        return result;
    }

    private MoveAction createMovingAction(PlayerView playerView, Entity entity, EntityProperties properties) {
        if (properties.isCanMove()) {
            return new MoveAction(
                    new Vec2Int(playerView.getMapSize() - 1, playerView.getMapSize() - 1),
                    true,
                    true);
        }
        return null;
    }

    private AttackAction createAttackAction(PlayerView playerView, Entity entity, EntityProperties properties) {
        EntityType[] validAutoAttackTargets;
        if (entity.getEntityType() == EntityType.BUILDER_UNIT) {
            validAutoAttackTargets = new EntityType[] { EntityType.RESOURCE };
        } else {
            validAutoAttackTargets = new EntityType[0];
        }
        return new AttackAction(null, new AutoAttack(properties.getSightRange(), validAutoAttackTargets));
    }

    private BuildAction createBuildAction(PlayerView playerView, Entity entity, EntityProperties properties) {
        if (properties.getBuild() != null) {
            int myId = playerView.getMyId();
            EntityType entityType = properties.getBuild().getOptions()[0];
            int currentUnits = 0;
            for (Entity otherEntity : playerView.getEntities()) {
                if (otherEntity.getPlayerId() != null && otherEntity.getPlayerId() == myId
                        && otherEntity.getEntityType() == entityType) {
                    currentUnits++;
                }
            }
            if ((currentUnits + 1) * playerView.getEntityProperties().get(entityType).getPopulationUse() <= properties.getPopulationProvide()) {
                return new BuildAction(
                        entityType,
                        new Vec2Int(
                                entity.getPosition().getX() + properties.getSize(),
                                entity.getPosition().getY() + properties.getSize() - 1
                        )
                );
            }
        }
        return null;
    }

    public void debugUpdate(PlayerView playerView, DebugInterface debugInterface) {
        debugInterface.send(new DebugCommand.Clear());
        debugInterface.getState();
    }
}