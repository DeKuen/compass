package ch.dekuen.android.compass.view;

import ch.dekuen.android.compass.AzimutListener;

abstract class CompassViewService implements AzimutListener {

    protected final double getViewDegrees(float azimut) {
        return Math.toDegrees(azimut);
    }
}
