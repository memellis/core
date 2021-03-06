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

package com.ellzone.slotpuzzle2d.physics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class BoxBodyBuilder {

    public static float WORLD_TO_BOX=0.01f;
    public static float BOX_TO_WORLD=100f;

    static float convertToBox(float x){
        return x*WORLD_TO_BOX;
    }

    static float convertToWorld(float x){
        return x*BOX_TO_WORLD;
    }

    public Body createCircleBody(World world, BodyDef.BodyType bodyType, float posx, float posy,  float radius){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(convertToBox(posx), convertToBox(posy));
        bodyDef.angle = 0;

        Body body = world.createBody(bodyDef);
        makeCircleBody(body, radius, bodyType,1,0,0,1);
        return body;
    }

    void makeCircleBody(Body body, float radius, BodyDef.BodyType bodyType,
                        float density,float restitution,float angle,float friction){

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.restitution = restitution;
        fixtureDef.friction = friction;
        fixtureDef.shape = new CircleShape();
        fixtureDef.shape.setRadius(convertToBox(radius));

        body.createFixture(fixtureDef);
        fixtureDef.shape.dispose();
    }

    public Body createEdgeBody(World world,BodyDef.BodyType bodyType,
                               float v1x, // X1 WORLD COORDINATE
                               float v1y, // Y1 WORLD COORDINATE
                               float v2x, // X2 WORLD COORDINATE
                               float v2y  // Y2 WORLD COORDINATE
    ){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;

        //CALCULATE CENTER OF LINE SEGMENT
        float posx=(v1x+v2x)/2f;
        float posy=(v1y+v2y)/2f;

        //CALCULATE LENGTH OF LINE SEGMENT
        float len=(float) Math.sqrt((v1x-v2x)*(v1x-v2x)+(v1y-v2y)*(v1y-v2y));

        //CONVERT CENTER TO BOX COORDINATES
        float bx = convertToBox(posx);
        float by = convertToBox(posy);
        bodyDef.position.set(bx, by);
        bodyDef.angle = 0;

        Body body = world.createBody(bodyDef);

        //ADD EDGE FIXTURE TO BODY
        makeEdgeShape(body, len, bodyType,1,0,1);

        //CALCULATE ANGLE OF THE LINE SEGMENT
        body.setTransform(bx, by, MathUtils.atan2(v2y-v1y, v2x-v1x));

        return body;
    }

    void makeEdgeShape(Body body, float len, BodyDef.BodyType bodyType,
                       float density, float restitution, float friction){
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.restitution = restitution;
        fixtureDef.friction = friction;

        EdgeShape es = new EdgeShape();
        float boxLen = convertToBox(len);
        es.set(-boxLen/2f,0,boxLen/2f,0);
        fixtureDef.shape = es;

        body.createFixture(fixtureDef);
        fixtureDef.shape.dispose();
    }

    Body createBoxBody(World world, BodyDef.BodyType bodyType, float posx, float posy, float width, float height, boolean fixedRotation) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(
                convertToBox(posx) + convertToBox(width),
                convertToBox(posy) + convertToBox(height));
        bodyDef.fixedRotation = fixedRotation;
        Body body = world.createBody(bodyDef);

        makeBoxBody(body, bodyType, convertToBox(width), convertToBox(height), 1, 0, 0, 1);
        return body;
    }

    void makeBoxBody(Body body, BodyDef.BodyType bodyType, float width, float height,
                     float density, float restitution, float angle, float friction) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = density;
        fixtureDef.restitution = restitution;
        fixtureDef.friction = friction;

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox(width, height);
        fixtureDef.shape = polygonShape;
        body.createFixture(fixtureDef);
        fixtureDef.shape.dispose();
    }
}
