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

package com.ellzone.slotpuzzle2d.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.effects.ScoreAccessor;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayState;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle2d.level.creator.LevelCallback;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreator;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.creator.LevelLoader;
import com.ellzone.slotpuzzle2d.level.creator.LevelObjectCreatorEntityHolder;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.HoldLightButton;
import com.ellzone.slotpuzzle2d.sprites.LightButton;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.score.Score;
import com.ellzone.slotpuzzle2d.sprites.SlotHandleSprite;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;

import java.util.Comparator;

import aurelienribon.tweenengine.equations.Quad;

import static com.ellzone.slotpuzzle2d.level.creator.LevelCreator.MINI_SLOT_MACHINE_LEVEL_TYPE;

public class PlayScreenMiniSlotMachine extends PlayScreen {

    public static final int LEVEL_TIME_LENGTH = 120;
    private int[][] reelGrid = new int[3][3];
    private Array<Array<Vector2>> rowMacthesToDraw;
    private ShapeRenderer shapeRenderer;
    private Array<HoldLightButton> holdLightButtons;
    private Array<SlotHandleSprite> slotHandles;
    private Array<ReelTile> reelsToFlash;

    public PlayScreenMiniSlotMachine(SlotPuzzle game, LevelDoor levelDoor, MapTile mapTile) {
        super(game, levelDoor, mapTile);
        initialiseMiniSlotMachine();
    }

    private void initialiseMiniSlotMachine() {
        rowMacthesToDraw = new Array<>();
        shapeRenderer = new ShapeRenderer();
    }

    protected void getLevelEntities(LevelObjectCreatorEntityHolder levelObjectCreator) {
        animatedReels = levelObjectCreator.getAnimatedReels();
        reelTiles = levelObjectCreator.getReelTiles();
        holdLightButtons = levelObjectCreator.getHoldLightButtons();
        slotHandles = levelObjectCreator.getHandles();
    }

    protected LevelLoader getLevelLoader() {
        LevelLoader levelLoader = new LevelLoader(game.annotationAssetManager, levelDoor, super.mapTile, animatedReels);
        levelLoader.setStoppedSpinningCallback(new LevelCallback() {
            @Override
            public void onEvent(ReelTile source) {
                if (levelDoor.getLevelType().equals(LevelCreator.MINI_SLOT_MACHINE_LEVEL_TYPE))
                    processReelStopped(source);
            }

        });
        levelLoader.setStoppedFlashingCallback(new LevelCallback() {
            @Override
            public void onEvent(ReelTile source) {
                processReelFlashingStopped(source);
            }
        });
        return levelLoader;
    }

    private void processReelStopped(ReelTile source) {
        reelsSpinning--;
        if (reelsSpinning < 1)
            if (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY ||
                playStateMachine.getStateMachine().getCurrentState() == PlayState.INTRO_SPINNING_SEQUENCE)
                matchReels();
    }

    private void processReelFlashingStopped(ReelTile reelTile) {
        reelScoreAnimation(reelTile);
    }

