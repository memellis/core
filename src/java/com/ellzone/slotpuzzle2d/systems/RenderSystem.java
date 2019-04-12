package com.ellzone.slotpuzzle2d.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ellzone.slotpuzzle2d.components.PositionComponent;
import com.ellzone.slotpuzzle2d.components.VisualComponent;

public class RenderSystem extends EntitySystem {
    private ImmutableArray<Entity> entities;

    private SpriteBatch batch;
    private OrthographicCamera camera;

    private ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VisualComponent> visualMapper = ComponentMapper.getFor(VisualComponent.class);

    public RenderSystem(OrthographicCamera camera) {
        batch = new SpriteBatch();

        this.camera = camera;
    }

    @Override
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PositionComponent.class, VisualComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {

    }

    @Override
    public void update(float deltaTime) {
        camera.update();
        batch.begin();
        batch.setProjectionMatrix(camera.combined);
        drawEntities();
        batch.end();
    }

    private void drawEntities() {
        for (int i = 0; i < entities.size(); ++i) {
            drawEntity(i);
        }
    }

    private void drawEntity(int i) {
        PositionComponent position;
        VisualComponent visual;
        Entity e = entities.get(i);

        position = positionMapper.get(e);
        visual = visualMapper.get(e);

        batch.draw(visual.region, position.x, position.y);
    }
}