package com.ellzone.slotpuzzle2d.prototypes.ashley;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ellzone.slotpuzzle2d.components.AnimatedReelComponent;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.level.LevelAnimatedReelAction;
import com.ellzone.slotpuzzle2d.level.LevelAnimatedReelCallback;
import com.ellzone.slotpuzzle2d.level.LevelCreatorInjectionInterface;
import com.ellzone.slotpuzzle2d.level.LevelHoldLightButtonCallback;
import com.ellzone.slotpuzzle2d.level.LevelHoldLightHoldButtonAction;
import com.ellzone.slotpuzzle2d.level.LevelObjectCreator;
import com.ellzone.slotpuzzle2d.level.LevelPointLightAction;
import com.ellzone.slotpuzzle2d.level.LevelPointLightCallback;
import com.ellzone.slotpuzzle2d.level.LevelReelHelperAction;
import com.ellzone.slotpuzzle2d.level.LevelReelHelperCallback;
import com.ellzone.slotpuzzle2d.level.LevelSlotHandleSpriteAction;
import com.ellzone.slotpuzzle2d.level.LevelSlotHandleSpriteCallback;
import com.ellzone.slotpuzzle2d.level.PlayScreenIntroSequence;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
import com.ellzone.slotpuzzle2d.systems.AnimatedReelSystem;
import com.ellzone.slotpuzzle2d.systems.LightSystem;
import com.ellzone.slotpuzzle2d.systems.PlayerControlSystem;
import com.ellzone.slotpuzzle2d.systems.ReelTileMovementSystem;
import com.ellzone.slotpuzzle2d.systems.RenderSystem;
import com.ellzone.slotpuzzle2d.systems.SlotHandlePulledPlayerSystemEvent;
import com.ellzone.slotpuzzle2d.systems.SlotHandleSystem;
import com.ellzone.slotpuzzle2d.systems.SystemCallback;
import com.ellzone.slotpuzzle2d.systems.SystemEvent;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import java.util.Comparator;
import box2dLight.RayHandler;

import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.EARTH_GRAVITY;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_HEIGHT;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_WIDTH;
import static com.ellzone.slotpuzzle2d.prototypes.screens.PlayScreenPrototype.SLOT_REEL_OBJECT_LAYER;

