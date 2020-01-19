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

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;

public class B2ContactListenerReelSink implements ContactListener {
    ReelSinkInterface reelSink;

    public B2ContactListenerReelSink(ReelSinkInterface reelSink) {
        this.reelSink = reelSink;
    }

    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();
        Object bodyAObject = bodyA.getUserData();
        Object bodyBObject = bodyB.getUserData();

        if (bodyAObject == null)
            return;
        if (bodyBObject == null)
            return;

        if (bodyAObject == reelSink && bodyBObject instanceof ReelTile) {
            dealWithReelsHittingSinkBottom((ReelTile) bodyBObject);
        }

        if (bodyBObject == reelSink && bodyAObject instanceof ReelTile) {
            dealWithReelsHittingSinkBottom((ReelTile) bodyAObject);
        }

        if (bodyAObject instanceof ReelTile && bodyBObject instanceof ReelTile) {
            dealWithReelTileHittingReelTile((ReelTile) bodyAObject, (ReelTile) bodyBObject);
        }
    }

    private void dealWithReelTileHittingReelTile(ReelTile reelTileA, ReelTile reelTileB) {
        reelSink.dealWithReelHittingReel(reelTileA, reelTileB);
    }

    private void dealWithReelsHittingSinkBottom(ReelTile reelTile) {
        reelSink.dealWithReelHittingReelSink(reelTile);
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
