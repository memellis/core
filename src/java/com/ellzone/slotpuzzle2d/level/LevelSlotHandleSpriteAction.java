package com.ellzone.slotpuzzle2d.level;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.ellzone.slotpuzzle2d.components.PositionComponent;
import com.ellzone.slotpuzzle2d.components.SlothandleSpriteComponent;
import com.ellzone.slotpuzzle2d.components.VisualComponent;
import com.ellzone.slotpuzzle2d.sprites.SlotHandleSprite;

public class LevelSlotHandleSpriteAction implements LevelSlotHandleSpriteCallback {
    private PooledEngine engine;
    public LevelSlotHandleSpriteAction(PooledEngine engine) {
        this.engine = engine;
    }

    @Override
    public void onEvent(SlotHandleSprite source) {
        Entity slotHandleBaseEntity = engine.createEntity();
        slotHandleBaseEntity.add(new SlothandleSpriteComponent(source));
        slotHandleBaseEntity.add(new PositionComponent(source.getSlotHandleBaseSprite().getX(), source.getSlotHandleBaseSprite().getY()));
        slotHandleBaseEntity.add(new VisualComponent(source.getSlotHandleBaseSprite()));
        engine.addEntity(slotHandleBaseEntity);
        Entity slotHandleEntity = engine.createEntity();
        slotHandleEntity.add(new PositionComponent(source.getSlotHandleSprite().getX(), source.getSlotHandleSprite().getY()));
        slotHandleEntity.add(new VisualComponent(source.getSlotHandleSprite()));
        engine.addEntity(slotHandleEntity);
    }
}
