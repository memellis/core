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

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.HoldLightButton;
import com.ellzone.slotpuzzle2d.sprites.ReelHelper;
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.SlotHandleSprite;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class LevelObjectCreatorEntityHolder extends LevelObjectCreator {
    private LevelHoldLightButtonCallback levelHoldLightButtonCallback;
    private LevelAnimatedReelCallback levelAnimatedReelCallback;
    private LevelSlotHandleSpriteCallback levelSlotHandleSpriteCallback;
    private LevelPointLightCallback levelPointLightCallback;
    private LevelReelHelperCallback levelReelHelperCallback;

    public LevelObjectCreatorEntityHolder(LevelCreatorInjectionInterface injection, World world, RayHandler rayHandler) {
        super(injection, world, rayHandler);
    }

    public World getWorld() {
        return world;
    }

    public RayHandler getRayHandler() {
        return rayHandler;
    }

    @Override
    public TextureAtlas getSlotHandleAtlas() {
        return super.getSlotHandleAtlas();
    }

    @Override
    public TweenManager getTweenManager() {
        return super.getTweenManager();

    }

    @Override
    public AnnotationAssetManager annotationAssetManager() {
        return super.levelCreatorInjectionInterface.getAnnotationAssetManager();
    }

    @Override
    public Texture getSlotReelScrollTexture() {
        return levelCreatorInjectionInterface.getSlotReelScrollTexture();
    }

    @Override
    public Sound getReelSpinningSound() {
        return levelCreatorInjectionInterface.getAnnotationAssetManager()
                .get(AssetsAnnotation.SOUND_REEL_SPINNING);
    }

    @Override
    public Sound getReelStoppingSound() {
        return levelCreatorInjectionInterface.getAnnotationAssetManager()
                .get(AssetsAnnotation.SOUND_REEL_STOPPED);
    }

    @Override
    public ReelSprites getReelSprites() {
        return levelCreatorInjectionInterface.getReelSprites();
    }


    public Color getReelPointLightColor() {
        return super.reelPointLightColor;
    }

    public void addTo(HoldLightButton holdLightButton) {
        lightButtons.add(holdLightButton);
    }

    public void addTo(AnimatedReel reel) {
        reels.add(reel);
    }

    public void addTo(SlotHandleSprite handle) { this.handle = handle; }

    public void addTo(PointLight pointLight) { pointLights.add(pointLight); }

    public void addTo(ReelHelper reelHelper) { this.reelHelper = reelHelper; }

    public void addHoldLightButtonCallback(LevelHoldLightButtonCallback callback) {
        this.levelHoldLightButtonCallback = callback;
    }

    public void addAnimatedReelCallback(LevelAnimatedReelCallback callback) {
        this.levelAnimatedReelCallback = callback;
    }

    public void addSlotHandleCallback(LevelSlotHandleSpriteCallback callback) {
        this.levelSlotHandleSpriteCallback = callback;
    }

    public void addPointLightCallback(LevelPointLightCallback callback) {
        this.levelPointLightCallback = callback;
    }

    public void addReelHelperCallback(LevelReelHelperCallback callback) {
        this.levelReelHelperCallback = callback;
    }

    public void delegateToCallback(HoldLightButton holdLightButton) {
        if (levelHoldLightButtonCallback != null)
            levelHoldLightButtonCallback.onEvent(holdLightButton);
    }

    public void addComponentToEntity(PointLight pointLight, Component component) {
        if (levelPointLightCallback != null)
            levelPointLightCallback.addComponent(component);
    }

    public void addComponentToEntity(HoldLightButton holdLightButton, Component component) {
        if (levelHoldLightButtonCallback != null)
            levelHoldLightButtonCallback.addComponent(component);
    }

    public void addComponentToEntity(ReelHelper reelHelper, Component component) {
        if (levelReelHelperCallback != null)
            levelReelHelperCallback.addComponent(component);
    }

    public void delegateToCallback(AnimatedReel animatedReel) {
        if (levelPointLightCallback != null)
            levelAnimatedReelCallback.onEvent(animatedReel);
    }

    public void delegateToCallback(SlotHandleSprite slotHandleSprite) {
        if (levelPointLightCallback != null)
            levelSlotHandleSpriteCallback.onEvent(slotHandleSprite);
    }

    public void delegateToCallback(PointLight pointLight) {
        if (levelPointLightCallback != null)
            levelPointLightCallback.onEvent(pointLight);
    }

    public void delegateToCallback(ReelHelper reelHelper) {
        if (levelReelHelperCallback != null)
            levelReelHelperCallback.onEvent(reelHelper);
    }
}
