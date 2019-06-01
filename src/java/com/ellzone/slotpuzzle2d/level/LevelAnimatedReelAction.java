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

package com.ellzone.slotpuzzle2d.level;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.ellzone.slotpuzzle2d.components.AnimatedReelComponent;
import com.ellzone.slotpuzzle2d.components.PlayerComponent;
import com.ellzone.slotpuzzle2d.components.PositionComponent;
import com.ellzone.slotpuzzle2d.components.ReelTileComponent;
import com.ellzone.slotpuzzle2d.components.TransformComponent;
import com.ellzone.slotpuzzle2d.components.VisualComponent;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;

import java.text.MessageFormat;

public class LevelAnimatedReelAction implements LevelAnimatedReelCallback {
    private PooledEngine engine;
    public LevelAnimatedReelAction(PooledEngine engine) {
        this.engine = engine;
    }

    @Override
    public void onEvent(AnimatedReel source) {
        Entity entity = engine.createEntity();
        AnimatedReelComponent animatedReelComponent = new AnimatedReelComponent(source);
        entity.add(animatedReelComponent);
        ReelTileComponent reelTileComponent = new ReelTileComponent(source.getReel());
        entity.add(reelTileComponent);
        entity.add(new PositionComponent(source.getReel().getX(), source.getReel().getY()));
        TransformComponent transformComponent = new TransformComponent();
        transformComponent.position.x = source.getReel().getX();
        transformComponent.position.y = source.getReel().getY();
        transformComponent.origin.x = source.getReel().getOriginX();
        transformComponent.origin.y = source.getReel().getOriginY();
        transformComponent.rotation = source.getReel().getRotation();
        transformComponent.scale.x = source.getReel().getScaleX();
        transformComponent.scale.y = source.getReel().getScaleY();
        entity.add(transformComponent);
        entity.add(new VisualComponent(source.getReel()));
        entity.add(new PlayerComponent());
        engine.addEntity(entity);
    }
}
