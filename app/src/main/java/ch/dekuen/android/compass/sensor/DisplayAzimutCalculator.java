package ch.dekuen.android.compass.sensor;

import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.Surface;

import ch.dekuen.android.compass.AzimutListener;

public class DisplayAzimutCalculator {
    private final AzimutListener listener;
    private final Display display;
    private float[] accelerationMeasurements;
    private float[] magneticMeasurements;

    public DisplayAzimutCalculator(AzimutListener listener, Display display) {
        this.listener = listener;
        this.display = display;
    }

    public void onAccelerationSensorChanged(float[] updates) {
        accelerationMeasurements = updates;
        calculate();
    }

    public void onMagneticSensorChanged(float[] updates) {
        magneticMeasurements = updates;
        calculate();
    }

    private void calculate() {
        if (accelerationMeasurements == null) {
            Log.i(getClass().getName(), "accelerationMeasurements is still null");
            return;
        } else if (magneticMeasurements == null) {
            Log.i(getClass().getName(), "magneticMeasurements is still null");
            return;
        }
        // todo
        // boolean isPhoneFacingUp = accelerationMeasurements[2] >= 0;
        float[] matrixR = new float[9];
        boolean success = SensorManager.getRotationMatrix(matrixR, null, accelerationMeasurements, magneticMeasurements);
        if (!success) {
            Log.i(getClass().getName(), "could not calculate rotation matrix");
            return;
        }
        float[] orientation = new float[3];
        // orientation contains: azimut, pitch and roll
        SensorManager.getOrientation(matrixR, orientation);
        // get angle around the z-axis rotated
        float azimut = orientation[0];
        // send to listener
        listener.onNewAzimut(azimut);
    }

    protected double getDisplayRotation() {
        switch (display.getRotation()) {
            case Surface.ROTATION_90:
                return Math.PI / 2;
            case Surface.ROTATION_180:
                return Math.PI;
            case Surface.ROTATION_270:
                return Math.PI * 1.5;
            case Surface.ROTATION_0:
            default:
                return 0;
        }
    }
}
