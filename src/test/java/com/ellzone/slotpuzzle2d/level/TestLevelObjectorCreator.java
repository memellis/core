package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.utils.InputMatrix;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import box2dLight.RayHandler;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LevelObjectCreator.class, World.class})

public class TestLevelObjectorCreator {
    private World worldMock;
    private RayHandler rayHandlerMock;
    private LevelCreatorInjectionInterface levelCreatorInjectionInterfaceMock;
    private int[][] testMatrix;

    @Before
    public void setUp() {
        setUpMocks();
    }

    @After
    public void tearDown() {
        tearDownMocks();
    }

    @Test
    public void testLevelObjectCreatorNewInstance() {
        LevelObjectCreator levelObjectCreator = new LevelObjectCreator(levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
    }

    @Test
    public void testCreateKevelMap() {
        LevelObjectCreator levelObjectCreator = new LevelObjectCreator(levelCreatorInjectionInterfaceMock, worldMock, rayHandlerMock);
        assertThat(levelObjectCreator, is(notNullValue()));
        testMatrix = createLevelMatrix();
        assertThat(testMatrix.length, is(equalTo(9)));
        assertThat(testMatrix[0].length, is(equalTo(12)));
    }

    private void setUpMocks() {
        worldMock = createMock(World.class);
        rayHandlerMock = createMock(RayHandler.class);
        levelCreatorInjectionInterfaceMock = createMock(LevelCreatorInjectionInterface.class);
    }

    private void tearDownMocks() {
        worldMock = null;
        rayHandlerMock = null;
    }

    private void replayAll() {

    }

    private void verifyAll() {

    }

    private int[][] createLevelMatrix() {
        String matrixToInput = "12 x 9\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n"
                + "-1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1\n";
        InputMatrix inputMatrix = new InputMatrix(matrixToInput);
        return inputMatrix.readMatrix();
    }

    private void createGrid(int[][] matrix) {
//        testGrid = new TupleValueIndex[matrix.length][matrix[0].length];
//        reelTiles = new Array<>();
//        for (int r = 0; r < matrix.length; r++) {
//            for (int c = 0; c < matrix[0].length; c++) {
//                testGrid[r][c] = new TupleValueIndex(r, c, r * matrix[0].length + c, matrix[r][c]);
//                ReelTile reelTileMock = PowerMock.createMock(ReelTile.class);
//                Whitebox.setInternalState(reelTileMock,"x", PlayScreen.PUZZLE_GRID_START_X + (c * 40));
//                Whitebox.setInternalState(reelTileMock,"y",(r  * 40) - PlayScreen.PUZZLE_GRID_START_Y);
//                Whitebox.setInternalState(reelTileMock, "tileDeleted", matrix[r][c] < 0);
//                Whitebox.setInternalState(reelTileMock, "index", r * testGrid[0].length + c);
//                reelTiles.add(reelTileMock);
//            }
//        }
    }

}
