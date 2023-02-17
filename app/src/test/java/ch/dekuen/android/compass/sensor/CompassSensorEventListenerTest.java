package ch.dekuen.android.compass.sensor;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Handler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import java.util.List;
import java.util.function.Consumer;

import ch.dekuen.android.compass.ReflectionHelper;

@RunWith(RobolectricTestRunner.class)
public class CompassSensorEventListenerTest {
    public static final float[] VALUES = {0.0f, 1.2f, 2.3f};
    public static final int SENSOR_TYPE = -99;
    private CompassSensorEventListener testee;
    private Handler handler;
    private ArgumentCaptor<Runnable> runnableCaptor;
    private float[] consumedValues;
    private final Consumer<float[]> consumer = floats -> consumedValues = floats;

    @Before
    public void before() {
        handler = mock(Handler.class);
        runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
        consumedValues = null;
    }

    @After
    public void after() {
        verifyNoMoreInteractions(handler);
        assertNull(consumedValues);
    }

    @Test
    public void onAccuracyChanged_AnyInput_consumeNothing() {
        // setup
        testee = new CompassSensorEventListener(handler, consumer, SENSOR_TYPE, 0f);
        Sensor sensor = mock(Sensor.class);
        // act
        testee.onAccuracyChanged(sensor, SENSOR_TYPE);
        // assert
        verifyNoMoreInteractions(sensor);
    }

    @Test
    public void onSensorChanged_NullEvent_consumeNothing() {
        // setup
        testee = new CompassSensorEventListener(handler, consumer, SENSOR_TYPE, 0f);
        // act & assert
        assertDoesNotThrow(() -> testee.onSensorChanged(null));
    }

    @Test
    public void onSensorChanged_NullSensor_consumeNothing() {
        // setup
        testee = new CompassSensorEventListener(handler, consumer, SENSOR_TYPE, 0f);
        SensorEvent event = mock(SensorEvent.class);
        // act
        testee.onSensorChanged(event);
        // assert
        verifyNoMoreInteractions(event);
    }

    @Test
    public void onSensorChanged_UnknownSensorType_consumeNothing() {
        // setup
        testee = new CompassSensorEventListener(handler, consumer, SENSOR_TYPE, 0f);
        Sensor sensor = mock(Sensor.class);
        SensorEvent event = mockEvent(sensor, 7, null);
        // act
        testee.onSensorChanged(event);
        // assert
        verifySensorEvent(event);
    }

    @Test
    public void onSensorChanged_FirstUpdate_consumeData() {
        // setup
        testee = new CompassSensorEventListener(handler, consumer, SENSOR_TYPE, 0f);
        Sensor sensor = mock(Sensor.class);
        SensorEvent event = mockEvent(sensor, SENSOR_TYPE, VALUES);
        // act
        testee.onSensorChanged(event);
        // assert
        verifySensorEvent(event);
        verify(handler).post(runnableCaptor.capture());
        Runnable runnable = runnableCaptor.getValue();
        runnable.run();
        assertArrayEquals(VALUES, consumedValues);
        consumedValues = null;
    }

    @Test
    public void onSensorChanged_TwoUpdates_ConsumeFilteredData() {
        // setup
        float alpha = 0.75f;
        testee = new CompassSensorEventListener(handler, consumer, SENSOR_TYPE, alpha);
        float[] updates0 = {0f, 2f, 3f};
        float[] updates1 = {-0f, -2f, -3f};
        Sensor sensor = mock(Sensor.class);
        SensorEvent event0 = mockEvent(sensor, SENSOR_TYPE, updates0);
        SensorEvent event1 = mockEvent(sensor, SENSOR_TYPE, updates1);
        // act
        testee.onSensorChanged(event0);
        testee.onSensorChanged(event1);
        // assert
        verify(handler, times(2)).post(runnableCaptor.capture());
        List<Runnable> runnable = runnableCaptor.getAllValues();
        assertEquals(2, runnable.size());
        runnable.get(0).run();
        assertArrayEquals(updates0, consumedValues);
        runnable.get(1).run();
        float[] expected1 = { 0f, 1f, 1.5f };
        assertArrayEquals(expected1, consumedValues);
        consumedValues = null;
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