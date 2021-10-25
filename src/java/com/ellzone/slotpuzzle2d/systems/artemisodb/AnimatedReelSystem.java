package com.ellzone.slotpuzzle2d.systems.artemisodb;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.ellzone.slotpuzzle2d.component.artemis.AnimatedReelComponent;
import com.ellzone.slotpuzzle2d.component.artemis.Position;
import com.ellzone.slotpuzzle2d.component.artemis.SpinScroll;
import com.ellzone.slotpuzzle2d.operation.artemisodb.TweenSpinScrollOperation;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReelECS;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle2d.utils.Random;

import net.mostlyoriginal.api.operation.common.Operation;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;

import static com.ellzone.slotpuzzle2d.operation.artemisodb.SlotPuzzleOperationFactory.spinScrollBetween;
import static net.mostlyoriginal.api.operation.OperationFactory.*;


public class AnimatedReelSystem extends EntityProcessingSystem {
    private LevelCreatorSystem levelCreatorSystem;
    private OrthographicCamera camera;
    protected M<Position> mPosition;
    protected M<SpinScroll> mSpinScroll;
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
        update(e);
        if (touched)
            processTouched(e);
    }

    private void update(Entity e) {
        AnimatedReelECS animatedReel =
                (AnimatedReelECS) levelCreatorSystem.getEntities().get(e.getId());
        final SpinScroll spinScroll = mSpinScroll.get(e.getId());

        animatedReel.getReel().setSy(spinScroll.sY);
        animatedReel.update(Gdx.graphics.getDeltaTime());
        levelCreatorSystem.getEntities().set(
                animatedReel.getReel().getEntityIds().get(0),
                animatedReel.getReel().getRegion());
    }

    public void touched(Vector3 unProjectTouch) {
        this.unProjectTouch = unProjectTouch;
        touched = true;
    }

    private void processTouched(Entity e) {
        final Position position = mPosition.get(e);
        AnimatedReelECS animatedReel =
                (AnimatedReelECS) levelCreatorSystem.getEntities().get(e.getId());

        ReelTile reelTile = animatedReel.getReel();
        Rectangle rectangle =
                new Rectangle(
                        position.x,
                        position.y,
                        reelTile.getTileWidth(),
                        reelTile.getRegionHeight());
        if (rectangle.contains(unProjectTouch.x, unProjectTouch.y))
            processReelTouched(e, reelTile);
    }

    private void processReelTouched(
            Entity animatedReelEntity,
            final ReelTile reelTile) {
        if (reelTile.isSpinning())
            setEndReelWhenReelFinishedSpinning(reelTile);
        else
            startReelSpinning(animatedReelEntity, reelTile);

    }

    private void setEndReelWhenReelFinishedSpinning(ReelTile reelTile) {
        System.out.println("touched endReel="+reelTile.getCurrentReel());
        reelTile.setEndReel(reelTile.getCurrentReel());
    }


    private void startReelSpinning(
            Entity animatedReelEntity,
            final ReelTile reel) {

        final SpinScroll spinScroll = mSpinScroll.get(animatedReelEntity.getId());
        E.E(animatedReelEntity.getId())
                .script(
                        sequence(
                                spinScrollBetween(
                                        0,
                                        spinScroll.sY,
                                        0,
                                        getNearestStartOfScrollHeight(
                                                spinScroll.sY + MathUtils.random(28000, 32768),
                                                reel.getScrollTextureHeight()),
                                        5.0f,
                                        Interpolation.sineIn),
                                new Operation() {
                                    @Override
                                    public boolean process(float delta, Entity e) {
                                        slowToFinish(e, spinScroll, reel);
                                        return true;
                                    }
                                }));
        reel.setEndReel(
                Random
                   .getInstance()
                   .nextInt(reel.getNumberOfReelsInTexture()));
        reel.startSpinning();
    }

    private void slowToFinish(Entity e, SpinScroll spinScroll, final ReelTile reelTile) {
        E.E(e).script(
                sequence(spinScrollBetween(
                        0,
                        spinScroll.sY,
                        0,
                        getEndSpinScroll(reelTile),
                        1.0f,
                        new Interpolation.ElasticOut(
                                2,
                                10,
                                7,
                                1)
                        ),
                        new Operation() {
                            @Override
                            public boolean process(float delta, Entity e) {
                                reelTile.setSpinning(false);
                                reelTile.setEndReel(
                                        Random
                                                .getInstance()
                                                .nextInt(reelTile.getNumberOfReelsInTexture()));
                                return true;
                            }
                        }));
    }

    private int getNearestStartOfScrollHeight(float sy, int reelScrollHeight) {
        return (int) ((sy / reelScrollHeight)) * reelScrollHeight;
    }

    private float getEndSpinScroll(ReelTile reelTile) {
        return getNearestStartOfScrollHeight(
                reelTile.getSy(), reelTile.getScrollTextureHeight()) +
                (MathUtils.random(2, 4) * reelTile.getScrollTextureHeight()) +
                (reelTile.getEndReel() * reelTile.getTileWidth());
    }
}
