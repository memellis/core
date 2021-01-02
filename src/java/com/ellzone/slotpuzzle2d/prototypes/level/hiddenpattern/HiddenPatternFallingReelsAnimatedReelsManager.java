/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.prototypes.level.hiddenpattern;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.ScoreAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayInterface;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayState;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStateMachine;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorInjectionInterface;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimple;
import com.ellzone.slotpuzzle2d.level.creator.LevelObjectCreatorEntityHolder;
import com.ellzone.slotpuzzle2d.level.map.MapLevelNameComparator;
import com.ellzone.slotpuzzle2d.level.sequence.PlayScreenIntroSequence;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.physics.ReelSink;
import com.ellzone.slotpuzzle2d.physics.contact.BoxHittingBoxContactListener;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.physics.contact.AnimatedReelsManager;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.AnimatedReelsMatrixCreator;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.score.Score;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.utils.Random;
import com.ellzone.slotpuzzle2d.utils.SlowMotion;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import box2dLight.RayHandler;

import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.PIXELS_PER_METER;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_HEIGHT;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_WIDTH;
import static com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimple.REELS_LAYER_NAME;
import static com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimpleScenario.BONUS_LEVEL_TYPE;
import static com.ellzone.slotpuzzle2d.messaging.MessageType.ReelSinkReelsLeftToFall;
import static com.ellzone.slotpuzzle2d.messaging.MessageType.ReelsLeftToFall;
import static com.ellzone.slotpuzzle2d.messaging.MessageType.SwapReelsAboveMe;
import static com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.MiniSlotMachineLevelPrototypeWithLevelCreator.MINI_SLOT_MACHINE_LEVEL_NAME;
import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_HEIGHT;
import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_WIDTH;
import static com.ellzone.slotpuzzle2d.screens.PlayScreen.LEVEL_TIME_LENGTH_IN_SECONDS;

