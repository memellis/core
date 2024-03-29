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

import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.All;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ellzone.slotpuzzle2d.component.artemis.Position;

@All({Position.class})
public class DebugPointRenderSystem extends EntityProcessingSystem {

    protected ComponentMapper<Position> mPositionComponent;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private boolean isEnabled = true;

    public DebugPointRenderSystem() {
        this.isEnabled = isEnabled;
    }

    public DebugPointRenderSystem(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    protected void initialize() {
        if (!isEnabled)
            return;
        super.initialize();
        batch = new SpriteBatch(2000);
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    protected void begin() {
        if (!isEnabled)
            return;
        batch.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    }

    @Override
    protected void end() {
        if (!isEnabled)
            return;
        shapeRenderer.end();
        batch.end();
    }

    @Override
    protected void process(Entity e) {
        if (!isEnabled)
            return;
        final Position position = mPositionComponent.get(e);
        shapeRenderer.circle(position.x, position.y, 5);
    }
}