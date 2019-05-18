package com.ellzone.slotpuzzle2d.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ellzone.slotpuzzle2d.components.MovementComponent;
import com.ellzone.slotpuzzle2d.components.PositionComponent;
import com.ellzone.slotpuzzle2d.components.ReelTileComponent;

public class ReelTileMovementSystem extends IteratingSystem {
    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<ReelTileComponent> reelTileComponentComponentMapper = ComponentMapper.getFor(ReelTileComponent.class);

    public ReelTileMovementSystem() {
        super(Family.all(PositionComponent.class, ReelTileComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent position = positionComponentComponentMapper.get(entity);
        ReelTileComponent reelTile = reelTileComponentComponentMapper.get(entity);
        position.x = reelTile.reelTile.getX();
        position.y = reelTile.reelTile.getY();
    }
}
