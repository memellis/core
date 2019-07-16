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

import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.ScoreAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayInterface;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayState;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStateMachine;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle2d.level.Card;
import com.ellzone.slotpuzzle2d.level.FlashSlots;
import com.ellzone.slotpuzzle2d.level.HiddenPattern;
import com.ellzone.slotpuzzle2d.level.HiddenPlayingCard;
import com.ellzone.slotpuzzle2d.level.LevelCallback;
import com.ellzone.slotpuzzle2d.level.LevelCreator;
import com.ellzone.slotpuzzle2d.level.LevelCreatorInjectionInterface;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.LevelLoader;
import com.ellzone.slotpuzzle2d.level.LevelObjectCreatorEntityHolder;
import com.ellzone.slotpuzzle2d.level.MapLevelNameComparator;
import com.ellzone.slotpuzzle2d.level.PlayScreenIntroSequence;
import com.ellzone.slotpuzzle2d.level.PlayScreenPopUps;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.prototypes.minislotmachine.SpinningSlotsWithMatchesWinFlashesLoadedLevel;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.HoldLightButton;
import com.ellzone.slotpuzzle2d.sprites.LightButton;
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
import com.ellzone.slotpuzzle2d.sprites.Score;
import com.ellzone.slotpuzzle2d.sprites.SlotHandleSprite;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

import net.dermetfan.gdx.assets.AnnotationAssetManager;
import aurelienribon.tweenengine.equations.Quad;
import box2dLight.RayHandler;
import jdk.nashorn.internal.codegen.ClassEmitter;

import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.PIXELS_PER_METER;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_HEIGHT;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_WIDTH;
import static com.ellzone.slotpuzzle2d.level.LevelCreator.HIDDEN_PATTERN_LEVEL_TYPE;
import static com.ellzone.slotpuzzle2d.level.LevelCreator.MINI_SLOT_MACHINE_TYPE;
import static com.ellzone.slotpuzzle2d.level.LevelCreator.PLAYING_CARD_LEVEL_TYPE;
import static com.ellzone.slotpuzzle2d.scene.Hud.addScore;

public class PlayScreen implements Screen, PlayInterface, LevelCreatorInjectionInterface {
    public static final int TILE_WIDTH = 40;
    public static final int TILE_HEIGHT = 40;
    public static final int GAME_LEVEL_WIDTH = 12;
    public static final int GAME_LEVEL_HEIGHT = 9;
    public static final int SLOT_REEL_OBJECT_LAYER = 2;
    public static final float PUZZLE_GRID_START_X = 160.0f;
    public static final float PUZZLE_GRID_START_Y = 40.0f;
    private static final String REELS_LAYER_NAME = "ReelSprites";
    private static final String SLOTPUZZLE_SCREEN = "PlayScreen";
    private LevelLoader levelLoader;
    private PlayStateMachine playStateMachine;
    private PlayStates playState;
    private SlotPuzzle game;
    private final OrthographicCamera camera = new OrthographicCamera();
    private Viewport viewport;
    private Stage stage;
    private float sW, sH;
    private final TweenManager tweenManager = new TweenManager();
    private FlashSlots flashSlots;
    private TextureAtlas tilesAtlas;
    private Sound chaChingSound,
                  pullLeverSound,
                  reelSpinningSound,
                  reelStoppedSound;
    private boolean isLoaded = false;
    private int reelsSpinning;
    private TiledMap level;
    private Random random;
    private OrthogonalTiledMapRenderer renderer;
    private boolean gameOver = false;
    private boolean inRestartLevel = false;
    private boolean win = false;
    private int touchX, touchY;
    private boolean displaySpinHelp;
    private int displaySpinHelpSprite;
    private Sprite[] sprites;
    private Hud hud;
    private Array<Score> scores;
    private BitmapFont font;
    private LevelDoor levelDoor;
    private HiddenPattern hiddenPattern;
    private MapTile mapTile;
    private int mapWidth;
    private int mapHeight;
    private boolean show = false;
    private PlayScreenIntroSequence playScreenIntroSequence;
    private PlayScreenPopUps playScreenPopUps;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private RayHandler rayHandler;
    private Array<AnimatedReel> animatedReels;
    private Array<ReelTile> reelTiles;
    private Array<HoldLightButton> holdLightButtons;
    private Array<SlotHandleSprite> slotHandles;
    private Texture slotReelScrollTexture;
    private ReelSprites reelSprites;
    private Viewport lightViewport;
    private int[][] reelGrid = new int[3][3];
    private Array<Array<Vector2>> rowMacthesToDraw;
    private ShapeRenderer shapeRenderer;

