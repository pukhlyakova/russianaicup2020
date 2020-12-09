package strategy;

import model.*;

import java.util.List;

public class BuilderUnitEntityActions {

    private Status status;

    public BuilderUnitEntityActions(Status status) {
        this.status = status;
    }

    // priority: attack, build, repair and move
    public void addEntityActions(PlayerView playerView, List<Entity> entities, Action result) {
        if (status.needNewBuilder()) {
            addNewBuilder(entities);
        }
        for (Entity entity : entities) {
            if (status.getBuilderIds().contains(entity.getId())) {
                builderAction(entity, result);
                continue;
            }
            result.getEntityActions().put(entity.getId(), collectResources(playerView, entity));
        }
    }

    private void addNewBuilder(List<Entity> entities) {
        for (Entity entity : entities) {
            if (!status.getBuilderIds().contains(entity.getId())) {
                status.getBuilderIds().add(entity.getId());
                break;
            }
        }
    }

    private void builderAction(Entity builder, Action result) {
        if (!status.getBrokenHouses().isEmpty()) {
            Entity house = nearestHouseForRepairs(builder);

            MoveAction moveAction = createMovingAction(house.getPosition());
            RepairAction repairAction = createRepairAction(house);

            EntityAction action = new EntityAction( moveAction, null, null, repairAction );
            result.getEntityActions().put(builder.getId(), action);
        } else if (status.shouldBuildHouse()) {
            MoveAction moveAction = createMovingAction(status.getHouseTarget());
            BuildAction buildAction = createBuildAction(builder);

            EntityAction action = new EntityAction( moveAction, buildAction, null, null );
            result.getEntityActions().put(builder.getId(), action);
        }
    }

    private Entity nearestHouseForRepairs(Entity builder) {
        Entity house = null;
        for (Entity entity : status.getBrokenHouses()) {
            if (house == null ||
                    Utils.distance(builder.getPosition(), entity.getPosition()) <
                    Utils.distance(builder.getPosition(), house.getPosition())) {
                house = entity;
            }
        }
        return house;
    }

    private EntityAction collectResources(PlayerView playerView, Entity entity) {
        EntityProperties properties = playerView.getEntityProperties().get(entity.getEntityType());

        Vec2Int target = new Vec2Int(playerView.getMapSize() - 1, playerView.getMapSize() - 1);

        for (Entity resource : status.getResources()) {
            if (Utils.distance(entity.getPosition(), resource.getPosition()) <
                Utils.distance(entity.getPosition(), target)) {
                target = resource.getPosition();
            }
        }
        MoveAction moveAction = createMovingAction(target);
        AttackAction attackAction = createAttackAction(properties);

        return new EntityAction( moveAction, null, attackAction, null );
    }

    private RepairAction createRepairAction(Entity house) {
        return new RepairAction(house.getId());
    }

    private BuildAction createBuildAction(Entity builder) {
        Vec2Int buildAt = new Vec2Int(builder.getPosition().getX(), builder.getPosition().getY() + 1);
        return new BuildAction(EntityType.HOUSE, buildAt);
    }

    private MoveAction createMovingAction(Vec2Int pos) {
        return new MoveAction(pos, true,true);
    }

    private AttackAction createAttackAction(EntityProperties properties) {
        EntityType[] validAutoAttackTargets = new EntityType[] { EntityType.RESOURCE };
        return new AttackAction(null, new AutoAttack(properties.getSightRange(), validAutoAttackTargets));
    }
}
