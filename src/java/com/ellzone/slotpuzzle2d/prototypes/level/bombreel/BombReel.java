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

package com.ellzone.slotpuzzle2d.prototypes.level.bombreel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
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
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.physics.ReelSink;
import com.ellzone.slotpuzzle2d.physics.contact.AnimatedReelsManager;
import com.ellzone.slotpuzzle2d.physics.contact.BoxHittingBoxContactListener;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.AnimatedReelsMatrixCreator;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.SlotPuzzleMatrices;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.utils.Random;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import static com.ellzone.slotpuzzle2d.messaging.MessageType.ReelSinkReelsLeftToFall;
import static com.ellzone.slotpuzzle2d.messaging.MessageType.ReelsLeftToFall;
import static com.ellzone.slotpuzzle2d.messaging.MessageType.SwapReelsAboveMe;

public class BombReel extends SPPrototype implements InputProcessor {
    public static final int SCREEN_OFFSET = 400;
    private OrthographicCamera camera;
    private ShapeRenderer renderer;
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private AnnotationAssetManager annotationAssetManager;
    private Random random;
    private World world;
    private Array<Body> reelBoxBodies = new Array<Body>();
    private Body groundBody, miniSlotMachineBody;
    private Body hitBody = null;
    private TweenManager tweenManager = new TweenManager();
    private Array<AnimatedReel> animatedReels;
    private int numberOfReelsToFall;
    private ReelSprites reelSprites;
    private Pixmap slotReelScrollPixmap;
    private Texture slotReelScrollTexture;
    private int spriteWidth, spriteHeight;
    private int displayWindowWidth, displayWindowHeight;
    private TextureAtlas slotHandleAtlas;
    private Sound pullLeverSound, reelSpinningSound, reelStoppingSound;
    private FitViewport viewport;
    private Stage stage;
    private PhysicsManagerCustomBodies physicsEngine;
    private MessageManager messageManager;
    private AnimatedReelsManager animatedReelsManager;
    private int slotMatrixCycleIndex;
    private Boolean isAutoFall;
    private int numberOfReelBoxesAsleep = 0;
    private int numberOfReelBoxesCreated = 0;
    int[] matrixIdentifier = new int[PlayScreen.GAME_LEVEL_WIDTH];
    private boolean cycleDynamic = true;
    private boolean testBombReel = true;
    private int reelToDelete = 84;
    private Array<Integer> reelsToDelete;
    private AnimatedReelsMatrixCreator animatedReelsMatrixCreator;
    private boolean reelsHaveFallen = false;
    private boolean debugSet = false;

    @Override
    public void create() {
        setGdxEngine();
        initialiseDisplay();
        setUpFont();
        loadAssets();
        setUpReelSprites();
        initialiseUniversalTweenEngine();
        isAutoFall = false;
        slotMatrixCycleIndex = 0;
        createScrollReelTexture();
        createPhysicsEngine();
        animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator(
                physicsEngine,
                slotReelScrollTexture,
                spriteWidth,
                spriteHeight,
                tweenManager);
        initialiseReelSlots();
        createPhysicsWorld();
        animatedReelsManager = new AnimatedReelsManager(animatedReels, reelBoxBodies);
        animatedReelsManager.setNumberOfReelsToFall(numberOfReelsToFall);
        messageManager = setUpMessageManager();
    }

    private void setUpReelSprites() {
        reelSprites = new ReelSprites(annotationAssetManager);
        spriteWidth = reelSprites.getReelWidth();
        spriteHeight = reelSprites.getReelHeight();
    }

    private void setUpFont() {
        font = new BitmapFont();
        font.setColor(Color.RED);
    }

    private void setGdxEngine() {
        setDisplaySize();
        renderer = new ShapeRenderer();
        debugRenderer = new Box2DDebugRenderer();
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(this);
    }

    private void setDisplaySize() {
        displayWindowWidth = SlotPuzzleConstants.VIRTUAL_WIDTH;
        displayWindowHeight = SlotPuzzleConstants.VIRTUAL_HEIGHT;
    }