    public PlayScreen(SlotPuzzle game, LevelDoor levelDoor, MapTile mapTile) {
        this.game = game;
        this.levelDoor = levelDoor;
        this.mapTile = mapTile;
        createPlayScreen();
        playStateMachine.getStateMachine().changeState(PlayState.INTRO_SPINNING_SEQUENCE);
    }

    private void createPlayScreen() {
        initialisePlayFiniteStateMachine();
        playState = PlayStates.INITIALISING;
        initialiseWorld();
        initialiseDependencies();
        setupPlayScreen();
        createReelIntroSequence();
    }

    private void initialisePlayFiniteStateMachine() {
        playStateMachine = new PlayStateMachine();
        playStateMachine.setConcretePlay(this);
        playStateMachine.getStateMachine().changeState(PlayState.INITIALISING);
    }

    private void initialiseDependencies() {
        initialiseScreen();
        initialiseTweenEngine();
        getAssets(game.annotationAssetManager);
        createSprites();
        slotReelScrollTexture = createSlotReelScrollTexture();
    }

    private void setupPlayScreen() {
        loadLevel();
        initialisePlayScreen();
        initialiseHud();
    }

    private Texture createSlotReelScrollTexture() {
        Pixmap slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        return new Texture(slotReelScrollPixmap);
    }

    private void initialiseWorld() {
        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();
        rayHandler = new RayHandler(world);
        rayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(0.25f, 0.25f, 0.25f, 0.25f);
    }

    private void initialisePlayScreen() {
        random = new Random();
        renderer = new OrthogonalTiledMapRenderer(level);
        displaySpinHelp = false;
        scores = new Array<>();
        font = new BitmapFont();
        sW = SlotPuzzleConstants.VIRTUAL_WIDTH;
        sH = SlotPuzzleConstants.VIRTUAL_HEIGHT;
        playScreenPopUps = new PlayScreenPopUps(tilesAtlas, (int) sW, (int) sH, game.batch, tweenManager, levelDoor);
        playScreenPopUps.initialise();
        rowMacthesToDraw = new Array<Array<Vector2>>();
        shapeRenderer = new ShapeRenderer();
    }

    private void initialiseHud() {
        hud = new Hud(game.batch);
        hud.setLevelName(levelDoor.getLevelName());
    }

