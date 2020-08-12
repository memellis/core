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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.ScoreAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayState;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStateMachine;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle2d.level.FlashSlots;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.creator.utils.FilterReelBoxes;
import com.ellzone.slotpuzzle2d.level.hidden.HiddenPlayingCard;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.physics.contact.AnimatedReelsManager;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.AnimatedReelsMatrixCreator;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.SlotPuzzleMatrices;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedFlashingEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
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

public class LevelCreatorSimple {
    public static final String HIDDEN_PATTERN_LEVEL_TYPE = "HiddenPattern";
    public static final String HIDDEN_PATTERN_LAYER_NAME = "Hidden Pattern Object";
    public static final String PLAYING_CARD_LEVEL_TYPE = "PlayingCard";
    public static final String BONUS_LEVEL_TYPE = "BonusLevelType";
    public static final String REELS_LAYER_NAME = "Reels";
    public static final int OFF_PLAY_SCREEN_OFFSET = 420;

    private LevelDoor levelDoor;
    private TiledMap level;
    private HiddenPlayingCard hiddenPlayingCard;
    private Array<Integer> hiddenPlayingCards;
    private TweenManager tweenManager;
    private Timeline reelFlashSeq;
    private Array<AnimatedReel> animatedReels;
    private Array<ReelTile> reelTiles;
    private AnnotationAssetManager annotationAssetManager;
    private TextureAtlas carddeckAtlas;
    private PhysicsManagerCustomBodies physics;
    private int levelWidth,
            levelHeight,
            reelsSpinning,
            reelsFlashing;
    private PlayStates playState;
    private boolean win = false, gameOver = false;
    private Array<Score> scores;
    private Array<Body> reelBoxes;
    private Array<Body> reelBoxesCollided;
    public Array<Integer> replacementReelBoxes;
    private Array<Integer> reelBoxesToDelete;
    private PuzzleGridTypeReelTile puzzleGridTypeReelTile;
    private int numberOfReelBoxesToReplace, numberOfReelBoxesToDelete;
    boolean matchedReels = false;
    boolean reelsAboveHaveFallen = false;
    private Array<TupleValueIndex> reelsToFall;
    private int mapWidth, mapHeight;
    private FlashSlots flashSlots;
    private boolean reelBoxesToBeCreated = false;
    private boolean enableCreateReplacementReelsBoxesFeature = false;
    private PlayStateMachine playStateMachine;
    private AnimatedReelsManager animatedReelsManager;
    private int reelBoxFalling = -1;
    private float reelBoxFallingY = -1.0f;
    private int reelBoxFallingNotDetectorCount = 0;
    private boolean usePreparedSlotPuzzleMatrix = false;
    private boolean usePreparedSlotPuzzleMatrixReelsToFall = false;
    private AnimatedReelsMatrixCreator animatedReelsMatrixCreator;
    private Array<Integer> myReelsToFall = new Array<>();
    private Array<Integer> myReelsToFallEndReel = new Array<>();
    private int myReelsToFallIndex;
    private Hud hud;
    private boolean itisTimeForRandomReplacementReelBox = false;

    public LevelCreatorSimple (
            LevelDoor levelDoor,
            Array<AnimatedReel> animatedReels,
            Array<ReelTile> reelTiles,
            TiledMap level,
            AnnotationAssetManager annotationAssetManager,
            TextureAtlas carddeckAtlas,
            TweenManager tweenManager,
            PhysicsManagerCustomBodies physics,
            int levelWidth,
            int levelHeight,
            PlayStates playState) {
        this.levelDoor = levelDoor;
        this.animatedReels = animatedReels;
        this.reelTiles = reelTiles;
        this.level = level;
        this.tweenManager = tweenManager;
        this.annotationAssetManager = annotationAssetManager;
        this.carddeckAtlas = carddeckAtlas;
        this.physics = physics;
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;
        this.playState = playState;
        initialise(levelDoor, reelTiles, level, tweenManager, levelWidth, levelHeight);
    }

