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

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import box2dLight.RayHandler;

public class PhysicsDebugSystem extends IteratingSystem {
    private Box2DDebugRenderer debugRenderer;
    private World world;
    private RayHandler rayHandler;
    private OrthographicCamera camera;

    public PhysicsDebugSystem(World world, RayHandler rayHandler, OrthographicCamera camera){
        super(Family.all().get());
        debugRenderer = new Box2DDebugRenderer();
        this.world = world;
        this.rayHandler = rayHandler;
        this.camera = camera;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        world.step(1/60f, 6, 2);
        rayHandler.setCombinedMatrix(camera);
        rayHandler.updateAndRender();
        debugRenderer.render(world, camera.combined);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
    }
}
