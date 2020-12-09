package strategy;

import model.*;

import java.util.List;

public class FighterEntityActions {

    private Status status;

    public FighterEntityActions(Status status) {
        this.status = status;
    }

    // priority: attack, build, repair и move

    public void addEntityActions(PlayerView playerView, List<Entity> entities, Action result) {
        for (Entity entity : entities) {
            EntityProperties properties = playerView.getEntityProperties().get(entity.getEntityType());

            Entity target = findTarget(entity);

            MoveAction moveAction = createMovingAction(target);
            AttackAction attackAction = createAttackAction(target, properties);

            result.getEntityActions().put(entity.getId(), new EntityAction(moveAction,
                                                                null,
                                                                           attackAction,
                                                                null));
        }
    }

    private Entity findTarget(Entity entity) {
        Entity target = null;

        for (Entity enemy : status.getEnemies()) {
            if (target == null ||
                    Utils.distance(entity.getPosition(), enemy.getPosition()) <
                    Utils.distance(entity.getPosition(), target.getPosition())) {
                target = enemy;
            }
        }
        return target;
    }

    private MoveAction createMovingAction(Entity target) {
        Vec2Int pos = new Vec2Int(0, 0);
        if (target != null) {
            pos = target.getPosition();
        }
        return new MoveAction(pos, true,true);
    }

    private AttackAction createAttackAction(Entity target, EntityProperties properties) {
        return new AttackAction(target == null ? null : target.getId(),
                                new AutoAttack(properties.getSightRange(),
                                new EntityType[0]));
    }
}