    public LevelCreatorSimple (
            LevelDoor levelDoor,
            Array<AnimatedReel> animatedReels,
            Array<ReelTile> reelTiles,
            TiledMap level,
            AnnotationAssetManager annotationAssetManager,
            TextureAtlas carddeckAtlas,
            TweenManager tweenManager,
            PhysicsManagerCustomBodies physics,
            int levelWidth,
            int levelHeight,
            PlayStateMachine playStateMachine,
            Hud hud) {
        this.levelDoor = levelDoor;
        this.animatedReels = animatedReels;
        this.reelTiles = reelTiles;
        this.level = level;
        this.tweenManager = tweenManager;
        this.annotationAssetManager = annotationAssetManager;
        this.carddeckAtlas = carddeckAtlas;
        this.physics = physics;
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;
        this.playStateMachine = playStateMachine;
        this.hud = hud;
        myReelsToFall.addAll(9, 10, 11, 12);
        myReelsToFallEndReel.addAll(7,7,7,7);
        initialise(levelDoor, reelTiles, level, tweenManager, levelWidth, levelHeight);
    }

    private void initialise(LevelDoor levelDoor, Array<ReelTile> reelTiles, TiledMap level, TweenManager tweenManager, int levelWidth, int levelHeight) {
        puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        reelBoxes = new Array<>();
        replacementReelBoxes = new Array<>();
        reelBoxesToDelete = new Array<>();
        reelTiles = createLevel(levelDoor, level, reelTiles, levelWidth, levelHeight);
        if (usePreparedSlotPuzzleMatrix)
            setUpLevelUsingSlotPuzzleMatrix();
        reelsSpinning = reelBoxes.size - 1;
        reelsFlashing = 0;
        scores = new Array<Score>();
        reelsToFall = new Array<TupleValueIndex>();
        getMapProperties(level);
        flashSlots = new FlashSlots(tweenManager, mapWidth, mapHeight, reelTiles);
    }

    private void setUpLevelUsingSlotPuzzleMatrix() {
        animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
        animatedReels = animatedReelsMatrixCreator.updateAnimatedReelsFromSlotPuzzleMatrix(
                SlotPuzzleMatrices.createMatrixWithANearlyFullMatrix(),
                animatedReels,
                replacementReelBoxes);
        reelBoxes = animatedReelsMatrixCreator.updateBoxBodiesFromAnimatedReels(
                animatedReels,
                reelBoxes);
        printMatchGrid(reelTiles, 12, 9);
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
            int levelWidth,
            int levelHeight) {
        if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
            hiddenPlayingCard = new HiddenPlayingCard(level, carddeckAtlas);

        reelTiles = populateLevel(level, reelTiles, levelWidth, levelHeight);
        reelTiles = checkLevel(reelTiles, levelWidth, levelHeight);
        reelTiles = adjustForAnyLonelyReels(reelTiles, levelWidth, levelHeight);
        return reelTiles;
    }

