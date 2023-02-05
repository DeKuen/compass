package ch.dekuen.android.compass.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import android.view.Surface;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.function.Supplier;

import ch.dekuen.android.compass.MathConstants;

@RunWith(RobolectricTestRunner.class)
public class CompassViewOrientationCorrectorTest {
    // Display class is final and can't be mocked normally. Thus the Supplier construct
    private static final Supplier<Integer> getDisplayRotation_0 = () -> Surface.ROTATION_0;
    private static final Supplier<Integer> getDisplayRotation_90 = () -> Surface.ROTATION_90;
    private static final Supplier<Integer> getDisplayRotation_180 = () -> Surface.ROTATION_180;
    private static final Supplier<Integer> getDisplayRotation_270 = () -> Surface.ROTATION_270;
    private static final float AZIMUT_NORTH = 0f;
    private static final float AZIMUT_NORTH_EAST = (float) (MathConstants.PI / 4);
    private static final float AZIMUT_SOUTH_EAST = (float) (MathConstants.PI * 3 / 4);
    private static final float AZIMUT_SOUTH_WEST = (float) (MathConstants.PI * 5 / 4);
    private static final float AZIMUT_NORTH_WEST = (float) (MathConstants.PI * 7 / 4);

    private CompassViewOrientationCorrector testee;

    @Test
    public void correctOrientation_NorthAndOrientationZeroAndDisplayUp_North() {
        // setup
        testee = new CompassViewOrientationCorrector(getDisplayRotation_0);
        // act
        double actual = testee.correctOrientation(AZIMUT_NORTH, true);
        // assert
        assertEquals(AZIMUT_NORTH, actual, 0.00001f);
    }

    @Test
    public void correctOrientation_NorthAndOrientationZeroAndDisplayDown_North() {
        // setup
        testee = new CompassViewOrientationCorrector(getDisplayRotation_0);
        // act
        double actual = testee.correctOrientation(AZIMUT_NORTH, false);
        // assert
        assertEquals(AZIMUT_NORTH, actual, 0.00001f);
    }

    @Test
    public void correctOrientation_SouthEastAndOrientationZeroAndDisplayUp_SouthEast() {
        // setup
        testee = new CompassViewOrientationCorrector(getDisplayRotation_0);
        // act
        double actual = testee.correctOrientation(AZIMUT_SOUTH_EAST, true);
        // assert
        assertEquals(AZIMUT_SOUTH_EAST, actual, 0.00001f);
    }

    @Test
    public void correctOrientation_SouthEastAndOrientationZeroAndDisplayDown_SouthWest() {
        // setup
        testee = new CompassViewOrientationCorrector(getDisplayRotation_0);
        // act
        double actual = testee.correctOrientation(AZIMUT_SOUTH_EAST, false);
        // assert
        assertEquals(AZIMUT_SOUTH_WEST, actual, 0.00001f);
    }

    @Test
    public void correctOrientation_SouthEastAndOrientation90AndDisplayUp_SouthWest() {
        // setup
        testee = new CompassViewOrientationCorrector(getDisplayRotation_90);
        // act
        double actual = testee.correctOrientation(AZIMUT_SOUTH_EAST, true);
        // assert
        assertEquals(AZIMUT_SOUTH_WEST, actual, 0.00001f);
    }

    @Test
    public void correctOrientation_SouthEastAndOrientation180AndDisplayUp_NorthWest() {
        // setup
        testee = new CompassViewOrientationCorrector(getDisplayRotation_180);
        // act
        double actual = testee.correctOrientation(AZIMUT_SOUTH_EAST, true);
        // assert
        assertEquals(AZIMUT_NORTH_WEST, actual, 0.00001f);
    }

    @Test
    public void correctOrientation_SouthEastAndOrientation270AndDisplayUp_NorthEast() {
        // setup
        testee = new CompassViewOrientationCorrector(getDisplayRotation_270);
        // act
        double actual = testee.correctOrientation(AZIMUT_SOUTH_EAST, true);
        // assert
        assertEquals(AZIMUT_NORTH_EAST, actual, 0.00001f);
    }
}