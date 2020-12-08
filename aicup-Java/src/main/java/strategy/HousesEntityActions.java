package strategy;

import model.*;

public class HousesEntityActions extends BaseEntityActions {

    private Statistics statistics; // Statistics

    private static final int MIN_BUILDERS_COUNT = 3;

    public HousesEntityActions(Statistics statistics) {
        this.statistics = statistics;
    }

    @Override
    protected AttackAction createAttackAction(PlayerView playerView, Entity entity, EntityProperties properties) {
        if (entity.getEntityType() == EntityType.TURRET) {
            EntityType[] validAutoAttackTargets =  new EntityType[0];
            return new AttackAction(null, new AutoAttack(properties.getSightRange(), validAutoAttackTargets));
        }
        return null;
    }

    @Override
    protected BuildAction createBuildAction(PlayerView playerView, Entity entity, EntityProperties properties) {
        if (properties.getBuild() != null) {

            EntityType entityType = properties.getBuild().getOptions()[0];
            int count = statistics.countOfEntityWithType(entityType);
            int buildersCount = statistics.countOfEntityWithType(EntityType.BUILDER_UNIT);

            // First build builders
            if (buildersCount < MIN_BUILDERS_COUNT && entityType != EntityType.BUILDER_UNIT) {
                return null;
            }

            if (buildersCount >= MIN_BUILDERS_COUNT && count * 3 > statistics.getPopulationUse()) {
                return null;
            }

            if (statistics.getPopulationUse() < statistics.getPopulationProvide()) {
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
}
