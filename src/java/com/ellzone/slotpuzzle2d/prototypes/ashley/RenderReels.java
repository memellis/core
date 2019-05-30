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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.components.PositionComponent;
import com.ellzone.slotpuzzle2d.components.ReelTileComponent;
import com.ellzone.slotpuzzle2d.components.VisualComponent;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.Reels;
import com.ellzone.slotpuzzle2d.systems.ReelTileMovementSystem;
import com.ellzone.slotpuzzle2d.systems.RenderSystem;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.utils.Random;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;

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

    private Reels initialiseReels(AnnotationAssetManager annotationAssetManager) {
        Reels reels = new Reels(annotationAssetManager);
        sprites = reels.getReels();
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

        return reels;
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
        introSequence = Timeline.createParallel();
        for(int i = 0; i < reelTiles.size; i++) {
            introSequence = introSequence
                    .push(buildSequence(reelTiles.get(i), i, random.nextFloat() * 5.0f, random.nextFloat() * 5.0f, reelTiles.size));
        }

        introSequence = introSequence
                .pushPause(0.3f)
                .start(tweenManager);
    }

    private Timeline buildSequence(Sprite target, int id, float delay1, float delay2, int numberOfSprites) {
        Vector2 targetXY = getRandomCorner();
        int targetPositionX = (id * spriteWidth) + (displayWindowWidth - (((spriteWidth * numberOfSprites) +  displayWindowWidth) / 2));
        return Timeline.createSequence()
                .push(SlotPuzzleTween.set(target, SpriteAccessor.POS_XY).target(targetXY.x, targetXY.y))
                .push(SlotPuzzleTween.set(target, SpriteAccessor.SCALE_XY).target(30, 30))
                .push(SlotPuzzleTween.set(target, SpriteAccessor.ROTATION).target(0))
                .push(SlotPuzzleTween.set(target, SpriteAccessor.OPACITY).target(0))
                .pushPause(delay1)
                .beginParallel()
                .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1, 1).ease(Quart.INOUT))
                .end()
                .pushPause(-0.5f)
                .push(SlotPuzzleTween.to(target, SpriteAccessor.POS_XY, 1.0f).target(targetPositionX, displayWindowHeight / 2                                                                                         ).ease(Back.OUT))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.ROTATION, 0.8f).target(360).ease(Cubic.INOUT))
                .pushPause(delay2)
                .beginParallel()
                .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 0.3f).target(3, 3).ease(Quad.IN))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 0.3f).target(0).ease(Quad.IN))
                .end()
                .pushPause(-0.5f)
                .beginParallel()
                .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1, 1).ease(Quart.INOUT))
                .end();
    }

    private Vector2 getRandomCorner() {
        int randomCorner = random.nextInt(4);
        switch (randomCorner) {
            case 0:
                return new Vector2(-1 * random.nextFloat(), -1 * random.nextFloat());
            case 1:
                return new Vector2(-1 * random.nextFloat(), displayWindowWidth + random.nextFloat());
            case 2:
                return new Vector2(displayWindowHeight + random.nextFloat(), -1 * random.nextFloat());
            case 3:
                return new Vector2(displayWindowHeight + random.nextFloat(), displayWindowWidth + random.nextFloat());
            default:
                return new Vector2(-0.5f, -0.5f);
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