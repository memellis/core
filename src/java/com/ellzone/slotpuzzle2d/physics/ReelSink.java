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
import com.badlogic.gdx.physics.box2d.BodyDef;

public class ReelSink {
    private final PhysicsManagerCustomBodies physics;

    public ReelSink(PhysicsManagerCustomBodies physics) {
        this.physics = physics;
    }

    public void createReelSink(float centreX,
                               float centreY,
                               int reelSinkWidth,
                               int reelSinkHeight,
                               int reelWidth,
                               int reelHeight,
                               Object userData) {
        Body reelSinkBottom = createReelSinkBottom(centreX, centreY, reelSinkWidth, reelSinkHeight, reelWidth, reelHeight);
        createReelSinkLeftHandSide(centreX, centreY, reelSinkWidth, reelSinkHeight, reelWidth, reelHeight);
        createReelSinkRightHandSide(centreX, centreY, reelSinkWidth, reelSinkHeight, reelWidth, reelHeight);
        reelSinkBottom.setUserData(userData);
    }

    private Body createReelSinkBottom(float centreX, float centreY, int reelSinkWidth, int reelSinkHeight, int reelWidth, int reelHeight) {
        return physics.createEdgeBody(
                BodyDef.BodyType.StaticBody,
                centreX - reelSinkWidth * reelWidth / 2 - 4,
                centreY - reelSinkHeight * reelHeight / 2 - reelHeight,
                centreX + reelSinkWidth * reelWidth / 2 + 4,
                centreY - reelSinkHeight * reelHeight / 2 - reelHeight);
    }

    private void createReelSinkLeftHandSide(float centreX, float centreY, int reelSinkWidth, int reelSinkHeight, int reelWidth, int reelHeight) {
        physics.createEdgeBody(
                 BodyDef.BodyType.StaticBody,
                centreX - reelSinkWidth * reelWidth / 2 - 4,
                centreY - reelSinkHeight * reelHeight / 2 - reelHeight,
                centreX - reelSinkWidth * reelWidth / 2 - 4,
                centreY + reelSinkHeight * reelHeight / 2 - reelHeight);
    }

    private void createReelSinkRightHandSide(float centreX, float centreY, int reelSinkWidth, int reelSinkHeight, int reelWidth, int reelHeight) {
        physics.createEdgeBody(
                BodyDef.BodyType.StaticBody,
                centreX + reelSinkWidth * reelWidth / 2 + 4,
                centreY - reelSinkHeight * reelHeight / 2 - reelHeight,
                centreX + reelSinkWidth * reelWidth / 2 + 4,
                centreY + reelSinkHeight * reelHeight / 2 - reelHeight);
    }
}
