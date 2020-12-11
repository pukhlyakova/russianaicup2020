package strategy;

import model.*;

import java.util.*;

public class BuilderUnitEntityActions {

    private Status status;

    private Map<Integer, Entity> builderToHouse;

    private HashSet<Integer> collectedResourceId;

    public BuilderUnitEntityActions(Status status) {
        this.status = status;
        this.builderToHouse = new HashMap<>();
        this.collectedResourceId = new HashSet<>();
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
            if (builderToHouse.containsKey(entity.getId())) {
                result.getEntityActions().put(entity.getId(), repairHouse(builderToHouse.get(entity.getId())));
                continue;
            }
            if (status.getBuilderIds().contains(entity.getId())) {
                result.getEntityActions().put(entity.getId(), builderAction());
                continue;
            }
            result.getEntityActions().put(entity.getId(), collectResources(playerView, entity));
        }
    }

    private void updateBuilderToHouse(List<Entity> entities) {
        builderToHouse.clear();

        Set<Integer> housesWithBuilder = new HashSet<>();

        // builders
        for (Entity entity : entities) {
            if (status.getBuilderIds().contains(entity.getId())) {
                for (Entity house : status.getBrokenHouses()) {
                    if (Utils.distance(entity.getPosition(), house.getPosition()) <= status.distanceForRepair()) {
                        builderToHouse.put(entity.getId(), house);
                        housesWithBuilder.add(house.getId());
                        break;
                    }
                }
            }
        }

        // we have houses that broken because of attack
        if (housesWithBuilder.size() < status.getBrokenHouses().size()) {
            for (Entity house : status.getBrokenHouses()) {
                if (housesWithBuilder.contains(house.getId())) {
                    continue;
                }
                Entity builder = null;
                for (Entity entity : entities) {
                    if (status.getBuilderIds().contains(entity.getId())) {
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
    }

    private EntityAction builderAction() {
        MoveAction moveAction = createMovingAction(status.getHouseTarget());
        BuildAction buildAction = createBuildAction(status.getHouseTarget());
        return new EntityAction( moveAction, buildAction, null, null );
    }

    private EntityAction repairHouse(Entity house) {
        MoveAction moveAction = createMovingAction(house.getPosition());
        RepairAction repairAction = createRepairAction(house);

        return new EntityAction(moveAction, null, null, repairAction );
    }

    private EntityAction collectResources(PlayerView playerView, Entity entity) {
        EntityProperties properties = playerView.getEntityProperties().get(entity.getEntityType());

        Vec2Int zeroPoint = new Vec2Int(0, 0);

        // target info
        Vec2Int target = new Vec2Int(playerView.getMapSize() - 1, playerView.getMapSize() - 1);
        int targetId = -1;

        for (Entity resource : status.getResources()) {
            if (collectedResourceId.contains(resource.getId())) {
                continue;
            }

            if (Utils.distance(zeroPoint, resource.getPosition()) <  Utils.distance(zeroPoint, target)) {
                target = resource.getPosition();
                targetId = resource.getId();
            }
        }
        if (targetId != -1) {
            collectedResourceId.add(targetId);
        }
        MoveAction moveAction = createMovingAction(target);
        AttackAction attackAction = createAttackAction(properties);

        return new EntityAction( moveAction, null, attackAction, null );
    }

    private void addNewBuilder(List<Entity> entities) {
        for (Entity entity : entities) {
            if (!status.getBuilderIds().contains(entity.getId())) {
                status.getBuilderIds().add(entity.getId());
                break;
            }
        }
    }

    private RepairAction createRepairAction(Entity house) {
        return new RepairAction(house.getId());
    }

    private BuildAction createBuildAction(Vec2Int housePos) {
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
