package com.ellzone.slotpuzzle2d.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ellzone.slotpuzzle2d.components.AnimatedReelComponent;
import com.ellzone.slotpuzzle2d.components.PositionComponent;

public class AnimatedReelSystem extends IteratingSystem {
        private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);
        private ComponentMapper<AnimatedReelComponent> animatedReelComponentMapper = ComponentMapper.getFor(AnimatedReelComponent.class);

        public AnimatedReelSystem() {
            super(Family.all(PositionComponent.class, AnimatedReelComponent.class).get());
        }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
    }
}
