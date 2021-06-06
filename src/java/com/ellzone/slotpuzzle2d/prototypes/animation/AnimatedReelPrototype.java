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

package com.ellzone.slotpuzzle2d.prototypes.animation;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.ellzone.slotpuzzle2d.audio.AudioManager;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReelHelper;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.utils.assets.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.TimeStamp;

import static com.ellzone.slotpuzzle2d.messaging.MessageType.PauseAudio;
import static com.ellzone.slotpuzzle2d.messaging.MessageType.PlayAudio;
import static com.ellzone.slotpuzzle2d.messaging.MessageType.StopAudio;

public class AnimatedReelPrototype extends SPPrototypeTemplate  {
    private AnimatedReelHelper animatedReelHelper;
    private Array<ReelTile> reels;
    private int currentReel = 0;
    private MessageDispatcher messageManager;
    private Telegraph audioManager;

    @Override
    protected void initialiseOverride() {
        animatedReelHelper = new AnimatedReelHelper(
                annotationAssetManager,
                tweenManager,
                6
        );
        reels = animatedReelHelper.getReelTiles();
        audioManager = new AudioManager(annotationAssetManager);
        messageManager = setUpMessageManager();
        createStartReelTimer();
    }

    private MessageManager setUpMessageManager() {
        MessageManager messageManager = MessageManager.getInstance();
        messageManager.addListeners(
                audioManager,
                PlayAudio.index,
                StopAudio.index,
                PauseAudio.index);
        return messageManager;
    }

    private void createStartReelTimer() {
        Timer.schedule(new Timer.Task(){
                           @Override
                           public void run() {
                               startAReel();
                           }
                       }
                , 1.0f
                , 0.5f
                , reels.size
        );
    }

    private void startAReel() {
        if (currentReel < reels.size) {
            System.out.println("startReel=" + currentReel + "@ " + TimeStamp.getTimeStamp());
            ReelTile reelTile = animatedReelHelper.getAnimatedReels().get(currentReel).getReel();
            reelTile.setIndex(currentReel);
            addReelSpinningCallback(reelTile);
            setReelPosition(reelTile);
            animatedReelHelper.getAnimatedReels().get(currentReel).setupSpinning();
            reelTile.setSpinning(true);
            currentReel++;
        }
    }

    private void addReelSpinningCallback(final ReelTile reel) {
        reel.addListener(new ReelTileListener() {
            @Override
            public void actionPerformed(ReelTileEvent event, ReelTile reelTile) {
                if (event instanceof ReelStoppedSpinningEvent)
                    processReelHasStoppedSpinning(reelTile);
            }
        });
    }

    private void processReelHasStoppedSpinning(ReelTile reelTile) {
        System.out.println("stopReel=" + reelTile.getIndex() + "@ " + TimeStamp.getTimeStamp());
        playSound(AssetsAnnotation.SOUND_REEL_STOPPED);
        restartSpinning(reelTile);
    }

    private void restartSpinning(ReelTile reelTile) {
        AnimatedReel animatedReel = animatedReelHelper.getAnimatedReels().get(reelTile.getIndex());
        animatedReel.reinitialise();
        animatedReel.setupSpinning();
        reelTile.setSpinning(true);
;    }

    private void playSound(String sound) {
        messageManager.dispatchMessage(PlayAudio.index, sound);
    }

    private void setReelPosition(ReelTile reelTile) {
        reelTile.setX(currentReel * 40);
        reelTile.setY(currentReel * 40);
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
        for (AnimatedReel animatedReel : animatedReelHelper.getAnimatedReels())
            animatedReel.draw(batch);
        batch.end();
    }

    @Override
    protected void initialiseUniversalTweenEngineOverride() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
    }
}
