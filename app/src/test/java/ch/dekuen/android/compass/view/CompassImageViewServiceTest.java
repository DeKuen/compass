package ch.dekuen.android.compass.view;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ch.dekuen.android.compass.ReflectionHelper.getFieldValue;

import android.view.Display;
import android.view.Surface;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricTestRunner;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import ch.dekuen.android.compass.ReflectionHelper;

@RunWith(RobolectricTestRunner.class)
public class CompassImageViewServiceTest {
    private CompassImageViewService testee;
    private ImageView compassImageView;
    private Display display;
    private ArgumentCaptor<RotateAnimation> rotateAnimationCaptor;

    @Before
    public void before() {
        compassImageView = mock(ImageView.class);
        display = mock(Display.class);
        testee = new CompassImageViewService(display, compassImageView);
        rotateAnimationCaptor = ArgumentCaptor.forClass(RotateAnimation.class);
    }

    @After
    public void after() {
        verifyNoMoreInteractions(compassImageView);
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

    @Test
    public void onNewAzimut_CalledStillRotating_UpdateTextAndRotateImage() {
        // setup
        int rotation = Surface.ROTATION_0;
        int rotationDegrees = 0;
        float azimutExact = 1f;
        when(display.getRotation()).thenReturn(rotation);
        // act
        testee.onNewAzimut(azimutExact);
        testee.onNewAzimut(azimutExact);
        // assert
        verify(display).getRotation();
        verify(compassImageView).startAnimation(rotateAnimationCaptor.capture());
        RotateAnimation rotateAnimation = rotateAnimationCaptor.getValue();
        float azimutDegrees = (float) (Math.toDegrees(azimutExact) - rotationDegrees);
        validateRotationAnimation(rotateAnimation, 0f, -azimutDegrees);
    }

    @Test
    public void onNewAzimut_CalledAfterRotating_UpdateTextAndRotateImage() {
        // setup
        int rotation = Surface.ROTATION_0;
        int rotationDegrees = 0;
        float azimutExact = 1f;
        when(display.getRotation()).thenReturn(rotation);
        // act
        testee.onNewAzimut(azimutExact);
        ReflectionHelper.setFieldValue(testee, "isRotating", new AtomicBoolean(false));
        testee.onNewAzimut(azimutExact);
        // assert
        verify(display, times(2)).getRotation();
        verify(compassImageView, times(2)).startAnimation(rotateAnimationCaptor.capture());
        List<RotateAnimation> rotateAnimations = rotateAnimationCaptor.getAllValues();
        assertEquals(2, rotateAnimations.size());
        float azimutDegrees = (float) (Math.toDegrees(azimutExact) - rotationDegrees);
        RotateAnimation rotateAnimation = rotateAnimations.get(0);
        validateRotationAnimation(rotateAnimation, 0f, -azimutDegrees);
        rotateAnimation = rotateAnimations.get(1);
        validateRotationAnimation(rotateAnimation, -azimutDegrees, -azimutDegrees);
    }

    private void onNewAzimut_WithRotation_UpdateTextAndRotateImage(int rotation, int rotationDegrees) {
        // setup
        float azimutExact = 1f;
        when(display.getRotation()).thenReturn(rotation);
        // act
        testee.onNewAzimut(azimutExact);
        // assert
        verify(display).getRotation();
        verify(compassImageView).startAnimation(rotateAnimationCaptor.capture());
        RotateAnimation rotateAnimation = rotateAnimationCaptor.getValue();
        double azimutDegrees = Math.toDegrees(azimutExact) - rotationDegrees;
        validateRotationAnimation(rotateAnimation, 0f, (float) -azimutDegrees);
    }

    private void validateRotationAnimation(RotateAnimation rotateAnimation, float fromDegrees, float toDegrees) {
        float mFromDegrees = (float) getFieldValue(rotateAnimation, "mFromDegrees");
        assertEquals(fromDegrees, mFromDegrees, 0f);
        float mToDegrees = (float) getFieldValue(rotateAnimation, "mToDegrees");
        assertEquals(toDegrees, mToDegrees, 0f);
        int pivotXType = (int) getFieldValue(rotateAnimation, "mPivotXType");
        assertEquals(Animation.RELATIVE_TO_SELF, pivotXType);
        float pivotXValue = (float) getFieldValue(rotateAnimation, "mPivotXValue");
        assertEquals(0.5f, pivotXValue, 0f);
        int pivotYType = (int) getFieldValue(rotateAnimation, "mPivotYType");
        assertEquals(Animation.RELATIVE_TO_SELF, pivotYType);
        float pivotYValue = (float) getFieldValue(rotateAnimation, "mPivotYValue");
        assertEquals(0.5f, pivotYValue, 0f);
    }
}