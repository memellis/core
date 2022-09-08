/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.systems.artemisodb;

import com.artemis.BaseSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Setup and manages basic orthographic camera.
 *
 * @author Daan van Yperen
 */
public class CameraSystem extends BaseSystem {

    public OrthographicCamera camera;
    public OrthographicCamera guiCamera;
    public final float zoom;

    public CameraSystem(float width, float height) {
        this.zoom = 1;
        setupViewport(width, height);
    }

    /**
     * @param zoom How much
     */
    public CameraSystem(float zoom) {
        this.zoom = zoom;
        float zoomFactorInverter = 1f / zoom;
        setupViewport(
                Gdx.graphics.getWidth() * zoomFactorInverter,
                Gdx.graphics.getHeight() * zoomFactorInverter);
    }

    protected void setupViewport(float width, float height) {
        camera = new OrthographicCamera(width, height);
        camera.setToOrtho(false, width, height);
        camera.update();

        guiCamera = new OrthographicCamera(width, height);
        guiCamera.setToOrtho(false, width, height);
        guiCamera.update();
    }

    @Override
    protected void processSystem() {

    }
}