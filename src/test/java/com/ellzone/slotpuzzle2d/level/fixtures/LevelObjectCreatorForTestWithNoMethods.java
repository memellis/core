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

import com.badlogic.gdx.physics.box2d.World;
import com.ellzone.slotpuzzle2d.level.LevelCreatorInjectionInterface;
import com.ellzone.slotpuzzle2d.level.LevelObjectCreator;

import box2dLight.RayHandler;

public class LevelObjectCreatorForTestWithNoMethods extends LevelObjectCreator {
    private ReflectionMapCreationClassForTesting reflectionMapCreationClassForTesting;
    public LevelObjectCreatorForTestWithNoMethods(LevelCreatorInjectionInterface levelCreatorInjectionInterface,
                                                  World world,
                                                  RayHandler rayHandler) {
        super(levelCreatorInjectionInterface, world, rayHandler);
    }
}