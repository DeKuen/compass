package ch.dekuen.android.compass.view;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static ch.dekuen.android.compass.view.CompassTextViewService.DECIMAL_FORMAT;

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

    @Before
    public void before() {
        azimutTextView = mock(TextView.class);
        testee = new CompassTextViewService(azimutTextView);
    }

    @After
    public void after() {
        verifyNoMoreInteractions(azimutTextView);
    }

    @Test
    public void onNewAzimut_NewValue_UpdateTextAndRotateImage() {
        // setup
        float azimutExact = 1f;
        double azimutDegrees = Math.toDegrees(azimutExact);
        String text = DECIMAL_FORMAT.format(azimutDegrees);
        // act
        testee.onNewAzimut(azimutExact);
        // assert
        verify(azimutTextView).setText(text);
    }
}