    private Array<ReelTile> checkLevel(Array<ReelTile> reelLevel, int levelWidth, int levelHeight) {
        ReelTileGridValue[][] grid = puzzleGridTypeReelTile.populateMatchGrid(reelLevel, levelWidth, levelHeight);
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

    Array<ReelTile> adjustForAnyLonelyReels(Array<ReelTile> levelReel, int levelWidth, int levelHeight) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] grid = puzzleGridTypeReelTile.populateMatchGrid(levelReel, levelWidth, levelHeight);
        Array<TupleValueIndex> lonelyTiles = puzzleGrid.getLonelyTiles(grid);
        for (TupleValueIndex lonelyTile : lonelyTiles)
            adjustAnyLonelyTileAtEdge(levelReel, grid, lonelyTile);
        return levelReel;
    }

    private void adjustAnyLonelyTileAtEdge(Array<ReelTile> levelReel, TupleValueIndex[][] grid, TupleValueIndex lonelyTile) {
        if (lonelyTile.r == 0) {
            adjustAdjacentTileEndReel(
                    levelReel.get(grid[lonelyTile.r][lonelyTile.c].index),
                    levelReel,
                    grid,
                    lonelyTile.r + 1,
                    lonelyTile.c);
        } else if (lonelyTile.c == 0) {
            adjustAdjacentTileEndReel(
                    levelReel.get(grid[lonelyTile.r][lonelyTile.c].index),
                    levelReel,
                    grid,
                    lonelyTile.r,
                    lonelyTile.c + 1);
        } else if (lonelyTile.r == levelHeight - 1) {
            adjustAdjacentTileEndReel(
                    levelReel.get(grid[lonelyTile.r][lonelyTile.c].index),
                    levelReel,
                    grid,
                    lonelyTile.r - 1,
                    lonelyTile.c);
        } else if (lonelyTile.c == levelWidth - 1) {
            adjustAdjacentTileEndReel(
                    levelReel.get(grid[lonelyTile.r][lonelyTile.c].index),
                    levelReel,
                    grid,
                    lonelyTile.r,
                    lonelyTile.c - 1);
        } else {
            adjustAnyLoneyAdjacentTile(levelReel, grid, lonelyTile);
        }
    }

    private void adjustAdjacentTileEndReel(
            ReelTile currentReelTile,
            Array<ReelTile> reelTiles,
            TupleValueIndex[][] grid,
            int adjacentR,
            int adjacentC) {
        if (grid[adjacentR][adjacentC] == null)
            return;
        currentReelTile.setEndReel(reelTiles.get(grid[adjacentR][adjacentC].index).getEndReel());
    }

    private void adjustAnyLoneyAdjacentTile(Array<ReelTile> levelReel, TupleValueIndex[][] grid, TupleValueIndex lonelyTile) {
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

    private Array<ReelTile> populateLevel(TiledMap level, Array<ReelTile> reelTiles, int levelWidth, int levelHeight) {
        int index = 0;
        for (MapObject mapObject : getRectangleMapObjectsByName(level, REELS_LAYER_NAME, "Reel")) {
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            int c = getColumnFromLevel(mapRectangle.getX());
            int r = getRowFromLevel(mapRectangle.getY(), levelHeight);

            if ((r >= 0) & (r <= levelHeight) & (c >= 0) & (c <= levelWidth)) {
                addReel(mapRectangle, reelTiles, index);
                index++;
            } else
                Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to grid r=" + r + " c=" + c + ". There it won't be added to the level! Sort it out in a level editor.");

        }
        return reelTiles;
    }

    public ReelTileGridValue[][] populateMatchGrid(Array<ReelTile> reelLevel, int gridWidth, int gridHeight) {
        return puzzleGridTypeReelTile.populateMatchGrid(reelLevel, gridWidth, gridHeight);
    }

    public void printMatchGrid(Array<ReelTile> reelLevel, int gridWidth, int gridHeight) {
        PuzzleGridTypeReelTile.printGrid(populateMatchGrid(reelLevel, gridWidth, gridHeight));
    }

    private int getRowFromLevel(float y, int levelHeight) {
        int row = (int) (y - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
        row = levelHeight - 1 - row;
        return row;
    }

    private int getColumnFromLevel(float x) {
        int column = (int) (x  - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
        return column;
    }

    private void addReel(Rectangle mapRectangle, Array<ReelTile> reelTiles, int index) {
        ReelTile reelTile = setUpAddedReel(mapRectangle, reelTiles, index);
        setUpRelatedReelTileBody(reelTile);
    }

    private void setUpRelatedReelTileBody(ReelTile reelTile) {
        Body reelTileBody = physics.createBoxBody(
                 BodyDef.BodyType.DynamicBody,
                reelTile.getDestinationX(),
                reelTile.getDestinationY() + OFF_PLAY_SCREEN_OFFSET,
                19,
                19,
                true);
        reelTileBody.setUserData(animatedReels.get(reelTile.getIndex()));
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
        return playState;
    }

    private void actionReelStoppedSpinning(ReelTileEvent event, ReelTile source) {
        source.stopSpinningSound();
        reelsSpinning--;
        if (reelsSpinning < 1)
            if ((playStateMachine.getStateMachine().getCurrentState() == PlayState.INTRO_SPINNING_SEQUENCE) |
                (playStateMachine.getStateMachine().getCurrentState() == PlayState.FLASH) |
                (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY))
                allReelsHaveStoppedSpinning();
    }

    public void allReelsHaveStoppedSpinning() {
        if (levelDoor.getLevelType().equals(HIDDEN_PATTERN_LEVEL_TYPE)) {
            if (testForHiddenPatternRevealed(reelTiles, levelWidth, levelHeight))
                iWonTheLevel();
        }
        if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE)) {
            if (testForHiddenPlayingCardsRevealed(reelTiles, levelWidth, levelHeight))
                iWonTheLevel();
        }
        if (levelDoor.getLevelType().equals(BONUS_LEVEL_TYPE)) {
            if (testForJackpot(reelTiles, levelWidth, levelHeight))
                iWonABonus();
        }
    }

    private void actionReelStoppedFlashing(ReelTileEvent event, ReelTile reelTile) {
        if (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY) {
            System.out.println("Reel stopped flashing Copy logic from PlayScreen");
        }
        reelScoreAnimation(reelTile);
        deleteReelAnimation(reelTile);
    }

    private boolean testForHiddenPatternRevealed(Array<ReelTile> levelReel, int levelWidth, int levelHeight) {
        TupleValueIndex[][] matchGrid = flashSlots.flashSlots(reelTiles);
        return hiddenPatternRevealed(matchGrid);
    }

    private boolean testForHiddenPlayingCardsRevealed(Array<ReelTile> levelReel, int levelWidth, int levelHeight) {
        TupleValueIndex[][] matchGrid = flashSlots.flashSlots(reelTiles);
        return hiddenPlayingCardsRevealed(matchGrid);
    }

    private boolean hiddenPlayingCardsRevealed(TupleValueIndex[][] grid) {
        boolean hiddenPlayingCardsRevealed = true;
        for (Integer hiddenPlayingCard : hiddenPlayingCards) {
            MapObject mapObject = level.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).get(hiddenPlayingCard.intValue());
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            for (int ro = (int) (mapRectangle.getX()); ro < (int) (mapRectangle.getX() + mapRectangle.getWidth()); ro += PlayScreen.TILE_WIDTH) {
                for (int co = (int) (mapRectangle.getY()); co < (int) (mapRectangle.getY() + mapRectangle.getHeight()); co += PlayScreen.TILE_HEIGHT) {
                    int c = getColumnFromLevel(ro);
                    int r = getRowFromLevel(co, levelHeight);

                    if ((r >= 0) & (r <= levelHeight) & (c >= 0) & (c <= levelWidth)) {
                        if (grid[r][c] != null)
                            if (!reelTiles.get(grid[r][c].getIndex()).isReelTileDeleted())
                                hiddenPlayingCardsRevealed = false;
                    } //else
//                        Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r=" + r + "c=" + c);
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
            int r = getRowFromLevel(mapRectangle.getY(), levelHeight);
            if ((r >= 0) & (r <= levelHeight) & (c >= 0) & (c <= levelWidth)) {
                if (grid[r][c] != null)
                    if (!reelTiles.get(grid[r][c].getIndex()).isReelTileDeleted())
                        hiddenPattern = false;
            }         }
        return hiddenPattern;
    }

    private boolean testForJackpot(Array<ReelTile> levelReel, int levelWidth, int levelHeight) {
        onlyFlashSlotsForReelsThatHaveStoppedSpinning();
        flashSlots.flashSlots(levelReel);
        return flashSlots.getMatchedSlots().size <= 0;
    }

    private void onlyFlashSlotsForReelsThatHaveStoppedSpinning() {

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
        reelBoxesToBeCreated = true;
    }

    public void setAnimatedReelsManager(AnimatedReelsManager animatedReelsManager) {
        this.animatedReelsManager = animatedReelsManager;
    }

    public Array<ReelTile> getReelTiles() {
        return this.reelTiles;
    }

    public void setReelTiles(Array<ReelTile> reelTiles) {
        this.reelTiles = reelTiles;
    }

    public Array<Body> getReelBoxes() {
        return this.reelBoxes;
    }

    public void setReelBoxes(Array<Body> reelBoxes) { this.reelBoxes = reelBoxes; }

    public Array<AnimatedReel> getAnimatedReels() {
        return animatedReels;
    }

    public void setAnimatedReels(Array<AnimatedReel> animatedReels) { this.animatedReels = animatedReels; }

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
                    deleteReel(source);
                    if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
                        testPlayingCardLevelWon(levelWidth, levelHeight);
                    if (levelDoor.getLevelType().equals(HIDDEN_PATTERN_LEVEL_TYPE))
                        testForHiddenPlatternLevelWon(levelWidth, levelHeight);
                    if (levelDoor.getLevelType().equals(BONUS_LEVEL_TYPE))
                        testForBonusLevelWon(levelWidth, levelHeight);
            }
        }
    };

    private void deleteReel(BaseTween<?> source) {
        ReelTile reel = (ReelTile) source.getUserData();
        hud.addScore((reel.getEndReel() + 1) * reel.getScore());
        int reelTileIndex = reelTiles.indexOf(reel, true);
        reel.deleteReelTile();
        reelBoxesToDelete.add(reelTileIndex);
        if (!replacementReelBoxes.contains(reelTileIndex, true))
            replacementReelBoxes.add(reelTileIndex);
    }

    private void testPlayingCardLevelWon(int levelWidth, int levelHeight) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] matchGrid = puzzleGridTypeReelTile.populateMatchGrid(reelTiles, levelWidth, levelHeight);
        puzzleGrid.matchGridSlots(matchGrid);
        if (hiddenPlayingCardsRevealed(matchGrid))
            iWonTheLevel();
    }

    private void testForHiddenPlatternLevelWon(int levelWidth, int levelHeight) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] matchGrid = puzzleGridTypeReelTile.populateMatchGrid(reelTiles, levelWidth, levelHeight);
        puzzleGrid.matchGridSlots(matchGrid);
        if (hiddenPatternRevealed(matchGrid))
            iWonTheLevel();
    }

    private void testForBonusLevelWon(int levelWidth, int levelHeight) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] matchGrid = puzzleGridTypeReelTile.populateMatchGrid(reelTiles, levelWidth, levelHeight);
        puzzleGrid.matchGridSlots(matchGrid);
        if (hiddenPatternRevealed(matchGrid))
            iWonTheLevel();
    }

    public void update(float dt) {
        physics.update(dt);
        updateReelBoxes();
        deleteReelBoxes(reelBoxesToDelete);
        if (enableCreateReplacementReelsBoxesFeature) {
            if (reelBoxesToBeCreated) {
                createReplacementReelBoxes();
                reelBoxesToBeCreated = false;
            }
        } else
            if (itisTimeForRandomReplacementReelBox)
                createRandomReplacementReelBox();
    }

    public void createStartRandomReelBoxTimer() {
        System.out.println("createStartRandomReelBoxTimer");
        Timer.schedule(new Timer.Task(){
                           @Override
                           public void run() {
                                timeTocreateRandomReplacementReelBox();
                           }
                       }
                , (Random.getInstance().nextFloat() + 1.0f) * 5
        );
    }

    private void timeTocreateRandomReplacementReelBox() {
        itisTimeForRandomReplacementReelBox = true;
        System.out.println("itisTimeForRandomReplacementReelBox set true");
    }

    private void createRandomReplacementReelBox() {
        itisTimeForRandomReplacementReelBox = false;

        if (replacementReelBoxes.size == 0)
            return;

        Array<Integer> randomReelBoxToReplace = new Array();

        randomReelBoxToReplace.add(replacementReelBoxes.get(
                Random.getInstance().nextInt(replacementReelBoxes.size)
        ));
        delegateCreateReplacementReelBoxes(randomReelBoxToReplace);

        createStartRandomReelBoxTimer();
    }

    public void render(SpriteBatch batch, float dt) {
        batch.begin();
        renderScore(batch);
        batch.end();
    }

    private void renderScore(SpriteBatch batch) {
        for (Score score : scores)
            score.render(batch);
    }

    private void createReplacementReelBoxes() {
        if (animatedReelsManager == null)
            return;
        Array<Integer> reelBoxesToReplace = getReelBoxesToReplace();

        delegateCreateReplacementReelBoxes(reelBoxesToReplace);
    }

    private void delegateCreateReplacementReelBoxes(Array<Integer> reelBoxesToReplace) {
        animatedReelsManager.setNumberOfReelsToFall(reelBoxesToReplace.size);
        Array<Integer> replacementReelsToDelete = new Array<>();
        for (Integer reelBoxIndex : reelBoxesToReplace)
            processReelBoxToReplace(replacementReelsToDelete, reelBoxIndex);
        reelsSpinning = reelBoxesToReplace.size;
        for (Integer replacementReelToDelete : replacementReelsToDelete)
            replacementReelBoxes.removeValue(replacementReelToDelete, true);
    }

    private void processReelBoxToReplace(Array<Integer> replacementReelsToDelete, Integer reelBoxIndex) {
        Array<Integer> deletedReelsInColumn = animatedReelsManager.getTheReelsDeletedInColumn(reelTiles.get(reelBoxIndex).getSnapX());
        if (deletedReelsInColumn.size > 0) {
            createReplacementReelBox(deletedReelsInColumn.get(deletedReelsInColumn.size - 1));
            replacementReelsToDelete.add(deletedReelsInColumn.get(deletedReelsInColumn.size - 1));
        }
        else {
            createReplacementReelBox(reelBoxIndex);
            replacementReelsToDelete.add(reelBoxIndex);
        }
        reelBoxFalling = reelBoxIndex;
        reelBoxFallingY = reelTiles.get(reelBoxIndex).getY();
    }

    private Array<Integer> getReelBoxesToReplace() {
        Array<Integer> reelBoxesToReplace = new Array<>();
        if (usePreparedSlotPuzzleMatrixReelsToFall)
            prepareReelsToFall(reelBoxesToReplace);
        else
            reelBoxesToReplace =
                    FilterReelBoxes.
                            filterReelBoxesByDifficultyLevel(
                                    replacementReelBoxes, 0.1f);
        return reelBoxesToReplace;
    }

    private void prepareReelsToFall(Array<Integer> reelBoxesToReplace) {
        reelBoxesToReplace.add(myReelsToFall.get(myReelsToFallIndex));
        myReelsToFallIndex++;
        if(myReelsToFallIndex>=myReelsToFall.size)
            myReelsToFallIndex = 0;
    }

    private void createReplacementReelBox(Integer reelBoxIndex) {
        updateReplacementReelTile(reelBoxIndex);
        updateReplacementAnimatedReel(reelBoxIndex);
        updateReplacementBody(reelBoxIndex);
    }

    private void updateReplacementReelTile(Integer reelBoxIndex) {
        ReelTile reelTile = reelTiles.get(reelBoxIndex.intValue());
        reelTile.unDeleteReelTile();
        reelTile.setScale(1.0f);
        Color reelTileColor = reelTile.getColor();
        reelTileColor.set(reelTileColor.r, reelTileColor.g, reelTileColor.b, 1.0f);
        reelTile.setColor(reelTileColor);
        if (usePreparedSlotPuzzleMatrixReelsToFall)
            reelTile.setEndReel(myReelsToFallEndReel.get(myReelsToFallIndex));
        else
            reelTile.setEndReel(Random.getInstance().nextInt( reelTile.getNumberOfReelsInTexture() - 1));
        reelTile.resetReel();
        reelTile.setSpinning(true);
        reelTile.setIsFallen(false);
        reelTile.setIsStoppedFalling(false);
    }

    private void updateReplacementBody(Integer reelBoxIndex) {
        Body reelTileBody = reelBoxes.get(reelBoxIndex);
        AnimatedReel animatedReel = (AnimatedReel) reelTileBody.getUserData();
        ReelTile reelTile = animatedReel.getReel();
        reelTileBody.setTransform(
                (reelTile.getDestinationX() + 19) / 100,
                (reelTile.getDestinationY() + 19 + OFF_PLAY_SCREEN_OFFSET) / 100,
                0);
        reelTileBody.setActive(true);
    }

    private void updateReplacementAnimatedReel(Integer reelBoxIndex) {
        AnimatedReel animatedReel = animatedReels.get(reelBoxIndex);
        animatedReel.reinitialise();
        animatedReel.setupSpinning();
    }

    public void setNumberOfReelsSpinning(int numberOfReelsSpinning) {
        this.reelsSpinning = numberOfReelsSpinning;
    }

    public int getNumberOfReelsSpinning() {
        return reelsSpinning;
    }

    public int getNumberOfReelsFlashing() {
        return flashSlots.getNumberOfReelsFlashing();
    }

    public boolean getAreReelsFlashing() {
        return flashSlots.areReelsFlashing();
    }

    public boolean getAreReelsStartedFlashing() {
        return flashSlots.areReelsStartedFlashing();
    }

    public boolean isFinishedMatchingSlots() {
        return flashSlots.isFinishedMatchingSlots();
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
                reelBoxCollided.setTransform((reelTile.getDestinationX() + 19) / 100, (reelTile.getDestinationY() + 19) / 100, 0);
            }
            reelBoxesCollided.removeRange(0, reelBoxesCollided.size - 1);
        }
    }

    private void deleteReelBoxes(Array<Integer> reelBoxesToDelete) {
        for (Integer reelBoxToDelete : reelBoxesToDelete) {
            AnimatedReel animatedReel = (AnimatedReel) reelBoxes.get(reelBoxToDelete).getUserData();
            reelBoxes.get(reelBoxToDelete).setTransform(
                    animatedReel.getReel().getDestinationX() + 19 / 100,
                    animatedReel.getReel().getDestinationY() + 19 + OFF_PLAY_SCREEN_OFFSET / 100, 0);
            reelBoxes.get(reelBoxToDelete).setActive(false);
        }
        reelBoxesToDelete.clear();
    }
}
