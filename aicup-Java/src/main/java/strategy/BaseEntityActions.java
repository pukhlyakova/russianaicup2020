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
        return null;
    }

    protected AttackAction createAttackAction(PlayerView playerView, Entity entity, EntityProperties properties) {
        EntityType[] validAutoAttackTargets =  new EntityType[0];
        return new AttackAction(null, new AutoAttack(properties.getSightRange(), validAutoAttackTargets));
    }

    protected BuildAction createBuildAction(PlayerView playerView, Entity entity, EntityProperties properties) {
        return null;
    }

    protected RepairAction createRepairAction(PlayerView playerView, Entity entity, EntityProperties properties) {
        return null;
    }
}
