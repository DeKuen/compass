package ch.dekuen.android.compassapp.listener;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.Optional;

import ch.dekuen.android.compassapp.service.AzimutService;

@RunWith(RobolectricTestRunner.class)
public class AzimutServiceTest {
    private static final float G = 9.81f;
    private AzimutService testee;

    @Before
    public void before() {
        testee = new AzimutService();
    }

    @Test
    public void getAzimut_NoRotationMatrix_emptyOptional() {
        // setup
        float[] acceleration = new float[1];
        float[] magneticField = new float[2];
        // act
        Optional<Float> actual = testee.getAzimut(acceleration, magneticField);
        // assert
        assertFalse(actual.isPresent());
    }

    @Test
    public void getAzimut_CalcAzimut_filledOptional() {
        // setup
        float[] acceleration = {0.01f, G, G};
        float[] magneticField = {1f, 1f, 1f};
        // act
        Optional<Float> actual = testee.getAzimut(acceleration, magneticField);
        // assert
        assertTrue(actual.isPresent());
        Float actualAzimut = actual.get();
        assertEquals(Float.valueOf(-1.5715171f), actualAzimut);
    }
}