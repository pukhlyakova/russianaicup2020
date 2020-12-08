package strategy;

import model.*;

import java.util.List;

public class BaseEntityActions {

    // priority: attack, build, repair и move

    public void addEntityActions(PlayerView playerView, List<Entity> entities, Action result) {

        for (Entity entity : entities) {
            EntityProperties properties = playerView.getEntityProperties().get(entity.getEntityType());

            MoveAction moveAction = createMovingAction(playerView, entity, properties);
            BuildAction buildAction = createBuildAction(playerView, entity, properties);
            AttackAction attackAction = createAttackAction(playerView, entity, properties);
            RepairAction repairAction = createRepairAction(playerView, entity, properties);

            result.getEntityActions().put(entity.getId(), new EntityAction(
                    moveAction,
                    buildAction,
                    attackAction,
                    repairAction
            ));
        }
    }

    protected MoveAction createMovingAction(PlayerView playerView, Entity entity, EntityProperties properties) {
        if (properties.isCanMove()) {
            return new MoveAction(
                    new Vec2Int(playerView.getMapSize() - 1, playerView.getMapSize() - 1),
                    true,
                    true);
        }
        return null;
    }

    protected AttackAction createAttackAction(PlayerView playerView, Entity entity, EntityProperties properties) {
        EntityType[] validAutoAttackTargets;
        if (entity.getEntityType() == EntityType.BUILDER_UNIT) {
            validAutoAttackTargets = new EntityType[] { EntityType.RESOURCE };
        } else {
            validAutoAttackTargets = new EntityType[0];
        }
        return new AttackAction(null, new AutoAttack(properties.getSightRange(), validAutoAttackTargets));
    }

    protected BuildAction createBuildAction(PlayerView playerView, Entity entity, EntityProperties properties) {
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

    protected RepairAction createRepairAction(PlayerView playerView, Entity entity, EntityProperties properties) {
        return null;
    }
}