    private void initialiseDisplay() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT, camera);
        stage = new Stage(viewport, batch);
    }

    private void loadAssets() {
        setUpAssetManager();
        getAssets();
    }

    private void getAssets() {
        slotHandleAtlas = annotationAssetManager.get(AssetsAnnotation.SLOT_HANDLE);
        pullLeverSound = annotationAssetManager.get(AssetsAnnotation.SOUND_PULL_LEVER);
        reelSpinningSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_SPINNING);
        reelStoppingSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_STOPPED);
    }

    private void setUpAssetManager() {
        annotationAssetManager = new AnnotationAssetManager();
        annotationAssetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        annotationAssetManager.load(new AssetsAnnotation());
        annotationAssetManager.finishLoading();
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

    private void initialiseUniversalTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
    }

    private void initialiseReelSlots() {
        if (testBombReel)
            testBombReelSetUp();
        else
            cycleSlotMatrix();
    }

    private void testBombReelSetUp() {
        animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                SlotPuzzleMatrices.createMatrixFWithTwoBombsSurroundedByReelTilesTopLeft(), false);
        numberOfReelsToFall = animatedReelsMatrixCreator.getNumberOfReelsToFall();
    }

    private void cycleSlotMatrix() {
        if (cycleDynamic)
            cycleDynamicSlotMatrix();
        else
            cycleStaticSlotMatrices();
    }

    private void cycleStaticSlotMatrices() {
        if (slotMatrixCycleIndex < SlotPuzzleMatrices.getSlotMatrices().size) {
            animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                    SlotPuzzleMatrices.getSlotMatrices().get(slotMatrixCycleIndex)
            );
            numberOfReelsToFall = animatedReelsMatrixCreator.getNumberOfReelsToFall();
        }
        slotMatrixCycleIndex++;
        slotMatrixCycleIndex %= SlotPuzzleMatrices.getSlotMatrices().size;
    }

    private void cycleDynamicSlotMatrix() {
        setColumnValues(matrixIdentifier, slotMatrixCycleIndex);
        int dynamicGrid[][] = SlotPuzzleMatrices.createDynamicMatrix(
                matrixIdentifier,
                PlayScreen.GAME_LEVEL_WIDTH,
                PlayScreen.GAME_LEVEL_HEIGHT);
        System.out.println();
        animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(dynamicGrid);
        numberOfReelsToFall = animatedReelsMatrixCreator.getNumberOfReelsToFall();
        slotMatrixCycleIndex++;
        slotMatrixCycleIndex %= Math.pow(2, PlayScreen.GAME_LEVEL_HEIGHT);
    }

    private void setColumnValues(int[] matrixIdentifier, int matrixValue) {
        for (int i=0; i<matrixIdentifier.length; i++)
            matrixIdentifier[i]=matrixValue + i;
    }

    private void createScrollReelTexture() {
        slotReelScrollPixmap = new Pixmap(spriteWidth, spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(reelSprites.getSprites());
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
    }

    private void createPhysicsEngine() {
        world = new World(new Vector2(0, -9.8f), true);
        physicsEngine = new PhysicsManagerCustomBodies(camera, world, debugRenderer);
    }

    private void createPhysicsWorld() {
        createReelSink();
        reelBoxBodies = animatedReelsMatrixCreator.createBoxBodiesFromAnimatedReels(animatedReels, reelBoxBodies);
        BoxHittingBoxContactListener contactListener = new BoxHittingBoxContactListener();
        world.setContactListener(contactListener);
    }

    private void createReelSink() {
        ReelSink reelSink = new ReelSink(physicsEngine);
        reelSink.createReelSink(
                SlotPuzzleConstants.VIRTUAL_WIDTH / 2 + 20,
                SlotPuzzleConstants.VIRTUAL_HEIGHT / 2 + 20,
                12,
                9,
                40,
                40);
    }

    private void update(float dt) {
        tweenManager.update(dt);
        physicsEngine.update(dt);
        updateAnimatedReels(dt);
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

    private void updateAnimatedReels(float dt) {
        for (AnimatedReel reel : animatedReels)
            reel.update(dt);
    }

    @Override
    public void render() {
        long start = TimeUtils.nanoTime();
        float dt = Gdx.graphics.getDeltaTime();
        float updateTime = (TimeUtils.nanoTime() - start) / 1000000000.0f;

        update(dt);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
        camera.update();

        physicsEngine.draw(batch);
        renderReelBoxes(batch, reelBoxBodies);
        if (isReelsStoppingMoving()) {
            if (!reelsHaveFallen) {
                reelsHaveFallen = true;
                System.out.println("ReelsStoppedFalling");
            }
            if (isAutoFall) {
                cycleSlotMatrix();
                reCreateBoxes();
                setBoxesActive();
            }
        }
        physicsEngine.draw(batch);

        renderWorld();
        renderFps(updateTime);
    }

    private void deleteAReel() {
        if (reelToDelete >= 0)
            if (!animatedReels.get(reelToDelete).getReel().isReelTileDeleted()) {
                deleteTheReel(reelToDelete);
                reelToDelete -= 12;
                if (reelToDelete < 0)
                    animatedReelsManager.printSlotMatrix();
            }
    }

    private void deleteTheReel(int reelToDelete) {
        reelBoxBodies.get(reelToDelete).setActive(false);
        animatedReels.get(reelToDelete).getReel().deleteReelTile();
        animatedReels.get(reelToDelete).getReel().setY(
                animatedReels.get(reelToDelete).getReel().getSnapY() + SCREEN_OFFSET);
        animatedReelsMatrixCreator.updateBoxBody(
                animatedReels.get(reelToDelete),
                false,
                reelBoxBodies.get(reelToDelete));
    }

    private void deleteReels() {
        for (Integer deleteReel : reelsToDelete)
            if (!animatedReels.get(deleteReel).getReel().isReelTileDeleted())
                deleteTheReel(deleteReel);
    }

    private boolean isReelsStoppingMoving() {
        return numberOfReelBoxesAsleep == numberOfReelBoxesCreated;
    }

    private void renderFps(float updateTime) {
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.begin();
        font.draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond() + " Play time: " + updateTime, 0, 20);
        batch.end();
    }

    private void renderWorld() {
        debugRenderer.render(world, viewport.getCamera().combined);
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
                    renderReel(animatedReel.getReel(), batch, reelBox, angle);
                    numberOfReelBoxesCreated++;
                }
                if (!reelBox.isAwake())
                    numberOfReelBoxesAsleep++;
            }
        }
        batch.end();
    }

    private void renderReel(ReelTile reelTile, SpriteBatch batch, Body reelBox, float angle) {
        reelTile.setPosition(
                reelBox.getPosition().x * 100 - 20,
                reelBox.getPosition().y * 100 - 20);
        reelTile.updateReelFlashSegments(
                reelBox.getPosition().x * 100 ,
                reelBox.getPosition().y * 100);
        reelTile.setOrigin(0, 0);
        reelTile.setSize(40, 40);
        reelTile.setRotation(angle);
        if (debugSet)
            reelTile.saveRegion();
        reelTile.draw(batch);
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
        if (button == Input.Buttons.LEFT)
            reCreateBoxes();
        else
            setBoxesActive();
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.A) {
            isAutoFall = !isAutoFall;
            return true;
        }
        if (keycode == Input.Keys.D) {
            System.out.println("Debug");
            debugSet = true;
            animatedReelsManager.printSlotMatrix();
            return true;
        }
        if (keycode == Input.Keys.C) {
            cycleDynamic = !cycleDynamic;
            return true;
        }
        return false;
    }

    private void setBoxesActive() {
        for (Body boxBody : reelBoxBodies)
            if (boxBody != null) {
                AnimatedReel animatedReel = (AnimatedReel) boxBody.getUserData();
                if (!animatedReel.getReel().isReelTileDeleted())
                    boxBody.setActive(true);
            }
    }

    private void reCreateBoxes() {
        numberOfReelsToFall = 0;
        animatedReelsMatrixCreator.setNumberOfReelsToFall(numberOfReelsToFall);
        for (AnimatedReel animatedReel : animatedReels)
            reinitialiseAnimatedReel(animatedReel);
        animatedReelsManager.setNumberOfReelsToFall(numberOfReelsToFall);
        reelBoxBodies = animatedReelsMatrixCreator.updateBoxBodiesFromAnimatedReels(
                animatedReels,
                reelBoxBodies);
        animatedReelsManager.setAnimatedReels(animatedReels);
        animatedReelsManager.setReelBodies(reelBoxBodies);
    }

    private void reinitialiseAnimatedReel(AnimatedReel animatedReel) {
        animatedReel.reinitialise();
        ReelTile reelTile = animatedReel.getReel();
        reelTile.setX(reelTile.getDestinationX());
        reelTile.setY(reelTile.getDestinationY() + SCREEN_OFFSET);
        reelTile.setIsFallen(false);
        reelTile.setIsStoppedFalling(false);
        if (!animatedReel.getReel().isReelTileDeleted()) {
            animatedReel.setupSpinning();
            animatedReel.getReel().startSpinning();
            numberOfReelsToFall++;
        }
    }

    @Override
    public void dispose () {
        if (world != null) {
            world.dispose();
        }
        if (renderer != null) {
            renderer.dispose();
        }
        if (debugRenderer != null) {
            debugRenderer.dispose();
        }
        if (font != null) {
            font.dispose();
        }
    }
}
