package com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorInjectionInterface;
import com.ellzone.slotpuzzle2d.level.creator.LevelObjectCreatorEntityHolder;
import com.ellzone.slotpuzzle2d.level.map.MapLevelNameComparator;
import com.ellzone.slotpuzzle2d.level.sequence.PlayScreenIntroSequence;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.LightButton;
import com.ellzone.slotpuzzle2d.sprites.HoldLightButton;
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
import com.ellzone.slotpuzzle2d.sprites.SlotHandleSprite;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import java.util.Random;

import box2dLight.RayHandler;

import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_HEIGHT;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.VIRTUAL_WIDTH;
import static com.ellzone.slotpuzzle2d.prototypes.screens.PlayScreenPrototype.SLOT_REEL_OBJECT_LAYER;
import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.PIXELS_PER_METER;

public class SpinningSlotsWithMatchesWinFlashesLoadedLevel
       extends SPPrototypeTemplate
       implements LevelCreatorInjectionInterface {

    public static final String LEVELS_LEVEL_6 = "levels/level 6 - 40x40.tmx";
    public static final String LEVEL_6_NAME = "1-6";

    private Random random;
    private Array<AnimatedReel> reels;
    private Sound pullLeverSound, reelSpinningSound, reelStoppingSound;
    private Vector2 touch;
    private TextureAtlas slotHandleAtlas;
    private int reelSpriteHelp;
    private Viewport lightViewport, hudViewport;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private RayHandler rayHandler;
    private Array<SlotHandleSprite> slotHandles = new Array<>();
    public Array<HoldLightButton> lightButtons = new Array<>();
    private int[][] reelGrid = new int[3][3];
    private int levelLightX, levelLightY;
    private Array<Array<Vector2>> rowMatchesToDraw;
    private ShapeRenderer shapeRenderer;
    private Texture slotReelScrollTexture;
    private Hud hud;
    private int score = 0;

    @Override
    protected void initialiseOverride() {
        touch = new Vector2();
        shapeRenderer = new ShapeRenderer();
        rowMatchesToDraw = new Array<Array<Vector2>>();
        initialiseWorld();
        random = new Random();
        slotReelScrollTexture = createSlotReelScrollTexture();
        loadlevel();
        createViewPorts();
        hud = setUpHud(batch);
        createIntroSequence();
   }

    private Texture createSlotReelScrollTexture() {
        Pixmap slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        return new Texture(slotReelScrollPixmap);
    }

    private Hud setUpHud(SpriteBatch batch) {
        Hud hud = new Hud(batch);
        hud.setLevelName(LEVEL_6_NAME);
        return hud;
    }

    private void initialiseWorld() {
        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();
        rayHandler = new RayHandler(world);
        rayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(0.2f, 0.2f, 0.2f, 0.25f);
    }

    private void loadlevel() {
        LevelObjectCreatorEntityHolder levelObjectCreator = new LevelObjectCreatorEntityHolder(this, world, rayHandler);
        TiledMap level = getLevelAssets(annotationAssetManager);
        Array<RectangleMapObject> extractedLevelRectangleMapObjects = extractLevelAssets(level);
        try {
            levelObjectCreator.createLevel(extractedLevelRectangleMapObjects);
            lightButtons = levelObjectCreator.getHoldLightButtons();
            reels = levelObjectCreator.getAnimatedReels();
            slotHandles = levelObjectCreator.getHandles();
        } catch (GdxRuntimeException gdxRuntimeException) {
            throw new GdxRuntimeException(gdxRuntimeException);
        }
        initialiseReels();
    }

    @Override
    protected void initialiseScreenOverride() {
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

    @Override
    protected void loadAssetsOverride() {
        slotHandleAtlas = annotationAssetManager.get(AssetsAnnotation.SLOT_HANDLE);
        pullLeverSound = annotationAssetManager.get(AssetsAnnotation.SOUND_PULL_LEVER);
        reelSpinningSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_SPINNING);
        reelStoppingSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_STOPPED);
    }

    private Array<RectangleMapObject> extractLevelAssets(TiledMap level) {
        Array<RectangleMapObject> levelRectangleMapObjects = getRectangleMapObjectsFromLevel(level);
        MapLevelNameComparator mapLevelNameComparator = new MapLevelNameComparator();
        levelRectangleMapObjects.sort(mapLevelNameComparator);
        return levelRectangleMapObjects;
    }

    private TiledMap getLevelAssets(AnnotationAssetManager annotationAssetManager) {
        return annotationAssetManager.get(LEVELS_LEVEL_6);
    }

    private Array<RectangleMapObject> getRectangleMapObjectsFromLevel(TiledMap level) {
        return level.getLayers().get(SLOT_REEL_OBJECT_LAYER).getObjects().getByType(RectangleMapObject.class);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        lightViewport.update(width, height);
        hudViewport.update(width, height);
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
        if (rowMatchesToDraw.size > 0)
            renderMacthedRows();
        renderLightButtons();
        renderRayHandler();
        renderHud(batch);
        renderWorld();
    }

    private void renderMacthedRows() {
        batch.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        for (Array<Vector2> matchedRow : rowMatchesToDraw) {
            if (matchedRow.size >= 2)
                for (int i = 0; i < matchedRow.size - 1; i++)
                    shapeRenderer.rectLine(matchedRow.get(i).x,
                                           matchedRow.get(i).y,
                                           matchedRow.get(i + 1).x,
                                           matchedRow.get(i + 1).y, 2);
        }
        shapeRenderer.end();
        batch.end();
    }

    private void renderWorld() {
        debugRenderer.render(world, lightViewport.getCamera().combined);
    }

    private void renderRayHandler() {
        rayHandler.setCombinedMatrix((OrthographicCamera) lightViewport.getCamera());
        rayHandler.updateAndRender();
    }

    private void renderLightButtons() {
        batch.setProjectionMatrix(lightViewport.getCamera().combined);
        batch.begin();
        for (LightButton lightButton : lightButtons)
            lightButton.getSprite().draw(batch);
        batch.end();
    }

    private void renderReels() {
        batch.begin();
        for (AnimatedReel reel : reels) {
            reel.draw(batch);
            sprites[reelSpriteHelp].setX(0);
            sprites[reelSpriteHelp].draw(batch);
        }
        for (SlotHandleSprite slotHandle : slotHandles)
            slotHandle.draw(batch);
        batch.end();
    }

    private void renderHud(SpriteBatch batch) {
        batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
    }

    @Override
    protected void initialiseUniversalTweenEngineOverride() {
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
    }

    private void initialiseReels() {
        for (AnimatedReel reel : reels)
            addReelStoppedListener(reel);
    }

    private void addReelStoppedListener(AnimatedReel reel) {
        reel.getReel().addListener(new ReelStoppedListener().invoke());
    }

    private Array<ReelTile> getReelTilesFromAnimatedReels(Array<AnimatedReel> animatedReels) {
        Array<ReelTile> reelTiles = new Array<>();
        for (AnimatedReel animatedReel : animatedReels)
            reelTiles.add(animatedReel.getReel());
        return reelTiles;
    }

    private void createIntroSequence() {
        PlayScreenIntroSequence playScreenIntroSequence = new PlayScreenIntroSequence(getReelTilesFromAnimatedReels(reels), tweenManager);
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
                System.out.println("Intro Sequence finished");
                break;
        }
    }

    public void handleInput() {
        if (Gdx.input.justTouched()) {
            touch = touch.set(Gdx.input.getX(), Gdx.input.getY());
            touch = viewport.unproject(touch);
            handleReelsTouched();
            handleSlotHandleIsTouch();
            touch = touch.set(Gdx.input.getX(), Gdx.input.getY());
            touch = lightViewport.unproject(touch);
            handleLightButtonTouched();
        }
    }

    private void handleLightButtonTouched() {
        for (LightButton lightButton : lightButtons) {
            if (lightButton.getSprite().getBoundingRectangle().contains(touch.x, touch.y))
                if (lightButton.getLight().isActive())
                    lightButton.getLight().setActive(false);
                else
                    lightButton.getLight().setActive(true);
        }
    }

    private void handleSlotHandleIsTouch() {
        for (SlotHandleSprite slotHandle : slotHandles)
            if (slotHandle.getBoundingRectangle().contains(touch))
                if (isReelsNotSpinning())
                    slotHandlePulled(slotHandle);
                else
                    reelStoppingSound.play();
    }

    private void slotHandlePulled(SlotHandleSprite slotHandle) {
        slotHandle.pullSlotHandle();
        pullLeverSound.play();
        clearRowMatchesToDraw();
        int i = 0;
        for (AnimatedReel animatedReel : reels) {
            if (!lightButtons.get(i).getLight().isActive()) {
                animatedReel.setEndReel(random.nextInt(sprites.length - 1));
                animatedReel.reinitialise();
                animatedReel.getReel().startSpinning();
            }
            i++;
        }
    }

    private void clearRowMatchesToDraw() {
        if (rowMatchesToDraw.size > 0)
            rowMatchesToDraw.removeRange(0, rowMatchesToDraw.size - 1);
    }

    private boolean isReelsNotSpinning() {
        boolean reelsNotSpinning = true;
        for (AnimatedReel animatedReel : reels)
            if (animatedReel.getReel().isSpinning())
                reelsNotSpinning = false;
        return reelsNotSpinning;
    }

    private void handleReelsTouched() {
        for (AnimatedReel animatedReel : reels) {
            if (animatedReel.getReel().getBoundingRectangle().contains(touch)) {
                clearRowMatchesToDraw();
                if (animatedReel.getReel().isSpinning()) {
                    if (animatedReel.getDampenedSineState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
                        reelSpriteHelp = animatedReel.getReel().getCurrentReel();
                        animatedReel.getReel().setEndReel(reelSpriteHelp - 1 < 0 ? 0 : reelSpriteHelp - 1);
                    }
                } else {
                    animatedReel.setEndReel(random.nextInt(sprites.length - 1));
                    animatedReel.reinitialise();
                    animatedReel.getReel().startSpinning();
                }
            }
        }
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
        rowMatchesToDraw = new Array<Array<Vector2>>();
        for (int row = 0; row < matchGrid.length; row++) {
            Array<ReelTileGridValue> depthSearchResults = puzzleGridTypeReelTile.depthFirstSearchIncludeDiagonals(matchGrid[row][0]);
            if (puzzleGridTypeReelTile.isRow(depthSearchResults, matchGrid))
                rowMatchesToDraw.add(drawMatches(depthSearchResults, 545, 450));
        }
    }

    private Array<Vector2> drawMatches(Array<ReelTileGridValue> depthSearchResults, int startX, int startY) {
        Array<Vector2> points = new Array<Vector2>();
        for (ReelTileGridValue cell : depthSearchResults) {
            hud.addScore(cell.value);
            points.add(new Vector2(startX + cell.c * 65, startY - cell.r * 65 ));
        }
        return points;
    }

    private void captureReelPositions() {
        for (int r = 0; r < reelGrid.length; r++) {
            for (int c = 0; c < reelGrid[0].length; c++)
                reelGrid[r][c] = getReelPosition(r, c);
        }
    }

    private int getReelPosition(int r, int c) {
        int reelPosition = reels.get(c).getEndReel() + r;
        if (reelPosition < 0)
            reelPosition = sprites.length - 1;
        else
            if(reelPosition > sprites.length - 1)
                reelPosition = 0;
        return reelPosition;
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