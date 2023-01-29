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
    public static final float AZIMUT_EXACT = 0f;
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
    public void onNewAzimut_Rotation0_UpdateText() {
        int rotation = Surface.ROTATION_0;
        int rotationDegrees = 0;
        boolean isPhoneFacingUp = true;
        double expectedDegrees = (Math.toDegrees(AZIMUT_EXACT) + rotationDegrees + 360) % 360;
        onNewAzimut_WithRotation_UpdateText(rotation, isPhoneFacingUp, expectedDegrees);
    }

    @Test
    public void onNewAzimut_Rotation0UpsideDown_UpdateText() {
        int rotation = Surface.ROTATION_0;
        int rotationDegrees = 0;
        boolean isPhoneFacingUp = false;
        double expectedDegrees = (Math.toDegrees(AZIMUT_EXACT) + rotationDegrees + 180 + 360) % 360;
        onNewAzimut_WithRotation_UpdateText(rotation, isPhoneFacingUp, expectedDegrees);
    }

    @Test
    public void onNewAzimut_Rotation90_UpdateText() {
        int rotation = Surface.ROTATION_90;
        int rotationDegrees = 90;
        boolean isPhoneFacingUp = true;
        double expectedDegrees = (Math.toDegrees(AZIMUT_EXACT) + rotationDegrees + 360) % 360;
        onNewAzimut_WithRotation_UpdateText(rotation, isPhoneFacingUp, expectedDegrees);
    }

    @Test
    public void onNewAzimut_Rotation90UpsideDown_UpdateText() {
        int rotation = Surface.ROTATION_90;
        int rotationDegrees = 90;
        boolean isPhoneFacingUp = false;
        double expectedDegrees = (Math.toDegrees(AZIMUT_EXACT) + rotationDegrees + 360) % 360;
        onNewAzimut_WithRotation_UpdateText(rotation, isPhoneFacingUp, expectedDegrees);
    }

    @Test
    public void onNewAzimut_Rotation180_UpdateText() {
        int rotation = Surface.ROTATION_180;
        int rotationDegrees = 180;
        boolean isPhoneFacingUp = true;
        double expectedDegrees = (Math.toDegrees(AZIMUT_EXACT) + rotationDegrees + 360) % 360;
        onNewAzimut_WithRotation_UpdateText(rotation, isPhoneFacingUp, expectedDegrees);
    }

    @Test
    public void onNewAzimut_Rotation180UpsideDown_UpdateText() {
        int rotation = Surface.ROTATION_180;
        int rotationDegrees = 180;
        boolean isPhoneFacingUp = false;
        double expectedDegrees = (Math.toDegrees(AZIMUT_EXACT) + rotationDegrees + 180 + 360) % 360;
        onNewAzimut_WithRotation_UpdateText(rotation, isPhoneFacingUp, expectedDegrees);
    }

    @Test
    public void onNewAzimut_Rotation270_UpdateText() {
        int rotation = Surface.ROTATION_270;
        int rotationDegrees = 270;
        boolean isPhoneFacingUp = true;
        double expectedDegrees = (Math.toDegrees(AZIMUT_EXACT) + rotationDegrees + 360) % 360;
        onNewAzimut_WithRotation_UpdateText(rotation, isPhoneFacingUp, expectedDegrees);
    }

    @Test
    public void onNewAzimut_Rotation270UpsideDown_UpdateText() {
        int rotation = Surface.ROTATION_270;
        int rotationDegrees = 270;
        boolean isPhoneFacingUp = false;
        double expectedDegrees = (Math.toDegrees(AZIMUT_EXACT) + rotationDegrees + 360) % 360;
        onNewAzimut_WithRotation_UpdateText(rotation, isPhoneFacingUp, expectedDegrees);
    }

    private void onNewAzimut_WithRotation_UpdateText(int rotation, boolean isPhoneFacingUp, double expectedDegrees) {
        // setup
        when(display.getRotation()).thenReturn(rotation);
        // act
        testee.onNewAzimut(AZIMUT_EXACT, isPhoneFacingUp);
        // assert
        verify(display).getRotation();
        String text = DECIMAL_FORMAT.format(expectedDegrees);
        verify(azimutTextView).setText(text);
    }
}