package com.ellzone.slotpuzzle2d.systems.artemisodb;

import com.artemis.Aspect;
import com.artemis.E;
import com.artemis.Entity;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.component.artemis.AnimatedReelComponent;
import com.ellzone.slotpuzzle2d.component.artemis.Position;
import com.ellzone.slotpuzzle2d.component.artemis.SpinScroll;
import com.ellzone.slotpuzzle2d.level.reel.ReelGrid;
import com.ellzone.slotpuzzle2d.puzzlegrid.CalculateMatches;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedPredictedReel;
import com.ellzone.slotpuzzle2d.sprites.reel.AnimatedReelECS;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle2d.utils.Random;

import net.mostlyoriginal.api.operation.common.Operation;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;

import static com.ellzone.slotpuzzle2d.operation.artemisodb.SlotPuzzleOperationFactory.spinScrollBetween;
import static net.mostlyoriginal.api.operation.OperationFactory.*;


public class AnimatedReelSystem extends EntityProcessingSystem {
    public static final int MIN_REEL_SPIN_RANGE = 28000;
    public static final int MAX_REEL_SPIN_RANGE = 32768;
    public static final String REEL_GRID_MATCHED_ROW = "ReelGridMatchedRow";
    private static int numberOfReelsSpinning = 0;
    private LevelCreatorSystem levelCreatorSystem;
    private OrthographicCamera camera;
    protected M<Position> mPosition;
    protected M<SpinScroll> mSpinScroll;
    private boolean isTouched = false;
    private boolean isSlotHandlePulled = false;
    private Vector3 unProjectTouch;
    private AnimatedPredictedReel animatedPredictedReel;
    private boolean isReelSpinDirectionClockwise = false;
    private CalculateMatches calculateMatches;

    public AnimatedReelSystem() {
        super(Aspect.all(Position.class, AnimatedReelComponent.class));
        setup();
    }

    public AnimatedReelSystem(boolean isReelSpinDirectionClockwise) {
        super(Aspect.all(Position.class, AnimatedReelComponent.class));
        this.isReelSpinDirectionClockwise = isReelSpinDirectionClockwise;
        setup();
    }


    public void touched(Vector3 unProjectTouch) {
        this.unProjectTouch = unProjectTouch;
        isTouched = true;
    }

    public void actionSlotHandlePulled() {
        isSlotHandlePulled = true;
    }

    private void setup() {
        setupCamera();
        calculateMatches = new CalculateMatches();
    }

