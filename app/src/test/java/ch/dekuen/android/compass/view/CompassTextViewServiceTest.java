package ch.dekuen.android.compass.view;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ch.dekuen.android.compass.view.CompassTextViewService.DECIMAL_FORMAT;

import android.view.Display;
import android.view.Surface;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CompassTextViewServiceTest {
    private CompassTextViewService testee;
    private TextView azimutTextView;
    private Display display;

    @Before
    public void before() {
        azimutTextView = mock(TextView.class);
        display = mock(Display.class);
        testee = new CompassTextViewService(display, azimutTextView);
    }

    @After
    public void after() {
        verifyNoMoreInteractions(azimutTextView);
    }

    @Test
    public void onNewAzimut_Rotation0_UpdateTextAndRotateImage() {
        onNewAzimut_WithRotation_UpdateTextAndRotateImage(Surface.ROTATION_0, 0);
    }

    @Test
    public void onNewAzimut_Rotation90_UpdateTextAndRotateImage() {
        onNewAzimut_WithRotation_UpdateTextAndRotateImage(Surface.ROTATION_90, -90);
    }

    @Test
    public void onNewAzimut_Rotation180_UpdateTextAndRotateImage() {
        onNewAzimut_WithRotation_UpdateTextAndRotateImage(Surface.ROTATION_180, 180);
    }

    @Test
    public void onNewAzimut_Rotation270_UpdateTextAndRotateImage() {
        onNewAzimut_WithRotation_UpdateTextAndRotateImage(Surface.ROTATION_270, 90);
    }

    private void onNewAzimut_WithRotation_UpdateTextAndRotateImage(int rotation, int rotationDegrees) {
        // setup
        float azimutExact = 1f;
        when(display.getRotation()).thenReturn(rotation);
        // act
        testee.onNewAzimut(azimutExact);
        // assert
        verify(display).getRotation();
        double azimutDegrees = Math.toDegrees(azimutExact) - rotationDegrees;
        String text = DECIMAL_FORMAT.format(azimutDegrees);
        verify(azimutTextView).setText(text);
    }
}