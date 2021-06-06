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

package com.ellzone.slotpuzzle2d.prototypes.tween;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReelHelper;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.utils.assets.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.TimeStamp;

import java.util.Random;

import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;

public class IntroSequenceSpinningReelsWithBombSet  extends SPPrototypeTemplate {
    private Pixmap slotReelScrollPixmap;
    private Texture slotReelScrollTexture;
    private Random random;
    AnimatedReelHelper animatedReelHelper;
    private Array<ReelTile> reels;
    private Timeline introSequence;
    private int currentReel = 0;

    @Override
    protected void initialiseOverride() {
        random = new Random();
        addBombSprite();
        animatedReelHelper = new AnimatedReelHelper(
                annotationAssetManager,
                tweenManager,
                16,
                0,
                reelSprites
        );
        reels = animatedReelHelper.getReelTiles();
        createIntroSequence();
        createStartReelTimer();
    }

    private void addBombSprite() {
        TextureAtlas reelAtlas = annotationAssetManager.get(AssetsAnnotation.REELS_EXTENDED);
        Sprite sprite = reelAtlas.createSprite(AssetsAnnotation.BOMB40x40);
        reelSprites.addSprite(reelAtlas.createSprite(AssetsAnnotation.BOMB40x40));
    }

    @Override
    protected void initialiseScreenOverride() {
    }

    @Override
    protected void loadAssetsOverride() {
    }

    @Override
    protected void disposeOverride() {
    }

    @Override
    protected void updateOverride(float dt) {
        animatedReelHelper.update(dt);
    }

    @Override
    protected void renderOverride(float dt) {
        batch.begin();
        for (AnimatedReel reel : animatedReelHelper.getAnimatedReels())
            reel.draw(batch);
        batch.end();
    }

    @Override
    protected void initialiseUniversalTweenEngineOverride() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
    }

    private void createIntroSequence() {
        introSequence = Timeline.createParallel();
        for(int i = 0; i < reels.size; i++) {
            introSequence = introSequence
                    .push(buildSequence(reels.get(i), i, random.nextFloat() * 5.0f, random.nextFloat() * 5.0f));
        }

        introSequence = introSequence
                .pushPause(0.3f)
                .start(tweenManager);
    }

    private void createStartReelTimer() {
        Timer.schedule(new Timer.Task(){
                           @Override
                           public void run() {
                               startAReel();
                           }
                       }
                , 1.0f
                , 0.1f
                , reels.size
        );
    }

    private void startAReel() {
        if (currentReel < reels.size) {
            System.out.println("startReel=" + currentReel + "@ " + TimeStamp.getTimeStamp());
            animatedReelHelper.getAnimatedReels().get(currentReel).setupSpinning();
            animatedReelHelper.getAnimatedReels().get(currentReel).getReel().setSpinning(true);
            currentReel++;
        }
    }

    private Timeline buildSequence(Sprite target, int id, float delay1, float delay2) {
        Vector2 targetXY = getRandomCorner();
        return Timeline.createSequence()
                .push(SlotPuzzleTween.set(target, SpriteAccessor.POS_XY).target(targetXY.x, targetXY.y))
                .push(SlotPuzzleTween.set(target, SpriteAccessor.SCALE_XY).target(30, 30))
                .push(SlotPuzzleTween.set(target, SpriteAccessor.ROTATION).target(0))
                .push(SlotPuzzleTween.set(target, SpriteAccessor.OPACITY).target(0))
                .pushPause(delay1)
                .beginParallel()
                .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1, 1).ease(Quart.INOUT))
                .end()
                .pushPause(-0.5f)
                .push(SlotPuzzleTween.to(target, SpriteAccessor.POS_XY, 1.0f).target(id * spriteWidth, id * spriteHeight).ease(Back.OUT))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.ROTATION, 0.8f).target(360).ease(Cubic.INOUT))
                .pushPause(delay2)
                .beginParallel()
                .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 0.3f).target(3, 3).ease(Quad.IN))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 0.3f).target(0).ease(Quad.IN))
                .end()
                .pushPause(-0.5f)
                .beginParallel()
                .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1, 1).ease(Quart.INOUT))
                .end();
    }

    private Vector2 getRandomCorner() {
        int randomCorner = random.nextInt(4);
        switch (randomCorner) {
            case 0:
                return new Vector2(-1 * random.nextFloat(), -1 * random.nextFloat());
            case 1:
                return new Vector2(-1 * random.nextFloat(), displayWindowWidth + random.nextFloat());
            case 2:
                return new Vector2(displayWindowHeight + random.nextFloat(), -1 * random.nextFloat());
            case 3:
                return new Vector2(displayWindowHeight + random.nextFloat(), displayWindowWidth + random.nextFloat());
            default:
                return new Vector2(-0.5f, -0.5f);
        }
    }
}
