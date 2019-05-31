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
import com.ellzone.slotpuzzle2d.components.PositionComponent;
import com.ellzone.slotpuzzle2d.components.ReelTileComponent;
import com.ellzone.slotpuzzle2d.components.VisualComponent;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;

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
        entity.add(new VisualComponent(source.getReel()));
        engine.addEntity(entity);
    }
}
