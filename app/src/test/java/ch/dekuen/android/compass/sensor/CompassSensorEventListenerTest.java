package ch.dekuen.android.compass.sensor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.dekuen.android.compass.AzimutListener;
import ch.dekuen.android.compass.ReflectionHelper;

@RunWith(RobolectricTestRunner.class)
public class CompassSensorEventListenerTest {
    private static final float G = 9.81f;
    private CompassSensorEventListener testee;
    private final List<Float> consumedFloats = new ArrayList<>();
    private final List<Boolean> consumedBooleans = new ArrayList<>();
    private final AzimutListener listener = (value, isPhoneFacingUp) -> {
        consumedFloats.add(value);
        consumedBooleans.add(isPhoneFacingUp);
    };

    @Before
    public void before() {
        consumedFloats.clear();
        consumedBooleans.clear();
        testee = new CompassSensorEventListener(listener);
    }

    @After
    public void after() {
        assertTrue(consumedFloats.isEmpty());
        assertTrue(consumedBooleans.isEmpty());
    }

    @Test
    public void onAccuracyChanged_AnyInput_consumeNothing() {
        // setup
        Sensor sensor = mock(Sensor.class);
        // act
        testee.onAccuracyChanged(sensor, -99);
        // assert
        verifyNoMoreInteractions(sensor);
    }

    @Test
    public void onSensorChanged_NullEvent_consumeNothing() {
        // setup
        // act & assert
        assertDoesNotThrow(() -> testee.onSensorChanged(null));
    }

    @Test
    public void onSensorChanged_NullSensor_consumeNothing() {
        // setup
        SensorEvent event = mock(SensorEvent.class);
        // act
        testee.onSensorChanged(event);
        // assert
        verifyNoMoreInteractions(event);
    }

    @Test
    public void onSensorChanged_UnknownSensorType_consumeNothing() {
        // setup
        Sensor sensor = mock(Sensor.class);
        SensorEvent event = mockEvent(sensor, -99, null);
        // act
        testee.onSensorChanged(event);
        //
        verifySensorEvent(event);
    }

    @Test
    public void onSensorChanged_OnlyDataFromAcceleration_consumeNothing() {
        onSensorChanged_OnlyDataFromOneSensor_consumeNothing(Sensor.TYPE_ACCELEROMETER);
    }

    @Test
    public void onSensorChanged_OnlyDataFromMagnetometer_consumeNothing() {
        onSensorChanged_OnlyDataFromOneSensor_consumeNothing(Sensor.TYPE_MAGNETIC_FIELD);
    }

    private void onSensorChanged_OnlyDataFromOneSensor_consumeNothing(int type) {
        // setup
        Sensor sensor = mock(Sensor.class);
        float[] values = new float[0];
        SensorEvent event = mockEvent(sensor, type, values);
        // act
        testee.onSensorChanged(event);
        //
        verifySensorEvent(event);
    }

    @Test
    public void onSensorChanged_getRotationMatrixFailed_consumeNothing() {
        // setup
        float[] acceleration = new float[3];
        float[] magneticField = new float[3];
        SensorEvent accelerometerEvent = mockEvent(mock(Sensor.class), Sensor.TYPE_ACCELEROMETER, acceleration);
        SensorEvent magnetometerEvent = mockEvent(mock(Sensor.class), Sensor.TYPE_MAGNETIC_FIELD, magneticField);
        // act
        testee.onSensorChanged(accelerometerEvent);
        testee.onSensorChanged(magnetometerEvent);
        // assert
        verifySensorEvent(accelerometerEvent);
        verifySensorEvent(magnetometerEvent);
    }

    @Test
    public void onSensorChanged_getRotationMatrixSuccess_consumeAzimut() {
        // setup
        float[] acceleration = {0.01f, G, G};
        float[] magneticField = {1f, 1f, 1f};
        SensorEvent accelerometerEvent = mockEvent(mock(Sensor.class), Sensor.TYPE_ACCELEROMETER, acceleration);
        SensorEvent magnetometerEvent = mockEvent(mock(Sensor.class), Sensor.TYPE_MAGNETIC_FIELD, magneticField);
        // act
        testee.onSensorChanged(accelerometerEvent);
        testee.onSensorChanged(magnetometerEvent);
        // assert
        verifySensorEvent(accelerometerEvent);
        verifySensorEvent(magnetometerEvent);
        assertEquals(1, consumedFloats.size());
        float azimut = -0.047145467f;
        assertEquals(azimut, consumedFloats.get(0), 0.0001f);
        consumedFloats.clear();
        assertEquals(Arrays.asList(true), consumedBooleans);
        consumedBooleans.clear();
    }

    @Test
    public void onSensorChanged_testLowPassFilterWithTwoAzimuts_consumeAzimut() {
        // setup
        float[] acceleration = {0.01f, G, G};
        float[] magneticField = {1f, 1f, 1f};
        SensorEvent accelerometerEvent = mockEvent(mock(Sensor.class), Sensor.TYPE_ACCELEROMETER, acceleration);
        SensorEvent magnetometerEvent = mockEvent(mock(Sensor.class), Sensor.TYPE_MAGNETIC_FIELD, magneticField);
        SensorEvent magnetometerEvent2 = mockEvent(mock(Sensor.class), Sensor.TYPE_MAGNETIC_FIELD, magneticField);
        // act
        testee.onSensorChanged(accelerometerEvent);
        testee.onSensorChanged(magnetometerEvent);
        testee.onSensorChanged(magnetometerEvent2);
        // assert
        verifySensorEvent(accelerometerEvent);
        verifySensorEvent(magnetometerEvent);
        verifySensorEvent(magnetometerEvent2);
        assertEquals(2, consumedFloats.size());
        float azimut = -0.047145467f;
        assertEquals(azimut, consumedFloats.get(0), 0.0001f);
        assertEquals(-0.09287657f, consumedFloats.get(1), 0.0001f);
        consumedFloats.clear();
        assertEquals(Arrays.asList(true, true), consumedBooleans);
        consumedBooleans.clear();
    }

    private static SensorEvent mockEvent(Sensor sensor, int type, float[] values) {
        SensorEvent event = mock(SensorEvent.class);
        when(sensor.getType()).thenReturn(type);
        event.sensor = sensor;
        ReflectionHelper.setFieldValue(event, "values", values);
        return event;
    }

    private void verifySensorEvent(SensorEvent event) {
        Sensor sensor = event.sensor;
        verify(sensor).getType();
        verifyNoMoreInteractions(sensor);
        verifyNoMoreInteractions(event);
    }
}