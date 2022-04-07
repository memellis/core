package com.ellzone.slotpuzzle2d.systems.artemisodb;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ellzone.slotpuzzle2d.component.artemis.Position;
import com.ellzone.slotpuzzle2d.component.artemis.Rotation;
import com.ellzone.slotpuzzle2d.component.artemis.TextureRegionRender;

import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;

public class RenderTextureRegionRotationSystem extends EntityProcessingSystem {
    private SpriteBatch batch;
    private LevelCreatorSystem levelCreatorSystem;
    protected M<Position> mPosition;
    protected M<Rotation> mRotate;

    public RenderTextureRegionRotationSystem() {
        super(Aspect.all(Position.class, TextureRegionRender.class, Rotation.class));
    }

    @Override
    protected void initialize() {
        super.initialize();
        batch = new SpriteBatch(2000);
    }

    @Override
    protected void begin() {
        batch.begin();
    }

    @Override
    protected void end() {
        batch.end();
    }

    @Override
    protected void process(Entity e) {
        final Position position = mPosition.get(e);
        final Rotation rotate = mRotate.get(e);

        TextureRegion entityTextureRegion =
                (TextureRegion) levelCreatorSystem.getEntities().get(e.getId());

         batch.draw(
                 entityTextureRegion,
                 position.x,
                 position.y,
                 entityTextureRegion.getRegionX(),
                 entityTextureRegion.getRegionY(),
                 entityTextureRegion.getRegionWidth(),
                 entityTextureRegion.getRegionHeight(),
                 1.0f,
                 1.0f,
                 rotate.angle
         );
    }
}
