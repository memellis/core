package com.ellzone.slotpuzzle2d.level;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.Color;
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
        source.setColor(Color.RED);
        source.setDistance(0.4f);
        entity = engine.createEntity();
        entity.add(new PointLightComponent(source));
        engine.addEntity(entity);
    }

    @Override
    public void addComponent(Component component) {
        entity.add(component);
    }
}
