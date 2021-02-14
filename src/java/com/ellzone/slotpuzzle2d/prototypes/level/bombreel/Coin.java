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
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;

import static com.ellzone.slotpuzzle2d.prototypes.level.bombreel.ExplosionDemo.PIXELS_TO_METERS;

public class Coin extends Sprite {
    private static final float SIZE = 0.5f * PIXELS_TO_METERS;
    private final Body coinBody;

    public Coin(final float x, final float y, World world) {

        super(new Texture(Gdx.files.internal("explosion/coin.png")));
        setSize(SIZE, SIZE);
        setPosition(x - SIZE / 2, y - SIZE / 2);
        setOriginCenter();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(getX() + SIZE / 2, getY() + SIZE / 2);

        coinBody = world.createBody(bodyDef);

        FixtureDef fd1 = new FixtureDef();

        CircleShape cTest = new CircleShape();
        cTest.setRadius((SIZE / 2) / PIXELS_TO_METERS);

        fd1.shape = cTest;
        //fd1.isSensor = true;
        fd1.density = 3f;
        fd1.friction = 0.4f;
        fd1.restitution = 0.5f;

        coinBody.createFixture(fd1).setUserData("coin");

    }

    public void update(){
        setRotation(MathUtils.radiansToDegrees * coinBody.getAngle());
        setPosition(
                (coinBody.getPosition().x * PIXELS_TO_METERS) - SIZE/2,
                (coinBody.getPosition().y * PIXELS_TO_METERS) - SIZE/2
        );

    }

    public void render(SpriteBatch batch) {
        batch.draw(this,
                getX(), getY(),
                getOriginX(), getOriginY(),
                getWidth(), getHeight(),
                getScaleX(), getScaleY(),
                getRotation()
        );
    }
}