public class RenderMiniSllotMachineLoadedFromALevel
       extends SPPrototype
       implements LevelCreatorInjectionInterface {

    public static final String LEVEL_6_ASSETS = "levels/level 6 component based - 40x40.tmx";
    private PooledEngine engine;
    private World world;
    private RayHandler rayHandler;
    private AnnotationAssetManager annotationAssetManager;
    private TextureAtlas slotHandleAtlas;
    private Texture slotReelScrollTexture;
    private Array<AnimatedReel> animatedReels;
    private Array<ReelTile> reelTiles;
    private ReelSprites reelSprites;
    private TweenManager tweenManager = new TweenManager();
    private RenderSystem renderSystem;
    private FitViewport viewport;
    private int[][] reelGrid = new int[3][3];
    private Array<Array<Vector2>> rowMacthesToDraw = new Array<Array<Vector2>>();
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;

    public void create() {
        OrthographicCamera camera = setupCamera();
        world = createWorld();
        rayHandler = createRayHandler(world);
        annotationAssetManager = loadAssets();
        createSlotReelScrollTexture(annotationAssetManager);
        initialiseUniversalTweenEngine();
        setUpScreen(camera);
        setupEngine(rayHandler, camera);
        loadlevel();
        animatedReels = getAnimatedReels();
        reelTiles = getReelTilesFromAnimatedReels(animatedReels);
        setUpIntroSequence(reelTiles);
        addAnimatedReelsStopListener(animatedReels);
        batch = renderSystem.getSpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }

    private OrthographicCamera setupCamera() {
        OrthographicCamera camera = new OrthographicCamera(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGHT / 2, 0);
        camera.update();
        return camera;
    }

    private void setUpScreen(OrthographicCamera camera) {
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
    }

    private World createWorld() {
        return new World(new Vector2(0, EARTH_GRAVITY), true);
    }

    private RayHandler createRayHandler(World world) {
        RayHandler rayHandler = new RayHandler(world);
        rayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(0.2f, 0.2f, 0.2f, 0.25f);
        return rayHandler;
    }

    private AnnotationAssetManager loadAssets() {
        AnnotationAssetManager annotationAssetManager = new AnnotationAssetManager();
        annotationAssetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        annotationAssetManager.load(new AssetsAnnotation());
        annotationAssetManager.finishLoading();
        return annotationAssetManager;
    }

    private void createSlotReelScrollTexture(AnnotationAssetManager annotationAssetManager) {
        reelSprites = new ReelSprites(annotationAssetManager);
        Pixmap slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(reelSprites.getSprites());
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
    }

    private void initialiseUniversalTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
    }

    private void setupEngine(RayHandler rayHandler, OrthographicCamera camera) {
        engine = new PooledEngine();
        engine.addSystem(new AnimatedReelSystem());
        engine.addSystem(new ReelTileMovementSystem());
        engine.addSystem(new SlotHandleSystem());
        renderSystem = new RenderSystem(world, rayHandler, camera);
        engine.addSystem(renderSystem);
        engine.addSystem(new LightSystem(world, rayHandler, (OrthographicCamera) renderSystem.getLightViewport().getCamera()));
        PlayerControlSystem playerControlSystem = new PlayerControlSystem(viewport, renderSystem.getLightViewport(), annotationAssetManager);
        engine.addSystem(playerControlSystem);
        playerControlSystem.addCallback(slotHandlePulledAction);
    }

    private TiledMap getLevelAssets(AnnotationAssetManager annotationAssetManager) {
        return annotationAssetManager.get(LEVEL_6_ASSETS);
    }

    private TextureAtlas getSlotHanldeAtlas(AnnotationAssetManager annotationAssetManager) {
        return annotationAssetManager.get(AssetsAnnotation.SLOT_HANDLE);
    }

    private void loadlevel() {
        LevelObjectCreator levelObjectCreator = new LevelObjectCreator(this, world, rayHandler);
        addLevelCallbacks(levelObjectCreator);
        TiledMap level = getLevelAssets(annotationAssetManager);
        slotHandleAtlas = getSlotHanldeAtlas(annotationAssetManager);
        Array<RectangleMapObject> extractedLevelRectangleMapObjects = extractLevelAssets(level);
        try {
            levelObjectCreator.createLevel(extractedLevelRectangleMapObjects);
        } catch (GdxRuntimeException gdxRuntimeException) {
            throw new GdxRuntimeException(gdxRuntimeException);
        }
    }

    private void addLevelCallbacks(LevelObjectCreator levelObjectCreator) {
        LevelHoldLightButtonCallback levelHoldLightButtonCallback = new LevelHoldLightHoldButtonAction(engine, world, rayHandler);
        levelObjectCreator.addHoldLightButtonCallback(levelHoldLightButtonCallback);
        LevelAnimatedReelCallback levelAnimatedReelCallback = new LevelAnimatedReelAction(engine);
        levelObjectCreator.addAnimatedReelCallback(levelAnimatedReelCallback);
        LevelSlotHandleSpriteCallback levelSlotHandleSpriteCallback = new LevelSlotHandleSpriteAction(engine);
        levelObjectCreator.addSlotHandleCallback(levelSlotHandleSpriteCallback);
        LevelPointLightCallback levelPointLightCallback = new LevelPointLightAction(engine);
        levelObjectCreator.addPointLightCallback(levelPointLightCallback);
        LevelReelHelperCallback levelReelHelperCallback = new LevelReelHelperAction(engine);
        levelObjectCreator.addReelHelperCallback(levelReelHelperCallback);
    }

    private Array<RectangleMapObject> extractLevelAssets(TiledMap level) {
        Array<RectangleMapObject> levelRectangleMapObjects = getRectangleMapObjectsFromLevel(level);
        MapLevelNameComparator mapLevelNameComparator = new MapLevelNameComparator();
        levelRectangleMapObjects.sort(mapLevelNameComparator);
        return levelRectangleMapObjects;
    }

    public class MapLevelNameComparator implements Comparator<RectangleMapObject> {
        @Override
        public int compare(RectangleMapObject first, RectangleMapObject second) {
            return first.getName().compareTo(second.getName());
        }
    }

    private Array<RectangleMapObject> getRectangleMapObjectsFromLevel(TiledMap level) {
        return level.getLayers().get(SLOT_REEL_OBJECT_LAYER).getObjects().getByType(RectangleMapObject.class);
    }

    private void setUpIntroSequence(Array<ReelTile> reelTiles) {
        PlayScreenIntroSequence playScreenIntroSequence = new PlayScreenIntroSequence(reelTiles, tweenManager);
        playScreenIntroSequence.createReelIntroSequence(introSequenceCallback);
        renderSystem.setStartRendering(true);
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
                System.out.print("Intro Sequence finished");
                break;
        }
    }

    private Array<AnimatedReel> getAnimatedReels() {
        Array<AnimatedReel> animatedReels = new Array<>();
        IteratingSystem animatedReelSystem = engine.getSystem(AnimatedReelSystem.class);
        ImmutableArray<Entity> animatedReelEntities = animatedReelSystem.getEntities();
        for (Entity animatedReelEntity : animatedReelEntities) {
            AnimatedReelComponent animatedReelComponent = animatedReelEntity.getComponent(AnimatedReelComponent.class);
            animatedReels.add(animatedReelComponent.animatedReel);
        }
        return animatedReels;
    }

    private Array<ReelTile> getReelTilesFromAnimatedReels(Array<AnimatedReel> animatedReels) {
        Array<ReelTile> reelTiles = new Array<>();
        for (AnimatedReel animatedReel : animatedReels)
            reelTiles.add(animatedReel.getReel());
        return reelTiles;
    }

    private void addAnimatedReelsStopListener(Array<AnimatedReel> animatedReels) {
        for (AnimatedReel reel : animatedReels)
            addReelStoppedListener(reel);
    }

    private void addReelStoppedListener(AnimatedReel animatedReel) {
        animatedReel.getReel().addListener(new ReelStoppedListener().invoke());
    }

    private class ReelStoppedListener {
        public ReelTileListener invoke() {
            return new ReelTileListener() {
                @Override
                public void actionPerformed(ReelTileEvent event, ReelTile source) {
                if (event instanceof ReelStoppedSpinningEvent) {
                    matchReels();
                }
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
            if (puzzleGridTypeReelTile.isRow(depthSearchResults, matchGrid)) {
                rowMacthesToDraw.add(drawMatches(depthSearchResults, 545, 450));
            };
        }
    }

    private Array<Vector2> drawMatches(Array<ReelTileGridValue> depthSearchResults, int startX, int startY) {
        Array<Vector2> points = new Array<Vector2>();
        for (ReelTileGridValue cell : depthSearchResults) {
            points.add(new Vector2(startX + cell.c * 65, startY - cell.r * 65 ));
        }
        return points;
    }

    private void captureReelPositions() {
        for (int r = 0; r < reelGrid.length; r++) {
            for (int c = 0; c < reelGrid[0].length; c++) {
                reelGrid[r][c] = getReelPosition(r, c);
            }
        }
    }

    private int getReelPosition(int r, int c) {
        int reelPosition = reelTiles.get(c).getEndReel() + r;
        if (reelPosition < 0)
            reelPosition = reelTiles.get(c).getNumberOfReelsInTexture() - 1;
        else {
            if(reelPosition > reelTiles.get(c).getNumberOfReelsInTexture() - 1) {
                reelPosition = 0;
            }
        }
        return reelPosition;
    }

    private SystemCallback slotHandlePulledAction = new SystemCallback() {
        @Override
        public void onEvent(SystemEvent systemEvent, Object source) {
            delegateSlotHandlePulledAction(systemEvent, source);
        }
    };

    private void delegateSlotHandlePulledAction(SystemEvent systemEvent, Object source) {
        if (systemEvent instanceof SlotHandlePulledPlayerSystemEvent)
            if (rowMacthesToDraw.size > 0)
                rowMacthesToDraw.removeRange(0, rowMacthesToDraw.size - 1);
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

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        renderSystem.getLightViewport().update(width, height);
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        engine.update(Gdx.graphics.getDeltaTime());
        tweenManager.update(Gdx.graphics.getDeltaTime());
        renderMacthedRows(batch, shapeRenderer);
    }

    private void renderMacthedRows(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        batch.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        for (Array<Vector2> matchedRow : rowMacthesToDraw) {
            if (matchedRow.size >= 2)
                for (int i = 0; i < matchedRow.size - 1; i++) {
                    shapeRenderer.rectLine(matchedRow.get(i).x, matchedRow.get(i).y, matchedRow.get(i + 1).x, matchedRow.get(i + 1).y, 2);
                }
        }
        shapeRenderer.end();
        batch.end();
    }
}
