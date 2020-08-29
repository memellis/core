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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.component.Example;
import com.ellzone.slotpuzzle2d.component.Translation;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReelHelper;
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.Random;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

@All({Translation.class, Example.class})
public class SlotPuzzleSystem extends FluidIteratingSystem {

    private  AnnotationAssetManager annotationAssetManager;
    private OrthographicCamera camera;
    protected SpriteBatch batch;
    private Texture texture;
    private ReelSprites reelSprites;
    Random random = Random.getInstance();
    private AnimatedReelHelper animatedReelHelper;
    private Array<ReelTile> reels;
    private final TweenManager tweenManager = new TweenManager();
    private int index = 0;

    @Override
    protected void initialize() {
        super.initialize();
        initialiseUniversalTweenEngine();
        annotationAssetManager = getAnnotationAssetManager();
        animatedReelHelper = new AnimatedReelHelper(
                annotationAssetManager,
                tweenManager,
                16
        );
        reels = animatedReelHelper.getReelTiles();
        setAimatedReelsSpinning();

        setUpCamera();

        batch = new SpriteBatch(100);

        reelSprites = new ReelSprites(annotationAssetManager);

        for (int i = 0; i < reels.size; i++) {
            E.E()
                    .example()
                    .translationX(MathUtils.random(-reels.get(i).getWidth(), Gdx.graphics.getWidth()))
                    .translationY(MathUtils.random(0, Gdx.graphics.getHeight()))
                    .exampleAge(MathUtils.random(10f));
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

    private void setAimatedReelsSpinning() {
        for (AnimatedReel animatedReel : animatedReelHelper.getAnimatedReels()) {
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
        if (index<reels.size) {
            animatedReelHelper.getAnimatedReels().get(index).update(world.delta);
            batch.draw(reels.get(index),
                    e.translationX() + MathUtils.sin(example.age) * 50f,
                    e.translationY() + MathUtils.cos(example.age) * 50f);
        }
        else
            index = 0;
        index++;
        example.age += world.delta;
    }

    @Override
    protected void dispose() {
        super.dispose();
        batch.dispose();
        batch = null;
    }
}
