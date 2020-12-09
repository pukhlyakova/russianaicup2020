package strategy;

import model.*;

import java.util.List;

public class BuilderUnitEntityActions {

    private Status status;

    public BuilderUnitEntityActions(Status status) {
        this.status = status;
    }

    public void addEntityActions(PlayerView playerView, List<Entity> entities, Action result) {
        if (entities.isEmpty()) {
            return;
        }
        builderAction(entities, result, playerView);

        for (Entity entity : entities) {
            if (status.getBuilderId() != null && entity.getId() == status.getBuilderId()) {
                continue;
            }
            EntityAction entityAction = collectResources(playerView, entity);
            result.getEntityActions().put(entity.getId(), entityAction);
        }
    }

    private Entity findBuilder(List<Entity> entities) {
        for (Entity entity : entities) {
            if (status.getBuilderId() != null && entity.getId() == status.getBuilderId()) {
                return entity;
            }
        }
        Entity builder = entities.get(0);
        status.setBuilderId(builder.getId());
        return builder;
    }

    private void builderAction(List<Entity> entities, Action result, PlayerView playerView) {
        if (status.getBuilderId() == null && status.getResource() < 100) {
            return;
        }
        Entity builder = findBuilder(entities);

        if (!status.getBrokenHouses().isEmpty()) {
            Entity house = status.getBrokenHouses().get(0);

            MoveAction moveAction = createMovingAction(house.getPosition());
            RepairAction repairAction = createRepairAction(house);

            EntityAction action = new EntityAction( moveAction, null, null, repairAction );
            result.getEntityActions().put(builder.getId(), action);
        } else if (status.shouldBuildHouse()) {
            EntityProperties properties = playerView.getEntityProperties().get(builder.getEntityType());

            MoveAction moveAction = createMovingAction(status.getHouseTarget());
            BuildAction buildAction = createBuildAction(status.getHouseTarget());

            EntityAction action = new EntityAction( moveAction, buildAction, null, null );
            result.getEntityActions().put(builder.getId(), action);
        }
    }

    private EntityAction collectResources(PlayerView playerView, Entity entity) {
        EntityProperties properties = playerView.getEntityProperties().get(entity.getEntityType());

        Vec2Int target = new Vec2Int(playerView.getMapSize() - 1, playerView.getMapSize() - 1);

        for (Entity resource : status.getResources()) {
            if (distance(entity.getPosition(), resource.getPosition()) < distance(entity.getPosition(), target)) {
                target = resource.getPosition();
            }
        }

        MoveAction moveAction = createMovingAction(target);
        AttackAction attackAction = createAttackAction(properties);

        return new EntityAction( moveAction, null, attackAction, null );
    }

    private double distance(Vec2Int c1, Vec2Int c2) {
        // distance between (0, 0) and (x, y)
        int x1 = c1.getX();
        int y1 = c1.getY();

        int x2 = c1.getX();
        int y2 = c1.getY();

        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    private RepairAction createRepairAction(Entity house) {
        return new RepairAction(house.getId());
    }

    private BuildAction createBuildAction(Vec2Int pos) {
        return new BuildAction(EntityType.HOUSE, pos);
    }

    private MoveAction createMovingAction(Vec2Int pos) {
        return new MoveAction(pos, true,true);
    }

    private AttackAction createAttackAction(EntityProperties properties) {
        EntityType[] validAutoAttackTargets = new EntityType[] { EntityType.RESOURCE };
        return new AttackAction(null, new AutoAttack(properties.getSightRange(), validAutoAttackTargets));
    }
}
