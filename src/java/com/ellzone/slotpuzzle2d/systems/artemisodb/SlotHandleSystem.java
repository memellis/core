package com.ellzone.slotpuzzle2d.systems.artemisodb;

import com.artemis.E;
import com.artemis.EBag;
import com.artemis.Entity;
import com.artemis.annotations.All;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ellzone.slotpuzzle2d.component.artemis.Rotation;
import com.ellzone.slotpuzzle2d.component.artemis.SlotHandle;
import com.ellzone.slotpuzzle2d.sprites.slothandle.SlotHandleSprite;

import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;

import java.util.Iterator;

import static net.mostlyoriginal.api.operation.OperationFactory.sequence;
import static com.ellzone.slotpuzzle2d.operation.artemisodb.SlotPuzzleOperationFactory.rotateBetween;

@All({SlotHandle.class})
public class SlotHandleSystem extends EntityProcessingSystem {
    private LevelCreatorSystem levelCreatorSystem;
    private OrthographicCamera camera;
    private Vector3 unProjectTouch;
    private boolean touched;
    protected M<Rotation> mRotation;
    private AnimatedReelSystem animatedReelSystem;

    public SlotHandleSystem() { setUp(); }

    private void setUp() {
        setUpCamera();
    }

    private void setUpCamera() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(
                false,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());
    }

    public void touched(Vector3 unProjectTouch) {
        this.unProjectTouch = unProjectTouch;
        camera.unproject(unProjectTouch);
        touched = true;
    }


    @Override
    protected void process(Entity e) {
        SlotHandleSprite slotHandle = (SlotHandleSprite) levelCreatorSystem.getEntities().get(e.getId());
        if (touched)
            processTouched(e, slotHandle);
    }

    private void processTouched(Entity e, SlotHandleSprite slotHandle) {
        if (slotHandle.getBoundingRectangle().contains(
                new Vector2(unProjectTouch.x , unProjectTouch.y))) {
            removeMatchedRowsDisplayed();
            slotHandle.pullSlotHandle();
            rotateSlotHandle(slotHandle);
            rotateReelsNotHeld();
        }
        touched = false;
    }

    private void rotateSlotHandle(SlotHandleSprite slotHandle) {
        int slotHandleEntityId = slotHandle.getEntityIds().get(1);
        Rotation rotation = E.E(slotHandleEntityId)
                .getRotation();
        E.E(slotHandleEntityId)
                .script(
                        sequence(
                                rotateBetween(
                                        rotation.angle,
                                    rotation.angle - 45,
                                0.5f,
                                        Interpolation.circle),
                                rotateBetween(
                                    rotation.angle - 45,
                                        rotation.angle,
                                0.5f,
                                        Interpolation.circle)));
    }

    private void rotateReelsNotHeld() {
        animatedReelSystem.actionSlotHandlePulled();
    }

    private void removeMatchedRowsDisplayed() {
        EBag reelGridMatchedRows = E.withGroup("ReelGridMatchedRow");
        for (Iterator<E> it = reelGridMatchedRows.iterator(); it.hasNext(); ) {
            E reelGridMatchedRow = it.next();
            reelGridMatchedRow.deleteFromWorld();
        }
    }
}
