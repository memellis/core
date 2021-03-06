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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.CylinderShapeBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelLetterTile;
import com.ellzone.slotpuzzle2d.utils.FileUtils;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import org.jrenner.smartfont.SmartFontGenerator;
import java.io.IOException;

import static com.ellzone.slotpuzzle2d.prototypes.screens.IntroScreenPrototype.FONT_LARGE;
import static com.ellzone.slotpuzzle2d.prototypes.screens.IntroScreenPrototype.FONT_MEDIUM;
import static com.ellzone.slotpuzzle2d.prototypes.screens.IntroScreenPrototype.FONT_SMALL;
import static com.ellzone.slotpuzzle2d.prototypes.screens.IntroScreenPrototype.GENERATED_FONTS_DIR;
import static com.ellzone.slotpuzzle2d.prototypes.screens.IntroScreenPrototype.LIBERATION_MONO_REGULAR_FONT_NAME;
import static com.ellzone.slotpuzzle2d.prototypes.tween.ReelLetterTilePlay.REEL_HEIGHT;
import static com.ellzone.slotpuzzle2d.prototypes.tween.ReelLetterTilePlay.REEL_WIDTH;

public class Render3DCylinder6 extends SPPrototype {
    public static final float REEL_3D_WIDTH = 0.4f;
    public static final float REEL_3D_SIDE_WITH = 0.01f;
    private ModelBatch modelBatch;
    private Environment environment;
    private PerspectiveCamera cam;
    private CameraInputController camController;
    private Array<ModelInstance> instances = new Array<ModelInstance>();
    private AnnotationAssetManager annotationAssetManager;
    private BitmapFont fontSmall;
    private BitmapFont fontMedium;
    private BitmapFont fontLarge;
    private Array<ReelLetterTile> reelLetterTiles;

    @Override
    public void create() {
        initialiseFonts();
        final Texture reelTexture = initialiseFontTexture("Hello World! ");
        instances = createReelModelInstance(reelTexture, instances);
        modelBatch = new ModelBatch();
        createEnvironment();
        createPerspectiveCamera();
        camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);
    }

    private Array<ModelInstance> createReelModelInstance(Texture reelTexture, Array<ModelInstance> instances) {
        final Material reelMaterial = new Material(TextureAttribute.createDiffuse(reelTexture),
                                                   ColorAttribute.createSpecular(1, 1, 1, 1),
                                                   FloatAttribute.createShininess(8f));
        final Material blackMaterial = new Material(ColorAttribute.createDiffuse(Color.BLACK));
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        addNodeCylinder(reelMaterial, modelBuilder);
        addNodeCylinderSide(blackMaterial, modelBuilder, "leftSideOfCylinder");
        addNodeCylinderSide(blackMaterial, modelBuilder, "rightSideOfCylinder");
        Model model = modelBuilder.end();
        instances.add(new ModelInstance(model, new Matrix4().trn(0f, 0f, 0f), "cylinder", true));
        instances.get(0).transform.rotate(0,0,1, 90);
        instances.get(0).transform.rotate(1,0,0, 180);
        instances.add(new ModelInstance(model, new Matrix4().trn(0f, 0f, 0f), "leftSideOfCylinder", true));
        instances.get(1).transform.rotate(0,0,1, 90);
        instances.get(1).transform.rotate(1,0,0, 180);
        instances.get(1).transform.translate(0, (REEL_3D_WIDTH + REEL_3D_SIDE_WITH) / 2, 0);
        instances.add(new ModelInstance(model, new Matrix4().trn(0f, 0f, 0f), "rightSideOfCylinder", true));
        instances.get(2).transform.rotate(0,0,1, 90);
        instances.get(2).transform.rotate(1,0,0, 180);
        instances.get(2).transform.translate(0, -(REEL_3D_WIDTH + REEL_3D_SIDE_WITH)/ 2, 0);
        return instances;
    }

    private void addNodeCylinder(Material material, ModelBuilder modelBuilder) {
        MeshPartBuilder mpb;
        modelBuilder.node().id = "cylinder";
        mpb = modelBuilder.part("cylinder",
                                 GL20.GL_TRIANGLES,
                                VertexAttributes.Usage.Position |
                                 VertexAttributes.Usage.Normal |
                                 VertexAttributes.Usage.TextureCoordinates |
                                 VertexAttributes.Usage.ColorPacked,
                                 material);
        mpb.setUVRange(1f, 1f, 0f, 0f);
        CylinderShapeBuilder.build(mpb, 2f, REEL_3D_WIDTH, 2f, 15, 0, 360, false);
    }

    private void addNodeCylinderSide(Material material, ModelBuilder modelBuilder, String id) {
        MeshPartBuilder mpb;
        modelBuilder.node().id = id;
        mpb = modelBuilder.part(id,
                GL20.GL_TRIANGLES,
               VertexAttributes.Usage.Position |
                         VertexAttributes.Usage.Normal |
                         VertexAttributes.Usage.TextureCoordinates |
                         VertexAttributes.Usage.ColorPacked,
                material);
        mpb.setUVRange(1f, 1f, 0f, 0f);
        CylinderShapeBuilder.build(mpb, 2.0f, REEL_3D_SIDE_WITH, 2.0f, 15, 0, 360, true);
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

    private void initialiseFonts() {
        SmartFontGenerator fontGen = new SmartFontGenerator();
        FileHandle exoFileInternal = Gdx.files.internal(LIBERATION_MONO_REGULAR_FONT_NAME);
        FileHandle generatedFontDir = Gdx.files.local(GENERATED_FONTS_DIR);
        generatedFontDir.mkdirs();

        FileHandle exoFile = Gdx.files.local(GENERATED_FONTS_DIR + LIBERATION_MONO_REGULAR_FONT_NAME);

        try {
            FileUtils.copyFile(exoFileInternal, exoFile);
        } catch (IOException ex) {
            Gdx.app.error(SlotPuzzleConstants.SLOT_PUZZLE, "Could not copy " + exoFileInternal.file().getPath() + " to file " + exoFile.file().getAbsolutePath() + " " + ex.getMessage());
        }

        fontSmall = fontGen.createFont(exoFile, FONT_SMALL, 24);
        fontMedium = fontGen.createFont(exoFile, FONT_MEDIUM, 48);
        fontLarge = fontGen.createFont(exoFile, FONT_LARGE, 64);
    }

    private Texture initialiseFontTexture(String reelText) {
        Pixmap textPixmap = new Pixmap(REEL_WIDTH * reelText.length(), REEL_HEIGHT, Pixmap.Format.RGBA8888);
        textPixmap = PixmapProcessors.createDynamicHorizontalFontTextForReel(fontSmall, reelText, textPixmap);
        PixmapProcessors.savePixmap(textPixmap);
        Texture textTexture = new Texture(textPixmap);
        return textTexture;
    }

    private void update() {
        camController.update();
        for (ModelInstance modelInstance : instances) {
            modelInstance.transform.rotate(0, -1, 0, 1);
        }
    }

    @Override
    public void render () {
        update();
        glClearFrame();
        modelBatch.begin(cam);
        modelBatch.render(instances, environment);
        modelBatch.end();
    }

    private void glClearFrame() {
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void dispose () {
        modelBatch.dispose();
        instances.clear();
    }
}