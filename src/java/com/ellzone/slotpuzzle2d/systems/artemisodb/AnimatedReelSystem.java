package com.ellzone.slotpuzzle2d.systems.artemisodb;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.ellzone.slotpuzzle2d.component.artemis.AnimatedReelComponent;
import com.ellzone.slotpuzzle2d.component.artemis.Position;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle2d.utils.Random;

import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;

public class AnimatedReelSystem extends EntityProcessingSystem {
    private LevelCreatorSystem levelCreatorSystem;
    private OrthographicCamera camera;
    protected M<Position> mPosition;
    private boolean touched = false;
    private Vector3 unProjectTouch;


    public AnimatedReelSystem() {
        super(Aspect.all(Position.class, AnimatedReelComponent.class));
        setup();
    }

    private void setup() {
        setupCamera();
    }

    private void setupCamera() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(
                false,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());
    }


    @Override
    protected void end() {
        touched = false;
    }

    @Override
    protected void process(Entity e) {
        if(touched)
            processTouched(e);
    }

    public void touched(Vector3 unProjectTouch) {
        this.unProjectTouch = unProjectTouch;
        System.out.println("Touched="+unProjectTouch);
        camera.unproject(unProjectTouch);
        System.out.println("UnProjectTouched="+unProjectTouch);
        touched = true;
    }

    private void processTouched(Entity e) {
        final Position position = mPosition.get(e);
        AnimatedReel animatedReel =
                (AnimatedReel) levelCreatorSystem.getEntities().get(e.getId());
        ReelTile reelTile = animatedReel.getReel();
        Rectangle rectangle =
                new Rectangle(
                        position.x,
                        position.y,
                        reelTile.getTileWidth(),
                        reelTile.getRegionHeight());
        if (rectangle.contains(unProjectTouch.x, unProjectTouch.y)) {
            System.out.println("An animated reel has been touched" + e.getId());
            startReelSpinning(reelTile, animatedReel);
        }
    }

    private void startReelSpinning(ReelTile reel, AnimatedReel animatedReel) {
        reel.setEndReel(Random.getInstance().nextInt(7));
        reel.startSpinning();
        reel.setSy(0);
        animatedReel.reinitialise();
    }
}
