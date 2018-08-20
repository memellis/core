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
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
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
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.sprites.Reels;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class Render3DCylinder3 extends SPPrototype {

    private ModelBatch modelBatch;
    private Environment environment;
    private PerspectiveCamera cam;
    private CameraInputController camController;
    private Array<ModelInstance> instances = new Array<ModelInstance>();
    private AnnotationAssetManager annotationAssetManager;

    @Override
    public void create() {

         annotationAssetManager =  loadAssets();
        Reels reels = new Reels(annotationAssetManager);

        final Texture reelTexture = initialiseReelTexture(reels);
        instances.add(createCylinderWithDifferentColors());

         modelBatch = new ModelBatch();
        createEnvironment();

        createPerspectiveCamera();

        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);

    }

    private ModelInstance createCylinderWithDifferentColors() {
        int primitiveType = GL20.GL_TRIANGLES;
        final Material materialRed = new Material(ColorAttribute.createDiffuse(Color.RED));
        final Material materialBlue = new Material(ColorAttribute.createDiffuse(Color.BLUE));
        final long attributes = VertexAttributes.Usage.Position |
                                VertexAttributes.Usage.Normal;

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder meshPartBuilder = modelBuilder.part("box",
                primitiveType,
                MeshBuilder.createAttributes(attributes),
                materialRed);
        BoxShapeBuilder.build(meshPartBuilder, 1.0f, 1.0f, 1.0f);
        meshPartBuilder = modelBuilder.part("cylinder",
                          primitiveType,
                          MeshBuilder.createAttributes(attributes),
                          materialBlue);
        CylinderShapeBuilder.build(meshPartBuilder, 1.0f, 1.0f, 2.0f, 16);
        ModelInstance modelInstance = new ModelInstance(modelBuilder.end());
        return modelInstance;
    }

    private void createEnvironment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
    }

    private void createPerspectiveCamera() {
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(-0.23f, -0.23f, -10.0f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
    }

    private Texture initialiseReelTexture(Reels reels) {
        Pixmap slotReelScrollPixmap = PixmapProcessors.createHorizontalPixmapToAnimate(reels.getReels());
        return new Texture(slotReelScrollPixmap);
    }

    private AnnotationAssetManager loadAssets()  {
        AnnotationAssetManager annotationAssetManager = new AnnotationAssetManager();
        annotationAssetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        annotationAssetManager.load(new AssetsAnnotation());
        annotationAssetManager.finishLoading();
        return annotationAssetManager;
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