    private void loadLevel() {
        LevelObjectCreatorEntityHolder levelObjectCreator = new LevelObjectCreatorEntityHolder(this, world, rayHandler);
        Array<RectangleMapObject> extractedLevelRectangleMapObjects = extractLevelAssets(level);
        levelObjectCreator.createLevel(extractedLevelRectangleMapObjects);
        getLevelEntities(levelObjectCreator);
        levelLoader = getLevelLoader();
        levelLoader.createLevel(GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
        reelsSpinning = reelTiles.size - 1;
        hiddenPattern = levelLoader.getHiddenPattern();
        getMapProperties(level);
        flashSlots = new FlashSlots(tweenManager, mapWidth, mapHeight, reelTiles);
    }

    private void getLevelEntities(LevelObjectCreatorEntityHolder levelObjectCreator) {
        animatedReels = levelObjectCreator.getAnimatedReels();
        reelTiles = levelObjectCreator.getReelTiles();
        holdLightButtons = levelObjectCreator.getHoldLightButtons();
        slotHandles = levelObjectCreator.getHandles();
    }

    private Array<RectangleMapObject> extractLevelAssets(TiledMap level) {
        Array<RectangleMapObject> levelRectangleMapObjects = getRectangleMapObjectsFromLevel(level);
        MapLevelNameComparator mapLevelNameComparator = new MapLevelNameComparator();
        levelRectangleMapObjects.sort(mapLevelNameComparator);
        return levelRectangleMapObjects;
    }

    private Array<RectangleMapObject> getRectangleMapObjectsFromLevel(TiledMap level) {
        return level.getLayers().get(SLOT_REEL_OBJECT_LAYER).getObjects().getByType(RectangleMapObject.class);
    }

    private LevelLoader getLevelLoader() {
        LevelLoader levelLoader = new LevelLoader(game.annotationAssetManager, levelDoor, mapTile, animatedReels);
        levelLoader.setStoppedSpinningCallback(stoppedSpinningCallback);
        levelLoader.setStoppedFlashingCallback(stoppedFlashingCallback);
        return levelLoader;
    }

    private LevelCallback stoppedSpinningCallback = new LevelCallback() {
        @Override
        public void onEvent (ReelTile source) {

            reelStoppedSound.play();
            reelsSpinning--;

            if (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY) {
                if (reelsSpinning <= -1) {
                    if (levelDoor.getLevelType().equals(LevelCreator.HIDDEN_PATTERN_LEVEL_TYPE))
                        if (testForHiddenPatternRevealed(reelTiles))
                            iWonTheLevel();
                    if (levelDoor.getLevelType().equals(LevelCreator.PLAYING_CARD_LEVEL_TYPE))
                        if (testForHiddenPlayingCardsRevealed(reelTiles))
                            iWonTheLevel();
                }
            }
            if (levelDoor.getLevelType().equals(LevelCreator.MINI_SLOT_MACHINE_TYPE))
                addReelStoppedListener(source);

        }
    };

    private void addReelStoppedListener(ReelTile reel) {
        reel.addListener(new ReelStoppedListener().invoke());
    }

    private class ReelStoppedListener {
        public ReelTileListener invoke() {
            return new ReelTileListener() {
                @Override
                public void actionPerformed(ReelTileEvent event, ReelTile source) {
                    if (event instanceof ReelStoppedSpinningEvent)
                        matchReels();
                }
            };
        }
    }

    private void matchReels() {
        captureReelPositions();
        PuzzleGridTypeReelTile puzzleGrid = new PuzzleGridTypeReelTile();
        ReelTileGridValue[][] matchGrid = puzzleGrid.populateMatchGrid(reelGrid);
        PuzzleGridTypeReelTile puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        matchGrid = puzzleGridTypeReelTile.createGridLinks(matchGrid);
        matchRowsToDraw(matchGrid, puzzleGridTypeReelTile);
    }

    private void matchRowsToDraw(ReelTileGridValue[][] matchGrid, PuzzleGridTypeReelTile puzzleGridTypeReelTile) {
        rowMacthesToDraw = new Array<Array<Vector2>>();
        for (int row = 0; row < matchGrid.length; row++) {
            Array<ReelTileGridValue> depthSearchResults = puzzleGridTypeReelTile.depthFirstSearchIncludeDiagonals(matchGrid[row][0]);
            if (puzzleGridTypeReelTile.isRow(depthSearchResults, matchGrid))
                rowMacthesToDraw.add(drawMatches(depthSearchResults, 340, 300));
        }
    }

    private Array<Vector2> drawMatches(Array<ReelTileGridValue> depthSearchResults, int startX, int startY) {
        Array<Vector2> points = new Array<Vector2>();
        for (ReelTileGridValue cell : depthSearchResults) {
            hud.addScore(cell.value);
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
        else
        if(reelPosition > sprites.length - 1)
            reelPosition = 0;
        return reelPosition;
    }

    private LevelCallback stoppedFlashingCallback = new LevelCallback() {
        @Override
        public void onEvent(ReelTile source) {
            if ((levelDoor.getLevelType().equals(LevelCreator.HIDDEN_PATTERN_LEVEL_TYPE)) ||
                (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))) {
                if (testForAnyLonelyReels(reelTiles)) {
                    win = false;
                    if (Hud.getLives() > 0) {
                        playState = PlayStates.LEVEL_LOST;
                        playScreenPopUps.setLevelLostSpritePositions();
                        playScreenPopUps.getLevelLostPopUp().showLevelPopUp(null);
                    } else
                        gameOver = true;
                }
                reelScoreAnimation(source);
                deleteReelAnimation(source);
            }
        }
    };

    private void initialiseScreen() {
        viewport = new FitViewport(SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);

        lightViewport = new FitViewport((float) VIRTUAL_WIDTH / PIXELS_PER_METER, (float) VIRTUAL_HEIGHT / PIXELS_PER_METER);
        lightViewport.getCamera().position.set( (float) VIRTUAL_WIDTH / PIXELS_PER_METER * 0.5f,
                (float) VIRTUAL_HEIGHT / PIXELS_PER_METER * 0.5f,
                0);
        lightViewport.getCamera().update();
        lightViewport.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
    }

    private void initialiseTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        SlotPuzzleTween.registerAccessor(Score.class, new ScoreAccessor());
    }

