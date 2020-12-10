package strategy;

import model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuilderUnitEntityActions {

    private Status status;

    private Map<Integer, Entity> builderToHouse;

    public BuilderUnitEntityActions(Status status) {
        this.status = status;
        this.builderToHouse = new HashMap<>();
    }

    // priority: attack, build, repair and move
    public void addEntityActions(PlayerView playerView, List<Entity> entities, Action result) {
        // update builderToHouse
        updateBuilderToHouse(entities);

        // add new builder if you need
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
        Entity house = builderToHouse.getOrDefault(builder.getId(), null);

        if (house != null) {
            MoveAction moveAction = createMovingAction(house.getPosition());
            RepairAction repairAction = createRepairAction(house);

            EntityAction action = new EntityAction( moveAction, null, null, repairAction );
            result.getEntityActions().put(builder.getId(), action);
        } else if (status.shouldBuildHouse()) {
            MoveAction moveAction = createMovingAction(status.getHouseTarget());
            BuildAction buildAction = createBuildAction(builder, status.getHouseTarget());

            EntityAction action = new EntityAction( moveAction, buildAction, null, null );
            result.getEntityActions().put(builder.getId(), action);
        }
    }

    private void updateBuilderToHouse(List<Entity> entities) {
        builderToHouse.clear();

        for (Entity house : status.getBrokenHouses()) {
            Entity builder = null;

            for (Entity entity : entities) {
                if (!status.getBuilderIds().contains(entity.getId())) {
                    continue;
                }
                if (builder == null || Utils.distance(house.getPosition(), entity.getPosition()) <
                                       Utils.distance(house.getPosition(), builder.getPosition())) {
                    builder = entity;
                }
            }
            if (builder != null) {
                builderToHouse.put(builder.getId(), house);
            }
        }
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

    private BuildAction createBuildAction(Entity builder, Vec2Int housePos) {
        Vec2Int buildAt = new Vec2Int(housePos.getX(), housePos.getY() + 1);
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
