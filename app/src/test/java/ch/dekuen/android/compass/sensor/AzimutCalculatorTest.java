package ch.dekuen.android.compass.sensor;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
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
import ch.dekuen.android.compass.MathConstants;

@RunWith(RobolectricTestRunner.class)
public class AzimutCalculatorTest {
    private static final float G = 9.81f;
    public static final float AZIMUT_NORTH = 0f;
    private static final float[] ACCELERATION_FLAT_ZERO = {AZIMUT_NORTH, AZIMUT_NORTH, G};
    private static final float[] ACCELERATION_FLAT_DISPLAY_DOWN = {AZIMUT_NORTH, AZIMUT_NORTH, -G};
    private static final float[] MAGNETIC_FIELD_FLAT_NORTH = {AZIMUT_NORTH, 1f, -1f};
    private static final float[] MAGNETIC_FIELD_FLAT_NORTH_EAST = {1f, 1f, -1f};
    private static final float[] MAGNETIC_FIELD_FLAT_SOUTH = {AZIMUT_NORTH, -1f, -1f};
    private static final float[] MAGNETIC_FIELD_FLAT_SOUTH_EAST = {-1f, -1f, -1f};
    private static final float[] MAGNETIC_FIELD_FLAT_SOUTH_WEST = {1f, -1f, -1f};
    private static final float AZIMUT_SOUTH_WEST = (float) -MathConstants.PI * 3 / 4;
    private static final float AZIMUT_SOUTH_EAST = (float) MathConstants.PI * 3 / 4;
    private static final float AZIMUT_NORTH_EAST = (float) -MathConstants.PI / 4;
    private static final float AZIMUT_SOUTH = (float) -MathConstants.PI;
    private AzimutCalculator testee;
    private AzimutListener listener;
    private ArgumentCaptor<Float> floatCaptor;
    private ArgumentCaptor<Boolean> booleanCaptor;

    @Before
    public void before() {
        listener = mock(AzimutListener.class);
        testee = new AzimutCalculator(listener);
        floatCaptor = ArgumentCaptor.forClass(Float.class);
        booleanCaptor = ArgumentCaptor.forClass(Boolean.class);
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
        verify(listener, never()).onNewAzimut(anyFloat(), anyBoolean());
    }

    @Test
    public void onAccelerationSensorChanged_AfterMagneticChanged_SendNorthAzimut() {
        Runnable runnable = () -> {
            testee.onAccelerationSensorChanged(ACCELERATION_FLAT_ZERO);
            testee.onMagneticSensorChanged(MAGNETIC_FIELD_FLAT_NORTH);
        };
        onSensorChanged_DataFromBothSensors_SendAzimut(runnable, AZIMUT_NORTH, true);
    }

    @Test
    public void onMagneticSensorChanged_AfterAccelerationChanged_sendNorthAzimut() {
        Runnable runnable = () -> {
            testee.onMagneticSensorChanged(MAGNETIC_FIELD_FLAT_NORTH);
            testee.onAccelerationSensorChanged(ACCELERATION_FLAT_ZERO);
        };
        onSensorChanged_DataFromBothSensors_SendAzimut(runnable, AZIMUT_NORTH, true);
    }

    @Test
    public void onMagneticSensorChanged_FlatSouth_sendSouthAzimut() {
        Runnable runnable = () -> {
            testee.onMagneticSensorChanged(MAGNETIC_FIELD_FLAT_SOUTH);
            testee.onAccelerationSensorChanged(ACCELERATION_FLAT_ZERO);
        };
        onSensorChanged_DataFromBothSensors_SendAzimut(runnable, AZIMUT_SOUTH, true);
    }

    @Test
    public void onMagneticSensorChanged_FlatSouthEast_sendSouthEastAzimut() {
        Runnable runnable = () -> {
            testee.onMagneticSensorChanged(MAGNETIC_FIELD_FLAT_SOUTH_EAST);
            testee.onAccelerationSensorChanged(ACCELERATION_FLAT_ZERO);
        };
        onSensorChanged_DataFromBothSensors_SendAzimut(runnable, AZIMUT_SOUTH_EAST, true);
    }

    @Test
    public void onMagneticSensorChanged_FlatSouthEastAndDisplayDown_sendSouthWestAzimut() {
        Runnable runnable = () -> {
            testee.onMagneticSensorChanged(MAGNETIC_FIELD_FLAT_SOUTH_EAST);
            testee.onAccelerationSensorChanged(ACCELERATION_FLAT_DISPLAY_DOWN);
        };
        onSensorChanged_DataFromBothSensors_SendAzimut(runnable, AZIMUT_SOUTH_WEST, false);
    }

    @Test
    public void onMagneticSensorChanged_FlatSouthWest_sendSouthWestAzimut() {
        Runnable runnable = () -> {
            testee.onMagneticSensorChanged(MAGNETIC_FIELD_FLAT_SOUTH_WEST);
            testee.onAccelerationSensorChanged(ACCELERATION_FLAT_ZERO);
        };
        onSensorChanged_DataFromBothSensors_SendAzimut(runnable, AZIMUT_SOUTH_WEST, true);
    }

    @Test
    public void onMagneticSensorChanged_FlatNorthEast_sendNorthEastAzimut() {
        Runnable runnable = () -> {
            testee.onMagneticSensorChanged(MAGNETIC_FIELD_FLAT_NORTH_EAST);
            testee.onAccelerationSensorChanged(ACCELERATION_FLAT_ZERO);
        };
        onSensorChanged_DataFromBothSensors_SendAzimut(runnable, AZIMUT_NORTH_EAST, true);
    }

    @Test
    public void onMagneticSensorChanged_FailedToCalcRotationMatrix_SendNothing() {
        // setup
        float[] acceleration = {AZIMUT_NORTH, AZIMUT_NORTH, AZIMUT_NORTH};
        // act
        testee.onMagneticSensorChanged(MAGNETIC_FIELD_FLAT_NORTH);
        testee.onAccelerationSensorChanged(acceleration);
        // assert
        verify(listener, never()).onNewAzimut(anyFloat(), anyBoolean());
    }

    private void onSensorChanged_DataFromBothSensors_SendAzimut(Runnable runnable, float azimutExpected, boolean expectedIsDisplayUp) {
        // setup
        // act
        runnable.run();
        // assert
        verify(listener).onNewAzimut(floatCaptor.capture(), booleanCaptor.capture());
        float floatCaptorValue = floatCaptor.getValue();
        assertEquals(azimutExpected, floatCaptorValue, 0.00001f);
        Boolean booleanCaptorValue = booleanCaptor.getValue();
        assertEquals(expectedIsDisplayUp, booleanCaptorValue);
    }

}