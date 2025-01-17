package strategy;

import model.*;

import java.util.List;

public class FighterEntityActions {

    private final static int WAIT_COUNT = 30;

    private Status status;

    public FighterEntityActions(Status status) {
        this.status = status;
    }

    // priority: attack, build, repair and move
    public void addEntityActions(PlayerView playerView, List<Entity> entities, Action result) {
        int mapSize = status.getMapSize();
        int half = mapSize / 2;
        int basePose = mapSize / 4;

        Vec2Int center = new Vec2Int(0, 0);
        Entity closes2Center = findTarget(center);

        for (Entity entity : entities) {
            EntityProperties properties = playerView.getEntityProperties().get(entity.getEntityType());

            Entity target = findTarget(entity.getPosition());
            Vec2Int position = new Vec2Int(basePose, basePose);

            if (closes2Center != null && Utils.distance(center, closes2Center.getPosition()) <= half) {
                position = closes2Center.getPosition();
            }
            if (entities.size() >= WAIT_COUNT) {
                if (target != null) {
                    position = target.getPosition();
                } else {
                    position = Utils.findUnseenAngle(status);
                }
            }

            MoveAction moveAction = createMovingAction(position);
            AttackAction attackAction = createAttackAction(target, properties);

            result.getEntityActions().put(entity.getId(), new EntityAction(moveAction,
                                                                null,
                                                                           attackAction,
                                                                null));
        }
    }

    private Entity findTarget(Vec2Int pos) {
        Entity target = null;

        for (Entity enemy : status.getEnemies()) {
            if (target == null) {
                target = enemy;
                continue;
            }
            // Find closes enemy
            if (Utils.distance(pos, enemy.getPosition()) < Utils.distance(pos, target.getPosition())) {
                target = enemy;
            }
        }
        return target;
    }

    private MoveAction createMovingAction(Vec2Int position) {
        return new MoveAction(position, true, true);
    }

    private AttackAction createAttackAction(Entity target, EntityProperties properties) {
        int pathfindRange = properties.getSightRange();

        return new AttackAction(target == null ? null : target.getId(),
                                new AutoAttack(pathfindRange, new EntityType[0]));
    }
}
