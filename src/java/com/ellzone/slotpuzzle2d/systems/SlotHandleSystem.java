package com.ellzone.slotpuzzle2d.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ellzone.slotpuzzle2d.components.SlothandleSpriteComponent;
import com.ellzone.slotpuzzle2d.components.TransformComponent;

public class SlotHandleSystem extends IteratingSystem {
    public SlotHandleSystem() {
        super(Family.all(TransformComponent.class,
                         SlothandleSpriteComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SlothandleSpriteComponent slothandleSpriteComponent = entity.getComponent(SlothandleSpriteComponent.class);
        TransformComponent transformComponent = entity.getComponent(TransformComponent.class);
        transformComponent.rotation = slothandleSpriteComponent.slotHandleSprite.getSlotHandleSprite().getRotation();
    }
}
