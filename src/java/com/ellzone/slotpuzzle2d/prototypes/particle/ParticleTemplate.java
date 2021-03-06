/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.prototypes.particle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.math.MathUtils;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.*;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.utils.assets.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public abstract class ParticleTemplate extends SPPrototype {
	public static final float MINIMUM_VIEWPORT_SIZE = 15.0f;
    public static final String SLOTPUZZLE_PLAY = "SlotPuzzlePlay";
	protected FitViewport viewport;
	protected Stage stage;
	protected PerspectiveCamera cam;
    protected SpriteBatch batch;
	protected int displayWindowWidth;
	protected int displayWindowHeight;
	protected AnnotationAssetManager annotationAssetManager;
	
	protected abstract void initialiseOverride();
    protected abstract void loadAssetsOverride(AnnotationAssetManager annotationAssetManager);
    protected abstract void disposeOverride();
    protected abstract void updateOverride(float delta);
    protected abstract void renderOverride(float delta);
    protected abstract void initialiseUniversalTweenEngineOverride();
	
    @Override
    public void create() {
        if (isCustomDisplay()) {
            initialiseOverride();
            return;
        }

        initialiseLibGdx();
		initialiseScreen();
        initialiseCamera();
        annotationAssetManager = loadAssets();
        initialiseUniversalTweenEngine();
        initialiseOverride();
    }
	
	protected void initialiseScreen() {
		viewport = new FitViewport(SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT);
		stage = new Stage(viewport, batch); 
	}
	
    protected void initialiseCamera() {
        cam = new PerspectiveCamera();
        cam.position.set(0, 0, 10);
        cam.lookAt(0, 0, 0);
		displayWindowWidth = SlotPuzzleConstants.VIRTUAL_WIDTH;
        displayWindowHeight = SlotPuzzleConstants.VIRTUAL_HEIGHT;
    }

    protected void initialiseLibGdx() {
        batch = new SpriteBatch();
    }
	
	protected void initialiseUniversalTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        initialiseUniversalTweenEngineOverride();
    }
	
	protected AnnotationAssetManager loadAssets() {
        if (isCustomDisplay()) {
            loadAssetsOverride(annotationAssetManager);
            return null;
        }

        AnnotationAssetManager annotationAssetManager = new AnnotationAssetManager();
        annotationAssetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        annotationAssetManager.load(new AssetsAnnotation());
        annotationAssetManager.finishLoading();

        loadAssetsOverride(annotationAssetManager);

        return annotationAssetManager;
    }

	@Override
    public void resize(int width, int height) {
        float halfHeight = MINIMUM_VIEWPORT_SIZE * 0.5f;
        if (height > width)
            halfHeight *= (float)height / (float)width;
        float halfFovRadians = MathUtils.degreesToRadians * cam.fieldOfView * 0.5f;
        float distance = halfHeight / (float)Math.tan(halfFovRadians);
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.position.set(0, 0, distance);
        cam.lookAt(0, 0, 0);
        cam.update();
		viewport.update(width, height);
    }
	
	private void update(float delta) {
        updateOverride(delta);
    }
	
	@Override
    public void render() {
		final float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());
        if (isCustomDisplay()) {
            renderOverride(delta);
            return;
        }

        update(delta);
		Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		renderOverride(delta);
		stage.draw();
	}
	
	@Override
    public void pause() {
        Gdx.app.log(SLOTPUZZLE_PLAY, "pause");
    }

    @Override
    public void resume() {
        Gdx.app.log(SLOTPUZZLE_PLAY, "resume");
    }
	
	@Override
	public void dispose() {
		batch.dispose();
		disposeOverride();
	}
	
	protected boolean isCustomDisplay() {
        return false;
    }
}
