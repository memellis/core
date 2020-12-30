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
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.audio.AudioManager;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.ScoreAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayInterface;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayState;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStateMachine;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle2d.level.FlashSlots;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.card.Card;
import com.ellzone.slotpuzzle2d.level.creator.LevelCallback;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreator;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorInjectionInterface;
import com.ellzone.slotpuzzle2d.level.creator.LevelLoader;
import com.ellzone.slotpuzzle2d.level.creator.LevelObjectCreatorEntityHolder;
import com.ellzone.slotpuzzle2d.level.hidden.HiddenPattern;
import com.ellzone.slotpuzzle2d.level.hidden.HiddenPlayingCard;
import com.ellzone.slotpuzzle2d.level.map.MapLevelNameComparator;
import com.ellzone.slotpuzzle2d.level.popups.PlayScreenPopUps;
import com.ellzone.slotpuzzle2d.level.sequence.PlayScreenIntroSequence;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.HoldLightButton;
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.SlotHandleSprite;
import com.ellzone.slotpuzzle2d.sprites.score.Score;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.FrameRate;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.utils.TimeStamp;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import java.sql.Timestamp;
import java.util.Random;

import aurelienribon.tweenengine.equations.Quad;
import box2dLight.RayHandler;

import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.PIXELS_PER_METER;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_HEIGHT;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_WIDTH;
import static com.ellzone.slotpuzzle2d.level.creator.LevelCreator.HIDDEN_PATTERN_LEVEL_TYPE;
import static com.ellzone.slotpuzzle2d.level.creator.LevelCreator.PLAYING_CARD_LEVEL_TYPE;
import static com.ellzone.slotpuzzle2d.messaging.MessageType.PauseAudio;
import static com.ellzone.slotpuzzle2d.messaging.MessageType.PlayAudio;
import static com.ellzone.slotpuzzle2d.messaging.MessageType.StopAudio;

public class PlayScreen implements Screen, PlayInterface, LevelCreatorInjectionInterface {
    public static final int TILE_WIDTH = 40;
    public static final int TILE_HEIGHT = 40;
    public static final int GAME_LEVEL_WIDTH = 12;
    public static final int GAME_LEVEL_HEIGHT = 9;
    public static final int SLOT_REEL_OBJECT_LAYER = 2;
    public static final float PUZZLE_GRID_START_X = 160.0f;
    public static final float PUZZLE_GRID_START_Y = 40.0f;
    public static final String SLOTPUZZLE_SCREEN = "PlayScreen";
    public static final int LEVEL_TIME_LENGTH_IN_SECONDS = 300;
    public static final String WIDTH_KEY = "width";
    public static final String HEIGHT_KEY = "height";

    protected SlotPuzzle game;
    protected Viewport viewport, lightViewport;
    protected OrthographicCamera camera = new OrthographicCamera();
    protected LevelDoor levelDoor;
    protected TweenManager tweenManager = new TweenManager();
    protected Sprite[] sprites;
    protected Array<AnimatedReel> animatedReels;
    protected Array<ReelTile> reelTiles;
    protected int reelsSpinning;
    protected MapTile mapTile;
    protected Hud hud;
    protected Random random;
    protected PlayStates playState;
    protected PlayStateMachine playStateMachine;
    protected PlayScreenPopUps playScreenPopUps;
    protected FlashSlots flashSlots;
    protected boolean displaySpinHelp;
    protected int displaySpinHelpSprite;
    protected boolean gameOver = false;
    protected Array<Score> scores;
    protected Sound chaChingSound,
                    pullLeverSound,
                    reelSpinningSound,
                    reelStoppedSound;

