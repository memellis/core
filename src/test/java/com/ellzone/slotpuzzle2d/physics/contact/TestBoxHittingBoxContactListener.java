package com.ellzone.slotpuzzle2d.physics.contact;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.gdx.MyGDXApplication;
import com.badlogic.gdx.math.Vector2;
import com.ellzone.slotpuzzle2d.messaging.MessageUtils;
import com.ellzone.slotpuzzle2d.physics.ReelSink;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.utils.reels.ReelUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TestBoxHittingBoxContactListener {

    private World world;
    private MyContact myTestContact;
    private Array<AnimatedReel> animatedReels;
    private Array<Body> reelBoxBodies;
    private AnimatedReelsManager animatedReelsManager;

    @Before
    public void setUp() {
        Gdx.app = new MyGDXApplication();
        world = new World(new Vector2(0,0), false);
        myTestContact = new MyContact(null, 0L);
        animatedReels = new Array<>();
        reelBoxBodies = new Array<Body>();
        animatedReelsManager = new AnimatedReelsManager(animatedReels, reelBoxBodies);
    }

    @After
    public void tearDown() {
        Gdx.app = null;
        world = null;
        myTestContact = null;
        animatedReels = null;
        reelBoxBodies = null;
        animatedReelsManager = null;
        MessageManager.getInstance().clearListeners();
    }

    @Test
    public void testReelFallingOntoReelContact() {
        AnimatedReel animatedReelA = ReelUtils.createAnimatedReel(160, 40);
        animatedReelA.getReel().setIsFallen(true);
        AnimatedReel animatedReelB = ReelUtils.createAnimatedReel(160, 80);
        animatedReelB.getReel().setIsFallen(false);
        animatedReelsManager.setNumberOfReelsToFall(1);
        MessageManager messageManager = MessageUtils.setUpMessageManager(animatedReelsManager);
        myTestContact.setFixtureA(animatedReelA);
        myTestContact.setFixtureB(animatedReelB);
        BoxHittingBoxContactListener boxHittingBoxContactListener = new BoxHittingBoxContactListener();
        boxHittingBoxContactListener.beginContact(myTestContact);
        assertThat(animatedReelsManager.getNumberOfReelsToFall(), is(equalTo(0)));
    }

    @Test
    public void testReelFallingOntoReelWithReversedFixtrures() {
        AnimatedReel animatedReelA = ReelUtils.createAnimatedReel(160, 40);
        animatedReelA.getReel().setIsFallen(true);
        AnimatedReel animatedReelB = ReelUtils.createAnimatedReel(160, 80);
        animatedReelB.getReel().setIsFallen(false);
        animatedReelsManager.setNumberOfReelsToFall(1);
        MessageManager messageManager = MessageUtils.setUpMessageManager(animatedReelsManager);
        myTestContact.setFixtureA(animatedReelB);
        myTestContact.setFixtureB(animatedReelA);
        BoxHittingBoxContactListener boxHittingBoxContactListener = new BoxHittingBoxContactListener();
        boxHittingBoxContactListener.beginContact(myTestContact);
        assertThat(animatedReelsManager.getNumberOfReelsToFall(), is(equalTo(0)));
    }

    public class MyContact extends Contact {
        MyFixture fixtureA, fixtureB;

        protected MyContact(World world, long addr) {
            super(world, addr);
        }

        public void setFixtureA(AnimatedReel animatedReel) {
            MyBody myBodyA = new MyBody(null, 0L);
            myBodyA.setUserData(animatedReel);
            fixtureA = new MyFixture(myBodyA, 0L);
        }

        public void setFixtureA(ReelSink reelSink) {
            MyBody myBodyA = new MyBody(null, 0L);
            myBodyA.setUserData(reelSink);
            fixtureA = new MyFixture(myBodyA, 0L);
        }

        @Override
        public Fixture getFixtureA() {
            return fixtureA;
        }

        public void setFixtureB(AnimatedReel animatedReel) {
            MyBody myBodyB = new MyBody(null, 0L);
            myBodyB.setUserData(animatedReel);
            fixtureB = new MyFixture(myBodyB, 0L);
        }

        public void setFixtureB(ReelSink reelSink) {
            MyBody myBodyB = new MyBody(null, 0L);
            myBodyB.setUserData(reelSink);
            fixtureB = new MyFixture(myBodyB, 0L);
        }

        @Override
        public Fixture getFixtureB() {
            return fixtureB;
        }
    }

    public class MyFixture extends Fixture {
        protected MyFixture(Body body, long addr) {
            super(body, addr);
        }
    }

    public class MyBody extends Body {
        protected MyBody(World world, long addr) {
            super(world, addr);
        }
    }
}
