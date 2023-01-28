package ch.dekuen.android.compass.view;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class CompassImageViewServiceTest {
    private CompassImageViewService testee;
    private ImageView compassImageView;

    @Before
    public void before() {
        compassImageView = mock(ImageView.class);
        testee = new CompassImageViewService(compassImageView);
    }

    @After
    public void after() {
        verifyNoMoreInteractions(compassImageView);
    }

    @Test
    public void onNewAzimut_NewValue_UpdateTextAndRotateImage() {
        // setup
        float azimutExact = 1f;
        double azimutDegrees = Math.toDegrees(azimutExact);
        // act
        testee.onNewAzimut(azimutExact);
        // assert
        verify(compassImageView).startAnimation(any(RotateAnimation.class));
    }
}