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

package com.ellzone.slotpuzzle2d.level.creator;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.spin.SpinWheelSlotPuzzleTileMap;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.HoldLightButton;
import com.ellzone.slotpuzzle2d.sprites.ReelHelper;
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.slothandle.SlotHandleSprite;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReelTileMap;
import com.ellzone.slotpuzzle2d.sprites.slothandle.SlotHandleTileMap;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.assets.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;

public class LevelObjectCreatorEntityHolder extends LevelObjectCreator {
    private LevelHoldLightButtonCallback levelHoldLightButtonCallback;
    private LevelAnimatedReelCallback levelAnimatedReelCallback;
    private LevelSlotHandleSpriteCallback levelSlotHandleSpriteCallback;
    private LevelSlotHandleCallback levelSlotHandleCallback;
    private LevelPointLightCallback levelPointLightCallback;
    private LevelConeLightCallback levelConeLightCallback;
    private LevelReelHelperCallback levelReelHelperCallback;
    private LevelSpinWheelCallback levelSpinWheelCallback;
    private Array<ReelTile> reelTiles = new Array<>();
    private Array<HoldLightButton> lightButtons = new Array<>();
    private Array<AnimatedReel> reels = new Array<>();
    private Array<AnimatedReelTileMap> animatedReels = new Array<>();
    private Array<PointLight> pointLights = new Array<>();
    private Array<ConeLight> coneLights = new Array<>();
    private Array<SlotHandleSprite> handles = new Array<>();
    private Array<SlotHandleTileMap> slotHandles = new Array<>();
    private Array<SpinWheelSlotPuzzleTileMap> spinWheels = new Array<>();
    private ReelHelper reelHelper;
    private LevelAnimatedReelTileMapCallback levelAnimatedReelTileMapCallback;

    public LevelObjectCreatorEntityHolder(
            LevelCreatorInjectionInterface injection, World world, RayHandler rayHandler) {
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
    public AnnotationAssetManager getAnnotationAssetManager() {
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

    public Array<ReelTile> getReelTiles() { return reelTiles; }

    public Color getReelPointLightColor() {
        return super.reelPointLightColor;
    }

    public Color getConeLightColor() {
        return new Color(Color.RED);
    }

    public void addTo(HoldLightButton holdLightButton) {
        lightButtons.add(holdLightButton);
    }

    public void addTo(AnimatedReel reel) {
        reels.add(reel);
        reelTiles.add(reel.getReel());
    }

    public void addTo(AnimatedReelTileMap animatedReel) {
        animatedReels.add(animatedReel);
    }

    public void addTo(SlotHandleSprite handle) { handles.add(handle); }

    public void addTo(SlotHandleTileMap handle) { slotHandles.add(handle); }

    public void addTo(PointLight pointLight) { pointLights.add(pointLight); }

    public void addTo(ConeLight coneLight) { coneLights.add(coneLight); }

    public void addTo(ReelHelper reelHelper) { this.reelHelper = reelHelper; }

    public void addTo(SpinWheelSlotPuzzleTileMap spinWheel) { spinWheels.add(spinWheel); }

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

    public void addConeLightCallback(LevelConeLightCallback callback) {
        this.levelConeLightCallback = callback;
    }

    public void addReelHelperCallback(LevelReelHelperCallback callback) {
        this.levelReelHelperCallback = callback;
    }

    public void addSpinWheelCallback(LevelSpinWheelCallback callback) {
        this.levelSpinWheelCallback = callback;
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
        if (levelAnimatedReelCallback != null)
            levelAnimatedReelCallback.onEvent(animatedReel);
    }

    public void delegateToCallback(AnimatedReelTileMap animatedReelTileMap) {
        if (levelAnimatedReelTileMapCallback != null)
            levelAnimatedReelTileMapCallback.onEvent(animatedReelTileMap);
    }

    public void delegateToCallback(SlotHandleSprite slotHandleSprite) {
        if (levelSlotHandleSpriteCallback != null)
            levelSlotHandleSpriteCallback.onEvent(slotHandleSprite);
    }

    public void delegateToCallback(SlotHandleTileMap slotHandle) {
        if (levelSlotHandleCallback != null)
            levelSlotHandleCallback.onEvent(slotHandle);
    }

    public void delegateToCallback(PointLight pointLight) {
        if (levelPointLightCallback != null)
            levelPointLightCallback.onEvent(pointLight);
    }

    public void delegateToCallback(ConeLight coneLight) {
        if (levelConeLightCallback != null)
            levelConeLightCallback.onEvent(coneLight);
    }

    public void delegateToCallback(ReelHelper reelHelper) {
        if (levelReelHelperCallback != null)
            levelReelHelperCallback.onEvent(reelHelper);
    }

    public void delegateToCallback(SpinWheelSlotPuzzleTileMap spinWheel) {
        if (levelSpinWheelCallback != null)
            levelSpinWheelCallback.onEvent(spinWheel);
    }

    public Array<HoldLightButton> getHoldLightButtons() {
        return lightButtons;
    }

    public Array<AnimatedReel> getAnimatedReels() {
        return reels;
    }

    public Array<AnimatedReelTileMap> getAnimatedReelsTileMap() { return animatedReels; }

    public Array<SlotHandleSprite> getHandles() { return handles; }

    public Array<SlotHandleTileMap> getSlotHandles() { return slotHandles; }

    public Array<SpinWheelSlotPuzzleTileMap> getSpinWheels() { return spinWheels; }
}
