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