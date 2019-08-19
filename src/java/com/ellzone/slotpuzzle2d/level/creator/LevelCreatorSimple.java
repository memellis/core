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
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
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
    private boolean hitSinkBottom = false, dropReplacementReelBoxes = false;
    private Array<Body> reelBoxes;
    private Array<Body> reelBoxesCollided;
    public Array<Integer> replacementReelBoxes;
    private PuzzleGridTypeReelTile puzzleGridTypeReelTile;
    private int numberOfReelBoxesToReplace, numberOfReelBoxesToDelete;
    boolean matchedReels = false;
    boolean reelsAboveHaveFallen = false;
    Array<TupleValueIndex> reelsToFall;
    private int mapWidth, mapHeight;
    private FlashSlots flashSlots;
    private int debugNumberOfReelsDeleted = 0;
    private int debugNumberOfReelBoxesDeleted = 0;

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

    private void initialise(LevelDoor levelDoor, Array<ReelTile> reelTiles, TiledMap level, TweenManager tweenManager, int levelWidth, int levelHeight) {
        puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        reelBoxes = new Array<Body>();
        replacementReelBoxes = new Array<Integer>();
        reelTiles = createLevel(levelDoor, level, reelTiles, levelWidth, levelHeight);
        reelsSpinning = reelBoxes.size - 1;
        reelsFlashing = 0;
        hitSinkBottom = false;
        scores = new Array<Score>();
        reelsToFall = new Array<TupleValueIndex>();
        getMapProperties(level);
        flashSlots = new FlashSlots(tweenManager, mapWidth, mapHeight, reelTiles);
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
            int levelWidth,
            int levelHeight) {
        if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
            hiddenPlayingCard = new HiddenPlayingCard(level, carddeckAtlas);

        reelTiles = populateLevel(level, reelTiles, levelWidth, levelHeight);
        reelTiles = checkLevel(reelTiles, levelWidth, levelHeight);
        reelTiles = adjustForAnyLonelyReels(reelTiles, levelWidth, levelHeight);
//        for (ReelTile reelTile : reelTiles)
//            reelTile.setEndReel(scenario1Reels[reelTile.getIndex()]);

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
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r + 1][lonelyTile.c].index).getEndReel());
        } else if (lonelyTile.c == 0) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c + 1].index).getEndReel());
        } else if (lonelyTile.r == levelHeight - 1) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r - 1][lonelyTile.c].index).getEndReel());
        } else if (lonelyTile.c == levelWidth - 1) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c - 1].index).getEndReel());
        } else {
            adjustAnyLoneyAdjacentTile(levelReel, grid, lonelyTile);
        }
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
        System.out.println("reelsSpinning="+reelsSpinning);
        if ((reelsSpinning <= -1) & (hitSinkBottom)) {
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
        } else {
            if (testForJackpot(reelTiles, levelWidth, levelHeight))
                iWonABonus();
        }
    }

    private void actionReelStoppedFlashing(ReelTileEvent event, ReelTile reelTile) {
        if ((playState == PlayStates.INTRO_FLASHING) | (playState != PlayStates.REELS_FLASHING)) {
            if (flashSlots.getNumberOfReelsFlashing() < 1) {
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

    private ReelTileGridValue[][] flashSlots(Array<ReelTile> reelTiles, int levelWidth, int levelHeight) {
        if (playState == PlayStates.INTRO_SPINNING)
            playState = PlayStates.INTRO_FLASHING;

        if (playState == PlayStates.REELS_SPINNING ||
            playState == PlayStates.PLAYING)
            playState = PlayStates.REELS_FLASHING;

        ReelTileGridValue[][] puzzleGrid = puzzleGridTypeReelTile.populateMatchGrid(reelTiles, levelWidth, levelHeight);

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
        if (numberOfReelBoxesToDelete == -1)
            System.out.println("numberOfReelBoxesToDelete=-1");

        for (TupleValueIndex matchedSlot : matchedSlots)
            reelTiles.get(matchedSlot.index).setScore(matchedSlot.value);

        flashSlots.flashMatchedSlotsForLevelCreator(matchedSlots, puzzleGridTypeReelTile);
        matchedReels = true;
    }

    private boolean testForHiddenPatternRevealed(Array<ReelTile> levelReel, int levelWidth, int levelHeight) {
        TupleValueIndex[][] matchGrid = flashSlots(levelReel, levelWidth, levelHeight);
        return hiddenPatternRevealed(matchGrid);
    }

    private boolean testForHiddenPlayingCardsRevealed(Array<ReelTile> levelReel, int levelWidth, int levelHeight) {
        TupleValueIndex[][] matchGrid = flashSlots(levelReel, levelWidth, levelHeight);
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
            int r = getRowFromLevel(mapRectangle.getY(), levelHeight);
            if ((r >= 0) & (r <= levelHeight) & (c >= 0) & (c <= levelWidth)) {
                if (grid[r][c] != null)
                    if (!reelTiles.get(grid[r][c].getIndex()).isReelTileDeleted())
                        hiddenPattern = false;
            } else
                Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r=" + r + "c=" + c);
        }
        return hiddenPattern;
    }

    private boolean testForJackpot(Array<ReelTile> levelReel, int levelWidth, int levelHeight) {
        TupleValueIndex[][] matchGrid = flashSlots(levelReel, levelWidth, levelHeight);
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
        return animatedReels;
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
                    //hudAddScore((reel.getEndReel() + 1) * reel.getScore());
                    //playSound(reelStoppedSound);
                    //playSound(chaChingSound);

                    reel.deleteReelTile();
                    debugNumberOfReelsDeleted++;
                    physics.deleteBody(reelBoxes.get(reelTilesIndex));

                    replacementReelBoxes.add(reelTilesIndex);
                    flashSlots.setNumberOfReelsFlashing(flashSlots.getNumberOfReelsFlashing() - 1);
                    numberOfReelBoxesToDelete--;
                    System.out.println("numberOfReelBoxesToDelete="+numberOfReelBoxesToDelete);
                    if (numberOfReelBoxesToDelete < 1) {
                        if ((playState == PlayStates.INTRO_FLASHING) | (playState == PlayStates.REELS_FLASHING)) {
                            findReelsAboveMe();
                            matchedReels = isThereMatchedSlots();
                            if (!matchedReels) {
                                if (replacementReelBoxes.size == 0)
                                    playState = PlayStates.PLAYING;
                                else
                                if (reelsToFall.size == 0)
                                    createReplacementReelBoxes();
                            } else {
                                System.out.println("What happends when I get here!");
//                                flashSlots(reelTiles, levelWidth, levelHeight);
                            }
                        }
                    }

                    if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
                        testPlayingCardLevelWon(levelWidth, levelHeight);
                    else
                    if (levelDoor.getLevelType().equals(HIDDEN_PATTERN_LEVEL_TYPE))
                        testForHiddenPlatternLevelWon(levelWidth, levelHeight);
            }
        }
    };

    private boolean isThereMatchedSlots() {
        ReelTileGridValue[][] puzzleGrid = puzzleGridTypeReelTile.populateMatchGrid(reelTiles, levelWidth, levelHeight);
        Array<ReelTileGridValue> matchedSlots = puzzleGridTypeReelTile.matchGridSlots(puzzleGrid);
        Array<ReelTileGridValue> duplicateMatchedSlots = PuzzleGridTypeReelTile.findDuplicateMatches(matchedSlots);

        PuzzleGridTypeReelTile.printGrid(puzzleGrid);

        matchedSlots = PuzzleGridTypeReelTile.adjustMatchSlotDuplicates(matchedSlots, duplicateMatchedSlots);
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

    public void update(float dt) {
        if ((playState == PlayStates.INTRO_FLASHING) | (playState == PlayStates.REELS_FLASHING)) {
            if ((reelsAboveHaveFallen) & (reelsToFall.size==0) & (flashSlots.getNumberOfReelsFlashing() == 0)) {
                matchedReels = isThereMatchedSlots();
                if (!matchedReels) {
                    if (replacementReelBoxes.size == 0)
                        playState = PlayStates.PLAYING;
                    else
                    if (reelsToFall.size == 0) {
                        System.out.println("I was calling createReplacementReelBoxes from here! 1");
                        createReplacementReelBoxes();
                    }
                    reelsAboveHaveFallen = false;
                }
            } else {
                if ((playState == PlayStates.INTRO_FLASHING) | (playState == PlayStates.REELS_FLASHING) | playState == PlayStates.PLAYING) {
                    if ((numberOfReelBoxesToDelete < 0) &
                            (!matchedReels) &
                            (reelsToFall.size == 0) &
                            (replacementReelBoxes.size > 0) &
                            (flashSlots.getNumberOfReelsFlashing() == 0)) {
                        matchedReels = isThereMatchedSlots();
                        if (!matchedReels) {
                            if ((numberOfReelBoxesToDelete < 0) &
                                    (reelsToFall.size == 0) &
                                    (replacementReelBoxes.size > 0)) {
                                System.out.println("I was calling createReplacementReelBoxes from here! 2");
                                createReplacementReelBoxes();
                            }
                        } else {
                            if (playState == PlayStates.REELS_FLASHING)
                                playState = PlayStates.PLAYING;
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
        if ((playState == PlayStates.INTRO_SPINNING) | (playState == PlayStates.REELS_SPINNING)) {
             if ((numberOfReelBoxesToDelete < 0) &
                    (reelsToFall.size == 0) &
                    (replacementReelBoxes.size == 0))
                playState = PlayStates.PLAYING;
        }

        physics.update(dt);
        updateReelBoxes();
    }

    private void createReplacementReelBoxes() {
        System.out.println();
        printMatchGrid(reelTiles, 12, 9);
        replacementReelBoxes = filterReelBoxesByDifficultyLevel(getDeletedReelTiles(), 0.1f);
        for (Integer reelBoxIndex : replacementReelBoxes)
            createReplacementReelBox(reelBoxIndex);

        reelsSpinning = replacementReelBoxes.size - 1;
        replacementReelBoxes.removeRange(0, replacementReelBoxes.size - 1);
        System.out.println("replacementReelBoxes.size="+replacementReelBoxes.size);
        if (replacementReelBoxes.size > 0)
            System.out.println("replacementReelBoxes.size="+replacementReelBoxes.size);
        dropReplacementReelBoxes = false;
        if (playState == PlayStates.INTRO_FLASHING)
            playState = PlayStates.INTRO_SPINNING;

        if (playState == PlayStates.REELS_FLASHING)
            playState = PlayStates.REELS_SPINNING;
    }

    private Array<Integer> getDeletedReelTiles() {
        Array<Integer> deleteReelTiles = new Array<>();
        for (ReelTile reelTile : reelTiles) {
            if (reelTile.isReelTileDeleted())
                deleteReelTiles.add(reelTile.getIndex());
        }
        return deleteReelTiles;
    }

    private Array<Integer> filterReelBoxesByDifficultyLevel(Array<Integer> deletedReelBoxes, float difficultyLevelFactor) {
        Array<Integer> filterReplacementReelBoxes = new Array<>();
        int numberOfReelBoxesToBeSelected = (int) Math.ceil(deletedReelBoxes.size * 0.1f);
//        int numberOfReelBoxesToBeSelected = 1;
        do {
            int next = Random.getInstance().nextInt(deletedReelBoxes.size);
            int nextIndex = deletedReelBoxes.get(next);
            if (!filterReplacementReelBoxes.contains(nextIndex, true))
                filterReplacementReelBoxes.add(nextIndex);
        } while (filterReplacementReelBoxes.size < numberOfReelBoxesToBeSelected);
        for (Integer frrb : filterReplacementReelBoxes)
            System.out.println("frrb="+frrb+" isDeleted="+reelTiles.get(frrb).isReelTileDeleted());
        return filterReplacementReelBoxes;
    }

    private void createReplacementReelBox(Integer reelBoxIndex) {
        ReelTile reelTile = reelTiles.get(reelBoxIndex.intValue());
        reelTile.unDeleteReelTile();
        reelTile.setScale(1.0f);
        Color reelTileColor = reelTile.getColor();
        reelTileColor.set(reelTileColor.r, reelTileColor.g, reelTileColor.b, 1.0f);
        reelTile.setColor(reelTileColor);
        reelTile.setEndReel(Random.getInstance().nextInt( reelTile.getNumberOfReelsInTexture() - 1));
        reelTile.resetReel();
        Body reelTileBody = physics.createBoxBody(BodyDef.BodyType.DynamicBody,
                reelTile.getDestinationX() + 20,
                reelTile.getDestinationY() + 360,
                19,
                19,
                true);
        reelTileBody.setUserData(reelTile);
        reelBoxes.set(reelBoxIndex, reelTileBody);
        AnimatedReel animatedReel = animatedReels.get(reelBoxIndex);
        animatedReel.reinitialise();
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
        TupleValueIndex[][] matchGrid = puzzleGridTypeReelTile.populateMatchGrid(reelTiles, levelWidth, levelHeight);
        TupleValueIndex[] reelsAboveMe = null;
        reelsToFall = new Array<TupleValueIndex>();
        PuzzleGridType puzzleGridType = new PuzzleGridType();
        for (Integer replacementReelBox : replacementReelBoxes) {
            reelsAboveMe = puzzleGridType.getReelsAboveMe(matchGrid,
                    PuzzleGridTypeReelTile.getRowFromLevel(reelTiles.get(replacementReelBox).getDestinationY(), levelHeight),
                    PuzzleGridTypeReelTile.getColumnFromLevel(reelTiles.get(replacementReelBox).getDestinationX()));
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
                reelBoxCollided.setTransform((reelTile.getDestinationX() + 20) / 100, (reelTile.getDestinationY() + 20) / 100, 0);
            }
            reelBoxesCollided.removeRange(0, reelBoxesCollided.size - 1);
        }
    }

    public void undeleteReelBox(int reelTileIndex) {
//        replacementReelBoxes.removeValue(reelTileIndex, true);
        printMatchGrid(reelTiles, 12, 9);
        System.out.println("I want to undeleteReelBox x="+reelTiles.get(reelTileIndex).getX()+" y="+reelTiles.get(reelTileIndex).getY());
    }
}
