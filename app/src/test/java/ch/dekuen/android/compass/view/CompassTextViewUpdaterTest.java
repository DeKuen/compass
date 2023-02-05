package ch.dekuen.android.compass.view;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ch.dekuen.android.compass.view.CompassTextViewUpdater.DECIMAL_FORMAT;

import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CompassTextViewUpdaterTest {
    private CompassTextViewUpdater testee;
    private CompassViewOrientationCorrector compassViewOrientationCorrector;
    private TextView azimutTextView;

    @Before
    public void before() {
        azimutTextView = mock(TextView.class);
        compassViewOrientationCorrector = mock(CompassViewOrientationCorrector.class);
        testee = new CompassTextViewUpdater(azimutTextView, compassViewOrientationCorrector);
    }

    @After
    public void after() {
        verifyNoMoreInteractions(azimutTextView);
        verifyNoMoreInteractions(compassViewOrientationCorrector);
    }

    @Test
    public void onNewAzimut_DisplayUp_UpdateText() {
        onNewAzimut_AzimutInRadians_UpdateText(true);
    }

    @Test
    public void onNewAzimut_DisplayDown_UpdateText() {
        onNewAzimut_AzimutInRadians_UpdateText(false);
    }

    private void onNewAzimut_AzimutInRadians_UpdateText(boolean isDisplayUp) {
        // setup
        float azimutRadians = (float) Math.PI / 2;
        when(compassViewOrientationCorrector.correctOrientation(azimutRadians, isDisplayUp)).thenReturn((double) azimutRadians);
        // act
        testee.onNewAzimut(azimutRadians, isDisplayUp);
        // assert
        verify(compassViewOrientationCorrector).correctOrientation(azimutRadians, isDisplayUp);
        String text = DECIMAL_FORMAT.format(90);
        verify(azimutTextView).setText(text);
    }
}