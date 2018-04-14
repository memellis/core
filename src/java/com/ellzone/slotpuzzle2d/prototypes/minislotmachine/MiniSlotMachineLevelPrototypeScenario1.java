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

package com.ellzone.slotpuzzle2d.prototypes.minislotmachine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.camera.CameraHelper;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.ScoreAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.level.Card;
import com.ellzone.slotpuzzle2d.level.LevelCreatorScenario1;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.physics.BoxBodyBuilder;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.physics.Vector;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.Reels;
import com.ellzone.slotpuzzle2d.sprites.Score;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.utils.Random;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class MiniSlotMachineLevelPrototypeScenario1 extends SPPrototypeTemplate {
    public static final int GAME_LEVEL_WIDTH = 12;
    public static final int GAME_LEVEL_HEIGHT = 9;
    public static final String REEL_OBJECT_LAYER = "Reels";
    public static final String HIDDEN_PATTERN_LEVEL_TYPE = "HiddenPattern";
    public static final String PLAYING_CARD_LEVEL_TYPE = "PlayingCard";
    public static final String MINI_SLOT_MACHINE_LEVEL_NAME = "Mini Slot Machine";
    public static final String BONUS_LEVEL_TYPE = "BonusLevelType";
    public static final String HIDDEN_PATTERN_LAYER_NAME = "Hidden Pattern Object";
    public static final int NUMBER_OF_SUITS = 4;
    public static final int NUMBER_OF_CARDS_IN_A_SUIT = 13;
    public static final int MAX_NUMBER_OF_REELS_HIT_SINK_BOTTOM = 3;

    public static int numberOfReelsToHitSinkBottom;
    public static int numberOfReelsToFall;
    public static int numberOfReelsAboveHitsIntroSpinning;

    private String logTag = SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName();
    private OrthographicCamera camera;
    private TiledMap miniSlotMachineLevel;
    private MapTile mapTile;
    private MapProperties levelProperties;
    private TextureAtlas reelAtlas, tilesAtlas, carddeckAtlas;
    private Array<DampenedSineParticle> dampenedSines;
    private Reels reels;
    private Array<ReelTile> reelTiles;
    private Array<AnimatedReel> animatedReels;
    private LevelDoor levelDoor;
    private Array<Card> cards;
    private Array<Integer> hiddenPlayingCards;
    private OrthogonalTiledMapRenderer tileMapRenderer;
    private int sW, sH;
    private Pixmap slotReelPixmap, slotReelScrollPixmap;
    private Texture slotReelTexture, slotReelScrollTexture;
    private int slotReelScrollheight;
    private Sound chaChingSound, pullLeverSound, reelSpinningSound, reelStoppedSound, jackpotSound;
    private Vector accelerator, velocityMin;
    private float acceleratorY, accelerateY, acceleratorFriction, velocityFriction, velocityY, velocityYMin;
    private float reelSlowingTargetTime;
    private Array<Timeline> endReelSeqs;
    private Timeline reelFlashSeq;
    private LevelCreatorScenario1 levelCreator;
    private boolean gameOver = false;
    private boolean inRestartLevel = false;
    private boolean win = false;
    private boolean displaySpinHelp;
    private int displaySpinHelpSprite;
    private int mapWidth, mapHeight, tilePixelWidth, tilePixelHeight;
    private Hud hud;
    private PhysicsManagerCustomBodies physics;
    private BoxBodyBuilder bodyFactory;
    private Array<Body> reelBoxes;
    private Array<Body> reelBoxesCollided;
    private Body reelSinkLhs, reelSinkRhs, reelSinkBottom;
    private float centreX = SlotPuzzleConstants.V_WIDTH / 2;
    private float centreY = SlotPuzzleConstants.V_HEIGHT / 2;

    @Override
    protected void initialiseOverride() {
        camera = CameraHelper.GetCamera(SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT);
        initialiseReels(this.annotationAssetManager);
        createSlotReelTexture();
        getAssets(annotationAssetManager);
        miniSlotMachineLevel = annotationAssetManager.get(AssetsAnnotation.MINI_SLOT_MACHINE_LEVEL1);
        getMapProperties(this.miniSlotMachineLevel);
        numberOfReelsToHitSinkBottom = 0;
        numberOfReelsAboveHitsIntroSpinning = 0;
        initialiseLevelDoor();
        createPlayScreen();
        initialisePhysics();
        levelCreator = new LevelCreatorScenario1(levelDoor,
                                                 miniSlotMachineLevel,
                                                 annotationAssetManager,
                                                 carddeckAtlas,
                                                 tweenManager,
                                                 physics,
                                                 GAME_LEVEL_WIDTH,
                                                 GAME_LEVEL_HEIGHT,
                                                 PlayScreen.PlayStates.INITIALISING);
        levelCreator.setPlayState(PlayScreen.PlayStates.INITIALISING);
        reelTiles = levelCreator.getReelTiles();
        animatedReels = levelCreator.getAnimatedReels();
        reelBoxes = levelCreator.getReelBoxes();
        reelBoxesCollided = new Array<Body>();
        hud = new Hud(batch);
        hud.setLevelName(levelDoor.levelName);
        hud.startWorldTimer();
        levelCreator.setPlayState(PlayScreen.PlayStates.INTRO_SPINNING);
    }

    private void getAssets(AnnotationAssetManager annotationAssetManager) {
        this.carddeckAtlas = annotationAssetManager.get(AssetsAnnotation.CARDDECK);
        this.chaChingSound = annotationAssetManager.get(AssetsAnnotation.SOUND_CHA_CHING);
        this.pullLeverSound = annotationAssetManager.get(AssetsAnnotation.SOUND_PULL_LEVER);
        this.reelSpinningSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_SPINNING);
        this.reelStoppedSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_STOPPED);
        this.jackpotSound = annotationAssetManager.get(AssetsAnnotation.SOUND_JACKPOINT);
    }

    private void initialisePhysics() {
        physics = new PhysicsManagerCustomBodies(camera);
        bodyFactory = physics.getBodyFactory();

        reelSinkBottom = physics.createEdgeBody(BodyDef.BodyType.StaticBody,
                centreX - 8 * 40 / 2 - 4,
                centreY - 4 * 40 / 2 - 40,
                centreX + 8 * 40 / 2 + 4,
                centreY - 4 * 40 / 2 - 40);
        reelSinkBottom.setUserData(this);
        reelSinkLhs = physics.createEdgeBody(BodyDef.BodyType.StaticBody,
                centreX - 8 * 40 / 2 - 4,
                centreY - 4 * 40 / 2 - 40,
                centreX - 8 * 40 / 2 - 4,
                centreY + 4 * 40 / 2 - 40);
        reelSinkRhs = physics.createEdgeBody(BodyDef.BodyType.StaticBody,
                centreX + 8 * 40 / 2 + 4,
                centreY - 4 * 40 / 2 - 40,
                centreX + 8 * 40 / 2 + 4,
                centreY + 4 * 40 / 2 - 40);
    }

    private void getMapProperties(TiledMap level) {
        MapProperties mapProperties = level.getProperties();
        mapWidth = mapProperties.get("width", Integer.class);
        mapHeight = mapProperties.get("height", Integer.class);
        tilePixelWidth = mapProperties.get("tilewidth", Integer.class);
        tilePixelHeight = mapProperties.get("tileheight", Integer.class);
    }

    private void createSlotReelTexture() {
        slotReelPixmap = new Pixmap(PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT, Pixmap.Format.RGBA8888);
        slotReelPixmap = PixmapProcessors.createDynamicScrollAnimatedPixmap(reels.getReels(), reels.getReels().length);
        slotReelTexture = new Texture(slotReelPixmap);
        slotReelScrollPixmap = new Pixmap((int) reels.getReelWidth(), (int)reels.getReelHeight(), Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(reels.getReels());
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        slotReelScrollheight = slotReelScrollTexture.getHeight();
    }

    @Override
    protected void initialiseScreenOverride() {
    }

    private void createPlayScreen() {
        initialisePlayScreen();
    }

    private void initialiseReels(AnnotationAssetManager annotationAssetManager) {
        this.reels = new Reels(annotationAssetManager);
    }

    private void initialiseLevelDoor() {
        levelDoor = new LevelDoor();
        levelDoor.levelName = MINI_SLOT_MACHINE_LEVEL_NAME;
        levelDoor.levelType = BONUS_LEVEL_TYPE;
    }

    private void initialisePlayScreen() {
        this.tileMapRenderer = new OrthogonalTiledMapRenderer(miniSlotMachineLevel);
        this.font = new BitmapFont();
        this.sW = SlotPuzzleConstants.V_WIDTH;
        this.sH = SlotPuzzleConstants.V_HEIGHT;
        reelTiles = new Array<ReelTile>();
    }

    public void handleInput(float dt) {
        int touchX, touchY;

        if (Gdx.input.justTouched()) {
            touchX = Gdx.input.getX();
            touchY = Gdx.input.getY();
            Vector3 unprojTouch = new Vector3(touchX, touchY, 0);
            viewport.unproject(unprojTouch);
            PlayScreen.PlayStates playState = levelCreator.getPlayState();
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
                default: break;
            }
        }
    }

    private void processIsTileClicked() {
        int touchX = Gdx.input.getX();
        int touchY = Gdx.input.getY();
        Vector2 newPoints = new Vector2(touchX, touchY);
        newPoints = viewport.unproject(newPoints);
        int c = (int) (newPoints.x - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
        int r = (int) (newPoints.y - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
        r = GAME_LEVEL_HEIGHT - 1 - r ;
        if ((r >= 0) & (r <= GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= GAME_LEVEL_WIDTH)) {
            TupleValueIndex[][] grid = levelCreator.populateMatchGrid(reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
            if (grid[r][c] != null) {
                ReelTile reel = reelTiles.get(grid[r][c].index);
                AnimatedReel animatedReel = levelCreator.getAnimatedReels().get(grid[r][c].index);
                if (!reel.isReelTileDeleted()) {
                    if (reel.isSpinning()) {
                        if (animatedReel.getDampenedSineState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
                            reel.setEndReel(reel.getCurrentReel());
                            displaySpinHelp = true;
                            displaySpinHelpSprite = reel.getCurrentReel();
                            Hud.addScore(-1);
                            pullLeverSound.play();
                            reelSpinningSound.play();
                        }
                    } else {
                        if (!reel.getFlashTween()) {
                            reelSlowingTargetTime = 3.0f;
                            reel.setEndReel(Random.getInstance().nextInt(reels.getReels().length - 1));
                            reel.startSpinning();
                            levelCreator.setNumberOfReelsSpinning(levelCreator.getNumberOfReelsSpinning()+1);
                            reel.setSy(0);
                            animatedReel.reinitialise();
                            Hud.addScore(-1);
                            if (pullLeverSound != null) {
                                pullLeverSound.play();
                            }
                        }
                    }
                }
            } else {
                Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE,"grid["+r+","+c+"] is null");
            }

        } else {
            Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r="+r+"c="+c);
        }
    }

    @Override
    protected void loadAssetsOverride() {
    }

    @Override
    protected void disposeOverride() {
    }

    @Override
    protected void updateOverride(float dt) {
        this.tweenManager.update(dt);
        this.levelCreator.update(dt);
        tileMapRenderer.setView(orthographicCamera);
        hud.update(dt);
        if (hud.getWorldTime() == 0) {
            if ((Hud.getLives() > 0) & (!inRestartLevel)) {
                inRestartLevel = true;
                levelCreator.setPlayState(PlayScreen.PlayStates.LEVEL_LOST);
            } else {
                gameOver = true;
            }
        }
        handlePlayState(this.levelCreator.getPlayState());
    }

    private void handlePlayState(PlayScreen.PlayStates playState) {
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
        handleInput(dt);
        tileMapRenderer.render();
        batch.begin();
        if (levelDoor.levelType.equals(PLAYING_CARD_LEVEL_TYPE)) {
            drawPlayingCards(batch);
        }
        for (Score score : levelCreator.getScores()) {
            score.render(batch);
        }
        if (displaySpinHelp) {
            reels.getReels()[displaySpinHelpSprite].draw(batch);
        }
        batch.end();
        renderReelBoxes(batch, reelBoxes, reelTiles);
        physics.draw(batch);
        batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        stage.draw();
    }

    private void renderReelBoxes(SpriteBatch batch, Array<Body> reelBoxes, Array<ReelTile> reelTiles) {
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

    public void drawPlayingCards(SpriteBatch spriteBatch) {
        for (Card card : cards) {
            card.draw(spriteBatch);
        }
    }

    @Override
    protected void initialiseUniversalTweenEngineOverride() {
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        SlotPuzzleTween.registerAccessor(Score.class, new ScoreAccessor());
    }

    public PlayScreen.PlayStates getPlayState() {
        return this.levelCreator.getPlayState();
    }

    public void setPlayState(PlayScreen.PlayStates playState) {
        this.levelCreator.setPlayState(playState);
    }

    public void dealWithHitSinkBottom(ReelTile reelTile) {
        if (this.getPlayState() == PlayScreen.PlayStates.INTRO_SPINNING) {
            levelCreator.setHitSinkBottom(true);
        }
        if ((this.getPlayState() == PlayScreen.PlayStates.INTRO_FLASHING) |
                (this.getPlayState() == PlayScreen.PlayStates.REELS_FLASHING)) {

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
        if ((Math.abs(rA - rB) == 1) & (cA == cB)) {
            reelTileA.setY(reelTileA.getDestinationY());
            Body reelbox = reelBoxes.get(reelTileA.getIndex());
            if (PhysicsManagerCustomBodies.isStopped(reelbox)) {
                if (levelCreator.getPlayState() == PlayScreen.PlayStates.INTRO_SPINNING) {
                    numberOfReelsAboveHitsIntroSpinning++;
                }
            }
        }
        if ((Math.abs(rA - rB) == 1) & (cA == cB)) {
            reelTileB.setY(reelTileB.getDestinationY());
            Body reelbox = reelBoxes.get(reelTileB.getIndex());
            if (PhysicsManagerCustomBodies.isStopped(reelbox)) {
                numberOfReelsAboveHitsIntroSpinning++;
            }
        }
        if ((levelCreator.getPlayState() == PlayScreen.PlayStates.INTRO_FLASHING) |
            (this.getPlayState() == PlayScreen.PlayStates.REELS_FLASHING)) {
            if  (cA == cB) {
                if (Math.abs(rA - rB) > 1) {
                    if (rA > rB) {
                        swapReelsAboveMe(reelTileB, reelTileA);
                        reelsLeftToFall(rB, cB);
                    } else {
                        swapReelsAboveMe(reelTileA, reelTileB);
                        reelsLeftToFall(rA, cA);
                    }
                }
                if (Math.abs(rA - rB) == 1) {
                    if (rA > rB) {
                        swapReelsAboveMe(reelTileB, reelTileA);
                        reelsLeftToFall(rB, cB);
                    } else {
                        swapReelsAboveMe(reelTileA, reelTileB);
                        reelsLeftToFall(rA, cA);
                    }
                }
                if (Math.abs(rA - rB) == 0) {
                    System.out.println("Difference between rows is == 0. I shouldn't get this.");
                }
            }
        }
    }

    private void swapReelsAboveMe(ReelTile reelTileA, ReelTile reelTileB) {
        TupleValueIndex[] reelsAboveMe = PuzzleGridType.getReelsAboveMe(levelCreator.populateMatchGrid(reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getRowFromLevel(reelTileA.getDestinationY(), GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getColumnFromLevel(reelTileA.getDestinationX()));

        float savedDestinationY = reelTileA.getDestinationY();
        int reelHasFallenFrom = levelCreator.findReel((int)reelTileB.getDestinationX(), (int) reelTileB.getDestinationY() + 40);
        ReelTile deletedReel = reelTiles.get(reelHasFallenFrom);

        reelTileA.setDestinationY(reelTileB.getDestinationY() + 40);
        reelTileA.setY(reelTileB.getDestinationY() + 40);
        reelTileA.unDeleteReelTile();

        deletedReel.setDestinationY(savedDestinationY);
        deletedReel.setY(savedDestinationY);

        ReelTile currentReel = reelTileA;

        for (int reelsAboveMeIndex = 0; reelsAboveMeIndex < reelsAboveMe.length; reelsAboveMeIndex++) {
            savedDestinationY = reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex()).getDestinationY();
            reelHasFallenFrom = levelCreator.findReel((int) currentReel.getDestinationX(), (int) currentReel.getDestinationY() + 40);
            deletedReel = reelTiles.get(reelHasFallenFrom);

            reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex()).setDestinationY(currentReel.getDestinationY() + 40);
            reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex()).setY(currentReel.getDestinationY() + 40);

            deletedReel.setDestinationY(savedDestinationY);
            deletedReel.setY(savedDestinationY);

            currentReel = reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex());
        }
    }

    private void swapReelsAboveMe(ReelTile reelTile) {
        TupleValueIndex[] reelsAboveMe = PuzzleGridType.getReelsAboveMe(levelCreator.populateMatchGrid(reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getRowFromLevel(reelTile.getDestinationY(), GAME_LEVEL_HEIGHT),
                PuzzleGridTypeReelTile.getColumnFromLevel(reelTile.getDestinationX()));

        float savedDestinationY = reelTile.getDestinationY();
        int reelHasFallenFrom = levelCreator.findReel((int)reelTile.getDestinationX(), (int) 120);
        ReelTile deletedReel = reelTiles.get(reelHasFallenFrom);

        reelTile.setDestinationY(120);
        reelTile.setY(120);
        reelTile.unDeleteReelTile();

        deletedReel.setDestinationY(savedDestinationY);
        deletedReel.setY(savedDestinationY);

        ReelTile currentReel = reelTile;

        for (int reelsAboveMeIndex = 0; reelsAboveMeIndex < reelsAboveMe.length; reelsAboveMeIndex++) {
            savedDestinationY = reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex()).getDestinationY();
            reelHasFallenFrom = levelCreator.findReel((int) currentReel.getDestinationX(), (int) currentReel.getDestinationY() + 40);
            deletedReel = reelTiles.get(reelHasFallenFrom);

            reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex()).setDestinationY(currentReel.getDestinationY() + 40);
            reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex()).setY(currentReel.getDestinationY() + 40);

            deletedReel.setDestinationY(savedDestinationY);
            deletedReel.setY(savedDestinationY);

            currentReel = reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex());
        }
    }

    private int getRow(float y) {
        return PuzzleGridTypeReelTile.getRowFromLevel(y, GAME_LEVEL_HEIGHT);
    }

    private int getColumn(float x) {
        return PuzzleGridTypeReelTile.getColumnFromLevel(x);
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
                if (reelsToFall.size == 0) {
                    levelCreator.setReelsAboveHaveFallen(true);
                }
            } else {
                finishedColumn = true;
            }
            row--;
        }
    }

    private int findReelToFall(int row, int column, Array<TupleValueIndex> reelsToFall) {
        int index = 0;
        while (index < reelsToFall.size) {
            if ((reelsToFall.get(index).getR() == row) & (reelsToFall.get(index).getC() == column)) {
                return index;
            } else {
                index++;
            }
        }
        return -1;
    }
}
