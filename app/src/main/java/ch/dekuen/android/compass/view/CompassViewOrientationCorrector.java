package ch.dekuen.android.compass.view;

import android.view.Surface;

import org.apache.commons.math3.util.MathUtils;

import java.util.function.Supplier;

import ch.dekuen.android.compass.MathConstants;

public class CompassViewOrientationCorrector {
    private final Supplier<Integer> getDisplayRotation;

    public CompassViewOrientationCorrector(Supplier<Integer> getDisplayRotation) {
        this.getDisplayRotation = getDisplayRotation;
    }

    // corrects azimut by phone orientation. The range of values in 0 to 2π.
    double correctOrientation(float azimut, boolean isDisplayUp) {
        return MathUtils.normalizeAngle(
                azimut
                        // mirror angle at y-axis if display is facing down by multiplying with 1 or -1
                        * (isDisplayUp ? 1 : -1)
                        // correct by display orientation (landscape to left/right, etc.)
                        + getDisplayRotationInRadians()
                ,
                // force positive angle by centering on π
                MathConstants.PI
        );
    }

    private double getDisplayRotationInRadians() {
        switch (getDisplayRotation.get()) {
            case Surface.ROTATION_90:
                return MathConstants.PI_HALF;
            case Surface.ROTATION_180:
                return MathConstants.PI;
            case Surface.ROTATION_270:
                return MathConstants.PI_THREE_HALVES;
            case Surface.ROTATION_0:
            default:
                return 0d;
        }
    }
}
