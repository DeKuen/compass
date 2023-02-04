package ch.dekuen.android.compass.sensor;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RunWith(RobolectricTestRunner.class)
public class OrientationCalculatorTest {
    private OrientationCalculator testee;
    private final List<float[]> measurements = new ArrayList<>();
    private final Consumer<float[]> consumer = floats -> measurements.add(floats.clone());

    @Before
    public void before() {
        testee = new OrientationCalculator(consumer);
    }

    @After
    public void after() {
        Assertions.assertEquals(0, measurements.size());
    }

    @Test
    public void calculate_MatrixR_Azimut() {
        // setup
        float[] matrixR = {0.0f, -0.7071068f, 0.7071068f, 0.9999998f, -0.00050968386f, -0.00050968386f, 0.00072080176f, 0.7071066f, 0.7071066f};
        // act
        testee.calculate(matrixR);
        // assert
        float[] updates = {-1.5715171f, -0.7853979f, -0.0010193676f};
        Assertions.assertEquals(1, measurements.size());
        assertArrayEquals(updates, measurements.get(0), 0.0001f);
        measurements.clear();
    }
}