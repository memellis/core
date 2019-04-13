package com.ellzone.slotpuzzle2d.prototypes.ashley;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.ellzone.slotpuzzle2d.components.MovementComponent;
import com.ellzone.slotpuzzle2d.components.PositionComponent;
import com.ellzone.slotpuzzle2d.components.SpriteComponent;
import com.ellzone.slotpuzzle2d.components.VisualComponent;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.sprites.Reels;
import com.ellzone.slotpuzzle2d.systems.MovementSystem;
import com.ellzone.slotpuzzle2d.systems.RenderSystem;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import java.text.MessageFormat;

public class RenderSystemTest extends SPPrototype {
    PooledEngine engine;
    Reels reels;

    public void create() {
        OrthographicCamera camera = setupCamera();

        AnnotationAssetManager annotationAssetManager = new AnnotationAssetManager();
        annotationAssetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        annotationAssetManager.load(new AssetsAnnotation());
        annotationAssetManager.finishLoading();

        reels = new Reels(annotationAssetManager);
        Texture crateTexture = new Texture("android.jpg");

        setupEngine(camera);

        setupCrate(crateTexture);

        for (int i = 0; i < 100; i++)
            addACoin();

    }

    private void addACoin() {
        Entity reelEntity = engine.createEntity();
        addPositionComponent(reelEntity);
        SpriteComponent spriteComponent = addSpriteComponent(reelEntity);
        addMovementComponent(reelEntity);
        addVisualComponent(reelEntity, spriteComponent);
        engine.addEntity(reelEntity);
    }

    private void addVisualComponent(Entity reelEntity, SpriteComponent spriteComponent) {
        reelEntity.add(new VisualComponent(spriteComponent.sprite));
    }

    private void addMovementComponent(Entity reelEntity) {
        reelEntity.add(new MovementComponent(10.0f, 10.0f));
    }

    private SpriteComponent addSpriteComponent(Entity reelEntity) {
        SpriteComponent spriteComponent = new SpriteComponent();
        spriteComponent.sprite = reels.getReels()[0];
        reelEntity.add(spriteComponent);
        return spriteComponent;
    }

    private void addPositionComponent(Entity reelEntity) {
        reelEntity.add(new PositionComponent(MathUtils.random(640), MathUtils.random(480)));
    }

    private void setupCrate(Texture crateTexture) {
        Entity crate = engine.createEntity();
        crate.add(new PositionComponent(50, 50));
        crate.add(new VisualComponent(new TextureRegion(crateTexture)));
        engine.addEntity(crate);
    }

    private void setupEngine(OrthographicCamera camera) {
        engine = new PooledEngine();
        engine.addSystem(new RenderSystem(camera));
        engine.addSystem(new MovementSystem());
    }

    private OrthographicCamera setupCamera() {
        OrthographicCamera camera = new OrthographicCamera(640, 480);
        camera.position.set(320, 240, 0);
        camera.update();
        return camera;
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        engine.update(Gdx.graphics.getDeltaTime());
    }
}
