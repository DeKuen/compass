package ch.dekuen.android.compassapp.listener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.hardware.Sensor;

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

    // @Test
    // void
}