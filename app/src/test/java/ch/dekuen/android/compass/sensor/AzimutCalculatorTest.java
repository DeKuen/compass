package ch.dekuen.android.compass.sensor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

@RunWith(RobolectricTestRunner.class)
public class AzimutCalculatorTest {
    private static final float G = 9.81f;
    private static final float[] ACCELERATION = {0.01f, G, G};
    private static final float[] MAGNETIC_FIELD = {1f, 1f, 1f};
    private AzimutCalculator testee;
    private AzimutListener listener;
    private ArgumentCaptor<Float> floatCaptor;
    private ArgumentCaptor<Boolean> booleanCaptor;

    @Before
    public void before() {
        listener = mock(AzimutListener.class);
        floatCaptor = ArgumentCaptor.forClass(Float.class);
        booleanCaptor = ArgumentCaptor.forClass(Boolean.class);
        testee = new AzimutCalculator(listener);
    }

    @After
    public void after() {
        verifyNoMoreInteractions(listener);
    }

    @Test
    public void onAccelerationSensorChanged_OnlyDataFromAcceleration_sendNothing() {
        Consumer<float[]> consumer = testee::onAccelerationSensorChanged;
        onSensorChanged_OnlyDataFromAcceleration_sendNothing(consumer);
    }

    @Test
    public void onMagneticSensorChanged_OnlyDataFromMagnetometer_sendNothing() {
        Consumer<float[]> consumer = testee::onMagneticSensorChanged;
        onSensorChanged_OnlyDataFromAcceleration_sendNothing(consumer);
    }

    private void onSensorChanged_OnlyDataFromAcceleration_sendNothing(Consumer<float[]> consumer) {
        // setup
        float[] updates = {0.1f, 1.2f, 2.3f};
        // act
        consumer.accept(updates);
        // assert
        verify(listener, never()).onNewAzimut(anyFloat(), anyBoolean());
    }

    @Test
    public void onAccelerationSensorChanged_AfterMagneticChanged_sendAzimut() {
        Runnable runnable = () -> {
            testee.onMagneticSensorChanged(MAGNETIC_FIELD);
            testee.onAccelerationSensorChanged(ACCELERATION);
        };
        onSensorChanged_DataFromBothSensors_sendAzimut(runnable);
    }

    @Test
    public void onMagneticSensorChanged_AfterAccelerationChanged_sendAzimut() {
        Runnable runnable = () -> {
            testee.onMagneticSensorChanged(MAGNETIC_FIELD);
            testee.onAccelerationSensorChanged(ACCELERATION);
        };
        onSensorChanged_DataFromBothSensors_sendAzimut(runnable);
    }

    private void onSensorChanged_DataFromBothSensors_sendAzimut(Runnable runnable) {
        // setup
        // act
        runnable.run();
        // assert
        verify(listener).onNewAzimut(floatCaptor.capture(), booleanCaptor.capture());
        float azimut = -1.5715171f;
        float floatCaptorValue = floatCaptor.getValue();
        assertEquals(azimut, floatCaptorValue, 0.0001f);
        boolean booleanCaptorValue = booleanCaptor.getValue();
        assertTrue(booleanCaptorValue);
    }

}