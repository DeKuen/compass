package ch.dekuen.android.compass.view;

import android.view.Display;
import android.view.Surface;

import ch.dekuen.android.compass.AzimutListener;

abstract class CompassViewService implements AzimutListener {
    private final Display display;

    protected CompassViewService(Display display) {
        this.display = display;
    }

    protected final double getViewDegrees(float azimut, boolean isPhoneFacingUp) {
        double azimutDegrees = Math.toDegrees(azimut);
        int upDownCorrection = 0;
        if(!isPhoneFacingUp) {
            upDownCorrection = 180;
        }
        int correction;
        switch (display.getRotation()) {
            case Surface.ROTATION_90:
                correction = 90;
                break;
            case Surface.ROTATION_180:
                correction = 180 + upDownCorrection;
                break;
            case Surface.ROTATION_270:
                correction = 270;
                break;
            case Surface.ROTATION_0:
            default:
                correction = upDownCorrection;
                break;
        }
        return (azimutDegrees + correction + 360) % 360;
    }
}
