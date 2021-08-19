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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.ellzone.slotpuzzle2d.component.artemis.ImageRender;
import com.ellzone.slotpuzzle2d.component.artemis.Position;
import com.ellzone.slotpuzzle2d.spin.SpinWheel;

@All({Position.class, ImageRender.class})
public class RenderImagesSystem extends EntityProcessingSystem {
    protected ComponentMapper<Position> positionComponentMapper;
    protected ComponentMapper<ImageRender> imageRenderComponentMapper;
    private SpriteBatch batch;
    private LevelCreatorSystem levelCreatorSystem;
    private OrthographicCamera camera;

    @Override
    protected void initialize() {
        super.initialize();
        batch = new SpriteBatch(2000);
        camera = new OrthographicCamera(
                Gdx.graphics.getWidth() / SpinWheel.PPM,
                Gdx.graphics.getHeight() / SpinWheel.PPM);
        camera.setToOrtho(false,
                Gdx.graphics.getWidth() / SpinWheel.PPM,
                Gdx.graphics.getHeight() / SpinWheel.PPM);
        batch.setProjectionMatrix(camera.combined);
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
        Image entityImage = (Image) levelCreatorSystem.getEntities().get(e.getId());
        entityImage.draw(batch,1.0f);
    }
}
