package strategy;

import model.*;

public class FighterEntityActions extends BaseEntityActions {

    @Override
    protected MoveAction createMovingAction(PlayerView playerView, Entity entity, EntityProperties properties) {
        return new MoveAction(
                new Vec2Int(playerView.getMapSize() - 1, playerView.getMapSize() - 1),
                true,
                true);
    }

    @Override
    protected AttackAction createAttackAction(PlayerView playerView, Entity entity, EntityProperties properties) {
        return super.createAttackAction(playerView, entity, properties);
    }

}
