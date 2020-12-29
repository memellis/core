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

package com.ellzone.slotpuzzle2d.sprites.reel;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.SlotMachineAnimatedReel;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.WorldScreenConvert;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class AnimatedReelTileMap implements AnimatedReelInterface {
    private SlotMachineAnimatedReel animatedReel;
    private float worldPositionX;
    private float worldPositionY;

    public AnimatedReelTileMap(
            float x,
            float y,
            float tileWidth,
            float tileHeight,
            float reelDisplayWidth,
            float reelDisplayHeight,
            int endReel,
            TweenManager tweenManager,
            AnnotationAssetManager annotationAssetManager) {

        animatedReel = new SlotMachineAnimatedReel(
                convertTileMapXToWorldPostionX(x) / 100,
                convertTileMapYToWorldPostionY(y) / 100,
                tileWidth,
                tileHeight,
                tileWidth,
                tileHeight * 3,
                endReel,
                tweenManager,
                annotationAssetManager);
        animatedReel.getAnimatedReel().getReel().
                setReelDisplaySize(
                        reelDisplayWidth / 50,
                        reelDisplayHeight / 50);
    }

    @Override
    public void setupSpinning() {
        animatedReel.getAnimatedReel().setupSpinning();
    }

    @Override
    public void setX(float x) {
        animatedReel.getAnimatedReel().setX(x);
    }

    @Override
    public void setY(float y) {
        animatedReel.getAnimatedReel().setY(y);
    }

    @Override
    public void setSx(float sx) {
        animatedReel.getAnimatedReel().setSx(sx);
    }

    @Override
    public void setSy(float sy) {
        animatedReel.getAnimatedReel().setSy(sy);
    }

    @Override
    public float getSx() {
        return animatedReel.getAnimatedReel().getSx();
    }

    @Override
    public float getSy() {
        return animatedReel.getAnimatedReel().getSy();
    }

    @Override
    public int getEndReel() {
        return animatedReel.getAnimatedReel().getEndReel();
    }

    @Override
    public void setEndReel(int endReel) {
        animatedReel.getAnimatedReel().setEndReel(endReel);
    }

    @Override
    public void update(float delta) {
        animatedReel.getAnimatedReel().update(delta);
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        animatedReel.getAnimatedReel().getReel().draw(spriteBatch);
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        animatedReel.getAnimatedReel().draw(shapeRenderer);
    }

    @Override
    public ReelTile getReel() {
        return animatedReel.getAnimatedReel().getReel();
    }

    @Override
    public void reinitialise() {
        animatedReel.getAnimatedReel().reinitialise();
    }

    @Override
    public DampenedSineParticle.DSState getDampenedSineState() {
        return animatedReel.getAnimatedReel().getDampenedSineState();
    }

    private float convertTileMapXToWorldPostionX(float x) {
        worldPositionX = WorldScreenConvert.convertTileMapXToWorldPostionX(x);
        return worldPositionX;
    }

    private float convertTileMapYToWorldPostionY(float y) {
        worldPositionY = WorldScreenConvert.convertTileMapYToWorldPostionY(y);
        return worldPositionY;
    }
}