    private LevelLoader levelLoader;
    protected Stage stage;
    private float sW, sH;
    private TextureAtlas tilesAtlas;
    protected boolean isLoaded = false;
    protected TiledMap level;
    protected OrthogonalTiledMapRenderer renderer;
    private boolean inRestartLevel = false;
    private boolean win = false;
    private int touchX, touchY;
    private BitmapFont font;
    private HiddenPattern hiddenPattern;
    private int mapWidth;
    private int mapHeight;
    protected boolean show = false;
    private PlayScreenIntroSequence playScreenIntroSequence;
    protected World world;
    private Box2DDebugRenderer debugRenderer;
    private RayHandler rayHandler;
    private Array<HoldLightButton> holdLightButtons;
    private Array<SlotHandleSprite> slotHandles;
    private Texture slotReelScrollTexture;
    protected ReelSprites reelSprites;
    private int[][] reelGrid = new int[3][3];
    private Array<Array<Vector2>> rowMacthesToDraw;
    protected ShapeRenderer shapeRenderer;
    protected FrameRate framerate;
    protected AudioManager audioManager;
    protected MessageManager messageManager;
    private int currentReel = 0;

    public PlayScreen(SlotPuzzle game, LevelDoor levelDoor, MapTile mapTile) {
        this.game = game;
        this.levelDoor = levelDoor;
        this.mapTile = mapTile;
        createPlayScreen();
        playStateMachine.getStateMachine().changeState(PlayState.INTRO_SPINNING_SEQUENCE);
    }

    protected void createPlayScreen() {
        initialisePlayFiniteStateMachine();
        playState = PlayStates.INITIALISING;
        initialiseWorld();
        initialiseDependencies();
        setupPlayScreen();
        messageManager = setUpMessageManager();
        createReelIntroSequence();
    }

    protected void initialisePlayFiniteStateMachine() {
        playStateMachine = new PlayStateMachine();
        playStateMachine.setConcretePlay(this);
        playStateMachine.getStateMachine().changeState(PlayState.INITIALISING);
    }

    protected void initialiseDependencies() {
        initialiseScreen();
        initialiseTweenEngine();
        getAssets(game.annotationAssetManager);
        createSprites();
        slotReelScrollTexture = createSlotReelScrollTexture();
        audioManager = new AudioManager(game.annotationAssetManager);
    }

    protected MessageManager setUpMessageManager() {
        MessageManager messageManager = MessageManager.getInstance();
        messageManager.addListeners(audioManager,
                PlayAudio.index,
                StopAudio.index,
                PauseAudio.index);
        return messageManager;
    }

    protected void setupPlayScreen() {
        loadLevel();
        initialisePlayScreen();
        initialiseHud();
    }

    private Texture createSlotReelScrollTexture() {
        Pixmap slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        return new Texture(slotReelScrollPixmap);
    }

    protected void initialiseWorld() {
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
        framerate = new FrameRate();
    }

