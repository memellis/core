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
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.graphics.ParticleEmitterBox2D;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class FireEmitter extends Actor {

    ParticleEffect fireEmitter;

    public FireEmitter(World aWorld){
        TextureAtlas textureAtlas = new TextureAtlas();
        textureAtlas.addRegion("particle",new TextureRegion(new Texture("box2d_particle_effects/particle.png")));
        fireEmitter = new ParticleEffect();
        fireEmitter.load(Gdx.files.internal("box2d_particle_effects/continuous.p"), textureAtlas);
        fireEmitter.getEmitters().add(new ParticleEmitterBox2D(aWorld,fireEmitter.getEmitters().first()));
        fireEmitter.getEmitters().removeIndex(0);
        fireEmitter.setPosition(5,2);
        fireEmitter.scaleEffect(0.013f);
        fireEmitter.start();

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        fireEmitter.update(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        fireEmitter.draw(batch);
    }
}