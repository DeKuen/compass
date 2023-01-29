package ch.dekuen.android.compass.sensor;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ch.dekuen.android.compass.sensor.CoordinatesLowPassFilter.LOW_PASS_FILTER_ALPHA;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RunWith(RobolectricTestRunner.class)
public class CoordinatesLowPassFilterTest {
    private CoordinatesLowPassFilter testee;
    private final List<float[]> measurements = new ArrayList<>();
    private final Consumer<float[]> consumer = floats -> measurements.add(floats.clone());

    @Before
    public void before() {
        testee = new CoordinatesLowPassFilter(consumer);
    }

    @After
    public void after() {
        assertEquals(0, measurements.size());
    }

    @Test
    public void onSensorChanged_FirstUpdates_ConsumeUpdates() {
        // setup
        float[] updates = {0.1f, 1.2f, 2.3f};
        // act
        testee.onSensorChanged(updates);
        // assert
        assertEquals(1, measurements.size());
        assertArrayEquals(updates, measurements.get(0));
        measurements.clear();
    }

    @Test
    public void onSensorChanged_TwoUpdates_ConsumeFilteredUpdates() {
        // setup
        float[] updates0 = {1f, 2f, 3f};
        float[] updates1 = {0f, 0.2f, 0.3f};
        // act
        testee.onSensorChanged(updates0);
        testee.onSensorChanged(updates1);
        // assert
        assertEquals(2, measurements.size());
        assertArrayEquals(updates0, measurements.get(0));
        float[] expected1 = {
                LOW_PASS_FILTER_ALPHA,
                CoordinatesLowPassFilter.applyFilter(updates0[1], updates1[1]),
                CoordinatesLowPassFilter.applyFilter(updates0[2], updates1[2])
        };
        assertArrayEquals(expected1, measurements.get(1));
        measurements.clear();
    }
}