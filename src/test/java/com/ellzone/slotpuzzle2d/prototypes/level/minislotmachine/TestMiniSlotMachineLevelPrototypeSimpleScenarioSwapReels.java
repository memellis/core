package com.ellzone.slotpuzzle2d.prototypes.level.minislotmachine;

import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.level.creator.LevelCreatorSimpleScenario;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;

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

import static org.easymock.EasyMock.captureFloat;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {MiniSlotMachineLevelPrototypeSimpleScenario.class} )


public class TestMiniSlotMachineLevelPrototypeSimpleScenarioSwapReels {
    private static final String LEVEL_CREATOR_FIELD_NAME = "levelCreator";
    private static final String REEL_TILES_FIELD_NAME = "reelTiles";

    private MiniSlotMachineLevelPrototypeSimpleScenario partialMockMiniSlotMachineLevelPrototypeSimpleScenario;
    private ReelTile reelTileAMock, reelTileBMock, deletedReelMock;
    private LevelCreatorSimpleScenario levelCreatorMock;
    private Array<ReelTile> reelTilesMock;
    private Capture<Float> reelTileASetDestinationYCapture,
                             reelTileASetYCapture,
                             deletedReelBSetDestinationYCapture,
                             deletedReelSetYCapture;

    @Before
    public void setUp() {
        setUpPowerMocks();
        setUpEasyMocks();
        setUpCaptures();
    }

    private void setUpPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeSimpleScenario = PowerMock.createNicePartialMock(MiniSlotMachineLevelPrototypeSimpleScenario.class,
                "swapReelsAboveMe");
    }

    private void setUpEasyMocks() {
        reelTileAMock = createMock(ReelTile.class);
        reelTileBMock = createMock(ReelTile.class);
        levelCreatorMock = createMock(LevelCreatorSimpleScenario.class);
        reelTilesMock = createMock(Array.class);
        deletedReelMock = createMock(ReelTile.class);
    }

    private void setUpCaptures() {
        reelTileASetDestinationYCapture = EasyMock.newCapture();
        reelTileASetYCapture = EasyMock.newCapture();
        deletedReelBSetDestinationYCapture = EasyMock.newCapture();
        deletedReelSetYCapture = EasyMock.newCapture();
    }

    @After
    public void tearDown() {
        tearDownEasyMocks();
        tearDownCaptures();
    }

    private void tearDownEasyMocks() {
        reelTileAMock = null;
        reelTileBMock = null;
        levelCreatorMock = null;
        reelTileAMock = null;
        deletedReelMock = null;
    }

    private void tearDownCaptures() {
        reelTileASetDestinationYCapture = null;
        reelTileASetYCapture = null;
        deletedReelBSetDestinationYCapture = null;
        deletedReelSetYCapture = null;
    }

    @Test
    public void testSwapReelsReelTileAReelTileB() throws Exception {
        setFields();
        setUpSwapReelsReelTileAReelTileBExpectations();
        replayAll();
        Whitebox.invokeMethod(partialMockMiniSlotMachineLevelPrototypeSimpleScenario,
                "swapReels",
                reelTileAMock,
                reelTileBMock);
        assertThat(reelTileASetYCapture.getValue(), is(equalTo(160.0f)));
        assertThat(reelTileASetYCapture.getValue(), is(equalTo( 160.0f)));
        assertThat(deletedReelBSetDestinationYCapture.getValue(), is(equalTo(80.0f)));
        assertThat(deletedReelSetYCapture.getValue(), is(equalTo( 80.0f)));
        verifyAll();
    }

    @Test
    public void testSwapReelsReelTile() throws Exception {
        setFields();
        setUpSwapReelTileExpectations();
        replayAll();
        Whitebox.invokeMethod(partialMockMiniSlotMachineLevelPrototypeSimpleScenario,
                "swapReels",
                reelTileAMock);
        assertThat(reelTileASetYCapture.getValue(), is(equalTo(120.0f)));
        assertThat(reelTileASetYCapture.getValue(), is(equalTo( 120.0f)));
        assertThat(deletedReelBSetDestinationYCapture.getValue(), is(equalTo(80.0f)));
        assertThat(deletedReelSetYCapture.getValue(), is(equalTo( 80.0f)));
        verifyAll();
    }

    private void setFields() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, LEVEL_CREATOR_FIELD_NAME, levelCreatorMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeSimpleScenario, REEL_TILES_FIELD_NAME, reelTilesMock);
    }

    private void setUpSwapReelsReelTileAReelTileBExpectations() {
        expect(reelTileAMock.getDestinationY()).andReturn(80.0f);
        expect(reelTileBMock.getDestinationX()).andReturn((40.0f));
        expect(reelTileBMock.getDestinationY()).andReturn(120.0f);
        expect(levelCreatorMock.findReel(40, 160)).andReturn(0);
        expect(reelTilesMock.get(0)).andReturn(deletedReelMock);
        expect(reelTileBMock.getDestinationY()).andReturn(120.0f);
        reelTileAMock.setDestinationY(captureFloat(reelTileASetDestinationYCapture));
        expect(reelTileBMock.getDestinationY()).andReturn(120.0f);
        reelTileAMock.setY(captureFloat(reelTileASetYCapture));
        reelTileAMock.unDeleteReelTile();
        deletedReelMock.setDestinationY(captureFloat(deletedReelBSetDestinationYCapture));
        deletedReelMock.setY(captureFloat(deletedReelSetYCapture));
    }

    private void setUpSwapReelTileExpectations() {
        expect(reelTileAMock.getDestinationY()).andReturn(80.0f);
        expect(reelTileAMock.getDestinationX()).andReturn((40.0f));
        expect(levelCreatorMock.findReel(40, 120)).andReturn(0);
        expect(reelTilesMock.get(0)).andReturn(deletedReelMock);
        reelTileAMock.setDestinationY(captureFloat(reelTileASetDestinationYCapture));
        reelTileAMock.setY(captureFloat(reelTileASetYCapture));
        reelTileAMock.unDeleteReelTile();
        deletedReelMock.setDestinationY(captureFloat(deletedReelBSetDestinationYCapture));
        deletedReelMock.setY(captureFloat(deletedReelSetYCapture));
    }

    private void replayAll() {
        replay(reelTileAMock,
               reelTileBMock,
                partialMockMiniSlotMachineLevelPrototypeSimpleScenario,
               reelTilesMock,
               deletedReelMock
        );
    }

    private void verifyAll() {
        verify(reelTileAMock,
               reelTileBMock,
                partialMockMiniSlotMachineLevelPrototypeSimpleScenario,
               reelTilesMock,
               deletedReelMock
        );
    }
}
