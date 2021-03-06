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
import com.ellzone.slotpuzzle2d.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle2d.level.card.Card;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreator;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.physics.BoxBodyBuilder;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.physics.Vector;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.score.Score;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.utils.assets.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.utils.Random;
import net.dermetfan.gdx.assets.AnnotationAssetManager;

/*
Search for PlanetUML with your favourite Internet earch engine.

@startuml

SPProtoypeTemplate <|-- MiniSlotMachinePrototypeWithLevelCreator : extends
MiniSlotMachinePrototypeWithLevelCreator --> LevelCreator : uses
PhysicsManagerCustomBodies --> World : creates
B2dContactListener --> World : registered with
B2dContactListener --> MiniSlotMachinePrototypeWithLevelCreator : calls dealWithMethods

class MiniSlotMachinePrototypeWithLevelCreator {
+{static} int numberOfReelsToFall
+{static} int numberOfReelsToHitSinkBottom
+{static} int numberOfReelsAboveHitsIntroSpinning
-OrthographicCamera camera

#initialiseOverride()
-getAssests()
-initialisePhysics()
-getMapProperties(TiledMap level)
-createSlotReelTexture()
-createPlayScreen()
-initialiseReels(AnnotationAssetManager annotationAssetManager)
-initialiseLevelDoor()
-initialisePlayScreen()
-handleInput(float dt)
-processIsTileClicked()
#updateOverride(float dt)
-handlePlayState(PlayScreen.PlayStates playState)
#renderOverride(float dt)
-renderReelBoxes(SpriteBatch batch, Array<Body> reelBoxes, Array<ReelTile> reelTiles)
+drawPlayingCards(SpriteBatch spriteBatch)
#initialiseUniversalTweenEngineOverride()
+PlayScreen.PlayStates getPlayState()
+dealWithHitSinkBottom(ReelTile reelTile)
+void dealWithReelTileHittingReelTile(ReelTile reelTileA, ReelTile reelTileB)
-swapReelsAboveMe(ReelTile reelTileA, ReelTile reelTileB)
-int getRow(float y)
-int getColumn(float x)
}

@enduml
 */

public class MiniSlotMachineLevelPrototypeWithLevelCreator extends SPPrototypeTemplate {
    public static final String REEL_OBJECT_LAYER = "ReelSprites";
    public static final String HIDDEN_PATTERN_LEVEL_TYPE = "HiddenPattern";
    public static final String PLAYING_CARD_LEVEL_TYPE = "PlayingCard";
    public static final String MINI_SLOT_MACHINE_LEVEL_NAME = "Mini Slot Machine";
    public static final String BONUS_LEVEL_TYPE = "BonusLevelType";
    public static final String HIDDEN_PATTERN_LAYER_NAME = "Hidden Pattern Object";
    public static final int NUMBER_OF_SUITS = 4;
    public static final int NUMBER_OF_CARDS_IN_A_SUIT = 13;
    public static final int MAX_NUMBER_OF_REELS_HIT_SINK_BOTTOM = 8;

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
    private ReelSprites reelSprites;
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
    private LevelCreator levelCreator;
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
    private Body reelSinkLhs, reelSinkRhs, reelSinkBottom;
    private float centreX = SlotPuzzleConstants.VIRTUAL_WIDTH / 2;
    private float centreY = SlotPuzzleConstants.VIRTUAL_HEIGHT / 2;
    private final GridSize levelGridSize =
            new GridSize(
                    SlotPuzzleConstants.GAME_LEVEL_WIDTH, SlotPuzzleConstants.GAME_LEVEL_HEIGHT);

