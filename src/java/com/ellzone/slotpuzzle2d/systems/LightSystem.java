package com.ellzone.slotpuzzle2d.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.components.LightButtonComponent;
import com.ellzone.slotpuzzle2d.components.LightVisualComponent;
import com.ellzone.slotpuzzle2d.utils.Random;

import box2dLight.RayHandler;

public class LightSystem extends IteratingSystem {
    private ComponentMapper<LightVisualComponent> lightVisualMapper = ComponentMapper.getFor(LightVisualComponent.class);
    private float timeCount = 0.0f;
    private Array<Entity> lightsQueue;
    private World world;
    private RayHandler rayHandler;
    private OrthographicCamera camera;
    private Random random = Random.getInstance();

    public LightSystem(World world, RayHandler rayHandler, OrthographicCamera camera) {
        super(Family.all(LightButtonComponent.class).get());
        this.world = world;
        this.rayHandler = rayHandler;
        this.camera = camera;
        lightsQueue = new Array<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        timeCount += deltaTime;
        if(timeCount >= 1.0f) {
            processLights(lightsQueue);
            timeCount = 0.0f;
        }
        lightsQueue.clear();
        world.step(1/60f, 6, 2);
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();
     }

    private void processLights(Array<Entity> lightsQueue) {
        for (Entity lightEntity : lightsQueue)
            processLight(lightEntity);
    }

    private void processLight(Entity lightEntity) {
        LightButtonComponent lightButtonComponent = lightEntity.getComponent(LightButtonComponent.class);
        lightButtonComponent.lightButton.getLight().setActive(random.nextBoolean());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        lightsQueue.add(entity);
    }
}
