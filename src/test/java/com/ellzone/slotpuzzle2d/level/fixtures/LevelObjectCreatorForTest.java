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

package com.ellzone.slotpuzzle2d.level.fixtures;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.World;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorInjectionInterface;
import com.ellzone.slotpuzzle2d.level.creator.LevelObjectCreator;

import box2dLight.RayHandler;

public class LevelObjectCreatorForTest extends LevelObjectCreator {
    private ReflectionMapCreationClassForTesting reflectionMapCreationClassForTesting;
    private ReflectionMapCreationClassForTestingWithDifferentContstuctors reflectionMapCreationClassForTestingWithDifferentContstuctors;
    private boolean delegatedToCallbackCalled = false;
    private boolean addToComponentEntityCalled = false;
    public float testPublicFloatField;


    public LevelObjectCreatorForTest(LevelCreatorInjectionInterface levelCreatorInjectionInterface,
                                     World world,
                                     RayHandler rayHandler) {
        super(levelCreatorInjectionInterface, world, rayHandler);
    }

    public void addTo(ReflectionMapCreationClassForTesting reflectionMapCreationClassForTesting) {
        this.reflectionMapCreationClassForTesting = reflectionMapCreationClassForTesting;
    }

    public void delegateToCallback(ReflectionMapCreationClassForTesting reflectionMapCreationClassForTesting) {
        delegatedToCallbackCalled = true;
    }

    public void addTo(ReflectionMapCreationClassForTestingWithDifferentContstuctors reflectionMapCreationClassForTestingWithDifferentContstuctors) {
        this.reflectionMapCreationClassForTestingWithDifferentContstuctors = reflectionMapCreationClassForTestingWithDifferentContstuctors;
    }

    public void delegateToCallback(ReflectionMapCreationClassForTestingWithDifferentContstuctors reflectionMapCreationClassForTestingWithDifferentContstuctors) {
        delegatedToCallbackCalled = true;
    }

    public void addComponentToEntity(ReflectionMapCreationClassForTestingWithDifferentContstuctors entity, Component component) {
        addToComponentEntityCalled = true;
    }

    public ReflectionMapCreationClassForTesting getReflectionMapCreationClassForTesting() {
        return reflectionMapCreationClassForTesting;
    }

    public ReflectionMapCreationClassForTestingWithDifferentContstuctors getReflectionMapCreationClassForTestingWithDifferentContstuctors() {
        return reflectionMapCreationClassForTestingWithDifferentContstuctors;
    }

    public boolean getDelegatedToCallback() {
        return delegatedToCallbackCalled;
    }

    public boolean getAddedToComponentEntity() { return addToComponentEntityCalled; }

    public float getTestPublicFloatField() { return testPublicFloatField; }
}