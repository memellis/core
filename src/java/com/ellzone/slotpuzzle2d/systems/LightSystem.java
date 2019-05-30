package com.ellzone.slotpuzzle2d.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.components.LightVisualComponent;
import com.ellzone.slotpuzzle2d.components.PointLightComponent;
import com.ellzone.slotpuzzle2d.components.PositionComponent;
import com.ellzone.slotpuzzle2d.utils.Random;

import java.text.MessageFormat;

import box2dLight.RayHandler;

import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.PIXELS_PER_METER;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_HEIGHT;

public class LightSystem extends IteratingSystem {
    public static final float WORLD_TIME_STEP = 1 / 60f;
    public static final int WORLD_VELOCITY_ITERATIONS = 6;
    public static final int WORLD_POSITION_ITERATIONS = 2;

    private ComponentMapper<PointLightComponent> pointLightMapper = ComponentMapper.getFor(PointLightComponent.class);
    private ComponentMapper<PositionComponent> positionMapper = ComponentMapper.getFor(PositionComponent.class);

    private float timeCount = 0.0f;
    private Array<Entity> lightsQueue;
    private World world;
    private RayHandler rayHandler;
    private OrthographicCamera camera;
    private Random random = Random.getInstance();

    public LightSystem(World world, RayHandler rayHandler, OrthographicCamera camera) {
        super(Family.all(PointLightComponent.class, PositionComponent.class).get());
        this.world = world;
        this.rayHandler = rayHandler;
        this.camera = camera;
        lightsQueue = new Array<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (Entity lightEntity : lightsQueue) {
            PointLightComponent pointLightComponent = pointLightMapper.get(lightEntity);
            PositionComponent positionComponent = positionMapper.get(lightEntity);
            pointLightComponent.pointLight.setPosition(
                    (float) positionComponent.x / PIXELS_PER_METER,
                    (float) (positionComponent.y) / PIXELS_PER_METER);
        }
        lightsQueue.clear();
        world.step(WORLD_TIME_STEP, WORLD_VELOCITY_ITERATIONS, WORLD_POSITION_ITERATIONS);
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();
     }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        lightsQueue.add(entity);
    }
}
