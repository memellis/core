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

package com.ellzone.slotpuzzle2d.prototypes.box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.camera.CameraHelper;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.physics.BoxBodyBuilder;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReelHelper;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.assets.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class Box2DFallingSpinningReelsWithCatchBox extends SPPrototype {
    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private AnnotationAssetManager annotationAssetManager;
    private PhysicsManagerCustomBodies physics;
    private BoxBodyBuilder bodyFactory;
    private AnimatedReelHelper animatedReelHelper;
    private TweenManager tweenManager;
    private Array<Body> reelBoxes;
    private Array<AnimatedReel> animatedReels;
    private float centreX = SlotPuzzleConstants.VIRTUAL_WIDTH / 2;
    private float centreY = SlotPuzzleConstants.VIRTUAL_HEIGHT / 2;
    private int currentReel = 0;

    @Override
    public void create() {
        camera = CameraHelper.GetCamera(SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT);
        batch = new SpriteBatch();
        viewport = new FitViewport(SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT, new OrthographicCamera());

        annotationAssetManager = loadAssets();
        initialiseUniversalTweenEngine();
        animatedReelHelper = new AnimatedReelHelper(
                annotationAssetManager, tweenManager, 4 * 6);
        animatedReels = animatedReelHelper.getAnimatedReels();

        physics = new PhysicsManagerCustomBodies(camera);
        bodyFactory = physics.getBodyFactory();
        createCatchBox();
        reelBoxes = createReelBoxes();
        createStartReelTimer();
    }

    private void createCatchBox() {
        physics.createEdgeBody(BodyDef.BodyType.StaticBody,
                centreX - 7 * 42 / 2,
                centreY - 4 * 42 / 2,
                centreX + 7 * 42 / 2,
                centreY - 4 * 42 / 2);
        physics.createEdgeBody(BodyDef.BodyType.StaticBody,
                centreX - 7 * 42 / 2,
                centreY - 4 * 42 / 2,
                centreX - 7 * 42 / 2,
                centreY + 4 * 42 / 2);
        physics.createEdgeBody(BodyDef.BodyType.StaticBody,
                centreX + 7 * 42 / 2,
                centreY - 4 * 42 / 2,
                centreX + 7 * 42 / 2,
                centreY + 4 * 42 / 2);
    }

    private AnnotationAssetManager loadAssets() {
        AnnotationAssetManager annotationAssetManager = new AnnotationAssetManager();
        annotationAssetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        annotationAssetManager.load(new AssetsAnnotation());
        annotationAssetManager.finishLoading();
        return annotationAssetManager;
    }

    private Array<Body> createReelBoxes() {
        Array<Body> reelBoxes = new Array<Body>();
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 6; column++) {
                reelBoxes.add(physics.createBoxBody(BodyDef.BodyType.DynamicBody,
                        centreX - 7 * 40 / 2 + 20 + (column * 40),
                        SlotPuzzleConstants.VIRTUAL_HEIGHT + (row * 40) / 2,
                        20,
                        20,
                        false));
            }
        }
        return reelBoxes;
    }

    private void initialiseUniversalTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        tweenManager = new TweenManager();
    }

    private void createStartReelTimer() {
        Timer.schedule(new Timer.Task(){
                           @Override
                           public void run() {
                               startAReel();
                           }
                       }
                , 0.0f
                , 0.02f
                , animatedReels.size
        );
    }

    private void startAReel() {
        if (currentReel < animatedReels.size) {
            animatedReels.get(currentReel).setupSpinning();
            animatedReels.get(currentReel++).getReel().setSpinning(true);
        }
    }

    @Override
    public void dispose() {
        physics.dispose();
        batch.dispose();
    }

    private void update(float dt) {
        tweenManager.update(dt);
        physics.update(dt);
        animatedReelHelper.update(dt);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(Gdx.graphics.getDeltaTime());

        renderReelBoxes(batch, reelBoxes, animatedReels);
        physics.draw(batch);
     }

    private void renderReelBoxes(SpriteBatch batch, Array<Body> reelBoxes, Array<AnimatedReel> animatedReels) {
        batch.begin();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        int i = 0;
        for (Body reelBox : reelBoxes) {
            Vector2 position = reelBox.getPosition();
            float angle = MathUtils.radiansToDegrees * reelBox.getAngle();
            ReelTile reelTile = animatedReels.get(i).getReel();
            reelTile.setPosition(position.x * 100 - centreX - 20, position.y * 100 - centreY - 20);
            reelTile.setOrigin(20, 20);
            reelTile.setSize( 40, 40);
            reelTile.setRotation(angle);
            reelTile.draw(batch);
            i++;
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
