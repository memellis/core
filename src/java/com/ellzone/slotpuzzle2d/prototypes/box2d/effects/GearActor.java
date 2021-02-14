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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class GearActor extends Image {

    private Body body;
    private World world;
    private float angle;

    public GearActor(World aWorld, float pos_x, float pos_y, float aWidth, float aHeight) {
        super(new Texture("box2d_particle_effects/gear.png"));
        this.setSize(aWidth,aHeight);
        this.setPosition(pos_x, pos_y);

        world = aWorld;
        BodyEditorLoader loader = new BodyEditorLoader(Gdx.files.internal("box2d_particle_effects/box2d_scene.json"));

        BodyDef bd = new BodyDef();
        bd.position.set(Gdx.graphics.getWidth()/2,Gdx.graphics.getHeight()/2);
        bd.type = BodyDef.BodyType.KinematicBody;
        bd.position.x = this.getX();
        bd.position.y = this.getY();
        body = world.createBody(bd);

        FixtureDef fd = new FixtureDef();
        fd.density = 1;
        fd.friction = 0.5f;
        fd.restitution = 0.3f;

        float scale = this.getWidth();
        loader.attachFixture(body, "gear", fd, scale);

        this.setOrigin(this.getWidth()/2,this.getHeight()/2);
        body.setAngularVelocity(1);
        body.setUserData(this);

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        this.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        this.setPosition(body.getPosition().x-this.getWidth()/2 , body.getPosition().y -this.getHeight()/2);
    }
}