    private void getAssets(AnnotationAssetManager annotationAssetManager) {
        getAtlasAssets(annotationAssetManager);
        getSoundAssets(annotationAssetManager);
        getLevelAssets(annotationAssetManager);
    }

    private void getLevelAssets(AnnotationAssetManager annotationAssetManager) {
        level = annotationAssetManager.get("levels/level " + (this.levelDoor.getId() + 1) + " - 40x40.tmx");
    }

    private void getSoundAssets(AnnotationAssetManager annotationAssetManager) {
        chaChingSound = annotationAssetManager.get(AssetsAnnotation.SOUND_CHA_CHING);
        pullLeverSound = annotationAssetManager.get(AssetsAnnotation.SOUND_PULL_LEVER);
        reelSpinningSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_SPINNING);
        reelStoppedSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_STOPPED);
    }

    private void getAtlasAssets(AnnotationAssetManager annotationAssetManager) {
        tilesAtlas = annotationAssetManager.get(AssetsAnnotation.TILES);
    }

    private void createSprites() {
        reelSprites = new ReelSprites(game.annotationAssetManager);
        sprites = reelSprites.getSprites();
    }

    private void getMapProperties(TiledMap level) {
        MapProperties mapProperties = level.getProperties();
        mapWidth = mapProperties.get("width", Integer.class);
        mapHeight = mapProperties.get("height", Integer.class);
    }

    private void createReelIntroSequence() {
        playState = PlayStates.INTRO_SEQUENCE;
        playScreenIntroSequence = new PlayScreenIntroSequence(reelTiles, tweenManager);
        playScreenIntroSequence.createReelIntroSequence(introSequenceCallback);
    }

    private TweenCallback introSequenceCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            delegateIntroSequenceCallback(type);
        }
    };

    private void delegateIntroSequenceCallback(int type) {
        switch (type) {
            case TweenCallback.END:
                playState = PlayStates.INTRO_POPUP;
                playScreenPopUps.setPopUpSpritePositions();
                playScreenPopUps.getLevelPopUp().showLevelPopUp(null);
                break;
        }
    }

    private boolean testForHiddenPatternRevealed(Array<ReelTile> reelTiles) {
        TupleValueIndex[][] matchGrid = flashSlots.flashSlots(reelTiles);
        return hiddenPattern.isHiddenPatternRevealed(matchGrid, this.reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
    }

    private boolean testForHiddenPlayingCardsRevealed(Array<ReelTile> reelTiles) {
        TupleValueIndex[][] matchGrid = flashSlots.flashSlots(reelTiles);
        return hiddenPattern.isHiddenPatternRevealed(matchGrid, this.reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
    }

    private boolean testForAnyLonelyReels(Array<ReelTile> levelReel) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] grid = levelLoader.populateMatchGrid(levelReel, GAME_LEVEL_WIDTH , GAME_LEVEL_HEIGHT);
        return puzzleGrid.anyLonelyTiles(grid);
    }

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
                    proceesDeleteReel(source);
            }
        }
    };

    private void proceesDeleteReel(BaseTween<?> source) {
        ReelTile reel = (ReelTile) source.getUserData();
        addScore((reel.getEndReel() + 1) * reel.getScore());
        reelStoppedSound.play();
        chaChingSound.play();
        reel.deleteReelTile();
        flashSlots.deleteAReel();
        if (levelDoor.getLevelType().equals(LevelCreator.PLAYING_CARD_LEVEL_TYPE))
            testPlayingCardLevelWon();
        else
            if (levelDoor.getLevelType().equals(LevelCreator.HIDDEN_PATTERN_LEVEL_TYPE))
                testForHiddenPlatternLevelWon();
    }

    private void reelScoreAnimation(ReelTile source) {
        Score score = new Score(source.getX(), source.getY(), (source.getEndReel() + 1) * source.getScore());
        scores.add(score);
        Timeline.createSequence()
                .beginParallel()
                .push(SlotPuzzleTween.to(score, ScoreAccessor.POS_XY, 2.0f).targetRelative(random.nextInt(20), random.nextInt(160)).ease(Quad.IN))
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

    public void handleInput() {
        if (Gdx.input.justTouched()) {
            touchX = Gdx.input.getX();
            touchY = Gdx.input.getY();
            Vector3 unprojTouch = new Vector3(touchX, touchY, 0);
            viewport.unproject(unprojTouch);
            switch (playState) {
                case INTRO_POPUP:
                    if (isOver(playScreenPopUps.getLevelPopUpSprites().get(0), unprojTouch.x, unprojTouch.y))
                        playScreenPopUps.getLevelPopUp().hideLevelPopUp(hideLevelPopUpCallback);
                    break;
                case LEVEL_LOST:
                    Gdx.app.debug(SLOTPUZZLE_SCREEN, "Lost Level");
                    if (isOver(playScreenPopUps.getLevelLostSprites().get(0), unprojTouch.x, unprojTouch.y))
                        playScreenPopUps.getLevelLostPopUp().hideLevelPopUp(levelOverCallback);
                    break;
                case WON_LEVEL:
                    Gdx.app.debug(SLOTPUZZLE_SCREEN, "Won Level");
                    if(isOver(playScreenPopUps.getLevelWonSprites().get(0), unprojTouch.x, unprojTouch.y))
                        playScreenPopUps.getLevelWonPopUp().hideLevelPopUp(levelWonCallback);
                    break;
                default: break;
            }
            if (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY) {
                Gdx.app.debug(SLOTPUZZLE_SCREEN, "Play");
                if (levelDoor.getLevelType().equals(HIDDEN_PATTERN_LEVEL_TYPE))
                    processIsTileClicked(unprojTouch.x, unprojTouch.y);
                if (levelDoor.getLevelType().equals(MINI_SLOT_MACHINE_TYPE)) {
                    handleReelsTouchedSlotMachine(unprojTouch.x, unprojTouch.y);
                    handleSlotHandleIsTouch(unprojTouch.x, unprojTouch.y);
                }
            }
            handleLightButtonTouched();
        }
    }

    private boolean isOver(Sprite sprite, float x, float y) {
        return sprite.getX() <= x && x <= sprite.getX() + sprite.getWidth()
            && sprite.getY() <= y && y <= sprite.getY() + sprite.getHeight();
    }

    private void processIsTileClicked(float touchX, float touchY) {
        int c = PuzzleGridTypeReelTile.getColumnFromLevel(touchX);
        int r = PuzzleGridTypeReelTile.getRowFromLevel(touchY, GAME_LEVEL_HEIGHT);
        if ((r >= 0) & (r <= GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= GAME_LEVEL_WIDTH)) {
            TupleValueIndex[][] grid = levelLoader.populateMatchGrid(reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
            ReelTile reelTile = reelTiles.get(grid[r][c].index);
            AnimatedReel animatedReel = animatedReels.get(grid[r][c].index);
            if (!reelTile.isReelTileDeleted()) {
                if (reelTile.isSpinning()) {
                    if (animatedReel.getDampenedSineState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE)
                        processReelTouchedWhileSpinning(reelTile);
                } else
                if (!reelTile.getFlashTween())
                    startReelSpinning(reelTile, animatedReel);
            }
        } else
            Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r="+r+"c="+c);
    }

    private void handleReelsTouchedSlotMachine(float touchX, float touchY) {
        for (AnimatedReel animatedReel : animatedReels) {
            if (animatedReel.getReel().getBoundingRectangle().contains(touchX, touchY)) {
                clearRowMatchesToDraw();
                if (animatedReel.getReel().isSpinning()) {
                    if (animatedReel.getDampenedSineState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE)
                        processReelTouchedWhileSpinning(animatedReel.getReel());
                } else {
                    animatedReel.setEndReel(random.nextInt(sprites.length - 1));
                    animatedReel.reinitialise();
                    animatedReel.getReel().startSpinning();
                }
            }
        }
    }

    private void clearRowMatchesToDraw() {
        if (rowMacthesToDraw.size > 0)
            rowMacthesToDraw.removeRange(0, rowMacthesToDraw.size - 1);
    }

    private void handleLightButtonTouched() {
        Vector2 touch = new Vector2(Gdx.input.getX(), Gdx.input.getY());
        touch = lightViewport.unproject(touch);
        for (LightButton lightButton : holdLightButtons)
            if (lightButton.getSprite().getBoundingRectangle().contains(touch.x, touch.y))
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
            }
            i++;
        }
    }

    private void testPlayingCardLevelWon() {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] matchGrid = levelLoader.populateMatchGrid(reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
        puzzleGrid.matchGridSlots(matchGrid);
        if (hiddenPattern.isHiddenPatternRevealed(matchGrid, reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT))
            iWonTheLevel();
    }

    private void testForHiddenPlatternLevelWon() {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] matchGrid = levelLoader.populateMatchGrid(reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
        puzzleGrid.matchGridSlots(matchGrid);
        if (hiddenPattern.isHiddenPatternRevealed(matchGrid, reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT))
            iWonTheLevel();
    }

    private void iWonTheLevel() {
        gameOver = true;
        win = true;
        playState = PlayStates.WON_LEVEL;
        mapTile.getLevel().setLevelCompleted();
        mapTile.getLevel().setScore(Hud.getScore());
        playScreenPopUps.setLevelWonSpritePositions();
        playScreenPopUps.getLevelWonPopUp().showLevelPopUp(null);
    }

    private TweenCallback hideLevelPopUpCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
        switch (type) {
            case TweenCallback.END:
                playState = PlayStates.PLAYING;
                hud.resetWorldTime(300);
                hud.startWorldTimer();
                testForHiddenPatternRevealed(reelTiles);
        }
    }
    };

    private TweenCallback levelWonCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            dispose();
            ((WorldScreen)game.getWorldScreen()).worldScreenCallBack();
            game.setScreen(game.getWorldScreen());
        }
    };


    private void startReelSpinning(ReelTile reel, AnimatedReel animatedReel) {
        reel.setEndReel(random.nextInt(sprites.length - 1));
        reel.startSpinning();
        reelsSpinning++;
        reel.setSy(0);
        animatedReel.reinitialise();
        addScore(-1);
        pullLeverSound.play();
    }

    private void processReelTouchedWhileSpinning(ReelTile reel) {
        reel.setEndReel(reel.getCurrentReel());
        displaySpinHelp = true;
        displaySpinHelpSprite = reel.getCurrentReel();
        addScore(-1);
        pullLeverSound.play();
        reelSpinningSound.play();
    }

    private TweenCallback levelOverCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
        switch (type) {
            case TweenCallback.END:
                delegateLevelOverCallback();
        }
        }
    };

    private void delegateLevelOverCallback() {
        tweenManager.killAll();
        Hud.resetScore();
        Hud.loseLife();
        hud.resetWorldTime(300);
        hud.stopWorldTimer();
        renderer = new OrthogonalTiledMapRenderer(level);
        displaySpinHelp = false;
        inRestartLevel = false;
        loadLevel();
        createReelIntroSequence();
        playStateMachine.getStateMachine().changeState(PlayState.INTRO_SPINNING_SEQUENCE);
    }

    @Override
    public int getNumberOfReelsFalling() {
        return 0;
    }

    @Override
    public int getNumberOfReelsSpinning() {
        return reelsSpinning;
    }

    @Override
    public int getNumberOfReelsMatched() {
        return 0;
    }

    @Override
    public int getNumberOfReelsFlashing() {
        return flashSlots.getNumberOfReelsFlashing();
    }

    @Override
    public int getNumberOfReelsToDelete() {
        return flashSlots.getNumberOfReelsToDelete();
    }

    @Override
    public boolean areReelsFalling() {
        return false;
    }

    @Override
    public boolean areReelsSpinning() {
        return reelsSpinning > 0;
    }

    @Override
    public boolean areReelsFlashing() {
        return flashSlots.areReelsFlashing();
    }

    @Override
    public boolean areReelsStartedFlashing() {
        return flashSlots.areReelsStartedFlashing();
    }

    @Override
    public boolean isFinishedMatchingSlots() {
        return flashSlots.isFinishedMatchingSlots();
    }

    @Override
    public boolean areReelsDeleted() {
        return (levelDoor.getLevelType().equals(LevelCreator.MINI_SLOT_MACHINE_TYPE)) ?
                false : flashSlots.areReelsDeleted();
    }

    @Override
    public void setReelsAreFlashing(boolean reelsAreFlashing) {
        flashSlots.setReelsAreFlashing(reelsAreFlashing);
    }

    public void updateState(float delta) {
    }

    private void update(float delta) {
        playStateMachine.update();
        tweenManager.update(delta);
        renderer.setView(camera);
        updateAnimatedReels(delta);
        hud.update(delta);
        if (hud.getWorldTime() == 0) {
            if ((Hud.getLives() > 0) & (!inRestartLevel)) {
                inRestartLevel = true;
                playState = PlayStates.LEVEL_LOST;
                playScreenPopUps.setLevelLostSpritePositions();
                playScreenPopUps.getLevelLostPopUp().showLevelPopUp(null);
            } else
                gameOver = true;
        }
        if ((gameOver) & (!win) & (Hud.getLives() == 0)) {
            dispose();
            game.setScreen(new EndOfGameScreen(game));
        }
    }

    private void updateAnimatedReels(float delta) {
        for (AnimatedReel animatedReel : animatedReels)
            animatedReel.update(delta);
    }

    @Override
    public void start() {
    }

    @Override
    public boolean isStopped() {
        return false;
    }

    @Override
    public void render(float delta) {
        if (show) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            if (isLoaded)
                renderGame(delta);
            else
                isLoaded = isAssetsLoaded();
        }
    }

    private void renderGame(float delta) {
        update(delta);
        handleInput();
        renderer.render();
        renderMainGameElements();
        drawCurrentPlayState();
        renderHud();
        stage.draw();
    }

    private boolean isAssetsLoaded() {
        if (game.annotationAssetManager.getProgress() < 1) {
            game.annotationAssetManager.update();
            return false;
        } else
            return true;
    }

    private void renderMainGameElements() {
        game.batch.begin();
        renderHiddenPattern();
        renderAnimatedReels();
        renderSlotHandle();
        renderScore();
        renderSpinHelper();
        game.batch.end();
        game.batch.begin();
        renderMacthedRows();
        game.batch.end();
        renderWorld();
        renderRayHandler();
        renderLightButtons();
    }

    private void renderMacthedRows() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        for (Array<Vector2> matchedRow : rowMacthesToDraw) {
            if (matchedRow.size >= 2) {
                for (int i = 0; i < matchedRow.size - 1; i++)
                    shapeRenderer.rectLine(matchedRow.get(i    ).x, matchedRow.get(i    ).y,
                                           matchedRow.get(i + 1).x, matchedRow.get(i + 1).y,
                                    2);
            }
        }
        shapeRenderer.end();
    }

    private void renderHud() {
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    private void renderHiddenPattern() {
        if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
            drawPlayingCards(game.batch);
    }

    private void renderWorld() {
        debugRenderer.render(world, lightViewport.getCamera().combined);
    }

    private void renderRayHandler() {
        rayHandler.setCombinedMatrix((OrthographicCamera) lightViewport.getCamera());
        rayHandler.updateAndRender();
    }

    private void renderLightButtons() {
        game.batch.setProjectionMatrix(lightViewport.getCamera().combined);
        game.batch.begin();
        for (HoldLightButton lightButton : holdLightButtons)
            lightButton.getSprite().draw(game.batch);
        game.batch.end();
    }

    private void renderAnimatedReels() {
        for (AnimatedReel animatedReel : animatedReels)
            if (!animatedReel.getReel().isReelTileDeleted())
                animatedReel.draw(game.batch);
    }

    private void renderSlotHandle() {
        for (SlotHandleSprite slotHandle : slotHandles)
            slotHandle.draw(game.batch);
    }

    private void renderSpinHelper() {
        if (displaySpinHelp)
            sprites[displaySpinHelpSprite].draw(game.batch);
    }

    private void renderScore() {
        for (Score score : scores)
            score.render(game.batch);
    }

    private void drawCurrentPlayState() {
        game.batch.setProjectionMatrix(camera.combined);
        switch (playState) {
            case INTRO_POPUP:
                playScreenPopUps.getLevelPopUp().draw(game.batch);
                break;
            case LEVEL_LOST:
                playScreenPopUps.getLevelLostPopUp().draw(game.batch);
                break;
            case WON_LEVEL:
                playScreenPopUps.getLevelWonPopUp().draw(game.batch);
                break;
            default:
                break;
        }
    }

    @Override
    public void show() {
        this.show = true;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "show() called.");
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,  height);
    }

    @Override
    public void pause() {
        this.show = false;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "pause() called.");
    }

    @Override
    public void resume() {
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "resume() called.");
    }

    @Override
    public void hide() {
        this.show = false;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "hide() called.");
    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
        chaChingSound.dispose();
    }

    private void drawPlayingCards(SpriteBatch spriteBatch) {
        if (hiddenPattern instanceof HiddenPlayingCard)
            for (Card card : ((HiddenPlayingCard) hiddenPattern).getCards())
                card.draw(spriteBatch);
    }

    @Override
    public AnnotationAssetManager getAnnotationAssetManager() {
        return game.annotationAssetManager;
    }

    @Override
    public ReelSprites getReelSprites() {
        return reelSprites;
    }

    @Override
    public Texture getSlotReelScrollTexture() {
        return slotReelScrollTexture;
    }

    @Override
    public TweenManager getTweenManager() {
        return tweenManager;
    }

    @Override
    public TextureAtlas getSlothandleAtlas() {
        return game.annotationAssetManager.get(AssetsAnnotation.SLOT_HANDLE);
    }
}