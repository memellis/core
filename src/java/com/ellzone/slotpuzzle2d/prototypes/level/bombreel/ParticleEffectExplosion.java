/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.prototypes.level.bombreel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;

public class ParticleEffectExplosion extends SPPrototype {
    private SpriteBatch batch;
    private ParticleEffect particleEffect;

    @Override
    public void create() {
        batch = new SpriteBatch();
        particleEffect = new ParticleEffect();
        particleEffect.load(
                Gdx.files.internal("bomb/particle_explosion.p"),
                Gdx.files.internal("bomb"));
        particleEffect.setPosition(
                Gdx.graphics.getWidth() / 2,
                Gdx.graphics.getBackBufferHeight() / 2
        );
        particleEffect.start();
    }

    @Override
    public void render() {
        long start = TimeUtils.nanoTime();
        float dt = Gdx.graphics.getDeltaTime();
        float updateTime = (TimeUtils.nanoTime() - start) / 1000000000.0f;

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        batch.begin();
        particleEffect.draw(batch, dt);
        batch.end();
    }
}