package com.ellzone.slotpuzzle2d.prototypes.ashley;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.ellzone.slotpuzzle2d.components.MovementComponent;
import com.ellzone.slotpuzzle2d.components.PositionComponent;
import com.ellzone.slotpuzzle2d.components.VisualComponent;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.systems.MovementSystem;
import com.ellzone.slotpuzzle2d.systems.RenderSystem;

public class RenderSystemTest extends SPPrototype {
    PooledEngine engine;

    public void create() {
        OrthographicCamera camera = new OrthographicCamera(640, 480);
        camera.position.set(320, 240, 0);
        camera.update();

        Texture crateTexture = new Texture("android.jpg");
        Texture coinTexture = new Texture("badlogic.jpg");

        engine = new PooledEngine();
        engine.addSystem(new RenderSystem(camera));
        engine.addSystem(new MovementSystem());

        Entity crate = engine.createEntity();
        crate.add(new PositionComponent(50, 50));
        crate.add(new VisualComponent(new TextureRegion(crateTexture)));

        engine.addEntity(crate);

        TextureRegion coinRegion = new TextureRegion(coinTexture);

        for (int i = 0; i < 100; i++) {
            Entity coin = engine.createEntity();
            coin.add(new PositionComponent(MathUtils.random(640), MathUtils.random(480)));
            coin.add(new MovementComponent(10.0f, 10.0f));
            coin.add(new VisualComponent(coinRegion));
            engine.addEntity(coin);
        }
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        engine.update(Gdx.graphics.getDeltaTime());
    }
}
