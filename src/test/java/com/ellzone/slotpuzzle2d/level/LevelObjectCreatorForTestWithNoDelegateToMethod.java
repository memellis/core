package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.physics.box2d.World;

import box2dLight.RayHandler;


public class LevelObjectCreatorForTestWithNoDelegateToMethod extends LevelObjectCreator {
    private ReflectionMapCreationClassForTesting reflectionMapCreationClassForTesting;
    public LevelObjectCreatorForTestWithNoDelegateToMethod(LevelCreatorInjectionInterface levelCreatorInjectionInterface,
                                     World world,
                                     RayHandler rayHandler) {
        super(levelCreatorInjectionInterface, world, rayHandler);
    }

    public void addTo(ReflectionMapCreationClassForTesting reflectionMapCreationClassForTesting) {
        this.reflectionMapCreationClassForTesting = reflectionMapCreationClassForTesting;
    }
}