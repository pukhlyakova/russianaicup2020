package strategy;

import model.*;

import java.util.List;

public class BuilderUnitEntityActions {

    private Statistics statistics; // update Statistics
    private Status status; // list of builders, who NOT extract resources
    private List<Entity> brokenHouse; // list of buildings that should be repaired

    public BuilderUnitEntityActions(List<Entity> brokenHouse, Statistics statistics, Status status) {
        this.brokenHouse = brokenHouse;
        this.statistics = statistics;
        this.status = status;
    }

    public void addEntityActions(PlayerView playerView, List<Entity> entities, Action result) {
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
        if (statistics.getResource() < 100) {
            return;
        }

        Entity builder = findBuilder(entities);

        if (!brokenHouse.isEmpty()) {
            Entity house = brokenHouse.get(0);

            MoveAction moveAction = createMovingAction(house.getPosition());
            RepairAction repairAction = createRepairAction(house);

            EntityAction action = new EntityAction( moveAction, null, null, repairAction );
            result.getEntityActions().put(builder.getId(), action);
        } else if (statistics.shouldBuildHouse()) {
            EntityProperties properties = playerView.getEntityProperties().get(builder.getEntityType());

            MoveAction moveAction = createMovingAction(10, 10);
            BuildAction buildAction = createBuildAction(builder, properties);

            EntityAction action = new EntityAction( moveAction, buildAction, null, null );
            result.getEntityActions().put(builder.getId(), action);
        }
    }

    private EntityAction collectResources(PlayerView playerView, Entity entity) {
        EntityProperties properties = playerView.getEntityProperties().get(entity.getEntityType());

        Vec2Int target = statistics.closesPositionOfEntityType(EntityType.RESOURCE, entity.getPosition());
        MoveAction moveAction = createMovingAction(target);
        AttackAction attackAction = createAttackAction(properties);

        return new EntityAction( moveAction, null, attackAction, null );
    }

    private RepairAction createRepairAction(Entity house) {
        return new RepairAction(house.getId());
    }

    private BuildAction createBuildAction(Entity entity, EntityProperties properties) {
        return new BuildAction(EntityType.HOUSE, new Vec2Int(entity.getPosition().getX() + properties.getSize(),
                entity.getPosition().getY() + properties.getSize() - 1));
    }

    private MoveAction createMovingAction(int x, int y) {
        return createMovingAction(new Vec2Int(x, y));
    }

    private MoveAction createMovingAction(Vec2Int pos) {
        return new MoveAction(pos, true,true);
    }

    private AttackAction createAttackAction(EntityProperties properties) {
        EntityType[] validAutoAttackTargets = new EntityType[] { EntityType.RESOURCE };
        return new AttackAction(null, new AutoAttack(properties.getSightRange(), validAutoAttackTargets));
    }
}
