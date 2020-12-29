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

package com.ellzone.slotpuzzle2d.sprites.reel;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;

public interface AnimatedReelInterface {
    void setupSpinning();

    void setX(float x);

    void setY(float y);

    void setSx(float sx);

    void setSy(float sy);

    float getSx();

    float getSy();

    int getEndReel();

    void setEndReel(int endReel);

    void update(float delta);

    void draw(SpriteBatch spriteBatch);

    void draw(ShapeRenderer shapeRenderer);

    ReelTile getReel();

    void reinitialise();

    DampenedSineParticle.DSState getDampenedSineState();
}