    @Override
    protected void initialiseOverride() {
        camera = CameraHelper.GetCamera(SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT);
        initialiseReels(this.annotationAssetManager);
        createSlotReelTexture();
        getAssets(annotationAssetManager);
        miniSlotMachineLevel = annotationAssetManager.get(AssetsAnnotation.MINI_SLOT_MACHINE_LEVEL);
        getMapProperties(this.miniSlotMachineLevel);
        numberOfReelsToHitSinkBottom = 0;
        numberOfReelsAboveHitsIntroSpinning = 0;
        initialiseLevelDoor();
        createPlayScreen();
        initialisePhysics();
        hud = new Hud(batch);
        levelCreator = new LevelCreator(
                levelDoor,
                miniSlotMachineLevel,
                annotationAssetManager,
                carddeckAtlas,
                tweenManager,
                physics,
                levelGridSize,
                PlayStates.INITIALISING,
                hud);
        levelCreator.setPlayState(PlayStates.INITIALISING);
        reelTiles = levelCreator.getReelTiles();
        animatedReels = levelCreator.getAnimatedReels();
        reelBoxes = levelCreator.getReelBoxes();
        hud.setLevelName(levelDoor.getLevelName());
        hud.startWorldTimer();
        startReelsSpinning(animatedReels, reelTiles);
        levelCreator.setPlayState(PlayStates.INTRO_SPINNING);
    }

