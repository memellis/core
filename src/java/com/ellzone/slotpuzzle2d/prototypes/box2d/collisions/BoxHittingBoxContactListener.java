package com.ellzone.slotpuzzle2d.prototypes.box2d.collisions;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ellzone.slotpuzzle2d.physics.ReelSink;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;

public class BoxHittingBoxContactListener implements ContactListener {
    public static final String ANIMATED_REEL_CLASS_NAME = "com.ellzone.slotpuzzle2d.sprites.AnimatedReel";
    private static final String REEL_SINK_CLASS_NAME = "com.ellzone.slotpuzzle2d.physics.ReelSink";

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
        dealWithContacts(fixtureA, fixtureB);
    }

    private void dealWithContacts(Fixture fixtureA, Fixture fixtureB) {
        if (isContactBetweenTwoReels(fixtureA, fixtureB))
            dealWithTwoReelBoxesHittingEachOther(fixtureA, fixtureB);
        if (isContactBetweenReelAndReelSink(fixtureA, fixtureB))
            dealWithReelBoxHittingReelSink(fixtureA, fixtureB);
        if (isContactBetweenReelSinkAndReel(fixtureA, fixtureB))
            dealWithReelSinkHittingReelBox(fixtureA, fixtureB);
    }

    private boolean isContactBetweenReelAndReelSink(Fixture fixtureA, Fixture fixtureB) {
        if (isAnimatedReel(getBodyClassNameFromFixture(fixtureA)) &
            isReelSink(getBodyClassNameFromFixture(fixtureB)))
            return true;
        return false;
    }

    private boolean isContactBetweenReelSinkAndReel(Fixture fixtureA, Fixture fixtureB) {
        if (isReelSink(getBodyClassNameFromFixture(fixtureA)) &
            isAnimatedReel(getBodyClassNameFromFixture(fixtureB)))
            return true;
        return false;
    }

    private void dealWithReelBoxHittingReelSink(Fixture fixtureA, Fixture fixtureB) {
        System.out.println("dealWithReelBoxHittingReelSink");
        AnimatedReel animatedReel = getAnimatedReel(fixtureA);
        ReelSink reelSink = getReelSink(fixtureB);
        FallenReel fallenReel = new FallenReel(animatedReel, reelSink);
        fallenReel.processFallenReelHittingReelSink();
    }

    private void dealWithReelSinkHittingReelBox(Fixture fixtureA, Fixture fixtureB) {
        System.out.println("dealWithReelBoxHittingReelSink");
        AnimatedReel animatedReel = getAnimatedReel(fixtureB);
        ReelSink reelSink = getReelSink(fixtureA);
        FallenReel fallenReel = new FallenReel(animatedReel, reelSink);
        fallenReel.processFallenReelHittingReelSink();
    }

    private ReelSink getReelSink(Fixture fixture) {
        return (ReelSink) fixture.getBody().getUserData();
    }

    private Boolean isReelSink(String className) {
        return className.equalsIgnoreCase(REEL_SINK_CLASS_NAME);
    }

    private boolean isContactBetweenTwoReels(Fixture fixtureA, Fixture fixtureB) {
        return isAnimatedReel(getBodyClassNameFromFixture(fixtureA)) &
            isAnimatedReel(getBodyClassNameFromFixture(fixtureB));
    }

    private void dealWithTwoReelBoxesHittingEachOther(Fixture fixtureA, Fixture fixtureB) {
        System.out.println("Two reels hit each other");

        AnimatedReel animatedReelA = getAnimatedReel(fixtureA);
        AnimatedReel animatedReelB = getAnimatedReel(fixtureB);

        if (isSameColumn(animatedReelA, animatedReelB)) {
            FallenReel fallenReel = new FallenReel(animatedReelA, animatedReelB);
            fallenReel.processRows();
        }
    }

    private boolean isSameColumn(AnimatedReel animatedReelA, AnimatedReel animatedReelB) {
        ReelTile reelA = animatedReelA.getReel();
        ReelTile reelB = animatedReelB.getReel();
        return getColumn(reelA) == getColumn(reelB);
    }

    private int getColumn(ReelTile reel) {
        return (int) ((int) reel.getX());
    }


    private AnimatedReel getAnimatedReel(Fixture fixture) {
        return (AnimatedReel) fixture.getBody().getUserData();
    }

    String getBodyClassNameFromFixture(Fixture fixture) {
        Object userData = fixture.getBody().getUserData();
        return userData == null ? "" : userData.getClass().getName();
    }

    Boolean isAnimatedReel(String className) {
        return className.equalsIgnoreCase(ANIMATED_REEL_CLASS_NAME);
    }
}

