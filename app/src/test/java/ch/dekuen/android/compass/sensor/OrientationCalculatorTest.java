package ch.dekuen.android.compass.sensor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.view.Display;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import ch.dekuen.android.compass.AzimutListener;

@RunWith(RobolectricTestRunner.class)
public class OrientationCalculatorTest {
    private OrientationCalculator testee;
    private AzimutListener listener;
    private Display display;
    private ArgumentCaptor<Float> floatCaptor;

    @Before
    public void before() {
        listener = mock(AzimutListener.class);
        display = mock(Display.class);
        floatCaptor = ArgumentCaptor.forClass(Float.class);
        testee = new OrientationCalculator(listener, display);
    }

    @After
    public void after() {
        verifyNoMoreInteractions(listener);
        verifyNoMoreInteractions(display);
    }

    @Test
    public void calculate_MatrixR_Azimut() {
        // setup
        float[] matrixR = {0.0f, -0.7071068f, 0.7071068f, 0.9999998f, -0.00050968386f, -0.00050968386f, 0.00072080176f, 0.7071066f, 0.7071066f};
        // act
        testee.calculate(matrixR);
        // assert
        verify(listener).onNewAzimut(floatCaptor.capture());
        float azimut = -1.5715171f;
        float floatCaptorValue = floatCaptor.getValue();
        assertEquals(azimut, floatCaptorValue, 0.0001f);
    }
}