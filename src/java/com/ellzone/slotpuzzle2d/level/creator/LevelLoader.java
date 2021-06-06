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

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.card.Card;
import com.ellzone.slotpuzzle2d.level.hidden.HiddenPattern;
import com.ellzone.slotpuzzle2d.level.hidden.HiddenPlayingCard;
import com.ellzone.slotpuzzle2d.level.hidden.HiddenShape;
import com.ellzone.slotpuzzle2d.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReelHelper;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedFlashingEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
import com.ellzone.slotpuzzle2d.utils.assets.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.Random;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import static com.ellzone.slotpuzzle2d.level.creator.LevelCreator.MINI_SLOT_MACHINE_LEVEL_TYPE;
import static com.ellzone.slotpuzzle2d.level.creator.LevelCreator.PLAYING_CARD_LEVEL_TYPE;

public class LevelLoader {
    private final AnnotationAssetManager annotationAssetManager;
    private LevelDoor levelDoor;
    private MapTile mapTileLevel;
    private TiledMap tiledMapLevel;
    private AnimatedReelHelper animatedReelHelper;
    private Array<ReelTile> reelTiles;
    private Array<AnimatedReel> animatedReels;
    private GridSize levelGridSize;
 	private Array<Card> cards;
    private Array<Integer> hiddenPlayingCards;
    private LevelCallback stoppedSpinningCallback, stoppedFlashingCallback;
    private HiddenPattern hiddenPattern;
    private PuzzleGridTypeReelTile puzzleGridTypeReelTile;
    private int reelsFromLevel;

    public LevelLoader(
            AnnotationAssetManager annotationAssetManager,
            LevelDoor levelDoor,
            MapTile mapTileLevel,
            Array<AnimatedReel> animatedReels) {
        this.annotationAssetManager = annotationAssetManager;
        this.levelDoor = levelDoor;
        this.mapTileLevel = mapTileLevel;
        this.animatedReels = animatedReels;
        reelTiles = new Array<>();
    }

    public Array<AnimatedReel> createAnimatedReelsInLevel(GridSize levelGridSize) {
        this.levelGridSize = levelGridSize;
        puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        tiledMapLevel = getLevelAssets(annotationAssetManager);
        if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
            initialiseHiddenPlayingCards();
        else
            initialiseHiddenShape();
        initialiseReelTiles(animatedReels);
        if (!levelDoor.getLevelType().equals(MINI_SLOT_MACHINE_LEVEL_TYPE)) {
            reelTiles = checkLevel(reelTiles, levelGridSize);
            reelTiles = adjustForAnyLonelyReels(reelTiles, levelGridSize);
        }
        return animatedReels;
    }

    public void setStoppedSpinningCallback(LevelCallback callback) {
        this.stoppedSpinningCallback = callback;
    }

    public void setStoppedFlashingCallback(LevelCallback callback) {
        this.stoppedFlashingCallback = callback;
    }

    private TiledMap getLevelAssets(AnnotationAssetManager annotationAssetManager) {
        return annotationAssetManager.get("levels/level " + (this.levelDoor.getId() + 1) + " - 40x40.tmx");
    }

    private void initialiseReelTiles(Array<AnimatedReel> animatedReels) {
        int index = 0;
        for (AnimatedReel animatedReel : animatedReels)
            initialiseReelTile(animatedReel, index++);
    }

    private void initialiseReelTile(AnimatedReel animatedReel, int index) {
        ReelTile reelTile = animatedReel.getReel();
        reelTile.setEndReel(Random.getInstance().nextInt(reelTile.getNumberOfReelsInTexture()));
        reelTile.setIndex(index);
        reelTile.setDestinationX(reelTile.getX());
        reelTile.setDestinationY(reelTile.getY());
        reelTile.addListener(
            new ReelTileListener() {
                @Override
                public void actionPerformed(ReelTileEvent event, ReelTile source) {
                if (event instanceof ReelStoppedSpinningEvent)
                    processReelHasStoppedSpinning(source);

                if (event instanceof ReelStoppedFlashingEvent)
                    processReelHasStoppedFlashing(source);
                }
            }
        );
        reelTile.unDeleteReelTile();
        animatedReel.reinitialise();
        reelTile.setSpinning(false);
        reelTiles.add(reelTile);
    }

    private void initialiseHiddenPlayingCards() {
        TextureAtlas cardDeckAtlas = annotationAssetManager.get(AssetsAnnotation.CARDDECK);
        hiddenPattern = new HiddenPlayingCard(tiledMapLevel, cardDeckAtlas);
        HiddenPlayingCard hiddenPlayingCard = (HiddenPlayingCard) hiddenPattern;
        cards = hiddenPlayingCard.getCards();
        hiddenPlayingCards = hiddenPlayingCard.getHiddenPlayingCards();
    }

    private void initialiseHiddenShape() {
        hiddenPattern = new HiddenShape(tiledMapLevel);
    }

    private void processReelHasStoppedSpinning(ReelTile source) {
        stoppedSpinningCallback.onEvent(source);
    }

    private void processReelHasStoppedFlashing(ReelTile source) {
        stoppedFlashingCallback.onEvent(source);
    }

    private Array<ReelTile> checkLevel(Array<ReelTile> reelLevel, GridSize levelGridSize) {
        return puzzleGridTypeReelTile.checkGrid(reelLevel, levelGridSize);
    }

    private Array<ReelTile> adjustForAnyLonelyReels(
            Array<ReelTile> levelReel,
            GridSize levelGridSize) {
        return puzzleGridTypeReelTile.adjustForAnyLonelyReels(levelReel, levelGridSize);
    }

    public TupleValueIndex[][] populateMatchGrid(Array<ReelTile> reelLevel,
                                                 GridSize levelGridSize) {
        return puzzleGridTypeReelTile.populateMatchGrid(reelLevel, levelGridSize);
    }

    public Array<ReelTile> getReelTiles() {
        return reelTiles;
    }

    public Array<Card> getCards() {
        return cards;
    }

    public HiddenPattern getHiddenPattern() {return hiddenPattern; }
}
