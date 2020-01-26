package com.ellzone.slotpuzzle2d.prototypes.box2d.collisions;

import com.badlogic.gdx.graphics.g3d.particles.influencers.RegionInfluencer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;

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
            dealWithTwoReelBoxesHittingEachOther(fixtureA, fixtureB);
    }

    private void dealWithTwoReelBoxesHittingEachOther(Fixture fixtureA, Fixture fixtureB) {
        System.out.println("Two reels hit each other");

        AnimatedReel animatedReelA = getAnimatedReel(fixtureA);
        AnimatedReel animatedReelB = getAnimatedReel(fixtureB);

        if(isSameColumn(animatedReelA, animatedReelB))
            processRows(animatedReelA, animatedReelB);
    }

    private boolean isSameColumn(AnimatedReel animatedReelA, AnimatedReel animatedReelB) {
        ReelTile reelA = animatedReelA.getReel();
        ReelTile reelB = animatedReelB.getReel();
        return getColumn(reelA) == getColumn(reelB);
    }

    private int getColumn(ReelTile reel) {
        return (int) ((int) reel.getX());
    }

    private void processRows(AnimatedReel animatedReelA, AnimatedReel animatedReelB) {
        System.out.println("process rows");
        System.out.println(animatedReelA.getReel().getY());
        System.out.println(animatedReelB.getReel().getY());
        System.out.println(animatedReelA.getReel().getY() - animatedReelB.getReel().getY());
    }

    private AnimatedReel getAnimatedReel(Fixture fixture) {
        return (AnimatedReel) fixture.getBody().getUserData();
    }

    String getBodyClassNameFromFixture(Fixture fixture) {
        Object userData = fixture.getBody().getUserData();
        System.out.println(userData == null ? "" : userData.getClass().getName());
        return userData == null ? "" : userData.getClass().getName();
    }

    Boolean isAnimatedReel(String className) {
        return className.equalsIgnoreCase(ANIMATED_REEL_CLASS_NAME);
    }
}