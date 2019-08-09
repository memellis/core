package com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine;

import com.ellzone.slotpuzzle2d.sprites.ReelTile;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.captureInt;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.createNicePartialMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {MiniSlotMachineLevelPrototypeSimpleScenario.class} )

public class TestMiniSlotMachineLevelPrototypeScnario1ProcessTileHittingTileReelTileAReelTileB {
    private MiniSlotMachineLevelPrototypeSimpleScenario partialMockMiniSlotMachineLevelPrototypeSimpleScenario;
    private ReelTile reelTileAMock, reelTileBMock;
    private Capture<ReelTile> reelTileCaptureA, reelTileCaptureB;
    private Capture<Integer> rCapture, cCapture;

    @Before
    public void setUp() {
        setUpPowerMocks();
        setUpEasyMocks();
        setUpCaptures();
    }

    private void setUpPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario = createNicePartialMock(MiniSlotMachineLevelPrototypeSimpleScenario.class,
                "swapReelsAboveMe",
                "reelsLeftToFall");
    }

    private void setUpEasyMocks() {
        reelTileAMock = createMock(ReelTile.class);
        reelTileBMock = createMock(ReelTile.class);
    }

    private void setUpCaptures() {
        reelTileCaptureA = EasyMock.newCapture();
        reelTileCaptureB = EasyMock.newCapture();
        rCapture = EasyMock.newCapture();
        cCapture = EasyMock.newCapture();
    }

    @After
    public void tearDown() {
        tearDownPowerMocks();
        tearDownEasyMocks();
        tearDownCaptures();
    }

    private void tearDownCaptures() {
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario = null;
    }

    private void tearDownEasyMocks() {
        reelTileAMock = null;
        reelTileBMock = null;
    }

    private void tearDownPowerMocks() {
        reelTileCaptureA = null;
        reelTileCaptureB = null;
        rCapture = null;
        cCapture = null;
    }

    @Test
    public void testProcessReelTileHitReelAGreatherThanReelB() throws Exception {
        setExpectations();
        replayAll();
        Whitebox.invokeMethod(partialMockMiniSlotMachineLevelPrototypeSimpleScenario,
                    "processTileHittingTile",
                    reelTileAMock,
                               reelTileBMock,
                    3, 2, 1, 2
                    );
        assertReelAGreaterReelB();
        verifyAll();
    }

    @Test
    public void testProcessReelTileHitReelALessThanOrEqualReelB() throws Exception {
        setExpectations();
        replayAll();
        Whitebox.invokeMethod(partialMockMiniSlotMachineLevelPrototypeSimpleScenario,
                "processTileHittingTile",
                reelTileAMock,
                            reelTileBMock,
                2, 2, 2, 2
        );
        assertReelALessThanOrEqualReelB();
        verifyAll();
    }

    private void assertReelAGreaterReelB() {
        assertThat(reelTileCaptureA.getValue(), is(equalTo(reelTileBMock)));
        assertThat(reelTileCaptureB.getValue(), is(equalTo(reelTileAMock)));
        assertThat(rCapture.getValue(), is(equalTo(1)));
        assertThat(cCapture.getValue(), is(equalTo(2)));
    }


    private void setExpectations() throws Exception {
        PowerMock.expectPrivate(partialMockMiniSlotMachineLevelPrototypeSimpleScenario,
                "swapReelsAboveMe",
                capture(reelTileCaptureA),
                capture(reelTileCaptureB));
        PowerMock.expectPrivate(partialMockMiniSlotMachineLevelPrototypeSimpleScenario,
                "reelsLeftToFall",
                  captureInt(rCapture),
                  captureInt(cCapture));
    }

    private void assertReelALessThanOrEqualReelB() {
        assertThat(reelTileCaptureA.getValue(), is(equalTo(reelTileAMock)));
        assertThat(reelTileCaptureB.getValue(), is(equalTo(reelTileBMock)));
        assertThat(rCapture.getValue(), is(equalTo(2)));
        assertThat(cCapture.getValue(), is(equalTo(2)));
    }

    private void replayAll(){
        replay(partialMockMiniSlotMachineLevelPrototypeSimpleScenario);
    }

    private void verifyAll() {
        verify(partialMockMiniSlotMachineLevelPrototypeSimpleScenario);
    }
}
