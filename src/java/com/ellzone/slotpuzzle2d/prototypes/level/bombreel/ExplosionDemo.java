/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.prototypes.level.bombreel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;

public class ExplosionDemo extends SPPrototype {
    public static final float PIXELS_TO_METERS = 100;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private World world;
    private Box2DDebugRenderer debugRenderer;

    private Array<Coin> coins;
    private BitmapFont defaultFont;
    private SpriteBatch hudBatch;


    @Override
    public void create() {
        super.create();

        world = new World(new Vector2(0, -10f), true);

        debugRenderer = new Box2DDebugRenderer();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.translate(400, 300);

        batch = new SpriteBatch();
        hudBatch = new SpriteBatch();

        coins = new Array<Coin>();
        Coin coin = new Coin(4, 1, world);
        coins.add(coin);

        float[] coors = {
                0, 6,
                0f, 1,
                4, 0.5f,
                8, 1,
                8, 6
        };

        new FloorBody(world, coors);

        createFont();


    }
    public void applyBlastImpulse(Body body, Vector2 blastCenter, Vector2 applyPoint, float blastPower) {
        Vector2 blastDir = applyPoint.cpy().sub(blastCenter);
        float distance = blastDir.len();
        if(distance == 0) return;

        float invDistance = 1f / distance;
        float impulseMag = Math.min(blastPower * invDistance, blastPower * 0.5f); //Not physically correct

        body.applyLinearImpulse(blastDir.nor().scl(impulseMag), applyPoint, true);
    }

    public void explode(final int numRays, float blastRadius, final float blastPower, float posX, float posY) {
        final Vector2 center = new Vector2(posX, posY);
        Vector2 rayDir = new Vector2();
        Vector2 rayEnd = new Vector2();

        for(int i = 0; i < numRays; i++) {
            float angle = (i / (float) numRays) * 360 * MathUtils.degreesToRadians;
            rayDir.set(MathUtils.sin(angle), MathUtils.cos(angle));
            rayEnd.set(center.x + blastRadius * rayDir.x, center.y + blastRadius * rayDir.y);

            RayCastCallback callback = new RayCastCallback() {

                @Override
                public float reportRayFixture(Fixture fixture, Vector2 point,
                                              Vector2 normal, float fraction) {
                    applyBlastImpulse(fixture.getBody(), center, point, blastPower / (float)numRays); /* call our method we created earlier */

                    return 0;
                }
            };

            world.rayCast(callback, center, rayEnd);
        }
    }

    @Override
    public void render() {
        Gdx.gl20.glClearColor(1, 1, 1 , 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        update();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        {
            for (Coin c : coins)
                c.render(batch);
        }
        batch.end();

        //Render HUD
        hudBatch.begin();
        {
            defaultFont.draw(hudBatch, "Press space to bang!", 10, 600 - 10);
            defaultFont.draw(hudBatch, "Click to get rich!", 10, 600 - 20);
            defaultFont.draw(hudBatch, "+/- Zoom!", 10, 600 - 30);
        }
        hudBatch.end();

        Matrix4 debugMatrix = batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS,
                PIXELS_TO_METERS, 0);

        debugRenderer.render(world, debugMatrix);
    }

    private void createFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("explosion/font.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS;


        defaultFont = generator.generateFont(parameter);
        defaultFont.setColor(Color.BLACK);
        generator.dispose();
    }

    private void update() {
        camera.update();
        world.step(1/60f, 6, 2);

        if (Gdx.input.isTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.input.getY();

            Vector3 camCoors = camera.unproject(new Vector3(x, y, 0));
            Coin coin = new Coin(camCoors.x / PIXELS_TO_METERS, camCoors.y / PIXELS_TO_METERS, world);
            coins.add(coin);
        }

        // Boom!!
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            explode(50, 5, 500, 4, 0.1f);
        }

        // Move
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            camera.position.x = camera.position.x - 10;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            camera.position.x = camera.position.x + 10;
        }

        // Zoom
        if (Gdx.input.isKeyJustPressed(Input.Keys.PLUS)) {
            camera.zoom -= 0.1;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            camera.zoom += 0.1;
        }

        for (Coin c : coins) {
            c.update();
        }
    }
}
