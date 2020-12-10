package strategy;

import model.*;

import java.util.List;

public class FighterEntityActions {

    private Status status;

    public FighterEntityActions(Status status) {
        this.status = status;
    }

    // priority: attack, build, repair and move
    public void addEntityActions(PlayerView playerView, List<Entity> entities, Action result) {
        int mapSize = status.getMapSize();
        int quarter = mapSize / 4;

        for (Entity entity : entities) {
            EntityProperties properties = playerView.getEntityProperties().get(entity.getEntityType());

            Entity target = findTarget(entity);
            Vec2Int position = new Vec2Int(quarter, quarter);
            if (entities.size() >= 10 ||
                (target != null && Utils.distance(entity.getPosition(), target.getPosition()) < quarter)) {
                position = new Vec2Int(target.getPosition().getX(), target.getPosition().getY());
            }

            MoveAction moveAction = createMovingAction(position);
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

    private MoveAction createMovingAction(Vec2Int position) {
        return new MoveAction(position, true, true);
    }

    private AttackAction createAttackAction(Entity target, EntityProperties properties) {
        return new AttackAction(target == null ? null : target.getId(),
                                new AutoAttack(properties.getSightRange(),
                                new EntityType[0]));
    }
}
