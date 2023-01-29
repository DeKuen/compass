package ch.dekuen.android.compass.sensor;

import android.hardware.SensorManager;
import android.view.Display;

import ch.dekuen.android.compass.AzimutListener;

public class OrientationCalculator {
    private final AzimutListener listener;
    private final Display display;

    public OrientationCalculator(AzimutListener listener, Display display) {
        this.listener = listener;
        this.display = display;
    }

    public void calculate(float[] matrixR) {

        float[] orientation = new float[3];
        // orientation contains: azimut, pitch and roll
        SensorManager.getOrientation(matrixR, orientation);
        // Azimuth, angle of rotation about the -z axis. The range of values is -π to π.
        float azimut = orientation[0];
        // send to listener
        listener.onNewAzimut(azimut);
    }
}
