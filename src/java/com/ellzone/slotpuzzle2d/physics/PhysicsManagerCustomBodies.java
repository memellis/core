package com.ellzone.slotpuzzle2d.physics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class PhysicsManagerCustomBodies {
    BoxBodyBuilder bodyFactory;

    float accumulator;
    boolean isPaused, disposeWorld;

    static final float BOX_STEP = 1 / 120f;
    static final float RENDER_STEP = 1 / 40f;
    static final int VELOCITY_ITER = 8;
    static final int POSITION_ITER = 3;

    World world;

    Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;

    public PhysicsManagerCustomBodies(OrthographicCamera cam) {
        this.isPaused = false;
        this.disposeWorld = false;
        if (this.world == null) {
            this.world = new World(new Vector2(0, -5), true);
            this.disposeWorld = true;
            this.world.setContactListener(new B2dContactListener());
        }
        this.bodyFactory = new BoxBodyBuilder();

        this.debugMatrix = cam.combined.cpy();
        this.debugMatrix.scale(BoxBodyBuilder.BOX_TO_WORLD, BoxBodyBuilder.BOX_TO_WORLD, 1f);
        this.debugRenderer = new Box2DDebugRenderer();
    }

    public PhysicsManagerCustomBodies(OrthographicCamera cam, World world) {
        this.world = world;
        new PhysicsManagerCustomBodies(cam);
    }

    public BoxBodyBuilder getBodyFactory() {
        return this.bodyFactory;
    }

    public Body createCircleBody(BodyDef.BodyType bodyType, float posx, float posy, float radius) {
        return this.bodyFactory.createCircleBody(this.world, bodyType, posx, posy, radius);
    }

    public Body createEdgeBody(BodyDef.BodyType bodyType,
                               float v1x,
                               float v1y,
                               float v2x,
                               float v2y) {
        return this.bodyFactory.createEdgeBody(this.world, bodyType, v1x, v1y, v2x, v2y);
    }

    public Body createBoxBody(BodyDef.BodyType bodyType, float posx, float posy, float width, float height, boolean fixedRotation) {
        return  this.bodyFactory.createBoxBody(this.world, bodyType, posx, posy, width, height, fixedRotation);
    }

    public static Boolean isStopped(Body body) {
        System.out.println("isStopped()="+body.getLinearVelocity().x);
        System.out.println("isStopped()="+body.getLinearVelocity().y);
        return body.getLinearVelocity().x <= 0.3f && body.getLinearVelocity().y <= 0.3f;
    }

    public void update(float dt){
        if(!isPaused){
            accumulator += dt;
            while(accumulator > dt){
                world.step(BOX_STEP,VELOCITY_ITER, POSITION_ITER);
                accumulator -= BOX_STEP;
            }
        }
    }

    public void draw(SpriteBatch spriteBatch){
        spriteBatch.begin();
        debugRenderer.render(world, debugMatrix);
        spriteBatch.end();
    }

    public void dispose(){
        if (debugRenderer != null) {
            debugRenderer.dispose();
        }
        disposeJoints();
        disposeBodies();

        if ((disposeWorld) & (world != null)) {
            world.dispose();
        }
    }

    private void disposeBodies() {
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
        for (Body body : bodies) {
            world.destroyBody(body);
        }
    }

    private void disposeJoints() {
        Array<Joint> joints = new Array<Joint>();
        world.getJoints(joints);
        for (Joint joint : joints) {
            world.destroyJoint(joint);
        }
    }

    public void deleteBody(Body body) {
        world.destroyBody(body);
        System.out.println("destroyedBody="+body);
    }

    public Matrix4 getDebugMatrix() {
        return debugMatrix;
    }
}