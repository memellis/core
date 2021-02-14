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

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class B2dContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        String classA = contact.getFixtureA().getBody().getUserData().getClass().getName();
        String classB = contact.getFixtureB().getBody().getUserData().getClass().getName();

        //Gdx.app.debug("begin Contact","between: "+classA+" and "+ classB);
        if(classA.equalsIgnoreCase("com.ellzone.slotpuzzle2d.prototypes.box2d.effects.WindowsFrame") &&
           classB.equalsIgnoreCase("com.ellzone.slotpuzzle2d.prototypes.box2d.effects.Ball")){
            Ball ball = (Ball)(contact.getFixtureB().getBody().getUserData());
            BallGenerator.getInstance().explode(ball);

        }
        else if(classB.equalsIgnoreCase("com.ellzone.slotpuzzle2d.prototypes.box2d.effects.WindowsFrame") &&
                classA.equalsIgnoreCase("com.ellzone.slotpuzzle2d.prototypes.box2d.effects.Ball")){
            Ball ball = (Ball)(contact.getFixtureA().getBody().getUserData());
            BallGenerator.getInstance().explode(ball);
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}