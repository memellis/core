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


package com.ellzone.slotpuzzle2d.systems.artemisodb;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ellzone.slotpuzzle2d.component.artemis.Position;
import com.ellzone.slotpuzzle2d.component.artemis.Rotation;
import com.ellzone.slotpuzzle2d.component.artemis.TextureRegionRender;

import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;

@Wire
public class RenderTextureRegionSystem extends EntityProcessingSystem {

    protected M<Position> mPosition;
    private SpriteBatch batch;
    private LevelCreatorSystem levelCreatorSystem;

    public RenderTextureRegionSystem() {
        super(Aspect.all(Position.class, TextureRegionRender.class).exclude(Rotation.class));
    }

    @Override
    protected void initialize() {
        super.initialize();
        batch = new SpriteBatch(2000);
    }

    @Override
    protected void begin() {
        batch.begin();
   }

    @Override
    protected void end() {
        batch.end();
    }

    @Override
    protected void process(Entity e) {
        final Position position = mPosition.get(e);
        TextureRegion entityTextureRegion =
                    (TextureRegion) levelCreatorSystem.getEntities().get(e.getId());
         batch.draw(
             entityTextureRegion,
             position.x,
             position.y
        );
    }
}
