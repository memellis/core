package com.ellzone.slotpuzzle2d.systems.artemisodb;

import com.artemis.Entity;
import com.artemis.annotations.All;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ellzone.slotpuzzle2d.component.artemis.SlotHandle;
import com.ellzone.slotpuzzle2d.sprites.slothandle.SlotHandleSprite;

@All({SlotHandle.class})
public class SlotHandleSystem extends EntityProcessingSystem {
    private LevelCreatorSystem levelCreatorSystem;
    private OrthographicCamera camera;
    private Vector3 unProjectTouch;
    private boolean touched;

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
            processTouched(slotHandle);
    }


    private void processTouched(SlotHandleSprite slotHandle) {
        if (slotHandle.getBoundingRectangle().contains(
                new Vector2(unProjectTouch.x , unProjectTouch.y))) {
            slotHandle.pullSlotHandle();
            System.out.println("pull SlotHandle");
        }
        touched = false;
    }
}
