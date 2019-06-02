package com.ellzone.slotpuzzle2d.prototypes.ashley;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.components.PositionComponent;
import com.ellzone.slotpuzzle2d.components.ReelTileComponent;
import com.ellzone.slotpuzzle2d.components.VisualComponent;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.level.PlayScreenIntroSequence;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelSprites;
import com.ellzone.slotpuzzle2d.systems.ReelTileMovementSystem;
import com.ellzone.slotpuzzle2d.systems.RenderSystem;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.utils.Random;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class RenderReels extends SPPrototype {
    public static final int NUMBER_OF_REEL_TILES = 15;
    private Sprite[] sprites;
    private int spriteWidth, spriteHeight;
    private int displayWindowWidth, displayWindowHeight;
    private Array<ReelTile> reelTiles;
    private PooledEngine engine;
    private Random random = Random.getInstance();
    private Timeline introSequence;
    private TweenManager tweenManager = new TweenManager();

    public void create() {
        intialiseDisplayWidthHeight();
        OrthographicCamera camera = setupCamera();
        initialiseUniversalTweenEngine();
        setupEngine(camera);
        initialiseReels(getAnnotationAssetManager());
        initialReelTiles();
        createIntroSequence();
    }

    private void initialiseUniversalTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
    }

    private void intialiseDisplayWidthHeight() {
        displayWindowWidth = SlotPuzzleConstants.VIRTUAL_WIDTH;
        displayWindowHeight = SlotPuzzleConstants.VIRTUAL_HEIGHT;
    }

    private OrthographicCamera setupCamera() {
        OrthographicCamera camera = new OrthographicCamera(SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT);
        camera.position.set(displayWindowWidth / 2, displayWindowHeight / 2, 0);
        camera.update();
        return camera;
    }

    private AnnotationAssetManager getAnnotationAssetManager() {
        AnnotationAssetManager annotationAssetManager = new AnnotationAssetManager();
        annotationAssetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        annotationAssetManager.load(new AssetsAnnotation());
        annotationAssetManager.finishLoading();
        return annotationAssetManager;
    }

    public void initialReelTiles() {
        reelTiles = new Array<ReelTile>();
        Texture slotReelScrollTexture = getReelTexture();
        for (int i = 0; i < NUMBER_OF_REEL_TILES; i++) {
            addReelTile(slotReelScrollTexture, i);
        }
    }

    private void addReelTile(Texture slotReelScrollTexture, int i) {
        ReelTile reelTile = new ReelTile(slotReelScrollTexture, slotReelScrollTexture.getHeight(), 0, 0, spriteWidth, spriteHeight, spriteWidth, spriteHeight, 0, null);
        reelTile.setX(i * spriteHeight);
        reelTile.setY(i * spriteWidth);
        reelTile.setSx(0);
        reelTile.setSy(0);
        reelTile.setEndReel(random.nextInt(sprites.length - 1));
        reelTiles.add(reelTile);
        Entity reelEntity = engine.createEntity();
        ReelTileComponent reelTileComponent = new ReelTileComponent(reelTile);
        reelEntity.add(reelTileComponent);
        reelEntity.add(new PositionComponent(MathUtils.random(displayWindowWidth), MathUtils.random(displayWindowHeight)));
        addVisualComponent(reelEntity, reelTile);
        engine.addEntity(reelEntity);
    }

    private Entity addVisualComponent(Entity reelEntity, TextureRegion textureRegion) {
        reelEntity.add(new VisualComponent(textureRegion));
        return reelEntity;
    }

    private ReelSprites initialiseReels(AnnotationAssetManager annotationAssetManager) {
        ReelSprites reelSprites = new ReelSprites(annotationAssetManager);
        sprites = reelSprites.getSprites();
        float startPosition = (displayWindowWidth - sprites.length * sprites[0].getWidth()) / 2;
        int i = 0;
        for (Sprite sprite : sprites) {
            sprite.setOrigin(0, 0);
            sprite.setX(startPosition + i * sprite.getWidth());
            sprite.setY((float) displayWindowHeight / 2 - sprite.getHeight() / 2);
            i++;
        }
        spriteWidth = (int) sprites[0].getWidth();
        spriteHeight = (int) sprites[0].getHeight();

        return reelSprites;
    }

    private Texture getReelTexture() {
        Pixmap slotReelScrollPixmap = new Pixmap(spriteWidth, spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        return new Texture(slotReelScrollPixmap);
    }

    private void setupEngine(OrthographicCamera camera) {
        engine = new PooledEngine();
        engine.addSystem(new ReelTileMovementSystem());
        engine.addSystem(new RenderSystem(camera));
    }

    private void createIntroSequence() {
        PlayScreenIntroSequence playScreenIntroSequence = new PlayScreenIntroSequence(reelTiles, tweenManager);
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
                System.out.print("Intro Sequence finished");
                break;
        }
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tweenManager.update(Gdx.graphics.getDeltaTime());
        engine.update(Gdx.graphics.getDeltaTime());
     }
}