    private void loadLevel() {
        LevelObjectCreatorEntityHolder levelObjectCreator = new LevelObjectCreatorEntityHolder(this, world, rayHandler);
        Array<RectangleMapObject> extractedLevelRectangleMapObjects = extractLevelAssets(level);
        levelObjectCreator.createLevel(extractedLevelRectangleMapObjects);
        getLevelEntities(levelObjectCreator);
        levelLoader = getLevelLoader();
        levelLoader.createLevel(GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
        reelsSpinning = reelTiles.size;
        hiddenPattern = levelLoader.getHiddenPattern();
        getMapProperties(level);
        flashSlots = new FlashSlots(tweenManager, mapWidth, mapHeight, reelTiles);
    }

    protected void getLevelEntities(LevelObjectCreatorEntityHolder levelObjectCreator) {
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
        return level.getLayers().get(SLOT_REEL_OBJECT_LAYER).
                    getObjects().getByType(RectangleMapObject.class);
    }

    protected LevelLoader getLevelLoader() {
        LevelLoader levelLoader =
                new LevelLoader(game.annotationAssetManager, levelDoor, mapTile, animatedReels);
        levelLoader.setStoppedSpinningCallback(stoppedSpinningCallback);
        levelLoader.setStoppedFlashingCallback(stoppedFlashingCallback);
        return levelLoader;
    }

    private LevelCallback stoppedSpinningCallback = new LevelCallback() {
        @Override
        public void onEvent (ReelTile source) {
            playSound(AssetsAnnotation.SOUND_REEL_STOPPED);
            reelsSpinning--;
            if (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY) {
                if (reelsSpinning < 1) {
                    if (levelDoor.getLevelType().equals(LevelCreator.HIDDEN_PATTERN_LEVEL_TYPE))
                        if (isHiddenPatternRevealed(reelTiles))
                            iWonTheLevel();
                    if (levelDoor.getLevelType().equals(LevelCreator.PLAYING_CARD_LEVEL_TYPE))
                        if (isHiddenPlayingCardsRevealed(reelTiles))
                            iWonTheLevel();
                }
            }
        }
    };

    protected LevelCallback stoppedFlashingCallback = new LevelCallback() {
        @Override
        public void onEvent(ReelTile source) {
            if ((levelDoor.getLevelType().equals(LevelCreator.HIDDEN_PATTERN_LEVEL_TYPE)) ||
                (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))) {
                if (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY)
                   if (testForAnyLonelyReels(reelTiles)) {
                        win = false;
                        if (hud.getLives() > 0) {
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
        mapWidth = mapProperties.get(WIDTH_KEY, Integer.class);
        mapHeight = mapProperties.get(HEIGHT_KEY, Integer.class);
    }

    private void createReelIntroSequence() {
        createStartReelTimer();
        playState = PlayStates.INTRO_SEQUENCE;
        playScreenIntroSequence = new PlayScreenIntroSequence(reelTiles, tweenManager);
        playScreenIntroSequence.createReelIntroSequence(introSequenceCallback);
    }

    private void createStartReelTimer() {
        Timer.schedule(new Timer.Task(){
                           @Override
                           public void run() {
                               startAReel();
                           }
                       }
                , 0.0f
                , 0.02f
                , reelTiles.size
        );
    }

    private void startAReel() {
        if (currentReel < reelTiles.size) {
            animatedReels.get(currentReel).setupSpinning();
            reelTiles.get(currentReel++).setSpinning(true);
        }
    }

    protected TweenCallback introSequenceCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            delegateIntroSequenceCallback(type, (ReelTile) source.getUserData());
        }
    };

    protected void delegateIntroSequenceCallback(int type, ReelTile reelTile) {
        switch (type) {
             case TweenCallback.END:
                playState = PlayStates.INTRO_POPUP;
                playScreenPopUps.setPopUpSpritePositions();
                playScreenPopUps.getLevelPopUp().showLevelPopUp(null);
                break;
        }
    }

    private boolean isHiddenPatternRevealed(Array<ReelTile> reelTiles) {
        TupleValueIndex[][] matchGrid = flashSlots.flashSlots(reelTiles);
        return hiddenPattern.isHiddenPatternRevealed(matchGrid, reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
    }

    private boolean isHiddenPlayingCardsRevealed(Array<ReelTile> reelTiles) {
        TupleValueIndex[][] matchGrid = flashSlots.flashSlots(reelTiles);
        return hiddenPattern.isHiddenPatternRevealed(matchGrid, reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
    }

    private boolean testForAnyLonelyReels(Array<ReelTile> levelReel) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] grid = levelLoader.populateMatchGrid(levelReel, GAME_LEVEL_WIDTH , GAME_LEVEL_HEIGHT);
        return puzzleGrid.anyLonelyTiles(grid);
    }

    private void deleteReelAnimation(ReelTile source) {
        Timeline.createSequence()
            .beginParallel()
            .delay(random.nextFloat() * 2.0f)
            .push(SlotPuzzleTween.to(source, SpriteAccessor.SCALE_XY, 0.5f).target(6, 6).ease(Quad.IN))
            .push(SlotPuzzleTween.to(source, SpriteAccessor.OPACITY, 0.5f).target(0).ease(Quad.IN))
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
        hud.addScore((reel.getEndReel() + 1) * reel.getScore());
        playSound(AssetsAnnotation.SOUND_REEL_STOPPED);
        playSound(AssetsAnnotation.SOUND_CHA_CHING);
        reel.deleteReelTile();
        flashSlots.deleteAReel();
        if (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY) {
            if (levelDoor.getLevelType().equals(LevelCreator.PLAYING_CARD_LEVEL_TYPE))
                testPlayingCardLevelWon();
            if (levelDoor.getLevelType().equals(LevelCreator.HIDDEN_PATTERN_LEVEL_TYPE))
                testForHiddenPlatternLevelWon();
        }
    }

    protected void reelScoreAnimation(ReelTile source) {
        Score score = new Score(source.getX(), source.getY(), (source.getEndReel() + 1) * source.getScore());
        scores.add(score);
        Timeline.createSequence()
                .beginParallel()
                .delay(random.nextFloat()*2.0f)
                .push(SlotPuzzleTween.to(score, ScoreAccessor.POS_XY, 2.5f).targetRelative(random.nextInt(20), random.nextInt(160)).ease(Quad.IN))
                .push(SlotPuzzleTween.to(score, ScoreAccessor.SCALE_XY, 2.5f).target(2.0f, 2.0f).ease(Quad.IN))
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

    protected boolean isOver(Sprite sprite, float x, float y) {
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
                        processReelTouchedWhileSpinning(reelTile, reelTile.getCurrentReel(), reelTile.getCurrentReel());
                } else
                if (!reelTile.getFlashTween())
                    startReelSpinning(reelTile, animatedReel);
            }
        } else
            Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r="+r+"c="+c);
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
        mapTile.getLevel().setScore(hud.getScore());
        playScreenPopUps.setLevelWonSpritePositions();
        playScreenPopUps.getLevelWonPopUp().showLevelPopUp(null);
    }

