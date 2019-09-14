package com.ellzone.slotpuzzle2d.prototypes.artemis;

import com.artemis.ArtemisPlugin;
import com.artemis.FluidEntityPlugin;
import com.artemis.World;
import com.artemis.WorldConfigurationBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.prototypes.artemis.system.ExampleSystem;

import net.mostlyoriginal.plugin.ProfilerPlugin;

public class ArtemisOdbQuickStart extends SPPrototype {

    public static final String BACKGROUND_COLOR_HEX = "969291";
    public static final String WORLD_NOT_INITIALISED = "World not initialised";
    public static final float MIN_DELTA = 1 / 15f;

    private World world;

    public void create() {
        world = createWorld();
    }

    private World createWorld() {
        return new World(new WorldConfigurationBuilder()
            .alwaysDelayComponentRemoval(true)
            .dependsOn(
                    ProfilerPlugin.class,
                    FluidEntityPlugin.class)
            .with(
                    new ExampleSystem()
            )
            .build());
    }

    @Override
    public void render () {
        if (world == null)
            throw new RuntimeException(WORLD_NOT_INITIALISED);

        preventSpikesInDeltaCausingInsaneWorldUpdates(Gdx.graphics.getDeltaTime());
        world.process();
    }

    private void preventSpikesInDeltaCausingInsaneWorldUpdates(float deltaTime) {
        world.setDelta(MathUtils.clamp(deltaTime, 0, MIN_DELTA));
    }
}