    private void setupCamera() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(
                false,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight());
    }

    @Override
    protected void end() {
        isTouched = false;
        isSlotHandlePulled = false;
    }

    @Override
    protected void process(Entity e) {
        update(e);
        if (isTouched)
            processTouched(e);
        if (isSlotHandlePulled)
            processSlotHandlePulled(e);
    }

    private void update(Entity e) {
        AnimatedReelECS animatedReel =
                (AnimatedReelECS) levelCreatorSystem.getEntities().get(e.getId());
        final SpinScroll spinScroll = mSpinScroll.get(e.getId());
        animatedReel.getReel().setSy(spinScroll.sY);
        animatedReel.update(Gdx.graphics.getDeltaTime());
        levelCreatorSystem.getEntities().set(
                animatedReel.getReel().getEntityIds().get(0),
                animatedReel.getReel().getRegion());
    }

    private void processTouched(Entity e) {
        final Position position = mPosition.get(e);
        ReelTile reelTile = getReelTile(e);
        Rectangle rectangle =
                new Rectangle(
                        position.x,
                        position.y,
                        reelTile.getTileWidth(),
                        reelTile.getRegionHeight());
        if (isTouched & rectangle.contains(unProjectTouch.x, unProjectTouch.y))
            processReelTouched(e, reelTile);
    }

    private ReelTile getReelTile(Entity e) {
        AnimatedReelECS animatedReel =
                (AnimatedReelECS) levelCreatorSystem.getEntities().get(e.getId());

        return animatedReel.getReel();
    }

    private void processReelTouched(
            Entity animatedReelEntity,
            final ReelTile reelTile) {
        if (reelTile.isSpinning())
            setEndReelWhenReelFinishedSpinning(reelTile);
        else
            startReelSpinning(animatedReelEntity, reelTile);
        isTouched = false;
    }

    private void setEndReelWhenReelFinishedSpinning(ReelTile reelTile) {
        reelTile.setEndReel(reelTile.getCurrentReel());
        if (animatedPredictedReel == null)
            createPredictedAnimatedReel();
        if (reelTile.getNumberOfReelsInTexture() > 0)
        animatedPredictedReel.getReel().setSy(
                ((reelTile.getCurrentReel() + 2) % reelTile.getNumberOfReelsInTexture())
                        * reelTile.getTileHeight());
        animatedPredictedReel.getReel().processSpinningState();
    }

    private void createPredictedAnimatedReel() {
        E animatedPredictedReelEntity = E.withTag("AnimatedPredictedReel");
        animatedPredictedReel = (AnimatedPredictedReel)
                levelCreatorSystem.getEntities().get(animatedPredictedReelEntity.id());
    }

    private void startReelSpinning(
            Entity animatedReelEntity,
            final ReelTile reel) {

        numberOfReelsSpinning++;
        final SpinScroll spinScroll = mSpinScroll.get(animatedReelEntity.getId());
        E.E(animatedReelEntity.getId())
                .script(
                        sequence(
                                spinScrollBetween(
                                        0,
                                        spinScroll.sY,
                                        0,
                                        getNearestStartOfScrollHeight(
                                                getNewSy(spinScroll, isReelSpinDirectionClockwise),
                                                reel.getScrollTextureHeight()),
                                        MathUtils.random(3.0f, 7.0f),
                                        Interpolation.fade),
                                new Operation() {
                                    @Override
                                    public boolean process(float delta, Entity e) {
                                        slowToFinish(e, spinScroll, reel);
                                        return true;
                                    }
                                }));
        reel.setEndReel(
                Random
                   .getInstance()
                   .nextInt(reel.getNumberOfReelsInTexture()));
        reel.startSpinning();
    }

    private float getNewSy(SpinScroll spinScroll, boolean isReelSpinDirectionClockwise) {
        return spinScroll.sY + (isReelSpinDirectionClockwise ?
                  MathUtils.random(MIN_REEL_SPIN_RANGE, MAX_REEL_SPIN_RANGE) :
                - MathUtils.random(MIN_REEL_SPIN_RANGE, MAX_REEL_SPIN_RANGE));
    }

    private void slowToFinish(Entity e, SpinScroll spinScroll, final ReelTile reelTile) {
        E.E(e).script(
                sequence(spinScrollBetween(
                        0,
                        spinScroll.sY,
                        0,
                        getEndSpinScroll(reelTile),
                        1.0f,
                        new Interpolation.ElasticOut(
                                2,
                                10,
                                7,
                                1)
                        ),
                        new Operation() {
                            @Override
                            public boolean process(float delta, Entity e) {
                                reelTile.setSpinning(false);
                                reelTile.setEndReel(
                                        Random
                                                .getInstance()
                                                .nextInt(reelTile.getNumberOfReelsInTexture()));
                                numberOfReelsSpinning--;
                                if (numberOfReelsSpinning == 0)
                                    checkForMatches();
                                return true;
                            }
                        }));
    }

    private int getNearestStartOfScrollHeight(float sy, int reelScrollHeight) {
        return (int) ((sy / reelScrollHeight)) * reelScrollHeight;
    }

    private float getEndSpinScroll(ReelTile reelTile) {
        return getNearestStartOfScrollHeight(
                reelTile.getSy(), reelTile.getScrollTextureHeight()) +
                (MathUtils.random(2, 4) * reelTile.getScrollTextureHeight()) +
                (reelTile.getEndReel() * reelTile.getTileWidth());
    }

    private void processSlotHandlePulled(Entity e) {
         ReelTile reelTile = getReelTile(e);
         if (!reelTile.isSpinning())
            startReelSpinning(e, getReelTile(e));
    }

    private void checkForMatches() {
        for (ReelGrid reelGrid :
                new Array.ArrayIterator<>(
                        levelCreatorSystem.getLevelObjectCreatorEntityHolder().getReelGrids())) {
            Array<Array<Vector2>> matchedRows = checkForMatchesForReelGrid(reelGrid);
            if (matchedRows.size > 0)
                setMatchesToBeDisplayed(matchedRows);
        }
    }


    private Array<Array<Vector2>> checkForMatchesForReelGrid(ReelGrid reelGrid) {
        int reelGridMatrixWidth = (int)
            (reelGrid.getWidth() / reelGrid.getAnimatedReelsWithinReelGrid().get(0).getTileWidth());
        int reelGridMatrixHeight = (int)
            (reelGrid.getHeight() / reelGrid.getAnimatedReelsWithinReelGrid().get(0).getTileHeight());
        int[][] reelGridMatrix = new int[reelGridMatrixWidth][reelGridMatrixHeight];
        captureReelPositions(reelGrid.getAnimatedReelsWithinReelGrid(), reelGridMatrix);
        return calculateMatches.process(reelGridMatrix, reelGrid);
    }

    private int[][] captureReelPositions(Array<AnimatedReelECS> reelsTiles, int[][] reelGridMatrix) {
        for (int r = 0; r < reelGridMatrix.length; r++)
            for (int c = 0; c < reelGridMatrix[0].length; c++)
                reelGridMatrix[r][c] = getReelPosition(reelsTiles, r, c);
        return reelGridMatrix;
    }

    private int getReelPosition(Array<AnimatedReelECS> reelTiles, int r, int c) {
            int reelOffset = isReelSpinDirectionClockwise ? 0 : 8;
        return (reelTiles.get(c).getReel().getCurrentReel() + reelOffset + r) %
                reelTiles.get(c).getReel().getNumberOfReelsInTexture();
    }

    private void setMatchesToBeDisplayed(Array<Array<Vector2>> matchedRows) {
        for (Array<Vector2> matchedRow : new Array.ArrayIterator<>(matchedRows))
            setUpMatchedRowToBeDisplay(matchedRow);
    }

    private void setUpMatchedRowToBeDisplay(Array<Vector2> matchedRow) {
        E.E()
                .vector2ShapeVectors(matchedRow)
                .colorRed(255)
                .group(REEL_GRID_MATCHED_ROW);
    }
}
