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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.ScoreAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle2d.level.FlashSlots;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.hidden.HiddenPlayingCard;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.MiniSlotMachineLevelPrototypeWithLevelCreator;
import com.ellzone.slotpuzzle2d.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReelHelper;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelStoppedFlashingEvent;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTileEvent;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTileListener;
import com.ellzone.slotpuzzle2d.sprites.score.Score;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Random;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import aurelienribon.tweenengine.equations.Quad;

import static com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.MiniSlotMachineLevelPrototypeSimpleScenario.HEIGHT_KEY;
import static com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.MiniSlotMachineLevelPrototypeSimpleScenario.WIDTH_KEY;

public class LevelCreatorSimpleScenario {
    public static final String HIDDEN_PATTERN_LEVEL_TYPE = "HiddenPattern";
    public static final String HIDDEN_PATTERN_LAYER_NAME = "Hidden Pattern Object";
    public static final String PLAYING_CARD_LEVEL_TYPE = "PlayingCard";
    public static final String BONUS_LEVEL_TYPE = "BonusLevelType";
    public static final String REELS_LAYER_NAME = "Reels";
    private final FlashSlots flashSlots;

    private LevelDoor levelDoor;
    private TiledMap level;
    private HiddenPlayingCard hiddenPlayingCard;
    private Array<Integer> hiddenPlayingCards;
    private TweenManager tweenManager;
    private Timeline reelFlashSeq;
    private AnimatedReelHelper animatedReelHelper;
    private Array<ReelTile> reelTiles;
    private AnnotationAssetManager annotationAssetManager;
    private TextureAtlas carddeckAtlas;
    private PhysicsManagerCustomBodies physics;
    private GridSize levelGridSize;
    private int reelsSpinning;
    private int reelsFlashing;
    private PlayStates playState;
    private boolean win = false, gameOver = false;
    private Array<Score> scores;
    private boolean hitSinkBottom = false, dropReplacementReelBoxes = false;
    private Array<Body> reelBoxes;
    private Array<Body> reelBoxesCollided;
    public Array<Integer> replacementReelBoxes;
    private PuzzleGridTypeReelTile puzzleGridTypeReelTile;
    private int numberOfReelBoxesToReplace, numberOfReelBoxesToDelete;
    boolean matchedReels = false;
    boolean reelsAboveHaveFallen = false;
    int scenario1Reels[] = {4, 6, 2, 5, 0, 1, 3, 1, 0, 4, 3, 4};
    Array<TupleValueIndex> reelsToFall;
    private int mapWidth, mapHeight;

    public LevelCreatorSimpleScenario(
            LevelDoor levelDoor,
            TiledMap level,
            AnnotationAssetManager annotationAssetManager,
            TextureAtlas carddeckAtlas,
            TweenManager tweenManager,
            PhysicsManagerCustomBodies physics,
            GridSize levelGridSize,
            PlayStates playState) {
        this.levelDoor = levelDoor;
        this.level = level;
        this.tweenManager = tweenManager;
        this.annotationAssetManager = annotationAssetManager;
        this.carddeckAtlas = carddeckAtlas;
        this.physics = physics;
        this.levelGridSize = levelGridSize;
        this.playState = playState;
        puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        reelBoxes = new Array<Body>();
        replacementReelBoxes = new Array<Integer>();
        animatedReelHelper = new AnimatedReelHelper(
                annotationAssetManager,
                tweenManager,
                getNumberOfReelMapObjectsByName(level, REELS_LAYER_NAME, "Reel"));
        reelTiles = animatedReelHelper.getReelTiles();
        reelTiles = createLevel(levelDoor, level, reelTiles, levelGridSize);
        reelsSpinning = reelBoxes.size - 1;
        reelsFlashing = 0;
        hitSinkBottom = false;
        scores = new Array<Score>();
        reelsToFall = new Array<TupleValueIndex>();
        getMapProperties(level);
        flashSlots = new FlashSlots(tweenManager, levelGridSize, reelTiles);
    }

