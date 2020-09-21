package com.ellzone.slotpuzzle2d.spin;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;

public interface SpinWheelSlotPuzzle {
    void setUpSpinWheel();

    void setUpSpinWheel(Stage stage);

    Image getWheelImage();

    Image getNeedleImage();

    void updateCoordinates(Body body, Image image, float incX, float incY);

    void spin(float omega);

    boolean spinningStopped();

    void setWorldContactListener(ContactListener listener);

    void setWorld(World world);

    Body getWheelBody();

    Body getNeedleBody();

    float getNeedleCenterX(float needleWidth);

    float getNeedleCenterY(float needleHeight);

    void setElements(ObjectMap<IntArray, Object> elements);

    void addElementData(Object object, IntArray data);

    Object getLuckyWinElement();
}
