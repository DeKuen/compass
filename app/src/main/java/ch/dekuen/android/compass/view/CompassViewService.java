package ch.dekuen.android.compass.view;

import android.view.Display;
import android.view.Surface;

import ch.dekuen.android.compass.AzimutListener;

abstract class CompassViewService implements AzimutListener {
    private final Display display;

    protected CompassViewService(Display display) {
        this.display = display;
    }

    protected final int getRotation() {
        switch (display.getRotation()) {
            case Surface.ROTATION_90:
                return -90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 90;
            case Surface.ROTATION_0:
            default:
                return 0;
        }
    }
}
