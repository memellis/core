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

import com.artemis.Entity;
import com.artemis.annotations.All;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ellzone.slotpuzzle2d.component.artemis.Color;
import com.ellzone.slotpuzzle2d.component.artemis.ImageRender;
import com.ellzone.slotpuzzle2d.component.artemis.Position;
import com.ellzone.slotpuzzle2d.component.artemis.Vector2Shape;

import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.camera.CameraSystem;


@All({Vector2Shape.class, Color.class})
public class RenderVectorsSystem extends EntityProcessingSystem {

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    protected M<Vector2Shape> mVector2Shape;
    protected M<Color> mColor;
    private CameraSystem cameraSystem;

    @Override
    protected void initialize() {
        super.initialize();
        batch = new SpriteBatch(2000);
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    protected void begin() {
        batch.setProjectionMatrix(cameraSystem.camera.combined);
        batch.begin();
    }

    @Override
    protected void end() {
        batch.end();
    }


    @Override
    protected void process(Entity e) {
        final Vector2Shape vector2Shape = mVector2Shape.get(e);
        final Color color = mColor.get(e);
        if (vector2Shape.getVectors().size < 2)
            return;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new com.badlogic.gdx.graphics.Color(
                color.red, color.green, color.blue, color.alpha));

        for (int i = 0; i < vector2Shape.getVectors().size - 1; i++) {
            shapeRenderer.rectLine(
                    vector2Shape.getVectors().get(i),
                    vector2Shape.getVectors().get(i+1),
                    2);
        }
        shapeRenderer.end();
    }
}
