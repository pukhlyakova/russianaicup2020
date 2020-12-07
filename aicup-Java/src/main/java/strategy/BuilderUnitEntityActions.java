package strategy;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BuilderUnitEntityActions {

    private Statistics statistics; // update Statistics
    private Status status; // list of builders, who NOT extract resources
    private List<Entity> brokenHouse; // list of buildings that should be repaired

    public BuilderUnitEntityActions(List<Entity> brokenHouse, Statistics statistics, Status status) {
        this.brokenHouse = brokenHouse;
        this.statistics = statistics;
        this.status = status;
    }

    public void addEntityActions(PlayerView playerView, Set<Entity> entities, Action result) {
        builderAction(entities, result, playerView);

        for (Entity entity : entities) {
            if (status.getBuilderId() != null && entity.getId() == status.getBuilderId()) {
                continue;
            }
            EntityAction entityAction = collectResources(playerView, entity);
            result.getEntityActions().put(entity.getId(), entityAction);
        }
    }

    private Entity findBuilder(Set<Entity> entities) {
        for (Entity entity : entities) {
            if (status.getBuilderId() != null && entity.getId() == status.getBuilderId()) {
                return entity;
            }
        }
        return null;
    }

    private void builderAction(Set<Entity> entities, Action result, PlayerView playerView) {
        if (!status.isBuilding() && brokenHouse.isEmpty() && !statistics.shouldBuildHouse()) {
            status.setBuilderId(null);
            status.setBuilding(false);
            return;
        }

        Entity builder = findBuilder(entities);

        if (builder == null) {
            builder = new ArrayList<>(entities).get(0);
            status.setBuilderId(builder.getId());
        }

        MoveAction moveAction = createMovingAction(10, 10);

        if (!brokenHouse.isEmpty()) {
            RepairAction repairAction = createRepairAction();
            EntityAction action = new EntityAction( moveAction, null, null, repairAction );
            result.getEntityActions().put(builder.getId(), action);
        } else if (status.isBuilding() || statistics.shouldBuildHouse()) {
            status.setBuilding(true);
            EntityProperties properties = playerView.getEntityProperties().get(builder.getEntityType());
            BuildAction buildAction = createBuildAction(builder, properties);
            EntityAction action = new EntityAction( moveAction, buildAction, null, null );
            result.getEntityActions().put(builder.getId(), action);
        }
    }

    private EntityAction collectResources(PlayerView playerView, Entity entity) {
        EntityProperties properties = playerView.getEntityProperties().get(entity.getEntityType());

        MoveAction moveAction = createMovingAction(playerView.getMapSize() - 1, playerView.getMapSize() - 1);
        AttackAction attackAction = createAttackAction(properties);

        return new EntityAction( moveAction, null, attackAction, null );
    }

    private RepairAction createRepairAction() {
        if (brokenHouse.isEmpty()) {
            return null;
        }
        return new RepairAction(brokenHouse.get(0).getId());
    }

    private BuildAction createBuildAction(Entity entity, EntityProperties properties) {
        return new BuildAction(EntityType.HOUSE, new Vec2Int(entity.getPosition().getX() + properties.getSize(),
                entity.getPosition().getY() + properties.getSize() - 1));
    }

    private MoveAction createMovingAction(int x, int y) {
        return new MoveAction(new Vec2Int(x, y), true,true);
    }

    private AttackAction createAttackAction(EntityProperties properties) {
        EntityType[] validAutoAttackTargets = new EntityType[] { EntityType.RESOURCE };
        return new AttackAction(null, new AutoAttack(properties.getSightRange(), validAutoAttackTargets));
    }
}
