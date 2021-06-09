package com.ellzone.slotpuzzle2d.physics.contact;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.gdx.MyGDXApplication;
import com.badlogic.gdx.math.Vector2;
import com.ellzone.slotpuzzle2d.messaging.MessageUtils;
import com.ellzone.slotpuzzle2d.physics.ReelSink;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.AnimatedReelsMatrixCreator;
import com.ellzone.slotpuzzle2d.prototypes.box2d.collisions.SlotPuzzleMatrices;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReel;
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
    private AnimatedReelsMatrixCreator animatedReelsMatrixCreator;

    @Before
    public void setUp() {
        Gdx.app = new MyGDXApplication();
        world = new World(new Vector2(0,0), false);
        myTestContact = new MyContact(null, 0L);
        animatedReels = new Array<>();
        reelBoxBodies = new Array<Body>();
        animatedReelsManager = new AnimatedReelsManager(animatedReels, reelBoxBodies);
        animatedReelsMatrixCreator = new AnimatedReelsMatrixCreator();
    }

    @After
    public void tearDown() {
        Gdx.app = null;
        world = null;
        myTestContact = null;
        animatedReels = null;
        reelBoxBodies = null;
        animatedReelsManager = null;
        animatedReelsMatrixCreator = null;
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

    @Test
    public void testReelFallingOntoTwoReels() {
        animatedReels.add(setUpAnimatedReel(160, 40, true));
        animatedReels.add(setUpAnimatedReel(160, 80, true));
        animatedReels.add(setUpAnimatedReel(160, 120, false));
        animatedReelsManager.setNumberOfReelsToFall(1);
        MessageManager messageManager = MessageUtils.setUpMessageManager(animatedReelsManager);
        myTestContact.setFixtureA(animatedReels.get(1));
        myTestContact.setFixtureB(animatedReels.get(2));
        BoxHittingBoxContactListener boxHittingBoxContactListener = new BoxHittingBoxContactListener();
        boxHittingBoxContactListener.beginContact(myTestContact);
        assertThat(animatedReelsManager.getNumberOfReelsToFall(), is(equalTo(0)));
    }

    @Test
    public void testFallingReelWhenMatchingReelsDeleted() {
        animatedReels = animatedReelsMatrixCreator.createAnimatedReelsFromSlotPuzzleMatrix(
                SlotPuzzleMatrices.createMatrixWithFillColumnNineBoxes());
        animatedReels.get(0).getReel().deleteReelTile();
        animatedReels.get(0).getReel().setDestinationY(80);
        animatedReels.get(0).getReel().setY(160);
        animatedReels.get(12).getReel().deleteReelTile();
        animatedReels.get(12).getReel().setY(320+400);
        animatedReels.get(24).getReel().deleteReelTile();
        animatedReels.get(24).getReel().setY(280+400);
        animatedReels.get(36).getReel().deleteReelTile();
        animatedReels.get(36).getReel().setY(280+400);
        animatedReels.get(48).getReel().deleteReelTile();
        animatedReels.get(48).getReel().setY(240+400);
        animatedReels.get(60).getReel().deleteReelTile();
        animatedReels.get(60).getReel().setY(200+400);
        animatedReels.get(84).getReel().deleteReelTile();
        animatedReels.get(72).getReel().setY(80);
        myTestContact.setFixtureA(animatedReels.get(72));
        myTestContact.setFixtureB(animatedReels.get(96));
        BoxHittingBoxContactListener boxHittingBoxContactListener = new BoxHittingBoxContactListener();
        boxHittingBoxContactListener.beginContact(myTestContact);
        assertThat(animatedReelsManager.getNumberOfReelsToFall(), is(equalTo(0)));
    }

    private AnimatedReel setUpAnimatedReel(int x, int y, boolean isFallen) {
        AnimatedReel animatedReel = ReelUtils.createAnimatedReel(x, y);
        animatedReel.getReel().setDestinationX(x);
        animatedReel.getReel().setDestinationY(y);
        animatedReel.getReel().setIsFallen(isFallen);
        return animatedReel;
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