    private int getNumberOfReelMapObjectsByName(TiledMap level, String layerName, String name) {
        return getRectangleMapObjectsByName(level, layerName, name).size;
    }

    private Array<RectangleMapObject> getRectangleMapObjectsByName(TiledMap level, String layerName, String name) {
        Array<RectangleMapObject> rectangleMapObjectsByName = new Array<>();
        for (RectangleMapObject mapObject :
                level.getLayers().get(layerName).getObjects().
                        getByType(RectangleMapObject.class))
            if (mapObject.getName().equals(name))
                rectangleMapObjectsByName.add(mapObject);
        return rectangleMapObjectsByName;
    }

    private void getMapProperties(TiledMap level) {
        MapProperties mapProperties = level.getProperties();
        mapWidth = mapProperties.get(WIDTH_KEY, Integer.class);
        mapHeight = mapProperties.get(HEIGHT_KEY, Integer.class);
    }

    private Array<ReelTile> createLevel(
            LevelDoor levelDoor,
            TiledMap level,
            Array<ReelTile> reelTiles,
            GridSize levelGridSize) {
        if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
            hiddenPlayingCard = new HiddenPlayingCard(level, carddeckAtlas);

        reelTiles = populateLevel(level, reelTiles, levelGridSize);
        reelTiles = checkLevel(reelTiles, levelGridSize);
        reelTiles = adjustForAnyLonelyReels(reelTiles, levelGridSize);

        return reelTiles;
    }

