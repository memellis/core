package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.physics.box2d.World;
import box2dLight.RayHandler;

public class LevelObjectCreatorForTestWithNoMethods extends LevelObjectCreator {
    private ReflectionMapCreationClassForTesting reflectionMapCreationClassForTesting;
    public LevelObjectCreatorForTestWithNoMethods(LevelCreatorInjectionInterface levelCreatorInjectionInterface,
                                     World world,
                                     RayHandler rayHandler) {
        super(levelCreatorInjectionInterface, world, rayHandler);
    }
}