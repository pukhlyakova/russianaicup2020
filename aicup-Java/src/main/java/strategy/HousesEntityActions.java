package strategy;

import model.*;

public class HousesEntityActions extends BaseEntityActions {

    private Statistics statistics; // Statistics

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
