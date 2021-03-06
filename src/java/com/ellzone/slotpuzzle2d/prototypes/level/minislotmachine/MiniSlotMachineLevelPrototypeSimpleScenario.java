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

package com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.camera.CameraHelper;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.ScoreAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.card.Card;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimpleScenario;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.physics.ReelSink;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.score.Score;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.utils.assets.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.Random;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class MiniSlotMachineLevelPrototypeSimpleScenario extends SPPrototypeTemplate {
    static final int GAME_LEVEL_WIDTH = 12;
    static final int GAME_LEVEL_HEIGHT = 9;
    private static final String PLAYING_CARD_LEVEL_TYPE = "PlayingCard";
    private static final String MINI_SLOT_MACHINE_LEVEL_NAME = "Mini Slot Machine";
    private static final String BONUS_LEVEL_TYPE = "BonusLevelType";
    public static final int MAX_NUMBER_OF_REELS_HIT_SINK_BOTTOM = 3;
    public static final String WIDTH_KEY = "width";
    public static final String HEIGHT_KEY = "height";

    public static int numberOfReelsToHitSinkBottom;
    public static int numberOfReelsToFall;
//    private static int numberOfReelsAboveHitsIntroSpinning;

    private String logTag = SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName();
    private OrthographicCamera camera;
    private TiledMap miniSlotMachineLevel;
    private TextureAtlas carddeckAtlas;
    private ReelSprites reelSprites;
    private Array<ReelTile> reelTiles;
    private Array<AnimatedReel> animatedReels;
    private LevelDoor levelDoor;
    private Array<Card> cards;
    private OrthogonalTiledMapRenderer tileMapRenderer;
    private Sound chaChingSound, pullLeverSound, reelSpinningSound, reelStoppedSound, jackpotSound;
    private Array<Timeline> endReelSeqs;
    private Timeline reelFlashSeq;
    private LevelCreatorSimpleScenario levelCreator;
    private boolean gameOver = false;
    private boolean inRestartLevel = false;
    private boolean win = false;
    private boolean displaySpinHelp;
    private int displaySpinHelpSprite;
    private Hud hud;
    private PhysicsManagerCustomBodies physics;
    private Array<Body> reelBoxes;
    private ShapeRenderer shapeRenderer;
    private int mapWidth;
    private int mapHeight;
    private TiledMap level;

    @Override
    protected void initialiseOverride() {
        getCamera();
        initialseAssests();
        initialiseReelCounts();
        initialiseLevelDoor();
        getLevelAssets(annotationAssetManager);
        getMapProperties(level);
        createPlayScreen();
        initialisePhysics();
        initialiseLevel();
    }

    private void getCamera() {
        camera = CameraHelper.GetCamera(SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT);
    }

    private void initialiseReelCounts() {
        numberOfReelsToHitSinkBottom = 0;
//        numberOfReelsAboveHitsIntroSpinning = 0;
    }

    private void initialseAssests() {
        initialiseReels(this.annotationAssetManager);
        getAssets(annotationAssetManager);
        miniSlotMachineLevel = annotationAssetManager.get(AssetsAnnotation.MINI_SLOT_MACHINE_LEVEL1);
    }

    private void getMapProperties(TiledMap level) {
        MapProperties mapProperties = level.getProperties();
        mapWidth = mapProperties.get(WIDTH_KEY, Integer.class);
        mapHeight = mapProperties.get(HEIGHT_KEY, Integer.class);
        System.out.println("mapWidth="+mapWidth);
        System.out.println(("mapHeight="+mapWidth));
    }

    private void initialisePhysics() {
        physics = new PhysicsManagerCustomBodies(camera);
        ReelSink reelSink = new ReelSink(physics);
        reelSink.createReelSink(
                SlotPuzzleConstants.VIRTUAL_WIDTH / 2,
                SlotPuzzleConstants.VIRTUAL_HEIGHT / 2,
                8,
                4,
                40,
                40,
                this);
    }

    private void initialiseLevel() {
        levelCreator = new LevelCreatorSimpleScenario(levelDoor,
                miniSlotMachineLevel,
                annotationAssetManager,
                carddeckAtlas,
                tweenManager,
                physics,
                new GridSize(
                        SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                        SlotPuzzleConstants.GAME_LEVEL_HEIGHT),
                PlayStates.INITIALISING);
        levelCreator.setPlayState(PlayStates.INITIALISING);
        reelTiles = levelCreator.getReelTiles();
        animatedReels = levelCreator.getAnimatedReels();
        reelBoxes = levelCreator.getReelBoxes();
        initialiseHud();
        levelCreator.setPlayState(PlayStates.INTRO_SPINNING);
    }

    private void getLevelAssets(AnnotationAssetManager annotationAssetManager) {
        level = annotationAssetManager.get("levels/level " + (this.levelDoor.getId() + 1) + " - 40x40.tmx");
    }

    private void initialiseHud() {
        hud = new Hud(batch);
        hud.setLevelName(levelDoor.getLevelName());
        hud.startWorldTimer();
    }

    private void getAssets(AnnotationAssetManager annotationAssetManager) {
        carddeckAtlas = annotationAssetManager.get(AssetsAnnotation.CARDDECK);
        chaChingSound = annotationAssetManager.get(AssetsAnnotation.SOUND_CHA_CHING);
        pullLeverSound = annotationAssetManager.get(AssetsAnnotation.SOUND_PULL_LEVER);
        reelSpinningSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_SPINNING);
        reelStoppedSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_STOPPED);
        jackpotSound = annotationAssetManager.get(AssetsAnnotation.SOUND_JACKPOINT);
    }

    @Override
    protected void initialiseScreenOverride() {
        shapeRenderer = new ShapeRenderer();
    }

    private void createPlayScreen() {
        initialisePlayScreen();
    }

    private void initialiseReels(AnnotationAssetManager annotationAssetManager) {
        this.reelSprites = new ReelSprites(annotationAssetManager);
    }

    private void initialiseLevelDoor() {
        levelDoor = new LevelDoor();
        levelDoor.setLevelName(MINI_SLOT_MACHINE_LEVEL_NAME);
        levelDoor.setLevelType(BONUS_LEVEL_TYPE);
    }

    private void initialisePlayScreen() {
        tileMapRenderer = new OrthogonalTiledMapRenderer(miniSlotMachineLevel);
        font = new BitmapFont();
        reelTiles = new Array<>();
    }

    public void handleInput() {
        int touchX, touchY;
        if (Gdx.input.justTouched()) {
            touchX = Gdx.input.getX();
            touchY = Gdx.input.getY();
            Vector3 unprojTouch = new Vector3(touchX, touchY, 0);
            viewport.unproject(unprojTouch);
            PlayStates playState = levelCreator.getPlayState();
            switchPlayState(playState);
        }
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
        int c = (int) (newPoints.x - PlayScreen.PUZZLE_GRID_START_X) / SlotPuzzleConstants.TILE_WIDTH;
        int r = (int) (newPoints.y - PlayScreen.PUZZLE_GRID_START_Y) / SlotPuzzleConstants.TILE_HEIGHT;
        r = GAME_LEVEL_HEIGHT - 1 - r ;
        return new Vector2(c, r);
    }

    private void processTileClicked(Vector2 tileClicked) {
        int r = (int) tileClicked.y;
        int c = (int) tileClicked.x;
        ReelTileGridValue[][] grid = levelCreator.populateMatchGrid(
                reelTiles,
                new GridSize(
                        SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                        SlotPuzzleConstants.GAME_LEVEL_HEIGHT
                ));
        if (grid[r][c] != null) {
            ReelTile reel = reelTiles.get(grid[r][c].index);
            AnimatedReel animatedReel = levelCreator.getAnimatedReels().get(grid[r][c].index);
            processReelClicked(reel, animatedReel);
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

    @Override
    protected void loadAssetsOverride() {
    }

    @Override
    protected void disposeOverride() {
    }

    @Override
    protected void updateOverride(float dt) {
        tweenManager.update(dt);
        levelCreator.update(dt);
        tileMapRenderer.setView(orthographicCamera);
        hud.update(dt);
        if (hud.getWorldTime() == 0) {
            if ((hud.getLives() > 0) & (!inRestartLevel)) {
                inRestartLevel = true;
                levelCreator.setPlayState(PlayStates.LEVEL_LOST);
            } else
                gameOver = true;
        }
        handlePlayState(levelCreator.getPlayState());
    }

    private void handlePlayState(PlayStates playState) {
        switch (playState) {
            case INTRO_POPUP:
                break;
            case LEVEL_LOST:
                break;
            case WON_LEVEL:
                break;
            case INITIALISING:
                break;
            case INTRO_FLASHING:
                break;
            case INTRO_SEQUENCE:
                break;
            case INTRO_SPINNING:
                break;
            case PLAYING:
                break;
            case RESTARTING_LEVEL:
                break;
            default:
                break;
        }
    }

    @Override
    protected void renderOverride(float dt) {
        handleInput();
        tileMapRenderer.render();
        batch.begin();
        if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
            drawPlayingCards(batch);

        for (Score score : levelCreator.getScores())
            score.render(batch);

        if (displaySpinHelp)
            reelSprites.getSprites()[displaySpinHelpSprite].draw(batch);

        batch.end();
        renderReelBoxes(batch, reelBoxes);
        renderAnimatedReelsFlash();
        physics.draw(batch);
        batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        stage.draw();
    }

    private void renderReelBoxes(SpriteBatch batch, Array<Body> reelBoxes) {
        batch.begin();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        int index = 0;
        for (Body reelBox : reelBoxes) {
            float angle = MathUtils.radiansToDegrees * reelBox.getAngle();
            if (index < animatedReels.size) {
                ReelTile reelTile = animatedReels.get(index).getReel();
                if (!reelTile.isReelTileDeleted()) {
                    reelTile.setPosition(reelBox.getPosition().x * 100 - 20, reelBox.getPosition().y * 100 - 20);
                    reelTile.setOrigin(0, 0);
                    reelTile.setSize(40, 40);
                    reelTile.setRotation(angle);
                    reelTile.draw(batch);
                }
            }
            index++;
        }
        batch.end();
    }

    private void renderAnimatedReelsFlash() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        for (AnimatedReel animatedReel : animatedReels)
            if (!animatedReel.getReel().isReelTileDeleted())
                if (animatedReel.getReel().getFlashState() == ReelTile.FlashState.FLASH_ON)
                    animatedReel.draw(shapeRenderer);
    }

    private void drawPlayingCards(SpriteBatch spriteBatch) {
        for (Card card : cards)
            card.draw(spriteBatch);
    }

    @Override
    protected void initialiseUniversalTweenEngineOverride() {
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        SlotPuzzleTween.registerAccessor(Score.class, new ScoreAccessor());
    }

    public PlayStates getPlayState() {
        return levelCreator.getPlayState();
    }

    public void dealWithHitSinkBottom(ReelTile reelTile) {
        if (getPlayState() == PlayStates.INTRO_SPINNING)
            levelCreator.setHitSinkBottom(true);

        if ((getPlayState() == PlayStates.INTRO_FLASHING) |
                (getPlayState() == PlayStates.REELS_FLASHING)) {

            int r = PuzzleGridTypeReelTile.getRowFromLevel(reelTile.getDestinationY(), GAME_LEVEL_HEIGHT);
            int c = PuzzleGridTypeReelTile.getColumnFromLevel(reelTile.getDestinationX());

            int currentTileAtBottomIndex = levelCreator.findReel((int)reelTile.getDestinationX(), 120);
            if (currentTileAtBottomIndex != -1) {
                swapReelsAboveMe(reelTile);
                reelsLeftToFall(r, c);
            }
        }
    }

    public void dealWithReelTileHittingReelTile(ReelTile reelTileA, ReelTile reelTileB) {
        int rA, cA, rB, cB;

        rA = PuzzleGridTypeReelTile.getRowFromLevel(reelTileA.getDestinationY(), GAME_LEVEL_HEIGHT);
        cA = PuzzleGridTypeReelTile.getColumnFromLevel(reelTileA.getDestinationX());
        rB = PuzzleGridTypeReelTile.getRowFromLevel(reelTileB.getDestinationY(), GAME_LEVEL_HEIGHT);
        cB = PuzzleGridTypeReelTile.getColumnFromLevel(reelTileB.getDestinationX());

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

    private void processTileHittingTile(ReelTile reelTileA, ReelTile reelTileB, int rA, int cA, int rB, int cB) {
        if (rA > rB) {
            swapReelsAboveMe(reelTileB, reelTileA);
            reelsLeftToFall(rB, cB);
        } else {
            swapReelsAboveMe(reelTileA, reelTileB);
            reelsLeftToFall(rA, cA);
        }
    }

    private void swapReelsAboveMe(ReelTile reelTileA, ReelTile reelTileB) {
        TupleValueIndex[] reelsAboveMe = PuzzleGridType.getReelsAboveMe(
                levelCreator.populateMatchGrid(
                        reelTiles,
                        new GridSize(
                                SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                                SlotPuzzleConstants.GAME_LEVEL_HEIGHT)),
                PuzzleGridTypeReelTile.getRowFromLevel(reelTileA.getDestinationY(), GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getColumnFromLevel(reelTileA.getDestinationX()));

        swapReels(reelTileA, reelTileB);
        ReelTile currentReel = reelTileA;

        for (int reelsAboveMeIndex = 0; reelsAboveMeIndex < reelsAboveMe.length; reelsAboveMeIndex++) {
            currentReel = swapReels(reelsAboveMe, reelsAboveMeIndex, currentReel);
        }
    }

    private void swapReels(ReelTile reelTileA, ReelTile reelTileB) {
        float savedDestinationY = reelTileA.getDestinationY();
        int reelHasFallenFrom = levelCreator.findReel((int)reelTileB.getDestinationX(), (int) reelTileB.getDestinationY() + 40);
        ReelTile deletedReel = reelTiles.get(reelHasFallenFrom);

        reelTileA.setDestinationY(reelTileB.getDestinationY() + 40);
        reelTileA.setY(reelTileB.getDestinationY() + 40);
        reelTileA.unDeleteReelTile();

        deletedReel.setDestinationY(savedDestinationY);
        deletedReel.setY(savedDestinationY);
    }

    private void swapReels(ReelTile reelTile) {
        float savedDestinationY = reelTile.getDestinationY();
        int reelHasFallenFrom = levelCreator.findReel((int)reelTile.getDestinationX(), 120);
        ReelTile deletedReel = reelTiles.get(reelHasFallenFrom);

        reelTile.setDestinationY(120);
        reelTile.setY(120);
        reelTile.unDeleteReelTile();

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
        TupleValueIndex[] reelsAboveMe = PuzzleGridType.getReelsAboveMe(
                levelCreator.populateMatchGrid(
                        reelTiles,
                        new GridSize(
                                SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                                SlotPuzzleConstants.GAME_LEVEL_HEIGHT)),
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
}
