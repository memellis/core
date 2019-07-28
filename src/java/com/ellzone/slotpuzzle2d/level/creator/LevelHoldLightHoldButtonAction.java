/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.level.creator;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.physics.box2d.World;
import com.ellzone.slotpuzzle2d.components.LightButtonComponent;
import com.ellzone.slotpuzzle2d.components.LightVisualComponent;
import com.ellzone.slotpuzzle2d.components.PlayerComponent;
import com.ellzone.slotpuzzle2d.level.creator.LevelHoldLightButtonCallback;
import com.ellzone.slotpuzzle2d.sprites.HoldLightButton;

import box2dLight.RayHandler;


public class LevelHoldLightHoldButtonAction implements LevelHoldLightButtonCallback {
    private PooledEngine engine;
    private World world;
    private RayHandler rayHandler;
    private Entity entity;

    public LevelHoldLightHoldButtonAction(PooledEngine engine, World world, RayHandler rayHandler) {
        this.engine = engine;
        this.world = world;
        this.rayHandler = rayHandler;
    }

    @Override
    public void onEvent(HoldLightButton source) {
        entity = engine.createEntity();
        LightButtonComponent lightButtonComponent = new LightButtonComponent(source);
        entity.add(lightButtonComponent);
        entity.add(new LightVisualComponent(lightButtonComponent.lightButton.getSprite()));
        entity.add(new PlayerComponent());
        engine.addEntity(entity);
    }

    @Override
    public void addComponent(Component component) {
        entity.add(component);
    }
}
