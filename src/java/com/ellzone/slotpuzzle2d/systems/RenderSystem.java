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
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.components.LightVisualComponent;
import com.ellzone.slotpuzzle2d.components.PositionComponent;
import com.ellzone.slotpuzzle2d.components.TransformComponent;
import com.ellzone.slotpuzzle2d.components.VisualComponent;

import box2dLight.RayHandler;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.PIXELS_PER_METER;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_HEIGHT;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_WIDTH;

public class RenderSystem extends EntitySystem {
    private ImmutableArray<Entity> visualEntities;
    private ImmutableArray<Entity> lightVisualEntities;

    private SpriteBatch batch;
    private World world;
    private RayHandler rayHandler;
    private OrthographicCamera camera;
    private Viewport lightViewport;

    private ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VisualComponent> visualMapper = ComponentMapper.getFor(VisualComponent.class);
    private ComponentMapper<LightVisualComponent> lightVisualMapper = ComponentMapper.getFor(LightVisualComponent.class);
    private ComponentMapper<TransformComponent> transformMapper = ComponentMapper.getFor(TransformComponent.class);

    private Box2DDebugRenderer debugRenderer;
    private boolean printStartMessage = true;
    private boolean startRendering = false;

    public RenderSystem(OrthographicCamera camera) {
        batch = new SpriteBatch();
        this.camera = camera;
        lightViewport = createLightViewPort();
    }

    public RenderSystem(World world, RayHandler rayHander, OrthographicCamera camera) {
        batch = new SpriteBatch();
        this.world = world;
        this.rayHandler = rayHander;
        this.camera = camera;
        debugRenderer = new Box2DDebugRenderer();
        lightViewport = createLightViewPort();
    }

    @Override
    public void addedToEngine(Engine engine) {
        visualEntities = engine.getEntitiesFor(
                Family.all(VisualComponent.class,
                           TransformComponent.class,
                           PositionComponent.class).get());
        lightVisualEntities = engine.getEntitiesFor(
                Family.all(PositionComponent.class,
                           LightVisualComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
    }

    public void setStartRendering(boolean startRendering) {
        this.startRendering = startRendering;
    }

    private Viewport createLightViewPort() {
        lightViewport = new FitViewport((float) VIRTUAL_WIDTH / PIXELS_PER_METER,
                                        (float) VIRTUAL_HEIGHT / PIXELS_PER_METER);
        lightViewport.getCamera().position.set(
                VIRTUAL_WIDTH / PIXELS_PER_METER * 0.5f,
                VIRTUAL_HEIGHT / PIXELS_PER_METER * 0.5f,
                0);
        lightViewport.getCamera().update();
        lightViewport.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        return lightViewport;
    }

    public Viewport getLightViewport() {
        return lightViewport;
    }

    public SpriteBatch getSpriteBatch() { return batch; }

    @Override
    public void update(float deltaTime) {
        if (startRendering) {
            if (printStartMessage) {
                printStartMessage = false;
                System.out.println("RenderSystem starting to update...");
            }
            camera.update();
            batch.begin();
            batch.setProjectionMatrix(camera.combined);
            drawVisualEntities();
            batch.end();
            batch.setProjectionMatrix(lightViewport.getCamera().combined);
            batch.begin();
            drawLightVisualEntities();
            batch.end();
        }
    }

    private void drawVisualEntities() {
        for (int i = 0; i < visualEntities.size(); ++i)
            drawVisualEntity(i);
    }

    private void drawVisualEntity(int i) {
        Entity e = visualEntities.get(i);
        PositionComponent position = positionMapper.get(e);
        VisualComponent visual = visualMapper.get(e);
        TransformComponent transform = transformMapper.get(e);

        if ((visual.region != null) & (!transform.isHidden))
            batch.draw(visual.region,
                       position.x,
                       position.y,
                       transform.origin.x,
                       transform.origin.y,
                       visual.region.getRegionWidth(),
                       visual.region.getRegionHeight(),
                       transform.scale.x,
                       transform.scale.y,
                       transform.rotation);
    }

    private void drawLightVisualEntities() {
        for (int i = 0; i < lightVisualEntities.size(); ++i)
            drawLightVisualEntity(i);
    }

    private void drawLightVisualEntity(int i) {
        Entity lightVisualEntity = lightVisualEntities.get(i);
        PositionComponent position = positionMapper.get(lightVisualEntity);
        LightVisualComponent lightVisual = lightVisualMapper.get(lightVisualEntity);
        batch.draw(lightVisual.region,
                (float) position.x / PIXELS_PER_METER,
                (float) position.y / PIXELS_PER_METER,
                (float) lightVisual.region.getRegionWidth() / PIXELS_PER_METER,
                (float) lightVisual.region.getRegionHeight() / PIXELS_PER_METER);
    }
}