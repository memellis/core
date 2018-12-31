package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedFlashingEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;

import aurelienribon.tweenengine.equations.Sine;

public class FlashSlots {
    public static final int FLASH_BATCH_POOL_SIZE = 3;

    private Timeline reelFlashSeq;
    private TweenManager tweenManager;
    private int mapWidth, mapHeight;
    private Array<ReelTile> reelTiles;
    private int numberOfReelsFlashing, numberOfReelsToDelete;
    private boolean startedFlashing, reelsAreFlashing, reelsAreDeleted;

    public FlashSlots(TweenManager tweenManager, int mapWidth, int mapHeight, Array<ReelTile> reelTiles) {
        this.tweenManager = tweenManager;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.reelTiles = reelTiles;
        initialiseFlashSlots();
    }

    private void initialiseFlashSlots() {
        reelsAreFlashing = false;
        startedFlashing = false;
        numberOfReelsFlashing = 0;
        numberOfReelsToDelete = 0;
    }

    public ReelTileGridValue[][] flashSlots(Array<ReelTile> reelTiles) {
        PuzzleGridTypeReelTile puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        ReelTileGridValue[][] puzzleGrid = puzzleGridTypeReelTile.populateMatchGrid(reelTiles,  mapWidth, mapHeight);

        Array<ReelTileGridValue> matchedSlots = puzzleGridTypeReelTile.matchGridSlots(puzzleGrid);
        Array<ReelTileGridValue> duplicateMatchedSlots = PuzzleGridTypeReelTile.findDuplicateMatches(matchedSlots);

        matchedSlots = PuzzleGridTypeReelTile.adjustMatchSlotDuplicates(matchedSlots, duplicateMatchedSlots);
        matchedSlots = PuzzleGridTypeReelTile.removeDuplicateMatches(duplicateMatchedSlots, matchedSlots);
        for (TupleValueIndex matchedSlot : matchedSlots) {
            reelTiles.get(matchedSlot.index).setScore(matchedSlot.value);
        }
        flashMatchedSlots(matchedSlots, puzzleGridTypeReelTile);
        return puzzleGrid;
    }

    private void flashMatchedSlots(Array<ReelTileGridValue> matchedSlots, PuzzleGridTypeReelTile puzzleGridTypeReelTile) {
        int matchSlotIndex, batchIndex, batchPosition;
        Array<ReelTileGridValue> matchSlotsBatch = new Array<>();
        float pushPause = 0.0f;
        matchSlotIndex = 0;
        numberOfReelsFlashing = matchedSlots.size;
        numberOfReelsToDelete = numberOfReelsFlashing;
        while (matchedSlots.size > 0) {
            startedFlashing = true;
            reelsAreFlashing = true;
            reelsAreDeleted = false;
            batchIndex = matchSlotIndex;
            for (int batchCount = batchIndex; batchCount < batchIndex + FLASH_BATCH_POOL_SIZE; batchCount++) {
                if (batchCount < matchedSlots.size) {
                    batchPosition = matchSlotsBatch.size;
                    matchSlotsBatch = puzzleGridTypeReelTile.depthFirstSearchAddToMatchSlotBatch(matchedSlots.get(0), matchSlotsBatch);

                    for (int deleteIndex = batchPosition; deleteIndex < matchSlotsBatch.size; deleteIndex++) {
                        matchedSlots.removeValue(matchSlotsBatch.get(deleteIndex), true);
                    }
                }
            }
            flashMatchedSlotsBatch(matchSlotsBatch, pushPause);
            pushPause += 2.0f;
            matchSlotsBatch.clear();
        }
    }

    private void flashMatchedSlotsBatch(Array<ReelTileGridValue> matchedSlots, float pushPause) {
        int index;
        for (int i = 0; i < matchedSlots.size; i++) {
            index = matchedSlots.get(i).getIndex();
            if (index  >= 0) {
                ReelTile reel = reelTiles.get(index);
                if (!reel.getFlashTween()) {
                    reel.setFlashMode(true);
                    Color flashColor = new Color(Color.WHITE);
                    reel.setFlashColor(flashColor);
                    initialiseReelFlash(reel, pushPause);
                }
            }
        }
    }

    private void initialiseReelFlash(ReelTile reel, float pushPause) {
        Array<Object> userData = new Array<>();
        reel.setFlashTween(true);
        reelFlashSeq = Timeline.createSequence();
        reelFlashSeq = reelFlashSeq.pushPause(pushPause);

        Color fromColor = new Color(Color.WHITE);
        fromColor.a = 1;
        Color toColor = new Color(Color.RED);
        toColor.a = 1;

        userData.add(reel);
        userData.add(reelFlashSeq);

        setUpFlashSequence(reel, userData, fromColor, toColor);
    }

    private void setUpFlashSequence(ReelTile reel, Array<Object> userData, Color fromColor, Color toColor) {
        reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.set(reel, ReelAccessor.FLASH_TINT)
                .target(fromColor.r, fromColor.g, fromColor.b)
                .ease(Sine.IN));
        reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.FLASH_TINT, 0.2f)
                .target(toColor.r, toColor.g, toColor.b)
                .ease(Sine.OUT)
                .repeatYoyo(17, 0));

        reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.set(reel, ReelAccessor.FLASH_TINT)
                .target(fromColor.r, fromColor.g, fromColor.b)
                .ease(Sine.IN));
        reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.FLASH_TINT, 0.05f)
                .target(toColor.r, toColor.g, toColor.b)
                .ease(Sine.OUT)
                .repeatYoyo(25, 0))
                .setCallback(reelFlashCallback)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .setUserData(userData)
                .start(tweenManager);
    }

    private TweenCallback reelFlashCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            switch (type) {
                case TweenCallback.COMPLETE:
                    delegateReelFlashCallback(source);
            }
        }
    };

    private void delegateReelFlashCallback(BaseTween<?> source) {
        @SuppressWarnings("unchecked")
        Array<Object> userData = (Array<Object>) source.getUserData();
        ReelTile reel = (ReelTile) userData.get(0);
        Timeline reelFlashSeq = (Timeline) userData.get(1);
        reelFlashSeq.kill();
        if (reel.getFlashTween()) {
            reel.setFlashOff();
            reel.setFlashTween(false);
            reel.processEvent(new ReelStoppedFlashingEvent());
        }
        numberOfReelsFlashing--;
    }

    public int getNumberOfReelsFlashing() {
        return numberOfReelsFlashing;
    }

    public int getNumberOfReelsToDelete() {
        return numberOfReelsToDelete;
    }

    public boolean areReelsDeleted() {
        return numberOfReelsToDelete == 0;
    }

    public boolean areReelsFlashing() {
        return numberOfReelsFlashing > 0;
    }

    public boolean areReelsStartedFlashing() {
        return startedFlashing;
    }

    public void setReelsAreFlashing(boolean reelsAreFlashing) {
        this.reelsAreFlashing = reelsAreFlashing;
    }

    public void deleteAReel() {
        numberOfReelsToDelete--;
    }
}
