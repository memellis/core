package com.ellzone.slotpuzzle2d.level.creator;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.ellzone.slotpuzzle2d.components.PointLightComponent;

import box2dLight.PointLight;

public class LevelPointLightAction implements LevelPointLightCallback {
    private PooledEngine engine;
    private Entity entity;

    public LevelPointLightAction(PooledEngine  engine) {
        this.engine = engine;
    }

    @Override
    public void onEvent(PointLight source) {
        source.setActive(true);
        entity = engine.createEntity();
        entity.add(new PointLightComponent(source));
        engine.addEntity(entity);
    }

    @Override
    public void addComponent(Component component) {
        entity.add(component);
    }
}