    private void getAssets(AnnotationAssetManager annotationAssetManager) {
        carddeckAtlas = annotationAssetManager.get(AssetsAnnotation.CARDDECK);
        chaChingSound = annotationAssetManager.get(AssetsAnnotation.SOUND_CHA_CHING);
        pullLeverSound = annotationAssetManager.get(AssetsAnnotation.SOUND_PULL_LEVER);
        reelSpinningSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_SPINNING);
        reelStoppedSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_STOPPED);
        jackpotSound = annotationAssetManager.get(AssetsAnnotation.SOUND_JACKPOINT);
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
        slotReelPixmap = new Pixmap(
                SlotPuzzleConstants.TILE_WIDTH,
                SlotPuzzleConstants.TILE_HEIGHT,
                Pixmap.Format.RGBA8888);
        slotReelPixmap = PixmapProcessors.createDynamicScrollAnimatedPixmap(
                reelSprites.getSprites(), reelSprites.getSprites().length);
        slotReelTexture = new Texture(slotReelPixmap);
        slotReelScrollPixmap = new Pixmap(
                (int) reelSprites.getReelWidth(),
                (int) reelSprites.getReelHeight(),
                Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(reelSprites.getSprites());
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        slotReelScrollheight = slotReelScrollTexture.getHeight();
    }

    private void startReelsSpinning(Array<AnimatedReel> animatedReels, Array<ReelTile> reelTiles) {
        for(AnimatedReel animatedReel : animatedReels) {
            animatedReel.setupSpinning();
            animatedReel.getReel().startSpinning();
        }
    }

    @Override
    protected void initialiseScreenOverride() {
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
        this.tileMapRenderer = new OrthogonalTiledMapRenderer(miniSlotMachineLevel);
        this.font = new BitmapFont();
        this.sW = SlotPuzzleConstants.VIRTUAL_WIDTH;
        this.sH = SlotPuzzleConstants.VIRTUAL_HEIGHT;
        reelTiles = new Array<>();
    }

     public void handleInput(float dt) {
        int touchX, touchY;

        if (Gdx.input.justTouched()) {
            touchX = Gdx.input.getX();
            touchY = Gdx.input.getY();
            Vector3 unprojTouch = new Vector3(touchX, touchY, 0);
            viewport.unproject(unprojTouch);
            switch (levelCreator.getPlayState()) {
                case INITIALISING:
                    Gdx.app.debug(logTag, "Initialising");
                    break;
                case INTRO_SEQUENCE:
                    Gdx.app.debug(logTag, "Intro Sequence");
                    break;
                case INTRO_POPUP:
                    break;
                case INTRO_SPINNING:
                    Gdx.app.debug(logTag, "Intro Spinning");
                    break;
                case INTRO_FLASHING:
                    Gdx.app.debug(logTag, "Intro Flashing");
                    break;
                case LEVEL_LOST:
                    Gdx.app.debug(logTag, "Lost Level");
                    break;
                case PLAYING:
                    Gdx.app.debug(logTag, "Play");
                    processIsTileClicked();
                    break;
                case REELS_SPINNING:
                    Gdx.app.debug(logTag, "ReelSprites Spinning");
                    break;
                case REELS_FLASHING:
                    Gdx.app.debug(logTag, "ReelSprites Flashing");
                case RESTARTING_LEVEL:
                    Gdx.app.debug(logTag, "Restarting Level");
                    break;
                case WON_LEVEL:
                    Gdx.app.debug(logTag, "Won Level");
                    break;
                default: break;
            }
        }
    }

    private void processIsTileClicked() {
        System.out.println("processIsTileClicked();");
        int touchX = Gdx.input.getX();
        int touchY = Gdx.input.getY();
        Vector2 newPoints = new Vector2(touchX, touchY);
        newPoints = viewport.unproject(newPoints);
        int c = (int) (newPoints.x - PlayScreen.PUZZLE_GRID_START_X) / SlotPuzzleConstants.TILE_WIDTH;
        int r = (int) (newPoints.y - PlayScreen.PUZZLE_GRID_START_Y) / SlotPuzzleConstants.TILE_HEIGHT;
        r = levelGridSize.getHeight() - 1 - r ;
        if ((r >= 0) & (r <= levelGridSize.getHeight()) & (c >= 0) & (c <= levelGridSize.getWidth())) {
            TupleValueIndex[][] grid =
                    levelCreator.populateMatchGrid(
                            reelTiles,
                            levelGridSize);
            System.out.println("touched r="+r+" c="+c);
            if (grid[r][c] != null) {
                System.out.println("grid["+r+","+c+"].index="+grid[r][c].index);

                ReelTile reel = reelTiles.get(grid[r][c].index);
                AnimatedReel animatedReel = levelCreator.getAnimatedReels().get(grid[r][c].index);
                if (!reel.isReelTileDeleted()) {
                    if (reel.isSpinning()) {
                        if (animatedReel.getDampenedSineState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
                            reel.setEndReel(reel.getCurrentReel());
                            displaySpinHelp = true;
                            displaySpinHelpSprite = reel.getCurrentReel();
                            hud.addScore(-1);
                            pullLeverSound.play();
                            reelSpinningSound.play();
                        }
                    } else {
                        if (!reel.getFlashTween()) {
                            reelSlowingTargetTime = 3.0f;
                            reel.setEndReel(Random.getInstance().nextInt(reelSprites.getSprites().length - 1));
                            reel.startSpinning();
                            levelCreator.setNumberOfReelsSpinning(levelCreator.getNumberOfReelsSpinning()+1);
                            reel.setSy(0);
                            animatedReel.reinitialise();
                            hud.addScore(-1);
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
        tweenManager.update(dt);
        levelCreator.update(dt);
        tileMapRenderer.setView(orthographicCamera);
        hud.update(dt);
        if (hud.getWorldTime() == 0) {
            if ((hud.getLives() > 0) & (!inRestartLevel)) {
                inRestartLevel = true;
                levelCreator.setPlayState(PlayStates.LEVEL_LOST);
            } else {
                gameOver = true;
            }
        }
        handlePlayState(this.levelCreator.getPlayState());
    }

    private void handlePlayState(PlayStates playState) {
        System.out.println("playState="+playState);
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
        if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE)) {
            drawPlayingCards(batch);
        }
        for (Score score : levelCreator.getScores()) {
            score.render(batch);
        }
        if (displaySpinHelp) {
            reelSprites.getSprites()[displaySpinHelpSprite].draw(batch);
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
                } else {
                    System.out.println("deleted reelTile="+reelTile+" index="+index+" x="+reelTile.getX()+" "+reelTile.getY());
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

    public PlayStates getPlayState() {
        return this.levelCreator.getPlayState();
    }

    public void setPlayState(PlayStates playState) {
        this.levelCreator.setPlayState(playState);
    }

    public void dealWithHitSinkBottom(ReelTile reelTile) {
        if (this.getPlayState() == PlayStates.INTRO_SPINNING) {
            levelCreator.setHitSinkBottom(true);
        }
        if ((this.getPlayState() == PlayStates.INTRO_FLASHING) |
            (this.getPlayState() == PlayStates.REELS_FLASHING)) {
            System.out.println("In dealWithHitSinkBottom + reelTile="+reelTile);
            System.out.println("reelTileA.destinationX="+reelTile.getDestinationX());
            System.out.println("reelTileA.destinationY="+reelTile.getDestinationY());
            int r = PuzzleGridTypeReelTile.getRowFromLevel(
                    reelTile.getDestinationY(),
                    levelGridSize.getHeight());
            int c = PuzzleGridTypeReelTile.getColumnFromLevel(reelTile.getDestinationX());
            System.out.println("reelTileA r="+r+" c="+c+" v="+reelTile.getEndReel() );

            int currentTileAtBottomIndex = levelCreator.findReel((int)reelTile.getDestinationX(), 120);
            if (currentTileAtBottomIndex != -1) {
                reelTiles.get(currentTileAtBottomIndex).setDestinationY(reelTile.getDestinationY());
                reelTiles.get(currentTileAtBottomIndex).setY(reelTile.getDestinationY());
                reelTile.setY(120);
                reelTile.setDestinationY(120);
            }
            levelCreator.printMatchGrid(
                    reelTiles,
                    new GridSize(
                            SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                            SlotPuzzleConstants.GAME_LEVEL_HEIGHT));
        }
    }

    public void dealWithReelTileHittingReelTile(ReelTile reelTileA, ReelTile reelTileB) {
        int rA, cA, rB, cB;
        System.out.println("In dealWithReelTileHittingReelTile + reelTileA="+reelTileA+" reelTileB="+reelTileB);
        System.out.println("reelTileA.destinationX="+reelTileA.getDestinationX());
        System.out.println("reelTileA.destinationY="+reelTileA.getDestinationY());
        System.out.println("reelTileB.destinationX="+reelTileB.getDestinationX());
        System.out.println("reelTileB.destinationY="+reelTileB.getDestinationY());
        rA = PuzzleGridTypeReelTile.getRowFromLevel(
                reelTileA.getDestinationY(),
                levelGridSize.getHeight());
        cA = PuzzleGridTypeReelTile.getColumnFromLevel(reelTileA.getDestinationX());
        System.out.println("reelTileA r="+rA+" c="+cA+" v="+reelTileA.getEndReel() );
        rB = PuzzleGridTypeReelTile.getRowFromLevel(
                reelTileB.getDestinationY(),
                levelGridSize.getHeight());
        cB = PuzzleGridTypeReelTile.getColumnFromLevel(reelTileB.getDestinationX());
        System.out.println("reelTileB r="+rB+" c="+cB+" v="+reelTileB.getEndReel());
        if ((Math.abs(rA - rB) == 1) & (cA == cB)) {
            reelTileA.setY(reelTileA.getDestinationY());
            Body reelbox = reelBoxes.get(reelTileA.getIndex());
            if (PhysicsManagerCustomBodies.isStopped(reelbox)) {
                if (levelCreator.getPlayState() == PlayStates.INTRO_SPINNING) {
                    numberOfReelsAboveHitsIntroSpinning++;
                    System.out.println("numberOfReelsAboveHitsIntroSpinning="+numberOfReelsAboveHitsIntroSpinning);
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
        if ((levelCreator.getPlayState() == PlayStates.INTRO_FLASHING) |
            (levelCreator.getPlayState() == PlayStates.INTRO_FLASHING)) {
                if  (cA == cB) {
                    System.out.println("dealWithReelTileHittingReelTile INTRO_FLASHING...");
                    if (Math.abs(rA - rB) > 1) {
                        System.out.println("Difference between rows is > 1");
                        if (rA > rB) {
                            swapReelsAboveMe(reelTileB, reelTileA);
                        } else {
                            swapReelsAboveMe(reelTileA, reelTileB);
                        }
                        levelCreator.printMatchGrid(reelTiles, levelGridSize);
                    }
                    if (Math.abs(rA - rB) == 1) {
                        System.out.println("Difference between rows is == 1");
                        if (rA > rB) {
                            swapReelsAboveMe(reelTileB, reelTileA);
                        } else {
                            swapReelsAboveMe(reelTileA, reelTileB);
                        }
                        levelCreator.printMatchGrid(reelTiles, levelGridSize);
                    }
                    if (Math.abs(rA - rB) == 0) {
                        System.out.println("Difference between rows is == 0. I shouldn't get this.");
                    }
                    System.out.println("reelTileA.destinationX="+reelTileA.getDestinationX());
                    System.out.println("reelTileA.destinationY="+reelTileA.getDestinationY());
                    System.out.println("reelTileB.destinationX="+reelTileB.getDestinationX());
                    System.out.println("reelTileB.destinationY="+reelTileB.getDestinationY());
            }
        }
    }

    private void swapReelsAboveMe(ReelTile reelTileA, ReelTile reelTileB) {
        TupleValueIndex[] reelsAboveMe = PuzzleGridType.getReelsAboveMe(
                levelCreator.populateMatchGrid(reelTiles, levelGridSize),
                PuzzleGridTypeReelTile.getRowFromLevel(
                        reelTileA.getDestinationY(), levelGridSize.getHeight()),
                PuzzleGridTypeReelTile.getColumnFromLevel(reelTileA.getDestinationX()));

        float savedDestinationY = reelTileA.getDestinationY();
        int reelHasFallenFrom = levelCreator.findReel(
                (int)reelTileB.getDestinationX(),
                (int) reelTileB.getDestinationY() + 40);
        ReelTile deletedReel = reelTiles.get(reelHasFallenFrom);

        reelTileA.setDestinationY(reelTileB.getDestinationY() + 40);
        reelTileA.setY(reelTileB.getDestinationY() + 40);
        reelTileA.unDeleteReelTile();

        deletedReel.setDestinationY(savedDestinationY);
        deletedReel.deleteReelTile();

        ReelTile currentReel = reelTileA;

        for (int reelsAboveMeIndex = 0; reelsAboveMeIndex < reelsAboveMe.length; reelsAboveMeIndex++) {
            System.out.println("currentReel destination r="+getRow(currentReel.getDestinationY())+" c="+getColumn(currentReel.getDestinationX()));

            savedDestinationY = reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex()).getDestinationY();
            reelHasFallenFrom = levelCreator.findReel(
                    (int) currentReel.getDestinationX(),
                    (int) currentReel.getDestinationY() + 40);
            deletedReel = reelTiles.get(reelHasFallenFrom);

            reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex()).setDestinationY(currentReel.getDestinationY() + 40);
            reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex()).setY(currentReel.getDestinationY() + 40);

            deletedReel.setDestinationY(savedDestinationY);
            deletedReel.setY(savedDestinationY);

            System.out.println("reelTileA.destinationY="+
                    reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex()).getDestinationY());
            System.out.println("reelTileB.destinationY="+
                    reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex()).getY());
            int rA = PuzzleGridTypeReelTile.getRowFromLevel(
                    reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex()).getDestinationY(),
                    levelGridSize.getHeight());
            System.out.println("reelTileA r="+rA+" v="+
                    reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex()).getEndReel() );
            int rB = PuzzleGridTypeReelTile.getRowFromLevel(
                    reelTileB.getDestinationY(),
                    levelGridSize.getHeight());
            int cB = PuzzleGridTypeReelTile.getColumnFromLevel(reelTileB.getDestinationX());
            System.out.println("reelTileB r="+rB+" c="+cB+" v="+reelTileB.getEndReel());

            currentReel = reelTiles.get(reelsAboveMe[reelsAboveMeIndex].getIndex());
        }
        levelCreator.printMatchGrid(reelTiles, levelGridSize);
    }

    private int getRow(float y) {
        return PuzzleGridTypeReelTile.getRowFromLevel(y, levelGridSize.getHeight());
    }

    private int getColumn(float x) {
        return PuzzleGridTypeReelTile.getColumnFromLevel(x);
    }
}
