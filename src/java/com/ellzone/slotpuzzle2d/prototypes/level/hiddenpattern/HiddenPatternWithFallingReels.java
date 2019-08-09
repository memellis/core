package com.ellzone.slotpuzzle2d.prototypes.level.hiddenpattern;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorInjectionInterface;
import com.ellzone.slotpuzzle2d.level.creator.LevelObjectCreatorEntityHolder;
import com.ellzone.slotpuzzle2d.level.map.MapLevelNameComparator;
import com.ellzone.slotpuzzle2d.level.sequence.PlayScreenIntroSequence;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.physics.ReelSink;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import java.util.Random;

import box2dLight.RayHandler;

import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.PIXELS_PER_METER;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_HEIGHT;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_WIDTH;
import static com.ellzone.slotpuzzle2d.prototypes.screens.PlayScreenPrototype.SLOT_REEL_OBJECT_LAYER;

public class HiddenPatternWithFallingReels extends SPPrototypeTemplate
        implements LevelCreatorInjectionInterface {

    public static final String LEVELS_LEVEL_7 = "levels/level 7 - 40x40.tmx";
    public static final String LEVEL_7_NAME = "1-7";

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
    private RayHandler rayHandler;
    private int reelSpriteHelp;
    private Sound pullLeverSound;
    private Sound reelSpinningSound;
    private Sound reelStoppingSound;
    private PhysicsManagerCustomBodies physics;
    private OrthographicCamera camera;

    @Override
    protected void initialiseOverride() {
        touch = new Vector2();
        initialiseWorld();
        random = new Random();
        camera = new OrthographicCamera();
        slotReelScrollTexture = createSlotReelScrollTexture();
        initialisePhysics();
        loadlevel();
        createViewPorts();
        hud = setUpHud(batch);
        createIntroSequence();
    }

    private void initialiseWorld() {
        world = new World(new Vector2(0, -9.8f), true);
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

    private void loadlevel() {
        LevelObjectCreatorEntityHolder levelObjectCreator = new LevelObjectCreatorEntityHolder(this, world, rayHandler);
        TiledMap level = getLevelAssets(annotationAssetManager);
        Array<RectangleMapObject> extractedLevelRectangleMapObjects = extractLevelAssets(level);
        try {
            levelObjectCreator.createLevel(extractedLevelRectangleMapObjects);
            reels = levelObjectCreator.getAnimatedReels();
        } catch (GdxRuntimeException gdxRuntimeException) {
            throw new GdxRuntimeException(gdxRuntimeException);
        }
        initialiseReels();
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
        return level.getLayers().get(SLOT_REEL_OBJECT_LAYER).getObjects().getByType(RectangleMapObject.class);
    }

    private void initialiseReels() {
        for (AnimatedReel reel : reels)
            addReelStoppedListener(reel);
    }

    private void addReelStoppedListener(AnimatedReel reel) {
        reel.getReel().addListener(new ReelStoppedListener().invoke());
    }

    private class ReelStoppedListener {
        public ReelTileListener invoke() {
            return new ReelTileListener() {
                @Override
                public void actionPerformed(ReelTileEvent event, ReelTile source) {
                    if (event instanceof ReelStoppedSpinningEvent)
                        processReelStoppedSpinning(event, source);
                }
            };
        }
    }

    private void processReelStoppedSpinning(ReelTileEvent event, ReelTile source) {
        System.out.println("In processReelStoppedSpinningEvent");
    }

    private Texture createSlotReelScrollTexture() {
        Pixmap slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        return new Texture(slotReelScrollPixmap);
    }

    private void createIntroSequence() {
        PlayScreenIntroSequence playScreenIntroSequence = new PlayScreenIntroSequence(getReelTilesFromAnimatedReels(reels), tweenManager);
        playScreenIntroSequence.createReelIntroSequence(introSequenceCallback);
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
    }

    @Override
    protected void disposeOverride() {
        debugRenderer.dispose();
        rayHandler.dispose();
        world.dispose();
    }

    @Override
    protected void updateOverride(float dt) {
        handleInput();
        tweenManager.update(dt);
        for (AnimatedReel reel : reels)
            reel.update(dt);
        hud.update(dt);
    }

    @Override
    protected void renderOverride(float dt) {
        renderReels();
        renderRayHandler();
        renderHud(batch);
        renderWorld();
        physics.draw(batch);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        lightViewport.update(width, height);
        hudViewport.update(width, height);
    }

    private void handleInput() {
    }

    private void renderReels() {
        batch.begin();
        for (AnimatedReel reel : reels) {
            reel.draw(batch);
            sprites[reelSpriteHelp].setX(0);
            sprites[reelSpriteHelp].draw(batch);
        }
        batch.end();
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
            float angle = MathUtils.radiansToDegrees * reelBox.getAngle();
            if (index < reels.size) {
                ReelTile reelTile = reels.get(index).getReel();
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

    @Override
    protected void initialiseScreenOverride() {
    }

    @Override
    public AnnotationAssetManager getAnnotationAssetManager() {
        return annotationAssetManager;
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
        return slotHandleAtlas;
    }
}
