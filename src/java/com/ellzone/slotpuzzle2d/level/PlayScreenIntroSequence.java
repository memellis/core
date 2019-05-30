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

package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;


import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;

import static com.badlogic.gdx.math.MathUtils.random;

public class PlayScreenIntroSequence {
    private Array<ReelTile> reelTiles;
    private TweenManager tweenManager;

    public PlayScreenIntroSequence(Array<ReelTile> reelTiles, TweenManager tweenManager) {
        this.reelTiles = reelTiles;
        this.tweenManager = tweenManager;
    }

    public void createReelIntroSequence(TweenCallback introSequenceCallback) {
        Timeline introSequence = Timeline.createParallel();
        for(int i=0; i < reelTiles.size; i++) {
            introSequence = introSequence
                    .push(buildSequence(reelTiles.get(i), i, random.nextFloat() * 3.0f, random.nextFloat() * 3.0f));
        }
        introSequence.pushPause(0.3f)
                .setCallback(introSequenceCallback)
                .setCallbackTriggers(TweenCallback.END)
                .start(tweenManager);
    }

    private Timeline buildSequence(Sprite target, int id, float delay1, float delay2) {
        Vector2 targetXY = getRandomCorner();
        return Timeline.createSequence()
                .push(SlotPuzzleTween.set(target, SpriteAccessor.POS_XY).target(targetXY.x, targetXY.y))
                .push(SlotPuzzleTween.set(target, SpriteAccessor.SCALE_XY).target(20, 20))
                .push(SlotPuzzleTween.set(target, SpriteAccessor.ROTATION).target(0))
                .push(SlotPuzzleTween.set(target, SpriteAccessor.OPACITY).target(0))
                .pushPause(delay1)
                .beginParallel()
                .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1, 1).ease(Quart.INOUT))
                .end()
                .pushPause(-0.5f)
                .push(SlotPuzzleTween.to(target, SpriteAccessor.POS_XY, 0.8f).target(reelTiles.get(id).getX(), reelTiles.get(id).getY()).ease(Back.OUT))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.ROTATION, 0.8f).target(360).ease(Cubic.INOUT))
                .pushPause(delay2)
                .beginParallel()
                .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 0.3f).target(3, 3).ease(Quad.IN))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 0.3f).target(0).ease(Quad.IN))
                .end()
                .pushPause(-0.5f)
                .beginParallel()
                .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1.0f, 1.0f).ease(Quart.INOUT))
                .end();
    }

    private Vector2 getRandomCorner() {
        int randomCorner = random.nextInt(4);
        switch (randomCorner) {
            case 0:
                return new Vector2(-1 * random.nextFloat(), -1 * random.nextFloat());
            case 1:
                return new Vector2(-1 * random.nextFloat(), SlotPuzzleConstants.VIRTUAL_WIDTH + random.nextFloat());
            case 2:
                return new Vector2(SlotPuzzleConstants.VIRTUAL_HEIGHT / 2 + random.nextFloat(), -1 * random.nextFloat());
            case 3:
                return new Vector2(SlotPuzzleConstants.VIRTUAL_HEIGHT + random.nextFloat(), SlotPuzzleConstants.VIRTUAL_WIDTH + random.nextFloat());
            default:
                return new Vector2(-0.5f, -0.5f);
        }
    }
}
