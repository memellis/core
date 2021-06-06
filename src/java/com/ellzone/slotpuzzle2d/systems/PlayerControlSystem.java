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

package com.ellzone.slotpuzzle2d.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.components.AnimatedReelComponent;
import com.ellzone.slotpuzzle2d.components.LightButtonComponent;
import com.ellzone.slotpuzzle2d.components.PlayerComponent;
import com.ellzone.slotpuzzle2d.components.ReelHelperComponent;
import com.ellzone.slotpuzzle2d.components.SlothandleSpriteComponent;
import com.ellzone.slotpuzzle2d.components.VisualComponent;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.LightButton;
import com.ellzone.slotpuzzle2d.sprites.ReelHelper;
import com.ellzone.slotpuzzle2d.sprites.slothandle.SlotHandleSprite;
import com.ellzone.slotpuzzle2d.utils.assets.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.Random;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class PlayerControlSystem extends EntitySystem {
    private Viewport viewport, lightViewport;
    private Vector2 touch = new Vector2();
    private AnnotationAssetManager annotationAssetManager;
    private ImmutableArray<Entity> animatedReelEntities;
    private ImmutableArray<Entity> lightButtonEntities;
    private ImmutableArray<Entity> slotHandleEntities;
    private ImmutableArray<Entity> reelHelperEntities;
    private ComponentMapper<AnimatedReelComponent> animatedReelComponentMapper = ComponentMapper.getFor(AnimatedReelComponent.class);
    private ComponentMapper<LightButtonComponent> lightButtonComponentMapper = ComponentMapper.getFor(LightButtonComponent.class);
    private ComponentMapper<SlothandleSpriteComponent> slothandleSpriteComponentMapper = ComponentMapper.getFor(SlothandleSpriteComponent.class);
    private ComponentMapper<ReelHelperComponent> reelHelperComponentMapper = ComponentMapper.getFor(ReelHelperComponent.class);
    private SystemCallback systemCallback;

    public PlayerControlSystem(Viewport viewport, Viewport lightViewPort, AnnotationAssetManager annotationAssetManager) {
        this.viewport = viewport;
        this.lightViewport = lightViewPort;
        this.annotationAssetManager = annotationAssetManager;
    }

    @Override
    public void addedToEngine(Engine engine) {
        animatedReelEntities = engine.getEntitiesFor(Family.all(AnimatedReelComponent.class, PlayerComponent.class).get());
        lightButtonEntities = engine.getEntitiesFor(Family.all(LightButtonComponent.class, PlayerComponent.class).get());
        slotHandleEntities = engine.getEntitiesFor(Family.all(SlothandleSpriteComponent.class, PlayerComponent.class).get());
        reelHelperEntities = engine.getEntitiesFor(Family.all(ReelHelperComponent.class, PlayerComponent.class).get());
    }

    @Override
    public void removedFromEngine(Engine engine) {
    }

    public void addCallback(SystemCallback callback) {
        this.systemCallback = callback;
    }

    @Override
    public void update(float deltaTime) {
        handleInput();
    }

    public void handleInput() {
        if (Gdx.input.justTouched()) {
            touch = touch.set(Gdx.input.getX(), Gdx.input.getY());
            touch = viewport.unproject(touch);
            handleAnimatedReelsTouched();
            handleSlotHandleIsTouched();
            touch = touch.set(Gdx.input.getX(), Gdx.input.getY());
            touch = lightViewport.unproject(touch);
            handleLightButtonTouched();
        }
    }

    private void handleAnimatedReelsTouched() {
        for (Entity animatedReelEntity : animatedReelEntities)
            animatedReelEntityTouched(animatedReelEntity);
    }

    private void animatedReelEntityTouched(Entity animatedReelEntity) {
        AnimatedReelComponent animatedReelComponent = animatedReelComponentMapper.get(animatedReelEntity);
        AnimatedReel animatedReel = animatedReelComponent.animatedReel;
        if (animatedReel.getReel().getBoundingRectangle().contains(touch)) {
            if (animatedReel.getReel().isSpinning()) {
                if (animatedReel.getDampenedSineState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
                    setReelHelpers(animatedReel);
                }
            } else {
                    animatedReel.setEndReel(Random.getInstance().nextInt(animatedReel.getReel().getNumberOfReelsInTexture()));
                    animatedReel.reinitialise();
                    animatedReel.getReel().startSpinning();
            }
        }
    }

    private void setReelHelpers(AnimatedReel animatedReel) {
        for (Entity reelHelper : reelHelperEntities)
            setReelHelper(reelHelper, animatedReel);
    }

    private void setReelHelper(Entity reelHelperEntity, AnimatedReel animatedReel) {
        ReelHelperComponent reelHelperComponent = reelHelperComponentMapper.get(reelHelperEntity);
        VisualComponent visualComponent = reelHelperEntity.getComponent(VisualComponent.class);
        ReelHelper reelHelper = reelHelperComponent.reelHelper;
        int reelSpriteHelp = animatedReel.getReel().getCurrentReel();
        visualComponent.region = reelHelper.getSprites()[reelSpriteHelp];
        animatedReel.getReel().setEndReel(reelSpriteHelp - 1 < 0 ? 0 : reelSpriteHelp - 1);
    }

    private void handleSlotHandleIsTouched() {
        for (Entity slotHandleEntity : slotHandleEntities)
            slotHandleEntityTouched(slotHandleEntity);
    }

    private void slotHandleEntityTouched(Entity slotHandleEntity) {
        SlotHandleSprite slotHandleSprite = slotHandleEntity.getComponent(SlothandleSpriteComponent.class).slotHandleSprite;
        if (slotHandleSprite.getBoundingRectangle().contains(touch)) {
            if (isAnimatedReelsNotSpinning())
                slotHandlePulled(slotHandleSprite);
            else
                playReelsStoppedSound();
        }
    }

    private void playReelsStoppedSound() {
        ((Sound) annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_STOPPED)).play();
    }

    private boolean isAnimatedReelsNotSpinning() {
        boolean reelsNotSpinning = true;
        for (Entity animatedReelEntity : animatedReelEntities)
            if (isAnimatedReelsNotSpinning(animatedReelEntity))
                reelsNotSpinning = false;

        return reelsNotSpinning;
    }

    private boolean isAnimatedReelsNotSpinning(Entity animatedReelEntity) {
        AnimatedReelComponent animatedReelComponent = animatedReelComponentMapper.get(animatedReelEntity);
        return animatedReelComponent.animatedReel.getReel().isSpinning();
     }

    private void slotHandlePulled(SlotHandleSprite slotHandleSprite) {
        slotHandleSprite.pullSlotHandle();
        playPullLeverSound();
        clearRowMatchesToDraw();
        int lightButtonIndex = 0;
        for (Entity animatedReelEntity : animatedReelEntities) {
            spinAnimatedReel(animatedReelEntity, lightButtonIndex);
            lightButtonIndex++;
        }
    }

    private void playPullLeverSound() {
        ((Sound) annotationAssetManager.get(AssetsAnnotation.SOUND_PULL_LEVER)).play();
    }

    private void clearRowMatchesToDraw() {
        systemCallback.onEvent(new SlotHandlePulledPlayerSystemEvent(), this);
    }

    private void spinAnimatedReel(Entity animatedReelEntity, int lightButtonIndex) {
        LightButton lightButton = lightButtonEntities.get(lightButtonIndex).getComponent(LightButtonComponent.class).lightButton;
        if (!lightButton.getLight().isActive()) {
            AnimatedReel animatedReel = animatedReelEntity.getComponent(AnimatedReelComponent.class).animatedReel;
            animatedReel.setEndReel(Random.getInstance().nextInt(animatedReel.getReel().getNumberOfReelsInTexture() - 1));
            animatedReel.reinitialise();
            animatedReel.getReel().startSpinning();
        }
    }


    private void handleLightButtonTouched() {
        for (Entity lightButtonEntity : lightButtonEntities)
            lightButtonEntityTouched(lightButtonEntity);
    }

    private void lightButtonEntityTouched(Entity lightButtonEntity) {
        LightButtonComponent lightButtonComponent = lightButtonComponentMapper.get(lightButtonEntity);
        LightButton lightButton = lightButtonComponent.lightButton;
        if (lightButton.getSprite().getBoundingRectangle().contains(touch.x, touch.y)) {
            if (lightButton.getLight().isActive())
                lightButton.getLight().setActive(false);
            else
                lightButton.getLight().setActive(true);
        }
    }
}
