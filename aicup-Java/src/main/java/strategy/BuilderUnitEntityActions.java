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

        // builder
        int builderId = findBuilder(entities);

        for (Entity entity : entities) {
            if (builderToHouse.containsKey(entity.getId())) {
                result.getEntityActions().put(entity.getId(), repairHouse(builderToHouse.get(entity.getId())));
                continue;
            }
            if (entity.getId() == builderId) {
                result.getEntityActions().put(entity.getId(), builderAction());
                continue;
            }
            result.getEntityActions().put(entity.getId(), collectResources(playerView, entity));
        }
    }

    private void updateBuilderToHouse(List<Entity> entities) {
        builderToHouse.clear();

        for (Entity house : status.getBrokenHouses()) {
            Entity builder = null;
            for (Entity entity : entities) {
                if (builderToHouse.containsKey(entity.getId())) {
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

    private int findBuilder(List<Entity> entities) {
        Entity builder = null;
        Vec2Int target = status.getHouseTarget();

        if (needNewHouse()) {
            for (Entity entity : entities) {
                if (builderToHouse.containsKey(entity.getId())) {
                    continue;
                }
                if (builder == null || Utils.distance(entity.getPosition(), target) <
                        Utils.distance(builder.getPosition(), target)) {
                    builder = entity;
                }
            }
        }
        return builder == null ? -1 : builder.getId();
    }

    private boolean needNewHouse() {
        int count = 0;
        for (Entity house : status.getBrokenHouses()) {
            if (!house.isActive()) {
                ++count;
            }
        }

        //to many broken houses
        if (count >= 3) {
            return false;
        }

        return status.getPopulationProvide() - status.getPopulationUse() <= 1 ||
               status.getResource() > 100;
    }

    private EntityAction builderAction() {
        Vec2Int pos = status.getHouseTarget();
        MoveAction moveAction = createMovingAction(new Vec2Int(pos.getX() + 1, pos.getY()));
        BuildAction buildAction = createBuildAction(new Vec2Int(pos.getX() + 1, pos.getY() + 1));
        return new EntityAction( moveAction, buildAction, null, null );
    }

    private EntityAction repairHouse(Entity house) {
        MoveAction moveAction = createMovingAction(house.getPosition());
        RepairAction repairAction = createRepairAction(house);

        return new EntityAction(moveAction, null, null, repairAction );
    }

    private EntityAction collectResources(PlayerView playerView, Entity entity) {
        EntityProperties properties = playerView.getEntityProperties().get(entity.getEntityType());

        // target info
        Vec2Int target = new Vec2Int(playerView.getMapSize() - 1, playerView.getMapSize() - 1);
        int targetId = -1;

        for (Entity resource : status.getResources()) {
            if (collectedResourceId.contains(resource.getId())) {
                continue;
            }

            if (Utils.distance(entity.getPosition(), resource.getPosition()) <
                Utils.distance(entity.getPosition(), target)) {
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

    private RepairAction createRepairAction(Entity house) {
        return new RepairAction(house.getId());
    }

    private BuildAction createBuildAction(Vec2Int housePos) {
        Vec2Int buildAt = new Vec2Int(housePos.getX(), housePos.getY());
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
