

package com.ellzone.slotpuzzle2d.systems.artemisodb;

import com.artemis.E;
import com.artemis.Entity;
import com.artemis.annotations.All;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ellzone.slotpuzzle2d.component.artemis.HoldLightButtonComponent;
import com.ellzone.slotpuzzle2d.sprites.HoldLightButton;

import box2dLight.RayHandler;

import static com.ellzone.slotpuzzle2d.SlotPuzzleConstants.PIXELS_PER_METER;

@All(HoldLightButtonComponent.class)
public class HoldLightButtonSystem extends EntityProcessingSystem {

    private final RayHandler rayHandler;
    private FitViewport lightViewport;
    private LevelCreatorSystem levelCreatorSystem;
    private float flashTime;
    private OrthographicCamera camera;

    public HoldLightButtonSystem(RayHandler rayHandler) {
        this.rayHandler = rayHandler;
        setUp();
    }

    private void setUp() {
        setupViewport(
                Gdx.graphics.getWidth() / PIXELS_PER_METER,
                Gdx.graphics.getHeight() / PIXELS_PER_METER
        );
        rayHandler.setCombinedMatrix(camera);
        rayHandler.setAmbientLight(0.7f);
    }

    private void setupViewport(float width, float height) {
        camera = new OrthographicCamera(width, height);
        camera.setToOrtho(false, width, height);
        camera.update();
    }


    @Override
    protected void begin() {
        super.begin();
        rayHandler.updateAndRender();
    }

    @Override
    protected void process(Entity e) {
        HoldLightButton holdLightButton =
                (HoldLightButton) levelCreatorSystem.getEntities().get(e.getId());
        flashTime += Gdx.graphics.getDeltaTime();
        if (flashTime > 1.0f) {
           holdLightButton.getLight().setActive(!holdLightButton.getLight().isActive());
           System.out.println("flash: "+ holdLightButton.getLight().isActive());
           System.out.println(holdLightButton.getLight().getPosition());
           flashTime = 0.0f;
        }
    }
}
