package com.ellzone.slotpuzzle2d.level.creator;

import com.badlogic.ashley.core.Component;

import box2dLight.PointLight;

public interface LevelPointLightCallback {
    public void onEvent(PointLight source);
    public void addComponent(Component component);
}
