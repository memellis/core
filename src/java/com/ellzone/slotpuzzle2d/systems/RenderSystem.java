package com.ellzone.slotpuzzle2d.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.components.LightVisualComponent;
import com.ellzone.slotpuzzle2d.components.PositionComponent;
import com.ellzone.slotpuzzle2d.components.VisualComponent;

import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.PIXELS_PER_METER;

public class RenderSystem extends EntitySystem {
    private ImmutableArray<Entity> visualEntities;
    private ImmutableArray<Entity> lightVisualEntities;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport lightViewport;

    private ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VisualComponent> visualMapper = ComponentMapper.getFor(VisualComponent.class);
    private ComponentMapper<LightVisualComponent> lightVisualMapper = ComponentMapper.getFor(LightVisualComponent.class);

    public RenderSystem(OrthographicCamera camera) {
        batch = new SpriteBatch();
        this.camera = camera;
        lightViewport = createLightViewPort();
    }

    @Override
    public void addedToEngine(Engine engine) {
        visualEntities = engine.getEntitiesFor(Family.all(PositionComponent.class, VisualComponent.class).get());
        lightVisualEntities = engine.getEntitiesFor(Family.all(PositionComponent.class, LightVisualComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
    }

    private Viewport createLightViewPort() {
        Viewport lightViewport = new FitViewport(SlotPuzzleConstants.V_WIDTH / PIXELS_PER_METER, SlotPuzzleConstants.V_HEIGHT / PIXELS_PER_METER);
        lightViewport.getCamera().position.set(lightViewport.getCamera().position.x + SlotPuzzleConstants.V_WIDTH / PIXELS_PER_METER * 0.5f,
                                               lightViewport.getCamera().position.y + SlotPuzzleConstants.V_HEIGHT / PIXELS_PER_METER * 0.5f,
                                               0);
        lightViewport.getCamera().update();
        lightViewport.update(SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT);

        return lightViewport;
    }

    @Override
    public void update(float deltaTime) {
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

    private void drawVisualEntities() {
        for (int i = 0; i < visualEntities.size(); ++i)
            drawVisualEntity(i);
    }

    private void drawVisualEntity(int i) {
        Entity e = visualEntities.get(i);
        PositionComponent position = positionMapper.get(e);
        VisualComponent visual = visualMapper.get(e);
        batch.draw(visual.region, position.x, position.y);
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
                   position.x,
                   position.y,
                (float) lightVisual.region.getRegionWidth() / PIXELS_PER_METER,
                (float) lightVisual.region.getRegionHeight() / PIXELS_PER_METER);
    }
}