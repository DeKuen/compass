package ch.dekuen.android.compassapp.listener;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

@ExtendWith(MockitoExtension.class)
class CompassEventListenerTest {

    private CompassEventListener testee;
    @Mock
    Consumer<Float> floatConsumer;

    @BeforeEach
    public void beforeEach() {
        testee = new CompassEventListener(floatConsumer);
    }

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(floatConsumer);
    }

    @Test
    void onAccuracyChanged_AnyInput_consumeNothing() {
        // setup
        Sensor sensor = mock(Sensor.class);
        // act
        testee.onAccuracyChanged(sensor, -99);
        // assert
        verifyNoMoreInteractions(sensor);
    }

    @Test
    void onSensorChanged_NullEvent_consumeNothing() {
        // setup
        // act & assert
        assertDoesNotThrow(() -> testee.onSensorChanged(null));
    }

    /*
    @Test
    void onSensorChanged_NullSensor_consumeNothing() {
        // setup
        SensorEvent event = new SensorEvent(null, 0, 0, null);
        // act & assert
        assertDoesNotThrow(() -> testee.onSensorChanged(event));
    }
    */

    @Test
    void onSensorChanged_UnknownSensorType_consumeNothing() {
        // setup
        Sensor sensor = mock(Sensor.class);
        when(sensor.getType()).thenReturn(-99);
        SensorEvent event = mock(SensorEvent.class);
        // when(event.sensor).thenReturn(sensor);
        event.sensor = sensor;
        assertDoesNotThrow(() -> testee.onSensorChanged(event));
    }
}