public class HiddenPatternFallingReelsAnimatedReelsManager extends SPPrototypeTemplate
        implements LevelCreatorInjectionInterface, PlayInterface {

    public static final String LEVELS_LEVEL_7 = "levels/level 7 - 40x40.tmx";
    public static final String LEVEL_7_NAME = "1-7";

    private String logTag = SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName();
    private Array<AnimatedReel> animatedReels;
    private Array<Body> reelBoxes;
    private Texture slotReelScrollTexture;
    private TextureAtlas slotHandleAtlas;
    private Vector2 touch;
    private Random random;
    private Hud hud;
    private FitViewport lightViewport;
    private FitViewport hudViewport;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private OrthogonalTiledMapRenderer tileMapRenderer;
    private RayHandler rayHandler;
    private int reelSpriteHelp;
    private Sound pullLeverSound;
    private Sound reelSpinningSound;
    private Sound reelStoppingSound;
    private PhysicsManagerCustomBodies physics;
    private OrthographicCamera camera;
    private LevelDoor levelDoor;
    private LevelCreatorSimple levelCreator;
    private Array<ReelTile> reelTiles;
    private ShapeRenderer shapeRenderer;
    private boolean displaySpinHelp;
    private int displaySpinHelpSprite;
    private boolean slowMotionEnabed = false;
    private SlowMotion slowMotion;
    private boolean introSequenceFinished = false;
    private float slowMotionCount = 0;
    private int currentReel = 0;
    private AnimatedReelsManager animatedReelsManager;
    private int numberOfReelsToFall = 0;
    private int numberOfReelBoxesAsleep = 0;
    private int numberOfReelBoxesCreated = 0;
    private MessageManager messageManager;
    private PlayStateMachine playStateMachine;
    private boolean reelsStoppedMoving = false;
    private AnimatedReelsMatrixCreator animatedReelsMatrixCreator;
    private boolean gameOver = false;
    private boolean debug = false;

    @Override
    protected void initialiseOverride() {
        if (debug)
            Gdx.app.setLogLevel(Application.LOG_DEBUG);
        touch = new Vector2();
        setSpritePositions();
        initialisePlayFiniteStateMachine();
        initialiseWorld();
        random = Random.getInstance();
        camera = new OrthographicCamera();
        slowMotion = new SlowMotion(slowMotionEnabed);
        slotReelScrollTexture = createSlotReelScrollTexture();
        createViewPorts();
        initialisePhysics();
        initialiseLevelDoor();
        hud = setUpHud(batch);
        loadlevel();
        createIntroSequence();
        playStateMachine.getStateMachine().changeState(PlayState.INTRO_SPINNING_SEQUENCE);
        messageManager = setUpMessageManager();
        activateReelBoxes();
    }

    private void setSpritePositions() {
        for (Sprite sprite : sprites)
            sprite.setPosition(0, 80);
    }

    private void initialiseWorld() {
        world = new World(new Vector2(0, -9.8f), true);
        BoxHittingBoxContactListener contactListener = new BoxHittingBoxContactListener();
        world.setContactListener(contactListener);
        debugRenderer = new Box2DDebugRenderer();
        setupRayHandler();
    }

    private void setupRayHandler() {
        rayHandler = new RayHandler(world);
        rayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(0.2f, 0.2f, 0.2f, 0.25f);
    }

    private void initialisePhysics() {
        physics = new PhysicsManagerCustomBodies(camera, world, debugRenderer);
        createReelSink();
    }

    private void createReelSink() {
        ReelSink reelSink = new ReelSink(physics);
        reelSink.createReelSink(
                SlotPuzzleConstants.VIRTUAL_WIDTH / 2,
                SlotPuzzleConstants.VIRTUAL_HEIGHT / 2 + 20,
                12,
                9,
                40,
                40);
    }

    private void initialiseLevelDoor() {
        levelDoor = new LevelDoor();
        levelDoor.setLevelName(MINI_SLOT_MACHINE_LEVEL_NAME);
        levelDoor.setLevelType(BONUS_LEVEL_TYPE);
    }

    private void loadlevel() {
        TiledMap level = createLevel();
        initialiseReels();
        createLevelCreator(level);
        reelBoxes = levelCreator.getReelBoxes();
        setupAnimatedReelsManager();
    }

    private void setupAnimatedReelsManager() {
        animatedReelsManager = new AnimatedReelsManager(animatedReels, reelBoxes);
        animatedReelsManager.setNumberOfReelsToFall(numberOfReelsToFall);
        levelCreator.setAnimatedReelsManager(animatedReelsManager);
    }

    private void initialisePlayFiniteStateMachine() {
        playStateMachine = new PlayStateMachine();
        playStateMachine.setConcretePlay(this);
        playStateMachine.getStateMachine().changeState(PlayState.INITIALISING);
    }

    private void createLevelCreator(TiledMap level) {
        levelCreator = new LevelCreatorSimple(
                levelDoor,
                animatedReels,
                reelTiles,
                level,
                annotationAssetManager,
                (TextureAtlas) annotationAssetManager.get(AssetsAnnotation.CARDDECK),
                tweenManager,
                physics,
                GAME_LEVEL_WIDTH,
                GAME_LEVEL_HEIGHT,
                playStateMachine,
                hud);
    }

    private TiledMap createLevel() {
        LevelObjectCreatorEntityHolder levelObjectCreator =
                new LevelObjectCreatorEntityHolder(this, world, rayHandler);
        TiledMap level = getLevelAssets(annotationAssetManager);
        tileMapRenderer = new OrthogonalTiledMapRenderer(level);
        Array<RectangleMapObject> extractedLevelRectangleMapObjects = extractLevelAssets(level);
        delegateCreateLevel(levelObjectCreator, extractedLevelRectangleMapObjects);
        return level;
    }

    private void delegateCreateLevel(LevelObjectCreatorEntityHolder levelObjectCreator, Array<RectangleMapObject> extractedLevelRectangleMapObjects) {
        try {
            levelObjectCreator.createLevel(extractedLevelRectangleMapObjects);
            animatedReels = levelObjectCreator.getAnimatedReels();
            reelTiles = levelObjectCreator.getReelTiles();
        } catch (GdxRuntimeException gdxRuntimeException) {
            throw new GdxRuntimeException(gdxRuntimeException);
        }
    }

    private void initialiseReels() {
        for (ReelTile reelTile : reelTiles) {
            reelTile.startSpinning();
            reelTile.setSx(0);
            reelTile.setEndReel(Random.getInstance().nextInt(reelSprites.getSprites().length - 1));
        }
    }

    private void createViewPorts() {
        lightViewport = new FitViewport((float) VIRTUAL_WIDTH / PIXELS_PER_METER, (float) VIRTUAL_HEIGHT / PIXELS_PER_METER);
        lightViewport.getCamera().position.set( (float) VIRTUAL_WIDTH / PIXELS_PER_METER * 0.5f,
                (float) VIRTUAL_HEIGHT / PIXELS_PER_METER * 0.5f,
                0);
        lightViewport.getCamera().update();
        lightViewport.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        hudViewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, new OrthographicCamera());
    }

    private Hud setUpHud(SpriteBatch batch) {
        Hud hud = new Hud(batch);
        hud.setLevelName(LEVEL_7_NAME);
        return hud;
    }

    private Array<RectangleMapObject> extractLevelAssets(TiledMap level) {
        Array<RectangleMapObject> levelRectangleMapObjects = getRectangleMapObjectsFromLevel(level);
        MapLevelNameComparator mapLevelNameComparator = new MapLevelNameComparator();
        levelRectangleMapObjects.sort(mapLevelNameComparator);
        return levelRectangleMapObjects;
    }

    private TiledMap getLevelAssets(AnnotationAssetManager annotationAssetManager) {
        return annotationAssetManager.get(LEVELS_LEVEL_7);
    }

    private Array<RectangleMapObject> getRectangleMapObjectsFromLevel(TiledMap level) {
        return level.getLayers().get(REELS_LAYER_NAME).getObjects().getByType(RectangleMapObject.class);
    }

    private Texture createSlotReelScrollTexture() {
        Pixmap slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        return new Texture(slotReelScrollPixmap);
    }

    private void createIntroSequence() {
        createStartReelTimer();
        PlayScreenIntroSequence playScreenIntroSequence = new PlayScreenIntroSequence(getReelTilesFromAnimatedReels(animatedReels), tweenManager);
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

    private MessageManager setUpMessageManager() {
        messageManager = MessageManager.getInstance();
        messageManager.addListeners(
                animatedReelsManager,
                SwapReelsAboveMe.index,
                ReelsLeftToFall.index,
                ReelSinkReelsLeftToFall.index);
        return messageManager;
    }

    private void activateReelBoxes() {
        for (Body reelBox : reelBoxes)
            if (!((AnimatedReel) (reelBox.getUserData())).getReel().isReelTileDeleted())
                reelBox.setActive(true);
    }

    private Array<ReelTile> getReelTilesFromAnimatedReels(Array<AnimatedReel> animatedReels) {
        Array<ReelTile> reelTiles = new Array<>();
         for (AnimatedReel animatedReel : animatedReels)
            reelTiles.add(animatedReel.getReel());
        return reelTiles;
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
                hud.resetWorldTime(LEVEL_TIME_LENGTH_IN_SECONDS);
                hud.resetWorldTime(120);
                hud.startWorldTimer();
                levelCreator.createStartRandomReelBoxTimer();
                levelCreator.allReelsHaveStoppedSpinning();
                introSequenceFinished = true;
                break;
        }
    }

    @Override
    protected void loadAssetsOverride() {
        slotHandleAtlas = annotationAssetManager.get(AssetsAnnotation.SLOT_HANDLE);
        pullLeverSound = annotationAssetManager.get(AssetsAnnotation.SOUND_PULL_LEVER);
        reelSpinningSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_SPINNING);
        reelStoppingSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_STOPPED);
    }

    @Override
    protected void initialiseUniversalTweenEngineOverride() {
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        SlotPuzzleTween.registerAccessor(Score.class, new ScoreAccessor());
    }

    @Override
    protected void initialiseScreenOverride() {
        shapeRenderer = new ShapeRenderer();
    }

    @Override
    protected void disposeOverride() {
        debugRenderer.dispose();
        rayHandler.dispose();
        world.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        lightViewport.update(width, height);
        hudViewport.update(width, height);
    }

    @Override
    protected void updateOverride(float dt) {
        if (slowMotion.isSlowMotionEnabled() & slowMotion.isSlowMotionTimerEnded(dt))
            return;
        deletegateUpdates(dt);
        if (!gameOver)
            handleInput();
    }

    private void deletegateUpdates(float dt) {
        tweenManager.update(dt);
        levelCreator.update(dt);
        hud.update(dt);
        if (isOutOfTime())
            weAreOutOfTime();
        playStateMachine.update();
        updateReels(dt);
    }

    private boolean isOutOfTime() {
        return hud.getWorldTime() == 0;
    }

    private void weAreOutOfTime() {
       if (gameOver)
           return;
       if (levelCreator.getReelsToFall().size == 0 &
            levelCreator.getScores().size == 0 &
            levelCreator.getNumberOfReelsSpinning() == 0 &
            !levelCreator.getAreReelsFlashing() &
            levelCreator.getReelsToCreated() &
            isReelsStoppedMoving()) {
                gameOver = true;
                levelCreator.setEndOfGame(true);
                checkIfWeWon();
        }
    }

    private void checkIfWeWon() {
        System.out.println("Check if we won");
    }

    private void updateReels(float dt) {
        for (AnimatedReel animatedReel : animatedReels)
            animatedReel.update(dt);
        updateReelBoxes();
    }

    private void updateReelBoxes() {
        if (animatedReelsManager.getNumberOfReelsToFall() == 0)
            if (animatedReelsManager.getReelsStoppedFalling() > 0)
                checkForReelsStoppedFalling();
    }

    private void checkForReelsStoppedFalling() {
        animatedReelsManager.checkForReelsStoppedFalling();
    }

    @Override
    protected void renderOverride(float dt) {
        delegateRender();
        delegateDraw();
        renderAnimatedReelsFlash();
    }

    private void delegateRender() {
        tileMapRenderer.render();
        renderReelBoxes(batch, reelBoxes);
        levelCreator.render(batch, 0);
        renderSpinHelper();
        if (isReelsStoppedMoving())
            processReelsStoppedMoving();
        else
            reelsStoppedMoving = false;
        renderRayHandler();
        renderHud(batch);
        renderWorld();
    }

    private void delegateDraw() {
        physics.draw(batch);
        hud.stage.draw();
        stage.draw();
    }

    private void processReelsStoppedMoving() {
        if (animatedReelsManager.getNumberOfReelsToFall() <= 0 &
            levelCreator.getNumberOfReelsSpinning() < 1) {
            if (!reelsStoppedMoving) {
                reelsStoppedMoving = true;
                levelCreator.allReelsHaveStoppedSpinning();
            }
        }
    }

    private boolean isReelsStoppedMoving() {
        return numberOfReelBoxesAsleep == numberOfReelBoxesCreated;
    }

    protected void renderSpinHelper() {
        if (displaySpinHelp) {
            batch.begin();
            sprites[displaySpinHelpSprite].draw(batch);
            batch.end();
        }
    }

    private void renderRayHandler() {
        rayHandler.setCombinedMatrix((OrthographicCamera) lightViewport.getCamera());
        rayHandler.updateAndRender();
    }

    private void renderHud(SpriteBatch batch) {
        batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    private void renderWorld() {
        debugRenderer.render(world, lightViewport.getCamera().combined);
    }

    private void renderReelBoxes(SpriteBatch batch, Array<Body> reelBoxes) {
        batch.begin();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        numberOfReelBoxesCreated = 0;
        numberOfReelBoxesAsleep = 0;
        for (Body reelBox : reelBoxes) {
            if (reelBox != null) {
                float angle = MathUtils.radiansToDegrees * reelBox.getAngle();
                AnimatedReel animatedReel = (AnimatedReel) reelBox.getUserData();
                if (!animatedReel.getReel().isReelTileDeleted()) {
                    renderReel(batch, reelBox, angle, animatedReel.getReel());
                    numberOfReelBoxesCreated++;
                }
                if (!reelBox.isAwake())
                    numberOfReelBoxesAsleep++;
            }
        }
        batch.end();
    }

    private void renderReel(SpriteBatch batch, Body reelBox, float angle, ReelTile reelTile) {
        reelTile.setPosition(
                reelBox.getPosition().x * 100 - 20,
                reelBox.getPosition().y * 100 - 20);
        reelTile.updateReelFlashSegments(
                reelBox.getPosition().x * 100 - 20,
                reelBox.getPosition().y * 100 - 20);
        reelTile.setOrigin(0, 0);
        reelTile.setSize(40, 40);
        reelTile.setRotation(angle);
        reelTile.draw(batch);
    }

    private void renderAnimatedReelsFlash() {
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        for (AnimatedReel animatedReel : animatedReels)
            if (!animatedReel.getReel().isReelTileDeleted())
                if (animatedReel.getReel().getFlashState() == ReelTile.FlashState.FLASH_ON)
                    animatedReel.draw(shapeRenderer);
    }

    public void handleInput() {
        if (Gdx.input.justTouched())
            if (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY)
                processIsTileClicked();

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            handleDForDebugKeyPressed();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.M))
            handleMforMatrixPrintKeyPressed();
    }

    private void processIsTileClicked() {
        Vector2 tileClicked = getTileClicked();
        processTileClicked(tileClicked);
    }

    private void handleDForDebugKeyPressed() {
        System.out.println("d key pressed - use this to insert a breakpoint");
    }

    private void handleMforMatrixPrintKeyPressed() {
        System.out.println();
        levelCreator.printMatchGrid(reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
    }

    private Vector2 getTileClicked() {
        int touchX = Gdx.input.getX();
        int touchY = Gdx.input.getY();
        Vector2 newPoints = new Vector2(touchX, touchY);
        newPoints = viewport.unproject(newPoints);
        int c = (int) (newPoints.x - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
        int r = (int) (newPoints.y - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
        r = GAME_LEVEL_HEIGHT - 1 - r ;
        return new Vector2(c, r);
    }

    private void processTileClicked(Vector2 tileClicked) {
        int r = (int) tileClicked.y;
        int c = (int) tileClicked.x;
        if (r>=0 && r<GAME_LEVEL_HEIGHT && c>=0 && c<GAME_LEVEL_WIDTH) {
            ReelTileGridValue[][] grid = levelCreator.populateMatchGrid(reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
            if (grid[r][c] != null) {
                ReelTile reel = reelTiles.get(grid[r][c].index);
                AnimatedReel animatedReel = levelCreator.getAnimatedReels().get(grid[r][c].index);
                processReelClicked(reel, animatedReel);
            }
        }
    }

    private void processReelClicked(ReelTile reel, AnimatedReel animatedReel) {
        if (!reel.isReelTileDeleted()) {
            if (reel.isSpinning()) {
                if (animatedReel.getDampenedSineState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE)
                    setEndReelWithCurrentReel(reel);
            } else
            if (!reel.getFlashTween()) {
                startReelSpinning(reel, animatedReel);
            }
        }
    }

    private void setEndReelWithCurrentReel(ReelTile reel) {
        reel.setEndReel(reel.getCurrentReel());
        displaySpinHelp = true;
        displaySpinHelpSprite = reel.getCurrentReel();
        hud.addScore(-1);
        pullLeverSound.play();
        reelSpinningSound.play();
    }

    private void startReelSpinning(ReelTile reel, AnimatedReel animatedReel) {
        reel.setEndReel(Random.getInstance().nextInt(reelSprites.getSprites().length - 1));
        reel.startSpinning();
        levelCreator.setNumberOfReelsSpinning(levelCreator.getNumberOfReelsSpinning() + 1);
        reel.setSy(0);
        animatedReel.reinitialise();
        hud.addScore(-1);
        if (pullLeverSound != null)
            pullLeverSound.play();
    }

    public LevelCreatorSimple getLevelCreator() {
        return levelCreator;
    }

    @Override
    public AnnotationAssetManager getAnnotationAssetManager() {
        return annotationAssetManager;
    }

    @Override
    public ReelSprites getReelSprites() {
        return reelSprites;
    }

    public void setReelSprites(ReelSprites reelSprites ) {
        this.reelSprites = reelSprites;
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
        return slotHandleAtlas;
    }

    @Override
    public int getNumberOfReelsFalling() {
        return 0;
    }

    @Override
    public int getNumberOfReelsSpinning() {
        return levelCreator.getNumberOfReelsSpinning();
    }

    @Override
    public int getNumberOfReelsMatched() {
        return 0;
    }

    @Override
    public int getNumberOfReelsFlashing() {
        return levelCreator.getNumberOfReelsFlashing();
    }

    @Override
    public int getNumberOfReelsToDelete() {
        return 0;
    }

    @Override
    public boolean areReelsFalling() {
        return false;
    }

    @Override
    public boolean areReelsSpinning() {
        return levelCreator.getNumberOfReelsSpinning() > 0;
    }

    @Override
    public boolean areReelsFlashing() {
        return false;
    }

    @Override
    public boolean areReelsStartedFlashing() {
        return levelCreator.getAreReelsStartedFlashing();
    }

    @Override
    public boolean isFinishedMatchingSlots() {
        return levelCreator.isFinishedMatchingSlots();
    }

    @Override
    public boolean areReelsDeleted() {
        return false;
    }

    @Override
    public void setReelsAreFlashing(boolean reelsAreFlashing) {
        //levelCreator.setReelsAreFlashing(reelsAreFlashing);
    }

    @Override
    public void updateState(float delta) {
    }

    @Override
    public void start() {
    }

    @Override
    public boolean isStopped() {
        return false;
    }
}
