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

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class ParticleExplosionDemo {
//    private World world;
//    public Array<Vector2> explosions = new Array<Vector2>();
//    public Array<ExplosionParticle> explosionParticles = new Array<ExplosionParticle>();
//
//    private void explodeABomb(Vector2 vector){
//        int numRays = ExplosionParticle.NUMRAYS;
//        for (int i = 0; i < numRays; i++) {
//            float angle = (i / (float)numRays) * 360 * MathUtils.degreesToRadians;
//            Vector2 rayDir = new Vector2( (float) Math.sin(angle),(float) Math.cos(angle) );
//            explosionParticles.add(new ExplosionParticle(world, vector, rayDir)); // create the particle
//        }
//        explosions.add(vector); // add explosion particle effect at vector
//    }
//
//    public void createBlast(Bomb bomb) {
//        bombsToExplode.add(bomb.body.getPosition());
//        bomb.isDead = true; // sets the bomb to dead so it can be removed outside of the box2d physics calculations
//    }
//
//
//    public void checkIfExplosionParticleStopped() {
//        for (ExplosionParticle party : partys) {
//            if (Math.abs(party.body.getLinearVelocity().x) < 5f
//                    && Math.abs(party.body.getLinearVelocity().y) < 5f) {
//                world.destroyBody(party.body);
//                partys.removeValue(party, true);
//            }
//        }
//    }
}
