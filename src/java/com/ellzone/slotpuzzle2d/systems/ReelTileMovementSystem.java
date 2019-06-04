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

package com.ellzone.slotpuzzle2d.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.ellzone.slotpuzzle2d.components.PositionComponent;
import com.ellzone.slotpuzzle2d.components.ReelTileComponent;
import com.ellzone.slotpuzzle2d.components.TransformComponent;

import java.text.MessageFormat;

public class ReelTileMovementSystem extends IteratingSystem {
    private ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<TransformComponent> transformComponentComponentMapper = ComponentMapper.getFor(TransformComponent.class);
    private ComponentMapper<ReelTileComponent> reelTileComponentComponentMapper = ComponentMapper.getFor(ReelTileComponent.class);

    public ReelTileMovementSystem() {
        super(Family.all(ReelTileComponent.class, TransformComponent.class, PositionComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ReelTileComponent reelTile = reelTileComponentComponentMapper.get(entity);
        PositionComponent position = positionComponentComponentMapper.get(entity);
        TransformComponent transformComponent = transformComponentComponentMapper.get(entity);
        position.x = reelTile.reelTile.getX();
        position.y = reelTile.reelTile.getY();
        transformComponent.rotation = reelTile.reelTile.getRotation();
        transformComponent.scale.x = reelTile.reelTile.getScaleX();
        transformComponent.scale.y = reelTile.reelTile.getScaleY();
    }
}
