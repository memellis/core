package com.ellzone.slotpuzzle2d.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ellzone.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.utils.Assets;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.equations.Quint;

public class SplashScreen implements Screen {
	private static final int PX_PER_METER = 400;
	private SlotPuzzle game;
	private final OrthographicCamera camera = new OrthographicCamera();
	boolean isLoaded;
 	private final TweenManager tweenManager = new TweenManager();
 	private Sprite slot;
 	private Sprite puzzle;
	private Sprite universal;
	private Sprite tween;
	private Sprite engine;
	private Sprite logo;
	private Sprite strip;
	private Sprite powered;
	private Sprite gdx;
	private Sprite veil;
	private TextureRegion gdxTex;
	
	public SplashScreen(SlotPuzzle game) {
		this.game = game;			
		defineSplashScreen();
	}

	private void defineSplashScreen() {
		isLoaded = false;
		Tween.setWaypointsLimit(10);
		Tween.setCombinedAttributesLimit(3);
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
		
		Assets.inst().load("splash/pack.atlas", TextureAtlas.class);
		Assets.inst().load("splash/splash3.pack.atlas", TextureAtlas.class);
		Assets.inst().update();
		Assets.inst().finishLoading();
		
		isLoaded = true;
		
		TextureAtlas atlas = Assets.inst().get("splash/pack.atlas", TextureAtlas.class);
		universal = atlas.createSprite("universal");
		tween = atlas.createSprite("tween");
		engine = atlas.createSprite("engine");
		logo = atlas.createSprite("logo");
		strip = atlas.createSprite("white");
		powered = atlas.createSprite("powered");
		gdx = atlas.createSprite("gdxblur");
		veil = atlas.createSprite("white");
		gdxTex = atlas.findRegion("gdx");

		TextureAtlas atlas1 = Assets.inst().get("splash/splash3.pack.atlas", TextureAtlas.class);
		slot = atlas1.createSprite("slot");
		puzzle = atlas1.createSprite("puzzle");
 
		float wpw = 1f;
		float wph = wpw * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
		
		camera.viewportWidth = wpw;
		camera.viewportHeight = wph;
		camera.update();

		Sprite[] sprites = new Sprite[] {slot, puzzle, universal, tween, engine, logo, powered, gdx};
		for (Sprite sp : sprites) {
			sp.setSize(sp.getWidth()/PX_PER_METER, sp.getHeight()/PX_PER_METER);
			sp.setOrigin(sp.getWidth()/2, sp.getHeight()/2);
		}

		slot.setPosition(-0.325f,  0.110f);
		puzzle.setPosition(0.020f, 0.110f);
		universal.setPosition(-0.325f, 0.028f);
		tween.setPosition(-0.320f, -0.066f);
		engine.setPosition(0.020f, -0.087f);
		logo.setPosition(0.238f, 0.022f);

		strip.setSize(wpw, wph);
		strip.setOrigin(wpw/2, wph/2);
		strip.setPosition(-wpw/2, -wph/2);

		powered.setPosition(-0.278f, -0.025f);
		gdx.setPosition(0.068f, -0.077f);

		veil.setSize(wpw, wph);
		veil.setPosition(-wpw/2, -wph/2);
		veil.setColor(1, 1, 1, 0);

		Timeline.createSequence()
			.push(Tween.set(slot, SpriteAccessor.POS_XY).targetRelative(-1,0))
			.push(Tween.set(puzzle, SpriteAccessor.POS_XY).targetRelative(1,0))			
			.push(Tween.set(tween, SpriteAccessor.POS_XY).targetRelative(-1, 0))
			.push(Tween.set(engine, SpriteAccessor.POS_XY).targetRelative(1, 0))
			.push(Tween.set(universal, SpriteAccessor.POS_XY).targetRelative(0, 0.5f))
			.push(Tween.set(logo, SpriteAccessor.SCALE_XY).target(7, 7))
			.push(Tween.set(logo, SpriteAccessor.OPACITY).target(0))
			.push(Tween.set(strip, SpriteAccessor.SCALE_XY).target(1, 0))
			.push(Tween.set(powered, SpriteAccessor.OPACITY).target(0))
			.push(Tween.set(gdx, SpriteAccessor.OPACITY).target(0))

			.pushPause(0.5f)
			.push(Tween.to(strip, SpriteAccessor.SCALE_XY, 0.8f).target(1, 0.6f).ease(Back.OUT))
			.push(Tween.to(slot, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Quart.OUT))			
			.push(Tween.to(puzzle, SpriteAccessor.POS_XY, 0.5f).targetRelative(-1, 0).ease(Quart.OUT))			
			.push(Tween.to(tween, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Quart.OUT))
			.push(Tween.to(engine, SpriteAccessor.POS_XY, 0.5f).targetRelative(-1, 0).ease(Quart.OUT))
			.push(Tween.to(universal, SpriteAccessor.POS_XY, 0.6f).targetRelative(0, -0.5f).ease(Quint.OUT))
			.pushPause(-0.3f)
			.beginParallel()
				.push(Tween.set(logo, SpriteAccessor.OPACITY).target(1))
				.push(Tween.to(logo, SpriteAccessor.SCALE_XY, 0.5f).target(1, 1).ease(Back.OUT))
			.end()
			.pushPause(0.3f)
			.push(Tween.to(slot, SpriteAccessor.SCALE_XY, 0.6f).waypoint(1.6f, 0.4f).target(1.2f, 1.2f).ease(Cubic.OUT))
			.push(Tween.to(puzzle, SpriteAccessor.SCALE_XY, 0.6f).waypoint(1.6f, 0.4f).target(1.2f, 1.2f).ease(Cubic.OUT))
			.pushPause(0.3f)
			.push(Tween.to(strip, SpriteAccessor.SCALE_XY, 0.5f).target(1, 1).ease(Back.IN))
			.pushPause(0.3f)
			.beginParallel()
				.push(Tween.to(slot, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
				.push(Tween.to(puzzle, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
				.push(Tween.to(tween, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
				.push(Tween.to(engine, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
				.push(Tween.to(universal, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
				.push(Tween.to(logo, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
			.end()
		
			.pushPause(-0.3f)
			.push(Tween.set(slot, SpriteAccessor.POS_XY).targetRelative(-1,0))
			.push(Tween.set(puzzle, SpriteAccessor.POS_XY).targetRelative(1,0))			
			.pushPause(0.5f)
			.push(Tween.to(slot, SpriteAccessor.POS_XY, 0.5f).targetRelative(-1, 0).ease(Quart.OUT))			
			.push(Tween.to(puzzle, SpriteAccessor.POS_XY, 0.5f).targetRelative(-1, 0).ease(Quart.OUT))			
			.pushPause(0.3f)
			.push(Tween.to(slot, SpriteAccessor.SCALE_XY, 0.6f).waypoint(1.6f, 0.4f).target(1.2f, 1.2f).ease(Cubic.OUT))
			.push(Tween.to(puzzle, SpriteAccessor.SCALE_XY, 0.6f).waypoint(1.6f, 0.4f).target(1.2f, 1.2f).ease(Cubic.OUT))
			.pushPause(-0.3f)
			.push(Tween.to(powered, SpriteAccessor.OPACITY, 0.3f).target(1))
			.beginParallel()
				.push(Tween.to(gdx, SpriteAccessor.OPACITY, 1.5f).target(1).ease(Cubic.IN))
				.push(Tween.to(gdx, SpriteAccessor.ROTATION, 2.0f).target(360*15).ease(Quad.OUT))
			.end()
			.pushPause(0.3f)
			.push(Tween.to(gdx, SpriteAccessor.SCALE_XY, 0.6f).waypoint(1.6f, 0.4f).target(1.2f, 1.2f).ease(Cubic.OUT))
			.pushPause(0.3f)
			.beginParallel()
				.push(Tween.to(powered, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
				.push(Tween.to(gdx, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
			.end()
			.pushPause(0.3f)

			.setCallback(new TweenCallback() {
				@Override
				public void onEvent(int arg0, BaseTween<?> arg1) {
					game.setScreen(new IntroScreen(game));
				}})
			.start(tweenManager);

	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}
	
	private void update(float dt) {
		tweenManager.update(dt);
		if (gdx.getRotation() > 360*15-20) gdx.setRegion(gdxTex);		
	}

	@Override
	public void render(float delta) {
		update(delta);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(isLoaded) {
			game.batch.setProjectionMatrix(camera.combined);
			game.batch.begin();
			strip.draw(game.batch);
			slot.draw(game.batch);
			puzzle.draw(game.batch);
			universal.draw(game.batch);
			tween.draw(game.batch);
			engine.draw(game.batch);
			logo.draw(game.batch);
			powered.draw(game.batch);
			gdx.draw(game.batch);
			if (veil.getColor().a > 0.1f) veil.draw(game.batch);
			game.batch.end();
		} else {
			if (Assets.inst().getProgress() < 1) {
				Assets.inst().update();
			} else {
				isLoaded = true;
			}
		}
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
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
		if (tweenManager != null) tweenManager.killAll();
	}

}
