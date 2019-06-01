package com.ellzone.slotpuzzle2d.level;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.ellzone.slotpuzzle2d.components.PlayerComponent;
import com.ellzone.slotpuzzle2d.components.PositionComponent;
import com.ellzone.slotpuzzle2d.components.SlothandleSpriteComponent;
import com.ellzone.slotpuzzle2d.components.TransformComponent;
import com.ellzone.slotpuzzle2d.components.VisualComponent;
import com.ellzone.slotpuzzle2d.sprites.SlotHandleSprite;

import java.text.MessageFormat;

public class LevelSlotHandleSpriteAction implements LevelSlotHandleSpriteCallback {
    private PooledEngine engine;
    public LevelSlotHandleSpriteAction(PooledEngine engine) {
        this.engine = engine;
    }

    @Override
    public void onEvent(SlotHandleSprite source) {
        createSlotHandleBaseEntity(source);
        createSlotHandleEntity(source);
    }

    private void createSlotHandleEntity(SlotHandleSprite source) {
        Entity slotHandleEntity = engine.createEntity();
        slotHandleEntity.add(new SlothandleSpriteComponent(source));
        slotHandleEntity.add(new PositionComponent(source.getSlotHandleSprite().getX(), source.getSlotHandleSprite().getY()));
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.position.x = source.getSlotHandleSprite().getX();
        transformComponent.position.y = source.getSlotHandleSprite().getY();
        transformComponent.origin.x = source.getSlotHandleSprite().getOriginX();
        transformComponent.origin.y = source.getSlotHandleSprite().getOriginY();
        transformComponent.rotation = source.getSlotHandleSprite().getRotation();
        transformComponent.scale.x = source.getSlotHandleSprite().getScaleX();
        transformComponent.scale.y = source.getSlotHandleSprite().getScaleY();
        slotHandleEntity.add(transformComponent);
        slotHandleEntity.add(new VisualComponent(source.getSlotHandleSprite()));
        slotHandleEntity.add(new PlayerComponent());
        engine.addEntity(slotHandleEntity);
    }

    private void createSlotHandleBaseEntity(SlotHandleSprite source) {
        Entity slotHandleBaseEntity = engine.createEntity();
        slotHandleBaseEntity.add(new PositionComponent(source.getSlotHandleBaseSprite().getX(), source.getSlotHandleBaseSprite().getY()));
        TransformComponent transformComponentBase = new TransformComponent();
        transformComponentBase.position.x = source.getSlotHandleSprite().getX();
        transformComponentBase.position.y = source.getSlotHandleSprite().getY();
        transformComponentBase.origin.x = source.getSlotHandleSprite().getOriginX();
        transformComponentBase.origin.y = source.getSlotHandleSprite().getOriginY();
        transformComponentBase.rotation = source.getSlotHandleBaseSprite().getRotation();
        transformComponentBase.scale.x = source.getSlotHandleBaseSprite().getScaleX();
        transformComponentBase.scale.y = source.getSlotHandleBaseSprite().getScaleY();
        slotHandleBaseEntity.add(transformComponentBase);
        slotHandleBaseEntity.add(new VisualComponent(source.getSlotHandleBaseSprite()));
        slotHandleBaseEntity.add(new PlayerComponent());
        engine.addEntity(slotHandleBaseEntity);
    }
}
