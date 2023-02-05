package ch.dekuen.android.compass.sensor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

import java.util.function.Consumer;

import ch.dekuen.android.compass.ReflectionHelper;

@RunWith(RobolectricTestRunner.class)
public class CompassSensorEventListenerTest {
    public static final float[] VALUES = {0.1f, 1.2f, 2.3f};
    public static final int SENSOR_TYPE = -99;
    private CompassSensorEventListener testee;
    private float[] measurements;
    private final Consumer<float[]> consumer = floats -> measurements = floats;

    @Before
    public void before() {
        measurements = null;
        testee = new CompassSensorEventListener(consumer, SENSOR_TYPE);
    }

    @After
    public void after() {
        assertNull(measurements);
    }

    @Test
    public void onAccuracyChanged_AnyInput_consumeNothing() {
        // setup
        Sensor sensor = mock(Sensor.class);
        // act
        testee.onAccuracyChanged(sensor, SENSOR_TYPE);
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
        SensorEvent event = mockEvent(sensor, 7, null);
        // act
        testee.onSensorChanged(event);
        // assert
        verifySensorEvent(event);
    }

    @Test
    public void onSensorChanged_DataFromSensor_consumeData() {
        // setup
        Sensor sensor = mock(Sensor.class);
        SensorEvent event = mockEvent(sensor, SENSOR_TYPE, VALUES);
        // act
        testee.onSensorChanged(event);
        // assert
        verifySensorEvent(event);
        assertEquals(VALUES, measurements);
        measurements = null;
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