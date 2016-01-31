package com.ellzone.slotpuzzle2d.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.effects.Particle;
import com.ellzone.effects.ParticleAccessor;
import com.ellzone.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.sprites.ReelLetter;
import com.ellzone.slotpuzzle2d.sprites.ReelSlotTile;
import com.ellzone.utils.Assets;
import com.ellzone.utils.PixmapProcessors;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Quart;

public class PlayScreen implements Screen {
	private static final int PX_PER_METER = 600;
	private static final float SIXTY_FPS = 1/60f;
	private SlotPuzzle game;
	private final OrthographicCamera camera = new OrthographicCamera();
	private Viewport viewport;
	private Stage stage;
	private Sprite cheesecake;
	private Sprite cherry;
	private Sprite grapes;
	private Sprite jelly;
	private Sprite lemon;
	private Sprite peach;
	private Sprite pear;
	private Sprite tomato;
 	private final TweenManager tweenManager = new TweenManager();
	private boolean isLoaded = false;
	private Pixmap slotReelPixmap;
	private Texture slotReelTexture;
	private Array<ReelSlotTile> slotReels;

	
	public PlayScreen(SlotPuzzle game) {
		this.game = game;
		createPlayScreen();		
	}
	
	private void createPlayScreen() {
		Tween.setWaypointsLimit(10);
		Tween.setCombinedAttributesLimit(3);
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());

		viewport = new FitViewport(800, 480, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);
        
		Assets.inst().load("reel/reels.pack.atlas", TextureAtlas.class);
		Assets.inst().update();
		Assets.inst().finishLoading();
		isLoaded = true;

		TextureAtlas atlas = Assets.inst().get("reel/reels.pack.atlas", TextureAtlas.class);
		cherry = atlas.createSprite("cherry");
		cheesecake = atlas.createSprite("cheesecake");
		grapes = atlas.createSprite("grapes");
		jelly = atlas.createSprite("jelly");
		lemon = atlas.createSprite("lemon");
		peach = atlas.createSprite("peach");
		pear = atlas.createSprite("pear");
		tomato = atlas.createSprite("tomato");

		//float wpw = 1f;
		//float wph = wpw * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();

		//camera.viewportWidth = wpw;
		//camera.viewportHeight = wph;
		//viewport.update(800, 480);

		Sprite[] sprites = new Sprite[] {cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
		//for (Sprite sp : sprites) {
		//	sp.setSize(sp.getWidth()/PX_PER_METER, sp.getHeight()/PX_PER_METER);
		//	sp.setOrigin(sp.getWidth()/2, sp.getHeight()/2);
		//}
		
		// Fixme calculate scrollStep as a 1/4 of the sprite width
		slotReels = new Array<ReelSlotTile>();

		slotReelPixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);		
		slotReelPixmap = PixmapProcessors.createDynamicScrollAnimatedPixmap(sprites, 8);
		slotReelTexture = new Texture(slotReelPixmap);

		for (int i = 0; i < 8; i++) {
			slotReels.add(new ReelSlotTile(this, slotReelTexture, sprites.length, sprites.length * sprites.length, SIXTY_FPS, (i * 32 + viewport.getWorldWidth()) / 3.2f, viewport.getWorldHeight() / 2.0f + 32 + 10, i));
			
		}
						
		Timeline.createSequence()
			.push(Tween.set(slotReels.get(0), SpriteAccessor.POS_XY).target(-60f, -20f))
			.push(Tween.set(slotReels.get(1), SpriteAccessor.POS_XY).target(-60f,  12f))
			.push(Tween.set(slotReels.get(2), SpriteAccessor.POS_XY).target(-60f,  44f))
			.push(Tween.set(slotReels.get(3), SpriteAccessor.POS_XY).target(-60f,  76f))
			.push(Tween.set(slotReels.get(4), SpriteAccessor.POS_XY).target(-60f, 108f))
			.push(Tween.set(slotReels.get(5), SpriteAccessor.POS_XY).target(-60f, 140f))
			.push(Tween.set(slotReels.get(6), SpriteAccessor.POS_XY).target(-60f, 172f))
			.push(Tween.set(slotReels.get(7), SpriteAccessor.POS_XY).target(-60f, 204f))			
			.pushPause(0.5f)
			.push(Tween.to(slotReels.get(0), SpriteAccessor.POS_XY, 0.8f).target(266f, 280f))
			.push(Tween.to(slotReels.get(1), SpriteAccessor.POS_XY, 0.8f).target(298f, 280f))
			.push(Tween.to(slotReels.get(2), SpriteAccessor.POS_XY, 0.8f).target(330f, 280f))
			.push(Tween.to(slotReels.get(3), SpriteAccessor.POS_XY, 0.8f).target(362f, 280f))
			.push(Tween.to(slotReels.get(4), SpriteAccessor.POS_XY, 0.8f).target(394f, 280f))
			.push(Tween.to(slotReels.get(5), SpriteAccessor.POS_XY, 0.8f).target(426f, 280f))
			.push(Tween.to(slotReels.get(6), SpriteAccessor.POS_XY, 0.8f).target(458f, 280f))
			.push(Tween.to(slotReels.get(7), SpriteAccessor.POS_XY, 0.8f).target(490f, 280f))
			.pushPause(0.3f)
			.start(tweenManager);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label gameOverLabel = new Label("PLAY SCREEN", font);
        Label playAgainLabel = new Label("Click to Play Again", font);

        table.add(gameOverLabel).expandX();
        table.row();
        table.add(playAgainLabel).expandX().padTop(10f);

        stage.addActor(table);		
   	}

	@Override
	public void show() {
		// TODO Auto-generated method stub	
	}
	
	public void handleInput(float dt) {
		//if (Gdx.input.isKeyJustPressed(key))
	}
	
	private void update(float delta) {
		tweenManager.update(delta);
		for (ReelSlotTile reel : slotReels) {
			reel.update(delta);
		}
	}

	@Override
	public void render(float delta) {
		update(delta);
		if(Gdx.input.justTouched()) {
            game.setScreen(new EndOfGameScreen(game));
            dispose();
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(isLoaded) {
			//game.batch.setProjectionMatrix(camera.combined);
			game.batch.begin();
			for (ReelSlotTile reel : slotReels) {
				reel.draw(game.batch);
			}
			game.batch.end();
		} else {
			if (Assets.inst().getProgress() < 1) {
				Assets.inst().update();
			} else {
				isLoaded = true;
			}
		}

        stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width,  height);		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		stage.dispose();
		
	}

}
