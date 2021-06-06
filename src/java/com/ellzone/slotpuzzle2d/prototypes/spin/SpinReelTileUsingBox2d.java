/*
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ellzone.slotpuzzle2d.prototypes.spin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.spin.SpinWheel;
import com.ellzone.slotpuzzle2d.spin.SpinWheelForSlotPuzzle;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReelHelper;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.assets.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class SpinReelTileUsingBox2d extends SPPrototype {

    private static final float WHEEL_DIAMETER = 500F;
    private static final int NUMBER_OF_PEGS = 12;
    private static final float GRAVITY = -9.80f;

    private static final String TAG = SpinWheelWithoutStage.class.getSimpleName();
    private World world;
    private SpinWheelForSlotPuzzle spinWheel;
    private SpriteBatch batch;
    private Image wheelImage;
    private Image needleImage;
    private boolean box2dDebugRender = true;
    private Box2DDebugRenderer renderer;
    private OrthographicCamera camera;
    private AnnotationAssetManager annotationAssetManager;
    private TweenManager tweenManager;
    private AnimatedReelHelper animatedReelHelper;
    private Array<AnimatedReel> animatedReels;
    private OrthographicCamera reelCamera;

    @Override
    public void create() {
        batch = new SpriteBatch();
        world = new World(new Vector2(0, GRAVITY), true);
        renderer = new Box2DDebugRenderer();
        reelCamera = new OrthographicCamera(
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());

        reelCamera.setToOrtho(
                false,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()
        );
        camera = new OrthographicCamera(
                Gdx.graphics.getWidth() / SpinWheel.PPM,
                Gdx.graphics.getHeight() / SpinWheel.PPM);
        camera.setToOrtho(false,
                Gdx.graphics.getWidth() / SpinWheel.PPM,
                Gdx.graphics.getHeight() / SpinWheel.PPM);
        annotationAssetManager = loadAssets();
        tweenManager = initialiseUniversalTweenEngine();
        animatedReelHelper = new AnimatedReelHelper(
                annotationAssetManager, tweenManager, 1);
        animatedReels = animatedReelHelper.getAnimatedReels();
        setUpSpinWheel();
    }

    private AnnotationAssetManager loadAssets() {
        AnnotationAssetManager annotationAssetManager = new AnnotationAssetManager();
        annotationAssetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        annotationAssetManager.load(new AssetsAnnotation());
        annotationAssetManager.finishLoading();
        return annotationAssetManager;
    }

    private TweenManager initialiseUniversalTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        return new TweenManager();
    }

    private void setUpSpinWheel() {
        spinWheel = new SpinWheelForSlotPuzzle(
                WHEEL_DIAMETER,
                Gdx.graphics.getWidth() / 2,
                Gdx.graphics.getHeight() / 2,
                NUMBER_OF_PEGS,
                world);
        spinWheel.setUpSpinWheel();
    }

    public void render() {
        final float delta = Math.min(1 / 30f, Gdx.graphics.getDeltaTime());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update(delta);

        if (!spinWheel.spinningStopped()) {
            spinWheel.updateCoordinates(
                    spinWheel.getWheelBody(), spinWheel.getWheelImage(), 0, 0);
            spinWheel.updateCoordinates(
                    spinWheel.getNeedleBody(), spinWheel.getNeedleImage(), 0, -25F);
        } else
            System.out.println("lucky element is: " + spinWheel.getLuckyWinElement());

        if (Gdx.input.isKeyPressed(Input.Keys.ENTER))
            spinWheel.spin(0.2F);

        if (Gdx.input.isKeyPressed(Input.Keys.S))
            spinWheel.spin(MathUtils.random(5F, 30F));

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        spinWheel.getWheelImage().draw(batch, 1.0f);
        spinWheel.getNeedleImage().draw(batch, 1.0f);
        batch.end();
        if (box2dDebugRender)
            box2dDebugRender();
        batch.setProjectionMatrix(reelCamera.combined);
        batch.begin();
        for (AnimatedReel animatedReel : animatedReels)
            animatedReel.draw(batch);
        batch.end();
    }

    private void box2dDebugRender() {
        renderer.render(world, camera.combined);
    }

    private void update(float delta) {
        world.step(1 / 60f, 8, 2);
        updateAnimatedReels();
    }

    private void updateAnimatedReels() {
        for (AnimatedReel animatedReel: animatedReels)
            updateAnimatedReel(animatedReel);
    }

    private void updateAnimatedReel(AnimatedReel animatedReel) {
        float spinWheelAngle = spinWheel.getWheelBody().getAngle();
        animatedReel.getReel().setSy(spinWheelAngle * animatedReel.getReel().getScrollTextureHeight());
        animatedReel.getReel().processSpinningState();
    }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {
        batch.dispose();
        world.dispose();
        renderer.dispose();
    }
}
