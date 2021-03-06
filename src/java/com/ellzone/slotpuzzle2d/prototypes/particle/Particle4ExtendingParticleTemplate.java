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

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelSprites;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTiles;
import com.ellzone.slotpuzzle2d.physics.Particles;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle2d.physics.Particle;
import com.ellzone.slotpuzzle2d.physics.Vector;
import com.ellzone.slotpuzzle2d.physics.DampenedSine;
import com.badlogic.gdx.math.Vector2;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsCallback;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsEvent;
import com.ellzone.slotpuzzle2d.utils.Random;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class Particle4ExtendingParticleTemplate extends ParticleTemplate {
	private ShapeRenderer shapeRenderer;
	private ReelSprites reelSprites;
	private Sprite[] sprites;
	private ReelTiles reelTiles;
	private Array<ReelTile> reelTilesArray;
	private int slotReelScrollheight;
	private Particles particles;
	private Array<Particle> reelParticles;
	private Array<Vector2> points = new Array<Vector2>();
	private Vector accelerator;
	private int dampPoint;
	private Array<DampenedSine> dampenedSines;
	private DampenedSine dampenedSine;
	private float graphStep;
	
	@Override
	protected void initialiseOverride() {
		initialiseReelTiles(annotationAssetManager);
		initialiseParticles();
        initialiseDampenedSine();
		shapeRenderer = new ShapeRenderer();
	}

	@Override
	protected void loadAssetsOverride(AnnotationAssetManager annotationAssetManager) {
	}
	
	private void initialiseReelTiles(AnnotationAssetManager annotationAssetManager) {
		reelSprites = new ReelSprites(annotationAssetManager);
		sprites = reelSprites.getSprites();
		reelTiles = new ReelTiles(reelSprites);
		reelTilesArray = reelTiles.getReelTiles();
		slotReelScrollheight = reelTiles.getReelTileTextureHeight();
	}
	
	private void initialiseParticles() {
		particles = new Particles(reelSprites, reelTiles);
		reelParticles = particles.getParticles();
		accelerator = particles.getAccelerator();
		dampPoint = particles.getDampoint();
	}
	
    private void initialiseDampenedSine() {
        dampenedSines = new Array<DampenedSine>();
        dampenedSine = new DampenedSine(0,
				                        reelTilesArray.get(0).getSy(),
				                       0,
				                       0,
				                       0,
				                       slotReelScrollheight * 20,
				                        slotReelScrollheight,
				                        reelTilesArray.get(0).getEndReel(),
                                        (int) reelTilesArray.get(0).getWidth());

        dampenedSine.setCallback(new SPPhysicsCallback() {
				public void onEvent(int type, SPPhysicsEvent event) {
					delegateDSCallback(type);
				};
			});
        dampenedSine.setCallbackTriggers(SPPhysicsCallback.PARTICLE_UPDATE + SPPhysicsCallback.END);
        dampenedSines.add(dampenedSine);
    }

    private void delegateDSCallback(int type) {
        if (type == SPPhysicsCallback.PARTICLE_UPDATE) {
            addGraphPoint(new Vector2(graphStep++ % displayWindowWidth, (displayWindowHeight / 2 + dampenedSines.get(0).dsEndReel)));
        } else {
            if (type == SPPhysicsCallback.END) {
                reelTilesArray.get(0).setEndReel(Random.getInstance().nextInt(sprites.length - 1));
                dampenedSines.get(0).initialiseDampenedSine();
                dampenedSines.get(0).position.y = 0;
                dampenedSines.get(0).setEndReel(reelTilesArray.get(0).getEndReel());
				dampenedSines.get(0).setCallback(new SPPhysicsCallback() {
					public void onEvent(int type, SPPhysicsEvent event) {
						delegateDSCallback(type);
					};
				});

			}
        }
    }
	
	@Override
	protected void disposeOverride() {
	}

	@Override
	protected void updateOverride(float delta) {
        for(ReelTile reelTile : reelTilesArray) {
            dampenedSines.get(0).update();
            reelTile.setSy(dampenedSines.get(0).position.y);
            reelTile.update(delta);
        }
	}

	@Override
	protected void renderOverride(float delta) {
        batch.begin();
        for (ReelTile reelTile : reelTilesArray) {
            reelTile.draw(batch);
 			sprites[reelTile.getEndReel()].setX(reelTile.getWidth());
			sprites[reelTile.getEndReel()].draw(batch);
		}
        batch.end();
        drawGraphPoint(shapeRenderer);
	}
	private void addGraphPoint(Vector2 newPoint) {
        points.add(newPoint);
    }

    private void drawGraphPoint(ShapeRenderer shapeRenderer) {
        if (points.size >= 2) {
            shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            int rr = 23;
            int rg = 32;
            int rb = 23;
            shapeRenderer.setColor(rr, rg, rb, 255);
            for (int i = 0; i < points.size - 1; i++) {
                shapeRenderer.line(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
            }
            shapeRenderer.end();
        }
    }
	
	@Override
	protected void initialiseUniversalTweenEngineOverride() {
	}
}
