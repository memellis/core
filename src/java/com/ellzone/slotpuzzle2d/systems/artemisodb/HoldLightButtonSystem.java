

package com.ellzone.slotpuzzle2d.systems.artemisodb;

import com.artemis.Entity;
import com.artemis.annotations.All;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.ellzone.slotpuzzle2d.component.artemis.HoldLightButtonComponent;
import com.ellzone.slotpuzzle2d.sprites.lights.HoldLightButton;

import box2dLight.RayHandler;

import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.PIXELS_PER_METER;

@All(HoldLightButtonComponent.class)
public class HoldLightButtonSystem extends EntityProcessingSystem {

    private final RayHandler rayHandler;
    private LevelCreatorSystem levelCreatorSystem;
    private OrthographicCamera ppmCamera, camera;
    private Vector3 unProjectTouch;
    private boolean touched;

    public HoldLightButtonSystem(RayHandler rayHandler) {
        this.rayHandler = rayHandler;
        setUp();
    }

    private void setUp() {
        ppmCamera =  setupViewport(
                (Gdx.graphics.getWidth() / (float) PIXELS_PER_METER),
                (Gdx.graphics.getHeight() / (float) PIXELS_PER_METER),
                false
        );
        camera = setupViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        rayHandler.setCombinedMatrix(ppmCamera);
        rayHandler.setAmbientLight(0.9f);
    }

    private OrthographicCamera setupViewport(float width, float height, boolean yDown) {
        OrthographicCamera camera = new OrthographicCamera(width, height);
        camera.setToOrtho(yDown, width, height);
        camera.update();
        return camera;
    }

    @Override
    protected void begin() {
        super.begin();
        rayHandler.setCombinedMatrix(ppmCamera);
        rayHandler.updateAndRender();
    }

    @Override
    protected void end() {
        touched = false;
    }

    @Override
    protected void process(Entity e) {
        HoldLightButton holdLightButton =
                (HoldLightButton) levelCreatorSystem.getEntities().get(e.getId());
        if (touched)
            processTouched(holdLightButton);
    }

    public void touched(Vector3 unProjectTouch) {
        this.unProjectTouch = unProjectTouch;
        camera.unproject(unProjectTouch);
        touched = true;
    }

    private void processTouched(HoldLightButton holdLightButton) {
        Rectangle rectangle = new Rectangle(
                holdLightButton.getSprite().getX(),
                holdLightButton.getSprite().getY(),
                holdLightButton.getSprite().getWidth(),
                holdLightButton.getSprite().getHeight()
        );
        if (rectangle.contains(unProjectTouch.x, unProjectTouch.y))
            processHoldLightButtonTouched(holdLightButton);
    }

    private void processHoldLightButtonTouched(HoldLightButton holdLightButton) {
         holdLightButton.getLight().setActive(!holdLightButton.getLight().isActive());
    }
}
