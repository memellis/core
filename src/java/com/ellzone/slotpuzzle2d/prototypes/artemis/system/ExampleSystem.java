package com.ellzone.slotpuzzle2d.prototypes.artemis.system;

import com.artemis.E;
import com.artemis.FluidIteratingSystem;
import com.artemis.annotations.All;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.ellzone.slotpuzzle2d.component.Translation;
import com.ellzone.slotpuzzle2d.component.Example;

@All({Translation.class, Example.class})
public class ExampleSystem extends FluidIteratingSystem {

    private OrthographicCamera camera;
    protected SpriteBatch batch;
    private Texture texture;

    @Override
    protected void initialize() {
        super.initialize();

        setUpCamera();

        batch = new SpriteBatch(100);

        texture = new Texture("dancingman.png");
        for (int i = 0; i < 40; i++) {
            E.E()
                    .example()
                    .translationX(MathUtils.random(-texture.getWidth(), Gdx.graphics.getWidth()))
                    .translationY(MathUtils.random(0, Gdx.graphics.getHeight()))
                    .exampleAge(MathUtils.random(10f));
        }
    }

    private void setUpCamera() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
    }

    @Override
    protected void begin() {
        super.begin();

        Gdx.gl.glClearColor(0f,0f,0.2f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
    }

    @Override
    protected void end() {
        super.end();
        batch.end();
    }

    @Override
    protected void process(E e) {
        final Example example = e.getExample();
        batch.draw(texture,
                e.translationX() + MathUtils.sin(example.age)* 50f, e.translationY() + MathUtils.cos(example.age) * 50f);
        example.age += world.delta;
    }

    @Override
    protected void dispose() {
        super.dispose();
        batch.dispose();
        batch = null;
    }
}