    private Array<ReelTile> checkLevel(Array<ReelTile> reelLevel, GridSize levelGridSize) {
        ReelTileGridValue[][] grid = puzzleGridTypeReelTile.populateMatchGrid(
                reelLevel, levelGridSize);
        int arraySizeR = grid.length;
        int arraySizeC = grid[0].length;

        for (int r = 0; r < arraySizeR; r++)
            for (int c = 0; c < arraySizeC; c++) {
                if (grid[r][c] == null) {
                    Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "Found null grid tile. r=" + r + " c= " + c + ". I will therefore create a deleted entry for the tile.");
                    grid[r][c] = new ReelTileGridValue(r, c, -1, -1);
                }
            }
        return reelLevel;
    }

    Array<ReelTile> adjustForAnyLonelyReels(Array<ReelTile> levelReel, GridSize levelGridSize) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] grid = puzzleGridTypeReelTile.populateMatchGrid(
                levelReel, levelGridSize);
        Array<TupleValueIndex> lonelyTiles = puzzleGrid.getLonelyTiles(grid);
        for (TupleValueIndex lonelyTile : lonelyTiles)
            adjustAnyLonelyTileAtEdge(levelReel, grid, lonelyTile);
        return levelReel;
    }

    private void adjustAnyLonelyTileAtEdge(
            Array<ReelTile> levelReel,
            TupleValueIndex[][] grid,
            TupleValueIndex lonelyTile) {
        if (lonelyTile.r == 0) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r + 1][lonelyTile.c].index).getEndReel());
        } else if (lonelyTile.c == 0) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c + 1].index).getEndReel());
        } else if (lonelyTile.r == levelGridSize.getHeight() - 1) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r - 1][lonelyTile.c].index).getEndReel());
        } else if (lonelyTile.c == levelGridSize.getWidth() - 1) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c - 1].index).getEndReel());
        } else {
            adjustAnyLoneyAdjacentTile(levelReel, grid, lonelyTile);
        }
    }

    private void adjustAnyLoneyAdjacentTile(
            Array<ReelTile> levelReel,
            TupleValueIndex[][] grid,
            TupleValueIndex lonelyTile) {
        if ((grid[lonelyTile.r + 1][lonelyTile.c] != null) && (grid[lonelyTile.r + 1][lonelyTile.c].value != -1)) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r + 1][lonelyTile.c].index).getEndReel());
        } else if ((grid[lonelyTile.r - 1][lonelyTile.c] != null) && (grid[lonelyTile.r - 1][lonelyTile.c].value != -1)) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r - 1][lonelyTile.c].index).getEndReel());
        } else if ((grid[lonelyTile.r][lonelyTile.c + 1] != null) && (grid[lonelyTile.r][lonelyTile.c + 1].value != -1)) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c + 1].index).getEndReel());
        } else if ((grid[lonelyTile.r][lonelyTile.c - 1] != null) && (grid[lonelyTile.r][lonelyTile.c - 1].value != -1)) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c - 1].index).getEndReel());
        }
    }

    private Array<ReelTile> populateLevel(
            TiledMap level,
            Array<ReelTile> reelTiles,
            GridSize levelGridSize) {
        int index = 0;
        for (MapObject mapObject : getRectangleMapObjectsByName(level, REELS_LAYER_NAME, "Reel")) {
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            int c = getColumnFromLevel(mapRectangle.getX());
            int r = getRowFromLevel(mapRectangle.getY(), levelGridSize.getHeight());

            if ((r >= 0) &
                    (r <= levelGridSize.getHeight()) &
                    (c >= 0) &
                    (c <= levelGridSize.getWidth())) {
                addReel(mapRectangle, reelTiles, index);
                index++;
            } else
                Gdx.app.debug(
                        SlotPuzzleConstants.SLOT_PUZZLE,
                        "I don't respond to grid r=" + r + " c=" + c +
                                ". There it won't be added to the level! Sort it out in a level editor.");

        }
        return reelTiles;
    }

    public ReelTileGridValue[][] populateMatchGrid(
            Array<ReelTile> reelLevel,
            GridSize levelGridSize) {
        return puzzleGridTypeReelTile.populateMatchGrid(reelLevel, levelGridSize);
    }

    public void printMatchGrid(Array<ReelTile> reelLevel, GridSize levelGridSize) {
        PuzzleGridTypeReelTile.printGrid(populateMatchGrid(reelLevel, levelGridSize));
    }

    private int getRowFromLevel(float y, int levelHeight) {
        int row = (int) (y - PlayScreen.PUZZLE_GRID_START_Y) / SlotPuzzleConstants.TILE_HEIGHT;
        row = levelHeight - 1 - row;
        return row;
    }

    private int getColumnFromLevel(float x) {
        int column = (int) (x  - PlayScreen.PUZZLE_GRID_START_X) / SlotPuzzleConstants.TILE_WIDTH;
        return column;
    }

    private void addReel(Rectangle mapRectangle, Array<ReelTile> reelTiles, int index) {
        ReelTile reelTile = setUpAddedReel(mapRectangle, reelTiles, index);
        setUpRelatedReelTileBody(reelTile);
    }

    private void setUpRelatedReelTileBody(ReelTile reelTile) {
        Body reelTileBody = physics.createBoxBody(BodyDef.BodyType.DynamicBody,
                reelTile.getX() + 20,
                reelTile.getY() + 360,
                19,
                19,
                true);
        reelTileBody.setUserData(reelTile);
        reelBoxes.add(reelTileBody);
    }

    private ReelTile setUpAddedReel(Rectangle mapRectangle, Array<ReelTile> reelTiles, int index) {
        ReelTile reelTile = reelTiles.get(index);
        reelTile.setX(mapRectangle.getX());
        reelTile.setY(mapRectangle.getY());
        reelTile.setDestinationX(mapRectangle.getX());
        reelTile.setDestinationY(mapRectangle.getY());
        reelTile.setIndex(index);
        reelTile.setSx(0);
        setUpReelTileListener(reelTile);
        return reelTile;
    }

    private void setUpReelTileListener(ReelTile reelTile) {
        reelTile.addListener(new ReelTileListener() {
            @Override
            public void actionPerformed(ReelTileEvent event, ReelTile source) {
                if (event instanceof ReelStoppedSpinningEvent)
                    actionReelStoppedSpinning(event, source);

                if ((event instanceof ReelStoppedFlashingEvent))
                    actionReelStoppedFlashing(event, source);
            }
        });
    }

    public void setPlayState(PlayStates playState) {
        this.playState = playState;
    }

    public PlayStates getPlayState() {
        return this.playState;
    }

    private void actionReelStoppedSpinning(ReelTileEvent event, ReelTile source) {
        source.stopSpinningSound();
        reelsSpinning--;
        if ((playState == PlayStates.INTRO_SPINNING) |
            (playState == PlayStates.REELS_SPINNING) |
            (playState == PlayStates.PLAYING))
            allReelsHaveStoppedSpinning();
    }

    private void allReelsHaveStoppedSpinning() {
        if ((reelsSpinning <= -1) & (hitSinkBottom)) {
            if (levelDoor.getLevelType().equals(HIDDEN_PATTERN_LEVEL_TYPE)) {
                if (testForHiddenPatternRevealed(reelTiles, levelGridSize))
                    iWonTheLevel();
            }
            if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE)) {
                if (testForHiddenPlayingCardsRevealed(reelTiles, levelGridSize))
                    iWonTheLevel();
            }
            if (levelDoor.getLevelType().equals(BONUS_LEVEL_TYPE)) {
                if (testForJackpot(reelTiles, levelGridSize))
                    iWonABonus();
            }
        } else {
            if (testForJackpot(reelTiles, levelGridSize))
                iWonABonus();
        }
    }

    private void actionReelStoppedFlashing(ReelTileEvent event, ReelTile reelTile) {
        if ((playState == PlayStates.INTRO_FLASHING) | (playState != PlayStates.REELS_FLASHING)) {
            if (flashSlots.getNumberOfReelsFlashing() <= 0) {
                // When do I need to testForAnyLonelyReels?
                //
                /*if (testForAnyLonelyReels(reelTiles, this.levelWidth, this.levelHeight)) {
                    win = false;
                    if (Hud.getLives() > 0) {
                        setPlayState(PlayScreen.PlayStates.LEVEL_LOST);
                    } else {
                        gameOver = true;
                    }
                }*/
            }
        }
        reelScoreAnimation(reelTile);
        deleteReelAnimation(reelTile);
    }

    private ReelTileGridValue[][] flashSlots(Array<ReelTile> reelTiles, GridSize levelGridSize) {
        if (playState == PlayStates.INTRO_SPINNING)
            playState = PlayStates.INTRO_FLASHING;

        if (playState == PlayStates.PLAYING)
            playState = PlayStates.REELS_FLASHING;

        ReelTileGridValue[][] puzzleGrid = puzzleGridTypeReelTile.populateMatchGrid(
                reelTiles,
                levelGridSize);

        Array<ReelTileGridValue> matchedSlots = puzzleGridTypeReelTile.matchGridSlots(puzzleGrid);
        Array<ReelTileGridValue> duplicateMatchedSlots = PuzzleGridTypeReelTile.findDuplicateMatches(matchedSlots);

        matchedSlots = PuzzleGridTypeReelTile.adjustMatchSlotDuplicates(matchedSlots, duplicateMatchedSlots);

        if (matchedSlots.size > 0)
            setUpToFlashMatchedReels(reelTiles, matchedSlots, duplicateMatchedSlots);
        else
            matchedReels = false;
        return puzzleGrid;
    }

    private void setUpToFlashMatchedReels(Array<ReelTile> reelTiles, Array<ReelTileGridValue> matchedSlots, Array<ReelTileGridValue> duplicateMatchedSlots) {
        matchedSlots.reverse();
        numberOfReelBoxesToReplace = matchedSlots.size - 1 - duplicateMatchedSlots.size / 2;
        numberOfReelBoxesToDelete = matchedSlots.size - 1 - duplicateMatchedSlots.size / 2;

        for (TupleValueIndex matchedSlot : matchedSlots)
            reelTiles.get(matchedSlot.index).setScore(matchedSlot.value);

        flashSlots.flashMatchedSlotsForLevelCreator(matchedSlots, puzzleGridTypeReelTile);
        matchedReels = true;
    }

    private boolean testForHiddenPatternRevealed(
            Array<ReelTile> levelReel,
            GridSize levelGridSize) {
        TupleValueIndex[][] matchGrid = flashSlots(levelReel, levelGridSize);
        return hiddenPatternRevealed(matchGrid);
    }

    private boolean testForHiddenPlayingCardsRevealed(
            Array<ReelTile> levelReel,
            GridSize levelGridSize) {
        TupleValueIndex[][] matchGrid = flashSlots(levelReel, levelGridSize);
        return hiddenPlayingCardsRevealed(matchGrid);
    }

    private boolean hiddenPlayingCardsRevealed(TupleValueIndex[][] grid) {
        boolean hiddenPlayingCardsRevealed = true;
        for (Integer hiddenPlayingCard : hiddenPlayingCards) {
            MapObject mapObject = level.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).get(hiddenPlayingCard.intValue());
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            for (int ro = (int) (mapRectangle.getX()); ro < (int) (mapRectangle.getX() + mapRectangle.getWidth()); ro += SlotPuzzleConstants.TILE_WIDTH) {
                for (int co = (int) (mapRectangle.getY()); co < (int) (mapRectangle.getY() + mapRectangle.getHeight()); co += SlotPuzzleConstants.TILE_HEIGHT) {
                    int c = getColumnFromLevel(ro);
                    int r = getRowFromLevel(co, levelGridSize.getHeight());

                    if ((r >= 0) &
                            (r <= levelGridSize.getHeight()) &
                            (c >= 0) &
                            (c <= levelGridSize.getWidth())) {
                        if (grid[r][c] != null)
                            if (!reelTiles.get(grid[r][c].getIndex()).isReelTileDeleted())
                                hiddenPlayingCardsRevealed = false;
                    } else
                        Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r=" + r + "c=" + c);
                }
            }
        }
        return hiddenPlayingCardsRevealed;
    }

    private boolean hiddenPatternRevealed(TupleValueIndex[][] grid) {
        boolean hiddenPattern = true;
        for (MapObject mapObject : level.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            int c = getColumnFromLevel(mapRectangle.getX());
            int r = getRowFromLevel(mapRectangle.getY(), levelGridSize.getHeight());
            if ((r >= 0) &
                    (r <= levelGridSize.getHeight()) &
                    (c >= 0) &
                    (c <= levelGridSize.getWidth())) {
                if (grid[r][c] != null)
                    if (!reelTiles.get(grid[r][c].getIndex()).isReelTileDeleted())
                        hiddenPattern = false;
            } else
                Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r=" + r + "c=" + c);
        }
        return hiddenPattern;
    }

    private boolean testForJackpot(Array<ReelTile> levelReel, GridSize levelGridSize) {
        TupleValueIndex[][] matchGrid = flashSlots(levelReel, levelGridSize);
        return true;
    }

    private void iWonTheLevel() {
        gameOver = true;
        win = true;
        playState = PlayStates.WON_LEVEL;
        //mapTile.getLevel().setLevelCompleted();
        //mapTile.getLevel().setScore(Hud.getScore());
    }

    private void iWonABonus() {
        System.out.println("iWonABonus!");
    }

    public Array<ReelTile> getReelTiles() {
        return this.reelTiles;
    }

    public Array<Body> getReelBoxes() {
        return this.reelBoxes;
    }

    public Array<AnimatedReel> getAnimatedReels() {
        return animatedReelHelper.getAnimatedReels();
    }

    private void reelScoreAnimation(ReelTile source) {
        Score score = new Score(source.getX(), source.getY(), (source.getEndReel() + 1) * source.getScore());
        scores.add(score);
        Timeline.createSequence()
                .beginParallel()
                .push(SlotPuzzleTween.to(score, ScoreAccessor.POS_XY, 2.0f).targetRelative(Random.getInstance().nextInt(20), Random.getInstance().nextInt(160)).ease(Quad.IN))
                .push(SlotPuzzleTween.to(score, ScoreAccessor.SCALE_XY, 2.0f).target(2.0f, 2.0f).ease(Quad.IN))
                .end()
                .setUserData(score)
                .setCallback(deleteScoreCallback)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(tweenManager);
    }

    private TweenCallback deleteScoreCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            switch (type) {
                case TweenCallback.COMPLETE:
                    Score score = (Score) source.getUserData();
                    scores.removeValue(score, false);

            }
        }
    };

    private void deleteReelAnimation(ReelTile source) {
        Timeline.createSequence()
                .beginParallel()
                .push(SlotPuzzleTween.to(source, SpriteAccessor.SCALE_XY, 0.3f).target(6, 6).ease(Quad.IN))
                .push(SlotPuzzleTween.to(source, SpriteAccessor.OPACITY, 0.3f).target(0).ease(Quad.IN))
                .end()
                .setUserData(source)
                .setCallback(deleteReelCallback)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .start(tweenManager);
    }

    private TweenCallback deleteReelCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            switch (type) {
                case TweenCallback.COMPLETE:
                    ReelTile reel = (ReelTile) source.getUserData();
                    int reelTilesIndex = reelTiles.indexOf(reel, true);
//                    hud.addScore((reel.getEndReel() + 1) * reel.getScore());
                    //playSound(reelStoppedSound);
                    //playSound(chaChingSound);

                    reel.deleteReelTile();
                    physics.deleteBody(reelBoxes.get(reelTilesIndex));
                    replacementReelBoxes.add(reelTilesIndex);
                    flashSlots.setNumberOfReelsFlashing(flashSlots.getNumberOfReelsFlashing() - 1);
                    numberOfReelBoxesToDelete--;
                    if (numberOfReelBoxesToDelete < 0) {
                        if ((playState == PlayStates.INTRO_FLASHING) | (playState == PlayStates.REELS_FLASHING)) {
                            findReelsAboveMe();
                            matchedReels = isThereMatchedSlots();
                            if (!matchedReels) {
                                if (replacementReelBoxes.size == 0)
                                    playState = PlayStates.PLAYING;
                                else
                                    if (reelsToFall.size == 0)
                                        createReplacementReelBoxes();
                            }
                        }
                    }

                    if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
                        testPlayingCardLevelWon(levelGridSize);
                    else
                        if (levelDoor.getLevelType().equals(HIDDEN_PATTERN_LEVEL_TYPE))
                            testForHiddenPlatternLevelWon(levelGridSize);
            }
        }
    };

    private boolean isThereMatchedSlots() {
        ReelTileGridValue[][] puzzleGrid = puzzleGridTypeReelTile.populateMatchGrid(
                reelTiles, levelGridSize);
        Array<ReelTileGridValue> matchedSlots = puzzleGridTypeReelTile.matchGridSlots(puzzleGrid);
        Array<ReelTileGridValue> duplicateMatchedSlots =
                PuzzleGridTypeReelTile.findDuplicateMatches(matchedSlots);

        matchedSlots = PuzzleGridTypeReelTile.adjustMatchSlotDuplicates(
                matchedSlots, duplicateMatchedSlots);
        if (matchedSlots.size > 0) {
            matchedSlots.reverse();
            numberOfReelBoxesToReplace = matchedSlots.size - 1 - duplicateMatchedSlots.size / 2;
            numberOfReelBoxesToDelete = matchedSlots.size - 1 - duplicateMatchedSlots.size / 2;

            for (TupleValueIndex matchedSlot : matchedSlots)
                reelTiles.get(matchedSlot.index).setScore(matchedSlot.value);

            flashSlots.flashMatchedSlotsForLevelCreator(matchedSlots, puzzleGridTypeReelTile);
            return true;
        }
        return false;
    }

    private void testPlayingCardLevelWon(GridSize levelGridSize) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] matchGrid = puzzleGridTypeReelTile.populateMatchGrid(
                reelTiles, levelGridSize);
        puzzleGrid.matchGridSlots(matchGrid);
        if (hiddenPlayingCardsRevealed(matchGrid))
            iWonTheLevel();
    }

    private void testForHiddenPlatternLevelWon(GridSize levelGridSize) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] matchGrid = puzzleGridTypeReelTile.populateMatchGrid(
                reelTiles, levelGridSize);
        puzzleGrid.matchGridSlots(matchGrid);
        if (hiddenPatternRevealed(matchGrid))
            iWonTheLevel();
    }

    public void update(float dt) {
       if ((playState == PlayStates.INTRO_FLASHING) | (playState == PlayStates.REELS_FLASHING)) {
           System.out.println("numberOfReelsFlashing="+flashSlots.getNumberOfReelsFlashing());
            if ((reelsAboveHaveFallen) & (reelsToFall.size==0) & (flashSlots.getNumberOfReelsFlashing() == 0)) {
                ReelTileGridValue[][] puzzleGrid = puzzleGridTypeReelTile.populateMatchGrid(
                        reelTiles, levelGridSize);
                Array<ReelTileGridValue> matchedSlots = puzzleGridTypeReelTile.matchGridSlots(puzzleGrid);
                Array<ReelTileGridValue> duplicateMatchedSlots = PuzzleGridTypeReelTile.findDuplicateMatches(matchedSlots);

                matchedReels = isThereMatchedSlots();
                if (!matchedReels) {
                    if (replacementReelBoxes.size == 0)
                        playState = PlayStates.PLAYING;
                    else
                        if (reelsToFall.size == 0)
                            createReplacementReelBoxes();
                    reelsAboveHaveFallen = false;
                }
            } else {
                if ((playState == PlayStates.INTRO_FLASHING) |
                    (playState == PlayStates.REELS_FLASHING) | playState == PlayStates.PLAYING) {
                    if ((numberOfReelBoxesToDelete < 0) &
                        (!matchedReels) &
                        (reelsToFall.size == 0) &
                        (replacementReelBoxes.size > 0) &
                        (flashSlots.getNumberOfReelsFlashing() == 0)) {
                        matchedReels = isThereMatchedSlots();
                        if (!matchedReels) {
                            if ((numberOfReelBoxesToDelete < 0) &
                                (reelsToFall.size == 0) &
                                (replacementReelBoxes.size > 0) &
                                (reelsToFall.size ==0))
                                createReplacementReelBoxes();

                        }
                    } else {
                        if ((numberOfReelBoxesToDelete < 0) &
                            (!matchedReels) &
                            (reelsToFall.size == 0) &
                            (replacementReelBoxes.size == 0)) {
                            playState = PlayStates.PLAYING;
                        }
                    }
                }
            }
        }
        if ((playState == PlayStates.INTRO_SPINNING) | (playState == PlayStates.REELS_SPINNING))
           if ((numberOfReelBoxesToDelete < 0) &
               (reelsToFall.size == 0) &
               (replacementReelBoxes.size == 0))
               playState = PlayStates.PLAYING;

        animatedReelHelper.update(dt);
        physics.update(dt);
        updateReelBoxes();
    }

    private void createReplacementReelBoxes() {
        for (Integer reelBoxIndex : replacementReelBoxes) {
            ReelTile reelTile = reelTiles.get(reelBoxIndex.intValue());
            reelTile.unDeleteReelTile();
            reelTile.setScale(1.0f);
            Color reelTileColor = reelTile.getColor();
            reelTileColor.set(reelTileColor.r, reelTileColor.g, reelTileColor.b, 1.0f);
            reelTile.setColor(reelTileColor);
            reelTile.setEndReel(Random.getInstance().nextInt(animatedReelHelper.getReelSprites().getSprites().length - 1));
            reelTile.resetReel();
            Body reelTileBody = physics.createBoxBody(BodyDef.BodyType.DynamicBody,
                    reelTile.getDestinationX() + 20,
                    reelTile.getDestinationY() + 360,
                    19,
                    19,
                    true);
            reelTileBody.setUserData(reelTile);
            reelBoxes.set(reelBoxIndex, reelTileBody);
            AnimatedReel animatedReel = animatedReelHelper.getAnimatedReels().get(reelBoxIndex);
            animatedReel.reinitialise();
        }
        reelsSpinning = replacementReelBoxes.size - 1;
        MiniSlotMachineLevelPrototypeWithLevelCreator.numberOfReelsToHitSinkBottom = replacementReelBoxes.size;
        replacementReelBoxes.removeRange(0, replacementReelBoxes.size - 1);
        dropReplacementReelBoxes = false;
        if (playState == PlayStates.INTRO_FLASHING)
            playState = PlayStates.INTRO_SPINNING;

        if (playState == PlayStates.REELS_FLASHING)
            playState = PlayStates.REELS_SPINNING;
    }

    public void setHitSinkBottom(boolean hitSinkBottom) {
        this.hitSinkBottom = hitSinkBottom;
    }

    public void setNumberOfReelsSpinning(int numberOfReelsSpinning) {
        this.reelsSpinning = numberOfReelsSpinning;
    }

    public int getNumberOfReelsSpinning() {
        return reelsSpinning;
    }

    public Array<Score> getScores() {
        return scores;
    }

    public int findReel(int destinationX, int destinationY) {
        int findReelIndex = 0;
        while (findReelIndex < reelTiles.size) {
            if ((reelTiles.get(findReelIndex).getDestinationX() == destinationX) &
                (reelTiles.get(findReelIndex).getDestinationY() == destinationY))
                return findReelIndex;
            findReelIndex++;
        }
        return -1;
    }

    private void findReelsAboveMe() {
        TupleValueIndex[][] matchGrid = puzzleGridTypeReelTile.populateMatchGrid(
                reelTiles, levelGridSize);
        TupleValueIndex[] reelsAboveMe = null;
        reelsToFall = new Array<TupleValueIndex>();
        PuzzleGridType puzzleGridType = new PuzzleGridType();
        for (Integer replacementReelBox : replacementReelBoxes) {
            reelsAboveMe = puzzleGridType.getReelsAboveMe(matchGrid,
                    PuzzleGridTypeReelTile.getRowFromLevel(
                            reelTiles.get(replacementReelBox).getDestinationY(),
                            levelGridSize.getHeight()),
                    PuzzleGridTypeReelTile.getColumnFromLevel(
                            reelTiles.get(replacementReelBox).getDestinationX()));
            for (int rami = 0; rami < reelsAboveMe.length; rami++)
                if (!reelsToFall.contains(reelsAboveMe[rami], true))
                    reelsToFall.add(reelsAboveMe[rami]);
        }
    }

    public Array<TupleValueIndex> getReelsToFall() {
        return reelsToFall;
    }

    public void setReelsToFall(Array<TupleValueIndex> reelsToFall) {
        this.reelsToFall = reelsToFall;
    }

    public void setReelsAboveHaveFallen(boolean reelsAboveHaveFallen) {
        this.reelsAboveHaveFallen = reelsAboveHaveFallen;
    }

    private void updateReelBoxes() {
        if ((reelBoxesCollided != null) && (reelBoxesCollided.size > 0)) {
            for (Body reelBoxCollided : reelBoxesCollided) {
                ReelTile reelTile = (ReelTile) reelBoxCollided.getUserData();
                reelBoxCollided.setTransform(
                        (reelTile.getDestinationX() + 20) / 100,
                        (reelTile.getDestinationY() + 20) / 100, 0);
            }
            reelBoxesCollided.removeRange(0, reelBoxesCollided.size - 1);
        }
    }
}
