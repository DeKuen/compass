package ch.dekuen.android.compass;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static ch.dekuen.android.compass.CompassViewService.DECIMAL_FORMAT;

import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CompassViewServiceTest {
    private CompassViewService testee;
    private ImageView compassImageView;
    private TextView azimutTextView;

    @Before
    public void before() {
        compassImageView = mock(ImageView.class);
        azimutTextView = mock(TextView.class);
        testee = new CompassViewService(compassImageView, azimutTextView);
    }

    @After
    public void after() {
        verifyNoMoreInteractions(compassImageView);
        verifyNoMoreInteractions(azimutTextView);
    }

    @Test
    public void updateCompass_NewValue_UpdateTextAndRotateImage() {
        // setup
        float azimutExact = 1f;
        double azimutDegrees = Math.toDegrees(azimutExact);
        String text = DECIMAL_FORMAT.format(azimutDegrees);
        // act
        testee.updateCompass(azimutExact);
        // assert
        verify(azimutTextView).setText(text);
        verify(compassImageView).startAnimation(any(RotateAnimation.class));
    }
}