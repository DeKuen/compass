package ch.dekuen.android.compass.view;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ch.dekuen.android.compass.ReflectionHelper.getFieldValue;

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

import ch.dekuen.android.compass.MathConstants;
import ch.dekuen.android.compass.ReflectionHelper;

@RunWith(RobolectricTestRunner.class)
public class CompassImageViewUpdaterTest {
    private CompassImageViewUpdater testee;
    private ImageView compassImageView;
    private CompassViewOrientationCorrector compassViewOrientationCorrector;
    private ArgumentCaptor<RotateAnimation> rotateAnimationCaptor;

    @Before
    public void before() {
        compassImageView = mock(ImageView.class);
        compassViewOrientationCorrector = mock(CompassViewOrientationCorrector.class);
        testee = new CompassImageViewUpdater(compassImageView, compassViewOrientationCorrector);
        rotateAnimationCaptor = ArgumentCaptor.forClass(RotateAnimation.class);
    }

    @After
    public void after() {
        verifyNoMoreInteractions(compassImageView);
        verifyNoMoreInteractions(compassViewOrientationCorrector);
    }

    @Test
    public void onNewAzimut_AzimutInRadians_RotateImage() {
        // setup
        float azimutRadians = (float) MathConstants.PI_HALF;
        boolean isDisplayUp = true;
        when(compassViewOrientationCorrector.correctOrientation(azimutRadians, isDisplayUp)).thenReturn((double) azimutRadians);
        // act
        testee.onNewAzimut(azimutRadians, isDisplayUp);
        // assert
        verify(compassViewOrientationCorrector).correctOrientation(azimutRadians, isDisplayUp);
        verify(compassImageView).startAnimation(rotateAnimationCaptor.capture());
        RotateAnimation rotateAnimation = rotateAnimationCaptor.getValue();
        double azimutDegrees = Math.toDegrees(azimutRadians);
        validateRotationAnimation(rotateAnimation, 0f, (float) -azimutDegrees);
    }

    @Test
    public void onNewAzimut_DisplayDown_RotateImage() {
        // setup
        float azimutRadians = (float) MathConstants.PI_HALF;
        boolean isDisplayUp = false;
        when(compassViewOrientationCorrector.correctOrientation(azimutRadians, isDisplayUp)).thenReturn((double) -azimutRadians);
        // act
        testee.onNewAzimut(azimutRadians, isDisplayUp);
        // assert
        verify(compassViewOrientationCorrector).correctOrientation(azimutRadians, isDisplayUp);
        verify(compassImageView).startAnimation(rotateAnimationCaptor.capture());
        RotateAnimation rotateAnimation = rotateAnimationCaptor.getValue();
        double azimutDegrees = Math.toDegrees(azimutRadians);
        validateRotationAnimation(rotateAnimation, 0f, (float) azimutDegrees);
    }

    @Test
    public void onNewAzimut_CalledStillRotating_RotateImage() {
        // setup
        float azimutRadians0 = (float) MathConstants.PI_HALF;
        float azimutRadians1 = (float) MathConstants.PI;
        boolean isDisplayUp = true;
        when(compassViewOrientationCorrector.correctOrientation(azimutRadians0, isDisplayUp)).thenReturn((double) azimutRadians0);
        // act
        testee.onNewAzimut(azimutRadians0, isDisplayUp);
        testee.onNewAzimut(azimutRadians1, isDisplayUp);
        // assert
        verify(compassViewOrientationCorrector).correctOrientation(azimutRadians0, isDisplayUp);
        verify(compassImageView).startAnimation(rotateAnimationCaptor.capture());
        RotateAnimation rotateAnimation = rotateAnimationCaptor.getValue();
        float azimutDegrees0 = (float) Math.toDegrees(azimutRadians0);
        validateRotationAnimation(rotateAnimation, 0f, -azimutDegrees0);
    }

    @Test
    public void onNewAzimut_CalledAfterRotating_RotateImage() {
        // setup
        float azimutRadians0 = (float) MathConstants.PI_HALF;
        float azimutRadians1 = (float) MathConstants.PI;
        boolean isDisplayUp = true;
        when(compassViewOrientationCorrector.correctOrientation(azimutRadians0, isDisplayUp)).thenReturn((double) azimutRadians0);
        when(compassViewOrientationCorrector.correctOrientation(azimutRadians1, isDisplayUp)).thenReturn((double) azimutRadians1);
        // act
        testee.onNewAzimut(azimutRadians0, isDisplayUp);
        ReflectionHelper.setFieldValue(testee, "isRotating", new AtomicBoolean(false));
        testee.onNewAzimut(azimutRadians1, isDisplayUp);
        // assert
        verify(compassViewOrientationCorrector).correctOrientation(azimutRadians0, isDisplayUp);
        verify(compassViewOrientationCorrector).correctOrientation(azimutRadians1, isDisplayUp);
        verify(compassImageView, times(2)).startAnimation(rotateAnimationCaptor.capture());
        List<RotateAnimation> rotateAnimations = rotateAnimationCaptor.getAllValues();
        assertEquals(2, rotateAnimations.size());
        float azimutDegrees0 = (float) Math.toDegrees(azimutRadians0);
        RotateAnimation rotateAnimation = rotateAnimations.get(0);
        validateRotationAnimation(rotateAnimation, 0f, -azimutDegrees0);
        rotateAnimation = rotateAnimations.get(1);
        float azimutDegrees1 = (float) Math.toDegrees(azimutRadians1);
        validateRotationAnimation(rotateAnimation, -azimutDegrees0, -azimutDegrees1);
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