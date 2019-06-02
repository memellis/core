package com.ellzone.slotpuzzle2d.level;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.ellzone.slotpuzzle2d.components.PlayerComponent;
import com.ellzone.slotpuzzle2d.components.ReelHelperComponent;
import com.ellzone.slotpuzzle2d.components.VisualComponent;
import com.ellzone.slotpuzzle2d.sprites.ReelHelper;

public class LevelReelHelperAction implements LevelReelHelperCallback {
    PooledEngine engine;
    Entity entity;
    public LevelReelHelperAction(PooledEngine engine) {
        this.engine = engine;
    }

    @Override
    public void onEvent(ReelHelper source) {
        entity = engine.createEntity();
        entity.add(new ReelHelperComponent(source));
        entity.add(new PlayerComponent());

        entity.add(new VisualComponent());
        engine.addEntity(entity);
    }

    @Override
    public void addComponent(Component component) {
        entity.add(component);
    }
}