    protected void reelScoreAnimation(ReelTile source) {
        Array<Vector2> sortedReelFlashSegments = new Array<>(source.getReelFlashSegments());
        sortedReelFlashSegments.sort(new Comparator<Vector2>() {
            @Override
            public int compare(Vector2 v1, Vector2 v2) {
                if (v1.y == v2.y) return 0;
                if (v1.y < v2.y) return -1;
                return 1;
            }
        });
        if (sortedReelFlashSegments.size > 0) {
            float baseFlashSegmentY = sortedReelFlashSegments.get(0).y;
            for (Vector2 reelFlashSegment : sortedReelFlashSegments) {
                Score score = new Score(reelFlashSegment.x, reelFlashSegment.y, (int) (source.getEndReel() + (reelFlashSegment.y - baseFlashSegmentY) / source.getWidth() + 1));
                scores.add(score);
                Timeline.createSequence()
                        .beginParallel()
                        .push(SlotPuzzleTween.to(score, ScoreAccessor.POS_XY, 2.0f).targetRelative(random.nextInt(20), random.nextInt(160)).ease(Quad.IN))
                        .push(SlotPuzzleTween.to(score, ScoreAccessor.SCALE_XY, 2.0f).target(2.0f, 2.0f).ease(Quad.IN))
                        .end()
                        .setUserData(score)
                        .setCallback(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> source) {
                                processDeleteScore(type, source);
                            }
                        })
                        .setCallbackTriggers(TweenCallback.COMPLETE)
                        .start(tweenManager);
            }
            source.clearReelFlashSegments();
        }
    }

    private void processDeleteScore(int type, BaseTween<?> source) {
        switch (type) {
            case TweenCallback.COMPLETE:
                Score score = (Score) source.getUserData();
                scores.removeValue(score, false);
        }
    }

    private void matchReels() {
        if (playStateMachine.getStateMachine().getCurrentState() == PlayState.INTRO_FLASHING_SEQUENCE)
            flashSlots.setReesStartedFlashing(true);

        captureReelPositions();
        PuzzleGridTypeReelTile puzzleGrid = new PuzzleGridTypeReelTile();
        ReelTileGridValue[][] matchGrid = puzzleGrid.populateMatchGrid(reelGrid);
        PuzzleGridTypeReelTile puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        matchGrid = puzzleGridTypeReelTile.createGridLinks(matchGrid);
        PuzzleGridTypeReelTile.printGrid(matchGrid);
        matchRowsToDraw(matchGrid, puzzleGridTypeReelTile);

        if (playStateMachine.getStateMachine().getCurrentState() == PlayState.INTRO_FLASHING_SEQUENCE)
            flashSlots.setFinishedMatchingSlots(true);
    }

    private void matchRowsToDraw(ReelTileGridValue[][] matchGrid, PuzzleGridTypeReelTile puzzleGridTypeReelTile) {
        Array<ReelTileGridValue> depthSearchResults = new Array<>();
        rowMacthesToDraw = new Array<Array<Vector2>>();
        for (int row = 0; row < matchGrid.length; row++) {
            depthSearchResults = puzzleGridTypeReelTile.depthFirstSearchIncludeDiagonals(matchGrid[row][0]);
            if (puzzleGridTypeReelTile.isRow(depthSearchResults, matchGrid)) {
                rowMacthesToDraw.add(drawMatches(depthSearchResults, 340, 300));
                flashSlots.flashSlotsForMiniSlotMachine(depthSearchResults);
            }
        }
    }

    private Array<Vector2> drawMatches(Array<ReelTileGridValue> depthSearchResults, int startX, int startY) {
        Array<Vector2> points = new Array<Vector2>();
        for (ReelTileGridValue cell : depthSearchResults) {
            hud.addScore((reelGrid[cell.r][cell.c] + 1));
            points.add(new Vector2(startX + cell.c * 40, startY - cell.r * 40));
        }
        return points;
    }

    private void captureReelPositions() {
        for (int r = 0; r < reelGrid.length; r++)
            for (int c = 0; c < reelGrid[0].length; c++)
                reelGrid[r][c] = getReelPosition(r, c);
    }

    private int getReelPosition(int r, int c) {
        int reelPosition = reelTiles.get(c).getEndReel() + r;
        if (reelPosition < 0)
            reelPosition = sprites.length - 1;
        else if (reelPosition > sprites.length - 1)
            reelPosition = 0;
        return reelPosition;
    }

    private void handleReelsTouchedSlotMachine(float touchX, float touchY) {
        for (AnimatedReel animatedReel : animatedReels) {
            if (animatedReel.getReel().getBoundingRectangle().contains(touchX, touchY)) {
                clearRowMatchesToDraw();
                if (animatedReel.getReel().isSpinning()) {
                    if (animatedReel.getDampenedSineState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
                        int currentReel = animatedReel.getReel().getCurrentReel();
                        processReelTouchedWhileSpinning(
                                animatedReel.getReel(),
                                currentReel,
                                currentReel + 1 == animatedReel.getReel().getNumberOfReelsInTexture() ?
                                        0 : currentReel + 1);
                    }
                } else {
                    animatedReel.setEndReel(random.nextInt(sprites.length - 1));
                    animatedReel.reinitialise();
                    animatedReel.getReel().startSpinning();
                    reelsSpinning++;
                }
            }
        }
    }

    private void clearRowMatchesToDraw() {
        if (rowMacthesToDraw.size > 0)
            rowMacthesToDraw.removeRange(0, rowMacthesToDraw.size - 1);
    }

    protected void handleInput() {
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.input.getY();
            Vector3 unprojectTouch = new Vector3(touchX, touchY, 0);
            viewport.unproject(unprojectTouch);
            switch (playState) {
                case INTRO_POPUP:
                    if (isOver(playScreenPopUps.getLevelPopUpSprites().get(0), unprojectTouch.x, unprojectTouch.y))
                        playScreenPopUps.getLevelPopUp().hideLevelPopUp(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> source) {
                                processHideLevelPopUp(type, source);
                            }
                        });
                    break;
                case BONUS_LEVEL_ENDED:
                    Gdx.app.debug(SLOTPUZZLE_SCREEN, "Bonus Level");
                    if (isOver(playScreenPopUps.getLevelBonusSprites().get(0), unprojectTouch.x, unprojectTouch.y))
                        playScreenPopUps.getLevelWonPopUp().hideLevelPopUp(levelWonCallback);
                    break;
                default:
                    break;
            }
            if (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY && playState != PlayStates.BONUS_LEVEL_ENDED) {
                Gdx.app.debug(SLOTPUZZLE_SCREEN, "Play");
                if (levelDoor.getLevelType().equals(MINI_SLOT_MACHINE_LEVEL_TYPE)) {
                    handleReelsTouchedSlotMachine(unprojectTouch.x, unprojectTouch.y);
                    handleSlotHandleIsTouch(unprojectTouch.x, unprojectTouch.y);
                    unprojectTouch = new Vector3(touchX, touchY, 0);
                    lightViewport.unproject(unprojectTouch);
                    handleLightButtonTouched(unprojectTouch.x, unprojectTouch.y);
                }
            }
        }
    }

    private void processHideLevelPopUp(int type, BaseTween<?> source) {
        switch (type) {
            case TweenCallback.END:
                playState = PlayStates.PLAYING;
                hud.resetWorldTime(LEVEL_TIME_LENGTH);
                hud.startWorldTimer();
                if (levelDoor.getLevelType().equals(MINI_SLOT_MACHINE_LEVEL_TYPE))
                    matchReels();
        }
    }

    protected void handleLightButtonTouched(float touchX, float touchY) {
        for (LightButton lightButton : holdLightButtons)
            if (lightButton.getSprite().getBoundingRectangle().contains(touchX, touchY))
                if (lightButton.getLight().isActive())
                    lightButton.getLight().setActive(false);
                else
                    lightButton.getLight().setActive(true);
    }

    private void handleSlotHandleIsTouch(float touchX, float touchY) {
        for (SlotHandleSprite slotHandle : slotHandles)
            if (slotHandle.getBoundingRectangle().contains(touchX, touchY))
                if (isReelsNotSpinning())
                    slotHandlePulled(slotHandle);
                else
                    reelStoppedSound.play();
    }

    private boolean isReelsNotSpinning() {
        boolean reelsNotSpinning = true;
        for (AnimatedReel animatedReel : animatedReels)
            if (animatedReel.getReel().isSpinning())
                reelsNotSpinning = false;
        return reelsNotSpinning;
    }

    private void slotHandlePulled(SlotHandleSprite slotHandle) {
        slotHandle.pullSlotHandle();
        pullLeverSound.play();
        clearRowMatchesToDraw();
        int i = 0;
        for (AnimatedReel animatedReel : animatedReels) {
            if (!holdLightButtons.get(i).getLight().isActive()) {
                animatedReel.setEndReel(random.nextInt(sprites.length - 1));
                animatedReel.reinitialise();
                animatedReel.getReel().startSpinning();
                reelsSpinning++;
            }
            i++;
        }
    }

    protected void weAreOutOfTime() {
        playState = PlayStates.BONUS_LEVEL_ENDED;
        gameOver = true;
        mapTile.getLevel().setLevelCompleted();
        mapTile.getLevel().setScore(hud.getScore());
        playScreenPopUps.setLevelBonusSpritePositions();
        playScreenPopUps.getLevelBonusCompletedPopUp().showLevelPopUp(null);
    }

    protected void renderMainGameElements() {
        game.batch.begin();
        renderAnimatedReels();
        renderSlotHandle();
        renderScore();
        renderSpinHelper();
        game.batch.end();
        game.batch.begin();
        renderMatchedRows();
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        renderAnimatedReelsFlash();
        game.batch.end();
        renderLightButtons();
        renderWorld();
        renderRayHandler();
    }

    protected void renderAnimatedReels() {
        for (AnimatedReel animatedReel : animatedReels)
            if (!animatedReel.getReel().isReelTileDeleted())
                animatedReel.draw(game.batch);
    }

    protected void renderAnimatedReelsFlash() {
        for (AnimatedReel animatedReel : animatedReels)
            if (!animatedReel.getReel().isReelTileDeleted())
                if (animatedReel.getReel().getFlashState() == ReelTile.FlashState.FLASH_ON)
                    animatedReel.draw(shapeRenderer);
    }

    protected void renderMatchedRows() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        for (Array<Vector2> matchedRow : rowMacthesToDraw)
            if (matchedRow.size >= 2)
                for (int i = 0; i < matchedRow.size - 1; i++)
                    shapeRenderer.rectLine(matchedRow.get(i).x, matchedRow.get(i).y,
                            matchedRow.get(i + 1).x, matchedRow.get(i + 1).y,
                            2);
        shapeRenderer.end();
    }

    protected void renderSlotHandle() {
        for (SlotHandleSprite slotHandle : slotHandles)
            slotHandle.draw(game.batch);
    }

    protected void renderLightButtons() {
        game.batch.setProjectionMatrix(lightViewport.getCamera().combined);
        game.batch.begin();
        for (HoldLightButton lightButton : holdLightButtons)
            lightButton.getSprite().draw(game.batch);
        game.batch.end();
    }
 }