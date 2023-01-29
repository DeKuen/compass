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
    private CompassSensorEventListener testee;
    private float[] accelerationMeasurements;
    private float[] magneticMeasurements;
    private final Consumer<float[]> accelerationConsumer = floats -> accelerationMeasurements = floats;
    private final Consumer<float[]> magneticConsumer = floats -> magneticMeasurements = floats;

    @Before
    public void before() {
        accelerationMeasurements = null;
        magneticMeasurements = null;
        testee = new CompassSensorEventListener(accelerationConsumer, magneticConsumer);
    }

    @After
    public void after() {
        assertNull(accelerationMeasurements);
        assertNull(magneticMeasurements);
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
    public void onSensorChanged_DataFromAcceleration_consumeAcceleration() {
        onSensorChanged_DataFromOneSensor_consumeData(Sensor.TYPE_ACCELEROMETER);
        // assert
        assertEquals(VALUES, accelerationMeasurements);
        accelerationMeasurements = null;
    }

    @Test
    public void onSensorChanged_DataFromMagnetometer_consumeMagnetic() {
        onSensorChanged_DataFromOneSensor_consumeData(Sensor.TYPE_MAGNETIC_FIELD);
        // assert
        assertEquals(VALUES, magneticMeasurements);
        magneticMeasurements = null;
    }

    private void onSensorChanged_DataFromOneSensor_consumeData(int type) {
        // setup
        Sensor sensor = mock(Sensor.class);
        SensorEvent event = mockEvent(sensor, type, VALUES);
        // act
        testee.onSensorChanged(event);
        //
        verifySensorEvent(event);
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