    protected TweenCallback hideLevelPopUpCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            switch (type) {
                case TweenCallback.END:
                    playState = PlayStates.PLAYING;
                    hud.resetWorldTime(LEVEL_TIME_LENGTH_IN_SECONDS);
                    hud.startWorldTimer();
                    if (levelDoor.getLevelType().equals(LevelCreator.HIDDEN_PATTERN_LEVEL_TYPE))
                        isHiddenPatternRevealed(reelTiles);
                    if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
                        isHiddenPatternRevealed(reelTiles);
            }
        }
    };

    protected TweenCallback levelWonCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            dispose();
            ((WorldScreen)game.getWorldScreen()).worldScreenCallBack(mapTile);
            game.setScreen(game.getWorldScreen());
        }
    };

    private void startReelSpinning(ReelTile reel, AnimatedReel animatedReel) {
        reel.setEndReel(random.nextInt(sprites.length - 1));
        reel.startSpinning();
        reelsSpinning++;
        reel.setSy(0);
        animatedReel.reinitialise();
        hud.addScore(-1);
        playSound(AssetsAnnotation.SOUND_PULL_LEVER);
    }

    protected void processReelTouchedWhileSpinning(ReelTile reel, int currentReel, int spinHelpSprite) {
        reel.setEndReel(currentReel);
        displaySpinHelp = true;
        displaySpinHelpSprite = spinHelpSprite;
        hud.addScore(-1);
        playSound(AssetsAnnotation.SOUND_PULL_LEVER);
        playSound(AssetsAnnotation.SOUND_REEL_SPINNING);
    }

    protected TweenCallback levelOverCallback = new TweenCallback() {
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
        hud.resetScore();
        hud.loseLife();
        hud.resetWorldTime(LEVEL_TIME_LENGTH_IN_SECONDS);
        hud.stopWorldTimer();
        renderer = new OrthogonalTiledMapRenderer(level);
        displaySpinHelp = false;
        inRestartLevel = false;
        currentReel = 0;
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
        return levelDoor.getLevelType().equals(LevelCreator.MINI_SLOT_MACHINE_LEVEL_TYPE) ?
                false : flashSlots.areReelsDeleted();
    }

    @Override
    public void setReelsAreFlashing(boolean reelsAreFlashing) {
//        flashSlots.setReelsAreFlashing(reelsAreFlashing);
    }

    public void updateState(float delta) {
    }

    protected void update(float delta) {
        playStateMachine.update();
        tweenManager.update(delta);
        renderer.setView(camera);
        updateAnimatedReels(delta);
        hud.update(delta);
        if (isOutOfTime())
            weAreOutOfTime();
        framerate.update();
        checkForGameOverCondition();
    }

    protected boolean isOutOfTime() {
        return hud.getWorldTime() == 0 && playState != PlayStates.BONUS_LEVEL_ENDED;
    }

    protected void checkForGameOverCondition() {
        if ((gameOver) & (!win) & (hud.getLives() == 0)) {
            dispose();
            game.setScreen(new EndOfGameScreen(game));
        }
    }

    protected void weAreOutOfTime() {
        if ((hud.getLives() > 0) & (!inRestartLevel)) {
            inRestartLevel = true;
            playState = PlayStates.LEVEL_LOST;
            playScreenPopUps.setLevelLostSpritePositions();
            playScreenPopUps.getLevelLostPopUp().showLevelPopUp(null);
        } else
            gameOver = true;
    }

    protected void updateAnimatedReels(float delta) {
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

    protected void renderGame(float delta) {
        update(delta);
        handleInput();
        renderer.render();
        renderMainGameElements();
        drawCurrentPlayState(delta);
        renderHud();
        stage.draw();
        framerate.render();
    }

    protected void handleInput() {
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
                if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
                    processIsTileClicked(unprojTouch.x, unprojTouch.y);
            }
        }
    }

    private boolean isAssetsLoaded() {
        if (game.annotationAssetManager.getProgress() < 1) {
            game.annotationAssetManager.update();
            return false;
        } else
            return true;
    }

    protected void renderMainGameElements() {
        game.batch.begin();
        renderHiddenPattern();
        renderAnimatedReels();
        renderScore();
        renderSpinHelper();
        game.batch.end();
        game.batch.begin();
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        renderAnimatedReelsFlash();
        game.batch.end();
        renderWorld();
        renderRayHandler();
    }

    protected void renderHud() {
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    protected void renderHiddenPattern() {
        if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
            drawPlayingCards(game.batch);
    }

    protected void renderWorld() {
        debugRenderer.render(world, lightViewport.getCamera().combined);
    }

    protected void renderRayHandler() {
        rayHandler.setCombinedMatrix((OrthographicCamera) lightViewport.getCamera());
        rayHandler.updateAndRender();
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


    protected void renderSpinHelper() {
        if (displaySpinHelp)
            sprites[displaySpinHelpSprite].draw(game.batch);
    }

    protected void renderScore() {
        for (Score score : scores)
            score.render(game.batch);
    }

    protected void drawCurrentPlayState(float delta) {
        game.batch.setProjectionMatrix(camera.combined);
        switch (playState) {
            case INTRO_POPUP:
                playScreenPopUps.getLevelPopUp().draw(game.batch);
                playScreenPopUps.getLevelPopUp().drawSpeechBubble(game.batch, delta);
                break;
            case LEVEL_LOST:
                playScreenPopUps.getLevelLostPopUp().draw(game.batch);
                playScreenPopUps.getLevelLostPopUp().drawSpeechBubble(game.batch, delta);
                break;
            case WON_LEVEL:
                playScreenPopUps.getLevelWonPopUp().draw(game.batch);
                playScreenPopUps.getLevelWonPopUp().drawSpeechBubble(game.batch, delta);
                break;
            case BONUS_LEVEL_ENDED:
                playScreenPopUps.getLevelBonusCompletedPopUp().draw(game.batch);
                playScreenPopUps.getLevelBonusCompletedPopUp().drawSpeechBubble(game.batch, delta);
            default:
                break;
        }
    }

    @Override
    public void show() {
        show = true;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "show() called.");
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,  height);
    }

    @Override
    public void pause() {
        show = false;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "pause() called.");
    }

    @Override
    public void resume() {
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "resume() called.");
    }

    @Override
    public void hide() {
        show = false;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "hide() called.");
    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
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

    private void playSound(String sound) {
        messageManager.dispatchMessage(PlayAudio.index, sound);
    }
}