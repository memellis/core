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

package com.ellzone.slotpuzzle2d.prototypes.box2d.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.Random;

public class BallGenerator {
    private final int MAX_NBR = 30;
    private int ballNbr;

    private World world;
    private Stage stage;

    private ParticleEffectPool ballExplosionPool;

    private static BallGenerator ballGenerator;

    static public BallGenerator getInstance(){
        if(ballGenerator==null){
            ballGenerator = new BallGenerator();
        }
        return ballGenerator;
    }

    private BallGenerator(){
        ballNbr = 0;
        TextureAtlas textureAtlas = new TextureAtlas();
        textureAtlas.addRegion("particle",new TextureRegion(new Texture("box2d_particle_effects/particle.png")));
        ParticleEffect explosionEffect = new ParticleEffect();
        explosionEffect.load(Gdx.files.internal("box2d_particle_effects/particles.p"), textureAtlas);
        ballExplosionPool = new ParticleEffectPool(explosionEffect,MAX_NBR*2,  MAX_NBR*2);
    }

    public void setup(Stage aStage,World aWorld){
        stage = aStage;
        world = aWorld;
    }


    public void emit(){
        if(ballNbr<MAX_NBR) {
            Random rand = new Random();
            Ball ball = new Ball(world, (float) ((rand.nextInt(60) - 30)) / 10, 9);
            stage.addActor(ball);
            ballNbr++;
            Gdx.app.debug("generatBalls", "Balls:" + ballNbr);
        }
    }

    public void explode(Ball aBall){
        ParticleEffectPool.PooledEffect effect = ballExplosionPool.obtain();
        aBall.explode(effect);
        ballNbr--;
    }
}