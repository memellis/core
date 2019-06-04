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
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.BoxShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CapsuleShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ConeShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;

public class Render3DCylinder5 extends SPPrototype {
    private Environment environment;
    private Array<ModelInstance> instances = new Array<ModelInstance>();
    private ModelBatch modelBatch;
    private PerspectiveCamera cam;
    private CameraInputController camController;


    @Override
    public void create() {
        setUpEnvironment();
        cam = (PerspectiveCamera) createPerspectiveCamera(cam);
        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);
        setUpModel();
    }

    private void setUpEnvironment() {
        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, -1.0f, -0.8f));
    }

    private Camera createPerspectiveCamera(Camera cam) {
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.position.set(-0.23f, -0.23f, -10.0f);
        cam.lookAt(0, 0, 0);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
        return cam;
    }

    private void setUpModel() {
        Model model = new Model();
        Texture texture = new Texture(Gdx.files.internal("badlogic.jpg"));

        Material material = new Material(TextureAttribute.createDiffuse(texture));
        Material solidMaterial = new Material();

        Mesh mesh = createMesh();
        ModelBuilder modelBuilder = new ModelBuilder();

        modelBuilder.begin();
        modelBuilder.manage(texture);

        addBoxNode(material, modelBuilder);
        addSphereNode(material, modelBuilder);
        addCodeNode(material, modelBuilder);
        addCylinderNode(material, modelBuilder);
        addCapsuleNode(material, modelBuilder);
        addCapsuleNoTextureNode(solidMaterial, modelBuilder);
        addMeshNode(material, mesh, modelBuilder);

        model = modelBuilder.end();
        addInstances(model);
        modelBatch = new ModelBatch();
    }

    private void addBoxNode(Material material, ModelBuilder modelBuilder) {
        modelBuilder.node().id = "box";
        MeshPartBuilder mpb = modelBuilder.part("box",
                                                 GL20.GL_TRIANGLES,
                                                Usage.Position |
                                                          Usage.Normal |
                                                          Usage.TextureCoordinates|
                                                          Usage.ColorPacked,
                                                 material);

      mpb.setColor(Color.RED);
      BoxShapeBuilder.build(mpb,1f, 1f, 1f);
    }

    private void addSphereNode(Material material, ModelBuilder modelBuilder) {
        MeshPartBuilder mpb;
        modelBuilder.node().id = "sphere";
        mpb = modelBuilder.part("sphere",
                                GL20.GL_TRIANGLES,
                               Usage.Position |
                                         Usage.Normal |
                                         Usage.TextureCoordinates |
                                         Usage.ColorPacked,
                                material);
        SphereShapeBuilder.build(mpb, 2f, 2f, 2f, 10, 5);
    }

    private void addCodeNode(Material material, ModelBuilder modelBuilder) {
        MeshPartBuilder mpb;
        modelBuilder.node().id = "cone";
        mpb = modelBuilder.part("cone",
                                 GL20.GL_TRIANGLES,
                                Usage.Position |
                                          Usage.Normal |
                                          Usage.TextureCoordinates |
                                          Usage.ColorPacked,
                                 material);
        mpb.setVertexTransform(new Matrix4().rotate(Vector3.X, -45f));
        ConeShapeBuilder.build(mpb, 2f, 3f, 1f, 8);
    }

    private void addCylinderNode(Material material, ModelBuilder modelBuilder) {
        MeshPartBuilder mpb;
        modelBuilder.node().id = "cylinder";
        mpb = modelBuilder.part("cylinder",
                                 GL20.GL_TRIANGLES,
                                Usage.Position |
                                          Usage.Normal |
                                          Usage.TextureCoordinates |
                                          Usage.ColorPacked,
                                 material);
        mpb.setUVRange(1f, 1f, 0f, 0f);
        CylinderShapeBuilder.build(mpb, 2f, 4f, 3f, 15);
    }

    private void addCapsuleNode(Material material, ModelBuilder modelBuilder) {
        MeshPartBuilder mpb;
        modelBuilder.node().id = "capsule";
        mpb = modelBuilder.part("capsule",
                                 GL20.GL_TRIANGLES,
                                 Usage.Position |
                                           Usage.Normal |
                                           Usage.TextureCoordinates |
                                           Usage.ColorPacked,
                                  material);
        mpb.setUVRange(1f, 1f, 0f, 0f);
        CapsuleShapeBuilder.build(mpb, 1.5f, 5f, 15);
    }

    private void addCapsuleNoTextureNode(Material solidMaterial, ModelBuilder modelBuilder) {
        MeshPartBuilder mpb;
        modelBuilder.node().id = "capsuleNoTexture";
        mpb = modelBuilder.part("capsuleNoTexture",
                                     GL20.GL_TRIANGLES,
                                    Usage.Position |
                                              Usage.Normal |
                                              Usage.ColorPacked,
                                              solidMaterial);
        mpb.setUVRange(1f, 1f, 0f, 0f);
        CapsuleShapeBuilder.build(mpb, 1.5f, 5f, 15);

        modelBuilder.node().id = "transformedSphere";
        mpb = modelBuilder.part("transformedSphere",
                                 GL20.GL_TRIANGLES,
                                Usage.Position |
                                          Usage.Normal |
                                          Usage.ColorPacked,
                                 solidMaterial);
        mpb.setUVRange(1f, 1f, 0f, 0f);
        SphereShapeBuilder.build(mpb, new Matrix4().translate(5, 0, 10).rotate(Vector3.Z, 45).scale(1, 2, 1), 1f, 1f, 1f, 12, 16);
    }

    private void addMeshNode(Material material, Mesh mesh, ModelBuilder modelBuilder) {
        MeshPartBuilder mpb;
        modelBuilder.node().id = "mesh";
        mpb = modelBuilder.part("mesh",
                                 GL20.GL_TRIANGLES,
                                 mesh.getVertexAttributes(),
                                 material);

        Matrix4 transform = new Matrix4();
        mpb.setVertexTransform(transform.setToTranslation(0, 2, 0));
        mpb.addMesh(mesh);
        mpb.setColor(Color.BLUE);
        mpb.setVertexTransform(transform.setToTranslation(1, 1, 0));
        mpb.addMesh(mesh);
        mpb.setColor(null);
        mpb.setVertexTransform(transform.setToTranslation(-1, 1, 0).rotate(Vector3.X, 45));
        mpb.addMesh(mesh);
        mpb.setVertexTransform(transform.setToTranslation(0, 1, 1));
        mpb.setUVRange(0.75f, 0.75f, 0.25f, 0.25f);
        mpb.addMesh(mesh);
    }

    private void addInstances(Model model) {
        instances.add(new ModelInstance(model, new Matrix4().trn(0f, 0f, 0f), "mesh", true));
        instances.add(new ModelInstance(model, new Matrix4().trn(-5f, 0f, -5f), "box", true));
        instances.add(new ModelInstance(model, new Matrix4().trn(5f, 0f, -5f), "sphere", true));
        instances.add(new ModelInstance(model, new Matrix4().trn(-5f, 0f, 5f), "cone", true));
        instances.add(new ModelInstance(model, new Matrix4().trn(5f, 0f, 5f), "cylinder", true));
        instances.add(new ModelInstance(model, new Matrix4().trn(0f, 0f, 5f), "capsule", true));
        instances.add(new ModelInstance(model, new Matrix4().trn(0f, 0f, 10f), "capsuleNoTexture", true));
        instances.add(new ModelInstance(model, new Matrix4().trn(0f, 0f, 0f), "transformedSphere", true));
    }

    private Mesh createMesh() {
        MeshBuilder meshBuilder = new MeshBuilder();
        meshBuilder.begin(Usage.Position | Usage.Normal | Usage.ColorPacked | Usage.TextureCoordinates, GL20.GL_TRIANGLES);
        meshBuilder.box(1f, 1f, 1f);
        Mesh mesh = new Mesh(true, meshBuilder.getNumVertices(), meshBuilder.getNumIndices(), meshBuilder.getAttributes());
        mesh = meshBuilder.end(mesh);
        return mesh;
    }

    @Override
    public void render() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();

    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        instances.clear();
    }
}