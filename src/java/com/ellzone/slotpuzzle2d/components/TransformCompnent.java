package com.ellzone.slotpuzzle2d.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class TransformCompnent implements Component {
    public final Vector3 position = new Vector3();
    public final Vector2 scale = new Vector2();
    public float rotation = 0.0f;
}
