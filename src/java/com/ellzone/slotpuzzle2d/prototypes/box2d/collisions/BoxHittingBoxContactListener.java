package com.ellzone.slotpuzzle2d.prototypes.box2d.collisions;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class BoxHittingBoxContactListener implements ContactListener {
    public static final String ANIMATED_REEL_CLASS_NAME = "com.ellzone.slotpuzzle2d.sprites.AnimatedReel";
    @Override
    public void endContact(Contact contact) {
        System.out.println("End contact");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    @Override
    public void beginContact(Contact contact) {
        System.out.println("Begin contact");
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        if (isAnimatedReel(getBodyClassNameFromFixture(fixtureA)) &
            isAnimatedReel(getBodyClassNameFromFixture(fixtureB)))
            dealWithTwoReelBoxesHittingEachOther();
    }

    private void dealWithTwoReelBoxesHittingEachOther() {
        System.out.println("Two reels hit each other");
    }

    String getBodyClassNameFromFixture(Fixture fixture) {
        Object userData = fixture.getBody().getUserData();
        return userData == null ? "" : userData.getClass().getName();
    }

    Boolean isAnimatedReel(String className) {
        return className.equalsIgnoreCase(ANIMATED_REEL_CLASS_NAME);
    }
}