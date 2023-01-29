package ch.dekuen.android.compass.view;

import ch.dekuen.android.compass.AzimutListener;

abstract class CompassViewService implements AzimutListener {

    protected final double getViewDegrees(float azimut) {
        // azimut = (float) ((azimut + 2 * Math.PI) % (2 * Math.PI));
        return Math.toDegrees(azimut);
    }
}
