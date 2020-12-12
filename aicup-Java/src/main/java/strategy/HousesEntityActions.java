package strategy;

import model.*;

import java.util.List;

public class HousesEntityActions {

    private Status status;

    public HousesEntityActions(Status status) {
        this.status = status;
    }

    // priority: attack, build, repair and move
    public void addEntityActions(PlayerView playerView, List<Entity> entities, Action result) {
        for (Entity entity : entities) {
            EntityProperties properties = playerView.getEntityProperties().get(entity.getEntityType());

            BuildAction buildAction = createBuildAction(entity, properties);
            AttackAction attackAction = createAttackAction(entity, properties);

            result.getEntityActions().put(entity.getId(), new EntityAction(
                    null, buildAction, attackAction, null
            ));
        }
    }

    private AttackAction createAttackAction(Entity entity, EntityProperties properties) {
        if (entity.getEntityType() == EntityType.TURRET) {
            EntityType[] validAutoAttackTargets =  new EntityType[0];
            return new AttackAction(null, new AutoAttack(properties.getSightRange(), validAutoAttackTargets));
        }
        return null;
    }

    private BuildAction createBuildAction(Entity entity, EntityProperties properties) {
        if (properties.getBuild() != null) {

            EntityType entityType = properties.getBuild().getOptions()[0];
            int count = status.countOfEntityWithType(entityType);

            // if we have less than 10 builders do not build fighters
            if (entityType != EntityType.BUILDER_UNIT && status.countOfEntityWithType(EntityType.BUILDER_UNIT) < 10) {
                return null;
            }

            // Do not build builders when you have 50 of them
            if (entityType == EntityType.BUILDER_UNIT && status.getResource() >= 500) {
                return null;
            }

            if (status.getPopulationUse() < status.getPopulationProvide()) {
                return new BuildAction(
                        entityType,
                        new Vec2Int(
                                entity.getPosition().getX() + properties.getSize() ,
                                entity.getPosition().getY() + properties.getSize() - 1)
                );
            }
        }
        return null;
    }
}
