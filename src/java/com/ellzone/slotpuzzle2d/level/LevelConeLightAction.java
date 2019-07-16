package com.ellzone.slotpuzzle2d.level;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.ellzone.slotpuzzle2d.components.ConeLightComponent;

import box2dLight.ConeLight;

public class LevelConeLightAction implements LevelConeLightCallback {
    private PooledEngine engine;
    private Entity entity;

    public LevelConeLightAction(PooledEngine  engine) {
        this.engine = engine;
    }

    @Override
    public void onEvent(ConeLight source) {
        source.setActive(true);
        entity = engine.createEntity();
        entity.add(new ConeLightComponent(source));
        engine.addEntity(entity);
    }

    @Override
    public void addComponent(Component component) {
        entity.add(component);
    }
}