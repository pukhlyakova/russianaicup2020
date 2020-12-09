package strategy;

import model.*;

public class HousesEntityActions extends BaseEntityActions {

    private Status status; // Statistics

    public HousesEntityActions(Status status) {
        this.status = status;
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

            if (status.getPopulationUse() < status.getPopulationProvide()) {
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
