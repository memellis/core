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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.math.Vector3;
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
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorInjectionInterface;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimple;
import com.ellzone.slotpuzzle2d.level.creator.LevelObjectCreatorEntityHolder;
import com.ellzone.slotpuzzle2d.level.map.MapLevelNameComparator;
import com.ellzone.slotpuzzle2d.level.sequence.PlayScreenIntroSequence;
import com.ellzone.slotpuzzle2d.physics.contact.B2ContactListenerReelSink;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.physics.ReelSink;
import com.ellzone.slotpuzzle2d.physics.ReelSinkInterface;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
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

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import box2dLight.RayHandler;

import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.PIXELS_PER_METER;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_HEIGHT;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_WIDTH;
import static com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimple.REELS_LAYER_NAME;
import static com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimpleScenario.BONUS_LEVEL_TYPE;
import static com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.MiniSlotMachineLevelPrototypeWithLevelCreator.MINI_SLOT_MACHINE_LEVEL_NAME;
import static com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine.MiniSlotMachineLevelPrototypeWithLevelCreator.numberOfReelsToHitSinkBottom;
import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_HEIGHT;
import static com.ellzone.slotpuzzle2d.screens.PlayScreen.GAME_LEVEL_WIDTH;

public class HiddenPatternWithFallingReels extends SPPrototypeTemplate
        implements LevelCreatorInjectionInterface, ReelSinkInterface {

    public static final String LEVELS_LEVEL_7 = "levels/level 7 - 40x40.tmx";
    public static final String LEVEL_7_NAME = "1-7";

    private String logTag = SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName();
    private Array<AnimatedReel> reels;
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
    private int numberOfReelsAboveHitsIntroSpinning;
    private Array<ReelTile> reelTiles;
    private ShapeRenderer shapeRenderer;
    private boolean displaySpinHelp;
    private int displaySpinHelpSprite;
    private boolean slowMotion = false;
    private boolean introSequenceFinished = false;
    private float slowMotionCount = 0;
    private int currentReel = 0;

    @Override
    protected void initialiseOverride() {
        touch = new Vector2();
        initialiseWorld();
        random = Random.getInstance();
        camera = new OrthographicCamera();
        slotReelScrollTexture = createSlotReelScrollTexture();
        createViewPorts();
        initialisePhysics();
        initialiseLevelDoor();
        loadlevel();
        hud = setUpHud(batch);
        levelCreator.setPlayState(PlayStates.INTRO_SPINNING);
        createIntroSequence();
    }

    private void initialiseWorld() {
        world = new World(new Vector2(0, -9.8f), true);
        world.setContactListener(new B2ContactListenerReelSink(this));
        debugRenderer = new Box2DDebugRenderer();
        rayHandler = new RayHandler(world);
        rayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(0.2f, 0.2f, 0.2f, 0.25f);
    }

    private void initialisePhysics() {
        physics = new PhysicsManagerCustomBodies(camera, world, debugRenderer);
        ReelSink reelSink = new ReelSink(physics);
        reelSink.createReelSink(
                SlotPuzzleConstants.VIRTUAL_WIDTH / 2,
                SlotPuzzleConstants.VIRTUAL_HEIGHT / 2 + 20,
                12,
                9,
                40,
                40,
                this);
    }

    private void initialiseLevelDoor() {
        levelDoor = new LevelDoor();
        levelDoor.setLevelName(MINI_SLOT_MACHINE_LEVEL_NAME);
        levelDoor.setLevelType(BONUS_LEVEL_TYPE);
    }

    private void loadlevel() {
        LevelObjectCreatorEntityHolder levelObjectCreator = new LevelObjectCreatorEntityHolder(this, world, rayHandler);
        TiledMap level = getLevelAssets(annotationAssetManager);
        tileMapRenderer = new OrthogonalTiledMapRenderer(level);
        Array<RectangleMapObject> extractedLevelRectangleMapObjects = extractLevelAssets(level);
        try {
            levelObjectCreator.createLevel(extractedLevelRectangleMapObjects);
            reels = levelObjectCreator.getAnimatedReels();
            reelTiles = levelObjectCreator.getReelTiles();
        } catch (GdxRuntimeException gdxRuntimeException) {
            throw new GdxRuntimeException(gdxRuntimeException);
        }
        initialiseReels();

        levelCreator = new LevelCreatorSimple(
                levelDoor,
                reels,
                reelTiles,
                level,
                annotationAssetManager,
                (TextureAtlas) annotationAssetManager.get(AssetsAnnotation.CARDDECK),
                tweenManager,
                physics,
                GAME_LEVEL_WIDTH,
                GAME_LEVEL_HEIGHT,
                PlayStates.INITIALISING);
        reelBoxes = levelCreator.getReelBoxes();
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

    @Override
    public void dealWithReelHittingReelSink(ReelTile reelTile) {
        numberOfReelsToHitSinkBottom++;
        if (numberOfReelsToHitSinkBottom < GAME_LEVEL_WIDTH)
            return;

        if (levelCreator.getPlayState() == PlayStates.INTRO_SPINNING)
            levelCreator.setHitSinkBottom(true);

        if (levelCreator.getPlayState() == PlayStates.REELS_SPINNING ||
            levelCreator.getPlayState() == PlayStates.INTRO_FLASHING ||
            levelCreator.getPlayState() == PlayStates.REELS_FLASHING ||
            levelCreator.getPlayState() == PlayStates.PLAYING) {

            if (levelCreator.getPlayState() == PlayStates.REELS_SPINNING ||
               (levelCreator.getPlayState() == PlayStates.PLAYING))
                System.out.println("I've hit sink bottom");

            int r = PuzzleGridTypeReelTile.getRowFromLevel(reelTile.getDestinationY(), GAME_LEVEL_HEIGHT);
            int c = PuzzleGridTypeReelTile.getColumnFromLevel(reelTile.getDestinationX());

            int currentTileAtBottomIndex = levelCreator.findReel((int)reelTile.getDestinationX(), 40);
            if (currentTileAtBottomIndex != -1) {
                swapReelsAboveMe(reelTile);
                reelsLeftToFall(r, c);
            }
        }
    }

    @Override
    public void dealWithReelHittingReel(ReelTile reelTileA, ReelTile reelTileB)  {
        int rA, cA, rB, cB;

        if (numberOfReelsToHitSinkBottom < GAME_LEVEL_WIDTH)
            return;

        if (!introSequenceFinished)
            return;

        rA = PuzzleGridTypeReelTile.getRowFromLevel(reelTileA.getDestinationY(), GAME_LEVEL_HEIGHT);
        cA = PuzzleGridTypeReelTile.getColumnFromLevel(reelTileA.getDestinationX());
        rB = PuzzleGridTypeReelTile.getRowFromLevel(reelTileB.getDestinationY(), GAME_LEVEL_HEIGHT);
        cB = PuzzleGridTypeReelTile.getColumnFromLevel(reelTileB.getDestinationX());

        if ((Math.abs(rA - rB) == 1) & (cA == cB))
            processReelTileHit(reelTileA);

        if ((Math.abs(rA - rB) == 1) & (cA == cB))
            processReelTileHit(reelTileB);

        if (levelCreator.getPlayState() == PlayStates.INTRO_SPINNING ||
            levelCreator.getPlayState() == PlayStates.INTRO_FLASHING ||
            levelCreator.getPlayState() == PlayStates.REELS_FLASHING ||
            levelCreator.getPlayState() == PlayStates.PLAYING) {
            if  (cA == cB) {
                if (Math.abs(rA - rB) > 1)
                    processTileHittingTile(reelTileA, reelTileB, rA, cA, rB, cA);

                if (Math.abs(rA - rB) == 1)
                    processTileHittingTile(reelTileA, reelTileB, rA, cA, rB, cB);

                if (Math.abs(rA - rB) == 0)
                    System.out.println("Difference between rows is == 0. I shouldn't get this.");
            }
        }
    }

    private void processReelTileHit(ReelTile reelTile) {
        reelTile.setY(reelTile.getDestinationY());
        levelCreator.printMatchGrid(reelTiles, 12, 9);
        Body reelbox = reelBoxes.get(reelTile.getIndex());
        if (PhysicsManagerCustomBodies.isStopped(reelbox)) {
            if (levelCreator.getPlayState() == PlayStates.INTRO_SPINNING)
                numberOfReelsAboveHitsIntroSpinning++;
        }
    }

    private void processTileHittingTile(ReelTile reelTileA, ReelTile reelTileB, int rA, int cA, int rB, int cB) {
        if (rA > rB) {
            swapReelsAboveMe(reelTileB, reelTileA);
            reelsLeftToFall(rB, cB);
            levelCreator.printMatchGrid(reelTiles, 12, 9);
        } else {
            swapReelsAboveMe(reelTileA, reelTileB);
            reelsLeftToFall(rA, cA);
            levelCreator.printMatchGrid(reelTiles, 12, 9);
        }
    }

    private void swapReelsAboveMe(ReelTile reelTileA, ReelTile reelTileB) {
        TupleValueIndex[] reelsAboveMe = PuzzleGridType.getReelsAboveMe(levelCreator.populateMatchGrid(reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getRowFromLevel(reelTileA.getDestinationY(), GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getColumnFromLevel(reelTileA.getDestinationX()));

        swapReels(reelTileA, reelTileB);
        ReelTile currentReel = reelTileA;

        for (int reelsAboveMeIndex = 0; reelsAboveMeIndex < reelsAboveMe.length; reelsAboveMeIndex++)
            currentReel = swapReels(reelsAboveMe, reelsAboveMeIndex, currentReel);
    }

    private void swapReels(ReelTile reelTileA, ReelTile reelTileB) {
        float savedDestinationY = reelTileA.getDestinationY();
        int reelHasFallenFrom = levelCreator.findReel((int)reelTileB.getDestinationX(), (int) reelTileB.getDestinationY() + 40);

        if (reelHasFallenFrom > 0) {
            ReelTile deletedReel = reelTiles.get(reelHasFallenFrom);
            System.out.println("swapReels deleteReel="+reelHasFallenFrom+" x="+deletedReel.getX()+" y="+deletedReel.getY());

            reelTileA.setDestinationY(reelTileB.getDestinationY() + 40);
            reelTileA.setY(reelTileB.getDestinationY() + 40);
            reelTileA.unDeleteReelTile();
            levelCreator.undeleteReelBox(reelTileA.getIndex());

            deletedReel.setDestinationY(savedDestinationY);
            deletedReel.setY(savedDestinationY);
        }
    }

    private void swapReels(ReelTile reelTile) {
        float savedDestinationY = reelTile.getDestinationY();
        System.out.println("savedDesinationY="+savedDestinationY);
        int reelHasFallenTo = levelCreator.findReel((int)reelTile.getDestinationX(), 40);
        ReelTile deletedReel = reelTiles.get(reelHasFallenTo);
        System.out.println("swapReels deleteReel="+reelHasFallenTo+" x="+deletedReel.getX()+" y="+deletedReel.getY());

        reelTile.setDestinationY(40);
        reelTile.setY(40);
        reelTile.unDeleteReelTile();
        levelCreator.undeleteReelBox(reelTile.getIndex());

        deletedReel.setDestinationY(savedDestinationY);
        deletedReel.setY(savedDestinationY);
    }

    private ReelTile swapReels(TupleValueIndex[] reelsAboveMe, int reelsAboveMeIndex, ReelTile currentReel) {
        float savedDestinationY = reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex()).getDestinationY();
        int reelHasFallenFrom = levelCreator.findReel((int) currentReel.getDestinationX(), (int) currentReel.getDestinationY() + 40);

        ReelTile deletedReel = reelTiles.get(reelHasFallenFrom);

        reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex()).setDestinationY(currentReel.getDestinationY() + 40);
        reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex()).setY(currentReel.getDestinationY() + 40);

        deletedReel.setDestinationY(savedDestinationY);
        deletedReel.setY(savedDestinationY);
        return reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex());
    }

    private void swapReelsAboveMe(ReelTile reelTile) {
        TupleValueIndex[] reelsAboveMe = PuzzleGridType.getReelsAboveMe(levelCreator.populateMatchGrid(reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getRowFromLevel(reelTile.getDestinationY(), GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getColumnFromLevel(reelTile.getDestinationX()));

        swapReels(reelTile);
        ReelTile currentReel = reelTile;

        for (int reelsAboveMeIndex = 0; reelsAboveMeIndex < reelsAboveMe.length; reelsAboveMeIndex++)
            currentReel = swapReels(reelsAboveMe, reelsAboveMeIndex, currentReel);
    }

    private void reelsLeftToFall(int rA, int cA) {
        Array<TupleValueIndex> reelsToFall = levelCreator.getReelsToFall();
        boolean finishedColumn = false;
        int index;
        int row = rA;
        while (!finishedColumn) {
            index = findReelToFall(row, cA, reelsToFall);
            if (index >= 0) {
                reelsToFall.removeIndex(index);
                levelCreator.setReelsToFall(reelsToFall);
                if (reelsToFall.size == 0)
                    levelCreator.setReelsAboveHaveFallen(true);
            } else
                finishedColumn = true;

            row--;
        }
    }

    private int findReelToFall(int row, int column, Array<TupleValueIndex> reelsToFall) {
        int index = 0;
        while (index < reelsToFall.size) {
            if ((reelsToFall.get(index).getR() == row) & (reelsToFall.get(index).getC() == column))
                return index;
            else
                index++;
        }
        return -1;
    }

    private Texture createSlotReelScrollTexture() {
        Pixmap slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        return new Texture(slotReelScrollPixmap);
    }

    private void createIntroSequence() {
        createStartReelTimer();
        PlayScreenIntroSequence playScreenIntroSequence = new PlayScreenIntroSequence(getReelTilesFromAnimatedReels(reels), tweenManager);
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
            reels.get(currentReel).setupSpinning();
            reelTiles.get(currentReel++).setSpinning(true);
        }
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
                System.out.println("Intro Sequence finished");
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
        if (slowMotion) {
            if (isSlowMotionTimerEnded(dt))
                return;
        }
        handleInput();
        tweenManager.update(dt);
        levelCreator.update(dt);
        tileMapRenderer.setView(orthographicCamera);
        hud.update(dt);
        for (AnimatedReel animatedReel : reels)
            animatedReel.update(dt);
    }

    private boolean isSlowMotionTimerEnded(float dt) {
        slowMotionCount+=dt;
        if (slowMotionCount<0.08f)
            return true;
        else
            slowMotionCount = 0;
        return false;
    }

    @Override
    protected void renderOverride(float dt) {
        tileMapRenderer.render();
        renderReelBoxes(batch, reelBoxes);
        renderRayHandler();
        renderHud(batch);
        renderWorld();
        physics.draw(batch);
        hud.stage.draw();
        stage.draw();
        renderAnimatedReelsFlash();
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
        int index = 0;
        for (Body reelBox : reelBoxes) {
            if (reelBox != null) {
                float angle = MathUtils.radiansToDegrees * reelBox.getAngle();
                if (index < reels.size) {
                    ReelTile reelTile = reels.get(index).getReel();
//                    if (!reelTile.isReelTileDeleted()) {
                        reelTile.setPosition(reelBox.getPosition().x * 100 - 20, reelBox.getPosition().y * 100 - 20);
                        reelTile.updateReelFlashSegments(reelBox.getPosition().x * 100 - 20, reelBox.getPosition().y * 100 - 20);
                        reelTile.setOrigin(0, 0);
                        reelTile.setSize(40, 40);
                        reelTile.setRotation(angle);
                        reelTile.draw(batch);
//                    }
                }
                index++;
            }
        }
        batch.end();
    }

    private void renderAnimatedReelsFlash() {
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        for (AnimatedReel animatedReel : reels)
            if (!animatedReel.getReel().isReelTileDeleted())
                if (animatedReel.getReel().getFlashState() == ReelTile.FlashState.FLASH_ON)
                    animatedReel.draw(shapeRenderer);
    }

    public void handleInput() {
        int touchX, touchY;
        if (Gdx.input.justTouched()) {
            touchX = Gdx.input.getX();
            touchY = Gdx.input.getY();
            Vector3 unProjectTouch = new Vector3(touchX, touchY, 0);
            viewport.unproject(unProjectTouch);
            PlayStates playState = levelCreator.getPlayState();
            switchPlayState(playState);
            System.out.println("playState="+playState);
            System.out.println("numberOfReelsFlashing="+levelCreator.getNumberOfReelsFlashing());
            levelCreator.printMatchGrid(reelTiles, 12, 9);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            System.out.println("Debug key pressed - use this to insert a breakpoint");
    }

    private void switchPlayState(PlayStates playState) {
        switch (playState) {
            case CREATED_REELS_HAVE_FALLEN:
                Gdx.app.debug(logTag, playState.toString());
                break;
            case HIT_SINK_BOTTOM:
                Gdx.app.debug(logTag, playState.toString());
                break;
            case INITIALISING:
                Gdx.app.debug(logTag, playState.toString());
                break;
            case INTRO_SEQUENCE:
                Gdx.app.debug(logTag, playState.toString());
                break;
            case INTRO_POPUP:
                Gdx.app.debug(logTag, playState.toString());
                break;
            case INTRO_SPINNING:
                Gdx.app.debug(logTag, playState.toString());
                break;
            case INTRO_FLASHING:
                Gdx.app.debug(logTag, playState.toString());
                break;
            case LEVEL_TIMED_OUT:
                Gdx.app.debug(logTag, playState.toString());
                break;
            case LEVEL_LOST:
                Gdx.app.debug(logTag, playState.toString());
                break;
            case PLAYING:
                Gdx.app.debug(logTag, playState.toString());
                processIsTileClicked();
                break;
            case REELS_SPINNING:
                Gdx.app.debug(logTag, playState.toString());
                break;
            case REELS_FLASHING:
                Gdx.app.debug(logTag, playState.toString());
                break;
            case RESTARTING_LEVEL:
                Gdx.app.debug(logTag, playState.toString());
                break;
            case WON_LEVEL:
                Gdx.app.debug(logTag, playState.toString());
                break;
            case BONUS_LEVEL_ENDED:
                Gdx.app.debug(logTag, playState.toString());
                break;
            default: break;
        }
    }

    private void processIsTileClicked() {
        Vector2 tileClicked = getTileClicked();
        processTileClicked(tileClicked);
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

    public void hudAddScore(int score) {
        hud.addScore(score);
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
}
