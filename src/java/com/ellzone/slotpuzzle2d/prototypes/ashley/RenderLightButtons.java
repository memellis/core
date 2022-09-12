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

package com.ellzone.slotpuzzle2d.prototypes.ashley;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.components.LightButtonComponent;
import com.ellzone.slotpuzzle2d.components.LightVisualComponent;
import com.ellzone.slotpuzzle2d.components.PositionComponent;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.sprites.lights.HoldLightButton;
import com.ellzone.slotpuzzle2d.systems.LightSystem;
import com.ellzone.slotpuzzle2d.systems.RenderSystem;
import box2dLight.RayHandler;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.EARTH_GRAVITY;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_WIDTH;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_HEIGHT;

public class RenderLightButtons extends SPPrototype {
    private PooledEngine engine;
    private World world;
    private RayHandler rayHandler;

    public void create() {
        OrthographicCamera camera = setupCamera();
        world = createWorld();
        rayHandler = createRayHandler(world);
        setupEngine(rayHandler, camera);
        createLightButtons();
    }


    private OrthographicCamera setupCamera() {
        OrthographicCamera camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        camera.update();
        return camera;
    }

    private World createWorld() {
        return new World(new Vector2(0, EARTH_GRAVITY), true);
    }

    private RayHandler createRayHandler(World world) {
        return new RayHandler(world);
    }

    private void setupEngine(RayHandler rayHandler, OrthographicCamera camera) {
        engine = new PooledEngine();
        RenderSystem renderSystem = new RenderSystem(world, rayHandler, camera);
        engine.addSystem(renderSystem);
        engine.addSystem(new LightSystem(world, rayHandler, (OrthographicCamera) renderSystem.getLightViewport().getCamera()));
    }

    private void createLightButtons() {
        for (int i = 0; i < 3; i++) {
            createLightButton(i);
        }
    }

    private void createLightButton(int i) {
        Entity entity = engine.createEntity();
        PositionComponent positionComponent =
                new PositionComponent(i * 40 + SlotPuzzleConstants.VIRTUAL_WIDTH / 2 - (3 * 40 ) / 2,
                                      SlotPuzzleConstants.VIRTUAL_HEIGHT / 4);
        LightButtonComponent lightButtonComponent =
                new LightButtonComponent(new HoldLightButton(
                        world,
                        rayHandler,
                        i * 40 + SlotPuzzleConstants.VIRTUAL_WIDTH / 2 - (3 * 40) / 2,
                        SlotPuzzleConstants.VIRTUAL_HEIGHT / 4, 40, 40));
        lightButtonComponent.lightButton.getSprite().setSize(40, 40);
        lightButtonComponent.lightButton.getLight().setActive(true);
        entity = addVisualComponent(entity, lightButtonComponent.lightButton.getSprite());
        entity.add(lightButtonComponent);
        entity.add(positionComponent);
        engine.addEntity(entity);
    }

    private Entity addVisualComponent(Entity entity, TextureRegion textureRegion) {
        return entity.add(new LightVisualComponent(textureRegion));
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float deltaTime = Gdx.graphics.getDeltaTime();
        engine.update(deltaTime);
    }
}
