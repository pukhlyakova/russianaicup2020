package strategy;

import model.*;

import java.util.List;

public class HousesEntityActions {

    private Status status;

    private static final int MAX_BUILDERS = 60;

    private static final int BUILDER_UNIT_MIN = 10;

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
                    null, buildAction, attackAction, null));
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

            int builderCount = status.countOfEntityWithType(EntityType.BUILDER_UNIT);

            // Do not build builders when you have MAX_BUILDERS builders
            if (entityType == EntityType.BUILDER_UNIT && builderCount >= MAX_BUILDERS) {
                return null;
            }

            // If we have less than BUILDER_UNIT_MIN builders do not build fighters
            if (entityType != EntityType.BUILDER_UNIT && builderCount < BUILDER_UNIT_MIN) {
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
