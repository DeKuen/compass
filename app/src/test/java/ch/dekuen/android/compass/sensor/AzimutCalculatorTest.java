package ch.dekuen.android.compass.sensor;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import java.util.function.Consumer;

import ch.dekuen.android.compass.AzimutListener;

@RunWith(RobolectricTestRunner.class)
public class AzimutCalculatorTest {
    private static final float G = 9.81f;
    private static final float[] ACCELERATION_FLAT_ZERO = {0f, 0f, G};
    private static final float[] MAGNETIC_FIELD_FLAT_NORTH = {0f, 1f, -1f};
    private static final float[] MAGNETIC_FIELD_FLAT_NORTH_EAST = {1f, 1f, -1f};
    private static final float[] MAGNETIC_FIELD_FLAT_SOUTH = {0f, -1f, -1f};
    private static final float[] MAGNETIC_FIELD_FLAT_SOUTH_EAST = {-1f, -1f, -1f};
    private static final float[] MAGNETIC_FIELD_FLAT_SOUTH_WEST = {1f, -1f, -1f};
    private AzimutCalculator testee;
    private AzimutListener listener;
    private ArgumentCaptor<Float> floatCaptor;

    @Before
    public void before() {
        listener = mock(AzimutListener.class);
        testee = new AzimutCalculator(listener);
        floatCaptor = ArgumentCaptor.forClass(Float.class);
    }

    @After
    public void after() {
        verifyNoMoreInteractions(listener);
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
        verify(listener, never()).onNewAzimut(anyFloat());
    }

    @Test
    public void onAccelerationSensorChanged_AfterMagneticChanged_SendNorthAzimut() {
        Runnable runnable = () -> {
            testee.onAccelerationSensorChanged(ACCELERATION_FLAT_ZERO);
            testee.onMagneticSensorChanged(MAGNETIC_FIELD_FLAT_NORTH);
        };
        float azimutExpected = 0f;
        onSensorChanged_DataFromBothSensors_SendAzimut(runnable, azimutExpected);
    }

    @Test
    public void onMagneticSensorChanged_AfterAccelerationChanged_sendNorthAzimut() {
        Runnable runnable = () -> {
            testee.onMagneticSensorChanged(MAGNETIC_FIELD_FLAT_NORTH);
            testee.onAccelerationSensorChanged(ACCELERATION_FLAT_ZERO);
        };
        float azimutExpected = 0f;
        onSensorChanged_DataFromBothSensors_SendAzimut(runnable, azimutExpected);
    }

    @Test
    public void onMagneticSensorChanged_FlatSouth_sendSouthAzimut() {
        Runnable runnable = () -> {
            testee.onMagneticSensorChanged(MAGNETIC_FIELD_FLAT_SOUTH);
            testee.onAccelerationSensorChanged(ACCELERATION_FLAT_ZERO);
        };
        float azimutExpected = (float) -Math.PI;
        onSensorChanged_DataFromBothSensors_SendAzimut(runnable, azimutExpected);
    }

    @Test
    public void onMagneticSensorChanged_FlatSouthEast_sendSouthEastAzimut() {
        Runnable runnable = () -> {
            testee.onMagneticSensorChanged(MAGNETIC_FIELD_FLAT_SOUTH_EAST);
            testee.onAccelerationSensorChanged(ACCELERATION_FLAT_ZERO);
        };
        float azimutExpected = (float) Math.PI * 3 / 4;
        onSensorChanged_DataFromBothSensors_SendAzimut(runnable, azimutExpected);
    }

    @Test
    public void onMagneticSensorChanged_FlatSouthWest_sendSouthWestAzimut() {
        Runnable runnable = () -> {
            testee.onMagneticSensorChanged(MAGNETIC_FIELD_FLAT_SOUTH_WEST);
            testee.onAccelerationSensorChanged(ACCELERATION_FLAT_ZERO);
        };
        float azimutExpected = (float) - Math.PI * 3 / 4;
        onSensorChanged_DataFromBothSensors_SendAzimut(runnable, azimutExpected);
    }

    @Test
    public void onMagneticSensorChanged_FlatNorthEast_sendNorthEastAzimut() {
        Runnable runnable = () -> {
            testee.onMagneticSensorChanged(MAGNETIC_FIELD_FLAT_NORTH_EAST);
            testee.onAccelerationSensorChanged(ACCELERATION_FLAT_ZERO);
        };
        float azimutExpected = (float) -Math.PI / 4;
        onSensorChanged_DataFromBothSensors_SendAzimut(runnable, azimutExpected);
    }

    @Test
    public void onMagneticSensorChanged_FailedToCalcRotationMatrix_SendNothing() {
        // setup
        float[] acceleration = {0f, 0f, 0f};
        // act
        testee.onMagneticSensorChanged(MAGNETIC_FIELD_FLAT_NORTH);
        testee.onAccelerationSensorChanged(acceleration);
        // assert
        verify(listener, never()).onNewAzimut(anyFloat());
    }

    private void onSensorChanged_DataFromBothSensors_SendAzimut(Runnable runnable, float azimutExpected) {
        // setup
        // act
        runnable.run();
        // assert
        verify(listener).onNewAzimut(floatCaptor.capture());
        float floatCaptorValue = floatCaptor.getValue();
        assertEquals(azimutExpected, floatCaptorValue, 0.00001f);
    }

}