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
    public void onNewAzimut_AzimutInRadians_UpdateText() {
        // setup
        float azimutRadians = (float) Math.PI / 2;
        // act
        testee.onNewAzimut(azimutRadians);
        // assert
        String text = DECIMAL_FORMAT.format(90);
        verify(azimutTextView).setText(text);
    }
}