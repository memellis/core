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

package com.ellzone.slotpuzzle2d.prototypes.cylinder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

public class Render3DCylinder1 extends SPPrototype {

    private ModelBatch modelBatch;
    private Environment environment;
    private PerspectiveCamera cam;
    private CameraInputController camController;
    public Array<ModelInstance> instances = new Array<ModelInstance>();

    @Override
    public void create() {
        final Texture badlogicTexture = new Texture(Gdx.files.internal("badlogic.jpg"));
        Texture rotatedBadlogicTexture = rotateTexture(badlogicTexture);

        final Material material = new Material(TextureAttribute.createDiffuse(rotatedBadlogicTexture),
                                      ColorAttribute.createSpecular(1, 1, 1, 1),
                                      FloatAttribute.createShininess(8f));

        final long attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;

        ModelBuilder modelBuilder = new ModelBuilder();
        Model cylinder = modelBuilder.createCylinder(4f, 4f, 4f, 32, material, attributes);
        ModelInstance modelInstance = new ModelInstance(cylinder);
        modelInstance.transform.rotate(0,0,1, 90);
        instances.add(modelInstance);

        modelBatch = new ModelBatch();
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(-0.23f, -0.23f, -10.0f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);

    }

    private Texture rotateTexture(Texture textureToRotate) {
        if (!textureToRotate.getTextureData().isPrepared()) {
            textureToRotate.getTextureData().prepare();
        }
        Pixmap pixmapToRotate = textureToRotate.getTextureData().consumePixmap();
        pixmapToRotate = PixmapProcessors.rotatePixmap(pixmapToRotate, 180);
        return new Texture(pixmapToRotate);
    }

    public void resume () {
    }

    private void update() {
        camController.update();
        for (ModelInstance modelInstance : instances) {
            modelInstance.transform.rotate(0, 1, 0, 1);
        }
    }

    @Override
    public void render () {
        update();

        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();
    }

    public void resize (int width, int height) {
    }

    public void pause () {
    }

    @Override
    public void dispose () {
        modelBatch.dispose();
        instances.clear();

    }
}
