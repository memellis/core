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

package com.ellzone.slotpuzzle2d.prototypes.artemis.system;

import com.artemis.E;
import com.artemis.FluidIteratingSystem;
import com.artemis.annotations.All;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReelHelper;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.assets.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;
import net.mostlyoriginal.api.component.basic.Pos;

import static net.mostlyoriginal.api.operation.JamOperationFactory.moveBetween;
import static net.mostlyoriginal.api.operation.OperationFactory.*;

@All({Pos.class})
public class SlotPuzzleUsingTweenSystem extends FluidIteratingSystem {

    private OrthographicCamera camera;
    protected SpriteBatch batch;
    private AnimatedReelHelper animatedReelHelper;
    private Array<ReelTile> reels;
    private final TweenManager tweenManager = new TweenManager();
    private int index = 0;

    @Override
    protected void initialize() {
        super.initialize();
        initialiseUniversalTweenEngine();
        AnnotationAssetManager annotationAssetManager = getAnnotationAssetManager();
        animatedReelHelper = new AnimatedReelHelper(
                annotationAssetManager,
                tweenManager,
                16
        );
        reels = animatedReelHelper.getReelTiles();
        setAnimatedReelsSpinning();

        setUpCamera();

        batch = new SpriteBatch(100);

        for (int i = 0; i < reels.size; i++) {
            E e = E.E()
                    .pos(
                            MathUtils.random(-reels.get(i).getWidth(), Gdx.graphics.getWidth()),
                            MathUtils.random(0, Gdx.graphics.getHeight())
                    );

            e
                    .script(
                            sequence(
                                    delay(1.0f),
                                    moveBetween(
                                            e.posX(),
                                            e.posY(),
                                            e.posX() + 100,
                                            e.posY() + 100,
                                            5.0f,
                                            Interpolation.bounce)
                            )
                    );
        }
    }


    private AnnotationAssetManager getAnnotationAssetManager() {
        AnnotationAssetManager annotationAssetManager = new AnnotationAssetManager();
        annotationAssetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        annotationAssetManager.load(new AssetsAnnotation());
        annotationAssetManager.finishLoading();
        return annotationAssetManager;
    }

    protected void initialiseUniversalTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
    }

    private void setAnimatedReelsSpinning() {
        for (AnimatedReel animatedReel :
                new Array.ArrayIterator<>(animatedReelHelper.getAnimatedReels())) {
            animatedReel.setupSpinning();
            animatedReel.getReel().setSpinning(true);
        }
    }

    private void setUpCamera() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
    }

    @Override
    protected void begin() {
        tweenManager.update(world.delta);
        super.begin();

        Gdx.gl.glClearColor(0f, 0f, 0.2f, 1f);
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
        if (index < reels.size) {
            animatedReelHelper.getAnimatedReels().get(index).update(world.delta);
            batch.draw(reels.get(index),
                    e.posX(),
                    e.posY());
        } else
            index = 0;
        index++;
    }

    @Override
    protected void dispose() {
        super.dispose();
        batch.dispose();
        batch = null;
    }
}