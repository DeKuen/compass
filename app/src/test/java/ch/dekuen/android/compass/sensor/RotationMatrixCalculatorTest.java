package ch.dekuen.android.compass.sensor;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@RunWith(RobolectricTestRunner.class)
public class RotationMatrixCalculatorTest {
    private static final float G = 9.81f;
    private static final float[] ACCELERATION = {0.01f, G, G};
    private static final float[] MAGNETIC_FIELD = {1f, 1f, 1f};
    private RotationMatrixCalculator testee;
    private final List<float[]> measurements = new ArrayList<>();
    private final Consumer<float[]> consumer = floats -> measurements.add(floats.clone());

    @Before
    public void before() {
        testee = new RotationMatrixCalculator(consumer);
    }

    @After
    public void after() {
        assertEquals(0, measurements.size());
    }

    @Test
    public void onAccelerationSensorChanged_OnlyDataFromAcceleration_SendNothing() {
        Consumer<float[]> consumer = testee::onAccelerationSensorChanged;
        onSensorChanged_OnlyDataFromAcceleration_SendNothing(consumer);
    }

    @Test
    public void onMagneticSensorChanged_OnlyDataFromMagnetometer_SendNothing() {
        Consumer<float[]> consumer = testee::onMagneticSensorChanged;
        onSensorChanged_OnlyDataFromAcceleration_SendNothing(consumer);
    }

    private void onSensorChanged_OnlyDataFromAcceleration_SendNothing(Consumer<float[]> consumer) {
        // setup
        float[] updates = {0.1f, 1.2f, 2.3f};
        // act
        consumer.accept(updates);
        // assert
        assertEquals(0, measurements.size());
    }

    @Test
    public void onAccelerationSensorChanged_AfterMagneticChanged_SendAzimut() {
        Runnable runnable = () -> {
            testee.onMagneticSensorChanged(MAGNETIC_FIELD);
            testee.onAccelerationSensorChanged(ACCELERATION);
        };
        onSensorChanged_DataFromBothSensors_SendAzimut(runnable);
    }

    @Test
    public void onMagneticSensorChanged_AfterAccelerationChanged_sendAzimut() {
        Runnable runnable = () -> {
            testee.onMagneticSensorChanged(MAGNETIC_FIELD);
            testee.onAccelerationSensorChanged(ACCELERATION);
        };
        onSensorChanged_DataFromBothSensors_SendAzimut(runnable);
    }

    @Test
    public void onMagneticSensorChanged_FailedToCalcRotationMatrix_SendNothing() {
        // setup
        float[] acceleration = {0f, 0f, 0f};
        // act
        testee.onMagneticSensorChanged(MAGNETIC_FIELD);
        testee.onAccelerationSensorChanged(acceleration);
        // assert
        assertEquals(0, measurements.size());
    }

    private void onSensorChanged_DataFromBothSensors_SendAzimut(Runnable runnable) {
        // setup
        // act
        runnable.run();
        // assert
        assertEquals(1, measurements.size());
        float[] matrixR = {0.0f, -0.7071068f, 0.7071068f, 0.9999998f, -0.00050968386f, -0.00050968386f, 0.00072080176f, 0.7071066f, 0.7071066f};
        assertArrayEquals(matrixR, measurements.get(0), 0.0001f);
        measurements.clear();
    }

}